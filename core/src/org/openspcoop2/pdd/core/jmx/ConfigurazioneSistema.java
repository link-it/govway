/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
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
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageFactory_impl;
import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.resources.MapReader;


/**
 * Implementazione JMX per la gestione della Configurazione di Sistema della Porta
 *   
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneSistema extends NotificationBroadcasterSupport implements DynamicMBean {

	/** Nomi metodi */
	public final static String VERSIONE_PDD = "getVersionePdD";
	public final static String VERSIONE_BASE_DATI = "getVersioneBaseDati";
	public final static String VERSIONE_JAVA = "getVersioneJava";
	public final static String TIPO_DATABASE = "getTipoDatabase";	
	public final static String INFORMAZIONI_DATABASE = "getInformazioniDatabase";
	public final static String MESSAGE_FACTORY = "getMessageFactory";
	public final static String DIRECTORY_CONFIGURAZIONE = "getDirectoryConfigurazione";
	public final static String PROTOCOLS = "getPluginProtocols";




	/** getAttribute */
	@Override
	public Object getAttribute(String attributeName) throws AttributeNotFoundException,MBeanException,ReflectionException{

		if( (attributeName==null) || (attributeName.equals("")) )
			throw new IllegalArgumentException("Il nome dell'attributo e' nullo o vuoto");

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

		if(actionName.equals(VERSIONE_PDD)){
			return this.getVersionePdD();
		}

		else if(actionName.equals(VERSIONE_BASE_DATI)){
			return this.getVersioneBaseDati();
		}

		else if(actionName.equals(VERSIONE_JAVA)){
			return this.getVersioneJava();
		}

		else if(actionName.equals(TIPO_DATABASE)){
			return this.getTipoDatabase();
		}

		else if(actionName.equals(INFORMAZIONI_DATABASE)){
			return this.getInformazioniDatabase();
		}
		
		else if(actionName.equals(MESSAGE_FACTORY)){
			return this.getMessageFactory();
		}
		
		else if(actionName.equals(DIRECTORY_CONFIGURAZIONE)){
			return this.getDirectoryConfigurazione();
		}
		
		else if(actionName.equals(PROTOCOLS)){
			return this.getPluginProtocols();
		}


		throw new UnsupportedOperationException("Operazione "+actionName+" sconosciuta");
	}

	/* MBean info */
	@Override
	public MBeanInfo getMBeanInfo(){

		// Descrizione della classe nel MBean
		String className = this.getClass().getName();
		String description = "Configurazione di Sistema della Porta di Dominio "+this.openspcoopProperties.getVersione();

		// VERSIONE_PDD
		MBeanOperationInfo versionePddOp = new MBeanOperationInfo(VERSIONE_PDD,"Visualizza la versione della Porta di Dominio",
						null,
						//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
						String.class.getName(),
						MBeanOperationInfo.ACTION);
		
		// VERSIONE_BASE_DATI
		MBeanOperationInfo versioneBaseDatiOp = new MBeanOperationInfo(VERSIONE_BASE_DATI,"Visualizza la versione della base dati",
						null,
						//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
						String.class.getName(),
						MBeanOperationInfo.ACTION);
		
		// VERSIONE_JAVA
		MBeanOperationInfo versioneJavaOp = new MBeanOperationInfo(VERSIONE_JAVA,"Visualizza la versione di Java",
						null,
						//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
						String.class.getName(),
						MBeanOperationInfo.ACTION);
		
		// TIPO_DATABASE
		MBeanOperationInfo versioneTipoDatabaseOp = new MBeanOperationInfo(TIPO_DATABASE,"Visualizza il tipo di Database",
						null,
						//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
						String.class.getName(),
						MBeanOperationInfo.ACTION);
		
		// INFORMAZIONI_DATABASE
		MBeanOperationInfo informazioniDatabaseOp = new MBeanOperationInfo(INFORMAZIONI_DATABASE,"Visualizza le informazioni sul Database",
						null,
						//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
						String.class.getName(),
						MBeanOperationInfo.ACTION);

		// MESSAGE_FACTORY
		MBeanOperationInfo messageFactoryOp = new MBeanOperationInfo(MESSAGE_FACTORY,"Visualizza la MessageFactory utilizzata dal prodotto",
						null,
						//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
						String.class.getName(),
						MBeanOperationInfo.ACTION);
		
		// DIRECTORY_CONFIGURAZIONE
		MBeanOperationInfo confDirectoryOp = new MBeanOperationInfo(DIRECTORY_CONFIGURAZIONE,"Visualizza la directory di configurazione",
						null,
						//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
						String.class.getName(),
						MBeanOperationInfo.ACTION);
				
		// PROTOCOLS
		MBeanOperationInfo protocolsOp = new MBeanOperationInfo(PROTOCOLS,"Visualizza i protocolli installati",
						null,
						//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
						String.class.getName(),
						MBeanOperationInfo.ACTION);

		
		// Mbean costruttore
		MBeanConstructorInfo defaultConstructor = new MBeanConstructorInfo("Default Constructor","Crea e inizializza una nuova istanza del MBean",null);

		// Lista attributi
		MBeanAttributeInfo[] attributes = null;

		// Lista Costruttori
		MBeanConstructorInfo[] constructors = new MBeanConstructorInfo[]{defaultConstructor};

		// Lista operazioni
		MBeanOperationInfo[] operations = new MBeanOperationInfo[]{versionePddOp,versioneBaseDatiOp,versioneJavaOp,versioneTipoDatabaseOp,informazioniDatabaseOp,
				messageFactoryOp,confDirectoryOp,protocolsOp};

		return new MBeanInfo(className,description,attributes,constructors,operations,null);
	}

	/* Variabili per la gestione JMX */
	private Logger log;
	org.openspcoop2.pdd.config.OpenSPCoop2Properties openspcoopProperties = null;

	/* Costruttore */
	public ConfigurazioneSistema(){
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		this.openspcoopProperties = org.openspcoop2.pdd.config.OpenSPCoop2Properties.getInstance();
	}

	/* Metodi di management JMX */

	public String getVersionePdD(){
		try{
			String versione = "Porta di Dominio "+CostantiPdD.OPENSPCOOP2_PRODUCT_VERSION;
			if(this.openspcoopProperties!=null){
				versione = "Porta di Dominio "+this.openspcoopProperties.getPddDetailsForServices();
			}
			return versione;
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}

	public String getVersioneBaseDati(){
		try{
			if(DBManager.isInitialized()==false){
				throw new Exception("Inizializzazione DBManager non effettuata");
			}
			DBManager dbManager = DBManager.getInstance();
			Resource resource = null;
			IDSoggetto dominio = this.openspcoopProperties.getIdentitaPortaDefault(null);
			String modulo = this.getClass().getName();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try{
				resource = dbManager.getResource(dominio, modulo, null);
				Connection c = (Connection) resource.getResource();
				String sql = "select * from "+CostantiDB.DB_INFO +" order by id DESC";
				pstmt = c.prepareStatement(sql);
				rs = pstmt.executeQuery();
				StringBuffer bf = new StringBuffer();
				while (rs.next()) {
					int major_version = rs.getInt("major_version");
					int minor_version = rs.getInt("minor_version");
					String details = rs.getString("notes");
					if(bf.length()>0){
						bf.append("\n");
					}
					bf.append("["+major_version+"."+minor_version+"] "+details);
				}

				if(bf.length()<=0){
					throw new Exception("BaseDati non possiede informazioni sul versionamento");
				}else{
					return bf.toString();
				}

			}finally{
				try{
					if(rs!=null)
						rs.close();
				}catch(Exception eClose){}
				try{
					if(pstmt!=null)
						pstmt.close();
				}catch(Exception eClose){}
				try{
					if(dbManager!=null)
						dbManager.releaseResource(dominio, modulo, resource);
				}catch(Exception eClose){}
			}

		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}

	public String getVersioneJava(){
		try{
			String v = System.getProperty("java.version");
			if(v!=null && !"".equals(v)){
				return v;
			}
			throw new Exception("Versione di Java non disponibile");

		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}

	public String getTipoDatabase(){
		try{
			if(this.openspcoopProperties!=null){
				return this.openspcoopProperties.getDatabaseType();
			}
			throw new Exception("Tipo di Database non disponibile");

		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}

	public String getInformazioniDatabase(){
		try{
			if(DBManager.isInitialized()==false){
				throw new Exception("Inizializzazione DBManager non effettuata");
			}
			DBManager dbManager = DBManager.getInstance();
			Resource resource = null;
			IDSoggetto dominio = this.openspcoopProperties.getIdentitaPortaDefault(null);
			String modulo = this.getClass().getName();
			StringBuffer bf = new StringBuffer();

			if(this.openspcoopProperties!=null){
				bf.append("TipoDatabase: "+this.openspcoopProperties.getDatabaseType());
			}
			else{
				throw new Exception("Tipo di Database non disponibile");
			}

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try{
				resource = dbManager.getResource(dominio, modulo, null);
				Connection c = (Connection) resource.getResource();

				DatabaseMetaData dbMetaDati = c.getMetaData();
				if(dbMetaDati!=null){

					if(bf.length()>0){
						bf.append("\n");
					}

					try {
						String productName = dbMetaDati.getDatabaseProductName();
						bf.append("DatabaseProductName: "+productName);
						bf.append("\n");
					} catch (SQLException e) {
					}

					try {
						String productVersion = dbMetaDati.getDatabaseProductVersion();
						bf.append("DatabaseProductVersion: "+productVersion);
						bf.append("\n");
					} catch (SQLException e) {
					}

					try {
						int v = dbMetaDati.getDatabaseMajorVersion();
						bf.append("DatabaseMajorVersion: "+v);
						bf.append("\n");
					} catch (SQLException e) {
					}

					try {
						int v = dbMetaDati.getDatabaseMinorVersion();
						bf.append("DatabaseMinorVersion: "+v);
						bf.append("\n");
					} catch (SQLException e) {
					}

					try {
						String driverName = dbMetaDati.getDriverName();
						bf.append("DriverName: "+driverName);
						bf.append("\n");
					} catch (SQLException e) {
					}

					try {
						String productVersion = dbMetaDati.getDriverVersion();
						bf.append("DriverVersion: "+productVersion);
						bf.append("\n");
					} catch (SQLException e) {
					}

					int v = dbMetaDati.getDriverMajorVersion();
					bf.append("DriverMajorVersion: "+v);
					bf.append("\n");
					
					v = dbMetaDati.getDriverMinorVersion();
					bf.append("DriverMinorVersion: "+v);
					bf.append("\n");

					try {
						v = dbMetaDati.getJDBCMajorVersion();
						bf.append("JDBCMajorVersion: "+v);
						bf.append("\n");
					} catch (SQLException e) {
					}

					try {
						v = dbMetaDati.getJDBCMinorVersion();
						bf.append("JDBCMinorVersion: "+v);
						bf.append("\n");
					} catch (SQLException e) {
					}

					try {
						String username = dbMetaDati.getUserName();
						bf.append("Username: "+username);
						bf.append("\n");
					} catch (SQLException e) {
					}

					try {
						ResultSet catalogs = dbMetaDati.getCatalogs();
						int size = 0;
						while (catalogs.next()) {
							size++;
						}
						
						catalogs = dbMetaDati.getCatalogs();
						int index = 0;
						while (catalogs.next()) {
							if(size==1){
								bf.append("Catalog: " + catalogs.getString(1) );
							}
							else{
								bf.append("Catalogs["+index+"]: " + catalogs.getString(1) );
							}
							bf.append("\n");
							index++;
						}
						catalogs.close();
					} catch (SQLException e) {
					}

				}

				if(bf.length()<=0){
					throw new Exception("Non sono disponibili informazioni sul database");
				}else{
					return bf.toString();
				}

			}finally{
				try{
					if(rs!=null)
						rs.close();
				}catch(Exception eClose){}
				try{
					if(pstmt!=null)
						pstmt.close();
				}catch(Exception eClose){}
				try{
					if(dbManager!=null)
						dbManager.releaseResource(dominio, modulo, resource);
				}catch(Exception eClose){}
			}

		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getMessageFactory(){
		try{
			OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getMessageFactory();
			return "OpenSPCoopMessageFactory (open:"+OpenSPCoop2MessageFactory_impl.class.getName().equals(factory.getClass().getName())+") "+factory.getClass().getName();
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getDirectoryConfigurazione(){
		try{
			if(this.openspcoopProperties!=null){
				
				StringBuffer bf = new StringBuffer();
				if(System.getenv(CostantiPdD.OPENSPCOOP2_LOCAL_HOME)!=null){
					if(bf.length()>0){
						bf.append("\n");
					}
					bf.append("SystemProperty["+CostantiPdD.OPENSPCOOP2_LOCAL_HOME+"]=["+System.getenv(CostantiPdD.OPENSPCOOP2_LOCAL_HOME)+"]");
				}
				if(System.getProperty(CostantiPdD.OPENSPCOOP2_LOCAL_HOME)!=null){
					if(bf.length()>0){
						bf.append("\n");
					}
					bf.append("JavaProperty["+CostantiPdD.OPENSPCOOP2_LOCAL_HOME+"]=["+System.getProperty(CostantiPdD.OPENSPCOOP2_LOCAL_HOME)+"]");
				}
				if(bf.length()>0){
					bf.append("\n");
				}
				bf.append("OpenSPCoop2PropertiesReader["+this.openspcoopProperties.getRootDirectory()+"]");
				
				return bf.toString();
			}
			throw new Exception("Directory di Configurazione non disponibile");
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getPluginProtocols(){
		try{
			MapReader<String, IProtocolFactory> prots = ProtocolFactoryManager.getInstance().getProtocolFactories();
			if(prots.size()<=0){
				throw new Exception("No protocol installed");
			}
			else{
				StringBuffer bfProtocols = new StringBuffer();
				Enumeration<String> keys = prots.keys();
				while (keys.hasMoreElements()) {
					String key = (String) keys.nextElement();
					IProtocolFactory pf = prots.get(key);
					if(pf.getManifest().getWeb().getEmptyContext()!=null && pf.getManifest().getWeb().getEmptyContext().isEnabled()){
						if(bfProtocols.length()>0){
							bfProtocols.append("\n");
						}
						bfProtocols.append("\"\" (protocol:"+key+")");
					}
					if(pf.getManifest().getWeb().sizeContextList()>0){
						for (String context : pf.getManifest().getWeb().getContextList()) {
							if(bfProtocols.length()>0){
								bfProtocols.append("\n");
							}
							bfProtocols.append(context+" (protocol:"+key+")");
						}
					}
				}
				String enabledProtocols = bfProtocols.toString();
				return enabledProtocols;
			}
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}

	
	

}
