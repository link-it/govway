package org.openspcoop2.pdd.core.controllo_traffico.policy.driver;

public enum TipoGestorePolicy {

	IN_MEMORY("inMemory"),
	WS("ws");
	
	private String tipo;
	
	public String getTipo() {
		return this.tipo;
	}

	TipoGestorePolicy(String tipo){
		this.tipo = tipo;
	}
	
}
