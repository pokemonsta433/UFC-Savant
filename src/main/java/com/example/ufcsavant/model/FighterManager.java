/*
package com.example.ufcsavant.model;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FighterManager {
        private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");
    private static HashMap<String, Fighter> fightersDict = new HashMap<>();

    public static List<Fighter> createFightersFromStatScrapes(String fightResultsPath, String fightStatsPath) {
        parseFightResults(fightResultsPath);
        parseFightStats(fightStatsPath);
        calculateAdvancedStats();
        return new ArrayList<>(fightersDict.values());
    }

    private static void parseFightResults(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                String event = row[0].trim();
                String bout = row[1].trim();
                String outcome = row[2].trim();
                String method = row[4].trim();
                int rnd = extractNumber(row[5].trim());
                String details = row[9].trim().replace("Punch ", "Punch(es) ").replace("Punches ", "Punch(es) ");

                String[] fighters = bout.split("vs.");
                String fighter1 = fighters[0].trim();
                String fighter2 = fighters[1].trim();

                Fighter f1 = fightersDict.computeIfAbsent(fighter1, name -> new Fighter(name, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f));
                Fighter f2 = fightersDict.computeIfAbsent(fighter2, name -> new Fighter(name, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f));

                updateFighterFightStats(f1, outcome.equals("W/L"), method, rnd, fighter2, details);
                updateFighterFightStats(f2, outcome.equals("L/W"), method, rnd, fighter1, details);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateFighterFightStats(Fighter fighter, boolean result, String method, int round, String opponent, String details) {
        // Update the fighter's fight statistics
        fighter.incrementFightCount();
        if (result) {
            fighter.incrementWins();
            fighter.getWinMethods().addMethod(method);

            // Update submission methods for wins
            if (method.contains("Submission")) {
                fighter.updateSubmissionMethods(details);
            }
        } else {
            fighter.incrementLosses();
            fighter.getLossMethods().addMethod(method);

            // Update submission methods against for losses
            if (method.contains("Submission")) {
                fighter.updateSubmissionMethodsAgainst(details);
            }
        }

        // Handle KO/TKO and other statistics if applicable
        if (method.contains("KO/TKO")) {
            fighter.updateHeadsKO(round);
        }
    }

    private static void parseFightStats(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                String event = row[0].trim();
                int rnd = extractNumber(row[2].trim());
                String fighterName = row[3].trim();

                // Extract stats
                int kd = extractNumber(row[4].trim());
                int sstr = extractNumber(row[5].trim());
                int tstr = extractNumber(row[7].trim());
                int td = extractNumber(row[8].trim());

                Fighter fighter = fightersDict.get(fighterName);
                if (fighter != null) {
                    fighter.addFightStats(rnd, kd, sstr, tstr, td);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void calculateAdvancedStats() {
        for (Fighter fighter : fightersDict.values()) {
            fighter.calculateAdvancedStats();
        }
    }

    private static int extractNumber(String input) {
        Matcher matcher = NUMBER_PATTERN.matcher(input);
        return matcher.find() ? Integer.parseInt(matcher.group()) : 0;
    }
}
*/