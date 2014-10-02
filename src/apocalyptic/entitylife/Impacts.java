package apocalyptic.entitylife;

import apocalyptic.util.PrivateCleanerBridge;
import ic2.api.item.ElectricItem;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Singleton class for Apocalyptic impacts.
 * @author Kunik
 */
public class Impacts {
    
    private int[] impacts;
    private Random rand = new Random();
    private Calendar time = new GregorianCalendar();
    private int dayFlag;
    private int tick;
    
    public static class ImpactsImpl {
        public static final Impacts INSTANCE = new Impacts();
    }
    
    public static boolean status;
    private static Map <String, String[]> playerInfo;
    
    
    public Impacts () {
        playerInfo = MainLifeThread.infoHash;
        impacts = new int[4];
        impacts[0] = MainLifeThread.nbt.readImpactsNBT(false, 0);
        impacts[1] = MainLifeThread.nbt.readImpactsNBT(false, 2);
        impacts[2] = MainLifeThread.nbt.readImpactsNBT(false, 3);
        impacts[3] = MainLifeThread.nbt.readImpactsNBT(true, 0);
        dayFlag = 0;
        tick = 360;
        status = false;
    }
    
    public static Impacts getInstance() {
        return ImpactsImpl.INSTANCE;
    }
    
    public void startProcess () {
        checkImpacts();
        checkSave();
        status = true;
    }
    
    private void checkSave () {
        if (tick == 0) {
            saveNBT();
            tick = 360;
        }
        else {
            tick--;
        }
    }
    
     public void saveNBT () {
        MainLifeThread.nbt.writeImpactsNBT(impacts[3], impacts[0], impacts[1], impacts[2]);
    }
    
    private void checkImpacts() {
        for (int iter1 = 0; iter1 < 4; iter1++) {
            if (impacts[iter1] == 0) {
                switch (iter1) {
                    case 0:
                        setSmallEffects(0);
                        impacts[iter1] = 1800 + rand.nextInt(3600);
                        break;
                    case 1:
                        setSmallEffects(1);
                        impacts[iter1] = 900 + rand.nextInt(1800);
                        break;
                    case 2:
                        setSmallEffects(2);
                        impacts[iter1] = 600 + rand.nextInt(1200);
                        break;
                    case 3:
                        setGreatEffects();
                        impacts[iter1] = calculateGreatImpact();
                        break;
                }
            }
            else {
                impacts[iter1]--;
            }
        }
    }
    
    private int calculateGreatImpact () {
        int grImpTime = 0;
        int tm;
        do {
            grImpTime = rand.nextInt(86400);
            tm = (grImpTime/3600) + time.get(Calendar.HOUR_OF_DAY);
            if (tm > 24) {
                tm -=24;
            }
        }
        while (tm <= 8 && tm >= 1 && (dayFlag == Calendar.getInstance().getTime().getDate() || dayFlag == 0));
        dayFlag = Calendar.getInstance().getTime().getDate();
        return grImpTime;
    }

    private void setSmallEffects (int i) {
        List players = MainLifeThread.confManager.playerEntityList;
        Iterator iterator = players.iterator();
        while(iterator.hasNext()) {
            EntityPlayerMP player = (EntityPlayerMP) iterator.next();
            String name = player.getEntityName();
            World world = player.getServerForPlayer().provider.worldObj;
            if (player.getServerForPlayer().provider.dimensionId == i) {
                if (Boolean.parseBoolean(MainLifeThread.infoHash.get(name)[3]) == false && findPlayerFeeling(name) < 99) {
                    addPlayerSmallEffect(player);
                }
                addMobsEffect(player, world);
            }
        }
    }
    
    private void setGreatEffects () {
        List players = MainLifeThread.confManager.playerEntityList;
        Iterator iterator = players.iterator();
        while(iterator.hasNext()) {
            EntityPlayerMP player = (EntityPlayerMP) iterator.next();
            addPlayerGreatEffect(player);
        }
        //PrivateCleanerBridge.getInstance().testRegeneration();
    }
    
    private double findPlayerFeeling (String nm) {
        for (PlayerLife pl: MainLifeThread.alLife) {
            if (pl.name.equals(nm)) {
                return pl.feeling;
            }
        }
        return -1;
    }
    
    private void addPlayerSmallEffect(EntityPlayerMP player) {
	player.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 200, 1));
	player.addPotionEffect(new PotionEffect(Potion.confusion.id, 200, 1));
	player.addPotionEffect(new PotionEffect(Potion.weakness.id, 200, 1));
	player.attackEntityFrom(DamageSource.magic, 1);
    }
    
    private void addPlayerGreatEffect (EntityPlayerMP player) {
        player.addPotionEffect(new PotionEffect(Potion.blindness.id, 400, 1));
        player.addPotionEffect(new PotionEffect(Potion.confusion.id, 200, 1));
        player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 600, 1));
        player.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 600, 1));
        player.addPotionEffect(new PotionEffect(Potion.weakness.id, 600, 1));
        decreasePlayerArmorCharge(player);
        player.attackEntityFrom(DamageSource.magic, 1);
        player.setEntityHealth(6);
    }
    
    private void decreasePlayerArmorCharge (EntityPlayerMP player) {
        for (int iter1 = 1; iter1 < 5; iter1++) {
            ItemStack armSrch = player.getCurrentItemOrArmor(iter1);
            if (armSrch != null) {
                int iter2 = 0;
                while (ElectricItem.canUse(armSrch, iter2)) {
                    iter2++;
                }
                ElectricItem.discharge(armSrch, iter2, 3, true, false);
            }
        }
    }
    
    private static void addMobsEffect(EntityPlayerMP player, World world) {
        List entityMob = world.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getBoundingBox(player.posX - 64, player.posY - 64, player.posZ - 64, player.posX + 64, player.posY + 64, player.posZ + 64));
        List entityAnimal = world.getEntitiesWithinAABB(EntityAnimal.class, AxisAlignedBB.getBoundingBox(player.posX - 64, player.posY - 64, player.posZ - 64, player.posX + 64, player.posY + 64, player.posZ + 64));
        Iterator entityMobIter = entityMob.iterator();
        Iterator entityAnimalIter = entityAnimal.iterator();
        while (entityMobIter.hasNext()) {
            EntityMob var4 = (EntityMob)entityMobIter.next();
            var4.addPotionEffect(new PotionEffect(Potion.weakness.id, 400));
            var4.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 400));
            var4.setFire(5);
        }
        while (entityAnimalIter.hasNext()) {
            EntityAnimal var4 = (EntityAnimal)entityAnimalIter.next();
            var4.addPotionEffect(new PotionEffect(Potion.weakness.id, 400));
            var4.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 400));
            var4.setFire(5);
        }
    }
}
