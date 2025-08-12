package es.uniovi.raul.roster.cli;

import picocli.CommandLine;
import picocli.CommandLine.*;

import java.io.PrintStream;
import java.util.Optional;

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

            return Optional.of(arguments);

        } catch (ParameterException ex) {
            Console.printError(ex.getMessage());
            picocli.usage(err);
            return Optional.empty();
        }
    }

}
