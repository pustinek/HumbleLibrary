package me.pustinek.humblelibrary;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import me.pustinek.humblelibrary.nms.interfaces.NMS;
import me.pustinek.humblelibrary.utils.ReflectionUtil;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.UUID;

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
        /*
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
         */

    }

    public void setItemSkull(@NotNull ItemMeta itemMeta, @NotNull String base64) {

        SkullMeta headMeta = (SkullMeta) itemMeta;

        UUID hashAsId = new UUID(base64.hashCode(), base64.hashCode());

        GameProfile profile = new GameProfile(hashAsId, null);
        profile.getProperties().put("textures", new Property("textures", base64));
        Field profileField;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
    }





}
