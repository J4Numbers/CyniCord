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

package uk.co.cynicode.CyniCord;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import uk.co.cynicode.CyniCord.Listeners.IrcChatListener;
import uk.co.cynicode.CyniCord.DataGetters.IDataGetter;

import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;

/**
 * An instantiation of a PircBotX bot
 *
 * @author CyniCode
 */
public class IrcManager {
	
	private PircBotX bot;
	
	private String hostname;
	private String nickname;
	private int port;

	private String adminChan;
	private String adminPass;

	private Map<String, String> gameChannelNames;
	private Map<String, String> ircChannelNames;
	
	/**
	 * Constructor for making a new Bot out of barely anything
	 *
	 * @param plugin : Used for getting the config options
	 * @param connection : Used for connecting to all the channels
	 * @throws Exception : So much that could go wrong here...
	 */
	public IrcManager(CyniCord plugin, IDataGetter connection) throws Exception {
		
		//Get the information about the IRC server we're going to
		// connect to
		this.port = plugin.getConfig().getInt("CyniCord.irc.port");
		this.hostname = plugin.getConfig().getString("CyniCord.irc.hostname");
		this.nickname = plugin.getConfig().getString("CyniCord.irc.nickname");

		this.setAdminChan(plugin.getConfig().getString( "CyniCord.irc.admin_channel" ));
		this.setAdminPass(plugin.getConfig().getString( "CyniCord.irc.admin_password" ));

		//Print out the information as some nice debug bits
		CyniCord.printDebug( this.nickname + "@" + this.hostname + ":" + this.port);
		
		//Make a new bot
		this.bot = new PircBotX();
		
		this.ircChannelNames = connection.getLoadedChannels();
		this.gameChannelNames = connection.getCyniChannels();
		
		//And then register the thing that will enable two-way chatter
		this.bot.getListenerManager().addListener(new IrcChatListener());
		
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

			this.bot.joinChannel(this.getAdminChan(), this.getAdminPass());

			//Let's just see if the map is empty or not first.
			if ( !this.ircChannelNames.isEmpty() ) {
				
				//For every channel there is in the map...
				for ( Map.Entry<String, String> thisChan : this.ircChannelNames.entrySet() ) {
					
					//Lock the channel in and join it
					this.bot.joinChannel( thisChan.getKey(), thisChan.getValue() );
					
				}
				
			}
			
		} catch (IOException e) {
			CyniCord.printSevere( "IRC connection has failed..." );
			throw e;
		} catch (IrcException e) {
			CyniCord.printSevere( "IRC connection has failed..." );
			throw e;
		}
	}
	
	/**
	 * Check our current joined channels against a new list of channels
	 * that we should be joined to. Then act on it.
	 * @param channels : A map of IRC channels and their keys
	 */
	public void compareChannels( Map<String, String> channels ) {

		CyniCord.printDebug( "Comparing channels called" );

		//Firstly, let's get all the channels we're currently joined
		// to with this bot.
		Set<String> currentChannels = getIrcChannelNames().keySet();

		CyniCord.printDebug( "Previous list gathered" );

		//Then let's get a list of all the channels that are
		// in the map we've been handed
		Set<String> currentNewChannels = channels.keySet();

		CyniCord.printDebug( "New list gathered" );

		//Let's just make sure that there are channels to be added to
		if ( currentChannels.isEmpty() && currentNewChannels.isEmpty() )
			return;

		CyniCord.printDebug( "No list is empty" );

		//Now... for every potentially new channel...
		for ( String thisChan : currentNewChannels ) {

			CyniCord.printDebug( String.format( "%s is being checked", thisChan ) );

			//If we're already joined to it...
			if ( currentChannels.contains( thisChan ) ) {

				CyniCord.printDebug( String.format("%s duplicated", thisChan) );

				//Then strike it from both records
				currentChannels.remove( thisChan );
				channels.remove( thisChan );

				CyniCord.printDebug( "Removed" );

			}

		}
		
		//Now, every record in this list is a channel that is
		// no-longer in the records, meaning that it's not 
		// needed anymore. Leave all such channels
		for ( String thisChan : currentChannels )  {
			CyniCord.printDebug( String.format("Leaving %s", thisChan) );

			leaveChannel(thisChan);
		}
		
		//The opposite is true for the new channels. These are
		// now channels that are not in our list of joined 
		// channels yet. Rectify that by joining them.
		for ( Map.Entry<String, String> thisChan : channels.entrySet() ) {
			CyniCord.printDebug( String.format("Joining %s", thisChan) );

			joinChannel(thisChan.getKey(), thisChan.getValue());
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
		getIrcChannelNames().put( name, pass );
		getBot().joinChannel( name, pass );
	}
	
	/**
	 * Method used for leaving a channel as long as we're initially
	 * connected to said channel.
	 * @param name : the name of the channel we're leaving
	 */
	public void leaveChannel( String name ) {
		
		//Let's first make sure that we're in the channel
		if ( getBot().channelExists( name ) ) {
			
			//Now that we've made sure of that, leave it
			getBot().partChannel( getBot().getChannel( name ) );
			
			getIrcChannelNames().remove( name );
			
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
		getBot().shutdown( true );
		
		CyniCord.printInfo("IRC has been killed.");
		
	}
	
	/**
	 * @return the instance of the bot
	 */
	public PircBotX getBot() {
		return this.bot;
	}
	
	/**
	 * @param bot the bot to set
	 */
	public void setBot( PircBotX bot ) {
		this.bot = bot;
	}
	
	/**
	 * @return The map of IRC channels
	 */
	public Map<String,String> getIrcChannelNames() {
		return this.ircChannelNames;
	}
	
	/**
	 * @param ircChannelNames the ircChannelNames to set
	 */
	public void setIrcChannelNames( Map<String, String> ircChannelNames ) {
		this.ircChannelNames = ircChannelNames;
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

	/**
	 * @return the admin channel that has been set
	 */
	public String getAdminChan() {
		return adminChan;
	}

	/**
	 * @param adminChan the admin channel to set
	 */
	public void setAdminChan(String adminChan) {
		this.adminChan = adminChan;
	}

	/**
	 * @return the admin channel password that has been set
	 */
	public String getAdminPass() {
		return adminPass;
	}

	/**
	 * @param adminPass the admin password to set
	 */
	public void setAdminPass(String adminPass) {
		this.adminPass = adminPass;
	}
}