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
	//public boolean startConnection( CyniCord plugin );
	
	public Runnable returnBooster();
	
	/**
	 * Kill the connection to whatever we're using
	 */
	public void endConnection();
	
	/**
	 * Get a set of all the channels that are available
	 * @throws Exception if an error up if something went wrong 
	 *  in finding something
	 */
	public void findAllChannels() throws Exception;
	
	/**
	 * Get all the IRC information about the channels
	 * @return Map< IRC Channel Name, IRC Chanel Password >
	 */
	public Map<String, String> getIrcChannels();
	
	/**
	 * Get all the loaded channels
	 * @return the loaded channels
	 */
	public Map<String, String> getLoadedChannels();
	
}
