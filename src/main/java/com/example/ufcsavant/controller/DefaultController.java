package com.example.ufcsavant.controller;

import com.example.ufcsavant.data.DatabaseManager;
import com.example.ufcsavant.model.Fighter;
import com.example.ufcsavant.model.FighterLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@ControllerAdvice
public class DefaultController {

    private final FighterLoader fighterLoader;

    @Autowired
    private DatabaseManager databaseManager;


    @Autowired
    public DefaultController(FighterLoader fighterLoader) {
        this.fighterLoader = fighterLoader;
    }

    @GetMapping(value = "/")
    public String index() {
        return "home";
    }

    @GetMapping(value = "/fighter/{name}")
    public String fighter(@PathVariable String name, Model model) {
        String fighterName = name.replace("+", " ");

        List<Fighter> fighters = fighterLoader.getFighters();

        Optional<Fighter> optionalFighter = fighters.stream()
                .filter(f -> f.getName().equalsIgnoreCase(fighterName))
                .findFirst();

        if (optionalFighter.isPresent()) {
            Fighter fighter = optionalFighter.get();
            model.addAttribute("fighter", fighter);

            // for percentile graphs
            Map<String, Float> percentiles = databaseManager.getFighterPercentiles(name);
            model.addAttribute("percentiles", percentiles);
            model.addAttribute("fighterName", name);

            return "fighter";
        } else {
            return "error-fighter-not-found";
        }
    }

     @GetMapping("/fighter-percentiles")
    public String getFighterPercentiles(@RequestParam String name, Model model) {
        Map<String, Float> percentiles = databaseManager.getFighterPercentiles(name);
        model.addAttribute("percentiles", percentiles);
        model.addAttribute("fighterName", name);
        return "fighter-percentiles";
    }


    @RestController
    @RequestMapping("/api")
    public class FighterController {

        @GetMapping("/allFighters")
        public ResponseEntity<List<String>> getAllFighters() {
            List<String> fighterNames = fighterLoader.getFighterNames();
            return ResponseEntity.ok(fighterNames);
        }

        @GetMapping("/fighters")
        public ResponseEntity<List<String>> searchFighters(@RequestParam String search) {
            List<String> allFighters = fighterLoader.getFighterNames();
            List<String> matchedFighters = new ArrayList<>();

            for (String fighter : allFighters) {
                if (fighter.toLowerCase().startsWith(search.toLowerCase())) {
                    matchedFighters.add(fighter);
                } else {
                    String[] nameParts = fighter.split(" ");
                    for (String part : nameParts) {
                        if (part.toLowerCase().startsWith(search.toLowerCase())) {
                            matchedFighters.add(fighter);
                            break;
                        }
                    }
                }
            }

            return ResponseEntity.ok(matchedFighters);
        }

    }
}
