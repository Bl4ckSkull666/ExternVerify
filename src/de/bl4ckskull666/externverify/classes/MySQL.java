/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  yamlapi.ConfigurationSection
 */
package de.bl4ckskull666.externverify.classes;

import de.bl4ckskull666.externverify.EV;
import de.bl4ckskull666.externverify.utils.Utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;

public final class MySQL {
    public static boolean hasMySQLDriver() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return true;
        } catch (ClassNotFoundException t) {
            EV.debug("Can't find MySQL Driver!", t.getLocalizedMessage(), Level.WARNING);
            return false;
        }
    }
    
    public static Connection getConnection(ConfigurationSection cs) {
        if(!cs.isString("host") || !cs.isInt("port") || !cs.isString("user") || !cs.isString("pass") || !cs.isString("data"))
            return null;
        
        try {
            return DriverManager.getConnection("jdbc:mysql://" + cs.getString("host") + ":" + cs.getString("port") + "/" + cs.getString("data"), cs.getString("user"), cs.getString("pass"));
        } catch (SQLException e) {
            EV.debug("Can't connect to MySQL Server!", e.getLocalizedMessage(), Level.WARNING);
            return null;
        }
    }
    
    public static boolean executeInsUpDelQuery(ConfigurationSection cs, String[] search, String[] replace) {
        if(!hasMySQLDriver())
            return false;
        
        if(!cs.isConfigurationSection("connect")) {
            EV.debug("Missing connect Configuration section in the config file.", "", Level.WARNING);
            return false;
        }
        
        Connection con = getConnection(cs.getConfigurationSection("connect"));
        if(con == null)
            return false;
        
        boolean bol = true;
        if(cs.isString("query")) {
            if(!executeInsUpDelQuery(con, Utils.replaceAll(cs.getString("query"), search, replace)))
                bol = false;
        } else if(cs.isList("query")) {
            for(String query: cs.getStringList("query")) {
                if(!executeInsUpDelQuery(con, Utils.replaceAll(query, search, replace)))
                    bol = false;
            }
        }
        close(con);
        return bol;
    }

    private static boolean executeInsUpDelQuery(Connection con, String query) {
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            EV.debug("Error on running insert/update/delete query " + query + ".", e.getLocalizedMessage(), Level.WARNING);
            return false;
        } finally {
            return true;
        }
    }
    
    public static String executeSingleSelectQuery(ConfigurationSection cs, String[] search, String[] replace) {
        if(!hasMySQLDriver())
            return "";
        
        if(!cs.isConfigurationSection("connect")) {
            EV.debug("Missing connect Configuration section in the config file.", "", Level.WARNING);
            return "";
        }
        
        Connection con = getConnection(cs.getConfigurationSection("connect"));
        if(con == null)
            return "";
        
        String tmp = "";
        if(cs.isString("query"))
            tmp = executeSingleSelectQuery(con, Utils.replaceAll(cs.getString("query"), search, replace));
        else if(cs.isList("query")) {
            for(String query: cs.getStringList("query"))
                tmp += (tmp.isEmpty()?"":"|") + executeSingleSelectQuery(con, Utils.replaceAll(query, search, replace));
        }
        close(con);
        return tmp;
    }

    private static String executeSingleSelectQuery(Connection con, String query) {
        String tmp = "";
        try {
            PreparedStatement statement = con.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            if(rs.next())
                tmp = String.valueOf(rs.getObject(1));
            rs.close();
            statement.close();
            EV.getPlugin().getLogger().log(Level.INFO, "Use: " + query + " - Result " + tmp);
        } catch (SQLException e) {
            EV.debug("Error on running single select query " + query + ".", e.getLocalizedMessage(), Level.WARNING);
        }
        return tmp;
    }
    
    public static ArrayList<Map<String, String>> executeMultiSelectQuery(ConfigurationSection cs, String[] search, String[] replace) {
        if(!hasMySQLDriver())
            return null;
        
        if(!cs.isConfigurationSection("connect")) {
            EV.debug("Missing connect Configuration section in the config file.", "", Level.WARNING);
            return null;
        }
        
        Connection con = getConnection(cs.getConfigurationSection("connect"));
        if(con == null)
            return null;
        
        ArrayList<Map<String, String>> tmp = new ArrayList<>();
        if(cs.isString("query"))
            tmp = executeMultiSelectQuery(con, Utils.replaceAll(cs.getString("query"), search, replace));
        else if(cs.isList("query")) {
            for(String query: cs.getStringList("query"))
                tmp.addAll(executeMultiSelectQuery(con, Utils.replaceAll(query, search, replace)));
        }
        close(con);
        return tmp;
    }

    private static ArrayList<Map<String, String>> executeMultiSelectQuery(Connection con, String query) {
        ArrayList<Map<String, String>> tmp = new ArrayList<>();
        try {
            PreparedStatement statement = con.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int cc = rsmd.getColumnCount();
            while(rs.next()) {
                Map<String, String> t = new HashMap<>();
                for(int r = 1; r <= cc; r++)
                    t.put(rsmd.getColumnName(r), String.valueOf(rs.getObject(r)));
                tmp.add(t);
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            EV.debug("Error on running multiple select query " + query + ".", e.getLocalizedMessage(), Level.WARNING);
        }
        return tmp;
    }

    public static void close(Connection con) {
        try {
            con.close();
        } catch (SQLException e) {
            EV.debug("Error on close MySQL connection.", e.getLocalizedMessage(), Level.WARNING);
        }
    }
}

