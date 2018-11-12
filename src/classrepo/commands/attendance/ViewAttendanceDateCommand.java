//@@author meisbokai

package classrepo.commands.attendance;

import static classrepo.common.Messages.MESSAGE_DATE_CONSTRAINTS;
import static classrepo.common.Utils.isValidDate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import classrepo.commands.Command;
import classrepo.commands.commandresult.CommandResult;
import classrepo.common.Messages;
import classrepo.data.exception.IllegalValueException;
import classrepo.data.person.details.Name;

/**
 *  Lists all the people who were present on a particular date.
 */
public class ViewAttendanceDateCommand extends Command {

    public static final String COMMAND_WORD = "viewAttenDate";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Views the attendance of the date. \n"
            + "Parameters: d/DATE \n"
            + "\tExample: " + COMMAND_WORD + " " + "d/28-10-2018";
    public static final String MESSAGE_SUCCESS = "Attendance for the given date, ";

    private String date;

    // Constructor
    public ViewAttendanceDateCommand(String date) throws IllegalValueException {
        if (!isValidDate(date) && !"0".equals(date)) {
            throw new IllegalValueException(MESSAGE_DATE_CONSTRAINTS);
        }
        this.date = date;
    }

    /**
     * Constructor used for Privileges
     * Command constructed has no functionality
     * */
    public ViewAttendanceDateCommand() {
        // Does nothing
    }

    @Override
    public CommandResult execute() {
        try {
            String outputDate = date;
            String present = "";
            String absent = "";
            if ("0".equals(date)) {
                outputDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            }
            final List<Name> listOfPresent = addressBook.getPresentPeople(date);
            final List<Name> listOfAbsent = addressBook.getAbsentPeople(date);
            for (Name n: listOfPresent) {
                present += (n + "\n");
            }
            for (Name n: listOfAbsent) {
                absent += (n + "\n");
            }

            return new CommandResult(MESSAGE_SUCCESS + outputDate + ":\n"
                    + "Present\n" + present + "\n"
                    + "Absent\n" + absent + "\n");

        } catch (IndexOutOfBoundsException ie) {
            return new CommandResult(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
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
