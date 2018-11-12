package classrepo.commands.commandformat;

import classrepo.commands.Command;

/** The abstract class for commands with the format of KEYWORD ... KEYWORD */
public abstract class KeywordsFormatCommand extends Command {
    /** Function called by Parser to determine if number of arguments provided is correct*/
    public abstract int getNumRequiredArg ();

    /** Passes the Strings into attributes of the command.
     *  Command object can assume the number of arguments is correct during setUp as Parser should have checked it.
     */
    public abstract void setUp(String[] arguments);
}
