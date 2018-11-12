package classrepo.storage.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import classrepo.common.Utils;
import classrepo.data.exception.IllegalValueException;
import classrepo.data.person.Fees;

/**
 * JAXB-friendly adapted fees data holder class.
 */
@XmlRootElement(name = "fees")
public class AdaptedFees {
    @XmlElement(required = true)
    private String value;

    @XmlElement(required = true)
    private String duedate;

    @XmlAttribute(required = true)
    private boolean isPrivate;
    /**
     * No-arg constructor for JAXB use.
     */
    public AdaptedFees() {}

    /**
     * Converts given fees into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created AdaptedFees
     */
    public AdaptedFees(Fees source) {
        value = source.value;
        duedate = source.duedate;
        isPrivate = source.isPrivate();
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
        return Utils.isAnyNull(value, duedate, isPrivate);
    }

    /**
     * Converts this jaxb-friendly adapted object into the Fees object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted fees
     */
    public Fees toModelType() throws IllegalValueException {
        return new Fees(value, duedate);
    }
}
