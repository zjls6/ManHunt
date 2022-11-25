package cc.zjlsx.manhunt.utils;

import net.md_5.bungee.api.ChatColor;

public class Color {

    /**
     * @param text The string of text to apply color/effects to
     * @return Returns a string of text with color/effects applied
     */
    public static String str(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

}
