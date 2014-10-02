package apocalyptic.teleporters;

import apocalyptic.bukkit.BukkitInteraction;
import apocalyptic.tile.TileEntityPortalStarterHeaven;
import apocalyptic.util.ApocalypticDB;
import apocalyptic.util.PrivateCleanerBridge;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraftforge.transformers.ForgeAccessTransformer;
import org.bukkit.ChatColor;

public class TeleportToHeaven extends Teleporter {

    private final WorldServer wsi;
    private final Random rand;
    private final MinecraftServer mcSrv;
    private final int side;
    private int iter;
    private ApocalypticDB bd = new ApocalypticDB();

    public TeleportToHeaven(WorldServer par1WorldServer, MinecraftServer mcServer, int flag) {
        super(par1WorldServer);
        this.wsi = par1WorldServer;
        this.rand = new Random();
        this.mcSrv = mcServer;
        this.side = flag;
        this.iter = 0;
    }

    @Override
    public void placeInPortal(Entity par1Entity, double par2, double par4, double par6, float par8) {
        if (this.wsi.provider.dimensionId == 4 && par1Entity instanceof EntityPlayer) {
            if (checkLink(par1Entity)) {
                TileEntityPortalStarterHeaven te = getTileEntity(par1Entity);
                int[] crds = te.getRemoteCoords();
                if (te.inventory[2] != null) {
                    this.placeInExistingPortal(par1Entity, par2, par4, par6, par8, crds[0] + 1, crds[1] + 1, crds[2] + 1);
                }
                else if (par1Entity instanceof EntityPlayerMP) {
                    par1Entity.setPosition((this.rand.nextInt(6000) - 3000), 250, (this.rand.nextInt(6000) - 3000));
                    BukkitInteraction.sendToPlayerChat(par1Entity.getEntityName(), "Темпоральное поле от перемещения другого игрока переместило Вас не туда...", ChatColor.RED);
                }
                te.inventory[2] = null;
                te.setStored(0);
            }
        } else if (this.wsi.provider.dimensionId == 0 && par1Entity instanceof EntityPlayer) {
            if (checkLink(par1Entity)) {
                int[] crds = getTileEntity(par1Entity).getRemoteCoords();
                PrivateCleanerBridge.getInstance().removeIslandRights(par1Entity.getEntityName());
                this.placeInExistingPortal(par1Entity, par2, par4, par6, par8, crds[0] + 1, crds[1] + 1, crds[2] + 1);
            }
        }
    }

    public boolean placeInExistingPortal(Entity par1Entity, double par2, double par4, double par6, float par8, int crd1, int crd2, int crd3) {
        par1Entity.setLocationAndAngles(crd1, crd2, crd3, par1Entity.rotationYaw, par1Entity.rotationPitch);
        return true;
    }

    private TileEntityPortalStarterHeaven getTileEntity(Entity entity) {
        
        int playerX = MathHelper.floor_double(entity.posX);
        int playerY = MathHelper.floor_double(entity.posY);
        int playerZ = MathHelper.floor_double(entity.posZ);
        
        for (int x = playerX - 2; x <= playerX + 2; x++) {
            for (int y = playerY - 2; y <= playerY + 2; y++) {
                for (int z = playerZ - 2; z <= playerZ + 2; z++) {
                    if (entity.worldObj.getBlockTileEntity(x, y, z) instanceof TileEntityPortalStarterHeaven) {
                        return (TileEntityPortalStarterHeaven) entity.worldObj.getBlockTileEntity(x, y, z);
                    }
                }
            }
        }
        return null;
    }

    private boolean checkRegionRight(Entity par1Entity) {
        return PrivateCleanerBridge.getInstance().checkRights(par1Entity.getEntityName());
    }

    private boolean checkLink(Entity par1Entity) {
        
        int playerX = MathHelper.floor_double(par1Entity.posX);
        int playerY = MathHelper.floor_double(par1Entity.posY);
        int playerZ = MathHelper.floor_double(par1Entity.posZ);
        
        TileEntityPortalStarterHeaven teHeaven = getTileEntity(par1Entity);
        if (teHeaven != null && !par1Entity.worldObj.isRemote && checkRegionRight(par1Entity)) {
            if (teHeaven.getLinkID() == 0) {
                int plIslID = this.definePlayerIslandID(par1Entity);
                int settedID;
                if (plIslID != 0) {
                    settedID = plIslID;
                } else {
                    settedID = this.setLink();
                }
                if (this.wsi.provider.dimensionId == 4) {
                    bd.executeUpdate("UPDATE `islands` SET `Player` = '" + par1Entity.getEntityName() + "', `coordX2` = '" + playerX + "', `coordY2` = '" + (playerY - 1) + "', `coordZ2` = '" + playerZ + "' WHERE `Id` = '" + settedID + "'");
                }
                teHeaven.setLinkID(settedID);
                teHeaven.setPlayerName(par1Entity.getEntityName());
                String[][] islArr = bd.requestIsland();
                if (this.wsi.provider.dimensionId == 4) {
                    teHeaven.setRemoteCoords(new int[]{Integer.parseInt(islArr[settedID - 1][1]), Integer.parseInt(islArr[settedID - 1][2]), Integer.parseInt(islArr[settedID - 1][3])});
                }
                if (this.wsi.provider.dimensionId == 0) {
                    teHeaven.setRemoteCoords(new int[]{Integer.parseInt(islArr[settedID - 1][5]), Integer.parseInt(islArr[settedID - 1][6]), Integer.parseInt(islArr[settedID - 1][7])});
                }
                return true;
            } else if (teHeaven.getLinkID() != 0) {
                if (this.wsi.provider.dimensionId == 0) {
                    String[][] islArr = bd.requestIsland();
                    teHeaven.setRemoteCoords(new int[]{Integer.parseInt(islArr[teHeaven.getLinkID() - 1][5]), Integer.parseInt(islArr[teHeaven.getLinkID() - 1][6]), Integer.parseInt(islArr[teHeaven.getLinkID() - 1][7])});
                }
                return true;
            }
        }
        return false;
    }
    
    private int definePlayerIslandID(Entity entity) {
        String[][] islArr = bd.requestIsland();
        for (int iter1 = 0; iter1 < islArr.length; iter1++) {
            if (islArr[iter1][4].equals(entity.getEntityName())) {
                return Integer.parseInt(islArr[iter1][0]);
            }
        }
        return 0;
    }

    private int setLink() {
        String[][] islArr = bd.requestIsland();
        while (true) {
            int rndIsl = rand.nextInt(islArr.length);
            if (islArr[rndIsl][4].isEmpty()) {
                return (rndIsl + 1);
            }
        }
    }
}