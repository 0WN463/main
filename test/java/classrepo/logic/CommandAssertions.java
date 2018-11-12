package classrepo.logic;

import static classrepo.common.Messages.MESSAGE_INVALID_EXAM_DISPLAYED_INDEX;
import static classrepo.common.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import java.util.Collections;
import java.util.List;

import classrepo.TestDataHelper;
import classrepo.commands.commandresult.CommandResult;
import classrepo.commands.commandresult.MessageType;
import classrepo.data.AddressBook;
import classrepo.data.ExamBook;
import classrepo.data.StatisticsBook;
import classrepo.data.person.Assessment;
import classrepo.data.person.Exam;
import classrepo.data.person.Person;
import classrepo.data.person.ReadOnlyExam;
import classrepo.data.person.ReadOnlyPerson;
import classrepo.stubs.StorageStub;

/**
 * This class holds the commonly used assertions of commands.
 * */
public class CommandAssertions {
    private static StorageStub storageStub;
    private static AddressBook addressBook;
    private static Logic logic;
    private static ExamBook examBook;
    private static StatisticsBook statisticsBook;

    public static void setData(StorageStub saveFile, AddressBook addressBook, Logic logic) {
        CommandAssertions.storageStub = saveFile;
        CommandAssertions.addressBook = addressBook;
        CommandAssertions.logic = logic;
    }

    public static void setData(StorageStub saveFile, AddressBook addressBook, Logic logic, ExamBook examBook,
                               StatisticsBook statisticsBook) {
        setData(saveFile, addressBook, logic);
        CommandAssertions.examBook = examBook;
        CommandAssertions.statisticsBook = statisticsBook;
    }

    /**
     * Executes the command and confirms that the status message is correct.
     * Assumptions: No output console message
     *              Expected AddressBook is empty
     *              No relevant persons expected
     *              Last shown list is empty
     *              Is not mutating
     * @see #assertCommandBehavior(String, String, String, AddressBook, boolean, List, boolean)
     */
    public static void assertCommandBehavior(String inputCommand, String expectedStatusMessage) throws Exception {
        assertCommandBehavior(inputCommand, expectedStatusMessage, "", AddressBook.empty(),
                false, Collections.emptyList(), false);
    }

    /**
     * Executes the command and confirms that the status and output messages are correct.
     * Assumptions: Expected AddressBook is empty
     *              No relevant persons expected
     *              Last shown list is empty
     *              Is not mutating
     * @see #assertCommandBehavior(String, String, String, AddressBook, boolean, List, boolean)
     */
    public static void assertCommandBehavior(String inputCommand,
                                             String expectedStatusMessage,
                                             String expectedOutputMessage) throws Exception {
        assertCommandBehavior(inputCommand, expectedStatusMessage, expectedOutputMessage,
                AddressBook.empty(), false, Collections.emptyList(), false);
    }

    /**
     * Executes the command and confirms that the result message are correct, and is in the correct console.
     * Assumptions: Result message is printed on only one of the consoles
     *              Expected AddressBook is empty
     *              No relevant persons expected
     *              Last shown list is empty
     *              Is not mutating
     * @param messageType specify which console the given message is supposed to be written to
     * @see #assertCommandBehavior(String, String, String, AddressBook, boolean, List, boolean)
     */
    public static void assertCommandBehavior(String inputCommand,
                                             String expectedMessage,
                                             MessageType messageType) throws Exception {
        if (messageType.equals(MessageType.STATUS)) {
            assertCommandBehavior(inputCommand, expectedMessage, "", AddressBook.empty(),
                    false, Collections.emptyList(), false);
        } else if (messageType.equals(MessageType.OUTPUT)) {
            assertCommandBehavior(inputCommand, "", expectedMessage, AddressBook.empty(),
                    false, Collections.emptyList(), false);
        }
    }

