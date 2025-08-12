package es.uniovi.raul.roster.cli;

import static java.lang.String.*;

public final class Console {

    public static final String BLACK = "\033[0;30m";
    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[0;32m";
    public static final String YELLOW = "\033[0;33m";
    public static final String BLUE = "\033[0;34m";
    public static final String MAGENTA = "\033[0;35m";
    public static final String CYAN = "\033[0;36m";
    public static final String WHITE = "\033[0;37m";

    // Bright (bold) versions
    public static final String BOLD_BLACK = "\033[1;30m";
    public static final String BOLD_RED = "\033[1;31m";
    public static final String BOLD_GREEN = "\033[1;32m";
    public static final String BOLD_YELLOW = "\033[1;33m";
    public static final String BOLD_BLUE = "\033[1;34m";
    public static final String BOLD_MAGENTA = "\033[1;35m";
    public static final String BOLD_CYAN = "\033[1;36m";
    public static final String BOLD_WHITE = "\033[1;37m";

    public static void printError(String message) {
        System.err.println(addColor(format("%n[Error] %s%n", message), BOLD_RED));
    }

    public static void printfError(String message, Object... args) {
        printError(format(message, args));
    }

    public static void printWarning(String message) {
        System.out.println(addColor(format("%n[Warning] %s%n", message), BOLD_MAGENTA));
    }

    public static void printfWarning(String message, Object... args) {
        printWarning(format(message, args));
    }

    public static String addColor(String message, String color) {
        final String AnsiReset = "\033[0m";
        return color + message + AnsiReset;
    }
}
