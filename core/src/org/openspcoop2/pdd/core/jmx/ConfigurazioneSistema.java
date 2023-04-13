/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import java.security.Provider;
import java.security.Security;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.crypto.Cipher;
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

import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.filetrace.FileTraceConfig;
import org.openspcoop2.pdd.logger.filetrace.FileTraceGovWayState;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.manifest.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsMultiException;
import org.openspcoop2.utils.certificate.hsm.HSMManager;
import org.openspcoop2.utils.certificate.ocsp.OCSPManager;
import org.openspcoop2.utils.datasource.DataSourceFactory;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.resources.CharsetUtilities;
import org.openspcoop2.utils.resources.MapReader;
import org.openspcoop2.utils.transport.http.SSLConstants;
import org.openspcoop2.utils.transport.http.SSLUtilities;
import org.slf4j.Logger;

import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;


/**
 * Implementazione JMX per la gestione della Configurazione di Sistema della Porta
 *   
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneSistema extends NotificationBroadcasterSupport implements DynamicMBean {

	/** Nomi metodi */
	public static final String VERSIONE_PDD = "getVersionePdD";
	public static final String VERSIONE_BASE_DATI = "getVersioneBaseDati";
	public static final String VERSIONE_JAVA = "getVersioneJava";
	public static final String VENDOR_JAVA = "getVendorJava";
	public static final String TIPO_DATABASE = "getTipoDatabase";	
	public static final String INFORMAZIONI_DATABASE = "getInformazioniDatabase";
	public static final String INFORMAZIONI_SSL = "getInformazioniSSL";
	public static final String INFORMAZIONI_COMPLETE_SSL = "getInformazioniCompleteSSL";
	public static final String INFORMAZIONI_CRYPTOGRAPHY_KEY_LENGTH = "getInformazioniCryptographyKeyLength";
	public static final String INFORMAZIONI_CHARSET = "getInformazioniCharset";
	public static final String INFORMAZIONI_INTERNAZIONALIZZAZIONE = "getInformazioniInternazionalizzazione";
	public static final String INFORMAZIONI_COMPLETE_INTERNAZIONALIZZAZIONE = "getInformazioniCompleteInternazionalizzazione";
	public static final String INFORMAZIONI_TIMEZONE= "getInformazioniTimeZone";
	public static final String INFORMAZIONI_COMPLETE_TIMEZONE = "getInformazioniCompleteTimeZone";
	public static final String INFORMAZIONI_PROPRIETA_JAVA_NETWORKING= "getInformazioniProprietaJavaNetworking";
	public static final String INFORMAZIONI_COMPLETE_PROPRIETA_JAVA_NETWORKING= "getInformazioniCompleteProprietaJavaNetworking";
	public static final String INFORMAZIONI_PROPRIETA_JAVA_ALTRO= "getInformazioniProprietaJavaAltro";
	public static final String INFORMAZIONI_PROPRIETA_SISTEMA= "getInformazioniProprietaSistema";
	public static final String MESSAGE_FACTORY = "getMessageFactory";
	public static final String DIRECTORY_CONFIGURAZIONE = "getDirectoryConfigurazione";
	public static final String PROTOCOLS = "getPluginProtocols";
	public static final String INFORMAZIONI_INSTALLAZIONE = "getInformazioniInstallazione";
	public static final String FILE_TRACE_CONFIG = "getFileTrace";
	public static final String FILE_TRACE_UPDATE = "updateFileTrace";

	private static boolean includePassword = false;
	public static boolean isIncludePassword() {
		return includePassword;
	}
	public static void setIncludePassword(boolean includePassword) {
		ConfigurazioneSistema.includePassword = includePassword;
	}

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
			}catch(JMException ex){
				// ignore
			}
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
			}catch(JMException ex){
				// ignore
			}
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
		
		else if(actionName.equals(VENDOR_JAVA)){
			return this.getVendorJava();
		}

		else if(actionName.equals(TIPO_DATABASE)){
			return this.getTipoDatabase();
		}

		else if(actionName.equals(INFORMAZIONI_DATABASE)){
			return this.getInformazioniDatabase();
		}
		
		else if(actionName.equals(INFORMAZIONI_SSL)){
			return this.getInformazioniSSL(false,false,false,false);
		}
		
		else if(actionName.equals(INFORMAZIONI_COMPLETE_SSL)){
			return this.getInformazioniSSL(true,true,true,true);
		}
		
		else if(actionName.equals(INFORMAZIONI_CRYPTOGRAPHY_KEY_LENGTH)){
			return this.getInformazioniCryptographyKeyLength();
		}
		
		else if(actionName.equals(INFORMAZIONI_CHARSET)){
			return this.getInformazioniCharset();
		}
		
		else if(actionName.equals(INFORMAZIONI_INTERNAZIONALIZZAZIONE)){
			return this.getInformazioniInternazionalizzazione(false);
		}
		
		else if(actionName.equals(INFORMAZIONI_COMPLETE_INTERNAZIONALIZZAZIONE)){
			return this.getInformazioniInternazionalizzazione(true);
		}
		
		else if(actionName.equals(INFORMAZIONI_TIMEZONE)){
			return this.getInformazioniTimeZone(false);
		}
		
		else if(actionName.equals(INFORMAZIONI_COMPLETE_TIMEZONE)){
			return this.getInformazioniTimeZone(true);
		}
		
		else if(actionName.equals(INFORMAZIONI_PROPRIETA_JAVA_NETWORKING)){
			return this.getInformazioniProprietaJava(false, true,false,ConfigurazioneSistema.includePassword);
		}
		
		else if(actionName.equals(INFORMAZIONI_COMPLETE_PROPRIETA_JAVA_NETWORKING)){
			return this.getInformazioniProprietaJava(true, true,false,ConfigurazioneSistema.includePassword);
		}
		
		else if(actionName.equals(INFORMAZIONI_PROPRIETA_JAVA_ALTRO)){
			return this.getInformazioniProprietaJava(true, false, true,ConfigurazioneSistema.includePassword);
		}
		
		else if(actionName.equals(INFORMAZIONI_PROPRIETA_SISTEMA)){
			return this.getInformazioniProprietaSistema();
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
		
		else if(actionName.equals(INFORMAZIONI_INSTALLAZIONE)){
			return this.getInformazioniInstallazione();
		}
		
		else if(actionName.equals(FILE_TRACE_CONFIG)){
			return this.getFileTrace();
		}
		
		else if(actionName.equals(FILE_TRACE_UPDATE)){
			return this.updateFileTrace();
		}


		throw new UnsupportedOperationException("Operazione "+actionName+" sconosciuta");
	}

	/* MBean info */
	@Override
	public MBeanInfo getMBeanInfo(){

		// Descrizione della classe nel MBean
		String className = this.getClass().getName();
		String description = "Configurazione di Sistema ("+this.openspcoopProperties.getVersione()+")";

		// VERSIONE_PDD
		MBeanOperationInfo versionePddOp = new MBeanOperationInfo(VERSIONE_PDD,"Visualizza la versione di GovWay",
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
		
		// VENDOR_JAVA
		MBeanOperationInfo vendorJavaOp = new MBeanOperationInfo(VENDOR_JAVA,"Visualizza le informazioni sul vendor di Java",
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
		
		// INFORMAZIONI_SSL
		MBeanOperationInfo informazioniSSLOp = new MBeanOperationInfo(INFORMAZIONI_SSL,"Visualizza le informazioni sulle connessioni SSL",
						null,
						//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
						String.class.getName(),
						MBeanOperationInfo.ACTION);
		
		// INFORMAZIONI_COMPLETE_SSL
		MBeanOperationInfo informazioniCompleteSSLOp = new MBeanOperationInfo(INFORMAZIONI_COMPLETE_SSL,"Visualizza le informazioni complete di algoritmi sulle connessioni SSL",
						null,
						//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
						String.class.getName(),
						MBeanOperationInfo.ACTION);
		
		// INFORMAZIONI_CRYPTOGRAPHY_KEY_LENGTH
		MBeanOperationInfo informazioniCRYPTOOp = new MBeanOperationInfo(INFORMAZIONI_CRYPTOGRAPHY_KEY_LENGTH,"Visualizza le informazioni sulla lunghezza delle chiavi di cifratura",
						null,
						//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
						String.class.getName(),
						MBeanOperationInfo.ACTION);
		
		// INFORMAZIONI_CHARSET
		MBeanOperationInfo informazioniCHARSETOp = new MBeanOperationInfo(INFORMAZIONI_CHARSET,"Visualizza le informazioni sul Charset",
						null,
						//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
						String.class.getName(),
						MBeanOperationInfo.ACTION);
		
		// INFORMAZIONI_INTERNAZIONALIZZAZIONE
		MBeanOperationInfo informazioniINTERNAZIONALIZZAZIONEOp = new MBeanOperationInfo(INFORMAZIONI_INTERNAZIONALIZZAZIONE,"Visualizza le informazioni sull'internazionalizzazione",
						null,
						//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
						String.class.getName(),
						MBeanOperationInfo.ACTION);
		
		// INFORMAZIONI_COMPLETE_INTERNAZIONALIZZAZIONE
		MBeanOperationInfo informazioniCompleteINTERNAZIONALIZZAZIONEOp = new MBeanOperationInfo(INFORMAZIONI_COMPLETE_INTERNAZIONALIZZAZIONE,"Visualizza le informazioni complete sull'internazionalizzazione",
						null,
						//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
						String.class.getName(),
						MBeanOperationInfo.ACTION);
		
		// INFORMAZIONI_TIMEZONE
		MBeanOperationInfo informazioniTIMEZONEOp = new MBeanOperationInfo(INFORMAZIONI_TIMEZONE,"Visualizza le informazioni sul TimeZone",
						null,
						//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
						String.class.getName(),
						MBeanOperationInfo.ACTION);
		
		// INFORMAZIONI_COMPLETE_TIMEZONE
		MBeanOperationInfo informazioniCompleteTIMEZONEOp = new MBeanOperationInfo(INFORMAZIONI_COMPLETE_TIMEZONE,"Visualizza le informazioni complete sul TimeZone",
						null,
						//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
						String.class.getName(),
						MBeanOperationInfo.ACTION);
		
		// INFORMAZIONI_PROPRIETA_JAVA_NETWORKING
		MBeanOperationInfo informazioniProprietaJavaNetworkingOp = new MBeanOperationInfo(INFORMAZIONI_PROPRIETA_JAVA_NETWORKING,"Visualizza le proprietà java riguardanti il networking",
						null,
						//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
						String.class.getName(),
						MBeanOperationInfo.ACTION);
		
		// INFORMAZIONI_COMPLETE_PROPRIETA_JAVA_NETWORKING
		MBeanOperationInfo informazioniCompleteProprietaJavaNetworkingOp = new MBeanOperationInfo(INFORMAZIONI_COMPLETE_PROPRIETA_JAVA_NETWORKING,"Visualizza tutte le proprietà java riguardanti il networking",
						null,
						//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
						String.class.getName(),
						MBeanOperationInfo.ACTION);
		
		// INFORMAZIONI_PROPRIETA_JAVA_ALTRO
		MBeanOperationInfo informazioniProprietaJavaAltroOp = new MBeanOperationInfo(INFORMAZIONI_PROPRIETA_JAVA_ALTRO,"Visualizza le proprietà java escluse quelle riguardanti il networking",
						null,
						//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
						String.class.getName(),
						MBeanOperationInfo.ACTION);
		
		// INFORMAZIONI_PROPRIETA_SISTEMA
		MBeanOperationInfo informazioniProprietaSistemaOp = new MBeanOperationInfo(INFORMAZIONI_PROPRIETA_SISTEMA,"Visualizza le proprietà di sistema",
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

		// FILE_TRACE_CONFIG
		MBeanOperationInfo fileTraceConfigOp = new MBeanOperationInfo(FILE_TRACE_CONFIG,"Visualizza il path della configurazione del FileTrace",
				null,
				//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
				String.class.getName(),
				MBeanOperationInfo.ACTION);
		
		// FILE_TRACE_UPDATE
		MBeanOperationInfo fileTraceUpdateOp = new MBeanOperationInfo(FILE_TRACE_UPDATE,"Aggiorna la configurazione del FileTrace",
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
		MBeanOperationInfo[] operations = new MBeanOperationInfo[]{versionePddOp,versioneBaseDatiOp,vendorJavaOp,versioneJavaOp,
				versioneTipoDatabaseOp,informazioniDatabaseOp,informazioniSSLOp,informazioniCompleteSSLOp,informazioniCRYPTOOp,
				informazioniCHARSETOp, informazioniINTERNAZIONALIZZAZIONEOp,informazioniCompleteINTERNAZIONALIZZAZIONEOp,
				informazioniTIMEZONEOp, informazioniCompleteTIMEZONEOp,
				informazioniProprietaJavaNetworkingOp, informazioniCompleteProprietaJavaNetworkingOp, 
				informazioniProprietaJavaAltroOp, informazioniProprietaSistemaOp,
				messageFactoryOp,confDirectoryOp,protocolsOp,
				fileTraceConfigOp, fileTraceUpdateOp};

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
			String versione = OpenSPCoop2Properties.getVersionePdD(this.openspcoopProperties);
			return versione;
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}

	public String getVersioneBaseDati(){
		try{
			VersioneBaseDatiChecker versioneBaseDatiChecker = new VersioneBaseDatiChecker(this.openspcoopProperties);
			return Utilities.execute(5, versioneBaseDatiChecker);
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
	
	public String getVendorJava(){
		try{
			String v = System.getProperty("java.vendor");
			if(v==null || "".equals(v)){
				v = System.getProperty("java.vm.vendor");
			}
			if(v==null || "".equals(v)){
				v = null;
			}
			
			String name = System.getProperty("java.vm.name");
			if(name==null || "".equals(name)){
				name = null;
			}
			
			if(v!=null && name!=null){
				return v + " " + name;
			}
			else if(v!=null){
				return v;
			}
			else if(name!=null){
				return name;
			}
			
			throw new Exception("Vendor Java non disponibile");

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
			InformazioniDatabaseChecker versioneBaseDatiChecker = new InformazioniDatabaseChecker(this.openspcoopProperties);
			return Utilities.execute(5, versioneBaseDatiChecker);
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public Map<String, String> getInformazioniAltriDatabase(){
		try{
			if(DataSourceFactory.sizeDatasources()>0) {
				List<String> jndiNames = DataSourceFactory.getJndiNameDatasources();
				if(jndiNames!=null && !jndiNames.isEmpty()) {
					Map<String, String> map = new HashMap<>();
					for (String jndiName : jndiNames) {
						try{
							map.put(jndiName, DataSourceFactory.getInstance(jndiName).getInformazioniDatabase());
						}catch(Throwable e){
							this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
							map.put(jndiName, JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage());
						}
					}
					return map;
				}
			}
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
		}
		return null;
	}
	
	public String getInformazioniSSL(boolean cipherSuites, boolean providerInfo, boolean hsmInfo, boolean ocspInfo){
		try{
			StringBuilder bf = new StringBuilder();
			bf.append("SupportedProtocols: "+SSLUtilities.getSSLSupportedProtocols());
			bf.append("\n");
			// Molto verboso
			if(cipherSuites){
				bf.append("SupportedCipherSuites: "+SSLUtilities.getSSLSupportedCipherSuites());
				bf.append("\n");
			}
			List<String> p = SSLUtilities.getSSLSupportedProtocols();
			if(p!=null && p.size()>0){
				for (String protocol : p) {
					if(cipherSuites){
						bf.append("\n");
					}
					printSSLInfo(protocol, bf, cipherSuites);
				}
				// Per retrocompatibilità verifico anche alias SSL e TLS in modo da sapere come si comportano se sono stati associati a delle configurazioni
				if(p.contains(SSLConstants.PROTOCOL_TLS)==false){
					if(cipherSuites){
						bf.append("\n");
					}
					printSSLInfo(SSLConstants.PROTOCOL_TLS, bf, cipherSuites);
				}
				if(p.contains(SSLConstants.PROTOCOL_SSL)==false){
					if(cipherSuites){
						bf.append("\n");
					}
					printSSLInfo(SSLConstants.PROTOCOL_SSL, bf, cipherSuites);
				}
			}

			if(providerInfo){
				bf.append("\n");
				bf.append("Providers: "+SSLUtilities.getSSLProvidersName());
				bf.append("\n");
				List<Provider> lProviders = SSLUtilities.getSSLProviders();
				for (Provider provider : lProviders) {
					printSSLProviderInfo(provider, bf);
				}
			}
			
			if(hsmInfo) {
				bf.append("\n");
				HSMManager hsmManager = HSMManager.getInstance();
				if(hsmManager!=null) {
					bf.append("HSM Keystore registered: "+hsmManager.getKeystoreTypes());
				}
				else {
					bf.append("HSM disabled"); 
				}
				bf.append("\n");
			}
			
			
			if(ocspInfo) {
				bf.append("\n");
				OCSPManager ocspManager = OCSPManager.getInstance();
				if(ocspManager!=null) {
					bf.append("OCSP policy registered: "+ocspManager.getOCSPConfigTypes());
				}
				else {
					bf.append("OCSP disabled"); 
				}
				bf.append("\n");
			}
			
			if(bf.length()<=0){
				throw new Exception("Non sono disponibili informazioni sul contesto SSL");
			}else{
				return bf.toString();
			}

		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	private void printSSLInfo(String protocol,StringBuilder bf,boolean cipherSuites){
		bf.append(protocol+": ");
		try{
			bf.append(SSLUtilities.getSSLEnabledProtocols(protocol));
		}catch(Exception n){
			bf.append(n.getMessage());
		}
		bf.append("\n");
		if(cipherSuites){
			bf.append("CipherSuites: ");
			try{
				bf.append(SSLUtilities.getSSLEnabledCipherSuites(protocol));
			}catch(Exception n){
				bf.append(n.getMessage());
			}
			bf.append("\n");	
		}
	}
	private void printSSLProviderInfo(Provider provider,StringBuilder bf){
		bf.append("\n");
		bf.append(provider.getName());
		bf.append("\n");	
		bf.append("Versione: v").append(provider.getVersionStr()).append(" ");
		bf.append(provider.getInfo());
		bf.append("\n");
		try{
			List<String> serviceTypes = SSLUtilities.getServiceTypes(provider);
			bf.append("ServiceTypes: "+serviceTypes);
			bf.append("\n");	
			for (String serviceType : serviceTypes) {	
				bf.append(serviceType+" Algorithms: "+SSLUtilities.getServiceTypeAlgorithms(provider, serviceType));
				bf.append("\n");	
			}
		}catch(Exception n){
			bf.append(n.getMessage());
		}
	}
	
	public String getInformazioniCryptographyKeyLength(){
		try{
			StringBuilder bf = new StringBuilder();
			Set<?> algorithms = Security.getAlgorithms("Cipher");
			if(algorithms!=null && algorithms.size()>0){
				Iterator<?> it = algorithms.iterator();
				while (it.hasNext()) {
					String algorithm = (String) it.next();
					bf.append(algorithm).append(": ");
					try{
						bf.append(Cipher.getMaxAllowedKeyLength(algorithm));
						bf.append(" bit");
					}catch(Throwable e){
						bf.append(e.getMessage());
					}
					bf.append("\n");
				}
			}
			if(bf.length()<=0){
				throw new Exception("Non sono disponibili informazioni sulla lunghezza delle chiavi di cifratura");
			}else{
				return bf.toString();
			}
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getInformazioniCharset(){
		try{
			StringBuilder bf = new StringBuilder();
			
			bf.append("Property 'file.encoding': ").append(CharsetUtilities.getDefaultCharsetByProperties()).append("\n");
			bf.append("java.io.Reader: ").append(CharsetUtilities.getDefaultCharsetByCode()).append("\n");
			bf.append("java.nio.charset.Charset: ").append(CharsetUtilities.getDefaultCharsetByCharset()).append("\n");
			
			if(bf.length()<=0){
				throw new Exception("Non sono disponibili informazioni sul charset");
			}else{
				return bf.toString();
			}
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getInformazioniInternazionalizzazione(boolean all){
		try{
			StringBuilder bf = new StringBuilder();
			
			StringBuilder bfInternal = new StringBuilder();
			Utilities.toString(java.util.Locale.getDefault(), bfInternal, "\n");
			if(bfInternal.length()>0){
				if(all){
					bf.append("DEFAULT ");
				}
				bf.append(bfInternal.toString());
			}

			if(all){
				java.util.Locale [] l = java.util.Locale.getAvailableLocales();
				if(l!=null){
					List<String> ll = new ArrayList<String>();
					Map<String,java.util.Locale> llMap = new HashMap<String,java.util.Locale>();
					for (int i = 0; i < l.length; i++) {
						ll.add(l[i].getDisplayName());
						llMap.put(l[i].getDisplayName(), l[i]);
					}
					Collections.sort(ll);
					for (String name : ll) {
						java.util.Locale locale = llMap.get(name);
						if(bf.length()>0){
							bf.append("\n");
						}
						Utilities.toString(locale, bf, "\n");
					}
				}
			}
			
			if(bf.length()<=0){
				throw new Exception("Non sono disponibili informazioni sulla internazionalizzazione");
			}else{
				return bf.toString();
			}
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getInformazioniTimeZone(boolean all){
		try{
			StringBuilder bf = new StringBuilder();
			
			StringBuilder bfInternal = new StringBuilder();
			Utilities.toString(java.util.TimeZone.getDefault(), bfInternal, all);
			if(bfInternal.length()>0){
				if(all){
					bf.append("DEFAULT ");
				}
				bf.append(bfInternal.toString());
			}
			
			if(all){
				String [] ids = java.util.TimeZone.getAvailableIDs();
				if(ids!=null){
					List<String> ll = new ArrayList<String>();
					for (int i = 0; i < ids.length; i++) {
						ll.add(ids[i]);
					}
					Collections.sort(ll);
					for (String id : ll) {
						if(bf.length()>0){
							bf.append("\n");
						}
						Utilities.toString(java.util.TimeZone.getTimeZone(id), bf, all);
					}
				}
			}
			
			if(bf.length()>0){
				bf.append("\n");
			}
			bf.append("DateTimeFormatter: "+DateUtils.getDEFAULT_DATA_ENGINE_TYPE());
			
			if(bf.length()<=0){
				throw new Exception("Non sono disponibili informazioni sulla internazionalizzazione");
			}else{
				return bf.toString();
			}
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	private boolean isPasswordProperty(String key) {
		return key.toLowerCase().contains("password");
	}
	private boolean isNetworkProperties(boolean allNetwork, String key) {
		boolean net = key.startsWith("java.net.") || 
				key.startsWith("javax.net.") || 
				key.startsWith("networkaddress.") ||
				key.startsWith("http.") ||
				key.startsWith("https.");
		
		if(allNetwork) {
			return net 
					|| 
					(
						key.startsWith("ftp.") ||
						key.startsWith("socks") ||
						key.startsWith("sun.net.") // implement specific
					);  
		}
		else {
			return net;
		}
				
	}
	
	public String getInformazioniProprietaJava(boolean allNetwork, boolean includeNetwork, boolean includeNotNetwork, boolean includePassword){
		try{
			StringBuilder bf = new StringBuilder();
			
			if(System.getSecurityManager()!=null) {
				bf.append("SecurityManager=").append(System.getSecurityManager().getClass().getName());
			}
			else {
				bf.append("SecurityManager non attivo");
			}
			
			Properties p = System.getProperties();
			Iterator<Object> keys = p.keySet().iterator();
			List<String> ll = new ArrayList<String>();
			while (keys.hasNext()) {
				Object o = keys.next();
				if(!(o instanceof String)) {
					continue;
				}
				String key = (String) o;
				if(!includeNetwork && !includeNotNetwork) {
					throw new Exception("Invocazione errata, almeno un parametro deve essere abilitato");
				}
				else if(includeNetwork && !includeNotNetwork) {
					if(this.isNetworkProperties(allNetwork, key)) {
						if(includePassword || !isPasswordProperty(key)) {
							ll.add(key);
						}
					}
				}
				else if(includeNotNetwork && !includeNetwork) {
					if(!this.isNetworkProperties(true, key)) {
						if(includePassword || !isPasswordProperty(key)) {
							ll.add(key);
						}
					}
				}
				else {
					if(includePassword || !isPasswordProperty(key)) {
						ll.add(key);
					}
				}
			}
			Collections.sort(ll);
			if(ll.size()>0) {
				bf.append("\n"); // Separo security manager
			}
			for (String key : ll) {
				if(bf.length()>0){
					bf.append("\n");
				}
				bf.append(key).append("=").append(p.get(key));
			}
			
			if(bf.length()<=0){
				throw new Exception("Non sono disponibili proprietà java");
			}else{
				return bf.toString();
			}
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getInformazioniProprietaSistema(){
		try{
			StringBuilder bf = new StringBuilder();
			
			if(System.getSecurityManager()!=null) {
				bf.append("SecurityManager=").append(System.getSecurityManager().getClass().getName());
			}
			else {
				bf.append("SecurityManager non attivo");
			}
			
			Map<String, String> map = System.getenv();
			Iterator<String> keys = map.keySet().iterator();
			List<String> ll = new ArrayList<String>();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				ll.add(key);
			}
			Collections.sort(ll);
			if(ll.size()>0) {
				bf.append("\n"); // Separo security manager
			}
			for (String key : ll) {
				if(bf.length()>0){
					bf.append("\n");
				}
				bf.append(key).append("=").append(map.get(key));
			}
			
			if(bf.length()<=0){
				throw new Exception("Non sono disponibili proprietà di sistema");
			}else{
				return bf.toString();
			}
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getMessageFactory(){
		try{
			StringBuilder sb = new StringBuilder();
			OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			
			String docOp2 = factory.getDocumentBuilderFactoryClass();
			String docSaaj = SOAPDocumentImpl.newInstanceDocumentBuilderFactory().getClass().getName();
			sb.append("DocumentBuilderFactory:").append(docSaaj).append(" ");
			if(docSaaj.equals(docOp2)==false) {
				sb.append("OpenSPCoop2DocumentBuilderFactory:").append(docOp2).append(" ");
					
			}
			
			String saxOp2 = factory.getSAXParserFactoryClass();
			String saxSaaj = SOAPDocumentImpl.newInstanceSAXParserFactory().getClass().getName();
			sb.append("SAXParserFactory:").append(saxSaaj).append(" ");
			if(saxSaaj.equals(saxOp2)==false) {
				sb.append("OpenSPCoop2SAXParserFactory:").append(saxOp2).append(" ");
					
			}
			
			return sb.toString();
			
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getDirectoryConfigurazione(){
		try{
			if(this.openspcoopProperties!=null){
				
				StringBuilder bf = new StringBuilder();
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
				bf.append("GovWayProperties["+this.openspcoopProperties.getRootDirectory()+"]");
				
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
			MapReader<String, IProtocolFactory<?>> prots = ProtocolFactoryManager.getInstance().getProtocolFactories();
			if(prots.size()<=0){
				throw new Exception("No protocol installed");
			}
			else{
				StringBuilder bfProtocols = new StringBuilder();
				Enumeration<String> keys = prots.keys();
				while (keys.hasMoreElements()) {
					String key = (String) keys.nextElement();
					IProtocolFactory<?> pf = prots.get(key);
					if(pf.getManifest().getWeb().getEmptyContext()!=null && pf.getManifest().getWeb().getEmptyContext().isEnabled()){
						if(bfProtocols.length()>0){
							bfProtocols.append("\n");
						}
						bfProtocols.append("\"\" (protocol:"+key+")");
					}
					if(pf.getManifest().getWeb().sizeContextList()>0){
						for (Context context : pf.getManifest().getWeb().getContextList()) {
							if(bfProtocols.length()>0){
								bfProtocols.append("\n");
							}
							bfProtocols.append(context.getName()+" (protocol:"+key+")");
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

	public String getInformazioniInstallazione(){
		try {
			List<GenericProperties> installerProperties = ConfigurazionePdDManager.getInstance().getGenericProperties(CostantiPdD.TIPOLOGIA_INSTALLER);
			if(installerProperties==null || installerProperties.isEmpty()) {
				throw new DriverConfigurazioneNotFound();
			}
			StringBuilder bf = new StringBuilder();
			// raccolto id
			List<String> ids = new ArrayList<String>();
			for (int i = 0; i < installerProperties.size(); i++) {
				ids.add(installerProperties.get(i).getNome());
			}
			Collections.sort(ids, Collections.reverseOrder());
			for (String name : ids) {
				for (GenericProperties gp : installerProperties) {
					if(gp.getNome().equals(name)) {
						if(bf.length()>0) {
							bf.append("\n");
						}
						bf.append(gp.getDescrizione());
						bf.append("\n");
						
						List<String> idParams = new ArrayList<String>();
						for (Property p : gp.getPropertyList()) {
							idParams.add(p.getNome());
						}
						Collections.sort(idParams);
						for (String idP : idParams) {
							for (Property p : gp.getPropertyList()) {
								if(p.getNome().equals(idP)) {
									
									if(p.getNome().endsWith("-000:sezione")) {
										bf.append(p.getValore());
										bf.append("\n");
									}
									else {
										bf.append("\t");
										String [] split = p.getNome().split(":");
										if(split!=null && split.length==3) {
											bf.append(split[1]);
											bf.append(" (");
											bf.append(split[2]);
											bf.append(") = ");
										}
										else {
											bf.append(p.getNome());
											bf.append(" = ");
										}
										bf.append(p.getValore());
										bf.append("\n");
									}
									
									break;
								}
							}
						}
												
						break;
					}
				}
			}
			
			if(bf.length()>0) {
				return bf.toString();
			}
			else {
				return "Informazioni non presenti";
			}
			
		}catch(DriverConfigurazioneNotFound notFound) {
			return "Informazioni non presenti";
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
		
	}
	
	public String getFileTrace(){
		try {
			FileTraceGovWayState state = this.openspcoopProperties.getFileTraceGovWayState();
			return state.toString();
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String updateFileTrace(){
		try {
			if(this.openspcoopProperties.isTransazioniFileTraceEnabled()){
				FileTraceConfig.update(this.openspcoopProperties.getTransazioniFileTraceConfig(), true);
				return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
			}
			else {
				throw new Exception("Funzionalità 'FileTrace' disabilitata");
			}
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
}

class VersioneBaseDatiChecker implements Callable<String>{

	private OpenSPCoop2Properties openspcoopProperties;
	public VersioneBaseDatiChecker(OpenSPCoop2Properties openspcoopProperties) {
		this.openspcoopProperties = openspcoopProperties;
	}
	
	@Override
	public String call() throws Exception {
		
		if(DBManager.isInitialized()==false){
			throw new Exception("Inizializzazione DBManager non effettuata");
		}
		DBManager dbManager = DBManager.getInstance();
		Resource resource = null;
		IDSoggetto dominio = this.openspcoopProperties.getIdentitaPortaDefaultWithoutProtocol();
		String modulo = this.getClass().getName();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			resource = dbManager.getResource(dominio, modulo, null);
			Connection c = (Connection) resource.getResource();
			String sql = "select * from "+CostantiDB.DB_INFO_CONSOLE +" order by id DESC";
			StringBuilder bf = new StringBuilder();
			pstmt = c.prepareStatement(sql);
			try {
				rs = pstmt.executeQuery();
				while (rs.next()) {
					int major_version = rs.getInt("major_version");
					int minor_version = rs.getInt("minor_version");
					String details = rs.getString("notes");
					if(bf.length()>0){
						bf.append("\n");
					}
					bf.append("["+major_version+"."+minor_version+"] "+details);
				}
			}catch(Throwable t) {
				
				try{
					if(rs!=null)
						rs.close();
				}catch(Exception eClose){
					// close
				}
				try{
					if(pstmt!=null)
						pstmt.close();
				}catch(Exception eClose){
					// close
				}
				
				sql = "select * from "+CostantiDB.DB_INFO +" order by id DESC";
				pstmt = c.prepareStatement(sql);
				rs = pstmt.executeQuery();
				try {
					while (rs.next()) {
						int major_version = rs.getInt("major_version");
						int minor_version = rs.getInt("minor_version");
						String details = rs.getString("notes");
						if(bf.length()>0){
							bf.append("\n");
						}
						bf.append("["+major_version+"."+minor_version+"] "+details);
					}
				}catch(Throwable tInternal) {
					throw new UtilsMultiException(t,tInternal);
				}
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
			}catch(Exception eClose){
				// close
			}
			try{
				if(pstmt!=null)
					pstmt.close();
			}catch(Exception eClose){
				// close
			}
			try{
				if(dbManager!=null)
					dbManager.releaseResource(dominio, modulo, resource);
			}catch(Exception eClose){
				// close
			}
		}
		
	}
	
}

class InformazioniDatabaseChecker implements Callable<String>{

	private OpenSPCoop2Properties openspcoopProperties;
	public InformazioniDatabaseChecker(OpenSPCoop2Properties openspcoopProperties) {
		this.openspcoopProperties = openspcoopProperties;
	}
	
	@Override
	public String call() throws Exception {
		if(DBManager.isInitialized()==false){
			throw new Exception("Inizializzazione DBManager non effettuata");
		}
		DBManager dbManager = DBManager.getInstance();
		Resource resource = null;
		if(this.openspcoopProperties==null) {
			throw new Exception("Inizializzazione OpenSPCoop2Properties non effettuata");
		}
		IDSoggetto dominio = this.openspcoopProperties.getIdentitaPortaDefaultWithoutProtocol();
		String modulo = this.getClass().getName();
		StringBuilder bf = new StringBuilder();

		if(this.openspcoopProperties.getDatabaseType()!=null){
			bf.append("TipoDatabase: "+this.openspcoopProperties.getDatabaseType());
		}
		else{
			throw new Exception("Tipo di Database non disponibile");
		}

		try{
			resource = dbManager.getResource(dominio, modulo, null);
			Connection c = (Connection) resource.getResource();

			JDBCUtilities.addInformazioniDatabaseFromMetaData(c, bf);
			
			if(bf.length()<=0){
				throw new Exception("Non sono disponibili informazioni sul database");
			}else{
				return bf.toString();
			}

		}finally{
			try{
				if(dbManager!=null)
					dbManager.releaseResource(dominio, modulo, resource);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
}
