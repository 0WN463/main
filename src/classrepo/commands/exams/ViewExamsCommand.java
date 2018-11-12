package classrepo.commands.exams;

import java.util.Optional;

import classrepo.commands.Command;
import classrepo.commands.commandformat.indexformat.IndexFormatCommand;
import classrepo.commands.commandresult.CommandResult;
import classrepo.common.Messages;
import classrepo.data.person.ReadOnlyPerson;
import classrepo.privilege.user.BasicUser;

/**
 * Shows the exams of the person identified using the last displayed index.
 * Users of insufficient privilege level can only check their own non-private exams, eg. a logged in BasicUser
 * cannot check any exam of others.
 * Users of high privilege level can check the exams of any person.
 */
public class ViewExamsCommand extends IndexFormatCommand {

    public static final String COMMAND_WORD = "viewexams";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Shows the exams of the person identified by the index number "
            + "used in the last person listing. Basic users can only view their own non-private exams.\n\t"
            + "Parameters: INDEX\n\t"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_VIEW_EXAMS_PERSON_SUCCESS = "Viewing exams of Person: %1$s";

    @Override
    public CommandResult execute() {
        try {
            final ReadOnlyPerson target = getTargetReadOnlyPerson();
            if (!addressBook.containsPerson(target)) {
                return new CommandResult(Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK);
            }

            //check privilege level
            if (privilege.getUser().equals(new BasicUser())) {
                final Optional<ReadOnlyPerson> myPerson = privilege.getMyReadOnlyPerson();
                //check if the user is logged in
                if (myPerson.isPresent()) {
                    // check if the user is targeting himself
                    if (target.equals(myPerson.get())) {
                        return new CommandResult(String.format(MESSAGE_VIEW_EXAMS_PERSON_SUCCESS,
                                target.getAsTextShowOnlyName()), target.getAsTextShowExam());
                    } else {
                        return new CommandResult(Messages.MESSAGE_WRONG_TARGET);
                    }
                } else {
                    return new CommandResult(Messages.MESSAGE_NOT_LOGGED_IN);
                }
            } else {
                return new CommandResult(String.format(MESSAGE_VIEW_EXAMS_PERSON_SUCCESS,
                        target.getAsTextShowOnlyName()), target.getAsTextShowAllExam());
            }
        } catch (IndexOutOfBoundsException ie) {
            return new CommandResult(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
    }

    @Override
    public Command.Category getCategory() {
        return Command.Category.EXAM;
    }

    @Override
    public String getCommandUsageMessage() {
        return MESSAGE_USAGE;
    }
}
