package apocalyptic;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class EventPlayerHook {

    @ForgeSubscribe
    public void entityAttacked(LivingDeathEvent event) {
        Entity player = event.entity;

        if (player instanceof EntityPlayer) {
            ((EntityPlayer) player).closeScreen();
        }

        if (event.isCancelable()) {
            event.setCanceled(false);
        }
    }
}
