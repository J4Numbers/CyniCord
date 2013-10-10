package uk.co.cynicode.CyniCord.DataGetters;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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

private Connection conn;
	
	public void startConnection(CyniCord plugin) {
		
		this.hostname = plugin.getConfig().getString( "CyniCord.database.hostname" );
		this.port = plugin.getConfig().getInt( "CyniCord.database.port" );
		this.database = plugin.getConfig().getString( "CyniCord.database.database" );
		this.prefix = plugin.getConfig().getString( "CyniCord.database.prefix" );
		
		this.username = plugin.getConfig().getString( "CyniCord.database.username" );
		this.password = plugin.getConfig().getString( "CyniCord.database.password" );
		
		if ( connect() != true ) {
			return;
		}
		
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

	public Map<String, String> getChannels() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getIRCChannels() {
		// TODO Auto-generated method stub
		return null;
	}

}
