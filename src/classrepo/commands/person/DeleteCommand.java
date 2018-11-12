package classrepo.commands.person;

import java.util.List;
import java.util.Set;

import classrepo.commands.commandformat.indexformat.IndexFormatCommand;
import classrepo.commands.commandresult.CommandResult;
import classrepo.common.Messages;
import classrepo.data.person.Exam;
import classrepo.data.person.ReadOnlyPerson;
import classrepo.data.person.UniqueExamList;
import classrepo.data.person.UniquePersonList.PersonNotFoundException;
import classrepo.privilege.Privilege;

/**
 * Deletes a person identified using its last displayed index from the address book.
 */
public class DeleteCommand extends IndexFormatCommand {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Deletes the person identified by the index number used in the last person listing.\n\t"
            + "Parameters: INDEX\n\t"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";
    public static final String MESSAGE_DELETING_SELF = "You cannot delete the account your are logged-in as!";

    @Override
    public CommandResult execute() {
        try {
            final ReadOnlyPerson target = getTargetReadOnlyPerson();
            privilege.checkTargetIsSelf(target);
            addressBook.removePerson(target);
            final List<ReadOnlyPerson> updatedList = addressBook.getAllPersons().immutableListView();

            Set<Exam> examSet = target.getExams();
            for (Exam e: examSet) {
                Exam updatedExam = new Exam(e);
                updatedExam.setTakers(updatedExam.getTakers() - 1);
                examBook.updateExam(e, updatedExam);
                addressBook.updateExam(e, updatedExam);
            }
            return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, target), updatedList);
        } catch (IndexOutOfBoundsException ie) {
            return new CommandResult(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        } catch (PersonNotFoundException pnfe) {
            return new CommandResult(Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK);
        } catch (Privilege.SelfModifyingException ste) {
            return new CommandResult(MESSAGE_DELETING_SELF);
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
        return Category.PERSON;
    }

    @Override
    public String getCommandUsageMessage() {
        return MESSAGE_USAGE;
    }
}
