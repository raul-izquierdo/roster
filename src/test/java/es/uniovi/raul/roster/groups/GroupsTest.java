package es.uniovi.raul.roster.groups;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GroupsTest {

    private List<String> loadFrom(String content) throws Exception {
        Reader reader = new StringReader(content);
        return Groups.load(reader);
    }

    @ParameterizedTest
    @DisplayName("load(Reader) parses valid CSV with various formats")
    @ValueSource(strings = {
            "01\n02\n03\n", // Simple CSV with one group per line
            "01,A\n02,B\n03,C\n", // CSV with more columns
            "01,\n02,,\n03,\"\"\n" // CSV with some missing columns
    })
    void loadValidCsvVariants(String content) throws Exception {
        var groups = loadFrom(content);
        assertEquals(List.of("01", "02", "03"), groups);
    }

    @ParameterizedTest
    @ValueSource(strings = { ",A\n", "\n", ",\n01\n" })
    @DisplayName("load(Reader) throws when a record has blank group id or file is empty")
    void loadInvalidOrEmpty(String content) {
        Exception ex = assertThrows(Groups.InvalidGroupFormatException.class, () -> loadFrom(content));
        assertNotNull(ex.getMessage());
    }
}
