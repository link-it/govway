package org.openspcoop2.protocol.sdk;

import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;

public abstract class BustaRawContent<T> {

	protected T element;
	
	public BustaRawContent(T element){
		this.element = element;
	}
	
	public T getElement() {
		return this.element;
	}
	
	public abstract String toString(TipoSerializzazione tipoSerializzazione) throws ProtocolException;
	
	public abstract byte[] toByteArray(TipoSerializzazione tipoSerializzazione) throws ProtocolException;
}
