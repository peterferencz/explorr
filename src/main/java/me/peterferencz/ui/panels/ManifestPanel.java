package me.peterferencz.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import me.peterferencz.app.Main;

public class ManifestPanel extends JPanel {

    public ManifestPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // ===== Header =====
        JLabel title = new JLabel("Manifest", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // ===== Table data =====
        Object[][] data;

        try {
            JarFile jar = Main.getGlobalContext().getJarFile();
            Manifest manifest = jar.getManifest();
            if (manifest != null) {
                Attributes mainAttrs = manifest.getMainAttributes();
                data = mainAttrs.entrySet().stream()
                        .map(e -> new Object[]{e.getKey().toString(), e.getValue().toString()})
                        .toArray(Object[][]::new);
            } else {
                data = new Object[][]{{"No manifest found", ""}};
            }
        } catch (Exception e) {
            data = new Object[][]{{"Error reading manifest", e.getMessage()}};
        }

        // ===== Table =====
        String[] columnNames = {"Attribute", "Value"};
        JTable table = new JTable(new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // make table read-only
            }
        });
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setReorderingAllowed(false);
        table.setGridColor(new Color(220, 220, 220));
    }
}

