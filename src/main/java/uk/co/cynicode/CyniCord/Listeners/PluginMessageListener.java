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

package uk.co.cynicode.CyniCord.Listeners;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import uk.co.cynicode.CyniCord.CyniCord;

import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * A class responsible for listening out for plugin messages
 * and acting upon them as it sees fit.
 * 
 * @author CyniCode
 */
public class PluginMessageListener implements Listener {
	
	/**
	 * The instance of CyniCord that we need in order to 
	 * access some of its methods... at some point in the
	 * potential future.
	 */
	private final CyniCord plugin;
	
	/**
	 * Constructor of the class in which we declare the plugin
	 * variable and assign it for potential later usage.
	 * @param plugin : Not used quite yet
	 */
	public PluginMessageListener( CyniCord plugin ) {
		this.plugin = plugin;
	}
	
	/**
	 * On every plugin event, a flag gets sent up here and we
	 * take a listen to whatever it says. If it's good news, 
	 * we celebrate by passing it onto the IRC client we have
	 * here.
	 * @param event : The Plugin Message Event that we're grabbing
	 */
	@EventHandler
	public void onPluginEvent( PluginMessageEvent event ) {
		try {
			
			//Let's start things off nice and smooth with some debug
			// saying that something is happening
			CyniCord.printDebug("Channel received : " + event.getTag());
			
			//Now we have to make sure that we're listening into the
			// right thing for this part of the server
			if (!event.getTag().equals("BungeeCord")) {
				//If not, throw it out
				CyniCord.printWarning("Listener was given message for channel " + event.getTag());
				return;
			}
			
			//Otherwise, continue onwards
			CyniCord.printDebug( "CyniChat message recieved" );
			
			//Initialise the data stream so we can read all the info
			// in and analyse it.
			DataInputStream in = new DataInputStream( new ByteArrayInputStream( event.getData() ) );
			
			//Get the instruction word for the message
			String instruct = in.readUTF();
			
			//If it's not FORWARD then it wasn't meant for us
			// at all.
			if ( !instruct.equalsIgnoreCase( "forward" ) ) {
				//Close the stream and kick it out
				in.close();
				return;
			}
			
			//Next is the direction that the plugin message
			// was intended for. We're not too interested in this
			String direct = in.readUTF();
			
			//This on the other hand... this is the subchannel that
			// is crucial to it being executed correctly
			String subChannel = in.readUTF();
			
			//Debug everything just to be sure
			CyniCord.printDebug( "Insruction: " + instruct );
			CyniCord.printDebug( "Direction: " + direct );
			CyniCord.printDebug( "Subchannel: " + subChannel );
			
			//Check our own subchannel
			if( !subChannel.equals("CyniChat") && !subChannel.equals( "CyniCordReply" ) ) {
				return;/*Not our problem*/
			}
			
			//And then continue with the natural order
			CyniCord.printDebug( "Next stage" );
			
			//Read in the basic information parts
			short len = in.readShort();
			byte[] msgbytes = new byte[len];
			in.readFully(msgbytes);
			
			//And debug again
			CyniCord.printDebug( "We're still going..." );
			
			//Create a stream from the data we've recieved
			//String servername = ( (Server) event.getSender()).getInfo().getName();
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(msgbytes));
			
			//And get more debug...
			CyniCord.printDebug( "Input streams created..." );
			
			//These two strings are basically unnecessary data for
			// the operation of this plugin
			String one = dis.readUTF();
			String two = dis.readUTF();

			if ( subChannel.equals( "CyniCordReply" ) ) {

				CyniCord.printDebug( "CyniCord reply found..." );

				String message = dis.readUTF();
				String channel = dis.readUTF();

				CyniCord.PBot.getBot().sendMessage( channel, message );

				CyniCord.printDebug( String.format( "'%s' sent over '%s'", message, channel ) );

				return;

			}

			//Get each and every part of the data that we'll have
			// been sent from the CyniChat instances
			//EndpointType type = EndpointType.values()[dis.readInt()];
			String fancyPlayerName = dis.readUTF();
			String playerName = dis.readUTF();
			String chatChannel = dis.readUTF();
			String IRCName = dis.readUTF();

			//If there's no channel to send it to, there's
			// no point in continuing any further.
			if ( IRCName.equals( "" ) ) return;

			String IRCPass = dis.readUTF();
			String message = dis.readUTF();
			
			//And debug them all out
			CyniCord.printDebug( "Details read..." );
			
			CyniCord.printDebug( "Fancy name : " + fancyPlayerName );
			CyniCord.printDebug( "Player name : " + playerName );
			CyniCord.printDebug( "Channel name : " + chatChannel );
			CyniCord.printDebug( "IRC Chan Name : " + IRCName );
			CyniCord.printDebug( "IRC Pass Name : " + IRCPass );
			CyniCord.printDebug( "Message : " + message );
			
			//Print the message into IRC
			CyniCord.sendMessage( IRCName, IRCPass, playerName, message );
			
			//And perform one final debug
			CyniCord.printDebug( "Message sent..." );
			
		} catch ( IOException e ) {
			CyniCord.printWarning("PluginMessage error...");
			e.printStackTrace();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
}
