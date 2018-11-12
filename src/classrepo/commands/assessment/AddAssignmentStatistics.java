package classrepo.commands.assessment;

import java.util.List;
import java.util.Map;

import classrepo.commands.commandformat.indexformat.IndexFormatCommand;
import classrepo.commands.commandresult.CommandResult;
import classrepo.commands.commandresult.ListType;
import classrepo.common.Messages;
import classrepo.data.person.Assessment;
import classrepo.data.person.AssignmentStatistics;
import classrepo.data.person.Grades;
import classrepo.data.person.Person;
import classrepo.data.person.UniqueStatisticsList;

/**
 * Creates a new statistic in the statistics book.
 */
public class AddAssignmentStatistics extends IndexFormatCommand {

    public static final String COMMAND_WORD = "addstatistics";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Adds new statistic to the StatisticsBook based "
            + "on index of assessment, as per the last listing of listassessment.\n\t"
            + "Parameters: EXAMNAME\n\t"
            + "Example: " + COMMAND_WORD
            + " 1";

    public static final String MESSAGE_SUCCESS = "New statistic added : %1$s";
    public static final String MESSAGE_DUPLICATE_STATISTIC = "This statistic already exists in the statistics book!";

    private AssignmentStatistics toAdd;

    @Override
    public CommandResult execute() {
        try {
            final Map<Person, Grades> grade;
            Assessment assessName = getTargetAssessment();
            String examName = assessName.getExamName();
            double maxGrade = 0;
            double minGrade = Double.MAX_VALUE;
            double averageScore;
            double total = 0;
            grade = assessName.getAllGrades();
            final int numPersons = grade.size();

            for (Grades gradeVal : grade.values()) {
                maxGrade = Math.max(maxGrade, gradeVal.getValue());
                minGrade = Math.min(minGrade, gradeVal.getValue());
                total += gradeVal.getValue();
            }
            if (numPersons > 0) {
                averageScore = (double) Math.round((total / numPersons) * 100) / 100;
            } else {
                averageScore = 0; //to account for empty grades
                minGrade = 0;
            }

            this.toAdd = new AssignmentStatistics(examName, averageScore, numPersons, maxGrade, minGrade);
            statisticsBook.addStatistic(toAdd);
            final List<AssignmentStatistics> updatedList = statisticsBook.getAllStatistics().immutableListView();
            return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd), updatedList,
                    ListType.STATISTICS);
        } catch (UniqueStatisticsList.DuplicateStatisticsException dpe) {
            return new CommandResult(MESSAGE_DUPLICATE_STATISTIC);
        } catch (AssessmentIndexOutOfBoundsException aie) {
            return new CommandResult(Messages.MESSAGE_INVALID_ASSESSMENT_DISPLAYED_INDEX);
        }
    }

    @Override
    public boolean isMutating() {
        return true;
    }

    @Override
    public Category getCategory() {
        return Category.ASSESSMENT;
    }

    @Override
    public String getCommandUsageMessage() {
        return MESSAGE_USAGE;
    }
}
