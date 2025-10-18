package me.peterferencz.app;

import java.util.jar.JarFile;

import javax.swing.JFrame;

import me.peterferencz.app.EventDispacher.Events;
import me.peterferencz.app.jar.ClassData;
import me.peterferencz.ui.Display;

public class Context {
    public boolean displayGTKTheme = true;

    public JFrame window = null;
    public Display display = null;

    private JarFile jar = null;
    private ClassData selectedClass = null;

    public JarFile getJarFile(){return jar;}
    public void setJarFile(JarFile jar){
        this.jar = jar;
        EventDispacher.dispatch(Events.JARFILECHOOSEN);
    }
    
    public ClassData getSelectedClass() { return selectedClass; }
    public void setSelectedClass(ClassData selectedClass) {
        this.selectedClass = selectedClass;
        EventDispacher.dispatch(Events.CLASSSELECTED);
    }
}
