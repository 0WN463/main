package classrepo.commands.person;

import java.util.List;

import classrepo.commands.Command;
import classrepo.commands.commandresult.CommandResult;
import classrepo.data.person.ReadOnlyPerson;
import classrepo.formatter.PersonListFormat;

/**
 * Lists all persons in the address book to the user.
 */
public class ListAllCommand extends Command {

    public static final String COMMAND_WORD = "listall";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Displays all persons (showing all non-private details) "
            + "in the address book as a list with index numbers.\n\t"
            + "Example: " + COMMAND_WORD;

    @Override
    public CommandResult execute() {
        List<ReadOnlyPerson> allPersons = addressBook.getAllPersons().immutableListView();
        return new CommandResult(getMessageForPersonListShownSummary(allPersons), allPersons,
                PersonListFormat.ALL_PUBLIC_DETAILS);
    }

    @Override
    public Category getCategory() {
        return Category.PERSON;
    }

    @Override
    public String getCommandUsageMessage() {
        return MESSAGE_USAGE;
    }
}
