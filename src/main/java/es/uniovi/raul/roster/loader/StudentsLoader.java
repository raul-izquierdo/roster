package es.uniovi.raul.roster.loader;

import java.io.*;
import java.util.*;

import es.uniovi.raul.roster.model.Student;
import es.uniovi.raul.roster.naming.NamingStrategy;

/**
 * Entry point for loading student data from various file formats.
 */
public class StudentsLoader {

    public static List<Student> load(String fileName, FileFormat format)
            throws InvalidStudentFormatException, IOException {

        try (InputStream inputStream = new FileInputStream(fileName)) {

            return load(inputStream, format);

        } catch (InvalidStudentFormatException e) {
            throw new InvalidStudentFormatException(String.format(
                    "'%s' is not a valid '%s' file. %s%nPlease check the format and try again.",
                    fileName, format.name().toLowerCase(), e.getMessage()));
        }
    }

    public static List<Student> load(InputStream inputStream, FileFormat format)
            throws InvalidStudentFormatException, IOException {

        FormatLoader loader = format.createFormatLoader();

        var builder = new ListBuilder();
        loader.load(inputStream, builder);

        var studentsList = builder.getStudents();

        if (studentsList.isEmpty())
            throw new InvalidStudentFormatException("No students found in the file. Please check the content.");

        return studentsList;
    }

}

class ListBuilder implements StudentBuilder {
    private List<Student> students;

    @Override
    public void start() {
        students = new ArrayList<>();
    }

    @Override
    public void buildStudent(String name, Optional<String> group) {

        if (group.isEmpty()) // If no group is still assigned, skip this student
            return;

        var rosterId = NamingStrategy.generateRosterId(name, group.get());

        students.add(new Student(name, group.get(), rosterId));
    }

    public List<Student> getStudents() {
        return new ArrayList<>(students);
    }
}
