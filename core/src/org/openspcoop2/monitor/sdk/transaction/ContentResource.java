package org.openspcoop2.monitor.sdk.transaction;

import org.openspcoop2.monitor.sdk.constants.MessageType;

/**
 * ContentResource
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContentResource extends AbstractContentResource {
		
	public ContentResource(MessageType messageType) {
		super(messageType);
	}
	
	/**
	 * Restituisce il nome della risorsa
	 * 
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Restituisce il valore della risorsa
	 * 
	 */
	@Override
	public String getValue() {
		return this.value;
	}
	
	public void setValue(String value) {
		this.value = value; 
	}
	
	protected String name = "";
	protected String value = "";

}
