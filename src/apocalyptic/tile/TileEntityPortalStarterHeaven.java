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
import ic2.api.network.INetworkClientTileEntityEventListener;
import java.util.Vector;

public class TileEntityPortalStarterHeaven extends TileEntity implements IInventory, IEnergySink, IEnergyStorage, IWrenchable, INetworkDataProvider, INetworkUpdateListener, INetworkClientTileEntityEventListener, IRedstonePowered {

    public static Random rand = new Random();
    public int type;
    public ItemStack[] inventory;
    public boolean core;
    public boolean initialized;
    public int tick;
    public boolean isActivated;
    private int linkID;
    private String playerName;
    private int[] localCoords;
    private int[] remoteCoords;
    public double electricityStored;
    public double prevElectricityStored;
    public boolean powered;
    public int dischw;
    private long counter;
    public int[] checkMatrix = {3, 8, 11, 12, 14, 15, 18, 23};
    public static final int SLOT_BATTERY = 1;
    public static final int SLOT_CORE = 2;

    public TileEntityPortalStarterHeaven() {
        
        this.inventory = new ItemStack[50];
        this.localCoords = new int[3];
        this.remoteCoords = new int[3];
        this.powered = false;
        this.isActivated = false;
        this.counter = 0;
        this.linkID = 0;
        this.playerName = "";
        this.initialized = false;
        this.core = false;
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
    public void setInventorySlotContents(int slotIndex, ItemStack itemStack) {
        
        inventory[slotIndex] = itemStack;
        
        if (itemStack != null && itemStack.stackSize > getInventoryStackLimit())
        {
            itemStack.stackSize = getInventoryStackLimit();
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
        linkID = tagCompound.getInteger("linkID");
        playerName = tagCompound.getString("playerName");
        localCoords = tagCompound.getIntArray("localCoords");
        remoteCoords = tagCompound.getIntArray("remoteCoords");
        core = tagCompound.getBoolean("core");
        NBTTagList tagList = tagCompound.getTagList("Inventory");

        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);

            byte slot = tag.getByte("Slot");

            if (slot >= 0 && slot < inventory.length) {
                inventory[slot] = ItemStack.loadItemStackFromNBT(tag);
            }
        }
        onInventoryChanged();
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
        tagCompound.setInteger("linkID", linkID);
        tagCompound.setIntArray("localCoords", localCoords);
        tagCompound.setIntArray("remoteCoords", remoteCoords);
        tagCompound.setBoolean("isActivated", isActivated);
        tagCompound.setString("playerName", playerName);
        tagCompound.setTag("Inventory", itemList);
        tagCompound.setDouble("electricityStored", electricityStored);
        tagCompound.setInteger("dischw", dischw);
        tagCompound.setBoolean("core", core);
    }

    @Override
    public void updateEntity() {
        if (!this.initialized && this.worldObj != null && this.worldObj.provider.dimensionId == 0) {
            if (!this.worldObj.isRemote) {
                MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
            }
            this.onInventoryChanged();
            this.initialized = true;
        }
        if (this.worldObj.provider.dimensionId == 0) {
            onUpdate();
            super.updateEntity();
        }
    }

    public void onUpdate() {
        if (!this.worldObj.isRemote && inventory[SLOT_BATTERY] != null) {
            if (getStored() < getCapacity()) {
                int discharge = ElectricItem.manager.discharge(inventory[SLOT_BATTERY], 36, 3, false, false);
                addEnergy(discharge);
            }
        }
        
        if (!this.worldObj.isRemote && inventory[SLOT_CORE] != null && inventory[SLOT_CORE].itemID == Apocalyptic.teleportationCore.itemID && !this.core) {
            this.core = true;
            NetworkHelper.updateTileEntityField(this, "core");
        }
        else if (!this.worldObj.isRemote && (inventory[SLOT_CORE] == null || (inventory[SLOT_CORE] != null && inventory[SLOT_CORE].itemID != Apocalyptic.teleportationCore.itemID)) && this.core) {
            this.core = false;
            NetworkHelper.updateTileEntityField(this, "core");
        }

        if (tick == 20) {
            if (checkFrame()) {
                if (isActivated && (!checkCore() || (getStored() < getCapacity()))) {
                    remPortal();
                    isActivated = false;
                    NetworkHelper.updateTileEntityField(this, "isActivated");
                } else if (checkCore() && getStored() == getCapacity() && !isActivated && (yCoord + 4) < 200) {
                    setPortal();
                    isActivated = true;
                    NetworkHelper.updateTileEntityField(this, "isActivated");
                }
            } else {
                remPortal();
                isActivated = false;
                NetworkHelper.updateTileEntityField(this, "isActivated");
            }
            tick = 0;
        } else {
            tick++;
        }
    }

