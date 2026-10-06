package com.example.joj2;

public enum Language {
    Python,
    Cpp,
    PlainText;

    public static Language fromString(String str) {
        if (str == null) return Language.PlainText;
        return switch (str.toLowerCase()) {
            case "python" -> Language.Python;
            case "c++" -> Language.Cpp;
            default -> Language.PlainText;
        };
    }
}
