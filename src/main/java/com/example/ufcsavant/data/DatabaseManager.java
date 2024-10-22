package com.example.ufcsavant.data;

import com.example.ufcsavant.model.Fighter;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DatabaseManager {
    private static DatabaseManager instance; // Static instance of the singleton
    private static final String URL = "jdbc:sqlite:data/fighters.db";

    // Private constructor to prevent instantiation
    private DatabaseManager() {
        connect(); // Optionally connect when the instance is created
    }

    // Public method to provide access to the singleton instance
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void connect() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            if (conn != null) {
                createTable(conn);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createTable(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS fighters (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "numFights INTEGER," +
                "wins INTEGER," +
                "losses INTEGER," +
                "headsKO REAL," +
                "headsChin REAL," +
                "tdSub REAL," +
                "tdAgainstSub REAL," +
                "td REAL," +
                "kd REAL," +
                "kdAgainst REAL," +
                "tdAgainst REAL" +
                ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertFighter(Fighter fighter) {
        String sql = "INSERT INTO fighters (name, numFights, wins, losses, headsKO, headsChin, " +
                "tdSub, tdAgainstSub, td, kd, kdAgainst, tdAgainst) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fighter.getName());
            pstmt.setInt(2, fighter.getNumFights());
            pstmt.setInt(3, fighter.getWins());
            pstmt.setInt(4, fighter.getLosses());
            pstmt.setFloat(5, fighter.getHeadsKO());
            pstmt.setFloat(6, fighter.getHeadsChin());
            pstmt.setFloat(7, fighter.getTdSub());
            pstmt.setFloat(8, fighter.getTdAgainstSub());
            pstmt.setFloat(9, fighter.getTd());
            pstmt.setFloat(10, fighter.getKd());
            pstmt.setFloat(11, fighter.getKdAgainst());
            pstmt.setFloat(12, fighter.getTdAgainst());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public boolean fighterExists(String name) {
    String sql = "SELECT COUNT(*) FROM fighters WHERE name = ?";
    try (Connection conn = DriverManager.getConnection(URL);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, name);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0; // Return true if count > 0
        }
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
    return false;
}

public void updateFighter(Fighter fighter) {
    String sql = "UPDATE fighters SET numFights = ?, wins = ?, losses = ?, " +
                 "headsKO = ?, headsChin = ?, tdSub = ?, tdAgainstSub = ?, " +
                 "td = ?, kd = ?, kdAgainst = ?, tdAgainst = ? WHERE name = ?";
    try (Connection conn = DriverManager.getConnection(URL);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, fighter.getNumFights());
        pstmt.setInt(2, fighter.getWins());
        pstmt.setInt(3, fighter.getLosses());
        pstmt.setFloat(4, fighter.getHeadsKO());
        pstmt.setFloat(5, fighter.getHeadsChin());
        pstmt.setFloat(6, fighter.getTdSub());
        pstmt.setFloat(7, fighter.getTdAgainstSub());
        pstmt.setFloat(8, fighter.getTd());
        pstmt.setFloat(9, fighter.getKd());
        pstmt.setFloat(10, fighter.getKdAgainst());
        pstmt.setFloat(11, fighter.getTdAgainst());
        pstmt.setString(12, fighter.getName()); // Update by name
        pstmt.executeUpdate();
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
}
    public Map<String, Float> getFighterPercentiles(String fighterName) {
        Map<String, Float> percentiles = new HashMap<>();

        // Stat names without underscores
        String[] stats = {
                "numFights",
                "wins",
                "losses",
                "headsKO",
                "headsChin",
                "tdSub",
                "tdAgainstSub",
                "kdAgainst",
                "tdAgainst"
        };

        String sqlTemplate = "SELECT (COUNT(*) * 100.0 / (SELECT COUNT(*) FROM fighters)) AS percentile " +
                "FROM fighters WHERE %s < (SELECT %s FROM fighters WHERE name = ?)";

        try (Connection conn = DriverManager.getConnection(URL)) {
            for (String stat : stats) {
                String sql = String.format(sqlTemplate, stat, stat);
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, fighterName);
                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        percentiles.put(stat, rs.getFloat("percentile"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return percentiles;
    }
}
