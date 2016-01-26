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



package org.openspcoop2.pools.core.driver;

import java.util.Vector;

import org.openspcoop2.pools.core.Datasource;
import org.openspcoop2.pools.core.ConnectionFactory;

/**
 * Contiene l'interfaccia di un 'reader' della configurazione poolConfig di OpenSPCoop
 * che permette di definire risorse da registrare in un albero JNDI:
 * <ul>
 * <li>DataSource ({@link org.openspcoop2.pools.core.Datasource})
 * <li>ConnectionFactory ({@link org.openspcoop2.pools.core.ConnectionFactory})
 * </ul>
 * <p>
 * Esistono le seguenti classi, che implementano l'interfaccia,
 * uno per ogni possibile unita' di memorizzazione della configurazione :
 * <ul>
 * <li>file XML   {@link DriverRisorseSistemaXML}
 * </ul>
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public interface IDriverRisorseSistemaGet {	
    

	/* ******** JNDI TREE ******** */
    
    /**
     * Restituisce le proprieta' che identificano un albero JNDI su cui registrare le risorse definite
     * nel poolConfig.xml
     *
     * @return proprieta' di contesto JNDI.
     * 
     */
    public java.util.Properties getJNDIContext() throws DriverRisorseSistemaException;
    
   






    /* ********  DataSource  ******** */
    
    /**
     * Restituisce il numero di DataSource definiti nel file poolConfig.xml 
     *
     * @return Il numero di DataSource definiti nel file poolConfig.xml
     * 
     */
    public int dataSourceListSize() throws DriverRisorseSistemaException;
   
    /**
     * Restituisce il DataSource con indice <var>index</var> definito nel file poolConfig.xml
     *  
     * @param index Indice del DataSource da ritornare
     * @return la configurazione {@link org.openspcoop2.pools.core.Datasource} di un DataSource con indice <var>index</var>
     * 
     */
    public Datasource getDataSource(int index) throws DriverRisorseSistemaException;
 
    /**
     * Restituisce un vector contenente tutti i DataSource definiti nel file poolConfig.xml
     *  
     * @return Vector di {@link org.openspcoop2.pools.core.Datasource}
     * 
     */
    public Vector<Datasource> getDataSources() throws DriverRisorseSistemaException;







    /* ********  ConnectionFactory  ******** */
    
    /**
     * Restituisce il numero di ConnectionFactory definiti nel file poolConfig.xml 
     *
     * @return Il numero di ConnectionFactory definiti nel file poolConfig.xml
     * 
     */
    public int connectionFactoryListSize() throws DriverRisorseSistemaException;
   
    /**
     * Restituisce la ConnectionFactory con indice <var>index</var> definito nel file poolConfig.xml
     *  
     * @param index Indice della ConnectionFactory da ritornare
     * @return la configurazione {@link org.openspcoop2.pools.core.ConnectionFactory} di una ConnectionFactory 
     *         con indice <var>index</var>
     * 
     */
    public ConnectionFactory getConnectionFactory(int index) throws DriverRisorseSistemaException;
 
    /**
     * Restituisce un vector contenente tutti le ConnectionFactory definite nel file poolConfig.xml
     *  
     * @return Vector di {@link org.openspcoop2.pools.core.ConnectionFactory}
     * 
     */
    public Vector<ConnectionFactory> getConnectionFactories() throws DriverRisorseSistemaException;

   
}
