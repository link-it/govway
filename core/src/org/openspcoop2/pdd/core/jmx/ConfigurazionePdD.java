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

import java.sql.Connection;
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

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;
import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.cache.GestoreCacheCleaner;
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
import org.openspcoop2.pdd.timers.pdnd.TimerGestoreCacheChiaviPDND;
import org.openspcoop2.pdd.timers.pdnd.TimerGestoreCacheChiaviPDNDLib;
import org.openspcoop2.pdd.timers.pdnd.TimerGestoreChiaviPDND;
import org.openspcoop2.pdd.timers.pdnd.TimerGestoreChiaviPDNDLib;
import org.openspcoop2.pdd.timers.proxy.TimerGestoreOperazioniRemoteLib;
import org.openspcoop2.pdd.timers.proxy.TimerSvecchiamentoOperazioniRemoteLib;
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
	public static final String MSG_DIAGNOSTICI_SEVERITA_LIVELLO = "msgDiagnosticiLivelloSeverita";
	public static final String MSG_DIAGNOSTICI_SEVERITA_LIVELLO_LOG4J = "msgDiagnosticiLivelloSeveritaLog4J";
	public static final String MSG_DIAGNOSTICI_APPENDER = "msgDiagnosticiAppender";	
	public static final String TRACCIAMENTO_ABILITATO = "tracciamentoAbilitato";
	public static final String DUMP_BINARIO_PD_ABILITATO = "dumpBinarioPortaDelegataAbilitato";
	public static final String DUMP_BINARIO_PA_ABILITATO = "dumpBinarioPortaApplicativaAbilitato";
	public static final String TRACCIAMENTO_APPENDER = "tracciamentoAppender";
	public static final String LOG4J_DIAGNOSTICA_ABILITATO = "log4jLogFileDiagnosticaAbilitato";
	public static final String LOG4J_OPENSPCOOP_ABILITATO = "log4jLogFileOpenSPCoopAbilitato";
	public static final String LOG4J_INTEGRATION_MANAGER_ABILITATO = "log4jLogFileIntegrationManagerAbilitato";
	public static final String LOG4J_TRACCIAMENTO_ABILITATO = "log4jLogFileTracciamentoAbilitato";
	public static final String LOG4J_DUMP_ABILITATO = "log4jLogFileDumpAbilitato";
	public static final String ERRORI_STATUS_CODE_ABILITATO = "transactionErrorStatusAbilitato";
	public static final String ERRORI_INSTANCE_ID_ABILITATO = "transactionErrorInstanceIdAbilitato";
	public static final String ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_BAD_REQUEST = "transactionErrorForceSpecificTypeInternalBadRequest";
	public static final String ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE = "transactionErrorForceSpecificTypeBadResponse";
	public static final String ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR = "transactionErrorForceSpecificTypeInternalResponseError";
	public static final String ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR= "transactionErrorForceSpecificTypeInternalError";
	public static final String ERRORI_FORCE_SPECIFIC_DETAILS = "transactionErrorForceSpecificDetails";
	public static final String ERRORI_SOAP_USE_GOVWAY_STATUS_AS_FAULT_CODE = "transactionErrorUseGovWayStatusAsSoapFaultCode";
	public static final String ERRORI_SOAP_GENERATE_HTTP_HEADER_GOVWAY_CODE = "transactionErrorGenerateHttpHeaderGovWayCode";
	
	public static final String TIMER_CONSEGNA_CONTENUTI_APPLICATIVI = "timerConsegnaContenutiApplicativi";
	public static final String TIMER_EVENTI = "timerEventi";
	public static final String TIMER_FILE_SYSTEM_RECOVERY = "timerFileSystemRecovery";
	public static final String TIMER_GESTORE_BUSTE_ONEWAY_NON_RISCONTRATE = "timerGestoreBusteOnewayNonRiscontrate";
	public static final String TIMER_GESTORE_BUSTE_ASINCRONE_NON_RISCONTRATE = "timerGestoreBusteAsincroneNonRiscontrate";
	public static final String TIMER_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_ELIMINATI = "timerGestoreMessaggiPuliziaMessaggiEliminati";
	public static final String TIMER_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_SCADUTI = "timerGestoreMessaggiPuliziaMessaggiScaduti";
	public static final String TIMER_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_NON_GESTITI = "timerGestoreMessaggiPuliziaMessaggiNonGestiti";
	public static final String TIMER_GESTORE_MESSAGGI_PULIZIA_CORRELAZIONE_APPLICATIVA = "timerGestoreMessaggiPuliziaCorrelazioneApplicativa";
	public static final String TIMER_GESTORE_MESSAGGI_VERIFICA_CONNESSIONI_ATTIVE = "timerGestoreMessaggiVerificaConnessioniAttive";
	public static final String TIMER_GESTORE_PULIZIA_MESSAGGI_ANOMALI = "timerGestorePuliziaMessaggiAnomali";
	public static final String TIMER_GESTORE_REPOSITORY_BUSTE = "timerGestoreRepositoryBuste";
	public static final String TIMER_MONITORAGGIO_RISORSE_THREAD = "timerMonitoraggioRisorseThread";
	public static final String TIMER_REPOSITORY_STATEFUL_THREAD = "timerRepositoryStatefulThread";
	public static final String TIMER_STATISTICHE_ORARIE = "timerStatisticheOrarie";
	public static final String TIMER_STATISTICHE_GIORNALIERE = "timerStatisticheGiornaliere";
	public static final String TIMER_STATISTICHE_SETTIMANALI = "timerStatisticheSettimanali";
	public static final String TIMER_STATISTICHE_MENSILI = "timerStatisticheMensili";
	public static final String TIMER_GESTORE_CHIAVI_PDND = "timerGestoreChiaviPDND";
	public static final String TIMER_GESTORE_CACHE_CHIAVI_PDND = "timerGestoreCacheChiaviPDND";
	public static final String TIMER_GESTORE_OPERAZIONI_REMOTE = "timerGestoreOperazioniRemote";
	public static final String TIMER_SVECCHIAMENTO_OPERAZIONI_REMOTE = "timerSvecchiamentoOperazioniRemote";
	public static final String TIMER_THRESHOLD_THREAD = "timerThresholdThread";
	public static final String TIMER_CLUSTER_DINAMICO = "timerClusterDinamico";
		
	/** Nomi metodi' */
	public static final String CHECK_CONNETTORE_BY_ID = "checkConnettoreById";
	public static final String CHECK_CONNETTORE_BY_NOME = "checkConnettoreByNome";
	public static final String CHECK_CONNETTORE_TOKEN_POLICY_VALIDATION = "checkConnettoreTokenPolicyValidazione";
	public static final String CHECK_CONNETTORE_TOKEN_POLICY_RETRIEVE = "checkConnettoreTokenPolicyNegoziazione";
	public static final String CHECK_CONNETTORE_ATTRIBUTE_AUTHORITY = "checkConnettoreAttributeAuthority";
	public static final String GET_CERTIFICATI_CONNETTORE_BY_ID = "getCertificatiConnettoreById";
	public static final String GET_CERTIFICATI_CONNETTORE_BY_NOME = "getCertificatiConnettoreByNome";
	public static final String GET_CERTIFICATI_TOKEN_POLICY_VALIDATION = "getCertificatiConnettoreTokenPolicyValidazione";
	public static final String GET_CERTIFICATI_TOKEN_POLICY_RETRIEVE = "getCertificatiConnettoreTokenPolicyNegoziazione";
	public static final String GET_CERTIFICATI_ATTRIBUTE_AUTHORITY = "getCertificatiConnettoreAttributeAuthority";
	public static final String CHECK_CERTIFICATI_CONNETTORE_HTTPS_BY_ID = "checkCertificatiConnettoreHttpsById";
	public static final String CHECK_CERTIFICATO_SERVIZIO_APPLICATIVO_BY_ID = "checkCertificatoApplicativoById";
	public static final String CHECK_CERTIFICATO_SERVIZIO_APPLICATIVO_BY_NOME = "checkCertificatoApplicativoByNome";
	public static final String CHECK_CERTIFICATO_MODI_SERVIZIO_APPLICATIVO_BY_ID = "checkCertificatoModIApplicativoById";
	public static final String CHECK_CERTIFICATO_MODI_SERVIZIO_APPLICATIVO_BY_NOME = "checkCertificatoModIApplicativoByNome";
	public static final String CHECK_CERTIFICATI_CONFIGURAZIONE_JVM = "checkCertificatiJvm";
	public static final String CHECK_CERTIFICATI_CONNETTORE_HTTPS_TOKEN_POLICY_VALIDAZIONE = "checkCertificatiConnettoreHttpsTokenPolicyValidazione";
	public static final String CHECK_CERTIFICATI_VALIDAZIONE_JWT_TOKEN_POLICY_VALIDAZIONE = "checkCertificatiValidazioneJwtTokenPolicyValidazione";
	public static final String CHECK_CERTIFICATI_FORWARD_TO_JWT_TOKEN_POLICY_VALIDAZIONE = "checkCertificatiForwardToJwtTokenPolicyValidazione";
	public static final String CHECK_CERTIFICATI_CONNETTORE_HTTPS_TOKEN_POLICY_NEGOZIAZIONE = "checkCertificatiConnettoreHttpsTokenPolicyNegoziazione";
	public static final String CHECK_CERTIFICATI_SIGNED_JWT_TOKEN_POLICY_NEGOZIAZIONE = "checkCertificatiSignedJwtTokenPolicyNegoziazione";
	public static final String CHECK_CERTIFICATI_CONNETTORE_HTTPS_ATTRIBUTE_AUTHORITY = "checkCertificatiConnettoreHttpsAttributeAuthority";
	public static final String CHECK_CERTIFICATI_ATTRIBUTE_AUTHORITY_JWT_RICHIESTA = "checkCertificatiAttributeAuthorityJwtRichiesta";
	public static final String CHECK_CERTIFICATI_ATTRIBUTE_AUTHORITY_JWT_RISPOSTA = "checkCertificatiAttributeAuthorityJwtRisposta";
	public static final String CHECK_PROXY_CONFIGURAZIONE_JVM = "checkProxyJvm";
	public static final String ABILITA_PORTA_DELEGATA = "enablePortaDelegata";
	public static final String DISABILITA_PORTA_DELEGATA = "disablePortaDelegata";
	public static final String ABILITA_PORTA_APPLICATIVA = "enablePortaApplicativa";
	public static final String DISABILITA_PORTA_APPLICATIVA = "disablePortaApplicativa";
	public static final String ABILITA_CONNETTORE_MULTIPLO = "enableConnettoreMultiplo";
	public static final String DISABILITA_CONNETTORE_MULTIPLO = "disableConnettoreMultiplo";
	public static final String ABILITA_SCHEDULING_CONNETTORE_MULTIPLO = "enableSchedulingConnettoreMultiplo";
	public static final String DISABILITA_SCHEDULING_CONNETTORE_MULTIPLO = "disableSchedulingConnettoreMultiplo";
	public static final String ABILITA_SCHEDULING_CONNETTORE_MULTIPLO_RUNTIME = "enableSchedulingConnettoreMultiploRuntimeRepository";
	public static final String DISABILITA_SCHEDULING_CONNETTORE_MULTIPLO_RUNTIME = "disableSchedulingConnettoreMultiploRuntimeRepository";
	public static final String RIPULISCI_RIFERIMENTI_CACHE_PREFIX = "ripulisciRiferimentiCache";
	public static final String RIPULISCI_RIFERIMENTI_CACHE_ACCORDO_COOPERAZIONE = RIPULISCI_RIFERIMENTI_CACHE_PREFIX+"AccordoCooperazione";
	public static final String RIPULISCI_RIFERIMENTI_CACHE_API = RIPULISCI_RIFERIMENTI_CACHE_PREFIX+"Api";
	public static final String RIPULISCI_RIFERIMENTI_CACHE_EROGAZIONE = RIPULISCI_RIFERIMENTI_CACHE_PREFIX+"Erogazione";
	public static final String RIPULISCI_RIFERIMENTI_CACHE_FRUIZIONE = RIPULISCI_RIFERIMENTI_CACHE_PREFIX+"Fruizione";
	public static final String RIPULISCI_RIFERIMENTI_CACHE_SOGGETTO = RIPULISCI_RIFERIMENTI_CACHE_PREFIX+"Soggetto";
	public static final String RIPULISCI_RIFERIMENTI_CACHE_APPLICATIVO = RIPULISCI_RIFERIMENTI_CACHE_PREFIX+"Applicativo";
	public static final String RIPULISCI_RIFERIMENTI_CACHE_RUOLO = RIPULISCI_RIFERIMENTI_CACHE_PREFIX+"Ruolo";
	public static final String RIPULISCI_RIFERIMENTI_CACHE_SCOPE = RIPULISCI_RIFERIMENTI_CACHE_PREFIX+"Scope";
	public static final String RIPULISCI_RIFERIMENTI_CACHE_TOKEN_POLICY_VALIDAZIONE = RIPULISCI_RIFERIMENTI_CACHE_PREFIX+"TokenPolicyValidazione";
	public static final String RIPULISCI_RIFERIMENTI_CACHE_TOKEN_POLICY_NEGOZIAZIONE = RIPULISCI_RIFERIMENTI_CACHE_PREFIX+"TokenPolicyNegoziazione";
	public static final String RIPULISCI_RIFERIMENTI_CACHE_ATTRIBUTE_AUTHORITY = RIPULISCI_RIFERIMENTI_CACHE_PREFIX+"AttributeAuthority";
	
	
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
		
	private static final String PARAMETRO_NON_FORNITO = "parametro richiesto non fornito";
	private static final String FORMATO_NON_VALIDO_NOME_SOGGETTO = "Formato non valido (nome@tipoSoggetto/nomeSoggetto)";
	
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
			return Costanti.isTRANSACTION_ERROR_STATUS_ABILITATO();
		
		if(attributeName.equals(ConfigurazionePdD.ERRORI_INSTANCE_ID_ABILITATO))
			return Costanti.isTRANSACTION_ERROR_INSTANCE_ID_ABILITATO();
		
		if(attributeName.equals(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_BAD_REQUEST))
			return ErroriProperties.isFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_BAD_REQUEST();
		
		if(attributeName.equals(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE))
			return ErroriProperties.isFORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE();
		
		if(attributeName.equals(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR))
			return ErroriProperties.isFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR();
						
		if(attributeName.equals(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR))
			return ErroriProperties.isFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR();
		
		if(attributeName.equals(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_DETAILS))
			return Costanti.isTRANSACTION_FORCE_SPECIFIC_ERROR_DETAILS();

		if(attributeName.equals(ConfigurazionePdD.ERRORI_SOAP_USE_GOVWAY_STATUS_AS_FAULT_CODE))
			return Costanti.isTRANSACTION_ERROR_SOAP_USE_GOVWAY_STATUS_AS_FAULT_CODE();
		
		if(attributeName.equals(ConfigurazionePdD.ERRORI_SOAP_GENERATE_HTTP_HEADER_GOVWAY_CODE))
			return Costanti.isTRANSACTION_ERROR_SOAP_GENERATE_HTTP_HEADER_GOVWAY_CODE();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_CONSEGNA_CONTENUTI_APPLICATIVI))
			return TimerConsegnaContenutiApplicativi.getSTATE().name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_EVENTI))
			return TimerEventiThread.getSTATE().name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_FILE_SYSTEM_RECOVERY))
			return TimerFileSystemRecoveryThread.getSTATE().name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_GESTORE_BUSTE_ONEWAY_NON_RISCONTRATE))
			return TimerGestoreBusteNonRiscontrateLib.getSTATE_ONEWAY().name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_GESTORE_BUSTE_ASINCRONE_NON_RISCONTRATE))
			return TimerGestoreBusteNonRiscontrateLib.getSTATE_ASINCRONI().name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_ELIMINATI))
			return TimerGestoreMessaggiLib.getSTATE_MESSAGGI_ELIMINATI().name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_SCADUTI))
			return TimerGestoreMessaggiLib.getSTATE_MESSAGGI_SCADUTI().name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_NON_GESTITI))
			return TimerGestoreMessaggiLib.getSTATE_MESSAGGI_NON_GESTITI().name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_GESTORE_MESSAGGI_PULIZIA_CORRELAZIONE_APPLICATIVA))
			return TimerGestoreMessaggiLib.getSTATE_CORRELAZIONE_APPLICATIVA().name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_GESTORE_MESSAGGI_VERIFICA_CONNESSIONI_ATTIVE))
			return TimerGestoreMessaggiLib.getSTATE_VERIFICA_CONNESSIONI_ATTIVE().name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_GESTORE_PULIZIA_MESSAGGI_ANOMALI))
			return TimerGestorePuliziaMessaggiAnomaliLib.getSTATE().name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_GESTORE_REPOSITORY_BUSTE))
			return TimerGestoreRepositoryBusteLib.getSTATE().name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_MONITORAGGIO_RISORSE_THREAD))
			return TimerMonitoraggioRisorseThread.getSTATE().name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_REPOSITORY_STATEFUL_THREAD))
			return TimerRepositoryStatefulThread.getSTATE().name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_STATISTICHE_ORARIE))
			return TimerStatisticheLib.getSTATE_STATISTICHE_ORARIE().name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_STATISTICHE_GIORNALIERE))
			return TimerStatisticheLib.getSTATE_STATISTICHE_GIORNALIERE().name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_STATISTICHE_SETTIMANALI))
			return TimerStatisticheLib.getSTATE_STATISTICHE_SETTIMANALI().name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_STATISTICHE_MENSILI))
			return TimerStatisticheLib.getSTATE_STATISTICHE_MENSILI().name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_GESTORE_CHIAVI_PDND))
			return TimerGestoreChiaviPDNDLib.getState().name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_GESTORE_CACHE_CHIAVI_PDND))
			return TimerGestoreCacheChiaviPDNDLib.getState().name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_GESTORE_OPERAZIONI_REMOTE))
			return TimerGestoreOperazioniRemoteLib.getState().name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_SVECCHIAMENTO_OPERAZIONI_REMOTE))
			return TimerSvecchiamentoOperazioniRemoteLib.getState().name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_THRESHOLD_THREAD))
			return TimerThresholdThread.getSTATE().name();
		
		if(attributeName.equals(ConfigurazionePdD.TIMER_CLUSTER_DINAMICO))
			return TimerClusterDinamicoThread.getSTATE().name();
				
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
				Costanti.setTRANSACTION_ERROR_STATUS_ABILITATO( (Boolean) attribute.getValue() );
			
			else if(attribute.getName().equals(ConfigurazionePdD.ERRORI_INSTANCE_ID_ABILITATO))
				Costanti.setTRANSACTION_ERROR_INSTANCE_ID_ABILITATO( (Boolean) attribute.getValue() );
			
			else if(attribute.getName().equals(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_BAD_REQUEST))
				this.setFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_BAD_REQUEST( (Boolean) attribute.getValue() );
			
			else if(attribute.getName().equals(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE))
				this.setFORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE( (Boolean) attribute.getValue() );
			
			else if(attribute.getName().equals(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR))
				this.setFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR( (Boolean) attribute.getValue() );
						
			else if(attribute.getName().equals(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR))
				this.setFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR( (Boolean) attribute.getValue() );

			else if(attribute.getName().equals(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_DETAILS))
				Costanti.setTRANSACTION_FORCE_SPECIFIC_ERROR_DETAILS( (Boolean) attribute.getValue() );
			
			else if(attribute.getName().equals(ConfigurazionePdD.ERRORI_SOAP_USE_GOVWAY_STATUS_AS_FAULT_CODE))
				Costanti.setTRANSACTION_ERROR_SOAP_USE_GOVWAY_STATUS_AS_FAULT_CODE( (Boolean) attribute.getValue() );
			
			else if(attribute.getName().equals(ConfigurazionePdD.ERRORI_SOAP_GENERATE_HTTP_HEADER_GOVWAY_CODE))
				Costanti.setTRANSACTION_ERROR_SOAP_GENERATE_HTTP_HEADER_GOVWAY_CODE( (Boolean) attribute.getValue() );
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_CONSEGNA_CONTENUTI_APPLICATIVI))
				TimerConsegnaContenutiApplicativi.setSTATE( getTimerState(attribute.getValue()) );
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_EVENTI))
				TimerEventiThread.setSTATE( getTimerState(attribute.getValue()) );
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_FILE_SYSTEM_RECOVERY))
				TimerFileSystemRecoveryThread.setSTATE( getTimerState(attribute.getValue()) );
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_GESTORE_BUSTE_ONEWAY_NON_RISCONTRATE))
				TimerGestoreBusteNonRiscontrateLib.setSTATE_ONEWAY( getTimerState(attribute.getValue()) );
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_GESTORE_BUSTE_ASINCRONE_NON_RISCONTRATE))
				TimerGestoreBusteNonRiscontrateLib.setSTATE_ASINCRONI( getTimerState(attribute.getValue()) );
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_ELIMINATI))
				TimerGestoreMessaggiLib.setSTATE_MESSAGGI_ELIMINATI( getTimerState(attribute.getValue()) );
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_SCADUTI))
				TimerGestoreMessaggiLib.setSTATE_MESSAGGI_SCADUTI( getTimerState(attribute.getValue()) );
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_NON_GESTITI))
				TimerGestoreMessaggiLib.setSTATE_MESSAGGI_NON_GESTITI( getTimerState(attribute.getValue()) );
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_GESTORE_MESSAGGI_PULIZIA_CORRELAZIONE_APPLICATIVA))
				TimerGestoreMessaggiLib.setSTATE_CORRELAZIONE_APPLICATIVA( getTimerState(attribute.getValue()) );
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_GESTORE_MESSAGGI_VERIFICA_CONNESSIONI_ATTIVE))
				TimerGestoreMessaggiLib.setSTATE_VERIFICA_CONNESSIONI_ATTIVE( getTimerState(attribute.getValue()) );
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_GESTORE_PULIZIA_MESSAGGI_ANOMALI))
				TimerGestorePuliziaMessaggiAnomaliLib.setSTATE( getTimerState(attribute.getValue()) );
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_GESTORE_REPOSITORY_BUSTE))
				TimerGestoreRepositoryBusteLib.setSTATE( getTimerState(attribute.getValue()) );
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_MONITORAGGIO_RISORSE_THREAD))
				TimerMonitoraggioRisorseThread.setSTATE( getTimerState(attribute.getValue()) );
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_REPOSITORY_STATEFUL_THREAD))
				TimerRepositoryStatefulThread.setSTATE( getTimerState(attribute.getValue()) );
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_STATISTICHE_ORARIE))
				TimerStatisticheLib.setSTATE_STATISTICHE_ORARIE( getTimerState(attribute.getValue()) );
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_STATISTICHE_GIORNALIERE))
				TimerStatisticheLib.setSTATE_STATISTICHE_GIORNALIERE( getTimerState(attribute.getValue()) );
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_STATISTICHE_SETTIMANALI))
				TimerStatisticheLib.setSTATE_STATISTICHE_SETTIMANALI( getTimerState(attribute.getValue()) );
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_STATISTICHE_MENSILI))
				TimerStatisticheLib.setSTATE_STATISTICHE_MENSILI( getTimerState(attribute.getValue()) );
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_GESTORE_CHIAVI_PDND))
				TimerGestoreChiaviPDNDLib.setState( getTimerState(attribute.getValue()) );
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_GESTORE_CACHE_CHIAVI_PDND))
				TimerGestoreCacheChiaviPDNDLib.setState( getTimerState(attribute.getValue()) );
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_GESTORE_OPERAZIONI_REMOTE))
				TimerGestoreOperazioniRemoteLib.setState( getTimerState(attribute.getValue()) );
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_SVECCHIAMENTO_OPERAZIONI_REMOTE))
				TimerSvecchiamentoOperazioniRemoteLib.setState( getTimerState(attribute.getValue()) );
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_THRESHOLD_THREAD))
				TimerThresholdThread.setSTATE( getTimerState(attribute.getValue()) );
			
			else if(attribute.getName().equals(ConfigurazionePdD.TIMER_CLUSTER_DINAMICO))
				TimerClusterDinamicoThread.setSTATE( getTimerState(attribute.getValue()) );
			
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
			
			if(param1==null) {
				throw new MBeanException(new Exception("["+CHECK_CONNETTORE_BY_ID+"] "+PARAMETRO_NON_FORNITO));
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
		
		if(actionName.equals(CHECK_CONNETTORE_TOKEN_POLICY_VALIDATION)){
			if(params.length != 1 && params.length != 2)
				throw new MBeanException(new Exception("["+CHECK_CONNETTORE_TOKEN_POLICY_VALIDATION+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			if(params.length==1) {
				return this.checkConnettoreTokenPolicyValidazione(param1);
			}
			else {
				String param2 = null;
				if(params[1]!=null && !"".equals(params[1])){
					param2 = (String)params[1];
				}
				return this.checkConnettoreTokenPolicyValidazione(param1,param2);
			}
		}
		
		if(actionName.equals(CHECK_CONNETTORE_TOKEN_POLICY_RETRIEVE)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+CHECK_CONNETTORE_TOKEN_POLICY_RETRIEVE+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			return this.checkConnettoreTokenPolicyNegoziazione(param1);
		}
		
		if(actionName.equals(CHECK_CONNETTORE_ATTRIBUTE_AUTHORITY)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+CHECK_CONNETTORE_ATTRIBUTE_AUTHORITY+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			return this.checkConnettoreAttributeAuthority(param1);
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
			
			if(param1==null) {
				throw new MBeanException(new Exception("["+GET_CERTIFICATI_CONNETTORE_BY_ID+"] "+PARAMETRO_NON_FORNITO));
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
		
		if(actionName.equals(GET_CERTIFICATI_TOKEN_POLICY_VALIDATION)){
			if(params.length != 1 && params.length != 2)
				throw new MBeanException(new Exception("["+GET_CERTIFICATI_TOKEN_POLICY_VALIDATION+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			
			if(params.length==1) {
				return this.getCertificatiConnettoreTokenPolicyValidazione(param1);
			}
			else {
				String param2 = null;
				if(params[1]!=null && !"".equals(params[1])){
					param2 = (String)params[1];
				}
				return this.getCertificatiConnettoreTokenPolicyValidazione(param1,param2);
			}
			
		}
		
		if(actionName.equals(GET_CERTIFICATI_TOKEN_POLICY_RETRIEVE)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+GET_CERTIFICATI_TOKEN_POLICY_RETRIEVE+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			return this.getCertificatiConnettoreTokenPolicyNegoziazione(param1);
		}
		
		if(actionName.equals(GET_CERTIFICATI_ATTRIBUTE_AUTHORITY)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+GET_CERTIFICATI_ATTRIBUTE_AUTHORITY+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			return this.getCertificatiConnettoreAttributeAuthority(param1);
		}
		
		if(actionName.equals(CHECK_CERTIFICATI_CONNETTORE_HTTPS_BY_ID)){
			if(params.length != 2)
				throw new MBeanException(new Exception("["+CHECK_CERTIFICATI_CONNETTORE_HTTPS_BY_ID+"] Lunghezza parametri non corretta: "+params.length));
			
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
			
			if(param1==null) {
				throw new MBeanException(new Exception("["+CHECK_CERTIFICATI_CONNETTORE_HTTPS_BY_ID+"] "+PARAMETRO_NON_FORNITO));
			}
			return this.checkCertificatiConnettoreHttpsById(param1, soglia);
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
			
			if(param1==null) {
				throw new MBeanException(new Exception("["+CHECK_CERTIFICATO_SERVIZIO_APPLICATIVO_BY_ID+"] "+PARAMETRO_NON_FORNITO));
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
			
			if(param1==null) {
				throw new MBeanException(new Exception("["+CHECK_CERTIFICATO_SERVIZIO_APPLICATIVO_BY_NOME+"] "+PARAMETRO_NON_FORNITO));
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
			
			if(param1==null) {
				throw new MBeanException(new Exception("["+CHECK_CERTIFICATO_MODI_SERVIZIO_APPLICATIVO_BY_ID+"] "+PARAMETRO_NON_FORNITO));
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
			
			if(param1==null) {
				throw new MBeanException(new Exception("["+CHECK_CERTIFICATO_MODI_SERVIZIO_APPLICATIVO_BY_NOME+"] "+PARAMETRO_NON_FORNITO));
			}
			return this.checkCertificatoModiApplicativoByNome(param1, soglia);
		}
		
		if(actionName.equals(CHECK_CERTIFICATI_CONFIGURAZIONE_JVM)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+CHECK_CERTIFICATI_CONFIGURAZIONE_JVM+"] Lunghezza parametri non corretta: "+params.length));
			
			int soglia = -1;
			if(params[0] instanceof Integer) {
				soglia = (Integer)params[0];
			}
			else {
				soglia = Integer.valueOf(params[0].toString());
			}
			
			return this.checkCertificatiJvm(soglia);
		}
		if(actionName.equals(CHECK_PROXY_CONFIGURAZIONE_JVM)){
			return this.checkProxyJvm();
		}
		
		if(actionName.equals(CHECK_CERTIFICATI_CONNETTORE_HTTPS_TOKEN_POLICY_VALIDAZIONE)){
			if(params.length != 2 && params.length != 3)
				throw new MBeanException(new Exception("["+CHECK_CERTIFICATI_CONNETTORE_HTTPS_TOKEN_POLICY_VALIDAZIONE+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			
			String tipo = null;
			if(params.length == 3 &&
				params[1]!=null && !"".equals(params[1])){
				tipo = (String)params[1];
			}
			
			int soglia = -1;
			if(params.length == 2) {
				if(params[1] instanceof Integer) {
					soglia = (Integer)params[1];
				}
				else {
					soglia = Integer.valueOf(params[1].toString());
				}
			}
			else {
				if(params[2] instanceof Integer) {
					soglia = (Integer)params[2];
				}
				else {
					soglia = Integer.valueOf(params[2].toString());
				}
			}
			
			if(params.length == 3) {
				return this.checkCertificatiConnettoreHttpsTokenPolicyValidazione(param1, tipo, soglia);
			}
			else {
				return this.checkCertificatiConnettoreHttpsTokenPolicyValidazione(param1, soglia);
			}
		}
		if(actionName.equals(CHECK_CERTIFICATI_VALIDAZIONE_JWT_TOKEN_POLICY_VALIDAZIONE)){
			if(params.length != 2)
				throw new MBeanException(new Exception("["+CHECK_CERTIFICATI_VALIDAZIONE_JWT_TOKEN_POLICY_VALIDAZIONE+"] Lunghezza parametri non corretta: "+params.length));
			
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
			
			return this.checkCertificatiValidazioneJwtTokenPolicyValidazione(param1, soglia);
		}
		if(actionName.equals(CHECK_CERTIFICATI_FORWARD_TO_JWT_TOKEN_POLICY_VALIDAZIONE)){
			if(params.length != 2)
				throw new MBeanException(new Exception("["+CHECK_CERTIFICATI_FORWARD_TO_JWT_TOKEN_POLICY_VALIDAZIONE+"] Lunghezza parametri non corretta: "+params.length));
			
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
			
			return this.checkCertificatiForwardToJwtTokenPolicyValidazione(param1, soglia);
		}
		
		if(actionName.equals(CHECK_CERTIFICATI_CONNETTORE_HTTPS_TOKEN_POLICY_NEGOZIAZIONE)){
			if(params.length != 2)
				throw new MBeanException(new Exception("["+CHECK_CERTIFICATI_CONNETTORE_HTTPS_TOKEN_POLICY_NEGOZIAZIONE+"] Lunghezza parametri non corretta: "+params.length));
			
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
			
			return this.checkCertificatiConnettoreHttpsTokenPolicyNegoziazione(param1, soglia);
		}
		if(actionName.equals(CHECK_CERTIFICATI_SIGNED_JWT_TOKEN_POLICY_NEGOZIAZIONE)){
			if(params.length != 2)
				throw new MBeanException(new Exception("["+CHECK_CERTIFICATI_SIGNED_JWT_TOKEN_POLICY_NEGOZIAZIONE+"] Lunghezza parametri non corretta: "+params.length));
			
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
			
			return this.checkCertificatiSignedJwtTokenPolicyNegoziazione(param1, soglia);
		}
		
		if(actionName.equals(CHECK_CERTIFICATI_CONNETTORE_HTTPS_ATTRIBUTE_AUTHORITY)){
			if(params.length != 2)
				throw new MBeanException(new Exception("["+CHECK_CERTIFICATI_CONNETTORE_HTTPS_ATTRIBUTE_AUTHORITY+"] Lunghezza parametri non corretta: "+params.length));
			
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
			
			return this.checkCertificatiConnettoreHttpsAttributeAuthority(param1, soglia);
		}
		if(actionName.equals(CHECK_CERTIFICATI_ATTRIBUTE_AUTHORITY_JWT_RICHIESTA)){
			if(params.length != 2)
				throw new MBeanException(new Exception("["+CHECK_CERTIFICATI_ATTRIBUTE_AUTHORITY_JWT_RICHIESTA+"] Lunghezza parametri non corretta: "+params.length));
			
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
			
			return this.checkCertificatiAttributeAuthorityJwtRichiesta(param1, soglia);
		}
		if(actionName.equals(CHECK_CERTIFICATI_ATTRIBUTE_AUTHORITY_JWT_RISPOSTA)){
			if(params.length != 2)
				throw new MBeanException(new Exception("["+CHECK_CERTIFICATI_ATTRIBUTE_AUTHORITY_JWT_RISPOSTA+"] Lunghezza parametri non corretta: "+params.length));
			
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
			
			return this.checkCertificatiAttributeAuthorityJwtRisposta(param1, soglia);
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
		
		if(actionName.equals(ABILITA_CONNETTORE_MULTIPLO)){
			if(params.length != 2)
				throw new MBeanException(new Exception("["+ABILITA_CONNETTORE_MULTIPLO+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			
			String param2 = null;
			if(params[1]!=null && !"".equals(params[1])){
				param2 = (String)params[1];
			}
			
			return this.updateStatoConnettoreMultiplo(param1, param2, true);
		}
		
		if(actionName.equals(DISABILITA_CONNETTORE_MULTIPLO)){
			if(params.length != 2)
				throw new MBeanException(new Exception("["+DISABILITA_CONNETTORE_MULTIPLO+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			
			String param2 = null;
			if(params[1]!=null && !"".equals(params[1])){
				param2 = (String)params[1];
			}
			
			return this.updateStatoConnettoreMultiplo(param1, param2, false);
		}
		
		if(actionName.equals(ABILITA_SCHEDULING_CONNETTORE_MULTIPLO)){
			if(params.length != 2)
				throw new MBeanException(new Exception("["+ABILITA_SCHEDULING_CONNETTORE_MULTIPLO+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			
			String param2 = null;
			if(params[1]!=null && !"".equals(params[1])){
				param2 = (String)params[1];
			}
			
			return this.updateSchedulingConnettoreMultiplo(param1, param2, true);
		}
		
		if(actionName.equals(DISABILITA_SCHEDULING_CONNETTORE_MULTIPLO)){
			if(params.length != 2)
				throw new MBeanException(new Exception("["+DISABILITA_SCHEDULING_CONNETTORE_MULTIPLO+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			
			String param2 = null;
			if(params[1]!=null && !"".equals(params[1])){
				param2 = (String)params[1];
			}
			
			return this.updateSchedulingConnettoreMultiplo(param1, param2, false);
		}
		
		if(actionName.equals(ABILITA_SCHEDULING_CONNETTORE_MULTIPLO_RUNTIME)){
			if(params.length != 2)
				throw new MBeanException(new Exception("["+ABILITA_SCHEDULING_CONNETTORE_MULTIPLO_RUNTIME+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			
			String param2 = null;
			if(params[1]!=null && !"".equals(params[1])){
				param2 = (String)params[1];
			}
			
			return this.updateSchedulingConnettoreMultiploMessaggiPresiInCarico(param1, param2, true);
		}
		
		if(actionName.equals(DISABILITA_SCHEDULING_CONNETTORE_MULTIPLO_RUNTIME)){
			if(params.length != 2)
				throw new MBeanException(new Exception("["+DISABILITA_SCHEDULING_CONNETTORE_MULTIPLO_RUNTIME+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			
			String param2 = null;
			if(params[1]!=null && !"".equals(params[1])){
				param2 = (String)params[1];
			}
			
			return this.updateSchedulingConnettoreMultiploMessaggiPresiInCarico(param1, param2, false);
		}
		
		if(actionName.equals(RIPULISCI_RIFERIMENTI_CACHE_ACCORDO_COOPERAZIONE)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+RIPULISCI_RIFERIMENTI_CACHE_ACCORDO_COOPERAZIONE+"] Lunghezza parametri non corretta: "+params.length));
			
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
			
			if(param1==null) {
				throw new MBeanException(new Exception("["+RIPULISCI_RIFERIMENTI_CACHE_ACCORDO_COOPERAZIONE+"] "+PARAMETRO_NON_FORNITO));
			}
			return this.ripulisciRiferimentiCacheAccordoCooperazione(param1);
		}
		
		if(actionName.equals(RIPULISCI_RIFERIMENTI_CACHE_API)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+RIPULISCI_RIFERIMENTI_CACHE_API+"] Lunghezza parametri non corretta: "+params.length));
			
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
			
			if(param1==null) {
				throw new MBeanException(new Exception("["+RIPULISCI_RIFERIMENTI_CACHE_API+"] "+PARAMETRO_NON_FORNITO));
			}
			return this.ripulisciRiferimentiCacheApi(param1);
		}
		
		if(actionName.equals(RIPULISCI_RIFERIMENTI_CACHE_EROGAZIONE)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+RIPULISCI_RIFERIMENTI_CACHE_EROGAZIONE+"] Lunghezza parametri non corretta: "+params.length));
			
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
			
			if(param1==null) {
				throw new MBeanException(new Exception("["+RIPULISCI_RIFERIMENTI_CACHE_EROGAZIONE+"] "+PARAMETRO_NON_FORNITO));
			}
			return this.ripulisciRiferimentiCacheErogazione(param1);
		}
		
		if(actionName.equals(RIPULISCI_RIFERIMENTI_CACHE_FRUIZIONE)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+RIPULISCI_RIFERIMENTI_CACHE_FRUIZIONE+"] Lunghezza parametri non corretta: "+params.length));
			
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
			
			if(param1==null) {
				throw new MBeanException(new Exception("["+RIPULISCI_RIFERIMENTI_CACHE_FRUIZIONE+"] "+PARAMETRO_NON_FORNITO));
			}
			return this.ripulisciRiferimentiCacheFruizione(param1);
		}
		
		if(actionName.equals(RIPULISCI_RIFERIMENTI_CACHE_SOGGETTO)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+RIPULISCI_RIFERIMENTI_CACHE_SOGGETTO+"] Lunghezza parametri non corretta: "+params.length));
			
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
			
			if(param1==null) {
				throw new MBeanException(new Exception("["+RIPULISCI_RIFERIMENTI_CACHE_SOGGETTO+"] "+PARAMETRO_NON_FORNITO));
			}
			return this.ripulisciRiferimentiCacheSoggetto(param1);
		}
		
		if(actionName.equals(RIPULISCI_RIFERIMENTI_CACHE_APPLICATIVO)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+RIPULISCI_RIFERIMENTI_CACHE_APPLICATIVO+"] Lunghezza parametri non corretta: "+params.length));
			
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
			
			if(param1==null) {
				throw new MBeanException(new Exception("["+RIPULISCI_RIFERIMENTI_CACHE_APPLICATIVO+"] "+PARAMETRO_NON_FORNITO));
			}
			return this.ripulisciRiferimentiCacheApplicativo(param1);
		}
		
		if(actionName.equals(RIPULISCI_RIFERIMENTI_CACHE_RUOLO)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+RIPULISCI_RIFERIMENTI_CACHE_RUOLO+"] Lunghezza parametri non corretta: "+params.length));
			
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
			
			if(param1==null) {
				throw new MBeanException(new Exception("["+RIPULISCI_RIFERIMENTI_CACHE_RUOLO+"] "+PARAMETRO_NON_FORNITO));
			}
			return this.ripulisciRiferimentiCacheRuolo(param1);
		}
		
		if(actionName.equals(RIPULISCI_RIFERIMENTI_CACHE_SCOPE)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+RIPULISCI_RIFERIMENTI_CACHE_SCOPE+"] Lunghezza parametri non corretta: "+params.length));
			
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
			
			if(param1==null) {
				throw new MBeanException(new Exception("["+RIPULISCI_RIFERIMENTI_CACHE_SCOPE+"] "+PARAMETRO_NON_FORNITO));
			}
			return this.ripulisciRiferimentiCacheScope(param1);
		}
		
		if(actionName.equals(RIPULISCI_RIFERIMENTI_CACHE_TOKEN_POLICY_VALIDAZIONE)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+RIPULISCI_RIFERIMENTI_CACHE_TOKEN_POLICY_VALIDAZIONE+"] Lunghezza parametri non corretta: "+params.length));
			
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
			
			if(param1==null) {
				throw new MBeanException(new Exception("["+RIPULISCI_RIFERIMENTI_CACHE_TOKEN_POLICY_VALIDAZIONE+"] "+PARAMETRO_NON_FORNITO));
			}
			return this.ripulisciRiferimentiCacheTokenPolicyValidazione(param1);
		}
		
		if(actionName.equals(RIPULISCI_RIFERIMENTI_CACHE_TOKEN_POLICY_NEGOZIAZIONE)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+RIPULISCI_RIFERIMENTI_CACHE_TOKEN_POLICY_NEGOZIAZIONE+"] Lunghezza parametri non corretta: "+params.length));
			
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
			
			if(param1==null) {
				throw new MBeanException(new Exception("["+RIPULISCI_RIFERIMENTI_CACHE_TOKEN_POLICY_NEGOZIAZIONE+"] "+PARAMETRO_NON_FORNITO));
			}
			return this.ripulisciRiferimentiCacheTokenPolicyNegoziazione(param1);
		}
		
		if(actionName.equals(RIPULISCI_RIFERIMENTI_CACHE_ATTRIBUTE_AUTHORITY)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+RIPULISCI_RIFERIMENTI_CACHE_ATTRIBUTE_AUTHORITY+"] Lunghezza parametri non corretta: "+params.length));
			
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
			
			if(param1==null) {
				throw new MBeanException(new Exception("["+RIPULISCI_RIFERIMENTI_CACHE_ATTRIBUTE_AUTHORITY+"] "+PARAMETRO_NON_FORNITO));
			}
			return this.ripulisciRiferimentiCacheAttributeAuthority(param1);
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
		
		// MetaData per l'attributo timerGestoreChiaviVAR
		MBeanAttributeInfo timerGestoreChiaviVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.TIMER_GESTORE_CHIAVI_PDND,String.class.getName(),
						"Indicazione se è abilitato il timer '"+TimerGestoreChiaviPDND.ID_MODULO+"' ("+TimerState.ENABLED.name()+"/"+TimerState.DISABLED.name()+")",
							JMXUtils.JMX_ATTRIBUTE_READABLE,JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo timerGestoreCacheChiaviVAR
		MBeanAttributeInfo timerGestoreCacheChiaviVAR 
			= new MBeanAttributeInfo(ConfigurazionePdD.TIMER_GESTORE_CACHE_CHIAVI_PDND,String.class.getName(),
						"Indicazione se è abilitato il timer '"+TimerGestoreCacheChiaviPDND.ID_MODULO+"' ("+TimerState.ENABLED.name()+"/"+TimerState.DISABLED.name()+")",
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
		
		// MetaData per l'operazione checkConnettoreTokenPolicyValidazione
		MBeanOperationInfo checkConnettoreTokenPolicyValidazione 
		= new MBeanOperationInfo(CHECK_CONNETTORE_BY_NOME,"Verifica la raggiungibilità dei connettori definiti nella Token Policy di validazione con nome fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomePolicy",String.class.getName(),"Nome della Token Policy di Validazione"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		MBeanOperationInfo checkConnettoreTokenPolicyValidazione_2
		= new MBeanOperationInfo(CHECK_CONNETTORE_BY_NOME,"Verifica la raggiungibilità dei connettori definiti nella Token Policy di validazione con nome fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomePolicy",String.class.getName(),"Nome della Token Policy di Validazione"),
				new MBeanParameterInfo("tipoConnettore",String.class.getName(),"Tipo del connettore da verificare ["+ConnettoreCheck.POLICY_TIPO_ENDPOINT_INTROSPECTION+","+ConnettoreCheck.POLICY_TIPO_ENDPOINT_USERINFO+"]"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione checkConnettoreTokenPolicyNegoziazione
		MBeanOperationInfo checkConnettoreTokenPolicyNegoziazione 
		= new MBeanOperationInfo(CHECK_CONNETTORE_BY_NOME,"Verifica la raggiungibilità del connettore definito nella Token Policy di negoziazione con nome fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomePolicy",String.class.getName(),"Nome della Token Policy di Negoziazione"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione checkConnettoreAttributeAuthority
		MBeanOperationInfo checkConnettoreAttributeAuthority 
		= new MBeanOperationInfo(CHECK_CONNETTORE_BY_NOME,"Verifica la raggiungibilità del connettore definito nell'AttributeAuthority con nome fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomePolicy",String.class.getName(),"Nome dell'AttributeAuthority"),
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
		
		// MetaData per l'operazione getCertificatiConnettoreTokenPolicyValidazione
		MBeanOperationInfo getCertificatiConnettoreTokenPolicyValidazione 
		= new MBeanOperationInfo(GET_CERTIFICATI_TOKEN_POLICY_VALIDATION,"Recupera i certificati server dell'endpoit definito nella token policy di validazione con nome fornito come parametro",
			new MBeanParameterInfo[]{
					new MBeanParameterInfo("nomePolicy",String.class.getName(),"Nome della Token Policy di Validazione"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione getCertificatiConnettoreTokenPolicyValidazione_2
		MBeanOperationInfo getCertificatiConnettoreTokenPolicyValidazione_2
		= new MBeanOperationInfo(GET_CERTIFICATI_TOKEN_POLICY_VALIDATION,"Recupera i certificati server dell'endpoit definito nella token policy di validazione con nome fornito come parametro",
			new MBeanParameterInfo[]{
					new MBeanParameterInfo("nomePolicy",String.class.getName(),"Nome della Token Policy di Validazione"),
					new MBeanParameterInfo("tipoConnettore",String.class.getName(),"Tipo del connettore da verificare ["+ConnettoreCheck.POLICY_TIPO_ENDPOINT_INTROSPECTION+","+ConnettoreCheck.POLICY_TIPO_ENDPOINT_USERINFO+"]"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione getCertificatiConnettoreTokenPolicyNegoziazione
		MBeanOperationInfo getCertificatiConnettoreTokenPolicyNegoziazione 
		= new MBeanOperationInfo(GET_CERTIFICATI_TOKEN_POLICY_VALIDATION,"Recupera i certificati server dell'endpoit definito nella token policy di negoziazione con nome fornito come parametro",
			new MBeanParameterInfo[]{
					new MBeanParameterInfo("nomePolicy",String.class.getName(),"Nome della Token Policy di Negoziazione"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione getCertificatiConnettoreAttributeAuthority
		MBeanOperationInfo getCertificatiConnettoreAttributeAuthority 
		= new MBeanOperationInfo(GET_CERTIFICATI_TOKEN_POLICY_VALIDATION,"Recupera i certificati server dell'endpoit definito nell'AttributeAuthority con nome fornito come parametro",
			new MBeanParameterInfo[]{
					new MBeanParameterInfo("nomePolicy",String.class.getName(),"Nome dell'AttributeAuthority"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
			
		// MetaData per l'operazione checkCertificatiConnettoreHttpsById
		MBeanOperationInfo checkCertificatiConnettoreHttpsById 
		= new MBeanOperationInfo(CHECK_CERTIFICATI_CONNETTORE_HTTPS_BY_ID,"Verifica i certificati presenti nei keystore e truststore del connettore https che possiede l'id fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("idConnettore",long.class.getName(),"Identificativo del connettore"),
				new MBeanParameterInfo("warningThreshold",int.class.getName(),"Soglia di warning (giorni)"),
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
		
		// MetaData per l'operazione checkCertificatiJvm
		MBeanOperationInfo checkCertificatiJvm 
		= new MBeanOperationInfo(CHECK_CERTIFICATI_CONFIGURAZIONE_JVM,"Verifica il keystore e truststore associato alla jvm",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("warningThreshold",int.class.getName(),"Soglia di warning (giorni)"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione checkProxyJvm
		MBeanOperationInfo checkProxyJvm 
		= new MBeanOperationInfo(CHECK_PROXY_CONFIGURAZIONE_JVM,"Verifica l'eventuale proxy associato alla jvm",
			null,
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione checkCertificatiConnettoreHttpsTokenPolicyValidazione
		MBeanOperationInfo checkCertificatiConnettoreHttpsTokenPolicyValidazione
		= new MBeanOperationInfo(CHECK_CERTIFICATI_CONNETTORE_HTTPS_TOKEN_POLICY_VALIDAZIONE,"Verifica i certificati del connettore https definito nella Token Policy di validazione con nome fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomePolicy",String.class.getName(),"Nome della Token Policy di Validazione"),
				new MBeanParameterInfo("warningThreshold",int.class.getName(),"Soglia di warning (giorni)"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		MBeanOperationInfo checkCertificatiConnettoreHttpsTokenPolicyValidazione_2
		= new MBeanOperationInfo(CHECK_CERTIFICATI_CONNETTORE_HTTPS_TOKEN_POLICY_VALIDAZIONE,"Verifica i certificati del connettore https definito nella Token Policy di validazione con nome fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomePolicy",String.class.getName(),"Nome della Token Policy di Validazione"),
				new MBeanParameterInfo("tipoConnettore",String.class.getName(),"Tipo del connettore da verificare ["+ConnettoreCheck.POLICY_TIPO_ENDPOINT_INTROSPECTION+","+ConnettoreCheck.POLICY_TIPO_ENDPOINT_USERINFO+"]"),
				new MBeanParameterInfo("warningThreshold",int.class.getName(),"Soglia di warning (giorni)"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione checkCertificatiValidazioneJwtTokenPolicyValidazione
		MBeanOperationInfo checkCertificatiValidazioneJwtTokenPolicyValidazione
		= new MBeanOperationInfo(CHECK_CERTIFICATI_VALIDAZIONE_JWT_TOKEN_POLICY_VALIDAZIONE,"Verifica i certificati utilizzati per la validazione JWT del Token definito nella Token Policy di validazione con nome fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomePolicy",String.class.getName(),"Nome della Token Policy di Validazione"),
				new MBeanParameterInfo("warningThreshold",int.class.getName(),"Soglia di warning (giorni)"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione checkCertificatiForwardToJwtTokenPolicyValidazione
		MBeanOperationInfo checkCertificatiForwardToJwtTokenPolicyValidazione
		= new MBeanOperationInfo(CHECK_CERTIFICATI_FORWARD_TO_JWT_TOKEN_POLICY_VALIDAZIONE,"Verifica i certificati utilizzati per firmare il JWT contenente le informazioni del Token definito nella Token Policy di validazione con nome fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomePolicy",String.class.getName(),"Nome della Token Policy di Validazione"),
				new MBeanParameterInfo("warningThreshold",int.class.getName(),"Soglia di warning (giorni)"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		
		// MetaData per l'operazione checkCertificatiConnettoreHttpsTokenPolicyNegoziazione
		MBeanOperationInfo checkCertificatiConnettoreHttpsTokenPolicyNegoziazione
		= new MBeanOperationInfo(CHECK_CERTIFICATI_CONNETTORE_HTTPS_TOKEN_POLICY_NEGOZIAZIONE,"Verifica i certificati del connettore https definito nella Token Policy di negoziazione con nome fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomePolicy",String.class.getName(),"Nome della Token Policy di Negoziazione"),
				new MBeanParameterInfo("warningThreshold",int.class.getName(),"Soglia di warning (giorni)"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione checkCertificatiSignedJwtTokenPolicyNegoziazione
		MBeanOperationInfo checkCertificatiSignedJwtTokenPolicyNegoziazione
		= new MBeanOperationInfo(CHECK_CERTIFICATI_SIGNED_JWT_TOKEN_POLICY_NEGOZIAZIONE,"Verifica i certificati utilizzati per firmare l'asserzione JWT definita nella Token Policy di negoziazione con nome fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomePolicy",String.class.getName(),"Nome della Token Policy di Negoziazione"),
				new MBeanParameterInfo("warningThreshold",int.class.getName(),"Soglia di warning (giorni)"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione checkCertificatiConnettoreHttpsAttributeAuthority
		MBeanOperationInfo checkCertificatiConnettoreHttpsAttributeAuthority
		= new MBeanOperationInfo(CHECK_CERTIFICATI_CONNETTORE_HTTPS_ATTRIBUTE_AUTHORITY,"Verifica i certificati del connettore https definito nell'AttributeAuthority con nome fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomePolicy",String.class.getName(),"Nome dell'AttributeAuthority"),
				new MBeanParameterInfo("warningThreshold",int.class.getName(),"Soglia di warning (giorni)"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione checkCertificatiAttributeAuthorityJwtRichiesta
		MBeanOperationInfo checkCertificatiAttributeAuthorityJwtRichiesta
		= new MBeanOperationInfo(CHECK_CERTIFICATI_ATTRIBUTE_AUTHORITY_JWT_RICHIESTA,"Verifica i certificati utilizzati per firmare il JWT della richiesta definito nell'AttributeAuthority con nome fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomePolicy",String.class.getName(),"Nome dell'AttributeAuthority"),
				new MBeanParameterInfo("warningThreshold",int.class.getName(),"Soglia di warning (giorni)"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione checkCertificatiAttributeAuthorityJwtRisposta
		MBeanOperationInfo checkCertificatiAttributeAuthorityJwtRisposta
		= new MBeanOperationInfo(CHECK_CERTIFICATI_ATTRIBUTE_AUTHORITY_JWT_RISPOSTA,"Verifica i certificati utilizzati per firmare il JWT della richiesta definito nell'AttributeAuthority con nome fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomePolicy",String.class.getName(),"Nome dell'AttributeAuthority"),
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
		
		// MetaData per l'operazione enableConnettoreMultiplo
		MBeanOperationInfo enableConnettoreMultiplo 
		= new MBeanOperationInfo(ABILITA_PORTA_APPLICATIVA,"Abilita lo stato del connettore della porta identificato dai parametri",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomePorta",String.class.getName(),"Nome della Porta"),
				new MBeanParameterInfo("nomeConnettore",String.class.getName(),"Nome del Connettore"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione disableConnettoreMultiplo
		MBeanOperationInfo disableConnettoreMultiplo
		= new MBeanOperationInfo(DISABILITA_PORTA_APPLICATIVA,"Disabilita lo stato del connettore della porta identificato dai parametri",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomePorta",String.class.getName(),"Nome della Porta"),
				new MBeanParameterInfo("nomeConnettore",String.class.getName(),"Nome del Connettore"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione enableSchedulingConnettoreMultiplo
		MBeanOperationInfo enableSchedulingConnettoreMultiplo 
		= new MBeanOperationInfo(ABILITA_PORTA_APPLICATIVA,"Abilita lo scheduling del connettore della porta identificato dai parametri",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomePorta",String.class.getName(),"Nome della Porta"),
				new MBeanParameterInfo("nomeConnettore",String.class.getName(),"Nome del Connettore"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione disableSchedulingConnettoreMultiplo
		MBeanOperationInfo disableSchedulingConnettoreMultiplo
		= new MBeanOperationInfo(DISABILITA_PORTA_APPLICATIVA,"Disabilita lo scheduling del connettore della porta identificato dai parametri",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomePorta",String.class.getName(),"Nome della Porta"),
				new MBeanParameterInfo("nomeConnettore",String.class.getName(),"Nome del Connettore"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione enableSchedulingConnettoreMultiploRuntimeRepository
		MBeanOperationInfo enableSchedulingConnettoreMultiploRuntimeRepository
		= new MBeanOperationInfo(ABILITA_PORTA_APPLICATIVA,"Abilita lo scheduling del connettore della porta identificato dai parametri nel RuntimeRepository",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomePorta",String.class.getName(),"Nome della Porta"),
				new MBeanParameterInfo("nomeConnettore",String.class.getName(),"Nome del Connettore"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione disableSchedulingConnettoreMultiploRuntimeRepository
		MBeanOperationInfo disableSchedulingConnettoreMultiploRuntimeRepository
		= new MBeanOperationInfo(DISABILITA_PORTA_APPLICATIVA,"Disabilita lo scheduling del connettore della porta identificato dai parametri nel RuntimeRepository",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomePorta",String.class.getName(),"Nome della Porta"),
				new MBeanParameterInfo("nomeConnettore",String.class.getName(),"Nome del Connettore"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione ripulisciRiferimentiCacheAccordoCooperazione
		MBeanOperationInfo ripulisciRiferimentiCacheAccordoCooperazione 
		= new MBeanOperationInfo(RIPULISCI_RIFERIMENTI_CACHE_ACCORDO_COOPERAZIONE,"Ripulisce i riferimenti in cache dell'accordo di cooperazione identificato dal parametro id",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("id",long.class.getName(),"Identificativo dell'accordo"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione ripulisciRiferimentiCacheApi
		MBeanOperationInfo ripulisciRiferimentiCacheApi 
		= new MBeanOperationInfo(RIPULISCI_RIFERIMENTI_CACHE_API,"Ripulisce i riferimenti in cache dell'accordo identificato dal parametro id",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("id",long.class.getName(),"Identificativo dell'accordo"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione ripulisciRiferimentiCacheErogazione
		MBeanOperationInfo ripulisciRiferimentiCacheErogazione 
		= new MBeanOperationInfo(RIPULISCI_RIFERIMENTI_CACHE_EROGAZIONE,"Ripulisce i riferimenti in cache dell'erogazione identificata dal parametro id",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("id",long.class.getName(),"Identificativo dell'erogazione"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione ripulisciRiferimentiCacheFruizione
		MBeanOperationInfo ripulisciRiferimentiCacheFruizione 
		= new MBeanOperationInfo(RIPULISCI_RIFERIMENTI_CACHE_FRUIZIONE,"Ripulisce i riferimenti in cache della fruizione identificata dal parametro id",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("id",long.class.getName(),"Identificativo della fruizione"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione ripulisciRiferimentiCacheSoggetto
		MBeanOperationInfo ripulisciRiferimentiCacheSoggetto 
		= new MBeanOperationInfo(RIPULISCI_RIFERIMENTI_CACHE_SOGGETTO,"Ripulisce i riferimenti in cache del soggetto identificato dal parametro id",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("id",long.class.getName(),"Identificativo del soggetto"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione ripulisciRiferimentiCacheApplicativo
		MBeanOperationInfo ripulisciRiferimentiCacheApplicativo 
		= new MBeanOperationInfo(RIPULISCI_RIFERIMENTI_CACHE_APPLICATIVO,"Ripulisce i riferimenti in cache dell'applicativo identificato dal parametro id",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("id",long.class.getName(),"Identificativo dell'applicativo"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione ripulisciRiferimentiCacheRuolo
		MBeanOperationInfo ripulisciRiferimentiCacheRuolo 
		= new MBeanOperationInfo(RIPULISCI_RIFERIMENTI_CACHE_RUOLO,"Ripulisce i riferimenti in cache del ruolo identificato dal parametro id",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("id",long.class.getName(),"Identificativo del ruolo"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione ripulisciRiferimentiCacheScope
		MBeanOperationInfo ripulisciRiferimentiCacheScope 
		= new MBeanOperationInfo(RIPULISCI_RIFERIMENTI_CACHE_SCOPE,"Ripulisce i riferimenti in cache dello scope identificato dal parametro id",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("id",long.class.getName(),"Identificativo dello scope"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione ripulisciRiferimentiCacheTokenPolicyValidazione
		MBeanOperationInfo ripulisciRiferimentiCacheTokenPolicyValidazione 
		= new MBeanOperationInfo(RIPULISCI_RIFERIMENTI_CACHE_TOKEN_POLICY_VALIDAZIONE,"Ripulisce i riferimenti in cache della token policy identificata dal parametro id",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("id",long.class.getName(),"Identificativo della policy"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione ripulisciRiferimentiCacheTokenPolicyNegoziazione
		MBeanOperationInfo ripulisciRiferimentiCacheTokenPolicyNegoziazione 
		= new MBeanOperationInfo(RIPULISCI_RIFERIMENTI_CACHE_TOKEN_POLICY_NEGOZIAZIONE,"Ripulisce i riferimenti in cache della token policy identificata dal parametro id",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("id",long.class.getName(),"Identificativo della policy"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione ripulisciRiferimentiCacheAttributeAuthority
		MBeanOperationInfo ripulisciRiferimentiCacheAttributeAuthority 
		= new MBeanOperationInfo(RIPULISCI_RIFERIMENTI_CACHE_ATTRIBUTE_AUTHORITY,"Ripulisce i riferimenti in cache dell'Attribute Authority identificata dal parametro id",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("id",long.class.getName(),"Identificativo dell'Authority"),
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
				timerRepositoryStatefulThreadVAR,
				timerGestoreChiaviVAR, timerGestoreCacheChiaviVAR};
		
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
		listOperation.add(checkConnettoreTokenPolicyValidazione);
		listOperation.add(checkConnettoreTokenPolicyValidazione_2);
		listOperation.add(checkConnettoreTokenPolicyNegoziazione);
		listOperation.add(checkConnettoreAttributeAuthority);
		listOperation.add(getCertificatiConnettoreById);
		listOperation.add(getCertificatiConnettoreByNome);
		listOperation.add(getCertificatiConnettoreTokenPolicyValidazione);
		listOperation.add(getCertificatiConnettoreTokenPolicyValidazione_2);
		listOperation.add(getCertificatiConnettoreTokenPolicyNegoziazione);
		listOperation.add(getCertificatiConnettoreAttributeAuthority);
		listOperation.add(checkCertificatiConnettoreHttpsById);
		listOperation.add(checkCertificatoApplicativoById);
		listOperation.add(checkCertificatoApplicativoByNome);
		listOperation.add(checkCertificatoModIApplicativoById);
		listOperation.add(checkCertificatoModIApplicativoByNome);
		listOperation.add(checkCertificatiJvm);
		listOperation.add(checkProxyJvm);
		listOperation.add(checkCertificatiConnettoreHttpsTokenPolicyValidazione);
		listOperation.add(checkCertificatiConnettoreHttpsTokenPolicyValidazione_2);
		listOperation.add(checkCertificatiValidazioneJwtTokenPolicyValidazione);
		listOperation.add(checkCertificatiForwardToJwtTokenPolicyValidazione);
		listOperation.add(checkCertificatiConnettoreHttpsTokenPolicyNegoziazione);
		listOperation.add(checkCertificatiSignedJwtTokenPolicyNegoziazione);
		listOperation.add(checkCertificatiConnettoreHttpsAttributeAuthority);
		listOperation.add(checkCertificatiAttributeAuthorityJwtRichiesta);
		listOperation.add(checkCertificatiAttributeAuthorityJwtRisposta);
		listOperation.add(enablePortaDelegata);
		listOperation.add(disablePortaDelegata);
		listOperation.add(enablePortaApplicativa);
		listOperation.add(disablePortaApplicativa);
		listOperation.add(enableConnettoreMultiplo);
		listOperation.add(disableConnettoreMultiplo);
		listOperation.add(enableSchedulingConnettoreMultiplo);
		listOperation.add(disableSchedulingConnettoreMultiplo);
		listOperation.add(enableSchedulingConnettoreMultiploRuntimeRepository);
		listOperation.add(disableSchedulingConnettoreMultiploRuntimeRepository);
		listOperation.add(ripulisciRiferimentiCacheAccordoCooperazione);
		listOperation.add(ripulisciRiferimentiCacheApi);
		listOperation.add(ripulisciRiferimentiCacheErogazione);
		listOperation.add(ripulisciRiferimentiCacheFruizione);
		listOperation.add(ripulisciRiferimentiCacheSoggetto);
		listOperation.add(ripulisciRiferimentiCacheApplicativo);
		listOperation.add(ripulisciRiferimentiCacheRuolo);
		listOperation.add(ripulisciRiferimentiCacheScope);
		listOperation.add(ripulisciRiferimentiCacheTokenPolicyValidazione);
		listOperation.add(ripulisciRiferimentiCacheTokenPolicyNegoziazione);
		listOperation.add(ripulisciRiferimentiCacheAttributeAuthority);
		MBeanOperationInfo[] operations = listOperation.toArray(new MBeanOperationInfo[1]);
		
		return new MBeanInfo(className,description,attributes,constructors,operations,null);
	}
	
	/* Variabili per la gestione JMX */
	private Logger log;
	private Logger logConnettori;
	org.openspcoop2.pdd.config.ConfigurazionePdDManager configReader = null;
	org.openspcoop2.pdd.config.OpenSPCoop2Properties openspcoopProperties = null;
	
	private void logError(String msg, Exception e) {
		this.log.error(msg, e);
	}
	
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
		this.msgDiagnosticiLivelloSeverita = LogLevels.toOpenSPCoop2(this.configReader.getSeveritaMessaggiDiagnostici(),true);
		this.msgDiagnosticiLivelloSeveritaLog4J = LogLevels.toOpenSPCoop2(this.configReader.getSeveritaLog4JMessaggiDiagnostici(),true);
		this.log4jDiagnosticaAbilitato = OpenSPCoop2Logger.loggerMsgDiagnosticoAbilitato;
		this.log4jOpenSPCoopAbilitato = OpenSPCoop2Logger.loggerMsgDiagnosticoReadableAbilitato;
		this.log4jIntegrationManagerAbilitato = OpenSPCoop2Logger.loggerIntegrationManagerAbilitato;
		
		MessaggiDiagnostici msg = this.configReader.getOpenSPCoopAppenderMessaggiDiagnostici();
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
		
		Tracciamento tracciamento = this.configReader.getOpenSPCoopAppenderTracciamento();
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

	}
	
	public boolean isCacheAbilitata() {
		return this.cacheAbilitata;
	}
	
	/* Metodi di management JMX */
	public String resetCache(){
		try{
			if(!this.cacheAbilitata)
				throw new CoreException("Cache non abilitata");
			org.openspcoop2.pdd.config.ConfigurazionePdDReader.resetCache();
			FileTraceConfig.resetFileTraceAssociatePorte();
			return JMXUtils.MSG_RESET_CACHE_EFFETTUATO_SUCCESSO;
		}catch(Exception e){
			this.logError(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String prefillCache(){
		try{
			if(!this.cacheAbilitata)
				throw new CoreException("Cache non abilitata");
			org.openspcoop2.pdd.config.ConfigurazionePdDReader.prefillCache(this.openspcoopProperties.getCryptConfigAutenticazioneApplicativi());
			return JMXUtils.MSG_PREFILL_CACHE_EFFETTUATO_SUCCESSO;
		}catch(Exception e){
			this.logError(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String printStatCache(){
		try{
			if(!this.cacheAbilitata)
				throw new CoreException("Cache non abilitata");
			return org.openspcoop2.pdd.config.ConfigurazionePdDReader.printStatsCache("\n");
		}catch(Exception e){
			this.logError(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public void abilitaCache(){
		try{
			org.openspcoop2.pdd.config.ConfigurazionePdDReader.abilitaCache();
			this.cacheAbilitata = true;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
		}
	}

	public String abilitaCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond){
		try{
			org.openspcoop2.pdd.config.ConfigurazionePdDReader.abilitaCache(dimensioneCache,algoritmoCacheLRU,itemIdleTime,itemLifeSecond,
					this.openspcoopProperties.getCryptConfigAutenticazioneApplicativi());
			this.cacheAbilitata = true;
			return JMXUtils.MSG_ABILITAZIONE_CACHE_EFFETTUATA;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public void disabilitaCache() throws JMException{
		try{
			org.openspcoop2.pdd.config.ConfigurazionePdDReader.disabilitaCache();
			this.cacheAbilitata = false;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			throw new JMException(e.getMessage());
		}
	}
	public String disabilitaCacheConEsito() {
		try{
			disabilitaCache();
			return JMXUtils.MSG_DISABILITAZIONE_CACHE_EFFETTUATA;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String listKeysCache(){
		try{
			if(!this.cacheAbilitata)
				throw new CoreException("Cache non abilitata");
			return org.openspcoop2.pdd.config.ConfigurazionePdDReader.listKeysCache("\n");
		}catch(Exception e){
			this.logError(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getObjectCache(String key){
		try{
			if(!this.cacheAbilitata)
				throw new CoreException("Cache non abilitata");
			return org.openspcoop2.pdd.config.ConfigurazionePdDReader.getObjectCache(key);
		}catch(Exception e){
			this.logError(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String removeObjectCache(String key){
		try{
			if(!this.cacheAbilitata)
				throw new CoreException("Cache non abilitata");
			org.openspcoop2.pdd.config.ConfigurazionePdDReader.removeObjectCache(key);
			return JMXUtils.MSG_RIMOZIONE_CACHE_EFFETTUATA;
		}catch(Exception e){
			this.logError(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
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
		ConfigurazionePdDReader.livelloMessaggiDiagnosticiJMX = LogLevels.toLog4J(this.msgDiagnosticiLivelloSeverita);
		ConfigurazionePdDReader.severitaMessaggiDiagnosticiJMX = LogLevels.toOpenSPCoop2(this.msgDiagnosticiLivelloSeverita);
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
		ConfigurazionePdDReader.livelloLog4JMessaggiDiagnosticiJMX = LogLevels.toLog4J(this.msgDiagnosticiLivelloSeveritaLog4J);
		ConfigurazionePdDReader.severitaLog4JMessaggiDiagnosticiJMX = LogLevels.toOpenSPCoop2(this.msgDiagnosticiLivelloSeveritaLog4J);
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
		}catch(Exception e){
			this.logError(e.getMessage(),e);
		}
	}
	
	public void setFORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE(boolean value) {
		try{
			ErroriProperties.setFORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE( 
					value,
					this.openspcoopProperties.getRootDirectory(), this.log, Loader.getInstance());
		}catch(Exception e){
			this.logError(e.getMessage(),e);
		}
	}
	
	public void setFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR(boolean value) {
		try{
			ErroriProperties.setFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR( 
					value,
					this.openspcoopProperties.getRootDirectory(), this.log, Loader.getInstance());
		}catch(Exception e){
			this.logError(e.getMessage(),e);
		}
	}
		
	public void setFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR(boolean value) {
		try{
			ErroriProperties.setFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR( 
					value,
					this.openspcoopProperties.getRootDirectory(), this.log, Loader.getInstance());
		}catch(Exception e){
			this.logError(e.getMessage(),e);
		}
	}

	public String checkConnettoreById(long idConnettore) {
		try{
			ConnettoreCheck.check(idConnettore, false, this.logConnettori);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String checkConnettoreByNome(String nomeConnettore) {
		try{
			ConnettoreCheck.check(nomeConnettore, false, this.logConnettori);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String checkConnettoreTokenPolicyValidazione(String nomePolicy) {
		try{
			ConnettoreCheck.checkTokenPolicyValidazione(nomePolicy, this.logConnettori);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	public String checkConnettoreTokenPolicyValidazione(String nomePolicy, String tipoConnettore) {
		try{
			ConnettoreCheck.checkTokenPolicyValidazione(nomePolicy, tipoConnettore, this.logConnettori);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String checkConnettoreTokenPolicyNegoziazione(String nomePolicy) {
		try{
			ConnettoreCheck.checkTokenPolicyNegoziazione(nomePolicy, this.logConnettori);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String checkConnettoreAttributeAuthority(String nomePolicy) {
		try{
			ConnettoreCheck.checkAttributeAuthority(nomePolicy, this.logConnettori);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getCertificatiConnettoreById(long idConnettore) {
		try{
			return ConnettoreCheck.getCertificati(idConnettore, true);
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getCertificatiConnettoreByNome(String nomeConnettore) {
		try{
			return ConnettoreCheck.getCertificati(nomeConnettore, true);
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getCertificatiConnettoreTokenPolicyValidazione(String nomePolicy) {
		try{
			return ConnettoreCheck.getCertificatiTokenPolicyValidazione(nomePolicy, this.logConnettori);
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	public String getCertificatiConnettoreTokenPolicyValidazione(String nomePolicy, String tipoConnettore) {
		try{
			return ConnettoreCheck.getCertificatiTokenPolicyValidazione(nomePolicy, tipoConnettore, this.logConnettori);
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getCertificatiConnettoreTokenPolicyNegoziazione(String nomePolicy) {
		try{
			return ConnettoreCheck.getCertificatiTokenPolicyNegoziazione(nomePolicy, this.logConnettori);
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getCertificatiConnettoreAttributeAuthority(String nomePolicy) {
		try{
			return ConnettoreCheck.getCertificatiAttributeAuthority(nomePolicy, this.logConnettori);
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String checkCertificatiConnettoreHttpsById(long idConnettore, int sogliaWarningGiorni) {
		try{
			boolean addCertificateDetails = true;
			String separator = ": ";
			String newLine = "\n";
			CertificateCheck statoCheck = ConfigurazionePdDManager.getInstance().checkCertificatiConnettoreHttpsByIdWithoutCache(idConnettore, sogliaWarningGiorni, 
					addCertificateDetails, separator, newLine);
			return statoCheck.toString(newLine);
		}catch(Exception e){
			this.logError(e.getMessage(),e);
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
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String checkCertificatoApplicativoByNome(String idApplicativo, int sogliaWarningGiorni) {
		try{
			if(!idApplicativo.contains("@") || !idApplicativo.contains("/")) {
				throw new CoreException(FORMATO_NON_VALIDO_NOME_SOGGETTO);
			}
			String [] tmp = idApplicativo.split("@");
			if(tmp==null || tmp.length!=2 || tmp[0]==null || tmp[1]==null) {
				throw new CoreException(FORMATO_NON_VALIDO_NOME_SOGGETTO);
			}
			else {
				String nome = tmp[0];
				if(!tmp[1].contains("/")) {
					throw new CoreException(FORMATO_NON_VALIDO_NOME_SOGGETTO);
				}
				else {
					String [] tmp2 = tmp[1].split("/");
					if(tmp2==null || tmp2.length!=2 || tmp2[0]==null || tmp2[1]==null) {
						throw new CoreException(FORMATO_NON_VALIDO_NOME_SOGGETTO);
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
		}catch(Exception e){
			this.logError(e.getMessage(),e);
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
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String checkCertificatoModiApplicativoByNome(String idApplicativo, int sogliaWarningGiorni) {
		try{
			if(!idApplicativo.contains("@") || !idApplicativo.contains("/")) {
				throw new CoreException(FORMATO_NON_VALIDO_NOME_SOGGETTO);
			}
			String [] tmp = idApplicativo.split("@");
			if(tmp==null || tmp.length!=2 || tmp[0]==null || tmp[1]==null) {
				throw new CoreException(FORMATO_NON_VALIDO_NOME_SOGGETTO);
			}
			else {
				String nome = tmp[0];
				if(!tmp[1].contains("/")) {
					throw new CoreException(FORMATO_NON_VALIDO_NOME_SOGGETTO);
				}
				else {
					String [] tmp2 = tmp[1].split("/");
					if(tmp2==null || tmp2.length!=2 || tmp2[0]==null || tmp2[1]==null) {
						throw new CoreException(FORMATO_NON_VALIDO_NOME_SOGGETTO);
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
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String checkCertificatiJvm(int sogliaWarningGiorni) {
		try{
			boolean addCertificateDetails = true;
			String separator = ": ";
			String newLine = "\n";
			CertificateCheck statoCheck = ConfigurazionePdDManager.getInstance().checkCertificatiJvm(sogliaWarningGiorni, 
					addCertificateDetails, separator, newLine);
			return statoCheck.toString(newLine);
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String checkProxyJvm() {
		try{
			ConnettoreCheck.checkProxyJvm(this.logConnettori);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String checkCertificatiConnettoreHttpsTokenPolicyValidazione(String nomePolicy, int sogliaWarningGiorni) {
		try{
			boolean addCertificateDetails = true;
			String separator = ": ";
			String newLine = "\n";
			CertificateCheck statoCheck = ConfigurazionePdDManager.getInstance().checkCertificatiConnettoreHttpsTokenPolicyValidazione(nomePolicy, sogliaWarningGiorni, 
					addCertificateDetails, separator, newLine);
			return statoCheck.toString(newLine);
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	public String checkCertificatiConnettoreHttpsTokenPolicyValidazione(String nomePolicy, String tipo, int sogliaWarningGiorni) {
		try{
			boolean addCertificateDetails = true;
			String separator = ": ";
			String newLine = "\n";
			CertificateCheck statoCheck = ConfigurazionePdDManager.getInstance().checkCertificatiConnettoreHttpsTokenPolicyValidazione(nomePolicy, tipo, sogliaWarningGiorni, 
					addCertificateDetails, separator, newLine);
			return statoCheck.toString(newLine);
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}

	public String checkCertificatiValidazioneJwtTokenPolicyValidazione(String nomePolicy, int sogliaWarningGiorni) {
		try{
			boolean addCertificateDetails = true;
			String separator = ": ";
			String newLine = "\n";
			CertificateCheck statoCheck = ConfigurazionePdDManager.getInstance().checkCertificatiValidazioneJwtTokenPolicyValidazione(nomePolicy, sogliaWarningGiorni, 
					addCertificateDetails, separator, newLine);
			return statoCheck.toString(newLine);
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String checkCertificatiForwardToJwtTokenPolicyValidazione(String nomePolicy, int sogliaWarningGiorni) {
		try{
			boolean addCertificateDetails = true;
			String separator = ": ";
			String newLine = "\n";
			CertificateCheck statoCheck = ConfigurazionePdDManager.getInstance().checkCertificatiForwardToJwtTokenPolicyValidazione(nomePolicy, sogliaWarningGiorni, 
					addCertificateDetails, separator, newLine);
			return statoCheck.toString(newLine);
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String checkCertificatiConnettoreHttpsTokenPolicyNegoziazione(String nomePolicy, int sogliaWarningGiorni) {
		try{
			boolean addCertificateDetails = true;
			String separator = ": ";
			String newLine = "\n";
			CertificateCheck statoCheck = ConfigurazionePdDManager.getInstance().checkCertificatiConnettoreHttpsTokenPolicyNegoziazione(nomePolicy, sogliaWarningGiorni, 
					addCertificateDetails, separator, newLine);
			return statoCheck.toString(newLine);
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String checkCertificatiSignedJwtTokenPolicyNegoziazione(String nomePolicy, int sogliaWarningGiorni) {
		try{
			boolean addCertificateDetails = true;
			String separator = ": ";
			String newLine = "\n";
			CertificateCheck statoCheck = ConfigurazionePdDManager.getInstance().checkCertificatiSignedJwtTokenPolicyNegoziazione(nomePolicy, sogliaWarningGiorni, 
					addCertificateDetails, separator, newLine);
			return statoCheck.toString(newLine);
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String checkCertificatiConnettoreHttpsAttributeAuthority(String nomePolicy, int sogliaWarningGiorni) {
		try{
			boolean addCertificateDetails = true;
			String separator = ": ";
			String newLine = "\n";
			CertificateCheck statoCheck = ConfigurazionePdDManager.getInstance().checkCertificatiConnettoreHttpsAttributeAuthority(nomePolicy, sogliaWarningGiorni, 
					addCertificateDetails, separator, newLine);
			return statoCheck.toString(newLine);
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String checkCertificatiAttributeAuthorityJwtRichiesta(String nomePolicy, int sogliaWarningGiorni) {
		try{
			boolean addCertificateDetails = true;
			String separator = ": ";
			String newLine = "\n";
			CertificateCheck statoCheck = ConfigurazionePdDManager.getInstance().checkCertificatiAttributeAuthorityJwtRichiesta(nomePolicy, sogliaWarningGiorni, 
					addCertificateDetails, separator, newLine);
			return statoCheck.toString(newLine);
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	
	public String checkCertificatiAttributeAuthorityJwtRisposta(String nomePolicy, int sogliaWarningGiorni) {
		try{
			boolean addCertificateDetails = true;
			String separator = ": ";
			String newLine = "\n";
			CertificateCheck statoCheck = ConfigurazionePdDManager.getInstance().checkCertificatiAttributeAuthorityJwtRisposta(nomePolicy, sogliaWarningGiorni, 
					addCertificateDetails, separator, newLine);
			return statoCheck.toString(newLine);
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String updateStatoPortaDelegata(String nomePorta, boolean enable) {
		try{
			IDPortaDelegata idPD = new IDPortaDelegata();
			idPD.setNome(nomePorta);
			this.configReader.updateStatoPortaDelegata(idPD, enable ? StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String updateStatoPortaApplicativa(String nomePorta, boolean enable) {
		try{
			IDPortaApplicativa idPA = new IDPortaApplicativa();
			idPA.setNome(nomePorta);
			this.configReader.updateStatoPortaApplicativa(idPA, enable ? StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String updateStatoConnettoreMultiplo(String nomePorta, String nomeConnettore, boolean enable) {
		try{
			IDPortaApplicativa idPA = new IDPortaApplicativa();
			idPA.setNome(nomePorta);
			String nomeServizioApplicativo = this.configReader.updateStatoConnettoreMultiplo(idPA, nomeConnettore, enable ? StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
			if(nomeServizioApplicativo==null) {
				throw new CoreException("Connettore '"+nomeConnettore+"' non trovato nella porta '"+nomePorta+"'");
			}
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String updateSchedulingConnettoreMultiplo(String nomePorta, String nomeConnettore, boolean enable) {
		try{
			IDPortaApplicativa idPA = new IDPortaApplicativa();
			idPA.setNome(nomePorta);
			String nomeServizioApplicativo = this.configReader.updateSchedulingConnettoreMultiplo(idPA, nomeConnettore, enable ? StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
			if(nomeServizioApplicativo==null) {
				throw new CoreException("Connettore '"+nomeConnettore+"' non trovato nella porta '"+nomePorta+"'");
			}
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String updateSchedulingConnettoreMultiploMessaggiPresiInCarico(String nomePorta, String nomeConnettore, boolean enable) {
		try{
			IDPortaApplicativa idPA = new IDPortaApplicativa();
			idPA.setNome(nomePorta);
			PortaApplicativa pa = this.configReader.getPortaApplicativa(idPA, null); // prendo volutamente quello in cache
			String nomeServizioApplicativo = null;
			if(pa.sizeServizioApplicativoList()>0) {
				for (PortaApplicativaServizioApplicativo paSA : pa.getServizioApplicativoList()) {
					String nomePaSA = paSA.getDatiConnettore()!= null ? paSA.getDatiConnettore().getNome() : CostantiConfigurazione.NOME_CONNETTORE_DEFAULT;
					if(nomeConnettore.equals(nomePaSA)) {
						nomeServizioApplicativo = paSA.getNome();
						break;
					}
				}
			}
			if(nomeServizioApplicativo==null) {
				throw new CoreException("Connettore '"+nomeConnettore+"' non trovato nella porta '"+nomePorta+"'");
			}
			
			DBManager dbManager = DBManager.getInstance();
			Resource resource = null;
			IDSoggetto dominio = this.openspcoopProperties.getIdentitaPortaDefaultWithoutProtocol();
			String modulo = this.getClass().getName()+".schedulingConnettoreMultiplo";
			try {
				resource = dbManager.getResource(dominio, modulo, null);
				Connection c = (Connection) resource.getResource();
				boolean debug = this.openspcoopProperties.isTimerConsegnaContenutiApplicativiSchedulingDebug();
				boolean checkEliminazioneLogica = this.openspcoopProperties.isTimerConsegnaContenutiApplicativiSchedulingCheckEliminazioneLogica();
				int row = -1;
				String op = "";
				if(enable) {
					op = "abilitato";
					row = GestoreMessaggi.abilitaSchedulingMessaggiDaRiconsegnareIntoBox(nomeServizioApplicativo, checkEliminazioneLogica,
							OpenSPCoop2Logger.getLoggerOpenSPCoopConsegnaContenutiSql(debug), c,  debug);
				}else {
					op = "disabilitato";
					row = GestoreMessaggi.disabilitaSchedulingMessaggiDaRiconsegnareIntoBox(nomeServizioApplicativo, checkEliminazioneLogica,
							OpenSPCoop2Logger.getLoggerOpenSPCoopConsegnaContenutiSql(debug), c,  debug);
				}
				if(row>=0) {
					return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO_PREFIX+op+" scheduling in "+row+" messagg"+(row==1 ? "io" : "i");
				}
			}finally {
				try{
					dbManager.releaseResource(dominio, modulo, resource);
				}catch(Exception eClose){
					// close
				}
			}
			
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String ripulisciRiferimentiCacheAccordoCooperazione(long id) {
		try{
			GestoreCacheCleaner.removeAccordoCooperazione(id);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String ripulisciRiferimentiCacheApi(long id) {
		try{
			GestoreCacheCleaner.removeAccordoServizioParteComune(id);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String ripulisciRiferimentiCacheErogazione(long id) {
		try{
			GestoreCacheCleaner.removeErogazione(id);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String ripulisciRiferimentiCacheFruizione(long id) {
		try{
			GestoreCacheCleaner.removeFruizione(id);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String ripulisciRiferimentiCacheSoggetto(long id) {
		try{
			GestoreCacheCleaner.removeSoggetto(id);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String ripulisciRiferimentiCacheApplicativo(long id) {
		try{
			GestoreCacheCleaner.removeApplicativo(id);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String ripulisciRiferimentiCacheRuolo(long id) {
		try{
			GestoreCacheCleaner.removeRuolo(id);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String ripulisciRiferimentiCacheScope(long id) {
		try{
			GestoreCacheCleaner.removeScope(id);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String ripulisciRiferimentiCacheTokenPolicyValidazione(long id) {
		try{
			GestoreCacheCleaner.removeGenericProperties(id);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String ripulisciRiferimentiCacheTokenPolicyNegoziazione(long id) {
		try{
			GestoreCacheCleaner.removeGenericProperties(id);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String ripulisciRiferimentiCacheAttributeAuthority(long id) {
		try{
			GestoreCacheCleaner.removeGenericProperties(id);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Exception e){
			this.logError(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}

}
