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

package uk.co.cynicode.CyniCord.Responses;

import net.md_5.bungee.api.config.ServerInfo;
import uk.co.cynicode.CyniCord.CyniCord;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * A class to send out messages to the child servers on Bungee
 * and to tell them to do something... FAST!
 *
 * @author Cynical
 */
public class PluginMessageResponder {

	public static void sendInstructionToServer( String instruct, String player,
												String channel, String returnChannel ) {

		//So for all the servers that we're connected to...
		for ( Map.Entry<String, ServerInfo> thisServer : CyniCord.servers.entrySet() ) {

			//Get the number of players on it since bungee needs that
			// to work.
			if ( thisServer.getValue().getPlayers().size() > 0 ) {

				// Now try this...
				try {

					//Create the streams
					ByteArrayOutputStream b = new ByteArrayOutputStream();
					DataOutputStream out = new DataOutputStream(b);

					//Now write nothing to the stream for
					// some reason which only debugging
					// shall reveal.
					out.writeUTF("");

					//Write the instruction to the stream
					// so that the reader can see what it
					// needs to do.
					out.writeUTF( instruct );

					//Along with the channel name and the
					// corresponding player to do something
					// to
					out.writeUTF( channel );
					out.writeUTF( player );

					//Then finally the channel (or user) to
					// return the completed message to when
					// we're done
					out.writeUTF( returnChannel );

					//Debug out the things for reasons
					CyniCord.printDebug( out.toString() );
					CyniCord.printDebug( b.toString() );

					//And make more streams
					ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
					DataOutputStream msg = new DataOutputStream(msgBytes);

					//We don't need the FORWARD and the ALL
					// because of some reason for which I
					// don't quite know...
					// Just the subchannel
					//  msg.writeUTF("Forward");
					//  msg.writeUTF("ALL");
					msg.writeUTF("CyniCord");

					//Push message content
					msg.writeShort(b.toByteArray().length);
					msg.write(b.toByteArray());

					//And add more debug again
					CyniCord.printDebug( msg.toString() );

					//Then send the message to the waiting
					// server and say we've done so
					thisServer.getValue().sendData( "BungeeCord", msgBytes.toByteArray() );
					CyniCord.printDebug( "Sent message to " + thisServer.getKey() );

				} catch (IOException ex) {

					//Durrr
					CyniCord.printSevere("Error sending message to BungeeChannelProxy");
					ex.printStackTrace();

				}

			}

		}

	}

}
