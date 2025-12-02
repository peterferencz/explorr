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

/**
 * A Menu for controlling the toolbar
 */
public class Toolbar extends JMenuBar{

    public Toolbar(){
        JMenu miFile = new JMenu("File");
        JMenuItem miFileOpen = new JMenuItem("Open");
        JMenuItem miFileExit = new JMenuItem("Exit");
        miFileOpen.addActionListener(e -> JarFileHandler.openFileDialog());
        miFileExit.addActionListener(e -> System.exit(0));
        miFile.add(miFileOpen);
        miFile.add(miFileExit);

        JMenu miView = new JMenu("View");
        JMenuItem miViewCloseall = new JMenuItem("Close all tabs");
        miViewCloseall.addActionListener(e -> Main.getGlobalContext().getDisplay().closeAllTabs());
        JMenuItem miViewOpenall = new JMenuItem("Expand Treeview");
        miViewOpenall.addActionListener(e -> EventDispacher.dispatch(Events.EXPANDALLTREEVIEW));
        miView.add(miViewCloseall);
        miView.add(miViewOpenall);

        JMenu miJar = new JMenu("Jar");
        JMenuItem miJarOpenmanifest = new JMenuItem("Open Manifest");
        miJarOpenmanifest.addActionListener(e -> EventDispacher.dispatch(Events.MANIFESTFILECHOOSEN));
        miJar.add(miJarOpenmanifest);

        UMLPanel umlPanel = new UMLPanel();
        JMenu miUml = new JMenu("UML");
        JMenuItem miUmlOpen = new JMenuItem("Open diagram");
        miUmlOpen.addActionListener(e -> Main.getGlobalContext().getDisplay().openTab("UML diagram", umlPanel));
        JMenuItem miUmlImage = new JMenuItem("Save image");
        miUmlImage.addActionListener(e -> EventDispacher.dispatch(Events.SAVEUMLDIAGRAM));
        miUml.add(miUmlOpen);
        miUml.add(miUmlImage);


        AboutPanel aboutPanel = new AboutPanel();
        JMenu miHelp = new JMenu("Help");
        JMenuItem miHelpAbout = new JMenuItem("About");
        miHelpAbout.addActionListener(e -> Main.getGlobalContext().getDisplay().openTab("About", aboutPanel));
        miHelp.add(miHelpAbout);

        add(miFile);
        add(miView);
        add(miJar);
        add(miUml);
        add(miHelp);
    }
}
