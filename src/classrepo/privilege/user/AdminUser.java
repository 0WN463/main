package classrepo.privilege.user;

import java.util.Arrays;
import java.util.List;

import classrepo.commands.Command;
import classrepo.commands.account.AddAccountCommand;
import classrepo.commands.account.DeleteAccountCommand;
import classrepo.commands.exams.AddExamCommand;
import classrepo.commands.exams.ClearExamsCommand;
import classrepo.commands.exams.DeleteExamCommand;
import classrepo.commands.exams.EditExamCommand;
import classrepo.commands.fees.EditFeesCommand;
import classrepo.commands.fees.PaidFeesCommand;
import classrepo.commands.person.AddCommand;
import classrepo.commands.person.ClearCommand;
import classrepo.commands.person.DeleteCommand;
import classrepo.commands.privilege.EditPasswordCommand;
import classrepo.commands.privilege.SetPermanentAdminCommand;

/**
 * Represents a user with Admin access
 * */
public class AdminUser extends TutorUser {
    private static List<Command> newAllowedCommand = Arrays.asList(
            new AddCommand(),
            new DeleteCommand(),
            new ClearCommand(),
            new EditPasswordCommand(),
            new ClearExamsCommand(),
            new EditFeesCommand(),
            new PaidFeesCommand(),
            new SetPermanentAdminCommand(),
            new AddAccountCommand(),
            new DeleteAccountCommand(),
            new AddExamCommand(),
            new DeleteExamCommand(),
            new EditExamCommand()
    );

    public AdminUser() {
        super();
        addAllowedCommands(newAllowedCommand);
        setCurrentLevel(PrivilegeLevel.Admin);
        sortCommands();
    }

    /**
     * Overrides the check with always be true to allow compatibility with "new" commands that were
     * unaccounted for (ie commands made by teammates)
     * */
    @Override
    public boolean isAllowedCommand(Command command) {
        return true;
    }
}
