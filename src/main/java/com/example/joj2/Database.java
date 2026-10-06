package com.example.joj2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.zip.GZIPInputStream;

public class Database {
    private static final String DB_FILE_NAME = "joj.db";
    private static final String DB_GZ_NAME = "joj.db.gz";
    private static final String dbURL = "jdbc:sqlite:" + DB_FILE_NAME;
    private static Database instance = null;
    private Connection dbConnection;

    private Database() {
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    private void ensureDatabaseExists() {
        File dbFile = new File(DB_FILE_NAME);
        File gzFile = new File(DB_GZ_NAME);

        if (!dbFile.exists() && gzFile.exists()) {
            System.out.println("Extracting " + DB_GZ_NAME + " to " + DB_FILE_NAME + "...");
            try (GZIPInputStream gis = new GZIPInputStream(new FileInputStream(gzFile));
                 FileOutputStream fos = new FileOutputStream(dbFile)) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = gis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                System.out.println("Database extracted successfully.");
            } catch (IOException e) {
                System.err.println("Failed to extract database archive: " + e.getMessage());
            }
        }
    }

    public void init() throws SQLException {
        ensureDatabaseExists();
        if (dbConnection == null || dbConnection.isClosed()) {
            dbConnection = DriverManager.getConnection(dbURL);
            createTables();
        }
    }

    private void createTables() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS runs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    problem_id INTEGER,
                    verdict TEXT,
                    passed_cases INTEGER,
                    total_cases INTEGER,
                    execution_time_ms INTEGER,
                    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY(problem_id) REFERENCES problems(id)
                );
                """;
        executeSQLStatement(sql);
    }

    public void executeSQLStatement(String statement) throws SQLException {
        Statement s = dbConnection.createStatement();
        s.execute(statement);

    }

    public ResultSet executeSQLQuery(String statement) throws SQLException {
        Statement s = dbConnection.createStatement();
        return s.executeQuery(statement);
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return dbConnection.prepareStatement(sql);
    }
}