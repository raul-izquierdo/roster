package es.uniovi.raul.roster.roster;

import static org.junit.jupiter.api.Assertions.*;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.uniovi.raul.roster.model.Student;
import es.uniovi.raul.roster.naming.ParenthesisStrategy;

class RosterTest {

    private List<Student> loadCsv(String content) throws Exception {
        Reader r = new StringReader(content);
        return Roster.load(r, new ParenthesisStrategy());
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
        Exception ex = assertThrows(Roster.InvalidRosterFormatException.class, () -> loadCsv(bad));
        assertTrue(ex.getMessage().toLowerCase().contains("csv"));
    }

    @Test
    @DisplayName("load(Reader) throws when no students present")
    void emptyRosterThrows() {
        String onlyHeader = "identifier,github_username,github_id,name\n";
        Exception ex = assertThrows(Roster.InvalidRosterFormatException.class, () -> loadCsv(onlyHeader));
        assertTrue(ex.getMessage().toLowerCase().contains("no students"));
    }
}
