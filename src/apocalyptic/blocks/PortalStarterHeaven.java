package apocalyptic.blocks;

import apocalyptic.Apocalyptic;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import apocalyptic.ApocalypticCreativeTab;
import apocalyptic.tile.TileEntityPortalStarterHeaven;
import java.util.Random;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class PortalStarterHeaven extends BlockContainer {

    private Icon topIcon;
    private Icon sideIcon;
    private Random random = new Random();

    public PortalStarterHeaven(int par1) {
        super(par1, Material.iron);
        setHardness(3.0F);
        setResistance(5.0F);
        setCreativeTab(ApocalypticCreativeTab.tabApocalyptic);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityPortalStarterHeaven();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i, float f, float g, float t) {
        TileEntity tile_entity = world.getBlockTileEntity(x, y, z);

        if (player.isSneaking()) {
            return false;
        }

        if (world.isRemote) {
            return true;
        }

        if (tile_entity != null) {
            TileEntityPortalStarterHeaven teportalheaven = (TileEntityPortalStarterHeaven) tile_entity;
            if (player.worldObj.provider.dimensionId == 0) {
                player.openGui(Apocalyptic.instance, teportalheaven.getType(), world, x, y, z);
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World world, int i, int j, int k, int par5, int par6) {
        TileEntityPortalStarterHeaven tilePortalHeaven = (TileEntityPortalStarterHeaven) world.getBlockTileEntity(i, j, k);
        if (tilePortalHeaven != null) {
            dropContent(0, tilePortalHeaven, world);
        }
        super.breakBlock(world, i, j, k, par5, par6);
    }

    public void dropContent(int newSize, TileEntityPortalStarterHeaven tilePortal2, World world) {
        for (int l = newSize; l < tilePortal2.getSizeInventory(); l++) {
            ItemStack itemstack = tilePortal2.getStackInSlot(l);
            if (itemstack == null) {
                continue;
            }
            float f = random.nextFloat() * 0.8F + 0.1F;
            float f1 = random.nextFloat() * 0.8F + 0.1F;
            float f2 = random.nextFloat() * 0.8F + 0.1F;
            while (itemstack.stackSize > 0) {
                int i1 = random.nextInt(21) + 10;
                if (i1 > itemstack.stackSize) {
                    i1 = itemstack.stackSize;
                }
                itemstack.stackSize -= i1;
                EntityItem entityitem = new EntityItem(world, (float) tilePortal2.xCoord + f, (float) tilePortal2.yCoord + (newSize > 0 ? 1 : 0) + f1, (float) tilePortal2.zCoord + f2, new ItemStack(itemstack.itemID, i1, itemstack.getItemDamage()));
                float f3 = 0.05F;
                entityitem.motionX = (float) random.nextGaussian() * f3;
                entityitem.motionY = (float) random.nextGaussian() * f3 + 0.2F;
                entityitem.motionZ = (float) random.nextGaussian() * f3;
                if (itemstack.hasTagCompound()) {
                    entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
                }
                world.spawnEntityInWorld(entityitem);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister par1) {
        this.topIcon = par1.registerIcon("apocalyptic:PortalStarterHeavenTop");
        this.sideIcon = par1.registerIcon("apocalyptic:PortalHeavenSide");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIcon(int side, int metadata) {
        if (side == 0 || side == 1) {
            return topIcon;
        } else {
            return sideIcon;
        }
    }
}