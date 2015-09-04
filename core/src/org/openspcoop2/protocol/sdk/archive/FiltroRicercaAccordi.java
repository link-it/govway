package org.openspcoop2.protocol.sdk.archive;

import org.openspcoop2.core.id.IDSoggetto;

public class FiltroRicercaAccordi {

	private String nome;
	private Integer versione;
	private IDSoggetto soggetto;
	
	public String getNome() {
		return this.nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Integer getVersione() {
		return this.versione;
	}
	public void setVersione(Integer versione) {
		this.versione = versione;
	}
	public IDSoggetto getSoggetto() {
		return this.soggetto;
	}
	public void setSoggetto(IDSoggetto soggetto) {
		this.soggetto = soggetto;
	}
}
