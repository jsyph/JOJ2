package com.example.joj2;

import java.sql.*;

public class RunTracker {
    private static RunTracker instance = null;

    private RunTracker() {
    }

    public static RunTracker getInstance() {
        if (instance == null) {
            instance = new RunTracker();
        }
        return instance;
    }

    /**
     * Returns the total number of unique problems solved in the database.
     */
    public int getSolvedCount() {
        // Use DISTINCT so we don't count the same problem twice
        String sql = "SELECT COUNT(DISTINCT problem_id) FROM runs WHERE verdict = 'Accepted'";
        try (PreparedStatement pstmt = Database.getInstance().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting solved count: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Calculates how many problems are left by comparing the total problems
     * to the number of solved ones.
     */
    public int getRemainingCount() {
        int totalProblems = Dealer.getInstance().getProblems().size();
        return totalProblems - getSolvedCount();
    }

    public void recordRun(int problemID, SubmissionVerdict verdict, int passed, int total, long timeMs) {
        String sql = """
                INSERT INTO runs (problem_id, verdict, passed_cases, total_cases, execution_time_ms) 
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement pstmt = Database.getInstance().prepareStatement(sql)) {
            pstmt.setInt(1, problemID);
            pstmt.setString(2, verdict.toString());
            pstmt.setInt(3, passed);
            pstmt.setInt(4, total);
            pstmt.setLong(5, timeMs);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to record run: " + e.getMessage());
        }
    }

    public int getBestScore(int problemID) {
        String sql = "SELECT MAX(passed_cases) FROM runs WHERE problem_id = ?";
        try (PreparedStatement pstmt = Database.getInstance().prepareStatement(sql)) {
            pstmt.setInt(1, problemID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalTries(int problemID) {
        String sql = "SELECT COUNT(*) FROM runs WHERE problem_id = ?";
        try (PreparedStatement pstmt = Database.getInstance().prepareStatement(sql)) {
            pstmt.setInt(1, problemID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean isSolved(int problemID) {
        String sql = "SELECT 1 FROM runs WHERE problem_id = ? AND verdict = 'Accepted' LIMIT 1";
        try (PreparedStatement pstmt = Database.getInstance().prepareStatement(sql)) {
            pstmt.setInt(1, problemID);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}