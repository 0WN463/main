package classrepo.data.person;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import classrepo.data.account.Account;
import classrepo.data.person.details.Address;
import classrepo.data.person.details.Email;
import classrepo.data.person.details.Name;
import classrepo.data.person.details.Phone;
import classrepo.data.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated.
 */
public class Person implements ReadOnlyPerson {

    private Name name;
    private Phone phone;
    private Email email;
    private Address address;
    private Account account;
    private Fees fees;
    private final Set<Tag> tags = new HashSet<>();
    private final Set<Exam> exams = new HashSet<>();
    private final Set<Assessment> assessments = new HashSet<>();
    private Attendance attendance;

    /**
     * Assumption: Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
        attendance = new Attendance();
        this.fees = new Fees();
    }

    /**
     * Assumption: Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags, Set<Exam> exams) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
        attendance = new Attendance();
        this.fees = new Fees();
        this.exams.addAll(exams);
    }

    public Person(ReadOnlyPerson source) {
        this(source.getName(), source.getPhone(), source.getEmail(), source.getAddress(), source.getTags());
    }

    /**
     * Only update the assessment when called in AddAssessmentCommand
     */
    public void addAssessment(Assessment assessment) {
        this.assessments.add(assessment);
    }

    /**
     * Only update the fees when called in EditFeesCommand
     * @param fees
     */
    public void setFees(Fees fees) {
        this.fees = fees;
        if (this.fees.duedate.equals("00-00-0000")) {
            this.fees.getEdited(false);
        } else {
            this.fees.getEdited(true);
        }
    }

    /**
     * Replaces this person's tags with the tags in {@code replacement}.
     */
    public void setTags(Set<Tag> replacement) {
        tags.clear();
        tags.addAll(replacement);
    }

    /**
     * Only adds the exam when called in RegisterExamCommand
     * @param exam to add into person
     */
    public void addExam(Exam exam) {
        exams.add(exam);
    }

    /**
     * Checks if the exam is already registered
     */
    public boolean isExamPresent(ReadOnlyExam exam) {
        boolean isPresent = false;
        if (exams.contains(exam)) {
            isPresent = true;
        }
        return isPresent;
    }

    /**
     * Removes the specified exam
     * @param exam to remove from person
     */
    public void removeExam(ReadOnlyExam exam) {
        exams.remove(exam);
    }

    /**
     * Clears all exams
     */
    public void clearExams() {
        exams.clear();
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void removeAccount() {
        account = null;
    }

    public Set<Assessment> getAssessments() {
        return new HashSet<>(assessments);
    }

    /**
     * Checks if the assessment is already added
     */
    public boolean isAssessmentPresent(Assessment assessment) {
        boolean assessmentPresent = false;
        if (assessments.contains(assessment)) {
            assessmentPresent = true;
        }
        return assessmentPresent;
    }

    /**
     * Removes the specified assessment
     * @param assessment to remove from person
     */
    public void removeAssessment(Assessment assessment) {
        assessments.remove(assessment);
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public Phone getPhone() {
        return phone;
    }

    @Override
    public Email getEmail() {
        return email;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public Fees getFees() {
        return fees; }
    @Override
    public Set<Exam> getExams() {
        return new HashSet<>(exams);
    }

    @Override
    public Set<Tag> getTags() {
        return new HashSet<>(tags);
    }

    @Override
    public Optional<Account> getAccount() {
        return Optional.ofNullable(account);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ReadOnlyPerson // instanceof handles nulls
                && this.isSameStateAs((ReadOnlyPerson) other));
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, fees, tags);
    }

    @Override
    public String toString() {
        return getAsTextShowAll();
    }

    @Override
    public Attendance getAttendance() {
        return attendance;
    }

    /** Has a boolean to check if the date is a duplicate **/
    public boolean updateAttendanceMethod(String date, Boolean isPresent, Boolean overWrite) {
        boolean duplicateDate = attendance.addAttendance(date, isPresent, overWrite);
        return duplicateDate;
    }

    /** Method to allow user to view the attendance of a person **/
    public String viewAttendanceMethod() {
        return attendance.viewAttendance();
    }

    /** Replaces the attendance if there is already a duplicate **/
    public boolean replaceAttendanceMethod(String date, Boolean isPresent, Boolean overWrite) {
        boolean duplicateDate = attendance.addAttendance(date, isPresent, overWrite);
        return duplicateDate;
    }

    /** Method to get the attendance of a particular date **/
    public Boolean viewAttendanceDateMethod(String date) {
        return attendance.viewAttendanceDate(date);
    }

    /** Setter for attendance **/
    public void setAttendance(Attendance attendance) {
        this.attendance = attendance;
    }
}
