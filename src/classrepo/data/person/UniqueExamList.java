package classrepo.data.person;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import classrepo.common.Utils;
import classrepo.data.exception.DuplicateDataException;

/**
 * A list of all exams sorted by the subject names. Does not allow null elements or duplicates.
 *
 * @see Exam#equals(Object)
 * @see Utils#elementsAreUnique(Collection)
 */
public class UniqueExamList implements Iterable<Exam> {

    private final List<Exam> internalList = new ArrayList<>();

    /**
     * Signals that an operation would have violated the 'no duplicates' property of the list.
     */
    public static class DuplicateExamException extends DuplicateDataException {
        protected DuplicateExamException() {
            super("Operation would result in duplicate exams");
        }
    }

    /**
     * Signals that an operation targeting a specified exam in the list would fail because
     * there is no such matching exam in the list.
     */
    public static class ExamNotFoundException extends Exception {}

    /**
     * Constructs empty exam list.
     */
    public UniqueExamList() {}

    /**
     * Constructs a list from the items in the given collection.
     * @param exams a collection of exams
     * @throws DuplicateExamException if the {@code exams} contains duplicate exams
     */
    public UniqueExamList(Collection<Exam> exams) throws DuplicateExamException {
        if (!Utils.elementsAreUnique(exams)) {
            throw new DuplicateExamException();
        }
        internalList.addAll(exams);
    }

    /**
     * Constructs a shallow copy of the list.
     */
    public UniqueExamList(UniqueExamList source) {
        internalList.addAll(source.internalList);
    }

    /**
     * Checks if the list contains an equivalent base exam as the given argument.
     */
    public boolean contains(ReadOnlyExam toCheck) {
        return internalList.contains(toCheck);
    }

    /**
     * Checks if the list contains an fully equivalent exam as the given argument.
     */
    public boolean containsFully(ReadOnlyExam toCheck) {
        for (Exam e: internalList) {
            if (e.equalsFully(toCheck)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a exam to the list. Performs a sort to ensure list is sorted by subject name.
     *
     * @throws DuplicateExamException if the exam is a duplicate of an existing base exam in the list.
     */
    public void add(Exam toAdd) throws DuplicateExamException {
        if (contains(toAdd)) {
            throw new DuplicateExamException();
        }
        internalList.add(toAdd);
        sort();
    }

    /**
     * Removes the equivalent exam from the list.
     *
     * @throws ExamNotFoundException if no such exam could be found in the list.
     */
    public void remove(ReadOnlyExam toRemove) throws ExamNotFoundException {
        final boolean examFoundAndDeleted = internalList.remove(toRemove);
        if (!examFoundAndDeleted) {
            throw new ExamNotFoundException();
        }
    }

    /**
     * Unmodifiable java List view with elements cast as immutable {@link ReadOnlyExam}s.
     * For use with other methods/libraries.
     * Any changes to the internal list/elements are immediately visible in the returned list.
     */
    public List<ReadOnlyExam> immutableListView() {
        return Collections.unmodifiableList(internalList);
    }

    /**
     * Clears all exams in list.
     */
    public void clear() {
        internalList.clear();
    }

    /**
     * Finds the equivalent base exam from the list.
     *
     * @throws ExamNotFoundException if no such exam could be found in the list.
     */
    public Exam find(ReadOnlyExam exam) throws ExamNotFoundException {
        for (Exam p: internalList) {
            if (p.equals(exam)) {
                return p;
            }
        }
        throw new ExamNotFoundException();
    }

    /**
     * Replaces an exam and adds in a new exam with updated details at the same index
     * @param exam the original exam.
     * @param updatedExam the new exam with updated details.
     * @throws ExamNotFoundException if the original base exam could not be found in the list.
     */
    public void updateExam(Exam exam, Exam updatedExam) throws ExamNotFoundException {
        boolean isExamPresent = false;
        if (internalList.contains(exam)) {
            isExamPresent = true;
            int index = internalList.indexOf(exam);
            internalList.remove(exam);
            internalList.add(index, updatedExam);
        }
        if (!isExamPresent) {
            throw new ExamNotFoundException();
        }
    }

    /**
     * Clear number of takers for all exams
     */
    public void clearTakers() {
        for (Exam e: internalList) {
            e.setTakers(0);
        }
    }

    @Override
    public Iterator<Exam> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueExamList // instanceof handles nulls
                && this.internalList.equals((
                (UniqueExamList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    /**
     * Sorts all exams in list by their subject names.
     */
    public void sort() {
        internalList.sort((Exam name1, Exam name2)->name1.getSubjectName().compareToIgnoreCase(name2.getSubjectName()));
    }
}
