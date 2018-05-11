package org.openspcoop2.monitor.sdk.transaction;

import java.util.HashMap;
import java.util.Set;

import org.openspcoop2.monitor.sdk.constants.ContentResourceNames;
import org.openspcoop2.monitor.sdk.constants.MessageType;

/**
 * TransportHeaderResource
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransportHeaderResource extends AbstractContentResource {
	
	private HashMap<String,String> header = null;
	public TransportHeaderResource(MessageType messageType) {
		super(messageType);
		this.header = new HashMap<String, String>();
	}
	
	public void setProperty(String name, String value) {
		this.header.put(name, value);
	}

	public void setValue(HashMap<String,String> header) {
		this.header = header;
	}
	

	@Override
	public String getName() {
		if (this.isRequest())
			return ContentResourceNames.REQ_TRANSPORT_HEADER;
		else
			return ContentResourceNames.RES_TRANSPORT_HEADER;
	}
	
	public String getProperty(String key) {
		return this.header.get(key);
	}
	
	@Override
	public HashMap<String,String> getValue() {
		return this.header;
	}
	
	public Set<String> keys(){
		return this.header.keySet();
	}



}
