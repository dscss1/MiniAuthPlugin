package ua.dscss2.miniauthplugin.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.dscss2.miniauthplugin.Configuration.PluginMessages;

import java.util.UUID;

public class User {
    public UUID uuid;
    public String name;
    public String email;
    public String password;
    public String discord;

    public User(@NotNull String userName, @NotNull String password, @NotNull UUID uuid, String email, String discord) {
        uuid = uuid;
        name = userName;
        email = email;
        password = password;
    }

    public void ChangePassword(@NotNull String CurrentPassword, @NotNull String NewPassword) {
        Player player = Bukkit.getPlayer(name);

        if (!password.equals(CurrentPassword)) {
            PluginMessages.sendMessage(player, "&c| &f Вы ввели &cнеправильный &f пароль");
            // Тут может быть ваша реклама
            // Мокси смотрит вашу историю поиска

            return;
        }

        password = NewPassword;
        PluginMessages.sendMessage(player, "&a| &f Ваш пароль &eИзменен");
    }

}
