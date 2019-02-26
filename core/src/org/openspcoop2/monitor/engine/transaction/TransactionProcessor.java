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
package org.openspcoop2.monitor.engine.transaction;

import org.apache.logging.log4j.Level;
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
		
		LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ERROR);
		
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
