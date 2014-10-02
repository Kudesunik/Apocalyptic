package apocalyptic;

import apocalyptic.client.ClientProxy;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import cpw.mods.fml.common.FMLCommonHandler;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.server.FMLServerHandler;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * @author Kunik
 */
public class PacketHandler implements IPacketHandler {

    private static Side side = FMLCommonHandler.instance().getEffectiveSide();
    
    private static Player getPlayer(String name) {
        return (Player)FMLServerHandler.instance().getServer().getConfigurationManager().getPlayerForUsername(name);
    }

    @Override
    public void onPacketData(INetworkManager network, Packet250CustomPayload packet, Player player) {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));

        if (packet.channel.equals("Apocalyptic-rad") && (side == Side.CLIENT)) {
            try {
                ClientProxy.setRadiation((int) inputStream.readDouble());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        if (packet.channel.equals("Apocalyptic-bv") && (side == Side.CLIENT)) {
            try {
                CommonProxy.invisibleBlocks = inputStream.readBoolean();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void LifePacketSender(double radiation, String name) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            outputStream.writeDouble(radiation);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Packet250CustomPayload packet_sender = new Packet250CustomPayload();
        packet_sender.channel = "Apocalyptic-rad";
        packet_sender.data = bos.toByteArray();
        packet_sender.length = bos.size();

        if (side == Side.SERVER) {
            PacketDispatcher.sendPacketToPlayer(packet_sender, getPlayer(name));
        }
    }

    public static void BlockVisibilitySender(boolean result) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            outputStream.writeBoolean(result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Packet250CustomPayload packet_sender = new Packet250CustomPayload();
        packet_sender.channel = "Apocalyptic-bv";
        packet_sender.data = bos.toByteArray();
        packet_sender.length = bos.size();
        if (side == Side.SERVER) {
            PacketDispatcher.sendPacketToAllPlayers(packet_sender);
        }
    }
}