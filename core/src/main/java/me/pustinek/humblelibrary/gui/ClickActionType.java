package me.pustinek.humblelibrary.gui;

import lombok.Getter;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum ClickActionType {

    LEFT_CLICK(ClickType.LEFT, Stream.of("left", "l", "leftclick", "lc")
            .collect(Collectors.toList())),
    RIGHT_CLICK(ClickType.RIGHT, Stream.of("right", "r", "rightclick", "rc")
            .collect(Collectors.toList())),
    LEFT_SHIFT(ClickType.SHIFT_LEFT, Stream.of("left_shift", "ls", "leftshift", "lsc")
            .collect(Collectors.toList())),
    RIGHT_SHIFT(ClickType.SHIFT_RIGHT, Stream.of("right_shift", "rs", "rightshift", "rsc")
            .collect(Collectors.toList())),
    MIDDLE(ClickType.MIDDLE, Stream.of("middle", "m", "middleclick", "mc")
            .collect(Collectors.toList()));

    private final ClickType clickType;

    private final List<String> possibleOptions;

    ClickActionType(ClickType clickType, List<String> possibleOptions) {
        this.clickType = clickType;
        this.possibleOptions = possibleOptions;
    }

    @Nullable
    public ClickActionType getActionByOption(String option) {
        if (getPossibleOptions().contains(option))
            return this;
        return null;
    }
}
