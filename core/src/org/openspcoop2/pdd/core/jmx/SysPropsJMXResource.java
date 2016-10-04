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
import javax.management.MBeanParameterInfo;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ReflectionException;

import org.slf4j.Logger;
import org.openspcoop2.pdd.config.SystemPropertiesManager;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;


/**
 * Implementazione JMX per la gestione delle proprietà di sistema
 *   
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SysPropsJMXResource extends NotificationBroadcasterSupport implements DynamicMBean {

	/** Nomi proprietà */
	public final static String OPENSPCOOP2_SYSTEM_PROPERTIES = "OpenSPCoop2Properties";
	
	/** Nomi metodi */
	public final static String REFRESH_PERSISTENT_SYSTEM_PROPERTIES = "refreshPersistentConfiguration"; 
	public final static String READ_ALL_SYSTEM_PROPERTIES = "readAllProperties"; 
	public final static String READ_OPENSPCOOP2_SYSTEM_PROPERTIES = "readOpenSPCoop2Properties"; 
	public final static String GET_SYSTEM_PROPERTY_VALUE = "getPropertyValue"; 
	public final static String REMOVE_SYSTEM_PROPERTY = "removeProperty";
	public final static String UPDATE_SYSTEM_PROPERTY = "updateProperty";
	public final static String INSERT_SYSTEM_PROPERTY = "insertProperty";
	
	public final static String EFFETTUATO_SUCCESSO = "Operazione effettuata con successo";
	public final static String EFFETTUATO_SUCCESSO_INFO = "Operazione effettuata con successo (Nota la Modifica non è persistente, inoltre in caso di invocazione del pulsante 'refreshPersistentConfiguration' questa impostazione non viene mantenuta)";

		
	/** getAttribute */
	@Override
	public Object getAttribute(String attributeName) throws AttributeNotFoundException,MBeanException,ReflectionException{
		
		if( (attributeName==null) || (attributeName.equals("")) )
			throw new IllegalArgumentException("Il nome dell'attributo e' nullo o vuoto");
		
		if(attributeName.equals(SysPropsJMXResource.OPENSPCOOP2_SYSTEM_PROPERTIES)){
			String v = this.spm.getPropertyValue(SystemPropertiesManager.SYSTEM_PROPERTIES);
			if(v==null || v.length()<=0){
				return "";
			}
			else{
				return v;
			}
		}
				
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
			
			if(attribute.getName().equals(SysPropsJMXResource.OPENSPCOOP2_SYSTEM_PROPERTIES)){
				// nop;
			
			}else
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
		
		if(actionName.equals(SysPropsJMXResource.REFRESH_PERSISTENT_SYSTEM_PROPERTIES)){
			return this.refreshPersistentConfigSystemProperty();
		}
		
		if(actionName.equals(SysPropsJMXResource.READ_ALL_SYSTEM_PROPERTIES)){
			return this.readAllSystemProperties();
		}
		
		if(actionName.equals(SysPropsJMXResource.READ_OPENSPCOOP2_SYSTEM_PROPERTIES)){
			return this.readOpenSPCoop2SystemProperties();
		}
		
		if(actionName.equals(SysPropsJMXResource.GET_SYSTEM_PROPERTY_VALUE)){
			
			if(params.length != 1)
				throw new MBeanException(new Exception("["+SysPropsJMXResource.GET_SYSTEM_PROPERTY_VALUE+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			
			return this.getSystemPropertyValue(param1);
		}
		
		if(actionName.equals(SysPropsJMXResource.REMOVE_SYSTEM_PROPERTY)){
			
			if(params.length != 1)
				throw new MBeanException(new Exception("["+SysPropsJMXResource.REMOVE_SYSTEM_PROPERTY+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			
			return this.removeSystemProperty(param1);
		}
		
		if(actionName.equals(SysPropsJMXResource.UPDATE_SYSTEM_PROPERTY)){
			
			if(params.length != 2)
				throw new MBeanException(new Exception("["+SysPropsJMXResource.UPDATE_SYSTEM_PROPERTY+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			
			String param2 = null;
			if(params[1]!=null && !"".equals(params[1])){
				param2 = (String)params[1];
			}
			
			return this.updateSystemProperty(param1,param2);
		}
		
		if(actionName.equals(SysPropsJMXResource.INSERT_SYSTEM_PROPERTY)){
			
			if(params.length != 2)
				throw new MBeanException(new Exception("["+SysPropsJMXResource.INSERT_SYSTEM_PROPERTY+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			
			String param2 = null;
			if(params[1]!=null && !"".equals(params[1])){
				param2 = (String)params[1];
			}
			
			return this.insertSystemProperty(param1,param2);
		}
		
		throw new UnsupportedOperationException("Operazione "+actionName+" sconosciuta");
	}
	
	/* MBean info */
	@Override
	public MBeanInfo getMBeanInfo(){
		
		// Descrizione della classe nel MBean
		String className = this.getClass().getName();
		String description = "Risorsa per la gestione delle proprietà di sistema utilizzate dalla Porta di Dominio "+this.openspcoopProperties.getVersione();

		// MetaData per l'attributo propertiesVAR
		MBeanAttributeInfo propertiesVAR 
			= new MBeanAttributeInfo(SysPropsJMXResource.OPENSPCOOP2_SYSTEM_PROPERTIES,String.class.getName(),
						"proprietà di sistema impostate tramite la configurazione della Porta di Dominio",
							JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);

		// MetaData per l'operazione refreshPersistentConfigurazioneOP
		MBeanOperationInfo refreshPersistentConfigurazioneOP 
			= new MBeanOperationInfo(SysPropsJMXResource.REFRESH_PERSISTENT_SYSTEM_PROPERTIES,"Reimposta le proprietà di sistema indicate nella configurazione della Porta di Dominio",
					null,
					//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione readAllPropertiesOP
		MBeanOperationInfo readAllPropertiesOP 
			= new MBeanOperationInfo(SysPropsJMXResource.READ_ALL_SYSTEM_PROPERTIES,"Visualizza tutte le proprietà di sistema",
					null,
					//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione readOpenSPCoop2PropertiesOP
		MBeanOperationInfo readOpenSPCoop2PropertiesOP 
			= new MBeanOperationInfo(SysPropsJMXResource.READ_OPENSPCOOP2_SYSTEM_PROPERTIES,"Visualizza le proprietà di sistema impostate tramite la configurazione della Porta di Dominio",
					null,
					//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
					String.class.getName(),
					MBeanOperationInfo.ACTION);
				
		// MetaData per l'operazione getPropertyValueOP con parametri
		MBeanOperationInfo getPropertyValueOP 
			= new MBeanOperationInfo(SysPropsJMXResource.GET_SYSTEM_PROPERTY_VALUE,"Ritorna il valore di una proprietà di sistema",
					new MBeanParameterInfo[]{
						new MBeanParameterInfo("nome",String.class.getName(),"Nome della proprietà"),
					},
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione removePropertyValueOP con parametri
		MBeanOperationInfo removePropertyValueOP 
			= new MBeanOperationInfo(SysPropsJMXResource.REMOVE_SYSTEM_PROPERTY,"Elimina una proprietà di sistema",
					new MBeanParameterInfo[]{
						new MBeanParameterInfo("nome",String.class.getName(),"Nome della proprietà"),
					},
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione updatePropertyValueOP con parametri
		MBeanOperationInfo updatePropertyValueOP 
			= new MBeanOperationInfo(SysPropsJMXResource.UPDATE_SYSTEM_PROPERTY,"Aggiorna una proprietà di sistema",
					new MBeanParameterInfo[]{
						new MBeanParameterInfo("nome",String.class.getName(),"Nome della proprietà"),
						new MBeanParameterInfo("valore",String.class.getName(),"Valore della proprietà"),
					},
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione insertPropertyValueOP con parametri
		MBeanOperationInfo insertPropertyValueOP 
			= new MBeanOperationInfo(SysPropsJMXResource.INSERT_SYSTEM_PROPERTY,"Crea una nuova proprietà di sistema",
					new MBeanParameterInfo[]{
						new MBeanParameterInfo("nome",String.class.getName(),"Nome della proprietà"),
						new MBeanParameterInfo("valore",String.class.getName(),"Valore della proprietà"),
					},
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// Mbean costruttore
		MBeanConstructorInfo defaultConstructor = new MBeanConstructorInfo("Default Constructor","Crea e inizializza una nuova istanza del MBean",null);

		// Lista attributi
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[]{propertiesVAR};
		
		// Lista Costruttori
		MBeanConstructorInfo[] constructors = new MBeanConstructorInfo[]{defaultConstructor};
		
		// Lista operazioni
		MBeanOperationInfo[] operations = new MBeanOperationInfo[]{refreshPersistentConfigurazioneOP,
				readAllPropertiesOP,readOpenSPCoop2PropertiesOP,getPropertyValueOP,removePropertyValueOP,
				updatePropertyValueOP,insertPropertyValueOP};
		
		return new MBeanInfo(className,description,attributes,constructors,operations,null);
	}
	
	/* Variabili per la gestione JMX */
	private Logger log;
	org.openspcoop2.pdd.config.ConfigurazionePdDManager configReader = null;
	org.openspcoop2.pdd.config.OpenSPCoop2Properties openspcoopProperties = null;
	private SystemPropertiesManager spm = null;
	
	/* Costruttore */
	public SysPropsJMXResource(){
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		this.configReader = org.openspcoop2.pdd.config.ConfigurazionePdDManager.getInstance();
		this.openspcoopProperties = org.openspcoop2.pdd.config.OpenSPCoop2Properties.getInstance();
		this.spm = new SystemPropertiesManager(this.configReader, this.log);
		
	}
	
	/* Metodi di management JMX */
	
	public String readAllSystemProperties(){
		try{
			String p = this.spm.readAllSystemProperties("\n");
			if(p==null || p.length()<=0){
				return "proprietà non presenti";
			}
			else{
				return p;
			}
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String readOpenSPCoop2SystemProperties(){
		try{
			String p = this.spm.readOpenSPCoop2SystemProperties("\n");
			if(p==null || p.length()<=0){
				return "proprietà impostate tramite OpenSPCoop2 non trovate";
			}
			else{
				return p;
			}
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getSystemPropertyValue(String key){
		try{
			String v = this.spm.getPropertyValue(key);
			if(v==null || v.length()<=0){
				return "proprietà ["+key+"] non presente";
			}
			else{
				return "["+key+"]=["+v+"]";
			}
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String removeSystemProperty(String key){
		try{
			this.spm.removeProperty(key);
			return SysPropsJMXResource.EFFETTUATO_SUCCESSO_INFO;
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String updateSystemProperty(String key,String value){
		try{
			this.spm.updateProperty(key,value);
			return SysPropsJMXResource.EFFETTUATO_SUCCESSO_INFO;
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String insertSystemProperty(String key,String value){
		try{
			this.spm.insertProperty(key,value);
			return SysPropsJMXResource.EFFETTUATO_SUCCESSO_INFO;
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String refreshPersistentConfigSystemProperty(){
		try{
			this.spm.updateSystemProperties();
			return SysPropsJMXResource.EFFETTUATO_SUCCESSO;
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
}
