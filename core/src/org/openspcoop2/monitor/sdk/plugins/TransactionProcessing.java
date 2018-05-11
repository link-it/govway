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
