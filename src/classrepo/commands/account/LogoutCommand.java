package classrepo.commands.account;

import static classrepo.common.Messages.MESSAGE_NOT_LOGGED_IN;

import classrepo.commands.Command;
import classrepo.commands.commandresult.CommandResult;

/**
 * Logs out of your account.
 */
public class LogoutCommand extends Command {

    public static final String COMMAND_WORD = "logout";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Logs out of your account, resetting privilege to Basic.\n\t "
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Logged out!";

    @Override
    public CommandResult execute() {
        if (privilege.isBase()) {
            return new CommandResult(MESSAGE_NOT_LOGGED_IN);
        }
        privilege.resetPrivilege();
        return new CommandResult(MESSAGE_SUCCESS);
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
