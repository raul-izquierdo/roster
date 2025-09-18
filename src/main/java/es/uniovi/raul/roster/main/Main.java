package es.uniovi.raul.roster.main;

import static es.uniovi.raul.roster.main.Reporter.*;

import java.io.IOException;
import java.util.*;

import es.uniovi.raul.roster.cli.*;
import es.uniovi.raul.roster.cli.Arguments.CliCommand;
import es.uniovi.raul.roster.loaders.groups.GroupsLoader;
import es.uniovi.raul.roster.loaders.groups.GroupsLoader.InvalidGroupFormatException;
import es.uniovi.raul.roster.loaders.roster.RosterLoader;
import es.uniovi.raul.roster.loaders.students.*;
import es.uniovi.raul.roster.model.*;

public class Main {
    public static void main(String[] args) {

        Optional<Arguments> argumentsOpt = ArgumentsParser.parse(args);

        if (argumentsOpt.isEmpty())
            System.exit(2);

        Arguments arguments = argumentsOpt.get();

        try {
            System.out.println(String.format(
                    "%nProcessing students from file '%s' (format: %s), using groups file '%s'.",
                    arguments.studentsFile, arguments.format, arguments.groupsFile));

            var teacherStudents = loadTeacherStudents(arguments.studentsFile, arguments.format, arguments.groupsFile);
            var roster = new Roster(teacherStudents);

            if (arguments.command == CliCommand.UPDATE)
                roster.setPreviousRoster(RosterLoader.load(arguments.rosterFile));

            boolean changesRequired = printRequiredChanges(roster, System.out);

            System.exit(changesRequired ? 1 : 0); // Exit with 1 if changes proposed, otherwise 0 (roster is up to date)

        } catch (Exception e) {
            Console.printError(e.getMessage());
            System.exit(2);
        }
    }

    public static List<Student> loadTeacherStudents(String studentsFile, FileFormat format, String groupsFile)
            throws IOException, InvalidStudentFormatException, InvalidGroupFormatException {

        List<String> teacherGroups = GroupsLoader.load(groupsFile);

        return StudentsLoader.load(studentsFile, format).stream()
                .filter(student -> teacherGroups.contains(student.group()))
                .toList();
    }

}
