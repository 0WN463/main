package classrepo;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import classrepo.common.Pair;
import classrepo.data.account.Account;
import classrepo.data.person.Assessment;
import classrepo.data.person.Exam;
import classrepo.data.person.Person;
import classrepo.formatter.Formatter;
import classrepo.formatter.PersonListFormat;

public class FormatterTest {
    /** A platform independent line separator. */
    private static final String NEWLINE = System.lineSeparator();
    private List<Pair<List<Person>, String>> inputToExpectedOutput = new ArrayList<>();

    @Test
    public void formatStringChain() {
        assertEquals(String.format(" A%s B%<s C%<s", NEWLINE),
                Formatter.format("A", "B", "C"));
        assertEquals(String.format(" A%s B%<s C%<s D%<s", NEWLINE),
                Formatter.format("A", "B\nC", "D"));
    }

    @Test
    public void formatPersonsAllDetailsFormat() throws Exception {
        TestDataHelper helper = new TestDataHelper();

        assertEmptyListShowsEmpty(PersonListFormat.ALL_PUBLIC_DETAILS);
        final String personFormat = " %1$d. Person %1$d%4$s"
                + " Phone: %1$d%4$s"
                + " Email: %1$d@email%4$s"
                + " Address: House of %1$d%4$s"
                + " Tags: [tag%2$d][tag%3$d]%4$s";
        addInputToExpectedOutput(helper.generatePersonList(false),
                String.format(personFormat + " %4$s %4$s", 1, 1, 2, NEWLINE));

        //List of 3 people, all fields non-private
        addInputToExpectedOutput(helper.generatePersonList(false, false , false),
                String.format(personFormat + " %4$s", 1, 1, 2, NEWLINE)
                        + String.format(personFormat + " %4$s", 2, 2, 3, NEWLINE)
                        + String.format(personFormat + " %4$s %4$s", 3, 4, 3, NEWLINE));
        //List of 3 people, number 2 is is private
        final String person2Format = " %1$d. Person %1$d%4$s"
                + " Tags: [tag%2$d][tag%3$d]%4$s";
        addInputToExpectedOutput(helper.generatePersonList(false, true , false),
                String.format(personFormat + " %4$s", 1, 1, 2, NEWLINE)
                        + String.format(person2Format + " %4$s", 2, 2, 3, NEWLINE)
                        + String.format(personFormat + " %4$s %4$s", 3, 4, 3, NEWLINE));

        assertFormatterBehaviour(PersonListFormat.ALL_PUBLIC_DETAILS);
    }


    @Test
    public void formatPersonsOnlyDueFeesFormat() throws Exception {
        TestDataHelper helper = new TestDataHelper();

        assertEmptyListShowsEmpty(PersonListFormat.FEES_DUE_DETAILS);
        Person p1 = helper.generatePerson(1, true);
        Person p2 = helper.generatePerson(2, false);
        Person p3 = helper.generatePerson(3, false);
        List<Person> persons = List.of(p1, p2, p3);
        String expected = String.format(" 1. Person 1%1$s {private Fees: 0.00 / 00-00-0000} %1$s Due!%1$s %1$s"
                + " 2. Person 2%1$s {private Fees: 0.00 / 00-00-0000} %1$s Due!%1$s %1$s"
                + " 3. Person 3%1$s {private Fees: 0.00 / 00-00-0000} %1$s Due!%1$s %1$s %1$s", NEWLINE);
        assertEquals(expected, Formatter.format(persons, PersonListFormat.FEES_DUE_DETAILS));

        addInputToExpectedOutput(helper.generatePersonList(false, false , false),
                String.format(" 1. Person 1%1$s {private Fees: 0.00 / 00-00-0000} %1$s Due!%1$s %1$s"
                        + " 2. Person 2%1$s {private Fees: 0.00 / 00-00-0000} %1$s Due!%1$s %1$s"
                        + " 3. Person 3%1$s {private Fees: 0.00 / 00-00-0000} %1$s Due!%1$s %1$s %1$s", NEWLINE));
        assertFormatterBehaviour(PersonListFormat.FEES_DUE_DETAILS);
    }

    @Test
    public void formatPersonListFeeFormat() throws Exception {
        TestDataHelper helper = new TestDataHelper();

        assertEmptyListShowsEmpty(PersonListFormat.FEES_DETAILS);
        addInputToExpectedOutput(helper.generatePersonList(false, false , false),
                String.format(" 1. Person 1%1$s No Fees owed!%1$s %1$s"
                        + " 2. Person 2%1$s No Fees owed!%1$s %1$s"
                        + " 3. Person 3%1$s No Fees owed!%1$s %1$s %1$s", NEWLINE));
        assertFormatterBehaviour(PersonListFormat.FEES_DETAILS);
    }

