package classrepo.logic;

import static classrepo.common.Messages.MESSAGE_INVALID_ASSESSMENT_DISPLAYED_INDEX;
import static classrepo.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static classrepo.common.Messages.MESSAGE_INVALID_GRADES;
import static classrepo.common.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static classrepo.common.Messages.MESSAGE_INVALID_STATISTICS_DISPLAYED_INDEX;
import static classrepo.common.Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK;
import static classrepo.common.Messages.MESSAGE_WRONG_NUMBER_ARGUMENTS;
import static classrepo.logic.CommandAssertions.assertCommandBehavior;
import static classrepo.logic.CommandAssertions.assertCommandBehaviorForExam;
import static classrepo.logic.CommandAssertions.assertInvalidIndexBehaviorForCommand;
import static classrepo.logic.CommandAssertions.setData;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import classrepo.TestDataHelper;
import classrepo.commands.Command;
import classrepo.commands.assessment.AddAssessmentCommand;
import classrepo.commands.assessment.AddAssignmentStatistics;
import classrepo.commands.assessment.AddGradesCommand;
import classrepo.commands.assessment.DeleteAssessmentCommand;
import classrepo.commands.assessment.DeleteGradesCommand;
import classrepo.commands.assessment.DeleteStatisticsCommand;
import classrepo.commands.assessment.ListAssessmentCommand;
import classrepo.commands.assessment.ListStatisticsCommand;
import classrepo.commands.assessment.ViewGradesCommand;
import classrepo.data.AddressBook;
import classrepo.data.ExamBook;
import classrepo.data.StatisticsBook;
import classrepo.data.person.Assessment;
import classrepo.data.person.AssignmentStatistics;
import classrepo.data.person.Grades;
import classrepo.data.person.Person;
import classrepo.data.person.ReadOnlyPerson;
import classrepo.privilege.Privilege;
import classrepo.privilege.user.AdminUser;
import classrepo.stubs.StorageStub;

/**
 * For testing of Assessment-related Commands
 */
public class AssessmentCommandsTest {

    /**
     *  See https://github.com/junit-team/junit4/wiki/rules#temporaryfolder-rule
     */
    @Rule
    public TemporaryFolder saveFolder = new TemporaryFolder();
    private AddressBook addressBook;
    private Logic logic;
    private StatisticsBook statisticsBook;

    @Before
    public void setUp() throws Exception {
        StorageStub stubFile = new StorageStub(saveFolder.newFile("testStubFile.txt").getPath(),
                saveFolder.newFile("testStubExamFile.txt").getPath(),
                saveFolder.newFile("testStubStatisticsFile.txt").getPath());

        addressBook = new AddressBook();
        ExamBook examBook = new ExamBook();
        statisticsBook = new StatisticsBook();
        // Privilege set to admin to allow all commands.
        // Privilege restrictions are tested separately under PrivilegeTest.
        Privilege privilege = new Privilege(new AdminUser());

        logic = new Logic(stubFile, addressBook, examBook, statisticsBook, privilege);
        setData(stubFile, addressBook, logic, examBook, statisticsBook);
    }

    @Test
    public void executeAddAssessment_validArgs_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        Assessment toBeAdded = helper.makeAssess();
        AddressBook expected = new AddressBook();
        expected.addAssessment(toBeAdded);
        List<? extends ReadOnlyPerson> dummyList = expected.getAllPersons().immutableListView();

