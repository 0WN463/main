package classrepo.commands.exams;

import java.util.List;

import classrepo.commands.commandformat.indexformat.IndexFormatCommand;
import classrepo.commands.commandresult.CommandResult;
import classrepo.commands.commandresult.ListType;
import classrepo.common.Messages;
import classrepo.data.person.ReadOnlyExam;
import classrepo.data.person.UniqueExamList.ExamNotFoundException;

/**
 * Deletes an exam identified using its last displayed index from the exam book.
 */
public class DeleteExamCommand extends IndexFormatCommand {

    public static final String COMMAND_WORD = "deleteexam";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Deletes the exam identified by the index number in the last exams listing.\n\t"
            + "Parameters: INDEX\n\t"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_EXAM_SUCCESS = "Deleted %1$s";

    @Override
    public CommandResult execute() {
        try {
            final ReadOnlyExam target = getTargetReadOnlyExam();
            examBook.removeExam(target);
            addressBook.removeExam(target);
            final List<ReadOnlyExam> updatedList = examBook.getAllExam().immutableListView();
            return new CommandResult(String.format(MESSAGE_DELETE_EXAM_SUCCESS, target), updatedList, ListType.EXAMS);
        } catch (ExamIndexOutOfBoundsException eie) {
            return new CommandResult(Messages.MESSAGE_INVALID_EXAM_DISPLAYED_INDEX);
        } catch (ExamNotFoundException enfe) {
            return new CommandResult(Messages.MESSAGE_EXAM_NOT_IN_EXAMBOOK);
        }
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
