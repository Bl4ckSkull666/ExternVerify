/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.externverify.listeners;

import de.bl4ckskull666.externverify.EV;
import de.bl4ckskull666.externverify.utils.Utils;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author Bl4ckSkull666
 */
public class PlayerJoin implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        EV.getErrors().sendErrors(e.getPlayer().getUniqueId(), false);
        if(!EV.getPlugin().getBoolean("use-auto-verify-on-join", false))
            return;
        
        if(!(e.getPlayer() instanceof CommandSender))
            return;
        
        ArrayList<OfflinePlayer> ops = new ArrayList<>();
        ops.add((OfflinePlayer)e.getPlayer());
        CommandSender cs = (CommandSender)e.getPlayer();
        
        Bukkit.getScheduler().runTaskAsynchronously(EV.getPlugin(), new Utils.runProcess(cs, ops));
    }
}
