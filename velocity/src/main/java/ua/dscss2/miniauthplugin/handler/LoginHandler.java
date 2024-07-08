package ua.dscss2.miniauthplugin.handler;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.Player;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboSessionHandler;
import net.elytrium.limboapi.api.player.LimboPlayer;
import ua.dscss2.miniauthplugin.MiniAuthPlugin;
import ua.dscss2.miniauthplugin.stats.Statistics;

public class LoginHandler implements LimboSessionHandler {
    private final Player proxyPlayer;
    private final ProtocolVersion version;
    private final MiniAuthPlugin plugin;
    private final Statistics statistics;
    private LimboPlayer player;
    private Limbo server;
    private long joinTime;
    private final int validX;
    private final int validY;
    private final int validZ;

    private double posX;
    private double posY;
    private double lastY;
    private double posZ;


    public LoginHandler(Player player, MiniAuthPlugin plugin) {
        this.proxyPlayer = player;
        this.version = player.getProtocolVersion();
        this.plugin = plugin;

        this.statistics = this.plugin.getStatistics();

        FALLING_COORDS FALLING_COORDS = new FALLING_COORDS();
        ua.dscss2.miniauthplugin.handler.FALLING_COORDS fallingCoords = FALLING_COORDS;
        this.validX = fallingCoords.X;
        this.validY = fallingCoords.Y;
        this.validZ = fallingCoords.Z;

        this.posX = this.validX;
        this.posY = this.validY;
        this.posZ = this.validZ;
    }


    @Override
    public void onSpawn(Limbo server, LimboPlayer player) {
        this.server = server;
        this.player = player;

        this.joinTime = System.currentTimeMillis();
    }

    @Override
    public void onMove(double x, double y, double z) {
        this.lastY = this.posY;
        this.posY = y;
        this.posZ = z;

        }
    }
