package com.example.ufcsavant.initializer;

import com.example.ufcsavant.model.Fighter;
import com.example.ufcsavant.model.FighterLoader;
import com.example.ufcsavant.data.DatabaseManager;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class ApplicationInitializer {

    private final FighterLoader fighterLoader;
    private final DatabaseManager dbManager;

    @Autowired
    public ApplicationInitializer(FighterLoader fighterLoader, DatabaseManager dbManager) {
        this.fighterLoader = fighterLoader;
        System.out.println("loading fighters");
        this.fighterLoader.loadFightersFromCSV();
        System.out.println("loaded " + this.fighterLoader.getFighterNames().size() + " fighters");
        this.dbManager = dbManager;
    }

    @PostConstruct
    public void initializeDatabase() {
        List<Fighter> fighters = fighterLoader.getFighters();

        for (Fighter fighter : fighters) {
            if (fighterExists(fighter.getName())) {
                dbManager.updateFighter(fighter);
            } else {
                dbManager.insertFighter(fighter);
            }
        }
    }

    private boolean fighterExists(String name) {
        return dbManager.fighterExists(name);
    }
}
