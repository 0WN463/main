package classrepo.privilege.user;

import java.util.List;

import classrepo.commands.Command;
import classrepo.commands.person.DeleteCommand;
import classrepo.commands.person.ViewAllCommand;

/** Class for generating erroneously set up Users*/
public class FaultyUserGenerator {
    public FaultyUserInternal generateFaultyUserInternal() {
        return new FaultyUserInternal();
    }

    public FaultyUserExternal generateFaultyUserExternal() {
        return new FaultyUserExternal();
    }

    /** A User with duplicated command within itself*/
    public class FaultyUserInternal extends BasicUser {
        private List<Command> newAllowedCommand = List.of(
                new ViewAllCommand(),
                new ViewAllCommand());

        public FaultyUserInternal() {
            super();
            addAllowedCommands(newAllowedCommand);
            setCurrentLevel(PrivilegeLevel.Tutor);
            sortCommands();
        }
    }

    /** Parent of FaultUserExternal*/
    public class UserParent extends BasicUser {
        private List<Command> newAllowedCommand = List.of(
                new DeleteCommand()
        );

        public UserParent() {
            super();
            addAllowedCommands(newAllowedCommand);
            setCurrentLevel(PrivilegeLevel.Tutor);
            sortCommands();
        }
    }

    /** A User with duplicated command as its parent (UserParent) */
    public class FaultyUserExternal extends UserParent {
        private List<Command> newAllowedCommand = List.of(
                new DeleteCommand()
        );

        public FaultyUserExternal() {
            super();
            addAllowedCommands(newAllowedCommand);
            setCurrentLevel(PrivilegeLevel.Tutor);
            sortCommands();
        }
    }
}
