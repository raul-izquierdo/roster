package es.uniovi.raul.roster.loaders.students;

import java.util.Optional;

/**
 * Interface for building student objects from different file formats.
 * <p>
 * Implements the Builder design pattern.
 */
public interface StudentBuilder {
    void start();

    /**
     * Builds a student with the given name and group.
     *
     * @param name The name of the student. Must not be null or empty.
     * @param group The group or laboratory associated with the student. May be empty if no group is assigned.
     */
    void buildStudent(String name, Optional<String> group);
}
