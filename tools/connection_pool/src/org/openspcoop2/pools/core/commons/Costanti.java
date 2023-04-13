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
    public static final String TRANSACTION_ISOLATION_NONE = "none";
    /** tipo di transaction isolation:  readCommitted*/
    public static final String TRANSACTION_ISOLATION_READ_COMMITTED = "readCommitted";
    /** tipo di transaction isolation: readUncommited  */
    public static final String TRANSACTION_ISOLATION_READ_UNCOMMITTED = "readUncommitted";
    /** tipo di transaction isolation: repeatableRead */
    public static final String TRANSACTION_ISOLATION_REPEATABLE_READ = "repeatableRead";
    /** tipo di transaction isolation: serializable */
    public static final String TRANSACTION_ISOLATION_SERIALIZABLE = "serializable";

    /** Disabilitato */
    public static final String DISABILITATO = "disabilitato";
    /** abilitato */
    public static final String ABILITATO = "abilitato";


    /** tipo di when exausted action: block */
    public static final String WHEN_EXHAUSTED_BLOCK = "block";
    /** tipo di when exausted action: grow */
    public static final String WHEN_EXHAUSTED_GROW = "grow";
    /** tipo di when exausted action: fail */
    public static final String WHEN_EXHAUSTED_FAIL = "fail";

    
    /** tipo di acknowledgment: autoAcknowledgment */
    public static final String ACKNOWLEDGMENT_AUTO = "autoAcknowledgment";
    /** tipo di acknowledgment: clientAcknowledgment  */
    public static final String ACKNOWLEDGMENT_CLIENT = "clientAcknowledgment";
    /** tipo di acknowledgment: dupsOkAcknowledgment */
    public static final String ACKNOWLEDGMENT_DUPS_OK = "dupsOkAcknowledgment";
    
    /** Risorsa di tipo XML */
    public static final String RISORSE_SISTEMA_XML = "xml";
    
    /** Dominio utilizzato per le risorse JMX */
    public static final String JMX_DOMINIO = "org.openspcoop2.pools";
    /** Tipo utilizzato per le risorse JMX */
    public static final String JMX_TYPE = "type";
    /** MBean per i datasources */
    public static final String JMX_MONITORAGGIO_DATASOURCE = "Datasource";
    /** MBean per le connection factories */
    public static final String JMX_MONITORAGGIO_CONNECTION_FACTORY = "ConnectionFactory";

  
}
