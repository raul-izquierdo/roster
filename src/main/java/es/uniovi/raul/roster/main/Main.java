package es.uniovi.raul.roster.main;

import static es.uniovi.raul.roster.loaders.roster.RosterLoader.*;
import static es.uniovi.raul.roster.main.Reporter.*;

import java.io.IOException;
import java.util.*;

import es.uniovi.raul.roster.cli.*;
import es.uniovi.raul.roster.cli.Arguments.CliCommand;
import es.uniovi.raul.roster.loaders.groups.GroupsLoader;
import es.uniovi.raul.roster.loaders.groups.GroupsLoader.InvalidGroupFormatException;
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

        Arguments arguments = argumentsOpt.get();

        try {

            var latestStudents = loadTeacherStudents(arguments.studentsFile, arguments.format,
                    arguments.groupsFile);

            if (latestStudents.isEmpty()) {
                Console.printWarning("No students found for the teacher's groups. No roster will be generated.");
                System.exit(NO_CHANGES_REQUIRED);
            }

            var existingRoster = (arguments.command == CliCommand.UPDATE)
                    ? loadRoster(arguments.rosterFile)
                    : List.<Student>of();

            boolean changesRequired = printRequiredChanges(existingRoster, latestStudents, System.out);

            System.exit(changesRequired ? CHANGES_REQUIRED : NO_CHANGES_REQUIRED);

        } catch (Exception e) {
            Console.printError(e.getMessage());
            System.exit(ERROR);
        }
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

        List<String> teacherGroups = GroupsLoader.loadTeacherGroups(groupsFile);

        List<Student> allStudents = StudentsLoader.loadStudents(studentsFile, format);
        System.out.println(
                String.format("%d students read from '%s' (format: %s).", allStudents.size(), studentsFile, format));

        List<Student> teacherStudents = allStudents.stream()
                .filter(student -> teacherGroups.contains(student.group()))
                .toList();
        System.out.println(String.format("%d students belong to the teacher's groups read from '%s' (%s)",
                teacherStudents.size(),
                groupsFile,
                String.join(", ", teacherGroups)));

        return teacherStudents;
    }

}
