package me.peterferencz.app.uml;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class LineConnectingClasses{
    public enum Connection {
        NONE,
        INHERITANCE,
        IMPLEMENTATION,
        COMPOSITION
    }

    private DiagramElement from;
    private DiagramElement to;
    Connection connection;
    
    public DiagramElement getFrom() { return from; }
    public DiagramElement getTo() { return to; }

    public LineConnectingClasses(DiagramElement from, DiagramElement to, Connection connection){
        this.from = from;
        this.to = to;
        this.connection = connection;
    }

    public Rectangle getBounds(){
        return from.getBounds().union(to.getBounds());
    }

    public Rectangle getBoundsForRepaint(double scale){
        return from.getBoundsForRepaint(scale, false).union(to.getBoundsForRepaint(scale, false));
    }

    /** Draw the line connecting the two components
     * @param g2 Graphics
     */
    public void draw(Graphics2D g2){
        Rectangle2D childRect = from.getBounds();
        Rectangle2D parentRect = to.getBounds();

        switch (connection) {
            case IMPLEMENTATION:
                g2.setColor(UmlTheme.InterfaceAccent);
                g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{10, 5}, 0f));
                break;
            case INHERITANCE:
            case COMPOSITION:
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(1f));
                break;
            default: throw new RuntimeException("Unknown connection type");
        }

        // Compute centers
        double childCenterX = childRect.getCenterX();
        double childCenterY = childRect.getCenterY();
        double parentCenterX = parentRect.getCenterX();
        double parentCenterY = parentRect.getCenterY();

        // Determine preferred connection sides
        boolean verticalConnection = Math.abs(parentCenterY - childCenterY) > Math.abs(parentCenterX - childCenterX);

        Point2D start;
        Point2D end;

        if (verticalConnection) {
            // Connect vertically
            if (childCenterY < parentCenterY) {
                // Child above parent
                start = new Point2D.Double(childCenterX, childRect.getMaxY());
                end = new Point2D.Double(parentCenterX, parentRect.getMinY());
            } else {
                // Child below parent
                start = new Point2D.Double(childCenterX, childRect.getMinY());
                end = new Point2D.Double(parentCenterX, parentRect.getMaxY());
            }
        } else {
            // Connect horizontally
            if (childCenterX < parentCenterX) {
                start = new Point2D.Double(childRect.getMaxX(), childCenterY);
                end = new Point2D.Double(parentRect.getMinX(), parentCenterY);
            } else {
                start = new Point2D.Double(childRect.getMinX(), childCenterY);
                end = new Point2D.Double(parentRect.getMaxX(), parentCenterY);
            }
        }

        // Compute a Manhattan path (rectangular)
        List<Point2D> path = new ArrayList<>();
        path.add(start);

        if (verticalConnection) {
            double midY = (start.getY() + end.getY()) / 2;
            path.add(new Point2D.Double(start.getX(), midY));
            path.add(new Point2D.Double(end.getX(), midY));
        } else {
            double midX = (start.getX() + end.getX()) / 2;
            path.add(new Point2D.Double(midX, start.getY()));
            path.add(new Point2D.Double(midX, end.getY()));
        }

        path.add(end);

        // Draw rectangular segments
        for (int i = 0; i < path.size() - 1; i++) {
            Point2D p1 = path.get(i);
            Point2D p2 = path.get(i + 1);
            g2.draw(new Line2D.Double(p1, p2));
        }

        // Draw line head at the parent side
        if(connection == Connection.INHERITANCE || connection == Connection.IMPLEMENTATION){
            drawArrowHead(g2, path.get(path.size() - 2), end, 20);
        }else if(connection == Connection.COMPOSITION){
            drawDiamondHead(g2, path.get(path.size() - 2), end, 15);
        }
    }

    /** Draws an arrow pointing to @param to
     * @param g2 Graphics
     * @param from From
     * @param to To
     * @param size Line thickness
     */
    private void drawArrowHead(Graphics2D g2, Point2D from, Point2D to, double size) {
        double angle = Math.atan2(to.getY() - from.getY(), to.getX() - from.getX());
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        // Create a hollow triangle (open arrowhead)
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];

        xPoints[0] = (int) to.getX();
        yPoints[0] = (int) to.getY();

        xPoints[1] = (int) (to.getX() - size * cos + size / 2 * sin);
        yPoints[1] = (int) (to.getY() - size * sin - size / 2 * cos);

        xPoints[2] = (int) (to.getX() - size * cos - size / 2 * sin);
        yPoints[2] = (int) (to.getY() - size * sin + size / 2 * cos);

        Color prevColor = g2.getColor();
        Stroke prevStroke = g2.getStroke();

        // White fill, colored outline
        g2.setStroke(new BasicStroke(1.2f));
        g2.setColor(Color.WHITE);
        g2.fillPolygon(xPoints, yPoints, 3);
        g2.setColor(prevColor);
        g2.drawPolygon(xPoints, yPoints, 3);

        g2.setStroke(prevStroke);
    }

    /** Draws an arrow with a diamond head pointing to @param to
     * @param g2 Graphics
     * @param from From
     * @param to To
     * @param size Line thickness
     */
    private void drawDiamondHead(Graphics2D g2, Point2D from, Point2D to, double size){
        double angle = Math.atan2(to.getY() - from.getY(), to.getX() - from.getX());
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        // Diamond points (4 points)
        int[] xPoints = new int[4];
        int[] yPoints = new int[4];

        // Tip of diamond (pointing to the target)
        xPoints[0] = (int) to.getX();
        yPoints[0] = (int) to.getY();

        // Back-left
        xPoints[1] = (int) (to.getX() - size * cos + size / 2 * sin);
        yPoints[1] = (int) (to.getY() - size * sin - size / 2 * cos);

        // Back-tip (furthest from the line)
        xPoints[2] = (int) (to.getX() - 2 * size * cos);
        yPoints[2] = (int) (to.getY() - 2 * size * sin);

        // Back-right
        xPoints[3] = (int) (to.getX() - size * cos - size / 2 * sin);
        yPoints[3] = (int) (to.getY() - size * sin + size / 2 * cos);

        // Save previous color and stroke
        Stroke prevStroke = g2.getStroke();

        // Draw filled diamond
        g2.setColor(Color.WHITE);
        g2.fillPolygon(xPoints, yPoints, 4);

        // Draw diamond outline
        g2.setStroke(new BasicStroke(1.2f));
        g2.setColor(Color.BLACK);
        g2.drawPolygon(xPoints, yPoints, 4);

        // Restore previous stroke
        g2.setStroke(prevStroke);
    }
}
