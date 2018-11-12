package classrepo.parser;

import java.util.regex.Pattern;

/**
 * RegexPatterns used by Parser to parse commands.
 */
public class RegexPattern {
    public static final Pattern PERSON_INDEX_ARGS_FORMAT = Pattern.compile("(?<targetIndex>.+)");

    public static final Pattern BOOLEAN_ARGS_FORMAT = Pattern.compile("(?<boolean>.+)");

    public static final Pattern KEYWORDS_ARGS_FORMAT =
            Pattern.compile("(?<keywords>\\S+(?:\\s+\\S+)*)"); // one or more keywords separated by whitespace

    public static final Pattern PERSON_DATA_ARGS_FORMAT = // '/' forward slashes are reserved for delimiter prefixes
            Pattern.compile("(?<name>[^/]+)"
                    + " (?<isPhonePrivate>p?)p/(?<phone>[^/]+)"
                    + " (?<isEmailPrivate>p?)e/(?<email>[^/]+)"
                    + " (?<isAddressPrivate>p?)a/(?<address>[^/]+)"
                    + "(?<tagArguments>(?: t/[^/]+)*)"); // variable number of tags


    public static final Pattern EXAM_DATA_ARGS_FORMAT = // '/' forward slashes are reserved for delimiter prefixes
            Pattern.compile("(?<isPrivate>p?)e/(?<examName>[^/]+)"
                    + " s/(?<subjectName>[^/]+)"
                    + " d/(?<examDate>[^/]+)"
                    + " st/(?<examStartTime>[^/]+)"
                    + " et/(?<examEndTime>[^/]+)"
                    + " dt/(?<examDetails>[^/]+)");

    public static final Pattern FEES_DATA_ARGS_FORMAT =
            Pattern.compile("(?<index>[^/]+)"
                    + " (?<fees>[^/]+)"
                    + " (?<date>[^/]+)");

    public static final Pattern STATISTICS_DATA_ARGS_FORMAT = //'/' forward slashes are reserved for delimiter prefixes
            Pattern.compile("(?<subjectName>[^/]+)"
                    + " (?<isExamPrivate>p?)e/(?<examName>[^/]+)"
                    + " ts/(?<topScorer>[^/]+)"
                    + " av/(?<averageScore>[^/]+)"
                    + " te/(?<totalExamTakers>[^/]+)"
                    + " ab/(?<numberAbsent>[^/]+)"
                    + " tp/(?<totalPass>[^/]+)"
                    + " mm/(?<maxMin>[^/]+)");

    public static final Pattern ATTENDANCE_ARGS_FORMAT = // '/' forward slashes are reserved for delimiter prefixes
            Pattern.compile("(?<targetIndex>.+)"
                    + " d/(?<date>[^/]+)"
                    + " att/(?<isPresent>[0-1])");

    public static final Pattern ATTENDANCE_VIEW_DATE_FORMAT =
            Pattern.compile("d/(?<date>[^/]+)"); // '/' forward slashes are reserved for delimiter prefixes

    public static final Pattern EDIT_EXAM_INDEX_ARGS_FORMAT = Pattern.compile("(?<targetIndex>[^/]+)"
            + "(p/(?<isPrivate>[^/]+))?"
            + "(e/(?<examName>[^/]+))?"
            + "(s/(?<subjectName>[^/]+))?"
            + "(d/(?<examDate>[^/]+))?" // '/' forward slashes are reserved for delimiter prefixes
            + "(st/(?<examStartTime>[^/]+))?"
            + "(et/(?<examEndTime>[^/]+))?"
            + "(dt/(?<examDetails>[^/]+))?");

    public static final Pattern ASSESSMENT_DATA_ARGS_FORMAT = //'/' forward slashes are reserved for delimiter prefixes
            Pattern.compile("(?<examName>[^/]+)");

    /**
     * Used for initial separation of command word and args.
     */
    public static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");
}
