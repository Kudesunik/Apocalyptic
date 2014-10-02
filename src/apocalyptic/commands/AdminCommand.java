package apocalyptic.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import apocalyptic.CommonProxy;
import apocalyptic.PacketHandler;
import apocalyptic.util.NBTHandler;
import apocalyptic.util.PrivateCleanerBridge;

public class AdminCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "serverop";
    }

    @Override
    public void processCommand(ICommandSender var1, String[] var2) {
        if (var2[0].equals("sky") && var2[1].equals("confirm")) {
            CommonProxy.skyLight = Integer.parseInt(var2[1], 16);
        }
        if (var2[0].equals("resetIslandRegions") && var2[1].equals("confirm")) {
            PrivateCleanerBridge.getInstance().createIslandRegions();
        }
        if (var2[0].equals("regenerate") && var2[1].equals("confirm")) {
            PrivateCleanerBridge.getInstance().testRegeneration();
        }
        if (var2[0].equals("setVision") && var2[1].equals("confirm")) {
            if (CommonProxy.invisibleBlocks) {
                CommonProxy.invisibleBlocks = false;
            }
            else {
                CommonProxy.invisibleBlocks = true;
            }
            PacketHandler.BlockVisibilitySender(CommonProxy.invisibleBlocks);
        }
        if (var2[0].equals("transfer") && var2[2].equals("confirm")) {
            NBTHandler.getInstance().filterInventory(var2[1]);
            var1.sendChatToPlayer("If player exists, his inventory transfer succeed");
        }
    }

    public String getCommandUsage(ICommandSender par1ICommandSender) {
        return "/" + getCommandName() + "name" + "status" + "additional";
    }
}
