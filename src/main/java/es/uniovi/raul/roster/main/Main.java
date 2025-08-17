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
            System.exit(1);

        Arguments arguments = argumentsOpt.get();

        try {

            var teacherStudents = loadTeacherStudents(arguments.studentsFile, arguments.format, arguments.groupsFile);
            var roster = new Roster(teacherStudents);

            if (arguments.command == CliCommand.UPDATE)
                roster.setPreviousRoster(RosterLoader.load(arguments.rosterFile));

            printRequiredChanges(roster, System.out);

        } catch (Exception e) {
            Console.printError(e.getMessage());
            System.exit(1);
        }

        System.exit(0);

    }

    public static List<Student> loadTeacherStudents(String studentsFile, FileFormat format, String groupsFile)
            throws IOException, InvalidStudentFormatException, InvalidGroupFormatException {

        List<Student> students = StudentsLoader.load(studentsFile, format);

        if (groupsFile != null) {
            List<String> teacherGroups = GroupsLoader.load(groupsFile);
            students = students.stream()
                    .filter(student -> teacherGroups.contains(student.group()))
                    .toList();
        } else
            Console.printWarning(
                    "The teacher has not specified his groups using a <groups.csv> file. So all students will be included in the output.");

        return students;

    }

}
