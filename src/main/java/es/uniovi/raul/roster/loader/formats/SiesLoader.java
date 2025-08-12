package es.uniovi.raul.roster.loader.formats;

import static java.lang.String.*;
import static org.apache.poi.ss.usermodel.CellType.*;

import java.util.Optional;

import org.apache.poi.ss.usermodel.*;

import es.uniovi.raul.roster.loader.InvalidStudentFormatException;

/**
 * Loader for SIES Excel files.
 *
 */
public final class SiesLoader extends ExcelLoader {

    // Excel cell positions (0-based)
    private static final int HEADERS_ROW = 9; // Aquí deberían estar los encabezados (fila 10 en Excel)
    private static final String NAME_HEADER = "Alumno";
    private static final String LABORATORY_HEADER = "Prácticas de Laboratorio";

    private static final int NAME_COLUMN = 2; // Aquí debería estar la columna "Alumno" - Column C
    private static final int LABORATORY_COLUMN = 10; // Aquí debería estar la columna "Prácticas de Laboratorio" - Column K

    private static final String LAB_PREFIX = "Prácticas de Laboratorio-";
    private static final String ENGLISH_PREFIX = "Inglés-";

    public SiesLoader() {
        this.startingRow = HEADERS_ROW + 1;
        this.nameColumn = NAME_COLUMN;
        this.groupColumn = LABORATORY_COLUMN;
    }

    @Override
    protected void readSheet(Sheet sheet) throws InvalidStudentFormatException {

        validateSiesFormat(sheet);

        super.readSheet(sheet);
    }

    @Override
    protected void handleValues(String studentName, Optional<String> lab) throws InvalidStudentFormatException {

        var group = extractGroupFromLab(lab);

        super.handleValues(studentName, group);
    }

    /**
     * Validates that the file is a SIES excel file.
     * Checks that the headers are in the expected positions and have the correct values.
     */
    private static void validateSiesFormat(Sheet sheet) throws InvalidStudentFormatException {

        Row headerRow = sheet.getRow(HEADERS_ROW); // Row 10 (0-based index)
        if (headerRow == null)
            throw new InvalidStudentFormatException("Header row 10 not found in the sheet.");

        Cell alumnoCell = headerRow.getCell(NAME_COLUMN); // Column C (0-based index)
        String alumnoValue = (alumnoCell != null && alumnoCell.getCellType() == STRING)
                ? alumnoCell.getStringCellValue().trim()
                : "";

        Cell laboratoryCell = headerRow.getCell(LABORATORY_COLUMN); // Column K (0-based index)
        String laboratoryValue = (laboratoryCell != null && laboratoryCell.getCellType() == STRING)
                ? laboratoryCell.getStringCellValue().trim()
                : "";

        if (!NAME_HEADER.equalsIgnoreCase(alumnoValue) || !LABORATORY_HEADER.equalsIgnoreCase(laboratoryValue)) {
            throw new InvalidStudentFormatException(format(
                    "Expected headers not found at C10 and K10. Expected: C10='%s', K10='%s'", NAME_HEADER,
                    LABORATORY_HEADER));
        }
    }

    /**
     * Extracts the group ID from the laboratory string used in SIES files.
     *
     * Examples:
     * "Prácticas de Laboratorio-01" -> "01"
     * "Prácticas de Laboratorio-Inglés-02" -> "i02"
     *
     * @param laboratory The laboratory string to extract the group ID from.
     * @return The extracted group ID.
     */
    private Optional<String> extractGroupFromLab(Optional<String> laboratory) throws InvalidStudentFormatException {

        if (laboratory.isEmpty())
            return Optional.empty();

        String value = laboratory.get();
        if (!value.startsWith(LAB_PREFIX))
            throw new InvalidStudentFormatException(
                    "Laboratory must start with '" + LAB_PREFIX + "'. Value: '" + value + "'");

        String rest = value.substring(LAB_PREFIX.length());
        if (rest.startsWith(ENGLISH_PREFIX))
            rest = "i" + rest.substring(ENGLISH_PREFIX.length());

        return Optional.of(rest);
    }
}
