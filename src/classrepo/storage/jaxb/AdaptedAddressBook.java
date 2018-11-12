package classrepo.storage.jaxb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import classrepo.data.AddressBook;
import classrepo.data.exception.IllegalValueException;
import classrepo.data.person.Assessment;
import classrepo.data.person.Person;
import classrepo.data.person.UniqueAssessmentsList;
import classrepo.data.person.UniquePersonList;

/**
 * JAXB-friendly adapted address book data holder class.
 */
@XmlRootElement(name = "AddressBook")
public class AdaptedAddressBook {

    @XmlElement
    private List<AdaptedPerson> persons = new ArrayList<>();

    @XmlElement
    private List<AdaptedAssessment> assessments = new ArrayList<>();

    @XmlElement(name = "password", defaultValue = "default_pw")
    private String password;

    @XmlAttribute
    private boolean isPermAdmin;

    /**
     * No-arg constructor for JAXB use.
     */
    public AdaptedAddressBook() {}
    /**
     * Converts a given AddressBook into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created AdaptedAddressBook
     */
    public AdaptedAddressBook(AddressBook source) {
        persons = new ArrayList<>();
        source.getAllPersons().forEach(person -> persons.add(new AdaptedPerson(person)));
        password = source.getMasterPassword();
        isPermAdmin = source.isPermAdmin();
        assessments = new ArrayList<>();
        source.getAllAssessments().forEach(assessment -> assessments.add(new AdaptedAssessment(assessment,
                source.getAllPersons())));
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
        return persons.stream().anyMatch(AdaptedPerson::isAnyRequiredFieldMissing);
    }


    /**
     * Converts this jaxb-friendly {@code AdaptedAddressBook} object into the corresponding(@code AddressBook} object.
     * @throws IllegalValueException if there were any data constraints violated in the adapted person
     */
    public AddressBook toModelType() throws IllegalValueException {
        final List<Person> personList = new ArrayList<>();
        final HashSet<String> usernameSet = new HashSet<>();
        for (AdaptedPerson person : persons) {
            Person modelPerson = person.toModelType();
            personList.add(modelPerson);
            checkDuplicateUsername(usernameSet, modelPerson);
        }
        List<Assessment> assessmentList = new ArrayList<>();
        for (AdaptedAssessment assess : assessments) {
            assessmentList.add(assess.toModelType(personList));
        }
        final String masterPassword = password;
        final AddressBook ab = new AddressBook(new UniquePersonList(personList),
                new UniqueAssessmentsList(assessmentList),
                masterPassword);
        ab.setPermAdmin(isPermAdmin);
        return ab;
    }

    /** Checks if the given Person have an account with an existing username*/
    private void checkDuplicateUsername(HashSet<String> usernameSet, Person modelPerson) throws IllegalValueException {
        if (modelPerson.hasAccount()) {
            if (usernameSet.contains(modelPerson.getAccount().get().getUsername())) {
                throw new IllegalValueException("Data contains duplicate username");
            }
            usernameSet.add(modelPerson.getAccount().get().getUsername());
        }
    }
}
