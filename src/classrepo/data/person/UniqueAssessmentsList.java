package classrepo.data.person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import classrepo.common.Utils;
import classrepo.data.exception.DuplicateDataException;

/**
 * A list of assessments. Does not allow null elements or duplicates.
 *
 * @see Person#equals(Object)
 * @see Utils#elementsAreUnique(Collection)
 */
public class UniqueAssessmentsList implements Iterable<Assessment> {

    private final List<Assessment> internalList = new ArrayList<>();

    /**
     * Signals that an operation would have violated the 'no duplicates' property of the list.
     */
    public static class DuplicateAssessmentException extends DuplicateDataException {
        protected DuplicateAssessmentException() {
            super("Operation would result in duplicate assessments");
        }
    }


    /**
     * Signals that an operation would have violated the 'no duplicates' property of the list.
     */
    public static class DuplicateGradesException extends DuplicateDataException {
        protected DuplicateGradesException() {
            super("Operation would result in duplicate assessments");
        }
    }

    /**
     * Signals that an operation targeting a specified assessment in the list would fail because
     * there is no such matching assessment in the list.
     */
    public static class AssessmentNotFoundException extends Exception {}

    /**
     * Constructs empty assessment list.
     */
    public UniqueAssessmentsList() {}

    /**
     * Constructs an assessment list with the given assessments.
     */
    public UniqueAssessmentsList(Assessment... assessments) throws DuplicateAssessmentException {
        final List<Assessment> initialTags = Arrays.asList(assessments);
        if (!Utils.elementsAreUnique(initialTags)) {
            throw new DuplicateAssessmentException();
        }
        internalList.addAll(initialTags);
    }

    /**
     * Constructs a list from the items in the given collection.
     * @param assessments a collection of assessments
     * @throws DuplicateAssessmentException if the {@code assessment} contains duplicate assessments
     */
    public UniqueAssessmentsList(Collection<Assessment> assessments) throws DuplicateAssessmentException {
        if (!Utils.elementsAreUnique(assessments)) {
            throw new DuplicateAssessmentException();
        }
        internalList.addAll(assessments);
    }

    /**
     * Constructs a shallow copy of the list.
     */
    public UniqueAssessmentsList(UniqueAssessmentsList source) {
        internalList.addAll(source.internalList);
    }

    /**
     * Unmodifiable java List view with elements cast as immutable {@link Assessment}s.
     * For use with other methods/libraries.
     * Any changes to the internal list/elements are immediately visible in the returned list.
     */
    public List<Assessment> immutableListView() {
        return Collections.unmodifiableList(internalList);
    }


    /**
     * Checks if the list contains an equivalent assessment as the given argument.
     */
    public boolean contains(Assessment toCheck) {
        return internalList.contains(toCheck);
    }

    /**
     * Adds an assessment to the list.
     *
     * @throws DuplicateAssessmentException if the assessment to add is a duplicate of an existing assessment in the
     * list.
     */
    public void add(Assessment toAdd) throws DuplicateAssessmentException {
        if (contains(toAdd)) {
            throw new DuplicateAssessmentException();
        }
        internalList.add(toAdd);
    }

    /**
     * Finds the equivalent assessment from the list.
     *
     * @throws AssessmentNotFoundException if no such assessment could be found in the list.
     */
    public Assessment find(Assessment assessment) throws AssessmentNotFoundException {
        for (Assessment p: internalList) {
            if (p.equals(assessment)) {
                return p;
            }
        }
        throw new AssessmentNotFoundException();
    }

    /**
     * Removes the equivalent assessment from the list.
     *
     * @throws AssessmentNotFoundException if no such assessment could be found in the list.
     */
    public void remove(Assessment toRemove) throws AssessmentNotFoundException {
        final boolean assessmentFoundAndDeleted = internalList.remove(toRemove);
        if (!assessmentFoundAndDeleted) {
            throw new AssessmentNotFoundException();
        }
    }

    /**
     * Clears all assessments in list.
     */
    public void clear() {
        internalList.clear();
    }

    /** Finds and returns the assessment which has the given name
     * @param examName
     * @return The assessment which matches the name. This should be guaranteed to be unique.
     * @throws AssessmentNotFoundException
     */
    public Assessment findAssessmentByName(String examName) throws AssessmentNotFoundException {
        for (Assessment p: internalList) {
            if (p.getExamName().equals(examName)) {
                return p;
            }
        }
        throw new AssessmentNotFoundException();
    }

    @Override
    public Iterator<Assessment> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueAssessmentsList // instanceof handles nulls
                && this.internalList.equals((
                (UniqueAssessmentsList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }
}
