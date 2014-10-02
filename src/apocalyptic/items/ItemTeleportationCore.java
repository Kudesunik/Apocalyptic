package apocalyptic.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import apocalyptic.ApocalypticCreativeTab;
import net.minecraft.util.Icon;

public class ItemTeleportationCore extends Item {

    private Icon FullIcon;

    public ItemTeleportationCore(int par1) {
        super(par1);
        this.setCreativeTab(ApocalypticCreativeTab.tabApocalyptic);
        this.setMaxDamage(1);
        this.setMaxStackSize(1);
    }

    @Override
    public void registerIcons(IconRegister par1) {
        this.FullIcon = par1.registerIcon("apocalyptic:ItemTeleportationCore");
    }

    @Override
    public Icon getIconFromDamage(int par1) {
        return this.FullIcon;
    }
}
