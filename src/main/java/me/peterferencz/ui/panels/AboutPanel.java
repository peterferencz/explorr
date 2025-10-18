package me.peterferencz.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class AboutPanel extends JPanel {

    public AboutPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Jar explorR", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(40, 40, 40));

        JLabel subtitle = new JLabel("Java Archive Explorer & UML Visualizer", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(120, 120, 120));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(title);
        titlePanel.add(subtitle);

        add(titlePanel, BorderLayout.NORTH);

        JTextArea description = new JTextArea(
            "Jar explorR is a lightweight Java tool designed for exploring .jar files,\n"+
            "inspecting class contents, and generating UML diagrams.\n\n"+
            "Developed by Péter Ferencz\n"+
            "Website: https://peterferencz.me\n\n"+
            "Created as part of BME Basics of Programming 3 (BMEVIIIAB00)"
        );
        description.setEditable(false);
        description.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        description.setForeground(new Color(60, 60, 60));
        description.setBackground(Color.WHITE);
        description.setBorder(new EmptyBorder(10, 20, 10, 20));
        description.setLineWrap(true);
        description.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(description);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
    }
}
