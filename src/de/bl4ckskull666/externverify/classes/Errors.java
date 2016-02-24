/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.externverify.classes;

import de.bl4ckskull666.externverify.EV;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Bl4ckSkull666
 */
public class Errors {
    private final static HashMap<UUID, ArrayList<Integer>> _seen = new HashMap<>();
    private final static Map<Integer, Error> _errors = new TreeMap<>();
    
    public void addError(String msg, String localization, Level level) {
        Error er = new Error(msg, localization, level);
        _errors.put((_errors.size()+1), er);
    }
    
    public void sendErrors(UUID uuid, boolean all) {
        Player p = Bukkit.getPlayer(uuid);
        if(p == null)
            return;
        
        if(!p.isOp() && p.hasPermission("externverify.admin"))
            return;
        
        ArrayList<Integer> seen = getSendedErrors(uuid);
        for(Map.Entry<Integer, Error> me: _errors.entrySet()) {
            if(seen.contains(me.getKey()) && !all)
                continue;
            
            EV.getLang().sendMessage(p, "info-error", "Error ID. %message%\\nLocalization: %local%", new String[] {"%id%","%message%","%local%"}, new String[] {String.valueOf(me.getKey()), me.getValue().getMessage(), me.getValue().getLocalization()});
            setSeen(uuid, me.getKey());
        }
    }
    
    private void setSeen(UUID uuid, int id) {
        if(!_seen.containsKey(uuid))
            _seen.put(uuid, new ArrayList());
        _seen.get(uuid).add(id);
    }
    
    private ArrayList<Integer> getSendedErrors(UUID uuid) {
        if(_seen.containsKey(uuid))
            return _seen.get(uuid);
        return new ArrayList();
    }
    
    public class Error {
        private final String _msg;
        private final String _localization;
        private final Level _level;

        public Error(String msg, String localization, Level level) {
            _msg = msg;
            _localization = localization;
            _level = level;
        }
        
        public String getMessage() {
            return _msg;
        }
        
        public String getLocalization() {
            return _localization;
        }
        
        public Level getLevel() {
            return _level;
        }
    }
}
