package me.peterferencz.app;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

import javax.swing.JFrame;

import me.peterferencz.app.EventDispacher.Events;
import me.peterferencz.app.jar.ClassData;
import me.peterferencz.ui.Display;

public class Context {
    private JFrame window = null;
    public JFrame getWindow() { return window; }
    public void setWindow(JFrame window) { this.window = window; }
    
    private Display display = null;
    public Display getDisplay() { return display; }
    public void setDisplay(Display display) { this.display = display; }

    private JarFile jar = null;
    private List<ClassData> classes = new ArrayList<>();
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

    public List<ClassData> getClasses(){ return classes; }
    public void setClassData(List<ClassData> classes){
        this.classes = classes;
    }
}
