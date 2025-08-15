package es.uniovi.raul.roster.naming;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ParenthesisStrategyTest {

    @Test
    void generateRosterIdHappyPath() {

        assertEquals("John Doe (01)", NamingStrategy.generateRosterId("John Doe", "01"));
    }

    @ParameterizedTest
    @CsvSource({ "'',01", " ,01", "John,''", "John,  " })
    @DisplayName("generateRosterId throws on blank name or group")
    void generateRosterIdInvalid(String name, String group) {

        assertThrows(IllegalArgumentException.class, () -> NamingStrategy.generateRosterId(name, group));
    }

    @Test
    void parseRosterIdHappyPath() {

        String id = "Jane Roe (i02)";
        assertEquals("Jane Roe", NamingStrategy.extractStudentName(id));
        assertEquals("i02", NamingStrategy.extractGroup(id));
    }

    @ParameterizedTest
    @CsvSource({ "no parenthesis", "(01)", "Name ()", "Name (01) extra" })
    @DisplayName("parse throws for malformed ids")
    void parseInvalid(String id) {

        assertThrows(IllegalArgumentException.class, () -> NamingStrategy.extractStudentName(id));
        assertThrows(IllegalArgumentException.class, () -> NamingStrategy.extractGroup(id));
    }
}
