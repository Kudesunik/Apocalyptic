package apocalyptic.tile;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.Items;
import ic2.api.network.INetworkDataProvider;
import ic2.api.network.INetworkUpdateListener;
import ic2.api.network.NetworkHelper;
import ic2.api.tile.IEnergyStorage;
import ic2.api.tile.IWrenchable;

import java.util.List;
import java.util.Random;
import java.util.Vector;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import apocalyptic.Apocalyptic;
import apocalyptic.RedstoneHelper;
import apocalyptic.api.IRedstonePowered;

public class TileEntityPortalStarterLvl2 extends TileEntity implements IInventory, IEnergySink, IEnergyStorage, IWrenchable, INetworkDataProvider, INetworkUpdateListener, IRedstonePowered {

    public static Random rand = new Random();
    public int type;
    public ItemStack[] inventory;
    public boolean initialized;
    public int tick;
    public boolean isActivated;
    public double electricityStored;
    public double prevElectricityStored;
    public boolean powered;
    public int dischw;
    public int[] frameMatrix = {2, 3, 4, 6, 10, 15, 16, 20, 22, 23, 24};
    public int[] emptyMatrix = {7, 8, 9, 12, 13, 14, 17, 18, 19};

    public TileEntityPortalStarterLvl2() {
        this.inventory = new ItemStack[50];
        this.powered = false;
        this.isActivated = false;
    }

