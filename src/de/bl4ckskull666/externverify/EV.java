package de.bl4ckskull666.externverify;

import de.bl4ckskull666.externverify.classes.Errors;
import de.bl4ckskull666.externverify.classes.Language;
import de.bl4ckskull666.externverify.classes.SpigotLanguage;
import de.bl4ckskull666.externverify.commands.Verify;
import de.bl4ckskull666.externverify.listeners.PlayerJoin;
import java.util.logging.Level;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Bl4ckSkull666
 */
public class EV extends JavaPlugin {

    @Override
    public void onEnable() {
        _plugin = this;
        if(!getDataFolder().exists()) {
            getDataFolder().mkdir();
            getConfig().options().copyDefaults(true);
        }
        saveConfig();
        
        getCommand("verify").setExecutor(new Verify());
        if(getBoolean("use-auto-verify-on-join", false))
            getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        
        loadLanguage();
        _error = new Errors();
    }
    
    private void loadLanguage() {
        if(getServer().getVersion().toLowerCase().contains("spigot")) {
            SpigotLanguage lang = new SpigotLanguage();
            _lang = (Language)lang;
            debug("Using Spigot Language support.", "", Level.INFO);
        } else 
            _lang = new Language();
    }
    
    public String getString(String path, String def) {
        if(!getConfig().isString(path)) {
            getConfig().set(path, def);
            saveConfig();
        }
        return getConfig().getString(path);
    }
    
    public int getInt(String path, int def) {
        if(!getConfig().isInt(path)) {
            getConfig().set(path, def);
            saveConfig();
        }
        return getConfig().getInt(path);
    }
    
    public double getDouble(String path, double def) {
        if(!getConfig().isDouble(path)) {
            getConfig().set(path, def);
            saveConfig();
        }
        return getConfig().getDouble(path);
    }
    
    public boolean getBoolean(String path, boolean def) {
        if(!getConfig().isBoolean(path)) {
            getConfig().set(path, def);
            saveConfig();
        }
        return getConfig().getBoolean(path);
    }
    
    private static EV _plugin = null;
    public static EV getPlugin() {
        return _plugin;
    }
    
    private static Language _lang = null;
    public static Language getLang() {
        return _lang;
    }
    
    private static Errors _error = null;
    public static Errors getErrors() {
        return _error;
    }
    
    public static void reload() {
        _plugin.reloadConfig();
        _plugin.loadLanguage();
        _error = new Errors();
    }
    
    public static void debug(String msg, String localization, Level lv) {
        if(_plugin.getBoolean("debug", false)) {
            _plugin.getLogger().log(lv, "[DEBUG1] {0}", msg);
            if(!localization.isEmpty())
                _plugin.getLogger().log(lv, "[DEBUG2] {0}", localization);
        }
        
        if(lv.equals(Level.WARNING))
            _error.addError(msg, localization, lv);
    }
}
