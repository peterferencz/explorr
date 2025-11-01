package me.peterferencz.ui;

import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import me.peterferencz.app.EventDispacher;
import me.peterferencz.app.EventDispacher.Events;
import me.peterferencz.app.Main;
import me.peterferencz.ui.panels.ClassPanel;
import me.peterferencz.ui.panels.ExplorerPanel;
import me.peterferencz.ui.panels.InfoPanel;
import me.peterferencz.ui.panels.ManifestPanel;

public class Display {
    
    private JTabbedPane tabbedPane;

    public Display(){
        JFrame window = new JFrame("Jar explorR");
        window.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        window.setSize(400, 300);
        Main.getGlobalContext().setWindow(window);
        Main.getGlobalContext().setDisplay(this);

        Toolbar toolbar = new Toolbar();
        window.add(toolbar);
        window.setJMenuBar(toolbar);

        tabbedPane = new JTabbedPane();
        InfoPanel infoPanel = new InfoPanel();
        if(Main.getGlobalContext().getJarFile() == null){
            openTab("Get started", infoPanel);
        }
        EventDispacher.subscribe(Events.JARFILECHOOSEN, () -> {
            closeTab(infoPanel);
        });
        
        ExplorerPanel explorerPanel = new ExplorerPanel();
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            explorerPanel,
            tabbedPane
        );
        splitPane.setDividerLocation(200);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.1);
        window.add(splitPane);

        EventDispacher.subscribe(Events.JARFILEFINISHEDLOADING, () -> window.repaint());
        EventDispacher.subscribe(Events.CLASSSELECTED, () -> openTab(
            Main.getGlobalContext().getSelectedClass().getClassName(),
            new ClassPanel(Main.getGlobalContext().getSelectedClass())
        ));
        ManifestPanel manifestPanel = new ManifestPanel();
        EventDispacher.subscribe(Events.MANIFESTFILECHOOSEN, () -> openTab(
            "Manifest",
            manifestPanel
        ));

        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    public void openTab(String name, JPanel panel){
        if(tabbedPane.indexOfComponent(panel) == -1){
            addTabToTabbedLayout(name, panel);
        }
        tabbedPane.setSelectedComponent(panel);
    }

    public void closeTab(JPanel panel){
        if(tabbedPane.indexOfComponent(panel) == -1){ return; }
        tabbedPane.remove(panel);;
    }

    public void closeAllTabs(){
        tabbedPane.removeAll();
    }

    private void addTabToTabbedLayout(String name, JPanel panel){
        
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabPanel.setOpaque(false);
        
        JLabel tabLabel = new JLabel(name + "  ");
        JButton closeButton = new JButton("x");
        closeButton.setFont(new Font("Dialog", Font.BOLD, 12));
        closeButton.setBorder(BorderFactory.createEmptyBorder());
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        closeButton.setOpaque(false);
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> tabbedPane.remove(panel));

        
        tabPanel.add(tabLabel);
        tabPanel.add(closeButton);
        
        tabbedPane.addTab(name, panel);
        tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(panel), tabPanel);
    }
}
