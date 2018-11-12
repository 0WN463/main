package classrepo.storage.jaxb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import classrepo.common.Utils;
import classrepo.data.exception.IllegalValueException;
import classrepo.data.person.Assessment;
import classrepo.data.person.Grades;
import classrepo.data.person.Person;
import classrepo.data.person.UniquePersonList;

/**
 * JAXB-friendly adapted assessment data holder class.
 */
public class AdaptedAssessment {
    @XmlElement(required = true)
    private String examName;

    @XmlElement(required = true)
    private List<Integer> personIndex = new ArrayList<>();

    @XmlElement(required = true)
    private List<Double> grades = new ArrayList<>();

    /**
     * No-arg constructor for JAXB use.
     */
    public AdaptedAssessment() {}

    /**
     * Converts a given Assessment into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created AdaptedAssessment
     */
    public AdaptedAssessment(Assessment source, UniquePersonList allPersons) {
        examName = source.getExamName();
        Map<Person, Grades> allVals = source.getAllGrades();

        for (Map.Entry<Person, Grades> entry : allVals.entrySet()) {
            Person person = entry.getKey();
            int index = allPersons.immutableListView().indexOf(person);
            if (index == -1) {
                continue;
            }
            personIndex.add(index);
            Grades grade = entry.getValue();
            double gradeVal = grade.getValue();
            grades.add(gradeVal);
        }
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
        return Utils.isAnyNull(examName);
    }

    /**
     * Converts this jaxb-friendly adapted assessment object into the Assessment object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted assessment
     */
    public Assessment toModelType(List<Person> personList) throws IllegalValueException {
        final String examName = this.examName;
        Assessment assess = new Assessment(examName);

        for (int i = 0; i < personIndex.size(); i++) {
            final Person personToInsert = personList.get(i);
            personToInsert.addAssessment(assess);
            double gradeVal = grades.get(i);
            final Grades gradeToInsert = new Grades(gradeVal);
            assess.addGrade(personToInsert, gradeToInsert);
        }
        return assess;
    }
}
