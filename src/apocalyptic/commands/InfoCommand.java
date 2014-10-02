package apocalyptic.commands;

import apocalyptic.bukkit.PluginInteraction;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class InfoCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "inform";
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        System.out.println("ololo");
        if (astring[0].equals("te")) {
            if (PluginInteraction.checkRights(icommandsender.getCommandSenderName())) {
                World world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(0);
                EntityLiving player = (EntityLiving) world.getPlayerEntityByName(icommandsender.getCommandSenderName());
                if (player != null) {
                    icommandsender.sendChatToPlayer("There is " + player.worldObj.getChunkFromBlockCoords((int) player.posX, (int) player.posZ).chunkTileEntityMap.size() + " TE in that chunk");
                } else {
                    icommandsender.sendChatToPlayer("You are not in overworld dimension");
                }
            } else {
                icommandsender.sendChatToPlayer("Нет прав на использование вне своей территории");
            }
        }
        else {
            icommandsender.sendChatToPlayer("Недопустимая команда. Доступно: /inform te");
        }
    }
}
