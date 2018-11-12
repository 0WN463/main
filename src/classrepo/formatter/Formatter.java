package classrepo.formatter;

import java.util.ArrayList;
import java.util.List;

import classrepo.data.person.Assessment;
import classrepo.data.person.AssignmentStatistics;
import classrepo.data.person.Printable;
import classrepo.data.person.ReadOnlyExam;
import classrepo.data.person.ReadOnlyPerson;

/**
 * Used for formatting text for display. e.g. for adding text decorations.
 */
public class Formatter {

    /** A decorative prefix added to the beginning of lines printed by AddressBook */
    private static final String LINE_PREFIX = " ";

    /** A platform independent line separator. */
    private static final String LS = System.lineSeparator();

    /** Format of indexed list item */
    private static final String MESSAGE_INDEXED_LIST_ITEM = "%1$d. %2$s";

    /** Offset required to convert between 1-indexing and 0-indexing.  */
    private static final int DISPLAYED_INDEX_OFFSET = 1;

    private static final IndexListFormat DEFAULT_INDEX_LIST_FORMAT = IndexListFormat.SINGLE_SPACED;

    /** Enum to described how an index list should be formatted. */
    private enum IndexListFormat { SINGLE_SPACED, DOUBLE_SPACED }

    /** Formats the given strings for displaying to the user. */
    public static String format(String... messages) {
        StringBuilder sb = new StringBuilder();
        for (String m : messages) {
            sb.append(LINE_PREFIX)
                    .append(m.replace("\n", LS + LINE_PREFIX))
                    .append(LS);
        }
        return sb.toString();
    }

    /**
     * Formats the given list of persons for displaying to the user. */
    public static String format(List<? extends ReadOnlyPerson> persons, PersonListFormat personListFormat) {
        final List<String> formattedPersons = new ArrayList<>();
        IndexListFormat indexListFormat = DEFAULT_INDEX_LIST_FORMAT;
        for (ReadOnlyPerson person : persons) {
            switch (personListFormat) {
            case ALL_PUBLIC_DETAILS: formattedPersons.add(person.getAsTextHidePrivate());
                indexListFormat = IndexListFormat.DOUBLE_SPACED;
                break;
            case ACCOUNT_DETAILS: formattedPersons.add(person.getAsTextShowAccount());
                break;
            case NAMES_ONLY: formattedPersons.add(person.getAsTextShowOnlyName());
                break;
            case FEES_DETAILS: formattedPersons.add(person.getAsTextShowFee());
                break;
            case FEES_DUE_DETAILS: formattedPersons.add(person.getAsTextShowDueFee());
                break;
            default:
                // all enums should be accounted for, asserts false if this statement is reached
                assert false;
                break;
            }
        }
        return format(asIndexedList(formattedPersons, indexListFormat));
    }

    /**
     * Formats a list of strings as an indexed list, using DEFAULT_INDEX_LIST_FORMAT.
     * @see #asIndexedList(List, IndexListFormat)
     * */
    private static String asIndexedList(List<String> listItems) {
        return asIndexedList(listItems, DEFAULT_INDEX_LIST_FORMAT);
    }

    /** Formats a list of strings as an indexed list. */
    private static String asIndexedList(List<String> listItems, IndexListFormat indexListFormat) {
        final StringBuilder builder = new StringBuilder();
        final int firstIndex = 0;
        int displayIndex = firstIndex + DISPLAYED_INDEX_OFFSET;
        for (String listItem : listItems) {
            builder.append(getIndexedListItem(displayIndex, listItem));

            if (indexListFormat == IndexListFormat.SINGLE_SPACED) {
                builder.append("\n");
            } else if (indexListFormat == IndexListFormat.DOUBLE_SPACED) {
                builder.append("\n").append("\n");
            }

            displayIndex++;
        }
        return builder.toString();
    }

    /**
     * Formats a string as an indexed list item.
     * @param visibleIndex index for this listing
     */
    private static String getIndexedListItem(int visibleIndex, String listItem) {
        return String.format(MESSAGE_INDEXED_LIST_ITEM, visibleIndex, listItem);
    }

    /**
     * Returns a concatenated version of the printable strings of each object.
     */
    public static String getPrintableString(boolean showPrivate, Printable... printables) {
        StringBuilder stringChain = new StringBuilder();
        for (Printable i: printables) {
            if (!i.getPrintableString(showPrivate).isEmpty()) {
                stringChain.append(i.getPrintableString(showPrivate)).append("\n");
            }
        }
        return stringChain.toString();
    }

    public static String getPrintableField(boolean showPrivate, boolean isPrivate, String fieldLabel, String value) {
        if (isPrivate) {
            if (showPrivate) {
                return String.format("{private %s: %s}", fieldLabel, value);
            } else {
                return "";
            }
        }
        return String.format("%s: %s", fieldLabel, value);
    }

    /** Formats the given list of exams for displaying to the admin user. */
    public static String formatExam(List<? extends ReadOnlyExam> exams) {
        final List<String> formattedExams = new ArrayList<>();
        for (ReadOnlyExam exam : exams) {
            formattedExams.add(exam.getAsTextShowAll());

        }
        return format(asIndexedList(formattedExams));
    }

    /** Formats the given list of assessments for displaying to the user. */
    public static String formatAssessments(List<? extends Assessment> assessments) {
        final List<String> formattedAssessments = new ArrayList<>();
        for (Assessment assessment : assessments) {
            formattedAssessments.add(assessment.getAsTextShowAll());

        }
        return format(asIndexedList(formattedAssessments));
    }

    /** Formats the given list of statistics for displaying to the user. */
    public static String formatStatistics(List<? extends AssignmentStatistics> statistics) {
        final List<String> formattedStatistics = new ArrayList<>();
        for (AssignmentStatistics stats : statistics) {
            formattedStatistics.add(stats.getAsTextShowAll());
        }
        return format(asIndexedList(formattedStatistics));
    }

    /**
     * Formats a string for an exam to be printed
     */
    public static String getPrintableExam(String examName, String subjectName, String examDate, String examStartTime,
                                          String examEndTime, String examDetails, int takers, boolean isPrivate) {
        final String examFormat = "%1$sExam: %2$s || Subject: %3$s || Date: %4$s || Starts: %5$s || "
            + "Ends: %6$s || Details: %7$s || %9$s: %8$d";
        final String takerWord;
        if (takers == 1) {
            takerWord = "Taker";
        } else {
            takerWord = "Takers";
        }
        if (isPrivate) {
            return String.format(examFormat, "private ", examName, subjectName, examDate,
                    examStartTime, examEndTime, examDetails, takers, takerWord);
        }
        return String.format(examFormat, "", examName, subjectName, examDate,
                examStartTime, examEndTime, examDetails, takers, takerWord);
    }

    /**
     * Formats a string for an assessment to be printed
     */
    public static String getPrintableAssessment(String examName) {
        return examName;
    }

}
