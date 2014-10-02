package apocalyptic;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ApocalypticCreativeTab extends CreativeTabs {
public static final CreativeTabs tabApocalyptic = new ApocalypticCreativeTab("Apocalyptic");

	public ApocalypticCreativeTab(String label) {
		super(label);
	}

	@Override
    public ItemStack getIconItemStack() {
		return new ItemStack(Apocalyptic.Surface);
	}

	@Override
    public String getTranslatedTabLabel() {
		return "Apocalyptic";
	}
}
