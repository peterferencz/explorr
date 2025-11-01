package me.peterferencz.app.jar;

import org.objectweb.asm.Opcodes;

public class Method {
    private String name;
    private String descriptor;
    private String signature;
    private int access;
        
    public String getName() { return name; }
    public String getDescriptor() { return descriptor; }
    public String getSignature() { return signature; }
    public int getAccess(){ return access; }

    public Method(String name, String descriptor, String signature, int access){
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.access = access;
    }

    public boolean isLambda(){
        return name.startsWith("lambda$");
    }

    public boolean isStatic(){
        return (access & Opcodes.ACC_STATIC) != 0;
    }

    public boolean isStaticConstructor(){
        return name.equals("<clinit>");
    }

    @Override
    public String toString() {
        return name;
    }
}
