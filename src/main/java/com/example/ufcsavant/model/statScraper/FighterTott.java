package com.example.ufcsavant.model.statScraper;

public class FighterTott {
    private final String fighter;
    private final String height;
    private final String weight;
    private final String reach;
    private final String stance;
    private final String dob;

    public FighterTott(String fighter, String height, String weight, String reach, String stance, String dob) {
        this.fighter = fighter;
        this.height = height;
        this.weight = weight;
        this.reach = reach;
        this.stance = stance;
        this.dob = dob;
    }

    // Getters for the fields
    public String getFighter() { return fighter; }
    public String getHeight() { return height; }
    public String getWeight() { return weight; }
    public String getReach() { return reach; }
    public String getStance() { return stance; }
    public String getDob() { return dob; }
}
