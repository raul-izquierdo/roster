package es.uniovi.raul.roster.main;

import java.io.PrintStream;

import es.uniovi.raul.roster.model.Roster;

public final class Reporter {

    public static void printRequiredChanges(Roster roster, PrintStream printer) {

        printStudentsToAdd(roster, printer);
        printStudentsToRemove(roster, printer);
        printGroupChanges(roster, printer);
    }

    private static void printStudentsToAdd(Roster roster, PrintStream printer) {

        printer.println(
                """

                        ## Students to add to the roster

                        Instructions:
                        - Go to the Classroom page.
                        - Click the 'Students' tab.
                        - Click the 'Update Students' button.
                        - Select and copy all the lines below at once, then paste them into the 'Create your roster manually' text area.
                        """);

        roster.findStudentsToEnroll().forEach(student -> printer.println(student.name()));
    }

    private static void printStudentsToRemove(Roster roster, PrintStream printer) {

        printer.println(
                """

                        ## Students to remove from the roster

                        Instructions:
                        - Go to the Classroom page.
                        - Click the 'Students' tab.
                        - For each of the following lines:
                            - Find the student with that roster ID and click the "trash" icon.
                                """);

        roster.findStudentsForRemoval().forEach(student -> printer.println(student.name()));
    }

    private static void printGroupChanges(Roster roster, PrintStream printer) {

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

        roster.findGroupChanges()
                .map(change -> change.old().rosterId() + " ---> " + change.updated().rosterId())
                .forEach(printer::println);
    }
}
