package uk.co.cynicode.CyniCord;

import java.util.logging.Logger;

import org.pircbotx.PircBotX;

import uk.co.cynicode.CyniCord.Listeners.PluginMessageListener;
import uk.co.cynicode.CyniCord.IRCManager;

import net.craftminecraft.bungee.bungeeyaml.pluginapi.ConfigurablePlugin;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.ProxyServer;

public class CyniCord extends ConfigurablePlugin {
	
	private static Logger logger = null;
	
	private static boolean debug = false;
	
	public static IRCManager PBot = null;
	
	@Override
	public void onEnable() {
		
		this.logger = getLogger();
		this.saveDefaultConfig();
		
		if ( getConfig().getString( "CyniCord.other.debug" ).equalsIgnoreCase( "true" ) ) {
			debug = true;
			printInfo( "Debugging enabled..." );
		} else {
			printInfo( "Debugging disabled..." );
		}
		
		PircBotX boy = new PircBotX();
		
		try {
			PBot = new IRCManager( this );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		ProxyServer.getInstance().getPluginManager().registerListener( this, new PluginMessageListener( this ) );
		logger.warning( "CyniCord has been activated..." );
	}
	
	public void onDisable() {
		
	}

	public static void printInfo( String output ) {
		logger.info( output );
	}
	
	public static void printWarning( String output ) {
		logger.warning( output );
	}
	
	public static void printSevere( String output ) {
		logger.severe( output );
	}
	
	public static void printDebug( String output ) {
		if ( debug == true )
			logger.info( "[DEBUG] " + output );
	}
}
