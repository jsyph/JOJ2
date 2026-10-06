package com.example.joj2;

import java.util.function.Consumer;

public interface Judge {
    void submit(
            Problem problem,
            Submission submission,
            Consumer<SubmissionStatus> updateCallback,
            Consumer<SubmissionVerdict> finishCallback);
}
