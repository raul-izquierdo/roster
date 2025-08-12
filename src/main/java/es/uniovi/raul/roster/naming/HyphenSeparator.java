package es.uniovi.raul.roster.naming;

/**
 * NamingStrategy provides methods to generate and parse roster IDs.
 */

// There is no need for a proper Strategy pattern here, as there won't be multiple strategies to choose from.

public final class HyphenSeparator implements NamingStrategy {

    private static final String SEPARATOR = " - ";

    /**
     * Generates a roster ID based on the student's name and group.
     * Examples:
     * "John Doe", "A" -> "John Doe_A"
     * "Izquierdo Castanedo, Raúl", "i02" -> "Izquierdo Castanedo, Raúl_i02"
     *
     * @param name  The student's name
     * @param group The student's group
     * @return A string representing the roster ID in the format "name-group"
     */
    @Override
    public String generateRosterId(String name, String group) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name must not be null or empty");
        if (group == null || group.isBlank())
            throw new IllegalArgumentException("Group must not be null or empty");

        return name + SEPARATOR + group;
    }

    @Override
    public String extractStudentName(String rosterId) {

        checkRosterIdStructure(rosterId);

        return rosterId.substring(0, rosterId.indexOf(SEPARATOR));
    }

    @Override
    public String extractGroup(String rosterId) {

        checkRosterIdStructure(rosterId);

        return rosterId.substring(rosterId.indexOf(SEPARATOR) + SEPARATOR.length());
    }

    private void checkRosterIdStructure(String rosterId) {
        int index = rosterId.indexOf(SEPARATOR);

        // Error si no hay separador
        if (index == -1)
            throw new IllegalArgumentException("Invalid roster ID format: " + rosterId);

        // Si no hay nada antes y después del separador, no es un formato válido
        if (index == 0 || index + SEPARATOR.length() == rosterId.length())
            throw new IllegalArgumentException("Invalid roster ID format: " + rosterId);
    }
}
