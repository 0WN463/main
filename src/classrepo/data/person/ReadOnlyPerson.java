package classrepo.data.person;

import java.util.Optional;
import java.util.Set;

import classrepo.common.Messages;
import classrepo.data.account.Account;
import classrepo.data.person.details.Address;
import classrepo.data.person.details.Email;
import classrepo.data.person.details.Name;
import classrepo.data.person.details.Phone;
import classrepo.data.tag.Tag;
import classrepo.formatter.Formatter;

/**
 * A read-only immutable interface for a Person in the addressbook.
 * Implementations should guarantee: details are present and not null, field values are validated.
 */
public interface ReadOnlyPerson {
    Name getName();
    Phone getPhone();
    Email getEmail();
    Address getAddress();
    Optional<Account> getAccount();
    Fees getFees();
    Attendance getAttendance();
    Set<Exam> getExams();
    Set<Assessment> getAssessments();

    /**
     * The returned {@code Set} is a deep copy of the internal {@code Set},
     * changes on the returned list will not affect the person's internal tags.
     */
    Set<Tag> getTags();

    default boolean hasAccount() {
        return getAccount().isPresent();
    }

    /**
     * Returns true if the values inside this object is same as those of the other
     * (Note: interfaces cannot override .equals)
     */
    default boolean isSameStateAs(ReadOnlyPerson other) {
        return other == this // short circuit if same object
                || (other != null // this is first to avoid NPE below
                && other.getName().equals(this.getName()) // state checks here onwards
                && other.getPhone().equals(this.getPhone())
                && other.getEmail().equals(this.getEmail())
                && other.getAddress().equals(this.getAddress()));
    }

    /**
     * Formats the person as text, showing only Name.
     */
    default String getAsTextShowOnlyName() {
        return getName().toString();
    }

    /**
     * Formats the person as text, showing all contact details.
     */
    default String getAsTextShowAll() {
        final StringBuilder builder = new StringBuilder();
        final String stringChain = Formatter.getPrintableString(
                true,
                getName(),
                getPhone(),
                getEmail(),
                getAddress());
        getAttendance();

        builder.append(stringChain);
        if (getFees().isEdited()) {
            builder.append(Formatter.getPrintableString(true, getFees()));
        }
        builder.append("Tags: ");
        for (Tag tag : getTags()) {
            builder.append(tag);
        }
        for (Exam exam : getExams()) {
            builder.append("\n");
            if (exam.isPrivate()) {
                builder.append("{");
            }
            builder.append(exam);
            if (exam.isPrivate()) {
                builder.append("}");
            }
        }

        for (Assessment assessment : getAssessments()) {
            builder.append("\n");
            builder.append("Assessment: ").append(assessment).append(" ").append(assessment.getGrade(this));
        }

        getAccount().ifPresent(a -> builder.append('\n')
                .append("User Type: ")
                .append(a.getPrintableString(true)));
        return builder.toString();
    }

    /**
     * Formats a person as text, showing only non-private contact details.
     */
    default String getAsTextHidePrivate() {
        final StringBuilder builder = new StringBuilder();
        final String stringChain = Formatter.getPrintableString(
                false,
                getName(),
                getPhone(),
                getEmail(),
                getAddress(),
                getFees());
        getAttendance();
        builder.append(stringChain)
                .append("Tags: ");
        for (Tag tag : getTags()) {
            builder.append(tag);
        }

        getAccount().ifPresent(a -> builder.append('\n')
                .append("User Type:")
                .append(a.getPrintableString(true)));
        return builder.toString();
    }

    /**
     * Formats a person as text, showing only non-private contact details.
     */
    default String getAsTextShowAccount() {
        final StringBuilder builder = new StringBuilder(getName().getPrintableString(true));
        getAccount().ifPresent(a -> builder.append(" User Type: ").append(a.getPrintableString(true)));
        return builder.toString();
    }

    /**
     * Formats the person as text, showing name and fees and status due.
     */
    default String getAsTextShowDueFee() {
        final StringBuilder builder = new StringBuilder();
        final String stringChain = Formatter.getPrintableString(
                true,
                getName(),
                getFees());
        builder.append(stringChain);
        builder.append("Due!\n");
        return builder.toString();
    }

    /**
     * Formats the person as text, showing name and fees.
     */
    default String getAsTextShowFee() {
        final StringBuilder builder = new StringBuilder();
        final String stringChain = Formatter.getPrintableString(
                true,
                getName());
        builder.append(stringChain);
        if (getFees().isEdited()) {
            builder.append(Formatter.getPrintableString(true, getFees()));
        } else {
            builder.append("No Fees owed!\n");
        }
        return builder.toString();
    }

    /**
     * Formats the person as text, showing name and assessments + grades.
     */
    default String getAsTextShowAssess() {
        final StringBuilder builder = new StringBuilder();
        final String stringChain = Formatter.getPrintableString(true, getName());
        builder.append(stringChain);
        for (Assessment assessment : getAssessments()) {
            if (assessment.getGrade(this) == null) {
                continue;
            }
            builder.append("Assessment: ").append(assessment).append(" ").append(assessment.getGrade(this))
                    .append("\n");
        }
        return builder.toString();
    }

    /**
     * Formats the person as text, showing name and non-private exams.
     */
    default String getAsTextShowExam() {
        final StringBuilder builder = new StringBuilder();
        for (Exam exam : getExams()) {
            if (exam.isPrivate()) {
                continue;
            }
            builder.append(exam).append("\n");
        }
        if (builder.toString().isEmpty()) {
            builder.append(String.format(Messages.MESSAGE_NO_NON_PRIVATE_EXAMS, getName()));
        }
        return builder.toString();
    }

    /**
     * Formats the person as text, showing name and all exams.
     */
    default String getAsTextShowAllExam() {
        final StringBuilder builder = new StringBuilder();
        if (getExams().isEmpty()) {
            builder.append(String.format(Messages.MESSAGE_NO_EXAMS, getName()));
        } else {
            for (Exam exam : getExams()) {
                builder.append(exam).append("\n");
            }
        }
        return builder.toString();
    }
}
