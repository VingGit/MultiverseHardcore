package me.lluiscamino.multiversehardcore.utils;

import me.lluiscamino.multiversehardcore.utils.MVWorldManagerFacade;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;

import java.util.HashMap;
import java.util.Map;

/**
 * Test stub implementing {@link MVWorldManagerFacade}.
 * Tracks worlds in a local map; does not depend on any Multiverse-Core classes.
 */
public class MockMVWorldManager implements MVWorldManagerFacade {

    private final ServerMock server;
    private final Map<String, GameMode> worldGameModes = new HashMap<>();
    private final java.util.Set<String> registeredWorlds = new java.util.HashSet<>();

    public MockMVWorldManager(ServerMock server) {
        this.server = server;
    }

    /** Convenience method used by test helpers to pre-register a world. */
    public boolean addWorld(String worldName) {
        return addWorld(worldName, World.Environment.NORMAL);
    }

    public boolean addWorld(String worldName, World.Environment environment) {
        WorldMock world = (WorldMock) server.getWorld(worldName);
        if (world == null) {
            world = server.addSimpleWorld(worldName);
            world.setEnvironment(environment);
        }
        worldGameModes.put(worldName, GameMode.SURVIVAL);
        registeredWorlds.add(worldName);
        return true;
    }

    /** Mirrors the old MVWorldManager.isMVWorld() for test assertions. */
    public boolean isMVWorld(String worldName) {
        return registeredWorlds.contains(worldName);
    }

    @Override
    public boolean createWorld(String worldName, World.Environment environment) {
        return addWorld(worldName, environment);
    }

    @Override
    public void deleteWorld(String worldName) {
        worldGameModes.remove(worldName);
        registeredWorlds.remove(worldName);
    }

    @Override
    public GameMode getDefaultGameMode(World world) {
        return worldGameModes.getOrDefault(world.getName(), GameMode.SURVIVAL);
    }

    @Override
    public void setWorldDifficulty(String worldName, Difficulty difficulty) {
        // No-op in tests – difficulty is cosmetic only.
    }
}
