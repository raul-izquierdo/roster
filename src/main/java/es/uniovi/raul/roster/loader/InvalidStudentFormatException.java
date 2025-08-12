package es.uniovi.raul.roster.loader;

/**
 * Exception thrown when the expected format in the file is not valid (the format has been changed?).
 */
public class InvalidStudentFormatException extends Exception {
    public InvalidStudentFormatException(String message) {
        super(message);
    }
}
