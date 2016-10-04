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


package org.openspcoop2.utils.resources;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.Vector;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.Context;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;


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
	/** MBeanServerConnection */
	private Object mbeanServerConnection = null;
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
		this(jndiNameMBeanServer,jndiContext,
				null,null,null,null,null,
				logger,loggerConsole);
	}
	
	public GestoreRisorseJMX(String tipoApplicationServer, String factory, String serverUrl, String username,  String password) throws RisorseJMXException{
		this(null,null,
				tipoApplicationServer,factory,serverUrl,username,password,
				null,null);
	}
	public GestoreRisorseJMX(String tipoApplicationServer, String factory, String serverUrl, String username,  String password,
			Logger logger) throws RisorseJMXException{
		this(null,null,
				tipoApplicationServer,factory,serverUrl,username,password,
				logger,null);
	}
	public GestoreRisorseJMX(String tipoApplicationServer, String factory, String serverUrl, String username,  String password,
			Logger logger,Logger loggerConsole) throws RisorseJMXException{
		this(null,null,
				tipoApplicationServer,factory,serverUrl,username,password,
				logger,loggerConsole);
	}
	
	private GestoreRisorseJMX(String modalita1_jndiNameMBeanServer,java.util.Properties modalita1_jndiContext,
            String modalita2_tipoApplicationServer, String modalita2_factory, String modalita2_serverUrl, String modalita2_username,  String modalita2_password,
            Logger logger,Logger loggerConsole) throws RisorseJMXException{
	
		// log
		if(logger==null){
			this.log = LoggerWrapperFactory.getLogger(GestoreRisorseJMX.class);
		}
		else{
			this.log = logger;
		}
		
		// logConsole
		if(loggerConsole==null){
			loggerConsole = LoggerWrapperFactory.getLogger("openspcoop2.startup");
		}
		
		try{
			//	Ci sono tre modi per avere l'MBean SERVER
			
			// 1: da JNDI
			if(modalita1_jndiNameMBeanServer!=null){
				GestoreJNDI jndi = new GestoreJNDI(modalita1_jndiContext);
				this.mbeanServer = (javax.management.MBeanServer) jndi.lookup(modalita1_jndiNameMBeanServer);
				if(this.mbeanServer==null)
					throw new Exception("MBeanServer ["+modalita1_jndiNameMBeanServer+"] non trovato");
				else{
					if(logger==null)
						loggerConsole.info("Attivata gestione jmx attraverso MBeanServer ["+modalita1_jndiNameMBeanServer+"]: "+this.mbeanServer.toString());
					this.log.info("Attivata gestione jmx attraverso MBeanServer ["+modalita1_jndiNameMBeanServer+"]: "+this.mbeanServer.toString());
				}
			}

            // 2: via url
            else if(modalita2_tipoApplicationServer!=null && !"".equals(modalita2_tipoApplicationServer)){
                if(modalita2_factory==null || "".equals(modalita2_factory)){
                        throw new Exception("Parametro 'factory' non fornito");
                }
                if(modalita2_serverUrl==null || "".equals(modalita2_serverUrl)){
                        throw new Exception("Parametro 'serverUrl' non fornito");
                }
                if(modalita2_tipoApplicationServer.equals("jboss7") ||
                		(modalita2_tipoApplicationServer!=null && modalita2_tipoApplicationServer.startsWith("wildfly")) ||
                		modalita2_tipoApplicationServer.startsWith("tomcat")){
                	
                	Class<?>jmxServiceURLClass = Class.forName("javax.management.remote.JMXServiceURL");
                	Constructor<?> constructorJmxServiceURLClass = jmxServiceURLClass.getConstructor(String.class);
                	Object serviceURL = constructorJmxServiceURLClass.newInstance(modalita2_serverUrl);

                	Class<?>jmxConnectorFactoryClass = Class.forName("javax.management.remote.JMXConnectorFactory");
                	Method connect = jmxConnectorFactoryClass.getMethod("connect", jmxServiceURLClass, java.util.Map.class);
                	Object jmxConnector = connect.invoke(null, serviceURL, null);
                	
                	Class<?>jmxConnectorClass = Class.forName("javax.management.remote.JMXConnector");
                	Method getMBeanServerConnection = jmxConnectorClass.getMethod("getMBeanServerConnection");
                	this.mbeanServerConnection = getMBeanServerConnection.invoke(jmxConnector);
    			}
    			else{
    				Properties properties = new Properties();
    				properties.put(Context.INITIAL_CONTEXT_FACTORY, modalita2_factory);
    				properties.put(Context.PROVIDER_URL, modalita2_serverUrl);
    				GestoreJNDI jndi = new GestoreJNDI(properties);
    				this.mbeanServerConnection = jndi.lookup("jmx/invoker/RMIAdaptor");
    				
    				if(modalita2_username!=null && modalita2_password!=null){
    					
    					Class<?>simplePrincipalClass = Class.forName("org.jboss.security.SimplePrincipal");
    					Constructor<?> constructorPrincipalClass = simplePrincipalClass.getConstructor(String.class);
    					Object simplePrincipal = constructorPrincipalClass.newInstance(modalita2_username);
    					
    					Class<?>securityAssociationClass = Class.forName("org.jboss.security.SecurityAssociation");
    					Method setPrincipal = securityAssociationClass.getMethod("setPrincipal", simplePrincipalClass);
    					setPrincipal.invoke(null, simplePrincipal);
    					Method setCredential = securityAssociationClass.getMethod("setCredential", String.class);
    					setCredential.invoke(null, modalita2_password);
    				}
    				
    			}
            }
			
			
			// 3: Prendendolo dall'application server, cercandone uno di default
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
			if(this.mbeanServer==null){
				throw new Exception("Operazione di registrazione permessa solo se il gestore viene inizializzato con il costruttore di default o indicando l'MBeanServer");
			}
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
				if(this.mbeanServer==null){
					throw new Exception("Operazione di cancellazione permessa solo se il gestore viene inizializzato con il costruttore di default o indicando l'MBeanServer");
				}
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
			if(this.mbeanServerConnection!=null){
				Class<?> c = Class.forName("javax.management.MBeanServerConnection");
				Method m = c.getMethod("getAttribute", ObjectName.class, String.class);
				return m.invoke(this.mbeanServerConnection, name, nomeAttributo);
			}
			else{
				return this.mbeanServer.getAttribute(name, nomeAttributo);
			}
		}catch(Exception e){
			this.log.error("Riscontrato errore durante la lettura dell'attributo ["+nomeAttributo+"] della risorsa ["+nomeRisorsa+"]: "+e.getMessage(),e);
			throw new RisorseJMXException("Riscontrato errore durante la lettura dell'attributo ["+nomeAttributo+"] della risorsa ["+nomeRisorsa+"]: "+e.getMessage(),e);
		}	
	}
	
	public void setAttribute(String nomeRisorsa,String nomeAttributo, Object value)throws RisorseJMXException{
		this.setAttribute(CostantiJMX.JMX_DOMINIO,CostantiJMX.JMX_TYPE,nomeRisorsa,nomeAttributo,value);
	}
	public void setAttribute(String dominio,String tipo,String nomeRisorsa,String nomeAttributo, Object value)throws RisorseJMXException{
		try{
			ObjectName name = new ObjectName(dominio,tipo,nomeRisorsa);
			javax.management.Attribute attribute = new javax.management.Attribute(nomeAttributo, value);
			if(this.mbeanServerConnection!=null){
				Class<?> c = Class.forName("javax.management.MBeanServerConnection");
				Method m = c.getMethod("setAttribute", ObjectName.class, javax.management.Attribute.class);
				m.invoke(this.mbeanServerConnection, name, nomeAttributo);
			}
			else{
				this.mbeanServer.setAttribute(name, attribute);
			}
		}catch(Exception e){
			this.log.error("Riscontrato errore durante l'aggiornamento dell'attributo ["+nomeAttributo+"] della risorsa ["+nomeRisorsa+"]: "+e.getMessage(),e);
			throw new RisorseJMXException("Riscontrato errore durante l'aggiornamento dell'attributo ["+nomeAttributo+"] della risorsa ["+nomeRisorsa+"]: "+e.getMessage(),e);
		}	
	}
	
	
	public Object invoke(String nomeRisorsa,String nomeMetodo,Object[]params,String[]signature)throws RisorseJMXException{
		return this.invoke(CostantiJMX.JMX_DOMINIO,CostantiJMX.JMX_TYPE,nomeRisorsa,nomeMetodo,params,signature);
	}
	public Object invoke(String dominio,String tipo,String nomeRisorsa,String nomeMetodo,Object[]params,String[]signature)throws RisorseJMXException{
		try{
			ObjectName name = new ObjectName(dominio,tipo,nomeRisorsa);
			if(this.mbeanServerConnection!=null){
				Class<?> c = Class.forName("javax.management.MBeanServerConnection");
				Method m = c.getMethod("invoke", ObjectName.class, String.class, Object[].class, String[].class);
				return m.invoke(this.mbeanServerConnection, name, nomeMetodo, params, signature);
			}
			else{
				return this.mbeanServer.invoke(name, nomeMetodo, params, signature);
			}
		}catch(Exception e){
			this.log.error("Riscontrato errore durante l'invocazione del metodo ["+nomeMetodo+"] della risorsa ["+nomeRisorsa+"]: "+e.getMessage(),e);
			throw new RisorseJMXException("Riscontrato errore durante l'invocazione del metodo ["+nomeMetodo+"] della risorsa ["+nomeRisorsa+"]: "+e.getMessage(),e);
		}	
	}
	
}
