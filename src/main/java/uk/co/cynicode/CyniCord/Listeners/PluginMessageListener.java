package uk.co.cynicode.CyniCord.Listeners;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import uk.co.cynicode.CyniCord.CyniCord;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PluginMessageListener implements Listener {
	
	private CyniCord plugin;
	
	public PluginMessageListener( CyniCord plugin ) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPluginEvent( PluginMessageEvent event ) {
		// Checks if the one sending the message is the server.
		if ( event.getTag() != "CyniChat" || !( event.getSender() instanceof Server ) )
			return;
		String servername = ( (Server) event.getSender()).getInfo().getName();
		ByteArrayInputStream bytestream = new ByteArrayInputStream( event.getData() );
		DataInputStream datastream = new DataInputStream(bytestream);
		try {
			String method = datastream.readUTF();
		} catch ( IOException e ) {
			ProxyServer.getInstance().getLogger().warning("[BungeeBan] PluginMessage error : Invalid date format");
		}
		return;
	}
	
}
