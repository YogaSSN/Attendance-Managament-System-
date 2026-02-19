package com.trackit.models;

public abstract class User {
    protected String id;
    protected String name;
    protected String password;
    
    public User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    
    public void setPassword(String password) { this.password = password; }
    
    public abstract String getRole();
}