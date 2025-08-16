package es.uniovi.raul.roster.loaders.roster;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import es.uniovi.raul.roster.model.Student;

class RosterLoaderTest {

    private List<Student> loadCsv(String content) throws Exception {
        Reader roster = new StringReader(content);
        return RosterLoader.load(roster).getStudents().toList();
    }

    @Test
    @DisplayName("load(Reader) parses roster CSV with correct header and rows")
    void loadValidRosterCsv() throws Exception {
        String csv = """
                identifier,github_username,github_id,name
                John Doe (01), johnd,1,John Doe
                Jane Roe (02),janer,2,Jane Roe
                Jeane Roe (03), "janer",2,Jeane Roe
                """;
        var roster = loadCsv(csv);
        assertEquals(3, roster.size());
        assertEquals("John Doe", roster.get(0).name());
        assertEquals("01", roster.get(0).group());
        assertEquals("John Doe (01)", roster.get(0).rosterId());
        assertEquals("Jane Roe (02)", roster.get(1).rosterId());
        assertEquals("Jeane Roe (03)", roster.get(2).rosterId());
    }

    @Test
    @DisplayName("load(Reader) throws for invalid header (missing column)")
    void invalidHeaderThrows() {
        String bad = "identifier,github_username,github_id\nJohn Doe (01),johnd,1\n";
        Exception ex = assertThrows(RosterLoader.InvalidRosterFormatException.class, () -> loadCsv(bad));
        assertTrue(ex.getMessage().toLowerCase().contains("csv"));
    }

    @Test
    @DisplayName("load(Reader) throws when no students present")
    void emptyRosterThrows() {
        String onlyHeader = "identifier,github_username,github_id,name\n";
        Exception ex = assertThrows(RosterLoader.InvalidRosterFormatException.class, () -> loadCsv(onlyHeader));
        assertTrue(ex.getMessage().toLowerCase().contains("no students"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            ", johnd,1,John Doe",
            "\"\", johnd,1,John Doe",
            "wrongFormatId, johnd,1,John Doe",
    })
    @DisplayName("load(Reader) throws when no identifier present or wrong")
    void noIdentifierThrows(String row) {
        String header = "identifier,github_username,github_id,name\n";
        String csv = header + row + "\n";
        assertThrows(RosterLoader.InvalidRosterFormatException.class, () -> loadCsv(csv));
    }
}
