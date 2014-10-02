package apocalyptic.blocks;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import apocalyptic.ApocalypticCreativeTab;
import apocalyptic.CommonProxy;
import apocalyptic.tile.TileEntityDetector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;

public class Detector extends BlockContainer {

    private Random random = new Random();
    private int meta;
    public int face;
    private Icon bottomIcon;
    private Icon topIcon;
    private Icon frontIconOffline;
    private Icon frontIconOnline;
    private Icon frontIconDetected;
    private Icon sideIcon;

    public Detector(int blockID) {
        super(blockID, Material.iron);
        setHardness(3.0F);
        setResistance(5.0F);
        this.setCreativeTab(ApocalypticCreativeTab.tabApocalyptic);
    }

    public boolean renderAsNormalBlock() {
        return false;
    }
    
    @Override
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLiving par5EntityLiving, ItemStack par6ItemStack) {
        meta = MathHelper.floor_double((double) (par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 2.5D) & 3;
        par1World.setBlockMetadataWithNotify(par2, par3, par4, meta, 3);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister par1IconRegister) {
        bottomIcon = par1IconRegister.registerIcon("apocalyptic:DetectorBottom");
        topIcon = par1IconRegister.registerIcon("apocalyptic:DetectorTop");
        frontIconOffline = par1IconRegister.registerIcon("apocalyptic:DetectorFrontOffline");
        frontIconOnline = par1IconRegister.registerIcon("apocalyptic:DetectorFrontOnline");
        frontIconDetected = par1IconRegister.registerIcon("apocalyptic:DetectorFrontDetected");
        sideIcon = par1IconRegister.registerIcon("apocalyptic:DetectorSide");
    }

    public Icon getIcon(int side, int metadata) {
        if (side == 0) {
            return bottomIcon;
        } else if (side == 1) {
            return topIcon;
        } else if ((side == 2 && metadata == 2) || (side == 5 && metadata == 3) || (side == 3 && metadata == 0) || (side == 4 && metadata == 1)) {
            return frontIconOffline;
        } else if ((side == 2 && metadata == 6) || (side == 5 && metadata == 7) || (side == 3 && metadata == 4) || (side == 4 && metadata == 5)) {
            return frontIconOnline;
        } else if ((side == 2 && metadata == 10) || (side == 5 && metadata == 11) || (side == 3 && metadata == 8) || (side == 4 && metadata == 9)) {
            return frontIconDetected;
        } else {
            return sideIcon;
        }
    }

    @Override
    public TileEntity createNewTileEntity(World var1) {
        return new TileEntityDetector();
    }

    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return isProvidingStrongPower(par1IBlockAccess, par2, par3, par4, par5);
    }

    public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        TileEntity tileentity = par1IBlockAccess.getBlockTileEntity(par2, par3, par4);
        TileEntityDetector detector = (TileEntityDetector) tileentity;
        return detector.detectorStatus ? (detector.blockMetadata >= 8 ? 1 : 0) : 0;
    }

    public boolean canProvidePower() {
        return false;
    }
}