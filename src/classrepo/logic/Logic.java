package classrepo.logic;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import classrepo.commands.Command;
import classrepo.commands.IncorrectCommand;
import classrepo.commands.commandresult.CommandResult;
import classrepo.common.Messages;
import classrepo.data.AddressBook;
import classrepo.data.ExamBook;
import classrepo.data.StatisticsBook;
import classrepo.data.person.Assessment;
import classrepo.data.person.AssignmentStatistics;
import classrepo.data.person.ReadOnlyExam;
import classrepo.data.person.ReadOnlyPerson;
import classrepo.parser.Parser;
import classrepo.privilege.Privilege;
import classrepo.storage.Storage;
import classrepo.storage.StorageFile;
import classrepo.storage.StorageFile.InvalidInitialisationException;
import classrepo.storage.StorageFile.InvalidStorageFilePathException;

/**
 * Represents the main Logic of the AddressBook.
 */
public class Logic {
    private Storage storage;
    private AddressBook addressBook;
    private Privilege privilege;
    private ExamBook examBook;
    private StatisticsBook statisticsBook;

    /** The list of persons shown to the user most recently.  */
    private List<? extends ReadOnlyPerson> lastShownList = Collections.emptyList();

    /** The list of assessments shown to the user most recently.  */
    private List<? extends Assessment> lastShownAssessmentList = Collections.emptyList();

    /** The list of statistics shown to the user most recently.  */
    private List<? extends AssignmentStatistics> lastShownStatisticsList = Collections.emptyList();

    /** The list of exams shown to the user most recently.  */
    private List<? extends ReadOnlyExam> lastShownExamList = Collections.emptyList();

    /**
     * Signals that an operation requiring password authentication failed.
     */
    public static class WrongPasswordEnteredException extends Exception {}

    public Logic() throws Exception {
        setStorage(initializeStorage());
        setAddressBook(storage.load());
        setExamBook(storage.loadExam());
        setStatisticsBook(storage.loadStatistics());
        storage.syncAddressBookExamBook(addressBook, examBook);
        initPrivilege();
    }

    Logic(Storage storageFile, AddressBook addressBook, ExamBook examBook, StatisticsBook statisticsBook, Privilege
            privilege) {
        this(storageFile, addressBook, examBook, statisticsBook);
        setPrivilege(privilege);
    }

    Logic(Storage storageFile, AddressBook addressBook, ExamBook examBook, StatisticsBook statisticsBook) {
        setStorage(storageFile);
        setAddressBook(addressBook);
        setExamBook(examBook);
        setStatisticsBook(statisticsBook);
    }

    /** Sets privilege as Admin if addressBook isPermAdmin, else remains at Basic*/
    public void initPrivilege() {
        if (!Optional.ofNullable(privilege).isPresent()) {
            privilege = new Privilege();
        }

        if (addressBook.isPermAdmin()) {
            privilege.raiseToAdmin();
        }
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public void setAddressBook(AddressBook addressBook) {
        this.addressBook = addressBook;
    }

    public void setPrivilege(Privilege privilege) {
        this.privilege = privilege;
    }

    public void setExamBook(ExamBook examBook) {
        this.examBook = examBook;
    }

    public void setStatisticsBook(StatisticsBook statisticsBook) {
        this.statisticsBook = statisticsBook;
    }

    /**
     * Creates the StorageFile object based on the user specified path (if any) or the default storage path.
     * @throws InvalidStorageFilePathException if the target file path is incorrect.
     * @throws InvalidInitialisationException if the JAXB set up has error
     */
    private StorageFile initializeStorage()
            throws InvalidStorageFilePathException,
            InvalidInitialisationException {

        return new StorageFile();
    }

    public String getStorageFilePath() {
        return storage.getPath();
    }

    public String getStorageFilePathExam() {
        return storage.getPathExam();
    }

    public String getStorageFilePathStatistics() {
        return storage.getPathStatistics();
    }

    /**
     * Unmodifiable view of the current last shown person list.
     */
    public List<ReadOnlyPerson> getLastShownList() {
        return Collections.unmodifiableList(lastShownList);
    }

    protected void setLastShownList(List<? extends ReadOnlyPerson> newList) {
        lastShownList = newList;
    }

    /**
     * Unmodifiable view of the current last shown assessment list.
     */
    public List<Assessment> getLastShownAssessmentList() {
        return Collections.unmodifiableList(lastShownAssessmentList);
    }

    protected void setLastShownAssessmentList (List<? extends Assessment> newList) {
        lastShownAssessmentList = newList;
    }

    /**
     * Unmodifiable view of the current last shown exam list.
     */
    public List<ReadOnlyExam> getLastShownExamList() {
        return Collections.unmodifiableList(lastShownExamList);
    }

    protected void setLastShownExamList(List<? extends ReadOnlyExam> newList) {
        lastShownExamList = newList;
    }

    /**
     * Parses the user command, executes it, and returns the result.
     * @throws Exception if there was any problem during command execution.
     */
    public CommandResult execute(String userCommandText) throws Exception {
        Command command = new Parser().parseCommand(userCommandText);
        CommandResult result = execute(command);
        recordResult(result);
        return result;
    }

    /**
     * Executes the command, updates storage if the command can potentially mutate data,
     * and returns the result.
     *
     * @param command user command
     * @return result of the command
     * @throws Exception if there was any problem during command execution.
     */
    private CommandResult execute(Command command) throws Exception {
        final CommandResult result;

        command.setData(addressBook, lastShownList, lastShownExamList, lastShownAssessmentList, privilege, examBook,
                statisticsBook, lastShownStatisticsList);

        // Checking instanceof IncorrectCommand to prevent overwriting the message of an incorrect command
        if (privilege.isAllowedCommand(command) || (command instanceof IncorrectCommand)) {
            result = command.execute();
        } else {
            result = new IncorrectCommand (String.format(Messages.MESSAGE_INSUFFICIENT_PRIVILEGE,
                    privilege.getRequiredPrivilegeAsString(command),
                    privilege.getLevelAsString())).execute();
        }

        if (command.isMutating()) {
            storage.save(addressBook);
            storage.saveStatistics(statisticsBook);
        }
        if (command.isExamMutating()) {
            storage.saveExam(examBook);
        }
        return result;
    }

    /** Updates the {@link #lastShownList} if the result contains a list of Persons.
     *  Updates the {@link #lastShownExamList} if the result contains a list of Exams.
     *  Updates the {@link #lastShownAssessmentList} if the result contains a list of Assessments.
     *  Updates the {@link #lastShownStatisticsList} if the result contains a list of Statistics.
     */
    private void recordResult(CommandResult result) {
        final Optional<List<? extends ReadOnlyPerson>> optPersonList = result.getRelevantPersons();
        optPersonList.ifPresent(a -> lastShownList = a);

        final Optional<List<? extends ReadOnlyExam>> optExamList = result.getRelevantExams();
        optExamList.ifPresent(a -> lastShownExamList = a);

        final Optional<List<? extends Assessment>> optAssessmentList = result.getRelevantAssessments();
        optAssessmentList.ifPresent(a -> lastShownAssessmentList = a);

        final Optional<List<? extends AssignmentStatistics>> optStatisticsList = result.getRelevantStatistics();
        optStatisticsList.ifPresent(a -> lastShownStatisticsList = a);
    }
}
