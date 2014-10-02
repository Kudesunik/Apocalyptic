package apocalyptic.teleporters;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import apocalyptic.Apocalyptic;
import apocalyptic.util.PrivateCleanerBridge;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

public class TeleportTo3Lvl extends Teleporter {

    private final WorldServer wsi;
    private final Random rand;
    private final MinecraftServer mcSrv;
    private final int side;
    private int iter;
    private int[][] portalMatrix = {{2, 3, 4, 6, 10, 11, 15, 16, 20, 22, 23, 24},
                                    {7, 8, 9, 12, 13, 14, 17, 18, 19}};
    List<Integer> portalFrameList = new ArrayList<Integer>();
    List<Integer> portalEmptyList = new ArrayList<Integer>();
    List<Integer> portalStoneList = new ArrayList<Integer>();

    public TeleportTo3Lvl(WorldServer par1WorldServer, MinecraftServer mcServer, int flag) {
        super(par1WorldServer);
        this.wsi = par1WorldServer;
        this.rand = new Random();
        this.mcSrv = mcServer;
        this.side = flag;
        this.iter = 0;
        setArrays();
    }

    @Override
    public void placeInPortal(Entity par1Entity, double par2, double par4, double par6, float par8) {
        if (this.wsi.provider.dimensionId == 3) {
            if (!PrivateCleanerBridge.getInstance().isRegionInLvl3Exists(par1Entity.getEntityName())) {
                this.makePortal(par1Entity);
            }
            this.placeInExistingPortal(par1Entity, par2, par4, par6, par8);
            PrivateCleanerBridge.getInstance().handlePortalRegionLvl3(par1Entity.getEntityName());
        } else if (this.wsi.provider.dimensionId == 0) {
            PrivateCleanerBridge.getInstance().deletePortalRegion(par1Entity.getEntityName());
            this.placeInExistingPortal(par1Entity, par2, par4, par6, par8);
            if (!PrivateCleanerBridge.getInstance().isRegionInLvl3Exists(par1Entity.getEntityName())) {
                this.deletePortal(par1Entity);
            }
        }
    }

    @Override
    public boolean placeInExistingPortal(Entity par1Entity, double par2, double par4, double par6, float par8) {

        int playerX = MathHelper.floor_double(par1Entity.posX);
        int playerY = MathHelper.floor_double(par1Entity.posY);
        int playerZ = MathHelper.floor_double(par1Entity.posZ);

        if (this.wsi.provider.dimensionId == 3) {
            par1Entity.setLocationAndAngles(playerX, playerY - 2, playerZ, par1Entity.rotationYaw, par1Entity.rotationPitch);
        } else {
            par1Entity.setLocationAndAngles(playerX, playerY + 2, playerZ, par1Entity.rotationYaw, par1Entity.rotationPitch);
        }
        return true;
    }

    @Override
    public boolean makePortal(Entity par1Entity) {

        int playerX = MathHelper.floor_double(par1Entity.posX);
        int playerY = MathHelper.floor_double(par1Entity.posY);
        int playerZ = MathHelper.floor_double(par1Entity.posZ);

        for (int xC = -2; xC <= 2; xC++) {
            for (int zC = -2; zC <= 2; zC++) {
                for (int yC = -1; yC <= 2; yC++) {
                    loadChunkIfNeeded(3, (playerX + xC), (playerZ + zC));
                    wsi.setBlockToAir(playerX + xC, playerY + yC, playerZ + zC);
                }
            }
        }

        if (this.side == 1) {
            for (int xC = -2; xC <= 2; xC++) {
                for (int zC = -2; zC <= 2; zC++) {
                    iter++;
                    loadChunkIfNeeded(3, (playerX + xC), (playerZ + zC));
                    if (portalFrameList.contains(iter)) {
                        wsi.setBlock(playerX + xC, playerY - 2, playerZ + zC, Apocalyptic.PortalFrameLvl3.blockID);
                    }
                    if (portalEmptyList.contains(iter)) {
                        wsi.setBlock(playerX + xC, playerY - 2, playerZ + zC, Apocalyptic.PortalLvl3.blockID);
                    }
                }
            }
        } else if (this.side == 2) {
            for (int zC = -2; zC <= 2; zC++) {
                for (int xC = -2; xC <= 2; xC++) {
                    iter++;
                    loadChunkIfNeeded(3, (playerX + xC), (playerZ + zC));
                    if (portalFrameList.contains(iter)) {
                        wsi.setBlock(playerX + xC, playerY - 2, playerZ + zC, Apocalyptic.PortalFrameLvl3.blockID);
                    }
                    if (portalEmptyList.contains(iter)) {
                        wsi.setBlock(playerX + xC, playerY - 2, playerZ + zC, Apocalyptic.PortalLvl3.blockID);
                    }
                }
            }
        }
        return true;
    }

    public boolean deletePortal(Entity par1Entity) {

        int playerX = MathHelper.floor_double(par1Entity.posX);
        int playerY = MathHelper.floor_double(par1Entity.posY);
        int playerZ = MathHelper.floor_double(par1Entity.posZ);

        for (int xC = -5; xC <= 5; xC++) {
            for (int yC = -4; yC <= 4; yC++) {
                for (int zC = -5; zC <= 5; zC++) {
                    loadChunkIfNeeded(3, (playerX + xC), (playerZ + zC));
                    if (mcSrv.worldServerForDimension(3).getBlockId(playerX + xC, playerY + yC, playerZ + zC) == Apocalyptic.PortalFrameLvl3.blockID || mcSrv.worldServerForDimension(3).getBlockId(playerX + xC, playerY + yC, playerZ + zC) == Apocalyptic.PortalLvl3.blockID) {
                        mcSrv.worldServerForDimension(3).setBlockToAir(playerX + xC, playerY + yC, playerZ + zC);
                    }
                }
            }
        }
        return true;
    }

    private void setArrays() {
        for (int index = 0; index < portalMatrix[0].length; index++) {
            portalFrameList.add(portalMatrix[0][index]);
        }
        for (int index = 0; index < portalMatrix[1].length; index++) {
            portalEmptyList.add(portalMatrix[1][index]);
        }
    }
    
    /**
     * Get x and y coords of block on chunk in dimension and load that chunk if needed
     * @param dimension
     * @param x
     * @param z 
     */
    private void loadChunkIfNeeded(int dimension, int x, int z) {
        Chunk chunkToLoad = this.mcSrv.worldServerForDimension(dimension).getChunkFromBlockCoords(x, z);
        IChunkProvider chunkProvider = this.mcSrv.worldServerForDimension(dimension).getChunkProvider();
        while (!chunkToLoad.isChunkLoaded) {
            chunkProvider.loadChunk(chunkToLoad.xPosition, chunkToLoad.zPosition);
        }
    }
}
