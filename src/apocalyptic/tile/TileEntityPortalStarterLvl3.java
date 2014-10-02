package apocalyptic.tile;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.item.ElectricItem;
import ic2.api.network.INetworkDataProvider;
import ic2.api.network.INetworkUpdateListener;
import ic2.api.network.NetworkHelper;
import ic2.api.tile.IEnergyStorage;
import ic2.api.tile.IWrenchable;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import apocalyptic.Apocalyptic;
import apocalyptic.api.IRedstonePowered;
import java.util.Vector;

public class TileEntityPortalStarterLvl3 extends TileEntity implements IInventory, IEnergySink, IEnergyStorage, IWrenchable, INetworkDataProvider, INetworkUpdateListener, IRedstonePowered {

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
    public int side;
    public int[] frameMatrix = {2, 4, 6, 10, 16, 20, 22, 24};
    public int[] emptyMatrix = {7, 8, 9, 12, 13, 14, 17, 18, 19};

    public TileEntityPortalStarterLvl3() {
        this.inventory = new ItemStack[50];
        this.powered = false;
        this.isActivated = false;
        this.side = 0;
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
        this.side = tagCompound.getInteger("side");
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
        tagCompound.setInteger("side", this.side);
    }

    public void updateEntity() {
        if (!initialized && worldObj != null) {
            if (worldObj.isRemote) {
                NetworkHelper.requestInitialData(this);
            } else {
                EnergyTileLoadEvent loadEvent = new EnergyTileLoadEvent(this);
                MinecraftForge.EVENT_BUS.post(loadEvent);
            }

            initialized = true;
        }

        onUpdate();
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
                if (isActivated && getStored() >= 120) {
                    remEnergy(120);
                } else if (isActivated && getStored() < 120) {
                    remEnergy(getStored());
                    remPortal(this.side);
                    isActivated = false;
                    NetworkHelper.updateTileEntityField(this, "isActivated");
                } else if (getStored() >= getCapacity() && !isActivated && checkFrame() != 0 && (yCoord + 4) < 200) {
                    setPortal(checkFrame());
                    this.side = checkFrame();
                }
            } else if (isActivated) {
                remEnergy(getStored());
                remPortal(this.side);
                isActivated = false;
                NetworkHelper.updateTileEntityField(this, "isActivated");
            }
            else {
                remPortal(this.side);
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
        return "TileEntityPortalStarterLvl3";
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
        Vector<String> vector = new Vector<String>(4);
        vector.add("electricityStored");
        vector.add("dischw");
        vector.add("isActivated");
        vector.add("side");
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
        return new ItemStack(Apocalyptic.PortalStarterLvl3, 1, getType());
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
            remPortal(this.side);
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
        return 2048;
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
        return 100000000;
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

    private boolean iterFrameCheck(int dxIter, int dzIter, int fxIter, int fzIter, int add1, int add2, int add3) {
        int iterStat = 0;
        int iterStat2 = 0;
        boolean chStatus = true;
        for (int xIter = dxIter; xIter <= fxIter; xIter++) {
            for (int zIter = dzIter; zIter <= fzIter; zIter++) {
                iterStat++;
                if ((frameMatrixCheck(iterStat) || (add1 == iterStat || add2 == iterStat || add3 == iterStat)) && chStatus) {
                    if (this.worldObj.getBlockId(xCoord + xIter, yCoord, zCoord + zIter) != Apocalyptic.PortalFrameLvl3.blockID) {
                        chStatus = false;
                    }
                }
            }
        }

        if (chStatus) {
            for (int x2Iter = dxIter; x2Iter <= fxIter; x2Iter++) {
                for (int z2Iter = dzIter; z2Iter <= fzIter; z2Iter++) {
                    iterStat2++;
                    if (emptyMatrixCheck(iterStat) && !chStatus) {
                        if (this.worldObj.getBlockId(xCoord + x2Iter, yCoord, zCoord + z2Iter) != 0) {
                            chStatus = false;
                        }
                    }
                }
            }
        }
        return chStatus;
    }

    public int checkFrame() {
        if (iterFrameCheck(-2, 0, 2, 4, 3, 15, 23)) {
            return 1;
        } else if (iterFrameCheck(-4, -2, 0, 2, 3, 11, 15)) {
            return 2;
        } else if (iterFrameCheck(-2, -4, 2, 0, 3, 11, 23)) {
            return 3;
        } else if (iterFrameCheck(0, -2, 4, 2, 11, 15, 23)) {
            return 4;
        } else {
            return 0;
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

    private void setPortalFunc(int dxIter, int dzIter, int fxIter, int fzIter) {
        int iterStat = 0;
        for (int xIter = dxIter; xIter <= fxIter; xIter++) {
            for (int zIter = dzIter; zIter <= fzIter; zIter++) {
                iterStat++;
                if (emptyMatrixCheck(iterStat)) {
                    this.worldObj.setBlock(xCoord + xIter, yCoord, zCoord + zIter, Apocalyptic.PortalLvl3.blockID);
                }
                isActivated = true;
            }
        }
    }

    private void setPortal(int side) {
        switch (side) {
            case 1:
                setPortalFunc(-2, 0, 2, 4);
                break;
            case 2:
                setPortalFunc(-4, -2, 0, 2);
                break;
            case 3:
                setPortalFunc(-2, -4, 2, 0);
                break;
            case 4:
                setPortalFunc(0, -2, 4, 2);
                break;
            default:
                //Nothing
                break;
        }
    }

    private void remPortal(int side2) {
        switch (side2) {
            case 1:
                remPortalFunc(-2, 0, 2, 4);
                break;
            case 2:
                remPortalFunc(-4, -2, 0, 2);
                break;
            case 3:
                remPortalFunc(-2, -4, 2, 0);
                break;
            case 4:
                remPortalFunc(0, -2, 4, 2);
                break;
            default:
                break;
        }
    }

    private void remPortalFunc(int dxIter, int dzIter, int fxIter, int fzIter) {
        for (int xIter = dxIter; xIter <= fxIter; xIter++) {
            for (int zIter = dzIter; zIter <= fzIter; zIter++) {
                if (this.worldObj.getBlockId(xCoord + xIter, yCoord, zCoord + zIter) == Apocalyptic.PortalLvl3.blockID) {
                    this.worldObj.setBlock(xCoord + xIter, yCoord, zCoord + zIter, 0);
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
