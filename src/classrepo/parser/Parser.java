package classrepo.parser;

import static classrepo.common.Messages.MESSAGE_DATE_CONSTRAINTS;
import static classrepo.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static classrepo.common.Messages.MESSAGE_WRONG_NUMBER_ARGUMENTS;
import static classrepo.parser.RegexPattern.ASSESSMENT_DATA_ARGS_FORMAT;
import static classrepo.parser.RegexPattern.ATTENDANCE_ARGS_FORMAT;
import static classrepo.parser.RegexPattern.ATTENDANCE_VIEW_DATE_FORMAT;
import static classrepo.parser.RegexPattern.BASIC_COMMAND_FORMAT;
import static classrepo.parser.RegexPattern.BOOLEAN_ARGS_FORMAT;
import static classrepo.parser.RegexPattern.EDIT_EXAM_INDEX_ARGS_FORMAT;
import static classrepo.parser.RegexPattern.EXAM_DATA_ARGS_FORMAT;
import static classrepo.parser.RegexPattern.FEES_DATA_ARGS_FORMAT;
import static classrepo.parser.RegexPattern.KEYWORDS_ARGS_FORMAT;
import static classrepo.parser.RegexPattern.PERSON_DATA_ARGS_FORMAT;
import static classrepo.parser.RegexPattern.PERSON_INDEX_ARGS_FORMAT;
import static java.lang.Integer.parseInt;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;

import classrepo.commands.Command;
import classrepo.commands.IncorrectCommand;
import classrepo.commands.account.AddAccountCommand;
import classrepo.commands.account.DeleteAccountCommand;
import classrepo.commands.account.ListAccountCommand;
import classrepo.commands.account.LoginCommand;
import classrepo.commands.account.LogoutCommand;
import classrepo.commands.assessment.AddAssessmentCommand;
import classrepo.commands.assessment.AddAssignmentStatistics;
import classrepo.commands.assessment.AddGradesCommand;
import classrepo.commands.assessment.DeleteAssessmentCommand;
import classrepo.commands.assessment.DeleteGradesCommand;
import classrepo.commands.assessment.DeleteStatisticsCommand;
import classrepo.commands.assessment.ListAssessmentCommand;
import classrepo.commands.assessment.ListStatisticsCommand;
import classrepo.commands.assessment.ViewGradesCommand;
import classrepo.commands.attendance.ReplaceAttendanceCommand;
import classrepo.commands.attendance.UpdateAttendanceCommand;
import classrepo.commands.attendance.ViewAttendanceDateCommand;
import classrepo.commands.attendance.ViewAttendancePersonCommand;
import classrepo.commands.commandformat.KeywordsFormatCommand;
import classrepo.commands.commandformat.indexformat.IndexFormatCommand;
import classrepo.commands.commandformat.indexformat.ObjectTargeted;
import classrepo.commands.exams.AddExamCommand;
import classrepo.commands.exams.ClearExamsCommand;
import classrepo.commands.exams.DeleteExamCommand;
import classrepo.commands.exams.DeregisterExamCommand;
import classrepo.commands.exams.EditExamCommand;
import classrepo.commands.exams.ListExamsCommand;
import classrepo.commands.exams.RegisterExamCommand;
import classrepo.commands.exams.ViewExamsCommand;
import classrepo.commands.fees.EditFeesCommand;
import classrepo.commands.fees.ListDueFeesCommand;
import classrepo.commands.fees.ListFeesCommand;
import classrepo.commands.fees.PaidFeesCommand;
import classrepo.commands.fees.ViewFeesCommand;
import classrepo.commands.general.ExitCommand;
import classrepo.commands.general.HelpCommand;
import classrepo.commands.person.AddCommand;
import classrepo.commands.person.ClearCommand;
import classrepo.commands.person.DeleteCommand;
import classrepo.commands.person.FindCommand;
import classrepo.commands.person.ListAllCommand;
import classrepo.commands.person.ListCommand;
import classrepo.commands.person.ViewAllCommand;
import classrepo.commands.person.ViewCommand;
import classrepo.commands.person.ViewSelfCommand;
import classrepo.commands.privilege.EditPasswordCommand;
import classrepo.commands.privilege.RaisePrivilegeCommand;
import classrepo.commands.privilege.SetPermanentAdminCommand;
import classrepo.commands.privilege.ViewPrivilegeCommand;
import classrepo.common.Messages;
import classrepo.data.exception.IllegalValueException;