    /**
     * Executes the command and confirms that the status message is correct and AddressBook is in the expected state
     * Assumptions: No output console message
     * @see #assertCommandBehavior(String, String, String, AddressBook, boolean, List, boolean)
     */
    public static void assertCommandBehavior(String inputCommand,
                                             String expectedStatusMessage,
                                             AddressBook expectedAddressBook,
                                             boolean isRelevantPersonsExpected,
                                             List<? extends ReadOnlyPerson> lastShownList,
                                             boolean isMutating) throws Exception {
        assertCommandBehavior(inputCommand, expectedStatusMessage, "", expectedAddressBook,
                isRelevantPersonsExpected, lastShownList, isMutating);
    }

    /**
     * Executes the command and confirms that the result messages are correct and
     * also confirms that the following three parts of the Logic object's state are as expected:<br>
     *      - the internal address book data are same as those in the {@code expectedAddressBook} <br>
     *      - the internal 'last shown list' matches the {@code lastShownList} <br>
     *
     *      if command is expected to save after execution
     *      - the StorageFile.save() is called using the execution {@code expectedAddressBook} <br>
     */
    public static void assertCommandBehavior(String inputCommand,
                                             String expectedStatusMessage,
                                             String expectedOutputMessage,
                                             AddressBook expectedAddressBook,
                                             boolean isRelevantPersonsExpected,
                                             List<? extends ReadOnlyPerson> lastShownList,
                                             boolean isMutating) throws Exception {
        //Execute the command
        CommandResult r = logic.execute(inputCommand);

        //Confirm the result messages contains the right data
        assertEquals(expectedOutputMessage, r.getOutputConsoleMessage());
        assertEquals(expectedStatusMessage, r.getStatusConsoleMessage());

        assertEquals(r.getRelevantPersons().isPresent(), isRelevantPersonsExpected);
        if (isRelevantPersonsExpected) {
            assertEquals(lastShownList, r.getRelevantPersons().get());
        }

        //Confirm the state of data is as expected
        assertEquals(expectedAddressBook, addressBook);
        assertEquals(lastShownList, logic.getLastShownList());

        //Confirm data is saved if is expected to
        if (isMutating) {
            assertTrue("Command did not have expected saving behaviour", storageStub.getHasSaved());
        }
    }

    /**
     * Executes the command and confirms that the result message is correct and
     * also confirms that the following three parts of the Logic object's state are as expected:<br>
     *      - the internal address book data are same as those in the {@code expectedAddressBook} <br>
     *      - the internal 'last shown list' matches the {@code lastShownList} <br>
     *
     *      if the command is mutating
     *      - the storage file content matches data in {@code expectedAddressBook} <br>
     */
    public static void assertCommandBehavior(String inputCommand,
                                             String expectedStatusMessage,
                                             String expectedOutputMessage,
                                             AddressBook expectedAddressBook,
                                             boolean isRelevantPersonsExpected,
                                             List<? extends ReadOnlyPerson> lastShownList,
                                             boolean isRelevantAssessmentsExpected,
                                             List<? extends Assessment> lastShownAssessmentsList,
                                             boolean isMutating) throws Exception {
        //Execute the command
        CommandResult r = logic.execute(inputCommand);

        //Confirm the result contains the right data
        assertEquals(expectedOutputMessage, r.getOutputConsoleMessage());
        assertEquals(expectedStatusMessage, r.getStatusConsoleMessage());
        assertEquals(r.getRelevantPersons().isPresent(), isRelevantPersonsExpected);

        if (isRelevantPersonsExpected) {
            assertEquals(lastShownList, r.getRelevantPersons().get());
        }

        if (isRelevantAssessmentsExpected) {
            assertEquals(lastShownAssessmentsList, logic.getLastShownAssessmentList());
        }
        //Confirm the state of data is as expected
        assertEquals(expectedAddressBook, addressBook);
        assertEquals(lastShownList, logic.getLastShownList());

        //Confirm data is saved if is expected to
        if (isMutating) {
            assertTrue("Command did not have expected saving behaviour", storageStub.getHasSaved());
        }
    }

