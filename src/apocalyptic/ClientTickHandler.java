package apocalyptic;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

/**
 * Unusable client tick handler, see server tick handler
 * Attention, this class is not registered on main class Apocalyptic
 * @author Kunik
 *
 */

public class ClientTickHandler implements ITickHandler
{
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {}

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        if (type.equals(EnumSet.of(TickType.RENDER)))
        {
            onRenderTick();
        }
        else if (type.equals(EnumSet.of(TickType.CLIENT)))
        {
            GuiScreen guiscreen = Minecraft.getMinecraft().currentScreen;

            if (guiscreen != null)
            {
                onTickInGUI(guiscreen);
            }
            else
            {
                onTickInGame();
            }
        }
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.RENDER, TickType.CLIENT);
    }

    @Override
    public String getLabel()
    {
        return null;
    }

    public void onRenderTick()
    {
    	Minecraft mc = FMLClientHandler.instance().getClient();
    }

    public void onTickInGUI(GuiScreen guiscreen)
    {
        onTickInGame();
    }

    public void onTickInGame() {
    	Minecraft mc = FMLClientHandler.instance().getClient();
        World world = mc.theWorld;
        EntityPlayer player = mc.thePlayer;
    }
}