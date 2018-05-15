package org.openspcoop2.monitor.engine.fs_recovery;

import org.openspcoop2.monitor.engine.config.LoggerManager;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
 * FSRecoveryProcessor
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FSRecoveryProcessor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Logger logger = null;
		try {
			
			LoggerManager.initLogger();
			
			logger = LoggerWrapperFactory.getLogger("org.openspcoop2.monitor.engine.fs_recovery");
			
			logger.info("Avvio thread TimerFSRecoveryThread ...");
			TimerFSRecoveryThread fsRecoveryThread = new TimerFSRecoveryThread(new FSRecoveryConfig(true));
			fsRecoveryThread.start();
			logger.info("TimerFSRecoveryThread avviato con successo");
		} catch (Exception e) {
			if(logger==null){
				logger = LoggerWrapperFactory.getLogger(FSRecoveryProcessor.class);
			}
			logger.error("TimerFSRecoveryThread ha riscontrato un errore: "+e.getMessage(),e);
		}
	}

}
