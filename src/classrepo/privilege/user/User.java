package classrepo.privilege.user;

import java.util.List;

import classrepo.commands.Command;

/**
 This interface describes who the current user is
 */
public interface User {
    /**
     * Enum to describe privilege levels. Used only as String
     * */
    enum PrivilegeLevel { Basic, Tutor, Admin }

    String getPrivilegeLevelAsString();
    PrivilegeLevel getPrivilegeLevel();
    List<Command> getAllowedCommands();
    boolean isAllowedCommand (Command command);
}
