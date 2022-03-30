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


package org.openspcoop2.pools.pdd.jmx;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.openspcoop2.pools.core.commons.Costanti;
import org.openspcoop2.pools.pdd.OpenSPCoop2PoolsStartup;
import org.openspcoop2.utils.resources.GestoreJNDI;



/**
 * Gestore risorse JMX utilizzate da OpenSPCoop
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreRisorseJMX {

	/** MBeanServer */
	MBeanServer mbeanServer  = null;
	/** Logger */
	Logger log = null;

	/** JMX Name */
	List<ObjectName> jmxNames = new ArrayList<ObjectName>();


	/**
	 * Inizializzazione del Gestore delle risorse JMX
	 */
	public GestoreRisorseJMX() throws RisorseJMXException{
		this(null,null);
	}
	/**
	 * Inizializzazione del Gestore delle risorse JMX
	 *
	 * @param jndiNameMBeanServer Nome JNDI del MBean Server
	 * @param jndiContext Contesto jndi per localizzare il MBean Server
	 */
	public GestoreRisorseJMX(String jndiNameMBeanServer,java.util.Properties jndiContext) throws RisorseJMXException{

		// log
		this.log = OpenSPCoop2PoolsStartup.getLogger();

		try{
			//	Ci sono due modi per avere l'MBean SERVER

			// 1: da JNDI
			if(jndiNameMBeanServer!=null){
				GestoreJNDI jndi = new GestoreJNDI(jndiContext);
				this.mbeanServer = (javax.management.MBeanServer) jndi.lookup(jndiNameMBeanServer);
				if(this.mbeanServer==null)
					throw new Exception("MBeanServer ["+jndiNameMBeanServer+"] non trovato");
				else{
					OpenSPCoop2PoolsStartup.getLoggerConsole().info("Attivata gestione jmx attraverso MBeanServer ["+jndiNameMBeanServer+"]: "+this.mbeanServer.toString());
					this.log.info("Attivata gestione jmx attraverso MBeanServer ["+jndiNameMBeanServer+"]: "+this.mbeanServer.toString());
				}
			}

			// 2: Prendendolo dall'application server, cercandone uno di default
			else{
				java.util.ArrayList<?> lServer = javax.management.MBeanServerFactory.findMBeanServer(null);
				if(lServer==null){
					throw new RisorseJMXException("Lista di MBean Server di default non trovata (MBeanServerFactory.findMBeanServer)");
				}else{
					if(lServer.isEmpty()){
						throw new RisorseJMXException("Lista di MBean Server di default vuota");
					}else{
						java.util.Iterator<?> it = 	lServer.iterator();
						if(it.hasNext()){
							this.mbeanServer = (javax.management.MBeanServer) it.next();
							if(this.mbeanServer==null)
								throw new Exception("MBeanServer di default non trovato");
							else{
								OpenSPCoop2PoolsStartup.getLoggerConsole().info("Attivata gestione jmx attraverso MBeanServer: "+this.mbeanServer.toString());
								this.log.info("Attivata gestione jmx attraverso MBeanServer: "+this.mbeanServer.toString());
							}
						}else{
							throw new RisorseJMXException("Lista di MBean Server di default vuota ?");
						}
					}
				}
			}

		}catch(Exception e){
			this.log.error("Riscontrato errore durante l'inizializzazione del gestore delle RisorseJMX: "+e.getMessage(),e);
			throw new RisorseJMXException("Riscontrato errore durante l'inizializzazione del gestore delle RisorseJMX: "+e.getMessage(),e);
		}
	}

	/**
	 * Registrazione del MBean per il monitoraggio
	 * 
	 * @throws RisorseJMXException
	 */
	public void registerMBeanMonitoraggioRisorseDataSources(String [] jndiName)throws RisorseJMXException{
		for(int i=0; i<jndiName.length; i++){
			try{
				String jmxName = jndiName[i];
				while(jmxName.contains(":/")){
					jmxName = jmxName.replace(":/", "_");
				}
				while(jmxName.contains("/")){
					jmxName = jmxName.replace("/", "_");
				}
				
				Hashtable<String,String> attributi = new Hashtable<String,String>();
				attributi.put(Costanti.JMX_TYPE, Costanti.JMX_MONITORAGGIO_DATASOURCE);
				attributi.put("name", jmxName);
				javax.management.ObjectName jmxNameMonitoraggio = 
					new javax.management.ObjectName(Costanti.JMX_DOMINIO,attributi);

				this.log.debug("Registrato Mbean col nome "+jmxNameMonitoraggio.getCanonicalName());
				this.mbeanServer.registerMBean(new org.openspcoop2.pools.pdd.jmx.MonitoraggioRisorseDatasource(jmxName), jmxNameMonitoraggio);
				this.jmxNames.add(jmxNameMonitoraggio);
			}catch(Exception e){
				this.log.error("Riscontrato errore durante l'inizializzazione della risorsa JMX MonitoraggioRisorse per i datasources: "+e.getMessage(),e);
				throw new RisorseJMXException("Riscontrato errore durante l'inizializzazione della risorsa JMX MonitoraggioRisorse per i datasources: "+e.getMessage(),e);
			}	
		}

	}

	/**
	 * Registrazione del MBean per il monitoraggio
	 * 
	 * @throws RisorseJMXException
	 */
	public void registerMBeanMonitoraggioRisorseConnectionFactories(String [] jndiName)throws RisorseJMXException{
		for(int i=0; i<jndiName.length; i++){
			try{
				String jmxName = jndiName[i];
				while(jmxName.contains(":/")){
					jmxName = jmxName.replace(":/", "_");
				}
				while(jmxName.contains("/")){
					jmxName = jmxName.replace("/", "_");
				}
				
				Hashtable<String,String> attributi = new Hashtable<String,String>();
				attributi.put(Costanti.JMX_TYPE, Costanti.JMX_MONITORAGGIO_CONNECTION_FACTORY);
				attributi.put("name", jmxName);
				javax.management.ObjectName jmxNameMonitoraggio = 
					new javax.management.ObjectName(Costanti.JMX_DOMINIO,attributi);

				this.log.debug("Registrato Mbean col nome "+jmxNameMonitoraggio.getCanonicalName());
				this.mbeanServer.registerMBean(new org.openspcoop2.pools.pdd.jmx.MonitoraggioRisorseConnectionFactory(jmxName), jmxNameMonitoraggio);
				this.jmxNames.add(jmxNameMonitoraggio);
			}catch(Exception e){
				this.log.error("Riscontrato errore durante l'inizializzazione della risorsa JMX MonitoraggioRisorse per le connection factories: "+e.getMessage(),e);
				throw new RisorseJMXException("Riscontrato errore durante l'inizializzazione della risorsa JMX MonitoraggioRisorse per le connection factories: "+e.getMessage(),e);
			}	
		}

	}


	/**
	 * Eliminazione dei MBean registrati
	 */
	public void unregisterMBeans(){
		for(int i=0; i<this.jmxNames.size(); i++){

			ObjectName jmxName = this.jmxNames.get(i);
			try{				
				ObjectName query = new ObjectName(jmxName.getCanonicalName()+",*");
				java.util.Set<?> nomi = this.mbeanServer.queryNames(query, null);
				if (! nomi.isEmpty())
					for(Object nome : nomi){
						ObjectName fullname = (ObjectName) nome;
						this.mbeanServer.unregisterMBean(fullname);
						this.log.info("Unbound della risorsa JMX ["+fullname.getCanonicalName()+"]");
					}
				else
					this.log.error("Riscontrato errore durante l'unbound della risorsa JMX ["+jmxName.toString()+"]: Non nregistrato");
			}catch(Exception e){
				this.log.error("Riscontrato errore durante l'unbound della risorsa JMX ["+jmxName.toString()+"]: "+e.getMessage(),e);
			}	
		}
	}
}
