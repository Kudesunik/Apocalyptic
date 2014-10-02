package apocalyptic.items;

import apocalyptic.Apocalyptic;
import apocalyptic.ApocalypticCreativeTab;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemStimulator extends Item {
    
    public ItemStimulator(int par1) {
        super(par1);
        this.setCreativeTab(ApocalypticCreativeTab.tabApocalyptic);
        this.setMaxStackSize(8);
    }
    
    @Override
    public void registerIcons(IconRegister par1) {
        this.itemIcon = par1.registerIcon("apocalyptic:ItemStimulator");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        par3EntityPlayer.addPotionEffect(new PotionEffect(Apocalyptic.regenFeel.id, 400, 0));
        return par1ItemStack;
    }
}
