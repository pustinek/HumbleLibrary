package me.pustinek.humblelibrary.utils;

import com.udojava.evalex.Expression;
import me.pustinek.humblelibrary.HumbleLibrary;
import me.pustinek.humblelibrary.nms.interfaces.NMS;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings(value = {"unused", "UnusedReturnValue"})
public class ItemBuilder implements Cloneable {
    private static final Pattern conditionPattern = Pattern.compile("\\{(con:)(.*)}");
    private final List<EnchantmentSetting> enchantments = new ArrayList<>();
    private final Set<ItemFlag> flags = new HashSet<>();
    private String name;
    private Material material;
    private String skullBase64;
    private List<String> lore = new ArrayList<>();
    private int amount = 1;

    private Integer customModelData = null;

    private HashMap<String, String> loreReplacements = new HashMap<>();
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
                this.name = meta.getDisplayName();
                meta.getEnchants().forEach((enc, lvl) -> {
                            EnchantmentSetting es = new EnchantmentSetting(enc, lvl, true);
                            enchantments.add(es);
                        }
                );
                flags.addAll(meta.getItemFlags());
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

    public ItemBuilder setLoreReplacements(HashMap<String, String> replacements) {
        this.loreReplacements = replacements;
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
            for (String key : loreReplacements.keySet()) {
                str = str.replace(key, loreReplacements.get(key));
            }
            finalStringList.add(str);
        }
        lore = finalStringList;
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


    public ItemStack build() {
        if (amount < 1) amount = 1;
        if (amount > 64) amount = 64;
        ItemStack item = new ItemStack(material, amount);

        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta == null) return item;

        if (name != null)
            itemMeta.setDisplayName(ChatUtils.chatColor(name));

        replaceLore();

        if (loreConditions != null && !loreConditions.isEmpty()) {
            this.lore = conditionalLoreParsing(lore, loreConditions);
        }


        if (!lore.isEmpty()) {
            List<String> lore = (itemMeta.hasLore() && itemMeta.getLore() != null) ? itemMeta.getLore() : new ArrayList<>();
            lore.addAll(this.lore);
            itemMeta.setLore(ChatUtils.chatColor(lore));
        }

        for (EnchantmentSetting enchantment : enchantments) {
            itemMeta.addEnchant(enchantment.enchantment, enchantment.level, enchantment.ignoreLevelRestrictions);
        }

        for (ItemFlag itemFlag : flags) {
            itemMeta.addItemFlags(itemFlag);
        }

        if (this.skullBase64 != null && !this.skullBase64.isEmpty() && material == Material.PLAYER_HEAD) {
            NMS nmsHandler = HumbleLibrary.getNmsHandler();
            if(nmsHandler != null)
             nmsHandler.setItemSkull(itemMeta, skullBase64);
        }

        if(customModelData != null)
            itemMeta.setCustomModelData(customModelData);

        item.setItemMeta(itemMeta);
        return item;
    }

    @Override
    public ItemBuilder clone() {
        return new ItemBuilder(material, name).addLore(lore).setLoreReplacements(loreReplacements).setLoreConditions(loreConditions).setAmount(amount);
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
