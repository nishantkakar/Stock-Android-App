package com.reverselabs.samplestock;

/**
 * Created by Nishant on 4/15/2016.
 */
public class SuggestGetSet {
    String id,name;
    public SuggestGetSet(String id, String name){
        this.setId(id);
        this.setName(name);
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
