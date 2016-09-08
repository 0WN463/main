package seedu.addressbook.logic;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import seedu.addressbook.commands.CommandResult;
import seedu.addressbook.commands.*;
import seedu.addressbook.common.Messages;
import seedu.addressbook.data.AddressBook;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.*;
import seedu.addressbook.data.tag.Tag;
import seedu.addressbook.data.tag.UniqueTagList;
import seedu.addressbook.storage.StorageFile;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static seedu.addressbook.common.Messages.*;

/**
 * The {@link Logic} object has 3 main pieces of state to verify when testing:
 *      - the internal addressbook
 *      - the internal last shown list
 *      - the storage file
 * Tests also verify verify the return value of the method you are testing.
 */
public class LogicTest {

    /**
     * See https://github.com/junit-team/junit4/wiki/rules#temporaryfolder-rule
     */
    @Rule
    public TemporaryFolder saveFolder = new TemporaryFolder();

    private StorageFile saveFile;
    private AddressBook addressBook;
    private Logic logic;

    @Before
    public void setup() throws Exception {
        saveFile = new StorageFile(saveFolder.newFile("testSaveFile.txt").getPath());
        addressBook = new AddressBook();
        saveFile.save(addressBook);
        logic = new Logic(saveFile, addressBook);
    }

    /**
     * Confirms that the following three parts of the Logic object's state is as expected:<br>
     *      - the internal addressbook is same as {@code expectedAddressBook} <br>
     *      - the internal last shown list is the same as {@code expectedLastList} <br>
     *      - the storage file content matches data in {@code expectedAddressBook} <br>
     */
    private void assertLogicObjectStateEquals(AddressBook expectedAddressBook, List<? extends ReadOnlyPerson> expectedLastList)
            throws StorageFile.StorageOperationException {
        assertEquals(addressBook, expectedAddressBook);
        assertEquals(logic.getLastShownList(), expectedLastList);
        assertEquals(addressBook, saveFile.load());
    }

    /**
     * Executes the command and confirms that the result message is correct and both in-memory and persistent data
     * were not affected.
     */
    private void assertNonMutatingCommandBehavior(String inputCommand, String expectedMessage) throws Exception {
        CommandResult r = logic.execute(inputCommand);
        assertEquals(r.feedbackToUser, expectedMessage);
        assertFalse(r.getRelevantPersons().isPresent());
        // no side effects to logic object
        assertLogicObjectStateEquals(AddressBook.empty(), Collections.emptyList());
    }

    @Test
    public void constructor() {
        //Constructor is called in the setup() method which executes before every test, no need to call it here again.

        //Confirm the last shown list is empty
        assertEquals(logic.getLastShownList(), Collections.emptyList());
    }

    @Test
    public void execute_invalid() throws Exception {
        String invalidCommand = "       ";
        assertNonMutatingCommandBehavior(invalidCommand,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
    }

    @Test
    public void execute_unknownCommandWord() throws Exception {
        String unknownCommand = "uicfhmowqewca";
        assertNonMutatingCommandBehavior(unknownCommand, HelpCommand.MESSAGE_ALL_USAGES);
    }

    @Test
    public void execute_help() throws Exception {
        assertNonMutatingCommandBehavior("help", HelpCommand.MESSAGE_ALL_USAGES);
    }

    @Test
    public void execute_exit() throws Exception {
        assertNonMutatingCommandBehavior("exit", ExitCommand.MESSAGE_EXIT_ACKNOWEDGEMENT);
    }

    @Test
    public void execute_clear() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        addressBook.addPerson(helper.generatePerson(1, true));
        addressBook.addPerson(helper.generatePerson(2, true));
        addressBook.addPerson(helper.generatePerson(3, true));
        CommandResult r = logic.execute("clear");

        assertEquals(r.feedbackToUser, ClearCommand.MESSAGE_SUCCESS);
        assertFalse(r.getRelevantPersons().isPresent());
        assertLogicObjectStateEquals(AddressBook.empty(), Collections.emptyList());
    }

