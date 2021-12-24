/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.web.ctrlstat.core;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryException;
import org.slf4j.Logger;

/**
 * DAOConsoleFactory
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DAOConsoleFactory extends DAOFactory {

	/** Copia Statica */
	private static DAOConsoleFactory daoConsoleFactory = null;

	private static synchronized void initialize(Logger log) throws DAOFactoryException{

		if(DAOConsoleFactory.daoConsoleFactory==null)
			DAOConsoleFactory.daoConsoleFactory = new DAOConsoleFactory(log);	

	}

	public static DAOConsoleFactory getInstance(Logger log) throws DAOFactoryException{

		if(DAOConsoleFactory.daoConsoleFactory==null)
			DAOConsoleFactory.initialize(log);

		return DAOConsoleFactory.daoConsoleFactory;
	}
	
	public DAOConsoleFactory(Logger log) throws DAOFactoryException {
		super(log, getProperties(log));
	}

	private static DAOConsolePropertiesFactory getProperties(Logger log) throws DAOFactoryException {
		try {
			return new DAOConsolePropertiesFactory(log);
		}catch(Throwable t) {
			throw new DAOFactoryException(t.getMessage(),t);
		}
	}
}
