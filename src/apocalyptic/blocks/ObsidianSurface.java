package apocalyptic.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockObsidian;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.Icon;

public class ObsidianSurface extends BlockObsidian {
    
    public ObsidianSurface (int par1) {
        super(par1);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister par1) {
	    this.blockIcon = par1.registerIcon("apocalyptic:ObsidianSurface");
    }
    
    @Override
    public Icon getIcon(int side, int metadata) {
        return blockIcon;
    }
}
