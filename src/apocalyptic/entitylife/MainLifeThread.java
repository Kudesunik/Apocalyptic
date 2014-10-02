package apocalyptic.entitylife;

import apocalyptic.ServerTickHandler;
import apocalyptic.util.NBTHandler;
import ic2.api.item.ElectricItem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;

/**
 * @author Kunik
 */
public class MainLifeThread {

    private Impacts impacts;
    private List<String> nowPlayers;
    private List<String> lastPlayers;
    public volatile static ArrayList<PlayerLife> alLife = new ArrayList<PlayerLife>();
    public static ServerConfigurationManager confManager;
    public static Map<String, String[]> infoHash;
    public static NBTHandler nbt;
    private int tick;
    public volatile boolean flag;

    public MainLifeThread() {
        infoHash = new ConcurrentHashMap<String, String[]>();
        nowPlayers = new ArrayList<String>();
        lastPlayers = new ArrayList<String>();
        confManager = MinecraftServer.getServer().getConfigurationManager();
        nbt = NBTHandler.getInstance();
        impacts = Impacts.getInstance();
        tick = 120;
        flag = false;
    }

    public void run() {
        setPlayersHash();
        startProcesses();
        ServerTickHandler.flag = true;
    }

    private void startProcesses() {
        for (PlayerLife fl : alLife) {
            fl.startProcess();
        }
        Impacts.getInstance().startProcess();
        checkSave();
    }

    private void checkSave() {
        if (tick == 0) {
            saveAllPlayers();
            tick = 120;
        } else {
            tick--;
        }
    }

    public void setPlayersHash() {
        List list = confManager.playerEntityList;
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            EntityPlayerMP player = (EntityPlayerMP) iterator.next();
            checkPlayerContainment(player.getEntityName());
        }
        checkPlayerExit();
        for (String pl : infoHash.keySet()) {
            EntityPlayerMP plEP = confManager.getPlayerForUsername(pl);
            String[] armor = new String[5];
            for (int iter1 = 0; iter1 < armor.length; iter1++) {
                ItemStack armSrch = plEP.getCurrentItemOrArmor(iter1);
                if (armSrch != null) {
                    if (ElectricItem.manager.canUse(armSrch, 1)) {
                        armor[iter1] = armSrch.getItemName() + "charged";
                    } else {
                        armor[iter1] = armSrch.getItemName();
                    }
                } else {
                    armor[iter1] = "null";
                }
            }
            infoHash.put(pl, strCreator((int) plEP.posY, plEP.getHealth(), String.valueOf(plEP.dimension), String.valueOf(plEP.isPlayerSleeping()), armor[1], armor[2], armor[3], armor[4]));
        }
    }

    private void checkPlayerContainment(String name) {
        nowPlayers.add(name);
        if (!lastPlayers.contains(name)) {
            infoHash.put(name, new String[8]);
            alLife.add(new PlayerLife(name, nbt.readNBT(name, "feeling"), nbt.readNBT(name, "radiation")));
        }
    }

    private void checkPlayerExit() {
        for (String pl : lastPlayers) {
            if (!nowPlayers.contains(pl)) {
                infoHash.remove(pl);
                ListIterator<PlayerLife> alFeelIter = alLife.listIterator();
                while (alFeelIter.hasNext()) {
                    PlayerLife flIter = alFeelIter.next();
                    if (flIter.name.equals(pl)) {
                        nbt.writeNBT(pl, "feeling", flIter.feeling);
                        nbt.writeNBT(pl, "radiation", flIter.radiation);
                        alFeelIter.remove();
                    }
                }
            }
        }
        lastPlayers.clear();
        lastPlayers.addAll(nowPlayers);
        nowPlayers.clear();
    }

    public void saveAllPlayers() {
        for (String pl : lastPlayers) {
            for (PlayerLife fl : alLife) {
                if (fl.name.equals(pl)) {
                    nbt.writeNBT(pl, "feeling", fl.feeling);
                    nbt.writeNBT(pl, "radiation", fl.radiation);
                }
            }
        }
        Impacts.getInstance().saveNBT();
    }

    private String[] strCreator(int height, int health, String world, String isSleeping, String eq1, String eq2, String eq3, String eq4) {
        return new String[]{String.valueOf(height), String.valueOf(health), world, isSleeping, eq1, eq2, eq3, eq4};
    }
}