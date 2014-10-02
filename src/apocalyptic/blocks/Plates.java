package apocalyptic.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockOreStorage;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class Plates extends BlockOreStorage {
    
    @SideOnly(Side.CLIENT)
    private Icon[] icon;
    
    public Plates (int var1) {
        super(var1);
        setHardness(5.0F);
        setResistance(10.0F);
        setStepSound(soundMetalFootstep);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister par1) {
        this.icon = new Icon[]{par1.registerIcon("apocalyptic:BlackPlate"), par1.registerIcon("apocalyptic:CornflowerPlate"), par1.registerIcon("apocalyptic:FloorPlate"), par1.registerIcon("apocalyptic:WhitePlate"), par1.registerIcon("apocalyptic:WarningPlate")};
    }
    
    @Override
    public int damageDropped(int metadata) {
        return metadata;
    }
    
    @Override
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return this.blockID;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(par1, 1, 0));
        par3List.add(new ItemStack(par1, 1, 1));
        par3List.add(new ItemStack(par1, 1, 2));
        par3List.add(new ItemStack(par1, 1, 3));
        par3List.add(new ItemStack(par1, 1, 4));
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIcon(int side, int metadata) {
        return this.icon[metadata];
    }
}
