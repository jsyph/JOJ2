package com.example.joj2;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class CppJudge implements Judge {
    @Override
    public void submit(
            Problem problem,
            Submission submission,
            Consumer<SubmissionStatus> updateCallback,
            Consumer<SubmissionVerdict> finishCallback) {

        new Thread(() -> {
            Path workDir = null;
            try {
                workDir = Files.createTempDirectory("cpp_judge_");
                File sourceFile = new File(workDir.toFile(), "solution.cpp");

                String os = System.getProperty("os.name").toLowerCase();
                String exeName = os.contains("win") ? "solution.exe" : "./solution.out";
                File exeFile = new File(workDir.toFile(), exeName);

                Files.writeString(sourceFile.toPath(), submission.getContent());

                // 1. Compile
                updateCallback.accept(SubmissionStatus.Compiling);
                if (!compile(sourceFile, exeFile)) {
                    updateCallback.accept(SubmissionStatus.Finished);
                    finishCallback.accept(SubmissionVerdict.CompileError);
                    return;
                }

                // 2. Test
                updateCallback.accept(SubmissionStatus.Testing);
                List<TestCase> testCases = problem.getTestCases();
                SubmissionVerdict finalVerdict = SubmissionVerdict.Accepted;

                for (int i = 0; i < testCases.size(); i++) {
                    // Pass workDir to use file-based I/O
                    finalVerdict = runTest(exeFile, testCases.get(i), problem.getTimeS(), workDir.toFile());

                    printProgress(i + 1, testCases.size(), finalVerdict);
                    if (finalVerdict != SubmissionVerdict.Accepted) break;
                }

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

    private SubmissionVerdict runTest(File exe, TestCase tc, int timeoutS, File workDir) {
        try {
            // Create temporary files for this specific test case
            File inFile = new File(workDir, "in.txt");
            File outFile = new File(workDir, "out.txt");

            Files.writeString(inFile.toPath(), tc.getInput());

            ProcessBuilder pb = new ProcessBuilder(exe.getAbsolutePath());

            pb.redirectInput(ProcessBuilder.Redirect.from(inFile));
            pb.redirectOutput(ProcessBuilder.Redirect.to(outFile));
            pb.redirectError(ProcessBuilder.Redirect.to(new File(workDir, "err.txt")));

            Process process = pb.start();

            boolean finished = process.waitFor(1, TimeUnit.MINUTES);

            if (!finished) {
                process.destroyForcibly();
                return SubmissionVerdict.TimeLimitExceeded;
            }

            if (process.exitValue() != 0) return SubmissionVerdict.RuntimeError;

            String actual = Files.readString(outFile.toPath()).trim();
            String expected = tc.getExpectedOutput().trim();

            return actual.equals(expected) ? SubmissionVerdict.Accepted : SubmissionVerdict.WrongAnswer;

        } catch (Exception e) {
            e.printStackTrace();
            return SubmissionVerdict.RuntimeError;
        }
    }

    private void printProgress(int current, int total, SubmissionVerdict verdict) {
        int barLength = 30;
        double percentage = (double) current / total;
        int filledLength = (int) (barLength * percentage);

        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            if (i < filledLength - 1) bar.append("=");
            else if (i == filledLength - 1) bar.append(">");
            else bar.append("-");
        }
        bar.append("] ");

        String status = (verdict == SubmissionVerdict.Accepted) ? "OK" : verdict.toString();

        System.out.print("\r" + bar.toString() + String.format("%d%% (%d/%d) Status: %s",
                (int) (percentage * 100), current, total, status));

        if (current == total || verdict != SubmissionVerdict.Accepted) {
            System.out.flush();
        }
    }

    private boolean compile(File source, File output) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                "g++", "-O2", source.getAbsolutePath(), "-o", output.getAbsolutePath()
        );
        Process p = pb.start();
        boolean finished = p.waitFor(30, TimeUnit.SECONDS);
        return finished && p.exitValue() == 0;
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