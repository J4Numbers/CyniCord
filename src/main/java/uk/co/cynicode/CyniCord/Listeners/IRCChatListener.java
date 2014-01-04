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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import net.md_5.bungee.api.config.ServerInfo;

import uk.co.cynicode.CyniCord.CyniCord;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import uk.co.cynicode.CyniCord.Responses.IrcResponses;

/**
 * Deal with all the output that the bot can see
 *
 * @author CyniCode
 */
public class IrcChatListener extends ListenerAdapter {

	/**
	 * Listen for all the chatter that is going on on the IRC bot's end so that
	 * any commands given there are going to be executed while commands given
	 * from inside MC will be left alone.
	 * @param event : The message event we're going to examine
	 * @throws Exception for if there is one
	*/
	@Override
	public void onMessage(MessageEvent event) throws Exception {
		
		//Every command starts with these two characters
		if (event.getMessage().startsWith(":?")) {
			
			//Everything after that is a keyword
			String[] argments = event.getMessage().split(" ");
			
			//So we need to get the basic info quite quickly
			org.pircbotx.Channel thisChan = event.getChannel();
			
			//Count the number of argments first of all. If
			// it's made of just the :? then it's a help command
			if (argments.length == 1) {
				
				//So we do the nice thing and help them
				CyniCord.printDebug("Default used...");
				IrcResponses.helpOutput(event.getBot(), event.getUser());
				return;
				
			}
			
			//It could literally be a help command
			if (argments[1].equalsIgnoreCase("help")) {
				
				//So we do the same
				CyniCord.printDebug("Help selected...");
				IrcResponses.helpOutput(event.getBot(), event.getUser());
				return;
				
			}
			
			//It might be a request for a list of all the people
			// in that channel or on that server
			if (argments[1].equalsIgnoreCase("list")) {
				
				//So check what it is first.
				// Having argments means that it's either a
				// channel request or an all request
				CyniCord.printDebug("Listing chosen...");
				if (argments.length > 2) {

					if (argments[2].equalsIgnoreCase("all")) {
						
						//The example of all being used here
						CyniCord.printDebug("You've either got 'all' as parameter...");
						CyniCord.printDebug(event.getUser().getNick() + " : " + thisChan.getName());
						IrcResponses.listOutput(event.getUser(), event.getBot(), thisChan.getName(), true);
						return;
						
					}
					
				} else {
					
					//And this is just for the current channel
					// check.
					CyniCord.printDebug("Or you don't....");
					CyniCord.printDebug(event.getUser().getNick() + " : " + thisChan.getName());
					IrcResponses.listOutput(event.getUser(), event.getBot(), thisChan.getName(), false);
					return;
					
				}
				
				//Return nothing otherwise
				return;
				
			}
			
			//Some people just want to kick others
			// TODO: Figure out how to do this for multiple servers
			if (argments[1].equalsIgnoreCase("kick")
					&& thisChan.isOp(event.getUser())) {
				
				//It's only doable if they're an op of the channel
				CyniCord.printDebug("Kicking...");
				
				if (argments[2] != null) {
					//Kick a person
					IrcResponses.kickOutput(event.getUser(), event.getBot(), argments[2], thisChan.getName());
					return;
				}
				
				//Or say that we need a person to kick
				event.respond("I'm sorry, you must include a person to be kicked");
				return;
				
			}
			
			//The same effectively goes for banning and unbanning
			if ((argments[1].equalsIgnoreCase("ban")
					|| argments[1].equalsIgnoreCase("unban"))
					&& thisChan.isOp(event.getUser())) {
				
				//They have to be an op of the channel
				CyniCord.printDebug("Banning...");
				
				if (argments[2] != null) {
					
					//Something was supplied as a player
					// And if it's not a ban that we're 
					// checking
					if (argments[1].equalsIgnoreCase("ban")) {
						IrcResponses.banOutput(event.getUser(), event.getBot(), argments[2], event.getChannel().getName(), false);
					} else {
						//Then it must be an unban
						IrcResponses.banOutput(event.getUser(), event.getBot(), argments[2], event.getChannel().getName(), true);
					}
					
					return;
					
				}
				
				//Otherwise, tell them that we need a player to 
				// ban/unban
				event.respond("I'm sorry, you must include a person to be un/banned");
				return;
				
			}
			
			//See banning for a general gist
			if ((argments[1].equalsIgnoreCase("mute")
					|| argments[1].equalsIgnoreCase("unmute"))
					&& thisChan.isOp(event.getUser())) {
				
				CyniCord.printDebug("Muting...");
				
				if (argments[2] != null) {
					
					if (argments[1].equalsIgnoreCase("mute")) {
						IrcResponses.muteOutput(event.getUser(), event.getBot(), argments[2], thisChan.getName(), false);
					} else {
						IrcResponses.muteOutput(event.getUser(), event.getBot(), argments[2], thisChan.getName(), true);
					}
					return;
					
				}
				
				event.respond("I'm sorry, you must include a person to be un/muted");
				return;
				
			}
			
			//Restart the CyniCord bot
			if (argments[1].equalsIgnoreCase("restart")
					&& thisChan.isOp( event.getUser() )
					&& thisChan.getName().equalsIgnoreCase(CyniCord.PBot.getAdminChan())) {
				
				//The person must be an op to restart
				// the IRC bot
				CyniCord.printDebug("Restarting...");
				CyniCord.PBot.restart();
				return;
				
			}
			
			//Kill the bot dead.
			if (argments[1].equalsIgnoreCase("kill")
					&& thisChan.isOp(event.getUser())
					&& thisChan.getName().equalsIgnoreCase( CyniCord.PBot.getAdminChan() )) {
				
				//Unsurprisingly, the person must be an op
				// to do this, else anyone could murder the
				// bot to death.
				CyniCord.printDebug("Murdering...");
				CyniCord.PBot.stop();
				return;
				
			}
			
			//Otherwise, it's an undefined command, put what they 
			// tried as a command anyway just in case.
			CyniCord.printDebug("\"" + argments[1] + "\"");
			
			//Then return as it's not going to be suitable for
			// publication to bukkit servers
			return;
			
		}
		
		//Now that we've got all the commands out the way, we can look
		// at sending an actual message to the bukkit servers beneath
		// the bungee instance
		
		//Get the basic debug about the person who sent the message
		CyniCord.printDebug("Sender: " + event.getUser().getNick());
		CyniCord.printDebug("Channel: " + event.getChannel().getName().toLowerCase());
		CyniCord.printDebug("Message: " + event.getMessage());
		
		//Send a message to all the servers saying that a message on
		// this channel occured, if it belongs to any of your own 
		// channels, then you can send it to your players in that 
		// channel
		String ircChannelName = event.getChannel().getName().toLowerCase();
		
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
					out.writeInt(0);// typeId

					//Now write nothing to the stream for
					// some reason which only debugging
					// shall reveal.
					out.writeUTF("");
					
					//Write the prefix of IRC and the nickname
					// of the user to the stream so that it's 
					// outputted correctly on the bukkit servers
					out.writeUTF( CyniCord.ircPrefix + event.getUser().getNick() );
					
					//Along with the channel name and the 
					// corresponding message
					out.writeUTF( ircChannelName );
					out.writeUTF( event.getMessage() );
					
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
					msg.writeUTF("CyniChat");
					
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
	
	/**
	 * If we get asked something in query, reply with things
	 * according to what they've asked.
	 * @param event : The IRC event details
	 */
	@Override
	public void onPrivateMessage(PrivateMessageEvent event) {
		
		//Debug what has happened
		CyniCord.printDebug("Private message called!");
		
		//Ask if we should pay attention to it or not
		if (event.getMessage().startsWith(":?")) {
			
			//And now split it into details
			String[] argments = event.getMessage().split(" ");
			
			//Tell the console who called it
			CyniCord.printDebug(":? called by " + event.getUser().getNick());
			
			//And ask what they're asking us
			//Talking means that the user wants the bot to
			// talk on a chosen IRC channel for them
			if (argments[1].equalsIgnoreCase("talk")) {
				
				//More debug
				CyniCord.printDebug("Talking with " + argments.length + " args... ");
				
				//And ask if there even is a message
				if (argments.length > 3) {
					
					CyniCord.printDebug("Talking...");
					/*if (ircResponses.talkOutput(event.getBot(), argments[2], stacker(argments, 3, argments.length)) == false) {
						event.respond("Invalid statement. Please make sure that channel exits in the MC server.");
					}*/
				}
				
			}
			
			//Otherwise, they're asking for help obviously
			// ircResponses.helpOutput(event.getBot(), event.getUser());
			
		}
		
	}
	
	/**
	 * Given an array and a start and an end index in that array,
	 * construct it into a sentence that we shall return.
	 * @param args : The array we're concatenating
	 * @param start : The start index
	 * @param end : The end index
	 * @return the complete sentence
	 */
	public String stacker(String[] args, int start, int end) {
		
		//Initialise the basic strings that we're using
		String finalString = "";
		String connector = "";
		
		//Now for the start to the end, read the array
		for (int i = start; i < end; i++) {
			
			//And add it into the sentence
			finalString += connector + args[i];
			connector = " ";
			
		}
		
		//then return what we have
		return finalString;
		
	}
}