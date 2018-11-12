package classrepo.commands.assessment;

import java.util.List;

import classrepo.commands.Command;
import classrepo.commands.commandresult.CommandResult;
import classrepo.commands.commandresult.ListType;
import classrepo.data.person.AssignmentStatistics;

/**
 * Lists all statistics in the statistics book to the user.
 */
public class ListStatisticsCommand extends Command {

    public static final String COMMAND_WORD = "liststatistics";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Displays all statistics in the statistics book as a list with index numbers.\n\t"
            + "Example: " + COMMAND_WORD;

    @Override
    public CommandResult execute() {
        List<AssignmentStatistics> allStatistics = statisticsBook.getAllStatistics().immutableListView();
        return new CommandResult(getMessageForStatisticsListShownSummary(allStatistics), allStatistics,
                ListType.STATISTICS);
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
