package me.pustinek.humblelibrary.item;

import lombok.Getter;
import org.checkerframework.checker.regex.qual.Regex;

@Getter
public class ItemStackComparatorOption {

    ItemStackProperty property;
    ItemStackComperatorType type; // Regex, normal
    Regex regex;

    public ItemStackComparatorOption(ItemStackProperty property, ItemStackComperatorType type) {
        this.property = property;
        this.type = type;
    }


}
