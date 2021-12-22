package me.pustinek.humblelibrary.common.utils.commands;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HumbleCommandAnnotation {
    String start();

    String permission() default "";

    /*
     Create enum for command type
     Command type:
     - Command
     - SubCommand
    */

}