/**
 * Parses user input.
 */
public class Parser {
    /**
     * Signals that the user input could not be parsed.
     */
    public static class ParseException extends Exception {
        ParseException(String message) {
            super(message);
        }
    }

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     */
    public Command parseCommand(String userInput) {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");
        switch (commandWord) {

        case AddCommand.COMMAND_WORD:
            return prepareAdd(arguments);

        case EditFeesCommand.COMMAND_WORD:
            return prepareFees(arguments);

        case PaidFeesCommand.COMMAND_WORD:
            return prepareSingleIndexCommand(arguments, new PaidFeesCommand(), ObjectTargeted.PERSON);

        case DeleteCommand.COMMAND_WORD:
            return prepareSingleIndexCommand(arguments, new DeleteCommand(), ObjectTargeted.PERSON);

        case ClearCommand.COMMAND_WORD:
            return prepareVoidCommand(arguments, new ClearCommand());

        case FindCommand.COMMAND_WORD:
            return prepareFind(arguments);

        case ListCommand.COMMAND_WORD:
            return prepareVoidCommand(arguments, new ListCommand());

        case ListAllCommand.COMMAND_WORD:
            return prepareVoidCommand(arguments, new ListAllCommand());

        case ViewCommand.COMMAND_WORD:
            return prepareSingleIndexCommand(arguments, new ViewCommand(), ObjectTargeted.PERSON);

        case ViewAllCommand.COMMAND_WORD:
            return prepareSingleIndexCommand(arguments, new ViewAllCommand(), ObjectTargeted.PERSON);

        case ExitCommand.COMMAND_WORD:
            return prepareVoidCommand(arguments, new ExitCommand());

        case ViewPrivilegeCommand.COMMAND_WORD:
            return new ViewPrivilegeCommand();

        case ViewSelfCommand.COMMAND_WORD:
            return prepareVoidCommand(arguments, new ViewSelfCommand());

        case RaisePrivilegeCommand.COMMAND_WORD:
            return prepareKeywordsCommand(arguments, new RaisePrivilegeCommand());

        case SetPermanentAdminCommand.COMMAND_WORD:
            return prepareSetPermAdmin(arguments);

        case EditPasswordCommand.COMMAND_WORD:
            return prepareKeywordsCommand(arguments, new EditPasswordCommand());

        case AddExamCommand.COMMAND_WORD:
            return prepareAddExam(arguments);

        case AddGradesCommand.COMMAND_WORD:
            return prepareAddGrades(arguments);

        case ViewGradesCommand.COMMAND_WORD:
            return prepareSingleIndexCommand(arguments, new ViewGradesCommand(), ObjectTargeted.PERSON);

        case AddAccountCommand.COMMAND_WORD:
            return prepareAddAccount(arguments);

        case DeleteAccountCommand.COMMAND_WORD:
            return prepareSingleIndexCommand(arguments, new DeleteAccountCommand(), ObjectTargeted.PERSON);

        case ListAccountCommand.COMMAND_WORD:
            return prepareVoidCommand(arguments, new ListAccountCommand());

        case LoginCommand.COMMAND_WORD:
            return prepareKeywordsCommand(arguments, new LoginCommand());

        case LogoutCommand.COMMAND_WORD:
            return prepareVoidCommand(arguments, new LogoutCommand());

        case ViewFeesCommand.COMMAND_WORD:
            return prepareViewFees(arguments);

        case AddAssignmentStatistics.COMMAND_WORD:
            return prepareSingleIndexCommand(arguments, new AddAssignmentStatistics(), ObjectTargeted.ASSESSMENT);

        case ListStatisticsCommand.COMMAND_WORD:
            return prepareVoidCommand(arguments, new ListStatisticsCommand());

        case UpdateAttendanceCommand.COMMAND_WORD:
            return prepareUpdateAttendance(arguments);

        case ReplaceAttendanceCommand.COMMAND_WORD:
            return prepareReplaceAttendance(arguments);

        case ViewAttendancePersonCommand.COMMAND_WORD:
            return prepareViewAttendance(arguments);

        case ViewAttendanceDateCommand.COMMAND_WORD:
            return prepareViewDateAttendance(arguments);

        case ListExamsCommand.COMMAND_WORD:
            return prepareVoidCommand(arguments, new ListExamsCommand());

        case ListFeesCommand.COMMAND_WORD:
            return prepareVoidCommand(arguments, new ListFeesCommand());

        case ListDueFeesCommand.COMMAND_WORD:
            return prepareVoidCommand(arguments, new ListDueFeesCommand());

        case DeleteExamCommand.COMMAND_WORD:
            return prepareSingleIndexCommand(arguments, new DeleteExamCommand(), ObjectTargeted.EXAM);

        case ClearExamsCommand.COMMAND_WORD:
            return prepareVoidCommand(arguments, new ClearExamsCommand());

        case EditExamCommand.COMMAND_WORD:
            return prepareEditExam(arguments);

        case RegisterExamCommand.COMMAND_WORD:
            return prepareRegisterExam(arguments);

        case DeregisterExamCommand.COMMAND_WORD:
            return prepareDeregisterExam(arguments);

        case ViewExamsCommand.COMMAND_WORD:
            return prepareSingleIndexCommand(arguments, new ViewExamsCommand(), ObjectTargeted.PERSON);

        case AddAssessmentCommand.COMMAND_WORD:
            return prepareAddAssessment(arguments);

        case DeleteAssessmentCommand.COMMAND_WORD:
            return prepareSingleIndexCommand(arguments, new DeleteAssessmentCommand(), ObjectTargeted.ASSESSMENT);

        case ListAssessmentCommand.COMMAND_WORD:
            return prepareVoidCommand(arguments, new ListAssessmentCommand());

        case DeleteStatisticsCommand.COMMAND_WORD:
            return prepareSingleIndexCommand(arguments, new DeleteStatisticsCommand(), ObjectTargeted.STATISTIC);

        case DeleteGradesCommand.COMMAND_WORD:
            return prepareDeleteGrades(arguments);

        case HelpCommand.COMMAND_WORD: // Fallthrough
            return prepareVoidCommand(arguments, new HelpCommand());

        default:
            // Do not call prepareVoidCommand as we should show the help message if commandWord is not recognised
            return new HelpCommand(Messages.MESSAGE_COMMAND_NOT_FOUND);
        }
    }

