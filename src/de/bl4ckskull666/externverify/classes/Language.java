/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.externverify.classes;

import de.bl4ckskull666.externverify.EV;
import de.bl4ckskull666.externverify.utils.LoadLanguage;
import java.io.File;
import java.io.IOException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Bl4ckSkull666
 */
public class Language {
    public final FileConfiguration _fc;
    
    public Language() {
        File f = new File(EV.getPlugin().getDataFolder(), "language.yml");
        if(!f.exists())
            LoadLanguage.loadLanguage(f);
        _fc = YamlConfiguration.loadConfiguration(f);
    }
    
    /**
     *
     * @param s = CommandSender
     * @param path = Pfad in Language to the Message
     * @param def = Default Message to set is no Message in language,yml define
     */
    public void sendMessage(CommandSender s, String path, String def) {
        sendMessage(s, path, def, new String[] {""}, new String[] {""});
    }
    
    /**
     *
     * @param s = CommandSender
     * @param path = Pfad in Language to the Message
     * @param def = Default Message to set is no Message in language,yml define
     * @param search = String Array to search parameters
     * @param replace = Replace String Array
     */
    public void sendMessage(CommandSender s, String path, String def, String[] search, String[] replace) {
        if(!_fc.isString(path) && !_fc.isString(path + ".message")) {
            _fc.set(path, def);
            try {
                _fc.save(new File(EV.getPlugin().getDataFolder(), "language.yml"));
            } catch(IOException ex) {}
        }
        
        String msg = (_fc.isString(path + ".message")?_fc.getString(path + ".message"):_fc.getString(path));
        if(search.length > 0 && replace.length > 0 && search.length == replace.length) {
            for(int i = 0; i < search.length; i++)
                msg = msg.replace(search[i], replace[i]);
        }
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }
}
