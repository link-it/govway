package org.openspcoop2.web.monitor.core.status;

import java.io.Serializable;
import java.util.List;

public interface ISondaPdd extends Serializable{
	
	// Aggiorna e restituisce lo stato della sonda
	public List<IStatus> updateStato() throws Exception;
	
	// Restituisce lo stato attuale della sonda
	public List<IStatus> getStato() throws Exception;
	
	// Reset delle metriche che definiscono lo stato
	public void reset() throws Exception;

	// Nome dell sonda
	public String getName();
//	public void setName(String name);
	
	// Identificativo della sonda all'interno del file di properties
	public String getIdentificativo();
	
	// Restituisce lo stato generale della sonda 
	public SondaStatus getStatoSondaPdd() throws Exception;
	
	// Restituisce il messaggio che descrive lo stato generale della sonda
	public String getMessaggioStatoSondaPdd() throws Exception;
	
	// Restituisce un link diretto alla pagina di dettaglio
	public String getLinkDettaglio() throws Exception;
	
}
