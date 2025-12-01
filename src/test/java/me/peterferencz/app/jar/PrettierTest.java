package me.peterferencz.app.jar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

public class PrettierTest {
    
        @Test
    void umlAccessCharPublic() {
        assertEquals("+", Prettier.getUMLAccessChar(Opcodes.ACC_PUBLIC));
    }

    @Test
    void umlAccessCharProtected() {
        assertEquals("#", Prettier.getUMLAccessChar(Opcodes.ACC_PROTECTED));
    }

    @Test
    void umlAccessCharPrivate() {
        assertEquals("-", Prettier.getUMLAccessChar(Opcodes.ACC_PRIVATE));
    }

    @Test
    void umlAccessCharPackagePrivate() {
        assertEquals("~", Prettier.getUMLAccessChar(0));
    }

        @Test
    void umlFieldUsingDescriptor() {
        Field f = new Field("age", "I", null, Opcodes.ACC_PUBLIC);
        assertEquals("+age: int", Prettier.getUML(f));
    }

    @Test
    void umlFieldUsingSignature() {
        Field f = new Field(
                "names",
                null,
                "Ljava/util/List<Ljava/lang/String;>;",
                Opcodes.ACC_PRIVATE
        );
        assertEquals("-names: List<String>", Prettier.getUML(f));
    }

    @Test
    void umlMethodUsingDescriptor() {
        Method m = new Method(
                "sum",
                "(II)I",
                null,
                Opcodes.ACC_PUBLIC
        );
        assertEquals("+sum(int, int): int", Prettier.getUML(m));
    }

    @Test
    void umlMethodUsingSignature() {
        Method m = new Method(
                "map",
                null,
                "(Ljava/util/List<Ljava/lang/String;>;)Ljava/util/List<Ljava/lang/Integer;>;",
                Opcodes.ACC_PROTECTED
        );
        assertEquals("#map(List<String>): List<Integer>", Prettier.getUML(m));
    }

    @Test
    void prettyDescriptorPrimitive() {
        assertEquals("int", Prettier.prettyDescriptor("I"));
    }

    @Test
    void prettyDescriptorArray() {
        assertEquals("int[]", Prettier.prettyDescriptor("[I"));
    }

    @Test
    void prettyDescriptorMethodForm() {
        assertEquals("(int, boolean): long", Prettier.prettyDescriptor("(IZ)J"));
    }

    @Test
    void prettySignatureGenerics() {
        assertEquals("(List<String>): List<Integer>",
                Prettier.prettySignature(
                        "(Ljava/util/List<Ljava/lang/String;>;)Ljava/util/List<Ljava/lang/Integer;>;"
                ));
    }

    @Test
    void prettySignatureNestedGenerics() {
        assertEquals("(Map<String, List<Integer>>): void",
                Prettier.prettySignature(
                        "(Ljava/util/Map<Ljava/lang/String;Ljava/util/List<Ljava/lang/Integer;>;>;)V"
                ));
    }

    @Test
    void prettyAccessCommonModifiers() {
        int flags = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;
        assertEquals("public static final", Prettier.prettyAccess(flags));
    }

}
