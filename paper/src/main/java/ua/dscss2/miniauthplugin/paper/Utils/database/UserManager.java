package ua.dscss2.miniauthplugin.paper.Utils.database;

import ua.dscss2.miniauthplugin.MiniAuthPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManager {
    public boolean isRegistered(String userId) {
        String query = "SELECT 1 FROM users WHERE user_id = ?";
        try (Connection conn = MiniAuthPlugin.getInstance().getDatabase().getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean registerUser(String userId, String userName, String userEmail) {
        if (isRegistered(userId)) {
            return false;
        }

        String insert = "INSERT INTO users (user_id, name, email) VALUES (?, ?, ?)";
        try (Connection conn = MiniAuthPlugin.getInstance().getDatabase().getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setString(1, userId);
            stmt.setString(2, userName);
            stmt.setString(3, userEmail);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
