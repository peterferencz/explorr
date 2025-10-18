package me.peterferencz.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import me.peterferencz.app.jar.JarFileHandler;

public class InfoPanel extends JPanel {

    public InfoPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(50, 40, 50, 40));

        // ===== Greeting Section =====
        JLabel greetingLabel = new JLabel("Welcome to Jar explorR", SwingConstants.CENTER);
        greetingLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        greetingLabel.setForeground(new Color(40, 40, 40));
        greetingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


        JLabel subtitleLabel = new JLabel("Your tool for exploring JAR files and visualizing classes", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(greetingLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(subtitleLabel);

        JLabel iconLabel = new JLabel(UIManager.getIcon("FileView.directoryIcon"), SwingConstants.CENTER);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel instructionLabel = new JLabel("Press Ctrl+O or click below to open a JAR file", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        instructionLabel.setForeground(new Color(100, 100, 100));
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton openButton = new JButton("Open JAR File");
        openButton.setMaximumSize(new Dimension(160, openButton.getPreferredSize().height));
        openButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        openButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        openButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        openButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Open file dialog on click
        openButton.addActionListener(e -> {
            JarFileHandler.openFileDialog();
        });

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(iconLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(headerPanel);
        contentPanel.add(Box.createVerticalStrut(25));
        contentPanel.add(instructionLabel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(openButton);

        // Add padding around center
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.add(contentPanel);

        add(centerWrapper, BorderLayout.CENTER);
    }
}
