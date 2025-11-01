package me.peterferencz.app.uml;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.peterferencz.app.jar.ClassData;
import me.peterferencz.app.jar.Field;
import me.peterferencz.app.jar.Method;
import me.peterferencz.app.jar.Prettier;

public class DiagramInterface extends DiagramElement{

    private boolean updatedDimensions;
    
    public DiagramInterface(ClassData classData) {
        super(classData);
    }

    @Override
    public void Draw(Graphics2D g2) {
        if(!updatedDimensions){
            calculateDimensions(g2);
            updatedDimensions = true;
        }

        Color accent = UmlTheme.InterfaceAccent;
        int currentY = drawClasslikeElementBox(g2, UmlTheme.InterfaceBackground, accent);
        Font originalFont = g2.getFont();
        FontMetrics fm = g2.getFontMetrics();

        // --- Fields ---
        g2.setColor(Color.BLACK);
        g2.setFont(originalFont.deriveFont(Font.PLAIN, originalFont.getSize() - 1));
        fm = g2.getFontMetrics();
        int lineHeight = fm.getHeight();
        
        // Draw fields
        for (Field field : classData.getFields()) {
            String fieldText = Prettier.getUML(field);
            if(field.isAbstract()){
                Font font = g2.getFont();
                g2.setFont(font.deriveFont(Font.ITALIC));
            }
            g2.drawString(fieldText, x + UmlTheme.Padding, currentY + lineHeight);
            Font font = g2.getFont();
            g2.setFont(font.deriveFont(Font.PLAIN));
            currentY += lineHeight + UmlTheme.PaddingField;
            if(field.isStatic()){
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(x + UmlTheme.Padding, currentY + 1, x + UmlTheme.Padding + fm.stringWidth(fieldText), currentY + 1);
            }
        }

        // --- Separator ---
        currentY += UmlTheme.PaddingSeparator;
        g2.setColor(accent);
        g2.setStroke(new BasicStroke(UmlTheme.SeparatorWeight));
        g2.drawLine(x, currentY, x + w, currentY);
        currentY += UmlTheme.PaddingSeparator;
        
        // --- Methods ---
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1f));
        List<Method> displayMethods = classData
            .getMethods()
            .stream()
            .filter(m -> !m.isLambda())
            .filter(m -> !m.isStaticConstructor())
            .toList();
        for (Method method : displayMethods) {
            String methodText = Prettier.getUML(method);
            g2.drawString(methodText, x + UmlTheme.Padding, currentY + lineHeight);
            currentY += lineHeight + UmlTheme.PaddingField;
            if(method.isStatic()){
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(x + UmlTheme.Padding, currentY + 1, x + UmlTheme.Padding + fm.stringWidth(methodText), currentY + 1);
            }
        }
        
        // Restore original font
        g2.setFont(originalFont);
    }

    private void calculateDimensions(Graphics2D g2){
        Font font = g2.getFont();
        g2.setFont(font.deriveFont(Font.BOLD));
        FontMetrics metrics = g2.getFontMetrics();
        int titleWidth = metrics.stringWidth(classData.getClassName());
        int stringHeight = metrics.getHeight();

        List<Method> nonLamdaMethods = classData.getMethods().stream().filter(m -> !m.isLambda()).toList();

        // + 1 for access char (+-~#)
        int maxFieldWidth = classData.getFields().stream().map(Prettier::getUML).mapToInt(metrics::stringWidth).max().orElse(100) + 1;
        int maxMethodWidth = nonLamdaMethods.stream().map(Prettier::getUML).mapToInt(metrics::stringWidth).max().orElse(0) + 1;

        this.w = 2*UmlTheme.Padding + Collections.max(List.of(titleWidth, maxFieldWidth, maxMethodWidth, UmlTheme.MinBoxWidth));
        h = UmlTheme.Padding +
            stringHeight +
            UmlTheme.PaddingElementname +
            UmlTheme.PaddingSeparator +
            (stringHeight + UmlTheme.PaddingField) * classData.getFields().size() +
            UmlTheme.PaddingSeparator +
            UmlTheme.PaddingSeparator +
            (stringHeight + UmlTheme.PaddingField) * nonLamdaMethods.size() +
            UmlTheme.Padding;
    }
}
