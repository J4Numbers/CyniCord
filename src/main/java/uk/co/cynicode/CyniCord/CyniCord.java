package uk.co.cynicode.CyniCord;

import java.util.Map;
import java.util.logging.Logger;

import uk.co.cynicode.CyniCord.Listeners.PluginMessageListener;

import net.craftminecraft.bungee.bungeeyaml.pluginapi.ConfigurablePlugin;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

/**
 * A plugin for interfacing with Bungee, CyniChat and IRC
 * @author Cynical
 */
public class CyniCord extends ConfigurablePlugin {
	
	private static CyniCord self;
	
	private static Logger logger;
	
	private static boolean debug = false;
	
	//private static IDataGetter connection = null;
	
	public static String ircPrefix;
	
	public static ProxyServer proxy = null;
	
	public static Map<String, ServerInfo> servers = null;
	
	public static IRCManager PBot = null;
	
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
		CyniCord.logger = getLogger();
		
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
		
		//try {
		//	if ( getConfig().getString( "CyniCord.other.storage" ).equalsIgnoreCase( "mysql" ) ) {
		//		connection = new MySQLDataGetter( this );
		//	} else {
		//		connection = new JSONDataGetter( this );
		//	}
		//} catch ( SQLException e ) {
		//	killPlugin();
		//} catch ( IOException ex ) {
		//	killPlugin();
		//} catch ( Exception exe ) {
		//	killPlugin();
		//}
		//
		//if ( connection.startConnection( this ) == false )
		//	killPlugin();
		
		//And try to initialise the IRC bot, otherwise this thing is
		// a pointless plugin.
		try {
			PBot = new IRCManager( this/*, connection*/ );
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
