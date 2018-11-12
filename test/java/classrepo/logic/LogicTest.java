package classrepo.logic;

import static classrepo.common.Messages.MESSAGE_COMMAND_NOT_FOUND;
import static classrepo.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static classrepo.common.Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK;
import static classrepo.common.Messages.MESSAGE_WRONG_NUMBER_ARGUMENTS;
import static classrepo.logic.CommandAssertions.assertCommandBehavior;
import static classrepo.logic.CommandAssertions.assertInvalidIndexBehaviorForCommand;
import static junit.framework.TestCase.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import classrepo.TestDataHelper;
import classrepo.commands.Command;
import classrepo.commands.account.ListAccountCommand;
import classrepo.commands.account.LogoutCommand;
import classrepo.commands.commandresult.MessageType;
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
import classrepo.common.Messages;
import classrepo.common.Pair;
import classrepo.data.AddressBook;
import classrepo.data.ExamBook;
import classrepo.data.StatisticsBook;
import classrepo.data.person.Assessment;
import classrepo.data.person.Grades;
import classrepo.data.person.Person;
import classrepo.data.person.ReadOnlyPerson;
import classrepo.data.person.details.Address;
import classrepo.data.person.details.Email;
import classrepo.data.person.details.Name;
import classrepo.data.person.details.Phone;
import classrepo.data.tag.Tag;
import classrepo.privilege.Privilege;
import classrepo.privilege.user.AdminUser;
import classrepo.stubs.StorageStub;

public class LogicTest {

    /**
     * See https://github.com/junit-team/junit4/wiki/rules#temporaryfolder-rule
     */
    @Rule
    public TemporaryFolder saveFolder = new TemporaryFolder();
    private AddressBook addressBook;
    private Privilege privilege;
    private StatisticsBook statisticBook;
    private Logic logic;

    @Before
    public void setUp() throws Exception {
        StorageStub stubFile = new StorageStub(saveFolder.newFile("testStubFile.txt").getPath(),
                saveFolder.newFile("testStubExamFile.txt").getPath(),
                saveFolder.newFile("testStubStatisticsFile.txt").getPath());

        addressBook = new AddressBook();
        ExamBook examBook = new ExamBook();
        statisticBook = new StatisticsBook();
        // Privilege set to admin to allow all commands.
        // Privilege restrictions are tested separately under PrivilegeTest.
        privilege = new Privilege(new AdminUser());

        logic = new Logic(stubFile, addressBook, examBook, statisticBook, privilege);
        CommandAssertions.setData(stubFile, addressBook, logic, examBook, statisticBook);
    }

    private void setUpThreePerson(AddressBook addressBook,
                                  AddressBook expected,
                                  Logic logic,
                                  TestDataHelper.ThreePersons threePersons) throws Exception {
        TestDataHelper helper = new TestDataHelper();
        helper.addToAddressBook(expected, threePersons.getExpected());
        helper.addToAddressBook(addressBook, threePersons.getActual());
        logic.setLastShownList(threePersons.getActual());
    }

    @Test
    public void constructor() {
        //Constructor is called in the setup() method which executes before every test, no need to call it here again.

        //Confirm the last shown list is empty
        assertEquals(Collections.emptyList(), logic.getLastShownList());
    }

    /** Checks if logic's privilege is raised to Admin when calling initPrivilege with PERSONS of isPerm = true.*/
    @Test
    public void init_isPerm() {
        final AddressBook ab = new AddressBook();
        ab.setPermAdmin(true);
        logic.setAddressBook(ab);
        privilege.resetPrivilege();

        logic.initPrivilege();
        assertEquals(new AdminUser(), privilege.getUser());
    }

    @Test
    public void defaultConstructor() throws Exception {
        // Verifies if addressbook.txt, exams.txt, statistics.txt are loadable
        logic = new Logic();
        //Confirm the last shown list is empty
        assertEquals(Collections.emptyList(), logic.getLastShownList());
    }

