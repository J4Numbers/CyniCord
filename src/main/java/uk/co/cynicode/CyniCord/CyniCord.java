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
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import net.craftminecraft.bungee.bungeeyaml.pluginapi.ConfigurablePlugin;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import uk.co.cynicode.CyniCord.DataGetters.JsonDataGetter;
import uk.co.cynicode.CyniCord.DataGetters.MySqlDataGetter;
import uk.co.cynicode.CyniCord.Listeners.PluginMessageListener;

import uk.co.cynicode.CyniCord.DataGetters.IDataGetter;

/**
 * A plugin for interfacing with Bungee, CyniChat and IRC
 * 
 * @author CyniCode
 */
public class CyniCord extends ConfigurablePlugin {
	
	/**
	 * This is an instance of the plugin kept around in case of emergencies
	 */
	private static CyniCord self;
	
	/**
	 * This is an instance of the logger which is only accessible through
	 * three methods in this class which print the varying levels
	 */
	private static Logger logger;
	
	/**
	 * This just asks if we're showing debug or not
	 */
	private static boolean debug = false;
	
	/**
	 * Thing for MySQL boosting
	 */
	private ScheduledTask runner;
	
	/**
	 * This thing is the connection thingy we're using for things
	 */
	private static IDataGetter connection = null;
	
	/**
	 * This is the IRC prefix for people on IRC talking to those in-game
	 */
	public static String ircPrefix;
	
	/**
	 * A proxy server instance
	 */
	public static ProxyServer proxy = null;
	
	/**
	 * A list of all the servers we're connected to
	 */
	public static Map<String, ServerInfo> servers = null;
	
	/**
	 * The instance of the IRC bot we need to do all the IRC things
	 */
	public static IrcManager PBot = null;
	
	/**
	 * Send a message using the IRC bot that we own
	 * @param channel : The channel to send it on
	 * @param password : The password of the channel if it exists
	 * @param player : The player who sent the message
	 * @param message : The message itself
	 */
	public static void sendMessage( String channel, String password, String player, String message ) {
		
		//If we're not in the channel, join it.
		if ( PBot.channelJoined(channel) == false )
			//Using the channel and the password
			PBot.joinChannel( channel, password );
		
		//Then send the message
		PBot.sendMessage( channel, player, message );
		
	}
	
	/**
	 * Start the plugin and run through all the motions
	 */
	@Override
	public void onEnable() {
		
		//Get the logger so we can log stuff amazingly enough
		logger = getLogger();
		
		//Then do config bullshit
		this.saveDefaultConfig();
		
		//Get all the details that we need from the configs
		if ( getConfig().getString( "CyniCord.other.debug" ).equalsIgnoreCase( "true" ) ) {
			debug = true;
			printInfo( "Debugging enabled..." );
		} else {
			printInfo( "Debugging disabled..." );
		}
		
		ircPrefix = getConfig().getString( "CyniCord.other.ircPrefix" );
		
		//Get what method of data storage we're using
		try {
			if ( getConfig().getString( "CyniCord.other.storage" ).equalsIgnoreCase( "mysql" ) ) {
				connection = new MySqlDataGetter( this );
				runner = this.getProxy().getScheduler()
					.schedule(this, 
						connection.returnBooster(), 
						5, TimeUnit.MINUTES );
			} else {
				connection = new JsonDataGetter( this );
			}
			
		} catch ( SQLException e ) {
			//Bang!
			//Ow!
			killPlugin();
		} catch ( IOException ex ) {
			//Splat!
			//Oof!
			killPlugin();
		} catch ( Exception exe ) {
			//Um... thud?
			//Thud.
			killPlugin();
		}
		
		//And try to initialise the IRC bot, otherwise this thing is
		// a pointless plugin.
		try {
			PBot = new IrcManager( this, connection );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		//Sort out the server information
		proxy = ProxyServer.getInstance();
		
		//Register the plugin listeners so that we can actually get
		// messages addressed to us
		proxy.getPluginManager().registerListener( this, new PluginMessageListener( this ) );
		
		//Get a list of servers that we're connected to
		servers = proxy.getServers();
		
		//And mark us as this
		self = this;
		
		//This is worthy of a warning <.< >.>
		logger.warning( "CyniCord has been activated..." );
		
	}
	
	/**
	 * And when killing things...
	 */
	@Override
	public void onDisable() {
		
		//connection.endConnection();
		
		//Tell the nice console what's happening
		printInfo( "Killing CyniCord..." );
		
		//Kill our nice bot
		try {
			PBot.stop();
			//self.killPlugin();
		} catch ( Exception e ) {
			printSevere( "Uh oh... something went bang" );
			e.printStackTrace();
		}
		
		//And that's that
		printInfo( "CyniCord has been shut down" );
		
	}
	
	/**
	 * Every now and again, we might need to completely kill the
	 * plugin independently of BungeeCord.
	 */
	public static void killPlugin() {
		
		//Like so
		self.onDisable();
		
	}
	
	/**
	 * Print out a piece of information
	 * @param output : Which is this
	 */
	public static void printInfo( String output ) {
		logger.info( output );
	}
	
	/**
	 * Print out a warning to the console
	 * @param output : Like this
	 */
	public static void printWarning( String output ) {
		logger.warning( output );
	}
	
	/**
	 * Print out something severe to the console
	 * @param output : Like... I don't know, the house is on fire
	 */
	public static void printSevere( String output ) {
		logger.severe( output );
	}
	
	/**
	 * Print out debug to the console
	 * @param output : Text.
	 */
	public static void printDebug( String output ) {
		
		//Only applicable if we're listening for debug
		if ( debug == true )
			logger.info( "[DEBUG] " + output);
		
	}
}
