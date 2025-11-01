package me.peterferencz.app.jar;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;

public class Prettier {

    public static String getUMLAccessChar(int access){
        if ((access & Opcodes.ACC_PUBLIC) != 0) return "+";
        if ((access & Opcodes.ACC_PROTECTED) != 0) return "#";
        if ((access & Opcodes.ACC_PRIVATE) != 0) return "-";
        return "~"; // package-private
    }

    public static String getUML(Field field){
        String pre = getUMLAccessChar(field.getAccess())
            + field.getName()
            + ": ";
        if(field.getSignature() != null){
            return pre + prettySignature(field.getSignature());
        }else{
            return pre + prettyDescriptor(field.getDescriptor());
        }
    }

    public static String getUML(Method method) {
        String pre = getUMLAccessChar(method.getAccess())
            + method.getName();
        if(method.getSignature() != null){
            return pre + prettySignature(method.getSignature());
        } else {
            return pre + prettyDescriptor(method.getDescriptor());
        }
    }

    // public static String prettyDescriptor(Field field){
    //     String descriptor = Type.getType(field.getDescriptor()).getClassName();
    //     return descriptor.substring(descriptor.lastIndexOf(".") + 1);
    // }

    // public static String prettySignature(Field field){
    //     if(field.getSignature() == null) return "";
    //     String descriptor = field.getSignature();

    //     return descriptor
    //         .replaceAll("Ljava/lang/", "")
    //         .replaceAll("Ljava/util/", "")
    //         .replaceAll(";", "")
    //         .replaceAll("/", ".");
    // }



    


    public static String prettyDescriptor(String descriptor) {
        int start = descriptor.indexOf('(');
        int end = descriptor.indexOf(')');

        if(start == -1 || end == -1){
            return parseNextType(descriptor, 0).type;
        }

        String[] params = parseMultipleTypes(descriptor.substring(start + 1, end));
        String returnType = parseNextType(descriptor.substring(end + 1), 0).type;

        return "(" + String.join(", ", params) + "): " + returnType;
    }

    public static String prettySignature(String signature){
        int start = signature.indexOf('(');
        int end = signature.indexOf(')');
        if(start == -1 || end == -1){
            return parseNextType(signature, 0).type;
        }

        String[] params = parseMultipleTypes(signature.substring(start + 1, end));
        String returnType = parseNextType(signature.substring(end + 1), 0).type;

        return "(" + String.join(", ", params) + "): " + returnType;
    }
    
    private static String simpleName(String internalName) {
        int idx = internalName.lastIndexOf('/');
        if (idx >= 0) internalName = internalName.substring(idx + 1);
        return internalName;
    }

    private static class ParsedType {
        String type;
        int nextIndex;
        ParsedType(String t, int i) { this.type = t; this.nextIndex = i; }
    }

    private static String[] parseMultipleTypes(String sig) {
        List<String> types = new ArrayList<>();
        for (int i = 0; i < sig.length(); ) {
            ParsedType parsed = parseNextType(sig, i);
            types.add(parsed.type);
            i = parsed.nextIndex;
        }
        return types.toArray(new String[0]);
    }

    private static ParsedType parseNextType(String sig, int start) {
        if (start >= sig.length()) return new ParsedType("", start);

        char c = sig.charAt(start);
        switch (c) {
            case 'B': return new ParsedType("byte", start + 1);
            case 'C': return new ParsedType("char", start + 1);
            case 'D': return new ParsedType("double", start + 1);
            case 'F': return new ParsedType("float", start + 1);
            case 'I': return new ParsedType("int", start + 1);
            case 'J': return new ParsedType("long", start + 1);
            case 'S': return new ParsedType("short", start + 1);
            case 'Z': return new ParsedType("boolean", start + 1);
            case 'V': return new ParsedType("void", start + 1);
            case '[': {
                ParsedType inner = parseNextType(sig, start + 1);
                return new ParsedType(inner.type + "[]", inner.nextIndex);
            }
            case 'L': {
                int idx = start + 1;
                StringBuilder sb = new StringBuilder();
                while (idx < sig.length() && sig.charAt(idx) != '<' && sig.charAt(idx) != ';') {
                    sb.append(sig.charAt(idx++));
                }
                String baseName = simpleName(sb.toString());

                StringBuilder generics = new StringBuilder();
                if (idx < sig.length() && sig.charAt(idx) == '<') {
                    idx++; // skip '<'
                    List<String> args = new ArrayList<>();
                    while (sig.charAt(idx) != '>') {
                        ParsedType arg = parseNextType(sig, idx);
                        args.add(arg.type);
                        idx = arg.nextIndex;
                    }
                    idx++; // skip '>'
                    generics.append("<").append(String.join(", ", args)).append(">");
                }

                if (idx < sig.length() && sig.charAt(idx) == ';') idx++; // skip ';'

                return new ParsedType(baseName + generics, idx);
            }
            case 'T': { // type variable
                int semi = sig.indexOf(';', start);
                String name = sig.substring(start + 1, semi);
                return new ParsedType(name, semi + 1);
            }
            default:
                return new ParsedType("?", start + 1);
        }
    }


    

    public static String prettyAccess(int access) {
        List<String> modifiers = new ArrayList<>();

        if ((access & Opcodes.ACC_PUBLIC) != 0) modifiers.add("public");
        if ((access & Opcodes.ACC_PRIVATE) != 0) modifiers.add("private");
        if ((access & Opcodes.ACC_PROTECTED) != 0) modifiers.add("protected");
        if ((access & Opcodes.ACC_STATIC) != 0) modifiers.add("static");
        if ((access & Opcodes.ACC_FINAL) != 0) modifiers.add("final");
        if ((access & Opcodes.ACC_ABSTRACT) != 0) modifiers.add("abstract");
        if ((access & Opcodes.ACC_SYNCHRONIZED) != 0) modifiers.add("synchronized");
        if ((access & Opcodes.ACC_NATIVE) != 0) modifiers.add("native");
        if ((access & Opcodes.ACC_STRICT) != 0) modifiers.add("strictfp");
        if ((access & Opcodes.ACC_TRANSIENT) != 0) modifiers.add("transient");
        if ((access & Opcodes.ACC_VOLATILE) != 0) modifiers.add("volatile");
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) modifiers.add("synthetic");
        if ((access & Opcodes.ACC_ENUM) != 0) modifiers.add("enum");
        if ((access & Opcodes.ACC_INTERFACE) != 0) modifiers.add("interface");

        return String.join(" ", modifiers);
    }
}
