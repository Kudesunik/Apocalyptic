package apocalyptic.util;

import java.io.File;
import java.io.IOException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import apocalyptic.Apocalyptic;
import com.google.common.io.Files;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NBTTagList;

/**
 * Apocalyptic NBT Tags handler
 * @author Kunik
 */
public class NBTHandler {
    public static File nbtFile;
    private static NBTTagCompound nbtTagsNames;
    public File nbtInventoryFile;
    public DataOutputStream outputStream;
    public static Map<String, Integer> totalItems = new HashMap<String, Integer>();
    private Map<String, String> convId = new HashMap<String, String>();
    
    public static class NBTHandlerImpl {
        public static final NBTHandler INSTANCE = new NBTHandler();
    }
    
    public static NBTHandler getInstance() {
        return NBTHandlerImpl.INSTANCE;
    }
    
    public NBTHandler () {
        nbtFile = new File("Apocalyptic.nbt");
        checkCreatedTagsFile();
        setValuesForIdsConverter();
    }
    
    /**
     * Old value - new value
     */
    private void setValuesForIdsConverter() {
        convId.put("2004", "2003"); //Engine
        convId.put("2003", "2004"); //Tank
        convId.put("2009", "2012"); //Rockwool
        convId.put("20260", "20266");
        convId.put("20261", "20267");
        convId.put("20258", "20259");
        convId.put("20262", "0");
        convId.put("20263", "0");
    }
    
