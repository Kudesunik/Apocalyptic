package apocalyptic;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import apocalyptic.api.IRedstonePowered;

public class RedstoneHelper {
	private static boolean isPoweredWire(World world, int x, int y, int z)
    {
        return world.getBlockId(x, y, z) == Block.redstoneWire.blockID && Block.blocksList[Block.redstoneWire.blockID].isProvidingStrongPower(world, x, y, z, 1) == 1;
    }

    public static void checkPowered(World world, TileEntity tileentity)
    {
        if(world != null && tileentity!=null && tileentity instanceof IRedstonePowered)
        {
            boolean powered = world.isBlockIndirectlyGettingPowered(tileentity.xCoord, tileentity.yCoord, tileentity.zCoord) ||
                    isPoweredWire(world, tileentity.xCoord+1, tileentity.yCoord, tileentity.zCoord) ||
                    isPoweredWire(world, tileentity.xCoord-1, tileentity.yCoord, tileentity.zCoord) ||
                    isPoweredWire(world, tileentity.xCoord, tileentity.yCoord, tileentity.zCoord+1) ||
                    isPoweredWire(world, tileentity.xCoord, tileentity.yCoord, tileentity.zCoord-1);
            ((IRedstonePowered)tileentity).setPowered(powered);
        }
    }
}
