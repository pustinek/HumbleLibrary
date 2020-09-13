package me.pustinek.humblelibrary.utils;

import org.bukkit.entity.Player;

public enum PlayerDirection {
    NORTH, WEST, EAST, SOUTH;

    public static PlayerDirection get(Player p) {
        float yaw = p.getLocation().getYaw();
        yaw = (yaw % 360 + 360) % 360; // true modulo, as javas modulo is weird for negative values
        if (yaw > 135 || yaw < -135) {
            return PlayerDirection.NORTH;
        } else if (yaw < -45) {
            return PlayerDirection.EAST;
        } else if (yaw > 45) {
            return PlayerDirection.WEST;
        } else {
            return PlayerDirection.SOUTH;
        }
    }

}
