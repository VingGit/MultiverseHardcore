package me.lluiscamino.multiversehardcore.events;

import me.lluiscamino.multiversehardcore.MultiverseHardcore;
import me.lluiscamino.multiversehardcore.exceptions.PlayerNotParticipatedException;
import me.lluiscamino.multiversehardcore.exceptions.WorldIsNotHardcoreException;
import me.lluiscamino.multiversehardcore.models.PlayerParticipation;
import me.lluiscamino.multiversehardcore.utils.WorldUtils;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.logging.Logger;

public class PlayerRespawn implements Listener {

    /**
     * Re-applies death-ban enforcement after a player respawns.
     *
     * Vanilla Minecraft resets the player's game mode to the world default (SURVIVAL)
     * as part of the respawn cycle. Same-world respawns do not fire
     * PlayerChangedWorldEvent, so neither our PlayerChangeOfWorld listener nor
     * Multiverse-Core's game-mode enforcement runs again. We therefore schedule a
     * delayed task here — after enter_world_ticks — so it fires once the player is
     * alive and both vanilla and Multiverse-Core (default delay: 1 tick) have
     * finished their own game-mode processing.
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        MultiverseHardcore plugin = MultiverseHardcore.getInstance();
        Logger log = plugin.getLogger();
        int enterWorldTicks = plugin.getConfig().getInt("enter_world_ticks");

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            World world = WorldUtils.getNormalWorld(player.getWorld());
            log.info("[DEBUG] respawn-enforce: player=" + player.getName()
                    + " world=" + world.getName()
                    + " isHC=" + WorldUtils.worldIsHardcore(world)
                    + " gameMode=" + player.getGameMode());
            if (!WorldUtils.worldIsHardcore(world)) return;

            try {
                PlayerParticipation participation = new PlayerParticipation(player, world);
                log.info("[DEBUG] respawn-enforce: isBanned=" + participation.isDeathBanned()
                        + " spectatorMode=" + participation.getHcWorld().getConfiguration().isSpectatorMode());
                if (!participation.isDeathBanned()) return;

                if (participation.getHcWorld().getConfiguration().isSpectatorMode()) {
                    player.setGameMode(GameMode.SPECTATOR);
                    log.info("[DEBUG] respawn-enforce: gameMode set → " + player.getGameMode());
                } else {
                    WorldUtils.respawnPlayer(player);
                    log.info("[DEBUG] respawn-enforce: respawnPlayer called for " + player.getName());
                }
            } catch (PlayerNotParticipatedException | WorldIsNotHardcoreException e) {
                log.info("[DEBUG] respawn-enforce: caught " + e.getClass().getSimpleName());
            }
        }, enterWorldTicks);
    }
}

