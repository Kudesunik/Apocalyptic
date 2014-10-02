package apocalyptic.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import apocalyptic.container.ContainerFilter;
import apocalyptic.container.ContainerPortalHeaven;
import apocalyptic.container.ContainerPortalLvl2;
import apocalyptic.container.ContainerPortalLvl3;
import apocalyptic.tile.TileEntityFilter;
import apocalyptic.tile.TileEntityPortalStarterHeaven;
import apocalyptic.tile.TileEntityPortalStarterLvl2;
import apocalyptic.tile.TileEntityPortalStarterLvl3;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler{
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z){
            TileEntity tile_entity = world.getBlockTileEntity(x, y, z);

            if(tile_entity instanceof TileEntityFilter){
                    return new ContainerFilter((TileEntityFilter) tile_entity, player.inventory);
            }

            if(tile_entity instanceof TileEntityPortalStarterLvl2){
                return new ContainerPortalLvl2((TileEntityPortalStarterLvl2) tile_entity, player.inventory);
            }
            
            if(tile_entity instanceof TileEntityPortalStarterLvl3){
                return new ContainerPortalLvl3((TileEntityPortalStarterLvl3) tile_entity, player.inventory);
            }
            
            if (tile_entity instanceof TileEntityPortalStarterHeaven) {
                return new ContainerPortalHeaven((TileEntityPortalStarterHeaven) tile_entity, player.inventory);
            }

            return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z){
            TileEntity tile_entity = world.getBlockTileEntity(x, y, z);

            if(tile_entity instanceof TileEntityFilter){
                    return new GuiFilter(player.inventory, (TileEntityFilter) tile_entity);
            }

            if(tile_entity instanceof TileEntityPortalStarterLvl2){
                return new GuiPortalStarterLvl2(player.inventory, (TileEntityPortalStarterLvl2) tile_entity);
            }
            
            if(tile_entity instanceof TileEntityPortalStarterLvl3){
                return new GuiPortalStarterLvl3(player.inventory, (TileEntityPortalStarterLvl3) tile_entity);
            }
            
            if (tile_entity instanceof TileEntityPortalStarterHeaven) {
                return new GuiPortalStarterHeaven(player.inventory, (TileEntityPortalStarterHeaven) tile_entity);
            }

    return null;
    }
}
