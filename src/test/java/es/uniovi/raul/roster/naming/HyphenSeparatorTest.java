package es.uniovi.raul.roster.naming;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HyphenSeparatorTest {

    @Test
    void generateRosterIdHappyPath() {
        var ns = new HyphenSeparator();
        assertEquals("John - 01", ns.generateRosterId("John", "01"));
    }

    @ParameterizedTest
    @CsvSource({ "'',01", " ,01", "John,''", "John,  " })
    @DisplayName("generateRosterId throws on blank name or group")
    void generateRosterIdInvalid(String name, String group) {
        var ns = new HyphenSeparator();
        assertThrows(IllegalArgumentException.class, () -> ns.generateRosterId(name, group));
    }

    @Test
    void parseRosterIdHappyPath() {
        var ns = new HyphenSeparator();
        String id = "Jane Roe - i02";
        assertEquals("Jane Roe", ns.extractStudentName(id));
        assertEquals("i02", ns.extractGroup(id));
    }

    @ParameterizedTest
    @CsvSource({ "no-separator", " - 01", "Name - ", " - " })
    @DisplayName("parse throws for malformed ids")
    void parseInvalid(String id) {
        var ns = new HyphenSeparator();
        assertThrows(IllegalArgumentException.class, () -> ns.extractStudentName(id));
        assertThrows(IllegalArgumentException.class, () -> ns.extractGroup(id));
    }
}
