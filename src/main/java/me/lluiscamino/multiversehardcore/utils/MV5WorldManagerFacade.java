package me.lluiscamino.multiversehardcore.utils;

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.options.CreateWorldOptions;
import org.mvplugins.multiverse.core.world.options.DeleteWorldOptions;

/**
 * Production implementation of {@link MVWorldManagerFacade} that delegates to
 * Multiverse-Core 5.x's {@link WorldManager}.
 */
public class MV5WorldManagerFacade implements MVWorldManagerFacade {

    private final WorldManager worldManager;

    public MV5WorldManagerFacade(@NotNull WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    @Override
    public boolean createWorld(String worldName, World.Environment environment) {
        return worldManager.createWorld(
                CreateWorldOptions.worldName(worldName).environment(environment)
        ).isSuccess();
    }

    @Override
    public void deleteWorld(String worldName) {
        worldManager.getWorld(worldName)
                .peek(w -> worldManager.deleteWorld(DeleteWorldOptions.world(w)));
    }

    @Override
    public GameMode getDefaultGameMode(World world) {
        return worldManager.getLoadedWorld(world)
                .map(w -> w.getGameMode())
                .getOrElse(GameMode.SURVIVAL);
    }

    @Override
    public void setWorldDifficulty(String worldName, Difficulty difficulty) {
        worldManager.getLoadedWorld(worldName)
                .peek(w -> w.setDifficulty(difficulty));
    }
}

