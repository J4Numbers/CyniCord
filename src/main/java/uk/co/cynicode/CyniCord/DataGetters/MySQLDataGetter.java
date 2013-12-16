/**
 * Copyright 2013 CyniCode (numbers@cynicode.co.uk).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

/**
 * A class to deal with all the MySQL data options that
 * are available for our nice viewers at home
 * 
 * @author CyniCode
 */
public class MySQLDataGetter implements IDataGetter {
	
	/**
	 * The String responsible for holding the hostname of the database
	 * that we're connecting to.
	 */
	private String hostname;
	
	/**
	 * The string responsible for the actual database that we're
	 * using to gather information about all our goodies.
	 */
	private String database;
	
	/**
	 * The string we're using to tell us what prefix all our tables
	 * have. That way we can check whether they exist or not.
	 */
	private String prefix;
	
	/**
	 * What port is our database on?
	 */
	private int port;
	
	
	
	/**
	 * What username is the database allocated to?
	 */
	private String username;
	
	/**
	 * And what password does that username have?
	 */
	private String password;
	
	
	
	/**
	 * Let's just have a field that tells the nice people whether
	 * or not we are currently connected to the database or not
	 */
	private boolean sql = false;
	
	
	
	/**
	 * These are all the channels that are in the database at any
	 * one read-time and their passwords
	 */
	private Map<String, String> loadedChannels = new HashMap<String, String>();
	
	/**
	 * These are all the channels in the database that are listed
	 * and their ircChannel counterparts
	 */
	private Map<String, String> cyniChannels = new HashMap<String, String>();
	
	/**
	 * This is the reverse of the cyniChannels map that will let
	 * us get the reverse lookups
	 */
	private Map<String, String> ircChannels = new HashMap<String, String>();
	
	
	
	/**
	 * Because we couldn't have an SQL class without some SQL magics
	 */
	private Connection conn;
	
	/**
	 * This constructor gets all the data from the configs and sets 
	 * up all of the connections we need in order to get information
	 * from the database.
	 * @param plugin : An instance of the plugin so we can grab the 
	 *  config informations
	 * @throws SQLException if the connection was unsuccessful
	 */
	//public boolean startConnection(CyniCord plugin) {
	public MySQLDataGetter( CyniCord plugin ) throws SQLException, Exception {
		
		//Set all the variables according to the configs
		this.hostname = (plugin.getConfig().getString( "CyniCord.database.hostname" ));
		this.port = (plugin.getConfig().getInt( "CyniCord.database.port" ));
		this.database = (plugin.getConfig().getString( "CyniCord.database.database" ));
		this.prefix = (plugin.getConfig().getString( "CyniCord.database.prefix" ));
		
		this.username = (plugin.getConfig().getString( "CyniCord.database.username" ));
		this.password = (plugin.getConfig().getString( "CyniCord.database.password" ));
		
		//Then try and connect to the server.
		try { 
			
			connect();
			findAllChannels();
			
		} catch ( SQLException e ) {
			
			//Something in the connect method went wonky
			throw e;
			
		} catch ( Exception ex ) {
			
			//Something in the findAllChannels method went wonky
			throw ex;
			
		}
		
		//And since we've got this far, tell the class that we have 
		// gained connection status
		this.sql = (true);
		
	}
	
	/**
	 * Actually connect to the database with the information we've been given
	 * @return true upon completion
	 * @throws SQLException if the database connection was unsuccessful
	 */
	private void connect() throws SQLException {
		
		//Create a property string so that we can establish a
		// JDBC connection to the database
		String sqlUrl = String.format("jdbc:mysql://%s:%s/%s", getHostname(), getPort(), getDatabase());
		
		//Then make the SQL properties
		Properties sqlStr = new Properties();
		
		//And fill them with information
		sqlStr.put("user", getUsername());
		sqlStr.put("password", getPassword());
		sqlStr.put("autoReconnect", "true");
		
		//Debug all the information that we have managed to aquire
		CyniCord.printDebug("H:"+getHostname()+" P:"+getPort()+" D:"+getDatabase()
			+" U:"+getUsername()+" Pass:"+getPassword() );
		
		//Then connect to the database
		try {
			setConn(DriverManager.getConnection(sqlUrl, sqlStr));
		} catch (SQLException e) {
			
			//If this is triggered, then bugger.
			CyniCord.printSevere("A MySQL connection could not be made!");
			throw e;
			
		}
	}
	
	/**
	 * Basically, kill the connection if it exists
	 */
	public void endConnection() {
		
		//If the connection is there...
		if ( isSQL() == true ) {
			
			//Then kill it
			try {
				getConn().close();
			} catch ( SQLException e ) {
				CyniCord.printSevere( "Something went wrong in shutting down the connection!" );
				e.printStackTrace();
			}
			
		}
		
		//Otherwise, we don't need to do anything other than
		// tell the class that we're no-longer connected.
		setSQL( false );
		
	}
	
