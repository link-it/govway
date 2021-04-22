/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ReflectionException;

import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.id.IDConnettore;
import org.openspcoop2.pdd.config.ConfigurazionePdD;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;
import org.openspcoop2.pdd.core.behaviour.built_in.load_balance.GestoreLoadBalancerCaching;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.slf4j.Logger;


/**
 * Implementazione JMX per la gestione della Configurazione di OpenSPCoop
 *   
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreConsegnaApplicativi extends NotificationBroadcasterSupport implements DynamicMBean {

	/** Nomi proprieta' */
	public final static String MAX_LIFE_PRESA_IN_CONSEGNA = "maxlifePresaInConsegna";

	/** Nomi metodi' */
	public final static String THREAD_POOL_STATUS = "getThreadPoolStatus";
	public final static String QUEUE_CONFIG = "getQueueConfig";
	public final static String GET_APPLICATIVI_PRIORITARI = "getApplicativiPrioritari";
	public final static String GET_CONNETTORI_PRIORITARI = "getConnettoriPrioritari";
	public final static String UPDATE_CONNETTORI_PRIORITARI = "updateConnettoriPrioritari";
	public final static String RESET_CONNETTORI_PRIORITARI = "resetConnettoriPrioritari";
		
	/** Attributi */
	private boolean cacheAbilitata = false;
	private int maxLifePresaInConsegna = -1;
	
	/** getAttribute */
	@Override
	public Object getAttribute(String attributeName) throws AttributeNotFoundException,MBeanException,ReflectionException{
		
		if( (attributeName==null) || (attributeName.equals("")) )
			throw new IllegalArgumentException("Il nome dell'attributo e' nullo o vuoto");
		
		if(attributeName.equals(JMXUtils.CACHE_ATTRIBUTE_ABILITATA))
			return this.cacheAbilitata;
		
		if(attributeName.equals(GestoreConsegnaApplicativi.MAX_LIFE_PRESA_IN_CONSEGNA))
			return this.maxLifePresaInConsegna;
		
		throw new AttributeNotFoundException("Attributo "+attributeName+" non trovato");
	}
	
	/** getAttributes */
	@Override
	public AttributeList getAttributes(String [] attributesNames){
		
		if(attributesNames==null)
			throw new IllegalArgumentException("Array nullo");
		
		AttributeList list = new AttributeList();
		for (int i=0; i<attributesNames.length; i++){
			try{
				list.add(new Attribute(attributesNames[i],getAttribute(attributesNames[i])));
			}catch(JMException ex){}
		}
		return list;
	}
	
	/** setAttribute */
	@Override
	public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException{
		
		if( attribute==null )
			throw new IllegalArgumentException("Il nome dell'attributo e' nullo");
		
		try{
			
			if(attribute.getName().equals(JMXUtils.CACHE_ATTRIBUTE_ABILITATA)){
				boolean v = (Boolean) attribute.getValue();
				if(v){
					// la cache DEVE essere abilitata
					if(!this.cacheAbilitata){
						this.abilitaCache();
					}
				}
				else{
					// la cache DEVE essere disabilitata
					if(this.cacheAbilitata){
						this.disabilitaCache();
					}
				}
			}
			
			throw new AttributeNotFoundException("Attributo "+attribute.getName()+" non trovato");
			
		}catch(ClassCastException ce){
			throw new InvalidAttributeValueException("il tipo "+attribute.getValue().getClass()+" dell'attributo "+attribute.getName()+" non e' valido");
		}catch(JMException j){
			throw new MBeanException(j);
		}
		
	}
	
	/** setAttributes */
	@Override
	public AttributeList setAttributes(AttributeList list){
		
		if(list==null)
			throw new IllegalArgumentException("Lista degli attributi e' nulla");
		
		AttributeList ret = new AttributeList();
		Iterator<?> it = ret.iterator();
		
		while(it.hasNext()){
			try{
				Attribute attribute = (Attribute) it.next();
				setAttribute(attribute);
				ret.add(attribute);
			}catch(JMException ex){}
		}
		
		return ret;
		
	}
	
	/** invoke */
	@Override
	public Object invoke(String actionName, Object[]params, String[]signature) throws MBeanException,ReflectionException{
		
		if( (actionName==null) || (actionName.equals("")) )
			throw new IllegalArgumentException("Nessuna operazione definita");
		
		if(actionName.equals(JMXUtils.CACHE_METHOD_NAME_RESET)){
			return this.resetCache();
		}
		
		if(actionName.equals(JMXUtils.CACHE_METHOD_NAME_PRINT_STATS)){
			return this.printStatCache();
		}
				
		if(actionName.equals(JMXUtils.CACHE_METHOD_NAME_DISABILITA)){
			return this.disabilitaCacheConEsito();
		}
		
		if(actionName.equals(JMXUtils.CACHE_METHOD_NAME_ABILITA)){
			if(params.length != 4)
				throw new MBeanException(new Exception("["+JMXUtils.CACHE_METHOD_NAME_ABILITA+"] Lunghezza parametri non corretta: "+params.length));
			
			Long param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (Long)params[0];
				if(param1<0){
					param1 = null;
				}
			}
			
			Boolean param2 = null;
			if(params[1]!=null && !"".equals(params[1])){
				param2 = (Boolean)params[1];
			}
			
			Long param3 = null;
			if(params[2]!=null && !"".equals(params[2])){
				param3 = (Long)params[2];
				if(param3<0){
					param3 = null;
				}
			}
			
			Long param4 = null;
			if(params[3]!=null && !"".equals(params[3])){
				param4 = (Long)params[3];
				if(param4<0){
					param4 = null;
				}
			}
					
			return this.abilitaCache(param1, param2, param3, param4 );
		}
		
		if(actionName.equals(JMXUtils.CACHE_METHOD_NAME_LIST_KEYS)){
			return this.listKeysCache();
		}
		
		if(actionName.equals(JMXUtils.CACHE_METHOD_NAME_GET_OBJECT)){
			
			if(params.length != 1)
				throw new MBeanException(new Exception("["+JMXUtils.CACHE_METHOD_NAME_GET_OBJECT+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			
			return this.getObjectCache(param1);
		}
		
		if(actionName.equals(JMXUtils.CACHE_METHOD_NAME_REMOVE_OBJECT)){
			
			if(params.length != 1)
				throw new MBeanException(new Exception("["+JMXUtils.CACHE_METHOD_NAME_REMOVE_OBJECT+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			
			return this.removeObjectCache(param1);
		}
			
		if(actionName.equals(THREAD_POOL_STATUS)){
			
			if(params.length > 1)
				throw new MBeanException(new Exception("["+THREAD_POOL_STATUS+"] Lunghezza parametri non corretta: "+params.length));
			
			if(params.length > 0) {
				
				String param1 = null;
				if(params[0]!=null && !"".equals(params[0])){
					param1 = (String)params[0];
				}
				
				return this.getThreadPoolStatus(param1);
			}
			else {
				return this.getThreadPoolStatus(CostantiConfigurazione.CODA_DEFAULT);
			}
		}
		
		if(actionName.equals(QUEUE_CONFIG)){
			
			if(params.length > 1)
				throw new MBeanException(new Exception("["+QUEUE_CONFIG+"] Lunghezza parametri non corretta: "+params.length));
			
			if(params.length > 0) {
				
				String param1 = null;
				if(params[0]!=null && !"".equals(params[0])){
					param1 = (String)params[0];
				}
				
				return this.getQueueConfig(param1);
			}
			else {
				return this.getQueueConfig(CostantiConfigurazione.CODA_DEFAULT);
			}
		}
		
		if(actionName.equals(GET_APPLICATIVI_PRIORITARI)){
			
			if(params.length > 1)
				throw new MBeanException(new Exception("["+QUEUE_CONFIG+"] Lunghezza parametri non corretta: "+params.length));
			
			if(params.length > 0) {
				
				String param1 = null;
				if(params[0]!=null && !"".equals(params[0])){
					param1 = (String)params[0];
				}
				
				return this.getApplicativiPrioritari(param1);
			}
			else {
				return this.getApplicativiPrioritari(CostantiConfigurazione.CODA_DEFAULT);
			}
		}
		
		if(actionName.equals(GET_CONNETTORI_PRIORITARI)){
			
			if(params.length > 1)
				throw new MBeanException(new Exception("["+QUEUE_CONFIG+"] Lunghezza parametri non corretta: "+params.length));
			
			if(params.length > 0) {
				
				String param1 = null;
				if(params[0]!=null && !"".equals(params[0])){
					param1 = (String)params[0];
				}
				
				return this.getConnettoriPrioritari(param1);
			}
			else {
				return this.getConnettoriPrioritari(CostantiConfigurazione.CODA_DEFAULT);
			}
		}
		
		if(actionName.equals(UPDATE_CONNETTORI_PRIORITARI)){
			
			if(params.length > 1)
				throw new MBeanException(new Exception("["+QUEUE_CONFIG+"] Lunghezza parametri non corretta: "+params.length));
			
			if(params.length > 0) {
				
				String param1 = null;
				if(params[0]!=null && !"".equals(params[0])){
					param1 = (String)params[0];
				}
				
				return this.updateConnettoriPrioritari(param1);
			}
			else {
				return this.updateConnettoriPrioritari(CostantiConfigurazione.CODA_DEFAULT);
			}
		}
		
		if(actionName.equals(RESET_CONNETTORI_PRIORITARI)){
			
			if(params.length > 1)
				throw new MBeanException(new Exception("["+QUEUE_CONFIG+"] Lunghezza parametri non corretta: "+params.length));
			
			if(params.length > 0) {
				
				String param1 = null;
				if(params[0]!=null && !"".equals(params[0])){
					param1 = (String)params[0];
				}
				
				return this.resetConnettoriPrioritari(param1);
			}
			else {
				return this.resetConnettoriPrioritari(CostantiConfigurazione.CODA_DEFAULT);
			}
		}
				
		throw new UnsupportedOperationException("Operazione "+actionName+" sconosciuta");
	}
	
	/* MBean info */
	@Override
	public MBeanInfo getMBeanInfo(){
				
		// Descrizione della classe nel MBean
		String className = this.getClass().getName();
		String description = "Risorsa per la configurazione ("+this.openspcoopProperties.getVersione()+")";

		// MetaData per l'attributo abilitaCache
		MBeanAttributeInfo cacheAbilitataVAR = JMXUtils.MBEAN_ATTRIBUTE_INFO_CACHE_ABILITATA;

		// MetaData per l'attributo maxLife
		MBeanAttributeInfo maxLife 
			= new MBeanAttributeInfo(GestoreConsegnaApplicativi.MAX_LIFE_PRESA_IN_CONSEGNA,String.class.getName(),
						"Tempo massimo (in secondi) che un messaggio può essere tenuto in consegna da parte del gestore",
							JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
				
		// MetaData per l'operazione resetCache
		MBeanOperationInfo resetCacheOP = JMXUtils.MBEAN_OPERATION_RESET_CACHE;
		
		// MetaData per l'operazione printStatCache
		MBeanOperationInfo printStatCacheOP = JMXUtils.MBEAN_OPERATION_PRINT_STATS_CACHE;
		
		// MetaData per l'operazione disabilitaCache
		MBeanOperationInfo disabilitaCacheOP = JMXUtils.MBEAN_OPERATION_DISABILITA_CACHE;
		
		// MetaData per l'operazione abilitaCache con parametri
		MBeanOperationInfo abilitaCacheParametriOP = JMXUtils.MBEAN_OPERATION_ABILITA_CACHE_CON_PARAMETRI;
		
		// MetaData per l'operazione listKeysCache
		MBeanOperationInfo listKeysCacheOP = JMXUtils.MBEAN_OPERATION_LIST_KEYS_CACHE; 

		// MetaData per l'operazione getObjectCache
		MBeanOperationInfo getObjectCacheOP = JMXUtils.MBEAN_OPERATION_GET_OBJECT_CACHE;
		
		// MetaData per l'operazione removeObjectCache
		MBeanOperationInfo removeObjectCacheOP = JMXUtils.MBEAN_OPERATION_REMOVE_OBJECT_CACHE;
				
		// MetaData per l'operazione threadPoolStatus
		MBeanOperationInfo threadPoolStatusDefault 
		= new MBeanOperationInfo(THREAD_POOL_STATUS,"Stato dei thread utilizzati per la consegna dei messaggi nella coda di default",
			null,
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione threadPoolStatus
		MBeanOperationInfo threadPoolStatus 
		= new MBeanOperationInfo(THREAD_POOL_STATUS,"Stato dei thread utilizzati per la consegna dei messaggi nella coda indicata",
			new MBeanParameterInfo[]{
					new MBeanParameterInfo("coda",String.class.getName(),"Nome della coda"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione threadPoolStatus
		MBeanOperationInfo queueConfigDefault 
		= new MBeanOperationInfo(QUEUE_CONFIG,"Configurazione della coda di default",
			null,
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione threadPoolStatus
		MBeanOperationInfo queueConfig 
		= new MBeanOperationInfo(QUEUE_CONFIG,"Configurazione della coda indicata",
			new MBeanParameterInfo[]{
					new MBeanParameterInfo("coda",String.class.getName(),"Nome della coda"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione getApplicativiPrioritariDefault
		MBeanOperationInfo getApplicativiPrioritariDefault 
		= new MBeanOperationInfo(GET_APPLICATIVI_PRIORITARI,"Applicativi configurati come prioritari nella coda di default",
			null,
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione getApplicativiPrioritari
		MBeanOperationInfo getApplicativiPrioritari
		= new MBeanOperationInfo(GET_APPLICATIVI_PRIORITARI,"Applicativi configurati come prioritari nella coda indicata",
			new MBeanParameterInfo[]{
					new MBeanParameterInfo("coda",String.class.getName(),"Nome della coda"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione getConnettoriPrioritariDefault
		MBeanOperationInfo getConnettoriPrioritariDefault 
		= new MBeanOperationInfo(GET_CONNETTORI_PRIORITARI,"Connettori configurati come prioritari nella coda di default",
			null,
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione getConnettoriPrioritari
		MBeanOperationInfo getConnettoriPrioritari
		= new MBeanOperationInfo(GET_CONNETTORI_PRIORITARI,"Connettori configurati come prioritari nella coda indicata",
			new MBeanParameterInfo[]{
					new MBeanParameterInfo("coda",String.class.getName(),"Nome della coda"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione updateConnettoriPrioritariDefault
		MBeanOperationInfo updateConnettoriPrioritariDefault 
		= new MBeanOperationInfo(UPDATE_CONNETTORI_PRIORITARI,"Aggiorna la configurazione dei connettori configurati come prioritari nella coda di default",
			null,
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione updateConnettoriPrioritari
		MBeanOperationInfo updateConnettoriPrioritari
		= new MBeanOperationInfo(UPDATE_CONNETTORI_PRIORITARI,"Aggiorna la configurazione dei connettori configurati come prioritari nella coda indicata",
			new MBeanParameterInfo[]{
					new MBeanParameterInfo("coda",String.class.getName(),"Nome della coda"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione resetConnettoriPrioritariDefault
		MBeanOperationInfo resetConnettoriPrioritariDefault 
		= new MBeanOperationInfo(RESET_CONNETTORI_PRIORITARI,"Rimuove i connettori configurati come prioritari nella coda di default",
			null,
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione resetConnettoriPrioritari
		MBeanOperationInfo resetConnettoriPrioritari
		= new MBeanOperationInfo(RESET_CONNETTORI_PRIORITARI,"Rimuove i connettori configurati come prioritari nella coda indicata",
			new MBeanParameterInfo[]{
					new MBeanParameterInfo("coda",String.class.getName(),"Nome della coda"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// Mbean costruttore
		MBeanConstructorInfo defaultConstructor = new MBeanConstructorInfo("Default Constructor","Crea e inizializza una nuova istanza del MBean",null);

		// Lista attributi
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[]{cacheAbilitataVAR,maxLife};
		
		// Lista Costruttori
		MBeanConstructorInfo[] constructors = new MBeanConstructorInfo[]{defaultConstructor};
		
		// Lista operazioni
		List<MBeanOperationInfo> listOperation = new ArrayList<>();
		listOperation.add(resetCacheOP);
		listOperation.add(printStatCacheOP);
		listOperation.add(disabilitaCacheOP);
		listOperation.add(abilitaCacheParametriOP);
		listOperation.add(listKeysCacheOP);
		listOperation.add(getObjectCacheOP);
		listOperation.add(removeObjectCacheOP);
		listOperation.add(threadPoolStatusDefault);
		listOperation.add(threadPoolStatus);
		listOperation.add(queueConfigDefault);
		listOperation.add(queueConfig);
		listOperation.add(getApplicativiPrioritariDefault);
		listOperation.add(getApplicativiPrioritari);
		listOperation.add(getConnettoriPrioritariDefault);
		listOperation.add(getConnettoriPrioritari);
		listOperation.add(updateConnettoriPrioritariDefault);
		listOperation.add(updateConnettoriPrioritari);
		listOperation.add(resetConnettoriPrioritariDefault);
		listOperation.add(resetConnettoriPrioritari);
		MBeanOperationInfo[] operations = listOperation.toArray(new MBeanOperationInfo[1]);
		
		return new MBeanInfo(className,description,attributes,constructors,operations,null);
	}
	
	/* Variabili per la gestione JMX */
	private Logger log;
	org.openspcoop2.pdd.config.OpenSPCoop2Properties openspcoopProperties = null;
	
	/* Costruttore */
	public GestoreConsegnaApplicativi(){
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		this.openspcoopProperties = org.openspcoop2.pdd.config.OpenSPCoop2Properties.getInstance();
				
		// Configurazione
		try{
			this.cacheAbilitata = GestoreLoadBalancerCaching.isCacheAbilitata();
		}catch(Exception e){
			this.log.error("Errore durante la comprensione dello stato della cache");
		}
		
		this.maxLifePresaInConsegna = this.openspcoopProperties.getTimerConsegnaContenutiApplicativi_presaInConsegnaMaxLife();

	}
	
	public boolean isCacheAbilitata() {
		return this.cacheAbilitata;
	}
	
	/* Metodi di management JMX */
	public String resetCache(){
		try{
			if(this.cacheAbilitata==false)
				throw new Exception("Cache non abilitata");
			GestoreLoadBalancerCaching.resetCache();
			return JMXUtils.MSG_RESET_CACHE_EFFETTUATO_SUCCESSO;
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String printStatCache(){
		try{
			if(this.cacheAbilitata==false)
				throw new Exception("Cache non abilitata");
			return GestoreLoadBalancerCaching.printStatsCache("\n");
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public void abilitaCache(){
		try{
			GestoreLoadBalancerCaching.abilitaCache();
			this.cacheAbilitata = true;
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
		}
	}

	public String abilitaCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond){
		try{
			GestoreLoadBalancerCaching.abilitaCache(dimensioneCache,algoritmoCacheLRU,itemIdleTime,itemLifeSecond, OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
			this.cacheAbilitata = true;
			return JMXUtils.MSG_ABILITAZIONE_CACHE_EFFETTUATA;
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public void disabilitaCache() throws JMException{
		try{
			GestoreLoadBalancerCaching.disabilitaCache();
			this.cacheAbilitata = false;
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			throw new JMException(e.getMessage());
		}
	}
	public String disabilitaCacheConEsito() {
		try{
			disabilitaCache();
			return JMXUtils.MSG_DISABILITAZIONE_CACHE_EFFETTUATA;
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String listKeysCache(){
		try{
			if(this.cacheAbilitata==false)
				throw new Exception("Cache non abilitata");
			return GestoreLoadBalancerCaching.listKeysCache("\n");
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getObjectCache(String key){
		try{
			if(this.cacheAbilitata==false)
				throw new Exception("Cache non abilitata");
			return GestoreLoadBalancerCaching.getObjectCache(key);
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String removeObjectCache(String key){
		try{
			if(this.cacheAbilitata==false)
				throw new Exception("Cache non abilitata");
			GestoreLoadBalancerCaching.removeObjectCache(key);
			return JMXUtils.MSG_RIMOZIONE_CACHE_EFFETTUATA;
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getThreadPoolStatus(String queue) {
		try{
			if(OpenSPCoop2Startup.threadConsegnaContenutiApplicativiRefMap==null ||
					!OpenSPCoop2Startup.threadConsegnaContenutiApplicativiRefMap.containsKey(queue)) {
				throw new Exception("Coda '"+queue+"' non esistente");
			}
			return OpenSPCoop2Startup.threadConsegnaContenutiApplicativiRefMap.get(queue).getThreadsImage();
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getQueueConfig(String queue) {
		try{
			if(OpenSPCoop2Startup.threadConsegnaContenutiApplicativiRefMap==null ||
					!OpenSPCoop2Startup.threadConsegnaContenutiApplicativiRefMap.containsKey(queue)) {
				throw new Exception("Coda '"+queue+"' non esistente");
			}
			return OpenSPCoop2Startup.threadConsegnaContenutiApplicativiRefMap.get(queue).getQueueConfig();
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getApplicativiPrioritari(String queue) {
		try{
			if(OpenSPCoop2Startup.threadConsegnaContenutiApplicativiRefMap==null ||
					!OpenSPCoop2Startup.threadConsegnaContenutiApplicativiRefMap.containsKey(queue)) {
				throw new Exception("Coda '"+queue+"' non esistente");
			}
			List<IDConnettore> list = ConfigurazionePdDManager.getInstance().getConnettoriConsegnaNotifichePrioritarie(queue);
			if(list==null || list.isEmpty()) {
				return "";
			}
			else {
				StringBuilder sb = new StringBuilder();
				for (IDConnettore idConnettore : list) {
					if(sb.length()>0) {
						sb.append(", ");
					}
					sb.append(idConnettore.getNome()).append("@").append(idConnettore.getIdSoggettoProprietario().toString());
				}
				return sb.toString();
			}
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getConnettoriPrioritari(String queue) {
		try{
			if(OpenSPCoop2Startup.threadConsegnaContenutiApplicativiRefMap==null ||
					!OpenSPCoop2Startup.threadConsegnaContenutiApplicativiRefMap.containsKey(queue)) {
				throw new Exception("Coda '"+queue+"' non esistente");
			}
			List<IDConnettore> list = ConfigurazionePdDManager.getInstance().getConnettoriConsegnaNotifichePrioritarie(queue);
			if(list==null || list.isEmpty()) {
				return "";
			}
			else {
				StringBuilder sb = new StringBuilder();
				for (IDConnettore idConnettore : list) {
					if(sb.length()>0) {
						sb.append(", ");
					}
					sb.append(idConnettore.getNomeConnettore());
				}
				return sb.toString();
			}
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String updateConnettoriPrioritari(String queue) {
		try{
			if(OpenSPCoop2Startup.threadConsegnaContenutiApplicativiRefMap==null ||
					!OpenSPCoop2Startup.threadConsegnaContenutiApplicativiRefMap.containsKey(queue)) {
				throw new Exception("Coda '"+queue+"' non esistente");
			}
			
			ConfigurazionePdDReader.removeObjectCache(ConfigurazionePdD.getKey_getConnettoriConsegnaNotifichePrioritarie(queue));
			
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}

	public String resetConnettoriPrioritari(String queue) {
		try{
			if(OpenSPCoop2Startup.threadConsegnaContenutiApplicativiRefMap==null ||
					!OpenSPCoop2Startup.threadConsegnaContenutiApplicativiRefMap.containsKey(queue)) {
				throw new Exception("Coda '"+queue+"' non esistente");
			}
			
			ConfigurazionePdDManager.getInstance().resetConnettoriConsegnaNotifichePrioritarie(queue);
			
			ConfigurazionePdDReader.removeObjectCache(ConfigurazionePdD.getKey_getConnettoriConsegnaNotifichePrioritarie(queue));
			
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
}
