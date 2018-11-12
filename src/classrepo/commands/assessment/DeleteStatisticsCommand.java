package classrepo.commands.assessment;

import java.util.List;

import classrepo.commands.commandformat.indexformat.IndexFormatCommand;
import classrepo.commands.commandresult.CommandResult;
import classrepo.commands.commandresult.ListType;
import classrepo.common.Messages;
import classrepo.data.person.AssignmentStatistics;
import classrepo.data.person.UniqueStatisticsList;

/**
 * Deletes a statistic identified using its last displayed index from the statistics book.
 */
public class DeleteStatisticsCommand extends IndexFormatCommand {

    public static final String COMMAND_WORD = "deletestatistics";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Deletes the statistic identified by the index number used in the last statistics listing.\n\t"
            + "Parameters: INDEX\n\t"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_STATISTICS_SUCCESS = "Deleted Statistics: %1$s";



    @Override
    public CommandResult execute() {
        try {
            final AssignmentStatistics target = getTargetStatistic();
            statisticsBook.removeStatistic(target);
            final List<AssignmentStatistics> updatedList = statisticsBook.getAllStatistics().immutableListView();
            return new CommandResult(String.format(MESSAGE_DELETE_STATISTICS_SUCCESS, target), updatedList,
                    ListType.STATISTICS);
        } catch (IndexOutOfBoundsException iob) {
            return new CommandResult(Messages.MESSAGE_INVALID_STATISTICS_DISPLAYED_INDEX);
        } catch (UniqueStatisticsList.StatisticsNotFoundException nfe) {
            return new CommandResult(Messages.MESSAGE_STATISTIC_NOT_IN_STATISTICSBOOK);
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