    @Test
    public void execute_invalidString_invalidCommandMessage() throws Exception {
        String invalidCommand = "       ";
        assertCommandBehavior(invalidCommand,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE)
        );
    }

    @Test
    public void execute_unknownCommandWord_helpMessageShown() throws Exception {
        final HelpCommand helpCommand = new HelpCommand();
        helpCommand.setData(addressBook, statisticBook, new ArrayList<>(), privilege);
        String unknownCommand = "uicfhmowqewca";
        assertCommandBehavior(unknownCommand, MESSAGE_COMMAND_NOT_FOUND, helpCommand.makeHelpManual());
    }

    // Checks if all void commands is rejected if there is a trailing argument.
    @Test
    public void executeVoidCommands_trailingArg_invalidCommandMessage() throws Exception {
        List<Pair<String, Command>> inputToExpectedOutput = List.of(
                new Pair<>("clear", new ClearCommand()),
                new Pair<>("list", new ListCommand()),
                new Pair<>("listall", new ListAllCommand()),
                new Pair<>("viewself", new ViewSelfCommand()),
                new Pair<>("listacc", new ListAccountCommand()),
                new Pair<>("logout", new LogoutCommand()),
                new Pair<>("help", new HelpCommand()));

        for (Pair<String, Command> inputToOutput: inputToExpectedOutput) {
            assertCommandBehavior(inputToOutput.getFirst() + " garbage",
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, inputToOutput.getSecond().getCommandUsageMessage())
            );
        }
    }

    @Test
    public void executeHelp() throws Exception {
        final HelpCommand helpCommand = new HelpCommand();
        helpCommand.setData(addressBook, statisticBook, new ArrayList<>(), privilege);
        assertCommandBehavior("help", helpCommand.makeHelpManual(), MessageType.OUTPUT);

        privilege.raiseToTutor();
        assertCommandBehavior("help", helpCommand.makeHelpManual(), MessageType.OUTPUT);

        privilege.raiseToAdmin();
        assertCommandBehavior("help", helpCommand.makeHelpManual(), MessageType.OUTPUT);
    }

    @Test
    public void executeExit() throws Exception {
        assertCommandBehavior("exit", ExitCommand.MESSAGE_EXIT_ACKNOWLEDGEMENT);
    }

    @Test
    public void executeClear() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        // generates the 3 test people and execute the add command
        for (int i = 1; i <= 3; ++i) {
            final Person testPerson = helper.generatePerson(i, true);
            addressBook.addPerson(testPerson);
            logic.execute(helper.generateAddCommand(testPerson));
        }

        assertCommandBehavior("clear",
                ClearCommand.MESSAGE_SUCCESS,
                AddressBook.empty(),
                true,
                Collections.emptyList(),
                true);
    }

    @Test
    public void executeAdd_invalidArgsFormat_invalidCommandMessage() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);
        assertCommandBehavior("add wrong args wrong args", expectedMessage);
        assertCommandBehavior("add Valid Name 12345 e/valid@email.butNoPhonePrefix a/valid, address",
                expectedMessage);
        assertCommandBehavior("add Valid Name p/12345 valid@email.butNoPrefix a/valid, address",
                expectedMessage);
        assertCommandBehavior("add Valid Name p/12345 e/valid@email.butNoAddressPrefix valid, address",
                expectedMessage);
    }

    @Test
    public void executeAdd_invalidPersonData_invalidMessage() throws Exception {
        assertCommandBehavior("add []\\[;] p/12345 e/valid@e.mail a/valid, address",
                Name.MESSAGE_NAME_CONSTRAINTS);
        assertCommandBehavior(
                "add Valid Name p/not_numbers e/valid@e.mail a/valid, address",
                Phone.MESSAGE_PHONE_CONSTRAINTS);
        assertCommandBehavior("add Valid Name p/12345 e/notAnEmail a/valid, address",
                Email.MESSAGE_EMAIL_CONSTRAINTS);
        assertCommandBehavior("add Valid Name p/12345 e/valid@e.mail a/valid, address t/invalid_-[.tag",
                Tag.MESSAGE_TAG_CONSTRAINTS);
        assertCommandBehavior("add Valid Name p/12345 e/valid@e.mail a/#$%#@#What Am I?",
                Address.MESSAGE_ADDRESS_CONSTRAINTS);
        assertCommandBehavior("add Valid Name p/1234 e/valid@e.mail a/valid, address t/invalid_-[.tag",
                Tag.MESSAGE_TAG_CONSTRAINTS);
    }

    @Test
    public void executeAdd_validData_success() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        Person toBeAdded = helper.makeAdam();
        AddressBook expected = new AddressBook();
        expected.addPerson(toBeAdded);

        // execute command and verify result
        assertCommandBehavior(helper.generateAddCommand(toBeAdded),
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded),
                expected,
                true,
                List.of(toBeAdded),
                true);
    }

    @Test
    public void executeAdd_duplicateData_duplicateNotAllowed() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        Person toBeAdded = helper.makeAdam();
        AddressBook expected = new AddressBook();
        expected.addPerson(toBeAdded);

        // setup starting state
        addressBook.addPerson(toBeAdded); // person already in internal address book

        // execute command and verify result
        assertCommandBehavior(
                helper.generateAddCommand(toBeAdded),
                AddCommand.MESSAGE_DUPLICATE_PERSON,
                expected,
                false,
                Collections.emptyList(),
                false);
    }

    @Test
    public void executeListShowsAllPersons() throws Exception {
        // prepare expectations
        TestDataHelper helper = new TestDataHelper();
        AddressBook expected = helper.generateAddressBook(false, true);
        List<? extends ReadOnlyPerson> expectedList = expected.getAllPersons().immutableListView();

        // prepare address book state
        helper.addToAddressBook(addressBook, false, true);

        assertCommandBehavior("list",
                Command.getMessageForPersonListShownSummary(expectedList),
                expected,
                true,
                expectedList,
                false);
    }

    @Test
    public void executeListAllShowsAllPersons() throws Exception {
        // prepare expectations
        TestDataHelper helper = new TestDataHelper();
        AddressBook expected = helper.generateAddressBook(false, true);
        List<? extends ReadOnlyPerson> expectedList = expected.getAllPersons().immutableListView();

        // prepare address book state
        helper.addToAddressBook(addressBook, false, true);

        assertCommandBehavior("listall",
                Command.getMessageForPersonListShownSummary(expectedList),
                expected,
                true,
                expectedList,
                false);
    }

    @Test
    public void executeView_invalidArgsFormat_invalidCommandMessage() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE);
        assertCommandBehavior("view ", expectedMessage);
        assertCommandBehavior("view arg not number", expectedMessage);
    }

    @Test
    public void executeView_invalidIndex_invalidIndexMessage() throws Exception {
        assertInvalidIndexBehaviorForCommand("view");
    }

    @Test
    public void executeView_validArgs_successOnlyShowsNonPrivate() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Person p1 = helper.generatePerson(1, true);
        Person p2 = helper.generatePerson(2, false);
        List<Person> lastShownList = helper.generatePersonList(p1, p2);
        AddressBook expected = helper.generateAddressBook(lastShownList);
        helper.addToAddressBook(addressBook, lastShownList);

        logic.setLastShownList(lastShownList);

        assertCommandBehavior("view 1",
                String.format(ViewCommand.MESSAGE_VIEW_PERSON_DETAILS, p1.getName()),
                p1.getAsTextHidePrivate(),
                expected,
                false,
                lastShownList,
                false);

        assertCommandBehavior("view 2",
                String.format(ViewCommand.MESSAGE_VIEW_PERSON_DETAILS, p2.getName()),
                p2.getAsTextHidePrivate(),
                expected,
                false,
                lastShownList,
                false);
    }

    @Test
    public void executeView_missingPerson_personMissingMessage() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Person p1 = helper.generatePerson(1, false);
        Person p2 = helper.generatePerson(2, false);
        List<Person> lastShownList = helper.generatePersonList(p1, p2);

        AddressBook expected = new AddressBook();
        expected.addPerson(p2);

        addressBook.addPerson(p2);
        logic.setLastShownList(lastShownList);

        assertCommandBehavior("view 1",
                MESSAGE_PERSON_NOT_IN_ADDRESSBOOK,
                expected,
                false,
                lastShownList,
                false);
    }

    @Test
    public void executeViewAll_invalidArgsFormat_invalidCommandMessage() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewAllCommand.MESSAGE_USAGE);
        assertCommandBehavior("viewall ", expectedMessage);
        assertCommandBehavior("viewall arg not number", expectedMessage);
    }

    @Test
    public void executeViewAll_invalidIndex_invalidIndexMessage() throws Exception {
        assertInvalidIndexBehaviorForCommand("viewall");
    }

    @Test
    public void executeViewAllAlsoShowsPrivate() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Person p1 = helper.generatePerson(1, true);
        Person p2 = helper.generatePerson(2, false);
        p1.setFees(helper.makeFees(1));
        Assessment assessment = helper.generateAssessment(1);
        Grades grade = new Grades(100);
        assessment.addGrade(p1, grade);
        p1.addAssessment(assessment);
        List<Person> lastShownList = helper.generatePersonList(p1, p2);
        AddressBook expected = helper.generateAddressBook(lastShownList);
        helper.addToAddressBook(addressBook, lastShownList);

        logic.setLastShownList(lastShownList);

        assertCommandBehavior("viewall 1",
                String.format(ViewCommand.MESSAGE_VIEW_PERSON_DETAILS, p1.getName()),
                p1.getAsTextShowAll(),
                expected,
                false,
                lastShownList,
                false);


        assertCommandBehavior("viewall 2",
                String.format(ViewCommand.MESSAGE_VIEW_PERSON_DETAILS, p2.getName()),
                p2.getAsTextShowAll(),
                expected,
                false,
                lastShownList,
                false);
    }

    @Test
    public void executeViewAll_missingPerson_missingPersonMessage() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Person p1 = helper.generatePerson(1, false);
        Person p2 = helper.generatePerson(2, false);
        List<Person> lastShownList = helper.generatePersonList(p1, p2);

        AddressBook expected = new AddressBook();
        expected.addPerson(p1);

        addressBook.addPerson(p1);
        logic.setLastShownList(lastShownList);

        assertCommandBehavior("viewall 2",
                MESSAGE_PERSON_NOT_IN_ADDRESSBOOK,
                expected,
                false,
                lastShownList,
                false);
    }

    @Test
    public void executeDelete_invalidArgsFormat_invalidCommandMessage() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);
        assertCommandBehavior("delete ", expectedMessage);
        assertCommandBehavior("delete arg not number", expectedMessage);
    }

    @Test
    public void executeDelete_invalidIndex_invalidIndexMessage() throws Exception {
        assertInvalidIndexBehaviorForCommand("delete");
    }

    @Test
    public void executeDeleteRemovesCorrectPerson() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        AddressBook expected = new AddressBook();
        TestDataHelper.ThreePersons threePersons = helper.generateThreePersons();

        setUpThreePerson(addressBook, expected, logic, threePersons);

        final Person p2 = threePersons.getExpectedPerson(2);
        expected.removePerson(p2);
        threePersons.getExpected().remove(p2);

        assertCommandBehavior("delete 2",
                String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, p2),
                expected,
                true,
                threePersons.getExpected(),
                true);
    }

    @Test
    public void executeDeleteSelfExceptionThrown() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        AddressBook expected = new AddressBook();
        TestDataHelper.ThreePersons threePersons = helper.generateThreePersons();

        setUpThreePerson(addressBook, expected, logic, threePersons);

        final Person p2 = threePersons.getActualPerson(2);
        privilege.setMyPerson(p2);
        assertCommandBehavior("delete 2",
                DeleteCommand.MESSAGE_DELETING_SELF,
                expected,
                false,
                threePersons.getExpected(),
                true);
    }

    @Test
    public void executeDeleteMissingInAddressBook() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Person p1 = helper.generatePerson(1, false);
        Person p2 = helper.generatePerson(2, true);
        Person p3 = helper.generatePerson(3, true);

        List<Person> threePersons = helper.generatePersonList(p1, p2, p3);

        AddressBook expected = helper.generateAddressBook(threePersons);
        expected.removePerson(p2);

        helper.addToAddressBook(addressBook, threePersons);
        addressBook.removePerson(p2);
        logic.setLastShownList(threePersons);

        assertCommandBehavior("delete 2",
                MESSAGE_PERSON_NOT_IN_ADDRESSBOOK,
                expected,
                false,
                threePersons,
                false);
    }

    @Test
    public void executeFind_invalidArgsFormat_invalidCommandMessage() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        assertCommandBehavior("find ", expectedMessage);
    }

    @Test
    public void executeFindOnlyMatchesFullWordsInNames() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Person pTarget1 = helper.generatePersonWithName("bla bla KEY bla");
        Person pTarget2 = helper.generatePersonWithName("bla KEY bla bceofeia");
        Person p1 = helper.generatePersonWithName("KE Y");
        Person p2 = helper.generatePersonWithName("KEYKEYKEY sduauo");

        List<Person> fourPersons = helper.generatePersonList(p1, pTarget1, p2, pTarget2);
        AddressBook expected = helper.generateAddressBook(fourPersons);
        List<Person> expectedList = helper.generatePersonList(pTarget1, pTarget2);
        helper.addToAddressBook(addressBook, fourPersons);

        assertCommandBehavior("find KEY",
                Command.getMessageForPersonListShownSummary(expectedList),
                expected,
                true,
                expectedList,
                false);
    }

    @Test
    public void executeFindIsCaseSensitive() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Person pTarget1 = helper.generatePersonWithName("bla bla KEY bla");
        Person pTarget2 = helper.generatePersonWithName("bla KEY bla bceofeia");
        Person p1 = helper.generatePersonWithName("key key");
        Person p2 = helper.generatePersonWithName("KEy sduauo");

        List<Person> fourPersons = helper.generatePersonList(p1, pTarget1, p2, pTarget2);
        AddressBook expected = helper.generateAddressBook(fourPersons);
        List<Person> expectedList = helper.generatePersonList(pTarget1, pTarget2);
        helper.addToAddressBook(addressBook, fourPersons);

        assertCommandBehavior("find KEY",
                Command.getMessageForPersonListShownSummary(expectedList),
                expected,
                true,
                expectedList,
                false);
    }

    @Test
    public void executeFindMatchesIfAnyKeywordPresent() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Person pTarget1 = helper.generatePersonWithName("bla bla KEY bla");
        Person pTarget2 = helper.generatePersonWithName("bla rAnDoM bla bceofeia");
        Person p1 = helper.generatePersonWithName("key key");
        Person p2 = helper.generatePersonWithName("KEy sduauo");

        List<Person> fourPersons = helper.generatePersonList(p1, pTarget1, p2, pTarget2);
        AddressBook expected = helper.generateAddressBook(fourPersons);
        List<Person> expectedList = helper.generatePersonList(pTarget1, pTarget2);
        helper.addToAddressBook(addressBook, fourPersons);

        assertCommandBehavior("find KEY rAnDoM",
                Command.getMessageForPersonListShownSummary(expectedList),
                expected,
                true,
                expectedList,
                false);
    }

    @Test
    public void executeViewSelf_notLoggedIn_errorMessage() throws Exception {
        assertCommandBehavior("viewself", Messages.MESSAGE_NOT_LOGGED_IN);
    }

    @Test
    public void executeViewSelf_loggedIn_success() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        AddressBook expected = new AddressBook();
        TestDataHelper.ThreePersons threePersons = helper.generateThreePersons();

        setUpThreePerson(addressBook, expected, logic, threePersons);

        final Person p2 = threePersons.getActualPerson(2);
        privilege.setMyPerson(p2);
        assertCommandBehavior("viewself",
                String.format(ViewSelfCommand.MESSAGE_VIEW_PERSON_DETAILS, p2.getName()),
                p2.getAsTextShowAll(),
                expected,
                false,
                threePersons.getExpected(),
                false);
    }

    @Test
    public void executeChangePassword_invalidArgs_invalidCommandMessage() throws Exception {
        final String initialPassword = addressBook.getMasterPassword();
        final String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                EditPasswordCommand.MESSAGE_USAGE);
        assertCommandBehavior("editpw", expectedMessage);
        assertCommandBehavior("editpw ", expectedMessage);
        assertEquals(addressBook.getMasterPassword(), initialPassword);
    }

    @Test
    public void executeChangePassword_invalidNumberOfArgs_invalidNumberMessage() throws Exception {
        final String initialPassword = addressBook.getMasterPassword();
        final String expectedMessage = MESSAGE_WRONG_NUMBER_ARGUMENTS;
        final int requiredArguments = 2;
        int actualArguments = 1;
        assertCommandBehavior("editpw default_pw",
                String.format(expectedMessage, requiredArguments, actualArguments, EditPasswordCommand.MESSAGE_USAGE));

        actualArguments = 3;
        assertCommandBehavior("editpw default_pw new_pw extra_arg",
                String.format(expectedMessage, requiredArguments, actualArguments,
                        EditPasswordCommand.MESSAGE_USAGE));
        assertEquals(addressBook.getMasterPassword(), initialPassword);
    }

    @Test
    public void executeChangePassword_wrongPassword_errorMessage() throws Exception {
        final String initialPassword = addressBook.getMasterPassword();
        String expectedMessage = EditPasswordCommand.MESSAGE_WRONG_PASSWORD;
        assertCommandBehavior("editpw wrong_password new_password", expectedMessage);
        assertCommandBehavior("editpw default_password1 new_password", expectedMessage);
        assertCommandBehavior("editpw Default_password new_password", expectedMessage);
        assertEquals(addressBook.getMasterPassword(), initialPassword);
    }

    @Test
    public void executeChangePassword_sameAsOldPassword_errorMessage() throws Exception {
        String expectedMessage = EditPasswordCommand.MESSAGE_SAME_AS_OLD_PASSWORD;
        assertCommandBehavior("editpw default_pw default_pw", expectedMessage);
        addressBook.setMasterPassword("new_password");
        assertCommandBehavior("editpw new_password new_password", expectedMessage);
    }

    @Test
    public void executeChangePassword_validArgs_success() throws Exception {
        final String expectedMessage = EditPasswordCommand.MESSAGE_SUCCESS;
        final String commandFormat = "editpw %s %s";
        String oldPassword = addressBook.getMasterPassword();
        String newPassword = "new_password";
        String commandInput = String.format(commandFormat, oldPassword, newPassword);
        assertCommandBehavior(commandInput,
                String.format(expectedMessage, newPassword));
        assertEquals(addressBook.getMasterPassword(), newPassword);

        oldPassword = addressBook.getMasterPassword();
        newPassword = "another_new_password";
        commandInput = String.format(commandFormat, oldPassword, newPassword);
        assertCommandBehavior(commandInput,
                String.format(expectedMessage, newPassword));
        assertEquals(addressBook.getMasterPassword(), newPassword);
    }
}
