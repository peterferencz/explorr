package me.peterferencz.app.uml;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import me.peterferencz.app.EventDispacher;
import me.peterferencz.app.EventDispacher.Events;
import me.peterferencz.app.Main;
import me.peterferencz.app.jar.ClassData;
import me.peterferencz.app.uml.LineConnectingClasses.Connection;

public class Diagram extends JPanel implements Scrollable{

    private static final int BASE_WIDTH = 2000;
    private static final int BASE_HEIGHT = 2000;
    private static final double MIN_SCALE = 0.1;
    private static final double MAX_SCALE = 5.0;
    private static final double ZOOM_FACTOR = 1.1;

    private boolean panning = false;
    private Point2D.Double lastPanPoint = null;
    private DiagramElement selected = null;
    private double selectOffsetX = 0;
    private double selectOffsetY = 0;
    private double scale = 1.0;

    
    private final List<DiagramElement> elements = new ArrayList<>();
    private final List<LineConnectingClasses> connections = new ArrayList<>();

    public Diagram() {
        EventDispacher.subscribe(Events.JARFILEFINISHEDLOADING, this::loadClassData);
        EventDispacher.subscribe(Events.SAVEUMLDIAGRAM, this::saveToImage);
        
        setBackground(Color.WHITE);
        setOpaque(true);
        setDoubleBuffered(true);
        updatePreferredSize();

        setupEventHandlers();
    }

    private void loadClassData() {
        elements.clear();

        for(ClassData data : Main.getGlobalContext().getClasses()){
            if(data.isEnum()){
                elements.add(new DiagramEnum(data));
            }else if(data.isInterface()){
                elements.add(new DiagramInterface(data));
            }else if(data.isClass()){
                elements.add(new DiagramClass(data));
            }else{
                throw new RuntimeException("Unknown classData type!");
            }
        }

        setupInheritence();

        setupImplementation();

        setupComposition();
    }

    private void setupComposition() {
        for(DiagramElement element : elements){
            List<DiagramElement> connected = elements.stream()
                .filter(e -> {
                    if(e.equals(element)) {return false; }
                    String search = element.getClassData().getClassPath().substring(0, element.getClassData().getClassPath().length()-".class".length());
                    
                    return e.getClassData().getFields()
                    .stream().anyMatch(f -> {
                        boolean descMatch = f.getDescriptor().contains(search);
                        boolean signMatch = f.getSignature() != null && f.getSignature().contains(search);
                        return descMatch || signMatch;
                    });
                })
                .filter(e -> !e.equals(element))
                .toList();
            for(DiagramElement el : connected){
                LineConnectingClasses line = new LineConnectingClasses(element, el, Connection.COMPOSITION);
                el.addConnection(line);
                element.addConnection(line);
                connections.add(line);
            }
        }
    }

    private void setupImplementation() {
        for(DiagramElement element : elements){
            if(!element.getClassData().isInterface()) { continue; }
            String infc = element.getClassData().getClassPath().substring(0, element.getClassData().getClassPath().length() - ".class".length());

            List<DiagramElement> implementElements = elements.stream()
                .filter(e -> e.getClassData().getInterfaces().contains(infc))
                .toList();
            for(DiagramElement el : implementElements){
                LineConnectingClasses line = new LineConnectingClasses(el, element, Connection.IMPLEMENTATION);
                el.addConnection(line);
                element.addConnection(line);
                connections.add(line);
            }
        }
    }

    private void setupInheritence() {
        for(DiagramElement element : elements){
            //Only allow classes
            if(!element.getClassData().isClass()){ continue; }
            DiagramClass clazz = (DiagramClass) element;


            DiagramClass parent = elements.stream()
                .filter(c -> c.getClassData().isClass())
                .map(c -> (DiagramClass) c)
                .filter(c -> 
                    c.getClassData().getClassPath()
                    .equals(clazz.getClassData().getSuperClass() + ".class"))
                .findFirst().orElse(null);
            if(parent != null){
                LineConnectingClasses connection = new LineConnectingClasses(clazz, parent, Connection.INHERITANCE);
                clazz.addConnection(connection);
                parent.addConnection(connection);
                connections.add(connection);
            }
        }
    }

