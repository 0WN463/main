package classrepo.data.person;

import java.util.Objects;

/**
 * Represents a statistic in the statistics book.
 */
public class AssignmentStatistics implements Printable {

    public static final String EXAM_NAME_EXAMPLE = "Math Midterms";
    public static final double AVERAGE_SCORE_EXAMPLE = 80.5;
    public static final int TOTAL_EXAM_TAKERS_EXAMPLE = 84; //Number of students who attended test
    public static final double MAX_SCORE_EXAMPLE = 87; //maximum score
    public static final double MIN_SCORE_EXAMPLE = 60; //minimum score

    private String examName;
    private double averageScore;
    private int totalExamTakers;
    private double maxScore;
    private double minScore;


    public AssignmentStatistics() {}

    /**
     * Validates given statistics.
     *
     */
    public AssignmentStatistics(String examName, double averageScore, int totalExamTakers, double maxScore,
                                double minScore) {

        this.examName = examName.trim();
        this.averageScore = averageScore;
        this.totalExamTakers = totalExamTakers;
        this.maxScore = maxScore;
        this.minScore = minScore;
    }

    @Override
    public String toString() {
        return examName + " Average Score: " + averageScore + " Total Exam Takers: "
                + totalExamTakers + " Max Score: " + maxScore + " Min Score: " + minScore;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AssignmentStatistics // instanceof handles nulls
                && this.examName.equals(((AssignmentStatistics) other).examName)); // state check
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(averageScore, totalExamTakers, maxScore, minScore);
    }

    @Override
    public String getPrintableString(boolean showPrivate) {
        return "Exam: " + examName + " || Average: " + averageScore + " || Attendees: "
                + totalExamTakers + " || Max: " + maxScore + " || Min: " + minScore;
    }

    /**
     * Formats the assessment as text to show all.
     */
    public String getAsTextShowAll() {
        final StringBuilder builder = new StringBuilder();
        final String stringChain = getPrintableString(true);
        builder.append(stringChain);
        return builder.toString();
    }

    public String getExamName() {
        return examName;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public int getTotalExamTakers() {
        return totalExamTakers;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public double getMinScore() {
        return minScore;
    }

}