    /**
     * Executes the command and confirms that the result messages are correct.
     * Assumptions: Expected ExamBook is empty
     *              No relevant persons expected
     *              Last shown list is empty
     *              Is not mutating
     * @see #assertCommandBehavior(String, String, ExamBook, boolean, List, boolean)
     */
    public static void assertCommandBehavior(String inputCommand, String expectedStatusMessage,
                                             ExamBook exambook, List<ReadOnlyExam> lastShownList) throws Exception {
        assertCommandBehavior(inputCommand, expectedStatusMessage, exambook,
                false, lastShownList, false);
    }

    /**
     * Executes the command and confirms that the status message is correct and
     * Assumptions: No output console message
     * @see #assertCommandBehavior(String, String, String, ExamBook, boolean, List, boolean)
     */
    public static void assertCommandBehavior(String inputCommand,
                                             String expectedStatusMessage,
                                             ExamBook expectedExamBook,
                                             boolean isRelevantExamsExpected,
                                             List<? extends ReadOnlyExam> lastShownList,
                                             boolean isExamMutating) throws Exception {
        assertCommandBehavior(inputCommand, expectedStatusMessage, "", expectedExamBook,
                isRelevantExamsExpected, lastShownList, isExamMutating);
    }

    /**
     * Executes the command and confirms that the result messages are correct and
     * Assumes the storage to be tested and the expectedOutputMessage to be empty
     * @see #assertCommandBehavior(String, String, String, AddressBook, ExamBook,
     *                                boolean, boolean, List, List, boolean, boolean)
     */
    public static void assertCommandBehavior(String inputCommand,
                                             String expectedStatusMessage,
                                             AddressBook expectedAddressBook,
                                             ExamBook expectedExamBook,
                                             boolean isRelevantPersonsExpected,
                                             boolean isRelevantExamsExpected,
                                             List<? extends ReadOnlyPerson> lastShownList,
                                             List<? extends ReadOnlyExam> lastShownExamList) throws Exception {
        assertCommandBehavior(inputCommand,
                expectedStatusMessage,
                "",
                expectedAddressBook,
                expectedExamBook,
                isRelevantPersonsExpected,
                isRelevantExamsExpected,
                lastShownList,
                lastShownExamList,
                true,
                true);
    }

    /**
     * Executes the command and confirms that the result messages are correct and
     * also confirms that the following three parts of the Logic object's state are as expected:<br>
     *      - the internal exam book data are same as those in the {@code expectedExamBook} <br>
     *      - the internal 'last shown list' matches the {@code lastShownList} <br>
     *
     *      if the command is exam-mutating
     *      - the storage exam file content matches data in {@code expectedExamBook} <br>
     */
    public static void assertCommandBehavior(String inputCommand,
                                             String expectedStatusMessage,
                                             String expectedOutputMessage,
                                             ExamBook expectedExamBook,
                                             boolean isRelevantExamsExpected,
                                             List<? extends ReadOnlyExam> lastShownList,
                                             boolean isExamMutating) throws Exception {
        //Execute the command
        CommandResult r = logic.execute(inputCommand);

        //Confirm the result contains the right data
        assertEquals(expectedStatusMessage, r.getStatusConsoleMessage());
        assertEquals(expectedOutputMessage, r.getOutputConsoleMessage());
        assertEquals(r.getRelevantExams().isPresent(), isRelevantExamsExpected);
        if (isRelevantExamsExpected) {
            assertEquals(lastShownList, r.getRelevantExams().get());
        }
        //Confirm the state of data is as expected
        assertEquals(expectedExamBook, examBook);
        assertEquals(lastShownList, logic.getLastShownExamList());

        //Confirm data is saved if is expected to
        if (isExamMutating) {
            assertTrue("Command did not have expected saving behaviour", storageStub.getHasSavedExams());
        }
    }

