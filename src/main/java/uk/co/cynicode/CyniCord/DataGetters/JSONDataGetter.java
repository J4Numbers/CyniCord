package uk.co.cynicode.CyniCord.DataGetters;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
	 * A constructor for the class that will deal with sending out feelers
	 * for all the asked data. This basically means that it sends out a 
	 * pulse to the other servers asking for information... whether or not
	 * it works is another matter.
	 * @param plugin : The instance of the plugin needed for access to the
	 *  configs
	 * @throws IOException if the PluginMessage goes awry
	 */
	//public boolean startConnection(CyniCord plugin) {
	public JSONDataGetter( CyniCord plugin ) throws IOException {
		try {
			//Create message
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			
			out.writeUTF( "CyniCord" );
			out.writeUTF( "channelReq" );
			
			ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
			DataOutputStream msg = new DataOutputStream(msgBytes);
			msg.writeUTF("Forward");
			msg.writeUTF("ALL");
			msg.writeUTF("CyniChat");
			//Push message content
			msg.writeShort(b.toByteArray().length);
			msg.write(b.toByteArray());
			
			//p.sendPluginMessage(plugin, "BungeeCord", msgBytes.toByteArray());
			CyniCord.printDebug("Message sent!");
			
		} catch (IOException ex) {
			
			CyniCord.printSevere("Error sending message to BungeeChannelProxy");
			throw ex;
			
		}
		
	}
	
	/**
	 * Always return true and simply dump all the items we have ahold of
	 */
	public void endConnection() {
		return;
	}
	
	public void findAllChannels() {
		// TODO Auto-generated method stub
		return;
	}
	
	public Map<String, String> getIrcChannels() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Map<String, String> getLoadedChannels() {
		// TODO Auto-generated method stub
		return null;
	}
	
}