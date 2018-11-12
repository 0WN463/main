package classrepo.commands.exams;

import java.util.List;

import classrepo.commands.Command;
import classrepo.commands.commandresult.CommandResult;
import classrepo.commands.commandresult.ListType;
import classrepo.data.person.ReadOnlyExam;

/**
 * Clears the exam book.
 */
public class ClearExamsCommand extends Command {

    public static final String COMMAND_WORD = "clearexams";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Clears exam book permanently.\n\t"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Exam book has been cleared!";

    @Override
    public CommandResult execute() {
        examBook.clear();
        addressBook.clearAllExam();
        final List<ReadOnlyExam> updatedList = examBook.getAllExam().immutableListView();
        return new CommandResult(MESSAGE_SUCCESS, updatedList, ListType.EXAMS);
    }

    @Override
    public boolean isMutating() {
        return true;
    }

    @Override
    public boolean isExamMutating() {
        return true;
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
