package apocalyptic.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import apocalyptic.ApocalypticCreativeTab;
import apocalyptic.bukkit.BukkitInteraction;
import apocalyptic.bukkit.PluginInteraction;
import apocalyptic.teleporters.TeleportToHeaven;
import apocalyptic.util.PrivateCleanerBridge;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.bukkit.ChatColor;

public class PortalHeaven extends BlockBreakable {
    
    private int timerChat;

    public PortalHeaven(int par1) {
        super(par1, "portal", Material.portal, false);
        this.timerChat = 0;
        this.setCreativeTab(ApocalypticCreativeTab.tabApocalyptic);
        setBlockUnbreakable();
        setResistance(6000000.0F);
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return false;
    }

    @Override
    public boolean isBlockSolid(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return false;
    }

    @Override
    public int idDropped(int par1, Random par2Random, int par3) {
        return 0;
    }

    @Override
    public int quantityDropped(Random par1Random) {
        return 0;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
        return null;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister par1) {
        this.blockIcon = par1.registerIcon("apocalyptic:FilteredAir");
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
                        thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, 4, new TeleportToHeaven(thePlayer.mcServer.worldServerForDimension(4), thePlayer.mcServer, 0));
                        PrivateCleanerBridge.getInstance().addIslandRights(thePlayer.getEntityName());
                        par5Entity.resetTeleport();
                    } else if (par1World.provider.dimensionId == 4) {
                        thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, 0, new TeleportToHeaven(thePlayer.mcServer.worldServerForDimension(0), thePlayer.mcServer, 0));
                        par5Entity.resetTeleport();
                    }
                }
            }
        } else if (!par5Entity.getInPortal() && par5Entity.isInsideOfMaterial(Material.portal)) {
            par5Entity.setInPortal();
        }
    }
}