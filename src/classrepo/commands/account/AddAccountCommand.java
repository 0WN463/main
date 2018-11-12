package classrepo.commands.account;

import java.util.List;

import classrepo.commands.commandformat.indexformat.IndexFormatCommand;
import classrepo.commands.commandformat.indexformat.ObjectTargeted;
import classrepo.commands.commandresult.CommandResult;
import classrepo.common.Messages;
import classrepo.data.account.Account;
import classrepo.data.exception.IllegalValueException;
import classrepo.data.person.Person;
import classrepo.data.person.ReadOnlyPerson;
import classrepo.data.person.UniquePersonList.PersonNotFoundException;

/**
 * Adds an account to a specific person, with a username and password of choice
 */
public class AddAccountCommand extends IndexFormatCommand {

    public static final String COMMAND_WORD = "addacc";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Adds an account as specified to the person identified by the "
            + "index number used in the last person listing.\n\t"
            + "Parameters: INDEX USERNAME PASSWORD PRIVILEGE\n\t"
            + "PRIVILEGE could be of type \"Basic\", \"Tutor\", \"Admin\".\n\t"
            + "Example: " + COMMAND_WORD + " 1 username password basic";

    public static final String MESSAGE_ADD_ACCOUNT_PERSON_SUCCESS = "Added Account to: %1$s";
    public static final String MESSAGE_USERNAME_TAKEN = "Username Taken!";
    public static final String MESSAGE_PERSON_HAS_ACCOUNT = "Target person already has an existing account!!";
    public static final String MESSAGE_INVALID_PRIVILEGE = "Desired privilege level of \"%s\" is not valid.\n%s";

    private final Account toAdd;

    /**
     * Constructor used for Privileges
     * Command constructed has no functionality
     * */
    public AddAccountCommand() {
        toAdd = null;
    }

    public AddAccountCommand(int targetVisibleIndex, String username, String password, String privilege)
            throws IllegalValueException {
        setTargetIndex(targetVisibleIndex, ObjectTargeted.PERSON);
        try {
            toAdd = new Account(username, password, privilege);
        } catch (IllegalValueException ive) {
            final String message = String.format(MESSAGE_INVALID_PRIVILEGE, privilege, ive.getMessage());
            throw new IllegalValueException(message);
        }
    }

    @Override
    public CommandResult execute() {
        try {
            if (addressBook.containsPersonWithUsername(toAdd.getUsername())) {
                return new CommandResult(MESSAGE_USERNAME_TAKEN);
            }

            final Person target = getTargetPerson();
            if (target.hasAccount()) {
                return new CommandResult(MESSAGE_PERSON_HAS_ACCOUNT);
            }

            toAdd.setPrivilegePerson(target);
            target.setAccount(toAdd);
            final List<ReadOnlyPerson> updatedList = addressBook.getAllPersons().immutableListView();

            return new CommandResult(String.format(MESSAGE_ADD_ACCOUNT_PERSON_SUCCESS, target.getName()), updatedList);
        } catch (IndexOutOfBoundsException ie) {
            return new CommandResult(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        } catch (PersonNotFoundException pnfe) {
            return new CommandResult(Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK);
        }
    }

    @Override
    public boolean isMutating() {
        return true;
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
