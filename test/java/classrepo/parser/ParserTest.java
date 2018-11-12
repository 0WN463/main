package classrepo.parser;

import static classrepo.commands.exams.EditExamCommand.MESSAGE_NO_ARGS_FOUND;
import static classrepo.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static classrepo.common.Messages.MESSAGE_WRONG_NUMBER_ARGUMENTS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import classrepo.TestDataHelper;
import classrepo.commands.Command;
import classrepo.commands.IncorrectCommand;
import classrepo.commands.assessment.AddAssessmentCommand;
import classrepo.commands.assessment.AddGradesCommand;
import classrepo.commands.assessment.DeleteAssessmentCommand;
import classrepo.commands.assessment.DeleteGradesCommand;
import classrepo.commands.assessment.ListAssessmentCommand;
import classrepo.commands.assessment.ViewGradesCommand;
import classrepo.commands.commandformat.indexformat.ObjectTargeted;
import classrepo.commands.exams.AddExamCommand;
import classrepo.commands.exams.EditExamCommand;
import classrepo.commands.exams.ListExamsCommand;
import classrepo.commands.general.ExitCommand;
import classrepo.commands.general.HelpCommand;
import classrepo.commands.person.AddCommand;
import classrepo.commands.person.ClearCommand;
import classrepo.commands.person.DeleteCommand;
import classrepo.commands.person.FindCommand;
import classrepo.commands.person.ListCommand;
import classrepo.commands.person.ViewAllCommand;
import classrepo.commands.person.ViewCommand;
import classrepo.commands.privilege.RaisePrivilegeCommand;
import classrepo.data.exception.IllegalValueException;
import classrepo.data.person.Assessment;
import classrepo.data.person.Exam;
import classrepo.data.person.Person;
import classrepo.data.person.ReadOnlyPerson;
import classrepo.data.person.details.Address;
import classrepo.data.person.details.Email;
import classrepo.data.person.details.Name;
import classrepo.data.person.details.Phone;
import classrepo.data.tag.Tag;


public class ParserTest {

    private Parser parser;

    @Before
    public void setUp() {
        parser = new Parser();
    }

