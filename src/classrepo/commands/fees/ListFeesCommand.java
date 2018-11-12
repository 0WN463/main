package classrepo.commands.fees;

import classrepo.commands.Command;
import classrepo.commands.commandresult.CommandResult;
import classrepo.formatter.PersonListFormat;

/**
 * Lists the fees alongside the name for all persons in the address book.
 */
public class ListFeesCommand extends Command {

    public static final String COMMAND_WORD = "listfees";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Displays all names and fees in the address book.\n\t"
            + "Example: " + COMMAND_WORD;

    @Override
    public CommandResult execute() {
        return new CommandResult(getMessageForFeesListShownSummary(addressBook.listFeesPerson()),
                addressBook.listFeesPerson(), PersonListFormat.FEES_DETAILS);
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
