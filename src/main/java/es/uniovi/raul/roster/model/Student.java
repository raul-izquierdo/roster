package es.uniovi.raul.roster.model;

/**
 * Represents a student with a name, group, and roster ID.
 * Example:
 * <pre>
 *     Student student = new Student("Izquierdo Castanedo, Raúl", "i02", "Izquierdo Castanedo, Raúl-i02");
 * </pre>
 * The roster ID is generated using the NamingStrategy class.
 *
 * @param name The student's name. Cannot be null or empty.
 * @param group The student's group or laboratory. Cannot be null or empty.
 * @param rosterId The unique roster ID for the student. Cannot be null or empty.
 */

public record Student(String name, String group, String rosterId) {

    public Student {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name must not be null or empty");
        if (group == null || group.isBlank())
            throw new IllegalArgumentException("Group must not be null or empty");
        if (rosterId == null || rosterId.isBlank())
            throw new IllegalArgumentException("Roster ID must not be null or empty");
    }
}