    @Test
    public void formatPersonsOnlyNameFormat() throws Exception {
        TestDataHelper helper = new TestDataHelper();

        assertEmptyListShowsEmpty(PersonListFormat.NAMES_ONLY);

        final String personFormat = " %d. Person %<d%s";
        addInputToExpectedOutput(helper.generatePersonList(false),
                String.format(personFormat + " %<s", 1, NEWLINE));

        //List of 3 people, all fields non-private
        addInputToExpectedOutput(helper.generatePersonList(false, false , false),
                String.format(personFormat, 1, NEWLINE)
                        + String.format(personFormat, 2, NEWLINE)
                        + String.format(personFormat + " %<s", 3, NEWLINE));

        //List of 3 people, all fields non-private*
        addInputToExpectedOutput(helper.generatePersonList(false, true , false),
                String.format(personFormat, 1, NEWLINE)
                        + String.format(personFormat, 2, NEWLINE)
                        + String.format(personFormat + " %<s", 3, NEWLINE));

        assertFormatterBehaviour(PersonListFormat.NAMES_ONLY);
    }

    @Test
    public void formatPersonsAccountFormat() throws Exception {
        TestDataHelper helper = new TestDataHelper();

        Person p1 = helper.generatePerson(1, true);
        Person p2 = helper.generatePerson(2, false);
        Person p3 = helper.generatePerson(3, false);
        List<Person> persons = List.of(p1, p2, p3);

        //List of 3 people, all without accounts
        String expected = String.format(" 1. Person 1%s"
                + " 2. Person 2%<s"
                + " 3. Person 3%<s"
                + " %<s", NEWLINE);
        assertEquals(expected, Formatter.format(persons, PersonListFormat.ACCOUNT_DETAILS));
        p1.setAccount(new Account("user1", "pw", "Basic"));
        p2.setAccount(new Account("user2", "pw", "Tutor"));
        p3.setAccount(new Account("user3", "pw", "Admin"));

        //List of 3 people, all with accounts
        expected = String.format(" 1. Person 1 User Type: Basic%s"
                + " 2. Person 2 User Type: Tutor%<s"
                + " 3. Person 3 User Type: Admin%<s"
                + " %<s", NEWLINE);
        assertEquals(expected, Formatter.format(persons, PersonListFormat.ACCOUNT_DETAILS));
    }

    @Test
    public void formatAllExam() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        List<Exam> exams = helper.generateExamList(false, true);
        final String examFormat = " %1$d. %2$sExam: Exam %1$d || Subject: Subject %1$d || Date: 01-02-2018 || "
                + "Starts: 10:00 || Ends: 12:00 || Details: Held in %1$d || Takers: 0" + NEWLINE;
        String expected = String.format(examFormat, 1, "") + String.format(examFormat, 2, "private ") + " " + NEWLINE;
        assertEquals(expected, Formatter.formatExam(exams));
    }

    @Test
    public void formatExam() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Exam e1 = helper.generateExam(1, true, 2);
        String expected = "private Exam: Exam 1 || Subject: Subject 1 || Date: 01-02-2018 || "
            + "Starts: 10:00 || Ends: 12:00 || Details: Held in 1 || Takers: 2";
        assertEquals(expected, Formatter.getPrintableExam(e1.getExamName(), e1.getSubjectName(), e1.getExamDate(),
                e1.getExamStartTime(), e1.getExamEndTime(), e1.getExamDetails(), e1.getTakers(), e1.isPrivate()));

        Exam e2 = helper.generateExam(2, false, 0);
        expected = "Exam: Exam 2 || Subject: Subject 2 || Date: 01-02-2018 || "
                + "Starts: 10:00 || Ends: 12:00 || Details: Held in 2 || Takers: 0";
        assertEquals(expected, Formatter.getPrintableExam(e2.getExamName(), e2.getSubjectName(), e2.getExamDate(),
                e2.getExamStartTime(), e2.getExamEndTime(), e2.getExamDetails(), e2.getTakers(), e2.isPrivate()));
    }

    @Test
    public void formatAssessment() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Assessment a1 = helper.generateAssessment(1);
        String expected = "1";
        assertEquals(expected, Formatter.getPrintableAssessment(a1.getExamName()));
    }

    @Test
    public void formatListAssessments() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Assessment assessment1 = new Assessment(Integer.toString(1));
        Assessment assessment2 = new Assessment(Integer.toString(2));
        List<Assessment> assessmentList = helper.generateAssessmentsList(assessment1, assessment2);
        final String assessmentFormat = " %1$d. %1$d" + NEWLINE;
        String expected = String.format(assessmentFormat, 1) + String.format(assessmentFormat, 2) + " " + NEWLINE;
        assertEquals(expected, Formatter.formatAssessments(assessmentList));
    }

    private void assertEmptyListShowsEmpty(PersonListFormat personListFormat) {
        assertEquals(" " + NEWLINE, Formatter.format(Collections.emptyList(), personListFormat));
    }

    private void addInputToExpectedOutput(List<Person> input, String output) {
        inputToExpectedOutput.add(new Pair<>(input, output));
    }

    /**
     * Asserts formatter turns input list in inputToExpectedOutput to its expected output
     * Also tests for empty list
     */
    private void assertFormatterBehaviour(PersonListFormat personListFormat) {
        assertEmptyListShowsEmpty(personListFormat);

        for (Pair<List<Person>, String> inputToOutput: inputToExpectedOutput) {
            final String actual = Formatter.format(inputToOutput.getFirst(), personListFormat);
            final String expected = inputToOutput.getSecond();
            assertEquals(expected, actual);
        }
    }
}
