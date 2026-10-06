package com.example.joj2;

public enum SubmissionVerdict {
    CompileError,
    RuntimeError,
    SystemError,
    Accepted,
    TimeLimitExceeded,
    WrongAnswer;

    @Override
    public String toString() {
        return switch (this) {
            case CompileError -> "Compile Error";
            case RuntimeError -> "Runtime Error";
            case SystemError -> "System Error";
            case Accepted -> "Accepted";
            case TimeLimitExceeded -> "Time Limit Exceeded";
            case WrongAnswer -> "Wrong Answer";
        };
    }
}
