package apocalyptic.blocks;

import apocalyptic.Apocalyptic;
import apocalyptic.ApocalypticCreativeTab;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.Block;
import static net.minecraft.block.Block.soundStoneFootstep;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;

public class HardSurface extends Block {

    public HardSurface(int par1) {
        super(par1, Material.rock);
        setCreativeTab(ApocalypticCreativeTab.tabApocalyptic);
        setHardness(50.0F);
        setResistance(2000.0F);
        setStepSound(soundStoneFootstep);
    }
    
    public int quantityDropped(Random par1Random)
    {
        return 1;
    }
    
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return Apocalyptic.HardSurface.blockID;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister par1) {
        this.blockIcon = par1.registerIcon("apocalyptic:HardSurface");
    }
    
    @Override
    public Icon getIcon(int side, int metadata) {
        return blockIcon;
    }
}
