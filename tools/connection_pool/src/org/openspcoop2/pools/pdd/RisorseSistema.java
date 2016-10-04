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



package org.openspcoop2.pools.pdd;


import org.slf4j.Logger;

import java.util.Vector;

import org.openspcoop2.pools.core.ConnectionFactory;
import org.openspcoop2.pools.core.Datasource;
import org.openspcoop2.pools.core.commons.Costanti;
import org.openspcoop2.pools.core.driver.IDriverRisorseSistemaGet;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.pools.core.driver.DriverRisorseSistemaXML;
import org.openspcoop2.pools.core.driver.DriverRisorseSistemaException;




/**
 * Classe utilizzata per effettuare letture di risorse del sistema
 * 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RisorseSistema  {

	/* Fonte su cui leggere le risorse*/
    private IDriverRisorseSistemaGet driverRisorseSistema;
	
	/** Logger utilizzato per debug. */
	private Logger log = null;
	
	
	
	
	
	
	
	/*   -------------- Costruttore -----------------  */ 

	/**
	 * Si occupa di inizializzare l'engine 
	 * L'engine inizializzato sara' diverso a seconda del <var>tipo</var>:
	 * <ul>
	 * <li> {@link DriverRisorseSistemaXML}, interroga una configurazione realizzata tramite un file xml.
    * </ul>
	 *
	 * @param tipo Tipo di configurazione.
	 * @param location Location della configurazione
	 */
	public RisorseSistema(String tipo,String location,Logger aLog)throws DriverRisorseSistemaException{

		if(aLog!=null)
			this.log = aLog;
		else
			this.log = LoggerWrapperFactory.getLogger("Configurazione delle Risorse di Sistema");
		
		try{ 
			// inizializzazione XML
			if(Costanti.RISORSE_SISTEMA_XML.equalsIgnoreCase(tipo)){
				this.driverRisorseSistema = new DriverRisorseSistemaXML(location,this.log);
				if( ((DriverRisorseSistemaXML)this.driverRisorseSistema).create ==false){
					this.log.error("Riscontrato errore durante l'inizializzazione della configurazione delle risorse di sistema, configurazione di tipo "+
							tipo+" con location: "+location);
				} 
			}
			
			// tipo  non conosciuto
			else
				this.log.error("Riscontrato errore durante l'inizializzazione della configurazione delle risorse di sistema, configurazione di tipo sconosciuta "+
						tipo+" con location: "+location);
			

		}catch(Exception e){
			this.log.error("Riscontrato errore durante l'inizializzazione della configurazione delle risorse di sistema: "+e.getMessage());
			throw new DriverRisorseSistemaException("Riscontrato errore durante l'inizializzazione della configurazione delle risorse di sistema: "+e.getMessage());
		}
	}
	
	
	
	
	
	
	
	
    /* ********  (utilizzo dei driver) ******** */ 
	
	
	
	/**
     * Restituisce le proprieta' che identificano un albero JNDI su cui registrare le risorse definite
     * nel poolConfig.xml
     *
     * @return proprieta' di contesto JNDI.
     * 
     */
    public java.util.Properties getJNDIContext() throws DriverRisorseSistemaException{
    	return this.driverRisorseSistema.getJNDIContext();
    }
    
   






    /* ********  DataSource  ******** */
    
    /**
     * Restituisce il numero di DataSource definiti nel file poolConfig.xml 
     *
     * @return Il numero di DataSource definiti nel file poolConfig.xml
     * 
     */
    public int dataSourceListSize() throws DriverRisorseSistemaException{
    	return this.driverRisorseSistema.dataSourceListSize();
    }
   
    /**
     * Restituisce il DataSource con indice <var>index</var> definito nel file poolConfig.xml
     *  
     * @param index Indice del DataSource da ritornare
     * @return la configurazione {@link org.openspcoop2.pools.core.Datasource} di un DataSource con indice <var>index</var>
     * 
     */
    public Datasource getDataSource(int index) throws DriverRisorseSistemaException{
    	return this.driverRisorseSistema.getDataSource(index);
    }
 
    /**
     * Restituisce un vector contenente tutti i DataSource definiti nel file poolConfig.xml
     *  
     * @return Vector di {@link org.openspcoop2.pools.core.Datasource}
     * 
     */
    public Vector<Datasource> getDataSources() throws DriverRisorseSistemaException{
    	return this.driverRisorseSistema.getDataSources();
    }





    
    
    /* ********  ConnectionFactory  ******** */
    
    /**
     * Restituisce il numero di ConnectionFactory definiti nel file poolConfig.xml 
     *
     * @return Il numero di ConnectionFactory definiti nel file poolConfig.xml
     * 
     */
    public int connectionFactoryListSize() throws DriverRisorseSistemaException{
    	return this.driverRisorseSistema.connectionFactoryListSize();
    }
   
    /**
     * Restituisce la ConnectionFactory con indice <var>index</var> definito nel file poolConfig.xml
     *  
     * @param index Indice della ConnectionFactory da ritornare
     * @return la configurazione {@link org.openspcoop2.pools.core.ConnectionFactory} di una ConnectionFactory 
     *         con indice <var>index</var>
     * 
     */
    public ConnectionFactory getConnectionFactory(int index) throws DriverRisorseSistemaException{
    	return this.driverRisorseSistema.getConnectionFactory(index);
    }
 
    /**
     * Restituisce un vector contenente tutti le ConnectionFactory definite nel file poolConfig.xml
     *  
     * @return Vector di {@link org.openspcoop2.pools.core.ConnectionFactory}
     * 
     */
    public Vector<ConnectionFactory> getConnectionFactories() throws DriverRisorseSistemaException{
    	return this.driverRisorseSistema.getConnectionFactories();
    }
	
}
