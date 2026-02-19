package es.uniovi.raul.roster.loaders.roster;

import static java.lang.String.*;

import java.io.*;
import java.util.*;

import org.apache.commons.csv.*;

import es.uniovi.raul.roster.model.*;
import es.uniovi.raul.roster.naming.NamingStrategy;

/**
 * The Roster is the list of students in a GH Classroom.
 * It can be downloaded in CSV format from the classroom.
 * This class loads that CSV file.
 */
public class RosterLoader {

    public static List<Student> loadRoster(String rosterFile)
            throws IOException, InvalidRosterFormatException {

        try (var reader = new java.io.FileReader(rosterFile)) {

            return loadRoster(reader);

        } catch (InvalidRosterFormatException e) {
            throw new InvalidRosterFormatException(
                    format("'%s' is not a valid roster file. %s.", rosterFile, e.getMessage()));
        }
    }

    public static List<Student> loadRoster(Reader reader)
            throws IOException, InvalidRosterFormatException {

        List<Student> students = new ArrayList<>();

        try (CSVParser parser = new CSVParser(reader,
                CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {

            validateHeader(parser);

            for (CSVRecord csvRecord : parser) {

                String rosterId = getValue(csvRecord, "identifier");

                var studentName = NamingStrategy.extractStudentName(rosterId);
                var group = NamingStrategy.extractGroup(rosterId);

                students.add(new Student(studentName, group, rosterId));
            }

        } catch (IllegalArgumentException e) { // Invalid identifier for NamingStrategy
            throw new InvalidRosterFormatException(e.getMessage());
        }

        if (students.isEmpty())
            throw new InvalidRosterFormatException("No students found in the roster file. Please check the content.");

        return new ArrayList<>(students);
    }

    // Checks that the header is exactly this four columnos (no more, no less): "identifier","github_username","github_id","name"
    private static void validateHeader(CSVParser parser) throws InvalidRosterFormatException {
        if (parser.getHeaderMap().size() != 4)
            throw new InvalidRosterFormatException("CSV header must contain exactly 4 columns.");

        for (String header : List.of("identifier", "github_username", "github_id", "name"))
            if (!parser.getHeaderMap().containsKey(header))
                throw new InvalidRosterFormatException("CSV does not contain '" + header + "' column.");

    }

    private static String getValue(CSVRecord csvRecord, String column) throws InvalidRosterFormatException {

        var value = findValue(csvRecord, column);

        if (value.isEmpty())
            throw new InvalidRosterFormatException(format("Record #%d: '%s' -> column '%s' cannot be blank",
                    csvRecord.getRecordNumber(), join(", ", csvRecord), column));

        return value.get();
    }

    private static Optional<String> findValue(CSVRecord csvRecord, String columnName) {
        String value = csvRecord.get(columnName);

        if (value == null || value.isBlank()) // For example `,a`
            return Optional.empty();

        return Optional.of(value);
    }

    /**
     * Exception thrown when the roster file format is invalid.
     */
    public static class InvalidRosterFormatException extends Exception {
        public InvalidRosterFormatException(String message) {
            super(message);
        }
    }
}
