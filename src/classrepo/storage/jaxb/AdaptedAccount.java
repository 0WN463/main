package classrepo.storage.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import classrepo.common.Utils;
import classrepo.data.account.Account;
import classrepo.data.exception.IllegalValueException;

/**
 * JAXB-friendly adapted tag data holder class.
 */
@XmlRootElement(name = "account")
public class AdaptedAccount {
    @XmlElement(required = true)
    private String username;

    @XmlElement(required = true)
    private String password;

    @XmlElement(required = true)
    private String privilege;
    /**
     * No-arg constructor for JAXB use.
     */
    public AdaptedAccount() {}

    /**
     * Converts a given Tag into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created AdaptedTag
     */
    public AdaptedAccount(Account source) {
        username = source.getUsername();
        password = source.getPassword();
        privilege = source.getPrivilege().getLevelAsString();
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
        return Utils.isAnyNull(username, password, privilege);
    }

    /**
     * Converts this jaxb-friendly adapted tag object into the Tag object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person
     */
    public Account toModelType() throws IllegalValueException {
        return new Account(username, password, privilege);
    }
}
