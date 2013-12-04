package uk.co.cynicode.CyniCord.DataGetters;

import java.util.Map;

import uk.co.cynicode.CyniCord.CyniCord;

/**
 * An interface for all possible methods of getting the data about
 * IRC channels
 * @author Cynical
 */
public interface IDataGetter {
	
	/**
	 * Start up the connection to whichever method you need
	 * @param plugin : The instance of the plugin that might be used
	 *  for something or another
	 * @return whether it has connected or not
	 */
	public boolean startConnection( CyniCord plugin );
	
	/**
	 * Kill the connection to whatever we're using
	 * @return whether the method is dead or not
	 */
	public boolean endConnection();
	
	/**
	 * Get a set of all the channels that are available
	 * @return Map< IRC Channel Name, Minecraft Channel Name >
	 */
	public Map<String, String> getChannels();
	
	/**
	 * Get all the IRC information about the channels
	 * @return Map< IRC Channel Name, IRC Chanel Password >
	 */
	public Map<String, String> getIRCChannels();
	
	/**
	 * Get all the loaded channels
	 * @return the loaded channels
	 */
	public Map<String, String> loadChannels();
	
}
