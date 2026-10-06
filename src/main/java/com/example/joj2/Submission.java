package com.example.joj2;

import java.util.Date;

public class Submission {
    private static int IDCount = 0;

    private final int ID;
    private final int problemID;
    private final Date dateSubmitted;
    private final Language language;
    private final String content;

    public Submission(int problemID, Language language, String content) {
        this.ID = IDCount++;
        this.problemID = problemID;
        this.dateSubmitted = new Date();
        this.language = language;
        this.content = content;
    }

    public int getID() {
        return ID;
    }

    public int getProblemID() {
        return problemID;
    }

    public Date getDateSubmitted() {
        return dateSubmitted;
    }

    public Language getLanguage() {
        return language;
    }

    public String getContent() {
        return content;
    }
}
