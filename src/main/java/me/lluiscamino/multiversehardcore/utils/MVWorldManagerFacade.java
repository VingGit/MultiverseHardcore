package me.lluiscamino.multiversehardcore.utils;

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;

/**
 * Thin facade over Multiverse-Core's WorldManager, exposing only what this plugin
 * actually needs. A real implementation wraps MV5's WorldManager; a mock
 * implementation is used in tests.
 */
public interface MVWorldManagerFacade {

    /**
     * Creates a new Multiverse-managed world.
     *
     * @return true if the world was created successfully
     */
    boolean createWorld(String worldName, World.Environment environment);

    /**
     * Deletes a Multiverse-managed world by name (no-op if the world does not exist).
     */
    void deleteWorld(String worldName);

    /**
     * Returns the default game mode configured for the given world in Multiverse.
     * Falls back to SURVIVAL if the world is not managed by Multiverse.
     */
    GameMode getDefaultGameMode(World world);

    /**
     * Sets the Bukkit difficulty on a Multiverse-managed world.
     */
    void setWorldDifficulty(String worldName, Difficulty difficulty);
}

