package es.uniovi.raul.roster.loaders.students;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import es.uniovi.raul.roster.model.Student;

class StudentsLoaderCsvTest {

    private List<Student> loadCsv(String content) throws Exception {
        InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        return StudentsLoader.load(is, FileFormat.CSV);
    }

    @Test
    @DisplayName("load(InputStream, CSV) returns students with groups only")
    void loadCsvHappyPath() throws Exception {
        var list = loadCsv("John Doe,01\nJane Roe,02\nNoGroup,\nAlsoNoGroup\n");
        assertEquals(2, list.size());
        assertEquals(new Student("John Doe", "01", "John Doe (01)"), list.get(0));
        assertEquals(new Student("Jane Roe", "02", "Jane Roe (02)"), list.get(1));
    }

    @Test
    @DisplayName("load(InputStream, CSV) returns students with quotes and spaces")
    // Si se pone un espacio después de la coma y antes de la comilla, se debe ignorar (IgnoreSurroundingSpaces hace que funcione; por defecto no)
    void loadCsvWithSurroundingSpaces() throws Exception {
        var list = loadCsv("""
                "Izquierdo, Raúl", "01"
                "Pérez, Manolo" , "i01"
                """);
        assertEquals(2, list.size());
        assertEquals(new Student("Izquierdo, Raúl", "01", "Izquierdo, Raúl (01)"), list.get(0));
        assertEquals(new Student("Pérez, Manolo", "i01", "Pérez, Manolo (i01)"), list.get(1));
    }

    @ParameterizedTest
    @ValueSource(strings = { ",01\n", ",\n", ",  \n" })
    @DisplayName("load(InputStream, CSV) throws when student name is blank")
    void loadCsvBlankNameThrows(String content) {
        Exception ex = assertThrows(InvalidStudentFormatException.class, () -> loadCsv(content));
        assertTrue(ex.getMessage().toLowerCase().contains("column '0'"));
    }

    @Test
    @DisplayName("load(InputStream, CSV) throws when file has no valid students")
    void loadCsvNoStudentsThrows() {
        // All rows have a name but no group, so builder collects none and Students.load throws the global error
        Exception ex = assertThrows(InvalidStudentFormatException.class, () -> loadCsv("NoGroup,\nAnother,\n"));
        assertTrue(ex.getMessage().toLowerCase().contains("no students"));
    }
}
