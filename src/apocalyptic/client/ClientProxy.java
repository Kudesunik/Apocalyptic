package apocalyptic.client;

import apocalyptic.Apocalyptic;
import apocalyptic.CommonProxy;
import apocalyptic.client.renderer.GasRenderer;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)

public class ClientProxy extends CommonProxy {
	public static boolean impactStatus;
        private static int radiation;

    @Override
    public void registerRenderers() {
            Apocalyptic.gasModel = RenderingRegistry.getNextAvailableRenderId();
            
            RenderingRegistry.registerBlockHandler(new GasRenderer());
    }
    
    public static void setRadiation (int rad) {
        radiation = rad;
    }
    
    public static int getRadiation () {
        return radiation;
    }

    /**
    public void initTickHandler(Apocalyptic apocalyptic) {
        TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT); //Register for CTH is not needed
    }
    */

}
