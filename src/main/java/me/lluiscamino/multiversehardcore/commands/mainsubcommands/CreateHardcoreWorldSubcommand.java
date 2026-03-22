package me.lluiscamino.multiversehardcore.commands.mainsubcommands;

import me.lluiscamino.multiversehardcore.utils.MVWorldManagerFacade;
import me.lluiscamino.multiversehardcore.commands.HelpCommand;
import me.lluiscamino.multiversehardcore.commands.MainSubcommand;
import me.lluiscamino.multiversehardcore.exceptions.HardcoreWorldCreationException;
import me.lluiscamino.multiversehardcore.exceptions.InvalidCommandInputException;
import me.lluiscamino.multiversehardcore.models.HardcoreWorld;
import me.lluiscamino.multiversehardcore.models.HardcoreWorldConfiguration;
import me.lluiscamino.multiversehardcore.utils.MessageSender;
import me.lluiscamino.multiversehardcore.utils.WorldUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public final class CreateHardcoreWorldSubcommand extends MainSubcommand {

    private boolean createNether, createEnd;
    private String worldName;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        try {
            initProperties(sender, args);
            checkValidInput();
            attemptWorldCreation();
            makeWorldHardcore();
            sendSuccessMessage();
        } catch (HardcoreWorldCreationException e) {
            handleHardcoreWorldCreationException(e);
        } catch (InvalidCommandInputException e) {
            MessageSender.sendError(sender, e.getMessage());
        }
        return true;
    }

    @Override
    protected String getRequiredPermission() {
        return "multiversehardcore.create";
    }

    protected void initProperties(@NotNull CommandSender sender, @NotNull String[] args) {
        super.initProperties(sender, args);
        createNether = args.length > 3 && Boolean.parseBoolean(args[3]);
        createEnd = args.length > 4 && Boolean.parseBoolean(args[4]);
        worldName = args.length > 1 ? args[1] : "";
    }

    private void checkValidInput() throws InvalidCommandInputException {
        checkSenderHasPermission();
        checkCommandContainsWorldName();
        checkWorldDoesNotExist();
    }

    private void checkWorldDoesNotExist() throws InvalidCommandInputException {
        if (WorldUtils.worldExists(worldName)) {
            throw new InvalidCommandInputException("World " + worldName + " already exists");
        }
    }

    private void checkCommandContainsWorldName() throws InvalidCommandInputException {
        if (args.length < 2) {
            throw new InvalidCommandInputException(getWrongUsageMessage(HelpCommand.CREATE_COMMAND));
        }
    }

    private void attemptWorldCreation() throws InvalidCommandInputException {
        MessageSender.sendInfo(sender, "Starting creation of world(s)...");
        if (!createWorlds()) {
            throw new InvalidCommandInputException("World(s) could not be created!");
        }
    }

    private void makeWorldHardcore() throws HardcoreWorldCreationException {
        HardcoreWorldConfiguration configuration = getConfigurationFromArgs();
        HardcoreWorld.createHardcoreWorld(configuration);
    }

    private void sendSuccessMessage() {
        MessageSender.sendSuccess(sender, "World " + ChatColor.DARK_GREEN + worldName + ChatColor.GREEN + " created!");
    }

    private void handleHardcoreWorldCreationException(@NotNull HardcoreWorldCreationException exception) {
        MVWorldManagerFacade facade = plugin.getMVWorldFacade();
        MessageSender.sendError(sender, exception.getMessage());
        facade.deleteWorld(worldName);
        if (createNether) facade.deleteWorld(worldName + "_nether");
        if (createEnd) facade.deleteWorld(worldName + "_the_end");
    }

    private HardcoreWorldConfiguration getConfigurationFromArgs() {
        return new HardcoreWorldConfiguration(
                plugin.getServer().getWorld(args[1]),
                args.length > 9 ? plugin.getServer().getWorld(args[9]) : null,
                new Date(),
                args.length <= 5 || Boolean.parseBoolean(args[5]),
                args.length > 6 ? Long.parseLong(args[6]) : 30,
                args.length <= 2 || Boolean.parseBoolean(args[2]),
                args.length <= 7 || Boolean.parseBoolean(args[7]),
                args.length <= 8 || Boolean.parseBoolean(args[8]));
    }

    private boolean createWorlds() {
        return createOverworld() && createNetherIfNecessary() && createEndIfNecessary();
    }

    private boolean createOverworld() {
        return createHardcoreWorld(worldName, World.Environment.NORMAL);
    }

    private boolean createNetherIfNecessary() {
        MVWorldManagerFacade facade = plugin.getMVWorldFacade();
        if (createNether && !createHardcoreWorld(worldName + "_nether", World.Environment.NETHER)) {
            facade.deleteWorld(worldName);
            return false;
        }
        return true;
    }

    private boolean createEndIfNecessary() {
        MVWorldManagerFacade facade = plugin.getMVWorldFacade();
        if (createEnd && !createHardcoreWorld(worldName + "_the_end", World.Environment.THE_END)) {
            facade.deleteWorld(worldName);
            if (createNether) facade.deleteWorld(worldName + "_nether");
            return false;
        }
        return true;
    }

    private boolean createHardcoreWorld(@NotNull String worldName, @NotNull World.Environment environment) {
        try {
            attemptWorldCreation(worldName, environment);
            makeWorldAttributesHardcore(worldName);
            return true;
        } catch (HardcoreWorldCreationException e) {
            return false;
        }
    }

    private void attemptWorldCreation(@NotNull String worldName, @NotNull World.Environment environment)
            throws HardcoreWorldCreationException {
        MVWorldManagerFacade facade = plugin.getMVWorldFacade();
        if (!facade.createWorld(worldName, environment)) {
            throw new HardcoreWorldCreationException("World " + worldName + " could not be created");
        }
    }

    private void makeWorldAttributesHardcore(@NotNull String worldName) {
        MVWorldManagerFacade facade = plugin.getMVWorldFacade();
        facade.setWorldDifficulty(worldName, org.bukkit.Difficulty.HARD);
    }
}
