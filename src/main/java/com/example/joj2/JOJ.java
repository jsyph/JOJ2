package com.example.joj2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class JOJ extends Application {

    private Stage primaryStage;
    private int secondsRemaining = 1200;
    private int score = 0;
    private int sessionSolvedCount = 0;
    private boolean isJudging = false;

    private final String countDownGIFURL = Objects.requireNonNull(getClass().getResource("clock.gif")).toString();
    private final String loosePageGIFURL = Objects.requireNonNull(getClass().getResource("judge.gif")).toString();
    private final String helpPageGIFURL = Objects.requireNonNull(getClass().getResource("shush.gif")).toString();
    private static final String startPageGIFURL = "https://media1.giphy.com/media/v1.Y2lkPTc5MGI3NjExNnZncDZ4ZXhqNXhwbnMxeHBrZ3RoOWM2dWFzN29tN2czcWJidGY1ZCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/a5viI92PAF89q/giphy.gif";
    private final AudioClip winSound = new AudioClip(Objects.requireNonNull(getClass().getResource("you-win.mp3")).toString());
    private final AudioClip loseSound = new AudioClip(Objects.requireNonNull(getClass().getResource("game-over.mp3")).toString());
    private final AudioClip startSound = new AudioClip(Objects.requireNonNull(getClass().getResource("bell.mp3")).toString());
    private static final String POP_YELLOW = "#F9E076";
    private static final String POP_MAGENTA = "#EF476F";
    private static final String POP_CYAN = "#118AB2";
    private static final String POP_WHITE = "#FDFFFC";
    private static final int countDownMax = 3;

    private final Label timerLabel = new Label("20:00");
    private final Label scoreLabel = new Label("SCORE: 0");
    private final Label progressLabel = new Label("PROGRESS: 0 / 0");
    private final Label statusLabel = new Label("JUDGE: READY");
    private Timer gameTimer;
    private WebView codeEditorView;
    private HBox footer;


    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        showStartPage();
    }

    private void showStartPage() {
        if (gameTimer != null) gameTimer.cancel();
        secondsRemaining = 1200;
        score = 0;
        sessionSolvedCount = 0;
        isJudging = false;

        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: " + POP_WHITE + "; -fx-border-color: black; -fx-border-width: 4;");

        ImageView startPageGIF = new ImageView(new Image(startPageGIFURL));
        startPageGIF.setFitWidth(330);
        startPageGIF.setPreserveRatio(true);

        Label title = new Label("Welcome to JOJ!");
        title.setStyle("-fx-text-fill: black; -fx-font-size: 42px; -fx-font-family: 'Verdana'; -fx-font-weight: bold;");

        GridPane statsTable = new GridPane();
        statsTable.setHgap(20);
        statsTable.setVgap(10);
        statsTable.setAlignment(Pos.CENTER);
        statsTable.setStyle("-fx-background-color: " + POP_YELLOW + "; -fx-border-color: black; -fx-border-width: 3; -fx-padding: 20;");

        addStatRow(statsTable, "SOLVED BY YOU:", String.valueOf(RunTracker.getInstance().getSolvedCount()), 0);
        addStatRow(statsTable, "TIME LIMIT:", "20:00", 1);

        Button startBtn = new Button("START CHALLENGE");
        startBtn.setStyle("-fx-background-color: " + POP_CYAN + "; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-border-color: black;");
        startBtn.setOnAction(e -> {
            showCountdown();
        });

        root.getChildren().addAll(startPageGIF, title, statsTable, startBtn);
        primaryStage.setScene(new Scene(root, 1200, 900));
        primaryStage.show();
    }

    private void showCountdown() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: black; -fx-border-color: " + POP_CYAN + "; -fx-border-width: 10;");

        Label timerNum = new Label(String.valueOf(countDownMax));
        timerNum.setStyle("-fx-text-fill: white; -fx-font-size: 140px; -fx-font-weight: bold;");

        ImageView countDownGIF = new ImageView(new Image(countDownGIFURL));
        countDownGIF.setFitHeight(330);
        countDownGIF.setPreserveRatio(true);

        root.getChildren().addAll(countDownGIF, timerNum);
        primaryStage.getScene().setRoot(root);

        Timer transitionTimer = new Timer(true);
        transitionTimer.scheduleAtFixedRate(new TimerTask() {
            int remaining = countDownMax;

            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (remaining > 1) {
                        remaining--;
                        timerNum.setText(String.valueOf(remaining));
                    } else {
                        startSound.play();
                        transitionTimer.cancel();
                        showCodingPage();
                    }
                });
            }
        }, 1000, 1000);
    }

    private void showCodingPage() {
        Problem problem = Dealer.getInstance().getProblem();
        if (problem == null) {
            showStartPage();
            return;
        }

        isJudging = false;
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: " + POP_WHITE + ";");

        progressLabel.setText("SOLVED: " + sessionSolvedCount);
        scoreLabel.setText("SCORE: " + score);
        HBox topBar = new HBox(40, timerLabel, progressLabel, scoreLabel);
        topBar.setPadding(new Insets(15));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: " + POP_CYAN + "; -fx-border-color: black; -fx-border-width: 0 0 4 0;");
        timerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        progressLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");
        scoreLabel.setStyle("-fx-text-fill: " + POP_YELLOW + "; -fx-font-size: 24px; -fx-font-weight: bold;");
        layout.setTop(topBar);

        WebView descView = new WebView();
        descView.getEngine().loadContent(renderPopArtHtml(problem));
        codeEditorView = new WebView();
        codeEditorView.getEngine().loadContent(renderAceEditor());

        SplitPane split = new SplitPane(descView, codeEditorView);
        split.setDividerPositions(0.4);
        layout.setCenter(split);

        footer = new HBox(20);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(15));
        footer.setStyle("-fx-background-color: " + POP_YELLOW + "; -fx-border-color: black; -fx-border-width: 4 0 0 0;");

        final Language[] selectedLang = {Language.Cpp};
        Button hintBtn = createHintButton(problem);
        Button langBtn = createLangButton(selectedLang);
        Button submitBtn = createSubmitButton(problem, selectedLang);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        footer.getChildren().addAll(statusLabel, spacer, hintBtn, langBtn, submitBtn);
        layout.setBottom(footer);

        startTimer();
        primaryStage.getScene().setRoot(layout);
    }

    private void renderGameOverUI(String reasonText) {
        if (gameTimer != null) gameTimer.cancel();
        loseSound.play();

        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: " + POP_YELLOW + "; -fx-border-color: black; -fx-border-width: 5;");

        Label msg = new Label("GAME OVER!");
        msg.setStyle("-fx-text-fill: " + POP_MAGENTA + "; -fx-font-size: 50px; -fx-font-weight: bold;");
        Label vLabel = new Label("REASON: " + reasonText + " | FINAL SCORE: " + score);
        vLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        ImageView loosePageGIF = new ImageView(new Image(loosePageGIFURL));
        loosePageGIF.setFitWidth(300);
        loosePageGIF.setPreserveRatio(true);

        Button backBtn = new Button("TRY AGAIN?");
        backBtn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 20px; -fx-padding: 10 40;");
        backBtn.setOnAction(e -> showStartPage());

        root.getChildren().addAll(msg, vLabel, loosePageGIF, backBtn);
        primaryStage.getScene().setRoot(root);
    }

    private Button createHintButton(Problem problem) {
        Button b = new Button("HINT");
        b.setStyle("-fx-background-color: " + POP_MAGENTA + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: black;");
        b.setOnAction(e -> {
            Stage hintStage = new Stage();
            hintStage.initOwner(primaryStage);
            VBox hRoot = new VBox(15);
            hRoot.setAlignment(Pos.CENTER);
            hRoot.setPadding(new Insets(20));
            hRoot.setStyle("-fx-background-color: " + POP_YELLOW + "; -fx-border-color: black; -fx-border-width: 5;");

            ImageView helpPageGIF = new ImageView(new Image(helpPageGIFURL));
            helpPageGIF.setFitHeight(120);
            helpPageGIF.setPreserveRatio(true);

            Label l = new Label("JUDGE REVEALS...");
            Label c = new Label(problem.getCategory().toString());
            c.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + POP_CYAN + ";");

            Button close = new Button("GOT IT");
            close.setStyle("-fx-background-color: black; -fx-text-fill: white;");
            close.setOnAction(ev -> hintStage.close());

            hRoot.getChildren().addAll(helpPageGIF, l, c, close);
            hintStage.setScene(new Scene(hRoot, 350, 320));
            hintStage.show();
        });
        return b;
    }

    private Button createSubmitButton(Problem problem, Language[] selectedLang) {
        Button submitBtn = new Button("SUBMIT!");
        submitBtn.setStyle("-fx-background-color: " + POP_CYAN + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: black;");
        submitBtn.setOnAction(e -> {
            setUIBusy(true);
            statusLabel.setText("JUDGE: ANALYZING...");
            String code = (String) codeEditorView.getEngine().executeScript("editor.getValue();");

            JudgeFactory.getJudge(selectedLang[0]).submit(problem, new Submission(problem.getID(), selectedLang[0], code),
                    status -> Platform.runLater(() -> statusLabel.setText("JUDGE: " + status.toString().toUpperCase() + "...")),
                    verdict -> Platform.runLater(() -> {
                        setUIBusy(false);
                        if (verdict == SubmissionVerdict.Accepted) {
                            winSound.play();
                            score += 100;
                            sessionSolvedCount++;
                            showCodingPage();
                        } else {
                            renderGameOverUI(verdict.toString());
                        }

                        statusLabel.setText("JUDGE: " + verdict.toString().toUpperCase());
                    })
            );
        });
        return submitBtn;
    }

    private void startTimer() {
        if (gameTimer != null) gameTimer.cancel();
        gameTimer = new Timer(true);
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (!isJudging && secondsRemaining > 0) {
                        secondsRemaining--;
                        timerLabel.setText(String.format("%02d:%02d", secondsRemaining / 60, secondsRemaining % 60));
                        if (secondsRemaining <= 0) renderGameOverUI("OUT OF TIME!");
                    }
                });
            }
        }, 1000, 1000);
    }

    private void setUIBusy(boolean busy) {
        this.isJudging = busy;
        footer.setDisable(busy);
        codeEditorView.setDisable(busy);
        codeEditorView.setOpacity(busy ? 0.7 : 1.0);
    }

    private void addStatRow(GridPane grid, String label, String value, int row) {
        grid.add(new Label(label), 0, row);
        Label v = new Label(value);
        v.setStyle("-fx-text-fill: " + POP_MAGENTA + "; -fx-font-weight: bold;");
        grid.add(v, 1, row);
    }

    private Button createLangButton(Language[] lang) {
        Button b = new Button("C++");
        b.setMinWidth(110);
        b.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-border-color: white;");
        b.setOnAction(e -> {
            lang[0] = (lang[0] == Language.Cpp) ? Language.Python : Language.Cpp;
            b.setText(lang[0] == Language.Cpp ? "C++" : "PYTHON");
            updateEditorMode(lang[0] == Language.Cpp ? "c_cpp" : "python");
        });
        return b;
    }

    private void updateEditorMode(String m) {
        codeEditorView.getEngine().executeScript("editor.session.setMode('ace/mode/" + m + "');");
    }

    private String renderAceEditor() {
        return "<html><head><script src='https://cdnjs.cloudflare.com/ajax/libs/ace/1.23.4/ace.js'></script>" +
                "<style>#editor { position: absolute; top: 0; right: 0; bottom: 0; left: 0; font-size: 16px; }</style>" +
                "</head><body><div id='editor'></div><script>var editor = ace.edit('editor');" +
                "editor.setTheme('ace/theme/chrome'); editor.session.setMode('ace/mode/c_cpp');</script></body></html>";
    }

    private String renderPopArtHtml(Problem p) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head>");
        // Standard MathJax 3 configuration
        sb.append("<script>window.MathJax = { tex: { inlineMath: [['$', '$'], ['\\\\(', '\\\\)']], processEscapes: true } };</script>");
        sb.append("<script type='text/javascript' id='MathJax-script' async src='https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-mml-chtml.js'></script>");
        sb.append("<style>body { background:white; color:black; font-family:Verdana; padding:20px; line-height:1.6; }");
        sb.append("h1 { color:").append(POP_CYAN).append("; border-bottom: 5px solid ").append(POP_MAGENTA).append("; }");
        sb.append(".problem-content { font-size:14px; white-space: pre-wrap; } </style></head><body>");

        sb.append("<h1>").append(p.getTitle()).append("</h1>");

        String content = p.getContent()
                .replace("Input ", "<b>Input</b> ")
                .replace("Output ", "<b>Output</b> ")
                .replace("Constraints ", "<b>Constraints</b> ");

        content = content.replaceAll("(\\\\[a-z]+|[a-zA-Z0-9]+[_^][a-zA-Z0-9{}]+)", "\\\\($1\\\\)");

        sb.append("<div class='problem-content'>").append(content).append("</div>");
        sb.append("</body></html>");
        return sb.toString();
    }
}