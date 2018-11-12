package classrepo.storage.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import classrepo.common.Utils;
import classrepo.data.exception.IllegalValueException;
import classrepo.data.person.Exam;


/**
 * JAXB-friendly adapted exam data holder class.
 */
public class AdaptedExam {
    @XmlAttribute(required = true)
    private Boolean isPrivate;
    @XmlElement(required = true)
    private String subjectName;
    @XmlElement(required = true)
    private String examName;
    @XmlElement(required = true)
    private String examDate;
    @XmlElement(required = true)
    private String examStartTime;
    @XmlElement(required = true)
    private String examEndTime;
    @XmlElement (required = true)
    private String examDetails;
    @XmlElement(required = true)
    private int takers;

    /**
     * No-arg constructor for JAXB use.
     */
    public AdaptedExam() {}

    /**
     * Converts a given Exam into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created AdaptedExam
     */
    public AdaptedExam(Exam source) {
        isPrivate = source.isPrivate();

        subjectName = source.getSubjectName();

        examName = source.getExamName();

        examDate = source.getExamDate();

        examStartTime = source.getExamStartTime();

        examEndTime = source.getExamEndTime();

        examDetails = source.getExamDetails();

        takers = source.getTakers();
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
        return Utils.isAnyNull(subjectName, examName, examDate, examStartTime, examEndTime,
                examDetails, takers, isPrivate);
    }

    /**
     * Converts this jaxb-friendly adapted exam object into the Exam object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted exam
     */
    public Exam toModelType() throws IllegalValueException {
        final String subjectName = this.subjectName;
        final String examName = this.examName;
        final String examDate = this.examDate;
        final String examStartTime = this.examStartTime;
        final String examEndTime = this.examEndTime;
        final String examDetails = this.examDetails;
        final int takers = this.takers;
        final boolean isPrivate = this.isPrivate;
        return new Exam(examName, subjectName, examDate, examStartTime, examEndTime, examDetails, takers, isPrivate);
    }
}
