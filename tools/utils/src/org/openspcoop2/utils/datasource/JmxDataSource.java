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


package org.openspcoop2.utils.datasource;

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

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;




/**
 * Implementazione JMX per la gestione delle risorse
 *   
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JmxDataSource extends NotificationBroadcasterSupport implements DynamicMBean {

	/** Nomi attributi */
	public final static String NUMERO_DATASOURCE = "datasources";
	
	/** Nomi metodi */
	public final static String CONNESSIONI_ALLOCATE = "getUsedConnections";
	public final static String DATASOURCE_CREATI = "getDatasources";
	
	public final static String MSG_OPERAZIONE_NON_EFFETTUATA = "Operazione non riuscita: ";
	
	/** getAttribute */
	@Override
	public Object getAttribute(String attributeName) throws AttributeNotFoundException,MBeanException,ReflectionException{
		
		if( (attributeName==null) || (attributeName.equals("")) )
			throw new IllegalArgumentException("Il nome dell'attributo e' nullo o vuoto");
		
		if(attributeName.equals(JmxDataSource.NUMERO_DATASOURCE))
			return DataSourceFactory.sizeDatasources();
		
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
			
			if(attribute.getName().equals(JmxDataSource.NUMERO_DATASOURCE)){
				// READ ONLY
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
		
		if(actionName.equals(JmxDataSource.CONNESSIONI_ALLOCATE)){
			
			if(params.length != 1)
				throw new MBeanException(new Exception("["+JmxDataSource.CONNESSIONI_ALLOCATE+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
				if(param1==null){
					throw new MBeanException(new Exception("Identificativo del datasource non fornito"));
				}
			}
			
			return this.getUsedDBConnections(param1);
		}
		
		else if(actionName.equals(JmxDataSource.DATASOURCE_CREATI)){
		
			return this.getDatasource();
			
		}
		
		throw new UnsupportedOperationException("Operazione "+actionName+" sconosciuta");
	}
	
	/* MBean info */
	@Override
	public MBeanInfo getMBeanInfo(){
		
		try{
		
			/*
			if(OpenSPCoopStartup.initialize){
				this.refresh();
			}
			*/
			
			// Per determinare se l'attributo e' leggibile/scrivibile
			final boolean READABLE = true;
			final boolean WRITABLE = true;
			
			// Per determinare se l'attributo e' ricavabile nella forma booleana isAttribute()
			final boolean IS_GETTER = true;
			
			// Descrizione della classe nel MBean
			String className = this.getClass().getName();
			String description = "Datasource allocati con la libreria org.openspcoop2.utils.datasource, premi pulsante (apply changes) per aggiornare i dati";
	
			// MetaData per l'attributo numMsgInConsegna
			MBeanAttributeInfo numDatasourceVAR 
				= new MBeanAttributeInfo(JmxDataSource.NUMERO_DATASOURCE,long.class.getName(),
							"Numero di Datasource allocati",
								READABLE,!WRITABLE,!IS_GETTER);
			
			// MetaData per l'operazione 
			MBeanOperationInfo getConnessioneAllocateOP 
				= new MBeanOperationInfo(JmxDataSource.CONNESSIONI_ALLOCATE,"Moduli funzionali che dispongono di una connessione verso il datasource fornito come parametro",
						new MBeanParameterInfo[]{
								new MBeanParameterInfo("idDatasource",String.class.getName(),"Identificativo del datasource")
							},
						String.class.getName(),
						MBeanOperationInfo.ACTION);
			
			// MetaData per l'operazione 
			MBeanOperationInfo getDatasourceAllocatiOP
				= new MBeanOperationInfo(JmxDataSource.DATASOURCE_CREATI,"Datasource allocati",
						null,
						String.class.getName(),
						MBeanOperationInfo.ACTION);
			
			// Mbean costruttore
			MBeanConstructorInfo defaultConstructor = new MBeanConstructorInfo("Default Constructor","Crea e inizializza una nuova istanza del MBean",null);
	
			// Lista attributi
			MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[]{numDatasourceVAR};
			
			// Lista Costruttori
			MBeanConstructorInfo[] constructors = new MBeanConstructorInfo[]{defaultConstructor};
			
			// Lista operazioni
			MBeanOperationInfo[] operations = new MBeanOperationInfo[]{getDatasourceAllocatiOP, getConnessioneAllocateOP};
			
			return new MBeanInfo(className,description,attributes,constructors,operations,null);
			
		}catch(Exception e){
			//System.out.println("ERRORE: "+e.getMessage());
			//e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	
	/* Variabili per la gestione JMX */
	private Logger log = LoggerWrapperFactory.getLogger(JmxDataSource.class);

	/* Costruttore */
	public JmxDataSource(){
		
	}
	
	
	/* Metodi di management JMX */
	

	public String getUsedDBConnections(String uuidDataSource){
		String[] risorse = null;
		try{
			risorse = DataSourceFactory.getInstance(uuidDataSource).getJmxStatus();
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
		if(risorse==null || risorse.length<=0)
			return "Nessuna connessione allocata";
		
		StringBuffer bf = new StringBuffer();
		bf.append(risorse.length+" connessioni allocate: \n");
		for(int i=0; i<risorse.length; i++){
			bf.append(risorse[i]+"\n");
		}
		return bf.toString();
	}
	
	public String getDatasource(){
		String[] risorse = null;
		try{
			risorse = DataSourceFactory.getJmxStatus();
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
		if(risorse==null || risorse.length<=0)
			return "Nessun datasource allocato";
		
		StringBuffer bf = new StringBuffer();
		bf.append(risorse.length+" datasource allocati: \n");
		for(int i=0; i<risorse.length; i++){
			bf.append(risorse[i]+"\n");
		}
		return bf.toString();
	}
	
}
