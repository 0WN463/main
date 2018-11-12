package classrepo.commands.privilege;

import classrepo.commands.Command;
import classrepo.commands.commandresult.CommandResult;

/**
 * Set/Unset the Privilege of AddressBook to be permanently Admin
 */
public class SetPermanentAdminCommand extends Command {

    public static final String COMMAND_WORD = "perm";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Sets/unset the Privilege of this application to be permanently Admin.\n\t"
            + "Parameters: STATUS\n\t"
            + "STATUS can be either \"true\" or \"false\" \n\t"
            + "Example: " + COMMAND_WORD + " true";

    public static final String MESSAGE_SUCCESS = "Admin set to%s permanent";

    private final boolean isPerm;

    /**
     * Constructor used for Privileges
     * Command constructed has no functionality
     * */
    public SetPermanentAdminCommand() {
        isPerm = false;
    }

    public SetPermanentAdminCommand(boolean isPerm) {
        this.isPerm = isPerm;
    }


    @Override
    public CommandResult execute() {
        addressBook.setPermAdmin(isPerm);
        final String modifierWord = isPerm ? "" : " not";
        return new CommandResult(String.format(MESSAGE_SUCCESS, modifierWord));
    }

    @Override
    public Category getCategory() {
        return Category.PRIVILEGE;
    }

    @Override
    public String getCommandUsageMessage() {
        return MESSAGE_USAGE;
    }

    @Override
    public boolean isMutating() {
        return true;
    }
}
