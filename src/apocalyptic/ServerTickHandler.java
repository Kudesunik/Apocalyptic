package apocalyptic;

import apocalyptic.entitylife.MainLifeThread;
import apocalyptic.util.PrivateCleanerBridge;
import java.util.EnumSet;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ServerTickHandler implements ITickHandler
{
	public World world0;
        public World world2;
        public World world3;
        public World world4;
        private static boolean wStatus;
        private static int tick;
        public static boolean flag = true;
        private MainLifeThread mlt;

        @Override
        public void tickStart(EnumSet<TickType> type, Object... tickData) {}

        @Override
        public void tickEnd(EnumSet<TickType> type, Object... tickData)
        {
                if(type.equals(EnumSet.of(TickType.SERVER)))
                {
                        onTickInGame();
                }
        }

                public EnumSet ticks()
            {
                return EnumSet.of(TickType.SERVER);
            }

            public String getLabel()
            {
                return null;
            }

            private void onTickInGame()
            {
                if (FMLCommonHandler.instance() == null || FMLCommonHandler.instance().getMinecraftServerInstance() == null)
                {
                    return;
                }
                
                if (!wStatus) {
                    world0 = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(0);
                    PrivateCleanerBridge.getInstance().setWorld(world0);
                    world2 = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(2);
                    world3 = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(3);
                    world4 = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(4);
                    this.mlt = new MainLifeThread();
                    wStatus = true;
                }
    
                if (tick == 20) {
                    if (flag) {
                        flag = false;
                        Apocalyptic.mlt.run();
                        tick = 0;
                    }
                    else {
                        Apocalyptic.apLogger.severe("[Apocalyptic] Missing tick in life system!");
                    }
                } else {
                    tick++;
                }
            }
}