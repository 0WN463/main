package classrepo.commands.commandresult;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import classrepo.data.person.Assessment;
import classrepo.data.person.AssignmentStatistics;
import classrepo.data.person.Exam;
import classrepo.data.person.ReadOnlyExam;
import classrepo.data.person.ReadOnlyPerson;
import classrepo.formatter.PersonListFormat;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {
    private static final String BLANK_MESSAGE = "";

    private static final PersonListFormat DEFAULT_LIST_FORMAT = PersonListFormat.NAMES_ONLY;
    private static final ListType DEFAULT_LIST_TYPE = ListType.PERSONS;

    /** The list of exams that was produced by the command */
    private List<? extends ReadOnlyExam> relevantExams;

    /** The list of assessments that was produced by the command */
    private List<? extends Assessment> relevantAssessments;

    /** The list of statistics that was produced by the command */
    private List<? extends AssignmentStatistics> relevantStatistics;

    /** The list of persons that was produced by the command */
    private List<? extends ReadOnlyPerson> relevantPersons;

    /** The output message to be shown to the user. Contains a description of the execution result */
    private String outputConsoleMessage;

    /** The status message to be shown to the user. Contains a description of the execution result */
    private String statusConsoleMessage;

    private PersonListFormat personListFormat;

    public CommandResult(String statusConsoleMessage) {
        this(statusConsoleMessage, MessageType.STATUS);
    }

    public CommandResult(String message, MessageType messageType) {
        if (messageType.equals(MessageType.STATUS)) {
            statusConsoleMessage = message;
        } else if (messageType.equals(MessageType.OUTPUT)) {
            outputConsoleMessage = message;
        }
    }

    public CommandResult(String statusConsoleMessage,
                         String outputConsoleMessage) {
        this.statusConsoleMessage = statusConsoleMessage;
        this.outputConsoleMessage = outputConsoleMessage;
    }

    public CommandResult(String statusConsoleMessage, List<?> relevantList) {
        // By default, assume list is list of ReadOnlyPerson, and shows all details.
        this(statusConsoleMessage, relevantList, DEFAULT_LIST_FORMAT, DEFAULT_LIST_TYPE);
    }

    public CommandResult(String statusConsoleMessage, List<?> relevantList, ListType listType) {
        this(statusConsoleMessage, relevantList, DEFAULT_LIST_FORMAT, listType);
    }

    public CommandResult(String statusConsoleMessage, List<?> relevantList, PersonListFormat personListFormat) {
        this(statusConsoleMessage, relevantList, personListFormat, DEFAULT_LIST_TYPE);
    }

    public CommandResult(String statusConsoleMessage,
                         List<?> relevantList,
                         PersonListFormat personListFormat,
                         ListType listType) {
        this.statusConsoleMessage = statusConsoleMessage;
        this.personListFormat = personListFormat;

        switch (listType) {
        case PERSONS: this.relevantPersons = castListToReadOnlyPerson(relevantList);
            break;

        case EXAMS: this.relevantExams = castListToExams(relevantList);
            break;

        case ASSESSMENT: this.relevantAssessments = castListToAssessments(relevantList);
            break;

        case STATISTICS: this.relevantStatistics = castListToStatistics(relevantList);
            break;

        default:
            // all enums should be accounted for, asserts false if this statement is reached
            assert false;
            break;
        }
    }

    /**
     * Returns list of persons relevant to the command command result, if any.
     */
    public Optional<List<? extends ReadOnlyPerson>> getRelevantPersons() {
        return Optional.ofNullable(relevantPersons);
    }

    /**
     * Returns list of exams relevant to the command command result, if any.
     */
    public Optional<List<? extends ReadOnlyExam>> getRelevantExams() {
        return Optional.ofNullable(relevantExams);
    }

    /**
     * Returns list of statistics relevant to the command command result, if any.
     */
    public Optional<List<? extends AssignmentStatistics>> getRelevantStatistics() {
        return Optional.ofNullable(relevantStatistics);
    }

    /**
     * Returns list of assessments relevant to the command command result, if any.
     */
    public Optional<List<? extends Assessment>> getRelevantAssessments() {
        return Optional.ofNullable(relevantAssessments);
    }

    public boolean hasStatusMessage() {
        return Optional.ofNullable(statusConsoleMessage).isPresent();
    }

    /** Returns a copy of the list as List ReadOnlyPerson */
    private List<? extends ReadOnlyPerson> castListToReadOnlyPerson(List<?> list) {
        return list.stream()
                .filter(element->element instanceof ReadOnlyPerson)
                .map(element->(ReadOnlyPerson) element)
                .collect(Collectors.toList());
    }

    /** Returns a copy of the list as List Exam */
    private List<? extends Exam> castListToExams(List<?> list) {
        return list.stream()
                .filter(element->element instanceof Exam)
                .map(element->(Exam) element)
                .collect(Collectors.toList());
    }

    /** Returns a copy of the list as List Assessment */
    private List<? extends Assessment> castListToAssessments(List<?> list) {
        return list.stream()
                .filter(element->element instanceof Assessment)
                .map(element->(Assessment) element)
                .collect(Collectors.toList());
    }

    /** Returns a copy of the list as List AssignmentStatistics */
    private List<? extends AssignmentStatistics> castListToStatistics(List<?> list) {
        return list.stream()
                .filter(element->element instanceof AssignmentStatistics)
                .map(element->(AssignmentStatistics) element)
                .collect(Collectors.toList());
    }

    /** Checks if there is any list attached to this Object */
    private boolean hasRelevantList() {
        return getRelevantPersons().isPresent()
                || getRelevantExams().isPresent()
                || getRelevantAssessments().isPresent()
                || getRelevantStatistics().isPresent();
    }

    /** Returns if this command results prints to output console*/
    public boolean hasOutputMessage() {
        return Optional.ofNullable(outputConsoleMessage).isPresent() || hasRelevantList();
    }

    /** Obtains the output message to be shown to the user. */
    public String getOutputConsoleMessage() {
        return Optional.ofNullable(outputConsoleMessage).orElse(BLANK_MESSAGE);
    }

    /** Obtains the status message to be shown to the user. */
    public String getStatusConsoleMessage() {
        return Optional.ofNullable(statusConsoleMessage).orElse(BLANK_MESSAGE);
    }

    public PersonListFormat getPersonListFormat() {
        return personListFormat;
    }
}
