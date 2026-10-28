import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * CodeAlpha Java Internship - Task 1: Student Grade Tracker
 * Console based program to input and manage student grades.
 */
public class StudentGradeTracker {

    /** Represents one student and all of his/her scores. */
    static class Student {
        private final String name;
        private final List<Double> grades = new ArrayList<>();

        Student(String name) {
            this.name = name;
        }

        String getName() { return name; }
        List<Double> getGrades() { return grades; }

        void addGrade(double g) { grades.add(g); }

        double average() {
            if (grades.isEmpty()) return 0;
            double sum = 0;
            for (double g : grades) sum += g;
            return sum / grades.size();
        }

        double highest() {
            double max = Double.NEGATIVE_INFINITY;
            for (double g : grades) max = Math.max(max, g);
            return grades.isEmpty() ? 0 : max;
        }

        double lowest() {
            double min = Double.POSITIVE_INFINITY;
            for (double g : grades) min = Math.min(min, g);
            return grades.isEmpty() ? 0 : min;
        }

        String letterGrade() {
            double a = average();
            if (a >= 90) return "A";
            if (a >= 80) return "B";
            if (a >= 70) return "C";
            if (a >= 60) return "D";
            return "F";
        }
    }

    private static final List<Student> students = new ArrayList<>();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Student Grade Tracker (CodeAlpha) ===");
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter choice: ", 1, 7);
            switch (choice) {
                case 1: addStudent(); break;
                case 2: addGradeToStudent(); break;
                case 3: viewStudent(); break;
                case 4: removeStudent(); break;
                case 5: summaryReport(); break;
                case 6: loadSampleData(); break;
                case 7: running = false; break;
                default: break;
            }
        }
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println("\n----------- MENU -----------");
        System.out.println("1. Add student (with grades)");
        System.out.println("2. Add grade to existing student");
        System.out.println("3. View one student");
        System.out.println("4. Remove student");
        System.out.println("5. Summary report (all students)");
        System.out.println("6. Load sample data");
        System.out.println("7. Exit");
    }

    private static void addStudent() {
        String name = readLine("Student name: ");
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }
        if (find(name) != null) {
            System.out.println("Student already exists. Use option 2 to add grades.");
            return;
        }
        Student s = new Student(name);
        System.out.println("Enter grades (0-100). Type -1 to finish.");
        while (true) {
            double g = readDouble("  Grade: ", -1, 100);
            if (g == -1) break;
            s.addGrade(g);
        }
        students.add(s);
        System.out.println("Student added.");
    }

    private static void addGradeToStudent() {
        Student s = find(readLine("Student name: "));
        if (s == null) { System.out.println("Student not found."); return; }
        s.addGrade(readDouble("Grade (0-100): ", 0, 100));
        System.out.println("Grade added.");
    }

    private static void viewStudent() {
        Student s = find(readLine("Student name: "));
        if (s == null) { System.out.println("Student not found."); return; }
        System.out.println("Name    : " + s.getName());
        System.out.println("Grades  : " + s.getGrades());
        System.out.printf("Average : %.2f (%s)%n", s.average(), s.letterGrade());
        System.out.printf("Highest : %.2f%n", s.highest());
        System.out.printf("Lowest  : %.2f%n", s.lowest());
    }

    private static void removeStudent() {
        Student s = find(readLine("Student name: "));
        if (s == null) { System.out.println("Student not found."); return; }
        students.remove(s);
        System.out.println("Student removed.");
    }

    private static void summaryReport() {
        if (students.isEmpty()) {
            System.out.println("No students recorded yet.");
            return;
        }
        System.out.println("\n================ SUMMARY REPORT ================");
        System.out.printf("%-15s %7s %9s %9s %9s %6s%n",
                "Name", "Count", "Average", "Highest", "Lowest", "Grade");
        System.out.println("------------------------------------------------------------");
        double classSum = 0;
        int classCount = 0;
        double classHigh = Double.NEGATIVE_INFINITY;
        double classLow = Double.POSITIVE_INFINITY;
        Student topper = null, bottom = null;

        for (Student s : students) {
            System.out.printf("%-15s %7d %9.2f %9.2f %9.2f %6s%n",
                    s.getName(), s.getGrades().size(), s.average(),
                    s.highest(), s.lowest(), s.letterGrade());
            for (double g : s.getGrades()) {
                classSum += g;
                classCount++;
                classHigh = Math.max(classHigh, g);
                classLow = Math.min(classLow, g);
            }
            if (!s.getGrades().isEmpty()) {
                if (topper == null || s.average() > topper.average()) topper = s;
                if (bottom == null || s.average() < bottom.average()) bottom = s;
            }
        }
        System.out.println("------------------------------------------------------------");
        if (classCount == 0) {
            System.out.println("No grades entered yet.");
            return;
        }
        System.out.printf("Students            : %d%n", students.size());
        System.out.printf("Class average       : %.2f%n", classSum / classCount);
        System.out.printf("Highest single score: %.2f%n", classHigh);
        System.out.printf("Lowest single score : %.2f%n", classLow);
        System.out.printf("Top student (avg)   : %s (%.2f)%n", topper.getName(), topper.average());
        System.out.printf("Lowest student (avg): %s (%.2f)%n", bottom.getName(), bottom.average());
    }

    private static void loadSampleData() {
        String[] names = {"Aman", "Riya", "Karan"};
        double[][] data = {{85, 92, 78}, {96, 88, 91}, {55, 67, 72}};
        for (int i = 0; i < names.length; i++) {
            if (find(names[i]) != null) continue;
            Student s = new Student(names[i]);
            for (double g : data[i]) s.addGrade(g);
            students.add(s);
        }
        System.out.println("Sample data loaded.");
    }

    // ---------- helpers ----------
    private static Student find(String name) {
        for (Student s : students) {
            if (s.getName().equalsIgnoreCase(name.trim())) return s;
        }
        return null;
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return sc.hasNextLine() ? sc.nextLine().trim() : "";
    }

    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            if (!sc.hasNextLine()) return max; // EOF -> exit
            try {
                int v = Integer.parseInt(sc.nextLine().trim());
                if (v >= min && v <= max) return v;
            } catch (NumberFormatException ignored) { }
            System.out.println("Please enter a number between " + min + " and " + max + ".");
        }
    }

    private static double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            if (!sc.hasNextLine()) return -1;
            try {
                double v = Double.parseDouble(sc.nextLine().trim());
                if (v >= min && v <= max) return v;
            } catch (NumberFormatException ignored) { }
            System.out.println("Please enter a valid number between " + min + " and " + max + ".");
        }
    }
}
