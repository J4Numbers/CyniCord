package uk.co.cynicode.CyniCord.DataGetters;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import uk.co.cynicode.CyniCord.CyniCord;

public class JSONDataGetter implements IDataGetter {

	public void startConnection(CyniCord plugin) {
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

				p.sendPluginMessage(plugin, "BungeeCord", msgBytes.toByteArray());
				CyniCord.printDebug("Message sent!");
			} catch (IOException ex) {
				CyniCord.printSevere("Error sending message to BungeeChannelProxy");
				ex.printStackTrace();
			}
		
	}

	public Map<String, String> getChannels() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getIRCChannels() {
		// TODO Auto-generated method stub
		return null;
	}

}
