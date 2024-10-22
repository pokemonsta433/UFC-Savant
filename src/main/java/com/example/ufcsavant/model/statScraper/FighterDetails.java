package com.example.ufcsavant.model.statScraper;

public class FighterDetails {
    private final String firstName;
    private final String lastName;
    private final String nickname;
    private final String url;

    public FighterDetails(String firstName, String lastName, String nickname, String url) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.url = url;
    }

    // Getters for the fields
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getNickname() { return nickname; }
    public String getUrl() { return url; }
}
