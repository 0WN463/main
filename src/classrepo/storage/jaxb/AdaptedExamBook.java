package classrepo.storage.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import classrepo.data.ExamBook;
import classrepo.data.exception.IllegalValueException;
import classrepo.data.person.Exam;
import classrepo.data.person.UniqueExamList;


/**
 * JAXB-friendly adapted exam book data holder class.
 */
@XmlRootElement(name = "ExamBook")
public class AdaptedExamBook {

    @XmlElement
    private List<AdaptedExam> examsList = new ArrayList<>();

    /**
     * No-arg constructor for JAXB use.
     */
    public AdaptedExamBook() {}

    /**
     * Converts a given AddressBook into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created AdaptedExamBook
     */
    public AdaptedExamBook(ExamBook source) {
        examsList = new ArrayList<>();
        source.getAllExam().forEach(exam -> examsList.add(new AdaptedExam(exam)));
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
        return examsList.stream().anyMatch(AdaptedExam::isAnyRequiredFieldMissing);
    }

    /**
     * Converts this jaxb-friendly {@code AdaptedExamBook} object into the corresponding(@code ExamBook} object.
     * @throws IllegalValueException if there were any data constraints violated in the adapted exam
     */
    public ExamBook toModelType() throws IllegalValueException {
        final List<Exam> examTempList = new ArrayList<>();
        for (AdaptedExam exam : examsList) {
            examTempList.add(exam.toModelType());
        }
        return new ExamBook(new UniqueExamList(examTempList));
    }
}
