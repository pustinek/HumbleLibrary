package me.pustinek.humblelibrary.config;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Settings used in @ItemBuilder class to set settings that will be used when
 * parsing from config (.yml) file
 *
 */
@Setter
@Getter
@AllArgsConstructor
public class ItemConfigSettings {
    String materialKey = "material";
    String nameKey = "name";
    String loreKey = "lore";
    String customModelDataKey = "custom_model_data";
    String flagsKey = "flags";
    String enchantmentsKey = "enchantments";
    String skullKey = "skull";


    public ItemConfigSettings() {
    }
}
