package kz.ifihtich.playerProfiles.ChatUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtil {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([a-fA-F0-9]{6})");

    private static final String[][] COLOR_CODES = {
            {"&0", "<black>"}, {"&1", "<dark_blue>"}, {"&2", "<dark_green>"}, {"&3", "<dark_aqua>"},
            {"&4", "<dark_red>"}, {"&5", "<dark_purple>"}, {"&6", "<gold>"}, {"&7", "<gray>"},
            {"&8", "<dark_gray>"}, {"&9", "<blue>"}, {"&a", "<green>"}, {"&b", "<aqua>"},
            {"&c", "<red>"}, {"&d", "<light_purple>"}, {"&e", "<yellow>"}, {"&f", "<white>"},
            {"&l", "<bold>"}, {"&m", "<strikethrough>"}, {"&n", "<underlined>"}, {"&o", "<italic>"},
            {"&r", "<reset>"}
    };
    public static String convertLegacyToMiniMessage(String input) {
        if (input == null) return "";

        Matcher matcher = HEX_PATTERN.matcher(input);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, "<color:#" + matcher.group(1) + ">");
        }
        matcher.appendTail(buffer);

        String result = buffer.toString();

        for (String[] pair : COLOR_CODES) {
            result = result.replace(pair[0], pair[1]);
            result = result.replace(pair[0].toUpperCase(), pair[1]);
        }

        return result;
    }

    public static void sendMessage(CommandSender sender, String message) {
        Component component = MINI_MESSAGE.deserialize(convertLegacyToMiniMessage(message));
        sender.sendMessage(component);
    }

    public static Component toComponent(String input) {
        return MINI_MESSAGE.deserialize(convertLegacyToMiniMessage(input));
    }

    public static String formatItemText(String input) {
        Component component = toComponent(input);
        return LEGACY_SERIALIZER.serialize(component);
    }

}
