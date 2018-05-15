package org.openspcoop2.monitor.engine.transaction;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.monitor.engine.config.LoggerManager;
import org.openspcoop2.monitor.engine.config.MonitorProperties;
import org.openspcoop2.monitor.engine.constants.CostantiConfigurazione;

import org.slf4j.Logger;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.Loader;

/**
 * TransactionProcessor
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransactionProcessor {
	
	public static void main(String[] args) throws Exception {
		
		LoggerManager.initLogger();
		
		Logger logCore = LoggerWrapperFactory.getLogger("org.openspcoop2.monitor.engine.transaction");
		Logger logSql = LoggerWrapperFactory.getLogger("org.openspcoop2.monitor.engine.transaction.sql");
		
		boolean debug = true;
		int poolSize = 5;
		int msgForThread = 10;
		
		String protocolloDefault = MonitorProperties.getInstance(logCore).getProperty(CostantiConfigurazione.PDD_MONITOR_DEFAULT_PROTOCOL, false, true);
		ConfigurazionePdD configPdD = new ConfigurazionePdD();
		configPdD.setLoader(new Loader());
		configPdD.setLog(logCore);
		configPdD.setAttesaAttivaJDBC(-1);
		configPdD.setCheckIntervalJDBC(-1);
		configPdD.setConfigurationDir(null);
		ProtocolFactoryManager.initialize(logCore, configPdD, protocolloDefault);
		
		DAOFactory daoFactory = DAOFactory.getInstance(logSql);
		
		TransactionLibrary.process(logCore,daoFactory,debug,poolSize,msgForThread);
	}

}
