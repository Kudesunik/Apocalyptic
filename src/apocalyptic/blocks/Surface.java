package apocalyptic.blocks;

import apocalyptic.Apocalyptic;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import apocalyptic.ApocalypticCreativeTab;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;

public class Surface extends Block {

    public Surface(int par1) {
        super(par1, Material.rock);
        setCreativeTab(ApocalypticCreativeTab.tabApocalyptic);
        setHardness(1.0F);
        setResistance(10.0F);
    }
    
    public int quantityDropped(Random par1Random)
    {
        return 1;
    }
    
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return Apocalyptic.Surface.blockID;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister par1) {
        this.blockIcon = par1.registerIcon("apocalyptic:Surface");
    }
}