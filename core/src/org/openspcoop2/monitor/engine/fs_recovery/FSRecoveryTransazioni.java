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

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
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
	private FSRecoveryTransazioniApplicativoServerConsegnaTerminataImpl transazioniApplicativoServerConsegnaTerminataImpl;
	private FSRecoveryTracceImpl tracceImpl;
	private FSRecoveryDiagnosticiImpl diagnosticiImpl;
	private FSRecoveryDumpImpl dumpImpl;
	
	public FSRecoveryTransazioni( 
			Logger log,
			boolean debug,
			DAOFactory daoFactory, Logger daoFactoryLogger, ServiceManagerProperties daoFactoryServiceManagerProperties,
			long gestioneSerializableDBAttesaAttiva, int gestioneSerializableDBCheckInterval,
			org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM,
			ITracciaProducer tracciamentoAppender,
			IDiagnosticProducer diagnosticoAppender,
			IDumpProducer dumpAppender, boolean transazioniRegistrazioneDumpHeadersCompactEnabled,
			File directoryDiagnostici, File directoryDiagnosticiDLQ,
			File directoryTracce, File directoryTracceDLQ,
			File directoryDump, File directoryDumpDLQ,
			File directoryTransazioniApplicativoServer, File directoryTransazioniApplicativoServerDLQ,
			File directoryTransazioniApplicativoServerConsegnaTerminata, File directoryTransazioniApplicativoServerConsegnaTerminataDLQ,
			File directoryTransazioni, File directoryTransazioniDLQ,
			int tentativi, long msAttesaProcessingFile,
			long maxFileProcessed) {
	
		this.transazioniImpl = new FSRecoveryTransazioniImpl(log, debug, transazioniSM, directoryTransazioni, directoryTransazioniDLQ, tentativi, msAttesaProcessingFile, maxFileProcessed);
		this.transazioniApplicativoServerImpl = new FSRecoveryTransazioniApplicativoServerImpl(log, debug,
				daoFactory, daoFactoryLogger, daoFactoryServiceManagerProperties,
				gestioneSerializableDBAttesaAttiva, gestioneSerializableDBCheckInterval,
				transazioniSM, directoryTransazioniApplicativoServer, directoryTransazioniApplicativoServerDLQ, tentativi, msAttesaProcessingFile, maxFileProcessed);
		this.transazioniApplicativoServerConsegnaTerminataImpl = new FSRecoveryTransazioniApplicativoServerConsegnaTerminataImpl(log, debug,
				daoFactory, daoFactoryLogger, daoFactoryServiceManagerProperties,
				gestioneSerializableDBAttesaAttiva, gestioneSerializableDBCheckInterval,
				transazioniSM, directoryTransazioniApplicativoServerConsegnaTerminata, directoryTransazioniApplicativoServerConsegnaTerminataDLQ, tentativi, msAttesaProcessingFile, maxFileProcessed);
		this.tracceImpl = new FSRecoveryTracceImpl(log, debug, tracciamentoAppender, directoryTracce, directoryTracceDLQ, tentativi, msAttesaProcessingFile, maxFileProcessed);
		this.dumpImpl = new FSRecoveryDumpImpl(log, debug, dumpAppender, directoryDump, directoryDumpDLQ, tentativi, msAttesaProcessingFile, maxFileProcessed);
		this.dumpImpl.setTransazioniRegistrazioneDumpHeadersCompactEnabled(transazioniRegistrazioneDumpHeadersCompactEnabled);
		this.diagnosticiImpl = new FSRecoveryDiagnosticiImpl(log, debug, diagnosticoAppender, directoryDiagnostici, directoryDiagnosticiDLQ, tentativi, msAttesaProcessingFile, maxFileProcessed);
	}
	
	public long process(FSRecoveryObjectType objectType, Connection connection){
		
		boolean all = false;
		if(objectType==null || FSRecoveryObjectType.TRANSAZIONI_GESTIONE_COMPLETA.equals(objectType)) {
			all = true;
		}
		
		long l = 0;
		if(all || FSRecoveryObjectType.TRANSAZIONI.equals(objectType)) {
			long lInternal = processTransazioni(connection);
			if(lInternal<0) {
				return lInternal;
			}
			l+=lInternal;
		}
		if(all || FSRecoveryObjectType.TRANSAZIONI_SERVER.equals(objectType)) {
			long lInternal = processTransazioniApplicativoServer(connection);
			if(lInternal<0) {
				return lInternal;
			}
			l+=lInternal;
		}
		if(all || FSRecoveryObjectType.TRANSAZIONI_SERVER_CONSEGNA_TERMINATA.equals(objectType)) {
			long lInternal = processTransazioniApplicativoServerConsegnaTerminata(connection);
			if(lInternal<0) {
				return lInternal;
			}
			l+=lInternal;
		}
		if(all || FSRecoveryObjectType.TRANSAZIONI_TRACCE.equals(objectType)) {
			long lInternal = processTracce(connection);
			if(lInternal<0) {
				return lInternal;
			}
			l+=lInternal;
		}
		if(all || FSRecoveryObjectType.TRANSAZIONI_DIAGNOSTICI.equals(objectType)) {
			long lInternal = processDiagnostici(connection);
			if(lInternal<0) {
				return lInternal;
			}
			l+=lInternal;
		}
		if(all || FSRecoveryObjectType.TRANSAZIONI_DUMP.equals(objectType)) {
			long lInternal = processDump(connection);
			if(lInternal<0) {
				return lInternal;
			}
			l+=lInternal;
		}
		
		return l;
	}
	
	private long processTransazioni(Connection connection){
		return this.transazioniImpl.process(connection);
	}
	private long processTransazioniApplicativoServer(Connection connection){
		return this.transazioniApplicativoServerImpl.process(connection);
	}
	private long processTransazioniApplicativoServerConsegnaTerminata(Connection connection){
		return this.transazioniApplicativoServerConsegnaTerminataImpl.process(connection);
	}
	private long processTracce(Connection connection){
		return this.tracceImpl.process(connection);
	}
	private long processDiagnostici(Connection connection){
		return this.diagnosticiImpl.process(connection);
	}
	private long processDump(Connection connection){
		return this.dumpImpl.process(connection);
	}
	
}
