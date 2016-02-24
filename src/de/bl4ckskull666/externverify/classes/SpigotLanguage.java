/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.externverify.classes;

import de.bl4ckskull666.externverify.utils.Utils;
import de.bl4ckskull666.externverify.EV;
import java.io.File;
import java.io.IOException;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Bl4ckSkull666
 */
public class SpigotLanguage extends Language {
    @Override
    public void sendMessage(CommandSender s, String path, String def) {
        sendMessage(s, path, def, new String[] {""}, new String[] {""});
    }
    
    @Override
    public void sendMessage(CommandSender s, String path, String def, String[] search, String[] replace) {
        if(!_fc.isString(path) && !_fc.isString(path + ".message")) {
            _fc.set(path, def);
            try {
                _fc.save(new File(EV.getPlugin().getDataFolder(), "language.yml"));
            } catch(IOException ex) {}
        }
        
        TextComponent msg = new TextComponent("");
        if(_fc.isString(path + ".message"))
            msg.addExtra(Utils.replaceAll(_fc.getString(path + ".message"), search, replace));
        else
            msg.addExtra(Utils.replaceAll(_fc.getString(path), search, replace));
        
        if(!(s instanceof Player)) {
            s.sendMessage(Utils.replaceAll(msg.getText(), search, replace));
            return;
        }
            
        if(_fc.isString(path + ".hover-msg")) {
            msg.setHoverEvent(
                new HoverEvent(
                    Utils.isHoverAction("show_" + _fc.getString(path + ".hover-type", "text"))?HoverEvent.Action.valueOf(("show_" + _fc.getString(path + ".hover-type", "text")).toUpperCase()):HoverEvent.Action.SHOW_TEXT, 
                    new ComponentBuilder(Utils.replaceAll(_fc.getString(path + ".hover-msg"), search, replace)).create()
                )
            );
        }
        
        if(_fc.isString(path + ".click-msg")) {
            msg.setClickEvent(
                new ClickEvent(
                    Utils.isClickAction(_fc.getString(path + ".click-type", "open_url"))?ClickEvent.Action.valueOf(_fc.getString(path + ".click-type", "open_url").toUpperCase()):ClickEvent.Action.OPEN_URL, 
                    Utils.replaceAll(_fc.getString(path + ".click-msg"), search, replace)
                )
            );
        }
        
        Player p = (Player)s;
        p.spigot().sendMessage(msg);
    }
}
