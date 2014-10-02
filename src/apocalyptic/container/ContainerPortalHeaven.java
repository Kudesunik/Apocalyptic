package apocalyptic.container;

import apocalyptic.tile.TileEntityPortalStarterHeaven;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerPortalHeaven extends Container {

    protected TileEntityPortalStarterHeaven tile_entity;
    private boolean initialized;
    private EntityPlayer myPlayer;

    public ContainerPortalHeaven(TileEntityPortalStarterHeaven tile_entity, InventoryPlayer player_inventory) {
        this.tile_entity = tile_entity;

        this.myPlayer = ((InventoryPlayer) player_inventory).player;

        addSlotToContainer(new Slot(tile_entity, 1, 152, 8));
        //Set up coordinates and value (Inst)
        addSlotToContainer(new Slot(tile_entity, 2, 152, 52));

        bindPlayerInventory(player_inventory);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tile_entity.isUseableByPlayer(player);
    }

    protected void bindPlayerInventory(InventoryPlayer player_inventory) {
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlotToContainer(new Slot(player_inventory, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }
        }

        for (int j = 0; j < 9; j++) {
            addSlotToContainer(new Slot(player_inventory, j, 8 + j * 18, 142));
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        List<ICrafting> crafters = this.crafters;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer p, int slotId) {
        Slot slot = (Slot) this.inventorySlots.get(slotId);
        if (slot != null) {
            ItemStack items = slot.getStack();
            if (items != null) {
                int initialCount = items.stackSize;
                if (slotId < tile_entity.getSizeInventory())
                {
                    mergeItemStack(items, tile_entity.getSizeInventory(), inventorySlots.size(), false);
                    if (items.stackSize == 0) {
                        slot.putStack((ItemStack) null);
                    } else {
                        slot.onSlotChanged();
                        if (initialCount != items.stackSize) {
                            return items;
                        }
                    }
                } else
                {
                    for (int i = 0; i < tile_entity.getSizeInventory(); i++) {
                        ItemStack targetStack = tile_entity.getStackInSlot(i);
                        if (targetStack == null) {
                            Slot targetSlot = (Slot) this.inventorySlots.get(i);
                            targetSlot.putStack(items);
                            slot.putStack((ItemStack) null);
                            break;
                        } else if (items.isStackable() && items.isItemEqual(targetStack)) {
                            mergeItemStack(items, i, i + 1, false);
                            if (items.stackSize == 0) {
                                slot.putStack((ItemStack) null);
                            } else {
                                slot.onSlotChanged();
                                if (initialCount != items.stackSize) {
                                    return items;
                                }
                            }
                            break;
                        }

                    }
                }
            }
        }
        return null;
    }

    public EntityPlayer getPlayer() {
        return myPlayer;
    }
}
