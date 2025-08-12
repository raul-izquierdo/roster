package es.uniovi.raul.roster.main;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.uniovi.raul.roster.model.Student;

// CHECKSTYLE:OFF

class MainPrintingTest {

    private static class Fixture {
        public final Student onlyInStudents;
        public final Student onlyInRoster;
        public final Student sameInBoth;
        public final Student differentInStudents;
        public final Student differentInRoster;

        public final List<Student> students;
        public final List<Student> roster;

        Fixture() {
            // Create shared fixture data: one same in both, one only in each list, and one different between lists
            this.onlyInStudents = new Student("OnlyInStudents", "01", "OnlyInStudents (01)");
            this.sameInBoth = new Student("SameInBoth", "01", "SameInBoth (01)");
            this.differentInStudents = new Student("Different", "02", "Different (02)");
            this.students = List.of(onlyInStudents, sameInBoth, differentInStudents);

            this.onlyInRoster = new Student("OnlyInRoster", "03", "OnlyInRoster (03)");
            this.differentInRoster = new Student("Different", "01", "Different (01)");
            this.roster = List.of(onlyInRoster, sameInBoth, differentInRoster);
        }
    }

    @Test
    @DisplayName("printNewStudents prints header and only students exclusive to students list")
    void printNewStudents_onlyStudents() {
        var fixture = new Fixture();
        var baos = new ByteArrayOutputStream();
        var printer = new PrintStream(baos, true);

        Main.printNewStudents(fixture.students, fixture.roster, printer);

        String out = baos.toString();
        assertTrue(out.contains("## Students to add to the roster"));
        assertTrue(out.contains(fixture.onlyInStudents.rosterId()));
        assertFalse(out.contains(fixture.onlyInRoster.rosterId()));
        assertFalse(out.contains(fixture.sameInBoth.rosterId()));
        assertFalse(out.contains(fixture.differentInStudents.rosterId()));
        assertFalse(out.contains(fixture.differentInRoster.rosterId()));
    }

    @Test
    @DisplayName("printStudentsToRemove prints header and only students exclusive to roster")
    void printStudentsToRemove_onlyRoster() {
        var fixture = new Fixture();
        var baos = new ByteArrayOutputStream();
        var printer = new PrintStream(baos, true);

        Main.printStudentsToRemove(fixture.students, fixture.roster, printer);

        String out = baos.toString();
        assertTrue(out.contains("## Students to remove from the roster"));
        assertTrue(out.contains(fixture.onlyInRoster.rosterId()));
        assertFalse(out.contains(fixture.onlyInStudents.rosterId()));
        assertFalse(out.contains(fixture.sameInBoth.rosterId()));
        assertFalse(out.contains(fixture.differentInRoster.rosterId()));
        assertFalse(out.contains(fixture.differentInStudents.rosterId()));
    }

    @Test
    @DisplayName("printStudentsToUpdate prints header and only changed group mapping")
    void printStudentsToUpdate_changed() {
        var fixture = new Fixture();
        var baos = new ByteArrayOutputStream();
        var printer = new PrintStream(baos, true);

        Main.printStudentsToUpdate(fixture.students, fixture.roster, printer);

        String out = baos.toString();
        assertTrue(out.contains("## Students who have changed groups"));

        String oldId = fixture.differentInRoster.rosterId();
        String newId = fixture.differentInStudents.rosterId();
        assertTrue(out.contains(oldId));
        assertTrue(out.contains(newId));
        // Ensure the old rosterId appears before the new one
        assertTrue(out.indexOf(oldId) < out.indexOf(newId));

        assertFalse(out.contains(fixture.onlyInStudents.rosterId()));
        assertFalse(out.contains(fixture.onlyInRoster.rosterId()));
        assertFalse(out.contains(fixture.sameInBoth.rosterId() + "\n"));
    }

    @Test
    @DisplayName("printNewStudents prints nothing if none are new")
    void printNewStudents_none() {
        var same = new Student("SameInBoth", "01", "SameInBoth (01)");
        var students = List.of(same);
        var roster = List.of(same);
        var baos = new ByteArrayOutputStream();
        var printer = new PrintStream(baos, true);

        Main.printNewStudents(students, roster, printer);
        assertEquals("", baos.toString());
    }

    @Test
    @DisplayName("printStudentsToRemove prints nothing if none to remove")
    void printStudentsToRemove_none() {
        var same = new Student("SameInBoth", "01", "SameInBoth (01)");
        var students = List.of(same);
        var roster = List.of(same);
        var baos = new ByteArrayOutputStream();
        var printer = new PrintStream(baos, true);

        Main.printStudentsToRemove(students, roster, printer);
        assertEquals("", baos.toString());
    }

    @Test
    @DisplayName("printStudentsToUpdate prints nothing if groups unchanged")
    void printStudentsToUpdate_none() {
        var same = new Student("SameInBoth", "01", "SameInBoth (01)");
        var students = List.of(same);
        var roster = List.of(same);
        var baos = new ByteArrayOutputStream();
        var printer = new PrintStream(baos, true);

        Main.printStudentsToUpdate(students, roster, printer);
        assertEquals("", baos.toString());
    }
}
