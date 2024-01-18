/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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


package org.openspcoop2.utils.jmx;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.Context;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;
import org.openspcoop2.utils.resources.GestoreJNDI;
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
	protected void logInfo(String msg) {
		if(this.log!=null) {
			this.log.info(msg);
		}
	}
	protected void logDebug(String msg, Throwable e) {
		if(this.log!=null) {
			this.log.debug(msg, e);
		}
	}
	protected void logError(String msg, Throwable e) {
		if(this.log!=null) {
			this.log.error(msg, e);
		}
	}
	
	private static final String MBEAN_SERVER_CONNECTION = "javax.management.MBeanServerConnection";
	
	/** JMX Name */
	private List<ObjectName> jmxNames = new ArrayList<>();
	
	
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
	
	private GestoreRisorseJMX(String modalita1JndiNameMBeanServer,java.util.Properties modalita1JndiContext,
            String modalita2TipoApplicationServer, String modalita2Factory, String modalita2ServerUrl, String modalita2Username,  String modalita2Password,
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
			loggerConsole = LoggerWrapperFactory.getLogger("govway.startup");
		}
		
		try{
			//	Ci sono tre modi per avere l'MBean SERVER
			
			// 1: da JNDI
			if(modalita1JndiNameMBeanServer!=null){
				GestoreJNDI jndi = new GestoreJNDI(modalita1JndiContext);
				this.mbeanServer = (javax.management.MBeanServer) jndi.lookup(modalita1JndiNameMBeanServer);
				if(this.mbeanServer==null)
					throw new RisorseJMXException("MBeanServer ["+modalita1JndiNameMBeanServer+"] non trovato");
				else{
					String msg = "Attivata gestione jmx attraverso MBeanServer ["+modalita1JndiNameMBeanServer+"]: "+this.mbeanServer.toString();
					if(logger==null) {
						loggerConsole.info(msg);
					}
					this.logInfo(msg);
				}
			}

            // 2: via url
            else if(modalita2TipoApplicationServer!=null && !"".equals(modalita2TipoApplicationServer)){
                if(modalita2Factory==null || "".equals(modalita2Factory)){
                        throw new RisorseJMXException("Parametro 'factory' non fornito");
                }
                if(modalita2ServerUrl==null || "".equals(modalita2ServerUrl)){
                        throw new RisorseJMXException("Parametro 'serverUrl' non fornito");
                }
                if(modalita2TipoApplicationServer.equals("jboss7") ||
                		(modalita2TipoApplicationServer!=null && modalita2TipoApplicationServer.startsWith("wildfly")) ||
                		modalita2TipoApplicationServer.startsWith("tomcat")){
                	
                	Class<?>jmxServiceURLClass = Class.forName("javax.management.remote.JMXServiceURL");
                	Constructor<?> constructorJmxServiceURLClass = jmxServiceURLClass.getConstructor(String.class);
                	Object serviceURL = constructorJmxServiceURLClass.newInstance(modalita2ServerUrl);

            		java.util.Map<String, Object> env = null;
                	if(modalita2Username!=null && modalita2Password!=null){
        				String[] creds = {modalita2Username, modalita2Password};
        				env = new HashMap<>();
        				env.put("jmx.remote.credentials", creds);
                	}
                	
                	Class<?>jmxConnectorFactoryClass = Class.forName("javax.management.remote.JMXConnectorFactory");
                	Method connect = jmxConnectorFactoryClass.getMethod("connect", jmxServiceURLClass, java.util.Map.class);
                	Object jmxConnector = connect.invoke(null, serviceURL, env);
                	
                	Class<?>jmxConnectorClass = Class.forName("javax.management.remote.JMXConnector");
                	Method getMBeanServerConnection = jmxConnectorClass.getMethod("getMBeanServerConnection");
                	this.mbeanServerConnection = getMBeanServerConnection.invoke(jmxConnector);
    			}
    			else{
    				Properties properties = new Properties();
    				properties.put(Context.INITIAL_CONTEXT_FACTORY, modalita2Factory);
    				properties.put(Context.PROVIDER_URL, modalita2ServerUrl);
    				GestoreJNDI jndi = new GestoreJNDI(properties);
    				this.mbeanServerConnection = jndi.lookup("jmx/invoker/RMIAdaptor");
    				
    				if(modalita2Username!=null && modalita2Password!=null){
    					
    					Class<?>simplePrincipalClass = Class.forName("org.jboss.security.SimplePrincipal");
    					Constructor<?> constructorPrincipalClass = simplePrincipalClass.getConstructor(String.class);
    					Object simplePrincipal = constructorPrincipalClass.newInstance(modalita2Username);
    					
    					Class<?>securityAssociationClass = Class.forName("org.jboss.security.SecurityAssociation");
    					Method setPrincipal = securityAssociationClass.getMethod("setPrincipal", simplePrincipalClass);
    					setPrincipal.invoke(null, simplePrincipal);
    					Method setCredential = securityAssociationClass.getMethod("setCredential", String.class);
    					setCredential.invoke(null, modalita2Password);
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
								throw new RisorseJMXException("MBeanServer di default non trovato");
							else{
								String msg = "Attivata gestione jmx attraverso MBeanServer: "+this.mbeanServer.toString();
								if(logger==null)
									loggerConsole.info(msg);
								this.logInfo(msg);
							}
						}else{
							throw new RisorseJMXException("Lista di MBean Server di default vuota ?");
						}
					}
				}
			}
			
		}catch(Exception e){
			this.logError("Riscontrato errore durante l'inizializzazione del gestore delle RisorseJMX: "+e.getMessage(),e);
			throw new RisorseJMXException("Riscontrato errore durante l'inizializzazione del gestore delle RisorseJMX: "+e.getMessage(),e);
		}
	}
	
	
	
		
	
	/**
	 * Registrazione del MBean generico
	 * 
	 * @throws RisorseJMXException
	 */
	public void registerMBean(Class<?> c,String nome)throws RisorseJMXException{
		this.registerMBean(c, CostantiJMX.JMX_DOMINIO,CostantiJMX.JMX_TYPE, nome, true);		
	}
	public void registerMBean(Class<?> c,String dominio,String type,String nome)throws RisorseJMXException{
		this.registerMBean(c, dominio, type, nome, true);		
	}
	public void registerMBean(Class<?> c,String dominio,String type,String nome, boolean throwExceptionAlreadyExists)throws RisorseJMXException{
		try{
			javax.management.ObjectName jmxName = 
				new javax.management.ObjectName(dominio,type,nome);
			if(this.mbeanServer==null){
				throw new RisorseJMXException("Operazione di registrazione permessa solo se il gestore viene inizializzato con il costruttore di default o indicando l'MBeanServer");
			}
			this.mbeanServer.registerMBean(ClassLoaderUtilities.newInstance(c), jmxName);
			this.jmxNames.add(jmxName);
		}catch(Exception e){
			if((e instanceof javax.management.InstanceAlreadyExistsException) && !throwExceptionAlreadyExists){
				this.logDebug("Risorsa JMX ["+nome+"] già esistente: "+e.getMessage(),e);
			}
			else{
				this.logError("Riscontrato errore durante l'inizializzazione della risorsa JMX ["+nome+"]: "+e.getMessage(),e);
				throw new RisorseJMXException("Riscontrato errore durante l'inizializzazione della risorsa JMX ["+nome+"]: "+e.getMessage(),e);
			}
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
					throw new RisorseJMXException("Operazione di cancellazione permessa solo se il gestore viene inizializzato con il costruttore di default o indicando l'MBeanServer");
				}
				this.mbeanServer.unregisterMBean(jmxName);
				this.logInfo("Unbound della risorsa JMX ["+jmxName.toString()+"]");
			}catch(Exception e){
				this.logError("Riscontrato errore durante l'unbound della risorsa JMX ["+jmxName.toString()+"]: "+e.getMessage(),e);
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
				Class<?> c = Class.forName(MBEAN_SERVER_CONNECTION);
				Method m = c.getMethod("getAttribute", ObjectName.class, String.class);
				return m.invoke(this.mbeanServerConnection, name, nomeAttributo);
			}
			else{
				return this.mbeanServer.getAttribute(name, nomeAttributo);
			}
		}catch(Exception e){
			String msg = "Riscontrato errore durante la lettura dell'attributo ["+nomeAttributo+"] nella risorsa ["+nomeRisorsa+"]: "+e.getMessage();
			this.logError(msg,e);
			throw new RisorseJMXException(msg,e);
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
				Class<?> c = Class.forName(MBEAN_SERVER_CONNECTION);
				Method m = c.getMethod("setAttribute", ObjectName.class, javax.management.Attribute.class);
				m.invoke(this.mbeanServerConnection, name, nomeAttributo);
			}
			else{
				this.mbeanServer.setAttribute(name, attribute);
			}
		}catch(Exception e){
			String msg = "Riscontrato errore durante l'aggiornamento dell'attributo ["+nomeAttributo+"] della risorsa ["+nomeRisorsa+"]: "+e.getMessage();
			this.logError(msg,e);
			throw new RisorseJMXException(msg,e);
		}	
	}
	
	
	public Object invoke(String nomeRisorsa,String nomeMetodo,Object[]params,String[]signature)throws RisorseJMXException{
		return this.invoke(CostantiJMX.JMX_DOMINIO,CostantiJMX.JMX_TYPE,nomeRisorsa,nomeMetodo,params,signature);
	}
	public Object invoke(String dominio,String tipo,String nomeRisorsa,String nomeMetodo,Object[]params,String[]signature)throws RisorseJMXException{
		try{
			ObjectName name = new ObjectName(dominio,tipo,nomeRisorsa);
			if(this.mbeanServerConnection!=null){
				Class<?> c = Class.forName(MBEAN_SERVER_CONNECTION);
				Method m = c.getMethod("invoke", ObjectName.class, String.class, Object[].class, String[].class);
				return m.invoke(this.mbeanServerConnection, name, nomeMetodo, params, signature);
			}
			else{
				return this.mbeanServer.invoke(name, nomeMetodo, params, signature);
			}
		}catch(Exception e){
			String msg = "Riscontrato errore durante l'invocazione del metodo ["+nomeMetodo+"] della risorsa ["+nomeRisorsa+"]: "+e.getMessage();
			this.logError(msg,e);
			throw new RisorseJMXException(msg,e);
		}	
	}
	
}
