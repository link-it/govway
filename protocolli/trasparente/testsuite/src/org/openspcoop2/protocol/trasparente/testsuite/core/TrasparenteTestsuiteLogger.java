/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.protocol.trasparente.testsuite.core;

import java.io.InputStream;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class TrasparenteTestsuiteLogger {

	/**  Logger log4j utilizzato per scrivere i tracciamenti */
	protected static Logger logger = null;

	public synchronized static void initialize(){
		if(logger == null){
			InputStream is = null;
			try{
				java.util.Properties loggerProperties = new java.util.Properties();
				is = TrasparenteTestsuiteLogger.class.getResourceAsStream("/testsuite_trasparente.log4j2.properties");
				if(is!=null){
					loggerProperties.load(TrasparenteTestsuiteLogger.class.getResourceAsStream("/testsuite_trasparente.log4j2.properties"));
				}
				LoggerWrapperFactory.setLogConfiguration(loggerProperties);
				logger = LoggerWrapperFactory.getLogger("openspcoop2.testsuite");
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
