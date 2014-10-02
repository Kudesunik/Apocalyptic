package apocalyptic.commands;

import apocalyptic.teleporters.CommandTeleporter;
import apocalyptic.teleporters.TeleportTo2Lvl;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class TeleportationCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "teleport";
    }

    @Override
    public void processCommand(ICommandSender var1, String[] var2) {
        World world = null;
        for (int iter1 = 0; iter1 <= 4; iter1++) {
            if (iter1 == 1) {
                continue;
            }
            if (FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(iter1).getPlayerEntityByName(var1.getCommandSenderName()) != null) {
                world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(iter1);
            }
        }

        try {
            if (var2[0].equals("2")) {
                EntityPlayerMP player = (EntityPlayerMP) world.getPlayerEntityByName(var1.getCommandSenderName());
                player.setPosition(Double.parseDouble(var2[1]), Double.parseDouble(var2[2]), Double.parseDouble(var2[3]));
                player.mcServer.getConfigurationManager().transferPlayerToDimension(player, 2, new CommandTeleporter(player.mcServer.worldServerForDimension(2), player.mcServer));
                player.resetTeleport();
            } else if (var2[0].equals("3")) {
                EntityPlayerMP player = (EntityPlayerMP) world.getPlayerEntityByName(var1.getCommandSenderName());
                player.setPosition(Double.parseDouble(var2[1]), Double.parseDouble(var2[2]), Double.parseDouble(var2[3]));
                player.mcServer.getConfigurationManager().transferPlayerToDimension(player, 3, new CommandTeleporter(player.mcServer.worldServerForDimension(3), player.mcServer));
                player.resetTeleport();
            } else if (var2[0].equals("4")) {
                EntityPlayerMP player = (EntityPlayerMP) world.getPlayerEntityByName(var1.getCommandSenderName());
                player.setPosition(Double.parseDouble(var2[1]), Double.parseDouble(var2[2]), Double.parseDouble(var2[3]));
                player.mcServer.getConfigurationManager().transferPlayerToDimension(player, 4, new CommandTeleporter(player.mcServer.worldServerForDimension(4), player.mcServer));
                player.resetTeleport();
            } else {
                EntityPlayerMP player = (EntityPlayerMP) world.getPlayerEntityByName(var1.getCommandSenderName());
                player.setPosition(Double.parseDouble(var2[1]), Double.parseDouble(var2[2]), Double.parseDouble(var2[3]));
                player.mcServer.getConfigurationManager().transferPlayerToDimension(player, 0, new CommandTeleporter(player.mcServer.worldServerForDimension(0), player.mcServer));
                player.resetTeleport();
            }
        } catch (Exception ex) {
            var1.sendChatToPlayer("Command syntax: /teleport 'dim' 'x' 'y' 'z'");
        }
    }

    @Override
    public String getCommandUsage(ICommandSender par1ICommandSender) {
        return "/" + getCommandName() + "id" + "coordX" + "coordY" + "coordZ";
    }
}
