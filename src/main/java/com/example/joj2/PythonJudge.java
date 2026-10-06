package com.example.joj2;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class PythonJudge implements Judge {
    @Override
    public void submit(
            Problem problem,
            Submission submission,
            Consumer<SubmissionStatus> updateCallback,
            Consumer<SubmissionVerdict> finishCallback) {

        new Thread(() -> {
            Path workDir = null;
            try {
                // Create a temporary directory for this submission
                workDir = Files.createTempDirectory("python_judge_");
                File scriptFile = new File(workDir.toFile(), "solution.py");
                Files.writeString(scriptFile.toPath(), submission.getContent());

                // 1. Syntax Check (Compile state)
                updateCallback.accept(SubmissionStatus.Compiling);
                if (!isSyntaxValid(scriptFile)) {
                    updateCallback.accept(SubmissionStatus.Finished);
                    finishCallback.accept(SubmissionVerdict.CompileError);
                    return;
                }

                // 2. Testing State
                updateCallback.accept(SubmissionStatus.Testing);
                List<TestCase> testCases = problem.getTestCases();
                SubmissionVerdict finalVerdict = SubmissionVerdict.Accepted;

                System.out.println("\nJudging Python Problem ID: " + problem.getID());

                for (int i = 0; i < testCases.size(); i++) {
                    finalVerdict = runTest(scriptFile, testCases.get(i), problem.getTimeS(), workDir.toFile());

                    // Terminal progress bar
                    printProgress(i + 1, testCases.size(), finalVerdict);

                    if (finalVerdict != SubmissionVerdict.Accepted) break;
                }
                System.out.println();

                // 3. Final State
                updateCallback.accept(SubmissionStatus.Finished);
                finishCallback.accept(finalVerdict);

            } catch (Exception e) {
                updateCallback.accept(SubmissionStatus.Error);
                e.printStackTrace();
            } finally {
                cleanup(workDir);
            }
        }).start();
    }

    private SubmissionVerdict runTest(File script, TestCase tc, int timeoutS, File workDir) {
        try {
            File inFile = new File(workDir, "in.txt");
            File outFile = new File(workDir, "out.txt");
            File errFile = new File(workDir, "err.txt");

            Files.writeString(inFile.toPath(), tc.getInput());

            // Use "python" or "python3" depending on your environment
            ProcessBuilder pb = new ProcessBuilder("python", script.getAbsolutePath());

            // REDIRECTION: Prevents hanging and increases speed
            pb.redirectInput(ProcessBuilder.Redirect.from(inFile));
            pb.redirectOutput(ProcessBuilder.Redirect.to(outFile));
            pb.redirectError(ProcessBuilder.Redirect.to(errFile));

            Process proc = pb.start();

            boolean finished = proc.waitFor(1, TimeUnit.MINUTES);

            if (!finished) {
                proc.destroyForcibly();
                return SubmissionVerdict.TimeLimitExceeded;
            }

            if (proc.exitValue() != 0) return SubmissionVerdict.RuntimeError;

            String actual = Files.readString(outFile.toPath()).trim();
            String expected = tc.getExpectedOutput().trim();

            return actual.equals(expected) ? SubmissionVerdict.Accepted : SubmissionVerdict.WrongAnswer;
        } catch (Exception e) {
            return SubmissionVerdict.SystemError;
        }
    }

    private boolean isSyntaxValid(File scriptFile) {
        try {
            // Point py_compile directly to the file to avoid stdin issues
            Process p = new ProcessBuilder("python", "-m", "py_compile", scriptFile.getAbsolutePath()).start();
            return p.waitFor(5, TimeUnit.SECONDS) && p.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void printProgress(int current, int total, SubmissionVerdict verdict) {
        int barLength = 30;
        double percentage = (double) current / total;
        int filled = (int) (barLength * percentage);

        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            if (i < filled - 1) bar.append("=");
            else if (i == filled - 1) bar.append(">");
            else bar.append("-");
        }
        bar.append("] ");

        String status = (verdict == SubmissionVerdict.Accepted) ? "OK" : verdict.toString();
        System.out.print("\r" + bar + String.format("%d%% (%d/%d) Status: %s",
                (int) (percentage * 100), current, total, status));
    }

    private void cleanup(Path dir) {
        if (dir == null) return;
        try (var files = Files.walk(dir)) {
            files.sorted(java.util.Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException ignored) {
        }
    }
}