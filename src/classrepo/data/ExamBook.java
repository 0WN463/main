package classrepo.data;

import classrepo.data.person.Exam;
import classrepo.data.person.ReadOnlyExam;
import classrepo.data.person.UniqueExamList;

/**
 * Represents the entire exam book. Contains the data of the exam book.
 */
public class ExamBook {

    private final UniqueExamList examList;

    /**
     * Creates an empty exam book.
     */
    public ExamBook() {
        examList = new UniqueExamList();
    }

    /**
     * Constructs an exam book with the given data.
     *
     * @param examList external changes to this will not affect this exam book
     */
    public ExamBook(UniqueExamList examList) {
        this.examList = new UniqueExamList(examList);
    }

    public static ExamBook empty() {
        return new ExamBook();
    }

    /**
     * Adds a exam to the exam book.
     *
     * @throws UniqueExamList.DuplicateExamException if an exam with equivalent base data already exists.
     */
    public void addExam(Exam toAdd) throws UniqueExamList.DuplicateExamException {
        examList.add(toAdd);
    }

    /**
     * Removes the equivalent exam from the exam book.
     *
     * @throws UniqueExamList.ExamNotFoundException if no such Exam could be found.
     */
    public void removeExam(ReadOnlyExam toRemove) throws UniqueExamList.ExamNotFoundException {
        examList.remove(toRemove);
    }

    /**
     * Clears all exams from the exam book.
     */
    public void clear() {
        examList.clear();
    }

    /**
     * Defensively copied UniqueExamList of all exams in the exam book at the time of the call.
     */
    public UniqueExamList getAllExam() {
        return new UniqueExamList(examList);
    }

    /**
     * Edits an exam in the ExamBook, potentially changing its index number when listing again
     *
     * @throws UniqueExamList.ExamNotFoundException if no such exam could be found.
     * @throws UniqueExamList.DuplicateExamException if the new exam already exists in the exam book.
     */
    public void editExam(ReadOnlyExam target, Exam editedExam)
            throws UniqueExamList.ExamNotFoundException, UniqueExamList.DuplicateExamException {
        if (examList.contains(target)) {
            examList.add(editedExam);
            examList.remove(target);
        } else {
            throw new UniqueExamList.ExamNotFoundException();
        }
    }

    /**
     * Finds and returns the given exam in the ExamBook
     *
     * @throws UniqueExamList.ExamNotFoundException if no such Exam could be found.
     */
    public Exam findExam(ReadOnlyExam exam) throws UniqueExamList.ExamNotFoundException {
        return examList.find(exam);
    }

    /**
     * Checks if an equivalent exam exists in the exam book.
     */
    public boolean contains(ReadOnlyExam exam) {
        return examList.containsFully(exam);
    }

    /**
     * Updates a particular exam to its new value at the same index number
     * @param exam the original exam
     * @param updatedExam the new exam to be updated to
     * @throws UniqueExamList.ExamNotFoundException if no such Exam could be found
     */
    public void updateExam(Exam exam, Exam updatedExam) throws UniqueExamList.ExamNotFoundException {
        examList.updateExam(exam, updatedExam);
    }

    /**
     * Clears number of takers for all exams
     */
    public void clearTakers() {
        examList.clearTakers();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ExamBook // instanceof handles nulls
                && this.examList.equals(((ExamBook) other).examList));
    }

    @Override
    public int hashCode() {
        return examList.hashCode();
    }
}
