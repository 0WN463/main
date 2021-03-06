package seedu.addressbook.privilege.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import seedu.addressbook.commands.person.AddCommand;
import seedu.addressbook.commands.person.DeleteCommand;

/** This test checks if the Users allowedCommands are set properly*/
public class UserTest {

    @Test
    public void commandComparator_sameClass_returnsEquals() {
        assertEquals(new AddCommand(), new AddCommand());
    }

    @Test
    public void commandComparator_differentClass_returnsNotEquals() {
        assertNotEquals(new AddCommand(), new DeleteCommand());
    }

    /** Test if a User have a duplicated command within itself
     *  Expects AssertionError when the FaultyUser is generated*/
    @Test(expected = AssertionError.class)
    public void constructUser_duplicatedInternalCommand_assertionErrorThrown() {
        FaultyUserGenerator generator = new FaultyUserGenerator();
        generator.generateFaultyUserInternal();
    }

    /** Test if a User have a duplicated command as its parent
     *  Expects AssertionError when the FaultyUser is generated*/
    @Test(expected = AssertionError.class)
    public void constructUser_duplicatedExternalCommand_assertionErrorThrown() {
        FaultyUserGenerator generator = new FaultyUserGenerator();
        generator.generateFaultyUserExternal();
    }

    @Test
    public void constructUser_basicUser_allowedCommandContainsNoDuplicateReturnsTrue() {
        assertTrue(new BasicUser().allowedCommandContainsNoDuplicate());
    }

    @Test
    public void constructUser_tutorUser_allowedCommandContainsNoDuplicateReturnsTrue() {
        assertTrue(new TutorUser().allowedCommandContainsNoDuplicate());
    }

    @Test
    public void constructUser_adminUser_allowedCommandContainsNoDuplicateReturnsTrue() {
        assertTrue(new AdminUser().allowedCommandContainsNoDuplicate());
    }
}
