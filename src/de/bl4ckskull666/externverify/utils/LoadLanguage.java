/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.externverify.utils;

import de.bl4ckskull666.externverify.EV;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Bl4ckSkull666
 */
public final class LoadLanguage {
    public static void loadLanguage(File f) {
        String msg = "";
        try {
            InputStream in = EV.getPlugin().getResource("language.yml");
            int c = -1;
            while((c = in.read()) != -1)
                msg += String.valueOf((char)c);
        } catch(IOException ex) {}
        
        if(!msg.isEmpty()) {
            try {
                FileConfiguration fcLang = YamlConfiguration.loadConfiguration(f);
                fcLang.loadFromString(msg);
                fcLang.save(f);
            } catch(IOException | InvalidConfigurationException ex) {
                
            }
        } else {
            EV.getPlugin().getLogger().log(Level.WARNING, "language.yml is empty");
        }
    }
    
    public static String getResourcesFile() {
        CodeSource src = EV.class.getProtectionDomain().getCodeSource();
        if(src != null) {
            try {
                URL jar = src.getLocation();
                ZipInputStream zip = new ZipInputStream( jar.openStream());
                ZipEntry ze;
                
                while((ze = zip.getNextEntry()) != null) {
                    String entryName = ze.getName();
                    if(entryName.equalsIgnoreCase("language.yml"))
                        return entryName;
                }
            } catch (IOException ex) {
                EV.getPlugin().getLogger().log(Level.WARNING, "Error : ", ex);
            }
        }
        return "";
    }
}
