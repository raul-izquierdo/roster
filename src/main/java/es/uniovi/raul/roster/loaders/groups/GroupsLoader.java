package es.uniovi.raul.roster.loaders.groups;

import static java.lang.String.*;

import java.io.*;
import java.util.*;

import org.apache.commons.csv.*;

/**
 * Loads the groups that are assgined to the teacher. Groups not present in this file will not be considered.
 */
public class GroupsLoader {

    public static List<String> load(String filePath) throws IOException, InvalidGroupFormatException {

        try (FileReader reader = new FileReader(filePath)) {
            return load(reader);

        } catch (InvalidGroupFormatException e) {
            throw new InvalidGroupFormatException(format("'%s'. %s", filePath, e.getMessage()));
        }
    }

    public static List<String> load(Reader reader) throws IOException, InvalidGroupFormatException {

        // Parse the whole stream with Commons CSV, like CsvLoader does.
        try (CSVParser parser = new CSVParser(reader,
                CSVFormat.DEFAULT.builder()
                        .setTrim(true)
                        .setIgnoreSurroundingSpaces(true)
                        .setSkipHeaderRecord(false)
                        .build())) {

            List<String> groups = new ArrayList<>();

            for (CSVRecord csvRecord : parser) {
                String group = getValue(csvRecord, 0);
                groups.add(group);
            }

            if (groups.isEmpty())
                throw new InvalidGroupFormatException("No groups found in the file. Please check the content.");

            return groups;

        }
    }

    private static String getValue(CSVRecord csvRecord, int column) throws InvalidGroupFormatException {

        var value = findValue(csvRecord, column);

        if (value.isEmpty())
            throw new InvalidGroupFormatException(
                    format("Record #%d: '%s' -> column '%d' (zero based) cannot be blank",
                            csvRecord.getRecordNumber(), join(", ", csvRecord), column));

        return value.get();
    }

    private static Optional<String> findValue(CSVRecord csvRecord, int column) {

        if (!csvRecord.isSet(column))
            return Optional.empty();

        String value = csvRecord.get(column);
        return (value == null || value.isBlank()) ? Optional.empty() : Optional.of(value);

    }

    public static class InvalidGroupFormatException extends Exception {
        public InvalidGroupFormatException(String message) {
            super(message);
        }
    }
}
