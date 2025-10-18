package me.peterferencz.app.jar;

import java.util.ArrayList;

public class ClassData {
    private String classPath;
    private String className;
    private String superClass;
    private int access;
    private ArrayList<String> interfaces;
    private ArrayList<Field> fields;
    private ArrayList<Method> methods;
    
    public String getClassPath() { return classPath; }
    public String getClassName() { return className; }
    public ArrayList<String> getInterfaces() { return interfaces; }
    public String getSuperClass() { return superClass; }
    public int getAccess() { return access; }
    public ArrayList<Method> getMethods() { return methods; }
    public ArrayList<Field> getFields() { return fields; }
    public void setClassPath(String classPath) { this.classPath = classPath; }
    public void setClassName(String className) { this.className = className; }
    public void setInterfaces(ArrayList<String> interfaces) { this.interfaces = interfaces; }
    public void setSuperClass(String superClass) { this.superClass = superClass; }
    public void setAccess(int access) { this.access = access; }
    public void setMethods(ArrayList<Method> methods) { this.methods = methods; }
    public void setFields(ArrayList<Field> fields) { this.fields = fields; }


    
    public ClassData(){
        fields = new ArrayList<>();
        methods = new ArrayList<>();
        interfaces = new ArrayList<>();
    }

    public ClassData(String classPath, String className, ArrayList<Field> fields, ArrayList<Method> methods, ArrayList<String> interfaces, String superClass, int access){
        this.classPath = classPath;
        this.className = className;
        this.fields = fields;
        this.methods = methods;
        this.interfaces = interfaces;
        this.superClass = superClass;
        this.access = access;
    }
}
