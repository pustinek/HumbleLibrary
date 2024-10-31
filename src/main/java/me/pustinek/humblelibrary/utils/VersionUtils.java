package me.pustinek.humblelibrary.utils;

import org.bukkit.Bukkit;

public class VersionUtils {
    private static final String serverVersion;

    static {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        serverVersion = packageName.substring(packageName.lastIndexOf('.') + 1);
    }

    public static boolean isVersionNewerThan(String version) {
        return serverVersion.compareTo(version) >= 0;
    }
}

