package me.lluiscamino.multiversehardcore.maincommand;

import org.mockbukkit.mockbukkit.world.WorldMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import me.lluiscamino.multiversehardcore.utils.TestUtils;
import org.bukkit.ChatColor;
import org.junit.Test;

import java.util.Date;

public class UnbanPlayerTest extends MainCommandTest {
    @Test
    public void playerCannotUnban() {
        String[] args = {"unban"};
        PlayerMock player = server.addPlayer();
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] "
                + ChatColor.RED + "You need the following permission to run this command: multiversehardcore.unban";
        mainCommand.onCommand(player, command, "", args);
        TestUtils.assertMessage(player, expectedMessage);
    }

    @Test
    public void OPHasToSpecifyWorld() {
        String[] args = {"unban"};
        PlayerMock op = TestUtils.addOP(server);
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] "
                + ChatColor.RED + "Wrong usage: " + ChatColor.BLUE + "/mvhc" + ChatColor.GREEN + " unban" +
                ChatColor.RED + " <world> <player>";
        mainCommand.onCommand(op, command, "", args);
        TestUtils.assertMessage(op, expectedMessage);
    }

    @Test
    public void OPHasToSpecifyPlayer() {
        String[] args = {"unban", "world"};
        PlayerMock op = TestUtils.addOP(server);
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] "
                + ChatColor.RED + "Wrong usage: " + ChatColor.BLUE + "/mvhc" + ChatColor.GREEN + " unban" +
                ChatColor.RED + " <world> <player>";
        mainCommand.onCommand(op, command, "", args);
        TestUtils.assertMessage(op, expectedMessage);
    }

    @Test
    public void OPCannotUnbanInNonExistentWorld() {
        PlayerMock op = TestUtils.addOP(server);
        String[] args = {"unban", "non_existent_world", op.getName()};
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] "
                + ChatColor.RED + "World does not exist!";
        mainCommand.onCommand(op, command, "", args);
        TestUtils.assertMessage(op, expectedMessage);
    }

    @Test
    public void OPCannotUnbanInNonHardcoreWorld() {
        PlayerMock op = TestUtils.addOP(server);
        WorldMock world = mockWorldCreator.createNormalWorld();
        String[] args = {"unban", world.getName(), op.getName()};
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] "
                + ChatColor.RED + "World " + world.getName() + " is not Hardcore";
        mainCommand.onCommand(op, command, "", args);
        TestUtils.assertMessage(op, expectedMessage);
    }

    @Test
    public void OPCannotUnbanNonExistentPlayer() {
        PlayerMock op = TestUtils.addOP(server);
        WorldMock world = mockWorldCreator.createNormalWorld();
        String[] args = {"unban", world.getName(), "non_existent_player"};
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] "
                + ChatColor.RED + "Player does not exist!";
        mainCommand.onCommand(op, command, "", args);
        TestUtils.assertMessage(op, expectedMessage);
    }

    @Test
    public void OPCannotUnbanAlivePlayer() {
        WorldMock world = mockWorldCreator.createHardcoreWorld();
        PlayerMock op = TestUtils.addOP(server);
        String[] args = {"unban", world.getName(), op.getName()};
        String[] expectedMessages = {
                ChatColor.DARK_RED + "[MV-HARDCORE] " + "You are entering a HARDCORE world, be careful!",
                ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.RED + "Player is not deathbanned!"
        };
        mainCommand.onCommand(op, command, "", args);
        TestUtils.assertMessages(op, expectedMessages);
    }

    @Test
    public void OPCannotUnbanPlayerThatHasNotParticipatedInWorld() {
        PlayerMock op = TestUtils.addOP(server);
        WorldMock world = mockWorldCreator.createHardcoreWorld();
        String[] args = {"unban", world.getName(), op.getName()};
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] "
                + ChatColor.RED + "Player " + op.getName() + " has not participated in the world " + world.getName();
        mainCommand.onCommand(op, command, "", args);
        TestUtils.assertMessage(op, expectedMessage);
    }

    @Test
    public void OPCanUnbanPlayer() {
        PlayerMock op = TestUtils.addOP(server);
        WorldMock world = mockWorldCreator.createHardcoreWorld();
        PlayerMock player = server.addPlayer();
        TestUtils.teleportPlayer(player, world);
        TestUtils.fireJoinEvent(server, player);
        TestUtils.killPlayer(server, player);
        String[] args = {"unban", world.getName(), player.getName()};
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.GREEN
                + "Player " + ChatColor.DARK_GREEN + player.getName() + ChatColor.GREEN + " has been unbanned!";
        mainCommand.onCommand(op, command, "", args);
        op.nextMessage(); // Skip death notification message
        TestUtils.assertMessage(op, expectedMessage);
    }

    @Test
    public void playerDeathBanNumIsUpdatedAfterUnban() {
        PlayerMock op = TestUtils.addOP(server);
        WorldMock world = mockWorldCreator.createHardcoreWorld();
        PlayerMock player = server.addPlayer();
        TestUtils.teleportPlayer(player, world);
        TestUtils.fireJoinEvent(server, player);
        TestUtils.killPlayer(server, player);
        String[] unbanArgs = {"unban", world.getName(), player.getName()};
        String[] getInfoArgs = {"player", world.getName(), player.getName()};
        Date mockJoinDate = new Date();
        String expectedMessage =
                ChatColor.BLUE + player.getName() + ChatColor.RESET + " info:\n" +
                        ChatColor.BOLD + "- Join Date: " + ChatColor.RESET + mockJoinDate + "\n" +
                        ChatColor.BOLD + "- Death banned: " + ChatColor.RESET + "NO\n" +
                        ChatColor.BOLD + "- Deaths: " + ChatColor.RESET + "0\n";
        mainCommand.onCommand(op, command, "", unbanArgs);
        op.nextMessage(); // Skip death notification message
        mainCommand.onCommand(op, command, "", getInfoArgs);
        op.nextMessage(); // Skip player unbanned message
        TestUtils.assertMessage(op, expectedMessage);
    }
}
