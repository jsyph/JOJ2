package com.example.joj2;

import javafx.application.Application;

import java.sql.SQLException;

public class Launcher {
    public static void main(String[] args) {
        try {
            Database.getInstance().init();
            Dealer.getInstance().loadProblems();
        } catch (SQLException e) {
            System.out.println("Database Error: " + e);
            System.exit(1);
        }

        Dealer.getInstance().shuffleProblems();

        Application.launch(JOJ.class, args);
    }
}
