package classrepo.common;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_COMMAND_NOT_FOUND = "Command not recognised! Displaying commands available.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format! \n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The person index provided is invalid. "
            + "Run the list/find command to generate a list of person.";
    public static final String MESSAGE_PERSON_NOT_IN_ADDRESSBOOK = "Person could not be found in address book!";
    public static final String MESSAGE_STATISTIC_NOT_IN_STATISTICSBOOK = "Statistic could not be found in statistics "
            + "book!";
    public static final String MESSAGE_INVALID_STATISTICS_DISPLAYED_INDEX = "The statistics index provided is invalid. "
            + "Run the liststatistics command to generate a list of statistics.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d persons listed!";
    public static final String MESSAGE_PROGRAM_LAUNCH_ARGS_USAGE = "Launch command format: "
            + "java Main [STORAGE_FILE_PATH]";
    public static final String MESSAGE_USING_STORAGE_FILE = "Using storage file : %1$s";
    public static final String MESSAGE_INSUFFICIENT_PRIVILEGE = "Insufficient Privilege.\n"
            + "Requires privilege level of %s, but current level is %s";
    public static final String MESSAGE_NOT_LOGGED_IN = "Unable to process command as user is not logged in.\n";
    public static final String MESSAGE_WRONG_NUMBER_ARGUMENTS =
            "Wrong number of arguments, expected %d, actual %d.\n%s";
    public static final String MESSAGE_FEES_LISTED_OVERVIEW = "%1$d fees listed!";
    public static final String MESSAGE_USING_EXAMS_FILE = "Using exams file : %1$s";
    public static final String MESSAGE_EXAMS_LISTED_OVERVIEW = "%1$d exams listed!";
    public static final String MESSAGE_USING_STATISTICS_FILE = "Using statistics file : %1$s";
    public static final String MESSAGE_STATISTICS_LISTED_OVERVIEW = "%1$d statistics listed!";
    public static final String MESSAGE_INVALID_GRADES = "Invalid command format! - Value of grades is out of bounds!";
    public static final String MESSAGE_EXAM_NOT_IN_EXAMBOOK = "Exam could not be found in exam book!";
    public static final String MESSAGE_INVALID_EXAM_DISPLAYED_INDEX = "The exam index provided is invalid. "
            + "Run the examslist command to generate a list of exams.";
    public static final String MESSAGE_INVALID_ASSESSMENT_DISPLAYED_INDEX = "The assessment index provided is invalid. "
            + "Run the listassess command to generate a list of assessments.";
    public static final String MESSAGE_ASSESSMENT_NOT_IN_ADDRESSBOOK = "Assessment could not be found in address book!";
    public static final String MESSAGE_ASSESSMENTS_LISTED_OVERVIEW = "%1$d assessments listed!";
    public static final String MESSAGE_DATE_CONSTRAINTS = "Date format should be in DD-MM-YYYY and be valid dates.\n";
    public static final String MESSAGE_FEES_VALUE_CONSTRAINTS = "Fees must be positive and not be 0.00.\n";
    public static final String MESSAGE_NO_NON_PRIVATE_EXAMS = "%1$s has no non-private exams.";
    public static final String MESSAGE_NO_EXAMS = "%1$s has no exams.";
    public static final String MESSAGE_WRONG_TARGET = "You cannot check the exams of others.";

    public static final String MESSAGE_WELCOME_ASCII_ART = "\n"
            + "   _____ _               _____                   __      ____ _  _   \n"
            + "  / ____| |             |  __ \\                  \\ \\    / /_ | || |  \n"
            + " | |    | | __ _ ___ ___| |__) |___ _ __   ___    \\ \\  / / | | || |_ \n"
            + " | |    | |/ _` / __/ __|  _  // _ \\ '_ \\ / _ \\    \\ \\/ /  | |__   _|\n"
            + " | |____| | (_| \\__ \\__ \\ | \\ \\  __/ |_) | (_) |    \\  /   | |_ | |  \n"
            + "  \\_____|_|\\__,_|___/___/_|  \\_\\___| .__/ \\___/      \\/    |_(_)|_|  \n"
            + "                                   | |                               \n"
            + "                                   |_|                               \n";
}
