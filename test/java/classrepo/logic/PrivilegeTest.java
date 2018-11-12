package classrepo.logic;

import static classrepo.common.Messages.MESSAGE_INSUFFICIENT_PRIVILEGE;
import static classrepo.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static classrepo.common.Messages.MESSAGE_WRONG_NUMBER_ARGUMENTS;
import static classrepo.logic.CommandAssertions.assertCommandBehavior;
import static classrepo.logic.CommandAssertions.setData;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import classrepo.TestDataHelper;
import classrepo.commands.Command;
import classrepo.commands.commandresult.MessageType;
import classrepo.commands.privilege.RaisePrivilegeCommand;
import classrepo.commands.privilege.SetPermanentAdminCommand;
import classrepo.commands.privilege.ViewPrivilegeCommand;
import classrepo.common.Pair;
import classrepo.data.AddressBook;
import classrepo.data.ExamBook;
import classrepo.data.StatisticsBook;
import classrepo.data.person.Person;
import classrepo.parser.Parser;
import classrepo.privilege.Privilege;
import classrepo.privilege.user.AdminUser;
import classrepo.privilege.user.BasicUser;
import classrepo.privilege.user.User;
import classrepo.stubs.StorageStub;

public class PrivilegeTest {

    /**
     * This tests for Commands that affects or depends on Privilege
     */
    @Rule
    public TemporaryFolder saveFolder = new TemporaryFolder();
    private AddressBook addressBook = new AddressBook();
    private Privilege privilege = new Privilege();

    @Before
    public void setUp() throws Exception {
        ExamBook examBook = new ExamBook();
        StatisticsBook statisticsBook = new StatisticsBook();
        StorageStub stubFile = new StorageStub(saveFolder.newFile("testStubFile.txt").getPath(),
                saveFolder.newFile("testStubExamFile.txt").getPath(),
                saveFolder.newFile("testStubStatisticsFile.txt").getPath());
        Logic logic = new Logic(stubFile, addressBook, examBook, statisticsBook, privilege);
        setData(stubFile, addressBook, logic);
    }

    @Test
    public void executeSay_notLoggedIn_notLoggedInMessageShown() throws Exception {
        final String feedbackFormat = ViewPrivilegeCommand.MESSAGE_NOT_LOGGED_IN
                + ViewPrivilegeCommand.MESSAGE_PRIVILEGE_FORMAT;
        assertCommandBehavior("viewpri",
                        String.format(feedbackFormat, User.PrivilegeLevel.Basic.toString()), MessageType.OUTPUT);

        privilege.raiseToTutor();
        assertCommandBehavior("viewpri",
                        String.format(feedbackFormat, User.PrivilegeLevel.Tutor.toString()), MessageType.OUTPUT);

        privilege.raiseToAdmin();
        assertCommandBehavior("viewpri",
                        String.format(feedbackFormat, User.PrivilegeLevel.Admin.toString()), MessageType.OUTPUT);
    }

    @Test
    public void executeSay_loggedIn_success() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        final Person testPerson = helper.makeAdam();
        final String feedbackFormat = String.format(ViewPrivilegeCommand.MESSAGE_LOGGED_IN, testPerson.getName())
                + ViewPrivilegeCommand.MESSAGE_PRIVILEGE_FORMAT;

        privilege.setMyPerson(testPerson);
        assertCommandBehavior("viewpri",
                String.format(feedbackFormat, User.PrivilegeLevel.Basic.toString()), MessageType.OUTPUT);

        privilege.raiseToTutor();
        assertCommandBehavior("viewpri",
                String.format(feedbackFormat, User.PrivilegeLevel.Tutor.toString()), MessageType.OUTPUT);