    @Test
    public void emptyInput_returnsIncorrect() {
        final String[] emptyInputs = { "", "  ", "\n  \n" };
        final String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, emptyInputs);
    }

    @Test
    public void unknownCommandWord_returnsHelp() {
        final String input = "unknowncommandword arguments arguments";
        parseAndAssertCommandType(input, HelpCommand.class);
    }

    /**
     * Test 0-argument commands
     */
    @Test
    public void helpCommand_parsedCorrectly() {
        final String input = "help";
        parseAndAssertCommandType(input, HelpCommand.class);
    }

    @Test
    public void clearCommand_parsedCorrectly() {
        final String input = "clear";
        parseAndAssertCommandType(input, ClearCommand.class);
    }

    @Test
    public void listCommand_parsedCorrectly() {
        final String input = "list";
        parseAndAssertCommandType(input, ListCommand.class);
    }

    @Test
    public void exitCommand_parsedCorrectly() {
        final String input = "exit";
        parseAndAssertCommandType(input, ExitCommand.class);
    }

    /**
     * Test single index argument commands
     */
    @Test
    public void deleteCommand_noArgs() {
        final String[] inputs = { "delete", "delete " };
        final String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    @Test
    public void deleteCommand_argsIsNotSingleNumber() {
        final String[] inputs = { "delete notAnumber ", "delete 8*wh12", "delete 1 2 3 4 5" };
        final String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    @Test
    public void deleteCommand_numericArg_indexParsedCorrectly() {
        final int testIndex = 1;
        final String input = "delete " + testIndex;
        final DeleteCommand result = parseAndAssertCommandType(input, DeleteCommand.class);
        assertEquals(result.getTargetIndex(ObjectTargeted.PERSON), testIndex);
    }

    @Test
    public void viewCommand_noArgs() {
        final String[] inputs = { "view", "view " };
        final String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    @Test
    public void viewCommand_argsIsNotSingleNumber() {
        final String[] inputs = { "view notAnumber ", "view 8*wh12", "view 1 2 3 4 5" };
        final String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    @Test
    public void viewCommand_numericArg_indexParsedCorrectly() {
        final int testIndex = 2;
        final String input = "view " + testIndex;
        final ViewCommand result = parseAndAssertCommandType(input, ViewCommand.class);
        assertEquals(result.getTargetIndex(ObjectTargeted.PERSON), testIndex);
    }

    @Test
    public void viewAllCommand_noArgs() {
        final String[] inputs = { "viewall", "viewall " };
        final String resultMessage =
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewAllCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    @Test
    public void viewAllCommand_argsIsNotSingleNumber() {
        final String[] inputs = { "viewall notAnumber ", "viewall 8*wh12", "viewall 1 2 3 4 5" };
        final String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewAllCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    @Test
    public void viewAllCommand_numericArg_indexParsedCorrectly() {
        final int testIndex = 3;
        final String input = "viewall " + testIndex;
        final ViewAllCommand result = parseAndAssertCommandType(input, ViewAllCommand.class);
        assertEquals(result.getTargetIndex(ObjectTargeted.PERSON), testIndex);
    }

    @Test
    public void viewGradesCommand_noArgs() {
        final String[] inputs = { "viewgrades", "viewgrades " };
        final String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewGradesCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    @Test
    public void viewGradesCommand_argsIsNotSingleNumber() {
        final String[] inputs = { "viewgrades notAnumber ", "viewgrades 8*wh12", "viewgrades 1 2 3 4 5" };
        final String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewGradesCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    @Test
    public void viewGradesCommand_numericArg_indexParsedCorrectly() {
        final int testIndex = 2;
        final String input = "viewgrades " + testIndex;
        final ViewGradesCommand result = parseAndAssertCommandType(input, ViewGradesCommand.class);
        assertEquals(result.getTargetIndex(ObjectTargeted.PERSON), testIndex);
    }

    /**
     * Test add assessment command
     */
    @Test
    public void addGradesCommand_invalidArgs() {
        final String[] inputs = {"addgrades", "addgrades ", "addgrades wrong args format"};
        final String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddGradesCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    /**
     * Test add assessment command
     */
    @Test
    public void addGradesCommand_invalidNumberOfArgs() {
        final String[] inputs = {
                //Missing grades value
                String.format("addgrades 1 2"),
        };
        final String resultMessage = String.format(MESSAGE_WRONG_NUMBER_ARGUMENTS, 3, 2,
                AddGradesCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    /**
     * Test double index argument commands
     */
    @Test
    public void deleteGradesCommand_noArgs() {
        final String[] inputs = { "deletegrades", "deletegrades " };
        final String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteGradesCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    @Test
    public void deleteGradesCommand_argsIsNotDoubleNumber() {
        final String[] inputs = { "deletegrades notAnumber ", "deletegrades 8*wh12", "deletegrades 1" };
        final String resultMessage = String.format(MESSAGE_WRONG_NUMBER_ARGUMENTS, 2, 1,
                DeleteGradesCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    /**
     * Test find persons by keyword in name command
     */
    @Test
    public void findCommand_invalidArgs() {
        // no keywords
        final String[] inputs = {
            "find",
            "find "
        };
        final String resultMessage =
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    @Test
    public void findCommand_validArgs_parsedCorrectly() {
        final String[] keywords = { "key1", "key2", "key3" };
        final Set<String> keySet = new HashSet<>(Arrays.asList(keywords));

        final String input = "find " + String.join(" ", keySet);
        final FindCommand result =
                parseAndAssertCommandType(input, FindCommand.class);
        assertEquals(keySet, result.getKeywords());
    }

    @Test
    public void findCommand_duplicateKeys_parsedCorrectly() {
        final String[] keywords = { "key1", "key2", "key3" };
        final Set<String> keySet = new HashSet<>(Arrays.asList(keywords));

        // duplicate every keyword
        final String input = "find " + String.join(" ", keySet) + " " + String.join(" ", keySet);
        final FindCommand result =
                parseAndAssertCommandType(input, FindCommand.class);
        assertEquals(keySet, result.getKeywords());
    }

    /**
     * Test add person command
     */
    @Test
    public void addCommand_invalidArgs() {
        final String[] inputs = {
            "add",
            "add ",
            "add wrong args format",
            // no phone prefix
            String.format("add %s %s e/%s a/%s", Name.EXAMPLE, Phone.EXAMPLE, Email.EXAMPLE, Address.EXAMPLE),
            // no email prefix
            String.format("add %s p/%s %s a/%s", Name.EXAMPLE, Phone.EXAMPLE, Email.EXAMPLE, Address.EXAMPLE),
            // no address prefix
            String.format("add %s p/%s e/%s %s", Name.EXAMPLE, Phone.EXAMPLE, Email.EXAMPLE, Address.EXAMPLE)
        };
        final String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    @Test
    public void addCommand_invalidPersonDataInArgs() {
        final String invalidName = "[]\\[;]";
        final String validName = Name.EXAMPLE;
        final String invalidPhoneArg = "p/not__numbers";
        final String validPhoneArg = "p/" + Phone.EXAMPLE;
        final String invalidEmailArg = "e/notAnEmail123";
        final String validEmailArg = "e/" + Email.EXAMPLE;
        final String invalidTagArg = "t/invalid_-[.tag";

        // address can be any string, so no invalid address
        final String addCommandFormatString = "add %s %s %s a/" + Address.EXAMPLE;

        // test each incorrect person data field argument individually
        final String[] inputs = {
                // invalid name
                String.format(addCommandFormatString, invalidName, validPhoneArg, validEmailArg),
                // invalid phone
                String.format(addCommandFormatString, validName, invalidPhoneArg, validEmailArg),
                // invalid email
                String.format(addCommandFormatString, validName, validPhoneArg, invalidEmailArg),
                // invalid tag
                String.format(addCommandFormatString, validName, validPhoneArg, validEmailArg) + " " + invalidTagArg
        };
        for (String input : inputs) {
            parseAndAssertCommandType(input, IncorrectCommand.class);
        }
    }

    @Test
    public void addCommand_validPersonData_parsedCorrectly() {
        try {
            final Person testPerson = generateTestPerson();
            final String input = convertPersonToAddCommandString(testPerson);
            final AddCommand result = parseAndAssertCommandType(input, AddCommand.class);
            assertEquals(result.getPerson(), testPerson);
        } catch (IllegalValueException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addCommand_duplicateTags_merged() throws IllegalValueException {
        final Person testPerson = generateTestPerson();
        StringBuilder input = new StringBuilder(convertPersonToAddCommandString(testPerson));
        for (Tag tag : testPerson.getTags()) {
            // create duplicates by doubling each tag
            input.append(" t/").append(tag.tagName);
        }

        final AddCommand result = parseAndAssertCommandType(input.toString(), AddCommand.class);
        assertEquals(result.getPerson(), testPerson);
    }

    @Test
    public void raisePrivilegeCommand_invalidArgs() {
        // no keywords
        final String[] inputs = {
            "raise",
            "raise "
        };
        final String resultMessage =
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, RaisePrivilegeCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    /**
     * Test add exam command
     */
    @Test
    public void addExamCommand_invalidArgs() {
        final String[] inputs = { "addexam", "addexam ", "addexam wrong args format",
                // no subject name prefix
                String.format("addexam %s %s d/%s st/%s et/%s dt/%s", Exam.EXAM_NAME_EXAMPLE,
                        Exam.SUBJECT_NAME_EXAMPLE, Exam.EXAM_DATE_EXAMPLE, Exam.EXAM_START_TIME_EXAMPLE,
                        Exam.EXAM_END_TIME_EXAMPLE, Exam.EXAM_DETAILS_EXAMPLE),
                // no date prefix
                String.format("addexam %s s/%s %s st/%s et/%s dt/%s", Exam.EXAM_NAME_EXAMPLE,
                        Exam.SUBJECT_NAME_EXAMPLE, Exam.EXAM_DATE_EXAMPLE, Exam.EXAM_START_TIME_EXAMPLE,
                        Exam.EXAM_END_TIME_EXAMPLE, Exam.EXAM_DETAILS_EXAMPLE),
                // no start time prefix
                String.format("addexam %s s/%s d/%s %s et/%s dt/%s", Exam.EXAM_NAME_EXAMPLE,
                        Exam.SUBJECT_NAME_EXAMPLE, Exam.EXAM_DATE_EXAMPLE, Exam.EXAM_START_TIME_EXAMPLE,
                        Exam.EXAM_END_TIME_EXAMPLE, Exam.EXAM_DETAILS_EXAMPLE),
                // no end time prefix
                String.format("addexam %s s/%s d/%s st/%s %s dt/%s", Exam.EXAM_NAME_EXAMPLE,
                        Exam.SUBJECT_NAME_EXAMPLE, Exam.EXAM_DATE_EXAMPLE, Exam.EXAM_START_TIME_EXAMPLE,
                        Exam.EXAM_END_TIME_EXAMPLE, Exam.EXAM_DETAILS_EXAMPLE),
                // no details prefix
                String.format("addexam %s s/%s d/%s st/%s et/%s %s", Exam.EXAM_NAME_EXAMPLE,
                        Exam.SUBJECT_NAME_EXAMPLE, Exam.EXAM_DATE_EXAMPLE, Exam.EXAM_START_TIME_EXAMPLE,
                        Exam.EXAM_END_TIME_EXAMPLE, Exam.EXAM_DETAILS_EXAMPLE),
        };
        final String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddExamCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    @Test
    public void addExamCommand_invalidExamDataInArgs() {
        final String invalidDateArg = "d/not__numbers";
        final String validDateArg = "d/" + Exam.EXAM_DATE_EXAMPLE;
        final String invalidStartTimeArg = "st/not__numbers";
        final String validStartTimeArg = "st/" + Exam.EXAM_START_TIME_EXAMPLE;
        final String invalidEndTimeArg = "et/not__numbers";
        final String validEndTimeArg = "et/" + Exam.EXAM_END_TIME_EXAMPLE;
        final String invalidEndTimeIntervalArg = "et/08:00";


        // subject name and details can be any string, so no invalid address
        final String addExamCommandFormatString = "addexam " + Exam.EXAM_NAME_EXAMPLE
                + " s/" + Exam.SUBJECT_NAME_EXAMPLE
                + " %s %s %s dt/" + Exam.EXAM_DETAILS_EXAMPLE;

        // test each incorrect person data field argument individually
        final String[] inputs = {
                // invalid date
                String.format(addExamCommandFormatString, invalidDateArg, validStartTimeArg, validEndTimeArg),
                // invalid start time
                String.format(addExamCommandFormatString, validDateArg, invalidStartTimeArg, validEndTimeArg),
                // invalid end time
                String.format(addExamCommandFormatString, validDateArg, validStartTimeArg, invalidEndTimeArg),
                // invalid time interval
                String.format(addExamCommandFormatString, validDateArg, validStartTimeArg,
                        invalidEndTimeIntervalArg),
        };
        for (String input : inputs) {
            parseAndAssertCommandType(input, IncorrectCommand.class);
        }
    }

    @Test
    public void addExamCommand_validExamData_parsedCorrectly() {
        try {
            final Exam testExam = generateTestExam();
            final String input = convertExamToAddExamCommandString(testExam);
            final AddExamCommand result = parseAndAssertCommandType(input, AddExamCommand.class);
            assertEquals(result.getExam(), testExam);
        } catch (IllegalValueException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addAssessmentCommand_validAssessmentData_parsedCorrectly() {
        try {
            final Assessment testAssessment = generateTestAssessment();
            final String input = convertAssessmentToAddAssessmentCommandString(testAssessment);
            final AddAssessmentCommand result = parseAndAssertCommandType(input, AddAssessmentCommand.class);
            assertEquals(result.getAssessment(), testAssessment);
        } catch (IllegalValueException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test add assessment command
     */
    @Test
    public void addAssessmentCommand_invalidArgs() {
        final String[] inputs = {
            "addassess",
            "addassess ",
        };
        final String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddAssessmentCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    @Test
    public void listAssessmentCommand_parsedCorrectly() {
        final String input = "listassess";
        parseAndAssertCommandType(input, ListAssessmentCommand.class);
    }

    /**
     * Test single index argument commands
     */
    @Test
    public void deleteAssessmentCommand_noArgs() {
        final String[] inputs = { "deleteassess", "deleteassess " };
        final String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                DeleteAssessmentCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    @Test
    public void deleteAssessmentCommand_argsIsNotSingleNumber() {
        final String[] inputs = { "deleteassess notAnumber ", "deleteassess 8*wh12", "deleteassess 1 2 3 4 5" };
        final String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                DeleteAssessmentCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    @Test
    public void deleteAssessmentCommand_numericArg_indexParsedCorrectly() {
        final int testIndex = -1;
        final String input = "deleteassess " + testIndex;
        final DeleteAssessmentCommand result = parseAndAssertCommandType(input, DeleteAssessmentCommand.class);
        assertEquals(result.getTargetIndex(ObjectTargeted.ASSESSMENT), testIndex);
    }

    /**
     * Test edit exam command
     */
    @Test
    public void editExamCommand_invalidArgs() {
        final String[] inputs = { "editexam", "editexam ",
                //no index
                String.format("editexam e/%s s/%s d/%s st/%s et/%s dt/%s", Exam.EXAM_NAME_EXAMPLE,
                        Exam.SUBJECT_NAME_EXAMPLE, Exam.EXAM_DATE_EXAMPLE, Exam.EXAM_START_TIME_EXAMPLE,
                        Exam.EXAM_END_TIME_EXAMPLE, Exam.EXAM_DETAILS_EXAMPLE),
                // no exam name prefix
                String.format("editexam 1 %s s/%s d/%s st/%s et/%s dt/%s", Exam.EXAM_NAME_EXAMPLE,
                        Exam.SUBJECT_NAME_EXAMPLE, Exam.EXAM_DATE_EXAMPLE, Exam.EXAM_START_TIME_EXAMPLE,
                        Exam.EXAM_END_TIME_EXAMPLE, Exam.EXAM_DETAILS_EXAMPLE)
        };
        String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditExamCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);

        String input = "editexam 1";
        resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_NO_ARGS_FOUND
                + EditExamCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, input);
    }

    @Test
    public void editExamCommand_indexIsNotSingleNumber() {
        final String[] inputs = { "editexam notAnumber s/Mathematics st/09:00 et/11:00 dt/Held in MPSH",
            "editexam 8*wh12 s/Mathematics",
            "editexam 1 2 3 4 5 s/Mathematics" };
        final String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditExamCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    @Test
    public void editExamCommand_numericIndex_indexParsedCorrectly() {
        final int testIndex = 1;
        final String input = String.format("editexam %d e/%s s/%s d/%s st/%s et/%s dt/%s",
                testIndex, Exam.EXAM_NAME_EXAMPLE,
                Exam.SUBJECT_NAME_EXAMPLE, Exam.EXAM_DATE_EXAMPLE, Exam.EXAM_START_TIME_EXAMPLE,
                Exam.EXAM_END_TIME_EXAMPLE, Exam.EXAM_DETAILS_EXAMPLE);
        final EditExamCommand result = parseAndAssertCommandType(input, EditExamCommand.class);
        assertEquals(result.getTargetIndex(ObjectTargeted.EXAM), testIndex);
    }

    @Test
    public void examsListCommand_parsedCorrectly() {
        final String input = "listexams";
        parseAndAssertCommandType(input, ListExamsCommand.class);
    }

    /** **/
    private static Person generateTestPerson() throws IllegalValueException {
        try {
            return new Person(
                new Name(Name.EXAMPLE),
                new Phone(Phone.EXAMPLE, true),
                new Email(Email.EXAMPLE, false),
                new Address(Address.EXAMPLE, true),
                new HashSet<>(Arrays.asList(new Tag("tag1"), new Tag("tag2"), new Tag("tag3")))
            );
        } catch (IllegalValueException ive) {
            throw new IllegalValueException("test person data should be valid by definition");
        }
    }

    /** **/
    private static String convertPersonToAddCommandString(ReadOnlyPerson person) {
        TestDataHelper helper = new TestDataHelper();
        String phoneField = helper.getPrefix(person.getPhone()) + person.getPhone();
        String emailField = helper.getPrefix(person.getEmail()) + person.getEmail();
        String addressField = helper.getPrefix(person.getAddress()) + person.getAddress();

        StringBuilder addCommand =
                new StringBuilder("add " + person.getName().fullName + phoneField + emailField + addressField);
        for (Tag tag : person.getTags()) {
            addCommand.append(" t/").append(tag.tagName);
        }
        return addCommand.toString();
    }

    /** **/
    private static Exam generateTestExam() throws IllegalValueException {
        try {
            return new Exam(Exam.EXAM_NAME_EXAMPLE,
                    Exam.SUBJECT_NAME_EXAMPLE,
                    Exam.EXAM_DATE_EXAMPLE,
                    Exam.EXAM_START_TIME_EXAMPLE,
                    Exam.EXAM_END_TIME_EXAMPLE,
                    Exam.EXAM_DETAILS_EXAMPLE,
                    false
            );
        } catch (IllegalValueException ive) {
            throw new IllegalValueException("test exam data should be valid by definition");
        }
    }

    /** **/
    private static Assessment generateTestAssessment() throws IllegalValueException {
        try {
            return new Assessment(Assessment.EXAM_NAME_EXAMPLE
            );
        } catch (IllegalValueException ive) {
            throw new IllegalValueException("test assessment data should be valid by definition");
        }
    }

    /** **/
    private static String convertExamToAddExamCommandString(Exam exam) {
        TestDataHelper helper = new TestDataHelper();
        String examNameField = helper.getExamNamePrefix(exam.isPrivate()) + exam.getExamName();
        String subjectNameField = helper.getSubjectNamePrefix() + exam.getSubjectName();
        String dateField = helper.getDatePrefix() + exam.getExamDate();
        String startTimeField = helper.getStartTimePrefix()
                + exam.getExamStartTime();
        String endTimeField = helper.getEndTimePrefix()
                + exam.getExamEndTime();
        String detailsField = helper.getDetailsPrefix() + exam.getExamDetails();

        String addExamCommand = "addexam " + examNameField
                + subjectNameField + dateField + startTimeField
                + endTimeField + detailsField;
        return addExamCommand;
    }

    /** **/
    private static String convertAssessmentToAddAssessmentCommandString(Assessment assessment) {
        String examNameField = assessment.getExamName();

        String addAssessmentCommand = "addassess " + examNameField;
        return addAssessmentCommand;
    }

    /**
     * Utility methods
     */

    /**
     * Asserts that parsing the given inputs will return IncorrectCommand with the given feedback message.
     */
    private void parseAndAssertIncorrectWithMessage(String feedbackMessage, String... inputs) {
        for (String input : inputs) {
            final IncorrectCommand result = parseAndAssertCommandType(input, IncorrectCommand.class);

            assertEquals(result.feedbackToUser, feedbackMessage);
        }
    }

    /**
     * Utility method for parsing input and asserting the class/type of the returned command object.
     *
     * @param input to be parsed
     * @param expectedCommandClass expected class of returned command
     * @return the parsed command object
     */
    private <T extends Command> T parseAndAssertCommandType(String input, Class<T> expectedCommandClass) {
        final Command result = parser.parseCommand(input);
        assertTrue(result.getClass().isAssignableFrom(expectedCommandClass));
        return (T) result;
    }
}
