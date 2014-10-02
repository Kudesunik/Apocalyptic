package apocalyptic.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import apocalyptic.Apocalyptic;
import apocalyptic.ApocalypticCreativeTab;
import apocalyptic.bukkit.BukkitInteraction;
import apocalyptic.bukkit.PluginInteraction;
import apocalyptic.teleporters.TeleportTo3Lvl;
import apocalyptic.util.PrivateCleanerBridge;
import java.util.List;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.bukkit.ChatColor;

public class PortalLvl3 extends BlockContainer {

    private int flag;
    private int timerChat;

    public PortalLvl3(int par1) {
        super(par1, Material.portal);
        this.flag = 0;
        this.timerChat = 0;
        this.setCreativeTab(ApocalypticCreativeTab.tabApocalyptic);
        setBlockUnbreakable();
        setResistance(6000000.0F);
    }
    
    @Override
    public TileEntity createNewTileEntity(World world){
            return new TileEntityEndPortal();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
        double d0 = (double) ((float) par2 + par5Random.nextFloat());
        double d1 = (double) ((float) par3 + 0.8F);
        double d2 = (double) ((float) par4 + par5Random.nextFloat());
        double d3 = 0.0D;
        double d4 = 0.0D;
        double d5 = 0.0D;
        par1World.spawnParticle("portalLvl3", d0, d1, d2, d3, d4, d5);
    }
    
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        float f = 0.0625F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return par5 != 0 ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
    }
    
    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }
    
    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    
    @Override
    public int quantityDropped(Random par1Random)
    {
        return 0;
    }
    
    @Override
    public int getRenderType()
    {
        return -1;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public int idPicked(World par1World, int par2, int par3, int par4)
    {
        return 0;
    }
    
    public void addCollisionBoxesToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity) {}

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

                        if (par1World.getBlockId(par2 - 1, par3, par4) == Apocalyptic.PortalLvl3.blockID || par1World.getBlockId(par2 + 1, par3, par4) == Apocalyptic.PortalLvl3.blockID) {
                            this.flag = 1;
                        } else if (par1World.getBlockId(par2, par3, par4 - 1) == Apocalyptic.PortalLvl3.blockID || par1World.getBlockId(par2, par3, par4 + 1) == Apocalyptic.PortalLvl3.blockID) {
                            this.flag = 2;
                        } else {
                            System.err.println("Error in portal size detection");
                        }

                        thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, 3, new TeleportTo3Lvl(thePlayer.mcServer.worldServerForDimension(3), thePlayer.mcServer, this.flag));
                        par5Entity.resetTeleport();
                        this.flag = 0;
                    } else if (par1World.provider.dimensionId == 3) {
                        thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, 0, new TeleportTo3Lvl(thePlayer.mcServer.worldServerForDimension(0), thePlayer.mcServer, 0));
                        par5Entity.resetTeleport();
                    }
                }
            }
        } else if (!par5Entity.getInPortal() && par5Entity.isInsideOfMaterial2(Material.portal) && par5Entity.timeAfterPortal == 0) {
            par5Entity.setInPortal();
        }
    }
}