package com.example.ufcsavant.model;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class FighterLoader {
    private List<Fighter> fighters = new ArrayList<>();
    private List<String> fighterNames = new ArrayList<>();

    public void loadFightersFromCSV() {
        fighters = new ArrayList<>();
        fighterNames = new ArrayList<>();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("derived_stats_complete.csv")) {
            assert inputStream != null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

                String line;
                br.readLine(); // Skip header line
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");

                    String name = values[0];
                    int numFights = Integer.parseInt(values[1]);
                    int wins = Integer.parseInt(values[2]);
                    int wKO = Integer.parseInt(values[3]);
                    int wSub = Integer.parseInt(values[4]);
                    int wDec = Integer.parseInt(values[5]);
                    int losses = Integer.parseInt(values[6]);
                    int lKO = Integer.parseInt(values[7]);
                    int lSub = Integer.parseInt(values[8]);
                    int lDec = Integer.parseInt(values[9]);

                    float headsKO = Float.parseFloat(values[10]);
                    float headsChin = Float.parseFloat(values[11]);
                    float tdSub = Float.parseFloat(values[12]);
                    float tdAgainstSub = Float.parseFloat(values[13]);
                    float td = Float.parseFloat(values[14]);
                    float kd = Float.parseFloat(values[15]);
                    float kdAgainst = Float.parseFloat(values[16]);
                    float tdAgainst = Float.parseFloat(values[17]);

                    Fighter fighter = new Fighter(name, numFights, wins, losses, wKO, wSub, wDec, lKO, lSub, lDec,
                            headsKO, headsChin, tdSub, tdAgainstSub, td, kd, kdAgainst, tdAgainst);
                    fighters.add(fighter);
                    fighterNames.add(fighter.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // update frequency stats from Json also
        updateFighterFrequencyStats(fighters);

    }

    public static void updateFighterFrequencyStats(List<Fighter> fighters) {
        String json = null;
        try {
            json = Files.readString(Path.of("src/main/resources/fighter_frequency_stats.json"));
        } catch (IOException e) {
            System.out.println("couldn't find JSON to update frequency stats");
            throw new RuntimeException(e);
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Map<String, Map<String, Integer>>>>(){}.getType();
        Map<String, Map<String, Map<String, Integer>>> fighterData = gson.fromJson(json, type);

        for (Fighter fighter : fighters) {
            String name = fighter.getName();
            if (fighterData.containsKey(name)) {
                Map<String, Map<String, Integer>> fighterInfo = fighterData.get(name);

                // Update common submission methods
                fighter.setCommonSubmissionMethods(fighterInfo.get("favorite_subs"));
                fighter.setCommonSubmissionMethodsAgainst(fighterInfo.get("weakness_subs"));
                fighter.setCommonKOMethods(fighterInfo.get("KO_methods"));
                fighter.setCommonChinMethods(fighterInfo.get("chin_methods"));
            }
        }
    }

    public List<Fighter> getFighters() {
        return fighters;
    }
    public List<String> getFighterNames(){
        return fighterNames;

    }
}
