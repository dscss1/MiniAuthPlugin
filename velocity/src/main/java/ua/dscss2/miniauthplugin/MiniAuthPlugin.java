package ua.dscss2.miniauthplugin;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.chunk.Dimension;
import net.elytrium.limboapi.api.chunk.VirtualWorld;
import net.elytrium.limboapi.api.event.LoginLimboRegisterEvent;
import net.elytrium.limboapi.api.LimboFactory;
import net.elytrium.limboapi.api.player.GameMode;
import org.slf4j.Logger;
import ua.dscss2.miniauthplugin.handler.LoginHandler;
import ua.dscss2.miniauthplugin.listener.TcpListener;
import ua.dscss2.miniauthplugin.stats.Statistics;

@Plugin(
        id = "miniauthplugin",
        name = "MiniAuthPlugin",
        version = "1.0",
        description = "Плагин на авторизацию",
        url = "http://ds-city.com",
        authors = {"dscss2", "ds-city"}
)
public class MiniAuthPlugin {

    private final ProxyServer server;
    private final Logger logger;
    private final LimboFactory limboFactory;
    private Statistics statistics;
    private Limbo filterServer;
    private VirtualWorld filterWorld;
    private TcpListener tcpListener;

    @Inject
    public MiniAuthPlugin(ProxyServer server, Logger logger, LimboFactory limboFactory) {
        this.server = server;
        this.logger = logger;
        this.limboFactory = limboFactory;
    }

    public void sendToFilterServer(Player player) {
        try {
            if (this.tcpListener != null) {
                this.tcpListener.registerAddress(player.getRemoteAddress().getAddress());
            }

            this.filterServer.spawnPlayer(player, new LoginHandler(player, this));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    @Subscribe
    public void onPlayerLogin(LoginEvent event) {
        Player player = event.getPlayer();
        logger.info("Player {} is attempting to log in...", player.getUsername());

        // Send player to Limbo during login process
        filterServer = limboFactory.createLimbo(filterWorld).setGameMode(GameMode.CREATIVE).setName("MiniAuthPlugin").setWorldTime(12000);
        sendToFilterServer(player);
    }

    @Subscribe
    public void onLoginLimboRegister(LoginLimboRegisterEvent event) {
        Player player = event.getPlayer();
        logger.info("Player {} registered for Limbo during login process.", player.getUsername());

        // Optionally, perform additional actions when player registers for Limbo during login
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        double x = 1.5;
        double y = 128;
        double z = 0.5;
        double CAPTCHA_YAW = 90;
        double CAPTCHA_PITCH = 38;
        logger.info("Plugin MiniAuth initialized.");
            filterWorld = this.limboFactory.createVirtualWorld(
                Dimension.OVERWORLD, x, y ,z, (float) CAPTCHA_YAW, (float) CAPTCHA_PITCH
        );
    }

    public ProxyServer getServer() {
        return server;
    }

    public Statistics getStatistics() {
        return statistics;
    }
}
