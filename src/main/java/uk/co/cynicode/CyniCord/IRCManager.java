package uk.co.cynicode.CyniCord;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

//import uk.co.cynicode.CyniCord.DataGetters.IDataGetter;
import uk.co.cynicode.CyniCord.Listeners.IRCChatListener;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;

/**
 * An instantiation of a PircBotX bot
 *
 * @author Matthew Ball
 *
 */
public class IRCManager {
	
	private PircBotX bot;
	
	private String hostname;
	private String nickname;
	private int port;
	private Map<String, String> gameChannelNames;
	private Map<String, String> ircChannelNames;
	
	/**
	 * Constructor for making a new Bot out of barely anything
	 *
	 * @param plugin : Used for getting the config options
	 * @throws Exception : So much that could go wrong here...
	 */
	public IRCManager(CyniCord plugin/*, IDataGetter connection*/ ) throws Exception {
		
		//Get the information about the IRC server we're going to
		// connect to
		this.port = plugin.getConfig().getInt("CyniCord.irc.port");
		this.hostname = plugin.getConfig().getString("CyniCord.irc.hostname");
		this.nickname = plugin.getConfig().getString("CyniCord.irc.nickname");
		
		//Print out the information as some nice debug bits
		CyniCord.printDebug( this.nickname + "@" + this.hostname + ":" + this.port);
		
		//Make a new bot
		this.bot = new PircBotX();
		
		//Map<String, String> channels = connection.loadChannels();
		
		//And then register the thing that will enable two-way chatter
		this.bot.getListenerManager().addListener(new IRCChatListener());
		
		//Set the name to whatever has been chosen
		this.bot.setName( this.nickname );
		
		//Then set the login to my own since I like it :P
		this.bot.setLogin("CyniBot");
		
		//More debug dear lizer, dear lizer
		CyniCord.printDebug( "Trying to connect " + this.nickname + " to " 
			+ this.hostname + ":" + this.port );
		
		//Now actually do what the debug is saying we're doing
		try {
			
			//And connect!
			this.bot.connect( this.hostname, this.port );
			
			//Huh... that was easy
			CyniCord.printInfo( "Connected " + this.nickname + " to IRC server: " + this.hostname );
			
			/*if ( channels != null ) {
				Set<String> names = channels.keySet();
				Iterator<String> iterNames = names.iterator();
				
				while ( iterNames.hasNext() ) {
					String thisChannel = iterNames.next();
				
				this.bot.joinChannel( thisChannel, channels.get(thisChannel) );
			}
			
		}*/
		
		} catch (Exception e) {
			CyniCord.printSevere( "IRC connection has failed..." );
			throw e;
		}
	}
	
	/**
	 * Ask if we are joined to this given channel.
	 * @param channel : The channel we are giving
	 * @return true or false
	 */
	public boolean channelJoined( String channel ) {
		return getBot().channelExists( channel );
	}
	
	/**
	 * Join the channel.
	 * @param name : This channel in fact
	 * @param pass : With this password
	 */
	public void joinChannel( String name, String pass ) {
		getBot().joinChannel( name, pass );
	}
	
	/**
	 * Given a map of channels, join to all of them
	 * @param channels : The channels we wish to join to
	 * @param ircChans : The channels and their passwords
	 */
	public void addChannels( Map<String, String> channels, Map<String, String> ircChans ) {
		
		//Go through the channels
		for ( Map.Entry<String, String> thisSet : channels.entrySet() ) {
			
			//And retrieve all the pertinent data
			String thisIrcChan = thisSet.getKey();
			String thisGameChan = thisSet.getValue();
			String thisIrcPass = ircChans.get( thisIrcChan );
			
			//Then ask if we already have the channels in the 
			// maps of the class. If we do, move on. Otherwise
			// we add them into the current lists.
			if ( !getIrcChannelNames().containsValue(thisGameChan) ) {
				getIrcChannelNames().put( thisIrcChan, thisGameChan);
				getIrcChannelNames().put( thisGameChan, thisGameChan );
			}
			
			//Now actually join the channels
			try {
				
				//like so
				CyniCord.printDebug( "Attempting to connect to " + thisIrcChan + " with password: " + thisIrcPass );
				getBot().joinChannel( thisIrcChan, thisIrcPass );
				
			} catch ( Exception e ) {
				
				//Or not...
				CyniCord.printSevere( "Failed to connect to the "+ thisIrcChan +" channel" );
				e.printStackTrace();
				
			}
			
		}
		
	}
	
	/**
	 * Send a message using the bot
	 * @param channel : Given the channel
	 * @param player : The player
	 * @param message : And the message itself
	 */
	public void sendMessage( String channel, String player, String message ) {
		
		//Try just in case of error
		try {
			
			//Send the message
			getBot().sendMessage( channel, player + " : " + message );
			//And we're done
			
		} catch ( Exception e ) {
			
			//Or not...
			CyniCord.printDebug( "Oops! Something went wrong!" );
			e.printStackTrace();
			
		}
		
	}
	
	/**
	 * Restart the IRC component of the plugin via various means
	 */
	public void restart() {
		
		//Tell the console what is happening
		CyniCord.printWarning("Restarting the IRC bot...");
		CyniCord.printInfo("Stopping the IRC bot...");
		
		//And kill the bot
		getBot().shutdown();
		
		//Then start it all up again
		CyniCord.printInfo("Starting up the IRC bot again...");
		try {
			
			//Well... attempt to do so anyway
			getBot().reconnect();
			//loadChannels(DataManager.returnAllChannels());
			
			//And it's done
			CyniCord.printInfo("Reconnected successfully");
			
		} catch (IOException e) {
			
			//These... are issues
			CyniCord.printSevere("We could not connect...");
			e.printStackTrace();
			
		} catch (NickAlreadyInUseException e) {
			
			//As is this
			CyniCord.printSevere("Our nickname was already in use...");
			e.printStackTrace();
			
		} catch (IrcException e) {
			
			//And that
			CyniCord.printSevere("IRC has failed... call in the drones...");
			e.printStackTrace();
			
		}
		
	}
	
	/**
	 * Kill the IRC component of the plugin ungracefully ... In other words,
	 * knock it over the head with a bat.
	 */
	public void stop() {
		
		//As it is said... stop the server
		CyniCord.printInfo("Shutting down IRC...");
		
		//With no chance of reconnection
		getBot().shutdown(true);
		
		CyniCord.printInfo("IRC has been killed.");
		
	}
	
	/**
	 * @return the instance of the bot
	 */
	public PircBotX getBot() {
		return this.bot;
	}
	
	/**
	 * @return The map of IRC channels
	 */
	public Map<String,String> getIrcChannelNames() {
		return this.ircChannelNames;
	}
	
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
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}
	
	/**
	 * @param nickname the nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
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
	 * @return the game channel names
	 */
	public Map<String, String> getGameChannelNames() {
		return gameChannelNames;
	}
	
}