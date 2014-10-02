package apocalyptic.tile;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.network.INetworkDataProvider;
import ic2.api.network.INetworkUpdateListener;
import ic2.api.network.NetworkHelper;
import ic2.api.tile.IEnergyStorage;
import ic2.api.tile.IWrenchable;

import java.util.List;
import java.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import apocalyptic.Apocalyptic;
import apocalyptic.CommonProxy;

/**
 * Detector Tile Entity TODO Well, this class is done on 100%, but if there is
 * some time, need to rewrite use mappings
 *
 * @author Kunik
 *
 */
public class TileEntityDetector extends TileEntity implements IEnergySink, IEnergyStorage, IWrenchable, INetworkDataProvider, INetworkUpdateListener {

    public int type;
    public boolean initialized;
    public boolean detectorStatus;
    public boolean prevDetectorStatus;
    public int meta;
    public int switcher;
    public int tick;
    public int tickRate;
    public double electricityStored;
    public double prevElectricityStored;

    public TileEntityDetector() {
        this.type = 1;
        this.tickRate = 20;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        electricityStored = tagCompound.getDouble("electricityStored");
        detectorStatus = tagCompound.getBoolean("detectorStatus");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setDouble("electricityStored", electricityStored);
        tagCompound.setBoolean("detectorStatus", detectorStatus);
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
        if (tick == 0) {
            meta = getBlockMetadata();
            if (getStored() < 2) {
                worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
                if (meta >= 4 && meta < 8) {
                    worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta - 4, 3);
                }
                if (meta >= 8) {
                    worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta - 8, 3);
                }
            } else if (getStored() >= 2 && detectorStatus == false) {
                worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
                if (meta < 4) {
                    worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta + 4, 3);
                }
                if (meta >= 8) {
                    worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta - 4, 3);
                }
            } else if (getStored() >= 2 && detectorStatus == true) {
                worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
                if (meta < 4) {
                    worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta + 8, 3);
                }
                if (meta < 8 && meta >= 4) {
                    worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta + 4, 3);
                }
            } else {
                //Strange situation
            }
            tick = tickRate;
        } else {
            tick--;
        }

        if (getStored() >= 2) {
            remEnergy(2);
        }
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
        if (field.equals("detectorStatus") && prevDetectorStatus != detectorStatus) {
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
            prevDetectorStatus = detectorStatus;
        }
        if (field.equals("prevElectricityStored") && prevElectricityStored != electricityStored) {
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
            prevElectricityStored = electricityStored;
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        Vector<String> vector = new Vector<String>(2);
        vector.add("electricityStored");
        vector.add("detectorStatus");
        return vector;
    }

    @Override
    public void validate() {
        super.validate();
    }

    @Override
    public void invalidate() {
        if (this.initialized) {
            if (!this.worldObj.isRemote) {
                MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
            }
            initialized = false;
        }
        super.invalidate();
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
        return new ItemStack(Apocalyptic.detectorBlock, 1, getType());
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
    public int getMaxSafeInput() {
        return 32;
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

    public boolean getDetectorStatus() {
        if (!worldObj.isRemote) {
            detectorStatus = CommonProxy.detectorStatus;
            NetworkHelper.updateTileEntityField(this, "detectorStatus");
        }
        return detectorStatus;
    }

    @Override
    public int getCapacity() {
        return 256;
    }

    @Override
    public int getOutput() {
        return 0;
    }

    @Override
    public boolean isTeleporterCompatible(Direction side) {
        return false;
    }

    public int getType() {
        return type;
    }
}
