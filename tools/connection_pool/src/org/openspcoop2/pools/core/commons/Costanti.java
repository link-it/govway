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



package org.openspcoop2.pools.core.commons;


/**
 * Costanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {


    /** tipo di transaction isolation: none */
    public final static String TRANSACTION_ISOLATION_NONE = "none";
    /** tipo di transaction isolation:  readCommitted*/
    public final static String TRANSACTION_ISOLATION_READ_COMMITTED = "readCommitted";
    /** tipo di transaction isolation: readUncommited  */
    public final static String TRANSACTION_ISOLATION_READ_UNCOMMITTED = "readUncommitted";
    /** tipo di transaction isolation: repeatableRead */
    public final static String TRANSACTION_ISOLATION_REPEATABLE_READ = "repeatableRead";
    /** tipo di transaction isolation: serializable */
    public final static String TRANSACTION_ISOLATION_SERIALIZABLE = "serializable";

    /** Disabilitato */
    public final static String DISABILITATO = "disabilitato";
    /** abilitato */
    public final static String ABILITATO = "abilitato";


    /** tipo di when exausted action: block */
    public final static String WHEN_EXHAUSTED_BLOCK = "block";
    /** tipo di when exausted action: grow */
    public final static String WHEN_EXHAUSTED_GROW = "grow";
    /** tipo di when exausted action: fail */
    public final static String WHEN_EXHAUSTED_FAIL = "fail";

    
    /** tipo di acknowledgment: autoAcknowledgment */
    public final static String ACKNOWLEDGMENT_AUTO = "autoAcknowledgment";
    /** tipo di acknowledgment: clientAcknowledgment  */
    public final static String ACKNOWLEDGMENT_CLIENT = "clientAcknowledgment";
    /** tipo di acknowledgment: dupsOkAcknowledgment */
    public final static String ACKNOWLEDGMENT_DUPS_OK = "dupsOkAcknowledgment";
    
    /** Risorsa di tipo XML */
    public final static String RISORSE_SISTEMA_XML = "xml";
    
    /** Dominio utilizzato per le risorse JMX */
    public static final String JMX_DOMINIO = "org.openspcoop2.pools";
    /** Tipo utilizzato per le risorse JMX */
    public static final String JMX_TYPE = "type";
    /** MBean per i datasources */
    public static final String JMX_MONITORAGGIO_DATASOURCE = "Datasource";
    /** MBean per le connection factories */
    public static final String JMX_MONITORAGGIO_CONNECTION_FACTORY = "ConnectionFactory";

  
}
