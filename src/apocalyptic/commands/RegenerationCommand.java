package apocalyptic.commands;

import apocalyptic.util.PrivateCleanerBridge;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class RegenerationCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "regenerate";
    }

    @Override
    public void processCommand(ICommandSender var1, String[] var2) {
        PrivateCleanerBridge.getInstance().regenerateSelection(var1.getCommandSenderName());
    }

    @Override
    public String getCommandUsage(ICommandSender par1ICommandSender) {
        return "/" + getCommandName();
    }
}