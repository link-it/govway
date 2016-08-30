/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


package org.openspcoop2.web.ctrlstat.gestori;

import java.util.ArrayList;
import java.util.List;

import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;

import org.slf4j.Logger;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.lib.queue.ClassQueue;
import org.openspcoop2.web.lib.queue.dao.Operation;

/**
 * GestoreGeneral
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public abstract class GestoreGeneral implements IGestore, Runnable {

	/**
	 * Filtra le operation con stesso identificativo, settando lo stato a
	 * SUCCESS
	 * 
	 * @param idOperation
	 *            l'identificativo delle operation da filtrare
	 * @param operationManager
	 *            la libreria per la scrittura nel db operation
	 * @param originalOperation
	 *            l'operazione orginale che funge da filtro per le altre
	 * @throws Exception
	 */
	public List<Operation> filterOperations(String idOperazione, ClassQueue operationManager, Operation originalOperation, QueueSession qs, Queue queue, Logger log) throws Exception {

		List<Operation> filteredOperations = new ArrayList<Operation>();

		// bug fix: il filtro puo contenere caratteri ' ma devono essere
		// preceduti da un '
		// ad esempio: "literal's" deve essere trasformato in "literal''s
		// escaping filter
		String escapedFilter = idOperazione.replaceAll("'", "''");
		String strMessageSelector = "ID = '" + escapedFilter + "'";
		
		QueueReceiver filtro = null;
		try {
			filtro = qs.createReceiver(queue, strMessageSelector);
			ObjectMessage operazioneFiltrata = (ObjectMessage) filtro.receive(CostantiControlStation.JMS_FILTER);

			while (operazioneFiltrata != null) {
				
				// Ricezione Operazione
				Object objFilter;
				try {
					objFilter = operazioneFiltrata.getObject();
				} catch (Exception e) {
					log.error(getName() + ": Impossibile recuperare il messaggio :" + e.toString(), e);
					continue;
				}

				int idOpFiltrata = Integer.parseInt(objFilter.toString());
				if (idOpFiltrata == 0) {
					log.error(getName() + ": Impossibile recuperare l'id del messaggio da filtrare.");
					continue;
				}

				Operation operazioneDaFiltrare = operationManager.getOperation(idOpFiltrata);

				operationManager.filterOperation(operazioneDaFiltrare, originalOperation);

				filteredOperations.add(operazioneDaFiltrare);
				
				// ricevo altra operazione duplicata se presente
				operazioneFiltrata = (ObjectMessage) filtro.receive(CostantiControlStation.JMS_FILTER);
			}
			
			return filteredOperations;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (filtro != null)
					filtro.close();
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * Nome del Gestore
	 * 
	 * @return Il nome del Gestore
	 */
	protected abstract String getName();

}
