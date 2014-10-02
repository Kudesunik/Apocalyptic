package apocalyptic.entitylife;

import apocalyptic.PacketHandler;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

/**
 * @author Kunik
 */
public class PlayerLife {
    
    public boolean status;
    public String name;
    public double feeling;
    public double radiation;
    private Map <String, String[]> playerInfo;
    private int tick;
    private boolean healthFlag14;
    private boolean healthFlag8;
    private int radTime;
    private int effectTime;
    
    public PlayerLife (String name, double feeling, double radiation) {
        status = false;
        this.name = name;
        this.feeling = feeling;
        this.radiation = radiation;
        playerInfo = MainLifeThread.infoHash;
        healthFlag14 = false;
        healthFlag8 = false;
        radTime = 0;
        effectTime = 0;
    }
    
    public void startProcess () {
        checkInBed();
        checkInHeaven();
        calculateLife();
        setEffects();
        status = true;
    }
    
    private int checkDim () {
        return Integer.parseInt(playerInfo.get(name)[2]);
    }
    
    private void checkInBed () {
        if (checkBedStatus()) {
            this.feeling++;
            checkFeelingBounds();
        }
    }
    
    private boolean checkBedStatus () {
        if (Boolean.parseBoolean(playerInfo.get(name)[3])) {
            if (this.radiation <= 10 && this.feeling == 100) {
                if (this.getPlayer().isPlayerSleeping()) {
                    getPlayer().wakeUpPlayer(true, false, false);
                }
                return false;
            }
            else {
                return true;
            }
        }
        return false;
    }
    
    private boolean checkHeight () {
        if (checkDim() == 0) {
            if (Integer.parseInt(playerInfo.get(name)[0]) > 210) {
                return true;
            }
            else if ( Boolean.parseBoolean(playerInfo.get(name)[3])) {
                this.radiation -= 0.1D;
            }
            else {
                this.radiation -= 0.05D;
            }
        }
        return false;
    }
    
    private void checkInHeaven () {
        if (checkDim() == 4) {
            this.feeling += 3;
        }
        checkFeelingBounds();
    }
    
    private void checkFeelingBounds () {
        if (feeling > 100) {
            this.feeling = 100;
        }
        else if (feeling < 0) {
            this.feeling = 0;
        }
    }
    
    private void checkRadiationBounds () {
        if (radiation > 600) {
            this.radiation = 600;
        }
        else if (radiation < 0) {
            this.radiation = 0;
        }
    }
    
    private void calculateLife () {
        if (tick == 5) {
            calculateFeeling();
            tick = 0;
        }
        else {
            tick++;
        }
        calculateRadiation();
    }
    
    private void calculateFeeling () {
        double decreaseValue;
            switch (checkDim()) {
                case 0:
                    decreaseValue = 0.05D;
                    break;
                case 2:
                    decreaseValue = 0.167D;
                    break;
                case 3:
                    decreaseValue = 0.333D;
                    break;
                default:
                    decreaseValue = 0;
            }
            if (isArmorForFeeling()) {
                decreaseValue /= 1.2D;
            }
            if (this.radiation >= 80 && this.radiation < 100) {
                decreaseValue *= 2D;
            }
            if (this.radiation >= 100 && this.radiation < 300) {
                decreaseValue *= 4D;
            }
            if (this.radiation >= 300) {
                decreaseValue *= 8D;
            }
            if (this.feeling > 0 && this.feeling <= 5) {
                decreaseValue *= 10D;
            }
            this.feeling -= decreaseValue;
            checkFeelingBounds();
            setGUIFeeling();
    }
    
    private void calculateRadiation () {
        double increaseValue = 0;
            if (checkDim() == 0 && checkHeight()) {
                increaseValue = 0.1D;
                if (isArmorQuantumForRadiation()) {
                    increaseValue = 0.05D;
                }
                if (isArmorNanoForRadiation()) {
                    increaseValue = 0.08D;
                }
            }
            this.radiation += increaseValue;
            checkRadiationBounds();
            setGUIRadiation();
    }
    
    private void setGUIFeeling () {
        getPlayer().addExperience((int) ((this.feeling) - (getPlayer().experience * 100)));
    }
    
    private void setGUIRadiation () {
        PacketHandler.LifePacketSender((int) this.radiation, this.name);
    }
    