    @Override
    public int getSizeInventory() {
        return this.inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slotIndex) {
        return this.inventory[slotIndex];
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.inventory[slot] = stack;

        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }
        onInventoryChanged();
    }

    @Override
    public ItemStack decrStackSize(int slotIndex, int amount) {
        if (inventory[slotIndex] != null) {
            if (inventory[slotIndex].stackSize <= amount) {
                ItemStack itemstack = inventory[slotIndex];
                inventory[slotIndex] = null;
                onInventoryChanged();
                return itemstack;
            }
            ItemStack itemstack1 = inventory[slotIndex].splitStack(amount);
            if (inventory[slotIndex].stackSize == 0) {
                inventory[slotIndex] = null;
            }
            onInventoryChanged();
            return itemstack1;
        } else {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slotIndex) {
        ItemStack stack = getStackInSlot(slotIndex);

        if (stack != null) {
            setInventorySlotContents(slotIndex, null);
        }

        return stack;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        if (worldObj == null) {
            return true;
        }
        if (worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this) {
            return false;
        }
        return player.getDistanceSq((double) xCoord + 0.5D, (double) yCoord + 0.5D, (double) zCoord + 0.5D) <= 64D;
    }

    @Override
    public void openChest() {
    }

    @Override
    public void closeChest() {
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        electricityStored = tagCompound.getDouble("electricityStored");
        dischw = tagCompound.getInteger("dischw");
        isActivated = tagCompound.getBoolean("isActivated");
        NBTTagList tagList = tagCompound.getTagList("Inventory");

        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);

            byte slot = tag.getByte("Slot");

            if (slot >= 0 && slot < inventory.length) {
                inventory[slot] = ItemStack.loadItemStackFromNBT(tag);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        NBTTagList itemList = new NBTTagList();

        for (int i = 0; i < inventory.length; i++) {
            ItemStack stack = inventory[i];

            if (stack != null) {
                NBTTagCompound tag = new NBTTagCompound();

                tag.setByte("Slot", (byte) i);
                stack.writeToNBT(tag);
                itemList.appendTag(tag);
            }
        }

        tagCompound.setTag("Inventory", itemList);
        tagCompound.setDouble("electricityStored", electricityStored);
        tagCompound.setInteger("dischw", dischw);
        tagCompound.setBoolean("isActivated", isActivated);
    }

    @Override
    public void updateEntity() {
        if (!initialized && worldObj != null) {
            if (!worldObj.isRemote) {
                MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
            }
            this.onInventoryChanged();
            this.initialized = true;
        }
        onUpdate();
        super.updateEntity();
    }

    public void onUpdate() {
        if (!this.worldObj.isRemote && inventory[1] != null) {
            if (getStored() < getCapacity()) {
                int discharge = ElectricItem.manager.discharge(inventory[1], 36, 3, false, false);
                addEnergy(discharge);
            }
        }

        if (tick == 20) {
            if (checkFrame() != 0) {
                if (isActivated && getStored() >= 80) {
                    remEnergy(80);
                } else if (isActivated && getStored() < 80) {
                    remEnergy(getStored());
                    remPortal();
                    isActivated = false;
                    NetworkHelper.updateTileEntityField(this, "isActivated");
                } else if (getStored() >= getCapacity() && !isActivated && checkFrame() != 0 && (yCoord + 4) < 200) {
                    setPortal(checkFrame());
                }
            } else if (isActivated) {
                remEnergy(getStored());
                remPortal();
                isActivated = false;
                NetworkHelper.updateTileEntityField(this, "isActivated");
            } else {
                remPortal();
                isActivated = false;
                NetworkHelper.updateTileEntityField(this, "isActivated");
            }
            tick = 0;
        } else if (tick != 20) {
            tick++;
        }
    }

    @Override
    public String getInvName() {
        return "TileEntityPortalStarterLvl2";
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
        return true;
    }

    @Override
    public boolean isAddedToEnergyNet() {
        return initialized;
    }

    @Override
    public void onNetworkUpdate(String field) {
        if (field.equals("prevElectricityStored") && prevElectricityStored != electricityStored) {
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
            prevElectricityStored = electricityStored;
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        Vector<String> vector = new Vector<String>(3);
        vector.add("electricityStored");
        vector.add("dischw");
        vector.add("isActivated");
        return vector;
    }

    @Override
    public void validate() {
        super.validate();
    }

    @Override
    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
        return false;
    }

    @Override
    public short getFacing() {
        return 0;
    }

    @Override
    public void setFacing(short facing) {
    }

    @Override
    public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
        return true;
    }

    @Override
    public float getWrenchDropRate() {
        return 1.0F;
    }

    @Override
    public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
        return new ItemStack(Apocalyptic.PortalStarterLvl2, 1, getType());
    }

    @Override
    public int demandsEnergy() {
        return getCapacity() - getStored();
    }

    @Override
    public int injectEnergy(Direction directionFrom, int amount) {
        if (amount > getMaxSafeInput()) {
            worldObj.setBlock(xCoord, yCoord, zCoord, 0);
            worldObj.createExplosion(null, xCoord, yCoord, zCoord, 0.8F, false);
            return 0;
        }
        int rejects = 0;
        int need = demandsEnergy();
        if (need >= amount) {
            electricityStored += amount;
        } else if (need < amount) {
            electricityStored += need;
            rejects = amount - need;
        }
        NetworkHelper.updateTileEntityField(this, "electricityStored");
        return rejects;
    }

    @Override
    public void invalidate() {
        if (this.initialized) {
            remPortal();
            if (this.isActivated) {
                this.isActivated = false;
                NetworkHelper.updateTileEntityField(this, "isActivated");
            }
            if (!this.worldObj.isRemote) {
                MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
            }
            initialized = false;
        }
        super.invalidate();
    }

    @Override
    public int getMaxSafeInput() {
        return 512;
    }

    @Override
    public int getStored() {
        return (int) electricityStored;
    }

    @Override
    public void setStored(int energy) {
        electricityStored = energy;
    }

    public int remEnergy(int amount) {
        electricityStored -= amount;
        NetworkHelper.updateTileEntityField(this, "electricityStored");
        return (int) electricityStored;
    }

    @Override
    public int addEnergy(int amount) {
        electricityStored += amount;
        NetworkHelper.updateTileEntityField(this, "electricityStored");
        return (int) electricityStored;
    }

    @Override
    public int getCapacity() {
        return 40000000;
    }

    @Override
    public int getOutput() {
        return 0;
    }

    public int getType() {
        return type;
    }

    @Override
    public boolean getPowered() {
        return powered;
    }

    @Override
    public void setPowered(boolean value) {
        powered = value;
    }

    @Override
    public boolean isInvNameLocalized() {
        return false;
    }

    @Override
    public boolean isStackValidForSlot(int i, ItemStack itemstack) {
        return false;
    }

    @Override
    public boolean isTeleporterCompatible(Direction side) {
        return false;
    }

    public int checkFrame() {
        int iterStat = 0;
        int iterStat2 = 0;
        boolean chStatus = true;
        for (int xIter = -2; xIter <= 2; xIter++) {
            for (int yIter = 0; yIter <= 4; yIter++) {
                iterStat++;
                if (frameMatrixCheck(iterStat) && chStatus) {
                    if (this.worldObj.getBlockId(xCoord + xIter, yCoord + yIter, zCoord) != Apocalyptic.PortalFrameLvl2.blockID) {
                        chStatus = false;
                    }
                }
            }
        }

        if (chStatus) {
            for (int x2Iter = -2; x2Iter <= 2; x2Iter++) {
                for (int y2Iter = 0; y2Iter <= 4; y2Iter++) {
                    iterStat2++;
                    if (emptyMatrixCheck(iterStat) && !chStatus) {
                        if (this.worldObj.getBlockId(xCoord + x2Iter, yCoord + y2Iter, zCoord) != 0) {
                            chStatus = false;
                        }
                    }
                }
            }
        }

        if (chStatus) {
            return 1;
        } else {
            iterStat = 0;
            iterStat2 = 0;
            chStatus = true;
            for (int zIter = -2; zIter <= 2; zIter++) {
                for (int yIter = 0; yIter <= 4; yIter++) {
                    iterStat++;
                    if (frameMatrixCheck(iterStat) && chStatus) {
                        if (this.worldObj.getBlockId(xCoord, yCoord + yIter, zCoord + zIter) != Apocalyptic.PortalFrameLvl2.blockID) {
                            chStatus = false;
                        }
                    }
                }
            }

            if (chStatus) {
                for (int z2Iter = -2; z2Iter <= 2; z2Iter++) {
                    for (int y2Iter = 0; y2Iter <= 4; y2Iter++) {
                        iterStat2++;
                        if (emptyMatrixCheck(iterStat) && chStatus) {
                            if (this.worldObj.getBlockId(xCoord + z2Iter, yCoord + y2Iter, zCoord) != 0) {
                                chStatus = false;
                            }
                        }
                    }
                }
            }

            if (chStatus) {
                return 2;
            } else {
                return 0;
            }
        }
    }

    private boolean frameMatrixCheck(int chVal) {
        boolean flag = false;
        for (int mIter : frameMatrix) {
            if (mIter == chVal && !flag) {
                flag = true;
            }
        }
        return flag;
    }

    private boolean emptyMatrixCheck(int chVal) {
        boolean flag = false;
        for (int mIter : emptyMatrix) {
            if (mIter == chVal && !flag) {
                flag = true;
            }
        }
        return flag;
    }

    private void setPortal(int side) {
        int iterStat = 0;
        if (side == 1) {
            for (int xIter = -2; xIter <= 2; xIter++) {
                for (int yIter = 0; yIter <= 4; yIter++) {
                    iterStat++;
                    if (emptyMatrixCheck(iterStat)) {
                        this.worldObj.setBlock(xCoord + xIter, yCoord + yIter, zCoord, Apocalyptic.PortalLvl2.blockID);
                    }
                    isActivated = true;
                }
            }
        }

        if (side == 2) {
            for (int zIter = -2; zIter <= 2; zIter++) {
                for (int yIter = 0; yIter <= 4; yIter++) {
                    iterStat++;
                    if (emptyMatrixCheck(iterStat)) {
                        this.worldObj.setBlock(xCoord, yCoord + yIter, zCoord + zIter, Apocalyptic.PortalLvl2.blockID);
                    }
                    isActivated = true;
                }
            }
        }
    }

    private void remPortal() {
        for (int xIter = -2; xIter <= 2; xIter++) {
            for (int yIter = 0; yIter <= 4; yIter++) {
                if (this.worldObj.getBlockId(xCoord + xIter, yCoord + yIter, zCoord) == Apocalyptic.PortalLvl2.blockID) {
                    this.worldObj.setBlock(xCoord + xIter, yCoord + yIter, zCoord, 0);
                }
            }
        }

        for (int zIter = -2; zIter <= 2; zIter++) {
            for (int yIter = 0; yIter <= 4; yIter++) {
                if (this.worldObj.getBlockId(xCoord, yCoord + yIter, zCoord + zIter) == Apocalyptic.PortalLvl2.blockID) {
                    this.worldObj.setBlock(xCoord, yCoord + yIter, zCoord + zIter, 0);
                }
            }
        }
    }

    public String getStatus() {
        if (isActivated) {
            return "\u0430\u043A\u0442\u0438\u0432\u0438\u0440\u043E\u0432\u0430\u043D";
        } else {
            return "\u043D\u0435 \u0430\u043A\u0442\u0438\u0432\u0438\u0440\u043E\u0432\u0430\u043D";
        }
    }
}
