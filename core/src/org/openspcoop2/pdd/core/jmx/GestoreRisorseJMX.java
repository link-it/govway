/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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


package org.openspcoop2.pdd.core.jmx;

import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.utils.jmx.RisorseJMXException;


/**
 * Gestore risorse JMX utilizzate da OpenSPCoop
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreRisorseJMX extends org.openspcoop2.utils.jmx.GestoreRisorseJMX {

	public GestoreRisorseJMX() throws RisorseJMXException {
		super();
	}


	public GestoreRisorseJMX(Logger logger) throws RisorseJMXException {
		super(logger);
	}


	public GestoreRisorseJMX(String jndiNameMBeanServer,
			Properties jndiContext, Logger logger, Logger loggerConsole)
			throws RisorseJMXException {
		super(jndiNameMBeanServer, jndiContext, logger, loggerConsole);
	}
	public GestoreRisorseJMX(String jndiNameMBeanServer,
			Properties jndiContext, Logger logger) throws RisorseJMXException {
		super(jndiNameMBeanServer, jndiContext, logger);
	}
	public GestoreRisorseJMX(String jndiNameMBeanServer, Properties jndiContext)
			throws RisorseJMXException {
		super(jndiNameMBeanServer, jndiContext);
	}
	
	
	public GestoreRisorseJMX(String tipoApplicationServer, String factory, String serverUrl, String username,  String password) throws RisorseJMXException{
		super(tipoApplicationServer,factory,serverUrl,username,password);
	}
	public GestoreRisorseJMX(String tipoApplicationServer, String factory, String serverUrl, String username,  String password,
			Logger logger) throws RisorseJMXException{
		super(tipoApplicationServer,factory,serverUrl,username,password,logger);
	}
	public GestoreRisorseJMX(String tipoApplicationServer, String factory, String serverUrl, String username,  String password,
			Logger logger,Logger loggerConsole) throws RisorseJMXException{
		super(tipoApplicationServer,factory,serverUrl,username,password,logger,loggerConsole);
	}


	/**
	 * Registrazione del MBean per la ConfigurazionePdD
	 * 
	 * @throws RisorseJMXException
	 */
	public void registerMBeanConfigurazionePdD()throws RisorseJMXException{
		try{
			this.registerMBean(org.openspcoop2.pdd.core.jmx.ConfigurazionePdD.class, CostantiPdD.JMX_CONFIGURAZIONE_PDD);
		}catch(Exception e){
			this.log.error("Riscontrato errore durante l'inizializzazione della risorsa JMX ConfigurazionePdD: "+e.getMessage(),e);
			throw new RisorseJMXException("Riscontrato errore durante l'inizializzazione della risorsa JMX ConfigurazionePdD: "+e.getMessage(),e);
		}	
		
	}
	
	
	/**
	 * Registrazione del MBean per l'accesso al registro dei servizi
	 * 
	 * @throws RisorseJMXException
	 */
	public void registerMBeanAccessoRegistroServizi()throws RisorseJMXException{
		try{
			this.registerMBean(org.openspcoop2.pdd.core.jmx.AccessoRegistroServizi.class, CostantiPdD.JMX_REGISTRO_SERVIZI);
		}catch(Exception e){
			this.log.error("Riscontrato errore durante l'inizializzazione della risorsa JMX AccessoRegistroServizi: "+e.getMessage(),e);
			throw new RisorseJMXException("Riscontrato errore durante l'inizializzazione della risorsa JMX AccessoRegistroServizi: "+e.getMessage(),e);
		}	
		
	}
	
	/**
	 * Registrazione del MBean per il monitoraggio
	 * 
	 * @throws RisorseJMXException
	 */
	public void registerMBeanMonitoraggioRisorse()throws RisorseJMXException{
		try{
			this.registerMBean(org.openspcoop2.pdd.core.jmx.MonitoraggioRisorse.class, CostantiPdD.JMX_MONITORAGGIO_RISORSE);
		}catch(Exception e){
			this.log.error("Riscontrato errore durante l'inizializzazione della risorsa JMX MonitoraggioRisorse: "+e.getMessage(),e);
			throw new RisorseJMXException("Riscontrato errore durante l'inizializzazione della risorsa JMX MonitoraggioRisorse: "+e.getMessage(),e);
		}	
		
	}
	
	/**
	 * Registrazione del MBean per l'autorizzazione
	 * 
	 * @throws RisorseJMXException
	 */
	public void registerMBeanAutorizzazione()throws RisorseJMXException{
		try{
			this.registerMBean(org.openspcoop2.pdd.core.jmx.EngineAutorizzazione.class, CostantiPdD.JMX_AUTORIZZAZIONE);
		}catch(Exception e){
			this.log.error("Riscontrato errore durante l'inizializzazione della risorsa JMX Autorizzazione: "+e.getMessage(),e);
			throw new RisorseJMXException("Riscontrato errore durante l'inizializzazione della risorsa JMX Autorizzazione: "+e.getMessage(),e);
		}	
		
	}
	
	/**
	 * Registrazione del MBean per l'autenticazione
	 * 
	 * @throws RisorseJMXException
	 */
	public void registerMBeanAutenticazione()throws RisorseJMXException{
		try{
			this.registerMBean(org.openspcoop2.pdd.core.jmx.EngineAutenticazione.class, CostantiPdD.JMX_AUTENTICAZIONE);
		}catch(Exception e){
			this.log.error("Riscontrato errore durante l'inizializzazione della risorsa JMX Autenticazione: "+e.getMessage(),e);
			throw new RisorseJMXException("Riscontrato errore durante l'inizializzazione della risorsa JMX Autenticazione: "+e.getMessage(),e);
		}	
		
	}
	
	/**
	 * Registrazione del MBean per la gestione dei token
	 * 
	 * @throws RisorseJMXException
	 */
	public void registerMBeanGestioneToken()throws RisorseJMXException{
		try{
			this.registerMBean(org.openspcoop2.pdd.core.jmx.EngineGestioneToken.class, CostantiPdD.JMX_TOKEN);
		}catch(Exception e){
			this.log.error("Riscontrato errore durante l'inizializzazione della risorsa JMX Token: "+e.getMessage(),e);
			throw new RisorseJMXException("Riscontrato errore durante l'inizializzazione della risorsa JMX Token: "+e.getMessage(),e);
		}	
		
	}
	
	/**
	 * Registrazione del MBean per il salvataggio delle risposte in cache
	 * 
	 * @throws RisorseJMXException
	 */
	public void registerMBeanResponseCaching()throws RisorseJMXException{
		try{
			this.registerMBean(org.openspcoop2.pdd.core.jmx.EngineResponseCaching.class, CostantiPdD.JMX_RESPONSE_CACHING);
		}catch(Exception e){
			this.log.error("Riscontrato errore durante l'inizializzazione della risorsa JMX Response Caching: "+e.getMessage(),e);
			throw new RisorseJMXException("Riscontrato errore durante l'inizializzazione della risorsa JMX Response Caching: "+e.getMessage(),e);
		}	
		
	}
	
	/**
	 * Registrazione del MBean per il salvataggio dei keystore
	 * 
	 * @throws RisorseJMXException
	 */
	public void registerMBeanKeystoreCaching()throws RisorseJMXException{
		try{
			this.registerMBean(org.openspcoop2.pdd.core.jmx.EngineKeystoreCaching.class, CostantiPdD.JMX_KEYSTORE_CACHING);
		}catch(Exception e){
			this.log.error("Riscontrato errore durante l'inizializzazione della risorsa JMX Keystore Caching: "+e.getMessage(),e);
			throw new RisorseJMXException("Riscontrato errore durante l'inizializzazione della risorsa JMX Keystore Caching: "+e.getMessage(),e);
		}	
		
	}
	
	/**
	 * Registrazione del MBean per il repository dei Messaggi
	 * 
	 * @throws RisorseJMXException
	 */
	public void registerMBeanRepositoryMessaggi()throws RisorseJMXException{
		try{
			this.registerMBean(org.openspcoop2.pdd.core.jmx.RepositoryMessaggi.class, CostantiPdD.JMX_REPOSITORY_MESSAGGI);
		}catch(Exception e){
			this.log.error("Riscontrato errore durante l'inizializzazione della risorsa JMX RepositoryMessaggi: "+e.getMessage(),e);
			throw new RisorseJMXException("Riscontrato errore durante l'inizializzazione della risorsa JMX RepositoryMessaggi: "+e.getMessage(),e);
		}	
		
	}
	

	/**
	 * Registrazione del MBean per lo stato dei servizi PdD
	 * 
	 * @throws RisorseJMXException
	 */
	public void registerMBeanStatoServiziPdD()throws RisorseJMXException{
		try{
			this.registerMBean(org.openspcoop2.pdd.core.jmx.StatoServiziJMXResource.class, CostantiPdD.JMX_STATO_SERVIZI_PDD);
		}catch(Exception e){
			this.log.error("Riscontrato errore durante l'inizializzazione della risorsa JMX StatoServiziPdD: "+e.getMessage(),e);
			throw new RisorseJMXException("Riscontrato errore durante l'inizializzazione della risorsa JMX StatoServiziPdD: "+e.getMessage(),e);
		}	
		
	}
	
	
	/**
	 * Registrazione del MBean per le informazioni statistiche
	 * 
	 * @throws RisorseJMXException
	 */
	public void registerMBeanStatistichePdD()throws RisorseJMXException{
		try{
			this.registerMBean(org.openspcoop2.pdd.core.jmx.StatisticsJMXResource.class, CostantiPdD.JMX_INFORMAZIONI_STATISTICHE_PDD);
		}catch(Exception e){
			this.log.error("Riscontrato errore durante l'inizializzazione della risorsa JMX StatistichePdD: "+e.getMessage(),e);
			throw new RisorseJMXException("Riscontrato errore durante l'inizializzazione della risorsa JMX StatistichePdD: "+e.getMessage(),e);
		}	
		
	}
	
	
	
	/**
	 * Registrazione del MBean per lo stato dei servizi PdD
	 * 
	 * @throws RisorseJMXException
	 */
	public void registerMBeanSystemPropertiesPdD()throws RisorseJMXException{
		try{
			this.registerMBean(org.openspcoop2.pdd.core.jmx.SysPropsJMXResource.class, CostantiPdD.JMX_SYSTEM_PROPERTIES_PDD);
		}catch(Exception e){
			this.log.error("Riscontrato errore durante l'inizializzazione della risorsa JMX SystemPropertiesPdD: "+e.getMessage(),e);
			throw new RisorseJMXException("Riscontrato errore durante l'inizializzazione della risorsa JMX SystemPropertiesPdD: "+e.getMessage(),e);
		}	
		
	}
	
	
	/**
	 * Registrazione del MBean per la configurazione di sistema della PdD
	 * 
	 * @throws RisorseJMXException
	 */
	public void registerMBeanConfigurazioneSistema()throws RisorseJMXException{
		try{
			this.registerMBean(org.openspcoop2.pdd.core.jmx.ConfigurazioneSistema.class, CostantiPdD.JMX_CONFIGURAZIONE_SISTEMA);
		}catch(Exception e){
			this.log.error("Riscontrato errore durante l'inizializzazione della risorsa JMX ConfigurazioneSistema: "+e.getMessage(),e);
			throw new RisorseJMXException("Riscontrato errore durante l'inizializzazione della risorsa JMX ConfigurazioneSistema: "+e.getMessage(),e);
		}	
		
	}
	
	
	/**
	 * Registrazione del MBean per il Controllo del Traffico della PdD
	 * 
	 * @throws RisorseJMXException
	 */
	public void registerMBeanControlloTraffico()throws RisorseJMXException{
		try{
			this.registerMBean(org.openspcoop2.pdd.core.jmx.ControlloTraffico.class, CostantiPdD.JMX_CONTROLLO_TRAFFICO);
		}catch(Exception e){
			this.log.error("Riscontrato errore durante l'inizializzazione della risorsa JMX ControlloTraffico: "+e.getMessage(),e);
			throw new RisorseJMXException("Riscontrato errore durante l'inizializzazione della risorsa JMX ControlloTraffico: "+e.getMessage(),e);
		}	
		
	}
	

	
	public Object getAttributeMBeanConfigurazionePdD(String nomeAttributo)throws RisorseJMXException{
		return this.getAttribute(CostantiPdD.JMX_CONFIGURAZIONE_PDD, nomeAttributo);
	}
	public Object getAttributeMBeanAccessoRegistroServizi(String nomeAttributo)throws RisorseJMXException{
		return this.getAttribute(CostantiPdD.JMX_REGISTRO_SERVIZI, nomeAttributo);
	}
	public Object getAttributeMBeanAutorizzazione(String nomeAttributo)throws RisorseJMXException{
		return this.getAttribute(CostantiPdD.JMX_AUTORIZZAZIONE, nomeAttributo);
	}
	public Object getAttributeMBeanAutenticazione(String nomeAttributo)throws RisorseJMXException{
		return this.getAttribute(CostantiPdD.JMX_AUTENTICAZIONE, nomeAttributo);
	}
	public Object getAttributeMBeanGestioneToken(String nomeAttributo)throws RisorseJMXException{
		return this.getAttribute(CostantiPdD.JMX_TOKEN, nomeAttributo);
	}
	public Object getAttributeMBeanResponseCaching(String nomeAttributo)throws RisorseJMXException{
		return this.getAttribute(CostantiPdD.JMX_RESPONSE_CACHING, nomeAttributo);
	}
	public Object getAttributeMBeanMonitoraggioRisorse(String nomeAttributo)throws RisorseJMXException{
		return this.getAttribute(CostantiPdD.JMX_MONITORAGGIO_RISORSE, nomeAttributo);
	}
	public Object getAttributeMBeanRepositoryMessaggi(String nomeAttributo)throws RisorseJMXException{
		return this.getAttribute(CostantiPdD.JMX_REPOSITORY_MESSAGGI, nomeAttributo);
	}
	public Object getAttributeMBeanStatoServiziPdD(String nomeAttributo)throws RisorseJMXException{
		return this.getAttribute(CostantiPdD.JMX_STATO_SERVIZI_PDD, nomeAttributo);
	}
	public Object getAttributeMBeanStatistichePdD(String nomeAttributo)throws RisorseJMXException{
		return this.getAttribute(CostantiPdD.JMX_INFORMAZIONI_STATISTICHE_PDD, nomeAttributo);
	}
	public Object getAttributeMBeanSystemPropertiesPdD(String nomeAttributo)throws RisorseJMXException{
		return this.getAttribute(CostantiPdD.JMX_SYSTEM_PROPERTIES_PDD, nomeAttributo);
	}
	public Object getAttributeMBeanConfigurazioneSistema(String nomeAttributo)throws RisorseJMXException{
		return this.getAttribute(CostantiPdD.JMX_CONFIGURAZIONE_SISTEMA, nomeAttributo);
	}
	public Object getAttributeMBeanControlloTraffico(String nomeAttributo)throws RisorseJMXException{
		return this.getAttribute(CostantiPdD.JMX_CONTROLLO_TRAFFICO, nomeAttributo);
	}

	
	
	
	
	public Object invokeMethodMBeanConfigurazionePdD(String nomeMetodo,Object[]params,String[]signature)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_CONFIGURAZIONE_PDD, nomeMetodo, params, signature);
	}
	public Object invokeMethodMBeanConfigurazionePdD(String nomeMetodo)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_CONFIGURAZIONE_PDD, nomeMetodo, null, null);
	}
	
	public Object invokeMethodMBeanAccessoRegistroServizi(String nomeMetodo,Object[]params,String[]signature)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_REGISTRO_SERVIZI, nomeMetodo, params, signature);
	}
	public Object invokeMethodMBeanAccessoRegistroServizi(String nomeMetodo)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_REGISTRO_SERVIZI, nomeMetodo, null, null);
	}
	
	public Object invokeMethodMBeanAutorizzazione(String nomeMetodo,Object[]params,String[]signature)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_AUTORIZZAZIONE, nomeMetodo, params, signature);
	}
	public Object invokeMethodMBeanAutorizzazione(String nomeMetodo)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_AUTORIZZAZIONE, nomeMetodo, null, null);
	}
	
	public Object invokeMethodMBeanAutenticazione(String nomeMetodo,Object[]params,String[]signature)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_AUTENTICAZIONE, nomeMetodo, params, signature);
	}
	public Object invokeMethodMBeanAutenticazione(String nomeMetodo)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_AUTENTICAZIONE, nomeMetodo, null, null);
	}
	
	public Object invokeMethodMBeanGestioneToken(String nomeMetodo,Object[]params,String[]signature)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_TOKEN, nomeMetodo, params, signature);
	}
	public Object invokeMethodMBeanGestioneToken(String nomeMetodo)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_TOKEN, nomeMetodo, null, null);
	}
	
	public Object invokeMethodMBeanResponseCaching(String nomeMetodo,Object[]params,String[]signature)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_RESPONSE_CACHING, nomeMetodo, params, signature);
	}
	public Object invokeMethodMBeanResponseCaching(String nomeMetodo)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_RESPONSE_CACHING, nomeMetodo, null, null);
	}
	
	public Object invokeMethodMBeanMonitoraggioRisorse(String nomeMetodo,Object[]params,String[]signature)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_MONITORAGGIO_RISORSE, nomeMetodo, params, signature);
	}
	public Object invokeMethodMBeanMonitoraggioRisorse(String nomeMetodo)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_MONITORAGGIO_RISORSE, nomeMetodo, null, null);
	}
	
	public Object invokeMethodMBeanRepositoryMessaggi(String nomeMetodo,Object[]params,String[]signature)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_REPOSITORY_MESSAGGI, nomeMetodo, params, signature);
	}
	public Object invokeMethodMBeanRepositoryMessaggi(String nomeMetodo)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_REPOSITORY_MESSAGGI, nomeMetodo, null, null);
	}
	
	public Object invokeMethodMBeanStatoServiziPdD(String nomeMetodo,Object[]params,String[]signature)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_STATO_SERVIZI_PDD, nomeMetodo, params, signature);
	}
	public Object invokeMethodMBeanStatoServiziPdD(String nomeMetodo)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_STATO_SERVIZI_PDD, nomeMetodo, null, null);
	}
	
	public Object invokeMethodMBeanStatistichePdD(String nomeMetodo,Object[]params,String[]signature)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_INFORMAZIONI_STATISTICHE_PDD, nomeMetodo, params, signature);
	}
	public Object invokeMethodMBeanStatistichePdD(String nomeMetodo)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_INFORMAZIONI_STATISTICHE_PDD, nomeMetodo, null, null);
	}
	
	public Object invokeMethodMBeanSystemPropertiesPdD(String nomeMetodo,Object[]params,String[]signature)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_SYSTEM_PROPERTIES_PDD, nomeMetodo, params, signature);
	}
	public Object invokeMethodMBeanSystemPropertiesPdD(String nomeMetodo)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_SYSTEM_PROPERTIES_PDD, nomeMetodo, null, null);
	}
	
	public Object invokeMethodMBeanConfigurazioneSistema(String nomeMetodo,Object[]params,String[]signature)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_CONFIGURAZIONE_SISTEMA, nomeMetodo, params, signature);
	}
	public Object invokeMethodMBeanConfigurazioneSistema(String nomeMetodo)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_CONFIGURAZIONE_SISTEMA, nomeMetodo, null, null);
	}
	
	public Object invokeMethodMBeanControlloTraffico(String nomeMetodo,Object[]params,String[]signature)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_CONTROLLO_TRAFFICO, nomeMetodo, params, signature);
	}
	public Object invokeMethodMBeanControlloTraffico(String nomeMetodo)throws RisorseJMXException{
		return invoke(CostantiPdD.JMX_CONTROLLO_TRAFFICO, nomeMetodo, null, null);
	}

}
