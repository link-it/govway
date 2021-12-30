/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;
import org.openspcoop2.pdd.core.connettori.ConnettoreCheck;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.filetrace.FileTraceConfig;
import org.openspcoop2.pdd.timers.TimerClusterDinamicoThread;
import org.openspcoop2.pdd.timers.TimerConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.timers.TimerConsegnaContenutiApplicativiThread;
import org.openspcoop2.pdd.timers.TimerEventiThread;
import org.openspcoop2.pdd.timers.TimerFileSystemRecoveryThread;
import org.openspcoop2.pdd.timers.TimerGestoreBusteNonRiscontrate;
import org.openspcoop2.pdd.timers.TimerGestoreBusteNonRiscontrateLib;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggi;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggiLib;
import org.openspcoop2.pdd.timers.TimerGestorePuliziaMessaggiAnomali;
import org.openspcoop2.pdd.timers.TimerGestorePuliziaMessaggiAnomaliLib;
import org.openspcoop2.pdd.timers.TimerGestoreRepositoryBuste;
import org.openspcoop2.pdd.timers.TimerGestoreRepositoryBusteLib;
import org.openspcoop2.pdd.timers.TimerMonitoraggioRisorseThread;
import org.openspcoop2.pdd.timers.TimerRepositoryStatefulThread;
import org.openspcoop2.pdd.timers.TimerState;
import org.openspcoop2.pdd.timers.TimerStatisticheLib;
import org.openspcoop2.pdd.timers.TimerStatisticheThread;
import org.openspcoop2.pdd.timers.TimerThresholdThread;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.registry.CertificateCheck;
import org.openspcoop2.protocol.utils.ErroriProperties;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;


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
	public final static String DUMP_BINARIO_PD_ABILITATO = "dumpBinarioPortaDelegataAbilitato";
	public final static String DUMP_BINARIO_PA_ABILITATO = "dumpBinarioPortaApplicativaAbilitato";
	public final static String TRACCIAMENTO_APPENDER = "tracciamentoAppender";
	public final static String LOG4J_DIAGNOSTICA_ABILITATO = "log4jLogFileDiagnosticaAbilitato";
	public final static String LOG4J_OPENSPCOOP_ABILITATO = "log4jLogFileOpenSPCoopAbilitato";
	public final static String LOG4J_INTEGRATION_MANAGER_ABILITATO = "log4jLogFileIntegrationManagerAbilitato";
	public final static String LOG4J_TRACCIAMENTO_ABILITATO = "log4jLogFileTracciamentoAbilitato";
	public final static String LOG4J_DUMP_ABILITATO = "log4jLogFileDumpAbilitato";
	public final static String ERRORI_STATUS_CODE_ABILITATO = "transactionErrorStatusAbilitato";
	public final static String ERRORI_INSTANCE_ID_ABILITATO = "transactionErrorInstanceIdAbilitato";
	public final static String ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_BAD_REQUEST = "transactionErrorForceSpecificTypeInternalBadRequest";
	public final static String ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE = "transactionErrorForceSpecificTypeBadResponse";
	public final static String ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR = "transactionErrorForceSpecificTypeInternalResponseError";
	public final static String ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR= "transactionErrorForceSpecificTypeInternalError";
	public final static String ERRORI_FORCE_SPECIFIC_DETAILS = "transactionErrorForceSpecificDetails";
	public final static String ERRORI_SOAP_USE_GOVWAY_STATUS_AS_FAULT_CODE = "transactionErrorUseGovWayStatusAsSoapFaultCode";
	public final static String ERRORI_SOAP_GENERATE_HTTP_HEADER_GOVWAY_CODE = "transactionErrorGenerateHttpHeaderGovWayCode";
	
	public final static String TIMER_CONSEGNA_CONTENUTI_APPLICATIVI = "timerConsegnaContenutiApplicativi";
	public final static String TIMER_EVENTI = "timerEventi";
	public final static String TIMER_FILE_SYSTEM_RECOVERY = "timerFileSystemRecovery";
	public final static String TIMER_GESTORE_BUSTE_ONEWAY_NON_RISCONTRATE = "timerGestoreBusteOnewayNonRiscontrate";
	public final static String TIMER_GESTORE_BUSTE_ASINCRONE_NON_RISCONTRATE = "timerGestoreBusteAsincroneNonRiscontrate";
	public final static String TIMER_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_ELIMINATI = "timerGestoreMessaggiPuliziaMessaggiEliminati";
	public final static String TIMER_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_SCADUTI = "timerGestoreMessaggiPuliziaMessaggiScaduti";
	public final static String TIMER_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_NON_GESTITI = "timerGestoreMessaggiPuliziaMessaggiNonGestiti";
	public final static String TIMER_GESTORE_MESSAGGI_PULIZIA_CORRELAZIONE_APPLICATIVA = "timerGestoreMessaggiPuliziaCorrelazioneApplicativa";
	public final static String TIMER_GESTORE_MESSAGGI_VERIFICA_CONNESSIONI_ATTIVE = "timerGestoreMessaggiVerificaConnessioniAttive";
	public final static String TIMER_GESTORE_PULIZIA_MESSAGGI_ANOMALI = "timerGestorePuliziaMessaggiAnomali";
	public final static String TIMER_GESTORE_REPOSITORY_BUSTE = "timerGestoreRepositoryBuste";
	public final static String TIMER_MONITORAGGIO_RISORSE_THREAD = "timerMonitoraggioRisorseThread";
	public final static String TIMER_REPOSITORY_STATEFUL_THREAD = "timerRepositoryStatefulThread";
	public final static String TIMER_STATISTICHE_ORARIE = "timerStatisticheOrarie";
	public final static String TIMER_STATISTICHE_GIORNALIERE = "timerStatisticheGiornaliere";
	public final static String TIMER_STATISTICHE_SETTIMANALI = "timerStatisticheSettimanali";
	public final static String TIMER_STATISTICHE_MENSILI = "timerStatisticheMensili";
	public final static String TIMER_THRESHOLD_THREAD = "timerThresholdThread";
	public final static String TIMER_CLUSTER_DINAMICO = "timerClusterDinamico";
		
	/** Nomi metodi' */
	public final static String CHECK_CONNETTORE_BY_ID = "checkConnettoreById";
	public final static String CHECK_CONNETTORE_BY_NOME = "checkConnettoreByNome";
	public final static String GET_CERTIFICATI_CONNETTORE_BY_ID = "getCertificatiConnettoreById";
	public final static String GET_CERTIFICATI_CONNETTORE_BY_NOME = "getCertificatiConnettoreByNome";
	public final static String CHECK_CERTIFICATO_SERVIZIO_APPLICATIVO_BY_ID = "checkCertificatoApplicativoById";
	public final static String CHECK_CERTIFICATO_SERVIZIO_APPLICATIVO_BY_NOME = "checkCertificatoApplicativoByNome";
	public final static String CHECK_CERTIFICATO_MODI_SERVIZIO_APPLICATIVO_BY_ID = "checkCertificatoModIApplicativoById";
	public final static String CHECK_CERTIFICATO_MODI_SERVIZIO_APPLICATIVO_BY_NOME = "checkCertificatoModIApplicativoByNome";
	public final static String ABILITA_PORTA_DELEGATA = "enablePortaDelegata";
	public final static String DISABILITA_PORTA_DELEGATA = "disablePortaDelegata";
	public final static String ABILITA_PORTA_APPLICATIVA = "enablePortaApplicativa";
	public final static String DISABILITA_PORTA_APPLICATIVA = "disablePortaApplicativa";
	
	/** Attributi */
	private boolean cacheAbilitata = false;
	private String msgDiagnosticiLivelloSeverita = "";
	private String msgDiagnosticiLivelloSeveritaLog4J = "";
	private String[] msgDiagnosticiAppender = null;
	private boolean tracciamentoAbilitato = true;
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
		
		if(attributeName.equals(ConfigurazionePdD.ERRORI_STATUS_CODE_ABILITATO))
			return Costanti.TRANSACTION_ERROR_STATUS_ABILITATO;
		
		if(attributeName.equals(ConfigurazionePdD.ERRORI_INSTANCE_ID_ABILITATO))
			return Costanti.TRANSACTION_ERROR_INSTANCE_ID_ABILITATO;
		
		if(attributeName.equals(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_BAD_REQUEST))
			return ErroriProperties.isFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_BAD_REQUEST();
		
		if(attributeName.equals(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE))
			return ErroriProperties.isFORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE();
		
		if(attributeName.equals(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR))
			return ErroriProperties.isFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR();
						
		if(attributeName.equals(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR))
			return ErroriProperties.isFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR();
		
		if(attributeName.equals(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_DETAILS))
			return Costanti.TRANSACTION_FORCE_SPECIFIC_ERROR_DETAILS;

		if(attributeName.equals(ConfigurazionePdD.ERRORI_SOAP_USE_GOVWAY_STATUS_AS_FAULT_CODE))
			return Costanti.TRANSACTION_ERROR_SOAP_USE_GOVWAY_STATUS_AS_FAULT_CODE;
		
		if(attributeName.equals(ConfigurazionePdD.ERRORI_SOAP_GENERATE_HTTP_HEADER_GOVWAY_CODE))
			return Costanti.TRANSACTION_ERROR_SOAP_GENERATE_HTTP_HEADER_GOVWAY_CODE;
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_CONSEGNA_CONTENUTI_APPLICATIVI))
			return TimerConsegnaContenutiApplicativi.STATE.name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_EVENTI))
			return TimerEventiThread.STATE.name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_FILE_SYSTEM_RECOVERY))
			return TimerFileSystemRecoveryThread.STATE.name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_GESTORE_BUSTE_ONEWAY_NON_RISCONTRATE))
			return TimerGestoreBusteNonRiscontrateLib.STATE_ONEWAY.name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_GESTORE_BUSTE_ASINCRONE_NON_RISCONTRATE))
			return TimerGestoreBusteNonRiscontrateLib.STATE_ASINCRONI.name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_ELIMINATI))
			return TimerGestoreMessaggiLib.STATE_MESSAGGI_ELIMINATI.name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_SCADUTI))
			return TimerGestoreMessaggiLib.STATE_MESSAGGI_SCADUTI.name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_NON_GESTITI))
			return TimerGestoreMessaggiLib.STATE_MESSAGGI_NON_GESTITI.name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_GESTORE_MESSAGGI_PULIZIA_CORRELAZIONE_APPLICATIVA))
			return TimerGestoreMessaggiLib.STATE_CORRELAZIONE_APPLICATIVA.name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_GESTORE_MESSAGGI_VERIFICA_CONNESSIONI_ATTIVE))
			return TimerGestoreMessaggiLib.STATE_VERIFICA_CONNESSIONI_ATTIVE.name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_GESTORE_PULIZIA_MESSAGGI_ANOMALI))
			return TimerGestorePuliziaMessaggiAnomaliLib.STATE.name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_GESTORE_REPOSITORY_BUSTE))
			return TimerGestoreRepositoryBusteLib.STATE.name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_MONITORAGGIO_RISORSE_THREAD))
			return TimerMonitoraggioRisorseThread.STATE.name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_REPOSITORY_STATEFUL_THREAD))
			return TimerRepositoryStatefulThread.STATE.name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_STATISTICHE_ORARIE))
			return TimerStatisticheLib.STATE_STATISTICHE_ORARIE.name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_STATISTICHE_GIORNALIERE))
			return TimerStatisticheLib.STATE_STATISTICHE_GIORNALIERE.name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_STATISTICHE_SETTIMANALI))
			return TimerStatisticheLib.STATE_STATISTICHE_SETTIMANALI.name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_STATISTICHE_MENSILI))
			return TimerStatisticheLib.STATE_STATISTICHE_MENSILI.name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_THRESHOLD_THREAD))
			return TimerThresholdThread.STATE.name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_CLUSTER_DINAMICO))
			return TimerClusterDinamicoThread.STATE.name();
				
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
			
			else if(attribute.getName().equals(ConfigurazionePdD.DUMP_BINARIO_PD_ABILITATO))
				this.setDumpBinarioPD((Boolean) attribute.getValue());
			
			else if(attribute.getName().equals(ConfigurazionePdD.DUMP_BINARIO_PA_ABILITATO))
				this.setDumpBinarioPA((Boolean) attribute.getValue());
			
			else if(attribute.getName().equals(ConfigurazionePdD.TRACCIAMENTO_APPENDER))
				this.tracciamentoAppender = (String[]) attribute.getValue();
			
			else if(attribute.getName().equals(ConfigurazionePdD.ERRORI_STATUS_CODE_ABILITATO))
				Costanti.TRANSACTION_ERROR_STATUS_ABILITATO = (Boolean) attribute.getValue();
			
			else if(attribute.getName().equals(ConfigurazionePdD.ERRORI_INSTANCE_ID_ABILITATO))
				Costanti.TRANSACTION_ERROR_INSTANCE_ID_ABILITATO = (Boolean) attribute.getValue();
			
			else if(attribute.getName().equals(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_BAD_REQUEST))
				this.setFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_BAD_REQUEST( (Boolean) attribute.getValue() );
			
			else if(attribute.getName().equals(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE))
				this.setFORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE( (Boolean) attribute.getValue() );
			
			else if(attribute.getName().equals(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR))
				this.setFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR( (Boolean) attribute.getValue() );
						
			else if(attribute.getName().equals(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR))
				this.setFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR( (Boolean) attribute.getValue() );

			else if(attribute.getName().equals(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_DETAILS))
				Costanti.TRANSACTION_FORCE_SPECIFIC_ERROR_DETAILS = (Boolean) attribute.getValue();
			
			else if(attribute.getName().equals(ConfigurazionePdD.ERRORI_SOAP_USE_GOVWAY_STATUS_AS_FAULT_CODE))
				Costanti.TRANSACTION_ERROR_SOAP_USE_GOVWAY_STATUS_AS_FAULT_CODE = (Boolean) attribute.getValue();
			
			else if(attribute.getName().equals(ConfigurazionePdD.ERRORI_SOAP_GENERATE_HTTP_HEADER_GOVWAY_CODE))
				Costanti.TRANSACTION_ERROR_SOAP_GENERATE_HTTP_HEADER_GOVWAY_CODE = (Boolean) attribute.getValue();
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_CONSEGNA_CONTENUTI_APPLICATIVI))
				TimerConsegnaContenutiApplicativi.STATE = getTimerState(attribute.getValue());
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_EVENTI))
				TimerEventiThread.STATE = getTimerState(attribute.getValue());
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_FILE_SYSTEM_RECOVERY))
				TimerFileSystemRecoveryThread.STATE = getTimerState(attribute.getValue());
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_GESTORE_BUSTE_ONEWAY_NON_RISCONTRATE))
				TimerGestoreBusteNonRiscontrateLib.STATE_ONEWAY = getTimerState(attribute.getValue());
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_GESTORE_BUSTE_ASINCRONE_NON_RISCONTRATE))
				TimerGestoreBusteNonRiscontrateLib.STATE_ASINCRONI = getTimerState(attribute.getValue());
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_ELIMINATI))
				TimerGestoreMessaggiLib.STATE_MESSAGGI_ELIMINATI = getTimerState(attribute.getValue());
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_SCADUTI))
				TimerGestoreMessaggiLib.STATE_MESSAGGI_SCADUTI = getTimerState(attribute.getValue());
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_NON_GESTITI))
				TimerGestoreMessaggiLib.STATE_MESSAGGI_NON_GESTITI = getTimerState(attribute.getValue());
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_GESTORE_MESSAGGI_PULIZIA_CORRELAZIONE_APPLICATIVA))
				TimerGestoreMessaggiLib.STATE_CORRELAZIONE_APPLICATIVA = getTimerState(attribute.getValue());
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_GESTORE_MESSAGGI_VERIFICA_CONNESSIONI_ATTIVE))
				TimerGestoreMessaggiLib.STATE_VERIFICA_CONNESSIONI_ATTIVE = getTimerState(attribute.getValue());
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_GESTORE_PULIZIA_MESSAGGI_ANOMALI))
				TimerGestorePuliziaMessaggiAnomaliLib.STATE = getTimerState(attribute.getValue());
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_GESTORE_REPOSITORY_BUSTE))
				TimerGestoreRepositoryBusteLib.STATE = getTimerState(attribute.getValue());
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_MONITORAGGIO_RISORSE_THREAD))
				TimerMonitoraggioRisorseThread.STATE = getTimerState(attribute.getValue());
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_REPOSITORY_STATEFUL_THREAD))
				TimerRepositoryStatefulThread.STATE = getTimerState(attribute.getValue());
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_STATISTICHE_ORARIE))
				TimerStatisticheLib.STATE_STATISTICHE_ORARIE = getTimerState(attribute.getValue());
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_STATISTICHE_GIORNALIERE))
				TimerStatisticheLib.STATE_STATISTICHE_GIORNALIERE = getTimerState(attribute.getValue());
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_STATISTICHE_SETTIMANALI))
				TimerStatisticheLib.STATE_STATISTICHE_SETTIMANALI = getTimerState(attribute.getValue());
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_STATISTICHE_MENSILI))
				TimerStatisticheLib.STATE_STATISTICHE_MENSILI = getTimerState(attribute.getValue());
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_THRESHOLD_THREAD))
				TimerThresholdThread.STATE = getTimerState(attribute.getValue());
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_CLUSTER_DINAMICO))
				TimerClusterDinamicoThread.STATE = getTimerState(attribute.getValue());
			
			else
				throw new AttributeNotFoundException("Attributo "+attribute.getName()+" non trovato");
			
		}catch(ClassCastException ce){
			throw new InvalidAttributeValueException("il tipo "+attribute.getValue().getClass()+" dell'attributo "+attribute.getName()+" non e' valido");
		}catch(JMException j){
			throw new MBeanException(j);
		}
		
	}
	private TimerState getTimerState(Object o) throws ClassCastException {
		TimerState state = TimerState.valueOf((String)o);
		if(state==null) {
			throw new ClassCastException("Valore indicato '"+o+"' non valido");
		}
		if(TimerState.OFF.equals(state)) {
			throw new ClassCastException("Valore indicato '"+o+"' non supportato");
		}
		return state;
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
		
		if(actionName.equals(JMXUtils.CACHE_METHOD_NAME_PREFILL)){
			return this.prefillCache();
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
			
		if(actionName.equals(CHECK_CONNETTORE_BY_ID)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+CHECK_CONNETTORE_BY_ID+"] Lunghezza parametri non corretta: "+params.length));
			
			Long param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				if(params[0] instanceof Long) {
					param1 = (Long)params[0];
				}
				else {
					param1 = Long.valueOf(params[0].toString());
				}
				if(param1<0){
					param1 = null;
				}
			}
			return this.checkConnettoreById(param1);
		}
		
		if(actionName.equals(CHECK_CONNETTORE_BY_NOME)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+CHECK_CONNETTORE_BY_NOME+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			return this.checkConnettoreByNome(param1);
		}
		
		if(actionName.equals(GET_CERTIFICATI_CONNETTORE_BY_ID)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+GET_CERTIFICATI_CONNETTORE_BY_ID+"] Lunghezza parametri non corretta: "+params.length));
			
			Long param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				if(params[0] instanceof Long) {
					param1 = (Long)params[0];
				}
				else {
					param1 = Long.valueOf(params[0].toString());
				}
				if(param1<0){
					param1 = null;
				}
			}
			return this.getCertificatiConnettoreById(param1);
		}
		
		if(actionName.equals(GET_CERTIFICATI_CONNETTORE_BY_NOME)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+GET_CERTIFICATI_CONNETTORE_BY_NOME+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			return this.getCertificatiConnettoreByNome(param1);
		}
		
		if(actionName.equals(CHECK_CERTIFICATO_SERVIZIO_APPLICATIVO_BY_ID)){
			if(params.length != 2)
				throw new MBeanException(new Exception("["+CHECK_CERTIFICATO_SERVIZIO_APPLICATIVO_BY_ID+"] Lunghezza parametri non corretta: "+params.length));
			
			Long param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				if(params[0] instanceof Long) {
					param1 = (Long)params[0];
				}
				else {
					param1 = Long.valueOf(params[0].toString());
				}
				if(param1<0){
					param1 = null;
				}
			}
			
			int soglia = -1;
			if(params[1] instanceof Integer) {
				soglia = (Integer)params[1];
			}
			else {
				soglia = Integer.valueOf(params[1].toString());
			}
			
			return this.checkCertificatoApplicativoById(param1, soglia);
		}
		if(actionName.equals(CHECK_CERTIFICATO_SERVIZIO_APPLICATIVO_BY_NOME)){
			if(params.length != 2)
				throw new MBeanException(new Exception("["+CHECK_CERTIFICATO_SERVIZIO_APPLICATIVO_BY_NOME+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			
			int soglia = -1;
			if(params[1] instanceof Integer) {
				soglia = (Integer)params[1];
			}
			else {
				soglia = Integer.valueOf(params[1].toString());
			}
			
			return this.checkCertificatoApplicativoByNome(param1, soglia);
		}
		
		if(actionName.equals(CHECK_CERTIFICATO_MODI_SERVIZIO_APPLICATIVO_BY_ID)){
			if(params.length != 2)
				throw new MBeanException(new Exception("["+CHECK_CERTIFICATO_MODI_SERVIZIO_APPLICATIVO_BY_ID+"] Lunghezza parametri non corretta: "+params.length));
			
			Long param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				if(params[0] instanceof Long) {
					param1 = (Long)params[0];
				}
				else {
					param1 = Long.valueOf(params[0].toString());
				}
				if(param1<0){
					param1 = null;
				}
			}
			
			int soglia = -1;
			if(params[1] instanceof Integer) {
				soglia = (Integer)params[1];
			}
			else {
				soglia = Integer.valueOf(params[1].toString());
			}
			
			return this.checkCertificatoModiApplicativoById(param1, soglia);
		}
		if(actionName.equals(CHECK_CERTIFICATO_MODI_SERVIZIO_APPLICATIVO_BY_NOME)){
			if(params.length != 2)
				throw new MBeanException(new Exception("["+CHECK_CERTIFICATO_MODI_SERVIZIO_APPLICATIVO_BY_NOME+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			
			int soglia = -1;
			if(params[1] instanceof Integer) {
				soglia = (Integer)params[1];
			}
			else {
				soglia = Integer.valueOf(params[1].toString());
			}
			
			return this.checkCertificatoModiApplicativoByNome(param1, soglia);
		}
		
		
		
		if(actionName.equals(ABILITA_PORTA_DELEGATA)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+ABILITA_PORTA_DELEGATA+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			
			return this.updateStatoPortaDelegata(param1, true);
		}
		
		if(actionName.equals(DISABILITA_PORTA_DELEGATA)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+DISABILITA_PORTA_DELEGATA+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			
			return this.updateStatoPortaDelegata(param1, false);
		}
		
		if(actionName.equals(ABILITA_PORTA_APPLICATIVA)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+ABILITA_PORTA_APPLICATIVA+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			
			return this.updateStatoPortaApplicativa(param1, true);
		}
		
		if(actionName.equals(DISABILITA_PORTA_APPLICATIVA)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+DISABILITA_PORTA_APPLICATIVA+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			
			return this.updateStatoPortaApplicativa(param1, false);
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

		// MetaData per l'attributo livelloMsgDiagnostici
		MBeanAttributeInfo livelloMsgDiagnosticiVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.MSG_DIAGNOSTICI_SEVERITA_LIVELLO,String.class.getName(),
						"Livello dei messaggi diagnostici emessi\n[off,fatal,errorProtocol,errorIntegration,infoProtocol,infoIntegration,debugLow,debugMedium,debugHigh,all]",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo livelloMsgDiagnosticiHumanReadable
		MBeanAttributeInfo livelloMsgDiagnosticiLog4JVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.MSG_DIAGNOSTICI_SEVERITA_LIVELLO_LOG4J,String.class.getName(),
						"Livello dei messaggi diagnostici human readable emessi\n[off,fatal,errorProtocol,errorIntegration,infoProtocol,infoIntegration,debugLow,debugMedium,debugHigh,all]",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo msgDiagnosticiAppender
		MBeanAttributeInfo msgDiagnosticiAppenderVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.MSG_DIAGNOSTICI_APPENDER,String[].class.getName(),
						"Appender personalizzati per la registrazione dei messaggi diagnostici emessi",
							JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
							
		// MetaData per l'attributo tracciamentoAbilitato
		MBeanAttributeInfo tracciamentoAbilitatoVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.TRACCIAMENTO_ABILITATO,boolean.class.getName(),
						"Indicazione se e' abilito il tracciamento delle buste emesse/ricevute",
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
						"Appender personalizzati per la registrazione delle buste emesse/ricevute",
							JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo log4jDiagnosticaAbilitato
		MBeanAttributeInfo log4jDiagnosticaAbilitatoVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.LOG4J_DIAGNOSTICA_ABILITATO,boolean.class.getName(),
						"Indicazione se è abilitato il logging su file govway_diagnostici.log",
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
						"Indicazione se è abilitato il logging su file govway_tracciamento.log",
							JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo log4jDumpAbilitato
		MBeanAttributeInfo log4jDumpAbilitatoVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.LOG4J_DUMP_ABILITATO,boolean.class.getName(),
						"Indicazione se è abilitato il logging su file openspcoop2_dump.log",
							JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo erroriStatusCodeAbilitatoVAR
		MBeanAttributeInfo erroriStatusCodeAbilitatoVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.ERRORI_STATUS_CODE_ABILITATO,boolean.class.getName(),
						"Indicazione se è abilitato la generazione dello status code negli errori generati dal Gateway",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo erroriInstanceIdAbilitatoVAR
		MBeanAttributeInfo erroriInstanceIdAbilitatoVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.ERRORI_INSTANCE_ID_ABILITATO,boolean.class.getName(),
						"Indicazione se è abilitato la generazione dell'identificativo dell'API invocata (instance) negli errori generati dal Gateway",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);

		// MetaData per l'attributo erroriForceSpecificErrorTypeInternalBadRequestVAR
		MBeanAttributeInfo erroriForceSpecificErrorTypeInternalBadRequestVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_BAD_REQUEST,boolean.class.getName(),
						"Indicazione se è abilitato la generazione di uno specifico tipo di errore per la gestione fallita di una richiesta, dovuta ad una errata configurazione del Gateway",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo erroriForceSpecificErrorTypeBadResponseVAR
		MBeanAttributeInfo erroriForceSpecificErrorTypeBadResponseVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE,boolean.class.getName(),
						"Indicazione se è abilitato la generazione di uno specifico tipo di errore per la gestione fallita di una risposta, dovuta alla risposta ritornata dal backend",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo erroriForceSpecificErrorTypeInternalResponseErrorVAR
		MBeanAttributeInfo erroriForceSpecificErrorTypeInternalResponseErrorVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR,boolean.class.getName(),
						"Indicazione se è abilitato la generazione di uno specifico tipo di errore per la gestione fallita di una risposta, dovuta ad una errata configurazione del Gateway",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
			
		// MetaData per l'attributo erroriForceSpecificErrorTypeInternalErrorVAR
		MBeanAttributeInfo erroriForceSpecificErrorTypeInternalErrorVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR,boolean.class.getName(),
						"Indicazione se è abilitato la generazione di uno specifico tipo di errore per la gestione fallita di una richiesta, dovuta ad un malfunzionamento del Gateway",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);

		// MetaData per l'attributo erroriForceSpecificDetailsVAR
		MBeanAttributeInfo erroriForceSpecificDetailsVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_DETAILS,boolean.class.getName(),
						"Indicazione se è abilitato la generazione di un dettaglio specifico negli errori generati dal Gateway",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo erroriSoapUseGovWayStatusAsFaultCodeVAR
		MBeanAttributeInfo erroriSoapUseGovWayStatusAsFaultCodeVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.ERRORI_SOAP_USE_GOVWAY_STATUS_AS_FAULT_CODE,boolean.class.getName(),
						"Indicazione se è abilitato la generazione di un codice di errore di dettaglio GovWay come FaultCode negli errori SOAP generati dal Gateway",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo erroriSoapGenerateHttpHeaderGovWayCodeVAR
		MBeanAttributeInfo erroriSoapGenerateHttpHeaderGovWayCodeVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.ERRORI_SOAP_GENERATE_HTTP_HEADER_GOVWAY_CODE,boolean.class.getName(),
						"Indicazione se è abilitato la generazione del codice http di errore in un header http negli errori SOAP generati dal Gateway",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo timerConsegnaContenutiApplicativiVAR
		MBeanAttributeInfo timerConsegnaContenutiApplicativiVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.TIMER_CONSEGNA_CONTENUTI_APPLICATIVI,String.class.getName(),
						"Indicazione se è abilitato il timer '"+TimerConsegnaContenutiApplicativiThread.ID_MODULO+"' ("+TimerState.ENABLED.name()+"/"+TimerState.DISABLED.name()+")",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);

		// MetaData per l'attributo timerEventiVAR
		MBeanAttributeInfo timerEventiVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.TIMER_EVENTI,String.class.getName(),
						"Indicazione se è abilitato il timer '"+TimerEventiThread.ID_MODULO+"' ("+TimerState.ENABLED.name()+"/"+TimerState.DISABLED.name()+")",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);

		// MetaData per l'attributo timerFileSystemRecoveryVAR
		MBeanAttributeInfo timerFileSystemRecoveryVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.TIMER_FILE_SYSTEM_RECOVERY,String.class.getName(),
						"Indicazione se è abilitato il timer '"+TimerFileSystemRecoveryThread.ID_MODULO+"' ("+TimerState.ENABLED.name()+"/"+TimerState.DISABLED.name()+")",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo timerGestoreBusteOnewayNonRiscontrateVAR
		MBeanAttributeInfo timerGestoreBusteOnewayNonRiscontrateVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.TIMER_GESTORE_BUSTE_ONEWAY_NON_RISCONTRATE,String.class.getName(),
						"Indicazione se è abilitato il timer '"+TimerGestoreBusteNonRiscontrate.ID_MODULO+"' (oneway) ("+TimerState.ENABLED.name()+"/"+TimerState.DISABLED.name()+")",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo timerGestoreBusteAsincroneNonRiscontrateVAR
		MBeanAttributeInfo timerGestoreBusteAsincroneNonRiscontrateVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.TIMER_GESTORE_BUSTE_ASINCRONE_NON_RISCONTRATE,String.class.getName(),
						"Indicazione se è abilitato il timer '"+TimerGestoreBusteNonRiscontrate.ID_MODULO+"' (asincroni) ("+TimerState.ENABLED.name()+"/"+TimerState.DISABLED.name()+")",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo timerGestoreMessaggiPuliziaMessaggiEliminatiVAR
		MBeanAttributeInfo timerGestoreMessaggiPuliziaMessaggiEliminatiVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.TIMER_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_ELIMINATI,String.class.getName(),
						"Indicazione se è abilitato il timer '"+TimerGestoreMessaggi.ID_MODULO+"' (eliminazione logica) ("+TimerState.ENABLED.name()+"/"+TimerState.DISABLED.name()+")",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo timerGestoreMessaggiPuliziaMessaggiScadutiVAR
		MBeanAttributeInfo timerGestoreMessaggiPuliziaMessaggiScadutiVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.TIMER_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_SCADUTI,String.class.getName(),
						"Indicazione se è abilitato il timer '"+TimerGestoreMessaggi.ID_MODULO+"' (scaduti) ("+TimerState.ENABLED.name()+"/"+TimerState.DISABLED.name()+")",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo timerGestoreMessaggiPuliziaMessaggiNonGestitiVAR
		MBeanAttributeInfo timerGestoreMessaggiPuliziaMessaggiNonGestitiVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.TIMER_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_NON_GESTITI,String.class.getName(),
						"Indicazione se è abilitato il timer '"+TimerGestoreMessaggi.ID_MODULO+"' (non gestiti) ("+TimerState.ENABLED.name()+"/"+TimerState.DISABLED.name()+")",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo timerGestoreMessaggiPuliziaCorrelazioneApplicativaVAR
		MBeanAttributeInfo timerGestoreMessaggiPuliziaCorrelazioneApplicativaVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.TIMER_GESTORE_MESSAGGI_PULIZIA_CORRELAZIONE_APPLICATIVA,String.class.getName(),
						"Indicazione se è abilitato il timer '"+TimerGestoreMessaggi.ID_MODULO+"' (correlazione applicativa) ("+TimerState.ENABLED.name()+"/"+TimerState.DISABLED.name()+")",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo timerGestoreMessaggiVerificaConnessioniAttiveVAR
		MBeanAttributeInfo timerGestoreMessaggiVerificaConnessioniAttiveVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.TIMER_GESTORE_MESSAGGI_VERIFICA_CONNESSIONI_ATTIVE,String.class.getName(),
						"Indicazione se è abilitato il timer '"+TimerGestoreMessaggi.ID_MODULO+"' (verifica connessioni attive) ("+TimerState.ENABLED.name()+"/"+TimerState.DISABLED.name()+")",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo timerGestorePuliziaMessaggiAnomaliVAR
		MBeanAttributeInfo timerGestorePuliziaMessaggiAnomaliVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.TIMER_GESTORE_PULIZIA_MESSAGGI_ANOMALI,String.class.getName(),
						"Indicazione se è abilitato il timer '"+TimerGestorePuliziaMessaggiAnomali.ID_MODULO+"' ("+TimerState.ENABLED.name()+"/"+TimerState.DISABLED.name()+")",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo timerGestoreRepositoryBusteVAR
		MBeanAttributeInfo timerGestoreRepositoryBusteVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.TIMER_GESTORE_REPOSITORY_BUSTE,String.class.getName(),
						"Indicazione se è abilitato il timer '"+TimerGestoreRepositoryBuste.ID_MODULO+"' ("+TimerState.ENABLED.name()+"/"+TimerState.DISABLED.name()+")",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo timerMonitoraggioRisorseThreadVAR
		MBeanAttributeInfo timerMonitoraggioRisorseThreadVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.TIMER_MONITORAGGIO_RISORSE_THREAD,String.class.getName(),
						"Indicazione se è abilitato il timer '"+TimerMonitoraggioRisorseThread.ID_MODULO+"' ("+TimerState.ENABLED.name()+"/"+TimerState.DISABLED.name()+")",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo timerRepositoryStatefulThreadVAR
		MBeanAttributeInfo timerRepositoryStatefulThreadVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.TIMER_REPOSITORY_STATEFUL_THREAD,String.class.getName(),
						"Indicazione se è abilitato il timer '"+TimerRepositoryStatefulThread.ID_MODULO+"' ("+TimerState.ENABLED.name()+"/"+TimerState.DISABLED.name()+")",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo timerStatisticheOrarieVAR
		MBeanAttributeInfo timerStatisticheOrarieVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.TIMER_STATISTICHE_ORARIE,String.class.getName(),
						"Indicazione se è abilitato il timer '"+TimerStatisticheThread.ID_MODULO+"' (orarie) ("+TimerState.ENABLED.name()+"/"+TimerState.DISABLED.name()+")",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo timerStatisticheGiornaliereVAR
		MBeanAttributeInfo timerStatisticheGiornaliereVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.TIMER_STATISTICHE_GIORNALIERE,String.class.getName(),
						"Indicazione se è abilitato il timer '"+TimerStatisticheThread.ID_MODULO+"' (giornaliere) ("+TimerState.ENABLED.name()+"/"+TimerState.DISABLED.name()+")",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo timerStatisticheSettimanaliVAR
		MBeanAttributeInfo timerStatisticheSettimanaliVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.TIMER_STATISTICHE_SETTIMANALI,String.class.getName(),
						"Indicazione se è abilitato il timer '"+TimerStatisticheThread.ID_MODULO+"' (settimanali) ("+TimerState.ENABLED.name()+"/"+TimerState.DISABLED.name()+")",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo timerStatisticheMensiliVAR
		MBeanAttributeInfo timerStatisticheMensiliVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.TIMER_STATISTICHE_MENSILI,String.class.getName(),
						"Indicazione se è abilitato il timer '"+TimerStatisticheThread.ID_MODULO+"' (mensili) ("+TimerState.ENABLED.name()+"/"+TimerState.DISABLED.name()+")",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);

		// MetaData per l'attributo timerThresholdThreadVAR
		MBeanAttributeInfo timerThresholdThreadVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.TIMER_THRESHOLD_THREAD,String.class.getName(),
						"Indicazione se è abilitato il timer '"+TimerThresholdThread.ID_MODULO+"' ("+TimerState.ENABLED.name()+"/"+TimerState.DISABLED.name()+")",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo timerClusterDinamicoVAR
		MBeanAttributeInfo timerClusterDinamicoVAR 
		= new MBeanAttributeInfo(ConfigurazionePdD.TIMER_CLUSTER_DINAMICO,String.class.getName(),
				"Indicazione se è abilitato il timer '"+TimerClusterDinamicoThread.ID_MODULO+"' ("+TimerState.ENABLED.name()+"/"+TimerState.DISABLED.name()+")",
				JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);

		// MetaData per l'operazione resetCache
		MBeanOperationInfo resetCacheOP = JMXUtils.MBEAN_OPERATION_RESET_CACHE;
				
		// MetaData per l'operazione prefillCache
		MBeanOperationInfo prefillCacheOP = JMXUtils.MBEAN_OPERATION_PREFILL_CACHE;
		
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
				
		// MetaData per l'operazione checkConettoreById
		MBeanOperationInfo checkConnettoreById 
		= new MBeanOperationInfo(CHECK_CONNETTORE_BY_ID,"Verifica la raggiungibilità del connettore con id fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("idConnettore",long.class.getName(),"Identificativo del connettore"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione checkConettoreByNome
		MBeanOperationInfo checkConnettoreByNome 
		= new MBeanOperationInfo(CHECK_CONNETTORE_BY_NOME,"Verifica la raggiungibilità del connettore con nome fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomeConnettore",String.class.getName(),"Nome del connettore"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
			
		// MetaData per l'operazione getCertificatiConnettoreById
		MBeanOperationInfo getCertificatiConnettoreById 
		= new MBeanOperationInfo(GET_CERTIFICATI_CONNETTORE_BY_ID,"Recupera i certificati server del connettore con id fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("idConnettore",long.class.getName(),"Identificativo del connettore"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione getCertificatiConnettoreByNome
		MBeanOperationInfo getCertificatiConnettoreByNome 
		= new MBeanOperationInfo(GET_CERTIFICATI_CONNETTORE_BY_NOME,"Recupera i certificati server del connettore con nome fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomeConnettore",String.class.getName(),"Nome del connettore"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione checkCertificatoApplicativoById
		MBeanOperationInfo checkCertificatoApplicativoById 
		= new MBeanOperationInfo(CHECK_CERTIFICATO_SERVIZIO_APPLICATIVO_BY_ID,"Verifica i certificati client associati all'applicativo che possiede l'id fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("idApplicativo",long.class.getName(),"Identificativo dell'applicativo"),
				new MBeanParameterInfo("warningThreshold",int.class.getName(),"Soglia di warning (giorni)"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione checkCertificatoApplicativoByNome
		MBeanOperationInfo checkCertificatoApplicativoByNome 
		= new MBeanOperationInfo(CHECK_CERTIFICATO_SERVIZIO_APPLICATIVO_BY_NOME,"Verifica i certificati client associati all'applicativo che possiede l'id fornito come parametro (formato: nomeApplicativo@tipoSoggetto/nomeSoggetto)",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("idApplicativo",String.class.getName(),"Identificativo dell'applicativo"),
				new MBeanParameterInfo("warningThreshold",int.class.getName(),"Soglia di warning (giorni)"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione checkCertificatoModIApplicativoById
		MBeanOperationInfo checkCertificatoModIApplicativoById 
		= new MBeanOperationInfo(CHECK_CERTIFICATO_MODI_SERVIZIO_APPLICATIVO_BY_ID,"Verifica il keystore ModI associato all'applicativo che possiede l'id fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("idApplicativo",long.class.getName(),"Identificativo dell'applicativo"),
				new MBeanParameterInfo("warningThreshold",int.class.getName(),"Soglia di warning (giorni)"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione checkCertificatoModIApplicativoByNome
		MBeanOperationInfo checkCertificatoModIApplicativoByNome 
		= new MBeanOperationInfo(CHECK_CERTIFICATO_SERVIZIO_APPLICATIVO_BY_NOME,"Verifica il keystore ModI associato all'applicativo che possiede l'id fornito come parametro (formato: nomeApplicativo@tipoSoggetto/nomeSoggetto)",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("idApplicativo",String.class.getName(),"Identificativo dell'applicativo"),
				new MBeanParameterInfo("warningThreshold",int.class.getName(),"Soglia di warning (giorni)"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione enablePortaDelegata
		MBeanOperationInfo enablePortaDelegata 
		= new MBeanOperationInfo(ABILITA_PORTA_DELEGATA,"Abilita lo stato della porta con nome fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomePorta",String.class.getName(),"Nome della Porta"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione disablePortaDelegata
		MBeanOperationInfo disablePortaDelegata 
		= new MBeanOperationInfo(DISABILITA_PORTA_DELEGATA,"Disabilita lo stato della porta con nome fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomePorta",String.class.getName(),"Nome della Porta"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione enablePortaApplicativa
		MBeanOperationInfo enablePortaApplicativa 
		= new MBeanOperationInfo(ABILITA_PORTA_APPLICATIVA,"Abilita lo stato della porta con nome fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomePorta",String.class.getName(),"Nome della Porta"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione disablePortaApplicativa
		MBeanOperationInfo disablePortaApplicativa
		= new MBeanOperationInfo(DISABILITA_PORTA_APPLICATIVA,"Disabilita lo stato della porta con nome fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomePorta",String.class.getName(),"Nome della Porta"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// Mbean costruttore
		MBeanConstructorInfo defaultConstructor = new MBeanConstructorInfo("Default Constructor","Crea e inizializza una nuova istanza del MBean",null);

		// Lista attributi
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[]{cacheAbilitataVAR,livelloMsgDiagnosticiVAR,
				livelloMsgDiagnosticiLog4JVAR,msgDiagnosticiAppenderVAR,tracciamentoAbilitatoVAR,
				dumpBinarioPDAbilitatoVAR,dumpBinarioPAAbilitatoVAR,
				tracciamentoAppenderVAR,
				log4jDiagnosticaAbilitatoVAR, log4jOpenSPCoopAbilitatoVAR, log4jIntegrationManagerAbilitatoVAR,
				log4jTracciamentoAbilitatoVAR, log4jDumpAbilitatoVAR,
				erroriStatusCodeAbilitatoVAR, erroriInstanceIdAbilitatoVAR, 
				erroriForceSpecificErrorTypeInternalBadRequestVAR,
				erroriForceSpecificErrorTypeBadResponseVAR, erroriForceSpecificErrorTypeInternalResponseErrorVAR,
				erroriForceSpecificErrorTypeInternalErrorVAR,
				erroriForceSpecificDetailsVAR,
				erroriSoapUseGovWayStatusAsFaultCodeVAR, erroriSoapGenerateHttpHeaderGovWayCodeVAR,
				timerConsegnaContenutiApplicativiVAR, 
				timerStatisticheOrarieVAR,timerStatisticheGiornaliereVAR,timerStatisticheSettimanaliVAR,timerStatisticheMensiliVAR,
				timerEventiVAR, timerFileSystemRecoveryVAR,
				timerGestoreBusteOnewayNonRiscontrateVAR, timerGestoreBusteAsincroneNonRiscontrateVAR,
				timerGestoreMessaggiPuliziaMessaggiEliminatiVAR, timerGestoreMessaggiPuliziaMessaggiScadutiVAR, timerGestoreMessaggiPuliziaMessaggiNonGestitiVAR,
				timerGestoreMessaggiPuliziaCorrelazioneApplicativaVAR, timerGestoreMessaggiVerificaConnessioniAttiveVAR,
				timerGestorePuliziaMessaggiAnomaliVAR, timerGestoreRepositoryBusteVAR,
				timerMonitoraggioRisorseThreadVAR, timerThresholdThreadVAR,
				timerClusterDinamicoVAR,
				timerRepositoryStatefulThreadVAR};
		
		// Lista Costruttori
		MBeanConstructorInfo[] constructors = new MBeanConstructorInfo[]{defaultConstructor};
		
		// Lista operazioni
		List<MBeanOperationInfo> listOperation = new ArrayList<>();
		listOperation.add(resetCacheOP);
		if(this.openspcoopProperties.isConfigurazioneCache_ConfigPrefill()){
			listOperation.add(prefillCacheOP);
		}
		listOperation.add(printStatCacheOP);
		listOperation.add(disabilitaCacheOP);
		listOperation.add(abilitaCacheParametriOP);
		listOperation.add(listKeysCacheOP);
		listOperation.add(getObjectCacheOP);
		listOperation.add(removeObjectCacheOP);
		listOperation.add(checkConnettoreById);
		listOperation.add(checkConnettoreByNome);
		listOperation.add(getCertificatiConnettoreById);
		listOperation.add(getCertificatiConnettoreByNome);
		listOperation.add(checkCertificatoApplicativoById);
		listOperation.add(checkCertificatoApplicativoByNome);
		listOperation.add(checkCertificatoModIApplicativoById);
		listOperation.add(checkCertificatoModIApplicativoByNome);
		listOperation.add(enablePortaDelegata);
		listOperation.add(disablePortaDelegata);
		listOperation.add(enablePortaApplicativa);
		listOperation.add(disablePortaApplicativa);
		MBeanOperationInfo[] operations = listOperation.toArray(new MBeanOperationInfo[1]);
		
		return new MBeanInfo(className,description,attributes,constructors,operations,null);
	}
	
	/* Variabili per la gestione JMX */
	private Logger log;
	private Logger logConnettori;
	org.openspcoop2.pdd.config.ConfigurazionePdDManager configReader = null;
	org.openspcoop2.pdd.config.OpenSPCoop2Properties openspcoopProperties = null;
	
	/* Costruttore */
	public ConfigurazionePdD(){
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		this.logConnettori = OpenSPCoop2Logger.getLoggerOpenSPCoopConnettori();
		this.configReader = org.openspcoop2.pdd.config.ConfigurazionePdDManager.getInstance();
		this.openspcoopProperties = org.openspcoop2.pdd.config.OpenSPCoop2Properties.getInstance();
				
		// Configurazione
		try{
			this.cacheAbilitata = ConfigurazionePdDReader.isCacheAbilitata();
		}catch(Exception e){
			this.log.error("Errore durante l'identificazione dello stato della cache");
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
			FileTraceConfig.resetFileTraceAssociatePorte();
			return JMXUtils.MSG_RESET_CACHE_EFFETTUATO_SUCCESSO;
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String prefillCache(){
		try{
			if(this.cacheAbilitata==false)
				throw new Exception("Cache non abilitata");
			org.openspcoop2.pdd.config.ConfigurazionePdDReader.prefillCache(this.openspcoopProperties.getCryptConfigAutenticazioneApplicativi());
			return JMXUtils.MSG_PREFILL_CACHE_EFFETTUATO_SUCCESSO;
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
			org.openspcoop2.pdd.config.ConfigurazionePdDReader.abilitaCache(dimensioneCache,algoritmoCacheLRU,itemIdleTime,itemLifeSecond,
					this.openspcoopProperties.getCryptConfigAutenticazioneApplicativi());
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
	
	public void setDumpBinarioPD(boolean v){
		this.dumpBinarioPDAbilitato = v;
		ConfigurazionePdDReader.dumpBinarioPDJMX = v;
	}
	
	public void setDumpBinarioPA(boolean v){
		this.dumpBinarioPAAbilitato = v;
		ConfigurazionePdDReader.dumpBinarioPAJMX = v;
	}
	
	public void setFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_BAD_REQUEST(boolean value) {
		try{
			ErroriProperties.setFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_BAD_REQUEST( 
					value,
					this.openspcoopProperties.getRootDirectory(), this.log, Loader.getInstance());
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
		}
	}
	
	public void setFORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE(boolean value) {
		try{
			ErroriProperties.setFORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE( 
					value,
					this.openspcoopProperties.getRootDirectory(), this.log, Loader.getInstance());
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
		}
	}
	
	public void setFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR(boolean value) {
		try{
			ErroriProperties.setFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR( 
					value,
					this.openspcoopProperties.getRootDirectory(), this.log, Loader.getInstance());
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
		}
	}
		
	public void setFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR(boolean value) {
		try{
			ErroriProperties.setFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR( 
					value,
					this.openspcoopProperties.getRootDirectory(), this.log, Loader.getInstance());
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
		}
	}

	public String checkConnettoreById(long idConnettore) {
		try{
			ConnettoreCheck.check(idConnettore, false, this.logConnettori);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String checkConnettoreByNome(String nomeConnettore) {
		try{
			ConnettoreCheck.check(nomeConnettore, false, this.logConnettori);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getCertificatiConnettoreById(long idConnettore) {
		try{
			return ConnettoreCheck.getCertificati(idConnettore, true);
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getCertificatiConnettoreByNome(String nomeConnettore) {
		try{
			return ConnettoreCheck.getCertificati(nomeConnettore, true);
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String checkCertificatoApplicativoById(long idApplicativo, int sogliaWarningGiorni) {
		try{
			boolean addCertificateDetails = true;
			String separator = ": ";
			String newLine = "\n";
			CertificateCheck statoCheck = ConfigurazionePdDManager.getInstance().checkCertificatoApplicativoWithoutCache(idApplicativo, sogliaWarningGiorni, 
					addCertificateDetails, separator, newLine);
			return statoCheck.toString(newLine);
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String checkCertificatoApplicativoByNome(String idApplicativo, int sogliaWarningGiorni) {
		try{
			if(!idApplicativo.contains("@") || !idApplicativo.contains("/")) {
				throw new Exception("Formato non valido (nome@tipoSoggetto/nomeSoggetto)");
			}
			String [] tmp = idApplicativo.split("@");
			if(tmp==null || tmp.length!=2 || tmp[0]==null || tmp[1]==null) {
				throw new Exception("Formato non valido (nome@tipoSoggetto/nomeSoggetto)");
			}
			else {
				String nome = tmp[0];
				if(!tmp[1].contains("/")) {
					throw new Exception("Formato non valido (nome@tipoSoggetto/nomeSoggetto)");
				}
				else {
					String [] tmp2 = tmp[1].split("/");
					if(tmp2==null || tmp2.length!=2 || tmp2[0]==null || tmp2[1]==null) {
						throw new Exception("Formato non valido (nome@tipoSoggetto/nomeSoggetto)");
					}
					else {
						String tipoSoggetto = tmp2[0];
						String nomeSoggetto = tmp2[1];
						IDServizioApplicativo idSA = new IDServizioApplicativo();
						idSA.setNome(nome);
						idSA.setIdSoggettoProprietario(new IDSoggetto(tipoSoggetto, nomeSoggetto));
						
						boolean addCertificateDetails = true;
						String separator = ": ";
						String newLine = "\n";
						CertificateCheck statoCheck = ConfigurazionePdDManager.getInstance().checkCertificatoApplicativoWithoutCache(idSA, sogliaWarningGiorni, 
								addCertificateDetails, separator, newLine);
						return statoCheck.toString(newLine);
					}
				}
			}
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String checkCertificatoModiApplicativoById(long idApplicativo, int sogliaWarningGiorni) {
		try{
			boolean addCertificateDetails = true;
			String separator = ": ";
			String newLine = "\n";
			CertificateCheck statoCheck = ConfigurazionePdDManager.getInstance().checkCertificatoModiApplicativoWithoutCache(idApplicativo, sogliaWarningGiorni, 
					addCertificateDetails, separator, newLine);
			return statoCheck.toString(newLine);
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String checkCertificatoModiApplicativoByNome(String idApplicativo, int sogliaWarningGiorni) {
		try{
			if(!idApplicativo.contains("@") || !idApplicativo.contains("/")) {
				throw new Exception("Formato non valido (nome@tipoSoggetto/nomeSoggetto)");
			}
			String [] tmp = idApplicativo.split("@");
			if(tmp==null || tmp.length!=2 || tmp[0]==null || tmp[1]==null) {
				throw new Exception("Formato non valido (nome@tipoSoggetto/nomeSoggetto)");
			}
			else {
				String nome = tmp[0];
				if(!tmp[1].contains("/")) {
					throw new Exception("Formato non valido (nome@tipoSoggetto/nomeSoggetto)");
				}
				else {
					String [] tmp2 = tmp[1].split("/");
					if(tmp2==null || tmp2.length!=2 || tmp2[0]==null || tmp2[1]==null) {
						throw new Exception("Formato non valido (nome@tipoSoggetto/nomeSoggetto)");
					}
					else {
						String tipoSoggetto = tmp2[0];
						String nomeSoggetto = tmp2[1];
						IDServizioApplicativo idSA = new IDServizioApplicativo();
						idSA.setNome(nome);
						idSA.setIdSoggettoProprietario(new IDSoggetto(tipoSoggetto, nomeSoggetto));
						
						boolean addCertificateDetails = true;
						String separator = ": ";
						String newLine = "\n";
						CertificateCheck statoCheck = ConfigurazionePdDManager.getInstance().checkCertificatoModiApplicativoWithoutCache(idSA, sogliaWarningGiorni, 
								addCertificateDetails, separator, newLine);
						return statoCheck.toString(newLine);
					}
				}
			}
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String updateStatoPortaDelegata(String nomePorta, boolean enable) {
		try{
			IDPortaDelegata idPD = new IDPortaDelegata();
			idPD.setNome(nomePorta);
			this.configReader.updateStatoPortaDelegata(idPD, enable ? StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String updateStatoPortaApplicativa(String nomePorta, boolean enable) {
		try{
			IDPortaApplicativa idPA = new IDPortaApplicativa();
			idPA.setNome(nomePorta);
			this.configReader.updateStatoPortaApplicativa(idPA, enable ? StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
}
