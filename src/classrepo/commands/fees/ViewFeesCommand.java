package classrepo.commands.fees;

import classrepo.commands.commandformat.indexformat.IndexFormatCommand;
import classrepo.commands.commandformat.indexformat.ObjectTargeted;
import classrepo.commands.commandresult.CommandResult;
import classrepo.common.Messages;
import classrepo.data.person.ReadOnlyPerson;
import classrepo.data.person.UniquePersonList.PersonNotFoundException;

/**
 * Shows the fees owed with respect to the person identified in the last displayed index.
 * It's a private detail which is showed.
 */
public class ViewFeesCommand extends IndexFormatCommand {

    public static final String COMMAND_WORD = "viewfees";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Shows the fees of the person identified by the index number used in the last person listing.\n\t"
            + "Parameters: INDEX\n\t"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_VIEWFEE_PERSON_SUCCESS = "Viewing person: %1$s";

    /**
     * Constructor used for Privileges
     * Command constructed has no functionality
     * */
    public ViewFeesCommand() {
    }

    public ViewFeesCommand(int targetVisibleIndex) {
        setTargetIndex(targetVisibleIndex, ObjectTargeted.PERSON);
    }

    @Override
    public CommandResult execute() {
        try {
            final ReadOnlyPerson target = getTargetReadOnlyPerson();
            addressBook.findPerson(target);
            return new CommandResult(String.format(MESSAGE_VIEWFEE_PERSON_SUCCESS, target.getName()),
                    target.getAsTextShowFee());

        } catch (IndexOutOfBoundsException ie) {
            return new CommandResult(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        } catch (PersonNotFoundException pnfe) {
            return new CommandResult(Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK);
        }
    }

    @Override
    public Category getCategory() {
        return Category.FEES;
    }

    @Override
    public String getCommandUsageMessage() {
        return MESSAGE_USAGE;
    }
}
