package apocalyptic.blocks;

import apocalyptic.Apocalyptic;
import apocalyptic.ApocalypticCreativeTab;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class PortalFrameLvl3 extends Block {
	public PortalFrameLvl3(int par1) {
        super(par1, Material.rock);
        setHardness(3.0F);
        setResistance(5.0F);
        setCreativeTab(ApocalypticCreativeTab.tabApocalyptic);
	}
        
        @Override
        public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5) {
		if (par1World.provider.dimensionId != 0) {
			destroyPortal(par1World, par2, par3, par4);
		}
	}
	
        @Override
	public void onBlockDestroyedByExplosion(World par1World, int par2, int par3, int par4, Explosion par5Explosion) {
		if (par1World.provider.dimensionId != 0) {
			destroyPortal(par1World, par2, par3, par4);
		}
	}
	
	private void destroyPortal(World par1World, int par2, int par3, int par4) {
		for (int xC = -4; xC <= 4; xC++) {
			for (int yC = -4; yC <= 4; yC++) {
				for (int zC = -4; zC <= 4; zC++) {
					if (par1World.getBlockId(par2 + xC, par3 + yC, par4 + zC) == this.blockID || par1World.getBlockId(par2 + xC, par3 + yC, par4 + zC) == Apocalyptic.PortalLvl3.blockID) {
						par1World.setBlockToAir(par2 + xC, par3 + yC, par4 + zC);
						par1World.createExplosion((Entity)null, par2 + xC, par3 + yC, par4 + zC, 0.0F, true);
					}
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister par1) {
		    this.blockIcon = par1.registerIcon("apocalyptic:PortalFrameLvl3");
	    }
}
