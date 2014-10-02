package apocalyptic.blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import apocalyptic.Apocalyptic;
import apocalyptic.ApocalypticCreativeTab;
import apocalyptic.bukkit.BukkitInteraction;
import apocalyptic.bukkit.PluginInteraction;
import apocalyptic.teleporters.TeleportTo2Lvl;
import apocalyptic.util.PrivateCleanerBridge;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.bukkit.ChatColor;

public class PortalLvl2 extends BlockBreakable {

    private int flag;
    private int timerChat;

    public PortalLvl2(int par1) {
        super(par1, "portal", Material.portal, false);
        this.flag = 0;
        this.timerChat = 0;
        this.setCreativeTab(ApocalypticCreativeTab.tabApocalyptic);
        setBlockUnbreakable();
        setResistance(6000000.0F);
    }

    @SideOnly(Side.CLIENT)
    public int idPicked(World par1World, int par2, int par3, int par4) {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass() {
        return 1;
    }

    public int quantityDropped(Random par1Random) {
        return 0;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
        return null;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
        float f;
        float f1;

        if (par1IBlockAccess.getBlockId(par2 - 1, par3, par4) != this.blockID && par1IBlockAccess.getBlockId(par2 + 1, par3, par4) != this.blockID) {
            f = 0.125F;
            f1 = 0.5F;
            this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f1, 0.5F + f, 1.0F, 0.5F + f1);
        } else {
            f = 0.5F;
            f1 = 0.125F;
            this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f1, 0.5F + f, 1.0F, 0.5F + f1);
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        if (par1IBlockAccess.getBlockId(par2, par3, par4) == this.blockID) {
            return false;
        } else {
            boolean flag = par1IBlockAccess.getBlockId(par2 - 1, par3, par4) == this.blockID && par1IBlockAccess.getBlockId(par2 - 2, par3, par4) != this.blockID;
            boolean flag1 = par1IBlockAccess.getBlockId(par2 + 1, par3, par4) == this.blockID && par1IBlockAccess.getBlockId(par2 + 2, par3, par4) != this.blockID;
            boolean flag2 = par1IBlockAccess.getBlockId(par2, par3, par4 - 1) == this.blockID && par1IBlockAccess.getBlockId(par2, par3, par4 - 2) != this.blockID;
            boolean flag3 = par1IBlockAccess.getBlockId(par2, par3, par4 + 1) == this.blockID && par1IBlockAccess.getBlockId(par2, par3, par4 + 2) != this.blockID;
            boolean flag4 = flag || flag1;
            boolean flag5 = flag2 || flag3;
            return flag4 && par5 == 4 ? true : (flag4 && par5 == 5 ? true : (flag5 && par5 == 2 ? true : flag5 && par5 == 3));
        }
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
        if (par5Random.nextInt(100) == 0) {
            par1World.playSound((double) par2 + 0.5D, (double) par3 + 0.5D, (double) par4 + 0.5D, "portal.portal", 0.5F, par5Random.nextFloat() * 0.4F + 0.8F, false);
        }

        for (int l = 0; l < 4; ++l) {
            double d0 = (double) ((float) par2 + par5Random.nextFloat());
            double d1 = (double) ((float) par3 + par5Random.nextFloat());
            double d2 = (double) ((float) par4 + par5Random.nextFloat());
            double d3 = 0.0D;
            double d4 = 0.0D;
            double d5 = 0.0D;
            int i1 = par5Random.nextInt(2) * 2 - 1;
            d3 = ((double) par5Random.nextFloat() - 0.5D) * 0.5D;
            d4 = ((double) par5Random.nextFloat() - 0.5D) * 0.5D;
            d5 = ((double) par5Random.nextFloat() - 0.5D) * 0.5D;

            if (par1World.getBlockId(par2 - 1, par3, par4) != this.blockID && par1World.getBlockId(par2 + 1, par3, par4) != this.blockID) {
                d0 = (double) par2 + 0.5D + 0.25D * (double) i1;
                d3 = (double) (par5Random.nextFloat() * 2.0F * (float) i1);
            } else {
                d2 = (double) par4 + 0.5D + 0.25D * (double) i1;
                d5 = (double) (par5Random.nextFloat() * 2.0F * (float) i1);
            }

            par1World.spawnParticle("portal", d0, d1, d2, d3, d4, d5);
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {
        if (!par1World.isRemote && !PluginInteraction.checkRights(par5Entity.getEntityName()) && par5Entity.ridingEntity == null && par5Entity.riddenByEntity == null && par5Entity instanceof EntityPlayerMP) {
            par5Entity.resetTeleport();
            if (timerChat == 0) {
                BukkitInteraction.sendToPlayerChat(par5Entity.getEntityName(), "Доступ к порталу отсутствует", ChatColor.RED);
                timerChat= 100;
            } else {
                timerChat--;
            }
        }
        if (par5Entity.readyToTeleport()) {
            if (par5Entity.ridingEntity == null && par5Entity.riddenByEntity == null) {
                if (par5Entity instanceof EntityPlayerMP) {
                    EntityPlayerMP thePlayer = (EntityPlayerMP) par5Entity;
                    if (par1World.provider.dimensionId == 0) {
                        if (par1World.getBlockId(par2 - 1, par3, par4) == Apocalyptic.PortalLvl2.blockID || par1World.getBlockId(par2 + 1, par3, par4) == Apocalyptic.PortalLvl2.blockID) {
                            this.flag = 1;
                        } else if (par1World.getBlockId(par2, par3, par4 - 1) == Apocalyptic.PortalLvl2.blockID || par1World.getBlockId(par2, par3, par4 + 1) == Apocalyptic.PortalLvl2.blockID) {
                            this.flag = 2;
                        } else {
                            System.err.println("Error in portal size detection");
                        }

                        thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, 2, new TeleportTo2Lvl(thePlayer.mcServer.worldServerForDimension(2), thePlayer.mcServer, this.flag));
                        par5Entity.resetTeleport();
                        this.flag = 0;
                    } else if (par1World.provider.dimensionId == 2) {
                        thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, 0, new TeleportTo2Lvl(thePlayer.mcServer.worldServerForDimension(0), thePlayer.mcServer, 0));
                        par5Entity.resetTeleport();
                    }
                }
            }
        } else if (!par5Entity.getInPortal() && par5Entity.isInsideOfMaterial(Material.portal) && par5Entity.timeAfterPortal == 0) {
            par5Entity.setInPortal();
        }
    }
}
