package me.pustinek.humblelibrary.common.utils.commands;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class HumbleCommand {



    /**
     * Get the argument that comes after the base command that this command reacts to.
     *
     * @return The string that should be in front of the command for this class to act
     */
    public abstract String getCommandStart();



    /**
     * Check if this Command instance can execute the given command and arguments.
     *
     * @param commandName The command to check for execution
     * @param args    The arguments to check
     * @return true if it can execute the command, false otherwise
     */
    public boolean canExecute(String commandName, String[] args) {
        String commandString = commandName + " " + StringUtils.join(args, " ");
        if (commandString.length() > getCommandStart().length()) {
            return commandString.toLowerCase().startsWith(getCommandStart().toLowerCase() + " ");
        }
        return commandString.toLowerCase().startsWith(getCommandStart().toLowerCase());
    }


    /**
     * Get a list of string to complete a command with (raw list, not matching ones not filtered out).
     *
     * @param args        Full string of args
     * @param trimmedArgs Arguments trimmed of the commandStart
     * @param sender      The CommandSender that wants to tab complete
     * @return A collection with all the possibilities for argument to complete
     */
    public List<String> getTabCompleteList(String[] args, String[] trimmedArgs, Object sender) {
        return new ArrayList<>();
    }




}
