package de.bl4ckskull666.externverify.utils;

import de.bl4ckskull666.externverify.EV;
import de.bl4ckskull666.externverify.classes.MySQL;
import java.util.ArrayList;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Bl4ckSkull666
 */
public final class Utils {
    public static boolean isHoverAction(String str) {
        try {
            HoverEvent.Action a = HoverEvent.Action.valueOf(str.toUpperCase());
            return (a != null);
        } catch(Exception ex) {
            return false;
        }
    }
    
    public static boolean isClickAction(String str) {
        try {
            ClickEvent.Action a = ClickEvent.Action.valueOf(str.toUpperCase());
            return (a != null);
        } catch(Exception ex) {
            return false;
        }
    }
    
    public static String replaceAll(String msg, String[] search, String[] replace) {
        if(search.length > 0 && replace.length > 0 && search.length == replace.length) {
            for(int i = 0; i < search.length; i++)
                msg = msg.replace(search[i], replace[i]);
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
    
    public static OfflinePlayer getOfflinePlayer(String name) {
        try {
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            return op;
        } catch(NoClassDefFoundError ex) {
            for(OfflinePlayer op: Bukkit.getOfflinePlayers()) {
                if(op.getName().equalsIgnoreCase(name))
                    return op;
            }
        }
        return null;
    }
    
    public static class runProcess implements Runnable {
        private final CommandSender _s;
        private final ArrayList<OfflinePlayer> _ops;
        
        public runProcess(CommandSender s, ArrayList<OfflinePlayer> ops) {
            _s = s;
            _ops = ops;
        }
        
        @Override
        public void run() {
            if(!EV.getPlugin().getConfig().isConfigurationSection("check-database")) {
                EV.debug("Missing check-database path in config.", "", Level.WARNING);
                EV.getLang().sendMessage(_s, "error-in-config", "Missing a configuration path. ( %path% )", new String[] {"%path%"}, new String[] {"check-database"});
                return;
            }
            
            ConfigurationSection cs = EV.getPlugin().getConfig().getConfigurationSection("check-database");
            String[] search = new String[] {"%name%", "%uuid%"};
            if(cs.isString("must-back")) {
                for(OfflinePlayer op: _ops) {
                    String[] replace = new String[] {op.getName(), op.getUniqueId().toString()};
                    String getback = MySQL.executeSingleSelectQuery(cs, search, replace);
                    if(getback.equalsIgnoreCase(cs.getString("must-back"))) {
                        if(EV.getPlugin().getConfig().isConfigurationSection("is-successful")) {
                            Bukkit.getScheduler().runTaskAsynchronously(EV.getPlugin(), new runProcessAfter(_s, op, EV.getPlugin().getConfig().getConfigurationSection("is-successful")));
                        } else {
                            if(_s.getName().equalsIgnoreCase(op.getName()))
                                EV.getLang().sendMessage(_s, "successfull-back-self", "Successful getting back what's needed to verify you.", search, replace);
                            else
                                EV.getLang().sendMessage(_s, "successfull-back-other", "Successful getting back what's needed for User %name%", search, replace);
                        }
                    } else {
                        //Wrong Result
                        if(_s.getName().equalsIgnoreCase(op.getName()))
                            EV.getLang().sendMessage(_s, "failed-result-self", "Getting a Wrong result back for you.", search, replace);
                        else
                            EV.getLang().sendMessage(_s, "failed-result-other", "Wrong Result for User %name%", search, replace);
                    }
                }
            } else {
                for(OfflinePlayer op: _ops) {
                    String[] replace = new String[] {op.getName(), op.getUniqueId().toString()};
                    if(MySQL.executeInsUpDelQuery(cs, search, replace)) {
                        if(_s.getName().equalsIgnoreCase(op.getName())) 
                            EV.getLang().sendMessage(_s, "successful-do-self", "Successful running Query for Verificate you.");
                        else 
                            EV.getLang().sendMessage(_s, "successful-do-other", "Successful running Query to Verificate User %name%", search, replace);
                    } else {
                        if(_s.getName().equalsIgnoreCase(op.getName()))
                            EV.getLang().sendMessage(_s, "failed-do-self", "Failed to run Verification Query.");
                        else
                            EV.getLang().sendMessage(_s, "failed-do-other", "Failed to run Verification Query for User %name%", search, replace);
                    }
                }
            }
        }
    }
    
    
    public static class runProcessAfter implements Runnable {
        private final CommandSender _s;
        private final OfflinePlayer _op;
        private final ConfigurationSection _cs;
        
        public runProcessAfter(CommandSender s, OfflinePlayer op, ConfigurationSection cs) {
            _s = s;
            _op = op;
            _cs = cs;
        }
        
        @Override
        public void run() {
            boolean isError = false;
            if(_cs.isConfigurationSection("mysql")) {
                if(!MySQL.executeInsUpDelQuery(_cs.getConfigurationSection("mysql"), new String[] {"%name%", "%uuid%"}, new String[] {_op.getName(), _op.getUniqueId().toString()}))
                    isError = true;
            }
            
            if(_cs.isString("commands") || _cs.isList("commands")) {
                if(_cs.isString("commands")) {
                    if(!runCommand(Utils.replaceAll(_cs.getString("commands"), new String[] {"%name%", "%uuid%"}, new String[] {_op.getName(), _op.getUniqueId().toString()})))
                        isError = true;
                } else {
                    for(String query: _cs.getStringList("commands")) {
                        if(!runCommand(Utils.replaceAll(query, new String[] {"%name%", "%uuid%"}, new String[] {_op.getName(), _op.getUniqueId().toString()})))
                            isError = true;
                    }
                }
            }
            
            if(isError) {
                if(_s.getName().equalsIgnoreCase(_op.getName()))
                    EV.getLang().sendMessage(_s, "failed-end-self", "Failed to close your Verification.");
                else
                    EV.getLang().sendMessage(_s, "failed-end-other", "Failed to close Verification for User %name%", new String[] {"%name%", "%uuid%"}, new String[] {_op.getName(), _op.getUniqueId().toString()});
            } else {
                if(_s.getName().equalsIgnoreCase(_op.getName()))
                    EV.getLang().sendMessage(_s, "successful-end-self", "Verification was successful.");
                else
                    EV.getLang().sendMessage(_s, "successful-end-other", "The Verification of User %name% was successful.", new String[] {"%name%", "%uuid%"}, new String[] {_op.getName(), _op.getUniqueId().toString()});
            }
        }
    }
    
    private static boolean runCommand(String command) {
        return Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}