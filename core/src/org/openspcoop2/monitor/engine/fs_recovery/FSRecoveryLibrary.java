package org.openspcoop2.monitor.engine.fs_recovery;

import org.openspcoop2.monitor.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticProducer;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaProducer;

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

	public static void generate(FSRecoveryConfig config,
			org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM,
			ITracciaProducer tracciamentoAppender,
			IDiagnosticProducer diagnosticoAppender,
			org.openspcoop2.core.eventi.dao.IServiceManager pluginsEventiSM){
		generate(config, transazioniSM, tracciamentoAppender, diagnosticoAppender, pluginsEventiSM, null);
	}
	public static void generate(FSRecoveryConfig config,
			org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM,
			ITracciaProducer tracciamentoAppender,
			IDiagnosticProducer diagnosticoAppender,
			org.openspcoop2.core.eventi.dao.IServiceManager pluginsEventiSM,
			Connection connection){
		try{
			
			File dir =  new File(config.getRepository());
			checkDir(dir, false, false);
			
			if(config.isRipristinoEventi()){
				if(config.isDebug()){
					config.getLogCore().debug("Esecuzione thread per ripristino eventi ....");
				}
				
				File dirEventi = new File(dir,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_EVENTI);
				checkDir(dirEventi, true, false);
				File dirEventiDLQ = new File(dirEventi,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_DLQ);
				checkDir(dirEventiDLQ, true, true);
				
				FSRecoveryEventi fs = new FSRecoveryEventi(config.getLogCore(),config.isDebug(), pluginsEventiSM,
						dirEventi,dirEventiDLQ, config.getTentativi());
				fs.process();
				
				if(config.isDebug()){
					config.getLogCore().debug("Esecuzione thread per ripristino eventi terminato");
				}
			}else{
				if(config.isDebug()){
					config.getLogCore().debug("Thread per ripristino eventi disabilitato");
				}
			}
			
			if(config.isRipristinoTransazioni()){
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
				
				File dirTransazioni = new File(dir,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_TRANSAZIONE);
				checkDir(dirTransazioni, true, false);
				File dirTransazioniDLQ = new File(dirTransazioni,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_DLQ);
				checkDir(dirTransazioniDLQ, true, true);
				
				FSRecoveryTransazioni fs = new FSRecoveryTransazioni(config.getLogCore(),config.isDebug(),transazioniSM,
						tracciamentoAppender, diagnosticoAppender, 
						dirDiagnostici,dirDiagnosticiDLQ,
						dirTracce,dirTracceDLQ,
						dirTransazioni,dirTransazioniDLQ,
						config.getTentativi());
				fs.process(connection);
				
				if(config.isDebug()){
					config.getLogCore().debug("Esecuzione thread per ripristino transazioni terminato");
				}
			}else{
				if(config.isDebug()){
					config.getLogCore().debug("Thread per ripristino transazioni disabilitato");
				}
			}
			
		}catch(Exception e){
			config.getLogCore().error("Errore durante il recovery da file system: "+e.getMessage(),e);
		} 
	}
	
	private static void checkDir(File dir, boolean checkWritable, boolean create) throws Exception{
		if(dir.exists()==false){
			if(create){
				if(dir.mkdir()==false){
					throw new Exception("Directory ["+dir.getAbsolutePath()+"] non esistente e creazione non riuscita");
				}
			}else{
				throw new Exception("Directory ["+dir.getAbsolutePath()+"] non esistente");
			}
		}
		if(dir.canRead()==false){
			throw new Exception("Directory ["+dir.getAbsolutePath()+"] non accessibile in lettura");
		}
		if(checkWritable){
			if(dir.canWrite()==false){
				throw new Exception("Directory ["+dir.getAbsolutePath()+"] non accessibile in scrittura");
			}
		}
	}
	
}
