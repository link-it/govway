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
package org.openspcoop2.monitor.engine.fs_recovery;

import java.io.File;
import java.sql.Connection;

import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticProducer;
import org.openspcoop2.protocol.sdk.dump.IDumpProducer;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaProducer;
import org.slf4j.Logger;


/**
 * FSRecoveryTransazioni
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FSRecoveryTransazioni {

	private FSRecoveryTransazioniImpl transazioniImpl;
	private FSRecoveryTransazioniApplicativoServerImpl transazioniApplicativoServerImpl;
	private FSRecoveryTracceImpl tracceImpl;
	private FSRecoveryDiagnosticiImpl diagnosticiImpl;
	private FSRecoveryDumpImpl dumpImpl;
	private static final int MINUTI_ATTESA_PROCESSING_FILE = 1;
	
	public FSRecoveryTransazioni( 
			Logger log,
			boolean debug,
			org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM,
			ITracciaProducer tracciamentoAppender,
			IDiagnosticProducer diagnosticoAppender,
			IDumpProducer dumpAppender,
			File directoryDiagnostici, File directoryDiagnosticiDLQ,
			File directoryTracce, File directoryTracceDLQ,
			File directoryDump, File directoryDumpDLQ,
			File directoryTransazioniApplicativoServer, File directoryTransazioniApplicativoServerDLQ,
			File directoryTransazioni, File directoryTransazioniDLQ,
			int tentativi) {
	
		this.transazioniImpl = new FSRecoveryTransazioniImpl(log, debug, transazioniSM, directoryTransazioni, directoryTransazioniDLQ, tentativi, MINUTI_ATTESA_PROCESSING_FILE);
		this.transazioniApplicativoServerImpl = new FSRecoveryTransazioniApplicativoServerImpl(log, debug, transazioniSM, directoryTransazioniApplicativoServer, directoryTransazioniApplicativoServerDLQ, tentativi, MINUTI_ATTESA_PROCESSING_FILE);
		this.tracceImpl = new FSRecoveryTracceImpl(log, debug, tracciamentoAppender, directoryTracce, directoryTracceDLQ, tentativi, MINUTI_ATTESA_PROCESSING_FILE);
		this.dumpImpl = new FSRecoveryDumpImpl(log, debug, dumpAppender, directoryDump, directoryDumpDLQ, tentativi, MINUTI_ATTESA_PROCESSING_FILE);
		this.diagnosticiImpl = new FSRecoveryDiagnosticiImpl(log, debug, diagnosticoAppender, directoryDiagnostici, directoryDiagnosticiDLQ, tentativi, MINUTI_ATTESA_PROCESSING_FILE);
	}
	
	public void process(Connection connection){
		
		this.transazioniImpl.process(connection);
		this.transazioniApplicativoServerImpl.process(connection);
		this.tracceImpl.process(connection);
		this.diagnosticiImpl.process(connection);
		this.dumpImpl.process(connection);
	}
	
}
