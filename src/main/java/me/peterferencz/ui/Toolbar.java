package me.peterferencz.ui;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import me.peterferencz.app.EventDispacher;
import me.peterferencz.app.Main;
import me.peterferencz.app.EventDispacher.Events;
import me.peterferencz.app.jar.JarFileHandler;
import me.peterferencz.ui.panels.AboutPanel;
import me.peterferencz.ui.panels.UMLPanel;

public class Toolbar extends JMenuBar{
    

    public Toolbar(){

        JMenu tb_file = new JMenu("File");
        JMenuItem tb_file_open = new JMenuItem("Open");
        JMenuItem tb_file_exit = new JMenuItem("Exit");
        tb_file_open.addActionListener(e -> JarFileHandler.openFileDialog());
        tb_file_exit.addActionListener(e -> System.exit(0));
        tb_file.add(tb_file_open);
        tb_file.add(tb_file_exit);

        JMenu tb_view = new JMenu("View");
        JMenuItem tb_view_closeall = new JMenuItem("Close all tabs");
        tb_view_closeall.addActionListener(e -> Main.getGlobalContext().getDisplay().closeAllTabs());
        tb_view.add(tb_view_closeall);

        JMenu tb_jar = new JMenu("Jar");
        JMenuItem tb_jar_manifest = new JMenuItem("Open Manifest");
        tb_jar_manifest.addActionListener(e -> EventDispacher.dispatch(Events.MANIFESTFILECHOOSEN));
        tb_jar.add(tb_jar_manifest);

        JMenu tb_uml = new JMenu("UML");
        JMenuItem tb_uml_open = new JMenuItem("Open diagram");
        UMLPanel umlPanel = new UMLPanel();
        tb_uml_open.addActionListener(e -> Main.getGlobalContext().getDisplay().openTab("UML diagram", umlPanel));
        JMenuItem tb_uml_image = new JMenuItem("Save image");
        tb_uml_image.addActionListener(e -> EventDispacher.dispatch(Events.SAVEUMLDIAGRAM));
        tb_uml.add(tb_uml_open);
        tb_uml.add(tb_uml_image);


        JMenu tb_help = new JMenu("Help");
        JMenuItem tb_help_about = new JMenuItem("About");
        AboutPanel aboutPanel = new AboutPanel();
        tb_help_about.addActionListener(e -> Main.getGlobalContext().getDisplay().openTab("About", aboutPanel));
        tb_help.add(tb_help_about);

        add(tb_file);
        add(tb_view);
        add(tb_jar);
        add(tb_uml);
        add(tb_help);
    }
}
