package apocalyptic.blocks;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class MultiSpawnPortalFrames extends ItemBlock {
    private final static String[] subNames = new String[] {"center frame", "frame"};

    public MultiSpawnPortalFrames(int id) {
        super(id);
        setHasSubtypes(true);
        this.setUnlocalizedName("Spawn portal");
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
