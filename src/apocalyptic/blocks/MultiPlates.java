package apocalyptic.blocks;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class MultiPlates extends ItemBlock {

    private final static String[] subNames = new String[] {"Black", "Cornflower", "Floor", "White", "Warning"};

    public MultiPlates(int id) {
        super(id);
        setHasSubtypes(true);
        this.setUnlocalizedName("Plate");
    }

    @Override
    public int getMetadata(int damageValue) {
        return damageValue;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        return getUnlocalizedName() + "." + subNames[itemstack.getItemDamage()];
    }
}
