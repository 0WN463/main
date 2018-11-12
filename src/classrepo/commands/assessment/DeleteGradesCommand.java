package classrepo.commands.assessment;

import classrepo.commands.commandformat.indexformat.IndexFormatCommand;
import classrepo.commands.commandformat.indexformat.ObjectTargeted;
import classrepo.commands.commandresult.CommandResult;
import classrepo.common.Messages;
import classrepo.data.person.Assessment;
import classrepo.data.person.Person;
import classrepo.data.person.UniquePersonList;

/**
 * Deletes the grades for a person (identified by index in last person listing) for a particular
 * assessment (identified by index in last assessment listing)
 */
public class DeleteGradesCommand extends IndexFormatCommand {
    public static final String COMMAND_WORD = "deletegrades";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Deletes the grades for a person identified by the index number in the last person listing "
            + "for an assessment identified by the index number in the last assessment listing.\n\t"
            + "Parameters: PERSON_INDEX ASSESSMENT_INDEX\n\t"
            + "Example: " + COMMAND_WORD + " 1 1";

    public static final String MESSAGE_DELETE_GRADES_SUCCESS = "Grades for this assessment have been deleted - "
            + "%1$s\n\t";
    public static final String MESSAGE_ASSESSMENT_NOT_PRESENT =
            "The assessment has not been added to this person!";

    /**
     * Constructor used for Privileges
     * Command constructed has no functionality
     * */
    public DeleteGradesCommand() {
    }

    public DeleteGradesCommand(int targetVisibleIndex, int targetAssessmentIndex) {
        setTargetIndex(targetVisibleIndex, ObjectTargeted.PERSON);
        setTargetIndex(targetAssessmentIndex, ObjectTargeted.ASSESSMENT);
    }

    @Override
    public CommandResult execute() {
        try {
            final Person person = addressBook.findPerson(getTargetReadOnlyPerson());
            final Assessment assessment = getTargetAssessment();

            if (!person.isAssessmentPresent(assessment)) {
                return new CommandResult(MESSAGE_ASSESSMENT_NOT_PRESENT);
            } else {
                person.removeAssessment(assessment);
                assessment.removeGrades(person);
                return new CommandResult(String.format(MESSAGE_DELETE_GRADES_SUCCESS, assessment));
            }
        } catch (AssessmentIndexOutOfBoundsException aie) {
            return new CommandResult(Messages.MESSAGE_INVALID_ASSESSMENT_DISPLAYED_INDEX);
        } catch (IndexOutOfBoundsException ie) {
            return new CommandResult(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        } catch (UniquePersonList.PersonNotFoundException pnfe) {
            return new CommandResult(Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK);
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
