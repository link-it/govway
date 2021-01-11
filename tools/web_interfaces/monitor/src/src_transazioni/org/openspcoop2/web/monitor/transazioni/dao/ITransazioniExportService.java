/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.web.monitor.transazioni.dao;

import org.openspcoop2.core.transazioni.TransazioneExport;
import org.openspcoop2.web.monitor.core.dao.IService;

import java.util.Date;

/**
 * ITransazioniExportService
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public interface ITransazioniExportService extends
		IService<TransazioneExport, Long> {

	/**
	 * Se esiste una entry con questo intervallo, la restituisce, altrimenti
	 * istanzia una nuova entry impostando come intervallo i parametri passati;
	 * 
	 * @param dataInizio
	 * @param dataFine
	 * @return la entry contenuta nell'intervallo
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