    private void setEffects () {
        if (feeling > 40 && feeling <= 100 && (this.healthFlag8 || this.healthFlag14)) {
            this.healthFlag8 = false;
            this.healthFlag14 = false;
        }
        if (feeling > 25 && feeling <= 40) {
            getPlayer().addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 1));
            checkHealthFlag14();
            if (Integer.parseInt(playerInfo.get(name)[1]) > 14 && healthFlag14) {
                getPlayer().setEntityHealth(14);
            }
            if (this.healthFlag8) {
                this.healthFlag8 = false;
            }
        }
        if (feeling > 5 && feeling <= 25) {
            getPlayer().addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 1));
            getPlayer().addPotionEffect(new PotionEffect(Potion.weakness.id, 100, 1));
            getPlayer().addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 100, 1));
            checkHealthFlag8();
            if (Integer.parseInt(playerInfo.get(name)[1]) > 8 && healthFlag8) {
                getPlayer().setEntityHealth(8);
            }
        }
        if (feeling > 0 && feeling <= 5) {
            getPlayer().addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 1));
            getPlayer().addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 100, 1));
            getPlayer().addPotionEffect(new PotionEffect(Potion.weakness.id, 100, 1));
            getPlayer().addPotionEffect(new PotionEffect(Potion.confusion.id, 100, 1));
        }
        if (feeling <= 0) {
            getPlayer().attackEntityFrom(DamageSource.fall, 100);
            this.feeling = 20D;
        }
        if (radiation >= 80 && radiation < 110) {
            getPlayer().addPotionEffect(new PotionEffect(Potion.hunger.id, 100, 1));
        }
        if (radiation >= 110 && radiation < 300) {
            getPlayer().addPotionEffect(new PotionEffect(Potion.hunger.id, 100, 1));
            if (radTime == 0) {
                getPlayer().addPotionEffect(new PotionEffect(Potion.confusion.id, 200, 1));
                radTime = 120;
            }
            else {
                radTime--;
            }
        }
        if (radiation >= 300 && radiation < 600) {
            getPlayer().addPotionEffect(new PotionEffect(Potion.poison.id, 100, 1));
            getPlayer().addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 1));
            getPlayer().addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 100, 1));
            if (radTime == 0) {
                getPlayer().addPotionEffect(new PotionEffect(Potion.confusion.id, 200, 1));
                radTime = 60;
            }
            else {
                radTime--;
            }
        }
        if (radiation >= 600) {
            getPlayer().attackEntityFrom(DamageSource.magic, 100);
            radiation = 400;
        }
    }
    
    private void checkHealthFlag14 () {
        if (Integer.parseInt(playerInfo.get(name)[1]) < 14 && !healthFlag8) {
            healthFlag8 = true;
        }
    }
    
    private void checkHealthFlag8 () {
        if (Integer.parseInt(playerInfo.get(name)[1]) < 8 && !healthFlag8) {
            healthFlag8 = true;
        }
    }
    
    private EntityPlayerMP getPlayer () {
        return MainLifeThread.confManager.getPlayerForUsername(name);
    }
    
    private boolean isArmorForFeeling () {
        boolean chRes = true;
        for (int iter1 = 4; iter1 <= 7; iter1++) {
            if (!playerInfo.get(name)[iter1].toLowerCase().contains("quantum") && chRes && !playerInfo.get(name)[iter1].toLowerCase().contains("charged")) {
                chRes = false;
            }
        }
        return chRes;
    }
    
    private boolean isArmorQuantumForRadiation () {
        boolean chRes = true;
        for (int iter1 = 4; iter1 <= 7; iter1++) {
            if (!playerInfo.get(name)[iter1].toLowerCase().contains("quantum") && chRes) {
                chRes = false;
            }
        }
        return chRes;
    }
    
    private boolean isArmorNanoForRadiation () {
        boolean chRes = true;
        for (int iter1 = 4; iter1 <= 7; iter1++) {
            if (!playerInfo.get(name)[iter1].toLowerCase().contains("quantum") && chRes) {
                chRes = false;
            }
        }
        if (!chRes) {
            chRes = true;
            for (int iter1 = 4; iter1 <= 7; iter1++) {
                if (!playerInfo.get(name)[iter1].toLowerCase().contains("nano") && chRes) {
                    chRes = false;
                }
            }
        }
        return chRes;
    }
}
