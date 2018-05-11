package org.openspcoop2.monitor.sdk.plugins;

import org.openspcoop2.monitor.sdk.exceptions.TransactionException;
import org.openspcoop2.monitor.sdk.transaction.Transaction;

/**
 * ITransactionProcessing
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface ITransactionProcessing {

	public void postProcessTransaction(Transaction transaction) throws TransactionException;
	
	public void processRealTimeResourcesBeforeSaveOnDatabase(Transaction transaction) throws TransactionException; // pdd
	public void processRealTimeResourcesAfterReadFromDatabase(Transaction transaction) throws TransactionException; // pddMonitor

}
