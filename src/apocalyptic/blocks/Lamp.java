package apocalyptic.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.BlockGlowStone;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;

public class Lamp extends BlockGlowStone {

    public Lamp(int par1, Material par2) {
        super(par1, par2);
        setHardness(0.3F);
        setStepSound(soundGlassFootstep);
        setLightValue(1.0F);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister par1) {
        this.blockIcon = par1.registerIcon("apocalyptic:Lamp");
    }
    
    @Override
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return this.blockID;
    }
    
    /**
     * Returns the usual quantity dropped by the block plus a bonus of 1 to 'i' (inclusive).
     */
    @Override
    public int quantityDroppedWithBonus(int par1, Random par2Random)
    {
        return 1;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    @Override
    public int quantityDropped(Random par1Random)
    {
        return 1;
    }
}
