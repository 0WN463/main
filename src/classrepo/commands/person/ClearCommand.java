package classrepo.commands.person;

import java.util.List;

import classrepo.commands.Command;
import classrepo.commands.commandresult.CommandResult;
import classrepo.data.person.ReadOnlyPerson;
import classrepo.privilege.Privilege;

/**
 * Clears the address book.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Clears address book permanently.\n\t"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Address book has been cleared!";
    public static final String MESSAGE_DELETING_SELF = "Clearing the address book"
            + " will delete the currently logged in account.\n"
            + "Logout and raise privilege to Admin using master password to execute this command. "
            + "Raise privilege using master password to execute this command instead.";

    @Override
    public CommandResult execute() {
        try {
            checkIsLoggedIn();
            addressBook.clear();
            examBook.clearTakers();
            final List<ReadOnlyPerson> updatedList = addressBook.getAllPersons().immutableListView();
            return new CommandResult(MESSAGE_SUCCESS, updatedList);
        } catch (Privilege.SelfModifyingException sme) {
            return new CommandResult(MESSAGE_DELETING_SELF);
        }
    }

    private void checkIsLoggedIn() throws Privilege.SelfModifyingException {
        if (privilege.getMyPerson().isPresent()) {
            throw new Privilege.SelfModifyingException();
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
        return Category.PERSON;
    }

    @Override
    public String getCommandUsageMessage() {
        return MESSAGE_USAGE;
    }
}
