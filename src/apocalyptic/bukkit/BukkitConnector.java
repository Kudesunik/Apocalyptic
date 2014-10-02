package apocalyptic.bukkit;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.server.FMLServerHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import org.bukkit.Server;
import org.bukkit.entity.Player;

@SideOnly(Side.SERVER)
public class BukkitConnector {
    
    private final static MinecraftServer vanillaServer = FMLServerHandler.instance().getServer();
    private final static Server bukkitServer = vanillaServer.worldServerForDimension(0).provider.worldObj.getServer();
    private static ClassLoader bukkitClassLoader = setBukkitClassloader();
    
    private static ClassLoader setBukkitClassloader() {
        if (bukkitServer.getPluginManager().getPlugins().length > 0) {
            return bukkitServer.getPluginManager().getPlugins()[0].getClass().getClassLoader();
        }
        else {
            vanillaServer.getLogAgent().getServerLogger().severe("There is no classloader for BukkitConnector! Stopping server");
            vanillaServer.stopServer();
        }
        return null;
    }
    
    public static ClassLoader getBukkitClassloader() {
        if (bukkitClassLoader != null) {
            return bukkitClassLoader;
        }
        else {
            vanillaServer.getLogAgent().getServerLogger().severe("There is no classloader for BukkitConnector! Stopping server");
            vanillaServer.stopServer();
        }
        return null;
    }
    
    public static MinecraftServer getVanillaServer() {
        return vanillaServer;
    }
    
    public static Server getBukkitServer() {
        return bukkitServer;
    }
    
    public static World getVanillaWorldForDimension(int i) {
        return vanillaServer.worldServerForDimension(i).provider.worldObj;
    }
    
    public static org.bukkit.World getBukkitWorldForDimension(int i) {
        switch (i) {
            case 2:
                return bukkitServer.getWorld("DIM2");
            case 3:
                return bukkitServer.getWorld("DIM3");
            case 4:
                return bukkitServer.getWorld("DIM4");
            default:
                return bukkitServer.getWorld("Apocalyptic");
        }
    }
    
    public static EntityPlayerMP getVanillaEntityPlayer(String playerName) {
        return vanillaServer.getConfigurationManager().getPlayerForUsername(playerName);
    }
    
    public static Player getBukkitEntityPlayer(String playerName) {
        return bukkitServer.getPlayerExact(playerName);
    }
}
