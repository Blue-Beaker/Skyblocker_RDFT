package me.xmrvizzy.skyblocker;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.text.Normalizer.Form;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.ServerAddress;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Pinger {
    public static final Pinger instance = new Pinger();
    static final MinecraftClient client = MinecraftClient.getInstance();
    private final List<ClientConnection> clientConnections = Collections.synchronizedList(Lists.newArrayList());
    private static final ThreadPoolExecutor PING_THREAD = new ScheduledThreadPoolExecutor(5, new ThreadFactoryBuilder().setNameFormat("Real Time Server Pinger #%d").setDaemon(true).build());
    public int ping = -1;
    ArrayList<Integer> pingResults = new ArrayList<Integer>();
    public void ping(){
        PING_THREAD.submit(() -> {
            ServerInfo server = client.getCurrentServerEntry();
            try {
                add(server,  () -> {
                });
            } catch (Exception e) {
            }
        });
    }

    public void add(final ServerInfo entry, final Runnable runnable) throws UnknownHostException {
        ServerAddress serverAddress = ServerAddress.parse(entry.address);
        final ClientConnection clientConnection = ClientConnection.connect(InetAddress.getByName(serverAddress.getAddress()), serverAddress.getPort(), false);
        this.clientConnections.add(clientConnection);
        entry.label = new TranslatableText("multiplayer.status.pinging");
        entry.ping = -1L;
        entry.playerListSummary = null;
        clientConnection.setPacketListener(new ClientQueryPacketListener() {
            private boolean sentQuery;
            private boolean received;
            private long startTime;

            public void onResponse(QueryResponseS2CPacket packet) {
                if (this.received) {
                    clientConnection.disconnect(new TranslatableText("multiplayer.status.unrequested"));
                } else {
                    this.received = true;
                    this.startTime = Util.getMeasuringTimeMs();
                    clientConnection.send(new QueryPingC2SPacket(this.startTime));
                    this.sentQuery = true;
                }
            }

            public void onPong(QueryPongS2CPacket packet) {
                long l = this.startTime;
                long m = Util.getMeasuringTimeMs();
                entry.ping = m - l;
                Pinger.instance.ping = (int)entry.ping;
                addPingResult((int)entry.ping);
                clientConnection.disconnect(new TranslatableText("multiplayer.status.finished"));
            }

            public void onDisconnected(Text reason) {
                if (!this.sentQuery) {
                    entry.ping = -1L;
                    addPingResult(-1);
                }
            }

            public ClientConnection getConnection() {
                return clientConnection;
            }
        });

        try {
            clientConnection.send(new HandshakeC2SPacket(serverAddress.getAddress(), serverAddress.getPort(), NetworkState.STATUS));
            clientConnection.send(new QueryRequestC2SPacket());
        } catch (Throwable var6) {
        }
    }
    public void addPingResult(int result){
        pingResults.add(result);
        while(pingResults.size()>100){
            pingResults.remove(0);
        }
    }
    public Text showPingResult(){
        ArrayList<Integer> results = new ArrayList<Integer>();
        results.addAll(pingResults);
        int totalPing = 0;
        int success = 0;
        for(int result:results){
            if(result>=0){
                success=success+1;
                totalPing=totalPing+result;
            }
        }
        float lossRate = 1-(float)success/(float)results.size();
        float avgPing = (float)totalPing/(float)success;

        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMaximumFractionDigits(1);
        return(new LiteralText("Avg.ping=")
            .append(new LiteralText(String.format("%.1fms", avgPing)).formatted(Formatting.GOLD))
            .append(new LiteralText(", Packet Loss="))
            .append(new LiteralText(nf.format(lossRate)).formatted(Formatting.GOLD))
            .append(new LiteralText(String.format(", From last %d Pings",results.size()))));
    }
    public void tick() {
        PING_THREAD.execute(()->
            {
                synchronized(clientConnections) {
                    Iterator<ClientConnection> iterator = clientConnections.iterator();
                    while(iterator.hasNext()) {
                        ClientConnection clientConnection = (ClientConnection)iterator.next();
                        if (clientConnection.isOpen()) {
                            clientConnection.tick();
                        } else {
                            iterator.remove();
                            clientConnection.handleDisconnection();
                        }
                    }
                }
            });
        }
}
