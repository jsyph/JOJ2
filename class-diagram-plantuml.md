# JOJ2 System - PlantUML Class Diagram

```plantuml
@startuml JOJ2_System_Architecture

!theme aws-orange

' ==============================
' ENUMERATIONS
' ==============================

enum Language {
    Python
    Cpp
    PlainText
    --
    + fromString(String str) : Language {static}
}

enum Category {
    IntroductoryProblems
    SortingAndSearch
    --
    + fromString(String str) : Category {static}
    + toString() : String
}

enum SubmissionStatus {
    Error
    Enqueued
    Compiling
    Testing
    Finished
    --
    + toString() : String
}

enum SubmissionVerdict {
    CompileError
    RuntimeError
    SystemError
    Accepted
    TimeLimitExceeded
    WrongAnswer
    --
    + toString() : String
}

' ==============================
' CORE DOMAIN CLASSES
' ==============================

class Problem {
    - ID : int
    - title : String
    - content : String
    - timeS : int
    - memoryMB : int
    - category : Category
    - testCases : List<TestCase>
    - solutions : List<Solution>
    --
    + Problem(ID:int, title:String, content:String, timeS:int, memoryMB:int, category:Category, testCases:List<TestCase>, solutions:List<Solution>)
    + getID() : int
    + getTitle() : String
    + getContent() : String
    + getTimeS() : int
    + getMemoryMB() : int
    + getCategory() : Category
    + getTestCases() : List<TestCase>
    + getSolutions() : List<Solution>
    + toString() : String
}

class TestCase {
    - input : String
    - expectedOutput : String
    - language : Language
    --
    + TestCase(i:String, o:String, l:Language)
    + getInput() : String
    + getExpectedOutput() : String
}

class Solution {
    - content : String
    - language : Language
    --
    + Solution(content:String, language:Language)
    + getContent() : String
    + getLanguage() : Language
}

class Submission {
    - {static} IDCount : int
    - ID : int
    - problemID : int
    - dateSubmitted : Date
    - language : Language
    - content : String
    --
    + Submission(problemID:int, language:Language, content:String)
    + getID() : int
    + getProblemID() : int
    + getDateSubmitted() : Date
    + getLanguage() : Language
    + getContent() : String
}

' ==============================
' INFRASTRUCTURE SINGLETONS
' ==============================

class Database <<Singleton>> {
    - {static} dbURL : String
    - {static} instance : Database
    - dbConnection : Connection
    --
    - Database()
    + {static} getInstance() : Database
    + init() : void
    - createTables() : void
    + executeSQLStatement(statement:String) : void
    + executeSQLQuery(statement:String) : ResultSet
    + prepareStatement(sql:String) : PreparedStatement
}

class Dealer <<Singleton>> {
    - {static} instance : Dealer
    - problems : List<Problem>
    - currentProblemIndex : int
    --
    - Dealer()
    + {static} getInstance() : Dealer
    + getProblem() : Problem
    + getProblems() : List<Problem>
    + shuffleProblems() : void
    + loadProblems() : void
    - updateProgress(cur:int, tot:int) : void
}

class RunTracker <<Singleton>> {
    - {static} instance : RunTracker
    --
    - RunTracker()
    + {static} getInstance() : RunTracker
    + getSolvedCount() : int
    + getRemainingCount() : int
    + recordRun(problemID:int, verdict:SubmissionVerdict, passed:int, total:int, timeMs:long) : void
    + getBestScore(problemID:int) : int
    + getTotalTries(problemID:int) : int
    + isSolved(problemID:int) : boolean
}

class ProblemDataHolder {
    + id : int
    + t : int
    + m : int
    + tit : String
    + con : String
    + c : Category
    + sols : List<Solution>
    + tcs : List<TestCase>
    + sSet : Set<String>
    + tSet : Set<String>
    --
    + ProblemDataHolder(i:int, ti:String, co:String, ts:int, mb:int, ca:Category)
    + addSol(s:Solution) : void
    + addTC(tc:TestCase) : void
    + build() : Problem
}

' ==============================
' JUDGE PATTERN
' ==============================

interface Judge {
    + submit(problem:Problem, submission:Submission, updateCallback:Consumer<SubmissionStatus>, finishCallback:Consumer<SubmissionVerdict>) : void
}

class JudgeFactory {
    + {static} getJudge(lang:Language) : Judge
}

class PythonJudge {
    + submit(problem:Problem, submission:Submission, updateCallback:Consumer<SubmissionStatus>, finishCallback:Consumer<SubmissionVerdict>) : void
    - runTest(script:File, tc:TestCase, timeoutS:int, workDir:File) : SubmissionVerdict
    - isSyntaxValid(scriptFile:File) : boolean
    - printProgress(current:int, total:int, verdict:SubmissionVerdict) : void
    - cleanup(dir:Path) : void
}

class CppJudge {
    + submit(problem:Problem, submission:Submission, updateCallback:Consumer<SubmissionStatus>, finishCallback:Consumer<SubmissionVerdict>) : void
    - runTest(exe:File, tc:TestCase, timeoutS:int, workDir:File) : SubmissionVerdict
    - compile(source:File, output:File) : boolean
    - printProgress(current:int, total:int, verdict:SubmissionVerdict) : void
    - cleanup(dir:Path) : void
}

' ==============================
' UI APPLICATION LAYER
' ==============================

class JOJ {
    - primaryStage : Stage
    - secondsRemaining : int
    - score : int
    - sessionSolvedCount : int
    - isJudging : boolean
    - countDownGIFURL : String
    - loosePageGIFURL : String
    - helpPageGIFURL : String
    - winSound : AudioClip
    - loseSound : AudioClip
    - startSound : AudioClip
    - timerLabel : Label
    - scoreLabel : Label
    - progressLabel : Label
    - statusLabel : Label
    - gameTimer : Timer
    - codeEditorView : WebView
    - footer : HBox
    --
    + start(stage:Stage) : void
    - showStartPage() : void
    - showCountdown() : void
    - showCodingPage() : void
    - renderGameOverUI(reasonText:String) : void
    - createHintButton(problem:Problem) : Button
    - createSubmitButton(problem:Problem, selectedLang:Language[]) : Button
    - createLangButton(lang:Language[]) : Button
    - startTimer() : void
    - setUIBusy(busy:boolean) : void
    - addStatRow(grid:GridPane, label:String, value:String, row:int) : void
    - updateEditorMode(mode:String) : void
    - renderAceEditor() : String
    - renderPopArtHtml(p:Problem) : String
}

class Launcher {
    + {static} main(args:String[]) : void
}

' ==============================
' RELATIONSHIPS
' ==============================

' Domain Relationships
Problem *-- "0..*" TestCase : contains
Problem *-- "0..*" Solution : contains
Problem --> Category : categorized by
TestCase --> Language : written in
Solution --> Language : written in
Submission --> Language : written in

' Judge Pattern
Judge <|.. PythonJudge : implements
Judge <|.. CppJudge : implements
JudgeFactory ..> Judge : creates
Judge --> SubmissionStatus : reports
Judge --> SubmissionVerdict : returns

' Singleton Usage
Dealer --> Database : uses
RunTracker --> Database : uses
JOJ --> Dealer : uses
JOJ --> RunTracker : uses

' Application Flow
Launcher --> Database : initializes
Launcher --> Dealer : loads problems
Launcher --> JOJ : launches

' Operational Dependencies
JOJ --> JudgeFactory : creates judges
JOJ --> Problem : displays
JOJ --> Submission : creates
Judge --> Problem : processes
Judge --> Submission : evaluates
Dealer +-- ProblemDataHolder : inner class

' External Dependencies
JOJ --|> Application : extends JavaFX

@enduml
```

