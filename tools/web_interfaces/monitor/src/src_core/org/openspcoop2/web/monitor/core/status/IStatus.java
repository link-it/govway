package org.openspcoop2.web.monitor.core.status;

import java.io.Serializable;

public interface IStatus extends Serializable{

	// Nome dell'oggetto di cui si vuole visualizzare lo stato
	public String getNome();
	public void setNome(String nome);

	// Stato dell'oggetto
	public SondaStatus getStato();
	public void setStato(SondaStatus stato);
	// Stato dell'oggetto in stringa
	

	// Descrizione dello stato rilevato
	public String getDescrizione();
	public void setDescrizione(String descrizione);
	
}
