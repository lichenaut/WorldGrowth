package com.lichenaut.worldgrowth.cmd;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.util.WGMsgBank;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
public class WGCommand implements CommandExecutor {

    private final Main plugin;
    private final WGMsgBank msgBank;

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String s, @Nonnull String[] strings) {
        if (checkDisallowed(commandSender, "worldgrowth.command")) return true;

        if (strings.length == 0) {
            messageSender(commandSender, msgBank.getInvalidCommand());
            return true;
        }

        if (strings[0].equals("help")) {
            if (checkDisallowed(commandSender, "worldgrowth.help")) return true;
            messageSender(commandSender, msgBank.getHelpCommand());
            return true;
        } else if (strings[0].equals("reload")) {
            if (checkDisallowed(commandSender, "worldgrowth.reload")) return true;
            plugin.reloadWG();
            messageSender(commandSender, msgBank.getReloadCommand());
            return true;
        }

        messageSender(commandSender, msgBank.getInvalidCommand());
        return true;
    }

    private void messageSender(CommandSender sender, BaseComponent[] message) {
        if (sender instanceof Player) {
            sender.spigot().sendMessage(message);
        } else {
            plugin.getLogging().info(new TextComponent(message).toLegacyText().replaceAll("§[0-9a-fA-FklmnoKLMNO]", ""));
        }
    }

    private boolean checkDisallowed(CommandSender sender, String permission) {
        return sender instanceof Player && !sender.hasPermission(permission);
    }
}
