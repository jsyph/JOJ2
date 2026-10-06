package com.example.joj2;

public class JudgeFactory {
    public static Judge getJudge(Language lang) {
        return switch (lang) {
            case Python -> new PythonJudge();
            case Cpp -> new CppJudge();
            default -> throw new IllegalArgumentException("Unsupported Language");
        };
    }
}