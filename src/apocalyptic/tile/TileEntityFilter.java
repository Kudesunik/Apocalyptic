package apocalyptic.tile;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.item.ElectricItem;
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
import ic2.api.network.INetworkClientTileEntityEventListener;

public class TileEntityFilter extends TileEntity implements IInventory, IEnergySink, IEnergyStorage, IWrenchable, INetworkDataProvider, INetworkClientTileEntityEventListener, INetworkUpdateListener, IRedstonePowered {

    public static Random rand = new Random();
    public int type;
    public ItemStack[] inventory;
    public boolean initialized;
    public int tick;
    public int tickRate;
    public double electricityStored;
    public double prevElectricityStored;
    public double filterDamage;
    public boolean existed;
    public boolean powered;
    public boolean flagP;
    public int exFilter;
    public int dischw;
    public int dmgs;
    public int overclockerUpgrades;
    public int transformerUpgrades;
    public int energyStorageUpgrades;
    private long counter;

    public TileEntityFilter() {
        this.inventory = new ItemStack[50];
        this.powered = false;
        this.tickRate = 20;
        this.flagP = false;
        this.initialized = false;
        this.counter = 0;
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
        exFilter = tagCompound.getInteger("exFilter");
        overclockerUpgrades = tagCompound.getInteger("overclockerUpgrades");
        transformerUpgrades = tagCompound.getInteger("transformerUpgrades");
        energyStorageUpgrades = tagCompound.getInteger("energyStorageUpgrades");
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
        tagCompound.setInteger("exFilter", exFilter);
        tagCompound.setInteger("overclockerUpgrades", overclockerUpgrades);
        tagCompound.setInteger("transformerUpgrades", transformerUpgrades);
        tagCompound.setInteger("energyStorageUpgrades", energyStorageUpgrades);
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
        type = 1;

        if (!this.worldObj.isRemote && inventory[2] != null) {
            if (getUpgrades("transformer") >= 1) {
                if (inventory[2].itemID == Items.getItem("energyCrystal").itemID || inventory[2].itemID == Items.getItem("lapPack").itemID) {
                    type = 2;
                }

                if (getUpgrades("transformer") >= 2) {
                    if (inventory[2].itemID == Items.getItem("lapotronCrystal").itemID) {
                        type = 3;
                    }
                }
            }

            if (getStored() < getCapacity()) {
                int discharge = ElectricItem.manager.discharge(inventory[2], 36, type, false, false);
                addEnergy(discharge);
            }
        }

        if (tick == 0) {
            initRedstone();

            //Inverted
            if (!powered) {
                flagP = true;
                if (!this.worldObj.isRemote && inventory[1] != null && Item.itemsList[inventory[1].itemID] == Apocalyptic.filterItem && getStored() > 1) {
                    if (getUpgrades("overclocker") == 0) {
                        dmgs = 1;
                        dischw = 2;
                        exFilter = 5;
                    } else if (getUpgrades("overclocker") == 1) {
                        dmgs = 2;
                        dischw = 4;
                        exFilter = 7;
                    } else if (getUpgrades("overclocker") == 2) {
                        dmgs = 4;
                        dischw = 8;
                        exFilter = 9;
                    } else if (getUpgrades("overclocker") == 3) {
                        dmgs = 8;
                        dischw = 16;
                        exFilter = 12;
                    } else if (getUpgrades("overclocker") == 4) {
                        dmgs = 16;
                        dischw = 32;
                        exFilter = 16;
                    } else if (getUpgrades("overclocker") == 5) {
                        dmgs = 32;
                        dischw = 64;
                        exFilter = 21;
                    } else if (getUpgrades("overclocker") == 6) {
                        dmgs = 64;
                        dischw = 128;
                        exFilter = 27;
                    } else if (getUpgrades("overclocker") == 7) {
                        dmgs = 128;
                        dischw = 256;
                        exFilter = 34;
                    } else if (getUpgrades("overclocker") >= 8) {
                        dmgs = 256;
                        dischw = 512;
                        exFilter = 42;
                    }

                    int dmgg = inventory[1].getItemDamage();

                    if (dmgg >= 900) {
                        inventory[1] = Items.getItem("cell");
                        Item.itemsList[inventory[1].itemID] = Items.getItem("cell").getItem();
                    } else {
                        inventory[1].setItemDamage(dmgg + dmgs);
                    }

                    NetworkHelper.updateTileEntityField(this, "exFilter");
                    NetworkHelper.updateTileEntityField(this, "dischw");
                    NetworkHelper.updateTileEntityField(this, "overclockerUpgrades");
                    NetworkHelper.updateTileEntityField(this, "transformerUpgrades");
                    NetworkHelper.updateTileEntityField(this, "energyStorageUpgrades");
                } else if (!this.worldObj.isRemote && ((inventory[1] != null && Item.itemsList[inventory[1].itemID] != Apocalyptic.filterItem) || inventory[1] == null)) {
                    changeFilter();
                    if (inventory[1] != null && Item.itemsList[inventory[1].itemID] != Apocalyptic.filterItem || inventory[1] == null) {
                        updateEnviroment(exFilter + 1);
                        exFilter = 0;
                        dischw = 0;
                        NetworkHelper.updateTileEntityField(this, "exFilter");
                        NetworkHelper.updateTileEntityField(this, "dischw");
                    }
                } else if (!this.worldObj.isRemote && getStored() <= 0) {
                    setStored(0);
                    dischw = 0;
                    NetworkHelper.updateTileEntityField(this, "dischw");
                    updateEnviroment(exFilter + 1);
                }
            } //Inverted
            else if (!this.worldObj.isRemote && powered && flagP) {
                updateEnviroment(exFilter + 1);
                flagP = false;
            }

            tick = tickRate;
        } else {
            tick--;
        }

        //Inverted
        if (!this.worldObj.isRemote && dischw > 0 && !powered && flagP) {
            remEnergy(dischw);
            for (int par1 = -exFilter; par1 <= exFilter; par1++) {
                for (int par2 = -exFilter; par2 <= exFilter; par2++) {
                    for (int par3 = -exFilter; par3 <= exFilter; par3++) {
                        Material worldblock = worldObj.getBlockMaterial(xCoord + par1, yCoord + par2, zCoord + par3);
                        if (worldblock == Material.gas && getStored() >= dischw && (yCoord + par2) < 205) {
                            worldObj.setBlock(xCoord + par1, yCoord + par2, zCoord + par3, Apocalyptic.FilteredAir.blockID);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getInvName() {
        return "TileEntityFilter";
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
        return true;
    }

    @Override
    public void onNetworkUpdate(String field) {
        if (field.equals("prevElectricityStored") && prevElectricityStored != electricityStored) {
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
            prevElectricityStored = electricityStored;
        }
    }

    private void initRedstone() {
        RedstoneHelper.checkPowered(worldObj, this);
    }

    @Override
    public List<String> getNetworkedFields() {
        Vector<String> vector = new Vector<String>(2);
        vector.add("electricityStored");
        vector.add("exFilter");
        vector.add("dischw");
        vector.add("overclockerUpgrades");
        vector.add("transformerUpgrades");
        vector.add("energyStorageUpgrades");
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
        return new ItemStack(Apocalyptic.filterBlock, 1, getType());
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
            updateEnviroment(exFilter + 1);
            if (!this.worldObj.isRemote) {
                MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
            }
            initialized = false;
        }
        super.invalidate();
    }

    @Override
    public int getMaxSafeInput() {
        int powi = (int) Math.pow(4, getUpgrades("transformer"));
        return (32 * powi);
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
        return (2000 + (getUpgrades("energyStorage") * 10000));
    }

    public int getUpgrades(String type) {
        overclockerUpgrades = 0;
        transformerUpgrades = 0;
        energyStorageUpgrades = 0;

        for (int it1 = 11; it1 <= 14; it1++) {
            ItemStack ItemStack_inv = inventory[it1];

            ItemStack itemStack_energy = Items.getItem("energyStorageUpgrade");
            ItemStack itemStack_overclocker = Items.getItem("overclockerUpgrade");
            ItemStack itemStack_transformer = Items.getItem("transformerUpgrade");

            if (inventory[it1] != null && ItemStack_inv.isItemEqual(itemStack_overclocker)) {
                overclockerUpgrades += ItemStack_inv.stackSize;
            } else if (inventory[it1] != null && ItemStack_inv.isItemEqual(itemStack_transformer)) {
                transformerUpgrades += ItemStack_inv.stackSize;
            } else if (inventory[it1] != null && ItemStack_inv.isItemEqual(itemStack_energy)) {
                energyStorageUpgrades += ItemStack_inv.stackSize;
            }
        }

        if (type.equals("overclocker")) {
            return overclockerUpgrades;
        }

        if (type.equals("transformer")) {
            return transformerUpgrades;
        }

        if (type.equals("energyStorage")) {
            return energyStorageUpgrades;
        } else {
            return 0;
        }
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

    public void changeFilter() {
        for (int itr = 3; itr <= 10; itr++) {
            if (inventory[itr] != null) {
                try {
                    if (getStored() >= 1500) {
                        if (Item.itemsList[inventory[itr].itemID] == Apocalyptic.filterItem) {
                            ItemStack con1 = getStackInSlot(itr).copy();
                            if (inventory[1] != null) {
                                ItemStack con2 = getStackInSlot(1).copy();
                                inventory[itr] = con2.copy();
                            } else {
                                inventory[itr] = null;
                            }
                            inventory[1] = con1.copy();
                            remEnergy(1500);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Exception in filter: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void setPowered(boolean value) {
        powered = value;
    }

    public void updateEnviroment(int dist) {
        for (int par1 = -dist; par1 <= dist; par1++) {
            for (int par2 = -dist; par2 <= dist; par2++) {
                for (int par3 = -dist; par3 <= dist; par3++) {
                    Material worldblock = worldObj.getBlockMaterial(xCoord + par1, yCoord + par2, zCoord + par3);
                    if (worldblock == Material.air && (yCoord + par2) < 205) {
                        worldObj.setBlock(xCoord + par1, yCoord + par2, zCoord + par3, 0);
                    }
                }
            }
        }
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

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        counter = 0;
    }

    @Override
    public boolean isAddedToEnergyNet() {
        return this.initialized;
    }
}
