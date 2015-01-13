/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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


package org.openspcoop2.utils.resources;

import java.util.Vector;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;


/**
 * Gestore risorse JMX utilizzate da OpenSPCoop
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreRisorseJMX {

	/** MBeanServer */
	private MBeanServer mbeanServer  = null;
	/** Logger */
	protected Logger log = null;
	
	/** JMX Name */
	private Vector<ObjectName> jmxNames = new Vector<ObjectName>();
	
	
	/**
	 * Inizializzazione del Gestore delle risorse JMX
	 */
	public GestoreRisorseJMX() throws RisorseJMXException{
		this(null,null,null);
	}
	public GestoreRisorseJMX(Logger logger) throws RisorseJMXException{
		this(null,null,logger);
	}
	/**
	 * Inizializzazione del Gestore delle risorse JMX
	 *
	 * @param jndiNameMBeanServer Nome JNDI del MBean Server
	 * @param jndiContext Contesto jndi per localizzare il MBean Server
	 */
	public GestoreRisorseJMX(String jndiNameMBeanServer,java.util.Properties jndiContext) throws RisorseJMXException{
		this(jndiNameMBeanServer,jndiContext,null,null);
	}
	public GestoreRisorseJMX(String jndiNameMBeanServer,java.util.Properties jndiContext,Logger logger) throws RisorseJMXException{
		this(jndiNameMBeanServer,jndiContext,logger,null);
	}
	public GestoreRisorseJMX(String jndiNameMBeanServer,java.util.Properties jndiContext,Logger logger,Logger loggerConsole) throws RisorseJMXException{
		
		// log
		if(logger==null){
			this.log = Logger.getLogger(GestoreRisorseJMX.class);
		}
		else{
			this.log = logger;
		}
		
		// logConsole
		if(loggerConsole==null){
			loggerConsole = Logger.getLogger("openspcoop2.startup");
		}
		
		try{
			//	Ci sono due modi per avere l'MBean SERVER
			
			// 1: da JNDI
			if(jndiNameMBeanServer!=null){
				GestoreJNDI jndi = new GestoreJNDI(jndiContext);
				this.mbeanServer = (javax.management.MBeanServer) jndi.lookup(jndiNameMBeanServer);
				if(this.mbeanServer==null)
					throw new Exception("MBeanServer ["+jndiNameMBeanServer+"] non trovato");
				else{
					if(logger==null)
						loggerConsole.info("Attivata gestione jmx attraverso MBeanServer ["+jndiNameMBeanServer+"]: "+this.mbeanServer.toString());
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
								if(logger==null)
									loggerConsole.info("Attivata gestione jmx attraverso MBeanServer: "+this.mbeanServer.toString());
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
	 * Registrazione del MBean generico
	 * 
	 * @throws RisorseJMXException
	 */
	public void registerMBean(Class<?> c,String nome)throws RisorseJMXException{
		this.registerMBean(c, CostantiJMX.JMX_DOMINIO,CostantiJMX.JMX_TYPE, nome);		
	}
	public void registerMBean(Class<?> c,String dominio,String type,String nome)throws RisorseJMXException{
		try{
			javax.management.ObjectName jmxName = 
				new javax.management.ObjectName(dominio,type,nome);
			this.mbeanServer.registerMBean(c.newInstance(), jmxName);
			this.jmxNames.add(jmxName);
		}catch(Exception e){
			this.log.error("Riscontrato errore durante l'inizializzazione della risorsa JMX ["+nome+"]: "+e.getMessage(),e);
			throw new RisorseJMXException("Riscontrato errore durante l'inizializzazione della risorsa JMX ["+nome+"]: "+e.getMessage(),e);
		}	
		
	}
	
	
	/**
	 * Eliminazione dei MBean registrati
	 */
	public void unregisterMBeans(){
		for(int i=0; i<this.jmxNames.size(); i++){
			javax.management.ObjectName jmxName = this.jmxNames.get(i);
			try{
				this.mbeanServer.unregisterMBean(jmxName);
				this.log.info("Unbound della risorsa JMX ["+jmxName.toString()+"]");
			}catch(Exception e){
				this.log.error("Riscontrato errore durante l'unbound della risorsa JMX ["+jmxName.toString()+"]: "+e.getMessage(),e);
			}	
		}
	}
	
		
	public Object getAttribute(String nomeRisorsa,String nomeAttributo)throws RisorseJMXException{
		return this.getAttribute(CostantiJMX.JMX_DOMINIO,CostantiJMX.JMX_TYPE,nomeRisorsa,nomeAttributo);
	}
	public Object getAttribute(String dominio,String tipo,String nomeRisorsa,String nomeAttributo)throws RisorseJMXException{
		try{
			ObjectName name = new ObjectName(dominio,tipo,nomeRisorsa);
			return this.mbeanServer.getAttribute(name, nomeAttributo);
		}catch(Exception e){
			this.log.error("Riscontrato errore durante la lettura dell'attributo ["+nomeAttributo+"] della risorsa ["+nomeRisorsa+"]: "+e.getMessage(),e);
			throw new RisorseJMXException("Riscontrato errore durante la lettura dell'attributo ["+nomeAttributo+"] della risorsa ["+nomeRisorsa+"]: "+e.getMessage(),e);
		}	
	}
	
	
	public Object invoke(String nomeRisorsa,String nomeMetodo,Object[]params,String[]signature)throws RisorseJMXException{
		return this.invoke(CostantiJMX.JMX_DOMINIO,CostantiJMX.JMX_TYPE,nomeRisorsa,nomeMetodo,params,signature);
	}
	public Object invoke(String dominio,String tipo,String nomeRisorsa,String nomeMetodo,Object[]params,String[]signature)throws RisorseJMXException{
		try{
			ObjectName name = new ObjectName(dominio,tipo,nomeRisorsa);
			return this.mbeanServer.invoke(name, nomeMetodo, params, signature);
		}catch(Exception e){
			this.log.error("Riscontrato errore durante l'invocazione del metodo ["+nomeMetodo+"] della risorsa ["+nomeRisorsa+"]: "+e.getMessage(),e);
			throw new RisorseJMXException("Riscontrato errore durante l'invocazione del metodo ["+nomeMetodo+"] della risorsa ["+nomeRisorsa+"]: "+e.getMessage(),e);
		}	
	}
	
}
