package com.example.ufcsavant.model;
import java.util.*;

public class Fighter {
    private final String name;
    private int numFights;
    private int wins;
    private int losses;
    private FightMethodDistribution winMethods;
    private FightMethodDistribution lossMethods;

    private Map<String, Integer> commonSubmissionMethods;
    private Map<String, Integer> commonSubmissionMethodsAgainst;
    private Map<String, Integer> commonKOMethods;
    private Map<String, Integer> commonChinMethods;
    private float headsKO;
    private float headsChin;
    private float tdSub;
    private float tdAgainstSub;
    private float td;
    private float kd;
    private float kdAgainst;
    private float tdAgainst;

    public Fighter(String name, int numFights, int wins, int losses,
                   int wKO, int wSub, int wDec,
                   int lKO, int lSub, int lDec,
                   float headsKO, float headsChin, float tdSub,
                   float tdAgainstSub, float td, float kd,
                   float kdAgainst, float tdAgainst) {
        this.name = name;
        this.numFights = numFights;
        this.wins = wins;
        this.losses = losses;
        this.winMethods = new FightMethodDistribution(wKO, wSub, wDec);
        this.lossMethods = new FightMethodDistribution(lKO, lSub, lDec);

        this.headsKO = headsKO;
        this.headsChin = headsChin;
        this.tdSub = tdSub;
        this.tdAgainstSub = tdAgainstSub;
        this.td = td;
        this.kd = kd;
        this.kdAgainst = kdAgainst;
        this.tdAgainst = tdAgainst;

        this.commonSubmissionMethods = new HashMap<>();
        this.commonSubmissionMethodsAgainst = new HashMap<>();
        this.commonKOMethods = new HashMap<>();
        this.commonChinMethods = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public int getNumFights() {
        return numFights;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public float getHeadsKO() {
        return headsKO;
    }

    public float getHeadsChin() {
        return headsChin;
    }

    public float getTdSub() {
        return tdSub;
    }

    public float getTdAgainstSub() {
        return tdAgainstSub;
    }

    public float getTd() {
        return td;
    }

    public float getKd() {
        return kd;
    }

    public float getKdAgainst() {
        return kdAgainst;
    }

    public float getTdAgainst() {
        return tdAgainst;
    }

    public FightMethodDistribution getWinMethods() {
        return winMethods;
    }

    public FightMethodDistribution getLossMethods() {
        return lossMethods;
    }


    public List<VictoryMethod> getCommonSubmissionMethods() {
        List<VictoryMethod> victoryMethodsList = new ArrayList<>();

        // Populate the list from the map
        for (Map.Entry<String, Integer> entry : commonSubmissionMethods.entrySet()) {
            victoryMethodsList.add(new VictoryMethod(entry.getKey(), entry.getValue()));
        }

        // Sort the list by count in descending order
        victoryMethodsList.sort(Comparator.comparingInt(VictoryMethod::getCount).reversed());

        return victoryMethodsList;
    }

    public List<VictoryMethod> getCommonSubmissionMethodsAgainst() {
        List<VictoryMethod> victoryMethodsList = new ArrayList<>();

        // Populate the list from the map
        for (Map.Entry<String, Integer> entry : commonSubmissionMethodsAgainst.entrySet()) {
            victoryMethodsList.add(new VictoryMethod(entry.getKey(), entry.getValue()));
        }

        // Sort the list by count in descending order
        victoryMethodsList.sort(Comparator.comparingInt(VictoryMethod::getCount).reversed());

        return victoryMethodsList;
    }

    public List<VictoryMethod> getCommonKOMethods() {
        List<VictoryMethod> victoryMethodsList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : commonKOMethods.entrySet()) {
            victoryMethodsList.add(new VictoryMethod(entry.getKey(), entry.getValue()));
        }
        victoryMethodsList.sort(Comparator.comparingInt(VictoryMethod::getCount).reversed());
        return victoryMethodsList;
    }

    public List<VictoryMethod> getCommonChinMethods() {
        List<VictoryMethod> victoryMethodsList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : commonChinMethods.entrySet()) {
            victoryMethodsList.add(new VictoryMethod(entry.getKey(), entry.getValue()));
        }
        victoryMethodsList.sort(Comparator.comparingInt(VictoryMethod::getCount).reversed());

        return victoryMethodsList;
    }



    public void incrementFightCount() {
        this.numFights++;
    }

    public void incrementWins() {
        this.wins++;
    }

    public void incrementLosses() {
        this.losses++;
    }
    public void updateSubmissionMethods(String details) {
        // Check if the submission method already exists in the map
        if (commonSubmissionMethods.containsKey(details)) {
            // If it exists, increment the count
            commonSubmissionMethods.put(details, commonSubmissionMethods.get(details) + 1);
        } else {
            // If it doesn't exist, add it with a count of 1
            commonSubmissionMethods.put(details, 1);
        }
    }

    public void updateSubmissionMethodsAgainst(String details) {
        // Check if the submission method already exists in the map
        if (commonSubmissionMethodsAgainst.containsKey(details)) {
            // If it exists, increment the count
            commonSubmissionMethodsAgainst.put(details, commonSubmissionMethodsAgainst.get(details) + 1);
        } else {
            // If it doesn't exist, add it with a count of 1
            commonSubmissionMethodsAgainst.put(details, 1);
        }
    }

    public void setCommonSubmissionMethods(Map<String, Integer> commonSubmissionMethods) {
        this.commonSubmissionMethods = commonSubmissionMethods;
    }

    public void setCommonSubmissionMethodsAgainst(Map<String, Integer> commonSubmissionMethodsAgainst) {
        this.commonSubmissionMethodsAgainst = commonSubmissionMethodsAgainst;
    }

    public void setCommonKOMethods(Map<String, Integer> commonKOMethods) {
        this.commonKOMethods = commonKOMethods;
    }

    public void setCommonChinMethods(Map<String, Integer> commonChinMethods) {
        this.commonChinMethods = commonChinMethods;
    }

}

