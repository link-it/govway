package org.openspcoop2.monitor.sdk.transaction;

import org.openspcoop2.monitor.sdk.constants.MessageType;

/**
 * AbstractContentResource
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractContentResource {

	protected AbstractContentResource(MessageType messageType) {
		this.messageType = messageType;
	}
	
	public boolean isRequest() {
		return (this.messageType.equals(MessageType.Richiesta));
	}
	
	public boolean isResponse() {
		return (this.messageType.equals(MessageType.Risposta));
	}

	public abstract String getName(); 
	
	public abstract Object getValue();
	
	private MessageType messageType;

}
