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

import jakarta.jms.Connection;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;

import org.openspcoop2.pools.core.commons.OpenSPCoopFactoryException;
import org.openspcoop2.pools.pdd.jms.JMSInfo;

/**
 * ConnectionFactory
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnectionFactory  extends ConnectionSessionJMSPool implements jakarta.jms.ConnectionFactory{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7156114179251757792L;

	public ConnectionFactory(JMSInfo configuration) throws OpenSPCoopFactoryException {
		super(configuration);
	}

	
	@Override
	public Connection createConnection() throws JMSException {
		try{
			return this.getResource();
		}catch(Exception e){
			throw new JMSException(e.getMessage());
		}
	}

	@Override
	public Connection createConnection(String arg0, String arg1)
			throws JMSException {
		try{
			return this.getResource();
		}catch(Exception e){
			throw new JMSException(e.getMessage());
		}
	}


	@Override
	public JMSContext createContext() {
		return null;
	}


	@Override
	public JMSContext createContext(int arg0) {
		return null;
	}


	@Override
	public JMSContext createContext(String arg0, String arg1) {
		return null;
	}


	@Override
	public JMSContext createContext(String arg0, String arg1, int arg2) {
		return null;
	}

}
