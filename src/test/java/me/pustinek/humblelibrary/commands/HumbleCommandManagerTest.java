package me.pustinek.humblelibrary.commands;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HumbleCommandManagerTest {





    @Test
    void onCommand() {
    }

    @Test
    @DisplayName("tab complete")
    void onTabComplete() {
        String commandName = "humbleplayervaults";
        List<String> results = new ArrayList<>();
        List<String> commandStarts = new ArrayList<>();


        List<DefaultCommand> commands = new ArrayList<>();
        commandStarts.add("Humbleplayervaults");
        commandStarts.add("humbleplayervaults vaults");
        commandStarts.add("humbleplayervaults admin");

        String[] args = {"humbleplayervaults"};

        for (String c : commandStarts) {
            if(!c.toLowerCase().startsWith(commandName.toLowerCase())){
                continue;
            }

            if (args.length == 1 && c.split(" ").length > 1) {
                // Show the first part of the command start
                // example: /examplePlugin [give,get,reload]
                results.add(c.substring(c.indexOf(' ') + 1));
            } else {
                String[] split = c.split(" ");


                if (split.length > args.length) {
                    if (split[args.length].contains(args[args.length - 1]) && split[args.length - 1].equalsIgnoreCase(args[args.length - 2]))
                        results.add(split[args.length]);
                } else {

                    int shortenedArgsSize = args.length - split.length;

                    String[] shortenedArgs = new String[shortenedArgsSize];
                    System.arraycopy(args, (split.length - 1), shortenedArgs, 0, args.length - split.length);

                   System.out.println("[" + c + "] 2.reached !" );
                }
            }

            System.out.println(results);

        }







        // Print out test
        System.out.println("Test");


    }
}