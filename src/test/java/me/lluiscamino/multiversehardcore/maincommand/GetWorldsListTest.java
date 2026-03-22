package me.lluiscamino.multiversehardcore.maincommand;

import org.mockbukkit.mockbukkit.world.WorldMock;
import org.mockbukkit.mockbukkit.command.ConsoleCommandSenderMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.bukkit.ChatColor;
import org.junit.Test;
import me.lluiscamino.multiversehardcore.utils.TestUtils;

public class GetWorldsListTest extends MainCommandTest {

    @Test
    public void playerCannotSeeWorldsList() {
        String[] args = {"list"};
        PlayerMock player = server.addPlayer();
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] "
                + ChatColor.RED + "You need the following permission to run this command: multiversehardcore.list";
        mainCommand.onCommand(player, command, "", args);
        TestUtils.assertMessage(player, expectedMessage);
    }

    @Test
    public void OPCanSeeEmptyWorldsList() {
        String[] args = {"list"};
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] "
                + ChatColor.BLUE + "Worlds list: ";
        PlayerMock op = TestUtils.addOP(server);
        mainCommand.onCommand(op, command, "", args);
        TestUtils.assertMessage(op, expectedMessage);
    }

    @Test
    public void consoleCanSeeEmptyWorldsList() {
        String[] args = {"list"};
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] "
                + ChatColor.BLUE + "Worlds list: ";
        ConsoleCommandSenderMock console = new ConsoleCommandSenderMock();
        mainCommand.onCommand(console, command, "", args);
        TestUtils.assertMessage(console, expectedMessage);
    }

    @Test
    public void OPCanSeeWorldsListWithOneWorld() {
        String worldName = "hardcore_world";
        String[] args = {"list"};
        String[] expectedMessages = {
                ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.BLUE + "Worlds list: ",
                worldName + "\n"
        };
        PlayerMock op = TestUtils.addOP(server);
        mockWorldCreator.createHardcoreWorld(worldName);
        mainCommand.onCommand(op, command, "", args);
        TestUtils.assertMessages(op, expectedMessages);
    }

    @Test
    public void OPCanSeeWorldsListWithNetherEndAndNormalWorld() {
        PlayerMock op = TestUtils.addOP(server);
        WorldMock world = mockWorldCreator.createHardcoreWorld();
        String worldName = world.getName();
        String[] args = {"list"};
        String[] expectedMessages = {
                ChatColor.DARK_RED + "[MV-HARDCORE] " + ChatColor.BLUE + "Worlds list: ",
                worldName + "\n" +
                        "\t" + worldName + "_nether\n" +
                        "\t" + worldName + "_the_end\n"
        };
        mockWorldCreator.createNetherWorld();
        mockWorldCreator.createTheEndWorld();
        mainCommand.onCommand(op, command, "", args);
        TestUtils.assertMessages(op, expectedMessages);
    }
}
