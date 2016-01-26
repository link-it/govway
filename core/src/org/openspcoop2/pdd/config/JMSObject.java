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



package org.openspcoop2.pdd.config;

import javax.jms.Connection;
import javax.jms.Session;

/**
 * Classe utilizzata per raccogliere le risorse ottenute da un broker JMS.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class JMSObject implements java.io.Serializable  {
    
	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
    
    /* ********  F I E L D S  P R I V A T I  ******** */
    
    private Connection connection;
    private Session session;
	



    /* ********  C O S T R U T T O R E  ******** */
    
    /**
     * Costruttore. 
     *
     * 
     */
    public JMSObject(){}
    
   



    /* ********  S E T T E R   ******** */
    /**
     * Imposta la connessione JMS.
     *
     * @param con Connessione JMS.
     * 
     */    
    public void setConnection(Connection con) {
	this.connection = con;
    }
    /**
     * Imposta la sessione JMS.
     *
     * @param s Sessione JMS.
     * 
     */    
    public void setSession(Session s) {
	this.session = s;
    }
    


    /* ********  G E T T E R   ******** */
    /**
     * Ritorna la connessione JMS.
     *
     * @return Connessione JMS.
     * 
     */    
    public Connection getConnection() {
	return this.connection;
    }
    /**
     * Ritorna la sessione JMS.
     *
     * @return Sessione JMS.
     * 
     */    
    public Session getSession() {
	return this.session;
    }
}
