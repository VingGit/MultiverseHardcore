package me.lluiscamino.multiversehardcore;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import me.lluiscamino.multiversehardcore.exceptions.HardcoreWorldCreationException;
import me.lluiscamino.multiversehardcore.models.HardcoreWorld;
import me.lluiscamino.multiversehardcore.models.HardcoreWorldConfiguration;
import org.bukkit.GameMode;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import me.lluiscamino.multiversehardcore.utils.MockMVWorldManager;
import me.lluiscamino.multiversehardcore.utils.MockWorldCreator;
import me.lluiscamino.multiversehardcore.utils.TestUtils;

import java.util.Date;

public class SpectatorModeDeathBanTest {

    private ServerMock server;
    private MockWorldCreator mockWorldCreator;

    @Before
    public void setUp() {
        server = MockBukkit.mock();
        MultiverseHardcore plugin = MockBukkit.load(MultiverseHardcore.class);
        MockMVWorldManager worldManager = new MockMVWorldManager(server);
        plugin.setMVWorldFacade(worldManager);
        mockWorldCreator = new MockWorldCreator(server, worldManager);
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testGameModeIsSetToSpectatorWhenJoiningWorld() throws HardcoreWorldCreationException {
        WorldMock world = mockWorldCreator.createNormalWorld();
        HardcoreWorld.createHardcoreWorld(new HardcoreWorldConfiguration(world, null, new Date(), true, 0, true, true, true));
        PlayerMock player = server.addPlayer();
        TestUtils.killPlayer(server, player);
        Assert.assertEquals(GameMode.SPECTATOR, player.getGameMode());
    }

    @Test
    public void testGameModeIsNotSetToSpectatorWhenJoiningWorldOP() throws HardcoreWorldCreationException {
        WorldMock world = mockWorldCreator.createNormalWorld();
        HardcoreWorld.createHardcoreWorld(new HardcoreWorldConfiguration(world, null, new Date(), true, 0, true, true, true));
        PlayerMock player = TestUtils.addOP(server);
        TestUtils.killPlayer(server, player);
        Assert.assertEquals(GameMode.SURVIVAL, player.getGameMode());
    }

    @Ignore("OP has all permissions including bypass in MockBukkit; cannot unset default:false permissions in test suite")
    @Test
    public void testGameModeIsSetToSpectatorWhenJoiningWorldOP() throws HardcoreWorldCreationException {
        WorldMock world = mockWorldCreator.createNormalWorld();
        HardcoreWorld.createHardcoreWorld(new HardcoreWorldConfiguration(world, null, new Date(), true, 0, true, true, true));
        PlayerMock player = TestUtils.addOP(server);
        TestUtils.killPlayer(server, player);
        Assert.assertEquals(GameMode.SPECTATOR, player.getGameMode());
    }

    @Test
    public void testGameModeIsSetToSurvivalWhenDeathBanEnds() throws HardcoreWorldCreationException, InterruptedException {
        final int banLengthInSeconds = 1;
        WorldMock world = mockWorldCreator.createNormalWorld();
        HardcoreWorld.createHardcoreWorld(new HardcoreWorldConfiguration(world, null, new Date(), false, banLengthInSeconds, true, true, true));
        PlayerMock player = server.addPlayer();
        TestUtils.killPlayer(server, player);
        Thread.sleep(banLengthInSeconds * 1000);
        TestUtils.fireJoinEvent(server, player);
        Assert.assertEquals(GameMode.SURVIVAL, player.getGameMode());
    }

    @Test
    public void testGameModeIsSetToSurvivalWhenJoiningDifferentWorld() throws HardcoreWorldCreationException {
        WorldMock hcWorld = mockWorldCreator.createNormalWorld();
        WorldMock world = mockWorldCreator.createNormalWorld();
        HardcoreWorld.createHardcoreWorld(new HardcoreWorldConfiguration(hcWorld, null, new Date(), true, 0, true, true, true));
        PlayerMock player = server.addPlayer();
        TestUtils.killPlayer(server, player);
        Assert.assertEquals(GameMode.SPECTATOR, player.getGameMode());
        TestUtils.teleportPlayer(player, world);
        TestUtils.fireJoinEvent(server, player);
        Assert.assertEquals(GameMode.SURVIVAL, player.getGameMode());
    }

    @Test
    public void testGameModeIsSetToSpectatorWhenDyingOnTheNether() throws HardcoreWorldCreationException {
        WorldMock world = mockWorldCreator.createNormalWorld();
        WorldMock nether = mockWorldCreator.createNetherWorld();
        HardcoreWorld.createHardcoreWorld(new HardcoreWorldConfiguration(world, null, new Date(), true, 0, true, true, true));
        PlayerMock player = server.addPlayer();
        TestUtils.teleportPlayer(player, nether);
        TestUtils.killPlayer(server, player);
        Assert.assertEquals(GameMode.SPECTATOR, player.getGameMode());
    }

    @Test
    public void testGameModeIsNotSetToSpectatorWhenDyingOnTheNether() throws HardcoreWorldCreationException {
        WorldMock world = mockWorldCreator.createNormalWorld();
        WorldMock nether = mockWorldCreator.createNetherWorld();
        HardcoreWorld.createHardcoreWorld(new HardcoreWorldConfiguration(world, null, new Date(), true, 0, true, false, true));
        PlayerMock player = server.addPlayer();
        TestUtils.teleportPlayer(player, nether);
        TestUtils.killPlayer(server, player);
        Assert.assertEquals(GameMode.SURVIVAL, player.getGameMode());
    }

    @Test
    public void testGameModeIsSetToSpectatorWhenDyingOnTheEnd() throws HardcoreWorldCreationException {
        WorldMock world = mockWorldCreator.createNormalWorld();
        WorldMock theEnd = mockWorldCreator.createTheEndWorld();
        HardcoreWorld.createHardcoreWorld(new HardcoreWorldConfiguration(world, null, new Date(), true, 0, true, true, true));
        PlayerMock player = server.addPlayer();
        TestUtils.teleportPlayer(player, theEnd);
        TestUtils.killPlayer(server, player);
        Assert.assertEquals(GameMode.SPECTATOR, player.getGameMode());
    }

    @Test
    public void testGameModeIsNotSetToSpectatorWhenDyingOnTheEnd() throws HardcoreWorldCreationException {
        WorldMock world = mockWorldCreator.createNormalWorld();
        WorldMock theEnd = mockWorldCreator.createTheEndWorld();
        HardcoreWorld.createHardcoreWorld(new HardcoreWorldConfiguration(world, null, new Date(), true, 0, true, true, false));
        PlayerMock player = server.addPlayer();
        TestUtils.teleportPlayer(player, theEnd);
        TestUtils.killPlayer(server, player);
        Assert.assertEquals(GameMode.SURVIVAL, player.getGameMode());
    }
}
