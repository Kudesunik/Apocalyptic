package apocalyptic.blocks;

import apocalyptic.ApocalypticCreativeTab;
import apocalyptic.CommonProxy;
import apocalyptic.util.PrivateCleanerBridge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class SpawnPortal extends PortalLvl2 {

    public SpawnPortal(int par1) {
        super(par1);
        this.setCreativeTab(ApocalypticCreativeTab.tabApocalyptic);
        setBlockUnbreakable();
        setResistance(6000000.0F);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {}

    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return false;
    }
    
    @Override
    public boolean canCollideCheck(int par1, boolean par2) {
        return CommonProxy.invisibleBlocks;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {}

    @Override
    public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {
        if (par5Entity.readyToTeleport()) {
            if (par5Entity.ridingEntity == null && par5Entity.riddenByEntity == null) {
                if (par5Entity instanceof EntityPlayerMP) {
                    EntityPlayerMP thePlayer = (EntityPlayerMP) par5Entity;
                    if (par1World.provider.dimensionId == 0) {
                        PrivateCleanerBridge.getInstance().spawnTrigger(thePlayer.getEntityName());
                        par5Entity.resetTeleport();
                    }
                }
            }
        } else if (!par5Entity.getInPortal() && par5Entity.isInsideOfMaterial(Material.portal)) {
            par5Entity.setInPortal();
        }
    }
}
