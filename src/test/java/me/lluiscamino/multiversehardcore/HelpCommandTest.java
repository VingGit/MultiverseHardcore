package me.lluiscamino.multiversehardcore;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import me.lluiscamino.multiversehardcore.commands.HelpCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import me.lluiscamino.multiversehardcore.utils.MockMVWorldManager;
import me.lluiscamino.multiversehardcore.utils.TestUtils;

public class HelpCommandTest {

    private final HelpCommand helpCommand = new HelpCommand();
    private ServerMock server;
    private PluginCommand command;

    @Before
    public void setUp() {
        server = MockBukkit.mock();
        MultiverseHardcore plugin = MockBukkit.load(MultiverseHardcore.class);
        MockMVWorldManager worldManager = new MockMVWorldManager(server);
        plugin.setMVWorldFacade(worldManager);
        if (command == null) command = MultiverseHardcore.getInstance().getCommand("mvhchelp");
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void helpDialogIsCorrect() {
        String expectedMessage =
                ChatColor.BOLD + "Available commands: " + ChatColor.RESET + "\n" +
                        ChatColor.BLUE + "/mvhc" + ChatColor.GREEN + " player" + ChatColor.RESET + "\n" +
                        ChatColor.BLUE + "/mvhc" + ChatColor.GREEN + " world";
        PlayerMock player = server.addPlayer();
        helpCommand.onCommand(player, command, "", new String[]{});
        TestUtils.assertMessage(player, expectedMessage);
    }

    @Test
    public void helpDialogIsCorrectAsOP() {
        String expectedMessage =
                ChatColor.BOLD + "Available commands: " + ChatColor.RESET + "\n" +
                        ChatColor.BLUE + "/mvhc" + ChatColor.GREEN + " create"
                        + ChatColor.RED + " <world>"
                        + ChatColor.GRAY + " spectator:[true|false] nether:[true|false] end:[true|false]"
                        + " forever:[true|false] ban_seconds:<n> inc_nether:[true|false] inc_end:[true|false]"
                        + " respawn:<world>" + ChatColor.RESET + "\n" +
                        ChatColor.BLUE + "/mvhc" + ChatColor.GREEN + " makehc"
                        + ChatColor.RED + " <world>"
                        + ChatColor.GRAY + " spectator:[true|false] forever:[true|false] ban_seconds:<n>"
                        + " inc_nether:[true|false] inc_end:[true|false] respawn:<world>" + ChatColor.RESET + "\n" +
                        ChatColor.BLUE + "/mvhc" + ChatColor.GREEN + " player" + ChatColor.GOLD + " <world> <player>" + ChatColor.RESET + "\n" +
                        ChatColor.BLUE + "/mvhc" + ChatColor.GREEN + " world" + ChatColor.GOLD + " <world>" + ChatColor.RESET + "\n" +
                        ChatColor.BLUE + "/mvhc" + ChatColor.GREEN + " worlds" + ChatColor.RESET + "\n" +
                        ChatColor.BLUE + "/mvhc" + ChatColor.GREEN + " unban" +
                        ChatColor.RED + " <world> <player>" + ChatColor.RESET + "\n" +
                        ChatColor.BLUE + "/mvhc" + ChatColor.GREEN + " version" + ChatColor.RESET + "\n";
        PlayerMock op = TestUtils.addOP(server);
        helpCommand.onCommand(op, command, "", new String[]{});
        TestUtils.assertMessage(op, expectedMessage);
    }

}
