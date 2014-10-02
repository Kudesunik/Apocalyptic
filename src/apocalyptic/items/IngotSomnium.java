package apocalyptic.items;

import apocalyptic.ApocalypticCreativeTab;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;

public class IngotSomnium extends Item {
    
    	public IngotSomnium (int par1) {
		super(par1);
                this.setCreativeTab(ApocalypticCreativeTab.tabApocalyptic);
	}
        
        @Override
	public void registerIcons(IconRegister par1) {
	    this.itemIcon = par1.registerIcon("apocalyptic:IngotSomnium");
        }
        
        @Override
        public Icon getIconFromDamage(int par1) {
            return this.itemIcon;
        }
}
