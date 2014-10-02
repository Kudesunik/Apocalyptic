package apocalyptic.itemtransfer;

import java.io.File;
import java.io.IOException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Apocalyptic ItemTransfer NBT Tags handler
 * @author Kunik
 */
public class NBTHandler {
    
    public static File[] listOfFiles;
    
    public NBTHandler () {
        File folder = new File(".");
        listOfFiles = folder.listFiles();
    }
    
    public static void main (String... args) {
        NBTHandler nbt = new NBTHandler();
        nbt.showCat(listOfFiles);
    }
    
    public void showCat(File[] listOfFiles) {
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                String files = listOfFiles[i].getName();
                System.out.println(files);
            }
        }
    }
}