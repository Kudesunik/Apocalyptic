package apocalyptic.util;

import apocalypticWG.providers.HeavenChunkProvider;
import apocalypticWG.providers.OverworldChunkProvider;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

@Deprecated
public class PrivateCleanerBridge {

    private ClassLoader classloader;
    private World worldObj;

    public static class PrivateCleanerBridgeImpl {
        public static final PrivateCleanerBridge INSTANCE = new PrivateCleanerBridge();
    }

    public static PrivateCleanerBridge getInstance() {
        return PrivateCleanerBridgeImpl.INSTANCE;
    }

    public void setClassloader(ClassLoader classloader) {
        System.out.println("Getted classloader from PC");
        this.classloader = classloader;
        System.out.println("Classloader = " + classloader);
    }
    
    public void setWorld (World world) {
        System.out.println("Setting world for PCB");
        this.worldObj = world;
        System.out.println("World = " + world);
    }

    private Object invokeMethod(String classname, String methodname, Class[] cls, Object[] object) throws Exception {
        System.out.println("Start getting class");
        Class clazz = Class.forName(classname, true, classloader);
        System.out.println("Getted " + clazz.getCanonicalName());
        Method method = clazz.getMethod("getInstance", new Class[0]);
        Object obj = method.invoke(clazz, new Object[0]);
        Method methodInInst = clazz.getMethod(methodname, cls);
        return methodInInst.invoke(obj, object);
    }

    public void handlePortalRegionLvl2(String name) {
        try {
            invokeMethod("ru.kunik.privatecleaner.util.ApocalypticBridge", "handlePortalRegionLvl2", new Class[]{String.class}, new Object[]{name});
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void handlePortalRegionLvl3(String name) {
        try {
            invokeMethod("ru.kunik.privatecleaner.util.ApocalypticBridge", "handlePortalRegionLvl3", new Class[]{String.class}, new Object[]{name});
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void createIslandRegions() {
        try {
            invokeMethod("ru.kunik.privatecleaner.util.ApocalypticBridge", "createIslandRegions", new Class[0], new Object[0]);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deletePortalRegion(String name) {
        try {
            invokeMethod("ru.kunik.privatecleaner.util.ApocalypticBridge", "deletePortalRegion", new Class[]{String.class}, new Object[]{name});
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean checkRights(String name) {
        try {
            return (Boolean) invokeMethod("ru.kunik.privatecleaner.util.ApocalypticBridge", "checkRights", new Class[]{String.class}, new Object[]{name});
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    public boolean isRegionInLvl2Exists(String name) {
        try {
            return (Boolean) invokeMethod("ru.kunik.privatecleaner.util.ApocalypticBridge", "isRegionInLvl2Exists", new Class[]{String.class}, new Object[]{name});
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    public boolean isRegionInLvl3Exists(String name) {
        try {
            return (Boolean) invokeMethod("ru.kunik.privatecleaner.util.ApocalypticBridge", "isRegionInLvl3Exists", new Class[]{String.class}, new Object[]{name});
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void addIslandRights(String name) {
        try {
            invokeMethod("ru.kunik.privatecleaner.util.ApocalypticBridge", "addIslandRights", new Class[]{String.class}, new Object[]{name});
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void removeIslandRights(String name) {
        try {
            invokeMethod("ru.kunik.privatecleaner.util.ApocalypticBridge", "removeIslandRights", new Class[]{String.class}, new Object[]{name});
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void regenerateSelection (String name) {
        try {
            invokeMethod("ru.kunik.privatecleaner.util.ApocalypticBridge", "regenerateSelection", new Class[]{String.class}, new Object[]{name});
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void testRegeneration () {
        try {
            invokeMethod("ru.kunik.privatecleaner.util.ApocalypticBridge", "testRegeneration", new Class[0], new Object[0]);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void spawnTrigger(String name) {
        try {
            Class clazz = Class.forName("org.greencubes.apocalyptic.ApocalypticBridge", true, classloader);
            Method methodInInst = clazz.getMethod("spawnTrigger", String.class);
            methodInInst.invoke(clazz, name);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void filterInventory(String name) {
        NBTHandler.getInstance().filterInventory(name);
    }
    
    public boolean checkBlockRegenerate (int minX, int maxX, int minY, int maxY, int minZ, int maxZ, int[] block) {
        if (minX <= block[0] && block[0] <= maxX && minY <= block[1] && block[1] <= maxY && minZ <= block[2] && block[2] <= maxZ) {
            return true;
        }
        return false;
    }
    
    public void regenerate (int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        List<int[]> chunks = new ArrayList<int[]>();
        IChunkProvider provider = new OverworldChunkProvider(worldObj, 0L, false);
        if (this.worldObj.provider.dimensionId == 4) {
            provider = new HeavenChunkProvider(worldObj, 0L, false);
        }
        for (int iter1 = minX; iter1 <= maxX; iter1++) {
            for (int iter2 = minY; iter2 <= maxY; iter2++) {
                for (int iter3 = minZ; iter3 <= maxZ; iter3++) {
                    Chunk chunk = this.worldObj.getChunkProvider().loadChunk((iter1 >> 4), (iter3 >> 4));
                    if (!this.checkArrayEquals(chunks, new int[] {chunk.xPosition, chunk.zPosition})) {
                        chunks.add(new int[] {chunk.xPosition, chunk.zPosition});
                        Chunk newChunk = provider.provideChunk(chunk.xPosition, chunk.zPosition);
                        for (int iter11 = 0; iter11 < 16; iter11++) {
                            for (int iter12 = 0; iter12 < 256; iter12++) {
                                for (int iter13 = 0; iter13 < 16; iter13++) {
                                    if (this.checkBlockRegenerate(minX, maxX, minY, maxY, minZ, maxZ, new int[] {((chunk.xPosition * 16) + iter11), iter12, ((chunk.zPosition * 16) + iter13)})) {
                                        chunk.setBlockIDWithMetadata(iter11, iter12, iter13, newChunk.getBlockID(iter11, iter12, iter13), newChunk.getBlockMetadata(iter11, iter12, iter13));
                                    }
                                }
                            }
                        }
                        try {
                            provider.provideChunk(chunk.xPosition, chunk.zPosition);
                            chunk.populateChunk(provider, this.worldObj.getChunkProvider(), chunk.xPosition, chunk.zPosition);
                        }
                        catch (Exception ex) {
                            System.out.println("Chunk population failed!");
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
    private boolean checkArrayEquals (List<int[]> list, int[] intArr) {
        for (int[] iterList : list) {
            if (Arrays.equals(iterList, intArr)) {
                return true;
            }
        }
        return false;
    }
}