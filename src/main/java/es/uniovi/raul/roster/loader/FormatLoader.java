package es.uniovi.raul.roster.loader;

import java.io.*;

/**
 * Interface for loading student data from various formats.
 * Implementations should handle the specifics of reading from different input sources.
 */
public interface FormatLoader {
    void load(InputStream inputStream, StudentBuilder builder) throws InvalidStudentFormatException, IOException;
}
