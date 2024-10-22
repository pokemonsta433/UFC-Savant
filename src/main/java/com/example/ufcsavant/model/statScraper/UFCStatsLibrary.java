/*
package com.example.ufcsavant.model;

import ch.qos.logback.core.joran.sanity.Pair;
import org.apache.commons.csv.CSVRecord;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class UFCStatsLibrary {

    public static Document getSoup(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (HttpStatusException e) {
            System.err.println("HTTP error fetching URL: " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.err.println("Error fetching URL: " + e.getMessage());
            return null;
        }
    }
    public static List<Map<String, String>> parseEventDetails(Document soup) {
        List<Map<String, String>> eventDetailsList = new ArrayList<>();

        // Extract event name and URLs
        Elements eventLinks = soup.select("a.b-link.b-link_style_black");
        for (Element tag : eventLinks) {
            String eventName = tag.text().trim();
            String eventUrl = tag.attr("href");

            // Create a map to store the event details
            Map<String, String> eventDetails = new HashMap<>();
            eventDetails.put("EVENT", eventName);
            eventDetails.put("URL", eventUrl);
            eventDetailsList.add(eventDetails);
        }

        // Extract event dates
        Elements eventDates = soup.select("span.b-statistics__date");
        for (int i = 1; i < eventDates.size(); i++) { // Skip the first element
            String eventDate = eventDates.get(i).text().trim();
            eventDetailsList.get(i - 1).put("DATE", eventDate); // Add date to the corresponding event
        }

        // Extract event locations
        Elements eventLocations = soup.select("td.b-statistics__table-col.b-statistics__table-col_style_big-top-padding");
        for (int i = 1; i < eventLocations.size(); i++) { // Skip the first element
            String eventLocation = eventLocations.get(i).text().trim();
            eventDetailsList.get(i - 1).put("LOCATION", eventLocation); // Add location to the corresponding event
        }

        return eventDetailsList;
    }

    // Method to update event details in a CSV file
    /*
    public static void updateEventDetails(List<CSVRecord> eventDetails) {
        // Implementation to write event details to a CSV file
    }

    // Method to get event names from a parsed soup
    public static List<String> getEventNames(String soup) {
        // Implementation to extract event names from the soup
        return null; // Placeholder
    }

    // Method to retrieve URLs of unparsed events
    public static List<String> getEventUrls(List<String> unparsedEvents) {
        // Implementation to get URLs based on the unparsed event names
        return null; // Placeholder
    }

    // Method to load previously parsed fight details
    public static List<CSVRecord> loadParsedFightDetails() throws IOException {
        // Implementation to read and return fight details from a CSV file
        return null; // Placeholder
    }

    // Method to parse fight details from the soup
    public static List<Map<String, String>> parseFightDetails(Document soup) {
        List<Map<String, String>> fightDetailsList = new ArrayList<>();

        // Extract fight detail URLs
        Elements fightRows = soup.select("tr.b-fight-details__table-row.b-fight-details__table-row__hover.js-fight-details-click");
        List<String> fightUrls = new ArrayList<>();
        for (Element row : fightRows) {
            String fightUrl = row.attr("data-link");
            fightUrls.add(fightUrl);
        }

        // Extract fighters in the event
        Elements fighterLinks = soup.select("a.b-link.b-link_style_black");
        List<String> fightersInEvent = new ArrayList<>();
        for (Element link : fighterLinks) {
            fightersInEvent.add(link.text().trim());
        }

        // Combine fighters in pairs to create fights
        List<String> fightsInEvent = new ArrayList<>();
        for (int i = 0; i < fightersInEvent.size(); i += 2) {
            if (i + 1 < fightersInEvent.size()) {
                String bout = fightersInEvent.get(i) + " vs. " + fightersInEvent.get(i + 1);
                fightsInEvent.add(bout);
            }
        }

        // Create fight details entries
        for (int i = 0; i < fightsInEvent.size(); i++) {
            Map<String, String> fightDetails = new HashMap<>();
            fightDetails.put("BOUT", fightsInEvent.get(i));
            fightDetails.put("URL", fightUrls.get(i));
            // Add the event title
            String eventTitle = soup.selectFirst("h2.b-content__title").text().trim();
            fightDetails.put("EVENT", eventTitle);
            fightDetailsList.add(fightDetails);
        }

        return fightDetailsList;
    }
    public static List<String> parseFightResults(Document soup) {
        List<String> fightResults = new ArrayList<>();

        // Parse event name
        fightResults.add(soup.selectFirst("h2.b-content__title").text());

        // Parse fighters
        Elements fighterLinks = soup.select("a.b-link.b-fight-details__person-link");
        for (Element link : fighterLinks) {
            fightResults.add(link.text());
        }

        // Parse outcome as either 'W' for win or 'L' for loss
        Elements outcomes = soup.select("div.b-fight-details__person i");
        for (Element outcome : outcomes) {
            fightResults.add(outcome.text());
        }

        // Parse weight class
        fightResults.add(soup.selectFirst("div.b-fight-details__fight-head").text());

        // Parse win method
        fightResults.add(soup.selectFirst("i.b-fight-details__text-item_first").text());

        // Parse remaining results (round, time, time format, referee, details)
        Elements remainingResults = soup.select("p.b-fight-details__text");
        if (remainingResults.size() > 0) {
            Elements roundInfo = remainingResults.get(0).select("i.b-fight-details__text-item");
            for (Element info : roundInfo) {
                fightResults.add(info.text().trim());
            }

            // Parse details (judges and scores)
            if (remainingResults.size() > 1) {
                fightResults.add(remainingResults.get(1).text());
            }
        }

        // Clean each element in the list, removing '\n' and extra spaces
        fightResults.replaceAll(text -> text.replace("\n", "").replaceAll(" {2,}", " ").trim());

        return fightResults;
    }

    public static List<String> organiseFightResults(List<String> resultsFromSoup, List<String> fightResultsColumnNames) {
        List<String> fightResultsClean = new ArrayList<>();

        // Append event name
        fightResultsClean.add(resultsFromSoup.get(0));

        // Join fighters' names into one, e.g. fighter_a vs. fighter_b
        fightResultsClean.add(String.join(" vs. ", resultsFromSoup.subList(1, 3)));

        // Join outcome as 'w/l' or 'l/w'
        fightResultsClean.add(String.join("/", resultsFromSoup.subList(3, 5)));

        // Remove label of results using regex
        Pattern pattern = Pattern.compile("^(.+?): ?");
        for (String text : resultsFromSoup.subList(5, resultsFromSoup.size())) {
            String cleanedText = pattern.matcher(text).replaceFirst("");
            fightResultsClean.add(cleanedText.trim());
        }

        // Create a list to represent the DataFrame structure
        List<String> organizedResults = new ArrayList<>(fightResultsColumnNames);
        organizedResults.addAll(fightResultsClean);

        // Return organized results
        return organizedResults;
    }

    public static List<List<String>> parseFightStats(Document soup) {
        // Create empty lists to store each fighter's stats
        List<String> fighterAStats = new ArrayList<>();
        List<String> fighterBStats = new ArrayList<>();

        // Find all 'td' tags with the class 'b-fight-details__table-col'
        Elements statColumns = soup.select("td.b-fight-details__table-col");

        // Loop through each 'td' tag
        for (Element column : statColumns) {
            // Find all 'p' tags within the current 'td'
            Elements stats = column.select("p");

            // Loop through each 'p' tag to get stats in alternate order
            for (int index = 0; index < stats.size(); index++) {
                String statText = stats.get(index).text().trim();

                // Check if index is even, append to fighterAStats; else, append to fighterBStats
                if (index % 2 == 0) {
                    fighterAStats.add(statText);
                } else {
                    fighterBStats.add(statText);
                }
            }
        }

        // Create a list to return both fighters' stats
        List<List<String>> result = new ArrayList<>();
        result.add(fighterAStats);
        result.add(fighterBStats);

        // Return the result
        return result;
    }

    public static List<List<String>> organiseFightStats(List<String> statsFromSoup) {
        // Create a list to store organized stats
        List<List<String>> fighterStatsClean = new ArrayList<>();

        // Temporary list to hold the current fighter's stats
        List<String> currentFighterStats = new ArrayList<>();

        // Loop through the stats from the soup
        for (String stat : statsFromSoup) {
            // Check if the stat is a fighter's name
            if (currentFighterStats.isEmpty() || !stat.equals(currentFighterStats.get(0))) {
                // If it's a new fighter's name, save the previous fighter's stats if they exist
                if (!currentFighterStats.isEmpty()) {
                    fighterStatsClean.add(new ArrayList<>(currentFighterStats));
                }
                // Start a new list for the new fighter
                currentFighterStats.clear();
                currentFighterStats.add(stat); // Add the fighter's name
            } else {
                // Otherwise, add the stat to the current fighter's stats
                currentFighterStats.add(stat);
            }
        }

        // Add the last fighter's stats to the organized list
        if (!currentFighterStats.isEmpty()) {
            fighterStatsClean.add(currentFighterStats);
        }

        // Return the organized stats
        return fighterStatsClean;
    }

    public static List<List<String>> convertFightStatsToDF(List<List<String>> cleanFighterStats,
                                                           List<String> totalsColumnNames,
                                                           List<String> significantStrikesColumnNames) {
        // Create lists to store each type of stat
        List<List<String>> totalsDF = new ArrayList<>();
        List<List<String>> significantStrikesDF = new ArrayList<>();

        // Check if list of stats is empty
        if (cleanFighterStats.isEmpty()) {
            // If empty, fill with NaNs
            List<String> emptyTotalsRow = new ArrayList<>();
            for (int i = 0; i < totalsColumnNames.size(); i++) {
                emptyTotalsRow.add("NaN");
            }
            totalsDF.add(emptyTotalsRow);

            List<String> emptySignificantStrikesRow = new ArrayList<>();
            for (int i = 0; i < significantStrikesColumnNames.size(); i++) {
                emptySignificantStrikesRow.add("NaN");
            }
            significantStrikesDF.add(emptySignificantStrikesRow);
        } else {
            // Number of rounds in the fight
            int numberOfRounds = (cleanFighterStats.size() - 2) / 2;

            // For each round in fight, get stats for totals and significant strikes
            for (int round = 0; round < numberOfRounds; round++) {
                // Create a new row for totals
                List<String> totalsRow = new ArrayList<>();
                totalsRow.add("Round " + (round + 1));
                totalsRow.addAll(cleanFighterStats.get(round + 1)); // Get totals for the round
                totalsDF.add(totalsRow);

                // Create a new row for significant strikes
                List<String> significantStrikesRow = new ArrayList<>();
                significantStrikesRow.add("Round " + (round + 1));
                significantStrikesRow.addAll(cleanFighterStats.get(round + 1 + numberOfRounds)); // Get significant strikes for the round
                significantStrikesDF.add(significantStrikesRow);
            }
        }

        // Merge totals and significant strikes into a single list
        List<List<String>> fighterStatsDF = new ArrayList<>();
        for (int i = 0; i < Math.max(totalsDF.size(), significantStrikesDF.size()); i++) {
            List<String> mergedRow = new ArrayList<>();
            if (i < totalsDF.size()) {
                mergedRow.addAll(totalsDF.get(i));
            }
            if (i < significantStrikesDF.size()) {
                mergedRow.addAll(significantStrikesDF.get(i));
            }
            fighterStatsDF.add(mergedRow);
        }

        // Return the combined stats DataFrame-like structure
        return fighterStatsDF;
    }

    public static List<List<String>> combineFighterStatsDFs(List<List<String>> fighterAStatsDF,
                                                            List<List<String>> fighterBStatsDF,
                                                            Document soup) {
        // Create a new list to hold the combined stats
        List<List<String>> fightStats = new ArrayList<>();

        // Add fighter A stats to fight stats
        fightStats.addAll(fighterAStatsDF);

        // Add fighter B stats to fight stats
        fightStats.addAll(fighterBStatsDF);

        // Get name of event from soup
        String eventName = soup.select("h2.b-content__title").text().trim();

        // Add EVENT column to each row
        for (List<String> row : fightStats) {
            row.add(eventName);
        }

        // Get names of fighters from soup
        List<String> fightersNames = new ArrayList<>();
        for (var tag : soup.select("a.b-link.b-fight-details__person-link")) {
            fightersNames.add(tag.text().trim());
        }

        // Get name of bout
        String boutName = String.join(" vs. ", fightersNames);

        // Add BOUT column to each row
        for (List<String> row : fightStats) {
            row.add(boutName);
        }

        // Reorder columns: specify the columns to move as a list
        List<String> colsToMove = List.of("EVENT", "BOUT");
        fightStats = moveColumns(fightStats, colsToMove, "ROUND", "before");

        return fightStats;
    }


    // Helper method to reorder columns (you may need to implement this)
    public static List<List<String>> moveColumns(List<List<String>> data, List<String> colsToMove, String refCol, String place) {
        // Get the list of all columns from the first row
        List<String> cols = new ArrayList<>(data.get(0));

        List<String> seg1 = new ArrayList<>();
        List<String> seg2 = new ArrayList<>();
        List<String> seg3 = new ArrayList<>();

        if (place.equals("after")) {
            seg1 = cols.subList(0, cols.indexOf(refCol) + 1);
            seg2 = colsToMove;
        } else if (place.equals("before")) {
            seg1 = cols.subList(0, cols.indexOf(refCol));
            seg2 = new ArrayList<>(colsToMove);
            seg2.add(refCol);
        }

        // Remove seg2 elements from seg1
        seg1.removeAll(seg2);

        // Get remaining columns that are not in seg1 or seg2
        seg3 = new ArrayList<>(cols);
        seg3.removeAll(seg1);
        seg3.removeAll(seg2);

        // Create a new column order
        List<String> newColumnOrder = new ArrayList<>(seg1);
        newColumnOrder.addAll(seg2);
        newColumnOrder.addAll(seg3);

        // Create a new DataFrame-like structure with reordered columns
        List<List<String>> reorderedData = new ArrayList<>();
        reorderedData.add(newColumnOrder); // Add new column names as the first row

        // Reorder each row based on the new column order
        for (int i = 1; i < data.size(); i++) {
            List<String> newRow = new ArrayList<>();
            for (String col : newColumnOrder) {
                int index = cols.indexOf(col);
                newRow.add(data.get(i).get(index)); // Assuming each row follows the same column structure
            }
            reorderedData.add(newRow);
        }

        return reorderedData;
    }

    // Method to parse and organize fight results and stats from the soup
    public static Pair<List<CSVRecord>, List<CSVRecord>> parseOrganiseFightResultsAndStats(String soup, String url) {
        // Implementation to parse and organize fight results and stats
        return null; // Placeholder
    }

    // Method to write fight details to a CSV file
    public static void writeFightDetails(List<CSVRecord> fightDetails) {
        // Implementation to write fight details to a CSV file
    }

    // Method to write fight results to a CSV file
    public static void writeFightResults(List<CSVRecord> fightResults) {
        // Implementation to write fight results to a CSV file
    }

    // Method to write fight stats to a CSV file
    public static void writeFightStats(List<CSVRecord> fightStats) {
        // Implementation to write fight stats to a CSV file
    }

    // Method to load previously parsed fighter details
    public static List<CSVRecord> loadParsedFighterTott() throws IOException {
        // Implementation to read and return fighter details from a CSV file
        return null; // Placeholder
    }

    // Method to parse fighter tale of the tape from the soup
    public static FighterTott parseFighterTott(Document soup) {
        // Parse fighter name
        String fighterName = soup.select("span.b-content__title-highlight").text();

        // Parse fighter's tale of the tape
        Elements tottElements = Objects.requireNonNull(soup.select("ul.b-list__box-list").first()).select("i");

        // Create a list to store the details
        List<String> details = new ArrayList<>();
        for (Element tag : tottElements) {
            // Ensure nextSibling is cast to a Node to call text() properly
            String detail = tag.text() + (tag.nextSibling() != null ? Objects.requireNonNull(tag.nextSibling()).toString().trim() : "");
            details.add(detail);
        }

        // Clean each element
        details.replaceAll(s -> s.replace("\n", "").replace("  ", "").trim());

        // Create FighterTott object with details
        String height = (details.size() > 0) ? details.get(0) : "";
        String weight = (details.size() > 1) ? details.get(1) : "";
        String reach = (details.size() > 2) ? details.get(2) : "";
        String stance = (details.size() > 3) ? details.get(3) : "";
        String dob = (details.size() > 4) ? details.get(4) : "";

        // Return the FighterTott object
        return new FighterTott(fighterName, height, weight, reach, stance, dob);
    }

    // Method to write fighter tale of the tape to a CSV file
    public static void writeFighterTott(List<CSVRecord> fighterTott) {
        // Implementation to write fighter details to a CSV file
    }

    // Method to generate alphabetical URLs for fighters
    public static List<String> generateAlphabeticalUrls() {
        // Create a list to store fighter URLs
        List<String> listOfAlphabeticalUrls = new ArrayList<>();

        // Generate URL for each letter in the alphabet
        for (char character = 'a'; character <= 'z'; character++) {
            String url = "http://ufcstats.com/statistics/fighters?char=" + character + "&page=all";
            listOfAlphabeticalUrls.add(url);
        }

        // Return the list of URLs
        return listOfAlphabeticalUrls;
    }
    public static FighterTott organiseFighterTott(List<String> tottFromSoup, String[] fighterTottColumnNames, String url) {
        // Create a list to store cleaned fighter details
        List<String> fighterTottClean = new ArrayList<>();

        // Remove label of results using regex
        for (String text : tottFromSoup) {
            String cleanedText = text.replaceAll("^[^:]+: ?", ""); // Remove label
            fighterTottClean.add(cleanedText);
        }
        // Append URL to fighterTottClean
        fighterTottClean.add(url);

        // Create a FighterTott object with the cleaned details
        return new FighterTott(
                fighterTottClean.get(0),
            fighterTottClean.size() > 1 ? fighterTottClean.get(1) : "",
            fighterTottClean.size() > 2 ? fighterTottClean.get(2) : "",
            fighterTottClean.size() > 3 ? fighterTottClean.get(3) : "",
            fighterTottClean.size() > 4 ? fighterTottClean.get(4) : "",
            url // Assuming DOB is last in the list
        );
    }

    public static List<FighterDetails> parseFighterDetails(Document soup) {
        // Create a list to store fighter details
        List<FighterDetails> fighterDetailsList = new ArrayList<>();

        // Select all fighter name elements
        Elements nameTags = soup.select("a.b-link.b-link_style_black");
        List<String> fighterNames = new ArrayList<>();

        // Loop through and get fighter's first name, last name, nickname
        for (Element tag : nameTags) {
            fighterNames.add(tag.text());
        }

        // Parse fighter URLs
        List<String> fighterUrls = new ArrayList<>();
        for (Element tag : nameTags) {
            fighterUrls.add(tag.attr("href"));
        }

        // Zip fighters' first name, last name, nickname, and URL into FighterDetails objects
        for (int i = 0; i < fighterNames.size(); i += 3) {
            String firstName = fighterNames.get(i);
            String lastName = (i + 1 < fighterNames.size()) ? fighterNames.get(i + 1) : "";
            String nickname = (i + 2 < fighterNames.size()) ? fighterNames.get(i + 2) : "";
            String url = (i / 3 < fighterUrls.size()) ? fighterUrls.get(i / 3) : "";
            fighterDetailsList.add(new FighterDetails(firstName, lastName, nickname, url));
        }

        // Return the list of fighter details
        return fighterDetailsList;
    }

}
*/