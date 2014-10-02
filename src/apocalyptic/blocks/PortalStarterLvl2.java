package apocalyptic.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import apocalyptic.Apocalyptic;
import apocalyptic.ApocalypticCreativeTab;
import apocalyptic.tile.TileEntityPortalStarterLvl2;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class PortalStarterLvl2 extends BlockContainer {

    private Icon Icon;
    private Random random = new Random();

    public PortalStarterLvl2(int par1) {
        super(par1, Material.iron);
        setHardness(3.0F);
        setResistance(5.0F);
        setCreativeTab(ApocalypticCreativeTab.tabApocalyptic);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister par1) {
        this.blockIcon = par1.registerIcon("apocalyptic:PortalStarterLvl2");
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityPortalStarterLvl2();
    }

    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving player) {
        int var7 = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 2.5D) & 3;
        world.setBlockMetadataWithNotify(x, y, z, var7, 3);
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
            TileEntityPortalStarterLvl2 teportalst2 = (TileEntityPortalStarterLvl2) tile_entity;
            player.openGui(Apocalyptic.instance, teportalst2.getType(), world, x, y, z);
        }

        return true;
    }

    @Override
    public void breakBlock(World world, int i, int j, int k, int par5, int par6) {
        TileEntityPortalStarterLvl2 tilePortal2 = (TileEntityPortalStarterLvl2) world.getBlockTileEntity(i, j, k);
        if (tilePortal2 != null) {
            dropContent(0, tilePortal2, world);
        }
        super.breakBlock(world, i, j, k, par5, par6);
    }

    public void dropContent(int newSize, TileEntityPortalStarterLvl2 tilePortal2, World world) {
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
}
