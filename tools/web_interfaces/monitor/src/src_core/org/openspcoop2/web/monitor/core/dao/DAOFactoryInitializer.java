package org.openspcoop2.web.monitor.core.dao;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryException;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

public class DAOFactoryInitializer {

	private static Logger log = LoggerWrapperFactory.getLogger(DAOFactoryInitializer.class);  // per poter associare il package 'dao' al logger 'sql'
	
	public static DAOFactory getInstanceDAOFactory() throws DAOFactoryException{
		return DAOFactory.getInstance(log);
	}
	
}
