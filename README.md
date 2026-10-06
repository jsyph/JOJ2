<div align="center">

# 💥 JOJ2 — Competitive Programming Survival Judge

[![Java 21](https://img.shields.io/badge/Java-21-118AB2?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-EF476F?style=for-the-badge&logo=java&logoColor=white)](https://openjfx.io/)
[![SQLite](https://img.shields.io/badge/SQLite-3-F9E076?style=for-the-badge&logo=sqlite&logoColor=000000)](https://sqlite.org/)
[![Judges](https://img.shields.io/badge/Judges-C%2B%2B%20%7C%20Python-118AB2?style=for-the-badge&logo=c%2B%2B&logoColor=white)](https://isocpp.org/)
[![Theme](https://img.shields.io/badge/Aesthetic-Pop%20Art%20Retro-EF476F?style=for-the-badge)](#-pop-art-color-palette)

<p align="center">
  <b>A high-octane, Pop-Art-styled desktop Online Judge and 20-minute survival challenge game.</b><br>
  Test your algorithmic problem-solving speed under pressure with instant verdicts, live audio-visual feedback, and embedded code editing.
</p>

[Key Features](#-key-features) • [Screenshots](#-screenshots--gameplay-flow) • [Color Palette](#-pop-art-color-palette) • [Architecture](#-architecture--design-patterns) • [Getting Started](#-getting-started)

---

</div>

## 🎨 Pop-Art Color Palette

The interface is inspired by classic Pop Art & comic book aesthetics, featuring high-contrast borders, punchy saturated primaries, and retro visual accents:

| Swatch | Name | Hex Code | Role in UI |
| :---: | :--- | :--- | :--- |
| <img src=".github/images/colors/yellow.png" width="22" height="22" alt="Pop Yellow" /> | **Pop Yellow** | `#F9E076` | Stats display tables, hint modals, bottom control bar, score banners |
| <img src=".github/images/colors/magenta.png" width="22" height="22" alt="Pop Magenta" /> | **Pop Magenta** | `#EF476F` | Game-over titles, hint action buttons, problem statement title underline |
| <img src=".github/images/colors/cyan.png" width="22" height="22" alt="Pop Cyan" /> | **Pop Cyan** | `#118AB2` | Top arena status bar, countdown window border, submit button, problem headers |
| <img src=".github/images/colors/white.png" width="22" height="22" alt="Pop White" /> | **Pop White** | `#FDFFFC` | Canvas background, workspace padding, and high-readability text |
| <img src=".github/images/colors/black.png" width="22" height="22" alt="Comic Black" /> | **Comic Black** | `#000000` | Heavy 3px–5px comic borders, secondary buttons, and countdown arena |

---

## ✨ Key Features

- ⏱️ **20-Minute Survival Challenge**: Race against a ticking clock to solve algorithmic problems consecutively. Each accepted solution rewards `+100` score points.
- ⚡ **Multi-Language Judging Engine**:
  - **C++ (GCC/g++)**: Automatic compilation and execution with strict time and memory limits.
  - **Python 3**: Native execution with input/output pipe streaming.
  - **Verdict Lifecycle**: Real-time status updates (`ENQUEUED` ➔ `COMPILING` ➔ `TESTING` ➔ `ACCEPTED` / `WRONG ANSWER` / `TIME LIMIT EXCEEDED` / `RUNTIME ERROR` / `COMPILE ERROR`).
- 📝 **Embedded Ace Code Editor**: Integrated Monaco/Ace Editor via JavaFX WebView with full syntax highlighting, line numbers, and instant C++ / Python mode switching.
- 📐 **MathJax 3 Formula Rendering**: Problem statements render beautiful LaTeX formulas and mathematical constraints ($2 \le n \le 2 \cdot 10^5$) natively in HTML.
- 💡 **Judge Hints System**: Need a clue? Request a category hint revealed through comic pop-ups.
- 🔊 **Meme GIFs & Sound Effects**: Ticking clocks, victory bells (`you-win.mp3`), failure buzzers (`game-over.mp3`), and classic meme animations keep the adrenaline high.
- 🗄️ **Zero-Config Pre-Seeded Database**: Comes with a pre-seeded SQLite database (`joj.db.gz`, 25 MB) featuring curated CSES problems and test datasets, which **auto-decompresses** on first launch.

---

## 📸 Screenshots & Gameplay Flow

### 1. The Welcome Lobby & Challenge Start
> Review your career stats (problems solved historically persisted in SQLite), get ready, and click **START CHALLENGE**.

<p align="center">
  <img src=".github/images/1.png" alt="JOJ2 Welcome Screen" width="85%" />
</p>

---

### 2. The 3-Second Dramatic Countdown
> When the countdown strikes zero, the 20-minute survival timer begins and problems are randomly shuffled.

<p align="center">
  <img src=".github/images/2.png" alt="Countdown Screen" width="85%" />
</p>

---

### 3. Problem Solving Arena
> Left pane displays the problem statement with MathJax LaTeX formatting. Right pane features the embedded Ace Editor with active line numbering and syntax styling.

<p align="center">
  <img src=".github/images/3.png" alt="Coding Arena" width="85%" />
</p>

---

### 4. Interactive Hint Modal
> Stuck on an algorithm? The Judge reveals the problem category (e.g. *Introductory Problems*, *Sorting and Searching*, *Dynamic Programming*).

<p align="center">
  <img src=".github/images/4.png" alt="Judge Hint Modal" width="85%" />
</p>

---

### 5. Solving in C++ with Real-Time Highlighting
> Write and inspect high-performance C++ code directly inside the integrated editor.

<p align="center">
  <img src=".github/images/5.png" alt="C++ Code Editor" width="85%" />
</p>

---

### 6. Multi-Language Switch & Accepted Verdict
> Switch seamlessly to Python, submit your code, and receive instant feedback: `JUDGE: ACCEPTED` with `+100` score increment and victory audio fanfare!

<p align="center">
  <img src=".github/images/6.png" alt="Accepted Verdict & Python Mode" width="85%" />
</p>

---

### 7. Game Over Screen
> Run out of time or submit a Wrong Answer? Face the Judge with a breakdown of your final score and reason for termination.

<p align="center">
  <img src=".github/images/7.png" alt="Game Over Screen" width="85%" />
</p>

---

## 🏛️ Architecture & Design Patterns

The project follows clean object-oriented principles:

```
src/main/java/com/example/joj2/
├── Launcher.java            # App bootstrapper (DB init & problem dealer shuffle)
├── JOJ.java                 # JavaFX GUI, event loops, WebViews, timer, and styling
├── Dealer.java              # Singleton managing problem rotation and shuffling
├── Database.java            # SQLite connection, schema migration & auto-unzipping
├── RunTracker.java          # Persistent run statistics & distinct problem solver metrics
├── Problem.java             # Problem domain model (statement, limits, testcases)
├── TestCase.java            # Input / Expected Output pairs
├── Solution.java            # Stored reference solutions
├── Submission.java          # User submission entity
├── SubmissionStatus.java    # Enqueued, Compiling, Testing, Finished
├── SubmissionVerdict.java   # Accepted, WrongAnswer, TimeLimitExceeded, etc.
├── Language.java            # Enum for Cpp and Python
├── Judge.java               # Core judging interface
├── CppJudge.java            # g++ compilation and execution runner
├── PythonJudge.java         # Python3 process execution runner
└── JudgeFactory.java        # Factory pattern: instantiates language-specific judges
```

### Key Design Patterns
- **Factory Pattern (`JudgeFactory`)**: Decouples language execution logic from the UI. Returns the appropriate `Judge` implementation (`CppJudge` or `PythonJudge`).
- **Singleton Pattern (`Database`, `Dealer`, `RunTracker`)**: Ensures unified access to the SQLite connection, problem pool, and historical stats throughout the session.
- **Asynchronous Execution & Platform Synchronization**: Code judging and process monitoring run on background daemon threads, reporting verdicts safely back to the JavaFX Application Thread via `Platform.runLater()`.

---

## 🚀 Getting Started

### Prerequisites

Ensure you have the following installed on your machine:

1. **Java 21 or higher** (with JavaFX support, such as [BellSoft Liberica Full JDK 21](https://bell-sw.com/pages/downloads/) or OpenJDK 21 + JavaFX SDK).
2. **C++ Compiler (`g++`)**: Required for judging C++ submissions.
   ```bash
   # Debian / Ubuntu
   sudo apt install build-essential

   # Arch Linux
   sudo pacman -S gcc

   # macOS (Homebrew)
   xcode-select --install
   ```
3. **Python 3 (`python3`)**: Required for judging Python submissions.
   ```bash
   # Debian / Ubuntu
   sudo apt install python3

   # Arch Linux
   sudo pacman -S python
   ```

---

### Installation & Running

1. **Clone the repository**:
   ```bash
   git clone git@github.com:jsyph/JOJ2.git
   cd JOJ2
   ```

2. **Run the Application**:
   Use the included Maven wrapper (`./mvnw` on Linux/macOS, `mvnw.cmd` on Windows):
   ```bash
   ./mvnw clean javafx:run
   ```

> [!NOTE]
> On the very first run, `Database.java` will automatically detect `joj.db.gz` and decompress it to `joj.db` within seconds. No manual database setup or SQL imports are required!

---

## 🗄️ Database Management

The application uses an SQLite database (`joj.db`) containing problem descriptions, testcases, and historical submission runs:

- **`joj.db.gz` (25 MB)**: Committed to Git as a compressed seed archive.
- **`joj.db`**: Automatically extracted on startup. Ignored by Git so your local test submissions and runs never pollute your repository history.

To manually re-compress the database at any time:
```bash
gzip -k -9 joj.db
```

---

## 📄 License

Developed for academic and competitive programming practice. Problem sets and reference solutions belong to their respective authors (CSES Problem Set).
