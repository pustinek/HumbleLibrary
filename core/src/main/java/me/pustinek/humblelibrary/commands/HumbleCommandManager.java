package me.pustinek.humblelibrary.commands;


import lombok.Setter;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class HumbleCommandManager implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final ArrayList<DefaultCommand> commands = new ArrayList<>();
    private final ArrayList<String> extraHelpMessagesFooter = new ArrayList<>();
    private final ArrayList<String> extraHelpMessagesHeader = new ArrayList<>();
    private final Consumer<CommandConsumer> messageConsumer;
    private int helpsPerPage = 9999;
    @Setter
    private String defaultNoPermissionMessage = "&cInsufficient permissions";


    /**
     * CommandManager constructor, used to handle all the command stuff
     *
     * @param plugin                     main plugin
     * @param helpMessageSendingConsumer consumer that will send-help messages to users on command
     * @param commandPrefixes            commands to register (need to be registered in plugin.yml)
     */
    public HumbleCommandManager(JavaPlugin plugin, Consumer<CommandConsumer> helpMessageSendingConsumer, String... commandPrefixes) throws CommandNotRegisteredException {
        this.plugin = plugin;
        this.messageConsumer = helpMessageSendingConsumer;

        for (String cmd : commandPrefixes) {
            PluginCommand command = plugin.getCommand(cmd);

            if (command == null) {
                throw new CommandNotRegisteredException("Command '" + cmd + "' not registered in the plugin.yml file", cmd);
            }

            command.setExecutor(this);
            command.setTabCompleter(this);
        }

    }

    /**
     * Register commands that will be used to handle tab-completing/executing
     *
     * @param commands Array of commands to register
     */
    public HumbleCommandManager addCommands(DefaultCommand... commands) {
        this.commands.addAll(Arrays.asList(commands));
        return this;
    }

    /**
     * Add messages/keys that should be appended to the end of all the help-messages returned,
     * when player executes the prefix/main-command
     *
     * @param messages Array of messages to add
     */
    public HumbleCommandManager addExtraHelpMessagesFooter(String... messages) {
        extraHelpMessagesFooter.addAll(Arrays.asList(messages));
        return this;
    }

    /**
     * Add messages/keys that should be appended to the top of all the help-messages returned,
     * when player executes the prefix/main-command
     *
     * @param messages Array of messages to add
     */
    public HumbleCommandManager addExtraHelpMessagesHeader(String... messages) {
        extraHelpMessagesHeader.addAll(Arrays.asList(messages));
        return this;
    }

    public HumbleCommandManager setNoPermissionMessage(String message) {
        this.defaultNoPermissionMessage = message;
        return this;
    }

    //TODO: create a pagination section for help-messages
    public HumbleCommandManager setHelpMessagesPerPage(int amount) {
        this.helpsPerPage = amount;
        return this;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        boolean executed = false;
        for (int i = 0; i < commands.size() && !executed; i++) {
            DefaultCommand cmd = commands.get(i);

            if (!cmd.canExecute(command, args)) continue;
            if (!cmd.hasPermission(sender)) {
                String noPermsMessage = cmd.getNoPermissionMessage() == null ? defaultNoPermissionMessage : cmd.getNoPermissionMessage();
                messageConsumer.accept(new CommandConsumer(sender, noPermsMessage));
                return true;
            }

            String[] trimmedStart = cmd.getCommandStart().split(" ");
            String[] argsToPass = new String[args.length - (trimmedStart.length - 1)];


            System.arraycopy(args, trimmedStart.length - 1, argsToPass, 0, args.length - (trimmedStart.length - 1));
            cmd.execute(sender, argsToPass);
            executed = true;
        }

        if (!executed && args.length == 0) {
            this.showHelp(sender, 0);
        } else if (!executed) {
            sender.sendMessage("invalid command");
        }

        return true;
    }


    public void showHelp(CommandSender target, int page) {

        // Add all messages to a list
        ArrayList<String> messages = new ArrayList<>();


        int totalPages = commands.size() / helpsPerPage;
        int lowerPage = page > 0 ? page - 1 : 0;
        int nextPage = page >= totalPages ? totalPages : page + 1;


        int starting = page * helpsPerPage;
        if (starting > commands.size()) starting = (page - 1) * helpsPerPage;
        int end = starting + helpsPerPage;
        if (end > commands.size()) end = commands.size();


        for (int i = starting; i < end; i++) {
            DefaultCommand command = commands.get(i);
            String help = command.getHelpKey(target);
            if (help != null && help.length() != 0 && command.hasPermission(target)) {
                messages.add(help);
            }
        }


        if (messages.size() > 0) {
            messages.addAll(0, extraHelpMessagesHeader);
            messages.addAll(extraHelpMessagesFooter);
        }

        for (String message : messages) {
            if (message.equalsIgnoreCase("help-footer")) {
                messageConsumer.accept(new CommandConsumer(target, message, page, totalPages, lowerPage, nextPage));
                continue;
            }
            messageConsumer.accept(new CommandConsumer(target, message));
        }
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> results = new ArrayList<>();

        String toCompletePrefix = args.length > 0 ? args[args.length - 1].toLowerCase() : "";

        for (DefaultCommand c : commands) {
            if (!c.getCommandStart().substring(0, c.getCommandStart().indexOf(" ")).equalsIgnoreCase(command.getName())) {
                continue;
            }

            if (!c.hasPermission(sender)) continue;

            if (args.length == 1) {
                // Show the first part of the command start
                // example: /examplePlugin [give,get,reload]
                String begin = c.getCommandStart();
                results.add(begin.substring(begin.indexOf(' ') + 1));
            } else {
                String[] split = c.getCommandStart().split(" ");
                if (split.length > args.length) {
                    if (split[args.length].contains(args[args.length - 1]) && split[args.length - 1].equalsIgnoreCase(args[args.length - 2]))
                        results.add(split[args.length]);
                } else {

                    int shortenedArgsSize = args.length - split.length;

                    String[] shortenedArgs = new String[shortenedArgsSize];
                    System.arraycopy(args, (split.length - 1), shortenedArgs, 0, args.length - split.length);

                    if (c.canExecute(command, args)) {
                        results = c.getTabCompleteList(args, shortenedArgs, sender);
                    }

                }
            }
        }

        // Filter and sort the results
        if (!results.isEmpty()) {
            SortedSet<String> set = new TreeSet<>();
            for (String suggestion : results) {
                if (suggestion.toLowerCase().startsWith(toCompletePrefix)) {
                    set.add(suggestion);
                }
            }
            results.clear();
            results.addAll(set);
        }
        return results;

    }
}
