package me.pustinek.humblelibrary.utils;

import com.udojava.evalex.Expression;
import lombok.Getter;
import me.pustinek.humblelibrary.config.ItemConfigSettings;
import me.pustinek.humblelibrary.exceptions.ItemConfigurationException;
import me.pustinek.humblelibrary.item.SkullCreator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings(value = {"unused", "UnusedReturnValue"})
@Getter
public class ItemBuilder implements Cloneable {
    private static final Pattern conditionPattern = Pattern.compile("\\{(con:)(.*)}");
    private final List<EnchantmentSetting> enchantments = new ArrayList<>();
    private final Set<ItemFlag> flags = new HashSet<>();
    private String name;
    private Material material;
    private String skullBase64;
    private List<String> lore = new ArrayList<>();
    private boolean wasSetLoreUsed = false;
    private int amount = 1;

    private ItemMeta clonedItemMeta = null;

    private Integer customModelData = null;

    PersistentDataHolder persistentDataHolder;

    private HashMap<String, String> replacements = new HashMap<>();
    private HashMap<String, String> loreConditions = new HashMap<>();


    public ItemBuilder(Material material, String name) {
        this.material = material;
        this.name = name;
    }

    public ItemBuilder(Material material) {
        this.material = material;
    }

    public ItemBuilder(ItemStack itemStack) {

        ItemStack is = itemStack.clone();
        this.material = is.getType();
        this.amount = is.getAmount();

        if (is.hasItemMeta()) {
            ItemMeta meta = is.getItemMeta();
            if (meta != null) {
                this.clonedItemMeta = meta;
                this.name = meta.getDisplayName();
                this.customModelData = meta.hasCustomModelData() ? meta.getCustomModelData() : -1;
                meta.getEnchants().forEach((enc, lvl) -> {
                            EnchantmentSetting es = new EnchantmentSetting(enc, lvl, true);
                            enchantments.add(es);
                        }
                );

                flags.addAll(meta.getItemFlags());

                //LOAD skull:


            }
        }
    }

