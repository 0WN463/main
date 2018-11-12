package classrepo.data.person;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import classrepo.common.Utils;
import classrepo.data.exception.DuplicateDataException;
import classrepo.data.exception.IllegalValueException;
import classrepo.data.person.details.Name;
import classrepo.data.tag.Tag;

/**
 * A list of persons. Does not allow null elements or duplicates.
 *
 * @see Person#equals(Object)
 * @see Utils#elementsAreUnique(Collection)
 */
public class UniquePersonList implements Iterable<Person> {

    private final List<Person> internalList = new ArrayList<>();

    /**
     * Signals that an operation would have violated the 'no duplicates' property of the list.
     */
    public static class DuplicatePersonException extends DuplicateDataException {
        protected DuplicatePersonException() {
            super("Operation would result in duplicate persons");
        }
    }

    /**
     * Signals that an operation targeting a specified person in the list would fail because
     * there is no such matching person in the list.
     */
    public static class PersonNotFoundException extends Exception {}

    /**
     * Constructs empty person list.
     */
    public UniquePersonList() {}

    /**
     * Constructs a list from the items in the given collection.
     * @param persons a collection of persons
     * @throws DuplicatePersonException if the {@code persons} contains duplicate persons
     */
    public UniquePersonList(Collection<Person> persons) throws DuplicatePersonException {
        if (!Utils.elementsAreUnique(persons)) {
            throw new DuplicatePersonException();
        }
        internalList.addAll(persons);
    }

    /**
     * Constructs a shallow copy of the list.
     */
    public UniquePersonList(UniquePersonList source) {
        internalList.addAll(source.internalList);
    }

    /**
     * Unmodifiable java List view with elements cast as immutable {@link ReadOnlyPerson}s.
     * For use with other methods/libraries.
     * Any changes to the internal list/elements are immediately visible in the returned list.
     */
    public List<ReadOnlyPerson> immutableListView() {
        return Collections.unmodifiableList(internalList);
    }

    /**
     * Checks if the list contains an equivalent person as the given argument.
     */
    public boolean contains(ReadOnlyPerson toCheck) {
        return internalList.contains(toCheck);
    }

    /**
     * Adds a person to the list.
     *
     * @throws DuplicatePersonException if the person to add is a duplicate of an existing person in the list.
     */
    public void add(Person toAdd) throws DuplicatePersonException {
        if (contains(toAdd)) {
            throw new DuplicatePersonException();
        }
        internalList.add(toAdd);
    }

    /**
     * Finds the equivalent person from the list.
     *
     * @throws PersonNotFoundException if no such person could be found in the list.
     */
    public Person find(ReadOnlyPerson readOnlyPerson) throws PersonNotFoundException {
        return internalList.stream()
                .filter(p -> p.equals(readOnlyPerson))
                .findAny()
                .orElseThrow(PersonNotFoundException:: new);
    }

    /**
     * Removes the equivalent person from the list.
     *
     * @throws PersonNotFoundException if no such person could be found in the list.
     */
    public void remove(ReadOnlyPerson toRemove) throws PersonNotFoundException {
        final boolean personFoundAndDeleted = internalList.remove(toRemove);
        if (!personFoundAndDeleted) {
            throw new PersonNotFoundException();
        }
    }

    /**
     * Loops through list and appends data to string person.
     *
     */
    public List<ReadOnlyPerson> listdueFees(String date) {
        if (internalList.isEmpty()) {
            return Collections.unmodifiableList(internalList);
        }
        List<Person> copy = new ArrayList<>();
        for (Person p: internalList) {
            if (p.getFees().isEdited()) {
                String p1 = Utils.convertToValidDateStringUser(p.getFees().duedate);
                String currentDate = Utils.convertToValidDateStringSystem(date);
                if (p1.compareTo(currentDate) <= 0) {
                    copy.add(p);
                    try {
                        Set<Tag> temp = new HashSet<>();
                        temp = p.getTags();
                        temp.add(new Tag("feesdue"));
                        p.setTags(temp);
                    } catch (IllegalValueException e) {
                        e.printStackTrace();
                    }
                } else {
                    Set<Tag> temp = new HashSet<>();
                    temp = p.getTags();
                    for (Tag t : temp) {
                        if ("feesdue".equals(t.tagName)) {
                            temp.remove(t);
                        }
                    }
                    p.setTags(temp);
                }
            }
        }
        copy.sort(new Utils.FeesComparator());
        return Collections.unmodifiableList(copy);
    }

