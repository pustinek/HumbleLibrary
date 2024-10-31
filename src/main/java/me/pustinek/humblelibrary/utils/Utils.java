package me.pustinek.humblelibrary.utils;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Utils {
    /**
     * Convert location to simple string format:
     * Format: world,x,y,z
     *
     * @param loc location to serialize
     */
    @Nullable
    public static String serializeLocation(Location loc) {
        if (loc == null) return null;

        StringJoiner sb = new StringJoiner(",");


        sb.add(loc.getWorld() != null ? loc.getWorld().getName() : "null")
                .add(String.valueOf(loc.getBlockX()))
                .add(String.valueOf(loc.getBlockY()))
                .add(String.valueOf(loc.getBlockZ()));
        return sb.toString();
    }

    /**
     * Convert string back to location
     * Required format: world,x,y,z
     *
     * @param location location in string format
     */
    @Nullable
    public static Location deserializeLocation(String location) {
        if (location == null || location.isEmpty()) return null;
        String[] list = location.split(",");
        int i = 0;
        World world = null;

        if (list.length == 4) {
            // No world
            world = Bukkit.getWorld(list[0]);
            i = 1;
        }

        try {
            int x = Integer.parseInt(list[i]);
            int y = Integer.parseInt(list[i + 1]);
            int z = Integer.parseInt(list[i + 2]);

            return new Location(world, x, y, z);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parse integer from string, in case of an exception
     * return defaultValue
     * Format: world,x,y,z
     *
     * @param toParse      String to parse
     * @param defaultValue Value to use in case of an error
     */
    public static Integer parseIntOrDefault(String toParse, int defaultValue) {
        if (toParse == null) return defaultValue;
        try {
            return Integer.parseInt(toParse);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Return itemBuilder from config, in case of missing values
     * use the defaults provided
     *
     * @param itemCS           ConfigurationSection of the item
     * @param defaultMaterial  Default material of the item
     * @param defaultName      Default name of the item
     * @param loreReplacements Lore to replace in item
     * @deprecated use {@link ItemBuilder class} instead
     */
    @Deprecated
    public static ItemBuilder itemBuilderFromConfig(ConfigurationSection itemCS, Material defaultMaterial, String defaultName, HashMap<String, String> loreReplacements){
        String name = itemCS.getString("name", defaultName);
        String materialName = itemCS.getString("material");
        List<String> lore = itemCS.getStringList("lore");
        List<String> enchants = itemCS.getStringList("enchantments");
        List<String> flags = itemCS.getStringList("flags");
        int customModel = itemCS.getInt("custom_model", -1);
        Material material;
        if (materialName != null && Material.getMaterial(materialName) != null) {
            material = Material.getMaterial(materialName);
        } else {
            material = defaultMaterial;
        }

        ItemBuilder ib = new ItemBuilder(material, name).addLore(lore);

        // Set skull
        if(itemCS.getString("skull") != null)
            ib.setSkull(itemCS.getString("skull"));

        // Add enchantments
        for (String enchant : enchants) {

            String enchantmentName;
            int level;

            if (enchant.contains(":")) {
                String[] split = enchant.split(":");

                if (split.length < 2) continue;

                enchantmentName = split[0];
                level = Utils.parseIntOrDefault(split[1], 1);
            } else {
                enchantmentName = enchant;
                level = 1;
            }
            try {
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantmentName.toLowerCase()));
                ib.applyEnchantment(enchantment, level, true);
            } catch (IllegalArgumentException ignore) {
            }


        }

        // Add flags:
        List<ItemFlag> itemFlags = new ArrayList<>();
        for (String flagString : flags) {
            try {
                ItemFlag itemFlag = ItemFlag.valueOf(flagString);
                itemFlags.add(itemFlag);
            } catch (IllegalArgumentException ignore) {
            }

        }
        // Set custom model:
        if(customModel > -1)
            ib.setCustomModelData(customModel);


        if (loreReplacements != null)
            ib.setReplacements(loreReplacements);

        return ib.addFlags(itemFlags);
    }



    /**
     * Return itemStack from config, in case of missing values
     * use the defaults provided
     *
     * @param itemCS           ConfigurationSection of the item
     * @param defaultMaterial  Default material of the item
     * @param defaultName      Default name of the item
     * @param loreReplacements Lore to replace in item
     * @deprecated use {@link ItemBuilder class} instead
     */
    @Deprecated
    public static ItemStack itemFromConfig(ConfigurationSection itemCS, Material defaultMaterial, String defaultName, HashMap<String, String> loreReplacements) {
        return itemBuilderFromConfig(itemCS, defaultMaterial, defaultName, loreReplacements).build();
    }

    /**
     * Return itemStack from config, in case of missing values
     * use the defaults provided
     *
     * @param itemCS          ConfigurationSection of the item
     * @param defaultMaterial Default material of the item
     * @param defaultName     Default name of the item
     * @deprecated use {@link ItemBuilder class} instead
     */
    @Deprecated
    public static ItemStack itemFromConfig(ConfigurationSection itemCS, Material defaultMaterial, String defaultName) {
        return itemFromConfig(itemCS, defaultMaterial, defaultName, null);
    }

    /**
     * Return formatted time:
     * hh:mm:ss
     *
     * @param timeInSeconds timeInSeconds
     */
    public static String getFormattedTime(long timeInSeconds) {

        long hours = timeInSeconds / 3600;
        long minutes = (timeInSeconds % 3600) / 60;
        long seconds = timeInSeconds % 60;

        return String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, seconds);

    }

    /**
     * Replace inside of a string
     * @param replacements replacements
     * @param text text to replace in
     *
     * @return List of replacements
     */
    public static List<String> replace(HashMap<String, String> replacements, String... text) {
        ArrayList<String> finalStringList = new ArrayList<>();
        for (String str : text) {
            for (String key : replacements.keySet()) {
                str = str.replace(key, replacements.get(key));
            }
            finalStringList.add(str);
        }

        return finalStringList;
    }

    public static String replace(HashMap<String, String> replacements, String text) {
        for (String key : replacements.keySet()) {
            text = text.replace(key, replacements.get(key));
        }


        return text;
    }

    /**
     * Put integerst together into a singe string
     *
     * @param digits digits to add together
     *
     * @return concatenated string
     * */
    public static String concatenateDigits(int... digits) {
        StringBuilder sb = new StringBuilder(digits.length);
        for (int digit : digits) {
            sb.append(digit);
        }
        return sb.toString();
    }
    /**
     * Add and increment value inside of a map
     *
     * @param map map to increment
     * @param key key of the map to use
     * @param amount amount to increment by
     *
     * */
    public static<K> void increment(Map<K, Integer> map, K key, int amount) {
        map.putIfAbsent(key, 0);
        map.put(key, map.get(key) + amount);
    }





}