    /**
     * Executes the command and confirms that the result messages are correct and
     * also confirms that the following two parts of the Logic object's state are as expected:<br>
     *      - the internal statistics book data are same as those in the {@code expectedStatisticsBook} <br>
     *
     *      if the command is mutating
     *      - the storage file content matches data in {@code expectedStatisticsBook} <br>
     */
    public static void assertCommandBehavior(String inputCommand,
                                             String expectedStatusMessage,
                                             StatisticsBook expectedStatisticsBook,
                                             boolean isMutating) throws Exception {
        // If we need the storage to be tested
        // Injects the storageStub object to check
        //Execute the command
        CommandResult r = logic.execute(inputCommand);

        //Confirm the result contains the right data
        assertEquals(expectedStatusMessage, r.getStatusConsoleMessage());

        //Confirm the state of data is as expected
        assertEquals(expectedStatisticsBook, statisticsBook);

        //Confirm data is saved if is expected to
        if (isMutating) {
            assertTrue("Command did not have expected saving behaviour", storageStub.getHasSaved());
        }
    }

    /**
     * Executes the command and confirms that the result messages are correct and
     * also confirms that the following six parts of the Logic object's state are as expected:<br>
     *     - the internal address book data are same as those in the {@code expectedAddressBook} <br>
     *     - the internal exam book data are same as those in the {@code expectedExamBook} <br>
     *     - the internal 'last shown list' matches the {@code lastShownList} <br>
     *     - the internal 'last shown exam list' matches the {@code lastShownExamList} <br>
     *
     *     if the command is mutating and exam-mutating
     *     - the storage file content matches data in {@code expectedAddressBook} <br>
     *     - the storage exam file content matches data in {@code expectedExamBook} <br>
     */
    public static void assertCommandBehavior(String inputCommand,
                                             String expectedStatusMessage,
                                             String expectedOutputMessage,
                                             AddressBook expectedAddressBook,
                                             ExamBook expectedExamBook,
                                             boolean isRelevantPersonsExpected,
                                             boolean isRelevantExamsExpected,
                                             List<? extends ReadOnlyPerson> lastShownList,
                                             List<? extends ReadOnlyExam> lastShownExamList,
                                             boolean isMutating,
                                             boolean isExamMutating) throws Exception {
        //Execute the command
        CommandResult r = logic.execute(inputCommand);
        //Confirm the result contains the right data
        assertEquals(expectedStatusMessage, r.getStatusConsoleMessage());
        assertEquals(expectedOutputMessage, r.getOutputConsoleMessage());
        assertEquals(r.getRelevantPersons().isPresent(), isRelevantPersonsExpected);
        assertEquals(r.getRelevantExams().isPresent(), isRelevantExamsExpected);

        if (isRelevantPersonsExpected) {
            assertEquals(lastShownList, r.getRelevantPersons().get());
        }
        if (isRelevantExamsExpected) {
            assertEquals(lastShownExamList, r.getRelevantExams().get());
        }

        //Confirm the state of data is as expected
        assertEquals(expectedExamBook, examBook);
        assertEquals(expectedAddressBook, addressBook);

        //Confirm data is saved if is expected to
        if (isMutating) {
            assertTrue("Command did not have expected saving behaviour", storageStub.getHasSaved());
        }
        if (isExamMutating) {
            assertTrue("Command did not have expected saving behaviour", storageStub.getHasSavedExams());
        }
    }


