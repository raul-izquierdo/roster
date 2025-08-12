package es.uniovi.raul.roster.loader.formats;

import static java.lang.String.*;

import java.io.*;
import java.util.Optional;

import org.apache.commons.csv.*;

import es.uniovi.raul.roster.loader.*;

public final class CsvLoader implements FormatLoader {

    @Override
    public void load(InputStream inputStream, StudentBuilder builder)
            throws InvalidStudentFormatException, IOException {

        builder.start();

        try (CSVParser parser = new CSVParser(new InputStreamReader(inputStream),
                CSVFormat.DEFAULT.builder()
                        .setTrim(true)
                        .setSkipHeaderRecord(false)
                        .build())) {

            for (CSVRecord csvRecord : parser) {

                var studentName = getValue(csvRecord, 0);

                if (studentName.isEmpty())
                    throw new InvalidStudentFormatException(format("Record #%d: '%s' -> Student name cannot be blank",
                            csvRecord.getRecordNumber(), join(", ", csvRecord)));

                var group = getValue(csvRecord, 1);

                builder.buildStudent(studentName.get(), group);
            }

        } catch (UncheckedIOException e) { // Handle the unchecked exception from CSV parsing
            throw new InvalidStudentFormatException(e.getCause().getMessage());
        }
    }

    private Optional<String> getValue(CSVRecord csvRecord, int column) {

        try {
            String value = csvRecord.get(column);

            if (value == null || value.isBlank()) // For example `,a`
                return Optional.empty();

            return Optional.of(value);

        } catch (ArrayIndexOutOfBoundsException e) { // Column does not exist
            return Optional.empty();
        }
    }
}
