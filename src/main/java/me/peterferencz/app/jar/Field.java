package me.peterferencz.app.jar;

import org.objectweb.asm.Opcodes;

public class Field {
    private String name;
    private String descriptor;
    private String signature;
    private int access;
        
    public String getName() { return name; }
    public String getDescriptor() { return descriptor; }
    public String getSignature() { return signature; }
    public int getAccess() { return access; }

    public Field(String name, String descriptor, String signature, int access){
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.access = access;
    }


    public boolean isStatic(){ return (access & Opcodes.ACC_STATIC) != 0; }
    public boolean isAbstract(){ return (access & Opcodes.ACC_ABSTRACT) != 0; }

    @Override
    public String toString() {
        return name;
    }

    public String getType() {
        int idx = name.lastIndexOf("/");
        if(idx == -1){
            return name;
        }
        return name.substring(name.lastIndexOf("/"), name.length()-1);
    }
}
