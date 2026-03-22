package me.lluiscamino.multiversehardcore.commands;

import me.lluiscamino.multiversehardcore.MultiverseHardcore;
import me.lluiscamino.multiversehardcore.files.HardcoreWorldsList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MainCommandTabCompleter implements TabCompleter {

    private static final List<String> BOOL = Arrays.asList("true", "false");
    /** Common ban lengths in seconds: 30 s, 1 h, 1 d, 7 d */
    private static final List<String> BAN_LENGTHS = Arrays.asList("30", "3600", "86400", "604800");
    private static final List<String> OP_SUBCOMMANDS = Arrays.asList(
            "create", "makehc", "player", "world", "worlds", "unban", "version");
    private static final List<String> PLAYER_SUBCOMMANDS = Arrays.asList("player", "world");

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return filter(sender.isOp() ? OP_SUBCOMMANDS : PLAYER_SUBCOMMANDS, args[0]);
        }

        switch (args[0].toLowerCase()) {
            case "create":
            case "createworld":
                return tabCreate(sender, args);
            case "makehc":
            case "makeworldhc":
            case "makeworldhardcore":
                return tabMakehc(sender, args);
            case "player":
            case "playerinfo":
            case "seeplayer":
                return tabPlayer(sender, args);
            case "world":
            case "worldinfo":
            case "seeworld":
                return tabWorld(args);
            case "unban":
                return tabUnban(sender, args);
            default:
                return Collections.emptyList();
        }
    }

    // /mvhc create <world> <spectator_mode> <create_nether> <create_end>
    //              <ban_forever> <ban_length> <include_nether> <include_end> <respawn_world>
    // args index:    1           2              3               4
    //                5            6             7               8             9
    private List<String> tabCreate(CommandSender sender, String[] args) {
        if (!sender.isOp()) return Collections.emptyList();
        switch (args.length - 1) {
            case 1:  return Collections.emptyList();          // free-text: new world name
            case 2:  return filter(BOOL, args[2]);            // spectator_mode
            case 3:  return filter(BOOL, args[3]);            // create_nether
            case 4:  return filter(BOOL, args[4]);            // create_end
            case 5:  return filter(BOOL, args[5]);            // ban_forever
            case 6:  return filter(BAN_LENGTHS, args[6]);     // ban_length (seconds)
            case 7:  return filter(BOOL, args[7]);            // include_nether
            case 8:  return filter(BOOL, args[8]);            // include_end
            case 9:  return filter(getLoadedWorldNames(), args[9]); // respawn_world
            default: return Collections.emptyList();
        }
    }

    // /mvhc makehc <world> <spectator_mode> <ban_forever> <ban_length>
    //              <include_nether> <include_end> <respawn_world>
    // args index:    1              2              3           4
    //                5               6              7
    private List<String> tabMakehc(CommandSender sender, String[] args) {
        if (!sender.isOp()) return Collections.emptyList();
        switch (args.length - 1) {
            case 1:  return filter(getLoadedWorldNames(), args[1]); // existing world
            case 2:  return filter(BOOL, args[2]);                  // spectator_mode
            case 3:  return filter(BOOL, args[3]);                  // ban_forever
            case 4:  return filter(BAN_LENGTHS, args[4]);           // ban_length (seconds)
            case 5:  return filter(BOOL, args[5]);                  // include_nether
            case 6:  return filter(BOOL, args[6]);                  // include_end
            case 7:  return filter(getLoadedWorldNames(), args[7]); // respawn_world
            default: return Collections.emptyList();
        }
    }

    // /mvhc player [world] [player]
    private List<String> tabPlayer(CommandSender sender, String[] args) {
        switch (args.length - 1) {
            case 1: return filter(getHardcoreWorldNames(), args[1]);
            case 2: return sender.isOp() ? filter(getOnlinePlayerNames(), args[2]) : Collections.emptyList();
            default: return Collections.emptyList();
        }
    }

    // /mvhc world [world]
    private List<String> tabWorld(String[] args) {
        if (args.length - 1 == 1) return filter(getHardcoreWorldNames(), args[1]);
        return Collections.emptyList();
    }

    // /mvhc unban <world> <player>
    private List<String> tabUnban(CommandSender sender, String[] args) {
        if (!sender.isOp()) return Collections.emptyList();
        switch (args.length - 1) {
            case 1: return filter(getHardcoreWorldNames(), args[1]);
            case 2: return filter(getOnlinePlayerNames(), args[2]);
            default: return Collections.emptyList();
        }
    }

    // ── helpers ────────────────────────────────────────────────────────────

    private List<String> getHardcoreWorldNames() {
        return Arrays.asList(HardcoreWorldsList.instance.getHardcoreWorldNames());
    }

    private List<String> getLoadedWorldNames() {
        return MultiverseHardcore.getInstance().getServer().getWorlds()
                .stream().map(w -> w.getName()).collect(Collectors.toList());
    }

    private List<String> getOnlinePlayerNames() {
        return MultiverseHardcore.getInstance().getServer().getOnlinePlayers()
                .stream().map(p -> p.getName()).collect(Collectors.toList());
    }

    private List<String> filter(List<String> options, String prefix) {
        if (prefix == null || prefix.isEmpty()) return options;
        String lower = prefix.toLowerCase();
        return options.stream()
                .filter(s -> s.toLowerCase().startsWith(lower))
                .collect(Collectors.toList());
    }
}

