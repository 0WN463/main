package classrepo.commands.account;

import static classrepo.common.Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK;

import classrepo.commands.commandformat.KeywordsFormatCommand;
import classrepo.commands.commandresult.CommandResult;
import classrepo.data.account.Account;
import classrepo.data.person.Person;
import classrepo.data.person.UniquePersonList;
import classrepo.logic.Logic.WrongPasswordEnteredException;

/**
 * Log into a previously created account. Privilege is raised/ decreased to match that of your account.
 */
public class LoginCommand extends KeywordsFormatCommand {

    public static final String COMMAND_WORD = "login";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Logs into your account. Raises current privilege to that of your account.\n\t "
            + "Parameters: USERNAME PASSWORD\n\t"
            + "Example: " + COMMAND_WORD + " IamSudo sudo1234";

    public static final String MESSAGE_SUCCESS = "Logged in as : %s (%s)";
    public static final String MESSAGE_WRONG_PASSWORD = "Wrong password entered";

    private static final int REQUIRED_ARGUMENTS = 2;

    private String userName;
    private String password;

    private void validatePassword(Account account) throws WrongPasswordEnteredException {
        if (!password.equals(account.getPassword())) {
            throw new WrongPasswordEnteredException();
        }
    }

    @Override
    public void setUp(String[] arguments) {
        assert(arguments.length == REQUIRED_ARGUMENTS);
        this.userName = arguments[0];
        this.password = arguments[1];
    }

    @Override
    public int getNumRequiredArg () {
        return REQUIRED_ARGUMENTS;
    }

    @Override
    public CommandResult execute() {
        try {
            Person requestedPerson = addressBook.findPersonByUsername(userName);
            final Account requestedAccount = requestedPerson.getAccount().get();
            validatePassword(requestedAccount);
            privilege.copyPrivilege(requestedAccount.getPrivilege());
            final String message = String.format(MESSAGE_SUCCESS,
                    requestedPerson.getName().toString(),
                    requestedAccount.getPrivilege().getLevelAsString());
            return new CommandResult(message);
        } catch (WrongPasswordEnteredException wpe) {
            return new CommandResult(MESSAGE_WRONG_PASSWORD);
        } catch (UniquePersonList.PersonNotFoundException pnf) {
            return new CommandResult(MESSAGE_PERSON_NOT_IN_ADDRESSBOOK);
        }
    }

    @Override
    public Category getCategory() {
        return Category.ACCOUNT;
    }

    @Override
    public String getCommandUsageMessage() {
        return MESSAGE_USAGE;
    }
}
