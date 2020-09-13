package me.pustinek.humblelibrary.utils;

import me.pustinek.humblelibrary.exceptions.ItemSlotParseException;
import me.pustinek.humblelibrary.inventory.ItemStackProperty;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class InventoryUtils {

    /**
     * Get the next valid inventory size, for chest (column size 9)
     * Limited between 0-54
     * @param inventorySize size of the inventory
     *
     * @return next available inventory size
     * */
    public static int getNextValidInventorySize(int inventorySize) {
        int nextSize = (int) (9 * (Math.ceil(Math.abs(inventorySize / 9))));
        return (Math.min(nextSize, 54));
    }

    /*
     * Parse slots for inventory
     * examples:
     * - 1,2,3,4 --> 1,2,3,4
     * - 0-2,11-13 -->
     * - 0|0-9 --> 0,1,2,3,4,5,6,7,9
     *
     * */
    public static List<Integer> parseSlots(@NotNull String s) throws ItemSlotParseException {
        ArrayList<Integer> slots = new ArrayList<>();
        if (s.isEmpty()) return slots;

        String[] parts = s.split(";");

        for (String part : parts) {
            if (part.contains("|")) {
                //TODO:  parse as relative

                String[] split = part.split("\\|");

                if (split.length < 2) continue;

                ArrayList<Integer> rows = parseSlotValues(split[0], true);
                ArrayList<Integer> relativeSlots = parseSlotValues(split[1], true);
                rows.forEach(row -> {
                    int startingSlot = (row * 9);
                    for (Integer relativeSlot : relativeSlots) {
                        slots.add(startingSlot + relativeSlot);
                    }
                });
            } else {
                // parse as absolute in inventory sense
                slots.addAll(parseSlotValues(part, false));
            }
        }

        return slots;
    }

    /**
     * Remove items from inventory
     *
     * @param inventory inventory to remove from
     * @param itemStack item to remove from inventory
     * @param amount amount to remove
     * @param toCompare what properties to compare in item, leaving empty will use itemstack.isSimlar() (default)
     *
     * @return amount of items removed
     */
    public static int removeItem(Inventory inventory, ItemStack itemStack, int amount, ItemStackProperty ...toCompare) {
        ItemStack[] contents = inventory.getContents();

        int toRemove = amount;
        int removedAmount = 0;
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if(item == null) continue;
            if(!isSimilar(item, itemStack, toCompare)) continue;

            int amountInItemStack = item.getAmount();

            if (amountInItemStack < toRemove) {
                removedAmount += amountInItemStack;
                toRemove -= amountInItemStack;
                inventory.clear(i);
            } else {
                int difference = amountInItemStack - toRemove;
                removedAmount += toRemove;
                toRemove = 0;
                item.setAmount(difference);
                inventory.setItem(i, item);
            }
        }

        return removedAmount;
    }
    /**
     * Remove all items from inventory
     *
     * @param inventory inventory to remove from
     * @param itemStack item to remove from inventory
     * @param toCompare what properties to compare in item, leaving empty will use itemstack.isSimlar() (default)
     *
     * @return amount of items removed
     */
    public static int removeItem(Inventory inventory, ItemStack itemStack, ItemStackProperty ...toCompare) {
        ItemStack[] contents = inventory.getContents();

        int removedAmount = 0;
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if(item == null) continue;
            if(!isSimilar(item, itemStack, toCompare)) continue;

            removedAmount += item.getAmount();
            inventory.clear(i);
        }

        return removedAmount;
    }

    /**
     * Add item to inventory
     *
     * @param inventory inventory to add to
     * @param item item to add
     * @param amount amount to add to inventory
     *
     * @return amount of items added to the inventory
     */
    public static int addItem(final Inventory inventory,final ItemStack item,int amount) {

        ItemStack clone = item.clone();

        final int maxSize = clone.getMaxStackSize();

        amount = Math.min(amount, checkFreeSpace(inventory, clone));
        int amountLeft = amount;
        while (amountLeft > 0){
            int amountToGive = Math.min(amountLeft, maxSize);
            clone.setAmount(amountToGive);
            inventory.addItem(clone);
            amountLeft -= amountToGive;
        }

        return amount;
    }



    /**
     * Check how much free space there is for specific item
     *
     * @param inventory inventory to check
     * @param itemStack item to check
     *
     * @return amount of available space for specified item
     */
    public static int checkFreeSpace(Inventory inventory, ItemStack itemStack) {
        int freeSpace = 0;
        int maxStackSize = itemStack.getMaxStackSize();

        for (ItemStack storageContent : inventory.getStorageContents()) {
            if (storageContent == null || storageContent.getType() == Material.AIR) {
                freeSpace += maxStackSize;
            } else if (storageContent.isSimilar(itemStack) && storageContent.getAmount() < maxStackSize) {
                freeSpace += (maxStackSize - storageContent.getAmount());
            }
        }
        return freeSpace;
    }




    /**
     * Check for similarities between two items
     *
     * @param item1 first item to compare
     * @param item2 second item to compare
     * @param toCompare properties to compare
     *
     * @return result if is similar
     */
    public static boolean isSimilar(ItemStack item1,ItemStack item2, ItemStackProperty...toCompare){
        if(item1 == null || item2 == null) return false;
        if(item1.equals(item2) || item1.isSimilar(item2)) return true;
        List<ItemStackProperty> toCheck = Arrays.asList(toCompare);


        if(toCheck.isEmpty() || toCheck.contains(ItemStackProperty.ALL)) return item1.isSimilar(item2);



        ItemMeta item2Meta = item2.getItemMeta();
        ItemMeta item1Meta = item1.getItemMeta();
        if(item1Meta == null || item2Meta == null) return false;

        return  item1.getType() == item2.getType() &&
                item1.hasItemMeta() == item2.hasItemMeta() &&
                (!toCheck.contains(ItemStackProperty.NAME) ||  item1Meta.getDisplayName().equalsIgnoreCase(item2Meta.getDisplayName())) &&
                (!toCheck.contains(ItemStackProperty.LORE) ||  item1Meta.getLore().containsAll(item2Meta.getLore())) &&
                (!toCheck.contains(ItemStackProperty.ENCHANTMENTS) || item1.getEnchantments().equals(item2.getEnchantments()))
                ;
    }



    private static ArrayList<Integer> parseSlotValues(String args, boolean isSlot) throws ItemSlotParseException {

        ArrayList<Integer> values = new ArrayList<>();

        String[] split = args.split(",");

        for (String s : split) {
            if (s.contains("-")) {
                String[] hyphenSplit = s.split("-");
                if (hyphenSplit.length != 2) {
                    throw new ItemSlotParseException(" Must have two numbers around a hyphen, not " + s);
                }
                int min = Integer.parseInt(hyphenSplit[0]);
                int max = Integer.parseInt(hyphenSplit[1]);

                if (isSlot && max > 8) {
                    throw new ItemSlotParseException("Relative slot must be between 0-8, got " + max + " !");
                }

                for (int i = min; i <= max; i++) {
                    values.add(i);
                }
            } else {
                int i = Integer.parseInt(s);
                if (i > 8 && isSlot) {
                    throw new ItemSlotParseException("Relative slot must be between 0-8, got " + i + " !");
                }
                values.add(i);
            }
        }
        return values;
    }
}

