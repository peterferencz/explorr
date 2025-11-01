package me.peterferencz.app.uml;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import me.peterferencz.app.jar.ClassData;

public abstract class DiagramElement {
    protected int x, y, w, h;
    protected ClassData classData;
    protected List<LineConnectingClasses> connections;

    public DiagramElement(ClassData classData){
        x = 0;
        y = 0;
        w = 100;
        h = 100;
        this.classData = classData;
        this.connections = new ArrayList<>();
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    
    public int getWidth() { return w; }
    public int getHeight() { return h; }

    public void moveBy(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    public ClassData getClassData() { return classData; }
    public void setClassData(ClassData classData) {  this.classData = classData; }

    public void addConnection(LineConnectingClasses line){
        connections.add(line);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, w, h);
    }

    public Rectangle getBoundsForRepaint(){
        //FIXME get scale
        double scale = 1;

        int scaledX = (int) Math.floor(x * scale) - 5;
        int scaledY = (int) Math.floor(y * scale) - 5;
        int scaledW = (int) Math.ceil(w * scale)  + 5;
        int scaledH = (int) Math.ceil(h * scale)  + 5;
        Rectangle rect = new Rectangle(scaledX, scaledY, scaledW, scaledH);

        for(LineConnectingClasses line : connections){
            rect = rect.union(line.getBounds());
        }
        return rect;
    }

    public boolean contains(double px, double py) {
        return px >= x && px <= x + w && py >= y && py <= y + h;
    }

    public abstract void Draw(Graphics2D g2);

    protected int drawClasslikeElementBox(Graphics2D g2, Color background, Color accent){
        g2.setColor(Color.WHITE);
        g2.fillRect(x, y, w, h);

        Font originalFont = g2.getFont();
        int currentY = y;

        //Fill background
        g2.setColor(Color.WHITE);
        g2.fillRect(x, y, w, h);
        
        // Class Name
        g2.setFont(originalFont.deriveFont(Font.BOLD));
        currentY += UmlTheme.Padding;
        String className = classData.getClassName();
        FontMetrics fm = g2.getFontMetrics();
        int classNameWidth = fm.stringWidth(className);
        int classNameX = x + (w - classNameWidth) / 2;
        g2.setColor(background);
        g2.fillRect(x, y, w, UmlTheme.PaddingElementname+UmlTheme.Padding +fm.getHeight());
        g2.setColor(Color.BLACK);
        g2.drawString(className, classNameX, currentY + fm.getHeight());
        currentY += fm.getHeight() + UmlTheme.PaddingElementname;

        // Border
        g2.setColor(accent);
        g2.setStroke(new BasicStroke(UmlTheme.SeparatorWeight));
        g2.drawRect(x, y, w, h);

        // Separator
        g2.drawLine(x, currentY, x + w, currentY);
        currentY += UmlTheme.PaddingSeparator;

        g2.setFont(originalFont);

        return currentY;
    }
}
