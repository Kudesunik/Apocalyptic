package apocalyptic.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import apocalyptic.ApocalypticCreativeTab;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;

public class PortalFrameHeaven extends Block {
	
	private Icon topIcon;
	private Icon sideIcon;

	public PortalFrameHeaven(int par1) {
            super(par1, Material.rock);
            setCreativeTab(ApocalypticCreativeTab.tabApocalyptic);
            setHardness(3.0F);
            setResistance(5.0F);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister par1) {
	    this.topIcon = par1.registerIcon("apocalyptic:PortalFrameHeavenTop");
	    this.sideIcon = par1.registerIcon("apocalyptic:PortalHeavenSide");
        }
	
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIcon(int side, int metadata)
	{
	   if(side == 0 || side == 1)
	          return topIcon;
	   else
	          return sideIcon;
	}
}
