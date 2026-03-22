package me.lluiscamino.multiversehardcore;

import me.lluiscamino.multiversehardcore.commands.HelpCommand;
import me.lluiscamino.multiversehardcore.commands.MainCommand;
import me.lluiscamino.multiversehardcore.commands.MainCommandTabCompleter;
import me.lluiscamino.multiversehardcore.events.PlayerChangeOfWorld;
import me.lluiscamino.multiversehardcore.events.PlayerDeath;
import me.lluiscamino.multiversehardcore.events.PlayerJoin;
import me.lluiscamino.multiversehardcore.files.HardcoreWorldsList;
import me.lluiscamino.multiversehardcore.utils.MV5WorldManagerFacade;
import me.lluiscamino.multiversehardcore.utils.MVWorldManagerFacade;
import me.lluiscamino.multiversehardcore.utils.MessageSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.mvplugins.multiverse.core.MultiverseCoreApi;

public class MultiverseHardcore extends JavaPlugin {

    private static MultiverseHardcore instance;
    private MVWorldManagerFacade mvWorldFacade;

    public static MultiverseHardcore getInstance() {
        return instance;
    }

    public MVWorldManagerFacade getMVWorldFacade() {
        return mvWorldFacade;
    }

    public void setMVWorldFacade(MVWorldManagerFacade facade) {
        this.mvWorldFacade = facade;
    }

    @Override
    public void onEnable() {
        instance = this;
        // Skip if facade was already injected (test environment)
        if (mvWorldFacade == null) {
            loadMultiverseCore();
        }
        saveDefaultConfig();
        loadMessagesPrefix();
        loadEventListeners();
        loadCommands();
        scheduleWorldCleanUp();
    }

    private void loadMultiverseCore() {
        try {
            mvWorldFacade = new MV5WorldManagerFacade(MultiverseCoreApi.get().getWorldManager());
        } catch (IllegalStateException e) {
            getLogger().warning("Multiverse-Core not yet available during onEnable. " +
                    "Ensure Multiverse-Core is listed as a dependency. " +
                    "(In test environments inject via setMVWorldFacade.)");
        }
    }

    private void loadMessagesPrefix() {
        String prefix = getConfig().getString("prefix");
        if (prefix != null) {
            MessageSender.setPrefix(prefix);
        }
    }

    private void loadEventListeners() {
        Listener[] listeners = {new PlayerDeath(), new PlayerChangeOfWorld(), new PlayerJoin()};
        for (Listener listener : listeners) {
            loadEventListener(listener);
        }
    }

    private void loadEventListener(Listener eventListener) {
        getServer().getPluginManager().registerEvents(eventListener, this);
    }

    private void loadCommands() {
        PluginCommand mainCommand = getCommand("mvhc");
        PluginCommand helpCommand = getCommand("mvhchelp");
        if (mainCommand != null && helpCommand != null) {
            mainCommand.setExecutor(new MainCommand());
            mainCommand.setTabCompleter(new MainCommandTabCompleter());
            helpCommand.setExecutor(new HelpCommand());
        } else {
            throw new RuntimeException("Multiverse-Hardcore Command not found!");
        }
    }

    private void scheduleWorldCleanUp() {
        int cleanWorldsTicks = getConfig().getInt("clean_worlds_ticks");
        getServer().getScheduler().runTaskLater(this, HardcoreWorldsList.instance::cleanWorlds, cleanWorldsTicks);
    }
}
