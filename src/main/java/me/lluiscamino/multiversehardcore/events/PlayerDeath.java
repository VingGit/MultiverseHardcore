package me.lluiscamino.multiversehardcore.events;

import me.lluiscamino.multiversehardcore.MultiverseHardcore;
import me.lluiscamino.multiversehardcore.exceptions.PlayerNotParticipatedException;
import me.lluiscamino.multiversehardcore.exceptions.WorldIsNotHardcoreException;
import me.lluiscamino.multiversehardcore.models.DeathBan;
import me.lluiscamino.multiversehardcore.models.PlayerParticipation;
import me.lluiscamino.multiversehardcore.utils.MessageSender;
import me.lluiscamino.multiversehardcore.utils.WorldUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Date;
import java.util.logging.Logger;

public class PlayerDeath implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(PlayerDeathEvent event) {
        Logger log = MultiverseHardcore.getInstance().getLogger();
        try {
            Player player = event.getEntity();
            log.info("[DEBUG] onDeath: " + player.getName() + " died in world=" + player.getWorld().getName());
            World world = getDeathBanWorld(player);
            log.info("[DEBUG] onDeath: ban-world resolved to=" + world.getName());
            String bypass = "multiversehardcore.bypass." + world.getName();
            if (player.isPermissionSet(bypass) && player.hasPermission(bypass)) {
                log.info("[DEBUG] onDeath: " + player.getName() + " has bypass perm, skipping ban");
                return;
            }
            PlayerParticipation participation = new PlayerParticipation(player, world);
            participation.addDeathBan(new Date(), event.getDeathMessage());
            log.info("[DEBUG] onDeath: ban recorded for " + player.getName() + " in " + world.getName()
                    + ", totalDeaths=" + participation.getNumDeathBans());
            sendPlayerDiedMessage(participation);
            player.setHealth(20);
            log.info("[DEBUG] onDeath: health restored, calling handlePlayerEnterWorld"
                    + " (player.world=" + player.getWorld().getName() + ")");
            WorldUtils.handlePlayerEnterWorld(event);
        } catch (PlayerNotParticipatedException | WorldIsNotHardcoreException e) {
            log.info("[DEBUG] onDeath: caught " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private World getDeathBanWorld(Player player) throws WorldIsNotHardcoreException {
        World world = player.getWorld();
        World normalWorld = WorldUtils.getNormalWorld(world);

        if (!WorldUtils.worldIsHardcore(world) && !WorldUtils.worldIsHardcore(normalWorld)) {
            throw new WorldIsNotHardcoreException("");
        } else if (!WorldUtils.worldIsHardcore(world)) {
            world = normalWorld;
        }
        return world;
    }

    private void sendPlayerDiedMessage(PlayerParticipation participation) {
        DeathBan deathBan = participation.getLastDeathBan(); // last death ban
        Player player = participation.getPlayer();
        World world = participation.getWorld();
        String message = deathBan.isForever() ?
                player.getDisplayName() + " died and won't be able to play in the world " +
                        ChatColor.RED + world.getName() + ChatColor.RESET + " again!" :
                player.getDisplayName() + " died and won't be able to play in the world " +
                        ChatColor.RED + world.getName() + ChatColor.RESET + " until " + deathBan.getEndDate();
        MessageSender.broadcast(message);
    }
}
