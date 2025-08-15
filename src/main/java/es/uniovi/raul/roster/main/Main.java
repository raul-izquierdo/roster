package es.uniovi.raul.roster.main;

import java.io.*;
import java.util.*;

import es.uniovi.raul.roster.cli.*;
import es.uniovi.raul.roster.cli.Console;
import es.uniovi.raul.roster.groups.Groups;
import es.uniovi.raul.roster.groups.Groups.InvalidGroupFormatException;
import es.uniovi.raul.roster.loader.*;
import es.uniovi.raul.roster.model.Student;
import es.uniovi.raul.roster.naming.*;
import es.uniovi.raul.roster.roster.Roster;
import es.uniovi.raul.roster.roster.Roster.InvalidRosterFormatException;

public class Main {
    public static void main(String[] args) {

        Optional<Arguments> argumentsOpt = ArgumentsParser.parse(args);

        if (argumentsOpt.isEmpty())
            System.exit(1);

        Arguments arguments = argumentsOpt.get();

        PrintStream printer = System.out;
        // NamingStrategy namingStrategy = new HyphenSeparator();
        RosterNamingStrategy namingStrategy = new ParenthesisStrategy();

        try {
            switch (arguments.command) {
                case CREATE -> createRoster(arguments, namingStrategy, printer);
                case UPDATE -> updateRoster(arguments, namingStrategy, printer);
                default -> throw new AssertionError("picocli failed");
            }
        } catch (Exception e) {
            Console.printError(e.getMessage());
            System.exit(1);
        }

        System.exit(0);

    }

    public static void createRoster(Arguments arguments, RosterNamingStrategy namingStrategy, PrintStream printer)
            throws IOException, InvalidStudentFormatException, InvalidGroupFormatException {

        printNewStudents(getTeacherStudents(arguments, namingStrategy), printer);
    }

    public static void updateRoster(Arguments arguments, RosterNamingStrategy namingStrategy, PrintStream printer)
            throws IOException, InvalidStudentFormatException, InvalidGroupFormatException,
            InvalidRosterFormatException {

        List<Student> students = getTeacherStudents(arguments, namingStrategy);
        List<Student> roster = Roster.load(arguments.rosterFile, namingStrategy);

        printNewStudents(students, roster, printer);
        printStudentsToRemove(students, roster, printer);
        printStudentsToUpdate(students, roster, printer);

    }

    private static void printNewStudents(List<Student> students, PrintStream printer) {

        if (students.isEmpty())
            return;

        printer.println(
                """

                        ## Students to add to the roster

                        Instructions:
                        - Go to the Classroom page.
                        - Click the 'Students' tab.
                        - Click the 'Update Students' button.
                        - Select and copy all the lines below at once, then paste them into the 'Create your roster manually' text area.
                        """);

        students.stream()
                .map(Student::rosterId)
                .forEach(printer::println);
    }

    /**
     *  Print rosterId of students that are in students lists but not in the roster.
     */
    public static void printNewStudents(List<Student> students, List<Student> roster, PrintStream printer) {

        var newStudents = students.stream()
                .filter(student -> roster.stream()
                        .noneMatch(rosterStudent -> rosterStudent.name().equals(student.name())))
                .toList();

        printNewStudents(newStudents, printer);
    }

    /**
    * Prints the students that are in the roster but not in the students list.
    */
    public static void printStudentsToRemove(List<Student> students, List<Student> roster, PrintStream printer) {

        var studentsToRemove = roster.stream()
                .filter(rosterStudent -> students.stream()
                        .noneMatch(student -> student.name().equals(rosterStudent.name())))
                .toList();

        if (studentsToRemove.isEmpty())
            return;

        printer.println(
                """

                        ## Students to remove from the roster

                        Instructions:
                        - Go to the Classroom page.
                        - Click the 'Students' tab.
                        - For each of the following lines:
                            - Find the student with that roster ID and click the "trash" icon.
                        """);

        studentsToRemove.stream()
                .map(Student::rosterId)
                .forEach(printer::println);
    }

    /**
     * Print the students which their name are both in the students list and in the roster but have changed of group.
     * For example:
     * students = [ {name = Antonio, group = 01, rosterId = "Antonio (01)"} ]
     * roster = [ {name = Antonio, group = 02, rosterId = "Antonio (02)"} ]
     * Prints:
     * Antonio (01) -> Antonio (02)
     */
    public static void printStudentsToUpdate(List<Student> students, List<Student> roster, PrintStream printer) {
        // Collect students who have changed groups
        List<GroupChange> changedStudents = students.stream()
                .flatMap(student -> roster.stream()
                        .filter(rosterStudent -> rosterStudent.name().equals(student.name())
                                && !rosterStudent.rosterId().equals(student.rosterId()))
                        .map(rosterStudent -> new GroupChange(rosterStudent, student)))
                .toList();

        if (changedStudents.isEmpty())
            return;

        printer.println(
                """

                        ## Students who have changed groups

                        Instructions:
                        - Go to the Classroom page.
                        - Click the 'Students' tab.
                        - For each of the following lines:
                            - Find the student using the old roster ID (shown on the left side of the arrow) and click the "pen" icon.
                            - Replace the old roster ID with the new one (shown on the right side of the arrow).
                        """);

        changedStudents.stream()
                .map(change -> change.old().rosterId() + " ---> " + change.updated().rosterId())
                .forEach(printer::println);
    }

    private static record GroupChange(Student old, Student updated) {
    }

    public static List<Student> getTeacherStudents(Arguments arguments, RosterNamingStrategy namingStrategy)
            throws IOException, InvalidStudentFormatException, InvalidGroupFormatException {

        List<Student> students = Students.load(arguments.studentsFile, arguments.format, namingStrategy);

        if (arguments.groupsFile != null) {
            List<String> teacherGroups = Groups.load(arguments.groupsFile);
            students = students.stream()
                    .filter(student -> teacherGroups.contains(student.group()))
                    .toList();
        }

        return students;

    }
}
