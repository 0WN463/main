package classrepo.privilege;

import java.util.List;
import java.util.Optional;

import classrepo.commands.Command;
import classrepo.data.exception.IllegalValueException;
import classrepo.data.person.Person;
import classrepo.data.person.ReadOnlyPerson;
import classrepo.privilege.user.AdminUser;
import classrepo.privilege.user.BasicUser;
import classrepo.privilege.user.TutorUser;
import classrepo.privilege.user.User;

/** Represents the privilege level of the user */
public class Privilege {
    public static final String PRIVILEGE_CONSTRAINTS = "Privilege should be \"Basic\", \"Tutor\" or \"Admin\"";

    /** Enums for the different privilege levels*/
    private enum PrivilegeLevels {
        BASIC(new BasicUser()),
        TUTOR(new TutorUser()),
        ADMIN(new AdminUser());

        private final User userType;
        PrivilegeLevels(User userType) {
            this.userType = userType;
        }

        public User getUserType() {
            return userType;
        }
    }

    private User user;
    private Person myPerson;

    public Privilege() {
        user = PrivilegeLevels.BASIC.getUserType();
    }

    public Privilege(User user) {
        this.user = user;
    }

    /**
     * Signals that an operation would have changed the properties of the currently logged in user.
     */
    public static class SelfModifyingException extends Exception {
        public SelfModifyingException() {
            super("Operation would result in errors due effects on logged in user");
        }
    }

    public static Privilege getPrivilegeFromString (String userType) throws IllegalValueException {
        for (PrivilegeLevels p : PrivilegeLevels.values()) {
            if (p.toString().equals(userType.toUpperCase())) {
                return new Privilege(p.getUserType());
            }
        }
        throw new IllegalValueException(PRIVILEGE_CONSTRAINTS);
    }

    /**
     * Copies all the information of another privilege into this object, effectively cloning it.
     */
    public void copyPrivilege(Privilege privilege) {
        this.user = privilege.getUser();
        this.myPerson = privilege.getMyPerson().orElse(null);
    }

    public void raiseToTutor() {
        user = PrivilegeLevels.TUTOR.getUserType();
    }

    public void raiseToAdmin() {
        user = PrivilegeLevels.ADMIN.getUserType();
    }

    public Optional<Person> getMyPerson() {
        return Optional.ofNullable(myPerson);
    }
    public Optional<ReadOnlyPerson> getMyReadOnlyPerson() {
        return Optional.ofNullable(myPerson);
    }

    public void setMyPerson(Person myPerson) {
        this.myPerson = myPerson;
    }

    /**
     * Resets the privilege to base (No myPerson assigned, Basic User).
     */
    public void resetPrivilege() {
        clearMyPerson();
        raiseToBasic();
    }

    public boolean isBase() {
        return (user.equals(PrivilegeLevels.BASIC.getUserType()) && myPerson == null);
    }

    private void clearMyPerson() {
        myPerson = null;
    }

    private void raiseToBasic() {
        user = PrivilegeLevels.BASIC.getUserType();
    }

    public String getLevelAsString() {
        return user.getPrivilegeLevelAsString();
    }

    public User getUser() {
        return user;
    }

    public List<Command> getAllowedCommands() {
        return user.getAllowedCommands();
    }

    public boolean isAllowedCommand(Command command) {
        return user.isAllowedCommand(command);
    }

    /**
     * Checks if the target is the currently logged in user
     * @throws SelfModifyingException if the above is true
     */
    public void checkTargetIsSelf(ReadOnlyPerson person) throws SelfModifyingException {
        if (isTargetSelf(person)) {
            throw new SelfModifyingException();
        }
    }

    private boolean isTargetSelf(ReadOnlyPerson person) {
        return person.equals(myPerson);
    }

    public String getRequiredPrivilegeAsString(Command command) {
        String requiredPrivilege = "PRIVILEGE NOT FOUND";
        for (PrivilegeLevels p : PrivilegeLevels.values()) {
            if (p.getUserType().isAllowedCommand(command)) {
                requiredPrivilege = p.getUserType().getPrivilegeLevelAsString();
            }
        }
        return requiredPrivilege;
    }
}
