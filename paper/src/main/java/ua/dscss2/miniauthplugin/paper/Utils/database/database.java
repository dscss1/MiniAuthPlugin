package ua.dscss2.miniauthplugin.paper.Utils.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ua.dscss2.miniauthplugin.Configuration.PluginMessages;
import ua.dscss2.miniauthplugin.MiniAuthPlugin;

import java.sql.Connection;
import java.sql.SQLException;

public class database {

    private final MiniAuthPlugin plugin;
    private static HikariDataSource dataSource;

    public database(MiniAuthPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadDB() {
        boolean dbEnabled = plugin.getConfig().getBoolean("main.database.enabled");
        if (dbEnabled) {
            connectionDB();
        } else {
            plugin.turnOff("База данних не увімкнена");
        }
    }

    private void connectionDB() {
        String dbHost = plugin.getConfig().getString("main.database.host");
        String dbPort = plugin.getConfig().getString("main.database.port");
        String dbName = plugin.getConfig().getString("main.database.database_name");
        String dbUser = plugin.getConfig().getString("main.database.user");
        String dbPassword = plugin.getConfig().getString("main.database.password");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName);
        config.setUsername(dbUser);
        config.setPassword(dbPassword);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);

        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                PluginMessages.consoleSend("&c| &fКакой хороший день все работать");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to connect to the database!");
            e.printStackTrace();

        }
    }



    public HikariDataSource getDataSource() {
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource != null) {
            return dataSource.getConnection();
        } else {
            throw new SQLException("DataSource is not initialized.");
        }
    }
}
