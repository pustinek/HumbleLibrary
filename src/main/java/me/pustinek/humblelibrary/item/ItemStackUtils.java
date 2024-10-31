package me.pustinek.humblelibrary.item;

import lombok.AllArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;


public class ItemStackUtils {



    public static boolean isSame(ItemStack itemStack, ItemStackProperty... properties) {

        return Arrays.stream(properties).allMatch(prop -> {






            return true;
        });

    }


    public static boolean isSimilar(ItemStack item1, ItemStack item2,  ItemStackProperty...toCompare){
        if(item1 == null || item2 == null) return false;
        if(item1.equals(item2) || item1.isSimilar(item2)) return true;

        List<ItemStackProperty> toCheck = Arrays.asList(toCompare);


        if(toCheck.isEmpty() || toCheck.contains(ItemStackProperty.ALL)) return item1.isSimilar(item2);

        ItemMeta m2 = item2.getItemMeta();
        ItemMeta m1 = item1.getItemMeta();
        if(m1 == null || m2 == null) return false;


        // Compare type
        if(item1.getType() != item2.getType()) return false;

        // Compare name
        if(toCheck.contains(ItemStackProperty.NAME)){
            if(!m1.hasDisplayName() || !m2.hasDisplayName()) return false;
            if(!m1.getDisplayName().equals(m2.getDisplayName())) return false;
        }

        // Compare lore
        if(toCheck.contains(ItemStackProperty.LORE)){
            if(!m1.hasLore() || !m2.hasLore()) return false;
            if(!m1.getLore().equals(m2.getLore())) return false;
        }

        // Compare enchantments
        if(toCheck.contains(ItemStackProperty.ENCHANTMENTS)){
            if(!m1.hasEnchants() || !m2.hasEnchants()) return false;
            if(!m1.getEnchants().equals(m2.getEnchants())) return false;
        }

        // Compare flags
        if(toCheck.contains(ItemStackProperty.FLAGS)){
            return m1.getItemFlags().equals(m2.getItemFlags());
        }

        return true;
    }
}
