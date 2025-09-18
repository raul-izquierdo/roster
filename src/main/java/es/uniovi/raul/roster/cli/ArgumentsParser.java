package es.uniovi.raul.roster.cli;

import java.io.PrintStream;
import java.util.Optional;

import es.uniovi.raul.roster.loaders.students.FileFormat;
import io.github.cdimascio.dotenv.Dotenv;
import picocli.CommandLine;
import picocli.CommandLine.ParameterException;

/** Parses and validates command line arguments. */
public class ArgumentsParser {

    /**
     * Parses command line args.
     * Prints usage, version, or errors as needed.
     *
     * @param args the command line arguments
     * @return an Optional containing the parsed Arguments or empty if parsing failed
     */
    public static Optional<Arguments> parse(String[] args) {
        return parse(args, System.out, System.err);
    }

    public static Optional<Arguments> parse(String[] args, PrintStream out, PrintStream err) {

        final Arguments arguments = new Arguments();

        final CommandLine picocli = new CommandLine(arguments)
                .setCaseInsensitiveEnumValuesAllowed(true)
                // .setColorScheme(CommandLine.Help.defaultColorScheme(Help.Ansi.ON))
                .setSeparator(" "); // Use space (`-g file`) instead of "=" (`-g=file`);

        try {
            picocli.parseArgs(args);

            if (picocli.isUsageHelpRequested()) {
                picocli.usage(out);
                return Optional.empty();
            }

            if (picocli.isVersionHelpRequested()) {
                picocli.printVersionHelp(out);
                return Optional.empty();
            }

            ensureRequiredEnvironment(arguments, picocli);

            return Optional.of(arguments);

        } catch (ParameterException ex) {
            Console.printError(ex.getMessage());
            picocli.usage(err);
            return Optional.empty();
        }
    }

    //#  -----------------------------------
    private static void ensureRequiredEnvironment(Arguments arguments, final CommandLine picocli) {

        if (arguments.studentsFile == null)
            arguments.studentsFile = getEnvironmentVariable("STUDENTS_FILE")
                    .orElseThrow(() -> new ParameterException(picocli,
                            "Missing required arguments: 'STUDENTS_FILE' should be provided either via command line or in a '.env' file"));

        if (arguments.format == null) {
            String formatName = getEnvironmentVariable("STUDENTS_FORMAT")
                    .orElseThrow(() -> new ParameterException(picocli,
                            "Missing required arguments: 'STUDENTS_FORMAT' should be provided either via command line or in a '.env' file"));
            try {
                arguments.format = FileFormat.valueOf(formatName.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ParameterException(picocli, "Invalid STUDENTS_FORMAT in environment: " + formatName);
            }
        }
    }

    private static Optional<String> getEnvironmentVariable(String key) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String value = dotenv.get(key);
        if (value == null)
            value = System.getenv(key);
        return Optional.ofNullable(value);
    }

}
