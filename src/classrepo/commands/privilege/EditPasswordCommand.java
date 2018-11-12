package classrepo.commands.privilege;

import classrepo.commands.commandformat.KeywordsFormatCommand;
import classrepo.commands.commandresult.CommandResult;
import classrepo.logic.Logic;

/**
 * Changes the master password to a new one.
 * Checks that the old password given is correct.
 */
public class EditPasswordCommand extends KeywordsFormatCommand {

    public static final String COMMAND_WORD = "editpw";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Changes master password to a new specified one.\n\t "
            + "Parameters: OLD_PASSWORD NEW_PASSWORD\n\t"
            + "Example: " + COMMAND_WORD + " old_password sudo1234";

    public static final String MESSAGE_SUCCESS = "Password changed to %s";
    public static final String MESSAGE_WRONG_PASSWORD = "Wrong password entered";
    public static final String MESSAGE_SAME_AS_OLD_PASSWORD = "New password entered is the same as old password";

    private static final int REQUIRED_ARGUMENTS = 2;

    private String newPassword;
    private String oldPassword;

    @Override
    public void setUp(String[] arguments) {
        assert(arguments.length == REQUIRED_ARGUMENTS);
        this.oldPassword = arguments[0];
        this.newPassword = arguments[1];
    }

    @Override
    public int getNumRequiredArg () {
        return REQUIRED_ARGUMENTS;
    }

    @Override
    public CommandResult execute() {
        try {
            validatePassword();
            if (oldPassword.equals(newPassword)) {
                return new CommandResult(MESSAGE_SAME_AS_OLD_PASSWORD);
            }
            addressBook.setMasterPassword(newPassword);
            return new CommandResult(String.format(MESSAGE_SUCCESS, newPassword));
        } catch (Logic.WrongPasswordEnteredException wpe) {
            return new CommandResult(MESSAGE_WRONG_PASSWORD);
        }
    }

    private void validatePassword() throws Logic.WrongPasswordEnteredException {
        if (!oldPassword.equals(addressBook.getMasterPassword())) {
            throw new Logic.WrongPasswordEnteredException();
        }
    }

    @Override
    public boolean isMutating() {
        return true;
    }

    @Override
    public Category getCategory() {
        return Category.PRIVILEGE;
    }

    @Override
    public String getCommandUsageMessage() {
        return MESSAGE_USAGE;
    }

}
