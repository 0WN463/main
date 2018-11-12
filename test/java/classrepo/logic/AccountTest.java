package classrepo.logic;

import static classrepo.commands.account.AddAccountCommand.MESSAGE_INVALID_PRIVILEGE;
import static classrepo.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static classrepo.common.Messages.MESSAGE_NOT_LOGGED_IN;
import static classrepo.common.Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK;
import static classrepo.common.Messages.MESSAGE_WRONG_NUMBER_ARGUMENTS;
import static classrepo.logic.CommandAssertions.*;
import static classrepo.logic.CommandAssertions.assertCommandBehavior;
import static classrepo.privilege.Privilege.PRIVILEGE_CONSTRAINTS;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import classrepo.TestDataHelper;
import classrepo.commands.Command;
import classrepo.commands.account.AddAccountCommand;
import classrepo.commands.account.DeleteAccountCommand;
import classrepo.commands.account.LoginCommand;
import classrepo.commands.account.LogoutCommand;
import classrepo.commands.person.ClearCommand;
import classrepo.data.AddressBook;
import classrepo.data.ExamBook;
import classrepo.data.StatisticsBook;
import classrepo.data.account.Account;
import classrepo.data.person.Person;
import classrepo.data.person.ReadOnlyPerson;
import classrepo.privilege.Privilege;
import classrepo.privilege.user.AdminUser;
import classrepo.privilege.user.BasicUser;
import classrepo.privilege.user.TutorUser;
import classrepo.privilege.user.User.PrivilegeLevel;
import classrepo.stubs.StorageStub;

public class AccountTest {

    /**
     * This tests for Commands that affects or depends on Account
     */
    @Rule
    public TemporaryFolder saveFolder = new TemporaryFolder();
    private AddressBook addressBook = new AddressBook();
    private Privilege privilege;
    private Logic logic;

    @Before
    public void setUp() throws Exception {
        ExamBook examBook = new ExamBook();
        StatisticsBook statisticsBook = new StatisticsBook();
        StorageStub stubFile = new StorageStub(saveFolder.newFile("testStubFile.txt").getPath(),
                saveFolder.newFile("testStubExamFile.txt").getPath(),
                saveFolder.newFile("testStubStatisticsFile.txt").getPath());

        privilege = new Privilege(new AdminUser());
        logic = new Logic(stubFile, addressBook, examBook, statisticsBook, privilege);
        setData(stubFile, addressBook, logic);
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
    public void executeAddAccount_invalidArgument_invalidMessageShown() throws Exception {
        assertCommandBehavior("addacc",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddAccountCommand.MESSAGE_USAGE));
    }

    @Test
    public void executeAddAccount_wrongNumberOfArguments_wrongNumberOfArgumentsMessageShown() throws Exception {
        final String[] inputs = {"addacc user password TUTOR",
            "addacc 2 password TUTOR",
            "addacc 2 username TUTOR",
            "addacc 2 username password",
            "addacc 2 username password TUTOR TrailingArgument"};
        for (String input : inputs) {
            assertCommandBehavior(input,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddAccountCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void executeAddAccount_invalidIndex_invalidIndexMessageShown() throws Exception {
        assertInvalidIndexBehaviorForCommand("addacc", "", "username password BASIC");
    }

    @Test
    public void executeAddAccount_targetPersonHasAccount_targetPersonHasAccountMessageShown() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        AddressBook expected = new AddressBook();
        TestDataHelper.ThreePersons threePersons = helper.generateThreePersons();

        setUpThreePerson(addressBook, expected, logic, threePersons);

        final Person p1 = threePersons.getActualPerson(1);
        p1.setAccount(new Account("SomeAccount", "password", "Basic"));
        final Person expectedP1 = threePersons.getExpectedPerson(1);
        expectedP1.setAccount(new Account("SomeAccount", "password", "Basic"));

        final String[] inputs = {"addacc 1 user password Basic",
            "addacc 1 user password TUTOR",
            "addacc 1 user password admin"};
        for (String input : inputs) {
            assertCommandBehavior(input,
                    AddAccountCommand.MESSAGE_PERSON_HAS_ACCOUNT,
                    expected,
                    false,
                    threePersons.getExpected(),
                    false);
        }
    }

    @Test
    public void executeAddAccount_personMissing_personMissingMessageShown() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        AddressBook expected = new AddressBook();
        TestDataHelper.ThreePersons threePersons = helper.generateThreePersons();

        setUpThreePerson(addressBook, expected, logic, threePersons);

        final Person p2 = threePersons.getActualPerson(2);
        addressBook.removePerson(p2);
        expected.removePerson(p2);
        assertCommandBehavior("addacc 2 user password basic",
                MESSAGE_PERSON_NOT_IN_ADDRESSBOOK,
                expected,
                false,
                threePersons.getExpected(),
                false);
    }

    @Test
    public void executeAddAccount_invalidPrivilegeValue_invalidPrivilegeValueMessageShown() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        AddressBook expected = new AddressBook();
        TestDataHelper.ThreePersons threePersons = helper.generateThreePersons();

        setUpThreePerson(addressBook, expected, logic, threePersons);

        final String[] inputPrivileges = {"SUPERMAN", "BasicUser", "Admins"};
        for (String inputPrivilege : inputPrivileges) {
            final String inputFormat = "addacc 2 user password %s";
            final String input = String.format(inputFormat, inputPrivilege);
            assertCommandBehavior(input,
                    String.format(MESSAGE_INVALID_PRIVILEGE, inputPrivilege, PRIVILEGE_CONSTRAINTS),
                    expected,
                    false,
                    threePersons.getExpected(),
                    false);
        }
    }