    /**
     * Parses arguments in the context of the Add person command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareAdd(String args) {
        final Matcher matcher = PERSON_DATA_ARGS_FORMAT.matcher(args.trim());
        // Validate arg string format
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
        try {
            return new AddCommand(
                    matcher.group("name"),

                    matcher.group("phone"),
                    isPrivatePrefixPresent(matcher.group("isPhonePrivate")),

                    matcher.group("email"),
                    isPrivatePrefixPresent(matcher.group("isEmailPrivate")),

                    matcher.group("address"),
                    isPrivatePrefixPresent(matcher.group("isAddressPrivate")),

                    getTagsFromArgs(matcher.group("tagArguments"))
            );
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }

    /**
     * Checks whether the private prefix of a contact detail in the add command's arguments string is present.
     */
    private static boolean isPrivatePrefixPresent(String matchedPrefix) {
        return "p".equals(matchedPrefix);
    }

    /**
     * Extracts the new person's tags from the add command's tag arguments string.
     * Merges duplicate tag strings.
     */
    private static Set<String> getTagsFromArgs(String tagArguments) {
        // no tags
        if (tagArguments.isEmpty()) {
            return Collections.emptySet();
        }
        // replace first delimiter prefix, then split
        final Collection<String> tagStrings = Arrays.asList(tagArguments.replaceFirst(" t/", "").split(" t/"));
        return new HashSet<>(tagStrings);
    }
    /**
     * Parses arguments in the context of the EditFees command
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareFees(String args) {
        final Matcher matcher = FEES_DATA_ARGS_FORMAT.matcher(args.trim());
        // Validate arg string format
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditFeesCommand.MESSAGE_USAGE));
        }
        try {
            final int targetIndex = parseArgsAsDisplayedIndex(matcher.group("index"));
            return new EditFeesCommand(
                    targetIndex,
                    matcher.group("fees"),
                    matcher.group("date")
            );
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        } catch (ParseException | NumberFormatException e) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditFeesCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Parses arguments for commands which are in the form of COMMAND_WORD INDEX.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareSingleIndexCommand(String args,
                                              IndexFormatCommand command,
                                              ObjectTargeted objectTargeted) {
        try {
            final int targetIndex = parseArgsAsDisplayedIndex(args);
            command.setTargetIndex(targetIndex, objectTargeted);
            return command;
        } catch (ParseException | NumberFormatException e) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    command.getCommandUsageMessage()));
        }
    }

    /**
     * Parses arguments in the context of the ViewFees command.
     */
    private Command prepareViewFees(String args) {
        try {
            final int targetIndex = parseArgsAsDisplayedIndex(args);
            return new ViewFeesCommand(targetIndex);
        } catch (ParseException | NumberFormatException e) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewFeesCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Parses the given arguments string as a single index number.
     *
     * @param args arguments string to parse as index number
     * @return the parsed index number
     * @throws ParseException if no region of the args string could be found for the index
     * @throws NumberFormatException the args string region is not a valid number
     */
    private int parseArgsAsDisplayedIndex(String args) throws ParseException, NumberFormatException {
        final Matcher matcher = PERSON_INDEX_ARGS_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            throw new ParseException("Could not find index number to parse");
        }
        return parseInt(matcher.group("targetIndex"));
    }

