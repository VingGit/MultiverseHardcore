package me.lluiscamino.multiversehardcore.maincommand;

import org.mockbukkit.mockbukkit.command.ConsoleCommandSenderMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.bukkit.ChatColor;
import org.junit.Test;
import me.lluiscamino.multiversehardcore.utils.TestUtils;

public class GetPluginVersionTest extends MainCommandTest {
    @Test
    public void playerCannotSeePluginVersion() {
        String[] args = {"version"};
        PlayerMock player = server.addPlayer();
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] "
                + ChatColor.RED + "You need the following permission to run this command: "
                + "multiversehardcore.version";
        mainCommand.onCommand(player, command, "", args);
        TestUtils.assertMessage(player, expectedMessage);
    }

    @Test
    public void OPCanSeePluginVersion() {
        String[] args = {"version"};
        PlayerMock op = TestUtils.addOP(server);
        String pluginVersion = plugin.getDescription().getVersion();
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] "
                + ChatColor.BLUE + "Version: " + pluginVersion;
        mainCommand.onCommand(op, command, "", args);
        TestUtils.assertMessage(op, expectedMessage);
    }

    @Test
    public void consoleCanSeePluginVersion() {
        String[] args = {"version"};
        ConsoleCommandSenderMock console = new ConsoleCommandSenderMock();
        String pluginVersion = plugin.getDescription().getVersion();
        String expectedMessage = ChatColor.DARK_RED + "[MV-HARDCORE] "
                + ChatColor.BLUE + "Version: " + pluginVersion;
        mainCommand.onCommand(console, command, "", args);
        TestUtils.assertMessage(console, expectedMessage);
    }
}
