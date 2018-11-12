package classrepo.data.person;

import static classrepo.common.Utils.isValidDate;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import classrepo.common.Messages;
import classrepo.data.exception.IllegalValueException;

/**
 * Represents an exam in the exam book.
 * Guarantees: details are present and not null, field values are validated.
 */
public class Exam implements ReadOnlyExam {

    public static final String EXAM_NAME_EXAMPLE = "Midterms";
    public static final String SUBJECT_NAME_EXAMPLE = "Mathematics";
    public static final String EXAM_DATE_EXAMPLE = "01-12-2018";
    public static final String EXAM_START_TIME_EXAMPLE = "09:00";
    public static final String EXAM_END_TIME_EXAMPLE = "10:00";
    public static final String EXAM_DETAILS_EXAMPLE = "Held in MPSH";

    public static final String MESSAGE_TIME_CONSTRAINTS =
            "Time should be in 24 hours format HH:MM and valid.";
    public static final String MESSAGE_TIME_INTERVAL_CONSTRAINTS =
            "Time interval is inaccurate.";

    private static final String TIME_VALIDATION_REGEX = "(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]";
    private static final String TIME_PATTERN = "HH:mm";

    private String examName;
    private String subjectName;
    private String examDate;
    private String examStartTime;
    private String examEndTime;
    private String examDetails;
    private int takers = 0;
    private boolean isPrivate;

    /**
     * Validates a fresh given exam.
     *
     * @throws IllegalValueException if given exam date or time string is invalid.
     * @throws IllegalValueException if time interval is invalid.
     */
    public Exam(String examName, String subjectName, String examDate, String examStartTime,
                String examEndTime, String examDetails, boolean isPrivate) throws IllegalValueException {
        this.examName = examName.trim();
        this.subjectName = subjectName.trim();
        String trimmedDate = examDate.trim();
        if (!isValidDate(trimmedDate)) {
            throw new IllegalValueException(Messages.MESSAGE_DATE_CONSTRAINTS);
        }
        this.examDate = trimmedDate;
        String trimmedStartTime = examStartTime.trim();
        String trimmedEndTime = examEndTime.trim();
        if (!isValidTime(trimmedStartTime) || !isValidTime(trimmedEndTime)) {
            throw new IllegalValueException(MESSAGE_TIME_CONSTRAINTS);
        }
        if (!isValidTimeInterval(trimmedStartTime, trimmedEndTime)) {
            throw new IllegalValueException(MESSAGE_TIME_INTERVAL_CONSTRAINTS);
        }
        this.examStartTime = trimmedStartTime;
        this.examEndTime = trimmedEndTime;
        this.examDetails = examDetails.trim();
        this.isPrivate = isPrivate;
    }

    /**
     * Validates a given exam.
     *
     * @throws IllegalValueException if given exam date or time string is invalid.
     * @throws IllegalValueException if time interval is invalid.
     */
    public Exam(String examName, String subjectName, String examDate, String examStartTime,
                String examEndTime, String examDetails, int takers, boolean isPrivate) throws IllegalValueException {
        this(examName, subjectName, examDate, examStartTime, examEndTime, examDetails, isPrivate);
        this.takers = takers;
    }

    /**
     * Copy constructor
     */
    public Exam(ReadOnlyExam original) {
        examName = original.getExamName();
        subjectName = original.getSubjectName();
        examDate = original.getExamDate();
        examStartTime = original.getExamStartTime();
        examEndTime = original.getExamEndTime();
        examDetails = original.getExamDetails();
        takers = original.getTakers();
        isPrivate = original.isPrivate();
    }

    /**
     * Checks if a given string is a valid time.
     */
    public static boolean isValidTime(String value) {
        return value.matches(TIME_VALIDATION_REGEX);
    }

    /**
     * Checks if a given time interval is valid
     */
    public static boolean isValidTimeInterval(String examStart, String examEnd) {
        boolean isValid;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_PATTERN);
        try {
            LocalTime startTime = LocalTime.parse(examStart, formatter);
            LocalTime endTime = LocalTime.parse(examEnd, formatter);
            isValid = startTime.isBefore(endTime);
        } catch (DateTimeParseException ex) {
            isValid = false;
        }
        return isValid;
    }

    @Override
    public String toString() {
        return getAsTextShowAll();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ReadOnlyExam // instanceof handles nulls
                && this.isSameStateAs((ReadOnlyExam) other)); // state check
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(examName, subjectName, examDate, examStartTime, examEndTime,
                examDetails, takers, isPrivate);
    }

    @Override
    public String getExamName() {
        return examName;
    }

    @Override
    public String getSubjectName() {
        return subjectName;
    }

    @Override
    public String getExamDate() {
        return examDate;
    }

    @Override
    public String getExamStartTime() {
        return examStartTime;
    }

    @Override
    public String getExamEndTime() {
        return examEndTime;
    }

    @Override
    public String getExamDetails() {
        return examDetails;
    }

    @Override
    public int getTakers() {
        return takers;
    }

    @Override
    public boolean isPrivate() {
        return isPrivate;
    }

    public void setTakers(int takers) {
        this.takers = takers;
    }

    /**
     * To check if an exam is fully equal to another exam
     */
    public boolean equalsFully(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ReadOnlyExam // instanceof handles nulls
                && this.isFullyEqual((ReadOnlyExam) other)); // state check
    }
}
