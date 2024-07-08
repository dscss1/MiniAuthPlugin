package ua.dscss2.miniauthplugin.velocity.listener;

import com.velocitypowered.api.proxy.config.ProxyConfig;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import ua.dscss2.miniauthplugin.velocity.MiniAuthPlugin;
import net.elytrium.pcap.Pcap;
import net.elytrium.pcap.PcapException;
import net.elytrium.pcap.data.PcapAddress;
import net.elytrium.pcap.data.PcapDevice;
import net.elytrium.pcap.data.PcapError;
import net.elytrium.pcap.handle.BpfProgram;
import net.elytrium.pcap.handle.PcapHandle;
import net.elytrium.pcap.layer.IP;
import net.elytrium.pcap.layer.Packet;
import net.elytrium.pcap.layer.TCP;
import net.elytrium.pcap.layer.data.LinkType;
import net.elytrium.pcap.layer.exception.LayerDecodeException;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

public class TcpListener {

    private final MiniAuthPlugin plugin;
    private final Map<InetAddress, TcpAwaitingPacket> tempPingTimestamp = new HashMap<>();
    @MonotonicNonNull
    private PcapHandle handle;

    static {
        try {
            Pcap.init();
        } catch (PcapException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public TcpListener(MiniAuthPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() throws PcapException {
        this.handle = Pcap.openLive("any",120, 1,
                10
        );

        int port = ((ProxyConfig) this.plugin.getServer().getConfiguration()).getQueryPort();
        BpfProgram filter = this.handle.compile("tcp and (dst port " + port + " or src port " + port + ")", 1);
        this.handle.setFilter(filter);
        filter.free();

        Set<InetAddress> localAddresses = Pcap.findAllDevs().stream()
                .map(PcapDevice::getAddresses)
                .flatMap(Collection::stream)
                .map(PcapAddress::getAddress)
                .filter(Objects::nonNull)
                .map(InetSocketAddress::getAddress)
                .collect(Collectors.toSet());

        LinkType datalink = this.handle.datalink();
        new Thread(() -> {
            Thread.currentThread().setContextClassLoader(MiniAuthPlugin.class.getClassLoader());
            long listenDelay = 50;

            try {
                this.handle.loop(-1, (packetHeader, rawPacket) -> {
                    try {
                        Packet packet = new Packet();
                        packet.decode(rawPacket, datalink);

                        // Ethernet/LinuxSLL -> IP -> TCP
                        IP ipPacket = (IP) packet.getLayers().get(1);
                        TCP tcpPacket = (TCP) packet.getLayers().get(2);

                        if (localAddresses.contains(ipPacket.getSrcAddress()) && tcpPacket.isPsh() && tcpPacket.isAck()) {
                            TcpAwaitingPacket previousPacket = this.tempPingTimestamp.get(ipPacket.getDstAddress());
                            if (previousPacket != null) {
                                long currentTime = System.currentTimeMillis();
                                if (previousPacket.time + listenDelay <= currentTime) {
                                    previousPacket.seq = tcpPacket.getAckSn();
                                    previousPacket.time = currentTime;
                                }
                            }
                        }

                        if (localAddresses.contains(ipPacket.getDstAddress()) && tcpPacket.isAck()) {
                            TcpAwaitingPacket awaitingPacket = this.tempPingTimestamp.get(ipPacket.getSrcAddress());
                            if (awaitingPacket != null && awaitingPacket.seq == tcpPacket.getSequence()) {
                                int pingDiff = (int) (System.currentTimeMillis() - awaitingPacket.time);
                                if (pingDiff > 2) {
                                    this.plugin.getStatistics().updatePing(ipPacket.getSrcAddress(), pingDiff);
                                }
                            }
                        }
                    } catch (LayerDecodeException e) {
                        throw new IllegalStateException(e);
                    }
                });
            } catch (PcapException e) {
                if (e.getError() != PcapError.ERROR_BREAK) {
                    throw new IllegalStateException(e);
                }
            }
        }).start();
    }

    public void registerAddress(InetAddress address) {
        this.tempPingTimestamp.put(address, new TcpAwaitingPacket(Integer.MIN_VALUE, 0));
    }

    public void removeAddress(InetAddress address) {
        this.tempPingTimestamp.remove(address);
    }

    public void stop() {
        this.handle.breakLoop();
        this.handle.close();
    }

    private static class TcpAwaitingPacket {

        private int seq;
        private long time;

        private TcpAwaitingPacket(int seq, long time) {
            this.seq = seq;
            this.time = time;
        }
    }
}