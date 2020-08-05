/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.pdd.logger.filetrace.FileTraceGovWayState;

/**
 * InformazioniStatoPorta
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InformazioniStatoPorta {

	public String formatStatoPorta(String versionePdD, 
			String versioneBaseDati,
			String confDir, String versioneJava, String vendorJava, String messageFactory,
			String statoServizioPD,String statoServizioPD_abilitazioni,String statoServizioPD_disabilitazioni,
			String statoServizioPA,String statoServizioPA_abilitazioni,String statoServizioPA_disabilitazioni,
			String statoServizioIM,
			String livelloSeveritaDiagnostici,String livelloSeveritaDiagnosticiLog4j,
			boolean log4j_diagnostica,  boolean log4j_openspcoop, boolean log4j_integrationManager, 
			boolean tracciamento, boolean dumpPD, boolean dumpPA,
			boolean log4j_tracciamento, boolean log4j_dump,
			String fileTraceGovWayState,
			boolean errorSpecificTypeProcessRequest, boolean errorSpecificTypeProcessResponse, boolean errorSpecificTypeInternalError,
			boolean errorStatus, boolean errorStatusInSoapFaultCode,
			boolean errorSpecificDetails, boolean errorInstanceId, boolean errorGenerateHttpErrorCodeInSoap,
			String infoDatabase, Map<String, String> infoConnessioneAltriDB,
			String infoSSL, String infoCryptographyKeyLength, 
			String infoCharset, String infoInternazionalizzazione, String infoTimeZone,  
			String infoProprietaJavaNetworking, String infoProprietaJavaAltro, String infoProprietaSistema,
			String infoProtocolli,
			String informazioniInstallazione,
			InformazioniStatoPortaCache ... cache){
		return formatStatoPorta(versionePdD, versioneBaseDati, confDir, versioneJava, vendorJava, messageFactory,
				statoServizioPD,statoServizioPD_abilitazioni,statoServizioPD_disabilitazioni,
				statoServizioPA,statoServizioPA_abilitazioni,statoServizioPA_disabilitazioni,
				statoServizioIM,
				livelloSeveritaDiagnostici, livelloSeveritaDiagnosticiLog4j,
				log4j_diagnostica, log4j_openspcoop, log4j_integrationManager,
				tracciamento, dumpPD, dumpPA,
				log4j_tracciamento, log4j_dump,
				fileTraceGovWayState,
				errorSpecificTypeProcessRequest, errorSpecificTypeProcessResponse, errorSpecificTypeInternalError,
				errorStatus, errorStatusInSoapFaultCode,
				errorSpecificDetails, errorInstanceId, errorGenerateHttpErrorCodeInSoap,
				infoDatabase, infoConnessioneAltriDB,
				infoSSL, infoCryptographyKeyLength, 
				infoCharset, infoInternazionalizzazione, infoTimeZone,
				infoProprietaJavaNetworking, infoProprietaJavaAltro, infoProprietaSistema,
				infoProtocolli,
				null,null,null,
				null,null,
				null, null,null,
				null, null,
				null, null, null, null,
				null, null, null, null, null, null,
				null, null,
				null, null, null, null, null,
				informazioniInstallazione,
				cache);
	}
	
	public String formatStatoPorta(String versionePdD, 
			String versioneBaseDati,
			String confDir, String versioneJava, String vendorJava, String messageFactory,
			String statoServizioPD,String statoServizioPD_abilitazioni,String statoServizioPD_disabilitazioni,
			String statoServizioPA,String statoServizioPA_abilitazioni,String statoServizioPA_disabilitazioni,
			String statoServizioIM,
			String livelloSeveritaDiagnostici,String livelloSeveritaDiagnosticiLog4j,
			boolean log4j_diagnostica,  boolean log4j_openspcoop, boolean log4j_integrationManager, 
			boolean tracciamento, boolean dumpPD, boolean dumpPA, 
			boolean log4j_tracciamento, boolean log4j_dump,
			String fileTraceGovWayState,
			boolean errorSpecificTypeProcessRequest, boolean errorSpecificTypeProcessResponse, boolean errorSpecificTypeInternalError,
			boolean errorStatus, boolean errorStatusInSoapFaultCode,
			boolean errorSpecificDetails, boolean errorInstanceId, boolean errorGenerateHttpErrorCodeInSoap,
			String infoDatabase, Map<String, String> infoConnessioneAltriDB, 
			String infoSSL, String infoCryptographyKeyLength, 
			String infoCharset, String infoInternazionalizzazione, String infoTimeZone, 
			String infoProprietaJavaNetworking, String infoProprietaJavaAltro, String infoProprietaSistema,
			String infoProtocolli,
			String statoConnessioniDB, Map<String, String> statoConnessioniAltriDB, String statoConnessioniJMS,
			String statoTransazioniId, String statoTransazioniIdProtocollo,
			String statoTimerVerificaConnessioni, String statoConnessioniPD, String statoConnessioniPA, 
			String statoTimerConsegnaAsincrona,  List<InformazioniStatoPoolThreads> statoPoolThread,
			String statoTimerGenerazioneStatisticheOrarie, String statoTimerGenerazioneStatisticheGiornaliere, String statoTimerGenerazioneSettimanali, String statoTimerGenerazioneMensili,
			String statoTimerMessaggiEliminati, String statoTimerMessaggiScaduti, String statoTimerBuste, String statoTimerCorrelazioneApplicativa, String statoTimerMessaggiNonGestiti, String statoTimerMessaggiAnomali,
			String statoTimerMonitoraggioRisorse, String statoTimerThreshold,
			String statoTimerEventi, String statoTimerFileSystemRecovery, String statoTimerBusteOneway, String statoTimerBusteAsincrone, String statoTimerRepositoryStateful,
			String informazioniInstallazione,			
			InformazioniStatoPortaCache ... cache){
		
		StringBuilder bf = new StringBuilder();
		
		// informazioni generali
		
		bf.append("\n");
		bf.append("===========================\n");
		bf.append("Informazioni Generali\n");
		bf.append("===========================\n");
		bf.append("\n");
		format(bf, versionePdD, "Versione PdD");
		bf.append("\n");
		format(bf, versioneBaseDati, "Versione BaseDati");
		bf.append("\n");
		format(bf, confDir, "Directory Configurazione");
		bf.append("\n");
		format(bf, vendorJava, "Vendor Java");
		bf.append("\n");
		format(bf, versioneJava, "Versione Java");
		bf.append("\n");
		format(bf, messageFactory, "Message Factory");
		bf.append("\n");
		
		bf.append("\n");
		bf.append("================================\n");
		bf.append("Stato Servizi\n");
		bf.append("================================\n");
		bf.append("\n");
		format(bf, statoServizioPD, "Porta Delegata");
		bf.append("\n");
		format(bf, statoServizioPD_abilitazioni, "Porta Delegata (abilitazioni puntuali)");
		bf.append("\n");
		format(bf, statoServizioPD_disabilitazioni, "Porta Delegata (disabilitazioni puntuali)");
		bf.append("\n");
		format(bf, statoServizioPA, "Porta Applicativa");
		bf.append("\n");
		format(bf, statoServizioPA_abilitazioni, "Porta Applicativa (abilitazioni puntuali)");
		bf.append("\n");
		format(bf, statoServizioPA_disabilitazioni, "Porta Applicativa (disabilitazioni puntuali)");
		bf.append("\n");
		format(bf, statoServizioIM, "Integration Manager");
		bf.append("\n");
		
		bf.append("===========================\n");
		bf.append("Informazioni Diagnostica\n");
		bf.append("===========================\n");
		bf.append("\n");
		format(bf, livelloSeveritaDiagnostici, "Severità");
		format(bf, livelloSeveritaDiagnosticiLog4j, "Severità Log4j");
		format(bf, log4j_diagnostica ? "abilitato" : "disabilitato", "Log4J govway_diagnostici.log");
		format(bf, log4j_openspcoop ? "abilitato" : "disabilitato", "Log4J openspcoop2.log");
		format(bf, log4j_integrationManager ? "abilitato" : "disabilitato", "Log4J openspcoop2_integrationManager.log");
		bf.append("\n");
		
		bf.append("===========================\n");
		bf.append("Informazioni Tracciamento\n");
		bf.append("===========================\n");
		bf.append("\n");
		format(bf, tracciamento ? "abilitato" : "disabilitato", "Buste");
		format(bf, dumpPD ? "abilitato" : "disabilitato", "Dump Binario Porta Delegata");
		format(bf, dumpPA ? "abilitato" : "disabilitato", "Dump Binario Porta Applicativa");
		format(bf, log4j_tracciamento ? "abilitato" : "disabilitato", "Log4J govway_tracciamento.log");
		format(bf, log4j_dump ? "abilitato" : "disabilitato", "Log4J openspcoop2_dump.log");
		FileTraceGovWayState fileTraceGovWayStateObject = null;
		if(fileTraceGovWayState!=null && !"".equals(fileTraceGovWayState)) {
			fileTraceGovWayStateObject = FileTraceGovWayState.toConfig(fileTraceGovWayState);
			format(bf, fileTraceGovWayStateObject.isEnabled() ? "abilitato" : "disabilitato", "FileTrace");
			if(fileTraceGovWayStateObject.isEnabled()) {
				format(bf, fileTraceGovWayStateObject.getPath(), "FileTrace path");
				format(bf, fileTraceGovWayStateObject.getLastModified(), "FileTrace lastModified");
				format(bf, fileTraceGovWayStateObject.isEnabledInErogazione() ? "abilitato" : "disabilitato", "FileTrace inPAContent");
				format(bf, fileTraceGovWayStateObject.isEnabledOutErogazione() ? "abilitato" : "disabilitato", "FileTrace outPAContent");
				format(bf, fileTraceGovWayStateObject.isEnabledInFruizione() ? "abilitato" : "disabilitato", "FileTrace inPDContent");
				format(bf, fileTraceGovWayStateObject.isEnabledOutFruizione() ? "abilitato" : "disabilitato", "FileTrace outPDContent");
			}
		}
		bf.append("\n");

		bf.append("===========================\n");
		bf.append("Errori generati dal Gateway\n");
		bf.append("===========================\n");
		bf.append("\n");
		format(bf, errorSpecificTypeProcessRequest ? "abilitato" : "disabilitato", "GovWay-Transaction-ErrorType Gestione Richiesta");
		format(bf, errorSpecificTypeProcessResponse ? "abilitato" : "disabilitato", "GovWay-Transaction-ErrorType Gestione Risposta");
		format(bf, errorSpecificTypeInternalError ? "abilitato" : "disabilitato", "GovWay-Transaction-ErrorType Errore Interno");
		format(bf, errorStatus ? "abilitato" : "disabilitato", "GovWay-Transaction-ErrorStatus Http/Problem");
		format(bf, errorStatusInSoapFaultCode ? "abilitato" : "disabilitato", "GovWay-Transaction-ErrorStatus SOAP Fault Code");
		format(bf, errorSpecificDetails ? "abilitato" : "disabilitato", "Dettaglio errore puntuale");
		format(bf, errorInstanceId ? "abilitato" : "disabilitato", "Generazione claim 'instance' nei Problem");
		format(bf, errorGenerateHttpErrorCodeInSoap ? "abilitato" : "disabilitato", "Http header 'GovWay-Transaction-ErrorCode' su protocollo SOAP");
		bf.append("\n");
		
		bf.append("=============================\n");
		bf.append("Informazioni Database Runtime\n");
		bf.append("=============================\n");
		bf.append("\n");
		bf.append(infoDatabase);
		bf.append("\n");
		
		if(infoConnessioneAltriDB!=null && infoConnessioneAltriDB.size()>0) {
			Iterator<String> it = infoConnessioneAltriDB.keySet().iterator();
			while (it.hasNext()) {
				String idAltroDB = (String) it.next();
				String infoConnessioniAltroDB = infoConnessioneAltriDB.get(idAltroDB);
				bf.append("================================================\n");
				bf.append("Informazioni Database "+idAltroDB+" \n");
				bf.append("================================================\n");
				bf.append("\n");
				bf.append(infoConnessioniAltroDB);
				bf.append("\n");
				bf.append("\n");
			}
		}
		
		bf.append("===========================\n");
		bf.append("Informazioni SSL\n");
		bf.append("===========================\n");
		bf.append("\n");
		bf.append(infoSSL);
		bf.append("\n");
		
		bf.append("============================\n");
		bf.append("Informazioni CipherKeyLength\n");
		bf.append("============================\n");
		bf.append("\n");
		bf.append(infoCryptographyKeyLength);
		bf.append("\n");
		
		bf.append("====================\n");
		bf.append("Informazioni Charset\n");
		bf.append("====================\n");
		bf.append("\n");
		bf.append(infoCharset);
		bf.append("\n");
		
		bf.append("===================================\n");
		bf.append("Informazioni Internazionalizzazione\n");
		bf.append("===================================\n");
		bf.append("\n");
		bf.append(infoInternazionalizzazione);
		bf.append("\n");
		
		bf.append("===================================\n");
		bf.append("Informazioni TimeZone\n");
		bf.append("===================================\n");
		bf.append("\n");
		bf.append(infoTimeZone);
		bf.append("\n");
		bf.append("\n");
		
		bf.append("======================================\n");
		bf.append("Informazioni Proprietà Java Networking\n");
		bf.append("======================================\n");
		bf.append("\n");
		bf.append(infoProprietaJavaNetworking);
		bf.append("\n");
		bf.append("\n");
		
		bf.append("======================================================\n");
		bf.append("Informazioni Altre Proprietà Java (escluso Networking)\n");
		bf.append("======================================================\n");
		bf.append("\n");
		bf.append(infoProprietaJavaAltro);
		bf.append("\n");
		bf.append("\n");
		
		bf.append("==============================\n");
		bf.append("Informazioni Proprietà Sistema\n");
		bf.append("==============================\n");
		bf.append("\n");
		bf.append(infoProprietaSistema);
		bf.append("\n");
		bf.append("\n");
		
		bf.append("===========================\n");
		bf.append("Informazioni Protocolli\n");
		bf.append("===========================\n");
		bf.append("\n");
		bf.append(infoProtocolli);
		bf.append("\n");
		bf.append("\n");
		
		if(cache!=null){
			for (int i = 0; i < cache.length; i++) {
				bf.append("===========================\n");
				bf.append("Cache "+cache[i].getNomeCache()+"\n");
				bf.append("===========================\n");
				bf.append("\n");
				format(bf, cache[i].isEnabled()+"", "Abilitata");
				if(cache[i].getStatoCache()!=null){
					bf.append(cache[i].getStatoCache());
				}
				bf.append("\n");
				bf.append("\n");
			}
		}
		
		if(statoConnessioniDB!=null){
			bf.append("======================================\n");
			bf.append("Connessioni Attive al Database Runtime\n");
			bf.append("======================================\n");
			bf.append("\n");
			bf.append(statoConnessioniDB);
			bf.append("\n");
			bf.append("\n");
		}
		
		if(statoConnessioniAltriDB!=null && statoConnessioniAltriDB.size()>0) {
			Iterator<String> it = statoConnessioniAltriDB.keySet().iterator();
			while (it.hasNext()) {
				String idAltroDB = (String) it.next();
				String statoConnessioniAltroDB = statoConnessioniAltriDB.get(idAltroDB);
				bf.append("================================================\n");
				bf.append("Connessioni Attive al Database "+idAltroDB+" \n");
				bf.append("================================================\n");
				bf.append("\n");
				bf.append(statoConnessioniAltroDB);
				bf.append("\n");
				bf.append("\n");
			}
		}
		
		if(statoConnessioniJMS!=null){
			bf.append("================================\n");
			bf.append("Connessioni Attive al Broker JMS\n");
			bf.append("================================\n");
			bf.append("\n");
			bf.append(statoConnessioniJMS);
			bf.append("\n");
			bf.append("\n");
		}
		
		if(statoTransazioniId!=null){
			bf.append("=======================================\n");
			bf.append("Identificativi delle Transazioni Attive\n");
			bf.append("=======================================\n");
			bf.append("\n");
			bf.append(statoTransazioniId);
			bf.append("\n");
			bf.append("\n");
		}
		
		if(statoTransazioniIdProtocollo!=null){
			bf.append("=====================================================\n");
			bf.append("Identificativi di Protocollo delle Transazioni Attive\n");
			bf.append("=====================================================\n");
			bf.append("\n");
			bf.append(statoTransazioniIdProtocollo);
			bf.append("\n");
			bf.append("\n");
		}
		
		if(statoConnessioniPD!=null || statoTimerVerificaConnessioni!=null){
			bf.append("=========================================================\n");
			bf.append("Connessioni HTTP Attive in uscita dal modulo InoltroBuste\n");
			bf.append("=========================================================\n");
			bf.append("\n");
			bf.append("Timer Verifica Connessioni: ").append(statoTimerVerificaConnessioni);
			if(statoConnessioniPD!=null){
				bf.append("\n");
				bf.append(statoConnessioniPD);
			}
			bf.append("\n");
			bf.append("\n");
		}
		
		if(statoConnessioniPA!=null || statoTimerVerificaConnessioni!=null){
			bf.append("=========================================================================\n");
			bf.append("Connessioni HTTP Attive in uscita dal modulo ConsegnaContenutiApplicativi\n");
			bf.append("=========================================================================\n");
			bf.append("\n");
			bf.append("Timer Verifica Connessioni: ").append(statoTimerVerificaConnessioni);
			if(statoConnessioniPA!=null){
				bf.append("\n");
				bf.append(statoConnessioniPA);
			}
			bf.append("\n");
			bf.append("\n");
		}
		
		if((statoPoolThread!=null && !statoPoolThread.isEmpty()) || statoTimerConsegnaAsincrona!=null){
			bf.append("=========================================================\n");
			if(statoPoolThread!=null){
				bf.append("Stato Thread Pool per la Consegna agli Applicativi\n");
			}
			else {
				bf.append("Consegna Asincrona agli Applicativi\n");
			}
			bf.append("=========================================================\n");
			bf.append("\n");
			bf.append("Timer: ").append(statoTimerConsegnaAsincrona);
			if(statoPoolThread!=null && !statoPoolThread.isEmpty()){
				for (InformazioniStatoPoolThreads informazioniStatoPoolThread : statoPoolThread) {
					bf.append("\n");
					bf.append(informazioniStatoPoolThread.getNomeCoda());
					bf.append("\n- ");
					bf.append("Stato: ").append(informazioniStatoPoolThread.getStato());
					bf.append("\n- ");
					bf.append("Configurazione: ").append(informazioniStatoPoolThread.getConfigurazione());
					bf.append("\n- ");
					bf.append("Connettori prioritari: ").append(informazioniStatoPoolThread.getConnettoriPrioritari());
					bf.append("\n- ");
					bf.append("Applicativi prioritari: ").append(informazioniStatoPoolThread.getApplicativiPrioritari());
				}
			}
			bf.append("\n");
			bf.append("\n");
		}
	
		if(statoTimerGenerazioneStatisticheOrarie!=null || 
				statoTimerGenerazioneStatisticheGiornaliere!=null ||
				statoTimerGenerazioneSettimanali!=null ||
				statoTimerGenerazioneMensili!=null) {
			bf.append("===================================\n");
			bf.append("Timers Generazione delle Statistiche\n");
			bf.append("===================================\n");
			bf.append("\n");
			bf.append("Orarie: ").append(statoTimerGenerazioneStatisticheOrarie);
			bf.append("\n");
			bf.append("Giornaliere: ").append(statoTimerGenerazioneStatisticheGiornaliere);
			bf.append("\n");
			bf.append("Settimanali: ").append(statoTimerGenerazioneSettimanali);
			bf.append("\n");
			bf.append("Mensili: ").append(statoTimerGenerazioneMensili);
			bf.append("\n");
			bf.append("\n");
		}
		
		if(statoTimerMessaggiEliminati!=null || 
				statoTimerMessaggiScaduti!=null ||
				statoTimerBuste!=null ||
				statoTimerCorrelazioneApplicativa!=null ||
				statoTimerMessaggiNonGestiti!=null ||
				statoTimerMessaggiAnomali!=null) {
			bf.append("====================================\n");
			bf.append("Timers Pulizia Repository di Runtime\n");
			bf.append("====================================\n");
			bf.append("\n");
			bf.append("Messaggi Gestiti: ").append(statoTimerMessaggiEliminati);
			bf.append("\n");
			bf.append("Messaggi Scaduti: ").append(statoTimerMessaggiScaduti);
			bf.append("\n");
			bf.append("Buste Gestite/Scadute: ").append(statoTimerBuste);
			bf.append("\n");
			bf.append("Correlazioni Applicative Scadute: ").append(statoTimerCorrelazioneApplicativa);
			bf.append("\n");
			bf.append("Richieste Parziali: ").append(statoTimerMessaggiNonGestiti);
			bf.append("\n");
			bf.append("Messaggi Inconsistenti: ").append(statoTimerMessaggiAnomali);
			bf.append("\n");
			bf.append("\n");
		}
		
		if(statoTimerMonitoraggioRisorse!=null || 
				statoTimerThreshold!=null) {
			bf.append("==================================\n");
			bf.append("Timers Monitoraggio delle Risorse\n");
			bf.append("==================================\n");
			bf.append("\n");
			bf.append("Risorse di Sistema: ").append(statoTimerMonitoraggioRisorse);
			bf.append("\n");
			bf.append("Spazio Disco: ").append(statoTimerThreshold);
			bf.append("\n");
			bf.append("\n");
		}
		
		if(statoTimerEventi!=null || 
				statoTimerFileSystemRecovery!=null ||
				statoTimerBusteOneway!=null ||
				statoTimerBusteAsincrone!=null ||
				statoTimerRepositoryStateful!=null) {
			bf.append("====================================\n");
			bf.append("Timers Pulizia Repository di Runtime\n");
			bf.append("====================================\n");
			bf.append("\n");
			bf.append("Generazione Eventi: ").append(statoTimerEventi);
			bf.append("\n");
			bf.append("FileSystem Recovery: ").append(statoTimerFileSystemRecovery);
			bf.append("\n");
			bf.append("Rispedizione Ack Oneway: ").append(statoTimerBusteOneway);
			bf.append("\n");
			bf.append("Rispedizione Ack Asincroni: ").append(statoTimerBusteAsincrone);
			bf.append("\n");
			bf.append("Repository Stateful: ").append(statoTimerRepositoryStateful);
			bf.append("\n");
			bf.append("\n");
		}

		bf.append("===========================\n");
		bf.append("Informazioni Installazione\n");
		bf.append("===========================\n");
		bf.append("\n");
		bf.append(informazioniInstallazione);
		bf.append("\n");
		bf.append("\n");
		
		return bf.toString();
	}
	
	private void format(StringBuilder bf,String v,String label){
		if(v==null || "".equals(v)){
			bf.append(label+": informazione non disponibile\n");
		}
		else{
			if(v.contains("\n")){
				bf.append(label+": \n");
				String [] tmp = v.split("\n");
				for (int i = 0; i < tmp.length; i++) {
					bf.append("\t- "+tmp[i]+"\n");
				}
			}else{
				bf.append(label+": "+v+"\n");
			}
		}
	}
}