    /**
     * Confirms the 'invalid argument index number behaviour' for the given command
     * targeting a single person in the last shown list, using visible index.
     * Used for commands in the form of COMMAND_WORD INDEX
     * Assumptions: The person list used for testing contains 2 persons
     * @param commandWord to test assuming it targets a single person in the last shown list based on visible index.
     */
    public static void assertInvalidIndexBehaviorForCommand(String commandWord) throws Exception {
        assertInvalidIndexBehaviorForCommand(commandWord, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Confirms the 'invalid argument index number behaviour' for the given command
     * targeting a single person in the last shown list, using visible index.
     * Used for commands in the form of COMMAND_WORD INDEX
     * @param commandWord to test assuming it targets a single person in the last shown list based on visible index.
     */
    public static void assertInvalidIndexBehaviorForCommand(String commandWord, String messageFormat) throws Exception {
        final String[] commands = {commandWord + " 0", commandWord + " -1", commandWord + " 3"};
        assertInvalidIndexBehaviour(commands, messageFormat);
    }

    /**
     * Confirms the 'invalid argument index number behaviour' for the given command
     * targeting a single person in the last shown list, using visible index.
     * Used for commands in the form of COMMAND_WORD PREFIX INDEX SUFFIX
     * PREFIX and SUFFIX can be empty
     * @param commandWord of the command.
     * @param prefix containing required information to enter before the INDEX.
     * @param suffix containing required information to enter after the INDEX.
     */
    public static void assertInvalidIndexBehaviorForCommand(String commandWord, String prefix,
                                                            String suffix) throws Exception {
        final String[] commands = {String.format("%s %s 0 %s", commandWord, prefix, suffix),
                String.format("%s %s -1 %s", commandWord, prefix, suffix),
                String.format("%s %s 3 %s", commandWord, prefix, suffix)};
        assertInvalidIndexBehaviour(commands, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Confirms the 'invalid argument index number behaviour' for the given command array
     * targeting a single person in the last shown list, using visible index.
     * Assumptions: No output console message
     *              Expected AddressBook is empty
     *              No relevant persons expected
     *              Last shown list does not change
     *              Is not mutating
     * @param commands to test assuming it targets a single person in the last shown list based on visible index.
     */
    private static void assertInvalidIndexBehaviour(String[] commands, String messageFormat) throws Exception {
        TestDataHelper helper = new TestDataHelper();
        List<Person> lastShownList = helper.generatePersonList(false, true);

        logic.setLastShownList(lastShownList);

        for (String command: commands) {
            assertCommandBehavior(command, messageFormat,
                    AddressBook.empty(), false, lastShownList, false);
        }
    }

    /**
     * Executes the command and confirms that the status message is correct.
     * Assumptions: No output console message
     *              Expected ExamBook is empty
     *              No relevant persons expected
     *              Last shown list is empty
     *              Is not exam-mutating
     * @see #assertCommandBehavior(String, String, ExamBook, boolean, List, boolean)
     */
    public static void assertCommandBehaviorForExam(String inputCommand, String expectedMessage) throws Exception {
        assertCommandBehavior(inputCommand, expectedMessage, ExamBook.empty(),
                false, Collections.emptyList(), false);
    }

    /**
     * Confirms the 'invalid argument index number behaviour' for the given command
     * targeting a single exam in the last shown list, using visible index.
     * Used for commands in the form of COMMAND_WORD INDEX
     * Assumptions: No output console message
     *              Expected ExamBook is empty
     *              No relevant persons expected
     *              Last shown list does not change
     *              Is not exam-mutating
     * @param commandWord to test assuming it targets a single exam in the last shown list based on visible index.
     */
    public static void assertInvalidIndexBehaviorForExamCommand(String commandWord) throws Exception {
        TestDataHelper helper = new TestDataHelper();
        List<Exam> lastShownList = helper.generateExamList(false, true);
        logic.setLastShownExamList(lastShownList);

        final String[] commands = {commandWord + " 0", commandWord + " -1", commandWord + " 3"};

        for (String command: commands) {
            assertCommandBehavior(command, MESSAGE_INVALID_EXAM_DISPLAYED_INDEX,
                    ExamBook.empty(), false, lastShownList, false);
        }
    }
}
