package es.uniovi.raul.roster.loaders.students.formats;

import static org.apache.poi.ss.usermodel.CellType.*;
import static org.apache.poi.ss.usermodel.WorkbookFactory.*;

import java.io.*;
import java.util.Optional;

import org.apache.poi.ss.usermodel.*;

import es.uniovi.raul.roster.loaders.students.*;

public class ExcelLoader implements FormatLoader {

    protected int startingRow;
    protected int nameColumn;
    protected int groupColumn;

    protected StudentBuilder studentsBuilder;

    public ExcelLoader() {
        this.startingRow = 0; // Default starting row (no headers)
        this.nameColumn = 0; // Default name column (first column)
        this.groupColumn = 1; // Default group column (second column)
    }

    @Override
    public final void load(InputStream inputStream, StudentBuilder builder)
            throws InvalidStudentFormatException, IOException {

        this.studentsBuilder = builder;
        this.studentsBuilder.start();

        try (Workbook workbook = create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            readSheet(sheet);
        }
    }

    /**
     * Reads the Excel sheet starting from the specified row and processes each row.
     */
    protected void readSheet(Sheet sheet) throws InvalidStudentFormatException {

        for (int rowIndex = startingRow; rowIndex <= sheet.getLastRowNum(); rowIndex++) {

            Row row = sheet.getRow(rowIndex);

            if (row == null) // Excel can have sparse rows
                continue;

            var nameOpt = getCellValue(row, nameColumn);
            if (nameOpt.isEmpty())
                break; // No more students in this file

            var laboratoryOpt = getCellValue(row, groupColumn);

            handleValues(nameOpt.get(), laboratoryOpt);
        }
    }

    /**
     * Handles the values extracted from the Excel sheet. Only students with a group assigned will be processed.
     *
     * @param studentName The name of the student.
     * @param group The group or laboratory associated with the student.
     * @throws InvalidStudentFormatException If the group format is invalid.
     */
    protected void handleValues(String studentName, Optional<String> group) throws InvalidStudentFormatException {
        studentsBuilder.buildStudent(studentName, group);
    }

    private static Optional<String> getCellValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        return (cell != null && cell.getCellType() == STRING)
                ? Optional.of(cell.getStringCellValue().trim())
                : Optional.empty();
    }
}
