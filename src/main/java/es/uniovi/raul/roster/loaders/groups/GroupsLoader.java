package es.uniovi.raul.roster.loaders.groups;

import static java.lang.String.*;

import java.io.*;
import java.util.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

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
                        .setSkipHeaderRecord(false)
                        .build())) {

            List<String> groups = new ArrayList<>();

            for (CSVRecord csvRecord : parser) {
                String groupId = csvRecord.get(0);

                if (groupId == null || groupId.isBlank()) // For example `,a`
                    throw new InvalidGroupFormatException(csvRecord, "Group ID cannot be blank.");

                groups.add(groupId);
            }

            if (groups.isEmpty())
                throw new InvalidGroupFormatException("No groups found in the file. Please check the content.");

            return groups;

        } catch (UncheckedIOException e) { // Handle the unchecked exception from CSV parsing
            throw new InvalidGroupFormatException(e.getCause().getMessage());
        }
    }

    public static class InvalidGroupFormatException extends Exception {
        public InvalidGroupFormatException(String message) {
            super(message);
        }

        public InvalidGroupFormatException(CSVRecord row, String message) {
            super(format("Record #%d: '%s' -> %s", row.getRecordNumber(), join(", ", row), message));
        }

    }
}
