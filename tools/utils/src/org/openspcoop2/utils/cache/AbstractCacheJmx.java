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


package org.openspcoop2.utils.cache;

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


/**
 * CacheJmx
 *   
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractCacheJmx extends NotificationBroadcasterSupport implements DynamicMBean {

	public abstract AbstractCacheWrapper getCacheWrapper();
	
	public abstract String getJmxDescription();
	
	
	
	/** Nomi proprieta' */
	public final static String CACHE_NAME = "cacheName";
	
	/** getAttribute */
	@Override
	public Object getAttribute(String attributeName) throws AttributeNotFoundException,MBeanException,ReflectionException{
		
		if( (attributeName==null) || (attributeName.equals("")) )
			throw new IllegalArgumentException("Il nome dell'attributo e' nullo o vuoto");
		
		if(attributeName.equals(Constants.CACHE_ATTRIBUTE_ABILITATA))
			return this.getCacheWrapper().isCacheEnabled();
		
		if(attributeName.equals(AbstractCacheJmx.CACHE_NAME))
			return this.getCacheWrapper().getCacheName();
		
		throw new AttributeNotFoundException("Attribute "+attributeName+" not found");
	}
	
	/** getAttributes */
	@Override
	public AttributeList getAttributes(String [] attributesNames){
		
		if(attributesNames==null)
			throw new IllegalArgumentException("Attributes Names undefined");
		
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
			throw new IllegalArgumentException("Attribute name undefined");
		
		try{
			
			if(attribute.getName().equals(Constants.CACHE_ATTRIBUTE_ABILITATA)){
				boolean v = (Boolean) attribute.getValue();
				if(v){
					// la cache DEVE essere abilitata
					if(!this.getCacheWrapper().isCacheEnabled()){
						this.abilitaCache();
					}
				}
				else{
					// la cache DEVE essere disabilitata
					if(this.getCacheWrapper().isCacheEnabled()){
						this.disabilitaCache();
					}
				}
			}
						
			else
				throw new AttributeNotFoundException("Attributo "+attribute.getName()+" non trovato");
			
		}catch(ClassCastException ce){
			throw new InvalidAttributeValueException("Type "+attribute.getValue().getClass()+" for attribute "+attribute.getName()+" is not valid");
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
			throw new IllegalArgumentException("Operation undefined");
		
		if(actionName.equals(Constants.CACHE_METHOD_NAME_RESET)){
			return this.resetCache();
		}
		
		if(actionName.equals(Constants.CACHE_METHOD_NAME_PRINT_STATS)){
			return this.printStatCache();
		}
				
		if(actionName.equals(Constants.CACHE_METHOD_NAME_DISABILITA)){
			return this.disabilitaCacheConEsito();
		}
				
		if(actionName.equals(Constants.CACHE_METHOD_NAME_ABILITA)){
			if(params.length != 4)
				throw new MBeanException(new Exception("["+Constants.CACHE_METHOD_NAME_ABILITA+"] Parameter size uncorrect: "+params.length));
			
			Integer param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (Integer)params[0];
				if(param1<0){
					param1 = null;
				}
			}
			
			Boolean param2 = null;
			if(params[1]!=null && !"".equals(params[1])){
				param2 = (Boolean)params[1];
			}
			
			Integer param3 = null;
			if(params[2]!=null && !"".equals(params[2])){
				param3 = (Integer)params[2];
				if(param3<0){
					param3 = null;
				}
			}
			
			Integer param4 = null;
			if(params[3]!=null && !"".equals(params[3])){
				param4 = (Integer)params[3];
				if(param4<0){
					param4 = null;
				}
			}
					
			return this.abilitaCache(param1, param2, param3, param4 );
		}
		
		if(actionName.equals(Constants.CACHE_METHOD_NAME_LIST_KEYS)){
			return this.listKeysCache();
		}
		
		if(actionName.equals(Constants.CACHE_METHOD_NAME_GET_OBJECT)){
			
			if(params.length != 1)
				throw new MBeanException(new Exception("["+Constants.CACHE_METHOD_NAME_GET_OBJECT+"] Parameter size uncorrect: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			
			return this.getObjectCache(param1);
		}
		
		if(actionName.equals(Constants.CACHE_METHOD_NAME_REMOVE_OBJECT)){
			
			if(params.length != 1)
				throw new MBeanException(new Exception("["+Constants.CACHE_METHOD_NAME_REMOVE_OBJECT+"] Parameter size uncorrect: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			
			return this.removeObjectCache(param1);
		}
		
		throw new UnsupportedOperationException("Operation "+actionName+" unknown");
	}
	
	/* MBean info */
	@Override
	public MBeanInfo getMBeanInfo(){
				
		// Descrizione della classe nel MBean
		String className = this.getClass().getName();
		String description = this.getJmxDescription();
		
		// MetaData per l'attributo abilitaCache
		MBeanAttributeInfo cacheAbilitataVAR = Constants.MBEAN_ATTRIBUTE_INFO_CACHE_ABILITATA;
		
		// MetaData per l'attributo registriServizi
		MBeanAttributeInfo registriServiziVAR 
			= new MBeanAttributeInfo(AbstractCacheJmx.CACHE_NAME,String[].class.getName(),
						"Elenco dei registri dei servizi utilizzati a RunTime dalla Porta di Dominio",
							Constants.JMX_ATTRIBUTE_READABLE,!Constants.JMX_ATTRIBUTE_WRITABLE,!Constants.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'operazione resetCache
		MBeanOperationInfo resetCacheOP = Constants.MBEAN_OPERATION_RESET_CACHE;
		
		// MetaData per l'operazione printStatCache
		MBeanOperationInfo printStatCacheOP = Constants.MBEAN_OPERATION_PRINT_STATS_CACHE;
		
		// MetaData per l'operazione disabilitaCache
		MBeanOperationInfo disabilitaCacheOP = Constants.MBEAN_OPERATION_DISABILITA_CACHE;
		
		// MetaData per l'operazione abilitaCache con parametri
		MBeanOperationInfo abilitaCacheParametriOP = Constants.MBEAN_OPERATION_ABILITA_CACHE_CON_PARAMETRI;
		
		// MetaData per l'operazione listKeysCache
		MBeanOperationInfo listKeysCacheOP = Constants.MBEAN_OPERATION_LIST_KEYS_CACHE; 

		// MetaData per l'operazione getObjectCache
		MBeanOperationInfo getObjectCacheOP = Constants.MBEAN_OPERATION_GET_OBJECT_CACHE;
		
		// MetaData per l'operazione removeObjectCache
		MBeanOperationInfo removeObjectCacheOP = Constants.MBEAN_OPERATION_REMOVE_OBJECT_CACHE;
		
		// Mbean costruttore
		MBeanConstructorInfo defaultConstructor = new MBeanConstructorInfo("Default Constructor","New Instance of MBean",null);

		// Lista attributi
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[]{cacheAbilitataVAR,registriServiziVAR};
		
		// Lista Costruttori
		MBeanConstructorInfo[] constructors = new MBeanConstructorInfo[]{defaultConstructor};
		
		// Lista operazioni
		MBeanOperationInfo[] operations = new MBeanOperationInfo[]{resetCacheOP,printStatCacheOP,disabilitaCacheOP,abilitaCacheParametriOP,listKeysCacheOP,getObjectCacheOP,removeObjectCacheOP};
		
		return new MBeanInfo(className,description,attributes,constructors,operations,null);
	}
	
	/* Variabili per la gestione JMX */

	/* Costruttore */
	public AbstractCacheJmx(){
	}
	
	
	/* Metodi di management JMX */
	public String resetCache(){
		try{
			if(this.getCacheWrapper().isCacheEnabled()==false)
				throw new Exception("Cache disabled");
			this.getCacheWrapper().resetCache();
			return Constants.MSG_RESET_CACHE_EFFETTUATO_SUCCESSO;
		}catch(Throwable e){
			this.getCacheWrapper().getLog().error(Constants.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return Constants.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String printStatCache(){
		try{
			if(this.getCacheWrapper().isCacheEnabled()==false)
				throw new Exception("Cache non abilitata");
			return this.getCacheWrapper().printStatsCache("\n");
		}catch(Throwable e){
			this.getCacheWrapper().getLog().error(Constants.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return Constants.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public void abilitaCache() throws JMException{
		try{
			this.getCacheWrapper().enableCache();
		}catch(Throwable e){
			this.getCacheWrapper().getLog().error(e.getMessage(),e);
		}
	}
	
	public String abilitaCache(Integer dimensioneCache,Boolean algoritmoCacheLRU,Integer itemIdleTime,Integer itemLifeSecond){
		try{
			this.getCacheWrapper().enableCache(dimensioneCache,algoritmoCacheLRU,itemIdleTime,itemLifeSecond);
			return Constants.MSG_ABILITAZIONE_CACHE_EFFETTUATA;
		}catch(Throwable e){
			this.getCacheWrapper().getLog().error(e.getMessage(),e);
			return Constants.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
		
	
	public void disabilitaCache() throws JMException{
		try{
			this.getCacheWrapper().disableCache();
		}catch(Throwable e){
			this.getCacheWrapper().getLog().error(e.getMessage(),e);
			throw new JMException(e.getMessage());
		}
	}
	public String disabilitaCacheConEsito() {
		try{
			disabilitaCache();
			return Constants.MSG_DISABILITAZIONE_CACHE_EFFETTUATA;
		}catch(Throwable e){
			this.getCacheWrapper().getLog().error(e.getMessage(),e);
			return Constants.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String listKeysCache(){
		try{
			if(this.getCacheWrapper().isCacheEnabled()==false)
				throw new Exception("Cache disabled");
			return this.getCacheWrapper().listKeysCache("\n");
		}catch(Throwable e){
			this.getCacheWrapper().getLog().error(Constants.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return Constants.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getObjectCache(String key){
		try{
			if(this.getCacheWrapper().isCacheEnabled()==false)
				throw new Exception("Cache disabled");
			return this.getCacheWrapper().getObjectCache(key);
		}catch(Throwable e){
			this.getCacheWrapper().getLog().error(Constants.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return Constants.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String removeObjectCache(String key){
		try{
			if(this.getCacheWrapper().isCacheEnabled()==false)
				throw new Exception("Cache disabled");
			return this.getCacheWrapper().removeObjectCache(key);
		}catch(Throwable e){
			this.getCacheWrapper().getLog().error(Constants.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return Constants.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
}
