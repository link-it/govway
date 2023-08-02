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



package org.openspcoop2.pools.pdd.jms.connectionsession;

import jakarta.jms.JMSException;

import org.slf4j.Logger;

/**
 * ExceptionListener
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExceptionListener implements jakarta.jms.ExceptionListener{

	private Connection connection;
	private Logger logger;
	private String jndiName;
	
	public ExceptionListener(Connection con,Logger logger,String jndiName){
		this.connection = con;
		this.logger = logger;
		this.jndiName = jndiName;
	}
	
	@Override
	public void onException(JMSException arg0) {
		this.logger.error("Exception listener ha rilevato un errore sulla connessione ["+this.jndiName+"]",arg0);
		this.connection.setValidationExceptionListener(arg0);
	}

}
