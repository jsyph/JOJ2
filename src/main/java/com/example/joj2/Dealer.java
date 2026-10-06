package com.example.joj2;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class Dealer {
    private static Dealer instance = null;
    private List<Problem> problems = new ArrayList<>();
    private int currentProblemIndex = 0;

    private Dealer() {
    }

    public static Dealer getInstance() {
        if (instance == null) instance = new Dealer();
        return instance;
    }

    public Problem getProblem() {
        if (problems == null || problems.isEmpty()) return null;
        // If we ran out of problems, wrap around or return null
        if (currentProblemIndex >= problems.size()) {
            currentProblemIndex = 0; // Reset for the next broadcast session
        }
        return problems.get(currentProblemIndex++);
    }

    public List<Problem> getProblems() {
        return problems;
    }

    public void shuffleProblems() {
        // This will now work because 'problems' is a mutable ArrayList
        if (problems != null && !problems.isEmpty()) {
            Collections.shuffle(problems);
            currentProblemIndex = 0; // Reset index after shuffle
        }
    }

    public void loadProblems() throws SQLException {
        Database db = Database.getInstance();
        db.init();
        Map<Integer, ProblemDataHolder> map = new LinkedHashMap<>();

        int total = 0;
        try (ResultSet rs = db.executeSQLQuery("SELECT COUNT(*) FROM problems")) {
            if (rs.next()) total = rs.getInt(1);
        }

        String sql = "SELECT p.*, s.source_code, s.lang, t.input_data, t.output_data " +
                "FROM problems p LEFT JOIN solutions s ON p.id = s.problem_id " +
                "LEFT JOIN testcases t ON p.id = t.problem_id";

        try (PreparedStatement stmt = db.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            int count = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                if (!map.containsKey(id)) {
                    count++;
                    updateProgress(count, total);
                }
                ProblemDataHolder h = map.computeIfAbsent(id, k -> {
                    try {
                        return new ProblemDataHolder(id, rs.getString("title"), rs.getString("content"),
                                rs.getInt("time_ms") / 1000, rs.getInt("memory_mb"),
                                Category.fromString(rs.getString("category")));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                if (rs.getString("source_code") != null)
                    h.addSol(new Solution(rs.getString("source_code"), Language.fromString(rs.getString("lang"))));
                if (rs.getString("input_data") != null)
                    h.addTC(new TestCase(rs.getString("input_data"), rs.getString("output_data"), Language.PlainText));
            }
        }

        // FIXED: Collect into a mutable ArrayList instead of using .toList()
        this.problems = map.values().stream()
                .map(ProblemDataHolder::build)
                .collect(Collectors.toCollection(ArrayList::new));

        System.out.println("\n[+] Loaded " + problems.size() + " problems.");
    }

    private void updateProgress(int cur, int tot) {
        if (tot == 0) return;
        double p = (double) cur / tot;
        System.out.print("\r[" + "=".repeat((int) (p * 40)) + ">" + " ".repeat(40 - (int) (p * 40)) + "] " + (int) (p * 100) + "%");
    }

    private static class ProblemDataHolder {
        int id, t, m;
        String tit, con;
        Category c;
        List<Solution> sols = new ArrayList<>();
        List<TestCase> tcs = new ArrayList<>();
        Set<String> sSet = new HashSet<>(), tSet = new HashSet<>();

        ProblemDataHolder(int i, String ti, String co, int ts, int mb, Category ca) {
            id = i;
            tit = ti;
            con = co;
            t = ts;
            m = mb;
            c = ca;
        }

        void addSol(Solution s) {
            if (sSet.add(s.getContent())) sols.add(s);
        }

        void addTC(TestCase tc) {
            if (tSet.add(tc.getInput())) tcs.add(tc);
        }

        Problem build() {
            return new Problem(id, tit, con, t, m, c, tcs, sols);
        }
    }
}