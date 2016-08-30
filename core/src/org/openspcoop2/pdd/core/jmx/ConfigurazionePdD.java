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
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;


/**
 * Implementazione JMX per la gestione della Configurazione di OpenSPCoop
 *   
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazionePdD extends NotificationBroadcasterSupport implements DynamicMBean {

	/** Nomi proprieta' */
	public final static String MSG_DIAGNOSTICI_SEVERITA_LIVELLO = "msgDiagnosticiLivelloSeverita";
	public final static String MSG_DIAGNOSTICI_SEVERITA_LIVELLO_LOG4J = "msgDiagnosticiLivelloSeveritaLog4J";
	public final static String MSG_DIAGNOSTICI_APPENDER = "msgDiagnosticiAppender";	
	public final static String TRACCIAMENTO_ABILITATO = "tracciamentoAbilitato";
	public final static String DUMP_ABILITATO = "dumpApplicativoAbilitato";
	public final static String DUMP_BINARIO_PD_ABILITATO = "dumpBinarioPortaDelegataAbilitato";
	public final static String DUMP_BINARIO_PA_ABILITATO = "dumpBinarioPortaApplicativaAbilitato";
	public final static String TRACCIAMENTO_APPENDER = "tracciamentoAppender";
	public final static String LOG4J_DIAGNOSTICA_ABILITATO = "log4jLogFileDiagnosticaAbilitato";
	public final static String LOG4J_OPENSPCOOP_ABILITATO = "log4jLogFileOpenSPCoopAbilitato";
	public final static String LOG4J_INTEGRATION_MANAGER_ABILITATO = "log4jLogFileIntegrationManagerAbilitato";
	public final static String LOG4J_TRACCIAMENTO_ABILITATO = "log4jLogFileTracciamentoAbilitato";
	public final static String LOG4J_DUMP_ABILITATO = "log4jLogFileDumpAbilitato";
	
	
	/** Attributi */
	private boolean cacheAbilitata = false;
	private String msgDiagnosticiLivelloSeverita = "";
	private String msgDiagnosticiLivelloSeveritaLog4J = "";
	private String[] msgDiagnosticiAppender = null;
	private boolean tracciamentoAbilitato = true;
	private boolean dumpApplicativoAbilitato = false;
	private boolean dumpBinarioPDAbilitato = false;
	private boolean dumpBinarioPAAbilitato = false;
	private String[] tracciamentoAppender = null;
	private boolean log4jDiagnosticaAbilitato = false;
	private boolean log4jOpenSPCoopAbilitato = false;
	private boolean log4jIntegrationManagerAbilitato = false;
	private boolean log4jTracciamentoAbilitato = false;
	private boolean log4jDumpAbilitato = false;
	
	/** getAttribute */
	@Override
	public Object getAttribute(String attributeName) throws AttributeNotFoundException,MBeanException,ReflectionException{
		
		if( (attributeName==null) || (attributeName.equals("")) )
			throw new IllegalArgumentException("Il nome dell'attributo e' nullo o vuoto");
		
		if(attributeName.equals(JMXUtils.CACHE_ATTRIBUTE_ABILITATA))
			return this.cacheAbilitata;
		
		if(attributeName.equals(ConfigurazionePdD.MSG_DIAGNOSTICI_SEVERITA_LIVELLO))
			return this.msgDiagnosticiLivelloSeverita;
		
		if(attributeName.equals(ConfigurazionePdD.MSG_DIAGNOSTICI_SEVERITA_LIVELLO_LOG4J))
			return this.msgDiagnosticiLivelloSeveritaLog4J;
		
		if(attributeName.equals(ConfigurazionePdD.MSG_DIAGNOSTICI_APPENDER))
			return this.msgDiagnosticiAppender;
		
		if(attributeName.equals(ConfigurazionePdD.TRACCIAMENTO_ABILITATO))
			return this.tracciamentoAbilitato;
		
		if(attributeName.equals(ConfigurazionePdD.DUMP_ABILITATO))
			return this.dumpApplicativoAbilitato;
		
		if(attributeName.equals(ConfigurazionePdD.DUMP_BINARIO_PD_ABILITATO))
			return this.dumpBinarioPDAbilitato;
		
		if(attributeName.equals(ConfigurazionePdD.DUMP_BINARIO_PA_ABILITATO))
			return this.dumpBinarioPAAbilitato;
		
		if(attributeName.equals(ConfigurazionePdD.TRACCIAMENTO_APPENDER))
			return this.tracciamentoAppender;
		
		if(attributeName.equals(ConfigurazionePdD.LOG4J_DIAGNOSTICA_ABILITATO))
			return this.log4jDiagnosticaAbilitato;
		
		if(attributeName.equals(ConfigurazionePdD.LOG4J_OPENSPCOOP_ABILITATO))
			return this.log4jOpenSPCoopAbilitato;
		
		if(attributeName.equals(ConfigurazionePdD.LOG4J_INTEGRATION_MANAGER_ABILITATO))
			return this.log4jIntegrationManagerAbilitato;
		
		if(attributeName.equals(ConfigurazionePdD.LOG4J_TRACCIAMENTO_ABILITATO))
			return this.log4jTracciamentoAbilitato;
		
		if(attributeName.equals(ConfigurazionePdD.LOG4J_DUMP_ABILITATO))
			return this.log4jDumpAbilitato;
		
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
			
			else if(attribute.getName().equals(ConfigurazionePdD.MSG_DIAGNOSTICI_SEVERITA_LIVELLO))
				this.setMsgDiagnosticiLivelloSeverita( (String) attribute.getValue() );
			
			else if(attribute.getName().equals(ConfigurazionePdD.MSG_DIAGNOSTICI_SEVERITA_LIVELLO_LOG4J))
				this.setMsgDiagnosticiLivelloSeveritaLog4J( (String) attribute.getValue() );
			
			else if(attribute.getName().equals(ConfigurazionePdD.MSG_DIAGNOSTICI_APPENDER))
				this.msgDiagnosticiAppender = (String[]) attribute.getValue();
			
			else if(attribute.getName().equals(ConfigurazionePdD.TRACCIAMENTO_ABILITATO))
				this.setTracciamentoAbilitato((Boolean) attribute.getValue());
			
			else if(attribute.getName().equals(ConfigurazionePdD.DUMP_ABILITATO))
				this.setDumpAbilitato((Boolean) attribute.getValue());
			
			else if(attribute.getName().equals(ConfigurazionePdD.DUMP_BINARIO_PD_ABILITATO))
				this.setDumpBinarioPD((Boolean) attribute.getValue());
			
			else if(attribute.getName().equals(ConfigurazionePdD.DUMP_BINARIO_PA_ABILITATO))
				this.setDumpBinarioPA((Boolean) attribute.getValue());
			
			else if(attribute.getName().equals(ConfigurazionePdD.TRACCIAMENTO_APPENDER))
				this.tracciamentoAppender = (String[]) attribute.getValue();
			
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
				
		throw new UnsupportedOperationException("Operazione "+actionName+" sconosciuta");
	}
	
	/* MBean info */
	@Override
	public MBeanInfo getMBeanInfo(){
				
		// Descrizione della classe nel MBean
		String className = this.getClass().getName();
		String description = "Risorsa per la configurazione della Porta di Dominio "+this.openspcoopProperties.getVersione();

		// MetaData per l'attributo abilitaCache
		MBeanAttributeInfo cacheAbilitataVAR = JMXUtils.MBEAN_ATTRIBUTE_INFO_CACHE_ABILITATA;

		// MetaData per l'attributo livelloMsgDiagnostici
		MBeanAttributeInfo livelloMsgDiagnosticiVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.MSG_DIAGNOSTICI_SEVERITA_LIVELLO,String.class.getName(),
						"Livello dei messaggi diagnostici emessi dalla Porta di Dominio \n[off,fatal,errorProtocol,errorIntegration,infoProtocol,infoIntegration,debugLow,debugMedium,debugHigh,all]",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo livelloMsgDiagnosticiHumanReadable
		MBeanAttributeInfo livelloMsgDiagnosticiLog4JVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.MSG_DIAGNOSTICI_SEVERITA_LIVELLO_LOG4J,String.class.getName(),
						"Livello dei messaggi diagnostici human readable emessi dalla Porta di Dominio \n[off,fatal,errorProtocol,errorIntegration,infoProtocol,infoIntegration,debugLow,debugMedium,debugHigh,all]",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo msgDiagnosticiAppender
		MBeanAttributeInfo msgDiagnosticiAppenderVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.MSG_DIAGNOSTICI_APPENDER,String[].class.getName(),
						"Appender personalizzati per la registrazione dei messaggi diagnostici emessi dalla Porta di Dominio",
							JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
							
		// MetaData per l'attributo tracciamentoAbilitato
		MBeanAttributeInfo tracciamentoAbilitatoVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.TRACCIAMENTO_ABILITATO,boolean.class.getName(),
						"Indicazione se e' abilito il tracciamento delle buste emesse/ricevute dalla Porta di Dominio",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo dumpApplicativoAbilitato
		MBeanAttributeInfo dumpApplicativoAbilitatoVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.DUMP_ABILITATO,boolean.class.getName(),
						"Indicazione se e' abilito la registrazione dei contenuti applicativi gestiti dalla Porta di Dominio",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo dumpBinarioPDAbilitato
		MBeanAttributeInfo dumpBinarioPDAbilitatoVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.DUMP_BINARIO_PD_ABILITATO,boolean.class.getName(),
						"Indicazione se e' abilito la registrazione dei dati binari transitati sulla Porta Delegata",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo dumpBinarioPAAbilitato
		MBeanAttributeInfo dumpBinarioPAAbilitatoVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.DUMP_BINARIO_PA_ABILITATO,boolean.class.getName(),
						"Indicazione se e' abilito la registrazione dei dati binari transitati sulla Porta Applicativa",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo tracciamentoAppender
		MBeanAttributeInfo tracciamentoAppenderVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.TRACCIAMENTO_APPENDER,String[].class.getName(),
						"Appender personalizzati per la registrazione delle buste emesse/ricevute dalla Porta di Dominio",
							JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo log4jDiagnosticaAbilitato
		MBeanAttributeInfo log4jDiagnosticaAbilitatoVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.LOG4J_DIAGNOSTICA_ABILITATO,boolean.class.getName(),
						"Indicazione se è abilitato il logging su file openspcoop2_msgDiagnostico.log",
							JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo log4jOpenSPCoopAbilitato
		MBeanAttributeInfo log4jOpenSPCoopAbilitatoVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.LOG4J_OPENSPCOOP_ABILITATO,boolean.class.getName(),
						"Indicazione se è abilitato il logging su file openspcoop2.log",
							JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo log4jIntegrationManagerAbilitato
		MBeanAttributeInfo log4jIntegrationManagerAbilitatoVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.LOG4J_INTEGRATION_MANAGER_ABILITATO,boolean.class.getName(),
						"Indicazione se è abilitato il logging su file openspcoop2_integrationManager.log",
							JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo log4jTracciamentoAbilitato
		MBeanAttributeInfo log4jTracciamentoAbilitatoVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.LOG4J_DIAGNOSTICA_ABILITATO,boolean.class.getName(),
						"Indicazione se è abilitato il logging su file openspcoop2_tracciamento.log",
							JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo log4jDumpAbilitato
		MBeanAttributeInfo log4jDumpAbilitatoVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.LOG4J_DUMP_ABILITATO,boolean.class.getName(),
						"Indicazione se è abilitato il logging su file openspcoop2_dump.log",
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
				
		// Mbean costruttore
		MBeanConstructorInfo defaultConstructor = new MBeanConstructorInfo("Default Constructor","Crea e inizializza una nuova istanza del MBean",null);

		// Lista attributi
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[]{cacheAbilitataVAR,livelloMsgDiagnosticiVAR,
				livelloMsgDiagnosticiLog4JVAR,msgDiagnosticiAppenderVAR,tracciamentoAbilitatoVAR,
				dumpApplicativoAbilitatoVAR,dumpBinarioPDAbilitatoVAR,dumpBinarioPAAbilitatoVAR,
				tracciamentoAppenderVAR,
				log4jDiagnosticaAbilitatoVAR, log4jOpenSPCoopAbilitatoVAR, log4jIntegrationManagerAbilitatoVAR,
				log4jTracciamentoAbilitatoVAR, log4jDumpAbilitatoVAR};
		
		// Lista Costruttori
		MBeanConstructorInfo[] constructors = new MBeanConstructorInfo[]{defaultConstructor};
		
		// Lista operazioni
		MBeanOperationInfo[] operations = new MBeanOperationInfo[]{resetCacheOP,printStatCacheOP,disabilitaCacheOP,abilitaCacheParametriOP,listKeysCacheOP,getObjectCacheOP,removeObjectCacheOP};
		
		return new MBeanInfo(className,description,attributes,constructors,operations,null);
	}
	
	/* Variabili per la gestione JMX */
	private Logger log;
	org.openspcoop2.pdd.config.ConfigurazionePdDManager configReader = null;
	org.openspcoop2.pdd.config.OpenSPCoop2Properties openspcoopProperties = null;
	
	/* Costruttore */
	public ConfigurazionePdD(){
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		this.configReader = org.openspcoop2.pdd.config.ConfigurazionePdDManager.getInstance();
		this.openspcoopProperties = org.openspcoop2.pdd.config.OpenSPCoop2Properties.getInstance();
				
		// Configurazione
		try{
			this.cacheAbilitata = ConfigurazionePdDReader.isCacheAbilitata();
		}catch(Exception e){
			this.log.error("Errore durante la comprensione dello stato della cache");
		}
				
		// Messaggi diagnostici
		this.msgDiagnosticiLivelloSeverita = LogLevels.toOpenSPCoop2(this.configReader.getSeverita_msgDiagnostici(),true);
		this.msgDiagnosticiLivelloSeveritaLog4J = LogLevels.toOpenSPCoop2(this.configReader.getSeveritaLog4J_msgDiagnostici(),true);
		this.log4jDiagnosticaAbilitato = OpenSPCoop2Logger.loggerMsgDiagnosticoAbilitato;
		this.log4jOpenSPCoopAbilitato = OpenSPCoop2Logger.loggerMsgDiagnosticoReadableAbilitato;
		this.log4jIntegrationManagerAbilitato = OpenSPCoop2Logger.loggerIntegrationManagerAbilitato;
		
		MessaggiDiagnostici msg = this.configReader.getOpenSPCoopAppender_MsgDiagnostici();
		if(msg!=null && msg.sizeOpenspcoopAppenderList()>0){
			this.msgDiagnosticiAppender = new String[msg.sizeOpenspcoopAppenderList()];
			for(int i=0; i<msg.sizeOpenspcoopAppenderList(); i++){
				OpenspcoopAppender appender = msg.getOpenspcoopAppender(i);
				this.msgDiagnosticiAppender[i] = ("Appender di tipo "+appender.getTipo()+", properties size:"+appender.sizePropertyList());
				for(int j=0; j<appender.sizePropertyList(); j++){
					this.msgDiagnosticiAppender[i] = this.msgDiagnosticiAppender[i] + "\n[nome="+
						appender.getProperty(j).getNome() +" valore="+appender.getProperty(j).getValore()+"]";
				}
			}
		}
				
		// Tracciamento
		this.tracciamentoAbilitato = this.configReader.tracciamentoBuste();
		this.dumpApplicativoAbilitato = this.configReader.dumpMessaggi();
		this.dumpBinarioPDAbilitato = this.configReader.dumpBinarioPD();
		this.dumpBinarioPAAbilitato = this.configReader.dumpBinarioPA();
		this.log4jTracciamentoAbilitato = OpenSPCoop2Logger.loggerTracciamentoAbilitato;
		this.log4jDumpAbilitato = OpenSPCoop2Logger.loggerDumpAbilitato;
		
		Tracciamento tracciamento = this.configReader.getOpenSPCoopAppender_Tracciamento();
		if(tracciamento!=null && tracciamento.sizeOpenspcoopAppenderList()>0){
			this.tracciamentoAppender = new String[tracciamento.sizeOpenspcoopAppenderList()];
			for(int i=0; i<tracciamento.sizeOpenspcoopAppenderList(); i++){
				OpenspcoopAppender appender = tracciamento.getOpenspcoopAppender(i);
				this.tracciamentoAppender[i]="Appender di tipo "+appender.getTipo()+", properties size:"+appender.sizePropertyList();
				for(int j=0; j<appender.sizePropertyList(); j++){
					this.tracciamentoAppender[i] = this.tracciamentoAppender[i] + "\n[nome="+
						appender.getProperty(j).getNome() +" valore="+appender.getProperty(j).getValore()+"]";
				}
			}
		}
		
		/*
		 * TODO: Configurazione della PortaDiDominio anche proprio nel file/db
		 */

	}
	
	public boolean isCacheAbilitata() {
		return this.cacheAbilitata;
	}
	
	/* Metodi di management JMX */
	public String resetCache(){
		try{
			if(this.cacheAbilitata==false)
				throw new Exception("Cache non abilitata");
			org.openspcoop2.pdd.config.ConfigurazionePdDReader.resetCache();
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
			return org.openspcoop2.pdd.config.ConfigurazionePdDReader.printStatsCache("\n");
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public void abilitaCache(){
		try{
			org.openspcoop2.pdd.config.ConfigurazionePdDReader.abilitaCache();
			this.cacheAbilitata = true;
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
		}
	}

	public String abilitaCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond){
		try{
			org.openspcoop2.pdd.config.ConfigurazionePdDReader.abilitaCache(dimensioneCache,algoritmoCacheLRU,itemIdleTime,itemLifeSecond);
			this.cacheAbilitata = true;
			return JMXUtils.MSG_ABILITAZIONE_CACHE_EFFETTUATA;
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public void disabilitaCache() throws JMException{
		try{
			org.openspcoop2.pdd.config.ConfigurazionePdDReader.disabilitaCache();
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
			return org.openspcoop2.pdd.config.ConfigurazionePdDReader.listKeysCache("\n");
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getObjectCache(String key){
		try{
			if(this.cacheAbilitata==false)
				throw new Exception("Cache non abilitata");
			return org.openspcoop2.pdd.config.ConfigurazionePdDReader.getObjectCache(key);
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String removeObjectCache(String key){
		try{
			if(this.cacheAbilitata==false)
				throw new Exception("Cache non abilitata");
			org.openspcoop2.pdd.config.ConfigurazionePdDReader.removeObjectCache(key);
			return JMXUtils.MSG_RIMOZIONE_CACHE_EFFETTUATA;
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public void setMsgDiagnosticiLivelloSeverita(String livelloMsgDiagnostici)throws JMException{
		if(!"off".equals(livelloMsgDiagnostici) && 
				!"fatal".equals(livelloMsgDiagnostici) && 
				!"errorProtocol".equals(livelloMsgDiagnostici) &&
				!"errorIntegration".equals(livelloMsgDiagnostici) && 
				!"infoProtocol".equals(livelloMsgDiagnostici) &&
				!"infoIntegration".equals(livelloMsgDiagnostici) && 
				!"debugLow".equals(livelloMsgDiagnostici) &&
				!"debugMedium".equals(livelloMsgDiagnostici) && 
				!"debugHigh".equals(livelloMsgDiagnostici) &&
				!"all".equals(livelloMsgDiagnostici)){
			throw new JMException("Livello "+livelloMsgDiagnostici+" non conosciuto");
		}
		this.msgDiagnosticiLivelloSeverita = livelloMsgDiagnostici;
		ConfigurazionePdDReader.livello_msgDiagnosticiJMX = LogLevels.toLog4J(this.msgDiagnosticiLivelloSeverita);
		ConfigurazionePdDReader.severita_msgDiagnosticiJMX = LogLevels.toOpenSPCoop2(this.msgDiagnosticiLivelloSeverita);
	}
	
	public void setMsgDiagnosticiLivelloSeveritaLog4J(String livelloMsgDiagnosticiLog4j)throws JMException{
		if(!"off".equals(livelloMsgDiagnosticiLog4j) && 
				!"fatal".equals(livelloMsgDiagnosticiLog4j) && 
				!"errorProtocol".equals(livelloMsgDiagnosticiLog4j) &&
				!"errorIntegration".equals(livelloMsgDiagnosticiLog4j) && 
				!"infoProtocol".equals(livelloMsgDiagnosticiLog4j) &&
				!"infoIntegration".equals(livelloMsgDiagnosticiLog4j) && 
				!"debugLow".equals(livelloMsgDiagnosticiLog4j) &&
				!"debugMedium".equals(livelloMsgDiagnosticiLog4j) && 
				!"debugHigh".equals(livelloMsgDiagnosticiLog4j) &&
				!"all".equals(livelloMsgDiagnosticiLog4j)){
			throw new JMException("Livello "+livelloMsgDiagnosticiLog4j+" non conosciuto");
		}
		this.msgDiagnosticiLivelloSeveritaLog4J = livelloMsgDiagnosticiLog4j;
		ConfigurazionePdDReader.livelloLog4J_msgDiagnosticiJMX = LogLevels.toLog4J(this.msgDiagnosticiLivelloSeveritaLog4J);
		ConfigurazionePdDReader.severitaLog4J_msgDiagnosticiJMX = LogLevels.toOpenSPCoop2(this.msgDiagnosticiLivelloSeveritaLog4J);
	}
	
	public void setTracciamentoAbilitato(boolean v){
		this.tracciamentoAbilitato = v;
		ConfigurazionePdDReader.tracciamentoBusteJMX = v;
	}
	
	public void setDumpAbilitato(boolean v){
		this.dumpApplicativoAbilitato = v;
		ConfigurazionePdDReader.dumpMessaggiJMX = v;
	}
	
	public void setDumpBinarioPD(boolean v){
		this.dumpBinarioPDAbilitato = v;
		ConfigurazionePdDReader.dumpBinarioPDJMX = v;
	}
	
	public void setDumpBinarioPA(boolean v){
		this.dumpBinarioPAAbilitato = v;
		ConfigurazionePdDReader.dumpBinarioPAJMX = v;
	}
}
