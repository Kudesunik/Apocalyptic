package apocalyptic;

import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {
    public static String APOCALYPTIC_ITEMS = "/apocalyptic/sprites/apocalyptic_items.png";
    public static String APOCALYPTIC_BLOCKS = "/apocalyptic/sprites/apocalyptic_blocks.png";

    public static long lastImpact;
    
    public static long futureImpact;
    
    public static boolean invisibleBlocks = false;

    public static boolean detectorStatus;

    //Timed
    public static boolean deleter;
    public static boolean relighter;
    
    public static int skyLight = 0xff54f6;
    
    public static float bordLight = 0.0529412F;
    public static float bordLight2 = 0.84705883F;
    public static float bordLight3 = 0.5F;

    public void registerRenderers() {}

    public void registerTextureFX() {}

    public void initTickHandler(Apocalyptic apocalyptic) {
    	TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
    }
}