	/**
	 * Generate a map of all the channels in the various tables
	 * @throws SQLException if there was a failure
	 */
	public final void findAllChannels() throws Exception {
		
		//Make some dummy maps to hold all the information we find in
		// this check of the database.
		Map<String, String> loadMap = new HashMap<String, String>();
		Map<String, String> cyniMap = new HashMap<String, String>();
		Map<String, String>  ircMap = new HashMap<String, String>();
		
		//Then try and create a new statement
		try {
			
			//By making the statement first
			PreparedStatement ps = getConn()
				.prepareStatement( String.format( "SELECT `channel_name`,"
					+ "`channel_irc_name`,"
					+ "`channel_irc_pass` "
					+ "FROM `%schannels`", 
					getPrefix() ) );
			
			//Then executing it
			ResultSet rs = ps.executeQuery();
			
			//And reading everything from the result set
			while ( rs.next() ) {
				
				//Get some basic information and put it into
				// some slightly pointless strings that just
				// make the program easier to read.
				String name = rs.getString(1);
				String irc = rs.getString(2);
				String ircPass = rs.getString(3);
				
				//And try to put them all into the map in some
				// shape or form.
				try {
					
					//First, we want them in all the channels
					// that we have listed as activated...
					loadMap.put( irc, ircPass );
					
					//Then we want to put them into the map
					// for all those which have connections
					cyniMap.put( name.toLowerCase(), irc );
					
					//Then we want to reverse that map we just made
					ircMap.put( irc, name.toLowerCase() );
					
				} catch (NullPointerException e) {
					
					//Then there might be a glitch...
					CyniCord.printSevere("Null Pointer found!");
					e.printStackTrace();
					//throw e;
					
				}
				
			}
			
		} catch (SQLException e) {
			
			//Or SQL itself could go down the drain completely.
			CyniCord.printSevere("Channel loading has failed!");
			e.printStackTrace();
			throw e;
			
		}
		
		//Now completely overwrite the old values with these
		// new ones that we've loaded in now
		setLoadedChannels( loadMap );
		setCyniChannels(   cyniMap );
		setIrcChannels(    ircMap  );
		
	}
	
	/**
	 * Because interfacing gets screwy with having a subclass which
	 * implements Runnable, we might as well just have a middle-method
	 * which does all the work for us... it just gets us a new instance
	 * of the class in any case.
	 * @return the new instance of the Runnable class
	 */
	public Runnable returnBooster() {
		return new boostConnection();
	}
	
	/**
	 * A class for boosting the database connection
	 */
	public class boostConnection implements Runnable {
		
		/**
		 * A running thing to run the codes
		 */
		public void run() {
			try {
				
				//Now, do another run on getting all the channels
				// from the database which should have been
				// flushed by the CyniChat plugin
				findAllChannels();
				
				//Once we've done that, compare all the old channels
				// with the new ones that we've just found.
				CyniCord.PBot
					.compareChannels( getLoadedChannels() );
				
				//Then let the server know that we did things
				CyniCord.printInfo( "Updated the IRC bot with "
					+ "the latest information.");
				
			} catch ( Exception e ) {
				
				//Well shit... kill everything.
				CyniCord.printSevere( e.getMessage() );
				endConnection();
				CyniCord.killPlugin();
				
			}
		}
		
	};
	
	/**
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}
	
	/**
	 * @param hostname the hostname to set
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	/**
	 * @return the database
	 */
	public String getDatabase() {
		return database;
	}
	
	/**
	 * @param database the database to set
	 */
	public void setDatabase(String database) {
		this.database = database;
	}
	
	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}
	
	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * @return the SQL
	 */
	public boolean isSQL() {
		return sql;
	}
	
	/**
	 * @param sql the SQL to set
	 */
	public void setSQL( boolean sql ) {
		this.sql = sql;
	}
	
	/**
	 * @return the loadedChannels
	 */
	public Map<String, String> getLoadedChannels() {
		return loadedChannels;
	}

	/**
	 * @param loadedChannels the loadedChannels to set
	 */
	public void setLoadedChannels(Map<String, String> loadedChannels) {
		this.loadedChannels = loadedChannels;
	}
	
	/**
	 * @return the cyniChannels
	 */
	public Map<String, String> getCyniChannels() {
		return cyniChannels;
	}
	
	/**
	 * @param cyniChannels the cyniChannels to set
	 */
	public void setCyniChannels( Map<String, String> cyniChannels ) {
		this.cyniChannels = cyniChannels;
	}
	
	/**
	 * @return the ircChannels
	 */
	public Map<String, String> getIrcChannels() {
		return ircChannels;
	}
	
	/**
	 * @param ircChannels the ircChannels to set
	 */
	public void setIrcChannels( Map<String, String> ircChannels ) {
		this.ircChannels = ircChannels;
	}
	
	/**
	 * @return the conn
	 */
	public Connection getConn() {
		return conn;
	}
	
	/**
	 * @param conn the conn to set
	 */
	public void setConn(Connection conn) {
		this.conn = conn;
	}
}
