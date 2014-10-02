package apocalyptic.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BukkitInteraction {
    
    public static void sendToPlayerChat(String name, String message) {
        sendToPlayerChat(name, message, ChatColor.WHITE);
    }
    
    public static void sendToPlayerChat(String name, String message, ChatColor color) {
        Player player = BukkitConnector.getBukkitEntityPlayer(name);
        player.sendMessage("§" + color.getChar() + message);
    }
}
