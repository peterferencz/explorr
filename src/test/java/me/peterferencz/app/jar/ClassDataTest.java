package me.peterferencz.app.jar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

public class ClassDataTest {
    
    @Test
    void classPathStrippedRemovesDotClass() {
        ClassData cd = new ClassData();
        cd.setClassPath("me/test/MyClass.class");

        assertEquals("me/test/MyClass", cd.getClassPathStripped());
    }

    @Test
    void classPathStrippedLeavesOtherPathsUntouched() {
        ClassData cd = new ClassData();
        cd.setClassPath("me/test/MyClass");

        assertEquals("me/test/MyClass", cd.getClassPathStripped());
    }

    @Test
    void detectsInterfaceCorrectly() {
        ClassData cd = new ClassData();
        cd.setAccess(Opcodes.ACC_INTERFACE);

        assertTrue(cd.isInterface());
        assertFalse(cd.isClass());
        assertFalse(cd.isEnum());
    }

    @Test
    void detectsEnumCorrectly() {
        ClassData cd = new ClassData();
        cd.setAccess(Opcodes.ACC_ENUM);

        assertTrue(cd.isEnum());
        assertFalse(cd.isInterface());
        assertFalse(cd.isClass());
    }

    @Test
    void detectsRegularClassCorrectly() {
        ClassData cd = new ClassData();
        cd.setAccess(Opcodes.ACC_PUBLIC);

        assertTrue(cd.isClass());
        assertFalse(cd.isEnum());
        assertFalse(cd.isInterface());
    }

    @Test
    void equalsTrueForSameClassPathAndName() {
        ClassData a = new ClassData();
        a.setClassPath("A/B/C.class");
        a.setClassName("C");

        ClassData b = new ClassData();
        b.setClassPath("A/B/C.class");
        b.setClassName("C");

        assertEquals(a, b);
    }

    @Test
    void equalsFalseForDifferentClassPaths() {
        ClassData a = new ClassData();
        a.setClassPath("A/B/C.class");
        a.setClassName("C");

        ClassData b = new ClassData();
        b.setClassPath("X/Y/Z.class");
        b.setClassName("C");

        assertNotEquals(a, b);
    }

    @Test
    void equalsFalseForDifferentNames() {
        ClassData a = new ClassData();
        a.setClassPath("A/B/C.class");
        a.setClassName("C");

        ClassData b = new ClassData();
        b.setClassPath("A/B/C.class");
        b.setClassName("Different");

        assertNotEquals(a, b);
    }

    @Test
    void parameterConstructorAssignsAllFields() {
        ArrayList<Field> fields = new ArrayList<>();
        fields.add(new Field("x", "I", null, Opcodes.ACC_PUBLIC));

        ArrayList<Method> methods = new ArrayList<>();
        methods.add(new Method("foo", "()V", null, Opcodes.ACC_PUBLIC));

        ArrayList<String> interfaces = new ArrayList<>();
        interfaces.add("java/io/Serializable");

        ClassData cd = new ClassData(
                "A/B/C.class",
                "C",
                fields,
                methods,
                interfaces,
                "java/lang/Object",
                Opcodes.ACC_PUBLIC
        );

        assertEquals("A/B/C.class", cd.getClassPath());
        assertEquals("C", cd.getClassName());
        assertEquals(fields, cd.getFields());
        assertEquals(methods, cd.getMethods());
        assertEquals(interfaces, cd.getInterfaces());
        assertEquals("java/lang/Object", cd.getSuperClass());
        assertEquals(Opcodes.ACC_PUBLIC, cd.getAccess());
    }

}
