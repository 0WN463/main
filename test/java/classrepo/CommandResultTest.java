package classrepo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import classrepo.commands.commandresult.CommandResult;
import classrepo.commands.commandresult.ListType;
import classrepo.commands.commandresult.MessageType;
import classrepo.data.person.Assessment;
import classrepo.data.person.AssignmentStatistics;
import classrepo.data.person.Exam;
import classrepo.data.person.Person;

/**
 * Due to the lack of GUI testing, CommandResult is tested thoroughly to ensure correct message is displayed,
 * as it is the Object that tells MainWindow how its details should be displayed.
 * */
public class CommandResultTest {
    private CommandResult commandResultWithStatusMessage =
            new CommandResult("status message", MessageType.STATUS);
    private CommandResult commandResultWithOutputMessage =
            new CommandResult("output message", MessageType.OUTPUT);
    private CommandResult commandResultWithBothMessage =
            new CommandResult("status message", "output message");
    private CommandResult commandResultWithPersonList =
            new CommandResult("status message", new ArrayList<>(), ListType.PERSONS);
    private CommandResult commandResultWithExamsList =
            new CommandResult("status message", new ArrayList<>(), ListType.EXAMS);
    private CommandResult commandResultWithAssessList =
            new CommandResult("status message", new ArrayList<>(), ListType.ASSESSMENT);
    private CommandResult commandResultWithStatsList =
            new CommandResult("status message", new ArrayList<>(), ListType.STATISTICS);

    @Test
    public void assertHasOutputMessageCorrect() {
        assertTrue(commandResultWithBothMessage.hasOutputMessage());
        assertTrue(commandResultWithOutputMessage.hasOutputMessage());
        assertTrue(commandResultWithExamsList.hasOutputMessage());
        assertTrue(commandResultWithPersonList.hasOutputMessage());
        assertTrue(commandResultWithAssessList.hasOutputMessage());
        assertTrue(commandResultWithStatsList.hasOutputMessage());
        assertFalse(commandResultWithStatusMessage.hasOutputMessage());
    }

    @Test
    public void assertHasStatusMessageCorrect() {
        assertTrue(commandResultWithBothMessage.hasStatusMessage());
        assertTrue(commandResultWithStatusMessage.hasStatusMessage());
        assertTrue(commandResultWithExamsList.hasStatusMessage());
        assertTrue(commandResultWithPersonList.hasStatusMessage());
        assertTrue(commandResultWithAssessList.hasStatusMessage());
        assertTrue(commandResultWithStatsList.hasStatusMessage());

        assertFalse(commandResultWithOutputMessage.hasStatusMessage());
    }

    @Test
    public void assertListConversionSuccess() {
        assertTrue(commandResultWithPersonList.getRelevantPersons()
                .filter(a -> a.equals(new ArrayList<Person>())).isPresent());
        assertTrue(commandResultWithExamsList.getRelevantExams()
                .filter(a -> a.equals(new ArrayList<Exam>())).isPresent());
        assertTrue(commandResultWithAssessList.getRelevantAssessments()
                .filter(a -> a.equals(new ArrayList<Assessment>())).isPresent());
        assertTrue(commandResultWithStatsList.getRelevantStatistics()
                .filter(a -> a.equals(new ArrayList<AssignmentStatistics>())).isPresent());
    }
}
