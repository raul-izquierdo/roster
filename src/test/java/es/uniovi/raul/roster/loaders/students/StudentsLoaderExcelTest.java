package es.uniovi.raul.roster.loaders.students;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.uniovi.raul.roster.model.Student;

class StudentsLoaderExcelTest {

    private byte[] excelWithRows(String[][] rows) throws Exception {
        try (var wb = new XSSFWorkbook()) {
            Sheet s = wb.createSheet();
            for (int i = 0; i < rows.length; i++) {
                Row r = s.createRow(i);
                for (int j = 0; j < rows[i].length; j++) {
                    if (rows[i][j] != null)
                        r.createCell(j).setCellValue(rows[i][j]);
                }
            }
            try (var baos = new ByteArrayOutputStream()) {
                wb.write(baos);
                return baos.toByteArray();
            }
        }
    }

    private List<Student> loadExcel(byte[] bytes) throws Exception {
        InputStream is = new java.io.ByteArrayInputStream(bytes);
        return StudentsLoader.loadStudents(is, FileFormat.EXCEL);
    }

    @Test
    @DisplayName("load(InputStream, EXCEL) returns students with groups only")
    void loadExcelHappyPath() throws Exception {
        byte[] bytes = excelWithRows(new String[][] {
                { "John", "01" },
                { "Jane", "02" },
                { "NoGroup", null }
        });
        var list = loadExcel(bytes);
        assertEquals(2, list.size());
        assertEquals(new Student("John", "01", "John (01)"), list.get(0));
        assertEquals(new Student("Jane", "02", "Jane (02)"), list.get(1));
    }

    @Test
    @DisplayName("load(InputStream, EXCEL) throws when file has no valid students")
    void loadExcelNoStudentsThrows() throws Exception {
        byte[] bytes = excelWithRows(new String[][] {
                { "NoGroup", null },
                { "AlsoNoGroup", null }
        });
        Exception ex = assertThrows(InvalidStudentFormatException.class, () -> loadExcel(bytes));
        assertTrue(ex.getMessage().toLowerCase().contains("no students"));
    }
}
