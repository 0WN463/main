package classrepo.commands.assessment;

import java.util.List;

import classrepo.commands.Command;
import classrepo.commands.commandformat.indexformat.IndexFormatCommand;
import classrepo.commands.commandresult.CommandResult;
import classrepo.commands.commandresult.ListType;
import classrepo.common.Messages;
import classrepo.data.person.Assessment;
import classrepo.data.person.UniqueAssessmentsList;

/**
 * Deletes an assessment identified using its last displayed index from the address book.
 */
public class DeleteAssessmentCommand extends IndexFormatCommand {

    public static final String COMMAND_WORD = "deleteassess";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Deletes the assessment identified by the index number used in the last assessment listing.\n\t"
            + "Parameters: INDEX\n\t"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_ASSESSMENT_SUCCESS = "Deleted Assessment: %1$s";

    @Override
    public CommandResult execute() {
        try {
            final Assessment target = getTargetAssessment();
            addressBook.removeAssessment(target);
            target.removeAllGrades();
            final List<Assessment> updatedList = addressBook.getAllAssessments().immutableListView();
            return new CommandResult(String.format(MESSAGE_DELETE_ASSESSMENT_SUCCESS, target), updatedList,
                    ListType.ASSESSMENT);
        } catch (Command.AssessmentIndexOutOfBoundsException aie) {
            return new CommandResult(Messages.MESSAGE_INVALID_ASSESSMENT_DISPLAYED_INDEX);
        } catch (UniqueAssessmentsList.AssessmentNotFoundException nfe) {
            return new CommandResult(Messages.MESSAGE_ASSESSMENT_NOT_IN_ADDRESSBOOK);
        }
    }

    @Override
    public boolean isMutating() {
        return true;
    }

    @Override
    public Command.Category getCategory() {
        return Command.Category.ASSESSMENT;
    }

    @Override
    public String getCommandUsageMessage() {
        return MESSAGE_USAGE;
    }
}
