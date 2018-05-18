package org.openspcoop2.monitor.engine.statistic;

import org.apache.logging.log4j.Level;
import org.openspcoop2.monitor.engine.config.LoggerManager;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
 * StatisticProcessor
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticProcessor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Logger logCore = null;
		try {
			LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ERROR);
						
			LoggerManager.initLogger();
			
			logCore = LoggerWrapperFactory.getLogger("org.openspcoop2.monitor.engine.statistic");
			
			logCore.info("Avvio thread TimerStatisticheThread ...");
			TimerStatisticheThread statThread = new TimerStatisticheThread(new StatisticsConfig(true));
			statThread.start();
			logCore.info("TimerStatisticheThread avviato con successo");
		} catch (Exception e) {
			if(logCore==null){
				logCore = LoggerWrapperFactory.getLogger(StatisticProcessor.class);
			}
			logCore.error("TimerStatisticheThread ha riscontrato un errore: "+e.getMessage(),e);
		}
	}

}
