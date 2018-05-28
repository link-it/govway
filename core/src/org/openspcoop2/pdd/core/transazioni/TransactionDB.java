package org.openspcoop2.pdd.core.transazioni;

import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;

public class TransactionDB {
	
	private String idTransazione;
	private EsitoTransazione esitoTransazione;
	
	public String getIdTransazione() {
		return this.idTransazione;
	}
	public void setIdTransazione(String idTransazione) {
		this.idTransazione = idTransazione;
	}
	public EsitoTransazione getEsitoTransazione() {
		return this.esitoTransazione;
	}
	public void setEsitoTransazione(EsitoTransazione esito) {
		this.esitoTransazione = esito;
	}
	
}
