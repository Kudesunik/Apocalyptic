package apocalyptic;

import apocalyptic.entitylife.MainLifeThread;
import apocalyptic.entitylife.PlayerLife;
import java.util.Iterator;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class EventPlayerPotionEffect {
    
    private int radIter = 20;
    private int feelIter = 20;

    @ForgeSubscribe
    public void onEntityUpdate(LivingUpdateEvent event) {
        if (event.entityLiving.isPotionActive(Apocalyptic.regenRad)) {
            if (radIter == 20) {
                Iterator<PlayerLife> iter = MainLifeThread.alLife.iterator();
                while (iter.hasNext()) {
                    PlayerLife next = iter.next();
                    if (next.name.equalsIgnoreCase(event.entityLiving.getEntityName())) {
                        next.radiation += 12;
                        return;
                    }
                }
                radIter = 20;
            }
            else {
                radIter--;
            }
        }
        if (event.entityLiving.isPotionActive(Apocalyptic.regenFeel)) {
            if (feelIter == 20) {
                Iterator<PlayerLife> iter = MainLifeThread.alLife.iterator();
                while (iter.hasNext()) {
                    PlayerLife next = iter.next();
                    if (next.name.equalsIgnoreCase(event.entityLiving.getEntityName())) {
                        if (next.feeling <= 95) {
                            next.feeling += 5;
                            System.out.println("Next");
                        }
                        else {
                            next.feeling = 100;
                            System.out.println("Fin");
                            event.entityLiving.removePotionEffect(Apocalyptic.regenFeel.id);
                        }
                    }
                }
                feelIter = 20;
            }
            else {
                feelIter--;
            }
        }
    }
}
