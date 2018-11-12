package classrepo.commands.fees;

import classrepo.commands.Command;
import classrepo.commands.commandresult.CommandResult;
import classrepo.formatter.PersonListFormat;

/**
 * Displays the names and fees of all people who have fees due with respect to today's date
 */
public class ListDueFeesCommand extends Command {

    public static final String COMMAND_WORD = "listdue";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Displays all the names and fees of people with fees due with respect to today's date.\n\t"
            + "Example: " + COMMAND_WORD;

    @Override
    public CommandResult execute() {
        String date = java.time.LocalDate.now().toString();
        return new CommandResult(getMessageForFeesListShownSummary(addressBook.listdueFeesPerson(date)),
                addressBook.listdueFeesPerson(date), PersonListFormat.FEES_DUE_DETAILS);
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
