/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

	public FSRecoveryDumpImpl( 
			Logger log,
			boolean debug,
			IDumpProducer dumpAppender,
			File directory, File directoryDLQ,
			int tentativi,
			int minutiAttesaProcessingFile) {
		super(log, debug, directory, directoryDLQ, tentativi, minutiAttesaProcessingFile);
		this.dumpAppender = dumpAppender;
	}

	@Override
	public void process(Connection connection) {
		this.log.info("Recovery Dump ...");
		super.process(connection);
		this.log.info("Recovery Dump completato");
	}

	@Override
	public void insertObject(File file, Connection connection) throws Exception {
		JaxbDeserializer deserializer = new JaxbDeserializer();
		DumpMessaggio dumpMessaggio = deserializer.readDumpMessaggio(file);
		
		//FARE UN WRAPPER COSI COME ABBIAMO FATTO PER TRACCIA E DIAGNOSTICO
		
		org.openspcoop2.protocol.sdk.dump.Messaggio messaggioOp2 = new org.openspcoop2.protocol.sdk.dump.Messaggio(dumpMessaggio);
		this.dumpAppender.dump(connection, messaggioOp2);
	}


}