    @Test
    public void executeAddAccount_duplicateUsername_duplicateUsernameMessageShown() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        AddressBook expected = new AddressBook();
        TestDataHelper.ThreePersons threePersons = helper.generateThreePersons();

        setUpThreePerson(addressBook, expected, logic, threePersons);

        final Person p1 = threePersons.getActualPerson(1);
        p1.setAccount(new Account("takenUserName", "password", "Tutor"));
        final Person expectP1 = threePersons.getExpectedPerson(1);
        expectP1.setAccount(new Account("takenUserName", "password", "Tutor"));

        final String[] inputs = {"addacc 2 takenUserName password TUTOR",
            "addacc 2 takenUserName AnotherPassword Basic",
            "addacc 3 takenUserName password admin"};
        for (String input : inputs) {
            assertCommandBehavior(input,
                    AddAccountCommand.MESSAGE_USERNAME_TAKEN,
                    expected,
                    false,
                    threePersons.getExpected(),
                    false);
        }
    }

    @Test
    public void executeAddAccount_validInput_success() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        AddressBook expected = new AddressBook();
        TestDataHelper.ThreePersons threePersons = helper.generateThreePersons();

        setUpThreePerson(addressBook, expected, logic, threePersons);

        final Person expectedP1 = threePersons.getExpectedPerson(1);
        final Account account1 = new Account("user", "password", "Basic");
        expectedP1.setAccount(account1);

        assertCommandBehavior("addacc 1 user password BASIC",
                String.format(AddAccountCommand.MESSAGE_ADD_ACCOUNT_PERSON_SUCCESS, "Person 1"),
                expected,
                true,
                threePersons.getExpected(),
                true);
        assertEquals(threePersons.getActualPerson(1).getAccount().get(), account1);

        final Person expectedP2 = threePersons.getExpectedPerson(2);
        final Account account2 = new Account("user2", "password2", "Admin");
        expectedP2.setAccount(account2);
        assertCommandBehavior("addacc 2 user2 password2 admin",
                String.format(AddAccountCommand.MESSAGE_ADD_ACCOUNT_PERSON_SUCCESS, "Person 2"),
                expected,
                true,
                threePersons.getExpected(),
                true);
        assertEquals(threePersons.getActualPerson(2).getAccount().get(), account2);

        final Person expectedP3 = threePersons.getExpectedPerson(3);
        final Account account3 = new Account("user3", "password3", "Tutor");
        expectedP3.setAccount(new Account("user3", "password3", "Tutor"));
        assertCommandBehavior("addacc 3 user3 password3 Tutor",
                String.format(AddAccountCommand.MESSAGE_ADD_ACCOUNT_PERSON_SUCCESS, "Person 3"),
                expected,
                true,
                threePersons.getExpected(),
                true);
        assertEquals(threePersons.getActualPerson(3).getAccount().get(), account3);
    }

    @Test
    public void executeDeleteAccount_invalidArgsFormat_invalidMessageShown() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteAccountCommand.MESSAGE_USAGE);
        assertCommandBehavior("deleteacc ", expectedMessage);
        assertCommandBehavior("deleteacc arg not number", expectedMessage);
    }

    @Test
    public void executeDeleteAccount_invalidIndex_invalidIndexMessageShown() throws Exception {
        assertInvalidIndexBehaviorForCommand("deleteacc");
    }

    @Test
    public void executeDeleteAccount_personMissing_personMissingMessageShown() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        AddressBook expected = new AddressBook();
        TestDataHelper.ThreePersons threePersons = helper.generateThreePersons();

        setUpThreePerson(addressBook, expected, logic, threePersons);

        final Person p2 = threePersons.getActualPerson(2);
        final Person expectedP2 = threePersons.getExpectedPerson(2);
        addressBook.removePerson(p2);
        expected.removePerson(expectedP2);

        assertCommandBehavior("deleteacc 2",
                MESSAGE_PERSON_NOT_IN_ADDRESSBOOK,
                expected,
                false,
                threePersons.getExpected(),
                false);
    }

    @Test
    public void executeDeleteAccount_targetHasNoAccount_noAccountMessageShown() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        AddressBook expected = new AddressBook();
        TestDataHelper.ThreePersons threePersons = helper.generateThreePersons();

        setUpThreePerson(addressBook, expected, logic, threePersons);
        assertCommandBehavior("deleteacc 2",
                DeleteAccountCommand.MESSAGE_PERSON_ACCOUNT_ABSENT,
                expected,
                false,
                threePersons.getExpected(),
                false);
    }

    @Test
    public void executeDeleteAccount_deleteSelf_targetingSelfMessageShown() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        AddressBook expected = new AddressBook();
        TestDataHelper.ThreePersons threePersons = helper.generateThreePersons();

        setUpThreePerson(addressBook, expected, logic, threePersons);

        final Account myAccount = new Account("myself", "password", "admin");
        threePersons.setBothPersons(1, myAccount);

        final Person self = threePersons.getActualPerson(1);
        privilege.setMyPerson(self);

        assertCommandBehavior("deleteacc 1",
                DeleteAccountCommand.MESSAGE_DELETING_SELF,
                expected,
                false,
                threePersons.getExpected(),
                false);
    }

    @Test
    public void executeDeleteAccount_validInput_success() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        AddressBook expected = new AddressBook();
        TestDataHelper.ThreePersons threePersons = helper.generateThreePersons();

        setUpThreePerson(addressBook, expected, logic, threePersons);

        threePersons.setBothPersons(3, new Account("user3", "pw3", "Admin"));

        final Person p2 = threePersons.getActualPerson(2);
        p2.setAccount(new Account("user2", "pw2", "basic"));

        assertCommandBehavior("deleteacc 2",
                String.format(DeleteAccountCommand.MESSAGE_DELETE_ACCOUNT_PERSON_SUCCESS, p2.getName()),
                expected,
                true,
                threePersons.getExpected(),
                true);
    }

    @Test
    public void executeListAccount_noPersonHasAccount_showsEmpty() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        AddressBook expected = new AddressBook();
        TestDataHelper.ThreePersons threePersons = helper.generateThreePersons();

        setUpThreePerson(addressBook, expected, logic, threePersons);

        List<? extends ReadOnlyPerson> expectedList = new ArrayList<>();

        assertCommandBehavior("listacc",
                Command.getMessageForPersonListShownSummary(expectedList),
                expected,
                true,
                expectedList,
                false);
    }

    @Test
    public void executeListAccount_personsWithAccount_showsList() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        AddressBook expected = new AddressBook();
        TestDataHelper.ThreePersons threePersons = helper.generateThreePersons();

        setUpThreePerson(addressBook, expected, logic, threePersons);

        final Account myAccount = new Account("myself", "password", "admin");
        threePersons.setBothPersons(1, myAccount);

        final Account myAccount2 = new Account("myself2", "password2", "admin");
        threePersons.setBothPersons(2, myAccount2);

        List<ReadOnlyPerson> expectedList = new ArrayList<>();
        expectedList.add(threePersons.getExpectedPerson(1));
        expectedList.add(threePersons.getExpectedPerson(2));

        assertCommandBehavior("listacc",
                Command.getMessageForPersonListShownSummary(expectedList),
                expected,
                true,
                expectedList,
                false);
    }

    @Test
    public void executeLogin_invalidArgument_invalidMessageShown() throws Exception {
        final TestDataHelper helper = new TestDataHelper();
        Person p1 = helper.generatePerson(1, false);
        p1.setAccount(new Account("username", "password", "Tutor"));
        addressBook.addPerson(p1);

        final AddressBook expectedAddressbook = new AddressBook();
        Person expectedP1 = helper.generatePerson(1, false);
        expectedP1.setAccount(new Account("username", "password", "Tutor"));
        expectedAddressbook.addPerson(expectedP1);
        final PrivilegeLevel initialPrivilege = privilege.getUser().getPrivilegeLevel();

        assertCommandBehavior("login",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, LoginCommand.MESSAGE_USAGE),
                expectedAddressbook,
                false,
                Collections.emptyList(),
                false);
        final PrivilegeLevel finalPrivilege = privilege.getUser().getPrivilegeLevel();
        assertEquals(initialPrivilege, finalPrivilege);
    }

    @Test
    public void executeLogin_invalidNumberOfArgument_invalidNumberOfArgumentMessageShown() throws Exception {
        final TestDataHelper helper = new TestDataHelper();
        Person p1 = helper.generatePerson(1, false);
        p1.setAccount(new Account("username", "password", "Tutor"));
        addressBook.addPerson(p1);

        final AddressBook expectedAddressBook = new AddressBook();
        Person expectedP1 = helper.generatePerson(1, false);
        expectedP1.setAccount(new Account("username", "password", "Tutor"));
        expectedAddressBook.addPerson(expectedP1);

        final String expectedMessage = MESSAGE_WRONG_NUMBER_ARGUMENTS;
        final PrivilegeLevel initialPrivilege = privilege.getUser().getPrivilegeLevel();
        final int requiredArguments = 2;
        int actualArguments = 1;

        assertCommandBehavior("login username",
                String.format(expectedMessage, requiredArguments, actualArguments, LoginCommand.MESSAGE_USAGE),
                expectedAddressBook,
                false,
                Collections.emptyList(),
                false);

        actualArguments = 1;
        assertCommandBehavior("login password",
                String.format(expectedMessage, requiredArguments, actualArguments, LoginCommand.MESSAGE_USAGE),
                expectedAddressBook,
                false,
                Collections.emptyList(),
                false);

        actualArguments = 3;
        assertCommandBehavior("login username password extra_argument",
                String.format(expectedMessage, requiredArguments, actualArguments, LoginCommand.MESSAGE_USAGE),
                expectedAddressBook,
                false,
                Collections.emptyList(),
                false);
        final PrivilegeLevel finalPrivilege = privilege.getUser().getPrivilegeLevel();
        assertEquals(initialPrivilege, finalPrivilege);
    }

    @Test
    public void executeLogin_noAccountWithUsername_noAccountMessageShown() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        AddressBook expected = new AddressBook();
        TestDataHelper.ThreePersons threePersons = helper.generateThreePersons();

        setUpThreePerson(addressBook, expected, logic, threePersons);

        threePersons.setBothPersons(1, new Account("username", "password", "Tutor"));

        final PrivilegeLevel initialPrivilege = privilege.getUser().getPrivilegeLevel();

        assertCommandBehavior("login otherUsername password",
                MESSAGE_PERSON_NOT_IN_ADDRESSBOOK,
                expected,
                false,
                threePersons.getExpected(),
                false);
        assertCommandBehavior("login password password",
                MESSAGE_PERSON_NOT_IN_ADDRESSBOOK,
                expected,
                false,
                threePersons.getExpected(),
                false);
        final PrivilegeLevel finalPrivilege = privilege.getUser().getPrivilegeLevel();
        assertEquals(initialPrivilege, finalPrivilege);
    }

    @Test
    public void executeLogin_wrongPassword_wrongPasswordMessageShown() throws Exception {
        final TestDataHelper helper = new TestDataHelper();
        Person p1 = helper.generatePerson(1, false);
        p1.setAccount(new Account("username", "password", "Tutor"));
        addressBook.addPerson(p1);

        final AddressBook expectedAddressBook = new AddressBook();
        Person expectedP1 = helper.generatePerson(1, false);
        expectedP1.setAccount(new Account("username", "password", "Tutor"));
        expectedAddressBook.addPerson(expectedP1);
        final PrivilegeLevel initialPrivilege = privilege.getUser().getPrivilegeLevel();

        assertCommandBehavior("login username WrongPassword",
                LoginCommand.MESSAGE_WRONG_PASSWORD,
                expectedAddressBook,
                false,
                Collections.emptyList(),
                false);
        assertCommandBehavior("login username username",
                LoginCommand.MESSAGE_WRONG_PASSWORD,
                expectedAddressBook,
                false,
                Collections.emptyList(),
                false);
        assertCommandBehavior("login username password12345",
                LoginCommand.MESSAGE_WRONG_PASSWORD,
                expectedAddressBook,
                false,
                Collections.emptyList(),
                false);
        final PrivilegeLevel finalPrivilege = privilege.getUser().getPrivilegeLevel();
        assertEquals(initialPrivilege, finalPrivilege);
    }

    @Test
    public void executeLogin_validInput_success() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        AddressBook expected = new AddressBook();
        TestDataHelper.ThreePersons threePersons = helper.generateThreePersons();

        setUpThreePerson(addressBook, expected, logic, threePersons);

        threePersons.setBothPersons(1, new Account("tutorUser", "password", "Tutor"));
        threePersons.setBothPersons(2, new Account("B_user", "password2", "Basic"));
        threePersons.setBothPersons(3, new Account("Sudo", "password", "Admin"));

        final Person p1 = threePersons.getActualPerson(1);
        assertCommandBehavior("login tutorUser password",
                String.format(LoginCommand.MESSAGE_SUCCESS, p1.getName(), "Tutor"),
                expected,
                false,
                threePersons.getExpected(),
                false);
        assertEquals(privilege.getUser(), new TutorUser());

        final Person p2 = threePersons.getActualPerson(2);
        assertCommandBehavior("login B_user password2",
                String.format(LoginCommand.MESSAGE_SUCCESS, p2.getName(), "Basic"),
                expected,
                false,
                threePersons.getExpected(),
                false);
        assertEquals(privilege.getUser(), new BasicUser());

        final Person p3 = threePersons.getActualPerson(3);
        assertCommandBehavior("login Sudo password",
                String.format(LoginCommand.MESSAGE_SUCCESS, p3.getName(), "Admin"),
                expected,
                false,
                threePersons.getExpected(),
                false);
        assertEquals(privilege.getUser(), new AdminUser());
    }

    @Test
    public void executeLogout_notLoggedIn_notLoggedInMessageShown() throws Exception {
        privilege.resetPrivilege();
        assertCommandBehavior("logout", MESSAGE_NOT_LOGGED_IN);
    }

    @Test
    public void executeLogout_validInput_success() throws Exception {
        assertCommandBehavior("logout", LogoutCommand.MESSAGE_SUCCESS);
        assertTrue(privilege.isBase());
    }

    // This is tested here as it requires knowledge of Account
    @Test
    public void executeClear_loggedIn_selfTargetingMessageShown() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        TestDataHelper.ThreePersons threePersons = helper.generateThreePersons();
        AddressBook expected = new AddressBook();

        setUpThreePerson(addressBook, expected, logic, threePersons);

        final Person p2 = threePersons.getActualPerson(2);

        privilege.setMyPerson(p2);

        assertCommandBehavior("clear",
                ClearCommand.MESSAGE_DELETING_SELF,
                expected,
                false,
                threePersons.getExpected(),
                true);
    }
}
