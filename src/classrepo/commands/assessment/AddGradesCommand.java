package classrepo.commands.assessment;

import classrepo.commands.commandformat.indexformat.IndexFormatCommand;
import classrepo.commands.commandformat.indexformat.ObjectTargeted;
import classrepo.commands.commandresult.CommandResult;
import classrepo.common.Messages;
import classrepo.data.exception.IllegalValueException;
import classrepo.data.person.Assessment;
import classrepo.data.person.Grades;
import classrepo.data.person.Person;
import classrepo.data.person.UniquePersonList;

/**
 * Adds a grade for specific assessment to chosen person based on person index and assessment index.
 */
public class AddGradesCommand extends IndexFormatCommand {

    public static final String COMMAND_WORD = "addgrades";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Adds grades to a person based on person index"
            + " and grades index.\n\t"
            + "Parameters: PERSON_INDEX ASSESSMENT_INDEX GRADE\n\t"
            + "Example: " + COMMAND_WORD
            + " 1 2 85";

    public static final String MESSAGE_ADD_GRADE_SUCCESS = "Grade has been added to %1$s for %2$s assessment";

    private double gradesVal;

    /**
     * Constructor used for Privileges
     * Command constructed has no functionality
     * */
    public AddGradesCommand() { }

    /**
     * Convenience constructor using raw values.
     *
     */
    public AddGradesCommand (int targetPersonIndex, int targetAssessIndex, double gradesVal) {
        setTargetIndex(targetPersonIndex, ObjectTargeted.PERSON);
        setTargetIndex(targetAssessIndex, ObjectTargeted.ASSESSMENT);
        this.gradesVal = gradesVal;
    }

    @Override
    public CommandResult execute() {
        try {
            final Person person = getTargetPerson();

            final Assessment targetAssess = getTargetAssessment();
            person.addAssessment(targetAssess);

            Grades grade = new Grades(gradesVal);

            targetAssess.addGrade(person, grade);

            return new CommandResult(String.format(MESSAGE_ADD_GRADE_SUCCESS, person.getName(), targetAssess));

        } catch (UniquePersonList.PersonNotFoundException pnf) {
            return new CommandResult(Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK);
        } catch (AssessmentIndexOutOfBoundsException aie) {
            return new CommandResult(Messages.MESSAGE_INVALID_ASSESSMENT_DISPLAYED_INDEX);
        } catch (IndexOutOfBoundsException pie) {
            return new CommandResult(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        } catch (IllegalValueException ive) {
            return new CommandResult(Messages.MESSAGE_INVALID_GRADES);
        }
    }

    @Override
    public boolean isMutating() {
        return true;
    }

    @Override
    public Category getCategory() {
        return Category.ASSESSMENT;
    }

    @Override
    public String getCommandUsageMessage() {
        return MESSAGE_USAGE;
    }

}
