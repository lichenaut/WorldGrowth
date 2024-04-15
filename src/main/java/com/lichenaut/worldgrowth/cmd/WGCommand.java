package com.lichenaut.worldgrowth.cmd;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.runnable.WGBoost;
import com.lichenaut.worldgrowth.runnable.WGRunnableManager;
import com.lichenaut.worldgrowth.util.WGMessager;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class WGCommand implements CommandExecutor {

    private static CompletableFuture<Void> commandFuture = CompletableFuture.completedFuture(null);
    private final Main main;
    private final WGMessager messager;
    private final Set<String> voteOptions = new HashSet<>();

    public WGCommand(Main main, WGMessager messager) {
        this.main = main;
        this.messager = messager;
        voteOptions.add("yes");
        voteOptions.add("y");
        voteOptions.add("no");
        voteOptions.add("n");
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String s, @Nonnull String[] strings) {
        if (checkDisallowed(commandSender, "worldgrowth.command")) return true;

        if (strings.length == 0) {
            commandFuture = commandFuture
                    .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getInvalidCommand(), false));
            return true;
        }

        switch (strings[0]) {
            case "info" -> {
                if (checkDisallowed(commandSender, "worldgrowth.info")) return true;

                StringBuilder progressBar = new StringBuilder(Arrays.toString(messager.getInfoCommand1()) + "\n");
                BaseComponent[] completeBarColor = messager.getCompleteBarColor();
                BaseComponent[] incompleteBarColor = messager.getIncompleteBarColor();
                BaseComponent[] completeBar = messager.combineMessage(completeBarColor, "=");
                BaseComponent[] incompleteBar = messager.combineMessage(incompleteBarColor, "=");
                int completeBars = (int) (33 * main.getPoints() / main.getBorderQuota());
                int incompleteBars = 33 - completeBars;
                double progressPercent = 100 * main.getPoints() / (double) main.getBorderQuota();
                progressBar.append(Arrays.toString(messager.combineMessage(completeBarColor, "[")));
                progressBar.append(Arrays.toString(completeBar).repeat(completeBars));
                progressBar.append(Arrays.toString(incompleteBar).repeat(incompleteBars));
                progressBar.append(Arrays.toString(messager.combineMessage(incompleteBarColor, "] " + String.format("%.2f", progressPercent))));
                //TODO

                return true;
            }
            case "help" -> {
                if (checkDisallowed(commandSender, "worldgrowth.help")) return true;

                commandFuture = commandFuture
                        .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getHelpCommand(), false));
                return true;
            }
            case "reload" -> {
                if (checkDisallowed(commandSender, "worldgrowth.reload")) return true;

                main.reloadWG();

                main.setMainFuture(main.getMainFuture()
                                .thenAcceptAsync(reloaded -> messager.sendMsg(commandSender, messager.getReloadCommand(), false)));
                return true;
            }
            case "boost" -> {
                if (commandSender instanceof Player player) {
                    if (player.isOp()) {
                        commandFuture = commandFuture
                                .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getOnlyConsoleCommand(), false));
                    }
                    return true;
                }
                if (strings.length != 3) {
                    commandFuture = commandFuture
                            .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getUsageBoostCommand(), false));
                    return true;
                }

                commandFuture = commandFuture
                        .thenApplyAsync(processed -> CompletableFuture.supplyAsync(() -> {
                            String multiplier = strings[1];
                            String ticks = strings[2];
                            if (!multiplier.matches("[0-9]+(\\.[0-9]+)?") || !ticks.matches("[0-9]+")) return null;

                            return new String[]{multiplier, ticks};
                        }))
                        .thenAcceptAsync(valuesFuture -> valuesFuture.thenAcceptAsync(values -> {
                            if (values == null) {
                                messager.sendMsg(commandSender, messager.getUsageBoostCommand(), false);
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
            case "vote" -> {
                if (!(commandSender instanceof Player)) {
                    commandFuture = commandFuture
                            .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getOnlyPlayerCommand(), false));
                    return true;
                }

                if (checkDisallowed(commandSender, "worldgrowth.vote")) return true;

                if (strings.length != 2 || !voteOptions.contains(strings[1].toLowerCase())) {
                    commandFuture = commandFuture
                            .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getUsageVoteCommand(), false));
                    return true;
                }

                if (strings[1].equalsIgnoreCase("yes") || strings[1].equalsIgnoreCase("y")) {
                    main.getVoteMath().addVote((Player) commandSender, true);
                    commandFuture = commandFuture
                            .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getVoteYesCommand(), false));
                } else if (strings[1].equalsIgnoreCase("no") || strings[1].equalsIgnoreCase("n")) {
                    main.getVoteMath().addVote((Player) commandSender, false);
                    commandFuture = commandFuture
                            .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getVoteNoCommand(), false));
                } else {
                    commandFuture = commandFuture
                            .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getUsageVoteCommand(), false));
                }

                return true;
            }
        }

        commandFuture = commandFuture
                .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getInvalidCommand(), false));
        return true;
    }

    private boolean checkDisallowed(CommandSender sender, String permission) {
        return sender instanceof Player && !sender.hasPermission(permission);
    }
}
