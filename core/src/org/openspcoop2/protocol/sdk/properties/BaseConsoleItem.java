package org.openspcoop2.protocol.sdk.properties;

import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemType;

public class BaseConsoleItem {

	private String id; // non modificabile
	private String label;
	private ConsoleItemType type;
	
	protected BaseConsoleItem(String id, String label, ConsoleItemType type) throws ProtocolException{
		if(id==null){
			throw new ProtocolException("Id undefined");
		}
		this.id = id;
		this.setLabel(label);
		this.setType(type);
	}

	public String getId() {
		return this.id;
	}
	
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) throws ProtocolException {
		if(label==null){
			throw new ProtocolException("Label undefined");
		}
		this.label = label;
	}
	
	
	public ConsoleItemType getType() {
		return this.type;
	}
	public void setType(ConsoleItemType type) throws ProtocolException {
		if(type==null){
			throw new ProtocolException("Type undefined");
		}
		this.type = type;
	}
}