    @Test
    public void execute_add_invalidArgsFormat() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);
        assertNonMutatingCommandBehavior(
                "add wrong args wrong args", expectedMessage);
        assertNonMutatingCommandBehavior(
                "add Valid Name 12345 e/valid@email.butNoPhonePrefix a/valid, address", expectedMessage);
        assertNonMutatingCommandBehavior(
                "add Valid Name p/12345 valid@email.butNoPrefix a/valid, address", expectedMessage);
        assertNonMutatingCommandBehavior(
                "add Valid Name p/12345 e/valid@email.butNoAddressPrefix valid, address", expectedMessage);
    }

    @Test
    public void execute_add_invalidPersonData() throws Exception {
        assertNonMutatingCommandBehavior(
                "add []\\[;] p/12345 e/valid@e.mail a/valid, address", Name.MESSAGE_NAME_CONSTRAINTS);
        assertNonMutatingCommandBehavior(
                "add Valid Name p/not_numbers e/valid@e.mail a/valid, address", Phone.MESSAGE_PHONE_CONSTRAINTS);
        assertNonMutatingCommandBehavior(
                "add Valid Name p/12345 e/notAnEmail a/valid, address", Email.MESSAGE_EMAIL_CONSTRAINTS);
        assertNonMutatingCommandBehavior(
                "add Valid Name p/12345 e/valid@e.mail a/valid, address t/invalid_-[.tag", Tag.MESSAGE_TAG_CONSTRAINTS);

    }

    @Test
    public void execute_add_successful() throws Exception {
        // setup expectations
        Person toBeAdded = new TestDataHelper().adam();
        AddressBook expectedAB = new AddressBook();
        expectedAB.addPerson(toBeAdded);

        // execute command
        CommandResult r = logic.execute(generateAddCommand(toBeAdded));

        // verify result
        assertEquals(r.feedbackToUser, String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded));
        assertFalse(r.getRelevantPersons().isPresent());
        assertLogicObjectStateEquals(expectedAB, Collections.emptyList());
    }

    /** Generates the correct add command based on the person given */
    private String generateAddCommand(Person p) {
        StringJoiner cmd = new StringJoiner(" ");

        cmd.add("add");

        cmd.add(p.getName().toString());
        cmd.add((p.getPhone().isPrivate() ? "pp/" : "p/") + p.getPhone());
        cmd.add((p.getEmail().isPrivate() ? "pe/" : "e/") + p.getEmail());
        cmd.add((p.getAddress().isPrivate() ? "pa/" : "a/") + p.getAddress());

        UniqueTagList tags = p.getTags();
        for(Tag t: tags){
            cmd.add("t/" + t.tagName);
        }

        return cmd.toString();
    }

    @Test
    public void execute_addDuplicate_notAllowed() throws Exception {
        // setup expectations
        Person toBeAdded = new TestDataHelper().adam();
        AddressBook expectedAB = new AddressBook();
        expectedAB.addPerson(toBeAdded);

        // setup starting state and execute command
        addressBook.addPerson(toBeAdded); // person already in internal address book
        CommandResult r = logic.execute(generateAddCommand(toBeAdded));

        // verify result
        assertEquals(r.feedbackToUser, AddCommand.MESSAGE_DUPLICATE_PERSON);
        assertFalse(r.getRelevantPersons().isPresent());
        assertLogicObjectStateEquals(expectedAB, Collections.emptyList());
    }



    /**
     * Utility method for testing invalid argument index number behaviour for commands
     * targeting a single person in the last shown list, using visible index.
     * @param commandWord to test assuming it targets a single person in the last shown list based on visible index.
     */
    private void execute_lastShownListIndexCommand_invalidIndex(String commandWord) throws Exception {
        String expectedMessage = Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
        List<Person> lastShownList = new ArrayList<>();
        TestDataHelper helper = new TestDataHelper();
        lastShownList.add(helper.generatePerson(1, false));
        lastShownList.add(helper.generatePerson(2, true));

        logic.setLastShownList(lastShownList);
        CommandResult r;

        r = logic.execute(commandWord + " -1");
        assertEquals(r.feedbackToUser, expectedMessage);
        assertFalse(r.getRelevantPersons().isPresent());
        assertLogicObjectStateEquals(AddressBook.empty(), lastShownList);

        r = logic.execute(commandWord + " 0");
        assertEquals(r.feedbackToUser, expectedMessage);
        assertFalse(r.getRelevantPersons().isPresent());
        assertLogicObjectStateEquals(AddressBook.empty(), lastShownList);

        r = logic.execute(commandWord + " 3");
        assertEquals(r.feedbackToUser, expectedMessage);
        assertFalse(r.getRelevantPersons().isPresent());
        assertLogicObjectStateEquals(AddressBook.empty(), lastShownList);
    }

    @Test
    public void execute_list_showsAllPersons() throws Exception {
        // expectations
        AddressBook expectedAB = new AddressBook();
        TestDataHelper helper = new TestDataHelper();
        expectedAB.addPerson(helper.generatePerson(1, false));
        expectedAB.addPerson(helper.generatePerson(2, true));
        List<? extends ReadOnlyPerson> expectedList = expectedAB.getAllPersons().immutableListView();

        // prep test
        addressBook.addPerson(helper.generatePerson(1, false));
        addressBook.addPerson(helper.generatePerson(2, true));
        CommandResult r = logic.execute("list");

        // return verification
        assertEquals(r.feedbackToUser, Command.getMessageForPersonListShownSummary(expectedList));
        assertEquals(r.getRelevantPersons().isPresent(), true);
        assertEquals(r.getRelevantPersons().get(), expectedList);

        // SUT state verification
        assertLogicObjectStateEquals(expectedAB, expectedList);
    }

    @Test
    public void execute_view_invalidArgsFormat() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE);
        assertNonMutatingCommandBehavior("view ", expectedMessage);
        assertNonMutatingCommandBehavior("view arg not number", expectedMessage);
    }

    @Test
    public void execute_view_invalidIndex() throws Exception {
        execute_lastShownListIndexCommand_invalidIndex("view");
    }

    @Test
    public void execute_view_onlyShowsNonPrivate() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Person p1 = helper.generatePerson(1, false);
        Person p2 = helper.generatePerson(2, false);
        List<Person> lastShownList = new ArrayList<>();
        lastShownList.add(p1);
        lastShownList.add(p2);
        AddressBook expectedAB = new AddressBook();
        expectedAB.addPerson(p1);
        expectedAB.addPerson(p2);

        addressBook.addPerson(p1);
        addressBook.addPerson(p2);
        logic.setLastShownList(lastShownList);
        CommandResult r;

        r = logic.execute("view 1");
        assertEquals(r.feedbackToUser, String.format(ViewCommand.MESSAGE_VIEW_PERSON_DETAILS, p1.getAsTextHidePrivate()));
        assertFalse(r.getRelevantPersons().isPresent());
        assertLogicObjectStateEquals(expectedAB, lastShownList);

        r = logic.execute("view 2");
        assertEquals(r.feedbackToUser, String.format(ViewCommand.MESSAGE_VIEW_PERSON_DETAILS, p2.getAsTextHidePrivate()));
        assertFalse(r.getRelevantPersons().isPresent());
        assertLogicObjectStateEquals(expectedAB, lastShownList);
    }

    @Test
    public void execute_view_missingInAddressBook() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Person p1 = helper.generatePerson(1, false);
        Person p2 = helper.generatePerson(2, false);
        List<Person> lastShownList = new ArrayList<>();
        lastShownList.add(p1);
        lastShownList.add(p2);

        AddressBook expectedAB = new AddressBook();
        expectedAB.addPerson(p2);

        addressBook.addPerson(p2);
        logic.setLastShownList(lastShownList);
        CommandResult r = logic.execute("view 1");

        assertEquals(r.feedbackToUser, Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK);
        assertFalse(r.getRelevantPersons().isPresent());
        assertLogicObjectStateEquals(expectedAB, lastShownList);
    }

    @Test
    public void execute_viewAll_invalidArgsFormat() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewAllCommand.MESSAGE_USAGE);
        assertNonMutatingCommandBehavior("viewall ", expectedMessage);
        assertNonMutatingCommandBehavior("viewall arg not number", expectedMessage);
    }

    @Test
    public void execute_viewAll_invalidIndex() throws Exception {
        execute_lastShownListIndexCommand_invalidIndex("viewall");
    }

    @Test
    public void execute_view_alsoShowsPrivate() throws Exception {
        List<Person> lastShownList = new ArrayList<>();
        TestDataHelper helper = new TestDataHelper();
        Person p1 = helper.generatePerson(1, false);
        Person p2 = helper.generatePerson(2, false);
        lastShownList.add(p1);
        lastShownList.add(p2);
        AddressBook expectedAB = new AddressBook();
        expectedAB.addPerson(p1);
        expectedAB.addPerson(p2);

        addressBook.addPerson(p1);
        addressBook.addPerson(p2);
        logic.setLastShownList(lastShownList);
        CommandResult r;

        r = logic.execute("view 1");
        assertEquals(r.feedbackToUser, String.format(ViewAllCommand.MESSAGE_VIEW_PERSON_DETAILS, p1.getAsTextShowAll()));
        assertFalse(r.getRelevantPersons().isPresent());
        assertLogicObjectStateEquals(expectedAB, lastShownList);

        r = logic.execute("view 2");
        assertEquals(r.feedbackToUser, String.format(ViewAllCommand.MESSAGE_VIEW_PERSON_DETAILS, p2.getAsTextShowAll()));
        assertFalse(r.getRelevantPersons().isPresent());
        assertLogicObjectStateEquals(expectedAB, lastShownList);
    }

    @Test
    public void execute_viewAll_missingInAddressBook() throws Exception {
        List<Person> lastShownList = new ArrayList<>();
        TestDataHelper helper = new TestDataHelper();
        Person p1 = helper.generatePerson(1, false);
        Person p2 = helper.generatePerson(2, false);
        lastShownList.add(p1);
        lastShownList.add(p2);

        AddressBook expectedAB = new AddressBook();
        expectedAB.addPerson(p1);

        addressBook.addPerson(p1);
        logic.setLastShownList(lastShownList);
        CommandResult r = logic.execute("viewall 2");

        assertEquals(r.feedbackToUser, Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK);
        assertFalse(r.getRelevantPersons().isPresent());
        assertLogicObjectStateEquals(expectedAB, lastShownList);
    }

    @Test
    public void execute_delete_invalidArgsFormat() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);
        assertNonMutatingCommandBehavior("delete ", expectedMessage);
        assertNonMutatingCommandBehavior("delete arg not number", expectedMessage);
    }

    @Test
    public void execute_delete_invalidIndex() throws Exception {
        execute_lastShownListIndexCommand_invalidIndex("delete");
    }

    @Test
    public void execute_delete_removesCorrectPerson() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Person p1 = helper.generatePerson(1, false);
        Person p2 = helper.generatePerson(2, true);
        Person p3 = helper.generatePerson(3, true);

        AddressBook expectedAB = new AddressBook();
        expectedAB.addPerson(p1);
        expectedAB.addPerson(p2);
        expectedAB.addPerson(p3);
        expectedAB.removePerson(p2);

        List<Person> lastShownList = new ArrayList<>();
        lastShownList.add(p1);
        lastShownList.add(p2);
        lastShownList.add(p3);

        addressBook.addPerson(p1);
        addressBook.addPerson(p2);
        addressBook.addPerson(p3);
        logic.setLastShownList(lastShownList);
        CommandResult r = logic.execute("delete 2");

        assertEquals(r.feedbackToUser, String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, p2));
        assertFalse(r.getRelevantPersons().isPresent());
        assertLogicObjectStateEquals(expectedAB, lastShownList);
    }

    @Test
    public void execute_delete_missingInAddressBook() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Person p1 = helper.generatePerson(1, false);
        Person p2 = helper.generatePerson(2, true);
        Person p3 = helper.generatePerson(3, true);

        AddressBook expectedAB = new AddressBook();
        expectedAB.addPerson(p1);
        expectedAB.addPerson(p2);
        expectedAB.addPerson(p3);
        expectedAB.removePerson(p2);

        List<Person> lastShownList = new ArrayList<>();
        lastShownList.add(p1);
        lastShownList.add(p2);
        lastShownList.add(p3);

        addressBook.addPerson(p1);
        addressBook.addPerson(p2);
        addressBook.addPerson(p3);
        addressBook.removePerson(p2);
        logic.setLastShownList(lastShownList);
        CommandResult r = logic.execute("delete 2");

        System.out.println(expectedAB.equals(addressBook));

        assertEquals(r.feedbackToUser, Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK);
        assertFalse(r.getRelevantPersons().isPresent());
        assertLogicObjectStateEquals(expectedAB, lastShownList);
    }

    /**
     * Generates a Person object with given name. Other fields will have some dummy values.
     */
    private Person generatePersonWithName(String name) throws Exception {
        return new Person(
                new Name(name),
                new Phone("1", false),
                new Email("1@email", false),
                new Address("House of 1", false),
                new UniqueTagList(new Tag("tag"))
        );
    }
    
    @Test
    public void execute_find_invalidArgsFormat() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        assertNonMutatingCommandBehavior("find ", expectedMessage);
    }

    @Test
    public void execute_find_onlyMatchesFullWordsInNames() throws Exception {
        Person pTarget1 = generatePersonWithName("bla bla KEY bla");
        Person pTarget2 = generatePersonWithName("bla KEY bla bceofeia");
        Person p1 = generatePersonWithName("KE Y");
        Person p2 = generatePersonWithName("KEYKEYKEY sduauo");
        
        AddressBook expectedAB = new AddressBook();
        expectedAB.addPerson(p1);
        expectedAB.addPerson(pTarget1);
        expectedAB.addPerson(p2);
        expectedAB.addPerson(pTarget2);
        
        List<Person> expectedList = new ArrayList<>();
        expectedList.addAll(Arrays.asList(pTarget1, pTarget2));

        addressBook.addPerson(p1);
        addressBook.addPerson(pTarget1);
        addressBook.addPerson(p2);
        addressBook.addPerson(pTarget2);
        CommandResult r = logic.execute("find KEY");
        
        assertEquals(r.feedbackToUser, Command.getMessageForPersonListShownSummary(expectedList));
        assertEquals(r.getRelevantPersons().isPresent(), true);
        assertEquals(r.getRelevantPersons().get(), expectedList);
        assertLogicObjectStateEquals(expectedAB, expectedList);
    }

    @Test
    public void execute_find_isCaseSensitive() throws Exception {
        Person pTarget1 = generatePersonWithName("bla bla KEY bla");
        Person pTarget2 = generatePersonWithName("bla KEY bla bceofeia");
        Person p1 = generatePersonWithName("key key");
        Person p2 = generatePersonWithName("KEy sduauo");

        AddressBook expectedAB = new AddressBook();
        expectedAB.addPerson(p1);
        expectedAB.addPerson(pTarget1);
        expectedAB.addPerson(p2);
        expectedAB.addPerson(pTarget2);

        List<Person> expectedList = new ArrayList<>();
        expectedList.addAll(Arrays.asList(pTarget1, pTarget2));

        addressBook.addPerson(p1);
        addressBook.addPerson(pTarget1);
        addressBook.addPerson(p2);
        addressBook.addPerson(pTarget2);
        CommandResult r = logic.execute("find KEY");

        assertEquals(r.feedbackToUser, Command.getMessageForPersonListShownSummary(expectedList));
        assertEquals(r.getRelevantPersons().isPresent(), true);
        assertEquals(r.getRelevantPersons().get(), expectedList);
        assertLogicObjectStateEquals(expectedAB, expectedList);
    }

    @Test
    public void execute_find_matchesIfAnyKeywordPresent() throws Exception {
        Person pTarget1 = generatePersonWithName("bla bla KEY bla");
        Person pTarget2 = generatePersonWithName("bla rAnDoM bla bceofeia");
        Person p1 = generatePersonWithName("key key");
        Person p2 = generatePersonWithName("KEy sduauo");

        AddressBook expectedAB = new AddressBook();
        expectedAB.addPerson(p1);
        expectedAB.addPerson(pTarget1);
        expectedAB.addPerson(p2);
        expectedAB.addPerson(pTarget2);

        List<Person> expectedList = new ArrayList<>();
        expectedList.addAll(Arrays.asList(pTarget1, pTarget2));

        addressBook.addPerson(p1);
        addressBook.addPerson(pTarget1);
        addressBook.addPerson(p2);
        addressBook.addPerson(pTarget2);
        CommandResult r = logic.execute("find KEY rAnDoM");

        assertEquals(r.feedbackToUser, Command.getMessageForPersonListShownSummary(expectedList));
        assertEquals(r.getRelevantPersons().isPresent(), true);
        assertEquals(r.getRelevantPersons().get(), expectedList);
        assertLogicObjectStateEquals(expectedAB, expectedList);
    }

    /**
     * A utility class to generate test data.
     */
    class TestDataHelper{

        Person adam() throws Exception {
            Name name = new Name("Adam Brown");
            Phone privatePhone = new Phone("111111", true);
            Email email = new Email("adam@gmail.com", false);
            Address privateAddress = new Address("111, alpha street", true);
            Tag tag1 = new Tag("tag1");
            Tag tag2 = new Tag("tag2");
            UniqueTagList tags = new UniqueTagList(tag1, tag2);
            return new Person(name, privatePhone, email, privateAddress, tags);
        }

        /**
         * Generates a valid person using the given seed.
         * Running this function with the same parameter values guarantees the returned person will have the same state.
         *
         * @param seed used to generate the person data field values
         * @param isAllFieldsPrivate determines if private-able fields (phone, email, address) will be private
         */
        Person generatePerson(int seed, boolean isAllFieldsPrivate) throws Exception {
            return new Person(
                    new Name("Person " + seed),
                    new Phone("" + Math.abs(seed), isAllFieldsPrivate),
                    new Email(seed + "@email", isAllFieldsPrivate),
                    new Address("House of " + seed, isAllFieldsPrivate),
                    new UniqueTagList(new Tag("tag" + Math.abs(seed)), new Tag("tag" + Math.abs(seed + 1)))
            );
        }
    }

}
