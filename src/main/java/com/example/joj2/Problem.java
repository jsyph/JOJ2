package com.example.joj2;

import java.util.List;

public class Problem {
    private final int ID;
    private final String title;
    private final String content;
    private final int timeS;
    private final int memoryMB;
    private final Category category;
    private final List<TestCase> testCases;
    private final List<Solution> solutions;

    public Problem(int ID,
                   String title,
                   String content,
                   int timeS,
                   int memoryMB,
                   Category category,
                   List<TestCase> testCases,
                   List<Solution> solutions) {
        this.ID = ID;
        this.title = title;
        this.content = content;
        this.timeS = timeS;
        this.memoryMB = memoryMB;
        this.category = category;
        this.testCases = testCases;
        this.solutions = solutions;
    }

    public int getID() {
        return ID;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getTimeS() {
        return timeS;
    }

    public int getMemoryMB() {
        return memoryMB;
    }

    public Category getCategory() {
        return category;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public List<Solution> getSolutions() {
        return solutions;
    }

    @Override
    public String toString() {
        return "Problem {" +
                "\n  ID = " + ID +
                "\n  Title = '" + title + '\'' +
                "\n  Category = " + category +
                "\n  Limits = " + timeS + "s / " + memoryMB + "MB" +
                "\n  TestCases Count = " + (testCases != null ? testCases.size() : 0) +
                "\n  Solutions Count = " + (solutions != null ? solutions.size() : 0) +
                "\n  Content Preview = '" + (content.length() > 50 ? content.substring(0, 50) + "..." : content) + '\'' +
                "\n}";
    }
}
