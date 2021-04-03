package net.trollyloki.minigames.library;

import net.trollyloki.minigames.library.commands.PartyCommand;
import net.trollyloki.minigames.library.managers.MiniGameManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MiniGameLibraryPlugin extends JavaPlugin {

    private MiniGameManager manager;

    @Override
    public void onEnable() {

        manager = new MiniGameManager(this);
        getServer().getPluginManager().registerEvents(manager, this);
        getCommand("party").setExecutor(new PartyCommand(manager));

    }

    /**
     * Gets the mini-game manager for this plugin
     *
     * @return Mini-game manager
     */
    public MiniGameManager getMiniGameManager() {
        return manager;
    }

}
