package com.example.joj2;

public enum Category {
    IntroductoryProblems,
    SortingAndSearch;

    public static Category fromString(String str) {
        if (str == null) return Category.IntroductoryProblems;
        return switch (str) {
            case "Sorting and Searching" -> Category.SortingAndSearch;
            default -> Category.IntroductoryProblems;
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case IntroductoryProblems -> "Introductory Problems";
            case SortingAndSearch -> "Sorting and Searching";
        };
    }
}
