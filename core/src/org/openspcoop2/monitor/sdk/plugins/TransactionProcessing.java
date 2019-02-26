/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.monitor.sdk.plugins;

import org.openspcoop2.monitor.sdk.exceptions.TransactionException;
import org.openspcoop2.monitor.sdk.transaction.SLATrace;
import org.openspcoop2.monitor.sdk.transaction.Transaction;

/**
 * StatisticProcessing
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransactionProcessing implements ITransactionProcessing {
	
	@Override
	public void postProcessTransaction(Transaction transaction) throws TransactionException {
		/** implementazione che:
		* - non modifica i contenuti inseriti dalla PdD
		* - non verifica alcuna condizione di allarme
		* - inserisce tempoRisposta = tempoElaborazione (caso sincrono)
		* - inserisce numero di record a -1 e quindi non determinabile
		*/
		SLATrace trace = new SLATrace();
		trace.setResponseTime((transaction.getTransaction().getDataUscitaRisposta().getTime()-transaction.getTransaction().getDataIngressoRichiesta().getTime()));
		trace.setReturnedRecordNumber(-1);
		transaction.setSlaTrace(trace);
	}

	@Override
	public void processRealTimeResourcesBeforeSaveOnDatabase(
			Transaction transaction) throws TransactionException {
		// nop;
	}

	@Override
	public void processRealTimeResourcesAfterReadFromDatabase(
			Transaction transaction) throws TransactionException {
		// nop;
	}

	
}
