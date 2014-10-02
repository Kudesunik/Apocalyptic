package apocalyptic.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;

public class SonmiumOre extends Block {
    
    public SonmiumOre (int par1) {
        super(par1, Material.iron);
        setHardness(3.0F);
        setResistance(5.0F);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister par1) {
	    this.blockIcon = par1.registerIcon("apocalyptic:SomniumOre");
    }
}