package classrepo.commands.commandformat.indexformat;

import java.util.HashMap;

import classrepo.commands.Command;
import classrepo.data.person.Assessment;
import classrepo.data.person.AssignmentStatistics;
import classrepo.data.person.Exam;
import classrepo.data.person.Person;
import classrepo.data.person.ReadOnlyExam;
import classrepo.data.person.ReadOnlyPerson;
import classrepo.data.person.UniqueExamList;
import classrepo.data.person.UniquePersonList;
import classrepo.ui.Gui;

/** The abstract class for commands with the format of KEYWORD ... KEYWORD */
public abstract class IndexFormatCommand extends Command {
    private HashMap<ObjectTargeted, Integer> targetMap = new HashMap<>();

    /**
     * Extracts the the target (immutable) person in the last shown list from the given arguments.
     *
     * @throws IndexOutOfBoundsException if the target index is out of bounds of the last viewed listing
     */
    protected ReadOnlyPerson getTargetReadOnlyPerson() throws IndexOutOfBoundsException {
        return relevantPersons.get(targetMap.get(ObjectTargeted.PERSON) - Gui.DISPLAYED_INDEX_OFFSET);
    }

    /**
     * Extracts the the target (mutable) person in the last shown list from the given arguments.
     *
     * @throws IndexOutOfBoundsException if the target index is out of bounds of the last viewed listing
     * @throws UniquePersonList.PersonNotFoundException if the target person cannot be found in the address book
     */
    protected Person getTargetPerson() throws IndexOutOfBoundsException, UniquePersonList.PersonNotFoundException {
        return addressBook.findPerson(getTargetReadOnlyPerson());
    }

    /**
     * Extracts the the target assessment in the last shown list from the given arguments.
     * @throws AssessmentIndexOutOfBoundsException if the target index is out of bounds of the last viewed listing
     */
    protected Assessment getTargetAssessment() throws AssessmentIndexOutOfBoundsException {
        try {
            return relevantAssessments.get(targetMap.get(ObjectTargeted.ASSESSMENT) - Gui.DISPLAYED_INDEX_OFFSET);
        } catch (IndexOutOfBoundsException iob) {
            throw new AssessmentIndexOutOfBoundsException(iob.getMessage());
        }
    }

    /**
     * Extracts the the target statistics in the last shown list from the given arguments.
     * @throws IndexOutOfBoundsException if the target index is out of bounds of the last viewed listing
     */
    protected AssignmentStatistics getTargetStatistic() throws IndexOutOfBoundsException {
        return relevantStatistics.get(targetMap.get(ObjectTargeted.STATISTIC) - Gui.DISPLAYED_INDEX_OFFSET);
    }

    /**
     * Extracts the target (mutable) exam in the last shown exam list from the given arguments.
     *
     * @throws ExamIndexOutOfBoundsException if the target exam index is out of bounds of the last viewed exam listing
     * @throws UniqueExamList.ExamNotFoundException if the target exam cannot be found in the exam book
     */
    protected Exam getTargetExam() throws ExamIndexOutOfBoundsException, UniqueExamList.ExamNotFoundException {
        return examBook.findExam(getTargetReadOnlyExam());
    }

    /**
     * Extracts the the target (immutable) exam in the last shown exam list from the given arguments.
     *
     * @throws ExamIndexOutOfBoundsException if the target exam index is out of bounds of the last viewed exam listing
     */
    protected ReadOnlyExam getTargetReadOnlyExam() throws ExamIndexOutOfBoundsException {
        try {
            return relevantExams.get(targetMap.get(ObjectTargeted.EXAM) - Gui.DISPLAYED_INDEX_OFFSET);
        } catch (IndexOutOfBoundsException e) {
            throw new ExamIndexOutOfBoundsException(e.getMessage());
        }

    }

    public void setTargetIndex(int targetIndex, ObjectTargeted objectTargeted) {
        targetMap.put(objectTargeted, targetIndex);
    }

    public int getTargetIndex(ObjectTargeted objectTargeted) {
        return targetMap.get(objectTargeted);
    }
}
