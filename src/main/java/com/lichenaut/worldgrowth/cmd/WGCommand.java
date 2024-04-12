package com.lichenaut.worldgrowth.cmd;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.runnable.WGBoost;
import com.lichenaut.worldgrowth.runnable.WGRunnableManager;
import com.lichenaut.worldgrowth.util.WGMessager;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class WGCommand implements CommandExecutor {

    private static final CompletableFuture<Boolean> COMMAND_FUTURE = CompletableFuture.completedFuture(null);
    private final Main main;
    private final WGMessager messager;

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String s, @Nonnull String[] strings) {
        if (checkDisallowed(commandSender, "worldgrowth.command")) return true;

        if (strings.length == 0) {
            COMMAND_FUTURE
                    .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getInvalidCommand()));
            return true;
        }

        switch (strings[0]) {
            case "info" -> { //TODO wip
                if (checkDisallowed(commandSender, "worldgrowth.info")) return true;

                int greenBars = (int) main.getPoints() / main.getBorderQuota() * 33;
                int grayBars = 33 - greenBars;
                StringBuilder progressBar = new StringBuilder("[");
                progressBar.append("=".repeat(Math.max(0, greenBars)));
                progressBar.append(" ".repeat(Math.max(0, grayBars)));
                progressBar.append("]\n");

                /*messager.sendMsg(commandSender,
                        messager.concatArrays(
                                messager.combineMessage(progressBar.toString(), messager.getProgressCommand1()),
                                messager.combineMessage(String.valueOf()), messager.getProgressCommand2()),*/
                return true;
            }
            case "help" -> {
                if (checkDisallowed(commandSender, "worldgrowth.help")) return true;

                COMMAND_FUTURE
                        .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getHelpCommand()));
                return true;
            }
            case "reload" -> {
                if (checkDisallowed(commandSender, "worldgrowth.reload")) return true;

                main.reloadWG();

                main.getMainFuture()
                        .thenAcceptAsync(reloaded -> messager.sendMsg(commandSender, messager.getReloadCommand()));
                return true;
            }
            case "boost" -> {
                if (commandSender instanceof Player player) {
                    if (player.isOp()) {
                        COMMAND_FUTURE
                                .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getOnlyConsoleCommand()));
                    }
                    return true;
                }
                if (strings.length != 3) {
                    COMMAND_FUTURE
                            .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getUsageBoostCommand()));
                    return true;
                }

                COMMAND_FUTURE
                        .thenApplyAsync(processed -> CompletableFuture.supplyAsync(() -> {
                            String multiplier = strings[1];
                            String ticks = strings[2];
                            if (!multiplier.matches("[0-9]+(\\.[0-9]+)?") || !ticks.matches("[0-9]+")) {
                                return null;
                            }

                            return new String[]{multiplier, ticks};
                        }))
                        .thenAcceptAsync(valuesFuture -> valuesFuture.thenAcceptAsync(values -> {
                            if (values == null) {
                                messager.sendMsg(commandSender, messager.getUsageBoostCommand());
                                return;
                            }

                            double multiplierDouble = Double.parseDouble(values[0]);
                            long delay = Integer.parseInt(values[1]);
                            WGRunnableManager boosterManager = main.getBoostManager();
                            boosterManager.addRunnable(new WGBoost(main, multiplierDouble) {
                                @Override
                                public void run() {
                                    runBoost(delay);
                                }
                            }, 0L);
                            boosterManager.addRunnable(new WGBoost(main, multiplierDouble) {
                                @Override
                                public void run() {
                                    runReset();
                                }
                            }, delay);
                        }));
                return true;
            }
        }

        COMMAND_FUTURE
                .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getInvalidCommand()));
        return true;
    }

    private boolean checkDisallowed(CommandSender sender, String permission) {
        return sender instanceof Player && !sender.hasPermission(permission);
    }
}
