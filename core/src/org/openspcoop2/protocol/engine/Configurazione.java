/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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



package org.openspcoop2.protocol.engine;

import org.slf4j.Logger;
import org.openspcoop2.protocol.engine.driver.repository.IGestoreRepository;

/**
 * Configurazione della Libreria engine di OpenSPCoop
 *
 * @author Manca Andrea (manca@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Configurazione {
	
	// Funzionalità utilizzate dall'engine del protocollo
	static private IGestoreRepository gestoreRepositoryBuste;
	static private String sqlQueryObjectType;
	static private Logger libraryLog;
	
	public static void init(int checkInterval,
			IGestoreRepository gestoreRepositoryBuste,String sqlQueryObject,Logger log){
		Configurazione.gestoreRepositoryBuste = gestoreRepositoryBuste;
		Configurazione.sqlQueryObjectType = sqlQueryObject;
		Configurazione.libraryLog = log;
	}

	/**
	 * @return IGestoreRepositoryBuste
	 */
	public static final IGestoreRepository getGestoreRepositoryBuste() {
		return Configurazione.gestoreRepositoryBuste;
	}
	/**
	 * @param gestoreRepositoryBuste
	 */
	public static final void setGestoreRepositoryBuste(
			IGestoreRepository gestoreRepositoryBuste) {
		Configurazione.gestoreRepositoryBuste = gestoreRepositoryBuste;
	}
	public static final String getSqlQueryObjectType() {
		return Configurazione.sqlQueryObjectType;
	}
	public static final void setSqlQueryObjectType(String sqlQueryObject) {
		Configurazione.sqlQueryObjectType = sqlQueryObject;
	}
	public static Logger getLibraryLog() {
		return Configurazione.libraryLog;
	}
	public static void setLibraryLog(Logger libraryLog) {
		Configurazione.libraryLog = libraryLog;
	}


}
