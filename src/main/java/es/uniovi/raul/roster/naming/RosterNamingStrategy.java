package es.uniovi.raul.roster.naming;

/**
 * Interface generating the id of a student in the roster based on their name and group.
 *
 * Strategy pattern
 */
public interface RosterNamingStrategy {

    /**
     * Generates a roster ID based on the student's name and group.
     *
     * @param name  The student's name
     * @param group The student's group
     * @return A string representing the roster ID generated for that student
     */
    String generateRosterId(String name, String group);

    String extractStudentName(String rosterId);

    String extractGroup(String rosterId);

}
