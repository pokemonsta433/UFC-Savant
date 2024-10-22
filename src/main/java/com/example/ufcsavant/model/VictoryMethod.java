package com.example.ufcsavant.model;


public class VictoryMethod {
    private final String name;
    private int count;

    public VictoryMethod(String name, Integer count) {
        this.name = name;
        this.count = count;
    }

    public VictoryMethod(String name) {
        this.name = name;
        this.count = 1;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void incrementCount() {
        this.count++;
    }
}
