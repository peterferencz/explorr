package me.peterferencz.app;

import javax.swing.tree.DefaultMutableTreeNode;

public class ExplorerNode extends DefaultMutableTreeNode {
    

    private String fileName;
    private String classPath;
    private boolean isFile;
    
    public String getClassPath() { return classPath; }
    public String getFileName() { return fileName; }
    public boolean isFile() { return isFile; }

    public ExplorerNode(String fileName, String classPath, boolean isFile){
        this.fileName = fileName;
        this.classPath = classPath;
        this.isFile = isFile;
    }

    @Override
    public String toString() {
        return fileName;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){ return false; }
        if(obj.getClass() != getClass()) { return false; }
        final ExplorerNode other = (ExplorerNode) obj;
        return (other.classPath == classPath) && (other.classPath == classPath) && (other.isFile == isFile);
    }

}
