package me.pustinek.humblelibrary;

import lombok.Getter;
import me.pustinek.humblelibrary.nms.interfaces.NMS;
import me.pustinek.humblelibrary.utils.ReflectionUtil;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public class HumbleLibrary {


    @Getter
    @Nullable
    private static NMS nmsHandler;

    private static JavaPlugin plugin;

    // For other classes in our library
    public static JavaPlugin getPlugin() {
        return plugin;
    }

    // This method must not be used any where in the library!
    public static void init(JavaPlugin plugin) {
        HumbleLibrary.plugin = plugin;

        String packageName = plugin.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);
        try {
            Class<?> clazz = Class.forName("me.pustinek.humblelibrary.nms." + ReflectionUtil.getVersion() + "NMSHandler");

            if (NMS.class.isAssignableFrom(clazz)) {
                nmsHandler = (NMS) clazz.getConstructor().newInstance();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }





}
