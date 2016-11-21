package org.openspcoop2.protocol.spcoop;

import javax.xml.soap.SOAPHeaderElement;

import org.openspcoop2.protocol.basic.Utilities;
import org.openspcoop2.protocol.sdk.BustaRawContent;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;

public class SPCoopBustaRawContent extends BustaRawContent<SOAPHeaderElement> {

	public SPCoopBustaRawContent(SOAPHeaderElement element) {
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
