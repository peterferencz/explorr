package me.peterferencz.ui.panels;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import me.peterferencz.app.uml.Diagram;

public class UMLPanel extends JPanel {
    
    private Diagram canvas;
    

    public UMLPanel(){
        setLayout(new BorderLayout());
        canvas = new Diagram();
        JScrollPane scrollPane = new JScrollPane(canvas);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setWheelScrollingEnabled(false);


        add(scrollPane, BorderLayout.CENTER);
    }


    
}
