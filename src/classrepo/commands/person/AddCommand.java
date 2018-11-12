package classrepo.commands.person;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import classrepo.commands.Command;
import classrepo.commands.commandresult.CommandResult;
import classrepo.data.exception.IllegalValueException;
import classrepo.data.person.Person;
import classrepo.data.person.ReadOnlyPerson;
import classrepo.data.person.UniquePersonList;
import classrepo.data.person.details.Address;
import classrepo.data.person.details.Email;
import classrepo.data.person.details.Name;
import classrepo.data.person.details.Phone;
import classrepo.data.tag.Tag;

/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Adds a person to the address book. "
            + "Contact details can be marked private by prepending 'p' to the prefix.\n\t"
            + "Parameters: NAME [p]p/PHONE [p]e/EMAIL [p]a/ADDRESS [t/TAG]...\n\t"
            + "Example: " + COMMAND_WORD
            + " John Doe p/98765432 e/johnd@gmail.com a/311, Clementi Ave 2, #02-25 t/friends t/owesMoney";

    public static final String MESSAGE_SUCCESS = "New person added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book";

    private final Person toAdd;

    /**
     * Constructor used for Privileges
     * Command constructed has no functionality
     * */
    public AddCommand() {
        this.toAdd = null;
    }

    /**
     * Convenience constructor using raw values.
     *
     * @throws IllegalValueException if any of the raw values are invalid
     */
    public AddCommand(String name,
                      String phone, boolean isPhonePrivate,
                      String email, boolean isEmailPrivate,
                      String address, boolean isAddressPrivate,
                      Set<String> tags) throws IllegalValueException {
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(new Tag(tagName));
        }
        this.toAdd = new Person(
                new Name(name),
                new Phone(phone, isPhonePrivate),
                new Email(email, isEmailPrivate),
                new Address(address, isAddressPrivate),
                tagSet
        );
    }

    public ReadOnlyPerson getPerson() {
        return toAdd;
    }

    @Override
    public CommandResult execute() {
        try {
            addressBook.addPerson(toAdd);
            final List<ReadOnlyPerson> updatedList = addressBook.getAllPersons().immutableListView();
            return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd), updatedList);
        } catch (UniquePersonList.DuplicatePersonException dpe) {
            return new CommandResult(MESSAGE_DUPLICATE_PERSON);
        }
    }

    @Override
    public boolean isMutating() {
        return true;
    }

    @Override
    public Category getCategory() {
        return Category.PERSON;
    }

    @Override
    public String getCommandUsageMessage() {
        return MESSAGE_USAGE;
    }
}
