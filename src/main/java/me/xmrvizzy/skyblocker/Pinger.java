package me.xmrvizzy.skyblocker;

import java.net.InetAddress;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.ServerAddress;

public class Pinger {
/*
    static final MinecraftClient client = MinecraftClient.getInstance();
    private static final ThreadPoolExecutor PING_THREAD = new ScheduledThreadPoolExecutor(5, new ThreadFactoryBuilder().setNameFormat("Real Time Server Pinger #%d").setDaemon(true).build());
    public void ping(){
        ClientConnection clientconnection = client.getNetworkHandler().getConnection();
    }

    public static void getRealTimeServerPing(String server)
    {
        PING_THREAD.submit(() ->
        {
            try
            {
                ServerAddress address = ServerAddress.parse(client.getNetworkHandler().getConnection().getAddress().toString());
                ClientConnection manager = ClientConnection.connect(InetAddress.getByName(address.getHost()), address.getPort(), false);

                manager.setListener(new ClientStatusPacketListener()
                {
                    private long currentSystemTime = 0L;

                    @Override
                    public void handleStatusResponse(ClientboundStatusResponsePacket packet)
                    {
                        this.currentSystemTime = Util.getMillis();
                        manager.send(new ServerboundPingRequestPacket(this.currentSystemTime));
                    }

                    @Override
                    public void handlePongResponse(ClientboundPongResponsePacket packet)
                    {
                        long i = this.currentSystemTime;
                        long j = Util.getMillis();
                        HUDHelper.currentServerPing = (int) (j - i);
                    }

                    @Override
                    public void onDisconnect(Component component) {}

                    @Override
                    public Connection getConnection()
                    {
                        return manager;
                    }
                });
                manager.send(new ClientIntentionPacket(address.getHost(), address.getPort(), ConnectionProtocol.STATUS));
                manager.send(new ServerboundStatusRequestPacket());
            }
            catch (Exception ignored) {}
        });
    }
 */
}
