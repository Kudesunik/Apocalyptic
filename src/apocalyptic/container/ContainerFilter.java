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

public class ContainerFilter extends Container{
    protected TileEntityFilter tile_entity;

    private boolean initialized;

    private EntityPlayer myPlayer;

    public ContainerFilter(TileEntityFilter tile_entity, InventoryPlayer player_inventory){
            this.tile_entity = tile_entity;

            this.myPlayer = ((InventoryPlayer) player_inventory).player;

            addSlotToContainer(new Slot(tile_entity, 1, 26, 17));

            addSlotToContainer(new Slot(tile_entity, 2, 26, 53));

            for (int st1 = 0; st1 < 4; st1++) {
            	for (int st2 = 0; st2 < 2; st2++) {
            		addSlotToContainer(new Slot(tile_entity, st1 + st2 * 4 + 3, 97 + st1 * 18, 10 + st2 * 18));
            	}
            }

            for (int st1 = 0; st1 < 4; st1++) {
            		addSlotToContainer(new Slot(tile_entity, st1 + 11, 97 + st1 * 18, 21 + 2 * 16));
            }

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