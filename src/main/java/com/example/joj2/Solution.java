package com.example.joj2;

public class Solution {
    private final String content;
    private final Language language;

    public Solution(String content, Language language) {
        this.content = content;
        this.language = language;
    }

    public String getContent() {
        return content;
    }

    public Language getLanguage() {
        return language;
    }
}
