package me.peterferencz.app.jar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import me.peterferencz.app.EventDispacher;
import me.peterferencz.app.EventDispacher.Events;
import me.peterferencz.app.Main;

public class JarFileHandler {
    private static Preferences prefs = Preferences.userNodeForPackage(JarFileHandler.class);
    private static File lastDirectory = new File(prefs.get("lastDir", System.getProperty("user.home")));
    
    public static void openFileDialog(){
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(lastDirectory);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        if(fc.showOpenDialog(Main.getGlobalContext().window) == JFileChooser.APPROVE_OPTION){
            try {
                prefs.put("lastDir", fc.getSelectedFile().getAbsolutePath());
                JarFile jar = new JarFile(fc.getSelectedFile());
                Main.getGlobalContext().setJarFile(jar);
                EventDispacher.dispatch(Events.JARFILEFINISHEDLOADING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ClassData readClassFile(String classPath){
        boolean classExt = classPath.endsWith(".class");
        if(classExt){
            classPath = classPath.substring(0, classPath.length()-".class".length());
        }
        classPath = classPath.replace('.', '/');
        if(classExt){
            classPath += ".class";
        }
        JarFile jar = Main.getGlobalContext().getJarFile();
        if(jar == null){
            throw new RuntimeException("Couldn't open class entry (doesn't exist)");
        }

        ClassData classData = new ClassData();

        try {
            JarEntry entry = jar.getJarEntry(classPath);
            
            String entryName = entry.getName();
            if (entryName.endsWith(".class")) {
                entryName = entryName.substring(0, entryName.length() - ".class".length());
            }
            classData.setClassName(entryName.substring(entryName.lastIndexOf('/') + 1));
            classData.setClassPath(classPath);

            InputStream fis = jar.getInputStream(entry);
            ClassReader reader = new ClassReader(fis);

            reader.accept(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                    classData.getInterfaces().addAll(Arrays.asList(interfaces));
                    classData.setSuperClass(superName);
                    classData.setAccess(access);
                }
                
                @Override
                public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                    classData.getFields().add(new Field(name, descriptor, signature));
                    return super.visitField(access, name, descriptor, signature, value);
                }

                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    classData.getMethods().add(new Method(name, descriptor, signature));
                    return super.visitMethod(access, name, descriptor, signature, exceptions);
                }
            }, 0);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classData;
    }

}
