package apocalyptic;

import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.PacketDispatcher;

public final class Utils {
	public static int tick;

	public static int recipe = 1231;

	public static void sendChatMessageToAllPlayers(String msg)
	{
		PacketDispatcher.sendPacketToAllPlayers(new Packet3Chat(msg));
	}

	public static void findRecipe(World world) {
		if(tick == 10 && recipe > 0) {
		CraftingManager.getInstance().getRecipeList().get(recipe).toString();
		CraftingManager.getInstance().getRecipeList().remove(recipe);
		System.out.println(recipe);
		recipe--;
		tick = 0;
		}
		else {
			tick++;
		}
	}
}
