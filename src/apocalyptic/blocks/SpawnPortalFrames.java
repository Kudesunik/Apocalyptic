package apocalyptic.blocks;

import apocalyptic.ApocalypticCreativeTab;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class SpawnPortalFrames extends Block {

    @SideOnly(Side.CLIENT)
    private Icon[] icon;

    public SpawnPortalFrames(int var1) {
        super(var1, Material.rock);
        setHardness(5.0F);
        setResistance(10.0F);
        setStepSound(soundStoneFootstep);
        this.setCreativeTab(ApocalypticCreativeTab.tabApocalyptic);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister par1) {
        this.icon = new Icon[]{par1.registerIcon("apocalyptic:SpawnPortalCenterFrame"), par1.registerIcon("apocalyptic:SpawnPortalFrame")};
    }
    
    @Override
    public int damageDropped(int metadata) {
        return metadata;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List) {
        par3List.add(new ItemStack(par1, 1, 0));
        par3List.add(new ItemStack(par1, 1, 1));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIcon(int side, int metadata) {
        return this.icon[metadata];
    }
}
