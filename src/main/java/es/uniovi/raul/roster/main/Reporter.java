package es.uniovi.raul.roster.main;

import static java.util.stream.Collectors.toMap;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import es.uniovi.raul.roster.model.Student;

/**
 * Generates report of required changes between two rosters.
 * Compares students lists and identifies additions, removals, and group changes.
 */
public final class Reporter {

    /**
     * Prints required changes comparing the existing roster with the latest students.
     *
     * @param existingRoster the list of students currently in GitHub Classroom
     * @param latestStudents the latest list of students from the university system
     * @param printer the PrintStream to write the output to
     * @return true if any changes are required, false otherwise
     */
    public static boolean printRequiredChanges(List<Student> existingRoster, List<Student> latestStudents,
            PrintStream printer) {

        boolean studentsAdded = printStudentsToAdd(existingRoster, latestStudents, printer);
        boolean studentsRemoved = printStudentsToRemove(existingRoster, latestStudents, printer);
        boolean studentsChanged = printGroupChanges(existingRoster, latestStudents, printer);

        return studentsAdded || studentsRemoved || studentsChanged;
    }

    private static boolean printStudentsToAdd(List<Student> existingRoster, List<Student> latestStudents,
            PrintStream printer) {

        var existingNames = existingRoster.stream()
                .map(Student::name)
                .toList();

        var studentsToAdd = latestStudents.stream()
                .filter(student -> !existingNames.contains(student.name()))
                .map(Student::rosterId)
                .toList();

        return printSection(
                """

                        ## Students to add to the roster

                        Instructions:
                        - Go to the Classroom page.
                        - Click the 'Students' tab.
                        - Click the 'Update Students' button.
                        - Select and copy all the lines below at once, then paste them into the 'Create your roster manually' text area.
                        """,
                studentsToAdd, printer);
    }

    private static boolean printStudentsToRemove(List<Student> existingRoster, List<Student> latestStudents,
            PrintStream printer) {

        var latestNames = latestStudents.stream()
                .map(Student::name)
                .toList();

        var studentsToRemove = existingRoster.stream()
                .filter(student -> !latestNames.contains(student.name()))
                .map(Student::rosterId)
                .toList();

        return printSection(
                """

                        ## Students to remove from the roster

                        Instructions:
                        - Go to the Classroom page.
                        - Click the 'Students' tab.
                        - For each of the following lines:
                            - Find the student with that roster ID and click the "trash" icon.
                        """,
                studentsToRemove, printer);
    }

    private static boolean printGroupChanges(List<Student> existingRoster, List<Student> latestStudents,
            PrintStream printer) {

        Map<String, Student> existingStudentMap = existingRoster.stream()
                .collect(toMap(Student::name, student -> student));

        var groupChanges = latestStudents.stream()
                .filter(latestStudent -> hasChangedGroup(existingStudentMap, latestStudent))
                .map(latestStudent -> String.format("%s ---> %s",
                        existingStudentMap.get(latestStudent.name()).rosterId(),
                        latestStudent.rosterId()))
                .toList();

        return printSection(
                """

                        ## Students who have changed groups

                        Instructions:
                        - Go to the Classroom page.
                        - Click the 'Students' tab.
                        - For each of the following lines:
                            - Find the student using the old roster ID (shown on the left side of the arrow) and click the "pen" icon.
                            - Replace the old roster ID with the new one (shown on the right side of the arrow).
                        """,
                groupChanges, printer);
    }

    // Returns true if the student exists in the existing roster and has a different group in the latest roster
    private static boolean hasChangedGroup(Map<String, Student> existingStudentMap, Student latestStudent) {
        var existing = existingStudentMap.get(latestStudent.name());
        return existing != null && !existing.group().equals(latestStudent.group());
    }

    // returns true if any lines were printed
    private static boolean printSection(String header, List<String> lines, PrintStream printer) {

        if (lines.isEmpty())
            return false;

        printer.println(header);
        lines.forEach(printer::println);
        return true;

    }

}
