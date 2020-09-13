package me.pustinek.humblelibrary.utils;

import org.bukkit.ChatColor;

import java.awt.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChatUtils {

    private static final Pattern REPLACE_ALL_RGB_PATTERN = Pattern.compile("(&)?&#([0-9a-fA-F]{6})");

    /**
     * @throws NumberFormatException If the provided hex color code is invalid or if version is lower than 1.16.
     */
    public static String parseHexColor(String hexColor) throws NumberFormatException {

        if (VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_16_1_R01)) {
            throw new NumberFormatException("Cannot use RGB colors in versions < 1.16");
        }

        if (hexColor.startsWith("#")) {
            hexColor = hexColor.substring(1); //fuck you im reassigning this.
        }
        if (hexColor.length() != 6) {
            throw new NumberFormatException("Invalid hex length");
        }
        Color.decode("#" + hexColor);
        StringBuilder assembledColorCode = new StringBuilder();
        assembledColorCode.append("\u00a7x");
        for (char curChar : hexColor.toCharArray()) {
            assembledColorCode.append("\u00a7").append(curChar);
        }
        return assembledColorCode.toString();
    }


    /**
     * Replace characters in a string, that can be displayed as colors
     * in console/user
     *
     * @param s string to parse
     */
    public static String chatColor(String s) {
        StringBuffer rgbBuilder = new StringBuffer();
        Matcher rgbMatcher = REPLACE_ALL_RGB_PATTERN.matcher(s);
        while (rgbMatcher.find()) {
            boolean isEscaped = (rgbMatcher.group(1) != null);
            if (!isEscaped) {
                try {
                    String hexCode = rgbMatcher.group(2);
                    rgbMatcher.appendReplacement(rgbBuilder, parseHexColor(hexCode));
                    continue;
                } catch (NumberFormatException ignored) {
                }
            }
            rgbMatcher.appendReplacement(rgbBuilder, "&#$2");
        }
        rgbMatcher.appendTail(rgbBuilder);

        s = rgbBuilder.toString();

        return ChatColor.translateAlternateColorCodes('&', s);
    }

    /**
     * Replace characters in a strings, that can be displayed as colors
     * in console/user
     *
     * @param lore list of strings to parse
     */
    public static java.util.List<String> chatColor(List<String> lore) {
        return lore.stream().map(ChatUtils::chatColor).collect(Collectors.toList());
    }

}
