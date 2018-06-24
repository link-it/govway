/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.util.Iterator;

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
import javax.management.NotificationBroadcasterSupport;
import javax.management.ReflectionException;

import org.slf4j.Logger;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.autenticazione.GestoreAutenticazione;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;


/**
 * Implementazione JMX per la gestione dell'autenticazione
 *   
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EngineAutenticazione extends NotificationBroadcasterSupport implements DynamicMBean {

	/** Attributi */
	private boolean cacheAbilitata = false;
	
	/** getAttribute */
	@Override
	public Object getAttribute(String attributeName) throws AttributeNotFoundException,MBeanException,ReflectionException{
		
		if( (attributeName==null) || (attributeName.equals("")) )
			throw new IllegalArgumentException("Il nome dell'attributo e' nullo o vuoto");
		
		if(attributeName.equals(JMXUtils.CACHE_ATTRIBUTE_ABILITATA))
			return this.cacheAbilitata;
		
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
			
			else
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
				throw new MBeanException(new Exception("[AbilitaCache] Lunghezza parametri non corretta: "+params.length));
			
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
		
		throw new UnsupportedOperationException("Operazione "+actionName+" sconosciuta");
	}
	
	/* MBean info */
	@Override
	public MBeanInfo getMBeanInfo(){
		
		// Descrizione della classe nel MBean
		String className = this.getClass().getName();
		String description = "Configurazione dei dati di autenticazione ai servizi esposti ("+OpenSPCoop2Properties.getInstance().getVersione()+")";
		
		// MetaData per l'attributo abilitaCache
		MBeanAttributeInfo cacheAbilitataVAR = JMXUtils.MBEAN_ATTRIBUTE_INFO_CACHE_ABILITATA;
		
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
		
		// Mbean costruttore
		MBeanConstructorInfo defaultConstructor = new MBeanConstructorInfo("Default Constructor","Crea e inizializza una nuova istanza del MBean",null);

		// Lista attributi
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[]{cacheAbilitataVAR};
		
		// Lista Costruttori
		MBeanConstructorInfo[] constructors = new MBeanConstructorInfo[]{defaultConstructor};
		
		// Lista operazioni
		MBeanOperationInfo[] operations = new MBeanOperationInfo[]{resetCacheOP,printStatCacheOP,disabilitaCacheOP,abilitaCacheParametriOP,listKeysCacheOP,getObjectCacheOP,removeObjectCacheOP};
		
		return new MBeanInfo(className,description,attributes,constructors,operations,null);
	}
	
	/* Variabili per la gestione JMX */
	private Logger log;
	
	/* Costruttore */
	public EngineAutenticazione(){
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
				
		// Configurazione
		this.cacheAbilitata = GestoreAutenticazione.isCacheAbilitata();
			
	}
	
	public boolean isCacheAbilitata() {
		return this.cacheAbilitata;
	}
	
	/* Metodi di management JMX */
	public String resetCache(){
		try{
			if(this.cacheAbilitata==false)
				throw new Exception("Cache non abilitata");
			GestoreAutenticazione.resetCache();
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
			return GestoreAutenticazione.printStatsCache("\n");
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String abilitaCache(){
		try{
			GestoreAutenticazione.abilitaCache();
			this.cacheAbilitata = true;
			return JMXUtils.MSG_ABILITAZIONE_CACHE_EFFETTUATA;
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}

	public String abilitaCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond){
		try{
			GestoreAutenticazione.abilitaCache(dimensioneCache,algoritmoCacheLRU,itemIdleTime,itemLifeSecond);
			this.cacheAbilitata = true;
			return JMXUtils.MSG_ABILITAZIONE_CACHE_EFFETTUATA;
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public void disabilitaCache() throws JMException{
		try{
			GestoreAutenticazione.disabilitaCache();
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
			return GestoreAutenticazione.listKeysCache("\n");
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getObjectCache(String key){
		try{
			if(this.cacheAbilitata==false)
				throw new Exception("Cache non abilitata");
			return GestoreAutenticazione.getObjectCache(key);
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String removeObjectCache(String key){
		try{
			if(this.cacheAbilitata==false)
				throw new Exception("Cache non abilitata");
			GestoreAutenticazione.removeObjectCache(key);
			return JMXUtils.MSG_RIMOZIONE_CACHE_EFFETTUATA;
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}

}
