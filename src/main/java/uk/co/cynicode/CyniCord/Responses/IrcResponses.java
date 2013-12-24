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

import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.exception.IrcException;
import uk.co.cynicode.CyniCord.CyniCord;

/**
 * A class for all the responses that are going to be given by the IRC bot
 *
 * @author CyniCode
 */
public class IrcResponses {

	/**
	 * Show all the help text
	 *
	 * @param bot : The bot that originally got pinged
	 * @param user : The user that pinged the bot
	 */
	public static void helpOutput( PircBotX bot, User user ) {

		bot.setMessageDelay(10);
		bot.sendMessage( user, "You wanted some help?" );
		bot.sendMessage( user, "This is the CyniCord bot (c) CyniCode 2013" );
		bot.sendMessage( user, "All commands start with ':?'" );
		bot.sendMessage( user, "':?'                    -> Show this help screen" );
		bot.sendMessage( user, "':? help'               -> Synonymous with ':?'" );
		bot.sendMessage( user, "':? list'               -> List all the players in this channel" );
		bot.sendMessage( user, "':? list all'           -> List all the players that are online" );
		bot.sendMessage( user, "':? kick   <MCUser>'    -> Kick this player inside the Minecraft channel (You must be an OP!)" );
		bot.sendMessage( user, "':? mute   <MCUser>'    -> Mute the player inside the Minecraft channel (You must be an OP!)" );
		bot.sendMessage( user, "':? unmute <MCUser>'    -> Unmute the player inside the Minecraft channel (You must be an OP!)" );
		bot.sendMessage( user, "':? ban    <MCUser>'    -> Ban the player inside the Minecraft channel (You must be an OP!)" );
		bot.sendMessage( user, "':? unban  <MCUser>'    -> Unban the player inside the Minecraft channel (You must be an OP!)" );
		bot.sendMessage( user, " " );
		bot.sendMessage( user, "If this didn't help you, then you're kinda screwed :P" );

	}

	/**
	 * List all the players on the server or in the channel
	 *
	 * @param user : The user that originally asked
	 * @param bot : The bot that was pinged
	 * @param channel : The channel we're returning the information into
	 * @param all : A boolean of whether it's a single channel or the whole server
	 */
	public static void listOutput( User user, PircBotX bot, String channel, boolean all ) {

		//Let's debug all the parameters
		CyniCord.printDebug(user.getNick() + " : " + channel + " : " + all);

		//And if it's just this channel we're checking...
		if ( !all ) {

			//Let's get the channel that we're listing out
			String listChan = CyniCord.PBot.getGameChannelNames().get( channel );

			PluginMessageResponder.sendInstructionToServer( "list", user.getNick(), listChan, channel );

		} else {

			PluginMessageResponder.sendInstructionToServer( "list", user.getNick(), "ALL", channel );

		}

	}

	/**
	 * Kick a player in the MC channel from inside IRC
	 *
	 * @param user : The user that kicked 'im
	 * @param bot : The bot that was pinged
	 * @param player : The player who got kicked
	 * @param channel : The channel the player will get kicked in
	 */
	public static void kickOutput( User user, PircBotX bot, String player, String channel ) {

		//Tell them that we've reached the method
		CyniCord.printDebug( "Kicking part 2..." );

		String gameChan = CyniCord.PBot.getGameChannelNames().get( channel );

		PluginMessageResponder.sendInstructionToServer( "kick", player, gameChan, channel );

	}

	/**
	 * Un/Mute a player inside a channel from the safety of IRC
	 *
	 * @param user : The user who's doing the un/muting
	 * @param bot : The bot who heard about the un/mute
	 * @param player : The player who will be un/muted
	 * @param channel : The channel they're being un/muted inside
	 * @param undo : Whether we're undoing a previous mute or just making a new one
	 */
	public static void muteOutput( User user, PircBotX bot, String player, String channel, Boolean undo ) {

		String gameChan = CyniCord.PBot.getGameChannelNames().get( channel );

		if ( undo ) {

			PluginMessageResponder.sendInstructionToServer( "unmute", player, gameChan, channel );

		} else {

			PluginMessageResponder.sendInstructionToServer( "mute", player, gameChan, channel );

		}

	}

	/**
	 * A method to un/ban a player in the MC channels from IRC
	 *
	 * @param user : The user that's doing the un/banning
	 * @param bot : The bot that heard about it
	 * @param player : The player that's being un/banned
	 * @param channel : The channel that's about to have one more un/banned player
	 * @param undo : Whether or not it's undoing a previous ban or just making a new one
	 */
	public static void banOutput( User user, PircBotX bot, String player, String channel, Boolean undo ) {

		String gameChan = CyniCord.PBot.getGameChannelNames().get( channel );

		if ( undo ) {

			PluginMessageResponder.sendInstructionToServer( "unban", player, gameChan, channel );

		} else {

			PluginMessageResponder.sendInstructionToServer( "ban", player, gameChan, channel );

		}

	}

	/**
	 * Direct a single message to this bot and relay it to a given channel
	 * in terms of the bot.
	 *
	 * @param bot : This is who is going to send the message
	 * @param chan : This is the channel we're going to send it over
	 * @param message : This is the message we're carrying
	 * @return true if the channel exists and we were successful...
	 *  false if the channel didn't actually exist in the first place.
	 */
	/* static boolean talkOutput( PircBotX bot, String chan, String message ) {

		//Tell the console what's happening
		CyniCord.printDebug( "Trying to TALK in: " + chan );

		//Then ask if the channel exists
		if ( CyniCord.data.getChannel( chan ) != null ) {

			//Since it does...
			CyniCord.printDebug( chan + " exists" );

			//Get this channel
			Channel thisChan = CyniCord.data.getChannel( chan );

			//And all those online
			Map<String, UserDetails> online = CyniCord.data.getOnlineUsers();

			//Then, for all those people...
			for ( Map.Entry<String,UserDetails> thisPlayer
					: online.entrySet() ) {

				//Get each of them
				CyniCord.printDebug( thisPlayer.getKey() + " is the next player in line" );

				//And ask if they are in this channel
				if ( thisPlayer.getValue().getAllChannels().contains( thisChan.getName() ) ) {

					try {

						//If they are...
						CyniCord.printDebug( "Player found... trying to send message" );

						//Send the message to them
						thisPlayer.getValue().getPlayer().sendMessage(
								thisChan.getColour()+"[IRC] ["
										+thisChan.getNick()+"] "
										+bot.getNick()+" : "+message );

					} catch ( NullPointerException e ) {

						//Or fail miserably and cry...
						CyniCord.printDebug( "Player not found... erroring" );
						e.printStackTrace();

					}

				}

			}

			//Once we've done that, send the message into IRC to echo
			// things
			CyniCord.printDebug( "Trying to send message to IRC..." );
			bot.sendMessage( thisChan.getIRC(), message );

			return true;

		}

		//Or the channel simply doesn't exist...
		CyniCord.printDebug( "Channel doesn't exist." );
		return false;

	}*/

}