        // execute command and verify result
        assertCommandBehavior(helper.generateAddAssessment(toBeAdded),
                String.format(AddAssessmentCommand.MESSAGE_SUCCESS, toBeAdded), expected, false,
                dummyList, true);
    }

    @Test
    public void executeAddAssessment_duplicateData_duplicateMessage() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        Assessment toBeAdded = helper.makeAssess();
        AddressBook expected = new AddressBook();
        expected.addAssessment(toBeAdded);
        List<? extends ReadOnlyPerson> dummyList = expected.getAllPersons().immutableListView();

        // setup starting state
        addressBook.addAssessment(toBeAdded); // assessment already in internal address book

        // execute command and verify result
        assertCommandBehavior(helper.generateAddAssessment(toBeAdded),
                AddAssessmentCommand.MESSAGE_DUPLICATE_ASSESSMENT, expected, false, dummyList, false);
    }

    @Test
    public void executeAddAssessment_invalidArgs_invalidMessage() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddAssessmentCommand.MESSAGE_USAGE);
        assertCommandBehavior("addassess", expectedMessage);
        assertCommandBehavior("addassess ", expectedMessage);
    }

    @Test
    public void executeDeleteAssessment_invalidArgs_invalidMessage() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteAssessmentCommand.MESSAGE_USAGE);
        assertCommandBehavior("deleteassess ", expectedMessage);
        assertCommandBehavior("deleteassess arg not number", expectedMessage);
    }

    @Test
    public void executeDeleteAssessment_invalidIndex_invalidIndexMessage() throws Exception {
        assertInvalidIndexBehaviorForCommand("deleteassess", MESSAGE_INVALID_ASSESSMENT_DISPLAYED_INDEX);
    }

    @Test
    public void executeListAssessments_validArgs_successfulList() throws Exception {
        // prepare expectations
        TestDataHelper helper = new TestDataHelper();
        AddressBook expected = helper.generateAddressBook(false, true);
        Assessment assessment = new Assessment("Math final");
        expected.addAssessment(assessment);
        addressBook.addAssessment(assessment);
        Assessment assessment2 = new Assessment("CG2271 Midterm");
        expected.addAssessment(assessment2);
        addressBook.addAssessment(assessment2);
        List<? extends Assessment> expectedList = List.of(assessment, assessment2);

        // prepare address book state
        helper.addToAddressBook(addressBook, false, true);

        assertCommandBehavior("listassess",
                Command.getMessageForAssessmentListShownSummary(expectedList),
                "",
                expected,
                false,
                Collections.emptyList(),
                true,
                expectedList,
                false);
    }

    @Test
    public void executeListAssessment_invalidArgs_invalidMessage() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListAssessmentCommand.MESSAGE_USAGE);
        assertCommandBehavior("listassess 1 2", expectedMessage);
        assertCommandBehavior("listassess any other arg", expectedMessage);
    }

    @Test
    public void executeAddGrades_validArgs_success() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Assessment a1 = helper.generateAssessment(1);
        List<Assessment> singleAssessment = helper.generateAssessmentsList(a1);
        addressBook.addAssessment(a1);
        logic.setLastShownAssessmentList(singleAssessment);

        Person p1 = helper.generatePerson(1, false);
        List<Person> personList = helper.generatePersonList(p1);
        addressBook.addPerson(p1);
        logic.setLastShownList(personList);

        Assessment a1Expected = helper.generateAssessment(1);
        List<Assessment> singleAssessmentExpected = helper.generateAssessmentsList(a1Expected);

        Person p1Expected = helper.generatePerson(1, false, 1, 100);
        List<Person> personListExpected = helper.generatePersonList(p1Expected);
        AddressBook expectedBook = helper.generateAddressBook(personListExpected);
        helper.addAssessmentsToAddressBook(expectedBook, singleAssessmentExpected);

        assertCommandBehavior("addgrades 1 1 100",
                String.format(AddGradesCommand.MESSAGE_ADD_GRADE_SUCCESS, p1Expected.getName(), a1Expected), "",
                expectedBook, false, logic.getLastShownList(), false,
                logic.getLastShownAssessmentList(), false);
    }

    @Test
    public void executeAddGrades_invalidParsedArgs_invalidCommandMessage() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddGradesCommand.MESSAGE_USAGE);

        assertCommandBehavior("addgrades 1 1 not_a_number", expectedMessage);
        assertCommandBehavior("addgrades 1 not_a_number 1", expectedMessage);
        assertCommandBehavior("addgrades 1 not_a_number not_a_number", expectedMessage);
        assertCommandBehavior("addgrades not_a_number 1 1", expectedMessage);
        assertCommandBehavior("addgrades not_a_number 1 not_a_number", expectedMessage);
        assertCommandBehavior("addgrades not_a_number not_a_number 1", expectedMessage);
        assertCommandBehavior("addgrades not_a_number not_a_number not_a_number", expectedMessage);
    }

    @Test
    public void executeAddGrades_noArgs_invalidCommandMessage() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddGradesCommand.MESSAGE_USAGE);
        assertCommandBehavior("addgrades", expectedMessage);
        assertCommandBehavior("addgrades ", expectedMessage);
    }

    @Test
    public void executeAddGrades_invalidPersonIndex_invalidIndexMessage() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Assessment a1 = helper.generateAssessment(1);
        List<Assessment> singleAssessment = helper.generateAssessmentsList(a1);
        addressBook.addAssessment(a1);
        logic.setLastShownAssessmentList(singleAssessment);

        Person p1 = helper.generatePerson(1, false);
        List<Person> personList = helper.generatePersonList(p1);
        logic.setLastShownList(personList);

        AddressBook expectedBook = new AddressBook();
        expectedBook.addAssessment(a1);

        assertCommandBehavior("addgrades 5 1 100",
                MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, "",
                expectedBook, false, logic.getLastShownList(), false,
                logic.getLastShownAssessmentList(), false);
    }

    @Test
    public void executeAddGrades_invalidAssessmentIndex_invalidAssessmentIndexMessage() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Assessment a1 = helper.generateAssessment(1);
        List<Assessment> singleAssessment = helper.generateAssessmentsList(a1);
        addressBook.addAssessment(a1);
        logic.setLastShownAssessmentList(singleAssessment);

        Person p1 = helper.generatePerson(1, false);
        List<Person> personList = helper.generatePersonList(p1);
        logic.setLastShownList(personList);
        addressBook.addPerson(p1);

        AddressBook expectedBook = new AddressBook();
        expectedBook.addAssessment(a1);
        expectedBook.addPerson(p1);

        assertCommandBehavior("addgrades 1 5 100",
                MESSAGE_INVALID_ASSESSMENT_DISPLAYED_INDEX, "",
                expectedBook, false, logic.getLastShownList(), false,
                logic.getLastShownAssessmentList(), false);
    }

    @Test
    public void executeAddGrades_invalidGrades_invalidGradesMessage() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Assessment a1 = helper.generateAssessment(1);
        List<Assessment> singleAssessment = helper.generateAssessmentsList(a1);
        addressBook.addAssessment(a1);
        logic.setLastShownAssessmentList(singleAssessment);

        Person p1 = helper.generatePerson(1, false);
        List<Person> personList = helper.generatePersonList(p1);
        logic.setLastShownList(personList);
        addressBook.addPerson(p1);

        AddressBook expectedBook = new AddressBook();
        expectedBook.addAssessment(a1);
        expectedBook.addPerson(p1);

        assertCommandBehavior("addgrades 1 1 -20.45",
                MESSAGE_INVALID_GRADES, "",
                expectedBook, false, logic.getLastShownList(), false,
                logic.getLastShownAssessmentList(), false);
    }

    @Test
    public void executeAddGrades_personMissing_personMissingMessage() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Assessment a1 = helper.generateAssessment(1);
        List<Assessment> singleAssessment = helper.generateAssessmentsList(a1);
        addressBook.addAssessment(a1);
        logic.setLastShownAssessmentList(singleAssessment);

        Person p1 = helper.generatePerson(1, false);
        List<Person> personList = helper.generatePersonList(p1);
        logic.setLastShownList(personList);

        AddressBook expectedBook = new AddressBook();
        expectedBook.addAssessment(a1);

        assertCommandBehavior("addgrades 1 1 100",
                MESSAGE_PERSON_NOT_IN_ADDRESSBOOK, "",
                expectedBook, false, logic.getLastShownList(), false,
                logic.getLastShownAssessmentList(), false);
    }

    @Test
    public void executeAddGrades_invalidNumberOfArgs_invalidNumberMessage() throws Exception {
        String expectedMessage = String.format(MESSAGE_WRONG_NUMBER_ARGUMENTS, 3, 1,
                AddGradesCommand.MESSAGE_USAGE);
        assertCommandBehaviorForExam("addgrades 1", expectedMessage);

        expectedMessage = String.format(MESSAGE_WRONG_NUMBER_ARGUMENTS, 3, 2,
                AddGradesCommand.MESSAGE_USAGE);
        assertCommandBehavior("addgrades 1 1", expectedMessage);
    }

    @Test
    public void executeDeleteGrades_noArgs_invalidCommandMessage() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteGradesCommand.MESSAGE_USAGE);
        assertCommandBehavior("deletegrades", expectedMessage);
        assertCommandBehavior("deletegrades ", expectedMessage);
    }

    @Test
    public void executeDeleteGrades_invalidNumberOfArgs_invalidNumberMessage() throws Exception {
        String expectedMessage = String.format(MESSAGE_WRONG_NUMBER_ARGUMENTS, 2, 1,
                DeleteGradesCommand.MESSAGE_USAGE);
        assertCommandBehavior("deletegrades 1", expectedMessage);

        expectedMessage = String.format(MESSAGE_WRONG_NUMBER_ARGUMENTS, 2, 3,
                DeleteGradesCommand.MESSAGE_USAGE);
        assertCommandBehavior("deletegrades 1 1 1", expectedMessage);
    }

    @Test
    public void executeDeleteGrades_invalidParsedArgs_invalidCommandMessage() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteGradesCommand.MESSAGE_USAGE);
        assertCommandBehavior("deletegrades not_a_number 1", expectedMessage);
        assertCommandBehavior("deletegrades 1 not_a_number", expectedMessage);
        assertCommandBehavior("deletegrades not_a_number not_a_number", expectedMessage);
    }

    @Test
    public void executeViewGrades_invalidArgsFormat_invalidCommandMessage() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewGradesCommand.MESSAGE_USAGE);
        assertCommandBehavior("viewgrades ", expectedMessage);
        assertCommandBehavior("viewgrades arg not number", expectedMessage);
    }

    @Test
    public void executeViewGrades_invalidIndex_invalidIndexMessage() throws Exception {
        assertInvalidIndexBehaviorForCommand("viewgrades");
    }

    @Test
    public void executeViewGrades_validArgs_success() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Person p1 = helper.generatePerson(1, false);
        List<Person> personList = helper.generatePersonList(p1);
        addressBook.addPerson(p1);
        logic.setLastShownList(personList);

        Grades grade = new Grades(100.00);
        Assessment a1 = helper.generateAssessment(1);
        a1.addGrade(p1, grade);
        List<Assessment> singleAssessment = helper.generateAssessmentsList(a1);
        addressBook.addAssessment(a1);

        Person p1Expected = helper.generatePerson(1, false, 1, 100);
        List<Person> personListExpected = helper.generatePersonList(p1Expected);
        AddressBook expectedBook = helper.generateAddressBook(personListExpected);
        helper.addAssessmentsToAddressBook(expectedBook, singleAssessment);

        assertCommandBehavior("viewgrades 1",
                String.format(ViewGradesCommand.MESSAGE_VIEW_GRADES_DETAILS, p1Expected.getAsTextShowAssess()), "",
                expectedBook, false, logic.getLastShownList(), false,
                logic.getLastShownAssessmentList(), false);
    }

    @Test
    public void executeViewGrades_personMissing_personMissingMessage() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Person p1 = helper.generatePerson(1, false);
        List<Person> personList = helper.generatePersonList(p1);
        logic.setLastShownList(personList);

        Grades grade = new Grades(100.00);
        Assessment a1 = helper.generateAssessment(1);
        a1.addGrade(p1, grade);
        List<Assessment> singleAssessment = helper.generateAssessmentsList(a1);
        addressBook.addAssessment(a1);

        AddressBook expectedBook = new AddressBook();
        helper.addAssessmentsToAddressBook(expectedBook, singleAssessment);

        assertCommandBehavior("viewgrades 1",
                MESSAGE_PERSON_NOT_IN_ADDRESSBOOK, "",
                expectedBook, false, logic.getLastShownList(), false,
                logic.getLastShownAssessmentList(), false);
    }

    @Test
    public void executeAddAssignmentStatistics_validArgs_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        AssignmentStatistics toBeAdded = helper.stat();

        StatisticsBook expected = new StatisticsBook();
        expected.addStatistic(toBeAdded);
        Assessment assessment = new Assessment("Spanish Quiz");
        addressBook.addAssessment(assessment);
        Person person1 = helper.makeAdam();
        Grades grade = new Grades(100);
        assessment.addGrade(person1, grade);
        addressBook.addPerson(person1);

        List<Assessment> singleAssessment = List.of(assessment);
        logic.setLastShownAssessmentList(singleAssessment);

        assertCommandBehavior("addstatistics 1", String.format(AddAssignmentStatistics.MESSAGE_SUCCESS,
                toBeAdded), expected, false);
    }

    @Test
    public void executeDeleteStatistics_invalidArgs_invalidMessage() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteStatisticsCommand.MESSAGE_USAGE);
        assertCommandBehavior("deletestatistics ", expectedMessage);
        assertCommandBehavior("deletestatistics arg not number", expectedMessage);
    }

    @Test
    public void executeDeleteStatistics_invalidIndex_invalidIndexMessage() throws Exception {
        assertInvalidIndexBehaviorForCommand("deletestatistics", MESSAGE_INVALID_STATISTICS_DISPLAYED_INDEX);
    }

    @Test
    public void executeListStatistics_invalidArgs_invalidMessage() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListStatisticsCommand.MESSAGE_USAGE);
        assertCommandBehavior("liststatistics 1 2", expectedMessage);
        assertCommandBehavior("liststatistics any other args", expectedMessage);
    }

    @Test
    public void executeAddAssignmentStatistics_duplicateStatistics_duplicateStatsMessage() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        AssignmentStatistics toBeAdded = helper.stat();
        statisticsBook.addStatistic(toBeAdded);

        StatisticsBook expected = new StatisticsBook();
        expected.addStatistic(toBeAdded);
        Assessment assessment = new Assessment("Spanish Quiz");
        addressBook.addAssessment(assessment);
        Person person1 = helper.makeAdam();
        Grades grade = new Grades(100);
        assessment.addGrade(person1, grade);
        addressBook.addPerson(person1);

        List<Assessment> singleAssessment = List.of(assessment);
        logic.setLastShownAssessmentList(singleAssessment);

        assertCommandBehavior("addstatistics 1", AddAssignmentStatistics.MESSAGE_DUPLICATE_STATISTIC,
                expected, false);
    }

    @Test
    public void executeAddAssignmentStatistics_invalidIndex_invalidIndexMessage() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();

        StatisticsBook expected = new StatisticsBook();
        Assessment assessment = new Assessment("Spanish Quiz");
        addressBook.addAssessment(assessment);
        Person person1 = helper.makeAdam();
        Grades grade = new Grades(100);
        assessment.addGrade(person1, grade);
        addressBook.addPerson(person1);

        List<Assessment> singleAssessment = List.of(assessment);
        logic.setLastShownAssessmentList(singleAssessment);

        assertCommandBehavior("addstatistics 2", MESSAGE_INVALID_ASSESSMENT_DISPLAYED_INDEX,
                expected, false);
    }
}
