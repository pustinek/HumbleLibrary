package me.pustinek.humblelibrary.commands;

import lombok.Getter;
import org.bukkit.command.CommandSender;

@Getter
public class CommandConsumer {
    CommandSender sender;
    String message;
    Object[] replacements;


    public CommandConsumer(CommandSender sender, String message, Object... replacements) {
        this.sender = sender;
        this.message = message;
        this.replacements = replacements;
    }
}
