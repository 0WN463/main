package classrepo.commands.general;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import classrepo.commands.Command;
import classrepo.commands.commandresult.CommandResult;

/**
 * Shows help instructions.
 */
public class HelpCommand extends Command {

    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Shows program usage instructions.\n\t"
            + "Example: " + COMMAND_WORD;

    private String attachedErrorMessage;

    public HelpCommand() {
    }

    public HelpCommand(String attachedErrorMessage) {
        this.attachedErrorMessage = attachedErrorMessage;
    }

    /**
     * Creates the help manual to the user based on what commands they can access
     * */
    public String makeHelpManual() {
        StringBuilder builder = new StringBuilder();
        List<Command> allowedCommands = privilege.getAllowedCommands();
        HashSet<Category> seenCategories = new HashSet<>();
        for (Command command: allowedCommands) {
            final Category category = command.getCategory();

            if (!seenCategories.contains(category)) {
                builder.append('\n').append(category.toString()).append('\n');
                seenCategories.add(category);
            }
            builder.append(command.getCommandUsageMessage()).append('\n');
        }
        // Removes the extra newline character added at the start
        builder.deleteCharAt(0);
        return builder.toString();
    }

    @Override
    public CommandResult execute() {
        attachedErrorMessage = Optional.ofNullable(attachedErrorMessage).orElse("");
        return new CommandResult(attachedErrorMessage, makeHelpManual());
    }

    @Override
    public String getCommandUsageMessage() {
        return MESSAGE_USAGE;
    }
}
