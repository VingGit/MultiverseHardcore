package me.lluiscamino.multiversehardcore.maincommand;

import org.mockbukkit.mockbukkit.world.WorldMock;
import org.mockbukkit.mockbukkit.command.ConsoleCommandSenderMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import me.lluiscamino.multiversehardcore.utils.MockMVWorldManager;
import org.bukkit.ChatColor;
import org.junit.Test;
import me.lluiscamino.multiversehardcore.utils.TestUtils;

public class CreateHardcoreWorldTest extends MainCommandTest {
    @Test
    public void playerCannotCreateHardcoreWorld() {
        String[] args = {"create", "hardcore_world"};
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] "
                + ChatColor.RED + "You need the following permission to run this command: multiversehardcore.create";
        PlayerMock player = server.addPlayer();
        mainCommand.onCommand(player, command, "", args);
        TestUtils.assertMessage(player, expectedMessage);
    }

    @Test
    public void cannotCreateHardcoreWorldWithNoName() {
        String[] args = {"create"};
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] "
                + ChatColor.RED + "Wrong usage: " + ChatColor.BLUE + "/mvhc" + ChatColor.GREEN + " create"
                + ChatColor.RED + " <world>"
                + ChatColor.GRAY + " spectator:[true|false] nether:[true|false] end:[true|false]"
                + " forever:[true|false] ban_seconds:<n> inc_nether:[true|false] inc_end:[true|false]"
                + " respawn:<world>";
        PlayerMock player = TestUtils.addOP(server);
        mainCommand.onCommand(player, command, "", args);
        TestUtils.assertMessage(player, expectedMessage);
    }

    @Test
    public void cannotCreateAlreadyExistingWorld() {
        String worldName = mockWorldCreator.createNormalWorld().getName();
        String[] args = {"create", worldName};
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] "
                + ChatColor.RED + "World " + worldName + " already exists";
        PlayerMock player = TestUtils.addOP(server);
        mainCommand.onCommand(player, command, "", args);
        TestUtils.assertMessage(player, expectedMessage);
    }

    @Test
    public void cannotCreateARespawnHardcoreWorldThatEqualsRespawnWorld() {
        String worldName = "hardcore_world";
        String[] args = {"create", worldName, "false", "false", "false", "true", "0", "true", "true", worldName};
        String[] expectedMessages = {
                ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.BLUE +
                        "Starting creation of world(s)...",
                ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.RED +
                        "World and spawn world cannot be equal"
        };
        PlayerMock player = TestUtils.addOP(server);
        mainCommand.onCommand(player, command, "", args);
        TestUtils.assertMessages(player, expectedMessages);
    }

    @Test
    public void cannotMakeAnRespawnHardcoreWorldWithNonExistentRespawnWorld() {
        String[] args = {"create", "hardcore_world", "false", "false", "false", "true", "0", "true", "true", "non_existing_world"};
        String[] expectedMessages = {
                ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.BLUE +
                        "Starting creation of world(s)...",
                ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.RED +
                        "Respawn world does not exist"
        };
        PlayerMock player = TestUtils.addOP(server);
        mainCommand.onCommand(player, command, "", args);
        TestUtils.assertMessages(player, expectedMessages);
    }

    @Test
    public void cannotCreateAnRespawnHardcoreWorldWithAHardcoreRespawnWorld() {
        WorldMock respawnWorld = mockWorldCreator.createNormalWorld();
        String[] args = {"create", "hardcore_world", "false", "false", "false", "true", "0", "true", "true", respawnWorld.getName()};
        String[] expectedMessages = {
                ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.BLUE +
                        "Starting creation of world(s)...",
                ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.RED +
                        "Respawn world cannot be hardcore"
        };
        PlayerMock player = TestUtils.addOP(server);
        mockWorldCreator.makeWorldHardcore(respawnWorld);
        mainCommand.onCommand(player, command, "", args);
        TestUtils.assertMessages(player, expectedMessages);
    }

    @Test
    public void cannotCreateAFiniteBanLengthHardcoreWorldWithANegativeBanLength() {
        String[] args = {"create", "hardcore_world", "true", "false", "false", "false", "-1", "true", "true", ""};
        String[] expectedMessages = {
                ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.BLUE +
                        "Starting creation of world(s)...",
                ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.RED +
                        "Ban length cannot be less than 0"
        };
        PlayerMock player = TestUtils.addOP(server);
        mainCommand.onCommand(player, command, "", args);
        TestUtils.assertMessages(player, expectedMessages);
    }

    @Test
    public void worldsAreNotCreatedOnError() {
        String[] args = {"create", "hardcore_world", "true", "true", "true", "false", "-1", "true", "true", ""};
        PlayerMock player = TestUtils.addOP(server);
        MockMVWorldManager worldManager = (MockMVWorldManager) plugin.getMVWorldFacade();
        mainCommand.onCommand(player, command, "", args);
        assert !worldManager.isMVWorld("hardcore_world") &&
                !worldManager.isMVWorld("hardcore_world_nether") &&
                !worldManager.isMVWorld("hardcore_world_the_end");
    }

    @Test
    public void consoleCanCreateSimpleHardcoreWorld() {
        String worldName = "hardcore_world";
        String[] args = {"create", worldName};
        String[] expectedMessages = {
                ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.BLUE + "Starting creation of world(s)...",
                ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.GREEN + "World " + ChatColor.DARK_GREEN + worldName + ChatColor.GREEN + " created!",
        };
        ConsoleCommandSenderMock sender = new ConsoleCommandSenderMock();
        mainCommand.onCommand(sender, command, "", args);
        TestUtils.assertMessages(sender, expectedMessages);
    }

    @Test
    public void OPCanCreateSimpleHardcoreSimpleWorld() {
        String worldName = "hardcore_world";
        String[] args = {"create", worldName};
        String[] expectedMessages = {
                ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.BLUE + "Starting creation of world(s)...",
                ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.GREEN + "World " + ChatColor.DARK_GREEN + worldName + ChatColor.GREEN + " created!",
        };
        PlayerMock player = TestUtils.addOP(server);
        mainCommand.onCommand(player, command, "", args);
        TestUtils.assertMessages(player, expectedMessages);
    }

    @Test
    public void OPCanCreateHardcoreWorldWithNetherAndTheEnd() {
        String worldName = "hardcore_world";
        String[] args = {"create", worldName, "true", "true", "true", "true", "0", "true", "true", "null"};
        String[] expectedMessages = {
                ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.BLUE + "Starting creation of world(s)...",
                ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.GREEN + "World " + ChatColor.DARK_GREEN + worldName + ChatColor.GREEN + " created!",
        };
        PlayerMock player = TestUtils.addOP(server);
        mainCommand.onCommand(player, command, "", args);
        TestUtils.assertMessages(player, expectedMessages);
        MockMVWorldManager worldManager = (MockMVWorldManager) plugin.getMVWorldFacade();
        assert worldManager.isMVWorld(worldName) &&
                worldManager.isMVWorld(worldName + "_nether") &&
                worldManager.isMVWorld(worldName + "_the_end");
    }
}