        privilege.raiseToAdmin();
        assertCommandBehavior("viewpri",
                String.format(feedbackFormat, User.PrivilegeLevel.Admin.toString()), MessageType.OUTPUT);
    }

    @Test
    public void assertDefaultPasswordSetUp() {
        final String defaultPassword = "default_pw";
        assertEquals(addressBook.getMasterPassword(), defaultPassword);
    }

    @Test
    public void executeRaisePrivilege_invalidCommand_invalidMessageShown() throws Exception {
        final String[] inputs = {"raise", "raise "};

        final String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                RaisePrivilegeCommand.MESSAGE_USAGE);

        for (String input: inputs) {
            assertCommandBehavior(input, expectedMessage);
        }
        assertEquals(privilege.getUser(), new BasicUser());
    }

    @Test
    public void executeRaisePrivilege_wrongNumberOfArg_wrongNumberOfArgMessageShown() throws Exception {
        // First value of the pair represents String input
        // Second value of the pair represents number of arguments supplied, as an Integer.
        List<Pair<String, Integer>> inputToExpectedOutput = new ArrayList<>();
        inputToExpectedOutput.add(new Pair<>("raise arg1 arg2 ", 2));
        inputToExpectedOutput.add(new Pair<>("raise arg1 arg2 arg3 ", 3));

        final int requiredArgument = 1;

        for (Pair<String, Integer> inputToOutput: inputToExpectedOutput) {
            final String expectedMessage = String.format(MESSAGE_WRONG_NUMBER_ARGUMENTS, requiredArgument,
                    inputToOutput.getSecond(), RaisePrivilegeCommand.MESSAGE_USAGE);
            assertCommandBehavior(inputToOutput.getFirst(),
                    expectedMessage);
        }
        assertEquals(privilege.getUser(), new BasicUser());
    }

    @Test
    public void executeRaisePrivilege_wrongPassword_wrongPasswordMessageShown() throws Exception {
        String expectedMessage = RaisePrivilegeCommand.MESSAGE_WRONG_PASSWORD;
        assertCommandBehavior("raise wrong_password", expectedMessage);
        assertEquals(privilege.getUser(), new BasicUser());
    }

    @Test
    public void executeRaisePrivilege_loggedIn_loggedInMessageShown() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Person person = helper.makeAdam();
        privilege.setMyPerson(person);
        String expectedMessage = String.format(RaisePrivilegeCommand.MESSAGE_LOGGED_IN, person.getName());
        assertCommandBehavior("raise default_pw", expectedMessage);
        assertEquals(privilege.getUser(), new BasicUser());
    }

    @Test
    public void executeRaisePrivilege_defaultPassword_success() throws Exception {
        String defaultPassword = AddressBook.DEFAULT_MASTER_PASSWORD;
        assertCommandBehavior("raise " + defaultPassword,
                String.format(RaisePrivilegeCommand.MESSAGE_SUCCESS, new AdminUser().getPrivilegeLevelAsString()));
        assertEquals(privilege.getUser(), new AdminUser());
    }

    @Test
    public void executeRaisePrivilege_changedPassword_success() throws Exception {
        addressBook.setMasterPassword("new_Password");
        assertCommandBehavior("raise new_Password",
                String.format(RaisePrivilegeCommand.MESSAGE_SUCCESS, new AdminUser().getPrivilegeLevelAsString()));
        assertEquals(privilege.getUser(), new AdminUser());
    }

    @Test
    public void executeSetPerm_invalidCommand_invalidMessageShown() throws Exception {
        privilege.raiseToAdmin();
        assertCommandBehavior("perm",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        SetPermanentAdminCommand.MESSAGE_USAGE));
        assertFalse(addressBook.isPermAdmin());
    }

    @Test
    public void executeSetPerm_invalidDesiredState_invalidDesiredStateMessageShown() throws Exception {
        privilege.raiseToAdmin();
        assertCommandBehavior("perm wut",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        SetPermanentAdminCommand.MESSAGE_USAGE));
        assertFalse(addressBook.isPermAdmin());
    }

    @Test
    public void executeSetPerm_validInput_success() throws Exception {
        privilege.raiseToAdmin();
        assertCommandBehavior("perm true",
                String.format(SetPermanentAdminCommand.MESSAGE_SUCCESS, ""));
        assertTrue(addressBook.isPermAdmin());

        assertCommandBehavior("perm false",
                String.format(SetPermanentAdminCommand.MESSAGE_SUCCESS, " not"));
        assertFalse(addressBook.isPermAdmin());
    }

    @Test
    public void executeAdminCommands_insufficientPrivilege_insufficientPrivilegeMessageShown() throws Exception {
        final String[] inputs = {"clear", "editpw default_pw new_pw",
            "add Valid Name p/12345 e/valid@e.mail a/valid, address ",
            "delete 1"};
        assertCommandsInsufficientPrivilege(inputs);
        privilege.raiseToTutor();
        assertCommandsInsufficientPrivilege(inputs);
    }

    @Test
    public void executeTutorCommands_insufficientPrivilege_insufficientPrivilegeMessageShown() throws Exception {
        final String[] inputs = {"viewall 1"};
        assertCommandsInsufficientPrivilege(inputs);
    }

    /** Asserts that the given command strings cannot be executed using the current privilege level*/
    private void assertCommandsInsufficientPrivilege(String[] inputs) throws Exception {
        Parser parser = new Parser();
        for (String input: inputs) {
            Command command = parser.parseCommand(input);
            assertCommandBehavior(input, String.format(MESSAGE_INSUFFICIENT_PRIVILEGE,
                    privilege.getRequiredPrivilegeAsString(command), privilege.getLevelAsString())
            );
        }
    }
}