    public ItemBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ItemBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public ItemBuilder addLore(List<String> lore) {
        this.lore.addAll(lore);
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        this.lore.addAll(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        this.lore = lore;
        this.wasSetLoreUsed = true;
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder applyEnchantment(Enchantment enchantment, int level, boolean ignoreLevelRestrictions) {
        EnchantmentSetting setting = new EnchantmentSetting(enchantment, level, ignoreLevelRestrictions);
        enchantments.add(setting);
        return this;
    }

    public ItemBuilder applyEnchantment(Enchantment enchantment, int level) {
        applyEnchantment(enchantment, level, false);
        return this;
    }


    public ItemBuilder clearEnchantments() {
        this.enchantments.clear();
        return this;
    }

    public ItemBuilder addFlags(ItemFlag... flags) {
        this.flags.addAll(Arrays.asList(flags));
        return this;
    }

    public ItemBuilder addFlags(List<ItemFlag> flags) {
        this.flags.addAll(flags);
        return this;
    }

    public ItemBuilder clearFlags() {
        flags.clear();
        return this;
    }

    public ItemBuilder setLoreConditions(HashMap<String, String> conditions) {
        this.loreConditions = conditions;
        return this;
    }

    public ItemBuilder setReplacements(HashMap<String, String> replacements) {
        this.replacements = replacements;
        return this;
    }

    public ItemBuilder setSkull(final String base64) {
        this.skullBase64 = base64;
        return this;
    }

    public ItemBuilder setCustomModelData(final int value){
        this.customModelData = value;
        return this;
    }


    private void replaceLore() {
        ArrayList<String> finalStringList = new ArrayList<>();
        for (String str : lore) {
            for (String key : replacements.keySet()) {
                str = str.replace(key, replacements.get(key));
            }
            finalStringList.add(str);
        }
        lore = finalStringList;
    }


    private void replaceName() {
        for (String key : replacements.keySet()) {
            name = name.replace(key, replacements.get(key));
        }
    }


    public List<String> conditionalLoreParsing(List<String> lore, HashMap<String, String> conditions) {

        ListIterator<String> iterator = lore.listIterator();
        List<String> actualList = new ArrayList<>();

        while (iterator.hasNext()) {
            String next = iterator.next();
            Matcher matcher = conditionPattern.matcher(next);
            boolean matchFound = matcher.find();

            if (!matchFound) {
                actualList.add(next);
                continue;
            }

            int index = 0;
            String group = matcher.group(2);
            if (group == null) {
                actualList.add(next);
                continue;
            }

            Expression exp = new Expression(group);

            conditions.forEach(exp::setVariable);

            try {
                BigDecimal eval = exp.eval();
                if (eval.intValue() > 0) actualList.add(next.replace(matcher.group(0), ""));
            } catch (Expression.ExpressionException ex) {
                actualList.add(next);
            }
        }

        return actualList;
    }


    public static ItemBuilder fromConfig(@NotNull ConfigurationSection cs) throws ItemConfigurationException {
        return fromConfig(cs, new ItemConfigSettings(), null);
    }
    public static ItemBuilder fromConfig(@NotNull ConfigurationSection cs, ItemConfigSettings settings) throws ItemConfigurationException {
        return fromConfig(cs, settings, null);
    }
    public static ItemBuilder fromConfig(@NotNull ConfigurationSection cs,ItemConfigSettings settings, @Nullable HashMap<String, String> replacements) throws ItemConfigurationException {
        String name = cs.getString(settings.getNameKey(), "");
        String materialName = cs.getString(settings.getMaterialKey());
        List<String> lore = cs.getStringList(settings.getLoreKey());
        List<String> enchants = cs.getStringList(settings.getEnchantmentsKey());
        List<String> flags = cs.getStringList(settings.getFlagsKey());
        int customModel = cs.getInt(settings.getCustomModelDataKey(), -1);
        Material material = null;
        if (materialName != null && Material.getMaterial(materialName) != null) {
            material = Material.getMaterial(materialName);
        }


        if(material == null)
            throw new ItemConfigurationException("Material " + materialName + " doesn't exist !");


        ItemBuilder ib = new ItemBuilder(material, name).addLore(lore);

        // Set skull
        if(cs.getString(settings.getSkullKey()) != null)
            ib.setSkull(cs.getString(settings.getSkullKey()));

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


        if (replacements != null)
            ib.setReplacements(replacements);

        return ib.addFlags(itemFlags);
    }


    public ItemStack build() {
        if (amount < 1) amount = 1;
        if (amount > 64) amount = 64;
        ItemStack item = new ItemStack(material, amount);

        ItemMeta itemMeta = clonedItemMeta != null ? clonedItemMeta : item.getItemMeta();

        if (itemMeta == null) return item;



        if (name != null){
            replaceName();
            itemMeta.setDisplayName(ChatUtils.chatColor(name));
        }

        replaceLore();

        if (loreConditions != null && !loreConditions.isEmpty()) {
            this.lore = conditionalLoreParsing(lore, loreConditions);
        }

        List<String> clonedLore =  (itemMeta.hasLore() && itemMeta.getLore() != null) ? itemMeta.getLore() : new ArrayList<>();

        if(wasSetLoreUsed){
            itemMeta.setLore(ChatUtils.chatColor(lore));
        }else{
           clonedLore.addAll(ChatUtils.chatColor(lore));
           itemMeta.setLore(clonedLore);
        }

        for (EnchantmentSetting enchantment : enchantments) {
            itemMeta.addEnchant(enchantment.enchantment, enchantment.level, enchantment.ignoreLevelRestrictions);
        }

        for (ItemFlag itemFlag : flags) {
            itemMeta.addItemFlags(itemFlag);
        }

        if (this.skullBase64 != null && !this.skullBase64.isEmpty() && material == Material.PLAYER_HEAD) {
            setItemSkull(itemMeta, skullBase64);
        }

        if(customModelData != null)
            itemMeta.setCustomModelData(customModelData);

        item.setItemMeta(itemMeta);
        return item;
    }




    private void setItemSkull(@NotNull ItemMeta itemMeta, @NotNull String base64) {

        if(!(itemMeta instanceof SkullMeta skullMeta))
            return;

        SkullCreator.mutateItemMeta(skullMeta, base64);
    }



    @Override
    public ItemBuilder clone() {

        ItemBuilder itemBuilder = new ItemBuilder(material, name)
                .addLore(lore)
                .setReplacements(replacements)
                .setLoreConditions(loreConditions)
                .setAmount(amount)
                .setSkull(skullBase64);

        if(customModelData != null) itemBuilder.setCustomModelData(customModelData);

        return itemBuilder;
    }

    protected static class EnchantmentSetting {
        private final Enchantment enchantment;
        private final int level;
        private final boolean ignoreLevelRestrictions;


        public EnchantmentSetting(Enchantment enchantment, int level, boolean ignoreLevelRestrictions) {
            this.enchantment = enchantment;
            this.level = level;
            this.ignoreLevelRestrictions = ignoreLevelRestrictions;
        }
    }
}
