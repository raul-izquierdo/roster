package es.uniovi.raul.roster.model;

import java.util.*;
import java.util.stream.Stream;

/**
 * The Roster is the list of students in a GH Classroom.
 */
public final class Roster {

    private List<Student> students;
    private Optional<Roster> previousRoster;

    public Roster(List<Student> students) {
        if (students == null)
            throw new IllegalArgumentException("Students list cannot be null");

        this.students = new ArrayList<>(students);
        this.previousRoster = Optional.empty();
    }

    public Roster(List<Student> students, Roster previousRoster) {
        if (students == null)
            throw new IllegalArgumentException("Students list cannot be null");

        this.students = students;
        this.previousRoster = Optional.ofNullable(previousRoster);
    }

    public Stream<Student> getStudents() {
        return students.stream();
    }

    public Optional<Roster> getPreviousRoster() {
        return previousRoster;
    }

    public void setPreviousRoster(Roster previousRoster) {
        this.previousRoster = Optional.ofNullable(previousRoster);
    }

    public Stream<Student> findStudentsToEnroll() {
        return students.stream()
                .filter(student -> getPreviousStudents()
                        .noneMatch(previousStudent -> previousStudent.name().equals(student.name())));
    }

    public Stream<Student> findStudentsForRemoval() {
        return getPreviousStudents()
                .filter(previousStudent -> students.stream()
                        .noneMatch(student -> student.name().equals(previousStudent.name())));
    }

    public Stream<GroupChange> findGroupChanges() {
        return students.stream()
                .flatMap(student -> getPreviousStudents()
                        .filter(previousStudent -> previousStudent.name().equals(student.name())
                                && !previousStudent.group().equals(student.group()))
                        .map(previousStudent -> new GroupChange(previousStudent, student)));
    }

    private Stream<Student> getPreviousStudents() {
        return previousRoster.map(Roster::getStudents).orElseGet(Stream::empty);
    }

    public static record GroupChange(Student old, Student updated) {
    }
}
