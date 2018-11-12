package classrepo.commands.exams;

import classrepo.commands.commandformat.indexformat.IndexFormatCommand;
import classrepo.commands.commandformat.indexformat.ObjectTargeted;
import classrepo.commands.commandresult.CommandResult;
import classrepo.common.Messages;
import classrepo.data.person.Exam;
import classrepo.data.person.Person;
import classrepo.data.person.UniqueExamList;
import classrepo.data.person.UniquePersonList;

/**
 * Registers a person identified using its last displayed index for a exam identified using its last displayed index.
 */
public class RegisterExamCommand extends IndexFormatCommand {
    public static final String COMMAND_WORD = "regexam";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Registers a person identified by the index number in the last person listing "
            + "for a exam identified by the index number in the last exam listing.\n\t"
            + "Parameters: PERSON_INDEX EXAM_INDEX\n\t"
            + "Example: " + COMMAND_WORD + " 1 1";

    public static final String MESSAGE_REGISTER_EXAM_SUCCESS = "Exam has been registered to person: %1$s!\n"
            + "Viewing exams of person: %1$s";
    public static final String MESSAGE_EXAM_ALREADY_REGISTERED =
            "The person is already registered under the targeted exam!";
    public static final int REQUIRED_ARGUMENTS = 2;

    /**
     * Constructor used for Privileges
     * Command constructed has no functionality
     * */
    public RegisterExamCommand() {
    }

    /**
     * Sets the indexes required for execution.
     */
    public RegisterExamCommand(int targetVisibleIndex, int targetExamIndex) {
        setTargetIndex(targetVisibleIndex, ObjectTargeted.PERSON);
        setTargetIndex(targetExamIndex, ObjectTargeted.EXAM);
    }

    @Override
    public CommandResult execute() {
        try {
            final Person personToEdit = getTargetPerson();
            final Exam exam = getTargetExam();
            if (!personToEdit.isExamPresent(exam)) {
                Exam originalExam = new Exam(exam);
                exam.setTakers(exam.getTakers() + 1);
                personToEdit.addExam(exam);
                addressBook.updateExam(originalExam, exam);
                return new CommandResult(String.format(MESSAGE_REGISTER_EXAM_SUCCESS,
                        personToEdit.getAsTextShowOnlyName()), personToEdit.getAsTextShowAllExam());
            } else {
                return new CommandResult(MESSAGE_EXAM_ALREADY_REGISTERED);
            }
        } catch (ExamIndexOutOfBoundsException eie) {
            return new CommandResult(Messages.MESSAGE_INVALID_EXAM_DISPLAYED_INDEX);
        } catch (IndexOutOfBoundsException ie) {
            return new CommandResult(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        } catch (UniquePersonList.PersonNotFoundException pnfe) {
            return new CommandResult(Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK);
        } catch (UniqueExamList.ExamNotFoundException enfe) {
            return new CommandResult(Messages.MESSAGE_EXAM_NOT_IN_EXAMBOOK);
        }
    }

    @Override
    public boolean isMutating() {
        return true;
    }

    @Override
    public boolean isExamMutating() {
        return true;
    }

    @Override
    public Category getCategory() {
        return Category.EXAM;
    }

    @Override
    public String getCommandUsageMessage() {
        return MESSAGE_USAGE;
    }
}