    /**
     * Parses arguments in the context of the Find person command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareFind(String args) {
        final Matcher matcher = KEYWORDS_ARGS_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    FindCommand.MESSAGE_USAGE));
        }

        // keywords delimited by whitespace
        final String[] keywords = matcher.group("keywords").split("\\s+");
        final Set<String> keywordSet = new HashSet<>(Arrays.asList(keywords));
        return new FindCommand(keywordSet);
    }

    /**
     * Parses arguments in the context of the SetPermAdmin command.
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareSetPermAdmin(String args) {
        final Matcher matcher = BOOLEAN_ARGS_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    SetPermanentAdminCommand.MESSAGE_USAGE));
        }

        try {
            final String booleanString = matcher.group("boolean").toLowerCase();
            final boolean isPerm = parseStringToBoolean(booleanString);
            return new SetPermanentAdminCommand(isPerm);
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    SetPermanentAdminCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Parses string into boolean, using "true" = true and "false" = false.
     * @throws IllegalValueException if string is neither "true" nor "false".
     */
    private boolean parseStringToBoolean (String string) throws IllegalValueException {
        if (!"true".equals(string) && !"false".equals(string)) {
            throw new IllegalValueException(MESSAGE_INVALID_COMMAND_FORMAT);
        }
        return "true".equals(string);
    }

