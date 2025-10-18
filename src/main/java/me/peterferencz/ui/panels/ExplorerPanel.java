package me.peterferencz.ui.panels;

import java.awt.BorderLayout;
import java.util.jar.JarFile;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import me.peterferencz.app.EventDispacher;
import me.peterferencz.app.EventDispacher.Events;
import me.peterferencz.app.jar.JarFileHandler;
import me.peterferencz.app.ExplorerNode;
import me.peterferencz.app.Main;

public class ExplorerPanel extends JPanel {

    private JTree tree;
    private JScrollPane scrollPane;

    public ExplorerPanel() {
        setLayout(new BorderLayout());
        

        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        tree = new JTree(root);
        tree.setRootVisible(false);
        scrollPane = new JScrollPane(tree);
        scrollPane.setPreferredSize(null);

        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(e -> {
            ExplorerNode node = (ExplorerNode) tree.getLastSelectedPathComponent();
            if(node == null){ return; }
            if(!node.isFile()){ return; }
            if(node.getFileName().equals("MANIFEST.MF") && node.getClassPath().equals("META-INF.MANIFEST.MF")){
                EventDispacher.dispatch(Events.MANIFESTFILECHOOSEN);
                return;
            }
            if(!node.getFileName().endsWith(".class")){
                //TODO open in-editor, or with external editor
                return;
            }
            
            Main.getGlobalContext().setSelectedClass(JarFileHandler.readClassFile(node.getClassPath()));
        });

        add(scrollPane, BorderLayout.CENTER);

        EventDispacher.subscribe(Events.JARFILECHOOSEN, () -> onJarSelected());
        onJarSelected();
    }

    private void onJarSelected(){
        JarFile jar = Main.getGlobalContext().getJarFile();
        ExplorerNode root = new ExplorerNode("", "", false);
        
        if(jar == null){
            tree.setModel(new DefaultTreeModel(root));
            return;
        }

        jar.stream()
            .filter(e -> !e.isDirectory())
            .forEach(entry -> {
                String[] parts = entry.getName().split("/");
                ExplorerNode node = root;
                for (int j = 0; j < parts.length; j++) {
                    String part = parts[j];
                    ExplorerNode child = null;
                    for (int i = 0; i < node.getChildCount(); i++) {
                        ExplorerNode existing = (ExplorerNode) node.getChildAt(i);
                        if (existing.getFileName().equals(part)) {
                            child = existing;
                            break;
                        }
                    }
                    if (child == null) {
                        boolean isFile = j == parts.length-1;
                        String classPath = node == root ? part : node.getClassPath()+"."+part;
                        child = new ExplorerNode(part, classPath, isFile);
                        node.add(child);
                    }
                    node = child;
                }
            });
        tree.setModel(new DefaultTreeModel(root));
    }
}
