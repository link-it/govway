/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
	public void processRealTimeResourcesAfterReadFromDatabase(Transaction transaction) throws TransactionException; // govwayMonitor

}
