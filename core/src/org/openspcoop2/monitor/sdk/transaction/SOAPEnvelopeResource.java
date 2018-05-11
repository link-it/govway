package org.openspcoop2.monitor.sdk.transaction;

import org.openspcoop2.monitor.sdk.constants.ContentResourceNames;
import org.openspcoop2.monitor.sdk.constants.MessageType;

/**
 * SOAPEnvelopeResource
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SOAPEnvelopeResource extends AbstractContentResource {
	
	public SOAPEnvelopeResource(MessageType messageType) {
		super(messageType);
	}

	@Override
	public String getName() {
		if (this.isRequest())
			return ContentResourceNames.REQ_SOAP_ENVELOPE;
		else
			return ContentResourceNames.RES_SOAP_ENVELOPE;
	}
	
	@Override
	public String getValue() {
		return this.soapEnvelope;
	}
	
	private String soapEnvelope = "";
}
