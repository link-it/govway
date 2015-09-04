package org.openspcoop2.protocol.sdk.archive;

import org.openspcoop2.core.id.IDSoggetto;

public class FiltroRicercaServizi {

	private String tipo;
	private String nome;
	private IDSoggetto soggetto;

	public String getTipo() {
		return this.tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getNome() {
		return this.nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public IDSoggetto getSoggetto() {
		return this.soggetto;
	}
	public void setSoggetto(IDSoggetto soggetto) {
		this.soggetto = soggetto;
	}
	
}
	

