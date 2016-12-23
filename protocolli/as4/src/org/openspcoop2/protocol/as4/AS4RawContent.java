package org.openspcoop2.protocol.as4;

import javax.xml.soap.SOAPEnvelope;

import org.openspcoop2.protocol.basic.Utilities;
import org.openspcoop2.protocol.sdk.BustaRawContent;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;

public class AS4RawContent extends BustaRawContent<SOAPEnvelope> {

	public AS4RawContent(SOAPEnvelope element) {
		super(element);
	}
	
	@Override
	public String toString(TipoSerializzazione tipoSerializzazione) throws ProtocolException{
		switch (tipoSerializzazione) {
		case XML:
		case DEFAULT:
			return Utilities.toString(this.element,false);
		default:
			throw new ProtocolException("Tipo di serializzazione ["+tipoSerializzazione+"] non supportata");
		}
	}
	
	@Override
	public byte[] toByteArray(TipoSerializzazione tipoSerializzazione) throws ProtocolException{
		switch (tipoSerializzazione) {
		case XML:
		case DEFAULT:
			return Utilities.toByteArray(this.element,false);
		default:
			throw new ProtocolException("Tipo di serializzazione ["+tipoSerializzazione+"] non supportata");
		}
	}

}
