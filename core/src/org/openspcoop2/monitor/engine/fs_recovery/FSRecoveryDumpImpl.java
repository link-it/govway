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
package org.openspcoop2.monitor.engine.fs_recovery;

import java.io.File;
import java.sql.Connection;

import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.utils.serializer.JaxbDeserializer;
import org.openspcoop2.protocol.sdk.dump.IDumpProducer;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsMultiException;
import org.slf4j.Logger;

/**
 * FSRecoveryDumpImpl
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FSRecoveryDumpImpl extends AbstractFSRecovery {
	private IDumpProducer dumpAppender;
	private boolean transazioniRegistrazioneDumpHeadersCompactEnabled = false;

	public FSRecoveryDumpImpl( 
			Logger log,
			boolean debug,
			IDumpProducer dumpAppender,
			File directory, File directoryDLQ,
			int tentativi,
			long msAttesaProcessingFile,
			long maxFileProcessed) {
		super(log, debug, directory, directoryDLQ, tentativi, msAttesaProcessingFile, maxFileProcessed);
		this.dumpAppender = dumpAppender;
	}

	public void setTransazioniRegistrazioneDumpHeadersCompactEnabled(
			boolean transazioniRegistrazioneDumpHeadersCompactEnabled) {
		this.transazioniRegistrazioneDumpHeadersCompactEnabled = transazioniRegistrazioneDumpHeadersCompactEnabled;
	}
	
	@Override
	public long process(Connection connection) {
		this.log.info("Recovery Dump ...");
		long l = super.process(connection);
		this.log.info("Recovery Dump completato");
		return l;
	}

	@Override
	public void insertObject(File file, Connection connection) throws UtilsException, UtilsMultiException {
		JaxbDeserializer deserializer = new JaxbDeserializer();
		DumpMessaggio dumpMessaggio = null;
		try {
			dumpMessaggio = deserializer.readDumpMessaggio(file);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		
		//FARE UN WRAPPER COSI COME ABBIAMO FATTO PER TRACCIA E DIAGNOSTICO
		
		org.openspcoop2.protocol.sdk.dump.Messaggio messaggioOp2 = new org.openspcoop2.protocol.sdk.dump.Messaggio(dumpMessaggio);
		try {
			this.dumpAppender.dump(connection, messaggioOp2, this.transazioniRegistrazioneDumpHeadersCompactEnabled);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		
	}


}
