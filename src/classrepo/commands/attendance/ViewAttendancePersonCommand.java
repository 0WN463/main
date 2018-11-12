//@@author meisbokai

package classrepo.commands.attendance;

import classrepo.commands.commandformat.indexformat.IndexFormatCommand;
import classrepo.commands.commandformat.indexformat.ObjectTargeted;
import classrepo.commands.commandresult.CommandResult;
import classrepo.common.Messages;
import classrepo.data.person.Person;
import classrepo.data.person.UniquePersonList;

/**
 *  Lists all the dates where the person's attendance has been taken.
 */
public class ViewAttendancePersonCommand extends IndexFormatCommand {

    public static final String COMMAND_WORD = "viewAttenPerson";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Views the attendance of a student. \n"
            + "Parameters: INDEX \n"
            + "\tExample: " + COMMAND_WORD + " " + "1";

    public static final String MESSAGE_SUCCESS = "Attendance for student, ";

    // Constructor
    public ViewAttendancePersonCommand(int targetIndex) {
        setTargetIndex(targetIndex, ObjectTargeted.PERSON);
    }

    /**
     * Constructor used for Privileges
     * Command constructed has no functionality
     * */
    public ViewAttendancePersonCommand() {
        // Does nothing
    }

    @Override
    public CommandResult execute() {
        try {
            Person person = addressBook.findPerson(getTargetPerson());
            final String output = person.viewAttendanceMethod();
            return new CommandResult(MESSAGE_SUCCESS + person.getName() + ":\n" + output);

        } catch (IndexOutOfBoundsException ie) {
            return new CommandResult(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        } catch (UniquePersonList.PersonNotFoundException pnfe) {
            return new CommandResult(Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK);
        }
    }

    @Override
    public String getCommandUsageMessage() {
        return MESSAGE_USAGE;
    }

    @Override
    public Category getCategory() {
        return Category.ATTENDANCE;
    }
}
