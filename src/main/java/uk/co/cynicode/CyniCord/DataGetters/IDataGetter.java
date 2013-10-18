package uk.co.cynicode.CyniCord.DataGetters;

import java.util.Map;

import uk.co.cynicode.CyniCord.CyniCord;

public interface IDataGetter {

	public boolean startConnection( CyniCord plugin );
	
	public boolean endConnection();
	
	public Map<String, String> getChannels();
	
	public Map<String, String> getIRCChannels();
	
	public Map<String, String> loadChannels();
	
}