    /**
     * Parses arguments in the context of the ChangePassword command.
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareVoidCommand(String args, Command command) {
        final boolean hasNoArguments = args.trim().isEmpty();
        if (hasNoArguments) {
            return command;
        } else {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    command.getCommandUsageMessage()));
        }
    }

    /**
     * Parses arguments in the context of the Keywords command.
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareKeywordsCommand(String args, KeywordsFormatCommand command) {
        final Matcher matcher = KEYWORDS_ARGS_FORMAT.matcher(args.trim());
        final String usageMessage = command.getCommandUsageMessage();
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    usageMessage));
        }
        // keywords delimited by whitespace
        final String[] keywords = matcher.group("keywords").split("\\s+");
        final int numRequiredArg = command.getNumRequiredArg();
        if (keywords.length == numRequiredArg) {
            command.setUp(keywords);
            return command;
        } else {
            return new IncorrectCommand(String.format(MESSAGE_WRONG_NUMBER_ARGUMENTS,
                    numRequiredArg, keywords.length, usageMessage));
        }
    }

    /**
     * Parses arguments in the context of the AddAccount command.
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareAddAccount(String args) {
        final Matcher matcher = KEYWORDS_ARGS_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddAccountCommand.MESSAGE_USAGE));
        }
        // keywords delimited by whitespace
        final String[] keywords = matcher.group("keywords").split("\\s+");
        try {
            final int targetIndex = parseArgsAsDisplayedIndex(keywords[0]);
            final int requiredArgs = 4;
            if (keywords.length != requiredArgs) {
                final String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddAccountCommand.MESSAGE_USAGE);
                throw new IllegalValueException(message);
            }
            return new AddAccountCommand(targetIndex, keywords[1], keywords[2], keywords[3]);
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        } catch (ParseException | NumberFormatException e) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddAccountCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Parses arguments in the context of the AddGrades command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareAddGrades(String args) {
        final Matcher matcher = KEYWORDS_ARGS_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddGradesCommand.MESSAGE_USAGE));
        }

        String[] arr = matcher.group("keywords").split("\\s+");
        final int requiredArgs = 3;
        if (arr.length != requiredArgs) {
            return new IncorrectCommand(String.format(MESSAGE_WRONG_NUMBER_ARGUMENTS , 3, arr.length,
                    AddGradesCommand.MESSAGE_USAGE));
        }
        try {
            final int targetPersonIndex = parseArgsAsDisplayedIndex(arr[0]);
            final int targetAssessmentIndex = parseArgsAsDisplayedIndex(arr[1]);
            final double targetGrades = Double.parseDouble(arr[2]);
            return new AddGradesCommand(targetPersonIndex, targetAssessmentIndex, targetGrades);
        } catch (ParseException | NumberFormatException e) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddGradesCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Parses arguments in the context of the AddExam command.
     */
    private Command prepareAddExam(String args) {
        final Matcher matcher = EXAM_DATA_ARGS_FORMAT.matcher(args.trim());
        // Validate arg string format
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddExamCommand.MESSAGE_USAGE));
        }
        try {
            return new AddExamCommand(
                    matcher.group("examName"),
                    matcher.group("subjectName"),
                    matcher.group("examDate"),
                    matcher.group("examStartTime"),
                    matcher.group("examEndTime"),
                    matcher.group("examDetails"),
                    isPrivatePrefixPresent(matcher.group("isPrivate"))
            );
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }

    /**
     * Parses arguments in the context of the UpdateAttendance command.
     */
    private Command prepareUpdateAttendance(String args) {

        final Matcher matcher = ATTENDANCE_ARGS_FORMAT.matcher(args.trim());
        // Validate arg string format
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    UpdateAttendanceCommand.MESSAGE_USAGE));
        }
        try {
            final int targetIndex = parseInt(matcher.group("targetIndex"));
            final Integer isPresent = parseInt(matcher.group("isPresent"));
            final boolean isPresentBool = isPresent.equals(1);
            if (!"0".equals(matcher.group("date"))) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                dateFormat.parse(matcher.group("date").trim());
            }

            return new UpdateAttendanceCommand(
                    targetIndex,
                    matcher.group("date"),
                    isPresentBool);
        } catch (NumberFormatException nfe) { //do the most specific catch on top
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    UpdateAttendanceCommand.MESSAGE_USAGE));
        } catch (java.text.ParseException pe) {
            return new IncorrectCommand(MESSAGE_DATE_CONSTRAINTS);
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(MESSAGE_DATE_CONSTRAINTS
                    + UpdateAttendanceCommand.MESSAGE_USAGE);
        }
    }

    /**
     * Parses arguments in the context of the ReplaceAttendance command.
     */
    private Command prepareReplaceAttendance(String args) {

        final Matcher matcher = ATTENDANCE_ARGS_FORMAT.matcher(args.trim());
        // Validate arg string format
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ReplaceAttendanceCommand.MESSAGE_USAGE));
        }
        try {
            final int targetIndex = parseInt(matcher.group("targetIndex"));
            final Integer isPresent = parseInt(matcher.group("isPresent"));
            final boolean isPresentBool = isPresent.equals(1);
            if (!"0".equals(matcher.group("date"))) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                dateFormat.parse(matcher.group("date").trim());
            }

            return new ReplaceAttendanceCommand(
                    targetIndex,
                    matcher.group("date"),
                    isPresentBool);
        } catch (NumberFormatException nfe) { //do the most specific catch on top
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ReplaceAttendanceCommand.MESSAGE_USAGE));
        } catch (java.text.ParseException pe) {
            return new IncorrectCommand(MESSAGE_DATE_CONSTRAINTS);
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(MESSAGE_DATE_CONSTRAINTS + ReplaceAttendanceCommand.MESSAGE_USAGE);
        }

    }

    /**
     * Parses arguments in the context of the ViewAttendance by person command.
     */
    private Command prepareViewAttendance(String args) {
        final Matcher matcher = PERSON_INDEX_ARGS_FORMAT.matcher(args.trim());
        // Validate arg string format
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ViewAttendancePersonCommand.MESSAGE_USAGE));
        }

        try {
            final int targetIndex = parseInt(matcher.group("targetIndex"));
            return new ViewAttendancePersonCommand(targetIndex);
        } catch (NumberFormatException nfe) { //do the most specific catch on top
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ViewAttendancePersonCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Parses arguments in the context of the ViewAttendance by date command.
     */
    private Command prepareViewDateAttendance(String args) {
        final Matcher matcher = ATTENDANCE_VIEW_DATE_FORMAT.matcher(args.trim());
        // Validate arg string format
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ViewAttendanceDateCommand.MESSAGE_USAGE));
        }
        try {
            return new ViewAttendanceDateCommand(matcher.group("date"));
        } catch (NumberFormatException nfe) { //do the most specific catch on top
            return new IncorrectCommand(nfe.getMessage());
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(MESSAGE_DATE_CONSTRAINTS
                    + ViewAttendanceDateCommand.MESSAGE_USAGE);
        }
    }

    /**
     * Parses arguments in the context of the DeleteGrades command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareDeleteGrades(String args) {
        final Matcher matcher = KEYWORDS_ARGS_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    DeleteGradesCommand.MESSAGE_USAGE));
        }

        String[] arr = matcher.group("keywords").split("\\s+");
        final int requiredArgs = 2;
        if (arr.length != requiredArgs) {
            return new IncorrectCommand(String.format(MESSAGE_WRONG_NUMBER_ARGUMENTS , 2, arr.length,
                    DeleteGradesCommand.MESSAGE_USAGE));
        }
        try {
            final int targetIndex = parseArgsAsDisplayedIndex(arr[0]);
            final int targetAssessmentIndex = parseArgsAsDisplayedIndex(arr[1]);
            return new DeleteGradesCommand(targetIndex, targetAssessmentIndex);
        } catch (ParseException | NumberFormatException e) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    DeleteGradesCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Parses arguments in the context of the EditExam command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareEditExam(String args) {
        final Matcher matcher = EDIT_EXAM_INDEX_ARGS_FORMAT.matcher(args.trim());
        // Validate arg string format
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EditExamCommand.MESSAGE_USAGE));
        }
        Map<ExamField, String> changedDetails = storeNewDetails(matcher);
        try {
            final int targetIndex = parseInt(matcher.group("targetIndex").trim());
            return new EditExamCommand(targetIndex, changedDetails);
        } catch (NumberFormatException nfe) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditExamCommand.MESSAGE_USAGE));
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ive.getMessage()));
        }
    }

    /**
     * Parses arguments in the context of the RegisterExam command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareRegisterExam(String args) {
        final Matcher matcher = KEYWORDS_ARGS_FORMAT.matcher(args.trim());
        // Validate arg string format
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    RegisterExamCommand.MESSAGE_USAGE));
        }

        String[] arr = matcher.group("keywords").split("\\s+");
        if (arr.length != RegisterExamCommand.REQUIRED_ARGUMENTS) {
            return new IncorrectCommand(String.format(MESSAGE_WRONG_NUMBER_ARGUMENTS,
                    RegisterExamCommand.REQUIRED_ARGUMENTS,
                    arr.length, RegisterExamCommand.MESSAGE_USAGE));
        }
        try {
            final int targetIndex = parseArgsAsDisplayedIndex(arr[0]);
            final int targetExamIndex = parseArgsAsDisplayedIndex(arr[1]);
            return new RegisterExamCommand(targetIndex, targetExamIndex);
        } catch (ParseException | NumberFormatException e) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    RegisterExamCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Parses arguments in the context of the DeregisterExam command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareDeregisterExam(String args) {
        final Matcher matcher = KEYWORDS_ARGS_FORMAT.matcher(args.trim());
        // Validate arg string format
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    DeregisterExamCommand.MESSAGE_USAGE));
        }

        String[] arr = matcher.group("keywords").split("\\s+");
        if (arr.length != DeregisterExamCommand.REQUIRED_ARGUMENTS) {
            return new IncorrectCommand(String.format(MESSAGE_WRONG_NUMBER_ARGUMENTS,
                    DeregisterExamCommand.REQUIRED_ARGUMENTS,
                    arr.length, DeregisterExamCommand.MESSAGE_USAGE));
        }
        try {
            final int targetIndex = parseArgsAsDisplayedIndex(arr[0]);
            final int targetExamIndex = parseArgsAsDisplayedIndex(arr[1]);
            return new DeregisterExamCommand(targetIndex, targetExamIndex);
        } catch (ParseException | NumberFormatException e) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    DeregisterExamCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Parses arguments in the context of the AddAssessment command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareAddAssessment (String args) {
        final Matcher matcher = ASSESSMENT_DATA_ARGS_FORMAT.matcher(args.trim());
        // Validate arg string format
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddAssessmentCommand.MESSAGE_USAGE));
        }
        try {
            return new AddAssessmentCommand(
                    matcher.group("examName")
            );
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }

    /**
     * Stores the new values of exam to be edited to.
     */
    private static Map<ExamField, String> storeNewDetails(Matcher matcher) {
        Map<ExamField, String> result = new HashMap<>();
        Optional<String> examName = Optional.ofNullable(matcher.group("examName"));
        if (examName.isPresent()) {
            result.put(ExamField.examName, examName.get());
        }
        Optional<String> subjectName = Optional.ofNullable(matcher.group("subjectName"));
        if (subjectName.isPresent()) {
            result.put(ExamField.subjectName, subjectName.get());
        }
        Optional<String> examDate = Optional.ofNullable(matcher.group("examDate"));
        if (examDate.isPresent()) {
            result.put(ExamField.examDate, examDate.get());
        }
        Optional<String> examStartTime = Optional.ofNullable(matcher.group("examStartTime"));
        if (examStartTime.isPresent()) {
            result.put(ExamField.examStartTime, examStartTime.get());
        }
        Optional<String> examEndTime = Optional.ofNullable(matcher.group("examEndTime"));
        if (examEndTime.isPresent()) {
            result.put(ExamField.examEndTime, examEndTime.get());
        }
        Optional<String> examDetails = Optional.ofNullable(matcher.group("examDetails"));
        if (examDetails.isPresent()) {
            result.put(ExamField.examDetails, examDetails.get());
        }
        Optional<String> isPrivate = Optional.ofNullable(matcher.group("isPrivate"));
        if (isPrivate.isPresent()) {
            result.put(ExamField.isPrivate, isPrivate.get());
        }
        return result;
    }
}
