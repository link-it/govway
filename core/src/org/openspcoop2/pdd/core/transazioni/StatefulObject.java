package org.openspcoop2.pdd.core.transazioni;

public class StatefulObject {

	private String idTransazione;
	private Object object;
	private StatefulObjectType type;
	private String protocollo;
	
	public StatefulObject(String protocollo){
		this.protocollo = protocollo;
	}
	
	public String getProtocollo() {
		return this.protocollo;
	}

	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}
	public String getIdTransazione() {
		return this.idTransazione;
	}
	public void setIdTransazione(String idTransazione) {
		this.idTransazione = idTransazione;
	}
	public Object getObject() {
		return this.object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	public StatefulObjectType getType() {
		return this.type;
	}
	public void setType(StatefulObjectType type) {
		this.type = type;
	}
	
}
