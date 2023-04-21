/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.spcoop.testsuite.core;

import java.io.InputStream;

import org.slf4j.Logger;
import org.openspcoop2.utils.LoggerWrapperFactory;

/**
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SPCoopTestsuiteLogger {

	/**  Logger log4j utilizzato per scrivere i tracciamenti */
	protected static Logger logger = null;

	public static synchronized void initialize(){
		if(logger == null){
			InputStream is = null;
			try{
				java.util.Properties loggerProperties = new java.util.Properties();
				is = SPCoopTestsuiteLogger.class.getResourceAsStream("/testsuite_spcoop.log4j2.properties");
				if(is!=null){
					loggerProperties.load(SPCoopTestsuiteLogger.class.getResourceAsStream("/testsuite_spcoop.log4j2.properties"));
				}
				LoggerWrapperFactory.setLogConfiguration(loggerProperties);
				logger = LoggerWrapperFactory.getLogger("govway.testsuite");
			}catch(Exception e){
				System.err.println("Riscontrato errore durante l'inizializzazione del sistema di logging di OpenSPCoop: "
						+e.getMessage());
				e.printStackTrace(System.err);
			}
		}
	}
	
	
	public static Logger getInstance(){
		if(logger==null){
			initialize();
		}
		return logger;
	}
	
}
