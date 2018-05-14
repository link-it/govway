package org.openspcoop2.web.monitor.transazioni.dao;

import it.link.pdd.core.transazioni.TransazioneExport;
import org.openspcoop2.web.monitor.core.dao.IService;

import java.util.Date;

public interface ITransazioniExportService extends
		IService<TransazioneExport, Long> {

	/**
	 * Se esiste una entry con questo intervallo, la restituisce, altrimenti
	 * istanzia una nuova entry impostando come intervallo i parametri passati;
	 * 
	 * @param dataInizio
	 * @param dataFine
	 * @return
	 */
	public TransazioneExport getByIntervallo(Date dataInizio, Date dataFine);

	/**
	 * Recupera la entry piu vecchia che possiee un
	 * 
	 * (export_state='completed' and delete_state='undefined') //eliminazione
	 * intervallo mai effettuata or (delete_state='error') //eleiminazione
	 * precedente effettuata ma terminata con errore or
	 * (delete_state='executing') //eliminaziona precedente crashata
	 * 
	 * 
	 * @return la entry individuata, oppure null se non ci sono entry che
	 *         soddisfano le condizioni
	 */
	public TransazioneExport getOldestForDelete() throws Exception;
}
