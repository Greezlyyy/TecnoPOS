package me.wiceh.tecnopos;

import me.wiceh.tecnopos.commands.POSCommand;
import me.wiceh.tecnopos.listeners.onInteract;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class TecnoPOS extends JavaPlugin {

    private static Economy econ = null;
    private static TecnoPOS instance;
    public String prefix;

    @Override
    public void onEnable() {
        if (!setupEconomy() ) {
            getLogger().severe(prefix + "§cPlugin disabilitato a causa del plugin 'Vault' non trovato.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        instance = this;
        prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"));
        getLogger().info(prefix + "§aPlugin abilitato!");
        loadConfig();
        registerCommands();
        registerListeners();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private void registerCommands() {
        getCommand("pos").setExecutor(new POSCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new onInteract(), this);
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }

    public static TecnoPOS getInstance() {
        return instance;
    }

    public static Economy getEconomy() {
        return econ;
    }
}
