package com.example.joj2;

class TestCase {
    private final String input, expectedOutput;
    private final Language language;

    public TestCase(String i, String o, Language l) {
        this.input = i;
        this.expectedOutput = o;
        this.language = l;
    }

    public String getInput() {
        return input;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }
}