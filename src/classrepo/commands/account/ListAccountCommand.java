package classrepo.commands.account;

import java.util.List;
import java.util.stream.Collectors;

import classrepo.commands.Command;
import classrepo.commands.commandresult.CommandResult;
import classrepo.data.person.ReadOnlyPerson;
import classrepo.formatter.PersonListFormat;

/**
 * Lists all persons with accounts in the address book to the user.
 */
public class ListAccountCommand extends Command {

    public static final String COMMAND_WORD = "listacc";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Displays all persons with accounts in the address book as a list with index numbers.\n\t"
            + "Example: " + COMMAND_WORD;

    @Override
    public CommandResult execute() {
        List<ReadOnlyPerson> allPersons = addressBook.getAllPersons().immutableListView();

        // We do not need an immutable list as this is a copy of UniquePersonList's internal list
        final List<ReadOnlyPerson> personsWithAccount = allPersons.stream()
                .filter(ReadOnlyPerson::hasAccount).collect(Collectors.toList());
        return new CommandResult(getMessageForPersonListShownSummary(personsWithAccount), personsWithAccount,
                PersonListFormat.ACCOUNT_DETAILS);
    }

    @Override
    public Category getCategory() {
        return Category.ACCOUNT;
    }

    @Override
    public String getCommandUsageMessage() {
        return MESSAGE_USAGE;
    }
}