    /**
     * Old code for portal starter in heaven.
     *
    public void onUpdateHeaven() {
        if (tick == 20) {
            if (checkFrame() && !isActivated) {
                setPortal();
                isActivated = true;
                NetworkHelper.updateTileEntityField(this, "isActivated");
                tick = 0;
            }
        }
        else {
            tick++;
        }
    }
    */

    @Override
    public String getInvName() {
        return "TileEntityPortalStarterHeaven";
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
        return true;
    }

    @Override
    public boolean isAddedToEnergyNet() {
        return this.initialized;
    }

    @Override
    public void onNetworkUpdate(String field) {
        
    }

    @Override
    public List<String> getNetworkedFields() {
        Vector<String> vector = new Vector<String>(8);
        vector.add("electricityStored");
        vector.add("dischw");
        vector.add("isActivated");
        vector.add("linkID");
        vector.add("playerName");
        vector.add("localCoords");
        vector.add("remoteCoords");
        vector.add("core");
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
        return new ItemStack(Apocalyptic.PortalStarterHeaven, 1, getType());
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
        NetworkHelper.updateTileEntityField(this, "electricityStored");
    }

    public int remEnergy(int amount) {
        electricityStored -= amount;
        NetworkHelper.updateTileEntityField(this, "electricityStored");
        return (int) electricityStored;
    }

    @Override
    public int addEnergy(int amount) {
        electricityStored += amount;
        if (electricityStored > getCapacity()) {
            electricityStored = getCapacity();
        }
        NetworkHelper.updateTileEntityField(this, "electricityStored");
        return (int) electricityStored;
    }

    @Override
    public int getCapacity() {
        return 180000000;
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

    public boolean checkFrame() {
        int iterStat = 0;
        boolean chStatus = true;
        for (int xIter = -2; xIter <= 2; xIter++) {
            for (int zIter = -2; zIter <= 2; zIter++) {
                iterStat++;
                if (frameMatrixCheck(iterStat) && chStatus) {
                    if (this.worldObj.getBlockId(xCoord + xIter, yCoord, zCoord + zIter) != Apocalyptic.PortalFrameHeaven.blockID) {
                        chStatus = false;
                    }
                }
            }

            if (chStatus) {
                for (int y2Iter = 1; y2Iter <= 2; y2Iter++) {
                    if (!chStatus) {
                        if (this.worldObj.getBlockId(xCoord, yCoord + y2Iter, zCoord) != 0) {
                            chStatus = false;
                        }
                    }
                }
            }
        }
        return chStatus;
    }

    private boolean frameMatrixCheck(int chVal) {
        boolean flag = false;
        for (int mIter : checkMatrix) {
            if (mIter == chVal && !flag) {
                flag = true;
            }
        }
        return flag;
    }

    private void setPortal() {
        for (int yIter = 1; yIter <= 2; yIter++) {
            if (this.worldObj.getBlockId(xCoord, yCoord + yIter, zCoord) != Apocalyptic.PortalHeaven.blockID) {
                this.worldObj.setBlock(xCoord, yCoord + yIter, zCoord, Apocalyptic.PortalHeaven.blockID);
            }
        }
    }

    private void remPortal() {
        for (int yIter = 1; yIter <= 2; yIter++) {
            if (this.worldObj.getBlockId(xCoord, yCoord + yIter, zCoord) == Apocalyptic.PortalHeaven.blockID) {
                this.worldObj.setBlock(xCoord, yCoord + yIter, zCoord, 0);
            }
        }
    }
    
    @Override
    public void onInventoryChanged() {
        super.onInventoryChanged();
    }

    private boolean checkCore() {
        return this.core;
    }

    public String getStatus() {
        if (isActivated) {
            return "\u0430\u043A\u0442\u0438\u0432\u0438\u0440\u043E\u0432\u0430\u043D";
        } else {
            return "\u043D\u0435 \u0430\u043A\u0442\u0438\u0432\u0438\u0440\u043E\u0432\u0430\u043D";
        }
    }

    public String getPlayerName() {
        if (!playerName.isEmpty()) {
            return playerName;
        }
        return "Unknown";
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
        NetworkHelper.updateTileEntityField(this, "playerName");
    }

    public int getLinkID() {
        return this.linkID;
    }

    public void setLinkID(int linkID) {
        this.linkID = linkID;
        NetworkHelper.updateTileEntityField(this, "linkID");
    }

    public int[] getLocalCoords() {
        return this.localCoords;
    }

    public void setLocalCoords(int[] localCoords) {
        this.localCoords = localCoords;
        NetworkHelper.updateTileEntityField(this, "localCoords");
    }

    public int[] getRemoteCoords() {
        return this.remoteCoords;
    }

    public void setRemoteCoords(int[] remoteCoords) {
        this.remoteCoords = remoteCoords;
        NetworkHelper.updateTileEntityField(this, "localCoords");
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        counter = 0;
    }
}