package apocalyptic.teleporters;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class CommandTeleporter extends Teleporter {

    public CommandTeleporter(WorldServer par1WorldServer, MinecraftServer mcServer) {
        super(par1WorldServer);
    }

    @Override
    public void placeInPortal(Entity par1Entity, double par2, double par4, double par6, float par8) {
        this.placeInExistingPortal(par1Entity, par2, par4, par6, par8);
    }

    @Override
    public boolean placeInExistingPortal(Entity par1Entity, double par2, double par4, double par6, float par8) {
        int playerX = MathHelper.floor_double(par1Entity.posX);
        int playerY = MathHelper.floor_double(par1Entity.posY);
        int playerZ = MathHelper.floor_double(par1Entity.posZ);
        par1Entity.setLocationAndAngles(playerX, playerY, playerZ, par1Entity.rotationYaw, par1Entity.rotationPitch);
        return true;
    }
}
