package apocalyptic.blocks;

import java.util.Random;

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
import apocalyptic.Apocalyptic;
import apocalyptic.ApocalypticCreativeTab;
import apocalyptic.tile.TileEntityFilter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Filter extends BlockContainer {

    private Random random = new Random();
    private Icon bottomIcon;
    private Icon topIcon;
    private Icon frontIcon;
    private Icon sideIcon;

    public Filter(int blockID) {
        super(blockID, Material.iron);
        setHardness(3.0F);
        setResistance(5.0F);
        this.setCreativeTab(ApocalypticCreativeTab.tabApocalyptic);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister par1IconRegister) {
        bottomIcon = par1IconRegister.registerIcon("apocalyptic:FilterBottom");
        topIcon = par1IconRegister.registerIcon("apocalyptic:FilterTop");
        frontIcon = par1IconRegister.registerIcon("apocalyptic:FilterFront");
        sideIcon = par1IconRegister.registerIcon("apocalyptic:FilterSide");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIcon(int side, int metadata) {
        if (side == 0) {
            return bottomIcon;
        } else if (side == 1) {
            return topIcon;
        } else if ((side == 2 && metadata == 2) || (side == 5 && metadata == 3) || (side == 3 && metadata == 0) || (side == 4 && metadata == 1)) {
            return frontIcon;
        } else {
            return sideIcon;
        }
    }

    @Override
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLiving par5EntityLiving, ItemStack par6ItemStack) {
        int var7 = MathHelper.floor_double((double) (par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 2.5D) & 3;
        par1World.setBlockMetadataWithNotify(par2, par3, par4, var7, 3);
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
            TileEntityFilter tefilter = (TileEntityFilter) tile_entity;
            player.openGui(Apocalyptic.instance, tefilter.getType(), world, x, y, z);
        }

        return true;
    }

    @Override
    public void breakBlock(World world, int i, int j, int k, int par5, int par6) {
        TileEntityFilter tileFilter = (TileEntityFilter) world.getBlockTileEntity(i, j, k);
        if (tileFilter != null) {
            dropContent(0, tileFilter, world);
        }
        super.breakBlock(world, i, j, k, par5, par6);
    }

    public void dropContent(int newSize, TileEntityFilter tileSolar, World world) {
        for (int l = newSize; l < tileSolar.getSizeInventory(); l++) {
            ItemStack itemstack = tileSolar.getStackInSlot(l);
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
                EntityItem entityitem = new EntityItem(world, (float) tileSolar.xCoord + f, (float) tileSolar.yCoord + (newSize > 0 ? 1 : 0) + f1, (float) tileSolar.zCoord + f2, new ItemStack(itemstack.itemID, i1, itemstack.getItemDamage()));
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

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityFilter();
    }
}
