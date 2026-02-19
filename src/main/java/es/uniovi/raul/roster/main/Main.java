package es.uniovi.raul.roster.main;

import static es.uniovi.raul.roster.loaders.groups.GroupsLoader.*;
import static es.uniovi.raul.roster.loaders.roster.RosterLoader.*;
import static es.uniovi.raul.roster.loaders.students.StudentsLoader.*;
import static es.uniovi.raul.roster.main.Reporter.*;

import java.io.IOException;
import java.util.*;

import es.uniovi.raul.roster.cli.*;
import es.uniovi.raul.roster.cli.Arguments.CliCommand;
import es.uniovi.raul.roster.loaders.groups.GroupsLoader.InvalidGroupFormatException;
import es.uniovi.raul.roster.loaders.roster.RosterLoader.InvalidRosterFormatException;
import es.uniovi.raul.roster.loaders.students.*;
import es.uniovi.raul.roster.model.Student;

/**
 * Entry point for the roster management application.
 * Processes student rosters and reports required changes for synchronization.
 */
public class Main {
    private static final int NO_CHANGES_REQUIRED = 0; // roster is already up to date
    private static final int CHANGES_REQUIRED = 1; // roster must be manually updated
    private static final int ERROR = 2;

    public static void main(String[] args) {

        Optional<Arguments> argumentsOpt = ArgumentsParser.parse(args);

        if (argumentsOpt.isEmpty())
            System.exit(ERROR);

        int exitCode = NO_CHANGES_REQUIRED;
        try {

            exitCode = run(argumentsOpt.get());

        } catch (Exception e) {
            System.err.printf("%n[Error] %s%n", e.getMessage());
            exitCode = ERROR;
        }

        System.exit(exitCode);
    }

    private static int run(Arguments arguments) throws IOException, InvalidStudentFormatException,
            InvalidGroupFormatException, InvalidRosterFormatException {

        var latestStudents = loadTeacherStudents(arguments.studentsFile, arguments.format,
                arguments.groupsFile);

        if (latestStudents.isEmpty()) {
            System.out.printf("%n[Warning] %s%n",
                    "No students found for the teacher's groups. No roster will be generated.");
            return NO_CHANGES_REQUIRED;
        }

        var existingRoster = (arguments.command == CliCommand.UPDATE)
                ? loadRoster(arguments.rosterFile)
                : Collections.<Student>emptyList();

        boolean changesRequired = printRequiredChanges(existingRoster, latestStudents, System.out);

        return changesRequired ? CHANGES_REQUIRED : NO_CHANGES_REQUIRED;
    }

    /**
     * Loads the students from the given file, removing those that do not belong to the teacher's groups.
     *
     * @param studentsFile the path to the students file
     * @param format the format of the students file
     * @param groupsFile the path to the file with the teacher groups
     *
     * @throws IOException if an I/O error occurs
     * @throws InvalidStudentFormatException if the students file format is invalid
     * @throws InvalidGroupFormatException if the groups file format is invalid
     */
    public static List<Student> loadTeacherStudents(String studentsFile, FileFormat format, String groupsFile)
            throws IOException, InvalidStudentFormatException, InvalidGroupFormatException {

        List<String> teacherGroups = loadTeacherGroups(groupsFile);

        List<Student> allStudents = loadStudents(studentsFile, format);
        System.out.printf("%d students read from '%s' (format: %s).%n", allStudents.size(), studentsFile, format);

        List<Student> teacherStudents = allStudents.stream()
                .filter(student -> teacherGroups.contains(student.group()))
                .toList();
        System.out.printf("%d students belong to the teacher's groups read from '%s' (%s)%n",
                teacherStudents.size(),
                groupsFile,
                String.join(", ", teacherGroups));

        return teacherStudents;
    }

}
