package uk.co.cynicode.CyniCord.DataGetters;

import java.util.Map;

import uk.co.cynicode.CyniCord.CyniCord;

public interface IDataGetter {

	public void startConnection( CyniCord plugin );
	
	public Map<String, String> getChannels();
	
	public Map<String, String> getIRCChannels();
	
}
