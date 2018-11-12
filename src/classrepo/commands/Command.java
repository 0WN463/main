package classrepo.commands;

import java.util.List;
import java.util.Objects;

import classrepo.commands.commandresult.CommandResult;
import classrepo.common.Messages;
import classrepo.data.AddressBook;
import classrepo.data.ExamBook;
import classrepo.data.StatisticsBook;
import classrepo.data.person.Assessment;
import classrepo.data.person.AssignmentStatistics;
import classrepo.data.person.ReadOnlyExam;
import classrepo.data.person.ReadOnlyPerson;
import classrepo.privilege.Privilege;

/**
 * Represents an executable command.
 */
public abstract class Command {

    private static final Category DEFAULT_CATEGORY = Category.GENERAL;
    private static final boolean DEFAULT_IS_MUTATING_SETTING = false;

    /**
     * Enum used to indicate which category the command belongs to
     * */
    public enum Category {
        PERSON,
        PRIVILEGE,
        GENERAL,
        ACCOUNT,
        ASSESSMENT,
        EXAM,
        FEES,
        ATTENDANCE
    }

    protected Privilege privilege;
    protected AddressBook addressBook;
    protected ExamBook examBook;
    protected StatisticsBook statisticsBook;
    protected List<? extends ReadOnlyPerson> relevantPersons;
    protected List<? extends Assessment> relevantAssessments;
    protected List<? extends ReadOnlyExam> relevantExams;
    protected List<? extends AssignmentStatistics> relevantStatistics;

    /**
     * Signals that the target exam index is out of bounds of the last viewed exams listing
     */
    public static class ExamIndexOutOfBoundsException extends IndexOutOfBoundsException {
        public ExamIndexOutOfBoundsException(String message) {
            super(message);
        }
    }

    /**
     * Signals that the target assessment index is out of bounds of the last viewed assessment listing
     */
    public static class AssessmentIndexOutOfBoundsException extends IndexOutOfBoundsException {
        public AssessmentIndexOutOfBoundsException(String message) {
            super(message);
        }
    }

    /**
     * Constructs a feedback message to summarise an operation that displayed a listing of persons.
     *
     * @param personsDisplayed used to generate summary
     * @return summary message for persons displayed
     */
    public static String getMessageForPersonListShownSummary(List<? extends ReadOnlyPerson> personsDisplayed) {
        return String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, personsDisplayed.size());
    }

    /**
     * Constructs a feedback message to summarise an operation that displayed a listing of fees.
     *
     * @param feesList used to generate summary
     * @return summary message for persons displayed
     */
    public static String getMessageForFeesListShownSummary(List<? extends ReadOnlyPerson> feesList) {
        return String.format(Messages.MESSAGE_FEES_LISTED_OVERVIEW, feesList.size());
    }

    /**
     * Constructs a feedback message to summarise an operation that displayed a listing of assessments.
     *
     * @param assessmentsDisplayed used to generate summary
     * @return summary message for persons displayed
     */
    public static String getMessageForAssessmentListShownSummary(List<? extends Assessment> assessmentsDisplayed) {
        return String.format(Messages.MESSAGE_ASSESSMENTS_LISTED_OVERVIEW, assessmentsDisplayed.size());
    }

    /**
     * Constructs a feedback message to summarise an operation that displayed a listing of exams.
     *
     * @param examsDisplayed used to generate summary
     * @return summary message for exams displayed
     */
    public static String getMessageForExamListShownSummary(List<? extends ReadOnlyExam> examsDisplayed) {
        return String.format(Messages.MESSAGE_EXAMS_LISTED_OVERVIEW, examsDisplayed.size());
    }

    /**
     * Constructs a feedback message to summarise an operation that displayed a listing of statistics.
     *
     * @param statisticsDisplayed used to generate summary
     * @return summary message for persons displayed
     */
    public static String getMessageForStatisticsListShownSummary(List<? extends AssignmentStatistics>
                                                                         statisticsDisplayed) {
        return String.format(Messages.MESSAGE_STATISTICS_LISTED_OVERVIEW, statisticsDisplayed.size());
    }

    /**
     * Executes the command and returns the result.
     */
    public abstract CommandResult execute();

    /**
     * Supplies the data the command will operate on.
     */
    public void setData(AddressBook addressBook, StatisticsBook statisticsBook, List<? extends ReadOnlyPerson>
            relevantPersons, Privilege privilege) {
        this.addressBook = addressBook;
        this.statisticsBook = statisticsBook;
        this.relevantPersons = relevantPersons;
        this.privilege = privilege;
    }

    public void setData(AddressBook addressBook, List<? extends ReadOnlyPerson> relevantPersons,
                        List<? extends ReadOnlyExam> relevantExams, List<? extends Assessment> relevantAssessments,
                        Privilege privilege, ExamBook exambook, StatisticsBook statisticsBook,
                        List<? extends AssignmentStatistics> relevantStatistics) {
        setData(addressBook, statisticsBook, relevantPersons, privilege);
        this.examBook = exambook;
        this.relevantExams = relevantExams;
        this.relevantAssessments = relevantAssessments;
        this.relevantStatistics = relevantStatistics;
    }


    public Category getCategory() {
        return DEFAULT_CATEGORY;
    }

    /**
     * Checks if the command can potentially change the data to be stored
     */
    public boolean isMutating() {
        return DEFAULT_IS_MUTATING_SETTING;
    }

    /**
     * Returns the usage message to be used to construct HelpCommand's message
     * This is needed as each Command's usage message needs to be static to be accessed by Parser.
     * This allows the Help Command to retrieve usage messages by looping through each Command.
     */
    public abstract String getCommandUsageMessage();

    /**
     * Checks if the command can potentially change the exam data to be stored
     */
    public boolean isExamMutating() {
        return DEFAULT_IS_MUTATING_SETTING;
    }

    // The below 2 functions is used by Users to determined if a Command already exists in their newAllowedCommands
    @Override
    public int hashCode() {
        // Hash based on class so that we can check if the same command is added to different users
        return Objects.hash(this.getClass());
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (this.getClass().equals(other.getClass())); // Checks if the classes are the same
    }
}
