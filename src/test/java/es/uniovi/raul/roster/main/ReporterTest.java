package es.uniovi.raul.roster.main;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.uniovi.raul.roster.model.Student;
import es.uniovi.raul.roster.naming.NamingStrategy;

class ReporterTest {

    private static Student makeStudent(String name, String group) {
        return new Student(name, group, NamingStrategy.generateRosterId(name, group));
    }

    private static PrintStream createCapturePrintStream() {
        return new PrintStream(new ByteArrayOutputStream());
    }

    // printRequiredChanges with no existing roster (empty existing)
    @Test
    @DisplayName("printRequiredChanges: with empty existing roster, returns all students as to add")
    void printRequiredChanges_emptyExisting_allToAdd() {
        var existing = List.<Student>of();
        var latest = List.of(makeStudent("Amador", "01"), makeStudent("Berto", "02"));

        boolean changesRequired = Reporter.printRequiredChanges(existing, latest, createCapturePrintStream());

        assertTrue(changesRequired);
    }

    @Test
    @DisplayName("printRequiredChanges: with empty existing and latest, returns false")
    void printRequiredChanges_bothEmpty_noChanges() {
        var existing = List.<Student>of();
        var latest = List.<Student>of();

        boolean changesRequired = Reporter.printRequiredChanges(existing, latest, createCapturePrintStream());

        assertFalse(changesRequired);
    }

    // Students to add
    @Test
    @DisplayName("printRequiredChanges: identifies new students to add")
    void printRequiredChanges_newStudents_identified() {
        var existing = List.of(makeStudent("Amador", "01"));
        var latest = List.of(
                makeStudent("Amador", "01"),
                makeStudent("Berto", "02"));

        boolean changesRequired = Reporter.printRequiredChanges(existing, latest, createCapturePrintStream());

        assertTrue(changesRequired);
    }

    // Students to remove
    @Test
    @DisplayName("printRequiredChanges: identifies students to remove")
    void printRequiredChanges_studentsToRemove_identified() {
        var existing = List.of(
                makeStudent("Amador", "01"),
                makeStudent("Carlos", "03"));
        var latest = List.of(makeStudent("Amador", "01"));

        boolean changesRequired = Reporter.printRequiredChanges(existing, latest, createCapturePrintStream());

        assertTrue(changesRequired);
    }

    // Group changes
    @Test
    @DisplayName("printRequiredChanges: identifies students with group changes")
    void printRequiredChanges_groupChanges_identified() {
        var existing = List.of(makeStudent("Amador", "01"));
        var latest = List.of(makeStudent("Amador", "02"));

        boolean changesRequired = Reporter.printRequiredChanges(existing, latest, createCapturePrintStream());

        assertTrue(changesRequired);
    }

    // Combined changes
    @Test
    @DisplayName("printRequiredChanges: identifies multiple types of changes")
    void printRequiredChanges_multipleChanges_allIdentified() {
        var existing = List.of(
                makeStudent("Amador", "01"),
                makeStudent("Carlos", "03"));
        var latest = List.of(
                makeStudent("Amador", "02"), // group change
                makeStudent("Berto", "02") // new student
        ); // Carlos removed

        boolean changesRequired = Reporter.printRequiredChanges(existing, latest, createCapturePrintStream());

        assertTrue(changesRequired);
    }

    // No changes
    @Test
    @DisplayName("printRequiredChanges: returns false when rosters are identical")
    void printRequiredChanges_identicalRosters_noChanges() {
        var existing = List.of(makeStudent("Amador", "01"), makeStudent("Berto", "02"));
        var latest = List.of(makeStudent("Amador", "01"), makeStudent("Berto", "02"));

        boolean changesRequired = Reporter.printRequiredChanges(existing, latest, createCapturePrintStream());

        assertFalse(changesRequired);
    }

    // Output verification
    @Test
    @DisplayName("printRequiredChanges: prints section headers when changes exist")
    void printRequiredChanges_printsOutput_whenChangesExist() {
        var existing = List.of(makeStudent("Amador", "01"));
        var latest = List.of(
                makeStudent("Amador", "02"), // group change
                makeStudent("Berto", "02") // new student
        );

        var output = new ByteArrayOutputStream();
        Reporter.printRequiredChanges(existing, latest, new PrintStream(output));
        String result = output.toString();

        assertTrue(result.contains("Students to add to the roster"));
        assertTrue(result.contains("Students who have changed groups"));
    }
}
