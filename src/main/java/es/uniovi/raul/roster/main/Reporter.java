package es.uniovi.raul.roster.main;

import java.io.PrintStream;
import java.util.stream.Stream;

import es.uniovi.raul.roster.model.*;

public final class Reporter {

    public static boolean printRequiredChanges(Roster roster, PrintStream printer) {

        boolean studentsAdded = printStudentsToAdd(roster, printer);
        boolean studentsRemoved = printStudentsToRemove(roster, printer);
        boolean studentsChanged = printGroupChanges(roster, printer);

        return studentsAdded || studentsRemoved || studentsChanged;
    }

    private static boolean printStudentsToAdd(Roster roster, PrintStream printer) {

        return printSection(
                """

                        ## Students to add to the roster

                        Instructions:
                        - Go to the Classroom page.
                        - Click the 'Students' tab.
                        - Click the 'Update Students' button.
                        - Select and copy all the lines below at once, then paste them into the 'Create your roster manually' text area.
                        """,
                roster.findStudentsToEnroll().map(Student::rosterId), printer);
    }

    private static boolean printStudentsToRemove(Roster roster, PrintStream printer) {

        return printSection(
                """

                        ## Students to remove from the roster

                        Instructions:
                        - Go to the Classroom page.
                        - Click the 'Students' tab.
                        - For each of the following lines:
                            - Find the student with that roster ID and click the "trash" icon.
                        """,
                roster.findStudentsForRemoval().map(Student::rosterId), printer);
    }

    private static boolean printGroupChanges(Roster roster, PrintStream printer) {

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
                roster.findGroupChanges()
                        .map(change -> change.old().rosterId() + " ---> " + change.updated().rosterId()),
                printer);
    }

    // returns true if any lines were printed
    private static boolean printSection(String header, Stream<String> lines, PrintStream printer) {

        var linesList = lines.toList();

        if (linesList.isEmpty())
            return false;

        printer.println(header);
        linesList.forEach(printer::println);
        return true;

    }

}
