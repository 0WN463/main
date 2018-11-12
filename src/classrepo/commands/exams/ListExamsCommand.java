package classrepo.commands.exams;

import java.util.List;

import classrepo.commands.Command;
import classrepo.commands.commandresult.CommandResult;
import classrepo.commands.commandresult.ListType;
import classrepo.data.person.ReadOnlyExam;

/**
 * Lists all exams in the exam book to the user.
 */
public class ListExamsCommand extends Command {

    public static final String COMMAND_WORD = "listexams";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Displays all exams in the exam book as a list with index numbers.\n\t"
            + "Example: " + COMMAND_WORD;

    @Override
    public CommandResult execute() {
        List<ReadOnlyExam> allExams = examBook.getAllExam().immutableListView();
        return new CommandResult(getMessageForExamListShownSummary(allExams), allExams, ListType.EXAMS);
    }

    @Override
    public Category getCategory() {
        return Category.EXAM;
    }

    @Override
    public String getCommandUsageMessage() {
        return MESSAGE_USAGE;
    }
}
