package es.uniovi.raul.roster.cli;

import es.uniovi.raul.roster.loaders.students.FileFormat;
import picocli.CommandLine.*;

// CHECKSTYLE:OFF

@Command(name = "roster", version = "3.1.1", showDefaultValues = true, mixinStandardHelpOptions = true, usageHelpAutoWidth = true, description = Messages.DESCRIPTION, customSynopsis = Messages.USAGE, footer = Messages.CREDITS)
public class Arguments {

    public enum CliCommand {
        CREATE, UPDATE
    }

    // First positional: simulated subcommand (required)
    @Parameters(index = "0", arity = "1", description = "The action to perform. Supported values: ${COMPLETION-CANDIDATES}.")
    public CliCommand command;

    // Second positional: students file (optional)
    @Parameters(index = "1", arity = "0..1", description = "The file with the list of students and their groups (optional).")
    public String studentsFile;

    @Option(names = "-f", description = "The format of the students file. Supported values: ${COMPLETION-CANDIDATES}.", required = false)
    public FileFormat format;

    @Option(names = "-s", defaultValue = "schedule.csv", description = "The file listing the teacher's groups.")
    public String groupsFile;

    @Option(names = "-r", defaultValue = "classroom_roster.csv", description = "The roster CSV file downloaded from GitHub Classroom (used only with the 'update' command).")
    public String rosterFile;

}

class Messages {
    static final String DESCRIPTION = """

            This tool helps you identify which students need to be added, updated, or removed from a GitHub Classroom roster.
            For more information, visit: https://github.com/raul-izquierdo/roster
            """;

    static final String USAGE = "\n\tjava -jar roster.jar <command> [OPTIONS] [<studentsFile>]";

    static final String CREDITS = """

            Escuela de Ingenieria Informatica. Universidad de Oviedo.
            Raúl Izquierdo Castanedo (raul@uniovi.es)
            """;

}
