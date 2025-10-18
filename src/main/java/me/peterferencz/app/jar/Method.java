package me.peterferencz.app.jar;

public class Method {
    private String name;
    private String descriptor;
    private String signature;
        
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescriptor() { return descriptor; }
    public void setDescriptor(String descriptor) { this.descriptor = descriptor; }
    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
    
    public Method(String name, String descriptor, String signature){
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
    }
}
