package apocalyptic.container;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import apocalyptic.tile.TileEntityFilter;
import apocalyptic.tile.TileEntityPortalStarterLvl2;

public class ContainerPortalLvl2 extends Container{
    protected TileEntityPortalStarterLvl2 tile_entity;

    private boolean initialized;

    private EntityPlayer myPlayer;

    public ContainerPortalLvl2(TileEntityPortalStarterLvl2 tile_entity, InventoryPlayer player_inventory){
            this.tile_entity = tile_entity;

            this.myPlayer = ((InventoryPlayer) player_inventory).player;

            addSlotToContainer(new Slot(tile_entity, 1, 152, 36));

            bindPlayerInventory(player_inventory);
}

    @Override
    public boolean canInteractWith(EntityPlayer player){
            return tile_entity.isUseableByPlayer(player);
    }

    protected void bindPlayerInventory(InventoryPlayer player_inventory){
    	for (int i = 0; i < 3; i++)
        {
            for (int k = 0; k < 9; k++)
            {
                addSlotToContainer(new Slot(player_inventory, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }
        }

        for (int j = 0; j < 9; j++)
        {
            addSlotToContainer(new Slot(player_inventory, j, 8 + j * 18, 142));
        }
    }

	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		List<ICrafting> crafters = this.crafters;
	}

    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int i) {
		ItemStack itemstack = null;
		Slot slot = (Slot) inventorySlots.get(i);
		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (i == 0)
			{
				if (!mergeItemStack(itemstack1, 1, 37, true))
				{
					return null;
				}
			}
			else if (i >= 1 && i < 28)
			{
				if (!mergeItemStack(itemstack1, 28, 37, false))
				{
					return null;
				}
			}
			else if (i >= 28 && i < 37)
			{
				if (!mergeItemStack(itemstack1, 1, 27, false))
				{
					return null;
				}
			}
			else if (!mergeItemStack(itemstack1, 1, 37, false))
			{
				return null;
			}
			if (itemstack1.stackSize == 0)
			{
				slot.putStack(null);
			}
			else
			{
				slot.onSlotChanged();
			}
			if (itemstack1.stackSize != itemstack.stackSize)
			{
				slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
			}
			else
			{
				return null;
			}
		}

		return itemstack;
	}

	public EntityPlayer getPlayer() {
		return myPlayer;
	}
}
