package uk.co.cynicode.CyniCord.DataGetters;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import uk.co.cynicode.CyniCord.CyniCord;

public class MySQLDataGetter implements IDataGetter {

private String hostname = "";
private String database = "";
private String prefix = "";
private int port = 0;

private String username = "";
private String password = "";

private boolean SQL = false;

private Map<String, String> loadedChannels = new HashMap<String, String>();

private Connection conn;
	
	public boolean startConnection(CyniCord plugin) {
		
		this.hostname = plugin.getConfig().getString( "CyniCord.database.hostname" );
		this.port = plugin.getConfig().getInt( "CyniCord.database.port" );
		this.database = plugin.getConfig().getString( "CyniCord.database.database" );
		this.prefix = plugin.getConfig().getString( "CyniCord.database.prefix" );
		
		this.username = plugin.getConfig().getString( "CyniCord.database.username" );
		this.password = plugin.getConfig().getString( "CyniCord.database.password" );
		
		if ( connect() != true ) {
			return false;
		}
		
		SQL = true;
		return true;
	}
	
	/**
	 * Actually connect to the database with the information we've been given
	 * @return true upon completion
	 */
	private boolean connect() {
		String sqlUrl = String.format("jdbc:mysql://%s:%s/%s", hostname, port, database);
		
		Properties sqlStr = new Properties();
		sqlStr.put("user", username);
		sqlStr.put("password", password);
		sqlStr.put("autoReconnect", "true");
		CyniCord.printDebug("H:"+hostname+" P:"+port+" D:"+database+" U:"+username+" Pass:"+password );
		try {
			conn = DriverManager.getConnection(sqlUrl, sqlStr);
		} catch (SQLException e) {
			CyniCord.printSevere("A MySQL connection could not be made!");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean endConnection() {
		
		if ( SQL == true ) {
			try {
				conn.close();
			} catch ( SQLException e ) {
				CyniCord.printSevere( "Something went wrong in shutting down the connection!" );
				e.printStackTrace();
			}
		}
		
		return true;
		
	}
	
	/**
	 * Generate a map of all the channels in the various tables
	 * @return the map of all the channels along with their IRC channels
	 */
	public Map<String, String> getChannels() {
		Map<String, String> channels = new HashMap<String, String>();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT `channel_name`,`channel_irc_name`,`channel_irc_pass` FROM `"+prefix+"channels`");
			ResultSet rs = ps.executeQuery();
			while ( rs.next() ) {
				String name = rs.getString(1);
				String irc = rs.getString(2);
				String ircPass = rs.getString(3);
				
				try {
					loadedChannels.put( irc, ircPass );
					channels.put( name.toLowerCase(), irc );
				} catch (NullPointerException e) {
					CyniCord.printSevere("Null Pointer found!");
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			CyniCord.printSevere("Channel loading has failed!");
			e.printStackTrace();
			CyniCord.killPlugin();
		}
		return channels;
	}

	public Map<String, String> getIRCChannels() {
		Map<String, String> channels = new HashMap<String, String>();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT `channel_name`,`channel_irc_name` FROM `"+prefix+"channels`");
			ResultSet rs = ps.executeQuery();
			while ( rs.next() ) {
				String name = rs.getString(1);
				String irc = rs.getString(2);
				
				try {
					channels.put( irc, name.toLowerCase() );
				} catch (NullPointerException e) {
					CyniCord.printSevere("Null Pointer found!");
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			CyniCord.printSevere("Channel loading has failed!");
			e.printStackTrace();
			CyniCord.killPlugin();
		}
		return channels;
	}

	public Map<String, String> loadChannels() {
		return loadedChannels;
	}
}
