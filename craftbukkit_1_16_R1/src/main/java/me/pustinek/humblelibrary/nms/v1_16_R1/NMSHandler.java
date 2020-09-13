package me.pustinek.humblelibrary.nms.v1_16_R1;


import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.pustinek.humblelibrary.nms.interfaces.NMS;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.UUID;

public class NMSHandler implements NMS {

    @Override
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
