package me.peterferencz.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import me.peterferencz.app.jar.ClassData;
import me.peterferencz.app.jar.Prettier;

public class ClassPanel extends JPanel {

    private ClassData classData;

    public ClassData getClassData(){
        return classData;
    }


    public ClassPanel(ClassData classData) {
        this.classData = classData;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel classNameLabel = new JLabel(classData.getClassName());
        classNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        classNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(classNameLabel);
        headerPanel.add(Box.createVerticalStrut(5));

        // Super
        if (classData.getSuperClass() != null) {
            JLabel superLabel = new JLabel("Extends: " + classData.getSuperClass());
            superLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            superLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            headerPanel.add(superLabel);
        }

        // Interfaces
        ArrayList<String> interfaces = classData.getInterfaces();
        if (interfaces != null && !interfaces.isEmpty()) {
            JLabel ifaceLabel = new JLabel("Implements:");
            ifaceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            ifaceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            headerPanel.add(ifaceLabel);

            for (String iface : interfaces) {
                JLabel iLabel = new JLabel("  • " + iface);
                iLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                iLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                headerPanel.add(iLabel);
            }
        }

        add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        String[][] propData = {
                {"Class Name", classData.getClassName()},
                {"Class Path", classData.getClassPath()},
                {"Modifiers", Prettier.prettyAccess(classData.getAccess())},
                {"Superclass", classData.getSuperClass() != null ? classData.getSuperClass() : "-"},
                {"Interfaces", interfaces != null ? String.join(", ", interfaces) : "-"}
        };
        JTable propTable = new JTable(new DefaultTableModel(propData, new String[]{"Property", "Value"}) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        });
        styleTable(propTable);
        tabbedPane.addTab("Properties", new JScrollPane(propTable));

        String[] fieldCols = {"Name", "Access", "Descriptor", "Signature"};
        Object[][] fieldRows = classData.getFields().stream()
                .map(f -> new Object[]{
                    f.getName(),
                    Prettier.prettyAccess(f.getAccess()),
                    Prettier.prettyDescriptor(f.getDescriptor()),
                    Prettier.prettySignature(f.getSignature())
                }).toArray(Object[][]::new);
        JTable fieldTable = new JTable(new DefaultTableModel(fieldRows, fieldCols) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        });
        styleTable(fieldTable);
        tabbedPane.addTab("Fields", new JScrollPane(fieldTable));

        String[] methodCols = {"Name", "Access", "Descriptor", "Signature"};
        Object[][] methodRows = classData.getMethods().stream()
                .map(m -> new Object[]{
                    m.getName(),
                    Prettier.prettyAccess(m.getAccess()),
                    Prettier.prettyDescriptor(m.getDescriptor()),
                    Prettier.prettySignature(m.getSignature())
                }).toArray(Object[][]::new);
        JTable methodTable = new JTable(new DefaultTableModel(methodRows, methodCols) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        });
        styleTable(methodTable);
        tabbedPane.addTab("Methods", new JScrollPane(methodTable));

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.setGridColor(new Color(220, 220, 220));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ClassPanel)) return false;
        ClassPanel other = (ClassPanel) obj;
        return classData.equals(other.getClassData());
    }
}