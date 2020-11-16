package me.pustinek.humblelibrary.nms.interfaces;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public interface NMS {

    /**
     *
     * @deprecated Replaced with internal method inside the ItemBuilder class
     */
    @Deprecated
    void setItemSkull(@NotNull ItemMeta itemMeta, @NotNull String base64);


}