package apocalyptic;

import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;

public class EventPlayerDestroyBlock {
    
    @ForgeSubscribe
    public void breakSpeed(BreakSpeed e) {
        World world = e.entityPlayer.worldObj;
        if (world.provider.dimensionId == 4 && (e.block.blockID == 116 || e.block.blockID == 117)) {
            e.setCanceled(true);
        }
    }
}
