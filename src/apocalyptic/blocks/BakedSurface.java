package apocalyptic.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import apocalyptic.ApocalypticCreativeTab;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.Icon;

public class BakedSurface extends Block
{
	public BakedSurface(int par1) {
        super(par1, Material.rock);
        setCreativeTab(ApocalypticCreativeTab.tabApocalyptic);
        setHardness(1.0F);
        setResistance(10.0F);
	}

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister par1) {
	    this.blockIcon = par1.registerIcon("apocalyptic:BakedSurface");
    }
    
    @Override
    public Icon getIcon(int side, int metadata) {
        return blockIcon;
    }
}
