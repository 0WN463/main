package classrepo.commands.person;

import java.util.Optional;

import classrepo.commands.Command;
import classrepo.commands.commandresult.CommandResult;
import classrepo.common.Messages;
import classrepo.data.person.ReadOnlyPerson;


/**
 * Shows all details of the logged in person.
 * Private contact details are shown.
 */
public class ViewSelfCommand extends Command {

    public static final String COMMAND_WORD = "viewself";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Shows all details of yourself.\n\t"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_VIEW_PERSON_DETAILS = "Viewing self: %1$s";

    @Override
    public CommandResult execute() {
        final Optional<ReadOnlyPerson> myPerson = privilege.getMyReadOnlyPerson();
        return myPerson.map(a -> new CommandResult(String.format(MESSAGE_VIEW_PERSON_DETAILS, a.getName()),
                a.getAsTextShowAll()))
                .orElse(new CommandResult(Messages.MESSAGE_NOT_LOGGED_IN));
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
