package classrepo.privilege.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import classrepo.Main;
import classrepo.commands.Command;
import classrepo.commands.account.ListAccountCommand;
import classrepo.commands.account.LoginCommand;
import classrepo.commands.account.LogoutCommand;
import classrepo.commands.assessment.ListAssessmentCommand;
import classrepo.commands.assessment.ListStatisticsCommand;
import classrepo.commands.exams.ViewExamsCommand;
import classrepo.commands.general.ExitCommand;
import classrepo.commands.general.HelpCommand;
import classrepo.commands.person.FindCommand;
import classrepo.commands.person.ListCommand;
import classrepo.commands.person.ViewCommand;
import classrepo.commands.person.ViewSelfCommand;
import classrepo.commands.privilege.RaisePrivilegeCommand;
import classrepo.commands.privilege.ViewPrivilegeCommand;

/**
 * Represents a user with Basic access
 * */
public class BasicUser implements User {
    private static List<Command> newAllowedCommands = Arrays.asList(
            new ViewCommand(),
            new ListCommand(),
            new ListAccountCommand(),
            new RaisePrivilegeCommand(),
            new ViewPrivilegeCommand(),
            new FindCommand(),
            new HelpCommand(),
            new LoginCommand(),
            new LogoutCommand(),
            new ExitCommand(),
            new ViewSelfCommand(),
            new ViewExamsCommand(),
            new ListStatisticsCommand(),
            new ListAssessmentCommand()
    );
    private List<Command> allowedCommands;
    private PrivilegeLevel currentLevel;

    public BasicUser() {
        currentLevel = PrivilegeLevel.Basic;
        allowedCommands = new ArrayList<>();
        allowedCommands.addAll(newAllowedCommands);
        sortCommands();
    }

    protected void addAllowedCommands(List<Command> newAllowedCommands) {
        allowedCommands.addAll(newAllowedCommands);
        assert allowedCommandContainsNoDuplicate();
    }

    /** Returns if allowedCommand contains no duplicate
     *  If false, logs the duplicated command into the logger
     */
    protected boolean allowedCommandContainsNoDuplicate() {
        Set<Command> commandSet = new HashSet<>();
        Set<Command> duplicatedCommands = new HashSet<>();
        for (Command allowCommand: allowedCommands) {
            // Java Set.add method returns true if element not already in set
            if (!commandSet.add(allowCommand)) {
                duplicatedCommands.add(allowCommand);
            }
        }

        if (duplicatedCommands.isEmpty()) {
            return true;
        } else {
            for (Command duplicatedCommand: duplicatedCommands) {
                Main.LOGGER.log(Level.WARNING, String.format("Duplicated %s for different User", duplicatedCommand));
            }
            return false;
        }
    }

    protected void setCurrentLevel(PrivilegeLevel currentLevel) {
        this.currentLevel = currentLevel;
    }

    public String getPrivilegeLevelAsString() {
        return currentLevel.toString();
    }

    public PrivilegeLevel getPrivilegeLevel() {
        return currentLevel;
    }

    public List<Command> getAllowedCommands() {
        return allowedCommands;
    }

    protected void sortCommands() {
        allowedCommands.sort(new SortByCategory());
    }

    /** Checks if this privilege level has access to the given command*/
    public boolean isAllowedCommand(Command command) {
        for (Command allowedCommand: allowedCommands) {
            if (command.getClass().equals(allowedCommand.getClass())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Comparator used to sort the commands in allowedCommands
     */
    class SortByCategory implements Comparator<Command> {
        /**
         * Compare commands based on category, if they are the same, compare name of the command
         */
        public int compare(Command a, Command b) {
            String categoryA = a.getCategory().toString();
            String categoryB = b.getCategory().toString();
            if (!categoryA.equals(categoryB)) {
                return categoryA.compareTo(categoryB);
            } else {
                return a.toString().compareTo(b.toString());
            }
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof BasicUser // instanceof handles nulls
                && this.currentLevel.equals(((BasicUser) other).currentLevel)); // state check
    }
}
