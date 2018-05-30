package org.openspcoop2.pdd.core.handlers.transazioni;

import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.pdd.core.handlers.PostOutResponseContext;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.slf4j.Logger;

public interface ISalvataggioTracceManager {

	public StatoSalvataggioTracce getInformazioniSalvataggioTracciaRichiesta(Logger log,
			PostOutResponseContext context,
			Transaction transaction,
			Transazione transazioneDTO,
			boolean pddStateless) throws Exception;
	
	public StatoSalvataggioTracce getInformazioniSalvataggioTracciaRisposta(Logger log,
			PostOutResponseContext context,
			Transaction transaction,
			Transazione transazioneDTO,
			boolean pddStateless) throws Exception;
	
}
