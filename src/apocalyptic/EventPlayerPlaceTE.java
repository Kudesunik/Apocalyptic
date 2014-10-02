package apocalyptic;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class EventPlayerPlaceTE {
    
    private static final List<Integer> TileEntities = new ArrayList<Integer>();
    
    @ForgeSubscribe
    @SideOnly(Side.SERVER)
    public void onEntityPlaceTE (PlayerInteractEvent event) {
        if (event.entityPlayer.worldObj.getChunkFromBlockCoords(event.x, event.z).chunkTileEntityMap.size() >= Apocalyptic.tePerChunk && event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && (event.entityPlayer.worldObj.getBlockTileEntity(event.x, event.y, event.z) == null || event.entityPlayer.isSneaking())) {
            if (event.entityLiving.getHeldItem() != null && (TileEntities.contains(event.entityLiving.getHeldItem().getItem().itemID) || ((event.entityLiving.getHeldItem().getItem().itemID < 4096) && Block.blocksList[event.entityLiving.getHeldItem().getItem().itemID].hasTileEntity()))) {
                event.setCanceled(true);
                event.entityPlayer.addChatMessage("\2474\u0414\u043E\u0441\u0442\u0438\u0433\u043D\u0443\u0442\u043E \u043C\u0430\u043A\u0441. \u043A\u043E\u043B\u0438\u0447\u0435\u0441\u0442\u0432\u043E TE \u0432 \u0447\u0430\u043D\u043A\u0435");
            }
        }
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && event.entityLiving.getHeldItem() != null) {
            if ((event.entityLiving.getHeldItem().itemID == 1407 || event.entityLiving.getHeldItem().itemID == 223) && (event.entityPlayer.worldObj.provider.dimensionId != 0)) {
                event.setCanceled(true);
                event.entityPlayer.addChatMessage("\2474Невозможно установить устройство!");
            }
            if (event.entityLiving.getHeldItem().itemID == 2007 && event.entityLiving.getHeldItem().getItemDamage() == 2 && event.entityPlayer.worldObj.provider.dimensionId == 4) {
                event.setCanceled(true);
                event.entityPlayer.addChatMessage("\2474Невозможно установить устройство!");
            }
        }
    }
    
    static {
        TileEntities.add(19416);
        TileEntities.add(19417);
        TileEntities.add(19418);
        TileEntities.add(19419);
        TileEntities.add(19420);
        TileEntities.add(19421);
        TileEntities.add(19422);
        TileEntities.add(19423);
        TileEntities.add(19424);
        TileEntities.add(19436);
        TileEntities.add(19437);
        TileEntities.add(19438);
        TileEntities.add(19439);
        TileEntities.add(19440);
        TileEntities.add(19443);
        TileEntities.add(19456);
        TileEntities.add(19457);
        TileEntities.add(19458);
        TileEntities.add(19460);
        TileEntities.add(19461);
        TileEntities.add(19464);
        TileEntities.add(19476);
        TileEntities.add(19477);
        TileEntities.add(19478);
        TileEntities.add(19479);
        TileEntities.add(19480);
        TileEntities.add(13384);
    }
}
