package com.example.ufcsavant.model;

public class FightMethodDistribution {
    private int submission;
    private int decision;
    private int koTko;

    public FightMethodDistribution(int koTko, int submission, int decision) {
        this.koTko = koTko;
        this.submission = submission;
        this.decision = decision;
    }

    public int getSubmission() {
        return submission;
    }

    public int getDecision() {
        return decision;
    }

    public int getKoTko() {
        return koTko;
    }

    public void addMethod(String method) {
        if (method.contains("KO") || method.contains("TKO")) {
            koTko++;
        } else if (method.contains("Submission")) {
            submission++;
        } else if (method.contains("Decision")) {
            decision++;
        }
    }
}