    /**
     * Loops through list and appends data to string person.
     *
     */
    public List<ReadOnlyPerson> listFees() {
        if (internalList.isEmpty()) {
            return Collections.unmodifiableList(internalList);
        }
        List<Person> copy = new ArrayList<>();
        for (Person p: internalList) {
            if (p.getFees().isEdited()) {
                copy.add(p);
            }
        }
        copy.sort(new Utils.FeesComparator());
        return Collections.unmodifiableList(copy);
    }

    /**
     * Clears all persons in list.
     */
    public void clear() {
        internalList.clear();
    }

    /** Finds and returns the Person who has the given username in its Account
     * @param username to be matched to a person.
     * @return The Person who matches the username. This should be guaranteed to be unique.
     * @throws PersonNotFoundException Person cannot be found with the given username in internalList
     */
    public Person findPersonByUsername(String username) throws PersonNotFoundException {
        for (Person p: internalList) {
            if (p.getAccount().filter(acc -> acc.getUsername().equals(username)).isPresent()) {
                return p;
            }
        }
        throw new PersonNotFoundException();
    }

    /**Checks if UniquePersonList holds a Person who has given username in its Account
     * @param username of the person to be associated with.
     * @return true if such a Person exists. False otherwise
     */
    public Boolean containsPersonWithUsername(String username) {
        try {
            findPersonByUsername(username);
            return true;
        } catch (PersonNotFoundException pne) {
            return false;
        }
    }

    /** Iterates through the UniquePersonList to check the attendance of each person
     * @param date of which list of present people should generate
     * @return A list of present people
     */
    public List listOfPresentPeople (String date) {
        List <Name> listOfPresent = new ArrayList<>();
        for (Person p: internalList) {
            Boolean isPresent = p.viewAttendanceDateMethod(date);
            if (isPresent) {
                listOfPresent.add(p.getName());
            }
        }
        return listOfPresent;
    }

    /** Iterates through the UniquePersonList to check the attendance of each person
     * @param date of which list of present people should generate
     * @return A list of absent/no attendance(default absent) people
     */
    public List listOfAbsentPeople (String date) {
        List <Name> listOfAbsent = new ArrayList<>();
        for (Person p: internalList) {
            Boolean isPresent = p.viewAttendanceDateMethod(date);
            if (!isPresent) {
                listOfAbsent.add(p.getName());
            }
        }
        return listOfAbsent;
    }

    /**
     * Checks an exam to its new values for all persons
     * @param exam the original exam
     * @param newExam the new exam with updated details
     */
    public void updateExam(Exam exam, Exam newExam) {
        for (Person p: internalList) {
            if (p.isExamPresent(exam)) {
                p.removeExam(exam);
                p.addExam(newExam);
            }
        }
    }

    /**
     * Removes an exam for all persons
     * @param exam the exam to be removed
     */
    public void removeExam(ReadOnlyExam exam) {
        for (Person p: internalList) {
            if (p.isExamPresent(exam)) {
                p.removeExam(exam);
            }
        }
    }

    /**
     * Clears all exams for all persons
     */
    public void clearAllExam() {
        for (Person p: internalList) {
            p.clearExams();
        }
    }

    @Override
    public Iterator<Person> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniquePersonList // instanceof handles nulls
                && this.internalList.equals((
                (UniquePersonList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }
}
