package com.example.joj2;

public enum SubmissionStatus {
    Error,
    Enqueued,
    Compiling,
    Testing,
    Finished;

    @Override
    public String toString() {
        return switch (this) {
            case Error -> "Error";
            case Enqueued -> "Enqueued";
            case Compiling -> "Compiling";
            case Testing -> "Testing";
            case Finished -> "Finished";
        };
    }
}
