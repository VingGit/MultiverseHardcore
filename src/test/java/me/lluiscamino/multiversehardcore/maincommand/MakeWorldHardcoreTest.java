package me.lluiscamino.multiversehardcore.maincommand;

import org.mockbukkit.mockbukkit.world.WorldMock;
import org.mockbukkit.mockbukkit.command.ConsoleCommandSenderMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.bukkit.ChatColor;
import org.junit.Test;
import me.lluiscamino.multiversehardcore.utils.TestUtils;

public class MakeWorldHardcoreTest extends MainCommandTest {
    @Test
    public void playerCannotMakeWorldHardcore() {
        String[] args = {"makehc", "hc_world"};
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] "
                + ChatColor.RED + "You need the following permission to run this command: multiversehardcore.makehc";
        PlayerMock player = server.addPlayer();
        mainCommand.onCommand(player, command, "", args);
        TestUtils.assertMessage(player, expectedMessage);
    }

    @Test
    public void cannotMakeWorldWithNoNameHardcore() {
        String[] args = {"makehc"};
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] "
                + ChatColor.RED + "Wrong usage: " + ChatColor.BLUE + "/mvhc" + ChatColor.GREEN + " makehc"
                + ChatColor.RED + " <world>" + ChatColor.GOLD + " <spectator_mode> <ban_forever> " +
                "<ban_length> <include_nether> <include_end> <respawn_world>";
        PlayerMock player = TestUtils.addOP(server);
        mainCommand.onCommand(player, command, "", args);
        TestUtils.assertMessage(player, expectedMessage);
    }

    @Test
    public void cannotMakeNonExistentWorldHardcore() {
        String[] args = {"makehc", "hc_world"};
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.RED +
                "World does not exist";
        PlayerMock player = TestUtils.addOP(server);
        mainCommand.onCommand(player, command, "", args);
        TestUtils.assertMessage(player, expectedMessage);
    }

    @Test
    public void cannotMakeAlreadyHardcoreWorldHardcore() {
        WorldMock world = mockWorldCreator.createNormalWorld();
        String worldName = world.getName();
        String[] args = {"makehc", worldName};
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.RED +
                "World is already hardcore";
        PlayerMock player = TestUtils.addOP(server);
        mockWorldCreator.makeWorldHardcore(world);
        mainCommand.onCommand(player, command, "", args);
        TestUtils.assertMessage(player, expectedMessage);
    }

    @Test
    public void cannotMakeAnRespawnHardcoreWorldThatEqualsRespawnWorld() {
        WorldMock world = mockWorldCreator.createNormalWorld();
        String worldName = world.getName();
        String[] args = {"makehc", worldName, "false", "true", "0", "true", "true", worldName};
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.RED +
                "World and spawn world cannot be equal";
        PlayerMock player = TestUtils.addOP(server);
        mainCommand.onCommand(player, command, "", args);
        TestUtils.assertMessage(player, expectedMessage);
    }

    @Test
    public void cannotMakeAnRespawnHardcoreWorldWithNonExistentRespawnWorld() {
        WorldMock world = mockWorldCreator.createNormalWorld();
        String worldName = world.getName();
        String[] args = {"makehc", worldName, "false", "true", "0", "true", "true", "non_existing_world"};
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.RED +
                "Respawn world does not exist";
        PlayerMock player = TestUtils.addOP(server);
        mainCommand.onCommand(player, command, "", args);
        TestUtils.assertMessage(player, expectedMessage);
    }

    @Test
    public void cannotMakeAnRespawnHardcoreWorldWithAHardcoreRespawnWorld() {
        WorldMock world = mockWorldCreator.createNormalWorld();
        WorldMock respawnWorld = mockWorldCreator.createNormalWorld();
        String[] args = {"makehc", world.getName(), "false", "true", "0", "true", "true", respawnWorld.getName()};
        String expectedMessage2 = ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.RED +
                "Respawn world cannot be hardcore";
        PlayerMock player = TestUtils.addOP(server);
        mockWorldCreator.makeWorldHardcore(respawnWorld);
        mainCommand.onCommand(player, command, "", args);
        TestUtils.assertMessage(player, expectedMessage2);
    }

    @Test
    public void cannotMakeAFiniteBanLengthHardcoreWorldWithANegativeBanLength() {
        WorldMock world = mockWorldCreator.createNormalWorld();
        String worldName = world.getName();
        String[] args = {"makehc", worldName, "true", "false", "-1", "true", "true", ""};
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.RED +
                "Ban length cannot be less than 0";
        PlayerMock player = TestUtils.addOP(server);
        mainCommand.onCommand(player, command, "", args);
        TestUtils.assertMessage(player, expectedMessage);
    }

    @Test
    public void consoleCanMakeWorldHardcore() {
        WorldMock world = mockWorldCreator.createNormalWorld();
        String worldName = world.getName();
        String[] args = {"makehc", worldName};
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.GREEN +
                "World " + ChatColor.DARK_GREEN + worldName + ChatColor.GREEN + " is now Hardcore!";
        ConsoleCommandSenderMock sender = new ConsoleCommandSenderMock();
        mainCommand.onCommand(sender, command, "", args);
        TestUtils.assertMessage(sender, expectedMessage);
    }

    @Test
    public void OPCanMakeWorldHardcore() {
        WorldMock world = mockWorldCreator.createNormalWorld();
        String worldName = world.getName();
        String[] args = {"makehc", worldName};
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.GREEN +
                "World " + ChatColor.DARK_GREEN + worldName + ChatColor.GREEN + " is now Hardcore!";
        PlayerMock player = TestUtils.addOP(server);
        mainCommand.onCommand(player, command, "", args);
        TestUtils.assertMessage(player, expectedMessage);
    }
}
