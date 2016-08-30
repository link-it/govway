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

import java.util.Enumeration;
import java.util.Hashtable;
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
import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.QueueManager;
import org.openspcoop2.pdd.core.connettori.IConnettore;
import org.openspcoop2.pdd.core.connettori.RepositoryConnettori;
import org.openspcoop2.pdd.logger.Dump;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.Tracciamento;
import org.openspcoop2.pdd.monitor.StatoPdd;
import org.openspcoop2.pdd.monitor.driver.DriverMonitoraggio;
import org.openspcoop2.pdd.monitor.driver.FilterSearch;
import org.openspcoop2.pdd.timers.TimerMonitoraggioRisorse;
import org.openspcoop2.pdd.timers.TimerThreshold;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.date.DateManager;




/**
 * Implementazione JMX per la gestione delle risorse
 *   
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MonitoraggioRisorse extends NotificationBroadcasterSupport implements DynamicMBean {

	/** Nomi attributi */
	public final static String NUMERO_MSG_IN_CONSEGNA = "numMsgInConsegna";
	public final static String TEMPO_MEDIO_ATTESA_IN_CONSEGNA = "tempoMedioAttesaInConsegna";
	public final static String TEMPO_MAX_ATTESA_IN_CONSEGNA = "tempoMaxAttesaInConsegna";
	public final static String NUMERO_MSG_IN_SPEDIZIONE = "numMsgInSpedizione";
	public final static String TEMPO_MEDIO_ATTESA_IN_SPEDIZIONE = "tempoMedioAttesaInSpedizione";
	public final static String TEMPO_MAX_ATTESA_IN_SPEDIZIONE = "tempoMaxAttesaInSpedizione";
	public final static String NUMERO_MSG_IN_PROCESSAMENTO = "numMsgInProcessamento";
	public final static String TEMPO_MEDIO_ATTESA_IN_PROCESSAMENTO = "tempoMedioAttesaInProcessamento";
	public final static String TEMPO_MAX_ATTESA_IN_PROCESSAMENTO = "tempoMaxAttesaInProcessamento";
	public final static String NUMERO_MSG_TOTALI = "totMessaggi";
	public final static String TEMPO_MEDIO_ATTESA_MSG_TOTALI = "tempoMedioAttesa";
	public final static String TEMPO_MAX_ATTESA_MSG_TOTALI = "tempoMaxAttesa";
	public final static String TOT_MSG_DUPLICATI = "totMessaggiDuplicati";
	public final static String STATO_DATI_MONITORAGGIO = "statoDatiMonitoraggioPdD";
	public final static String RISORSE_DI_SISTEMA = "risorseDiSistema";
	
	/** Nomi metodi */
	public final static String GET_STATO_RISORSE_DI_SISTEMA = "getStatoRisorseSistema";
	public final static String GET_STATO_CACHES = "getStatoCache";
	public final static String CONNESSIONI_ALLOCATE_DB_MANAGER = "getUsedDBConnections";
	public final static String CONNESSIONI_ALLOCATE_QUEUE_MANAGER = "getUsedQueueConnections";
	public final static String CONNESSIONI_ALLOCATE_CONNETTORI_PD = "getActivePDConnections";
	public final static String CONNESSIONI_ALLOCATE_CONNETTORI_PA = "getActivePAConnections";
	
	
	
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
			}catch(JMException ex){}
		}
		
		return ret;
		
	}
	
	/** invoke */
	@Override
	public Object invoke(String actionName, Object[]params, String[]signature) throws MBeanException,ReflectionException{
		
		if( (actionName==null) || (actionName.equals("")) )
			throw new IllegalArgumentException("Nessuna operazione definita");
		
		if(actionName.equals(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_DB_MANAGER)){
			return this.getUsedDBConnections();
		}
		
		else if(actionName.equals(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_QUEUE_MANAGER)){
			return this.getUsedQueueConnections();
		}
		
		else if(actionName.equals(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_CONNETTORI_PD)){
			return this.getActivePDConnections();
		}
		
		else if(actionName.equals(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_CONNETTORI_PA)){
			return this.getActivePAConnections();
		}
		
		else if(actionName.equals(MonitoraggioRisorse.GET_STATO_RISORSE_DI_SISTEMA)){
			this.refreshDatiMonitoraggioPdD();
			return this.risorseDiSistema;
		}
		
		else if(actionName.equals(MonitoraggioRisorse.GET_STATO_CACHES)){
			return this.getStatoCaches();
		}
		
		throw new UnsupportedOperationException("Operazione "+actionName+" sconosciuta");
	}
	
	/* MBean info */
	@Override
	public MBeanInfo getMBeanInfo(){
		
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
		String description = "Monitoraggio delle risorse utilizzate dalla Porta di Dominio "+OpenSPCoop2Properties.getInstance().getVersione()+", premi pulsante (apply changes) per aggiornare i dati";

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
		MBeanOperationInfo getConnessioneAllocateQueueManagerOP 
			= new MBeanOperationInfo(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_QUEUE_MANAGER,"Moduli funzionali che dispongono di una connessione verso il Broker JMS",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getActiveConnectionsPD_OP 
			= new MBeanOperationInfo(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_CONNETTORI_PD,"Connessioni attive su cui e' in corso un inoltro di busta (PortaDelegata)",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getActiveConnectionsPA_OP 
			= new MBeanOperationInfo(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_CONNETTORI_PA,"Connessioni attive su cui e' in corso una consegna di contenuti applicativi (PortaApplicativa)",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getRisorseSistema_OP 
			= new MBeanOperationInfo(MonitoraggioRisorse.GET_STATO_RISORSE_DI_SISTEMA,"Ritorna lo stato delle risorse di sistema",
					null,
					String.class.getName(),
					MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione 
		MBeanOperationInfo getRisorseCache_OP 
			= new MBeanOperationInfo(MonitoraggioRisorse.GET_STATO_CACHES,"Ritorna lo stato delle cache utilizzate dalla Porta",
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
		MBeanOperationInfo[] operations = new MBeanOperationInfo[]{getRisorseSistema_OP,getRisorseCache_OP,
				getConnessioneAllocateDBManagerOP,getConnessioneAllocateQueueManagerOP,
				getActiveConnectionsPD_OP,getActiveConnectionsPA_OP};
		
		return new MBeanInfo(className,description,attributes,constructors,operations,null);
	}
	
	
	/* Variabili per la gestione JMX */
	private Logger log;

	/* Costruttore */
	public MonitoraggioRisorse(){
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
	}
	
	public static final String MESSAGGIO_RISORSE_ADEGUATE = "La Porta di Dominio possiede le adeguate risorse di sistema";
	
	public void refreshDatiMonitoraggioPdD(){

		StringBuffer bf = new StringBuffer();
		if( TimerMonitoraggioRisorse.risorseDisponibili == false){
			bf.append("Risorse di sistema non disponibili: "+TimerMonitoraggioRisorse.risorsaNonDisponibile.getMessage());
		}
		if( TimerThreshold.freeSpace == false){
			if(bf.length()>0){
				bf.append("\n");
			}
			bf.append("[ThresholdCheck] Non sono disponibili abbastanza risorse per la gestione delle richieste");
		}
		if( Tracciamento.tracciamentoDisponibile == false){
			if(bf.length()>0){
				bf.append("\n");
			}
			bf.append("Tracciamento non disponibile: "+Tracciamento.motivoMalfunzionamentoTracciamento.getMessage());
		}
		if( MsgDiagnostico.gestoreDiagnosticaDisponibile == false){
			if(bf.length()>0){
				bf.append("\n");
			}
			bf.append("Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage());
		}
		if( Dump.sistemaDumpDisponibile == false){
			if(bf.length()>0){
				bf.append("\n");
			}
			bf.append("Sistema di dump dei contenuti applicativi non disponibile: "+Dump.motivoMalfunzionamentoDump.getMessage());
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
					this.tempoMaxAttesa = Utilities.convertSystemTimeIntoString_millisecondi(stato.getTempoMaxAttesa()*1000, false);
				if(stato.getTempoMaxAttesaInConsegna()>0)
					this.tempoMaxAttesaInConsegna = Utilities.convertSystemTimeIntoString_millisecondi(stato.getTempoMaxAttesaInConsegna()*1000, false);
				if(stato.getTempoMaxAttesaInSpedizione()>0)
					this.tempoMaxAttesaInSpedizione = Utilities.convertSystemTimeIntoString_millisecondi(stato.getTempoMaxAttesaInSpedizione()*1000, false);
				if(stato.getTempoMaxAttesaInProcessamento()>0)
					this.tempoMaxAttesaInProcessamento = Utilities.convertSystemTimeIntoString_millisecondi(stato.getTempoMaxAttesaInProcessamento()*1000, false);
				if(stato.getTempoMedioAttesa()>0)
					this.tempoMedioAttesa = Utilities.convertSystemTimeIntoString_millisecondi(stato.getTempoMedioAttesa()*1000, false);
				if(stato.getTempoMedioAttesaInConsegna()>0)
					this.tempoMedioAttesaInConsegna = Utilities.convertSystemTimeIntoString_millisecondi(stato.getTempoMedioAttesaInConsegna()*1000, false);
				if(stato.getTempoMedioAttesaInSpedizione()>0)
					this.tempoMedioAttesaInSpedizione = Utilities.convertSystemTimeIntoString_millisecondi(stato.getTempoMedioAttesaInSpedizione()*1000, false);
				if(stato.getTempoMedioAttesaInProcessamento()>0)
					this.tempoMedioAttesaInProcessamento = Utilities.convertSystemTimeIntoString_millisecondi(stato.getTempoMedioAttesaInProcessamento()*1000, false);
			
				this.datiMonitoraggioPdD = "Aggiornati al "+DateManager.getDate().toString();
			}else{
				this.datiMonitoraggioPdD = "Non disponibili: e' necessario indicare il tipo di database in openspcoop2.properties";
				throw new Exception("Per il monitoraggio della porta di dominio e' necessario indicare il tipo di database in openspcoop2.properties");
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
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getUsedDBConnections(){
		String[] risorse = null;
		try{
			risorse = DBManager.getStatoRisorse();
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
		if(risorse==null)
			return "Nessuna connessione allocata";
		
		StringBuffer bf = new StringBuffer();
		bf.append(risorse.length+" risorse allocate: \n");
		for(int i=0; i<risorse.length; i++){
			bf.append(risorse[i]+"\n");
		}
		return bf.toString();
	}
	
	public String getUsedQueueConnections(){
		String[] risorse = null;
		try{
			risorse = QueueManager.getStatoRisorse();
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
		if(risorse==null)
			return "Nessuna connessione allocata";
		
		StringBuffer bf = new StringBuffer();
		bf.append(risorse.length+" risorse allocate: \n");
		for(int i=0; i<risorse.length; i++){
			bf.append(risorse[i]+"\n");
		}
		return bf.toString();
	}
	
	public String getActivePDConnections(){	
		Hashtable<String, IConnettore> connettori_pd = RepositoryConnettori.getConnettori_pd();		
		return getActiveConnections(connettori_pd);
	}
	public String getActivePAConnections(){	
		Hashtable<String, IConnettore> connettori_pa = RepositoryConnettori.getConnettori_pa();		
		return getActiveConnections(connettori_pa);
	}
	private String getActiveConnections(Hashtable<String, IConnettore> connettori){
		
		if(connettori==null || connettori.size()==0)
			return "Nessuna connessione allocata";
		
		StringBuffer bf = new StringBuffer();
		bf.append(connettori.size()+" connessioni allocate: \n");
		
		Enumeration<String> cs = connettori.keys();
		while(cs.hasMoreElements()){
			String id = cs.nextElement();
			IConnettore c = connettori.get(id);
			bf.append(id+" -> ["+c.getLocation()+"]\n");
		}
		return bf.toString();
	}
	
}
