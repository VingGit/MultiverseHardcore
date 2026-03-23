package me.lluiscamino.multiversehardcore.utils;

import me.lluiscamino.multiversehardcore.MultiverseHardcore;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.command.ConsoleCommandSenderMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;

import java.lang.reflect.Field;

public final class TestUtils {

    private TestUtils() {
    }

    public static void assertMessage(@NotNull ConsoleCommandSenderMock consoleCommandSender, @NotNull String expectedMessage) {
        Assert.assertEquals(expectedMessage, consoleCommandSender.nextMessage());
    }

    public static void assertMessage(@NotNull PlayerMock player, @NotNull String expectedMessage) {
        Assert.assertEquals(expectedMessage, player.nextMessage());
    }

    public static void assertMessages(@NotNull ConsoleCommandSenderMock consoleCommandSender, @NotNull String[] expectedMessages) {
        String message = consoleCommandSender.nextMessage();
        int i = 0;
        while (message != null && i < expectedMessages.length) {
            Assert.assertEquals(expectedMessages[i++], message);
            message = consoleCommandSender.nextMessage();
        }
        Assert.assertNull(message);
        Assert.assertEquals(expectedMessages.length, i);
    }

    public static void assertMessages(@NotNull PlayerMock player, @NotNull String[] expectedMessages) {
        String message = player.nextMessage();
        int i = 0;
        while (message != null && i < expectedMessages.length) {
            Assert.assertEquals(expectedMessages[i++], message);
            message = player.nextMessage();
        }
        Assert.assertNull(message);
        Assert.assertEquals(expectedMessages.length, i);
    }

    public static void assertWorldsAreEqual(@NotNull WorldMock world1, @NotNull WorldMock world2) {
        Assert.assertEquals(world1.getName(), world2.getName());
    }

    public static PlayerMock addOP(ServerMock server) {
        PlayerMock player = server.addPlayer();
        player.setOp(true);
        return player;
    }

    public static void killPlayer(@NotNull ServerMock server, @NotNull PlayerMock player) {
        player.setHealth(0);
        // Simulate the player clicking "Respawn" on the death screen.
        // Our PlayerRespawn listener then schedules the enforce-task
        // (enter_world_ticks = 2) so it fires during performTicks below.
        PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(
                player, player.getLocation(), false, false, false,
                PlayerRespawnEvent.RespawnReason.DEATH);
        server.getPluginManager().callEvent(respawnEvent);
        // MockBukkit sets alive=false after PlayerDeathEvent but does not restore it via
        // setHealth(). Force alive=true before performTicks so that player.teleport() inside
        // the scheduled ban-enforcement task (enter_world_ticks = 2) succeeds.
        forceRevive(player);
        server.getScheduler().performTicks(2L);
    }

    /**
     * Walks the class hierarchy of {@code player} to find {@code LivingEntityMock.alive}
     * and sets it to {@code true}.  This mirrors the real-server flow where the player
     * goes through the respawn screen and becomes valid before any subsequent world events.
     */
    private static void forceRevive(@NotNull PlayerMock player) {
        Class<?> clazz = player.getClass();
        while (clazz != null) {
            try {
                Field f = clazz.getDeclaredField("alive");
                f.setAccessible(true);
                f.setBoolean(player, true);
                return;
            } catch (NoSuchFieldException ignored) {
                clazz = clazz.getSuperclass();
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot revive player in test via reflection", e);
            }
        }
        throw new RuntimeException("Field 'alive' not found in PlayerMock hierarchy");
    }

    public static void teleportPlayer(@NotNull PlayerMock player, @NotNull WorldMock world) {
        player.setLocation(world.getSpawnLocation());
    }

    public static void fireJoinEvent(@NotNull ServerMock server, @NotNull PlayerMock player) {
        server.getPluginManager().callEvent(new PlayerJoinEvent(player, net.kyori.adventure.text.Component.empty()));
        server.getScheduler().performTicks(2L);
    }

    public static WorldMock getPlayerWorld(@NotNull PlayerMock player) {
        return (WorldMock) player.getLocation().getWorld();
    }

    public static void setPlayerPermissions(@NotNull MultiverseHardcore plugin, @NotNull PlayerMock player,
                                            @NotNull String... permissions) {
        PermissionAttachment attachment = player.addAttachment(plugin);
        for (String permission : permissions) {
            attachment.setPermission(permission, true);
        }
    }
}
