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

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.monitor.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticProducer;
import org.openspcoop2.protocol.sdk.dump.IDumpProducer;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaProducer;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

import java.io.File;
import java.sql.Connection;

/**
 * FSRecoveryLibrary
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FSRecoveryLibrary {
	
	private FSRecoveryLibrary() {}

	public static void generate(FSRecoveryConfig config,
			DAOFactory daoFactory, Logger daoFactoryLogger, ServiceManagerProperties daoFactoryServiceManagerProperties,
			long gestioneSerializableDBAttesaAttiva, int gestioneSerializableDBCheckInterval,
			org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM,
			ITracciaProducer tracciamentoAppender,
			IDiagnosticProducer diagnosticoAppender,
			IDumpProducer dumpAppender, boolean transazioniRegistrazioneDumpHeadersCompactEnabled,
			org.openspcoop2.core.eventi.dao.IServiceManager pluginsEventiSM){
		generate(config, 
				daoFactory, daoFactoryLogger, daoFactoryServiceManagerProperties,
				gestioneSerializableDBAttesaAttiva, gestioneSerializableDBCheckInterval,
				transazioniSM, tracciamentoAppender, diagnosticoAppender, 
				dumpAppender, transazioniRegistrazioneDumpHeadersCompactEnabled,
				pluginsEventiSM, null, null);
	}
	public static long generate(FSRecoveryConfig config,
			DAOFactory daoFactory, Logger daoFactoryLogger, ServiceManagerProperties daoFactoryServiceManagerProperties,
			long gestioneSerializableDBAttesaAttiva, int gestioneSerializableDBCheckInterval,
			org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM,
			ITracciaProducer tracciamentoAppender,
			IDiagnosticProducer diagnosticoAppender,
			IDumpProducer dumpAppender, boolean transazioniRegistrazioneDumpHeadersCompactEnabled,
			org.openspcoop2.core.eventi.dao.IServiceManager pluginsEventiSM,
			Connection connection,
			FSRecoveryObjectType objectType){
		try{
			
			File dir =  new File(config.getRepository());
			checkDir(dir, false, false);
			
			long l = 0;
			
			if(config.isRipristinoEventi() && 
					(objectType==null || objectType.isGestioneEventi())){
				if(config.isDebug()){
					config.getLogCore().debug("Esecuzione thread per ripristino eventi ....");
				}
				
				File dirEventi = new File(dir,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_EVENTI);
				checkDir(dirEventi, true, false);
				File dirEventiDLQ = new File(dirEventi,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_DLQ);
				checkDir(dirEventiDLQ, true, true);
				
				FSRecoveryEventi fs = new FSRecoveryEventi(config.getLogCore(),config.isDebug(), pluginsEventiSM,
						dirEventi,dirEventiDLQ, config.getTentativi(), config.getProcessingEventFileAfterMs(), config.getMaxFileLimit());
				long lInternal = fs.process(objectType);
				if(lInternal<0) {
					return lInternal;
				}
				l+=lInternal;
				
				if(config.isDebug()){
					config.getLogCore().debug("Esecuzione thread per ripristino eventi terminato");
				}
			}else{
				if(config.isDebug()){
					config.getLogCore().debug("Thread per ripristino eventi disabilitato");
				}
			}
			
			if(config.isRipristinoTransazioni() && 
					(objectType==null || objectType.isGestioneTransazionii())){
				if(config.isDebug()){
					config.getLogCore().debug("Esecuzione thread per ripristino transazioni ....");
				}
				
				File dirDiagnostici = new File(dir,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_DIAGNOSTICO);
				checkDir(dirDiagnostici, true, false);
				File dirDiagnosticiDLQ = new File(dirDiagnostici,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_DLQ);
				checkDir(dirDiagnosticiDLQ, true, true);
				
				File dirTracce = new File(dir,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_TRACCIA);
				checkDir(dirTracce, true, false);
				File dirTracceDLQ = new File(dirTracce,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_DLQ);
				checkDir(dirTracceDLQ, true, true);
				
				File dirDump = new File(dir,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_DUMP);
				checkDir(dirDump, true, false);
				File dirDumpDLQ = new File(dirDump,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_DLQ);
				checkDir(dirDumpDLQ, true, true);
				
				File dirTransazioniApplicativoServer = new File(dir,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_TRANSAZIONE_APPLICATIVO_SERVER);
				checkDir(dirTransazioniApplicativoServer, true, false);
				File dirTransazioniApplicativoServerDLQ = new File(dirTransazioniApplicativoServer,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_DLQ);
				checkDir(dirTransazioniApplicativoServerDLQ, true, true);
				
				File dirTransazioniApplicativoServerConsegnaTerminata = new File(dir,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_TRANSAZIONE_APPLICATIVO_SERVER_CONSEGNA_TERMINATA);
				checkDir(dirTransazioniApplicativoServerConsegnaTerminata, true, false);
				File dirTransazioniApplicativoServerConsegnaTerminataDLQ = new File(dirTransazioniApplicativoServerConsegnaTerminata,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_DLQ);
				checkDir(dirTransazioniApplicativoServerConsegnaTerminataDLQ, true, true);
				
				File dirTransazioni = new File(dir,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_TRANSAZIONE);
				checkDir(dirTransazioni, true, false);
				File dirTransazioniDLQ = new File(dirTransazioni,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_DLQ);
				checkDir(dirTransazioniDLQ, true, true);
				
				FSRecoveryTransazioni fs = new FSRecoveryTransazioni(config.getLogCore(),config.isDebug(),
						daoFactory, daoFactoryLogger, daoFactoryServiceManagerProperties,
						gestioneSerializableDBAttesaAttiva, gestioneSerializableDBCheckInterval,
						transazioniSM,
						tracciamentoAppender, diagnosticoAppender, 
						dumpAppender, transazioniRegistrazioneDumpHeadersCompactEnabled, 
						dirDiagnostici,dirDiagnosticiDLQ,
						dirTracce,dirTracceDLQ,
						dirDump,dirDumpDLQ,
						dirTransazioniApplicativoServer, dirTransazioniApplicativoServerDLQ,
						dirTransazioniApplicativoServerConsegnaTerminata, dirTransazioniApplicativoServerConsegnaTerminataDLQ,
						dirTransazioni,dirTransazioniDLQ,
						config.getTentativi(), config.getProcessingTransactionFileAfterMs(), config.getMaxFileLimit());
				long lInternal = fs.process(objectType, connection);
				if(lInternal<0) {
					return lInternal;
				}
				l+=lInternal;
				
				if(config.isDebug()){
					config.getLogCore().debug("Esecuzione thread per ripristino transazioni terminato");
				}
			}else{
				if(config.isDebug()){
					config.getLogCore().debug("Thread per ripristino transazioni disabilitato");
				}
			}
			
			return l;
			
		}catch(Exception e){
			config.getLogCore().error("Errore durante il recovery da file system: "+e.getMessage(),e);
			return -1;
		} 
	}
	
	private static void checkDir(File dir, boolean checkWritable, boolean create) throws UtilsException{
		String prefix = "Directory ["+dir.getAbsolutePath()+"] ";
		if(!dir.exists()){
			if(create){
				if(!dir.mkdir()){
					throw new UtilsException(prefix+"non esistente e creazione non riuscita");
				}
			}else{
				throw new UtilsException(prefix+"non esistente");
			}
		}
		if(!dir.canRead()){
			throw new UtilsException(prefix+"non accessibile in lettura");
		}
		if(checkWritable &&
			!dir.canWrite()){
			throw new UtilsException(prefix+"non accessibile in scrittura");
		}
	}
	
}