    public void filterInventory(String playerName) {
        try {
            File playerFile = getOldPlayerFile(playerName);
            if (playerFile == null) return;
            NBTTagCompound nbtPlayer = CompressedStreamTools.decompress(Files.toByteArray(playerFile));
            NBTTagList inventoryTagList = (NBTTagList)nbtPlayer.getTagList("Inventory").copy();
            NBTTagList newInventoryTagList = new NBTTagList();
            for (int i = 0; i < inventoryTagList.tagCount(); ++i) {
                NBTTagCompound nbttagcompound = (NBTTagCompound)inventoryTagList.tagAt(i);
                
                if (convId.containsKey(nbttagcompound.getTag("id").toString())) {
                    nbttagcompound.setShort("id", Short.parseShort(convId.get(nbttagcompound.getTag("id").toString())));
                }
                
                NBTTagCompound transferredInventoryTag = transferLimitFilter(nbttagcompound);
                if (transferredInventoryTag != null) {
                    newInventoryTagList.appendTag(transferredInventoryTag);
                }
            }
            File newPlayerFile = getNewPlayerFile(playerName);
            NBTTagCompound newNbtPlayer = CompressedStreamTools.decompress(Files.toByteArray(newPlayerFile));
            NBTTagList newPlayerInventoryTagList = new NBTTagList();
            for (int i = 0; i < newInventoryTagList.tagCount(); ++i) {
                NBTTagCompound nbttagcompound = (NBTTagCompound)newInventoryTagList.tagAt(i);
                newPlayerInventoryTagList.appendTag(nbttagcompound);
                System.out.println(nbttagcompound);
            }
            newNbtPlayer.removeTag("Inventory");
            newNbtPlayer.setTag("Inventory", newPlayerInventoryTagList);
            outputStream = new DataOutputStream(new FileOutputStream(newPlayerFile));
            CompressedStreamTools.writeCompressed(newNbtPlayer, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private NBTTagCompound transferLimitFilter(NBTTagCompound nbttagcompound) {
        for (String str : Apocalyptic.transferLimit) {
            String[] strArr = str.split(":");
            if (nbttagcompound.getTag("id").toString().equals(strArr[0]) && (nbttagcompound.getTag("Damage").toString().equals(strArr[1]) || strArr[1].equals("X"))) {
                String hashKey = strArr[0] + strArr[1];
                if (strArr[2].equals("0")) {
                    return null;
                }
                else if (totalItems.containsKey(hashKey) && totalItems.get(hashKey) == Integer.parseInt(strArr[2])) {
                    return null;
                }
                else if (strArr[2].matches("\\d*")) {
                    int inStack  = nbttagcompound.getByte("Count");
                    int set = Integer.parseInt(strArr[2]);
                    if (totalItems.containsKey(hashKey)) {
                        int total = totalItems.get(hashKey) + inStack;
                        if (total > set) {
                            nbttagcompound.setByte("Count", (byte) (total - set));
                            totalItems.put(hashKey, set);
                        }
                        else {
                            totalItems.put(hashKey, (totalItems.get(hashKey) + inStack));
                        }
                    }
                    else {
                        if (inStack > set) {
                            nbttagcompound.setByte("Count", (byte) set);
                        }
                        totalItems.put(hashKey, set);
                    }
                }
            }
        }
        for (String str : Apocalyptic.transferTagRemove) {
            String[] strArr = str.split(":");
            if (nbttagcompound.getTag("id").toString().equals(strArr[0]) && (nbttagcompound.getTag("Damage").toString().equals(strArr[1]) || strArr[1].equals("X")) && nbttagcompound.getTag("tag") != null) {
                nbttagcompound.setTag("tag", new NBTTagCompound("tag"));
            }
        }
        return nbttagcompound;
    }
    
    private String convertOldIds(String str) {
        if (convId.containsKey(str)) {
            return convId.get(str);
        }
        else {
            return str;
        }
    }
    
    private File getOldPlayerFile(String playerName) {
        File directory = new File("players");
        for (File file : directory.listFiles()) {
            if (file.getName().equalsIgnoreCase(playerName + ".dat")) {
                return file;
            }
        }
        return null;
    }
    
    private File getNewPlayerFile(String playerName) {
        File directory = new File("Apocalyptic/players");
        for (File file : directory.listFiles()) {
            if (file.getName().equalsIgnoreCase(playerName + ".dat")) {
                return file;
            }
        }
        try {
            File nullFile = new File("players/0.dat");
            NBTTagCompound nbtPlayer = CompressedStreamTools.decompress(Files.toByteArray(nullFile));
            File newPlayerFile = new File("Apocalyptic/players/" + playerName + ".dat");
            outputStream = new DataOutputStream(new FileOutputStream(newPlayerFile));
            CompressedStreamTools.writeCompressed(nbtPlayer, outputStream);
            outputStream.flush();
            outputStream.close();
            return newPlayerFile;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    private void checkCreatedTagsFile () {
            if (!nbtFile.exists()) {
                try {
                    nbtFile.createNewFile();
                    NBTTagCompound newNBTCompound = new NBTTagCompound("impacts");
                    newNBTCompound.setInteger("smallImpact1x", 3600);
                    newNBTCompound.setInteger("smallImpact2x", 1800);
                    newNBTCompound.setInteger("smallImpact3x", 1200);
                    newNBTCompound.setInteger("greatImpact", 20);
                    NBTTagCompound writeNewNBTCompound = new NBTTagCompound();
                    writeNewNBTCompound.setCompoundTag("impacts", newNBTCompound);
                    CompressedStreamTools.write(writeNewNBTCompound, nbtFile);
                    nbtTagsNames = CompressedStreamTools.read(nbtFile);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
    }
    
    public double readNBT (String name, String type) {
        readNBTTags(name);
        return nbtTagsNames.getCompoundTag(name).getDouble(type);
    }
    
    public int readImpactsNBT (boolean great, int dim) {
        readNBTTags(null);
        NBTTagCompound impNBT = nbtTagsNames.getCompoundTag("impacts");
        if (great) {
            return impNBT.getInteger("greatImpact");
        }
        else {
            switch (dim) {
                case 0:
                    return impNBT.getInteger("smallImpact1x");
                case 2:
                    return impNBT.getInteger("smallImpact2x");
                case 3:
                    return impNBT.getInteger("smallImpact3x");
                default:
                    return 0;
            }
        }
    }
    
    public void writeImpactsNBT (int great, int small1x, int small2x, int small3x) {
        readNBTTags(null);
        nbtTagsNames.getCompoundTag("impacts").setInteger("greatImpact", great);
        nbtTagsNames.getCompoundTag("impacts").setInteger("smallImpact1x", small1x);
        nbtTagsNames.getCompoundTag("impacts").setInteger("smallImpact2x", small2x);
        nbtTagsNames.getCompoundTag("impacts").setInteger("smallImpact3x", small3x);
        try {
            CompressedStreamTools.write(nbtTagsNames, nbtFile);
        readNBTTags(null);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void writeNBT (String name, String type, Double value) {
        readNBTTags(name);
        nbtTagsNames.getCompoundTag(name).setDouble(type, value);
        try {
            CompressedStreamTools.write(nbtTagsNames, nbtFile);
        } catch (IOException ex) {
            Apocalyptic.apLogger.severe("NBT writing error");
        }
    }
    
    private void readNBTTags (String name) {
        try {
            nbtTagsNames = CompressedStreamTools.read(nbtFile);
            if (name != null && !nbtTagsNames.hasKey(name)) {
                NBTTagCompound newNBTCompound = new NBTTagCompound(name);
                newNBTCompound.setDouble("feeling", 100);
                newNBTCompound.setDouble("radiation", 0);
                nbtTagsNames.setCompoundTag(name, newNBTCompound);
                CompressedStreamTools.write(nbtTagsNames, nbtFile);
                nbtTagsNames = CompressedStreamTools.read(nbtFile);
            }
        } catch (IOException ex) {
            Apocalyptic.apLogger.severe("NBT tag reading error");
        }
    }
}
