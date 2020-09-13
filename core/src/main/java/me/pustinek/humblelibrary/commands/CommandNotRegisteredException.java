package me.pustinek.humblelibrary.commands;

public class CommandNotRegisteredException extends Exception {
    final String command;

    public CommandNotRegisteredException(String message, String command) {
        super(message);
        this.command = command;
    }
}
