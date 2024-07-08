package ua.dscss2.miniauthplugin;

import org.bukkit.plugin.java.JavaPlugin;
import ua.dscss2.miniauthplugin.Configuration.PluginMessages;
import ua.dscss2.miniauthplugin.Utils.database.database;
//import ua.dscss2.miniauthplugin.Utils.database.database;
//import ua.dscss2.miniauthplugin.commands.commandExecutor;


public final class MiniAuthPlugin extends JavaPlugin {
    private static MiniAuthPlugin instance;
    private database database = new database(this);

    public static MiniAuthPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
        // getCommand("MiniAuthPlugin").setExecutor(new commandExecutor());
        PluginMessages.consoleSend("&a|&f Плагин успешно запущен");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        PluginMessages.consoleSend("&c|&f Выключено");
    }

    @SuppressWarnings("UnstableApiUsage")
    public static String getVersion() {
        return instance.getPluginMeta().getVersion();
    }

    public database getDatabase() {
        return database;
    }

    public void turnOff(String Exception){
        getLogger().severe(Exception);
        getServer().getPluginManager().disablePlugin(this);
    }
}
