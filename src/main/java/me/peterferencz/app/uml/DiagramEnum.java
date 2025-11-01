package me.peterferencz.app.uml;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.Collections;
import java.util.List;

import me.peterferencz.app.jar.ClassData;
import me.peterferencz.app.jar.Field;
import me.peterferencz.app.jar.Prettier;

public class DiagramEnum extends DiagramElement {

    private boolean updatedDimensions;

    public DiagramEnum(ClassData classData) {
        super(classData);
    }

    @Override
    public void Draw(Graphics2D g2) {
        if(!updatedDimensions){
            calcualteDimensions(g2);
            updatedDimensions = true;
        }

        int currentY = drawClasslikeElementBox(g2, UmlTheme.EnumBackground, UmlTheme.EnumAccent);

        Font originalFont = g2.getFont();
        g2.setColor(Color.BLACK);
        g2.setFont(originalFont.deriveFont(Font.PLAIN, originalFont.getSize() - 1));
        FontMetrics fm = g2.getFontMetrics();
        int lineHeight = fm.getHeight();

        for (Field field : classData.getFields()) {
            String enumValue = field.getName();
            if(enumValue.equals("$VALUES") || enumValue.equals("ENUM$VALUES")) { continue; }
            g2.drawString(enumValue, x + UmlTheme.Padding, currentY + lineHeight);
            currentY += lineHeight + UmlTheme.PaddingField;
        }

        g2.setFont(originalFont);
    }

    private void calcualteDimensions(Graphics2D g2){
        Font font = g2.getFont();
        g2.setFont(font.deriveFont(Font.BOLD));
        FontMetrics metrics = g2.getFontMetrics();
        int titleWidth = metrics.stringWidth(classData.getClassName());
        int stringHeight = metrics.getHeight();

        // + 1 for access char (+-~#)
        int maxFieldWidth = classData.getFields().stream().map(Prettier::getUML).mapToInt(metrics::stringWidth).max().orElse(100) + 1;

        this.w = 2*UmlTheme.Padding + Collections.max(List.of(titleWidth, maxFieldWidth, UmlTheme.MinBoxWidth));
        h = UmlTheme.Padding +
            stringHeight +
            UmlTheme.PaddingElementname +
            UmlTheme.PaddingSeparator +
            (stringHeight + UmlTheme.PaddingField) * (classData.getFields().size() -1) + // -1 for $VALUES
            UmlTheme.Padding;
    }
    
}
