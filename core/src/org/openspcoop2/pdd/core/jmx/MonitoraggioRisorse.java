/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
import java.util.Map;

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

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.pdd.config.DBConsegneMessageBoxManager;
import org.openspcoop2.pdd.config.DBConsegnePreseInCaricoManager;
import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.DBStatisticheManager;
import org.openspcoop2.pdd.config.DBTransazioniManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.QueueManager;
import org.openspcoop2.pdd.core.connettori.IConnettore;
import org.openspcoop2.pdd.core.connettori.RepositoryConnettori;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.core.transazioni.TransactionNotExistsException;
import org.openspcoop2.pdd.logger.Dump;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.Tracciamento;
import org.openspcoop2.pdd.monitor.StatoPdd;
import org.openspcoop2.pdd.monitor.driver.DriverMonitoraggio;
import org.openspcoop2.pdd.monitor.driver.FilterSearch;
import org.openspcoop2.pdd.services.connector.ConnectorApplicativeThreadPool;
import org.openspcoop2.pdd.timers.TimerMonitoraggioRisorseThread;
import org.openspcoop2.pdd.timers.TimerThresholdThread;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;




/**
 * Implementazione JMX per la gestione delle risorse
 *   
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MonitoraggioRisorse extends NotificationBroadcasterSupport implements DynamicMBean {

	/** Nomi attributi */
	public static final String NUMERO_MSG_IN_CONSEGNA = "numMsgInConsegna";
	public static final String TEMPO_MEDIO_ATTESA_IN_CONSEGNA = "tempoMedioAttesaInConsegna";
	public static final String TEMPO_MAX_ATTESA_IN_CONSEGNA = "tempoMaxAttesaInConsegna";
	public static final String NUMERO_MSG_IN_SPEDIZIONE = "numMsgInSpedizione";
	public static final String TEMPO_MEDIO_ATTESA_IN_SPEDIZIONE = "tempoMedioAttesaInSpedizione";
	public static final String TEMPO_MAX_ATTESA_IN_SPEDIZIONE = "tempoMaxAttesaInSpedizione";
	public static final String NUMERO_MSG_IN_PROCESSAMENTO = "numMsgInProcessamento";
	public static final String TEMPO_MEDIO_ATTESA_IN_PROCESSAMENTO = "tempoMedioAttesaInProcessamento";
	public static final String TEMPO_MAX_ATTESA_IN_PROCESSAMENTO = "tempoMaxAttesaInProcessamento";
	public static final String NUMERO_MSG_TOTALI = "totMessaggi";
	public static final String TEMPO_MEDIO_ATTESA_MSG_TOTALI = "tempoMedioAttesa";
	public static final String TEMPO_MAX_ATTESA_MSG_TOTALI = "tempoMaxAttesa";
	public static final String TOT_MSG_DUPLICATI = "totMessaggiDuplicati";
	public static final String STATO_DATI_MONITORAGGIO = "statoDatiMonitoraggioPdD";
	public static final String RISORSE_DI_SISTEMA = "risorseDiSistema";
	
	/** Nomi metodi */
	public static final String GET_STATO_RISORSE_DI_SISTEMA = "getStatoRisorseSistema";
	public static final String GET_STATO_RISORSE_DI_SISTEMA_METHOD2 = "listStatoRisorseSistema"; // per farlo comparire in jmx-console
	public static final String GET_STATO_CACHES = "getStatoCache";
	public static final String GET_STATO_CACHES_METHOD2 = "listStatoCache"; // per farlo comparire in jmx-console
	public static final String CONNESSIONI_ALLOCATE_DB_MANAGER = "getUsedDBConnections";
	public static final String CONNESSIONI_ALLOCATE_DB_MANAGER_METHOD2 = "listUsedDBConnections"; // per farlo comparire in jmx-console
	public static final String CONNESSIONI_ALLOCATE_QUEUE_MANAGER = "getUsedQueueConnections";
	public static final String CONNESSIONI_ALLOCATE_QUEUE_MANAGER_METHOD2 = "listUsedQueueConnections"; // per farlo comparire in jmx-console
	public static final String TRANSAZIONI_ATTIVE_ID = "getActiveTransactions";
	public static final String TRANSAZIONI_ATTIVE_ID_METHOD2 = "listActiveTransactions"; // per farlo comparire in jmx-console
	public static final String TRANSAZIONI_ATTIVE_ID_PROTOCOLLO = "getActiveProtocolIds";
	public static final String TRANSAZIONI_ATTIVE_ID_PROTOCOLLO_METHOD2 = "listActiveProtocolIds"; // per farlo comparire in jmx-console
	public static final String GET_DETTAGLIO_TRANSAZIONE_ATTIVA = "getActiveTransactionDetails";
	public static final String CONNESSIONI_ALLOCATE_CONNETTORI_PD = "getActivePDConnections";
	public static final String CONNESSIONI_ALLOCATE_CONNETTORI_PD_METHOD2 = "listActivePDConnections"; // per farlo comparire in jmx-console
	public static final String CONNESSIONI_ALLOCATE_CONNETTORI_PA = "getActivePAConnections";
	public static final String CONNESSIONI_ALLOCATE_CONNETTORI_PA_METHOD2 = "listActivePAConnections"; // per farlo comparire in jmx-console
	
	public static final String BIO_HTTP_CLIENT_CONNECTION_MANAGER_STATUS = "getBIOHttpClientConnectionManagerStatus";
	public static final String BIO_HTTP_CLIENT_CONNECTION_MANAGER_STATUS_METHOD2 = "listBIOHttpClientConnectionManagerStatus"; // per farlo comparire in jmx-console
	public static final String BIO_HTTP_CLIENT_CONNECTION_MANAGER_RESTART = "restartBIOHttpClientConnectionManager";
	public static final String BIO_WORKER_THREAD_POOL_STATUS = "getBIOWorkerThreadPoolStatus";
	public static final String BIO_WORKER_THREAD_POOL_STATUS_METHOD2 = "listBIOWorkerThreadPoolStatus"; // per farlo comparire in jmx-console
	
	public static final String NIO_HTTP_CLIENT_CONNECTION_MANAGER_STATUS = "getNIOHttpClientConnectionManagerStatus";
	public static final String NIO_HTTP_CLIENT_CONNECTION_MANAGER_STATUS_METHOD2 = "getNIOHttpClientConnectionManagerStatus"; // per farlo comparire in jmx-console
	public static final String NIO_HTTP_CLIENT_CONNECTION_MANAGER_RESTART = "restartNIOHttpClientConnectionManager";
	public static final String NIO_HTTP_CLIENT_CONNECTION_MANAGER_IO_THREAD_COUNT = "getNIOHttpClientConnectionManagerIOThreadCount";
	public static final String NIO_HTTP_CLIENT_CONNECTION_MANAGER_IO_THREAD_COUNT_METHOD2 = "getNIOHttpClientConnectionManagerIOThreadCount"; // per farlo comparire in jmx-console
	public static final String NIO_WORKER_THREAD_POOL_STATUS = "getNIOWorkerThreadPoolStatus";
	public static final String NIO_WORKER_THREAD_POOL_STATUS_METHOD2 = "getNIOWorkerThreadPoolStatus"; // per farlo comparire in jmx-console
		
	/** Attributi */
	// Messaggi in consegna
    private long numMsgInConsegna = 0;
    // Secondi
    private String tempoMedioAttesaInConsegna = "";
    private String tempoMaxAttesaInConsegna = "";  
    //  Messaggi in spedizione
    private long numMsgInSpedizione = 0;
    //  Secondi
    private String tempoMedioAttesaInSpedizione = "";
    private String tempoMaxAttesaInSpedizione = ""; 
    //  Messaggi in processamento
    private long numMsgInProcessamento=0;
    //  Secondi
    private String tempoMedioAttesaInProcessamento = "";
    private String tempoMaxAttesaInProcessamento = "";
    // Messaggi totali
    private long totMessaggi=0;
    // Secondi
    private String tempoMedioAttesa = "";
    private String tempoMaxAttesa = "";
    // Messaggi duplicati
    private long totMessaggiDuplicati=0;
    
    //  Risorse di sistema non disponibili o disponibili ad un livello critico
    private String risorseDiSistema ="Lettura dati non effettuata         ";
	
    // Stato dati MonitoraggioPdD
    private String datiMonitoraggioPdD = "Non disponibili";
	
	/** getAttribute */
	@Override
	public Object getAttribute(String attributeName) throws AttributeNotFoundException,MBeanException,ReflectionException{
		
		if( (attributeName==null) || (attributeName.equals("")) )
			throw new IllegalArgumentException("Il nome dell'attributo e' nullo o vuoto");
		
		if(attributeName.equals(MonitoraggioRisorse.NUMERO_MSG_IN_CONSEGNA))
			return this.numMsgInConsegna;
		
		if(attributeName.equals(MonitoraggioRisorse.TEMPO_MEDIO_ATTESA_IN_CONSEGNA))
			return this.tempoMedioAttesaInConsegna;
		
		if(attributeName.equals(MonitoraggioRisorse.TEMPO_MAX_ATTESA_IN_CONSEGNA))
			return this.tempoMaxAttesaInConsegna;
		
		if(attributeName.equals(MonitoraggioRisorse.NUMERO_MSG_IN_SPEDIZIONE))
			return this.numMsgInSpedizione;
		
		if(attributeName.equals(MonitoraggioRisorse.TEMPO_MEDIO_ATTESA_IN_SPEDIZIONE))
			return this.tempoMedioAttesaInSpedizione;
		
		if(attributeName.equals(MonitoraggioRisorse.TEMPO_MAX_ATTESA_IN_SPEDIZIONE))
			return this.tempoMaxAttesaInSpedizione;
		
		if(attributeName.equals(MonitoraggioRisorse.NUMERO_MSG_IN_PROCESSAMENTO))
			return this.numMsgInProcessamento;
		
		if(attributeName.equals(MonitoraggioRisorse.TEMPO_MEDIO_ATTESA_IN_PROCESSAMENTO))
			return this.tempoMedioAttesaInProcessamento;
		
		if(attributeName.equals(MonitoraggioRisorse.TEMPO_MAX_ATTESA_IN_PROCESSAMENTO))
			return this.tempoMaxAttesaInProcessamento;
		
		if(attributeName.equals(MonitoraggioRisorse.NUMERO_MSG_TOTALI))
			return this.totMessaggi;
		
		if(attributeName.equals(MonitoraggioRisorse.TEMPO_MEDIO_ATTESA_MSG_TOTALI))
			return this.tempoMedioAttesa;
		
		if(attributeName.equals(MonitoraggioRisorse.TEMPO_MAX_ATTESA_MSG_TOTALI))
			return this.tempoMaxAttesa;
		
		if(attributeName.equals(MonitoraggioRisorse.TOT_MSG_DUPLICATI))
			return this.totMessaggiDuplicati;
		
		if(attributeName.equals(MonitoraggioRisorse.RISORSE_DI_SISTEMA))
			return this.risorseDiSistema;
		
		if(attributeName.equals(MonitoraggioRisorse.STATO_DATI_MONITORAGGIO))
			return this.datiMonitoraggioPdD;
		
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
			
			if(attribute.getName().equals(MonitoraggioRisorse.NUMERO_MSG_IN_CONSEGNA))
				this.numMsgInConsegna = (Long) attribute.getValue();
			
			else if(attribute.getName().equals(MonitoraggioRisorse.TEMPO_MEDIO_ATTESA_IN_CONSEGNA))
				this.tempoMedioAttesaInConsegna = (String) attribute.getValue();
			
			else if(attribute.getName().equals(MonitoraggioRisorse.TEMPO_MAX_ATTESA_IN_CONSEGNA))
				this.tempoMaxAttesaInConsegna = (String) attribute.getValue();
			
			else if(attribute.getName().equals(MonitoraggioRisorse.NUMERO_MSG_IN_SPEDIZIONE))
				this.numMsgInSpedizione = (Long) attribute.getValue();
			
			else if(attribute.getName().equals(MonitoraggioRisorse.TEMPO_MEDIO_ATTESA_IN_SPEDIZIONE))
				this.tempoMedioAttesaInSpedizione = (String) attribute.getValue();
			
			else if(attribute.getName().equals(MonitoraggioRisorse.TEMPO_MAX_ATTESA_IN_SPEDIZIONE))
				this.tempoMaxAttesaInSpedizione = (String) attribute.getValue();
			
			else if(attribute.getName().equals(MonitoraggioRisorse.NUMERO_MSG_IN_PROCESSAMENTO))
				this.numMsgInProcessamento = (Long) attribute.getValue();
			
			else if(attribute.getName().equals(MonitoraggioRisorse.TEMPO_MEDIO_ATTESA_IN_PROCESSAMENTO))
				this.tempoMedioAttesaInProcessamento = (String) attribute.getValue();
			
			else if(attribute.getName().equals(MonitoraggioRisorse.TEMPO_MAX_ATTESA_IN_PROCESSAMENTO))
				this.tempoMaxAttesaInProcessamento = (String) attribute.getValue();
			
			else if(attribute.getName().equals(MonitoraggioRisorse.NUMERO_MSG_TOTALI))
				this.totMessaggi = (Long) attribute.getValue();
			
			else if(attribute.getName().equals(MonitoraggioRisorse.TEMPO_MEDIO_ATTESA_MSG_TOTALI))
				this.tempoMedioAttesa = (String) attribute.getValue();
			
			else if(attribute.getName().equals(MonitoraggioRisorse.TEMPO_MAX_ATTESA_MSG_TOTALI))
				this.tempoMaxAttesa = (String) attribute.getValue();
			
			else if(attribute.getName().equals(MonitoraggioRisorse.TOT_MSG_DUPLICATI))
				this.totMessaggiDuplicati = (Long) attribute.getValue();
			
			else if(attribute.getName().equals(MonitoraggioRisorse.RISORSE_DI_SISTEMA))
				this.risorseDiSistema = (String) attribute.getValue();
			
			else if(attribute.getName().equals(MonitoraggioRisorse.STATO_DATI_MONITORAGGIO))
				this.refreshDatiMonitoraggioPdD();
			
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
		
		if(actionName.equals(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_DB_MANAGER) || actionName.equals(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_DB_MANAGER_METHOD2)){
			return this.getUsedDBConnections();
		}
		
		else if(actionName.equals(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_QUEUE_MANAGER) || actionName.equals(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_QUEUE_MANAGER_METHOD2)){
			return this.getUsedQueueConnections();
		}
		
		else if(actionName.equals(MonitoraggioRisorse.TRANSAZIONI_ATTIVE_ID) || actionName.equals(MonitoraggioRisorse.TRANSAZIONI_ATTIVE_ID_METHOD2)){
			return this.getTransazioniAttiveId();
		}
		
		else if(actionName.equals(MonitoraggioRisorse.GET_DETTAGLIO_TRANSAZIONE_ATTIVA)){
			
			if(params.length != 1)
				throw new MBeanException(new Exception("["+MonitoraggioRisorse.GET_DETTAGLIO_TRANSAZIONE_ATTIVA+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			
			return this.getDettaglioTransazioneAttivaId(param1);
		}
		
		else if(actionName.equals(MonitoraggioRisorse.TRANSAZIONI_ATTIVE_ID_PROTOCOLLO) || actionName.equals(MonitoraggioRisorse.TRANSAZIONI_ATTIVE_ID_PROTOCOLLO_METHOD2)){
			return this.getTransazioniAttiveIdProtocollo();
		}
		
		else if(actionName.equals(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_CONNETTORI_PD) || actionName.equals(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_CONNETTORI_PD_METHOD2)){
			return this.getActivePDConnections();
		}
		
		else if(actionName.equals(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_CONNETTORI_PA) || actionName.equals(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_CONNETTORI_PA_METHOD2)){
			return this.getActivePAConnections();
		}
		
		else if(actionName.equals(MonitoraggioRisorse.GET_STATO_RISORSE_DI_SISTEMA) || actionName.equals(MonitoraggioRisorse.GET_STATO_RISORSE_DI_SISTEMA_METHOD2)){
			this.refreshDatiMonitoraggioPdD();
			return this.risorseDiSistema;
		}
		
		else if(actionName.equals(MonitoraggioRisorse.GET_STATO_CACHES) || actionName.equals(MonitoraggioRisorse.GET_STATO_CACHES_METHOD2)){
			return this.getStatoCaches();
		}
		
		else if(actionName.equals(MonitoraggioRisorse.BIO_HTTP_CLIENT_CONNECTION_MANAGER_STATUS) || actionName.equals(MonitoraggioRisorse.BIO_HTTP_CLIENT_CONNECTION_MANAGER_STATUS_METHOD2)){
			return this.getBIOHttpClientConnectionManagerStatus();
		}
		else if(actionName.equals(MonitoraggioRisorse.BIO_HTTP_CLIENT_CONNECTION_MANAGER_RESTART)){
			return this.restartBIOHttpClientConnectionManager();
		}
		else if(actionName.equals(MonitoraggioRisorse.BIO_WORKER_THREAD_POOL_STATUS) || actionName.equals(MonitoraggioRisorse.BIO_WORKER_THREAD_POOL_STATUS_METHOD2)){
			return this.getBIOWorkerThreadPoolStatus();
		}
		
		else if(actionName.equals(MonitoraggioRisorse.NIO_HTTP_CLIENT_CONNECTION_MANAGER_STATUS) || actionName.equals(MonitoraggioRisorse.NIO_HTTP_CLIENT_CONNECTION_MANAGER_STATUS_METHOD2)){
			return this.getNIOHttpClientConnectionManagerStatus();
		}
		else if(actionName.equals(MonitoraggioRisorse.NIO_HTTP_CLIENT_CONNECTION_MANAGER_IO_THREAD_COUNT) || actionName.equals(MonitoraggioRisorse.NIO_HTTP_CLIENT_CONNECTION_MANAGER_IO_THREAD_COUNT_METHOD2)){
			return this.getNIOHttpClientConnectionManagerIOThreadCount();
		}
		else if(actionName.equals(MonitoraggioRisorse.NIO_HTTP_CLIENT_CONNECTION_MANAGER_RESTART)){
			return this.restartNIOHttpClientConnectionManager();
		}
		else if(actionName.equals(MonitoraggioRisorse.NIO_WORKER_THREAD_POOL_STATUS) || actionName.equals(MonitoraggioRisorse.NIO_WORKER_THREAD_POOL_STATUS_METHOD2)){
			return this.getNIOWorkerThreadPoolStatus();
		}
		
		throw new UnsupportedOperationException("Operazione "+actionName+" sconosciuta");
	}
	
	/* MBean info */
	@Override
	public MBeanInfo getMBeanInfo(){
		
		/**
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
		String description = "Monitoraggio delle risorse utilizzate ("+OpenSPCoop2Properties.getInstance().getVersione()+"), premi pulsante (apply changes) per aggiornare i dati";

		// MetaData per l'attributo numMsgInConsegna
		MBeanAttributeInfo numMsgInConsegnaVAR 
			= new MBeanAttributeInfo(MonitoraggioRisorse.NUMERO_MSG_IN_CONSEGNA,long.class.getName(),
						"Numero di Messaggi in gestione nel modulo 'ConsegnaContenutiApplicativi'",
							READABLE,!WRITABLE,!IS_GETTER);
		
		// MetaData per l'attributo tempoMedioAttesaInConsegna
		MBeanAttributeInfo tempoMedioAttesaInConsegnaVAR 
			= new MBeanAttributeInfo(MonitoraggioRisorse.TEMPO_MEDIO_ATTESA_IN_CONSEGNA,String.class.getName(),
						"Tempo medio di attesa dei Messaggi prima di essere stati consegnati ai servizi applicativi dal modulo 'ConsegnaContenutiApplicativi'",
							READABLE,!WRITABLE,!IS_GETTER);
		
		// MetaData per l'attributo tempoMaxAttesaInConsegna
		MBeanAttributeInfo tempoMaxAttesaInConsegnaVAR 
			= new MBeanAttributeInfo(MonitoraggioRisorse.TEMPO_MAX_ATTESA_IN_CONSEGNA,String.class.getName(),
						"Tempo massimo di attesa dei Messaggi prima di essere stati consegnati ai servizi applicativi dal modulo 'ConsegnaContenutiApplicativi'",
							READABLE,!WRITABLE,!IS_GETTER);
		
		// MetaData per l'attributo numMsgInSpedizione
		MBeanAttributeInfo numMsgInSpedizioneVAR 
			= new MBeanAttributeInfo(MonitoraggioRisorse.NUMERO_MSG_IN_SPEDIZIONE,long.class.getName(),
						"Numero di Messaggi in gestione nei moduli 'InoltroBuste'",
							READABLE,!WRITABLE,!IS_GETTER);
		
		// MetaData per l'attributo tempoMedioAttesaInSpedizione
		MBeanAttributeInfo tempoMedioAttesaInSpedizioneVAR 
			= new MBeanAttributeInfo(MonitoraggioRisorse.TEMPO_MEDIO_ATTESA_IN_SPEDIZIONE,String.class.getName(),
						"Tempo medio di attesa dei Messaggi prima di essere stati inoltrate alle porte di dominio destinatarie dai moduli 'InoltroBuste'",
							READABLE,!WRITABLE,!IS_GETTER);
		
		// MetaData per l'attributo tempoMaxAttesaInSpedizione
		MBeanAttributeInfo tempoMaxAttesaInSpedizioneVAR 
			= new MBeanAttributeInfo(MonitoraggioRisorse.TEMPO_MAX_ATTESA_IN_SPEDIZIONE,String.class.getName(),
						"Tempo massimo di attesa dei Messaggi prima di essere stati inoltrate alle porte di dominio destinatarie dai moduli 'InoltroBuste'",
							READABLE,!WRITABLE,!IS_GETTER);
		
		// MetaData per l'attributo numMsgInProcessamento
		MBeanAttributeInfo numMsgInProcessamentoVAR 
			= new MBeanAttributeInfo(MonitoraggioRisorse.NUMERO_MSG_IN_PROCESSAMENTO,long.class.getName(),
						"Numero di Messaggi in gestione nei moduli interni all'infrastruttura della porta di dominio",
							READABLE,!WRITABLE,!IS_GETTER);
		
		// MetaData per l'attributo tempoMedioAttesaInProcessamento
		MBeanAttributeInfo tempoMedioAttesaInProcessamentoVAR 
			= new MBeanAttributeInfo(MonitoraggioRisorse.TEMPO_MEDIO_ATTESA_IN_PROCESSAMENTO,String.class.getName(),
						"Tempo medio di attesa in gestione nei moduli interni all'infrastruttura della porta di dominio",
							READABLE,!WRITABLE,!IS_GETTER);
		
		// MetaData per l'attributo tempoMaxAttesaInProcessamento
		MBeanAttributeInfo tempoMaxAttesaInProcessamentoVAR 
			= new MBeanAttributeInfo(MonitoraggioRisorse.TEMPO_MAX_ATTESA_IN_PROCESSAMENTO,String.class.getName(),
						"Tempo massimo di attesa in gestione nei moduli interni all'infrastruttura della porta di dominio",
							READABLE,!WRITABLE,!IS_GETTER);
		
		// MetaData per l'attributo totMessaggi
		MBeanAttributeInfo totMessaggiVAR 
			= new MBeanAttributeInfo(MonitoraggioRisorse.NUMERO_MSG_TOTALI,long.class.getName(),
						"Numero di Messaggi totali in gestione nella porta di dominio",
							READABLE,!WRITABLE,!IS_GETTER);
		
		// MetaData per l'attributo tempoMedioAttesa
		MBeanAttributeInfo tempoMedioAttesaVAR 
			= new MBeanAttributeInfo(MonitoraggioRisorse.TEMPO_MEDIO_ATTESA_MSG_TOTALI,String.class.getName(),
						"Tempo medio di attesa dei Messaggi gestiti nella porta di dominio",
							READABLE,!WRITABLE,!IS_GETTER);
		
		// MetaData per l'attributo tempoMaxAttesa
		MBeanAttributeInfo tempoMaxAttesaVAR 
			= new MBeanAttributeInfo(MonitoraggioRisorse.TEMPO_MAX_ATTESA_MSG_TOTALI,String.class.getName(),
						"Tempo massimo di attesa dei Messaggi gestiti nella porta di dominio",
							READABLE,!WRITABLE,!IS_GETTER);
		
		// MetaData per l'attributo totMessaggiDuplicati
		MBeanAttributeInfo totMessaggiDuplicatiVAR 
			= new MBeanAttributeInfo(MonitoraggioRisorse.TOT_MSG_DUPLICATI,long.class.getName(),
						"Numero di Messaggi duplicati gestiti nella porta di dominio",
							READABLE,!WRITABLE,!IS_GETTER);
		
		// MetaData per l'attributo risorseDisponibili
		MBeanAttributeInfo risorseDisponibiliVAR 
			= new MBeanAttributeInfo(MonitoraggioRisorse.RISORSE_DI_SISTEMA,String.class.getName(),
						"Indicazione sullo stato delle risorse disponibili per la porta di dominio",
							READABLE,!WRITABLE,!IS_GETTER);
		
		// MetaData per l'attributo risorseDisponibili
		MBeanAttributeInfo monitoraggioPdDVAR 
			= new MBeanAttributeInfo(MonitoraggioRisorse.STATO_DATI_MONITORAGGIO,String.class.getName(),
						"Monitoraggio della porta di dominio, premi pulsante (apply changes) per aggiornare i dati",
							READABLE,WRITABLE,!IS_GETTER);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getConnessioneAllocateDBManagerOP 
			= new MBeanOperationInfo(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_DB_MANAGER,"Moduli funzionali che dispongono di una connessione verso il database dei messaggi",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getConnessioneAllocateDBManagerOPmethod2
			= new MBeanOperationInfo(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_DB_MANAGER_METHOD2,"Moduli funzionali che dispongono di una connessione verso il database dei messaggi",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getConnessioneAllocateQueueManagerOP 
			= new MBeanOperationInfo(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_QUEUE_MANAGER,"Moduli funzionali che dispongono di una connessione verso il Broker JMS",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getConnessioneAllocateQueueManagerOPmethod2 
			= new MBeanOperationInfo(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_QUEUE_MANAGER_METHOD2,"Moduli funzionali che dispongono di una connessione verso il Broker JMS",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getTransazioniAttiveOP 
		= new MBeanOperationInfo(TRANSAZIONI_ATTIVE_ID,"Transazioni attive",
			null,
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getTransazioniAttiveOPmethod2 
		= new MBeanOperationInfo(TRANSAZIONI_ATTIVE_ID_METHOD2,"Transazioni attive",
			null,
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getDettaglioTransazioneAttiva 
		= new MBeanOperationInfo(GET_DETTAGLIO_TRANSAZIONE_ATTIVA,"Recupera i dettagli della transazione con id fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("idTransazione",String.class.getName(),"Identificativo della transazione"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getTransazioniIdProtocolloAttiviOP 
			= new MBeanOperationInfo(MonitoraggioRisorse.TRANSAZIONI_ATTIVE_ID_PROTOCOLLO,"Identificativi di Protocollo attivi sulla Porta",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getTransazioniIdProtocolloAttiviOPmethod2  
			= new MBeanOperationInfo(MonitoraggioRisorse.TRANSAZIONI_ATTIVE_ID_PROTOCOLLO_METHOD2,"Identificativi di Protocollo attivi sulla Porta",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
				
		// MetaData per l'operazione 
		MBeanOperationInfo getActiveConnectionsPD 
			= new MBeanOperationInfo(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_CONNETTORI_PD,"Connessioni attive su cui e' in corso un inoltro di busta (PortaDelegata)",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getActiveConnectionsPDmethod2   
			= new MBeanOperationInfo(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_CONNETTORI_PD_METHOD2,"Connessioni attive su cui e' in corso un inoltro di busta (PortaDelegata)",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getActiveConnectionsPA 
			= new MBeanOperationInfo(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_CONNETTORI_PA,"Connessioni attive su cui e' in corso una consegna di contenuti applicativi (PortaApplicativa)",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getActiveConnectionsPAmethod2    
			= new MBeanOperationInfo(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_CONNETTORI_PA_METHOD2,"Connessioni attive su cui e' in corso una consegna di contenuti applicativi (PortaApplicativa)",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getRisorseSistemaOP 
			= new MBeanOperationInfo(MonitoraggioRisorse.GET_STATO_RISORSE_DI_SISTEMA,"Ritorna lo stato delle risorse di sistema",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getRisorseSistemaOPmethod2   
			= new MBeanOperationInfo(MonitoraggioRisorse.GET_STATO_RISORSE_DI_SISTEMA_METHOD2,"Ritorna lo stato delle risorse di sistema",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getRisorseCacheOP 
			= new MBeanOperationInfo(MonitoraggioRisorse.GET_STATO_CACHES,"Ritorna lo stato delle cache utilizzate dalla Porta",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getRisorseCacheOPmethod2  
			= new MBeanOperationInfo(MonitoraggioRisorse.GET_STATO_CACHES_METHOD2,"Ritorna lo stato delle cache utilizzate dalla Porta",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		
		// MetaData per l'operazione 
		MBeanOperationInfo getBIOHttpClientConnectionManagerStatusOP 
			= new MBeanOperationInfo(MonitoraggioRisorse.BIO_HTTP_CLIENT_CONNECTION_MANAGER_STATUS,"Ritorna lo stato del connection manager http per le connessioni BIO",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getBIOHttpClientConnectionManagerStatusOPmethod2  
			= new MBeanOperationInfo(MonitoraggioRisorse.BIO_HTTP_CLIENT_CONNECTION_MANAGER_STATUS_METHOD2,"Ritorna lo stato del connection manager http per le connessioni BIO",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo restartBIOHttpClientConnectionManagerOPmethod  
			= new MBeanOperationInfo(MonitoraggioRisorse.BIO_HTTP_CLIENT_CONNECTION_MANAGER_RESTART,"Effettua il riavvio del connection manager http per le connessioni BIO",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getBIOWorkerThreadPoolStatusOP 
			= new MBeanOperationInfo(MonitoraggioRisorse.BIO_WORKER_THREAD_POOL_STATUS,"Ritorna lo stato del pool di thread utilizzata per inviare la richiesta in streaming tramite connettore HTTP BIO",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getBIOWorkerThreadPoolStatusOPmethod2  
			= new MBeanOperationInfo(MonitoraggioRisorse.BIO_WORKER_THREAD_POOL_STATUS_METHOD2,"Ritorna lo stato del pool di thread utilizzata per inviare la richiesta in streaming tramite connettore HTTP BIO",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		
		
		// MetaData per l'operazione 
		MBeanOperationInfo getNIOHttpClientConnectionManagerStatusOP 
			= new MBeanOperationInfo(MonitoraggioRisorse.NIO_HTTP_CLIENT_CONNECTION_MANAGER_STATUS,"Ritorna lo stato del connection manager http per le connessioni NIO",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getNIOHttpClientConnectionManagerStatusOPmethod2  
			= new MBeanOperationInfo(MonitoraggioRisorse.NIO_HTTP_CLIENT_CONNECTION_MANAGER_STATUS_METHOD2,"Ritorna lo stato del connection manager http per le connessioni NIO",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo restartNIOHttpClientConnectionManagerOPmethod  
			= new MBeanOperationInfo(MonitoraggioRisorse.NIO_HTTP_CLIENT_CONNECTION_MANAGER_RESTART,"Effettua il riavvio del connection manager http per le connessioni NIO",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getNIOWorkerThreadPoolStatusOP 
			= new MBeanOperationInfo(MonitoraggioRisorse.NIO_WORKER_THREAD_POOL_STATUS,"Ritorna lo stato del pool di thread utilizzata per inviare la richiesta in streaming tramite connettore HTTP NIO",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getNIOWorkerThreadPoolStatusOPmethod2  
			= new MBeanOperationInfo(MonitoraggioRisorse.NIO_WORKER_THREAD_POOL_STATUS_METHOD2,"Ritorna lo stato del pool di thread utilizzata per inviare la richiesta in streaming tramite connettore HTTP NIO",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);		
		
		// MetaData per l'operazione 
		MBeanOperationInfo getNIOHttpClientConnectionManagerIOThreadCountOP 
			= new MBeanOperationInfo(MonitoraggioRisorse.NIO_HTTP_CLIENT_CONNECTION_MANAGER_IO_THREAD_COUNT,"Ritorna il numero thread dedicati per IORactor del connettore HTTP NIO",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getNIOHttpClientConnectionManagerIOThreadCountOPmethod2  
			= new MBeanOperationInfo(MonitoraggioRisorse.NIO_HTTP_CLIENT_CONNECTION_MANAGER_IO_THREAD_COUNT_METHOD2,"Ritorna il numero thread dedicati per IORactor del connettore HTTP NIO",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);		

		// Mbean costruttore
		MBeanConstructorInfo defaultConstructor = new MBeanConstructorInfo("Default Constructor","Crea e inizializza una nuova istanza del MBean",null);

		// Lista attributi
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[]{monitoraggioPdDVAR,risorseDisponibiliVAR,
				numMsgInConsegnaVAR,tempoMedioAttesaInConsegnaVAR,tempoMaxAttesaInConsegnaVAR,
				numMsgInSpedizioneVAR,tempoMedioAttesaInSpedizioneVAR,tempoMaxAttesaInSpedizioneVAR,
				numMsgInProcessamentoVAR,tempoMedioAttesaInProcessamentoVAR,tempoMaxAttesaInProcessamentoVAR,
				totMessaggiVAR,tempoMedioAttesaVAR,tempoMaxAttesaVAR,totMessaggiDuplicatiVAR};
		
		// Lista Costruttori
		MBeanConstructorInfo[] constructors = new MBeanConstructorInfo[]{defaultConstructor};
		
		// Lista operazioni
		MBeanOperationInfo[] operations = new MBeanOperationInfo[]{getRisorseSistemaOP,getRisorseSistemaOPmethod2,getRisorseCacheOP,getRisorseCacheOPmethod2,
				getConnessioneAllocateDBManagerOP,getConnessioneAllocateDBManagerOPmethod2,getConnessioneAllocateQueueManagerOP,getConnessioneAllocateQueueManagerOPmethod2,
				getTransazioniAttiveOP,getTransazioniAttiveOPmethod2, getDettaglioTransazioneAttiva, getTransazioniIdProtocolloAttiviOP,getTransazioniIdProtocolloAttiviOPmethod2,
				getActiveConnectionsPD,getActiveConnectionsPDmethod2,getActiveConnectionsPA,getActiveConnectionsPAmethod2,
				getBIOHttpClientConnectionManagerStatusOP, getBIOHttpClientConnectionManagerStatusOPmethod2,
				restartBIOHttpClientConnectionManagerOPmethod,
				getBIOWorkerThreadPoolStatusOP,getBIOWorkerThreadPoolStatusOPmethod2,
				getNIOHttpClientConnectionManagerStatusOP, getNIOHttpClientConnectionManagerStatusOPmethod2,
				restartNIOHttpClientConnectionManagerOPmethod,
				getNIOHttpClientConnectionManagerIOThreadCountOP, getNIOHttpClientConnectionManagerIOThreadCountOPmethod2,
				getNIOWorkerThreadPoolStatusOP,getNIOWorkerThreadPoolStatusOPmethod2};
		
		return new MBeanInfo(className,description,attributes,constructors,operations,null);
	}
	
	
	/* Variabili per la gestione JMX */
	private Logger log;

	/* Costruttore */
	public MonitoraggioRisorse(){
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
	}
	
	public static final String MESSAGGIO_RISORSE_ADEGUATE = "GovWay possiede le adeguate risorse di sistema";
	
	public void refreshDatiMonitoraggioPdD(){

		StringBuilder bf = new StringBuilder();
		if( !TimerMonitoraggioRisorseThread.isRisorseDisponibili()){
			bf.append("Risorse di sistema non disponibili: "+TimerMonitoraggioRisorseThread.getRisorsaNonDisponibile().getMessage());
		}
		if( !TimerThresholdThread.freeSpace){
			if(bf.length()>0){
				bf.append("\n");
			}
			bf.append("[ThresholdCheck] Non sono disponibili abbastanza risorse per la gestione delle richieste");
		}
		if( !Tracciamento.tracciamentoDisponibile){
			if(bf.length()>0){
				bf.append("\n");
			}
			bf.append("Tracciamento non disponibile: "+Tracciamento.motivoMalfunzionamentoTracciamento.getMessage());
		}
		if( !MsgDiagnostico.gestoreDiagnosticaDisponibile){
			if(bf.length()>0){
				bf.append("\n");
			}
			bf.append("Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage());
		}
		if( !Dump.isSistemaDumpDisponibile()){
			if(bf.length()>0){
				bf.append("\n");
			}
			bf.append("Sistema di dump dei contenuti applicativi non disponibile: "+Dump.getMotivoMalfunzionamentoDump().getMessage());
		}
		if(bf.length()==0){
			bf.append(MonitoraggioRisorse.MESSAGGIO_RISORSE_ADEGUATE);
		}
		this.risorseDiSistema = bf.toString();
		
		try{
			OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
			if(properties.getDatabaseType()!=null){
				DriverMonitoraggio driver = new DriverMonitoraggio(properties.getJNDIName_DataSource(),
						properties.getDatabaseType(),properties.getJNDIContext_DataSource(),this.log);
				StatoPdd stato = driver.getStatoRichiestePendenti(new FilterSearch());
				this.numMsgInConsegna = stato.getNumMsgInConsegna();
				this.numMsgInSpedizione = stato.getNumMsgInSpedizione();
				this.numMsgInProcessamento = stato.getNumMsgInProcessamento();
				this.totMessaggi = stato.getTotMessaggi();
				this.totMessaggiDuplicati = stato.getTotMessaggiDuplicati();
				if(stato.getTempoMaxAttesa()>0)
					this.tempoMaxAttesa = Utilities.convertSystemTimeIntoStringMillisecondi(stato.getTempoMaxAttesa()*1000, false);
				if(stato.getTempoMaxAttesaInConsegna()>0)
					this.tempoMaxAttesaInConsegna = Utilities.convertSystemTimeIntoStringMillisecondi(stato.getTempoMaxAttesaInConsegna()*1000, false);
				if(stato.getTempoMaxAttesaInSpedizione()>0)
					this.tempoMaxAttesaInSpedizione = Utilities.convertSystemTimeIntoStringMillisecondi(stato.getTempoMaxAttesaInSpedizione()*1000, false);
				if(stato.getTempoMaxAttesaInProcessamento()>0)
					this.tempoMaxAttesaInProcessamento = Utilities.convertSystemTimeIntoStringMillisecondi(stato.getTempoMaxAttesaInProcessamento()*1000, false);
				if(stato.getTempoMedioAttesa()>0)
					this.tempoMedioAttesa = Utilities.convertSystemTimeIntoStringMillisecondi(stato.getTempoMedioAttesa()*1000, false);
				if(stato.getTempoMedioAttesaInConsegna()>0)
					this.tempoMedioAttesaInConsegna = Utilities.convertSystemTimeIntoStringMillisecondi(stato.getTempoMedioAttesaInConsegna()*1000, false);
				if(stato.getTempoMedioAttesaInSpedizione()>0)
					this.tempoMedioAttesaInSpedizione = Utilities.convertSystemTimeIntoStringMillisecondi(stato.getTempoMedioAttesaInSpedizione()*1000, false);
				if(stato.getTempoMedioAttesaInProcessamento()>0)
					this.tempoMedioAttesaInProcessamento = Utilities.convertSystemTimeIntoStringMillisecondi(stato.getTempoMedioAttesaInProcessamento()*1000, false);
			
				this.datiMonitoraggioPdD = "Aggiornati al "+DateManager.getDate().toString();
			}else{
				this.datiMonitoraggioPdD = "Non disponibili: e' necessario indicare il tipo di database in govway.properties";
				throw new CoreException("Per il monitoraggio della porta di dominio e' necessario indicare il tipo di database in govway.properties");
			}
		}catch(Throwable e){
			this.log.error("DriverMonitoraggio non inizializzato",e);
			this.datiMonitoraggioPdD = "Non disponibili: "+e.getMessage();
		}
			
	
	}
	
	/* Metodi di management JMX */
	
	public String getStatoCaches(){
		try{
			return Cache.printStatistics("\n","\n-----------------------------------------------------\n");
		}catch(Exception e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public static final String MSG_NESSUNA_CONNESSIONE_ALLOCATA = "Nessuna connessione allocata";
	public static final String MSG_CONNESSIONI_ALLOCATE = " risorse allocate: ";
	public static final String MSG_CONNESSIONI_ALLOCATE_TRANSAZIONI = " risorse allocate per la gestione delle transazioni: ";
	public static final String MSG_CONNESSIONI_ALLOCATE_STATISTICHE = " risorse allocate per la generazione delle statistiche: ";
	public static final String MSG_CONNESSIONI_ALLOCATE_CONSEGNE_PRESE_IN_CARICO_SMISTATORE = " risorse allocate per lo smistatore dei messaggi presi in carico: ";
	public static final String MSG_CONNESSIONI_ALLOCATE_CONSEGNE_PRESE_IN_CARICO_RUNTIME = " risorse allocate per il gestore runtime dei messaggi presi in carico: ";
	public static final String MSG_CONNESSIONI_ALLOCATE_CONSEGNE_PRESE_IN_CARICO_TRANSAZIONI = " risorse allocate per il gestore delle tracce relative ai messaggi presi in carico: ";
	public static final String MSG_CONNESSIONI_ALLOCATE_CONSEGNE_MESSAGE_BOX_RUNTIME = " risorse allocate per il gestore runtime del servizio MessageBox: ";
	public static final String MSG_CONNESSIONI_ALLOCATE_CONSEGNE_MESSAGE_BOX_TRANSAZIONI = " risorse allocate per il gestore delle tracce del servizio MessageBox: ";
	public static final String MSG_CONNESSIONI_HTTP_ALLOCATE = " connessioni allocate: ";
	
	public static final String MSG_NESSUNA_TRANSAZIONE_ATTIVA = "Nessuna transazione attiva";
	public static final String MSG_TRANSAZIONI_ATTIVE = " transazioni attive: ";
	public static final String MSG_TRANSAZIONI_ATTIVE_ID_PROTOCOLLO = " id di protocollo attivi: ";
	
	public static final String MSG_NESSUN_POOL_THREAD_ATTIVO = "Nessun pool di thread attivo";
	public static final String MSG_NESSUN_THREAD_ATTIVO = "Nessun thread attivo";
	
	public static final String MSG_FUNZIONALITA_DISABILITATA = "Funzionalità disabilitata";
	
	public String getUsedDBConnections(){
		String[] risorse = null;
		String[] risorseTransaction = null;
		String[] risorseStatistiche = null;
		String[] risorseConsegnePreseInCaricoSmistatore = null;
		String[] risorseConsegnePreseInCaricoRuntime = null;
		String[] risorseConsegnePreseInCaricoTransazioni = null;
		String[] risorseConsegneMessageBoxRuntime = null;
		String[] risorseConsegneMessageBoxTransazioni = null;
		try{
			risorse = DBManager.getStatoRisorse();
			
			OpenSPCoop2Properties prop = OpenSPCoop2Properties.getInstance();
			
			boolean useRuntimePdDTransazioni = DBTransazioniManager.getInstance().useRuntimePdD();
			if(!useRuntimePdDTransazioni &&
				!prop.isTransazioniDatasourceUseDBUtils()) {
				// le aggiungo altrimenti non c'è altro modo per vederle
				risorseTransaction = DBTransazioniManager.getStatoRisorse();
			}
			
			if(prop.isStatisticheGenerazioneEnabled()) {
				boolean useRuntimePdDStatistiche = DBStatisticheManager.getInstance().useRuntimePdD();
				boolean useTransazioniStatistiche = DBStatisticheManager.getInstance().useTransazioni();
				if(!useRuntimePdDStatistiche && !useTransazioniStatistiche &&
					!prop.isStatisticheDatasourceUseDBUtils()) {
					// le aggiungo altrimenti non c'è altro modo per vederle
					risorseStatistiche = DBStatisticheManager.getStatoRisorse();
				}
			}
			
			if(prop.isServerJ2EE()!=null && !prop.isServerJ2EE() &&
				prop.isTimerConsegnaContenutiApplicativiAbilitato()){
				boolean useDefaultManager = DBConsegnePreseInCaricoManager.getInstanceSmistatore().useDefaultManager();
				if(!useDefaultManager &&
					!prop.isTimerConsegnaContenutiApplicativi_smistatore_runtime_dataSource_useDBUtils()) {
					// le aggiungo altrimenti non c'è altro modo per vederle
					risorseConsegnePreseInCaricoSmistatore = DBConsegnePreseInCaricoManager.getStatoRisorseSmistatore();
				}
				
				useDefaultManager = DBConsegnePreseInCaricoManager.getInstanceRuntime().useDefaultManager();
				if(!useDefaultManager &&
					!prop.isTimerConsegnaContenutiApplicativi_runtime_dataSource_useDBUtils()) {
					// le aggiungo altrimenti non c'è altro modo per vederle
					risorseConsegnePreseInCaricoRuntime = DBConsegnePreseInCaricoManager.getStatoRisorseRuntime();
				}
				
				useDefaultManager = DBConsegnePreseInCaricoManager.getInstanceTransazioni().useDefaultManager();
				if(!useDefaultManager &&
					!prop.isTimerConsegnaContenutiApplicativi_transazioni_dataSource_useDBUtils()) {
					// le aggiungo altrimenti non c'è altro modo per vederle
					risorseConsegnePreseInCaricoTransazioni = DBConsegnePreseInCaricoManager.getStatoRisorseTransazioni();
				}
			}
			
			if(prop.isIntegrationManagerEnabled()) {
				boolean useDefaultManager = DBConsegneMessageBoxManager.getInstanceRuntime().useDefaultManager() ||  DBConsegneMessageBoxManager.getInstanceRuntime().useConsegnePreseInCaricoManager();
				if(!useDefaultManager &&
					!prop.isIntegrationManager_runtime_dataSource_useDBUtils()) {
					// le aggiungo altrimenti non c'è altro modo per vederle
					risorseConsegneMessageBoxRuntime = DBConsegneMessageBoxManager.getStatoRisorse_runtime();
				}
				
				useDefaultManager = DBConsegneMessageBoxManager.getInstanceTransazioni().useDefaultManager() ||  DBConsegneMessageBoxManager.getInstanceTransazioni().useConsegnePreseInCaricoManager();
				if(!useDefaultManager &&
					!prop.isIntegrationManager_transazioni_dataSource_useDBUtils()) {
					// le aggiungo altrimenti non c'è altro modo per vederle
					risorseConsegneMessageBoxTransazioni = DBConsegneMessageBoxManager.getStatoRisorse_transazioni();
				}
			}
			
		}catch(Exception e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}		
		return getResultUsedDBConnections(risorse, risorseTransaction, risorseStatistiche,
				risorseConsegnePreseInCaricoSmistatore, risorseConsegnePreseInCaricoRuntime, risorseConsegnePreseInCaricoTransazioni,
				risorseConsegneMessageBoxRuntime, risorseConsegneMessageBoxTransazioni);
	}
	public static String getResultUsedDBConnections(String[] risorse, String[] risorseTransaction, String[] risorseStatistiche,
			String[] risorseConsegnePreseInCaricoSmistatore, String[] risorseConsegnePreseInCaricoRuntime, String[] risorseConsegnePreseInCaricoTransazioni,
			String[] risorseConsegneMessageBoxRuntime, String[] risorseConsegneMessageBoxTransazioni) {
		
		if((risorse==null || risorse.length<=0) && 
				(risorseTransaction==null || risorseTransaction.length<=0) && 
				(risorseStatistiche==null || risorseStatistiche.length<=0)
				) {
			return MSG_NESSUNA_CONNESSIONE_ALLOCATA;
		}
			
		StringBuilder bf = new StringBuilder();
		if(risorse!=null && risorse.length>0) {
			bf.append(risorse.length+MSG_CONNESSIONI_ALLOCATE+"\n");
			for(int i=0; i<risorse.length; i++){
				bf.append(risorse[i]+"\n");
			}
		}
		if(risorseTransaction!=null && risorseTransaction.length>0) {
			bf.append(risorseTransaction.length+MSG_CONNESSIONI_ALLOCATE_TRANSAZIONI+"\n");
			for(int i=0; i<risorseTransaction.length; i++){
				bf.append(risorseTransaction[i]+"\n");
			}
		}
		if(risorseStatistiche!=null && risorseStatistiche.length>0) {
			bf.append(risorseStatistiche.length+MSG_CONNESSIONI_ALLOCATE_STATISTICHE+"\n");
			for(int i=0; i<risorseStatistiche.length; i++){
				bf.append(risorseStatistiche[i]+"\n");
			}
		}
		if(risorseConsegnePreseInCaricoSmistatore!=null && risorseConsegnePreseInCaricoSmistatore.length>0) {
			bf.append(risorseConsegnePreseInCaricoSmistatore.length+MSG_CONNESSIONI_ALLOCATE_CONSEGNE_PRESE_IN_CARICO_SMISTATORE+"\n");
			for(int i=0; i<risorseConsegnePreseInCaricoSmistatore.length; i++){
				bf.append(risorseConsegnePreseInCaricoSmistatore[i]+"\n");
			}
		}
		if(risorseConsegnePreseInCaricoRuntime!=null && risorseConsegnePreseInCaricoRuntime.length>0) {
			bf.append(risorseConsegnePreseInCaricoRuntime.length+MSG_CONNESSIONI_ALLOCATE_CONSEGNE_PRESE_IN_CARICO_RUNTIME+"\n");
			for(int i=0; i<risorseConsegnePreseInCaricoRuntime.length; i++){
				bf.append(risorseConsegnePreseInCaricoRuntime[i]+"\n");
			}
		}
		if(risorseConsegnePreseInCaricoTransazioni!=null && risorseConsegnePreseInCaricoTransazioni.length>0) {
			bf.append(risorseConsegnePreseInCaricoTransazioni.length+MSG_CONNESSIONI_ALLOCATE_CONSEGNE_PRESE_IN_CARICO_TRANSAZIONI+"\n");
			for(int i=0; i<risorseConsegnePreseInCaricoTransazioni.length; i++){
				bf.append(risorseConsegnePreseInCaricoTransazioni[i]+"\n");
			}
		}
		if(risorseConsegneMessageBoxRuntime!=null && risorseConsegneMessageBoxRuntime.length>0) {
			bf.append(risorseConsegneMessageBoxRuntime.length+MSG_CONNESSIONI_ALLOCATE_CONSEGNE_MESSAGE_BOX_RUNTIME+"\n");
			for(int i=0; i<risorseConsegneMessageBoxRuntime.length; i++){
				bf.append(risorseConsegneMessageBoxRuntime[i]+"\n");
			}
		}
		if(risorseConsegneMessageBoxTransazioni!=null && risorseConsegneMessageBoxTransazioni.length>0) {
			bf.append(risorseConsegneMessageBoxTransazioni.length+MSG_CONNESSIONI_ALLOCATE_CONSEGNE_MESSAGE_BOX_TRANSAZIONI+"\n");
			for(int i=0; i<risorseConsegneMessageBoxTransazioni.length; i++){
				bf.append(risorseConsegneMessageBoxTransazioni[i]+"\n");
			}
		}
		
		return bf.toString();
	}
	
	public String getUsedQueueConnections(){
		String[] risorse = null;
		try{
			risorse = QueueManager.getStatoRisorse();
		}catch(Exception e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
		return getResultUsedQueueConnections(risorse);
	}
	public static String getResultUsedQueueConnections(String[] risorse) {
		if(risorse==null || risorse.length<=0)
			return MSG_NESSUNA_CONNESSIONE_ALLOCATA;
		
		StringBuilder bf = new StringBuilder();
		bf.append(risorse.length+MSG_CONNESSIONI_ALLOCATE+"\n");
		for(int i=0; i<risorse.length; i++){
			bf.append(risorse[i]+"\n");
		}
		return bf.toString();
	}
	
	public String getTransazioniAttiveId() {
		List<String> risorse = null;
		try{
			risorse = TransactionContext.getTransactionKeys();
		}catch(Exception e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
		return getResultTransazioniAttiveId(risorse);
	}
	public static String getResultTransazioniAttiveId(List<String> risorse) {
		if(risorse==null || risorse.isEmpty())
			return MSG_NESSUNA_TRANSAZIONE_ATTIVA;
		
		StringBuilder bf = new StringBuilder();
		bf.append(risorse.size()+MSG_TRANSAZIONI_ATTIVE+"\n");
		for(int i=0; i<risorse.size(); i++){
			bf.append(risorse.get(i)+"\n");
		}
		return bf.toString();
	}
	
	public String getDettaglioTransazioneAttivaId(String idTransazione) {
		
		try{
			Transaction tr = TransactionContext.getTransaction(idTransazione);
			if(tr!=null) {
				return tr.toString();
			}
			throw new TransactionNotExistsException();
		}
		catch(TransactionNotExistsException notFound) {
			return "Transazione non esistente";
		}
		catch(Exception e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
		
	}
	
	public String getTransazioniAttiveIdProtocollo() {
		List<String> risorse = null;
		try{
			risorse = TransactionContext.getIdBustaKeys();
		}catch(Exception e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
		return getResultTransazioniAttiveIdProtocollo(risorse);
	}
	public static String getResultTransazioniAttiveIdProtocollo(List<String> risorse) {
		if(risorse==null || risorse.isEmpty())
			return MSG_NESSUNA_TRANSAZIONE_ATTIVA;
		
		StringBuilder bf = new StringBuilder();
		bf.append(risorse.size()+MSG_TRANSAZIONI_ATTIVE_ID_PROTOCOLLO+"\n");
		for(int i=0; i<risorse.size(); i++){
			bf.append(risorse.get(i)+"\n");
		}
		return bf.toString();
	}
	
	public String getActivePDConnections(){	
		Map<String, IConnettore> connettoriPD = RepositoryConnettori.getConnettori_pd();		
		return getActiveConnections(connettoriPD);
	}
	public String getActivePAConnections(){	
		Map<String, IConnettore> connettoriPA = RepositoryConnettori.getConnettori_pa();		
		return getActiveConnections(connettoriPA);
	}
	private String getActiveConnections(Map<String, IConnettore> connettoriParam){
		
		List<String> cConnettori = new ArrayList<>();
		for (Map.Entry<String,IConnettore> entry : connettoriParam.entrySet()) {
			String id = entry.getKey();
			IConnettore c = connettoriParam.get(id);
			String location = null;
			try{
				location = c.getLocation();
			}catch(Exception e){
				location = "ERRORE: "+e.getMessage();
			}
			cConnettori.add((id+" -> ["+location+"]"));
		}
		return getResultActiveConnections(cConnettori);
		
	}
	public static String getResultActiveConnections(List<String> cConnettori) {
		if(cConnettori==null || cConnettori.isEmpty())
			return MSG_NESSUNA_CONNESSIONE_ALLOCATA;
		
		StringBuilder bf = new StringBuilder();
		bf.append(cConnettori.size()+MSG_CONNESSIONI_HTTP_ALLOCATE+"\n");
		for(int i=0; i<cConnettori.size(); i++){
			bf.append(cConnettori.get(i)+"\n");
		}
		return bf.toString();
	}

	
	
	
	public String getBIOHttpClientConnectionManagerStatus(){
		String stato = null;
		try{
			stato = org.openspcoop2.pdd.core.connettori.httpcore5.ConnettoreHTTPCOREConnectionManager.getHttpClientConnectionManagerStatus();
			if(stato==null || StringUtils.isEmpty(stato)) {
				return MSG_NESSUNA_CONNESSIONE_ALLOCATA;
			}
		}catch(Exception e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
		return stato;
	}
	public String restartBIOHttpClientConnectionManager(){
		try{
			org.openspcoop2.pdd.core.connettori.httpcore5.ConnettoreHTTPCOREConnectionManager.restartConnectionManager();
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Exception e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	public String getBIOWorkerThreadPoolStatus(){
		String stato = null;
		try{
			if(ConnectorApplicativeThreadPool.isSyncRequestPoolThreadsEnabled()) {
				stato = ConnectorApplicativeThreadPool.getSyncRequestPoolThreadsImage();
				if(stato==null || StringUtils.isEmpty(stato)) {
					return MSG_NESSUN_POOL_THREAD_ATTIVO;
				}
			}
			else {
				stato = MSG_FUNZIONALITA_DISABILITATA;
			}
		}catch(Exception e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
		return stato;
	}
	
	public String getNIOHttpClientConnectionManagerStatus(){
		String stato = null;
		try{
			OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
			if(op2Properties.isNIOEnabled()) {
				stato = org.openspcoop2.pdd.core.connettori.httpcore5.nio.ConnettoreHTTPCOREConnectionManager.getHttpClientConnectionManagerStatus();
				if(stato==null || StringUtils.isEmpty(stato)) {
					return MSG_NESSUNA_CONNESSIONE_ALLOCATA;
				}
			}
			else {
				stato = MSG_FUNZIONALITA_DISABILITATA;
			}
		}catch(Exception e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
		return stato;
	}
	public String restartNIOHttpClientConnectionManager(){
		try{
			org.openspcoop2.pdd.core.connettori.httpcore5.nio.ConnettoreHTTPCOREConnectionManager.restartConnectionManager();
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Exception e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	public String getNIOWorkerThreadPoolStatus(){
		String stato = null;
		try{
			if(ConnectorApplicativeThreadPool.isAsyncPoolThreadsEnabled()) {
				stato = ConnectorApplicativeThreadPool.getAsyncPoolThreadsImage();
				if(stato==null || StringUtils.isEmpty(stato)) {
					return MSG_NESSUN_POOL_THREAD_ATTIVO;
				}
			}
			else {
				stato = MSG_FUNZIONALITA_DISABILITATA;
			}
		}catch(Exception e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
		return stato;
	}
	public String getNIOHttpClientConnectionManagerIOThreadCount(){
		String stato = null;
		try{
			OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
			if(op2Properties.isNIOEnabled()) {
				stato = org.openspcoop2.pdd.core.connettori.httpcore5.nio.ConnettoreHTTPCOREConnectionManager.getNIOHttpClientConnectionManagerIOThreadCount();
				if(stato==null || StringUtils.isEmpty(stato)) {
					return MSG_NESSUN_THREAD_ATTIVO;
				}
			}
			else {
				stato = MSG_FUNZIONALITA_DISABILITATA;
			}
		}catch(Exception e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
		return stato;
	}
	
}
