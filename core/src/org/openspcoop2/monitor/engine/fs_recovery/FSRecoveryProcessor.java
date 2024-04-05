/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import org.apache.logging.log4j.Level;
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
			LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ERROR);
			
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
