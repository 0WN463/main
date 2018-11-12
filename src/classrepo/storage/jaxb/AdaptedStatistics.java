package classrepo.storage.jaxb;

import javax.xml.bind.annotation.XmlElement;

import classrepo.common.Utils;
import classrepo.data.exception.IllegalValueException;
import classrepo.data.person.AssignmentStatistics;

/**
 * JAXB-friendly adapted statistics data holder class.
 */
public class AdaptedStatistics {
    @XmlElement(required = true)
    private String examName;
    @XmlElement(required = true)
    private double averageScore;
    @XmlElement(required = true)
    private int totalExamTakers;
    @XmlElement (required = true)
    private double maxScore;
    @XmlElement (required = true)
    private double minScore;

    /**
     * No-arg constructor for JAXB use.
     */
    public AdaptedStatistics() {}

    /**
     * Converts a given statistic into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created AdaptedStatistics
     */
    public AdaptedStatistics(AssignmentStatistics source) {
        examName = source.getExamName();

        averageScore = source.getAverageScore();

        totalExamTakers = source.getTotalExamTakers();

        maxScore = source.getMaxScore();

        minScore = source.getMinScore();
    }

    /**
     * Returns true if any required field is missing.
     *
     * JAXB does not enforce (required = true) without a given XML schema.
     * Since we do most of our validation using the data class constructors, the only extra logic we need
     * is to ensure that every xml element in the document is present. JAXB sets missing elements as null,
     * so we check for that.
     */
    public boolean isAnyRequiredFieldMissing() {
        return Utils.isAnyNull(examName, averageScore, totalExamTakers, maxScore, minScore);
    }

    /**
     * Converts this jaxb-friendly adapted statistics object into the Assignment Statistics object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted statistics
     */
    public AssignmentStatistics toModelType() throws IllegalValueException {
        final String examName = this.examName;
        final double averageScore = this.averageScore;
        final int totalExamTakers = this.totalExamTakers;
        final double maxScore = this.maxScore;
        final double minScore = this.minScore;
        return new AssignmentStatistics(examName, averageScore, totalExamTakers, maxScore, minScore);
    }
}
