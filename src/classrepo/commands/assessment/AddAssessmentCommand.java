package classrepo.commands.assessment;

import java.util.List;

import classrepo.commands.Command;
import classrepo.commands.commandresult.CommandResult;
import classrepo.commands.commandresult.ListType;
import classrepo.data.exception.IllegalValueException;
import classrepo.data.person.Assessment;
import classrepo.data.person.UniqueAssessmentsList;

/**
 * Adds a new assessment to the address book.
 */
public class AddAssessmentCommand extends Command {

    public static final String COMMAND_WORD = "addassess";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Adds an assessment to the address book. "
            + "Parameters: ASSESSMENT_NAME\n\t"
            + "Example: " + COMMAND_WORD
            + " Math Midterm";

    public static final String MESSAGE_SUCCESS = "New assessment added: %1$s";
    public static final String MESSAGE_DUPLICATE_ASSESSMENT = "This assessment has already been entered!";

    private final Assessment toAdd;

    /**
     * Constructor used for Privileges
     * Command constructed has no functionality
     * */
    public AddAssessmentCommand() {
        this.toAdd = null;
    }

    /**
     * Convenience constructor using raw values.
     *
     * @throws IllegalValueException if any of the raw values are invalid
     */
    public AddAssessmentCommand(String examName) throws IllegalValueException {
        this.toAdd = new Assessment(examName);
    }

    public Assessment getAssessment() {
        return toAdd;
    }

    @Override
    public CommandResult execute() {
        try {
            addressBook.addAssessment(toAdd);
            final List<Assessment> updatedList = addressBook.getAllAssessments().immutableListView();
            return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd), updatedList, ListType.ASSESSMENT);
        } catch (UniqueAssessmentsList.DuplicateAssessmentException dpe) {
            return new CommandResult(MESSAGE_DUPLICATE_ASSESSMENT);
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
