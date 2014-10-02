package apocalyptic.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import apocalyptic.ApocalypticCreativeTab;

public class ItemFilter extends Item {

    public ItemFilter(int par1) {
        super(par1);
        this.setCreativeTab(ApocalypticCreativeTab.tabApocalyptic);
        this.setMaxDamage(900);
        this.setMaxStackSize(1);
    }

    @Override
    public void registerIcons(IconRegister par1) {
        this.itemIcon = par1.registerIcon("apocalyptic:ItemFilter");
    }
}
