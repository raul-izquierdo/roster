package es.uniovi.raul.roster.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.uniovi.raul.roster.naming.NamingStrategy;

class RosterModelTest {

    private static Student makeStudent(String name, String group) {
        return new Student(name, group, NamingStrategy.generateRosterId(name, group));
    }

    // Constructors
    @Test
    @DisplayName("1-arg constructor: null students throws IllegalArgumentException")
    void ctor_oneArg_nullStudents_throws() {
        assertThrows(IllegalArgumentException.class, () -> new Roster(null));
    }

    @Test
    @DisplayName("2-arg constructor: null students throws IllegalArgumentException")
    void ctor_twoArg_nullStudents_throws() {
        assertThrows(IllegalArgumentException.class, () -> new Roster(null, null));
    }

    @Test
    @DisplayName("1-arg constructor: sets students, empty previous, defensive copy")
    void ctor_oneArg_setsStudents_andEmptyPrevious_andDefensiveCopy() {
        var original = new ArrayList<>(List.of(makeStudent("Amador", "01"), makeStudent("Berto", "02")));
        var roster = new Roster(original);

        // Mutate original after construction; Roster should not be affected
        original.add(makeStudent("Charlie", "03"));

        var names = roster.getStudents().map(Student::name).toList();
        assertEquals(List.of("Amador", "Berto"), names);
        assertTrue(roster.getPreviousRoster().isEmpty());
    }

    @Test
    @DisplayName("2-arg constructor: sets students and non-empty previous when provided")
    void ctor_twoArg_setsPrevious_present() {
        var previous = new Roster(List.of(makeStudent("Amador", "01")));
        var current = new Roster(List.of(makeStudent("Berto", "02")), previous);
        assertTrue(current.getPreviousRoster().isPresent());
        assertSame(previous, current.getPreviousRoster().get());
    }

    @Test
    @DisplayName("2-arg constructor: null previous yields Optional.empty()")
    void ctor_twoArg_nullPrevious_empty() {
        var current = new Roster(List.of(makeStudent("Berto", "02")), null);
        assertTrue(current.getPreviousRoster().isEmpty());
    }

    // Accessors and mutators
    @Test
    @DisplayName("setPreviousRoster updates Optional and accepts null to clear it")
    void setPreviousRoster_updatesAndClears() {
        var current = new Roster(List.of(makeStudent("Berto", "02")));
        assertTrue(current.getPreviousRoster().isEmpty());

        var previous = new Roster(List.of(makeStudent("Amador", "01")));
        current.setPreviousRoster(previous);
        assertTrue(current.getPreviousRoster().isPresent());
        assertSame(previous, current.getPreviousRoster().get());

        current.setPreviousRoster(null);
        assertTrue(current.getPreviousRoster().isEmpty());
    }

    @Test
    @DisplayName("getStudents: returns current students regardless of previous roster")
    void getStudents_ignoresPrevious() {
        var previous = new Roster(List.of(makeStudent("Carlos", "03")));
        var current = new Roster(List.of(makeStudent("Amador", "01"), makeStudent("Berto", "02")), previous);
        var names = current.getStudents().map(Student::name).sorted().toList();
        assertEquals(List.of("Amador", "Berto"), names);
    }

    // Diff operations: no previous roster
    @Test
    @DisplayName("findStudentsToEnroll: with no previous roster returns all current students")
    void findStudentsToEnroll_noPrevious_allCurrent() {
        var roster = new Roster(List.of(makeStudent("Amador", "01"), makeStudent("Berto", "02")));
        var names = roster.findStudentsToEnroll().map(Student::name).sorted().toList();
        assertEquals(List.of("Amador", "Berto"), names);
    }

    @Test
    @DisplayName("findStudentsForRemoval: with no previous roster returns empty")
    void findStudentsForRemoval_noPrevious_empty() {
        var roster = new Roster(List.of(makeStudent("Amador", "01")));
        assertTrue(roster.findStudentsForRemoval().toList().isEmpty());
    }

    @Test
    @DisplayName("findGroupChanges: with no previous roster returns empty")
    void findGroupChanges_noPrevious_empty() {
        var roster = new Roster(List.of(makeStudent("Amador", "01")));
        assertTrue(roster.findGroupChanges().toList().isEmpty());
    }

    // Diff operations: with previous roster
    @Test
    @DisplayName("findStudentsToEnroll: only students present in current and absent in previous (by name)")
    void findStudentsToEnroll_withPrevious_onlyNew() {
        var previous = new Roster(List.of(makeStudent("Amador", "01"), makeStudent("Carlos", "03")));
        var current = new Roster(List.of(makeStudent("Amador", "02"), makeStudent("Berto", "02")), previous); // Alice present (group changed), Bob is new

        var names = current.findStudentsToEnroll().map(Student::name).toList();
        assertEquals(List.of("Berto"), names);
    }

    @Test
    @DisplayName("findStudentsForRemoval: only students present in previous and absent in current (by name)")
    void findStudentsForRemoval_withPrevious_onlyRemoved() {
        var previous = new Roster(List.of(makeStudent("Amador", "01"), makeStudent("Carlos", "03")));
        var current = new Roster(List.of(makeStudent("Amador", "02"), makeStudent("Berto", "02")), previous); // Carol removed

        var names = current.findStudentsForRemoval().map(Student::name).toList();
        assertEquals(List.of("Carlos"), names);
    }

    @Test
    @DisplayName("findGroupChanges: only students whose group changed, paired as (old, updated)")
    void findGroupChanges_withPrevious_onlyChanged() {
        var aliceOld = makeStudent("Amador", "01");
        var aliceNew = makeStudent("Amador", "02");
        var bob = makeStudent("Berto", "02");

        var previous = new Roster(List.of(aliceOld, bob));
        var current = new Roster(List.of(aliceNew, bob), previous);

        var changes = current.findGroupChanges().toList();
        assertEquals(1, changes.size());
        var change = changes.get(0);
        assertEquals(aliceOld, change.old());
        assertEquals(aliceNew, change.updated());
    }
}
