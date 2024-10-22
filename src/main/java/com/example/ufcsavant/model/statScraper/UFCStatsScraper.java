/*
package com.example.ufcsavant.model;

import ch.qos.logback.core.joran.sanity.Pair;
import org.apache.commons.csv.*;
import org.jsoup.nodes.Document;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.example.ufcsavant.model.UFCStatsLibrary;

public class UFCStatsScraper {

    public static void main(String[] args) throws IOException {
        // Load configuration
        Map<String, Object> config = UFCStatsLibrary.loadConfig("scrape_ufc_stats_config.yaml");

        // Check for unparsed events
        List<Map<String, String>> parsedEventDetails = UFCStatsLibrary.readCsv(config.get("event_details_file_name").toString());
        List<String> listOfParsedEvents = parsedEventDetails.stream()
                .map(event -> event.get("EVENT")).toList();

        Document soup = UFCStatsLibrary.getSoup(config.get("completed_events_all_url").toString());
        assert soup != null;
        List<Map<String, String>> updatedEventDetails = UFCStatsLibrary.parseEventDetails(soup);
        List<String> listOfAllEvents = updatedEventDetails.stream()
                .map(event -> event.get("EVENT")).toList();

        List<String> listOfUnparsedEvents = listOfAllEvents.stream()
                .filter(event -> !listOfParsedEvents.contains(event))
                .collect(Collectors.toList());

        boolean unparsedEvents = false;
        if (listOfUnparsedEvents.isEmpty()) {
            System.out.println("All available events have been parsed.");
        } else {
            unparsedEvents = true;
            System.out.println(listOfUnparsedEvents);
            UFCStatsLibrary.writeCsv(config.get("event_details_file_name").toString(), updatedEventDetails);
        }

        // Parse all missing events
        if (unparsedEvents) {
            List<Map<String, String>> parsedFightDetails = UFCStatsLibrary.readCsv(config.get("fight_details_file_name").toString());

            // Define list of URLs of missing fights to parse
            List<String> listOfUnparsedEventUrls = updatedEventDetails.stream()
                    .filter(event -> listOfUnparsedEvents.contains(event.get("EVENT")))
                    .map(event -> event.get("URL")).toList();

            List<Map<String, String>> unparsedFightDetails = new ArrayList<>();

            // Loop through each event and parse fight details
            for (String url : listOfUnparsedEventUrls) {
                soup = UFCStatsLibrary.getSoup(url);
                assert soup != null;
                List<Map<String, String>> fightDetails = UFCStatsLibrary.parseFightDetails(soup);
                unparsedFightDetails.addAll(fightDetails);
            }

            // Concatenate unparsed and parsed fight details
            parsedFightDetails.addAll(unparsedFightDetails);
            UFCStatsLibrary.writeCsv(config.get("fight_details_file_name").toString(), parsedFightDetails);

            // Parse fight results and fight stats
            List<Map<String, String>> unparsedFightResults = new ArrayList<>();
            List<Map<String, String>> unparsedFightStats = new ArrayList<>();

            List<String> listOfUnparsedFightDetailsUrls = unparsedFightDetails.stream()
                    .map(fight -> fight.get("URL")).toList();

            // Loop through each fight and parse results and stats
            for (String url : listOfUnparsedFightDetailsUrls) {
                soup = UFCStatsLibrary.getSoup(url);
                Pair<List<Map<String, String>>, List<Map<String, String>>> resultsAndStats = UFCStatsLibrary.parseOrganiseFightResultsAndStats(
                        soup,
                        url,
                        config.get("fight_results_column_names"),
                        config.get("totals_column_names"),
                        config.get("significant_strikes_column_names")
                );

                unparsedFightResults.addAll(resultsAndStats.getFirst());
                unparsedFightStats.addAll(resultsAndStats.getSecond());
            }

            // Concatenate parsed and unparsed results and stats
            List<Map<String, String>> parsedFightResults = UFCStatsLibrary.readCsv(config.get("fight_results_file_name").toString());
            List<Map<String, String>> parsedFightStats = UFCStatsLibrary.readCsv(config.get("fight_stats_file_name").toString());

            parsedFightResults.addAll(unparsedFightResults);
            parsedFightStats.addAll(unparsedFightStats);

            UFCStatsLibrary.writeCsv(config.get("fight_results_file_name").toString(), parsedFightResults);
            UFCStatsLibrary.writeCsv(config.get("fight_stats_file_name").toString(), parsedFightStats);
        }

        // Check for unparsed fighters
        List<Map<String, String>> parsedFighterDetails = UFCStatsLibrary.readCsv(config.get("fighter_details_file_name").toString());
        List<String> listOfParsedUrls = parsedFighterDetails.stream()
                .map(fighter -> fighter.get("URL")).toList();

        List<String> listOfAlphabeticalUrls = UFCStatsLibrary.generateAlphabeticalUrls();
        List<Map<String, String>> allFighterDetails = new ArrayList<>();

        // Loop through alphabetical URLs
        for (String url : listOfAlphabeticalUrls) {
            soup = UFCStatsLibrary.getSoup(url);
            List<Map<String, String>> fighterDetails = UFCStatsLibrary.parseFighterDetails(soup, config.get("fighter_details_column_names"));
            allFighterDetails.addAll(fighterDetails);
        }

        List<String> unparsedFighterUrls = allFighterDetails.stream()
                .map(fighter -> fighter.get("URL")).toList();

        List<String> listOfUnparsedFighterUrls = unparsedFighterUrls.stream()
                .filter(url -> !listOfParsedUrls.contains(url))
                .collect(Collectors.toList());

        boolean unparsedFighters = false;
        if (listOfUnparsedFighterUrls.isEmpty()) {
            System.out.println("All available fighters have been parsed.");
        } else {
            unparsedFighters = true;
            System.out.println(listOfUnparsedFighterUrls);
            UFCStatsLibrary.writeCsv(config.get("fighter_details_file_name").toString(), allFighterDetails);
        }

        // Parse all missing fighters
        if (unparsedFighters) {
            List<Map<String, String>> parsedFighterTott = UFCStatsLibrary.readCsv(config.get("fighter_tott_file_name").toString());
            List<Map<String, String>> unparsedFighterTott = new ArrayList<>();

            // Loop through unparsed fighter URLs
            for (String url : listOfUnparsedFighterUrls) {
                soup = UFCStatsLibrary.getSoup(url);
                List<String> fighterTott = UFCStatsLibrary.parseFighterTott(soup);
                Map<String, String> fighterTottMap = UFCStatsLibrary.organiseFighterTott(fighterTott, config.get("fighter_tott_column_names"), url);
                unparsedFighterTott.add(fighterTottMap);
            }

            parsedFighterTott.addAll(unparsedFighterTott);
            UFCStatsLibrary.writeCsv(config.get("fighter_tott_file_name").toString(), parsedFighterTott);
        }
    }
}
*/