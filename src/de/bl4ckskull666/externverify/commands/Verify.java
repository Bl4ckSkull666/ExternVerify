/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.externverify.commands;

import de.bl4ckskull666.externverify.EV;
import de.bl4ckskull666.externverify.utils.Utils;
import java.util.ArrayList;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Bl4ckSkull666
 */
public class Verify implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        ArrayList<OfflinePlayer> ops = new ArrayList<>();
        if(a.length > 0) {
            for(String arg: a) {
                if(arg.equalsIgnoreCase("reload") && s.hasPermission("externverify.admin")) {
                    EV.reload();
                    EV.getLang().sendMessage(s, "reloaded", "Reloaded successful Configuration, Language and Errors");
                    return true;
                }
                
                if(arg.equalsIgnoreCase("errors") && s.hasPermission("externverify.admin")) {
                    EV.getErrors().sendErrors(s);
                    return true;
                }
                    
                if((arg.equalsIgnoreCase("me") || arg.equalsIgnoreCase("ich") || arg.equalsIgnoreCase("mich")) && s instanceof Player) {
                    ops.add((Player)s);
                    continue;
                }
                    
                OfflinePlayer op = Utils.getOfflinePlayer(arg);
                if(op != null && s.hasPermission("extremverify.others"))
                    ops.add(op);
            }
        }
            
        if(s instanceof Player && ops.isEmpty())
            ops.add((Player)s);
          
        if(ops.isEmpty()) {
            EV.getLang().sendMessage(s, "need-players", "You have forget to tell us player names.");
            return true;
        }
            
        Bukkit.getScheduler().runTaskAsynchronously(EV.getPlugin(), new Utils.runProcess(s, ops));
        return true;
    }
}
