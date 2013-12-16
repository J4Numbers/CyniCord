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

package uk.co.cynicode.CyniCord.DataGetters;

import java.util.Map;

import uk.co.cynicode.CyniCord.CyniCord;

/**
 * An interface for all possible methods of getting the data about
 * IRC channels
 * 
 * @author CyniCode
 */
public interface IDataGetter {
	
	/**
	 * Start up the connection to whichever method you need
	 * @param plugin : The instance of the plugin that might be used
	 *  for something or another
	 * @return whether it has connected or not
	 */
	//public boolean startConnection( CyniCord plugin );
	
	public Runnable returnBooster();
	
	/**
	 * Kill the connection to whatever we're using
	 */
	public void endConnection();
	
	/**
	 * Get a set of all the channels that are available
	 * @throws Exception if an error up if something went wrong 
	 *  in finding something
	 */
	public void findAllChannels() throws Exception;
	
	/**
	 * Get all the IRC information about the channels
	 * @return Map< IRC Channel Name, IRC Chanel Password >
	 */
	public Map<String, String> getIrcChannels();
	
	/**
	 * Get all the loaded channels
	 * @return the loaded channels
	 */
	public Map<String, String> getLoadedChannels();
	
}
