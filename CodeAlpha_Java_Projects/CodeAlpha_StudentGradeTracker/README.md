# CodeAlpha_StudentGradeTracker

Java internship **Task 1** at CodeAlpha: a console-based Student Grade Tracker.

## Features
- Add students with any number of grades (0-100); add more grades later
- Per-student average, highest, lowest and letter grade
- Summary report of all students plus class average, highest/lowest score, top and lowest student
- Remove students; load sample data for a quick demo
- Input validation (bad numbers and out-of-range grades are rejected)
- Uses `ArrayList` for students and grades, with a `Student` class (OOP)

## Run
```bash
cd src
javac StudentGradeTracker.java
java StudentGradeTracker
```
Requires JDK 8 or newer. No external libraries.

## Sample report
```
Name              Count   Average   Highest    Lowest  Grade
Priya                 4     88.50     99.00     75.00      B
Riya                  3     91.67     96.00     88.00      A
Class average       : 88.40
```