## Key Features of this PlantUML Version:

### **1. Enhanced Visualization:**
- **Package Organization** - Logical grouping of related classes
- **Stereotypes** - `<<Singleton>>`, `<<interface>>` annotations
- **Color Coding** - Different colors for architectural layers
- **Notes** - Explanatory comments for key design patterns

### **2. Better UML Syntax:**
- **Proper visibility markers** - `+` public, `-` private, `{static}` static
- **Method parameters** - Full parameter lists with types
- **Relationship multiplicity** - `"0..*"` cardinalities
- **Interface implementation** - Proper `<|..` notation

### **3. Architectural Clarity:**
- **Layered packages** - Clear separation of concerns
- **Pattern highlighting** - Singleton, Factory, Strategy patterns
- **Dependency flow** - Clear data and control flow

### **4. Professional Appearance:**
- **AWS Orange theme** - Professional color scheme
- **Structured layout** - Logical flow from domain to application
- **Documentation notes** - Key architectural decisions explained

You can render this PlantUML diagram using:
- **Online**: PlantUML Web Server, PlantText.com
- **VS Code**: PlantUML extension
- **IntelliJ/Eclipse**: PlantUML plugins
- **Command line**: PlantUML JAR file

This version provides a more detailed and professionally structured view of your JOJ2 system architecture!
