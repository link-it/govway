package org.openspcoop2.pdd.core.handlers.transazioni;

import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.pdd.core.handlers.IntegrationManagerResponseContext;
import org.openspcoop2.pdd.core.handlers.PostOutResponseContext;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.slf4j.Logger;

public interface ISalvataggioDiagnosticiManager {

	public StatoSalvataggioDiagnostici getInformazioniSalvataggioDiagnostici(Logger log,
			PostOutResponseContext context,
			Transaction transaction,
			Transazione transazioneDTO,
			boolean pddStateless) throws Exception;
	
	public StatoSalvataggioDiagnostici getInformazioniSalvataggioDiagnostici(Logger log,
			IntegrationManagerResponseContext context,
			Transaction transaction,
			Transazione transazioneDTO,
			boolean pddStateless) throws Exception;

}
