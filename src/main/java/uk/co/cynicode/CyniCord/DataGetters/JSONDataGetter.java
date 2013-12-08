package uk.co.cynicode.CyniCord.DataGetters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import uk.co.cynicode.CyniCord.CyniCord;

/**
 * A class for the dealing with a file-based channel system.
 * 
 * PLEASE NOTE: This does not work yet.
 * The logistics of telling bungee to hand over some of its data
 * is incredibly frustrating.
 * 
 * @author Cynical
 */
public class JSONDataGetter implements IDataGetter {
	
	/**
	 * The item which contains the mapping of the irc channels
	 * and their corresponding passwords
	 */
	private Map<String, String> loadedChannels = new HashMap<String, String>();
	
	/**
	 * These are all the channels that are on the servers that are
	 * mapped onto the irc channels
	 */
	private Map<String, String> cyniChannels = new HashMap<String, String>();
	
	/**
	 * This is the cyniChannel mapping, but reversed
	 */
	private Map<String, String> ircChannels = new HashMap<String, String>();
	
	/**
	 * A constructor for the class that will deal with sending out feelers
	 * for all the asked data. This basically means that it sends out a 
	 * pulse to the other servers asking for information... whether or not
	 * it works is another matter.
	 * @param plugin : The instance of the plugin needed for access to the
	 *  configs
	 * @throws IOException if the PluginMessage goes awry
	 */
	public JSONDataGetter( CyniCord plugin ) throws IOException {
		
	}
	
	/**
	 * Always return true and simply dump all the items we have ahold of
	 */
	public void endConnection() {
		
	}
	
	/**
	 * Unimplemented method to get all the channels available
	 */
	public void findAllChannels() {
		
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
	
	public Runnable returnBooster() {
		return null;
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
	public void setLoadedChannels( Map<String, String> loadedChannels ) {
		this.loadedChannels = loadedChannels;
	}
	
}