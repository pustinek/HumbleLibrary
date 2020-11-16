package me.pustinek.humblelibrary.commands;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class DefaultCommand {

    private final JavaPlugin plugin;
    private String noPermissionMessage;


    public DefaultCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }


    /**
     * Get the permission required to execute the command, settings to null
     * will disable the permission required
     *
     *
     * @return permission name that is required
     */
    public abstract String getPermission();


    /**
     * Get the argument that comes after the base command that this command reacts to.
     *
     * @return The string that should be in front of the command for this class to act
     */
    public abstract String getCommandStart();


    /**
     * Returns the correct help string key to be used on the help page.
     *
     * @param target The CommandSender that the help message is for
     * @return The help message key according to the permissions of the reciever
     */
    public abstract String getHelpKey(CommandSender target);


    public CommandSenderType getSenderTypeLimit() {
        return CommandSenderType.ANY;
    }


    /**
     * Check if this Command instance can execute the given command and arguments.
     *
     * @param command The command to check for execution
     * @param args    The arguments to check
     * @return true if it can execute the command, false otherwise
     */
    public boolean canExecute(Command command, String[] args) {
        String commandString = command.getName() + " " + StringUtils.join(args, " ");
        if (commandString.length() > getCommandStart().length()) {
            return commandString.toLowerCase().startsWith(getCommandStart().toLowerCase() + " ");
        }
        return commandString.toLowerCase().startsWith(getCommandStart().toLowerCase());
    }


    /**
     * Check if this Command instance can execute the given command and arguments.
     *
     * @param sender The command to check for execution
     * @return true if it can execute the command, false otherwise
     */
    public boolean canTypeExecute(CommandSender sender) {
        if (getSenderTypeLimit() == CommandSenderType.ANY) return true;
        if (getSenderTypeLimit() == CommandSenderType.PLAYER && (sender instanceof ConsoleCommandSender)) return false;
        return getSenderTypeLimit() != CommandSenderType.CONSOLE || (!(sender instanceof Player));
    }

    /**
     * Check if player has the permission to run this command
     *
     * @param sender Sender of the command
     * @return true if it can execute the command, false otherwise
     */
    public boolean hasPermission(CommandSender sender) {
        if (getPermission() == null || getPermission().isEmpty()) return true;
        return sender.hasPermission(getPermission());
    }


    /**
     * Execute a (sub)command if the conditions are met.
     *
     * @param sender The commandSender that executed the command
     * @param args   The arguments that are given
     */
    public abstract void execute(CommandSender sender, String[] args);

    /**
     * Get a list of string to complete a command with (raw list, not matching ones not filtered out).
     *
     * @param args        Full string of args
     * @param trimmedArgs Arguments trimmed of the commandStart
     * @param sender      The CommandSender that wants to tab complete
     * @return A collection with all the possibilities for argument to complete
     */
    public List<String> getTabCompleteList(String[] args, String[] trimmedArgs, CommandSender sender) {
        return new ArrayList<>();
    }


}