    private void setupEventHandlers() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
        addMouseWheelListener(this::handleMouseWheel);
    }

    private void handleMousePressed(MouseEvent e) {
        requestFocusInWindow();
        JViewport vp = getViewport();

        if (SwingUtilities.isRightMouseButton(e)) {
            startPanning(e, vp);
            return;
        }

        if (SwingUtilities.isLeftMouseButton(e)) {
            selectObjectAt(e.getPoint());
            return;
        }
    }

    private void startPanning(MouseEvent e, JViewport vp) {
        panning = true;
        Point pointInViewport = SwingUtilities.convertPoint(this, e.getPoint(), vp);
        lastPanPoint = new Point2D.Double(pointInViewport.x, pointInViewport.y);
        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        selected = null;
    }

    private void selectObjectAt(Point viewPoint) {
        Point2D.Double modelPoint = viewPointToModel(viewPoint);
        selected = null;
        
        for(DiagramElement dc : elements){
            if(!dc.contains(modelPoint.x, modelPoint.y)) { continue; }
            selected = dc;
            selectOffsetX = modelPoint.x - dc.getX();
            selectOffsetY = modelPoint.y - dc.getY();
            break;
        }
    }

    private void handleMouseDragged(MouseEvent e) {
        if (panning) {
            performPanning(e);
        } else if (selected != null) {
            dragSelectedObject(e);
        }
    }

    private void performPanning(MouseEvent e) {
        JViewport vp = getViewport();
        if (vp == null || lastPanPoint == null) return;

        Point current = SwingUtilities.convertPoint(this, e.getPoint(), vp);
        double dx = lastPanPoint.x - current.x;
        double dy = lastPanPoint.y - current.y;

        Point viewPos = vp.getViewPosition();
        int newX = (int) Math.round(viewPos.x + dx);
        int newY = (int) Math.round(viewPos.y + dy);

        // Clamp to bounds
        newX = Math.max(0, Math.min(newX, getWidth() - vp.getWidth()));
        newY = Math.max(0, Math.min(newY, getHeight() - vp.getHeight()));

        vp.setViewPosition(new Point(newX, newY));
        lastPanPoint = new Point2D.Double(current.x, current.y);
    }

    private void dragSelectedObject(MouseEvent e) {
        Point2D.Double currentModel = viewPointToModel(e.getPoint());
    
        double newX = currentModel.x - selectOffsetX;
        double newY = currentModel.y - selectOffsetY;
        
        // Snap to grid
        newX = Math.round(newX / 10.0) * 10.0;
        newY = Math.round(newY / 10.0) * 10.0;

        //TODO make scaling work
        Rectangle oldBounds = selected.getBoundsForRepaint();

        selected.setX((int)newX);
        selected.setY((int)newY);

        //TODO make scaling work
        Rectangle newBounds = selected.getBoundsForRepaint();

        // Create a repaint area that covers both positions with padding
        int pad = (int) (5 * scale); // Scale the padding too
        Rectangle repaintArea = oldBounds.union(newBounds);
        repaintArea.grow(pad, pad);

        repaint(repaintArea);

    }

    private void handleMouseReleased(MouseEvent e) {
        if (panning) {
            panning = false;
            lastPanPoint = null;
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void handleMouseWheel(MouseWheelEvent e) {
        JViewport vp = getViewport();
        if (vp == null) return;

        Point viewPos = vp.getViewPosition();
        double mouseModelX = (viewPos.x + e.getX()) / scale;
        double mouseModelY = (viewPos.y + e.getY()) / scale;

        double factor = e.getWheelRotation() < 0 ? ZOOM_FACTOR : 1.0 / ZOOM_FACTOR;
        double newScale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale * factor));
        
        
        // if (Math.abs(newScale - scale) < 1e-6) return;

        scale = newScale;
        updatePreferredSize();
        
        // Adjust viewport to keep mouse point stable
        SwingUtilities.invokeLater(() -> {
            adjustViewportAfterZoom(vp, mouseModelX, mouseModelY, e.getX(), e.getY());
        });

        revalidate();
    }

    private void adjustViewportAfterZoom(JViewport vp, double mouseModelX, double mouseModelY, int mouseX, int mouseY) {
        int newViewX = (int) Math.round(mouseModelX * scale - mouseX);
        int newViewY = (int) Math.round(mouseModelY * scale - mouseY);
        newViewX = Math.max(0, Math.min(newViewX, getWidth() - vp.getWidth()));
        newViewY = Math.max(0, Math.min(newViewY, getHeight() - vp.getHeight()));
        vp.setViewPosition(new Point(newViewX, newViewY));
        repaint();
    }

    public void setScale(double newScale) {
        newScale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, newScale));
        if (Math.abs(newScale - scale) > 1e-6) {
            scale = newScale;
            updatePreferredSize();
            revalidate();
            repaint();
        }
    }

    public DiagramElement getSelectedClass() {
        return selected;
    }

    // Utility methods
    private JViewport getViewport() {
        return (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, this);
    }

    private void updatePreferredSize() {
        int w = Math.max(1, (int) Math.round(BASE_WIDTH * scale));
        int h = Math.max(1, (int) Math.round(BASE_HEIGHT * scale));
        setPreferredSize(new Dimension(w, h));
    }

    private Point2D.Double viewPointToModel(Point viewPoint) {
        return new Point2D.Double(viewPoint.x / scale, viewPoint.y / scale);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    
        Graphics2D g2 = (Graphics2D) g.create();
    
        // Enable anti-aliasing for smoother graphics
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        // Apply scaling
        AffineTransform originalTransform = g2.getTransform();
        g2.scale(scale, scale);

        drawGrid(g2);
        drawElements(g2);
        drawClassConnections(g2);

        g2.setTransform(originalTransform);
        g2.dispose();
    }

    private void drawGrid(Graphics2D g2) {
        Rectangle clip = g2.getClipBounds();
        if (clip == null) return;
        
        // Only draw grid in visible area
        int startX = (int) (clip.x / 100) * 100;
        int startY = (int) (clip.y / 100) * 100;
        int endX = (int) (clip.x + clip.width + 100);
        int endY = (int) (clip.y + clip.height + 100);
        
        g2.setColor(new Color(240, 240, 240));
        
        // Draw grid
        for (int x = startX; x < endX; x += 100) {
            if (x >= clip.x && x <= clip.x + clip.width) {
                g2.drawLine(x, startY, x, endY);
            }
        }
        for (int y = startY; y < endY; y += 100) {
            if (y >= clip.y && y <= clip.y + clip.height) {
                g2.drawLine(startX, y, endX, y);
            }
        }

    }

    private void drawElements(Graphics2D g2) {
        for (DiagramElement element : elements) {
            element.Draw(g2);
        }

        for(LineConnectingClasses line : connections){
            line.Draw(g2);
        }
    }

    private void drawClassConnections(Graphics2D g2){
        // for(DiagramClass clazz : classes){
        //     if(clazz.getParent() != null){
        //         LineConnectingClasses line = new LineConnectingClasses(clazz, clazz.getParent(), Connection.INHERITANCE);
        //         line.Draw(g2);
        //     }
        // }
    }

    public void saveToImage(){
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        if(fc.showOpenDialog(Main.getGlobalContext().getWindow()) == JFileChooser.APPROVE_OPTION){
            fc.getSelectedFile();
        }else{
            return;
        }

        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        printAll(g2);
        g2.dispose();
        try {
            ImageIO.write(image, "png", fc.getSelectedFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        // Return the viewport size, not preferred size
        Container parent = getParent();
        if (parent instanceof JViewport) {
            return parent.getSize();
        }
        return new Dimension(BASE_WIDTH, BASE_HEIGHT);
        // return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        // return (int) (50 * scale); // Adjust as needed
        return 16;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return (orientation == SwingConstants.VERTICAL) ? visibleRect.height : visibleRect.width;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false; // Allow vertical scrolling
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
    }
}