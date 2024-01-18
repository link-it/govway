/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.config;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openspcoop2.pdd.core.jmx.InformazioniStatoPoolThreads;
import org.openspcoop2.pdd.core.jmx.InformazioniStatoPorta;
import org.openspcoop2.pdd.core.jmx.InformazioniStatoPortaCache;
import org.openspcoop2.pdd.timers.TimerState;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.lib.mvc.PageData;

/**
 * Questa servlet si occupa di esportare le tracce in formato xml zippandole
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ConfigurazioneSistemaExporter extends HttpServlet {

	private static final long serialVersionUID = -7341279067126334095L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.processRequest(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.processRequest(req, resp);
	}

	/**
	 * Processa la richiesta pervenuta e si occupa di fornire i dati richiesti
	 * in formato zip
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	private void processRequest(HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();
		
		ConfigurazioneHelper confHelper = null;
		try {
			confHelper = new ConfigurazioneHelper(request, pd, session);
		
			ControlStationCore.logDebug("Ricevuta Richiesta di esportazione configurazione di Sistema...");
			Enumeration<?> en = confHelper.getParameterNames();
			ControlStationCore.logDebug("Parametri (nome = valore):\n-----------------");
			while (en.hasMoreElements()) {
				String param = (String) en.nextElement();
				String value = confHelper.getParameter(param);
				ControlStationCore.logDebug(param + " = " + value);
			}
			ControlStationCore.logDebug("-----------------");
			
			
			String alias = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER);
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();
			
			// Setto Proprietà Export File
			HttpUtilities.setOutputFile(response, true, "ConfigurazioneSistema.txt");
				
			OutputStream out = response.getOutputStream();	
			out.write(getInformazioniStatoPorta(alias, confCore).getBytes());
			out.flush();
			out.close();
		
		} catch (Exception e) {
			ControlStationCore.logError("Errore durante l'export della configurazione di sistema: "+e.getMessage(), e);
		} 
	}

	
	private String getInformazioniStatoPorta(String alias, ConfigurazioneCore confCore) throws Exception{
		
		InformazioniStatoPorta infoStatoPorta = new InformazioniStatoPorta();
		
		String versionePdD = null;
		try{
			versionePdD = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsa(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeMetodoVersionePdD(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura della versione della PdD (jmxResourcePdD): "+e.getMessage(),e);
			versionePdD = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String versioneBaseDati = null;
		try{
			versioneBaseDati = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsa(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeMetodoVersioneBaseDati(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura della versione della base dati (jmxResourcePdD): "+e.getMessage(),e);
			versioneBaseDati = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String confDir = null;
		try{
			confDir = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsa(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeMetodoDirectoryConfigurazione(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura della directory di configurazione (jmxResourcePdD): "+e.getMessage(),e);
			confDir = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String versioneJava = null;
		try{
			versioneJava = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsa(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeMetodoVersioneJava(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura della versione di java (jmxResourcePdD): "+e.getMessage(),e);
			versioneJava = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String vendorJava = null;
		try{
			vendorJava = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsa(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeMetodoVendorJava(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura delle informazioni sul vendor di java (jmxResourcePdD): "+e.getMessage(),e);
			vendorJava = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String messageFactory = null;
		try{
			messageFactory = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsa(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeMetodoMessageFactory(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura della message factory (jmxResourcePdD): "+e.getMessage(),e);
			messageFactory = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String statoServizioPD = null;
		try{
			statoServizioPD = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaStatoServiziPdD(alias),
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegata(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura dello stato del servizio Porta Delegata (jmxResourcePdD): "+e.getMessage(),e);
			statoServizioPD = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String statoServizioPD_abilitazioni = null;
		try{
			statoServizioPD_abilitazioni = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaStatoServiziPdD(alias),
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegataAbilitazioniPuntuali(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura dello stato del servizio Porta Delegata 'Abilitazioni Puntuali' (jmxResourcePdD): "+e.getMessage(),e);
			statoServizioPD_abilitazioni = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String statoServizioPD_disabilitazioni = null;
		try{
			statoServizioPD_disabilitazioni = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaStatoServiziPdD(alias),
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegataDisabilitazioniPuntuali(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura dello stato del servizio Porta Delegata 'Disabilitazioni Puntuali' (jmxResourcePdD): "+e.getMessage(),e);
			statoServizioPD_disabilitazioni = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String statoServizioPA = null;
		try{
			statoServizioPA = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaStatoServiziPdD(alias),
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativa(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura dello stato del servizio Porta Applicativa (jmxResourcePdD): "+e.getMessage(),e);
			statoServizioPA = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String statoServizioPA_abilitazioni = null;
		try{
			statoServizioPA_abilitazioni = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaStatoServiziPdD(alias),
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativaAbilitazioniPuntuali(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura dello stato del servizio Porta Applicativa 'Abilitazioni Puntuali' (jmxResourcePdD): "+e.getMessage(),e);
			statoServizioPA_abilitazioni = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String statoServizioPA_disabilitazioni = null;
		try{
			statoServizioPA_disabilitazioni = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaStatoServiziPdD(alias),
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativaDisabilitazioniPuntuali(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura dello stato del servizio Porta Applicativa 'Disabilitazioni Puntuali' (jmxResourcePdD): "+e.getMessage(),e);
			statoServizioPA_disabilitazioni = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String statoServizioIM = null;
		try{
			statoServizioIM = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaStatoServiziPdD(alias),
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativa(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura dello stato del servizio Integration Manager (jmxResourcePdD): "+e.getMessage(),e);
			statoServizioIM = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String livelloSeveritaDiagnostici = null;
		try{
			livelloSeveritaDiagnostici = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoSeveritaDiagnostici(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura del livello di severità dei diagnostici (jmxResourcePdD): "+e.getMessage(),e);
			livelloSeveritaDiagnostici = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String livelloSeveritaDiagnosticiLog4j = null;
		try{
			livelloSeveritaDiagnosticiLog4j = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoSeveritaDiagnosticiLog4j(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura del livello di severità log4j dei diagnostici (jmxResourcePdD): "+e.getMessage(),e);
			livelloSeveritaDiagnosticiLog4j = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String log4jDiagnostica = null;
		try{
			log4jDiagnostica = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoLog4jDiagnostica(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura dello stato del file di log Log4j govway_diagnostici.log (jmxResourcePdD): "+e.getMessage(),e);
			log4jDiagnostica = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String log4jOpenspcoop = null;
		try{
			log4jOpenspcoop = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoLog4jOpenspcoop(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura dello stato del file di log Log4j govway.log (jmxResourcePdD): "+e.getMessage(),e);
			log4jOpenspcoop = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String log4jIntegrationManager = null;
		try{
			log4jIntegrationManager = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoLog4jIntegrationManager(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura dello stato del file di log Log4j govway_integrationManager.log (jmxResourcePdD): "+e.getMessage(),e);
			log4jIntegrationManager = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String tracciamento = null;
		try{
			tracciamento = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTracciamento(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura dello stato del tracciamento buste (jmxResourcePdD): "+e.getMessage(),e);
			tracciamento = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String dumpPD = null;
		try{
			dumpPD = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoDumpPD(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura dello stato del dump binario della Porta Delegata (jmxResourcePdD): "+e.getMessage(),e);
			dumpPD = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String dumpPA = null;
		try{
			dumpPA = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoDumpPA(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura dello stato del dump binario della Porta Applicativa (jmxResourcePdD): "+e.getMessage(),e);
			dumpPA = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String log4j_tracciamento = null;
		try{
			log4j_tracciamento = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoLog4jTracciamento(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura dello stato del file di log Log4j govway_tracciamento.log (jmxResourcePdD): "+e.getMessage(),e);
			log4j_tracciamento = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String log4j_dump = null;
		try{
			log4j_dump = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoLog4jDump(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura dello stato del file di log Log4j govway_dump.log (jmxResourcePdD): "+e.getMessage(),e);
			log4j_dump = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String fileTraceGovWayState = null;
		try{
			fileTraceGovWayState = confCore.getInvoker().invokeJMXMethod(alias, confCore.getJmxPdDConfigurazioneSistemaType(alias),
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsa(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeMetodoGetFileTrace(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura dello stato di configurazione del file trace (jmxResourcePdD): "+e.getMessage(),e);
			log4j_dump = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		
		String errorSpecificTypeProcessRequest = null;
		try{
			errorSpecificTypeProcessRequest = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalRequestError(alias));
		}catch(Exception e){
			String tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_TYPE+" ("+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_REQUEST+")";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			errorSpecificTypeProcessRequest = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String errorSpecificTypeProcessResponse_1 = null;
		try{
			errorSpecificTypeProcessResponse_1 = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeBadResponse(alias));
		}catch(Exception e){
			String tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_TYPE+" ("+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_RESPONSE+")";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			errorSpecificTypeProcessResponse_1 = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		String errorSpecificTypeProcessResponse_2 = null;
		try{
			errorSpecificTypeProcessResponse_2 = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalResponseError(alias));
		}catch(Exception e){
			String tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_TYPE+" ("+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_RESPONSE+")";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			errorSpecificTypeProcessResponse_2 = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		String errorSpecificTypeProcessResponse = null;
		if(ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE.contentEquals(errorSpecificTypeProcessResponse_1)) {
			errorSpecificTypeProcessResponse = errorSpecificTypeProcessResponse_1;
		}
		else if(ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE.contentEquals(errorSpecificTypeProcessResponse_2)) {
			errorSpecificTypeProcessResponse = errorSpecificTypeProcessResponse_2;
		}
		else {
			errorSpecificTypeProcessResponse = ("true".equals(errorSpecificTypeProcessResponse_1) && "true".equals(errorSpecificTypeProcessResponse_2)) + ""; 
		}
		
		String errorSpecificTypeInternalError = null;
		try{
			errorSpecificTypeInternalError = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalError(alias));
		}catch(Exception e){
			String tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_TYPE+" ("+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_INTERNAL_ERROR+")";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			errorSpecificTypeInternalError = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String errorStatus = null;
		try{
			errorStatus = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorStatusCode(alias));
		}catch(Exception e){
			String tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_STATUS+" ("+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_STATUS_CODE+")";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			errorStatus = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String errorStatusInSoapFaultCode = null;
		try{
			errorStatusInSoapFaultCode = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorUseStatusCodeAsFaultCode(alias));
		}catch(Exception e){
			String tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_STATUS+" ("+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_USE_STATUS_CODE_AS_SOAP_FAULT+")";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			errorStatusInSoapFaultCode = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String errorSpecificDetails = null;
		try{
			errorSpecificDetails = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorDetails(alias));
		}catch(Exception e){
			String tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_DETAILS+" ("+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_DETAILS+")";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			errorSpecificDetails = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String errorInstanceId = null;
		try{
			errorInstanceId = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorInstanceId(alias));
		}catch(Exception e){
			String tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_INSTANCE+" ("+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_INSTANCE_ID+")";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			errorInstanceId = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String errorGenerateHttpErrorCodeInSoap = null;
		try{
			errorGenerateHttpErrorCodeInSoap = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorGenerateHttpHeaderGovWayCode(alias));
		}catch(Exception e){
			String tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_GENERATE_HTTP+" ("+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_GENERATE_HTTP_CODE+")";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			errorGenerateHttpErrorCodeInSoap = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}		

		String infoDatabase = null;
		try{
			infoDatabase = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsa(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniDatabase(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura delle informazioni sul database (jmxResourcePdD): "+e.getMessage(),e);
			infoDatabase = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		HashMap<String, String> infoConnessioneAltriDB = null;
		HashMap<String, String> statoConnessioniAltriDB = null;
		try{
			int numeroDatasource = 0;
			try{
				String stato = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
						confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaDatasourceGW(alias),
						confCore.getJmxPdDConfigurazioneSistemaNomeAttributoNumeroDatasourceGW(alias));
				if(stato!=null && !"".equals(stato)) {
					numeroDatasource = Integer.valueOf(stato);
				}
			}catch(Exception e){
				ControlStationCore.logDebug("Numero di datasource attivi non ottenibili: "+e.getMessage());
			}
			if(numeroDatasource>0) {
				String nomiDatasource = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
						confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaDatasourceGW(alias),
						confCore.getJmxPdDConfigurazioneSistemaNomeMetodoGetDatasourcesGW(alias));
				if(nomiDatasource!=null && !"".equals(nomiDatasource)) {
					/* Esempio:
					 * 3 datasource allocati: 
	(2020-01-23_15:40:22.391) idDatasource:88c4db87-07a5-4fa6-95a5-e6caf4c21a7f jndiName:org.govway.datasource.tracciamento ConnessioniAttive:0
	(2020-01-23_15:40:22.396) idDatasource:bae6582a-659b-4b70-bc9c-aca3570b45af jndiName:org.govway.datasource.statistiche ConnessioniAttive:0
	(2020-01-23_15:40:22.627) idDatasource:4ff843af-94d6-4506-8ecf-aac52bcb3525 jndiName:org.govway.datasource.console ConnessioniAttive:0
					 **/
					String [] lines = nomiDatasource.split("\n");
					if(lines!=null && lines.length>0) {
						for (String line : lines) {
							if(line.startsWith("(")) {
								String [] tmp = line.split(" ");
								if(tmp!=null && tmp.length>3) {
									String nomeDS = tmp[2]+" "+tmp[1];
									try{
										String idDS = tmp[1].split(":")[1];
										
										String statoInfo = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
												confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaDatasourceGW(alias),
												confCore.getJmxPdDConfigurazioneSistemaNomeMetodoGetInformazioniDatabaseDatasourcesGW(alias),
												idDS);
										if(infoConnessioneAltriDB==null) {
											infoConnessioneAltriDB = new HashMap<>();
										}
										infoConnessioneAltriDB.put(nomeDS,statoInfo);
										
										if(!confCore.isClusterAsyncUpdate()) {
											String statoConnessioni = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
													confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaDatasourceGW(alias),
													confCore.getJmxPdDConfigurazioneSistemaNomeMetodoGetUsedConnectionsDatasourcesGW(alias),
													idDS);
											if(statoConnessioniAltriDB==null) {
												statoConnessioniAltriDB = new HashMap<>();
											}
											statoConnessioniAltriDB.put(nomeDS,statoConnessioni);
										}
										
									}catch(Exception e){
										ControlStationCore.logError("Errore durante la lettura delle informazioni verso il database "+nomeDS+" (jmxResourcePdD): "+e.getMessage(),e);
										
										if(infoConnessioneAltriDB==null) {
											infoConnessioneAltriDB = new HashMap<>();
										}
										infoConnessioneAltriDB.put(nomeDS,ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE);
										
										if(!confCore.isClusterAsyncUpdate()) {
											if(statoConnessioniAltriDB==null) {
												statoConnessioniAltriDB = new HashMap<>();
											}
											statoConnessioniAltriDB.put(nomeDS,ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE);
										}
									}		
								}
							}
						}
					}
				}
			}
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura delle informazioni verso altri database (jmxResourcePdD): "+e.getMessage(),e);
			
			if(infoConnessioneAltriDB==null) {
				infoConnessioneAltriDB = new HashMap<>();
			}
			infoConnessioneAltriDB.put("GovWayDatasources",ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE);
			
			if(!confCore.isClusterAsyncUpdate()) {
				if(statoConnessioniAltriDB==null) {
					statoConnessioniAltriDB = new HashMap<>();
				}
				statoConnessioniAltriDB.put("GovWayDatasources",ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE);
			}
		}
		if(confCore.isClusterAsyncUpdate()) {
			if(statoConnessioniAltriDB==null) {
				statoConnessioniAltriDB = new HashMap<>();
			}
			statoConnessioniAltriDB.put("GovWayDatasources",ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE_CLUSTER_ASYNC_UPDATE);
		}
		
		
		
		String infoSSL = null;
		try{
			infoSSL = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsa(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteSSL(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura delle informazioni SSL (jmxResourcePdD): "+e.getMessage(),e);
			infoSSL = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String infoCryptographyKeyLength = null;
		try{
			infoCryptographyKeyLength = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsa(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniCryptographyKeyLength(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura delle informazioni sulla lunghezza delle chiavi di cifratura (jmxResourcePdD): "+e.getMessage(),e);
			infoCryptographyKeyLength = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String infoCharset = null;
		try{
			infoCharset = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsa(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniCharset(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura delle informazioni sul charset (jmxResourcePdD): "+e.getMessage(),e);
			infoCharset = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String infoInternazionalizzazione = null;
		try{
			infoInternazionalizzazione = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsa(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteInternazionalizzazione(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura delle informazioni sull'internazionalizzazione (jmxResourcePdD): "+e.getMessage(),e);
			infoInternazionalizzazione = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String infoTimeZone = null;
		try{
			infoTimeZone = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsa(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteTimeZone(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura delle informazioni sul TimeZone (jmxResourcePdD): "+e.getMessage(),e);
			infoTimeZone = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String infoProprietaJavaNetworking = null;
		try{
			infoProprietaJavaNetworking = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsa(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteProprietaJavaNetworking(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura delle informazioni sulle proprietà java di networking (jmxResourcePdD): "+e.getMessage(),e);
			infoProprietaJavaNetworking = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String infoProprietaJavaAltro = null;
		try{
			infoProprietaJavaAltro = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsa(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaJavaAltro(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura delle informazioni sulle proprietà java (escluse quelle di networking) (jmxResourcePdD): "+e.getMessage(),e);
			infoProprietaJavaAltro = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String infoProprietaSistema = null;
		try{
			infoProprietaSistema = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsa(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaSistema(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura delle informazioni sulle proprietà di sistema (jmxResourcePdD): "+e.getMessage(),e);
			infoProprietaSistema = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String infoProtocolli = null;
		try{
			infoProtocolli = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsa(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeMetodoPluginProtocols(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura delle informazioni sui protocolli (jmxResourcePdD): "+e.getMessage(),e);
			infoProtocolli = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		InformazioniStatoPortaCache [] cacheArray = null;
		
		List<String> caches = confCore.getJmxPdDCaches(alias);
		if(caches!=null && caches.size()>0){
			
			cacheArray = new InformazioniStatoPortaCache[caches.size()];
			int i = 0;
			for (String cacheName : caches) {
				
				boolean enabled = false;
				try{
					String stato = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
							cacheName,
							confCore.getJmxPdDCacheNomeAttributoCacheAbilitata(alias));
					if(stato.equalsIgnoreCase("true")){
						enabled = true;
					}
				}catch(Exception e){
					ControlStationCore.logError("Errore durante la lettura dello stato della cache ["+cacheName+"](jmxResourcePdD): "+e.getMessage(),e);
				}
				
				cacheArray[i] = new InformazioniStatoPortaCache(cacheName, enabled);
				
				if(enabled){
					String params = null;
					try{
						params = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDCacheType(alias), 
								cacheName,
								confCore.getJmxPdDCacheNomeMetodoStatoCache(alias));
					}catch(Exception e){
						ControlStationCore.logError("Errore durante la lettura dello stato della cache ["+cacheName+"](jmxResourcePdD): "+e.getMessage(),e);
						params = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
					}
					cacheArray[i].setStatoCache(params);
				}
						
				i++;
			}
		}
		
		String statoConnessioniDB = null;
		if(!confCore.isClusterAsyncUpdate()) {
			try{
				statoConnessioniDB = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
						confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaMonitoraggio(alias),
						confCore.getJmxPdDConfigurazioneSistemaNomeMetodoConnessioniDB(alias));
			}catch(Exception e){
				ControlStationCore.logError("Errore durante la lettura dello stato delle connessioni al database (jmxResourcePdD): "+e.getMessage(),e);
				statoConnessioniDB = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
			}
		}
		else {
			statoConnessioniDB = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE_CLUSTER_ASYNC_UPDATE;
		}
		
		// statoConnessioniAltriDB, letto prima durante l'acquisizione delle informazioni
				
		String statoConnessioniJMS = null;
		if(!confCore.isClusterAsyncUpdate()) {
			try{
				statoConnessioniJMS = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
						confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaMonitoraggio(alias),
						confCore.getJmxPdDConfigurazioneSistemaNomeMetodoConnessioniJMS(alias));
			}catch(Exception e){
				ControlStationCore.logError("Errore durante la lettura dello stato delle connessioni al broker JMS (jmxResourcePdD): "+e.getMessage(),e);
				statoConnessioniJMS = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
			}
		}
		else {
			statoConnessioniJMS = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE_CLUSTER_ASYNC_UPDATE;
		}
		
		String statoTransazioniId = null;
		if(!confCore.isClusterAsyncUpdate()) {
			try{
				statoTransazioniId = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
						confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaMonitoraggio(alias),
						confCore.getJmxPdDConfigurazioneSistemaNomeMetodoIdTransazioniAttive(alias));
			}catch(Exception e){
				ControlStationCore.logError("Errore durante la lettura degli identificativi delle transazioni attive (jmxResourcePdD): "+e.getMessage(),e);
				statoTransazioniId = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
			}
		}
		else {
			statoTransazioniId = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE_CLUSTER_ASYNC_UPDATE;
		}
		
		String statoTransazioniIdProtocollo = null;
		if(!confCore.isClusterAsyncUpdate()) {
			try{
				statoTransazioniIdProtocollo = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
						confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaMonitoraggio(alias),
						confCore.getJmxPdDConfigurazioneSistemaNomeMetodoIdProtocolloTransazioniAttive(alias));
			}catch(Exception e){
				ControlStationCore.logError("Errore durante la lettura degli identificativi di protocollo delle transazioni attive (jmxResourcePdD): "+e.getMessage(),e);
				statoTransazioniIdProtocollo = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
			}
		}
		else {
			statoTransazioniIdProtocollo = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE_CLUSTER_ASYNC_UPDATE;
		}
		
		String statoTimerVerificaConnessioni = null;
		try{
			statoTimerVerificaConnessioni = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiVerificaConnessioniAttive(alias));
		}catch(Exception e){
			String tipo ="stato timer '"+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_VERIFICA_CONNESSIONI_ATTIVE+"'";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			statoTimerVerificaConnessioni = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String statoConnessioniPD = null;
		if(!confCore.isClusterAsyncUpdate()) {
			try{
				statoConnessioniPD = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
						confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaMonitoraggio(alias),
						confCore.getJmxPdDConfigurazioneSistemaNomeMetodoConnessioniPD(alias));
			}catch(Exception e){
				ControlStationCore.logError("Errore durante la lettura dello stato delle connessioni al servizio PD (jmxResourcePdD): "+e.getMessage(),e);
				statoConnessioniPD = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
			}
		}
		else {
			statoConnessioniPD = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE_CLUSTER_ASYNC_UPDATE;
		}
		
		String statoConnessioniPA = null;
		if(!confCore.isClusterAsyncUpdate()) {
			try{
				statoConnessioniPA = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
						confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaMonitoraggio(alias),
						confCore.getJmxPdDConfigurazioneSistemaNomeMetodoConnessioniPA(alias));
			}catch(Exception e){
				ControlStationCore.logError("Errore durante la lettura dello stato delle connessioni al servizio PA (jmxResourcePdD): "+e.getMessage(),e);
				statoConnessioniPA = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
			}
		}
		else {
			statoConnessioniPA = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE_CLUSTER_ASYNC_UPDATE;
		}
		
		String statoTimerConsegnaAsincrona = null;
		try{
			statoTimerConsegnaAsincrona = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerConsegnaContenutiApplicativi(alias));
		}catch(Exception e){
			String tipo ="stato timer '"+
					ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_NOTIFICHE+"'";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			statoTimerConsegnaAsincrona = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		TimerState timerState = null;
		try {
			timerState = TimerState.valueOf(statoTimerConsegnaAsincrona);
		}catch(Throwable t) {}
		boolean timerAttivo = timerState!=null && TimerState.ENABLED.equals(timerState);
		
		List<InformazioniStatoPoolThreads> statoPoolThread = null;
		if(timerAttivo) {
			statoPoolThread = new ArrayList<InformazioniStatoPoolThreads>();
			
			List<String> code = confCore.getConsegnaNotificaCode();
			for (String coda : code) {
			
				String stato = null;
				try{
					stato = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
							confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConsegnaContenutiApplicativi(alias),
							confCore.getJmxPdDConfigurazioneSistemaNomeMetodoGetThreadPoolStatus(alias),
							coda);
				}catch(Exception e){
					ControlStationCore.logError("Errore durante la lettura dello stato del thread pool della coda '"+coda+"' per la consegna agli applicativi (jmxResourcePdD): "+e.getMessage(),e);
					stato = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
				}
				
				String configurazione = null;
				try{
					configurazione = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
							confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConsegnaContenutiApplicativi(alias),
							confCore.getJmxPdDConfigurazioneSistemaNomeMetodoGetQueueConfig(alias),
							coda);
				}catch(Exception e){
					ControlStationCore.logError("Errore durante la lettura della configurazione del thread pool della coda '"+coda+"' per la consegna agli applicativi (jmxResourcePdD): "+e.getMessage(),e);
					configurazione = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
				}
				
				String connettoriPrioritari = null;
				try{
					connettoriPrioritari = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
							confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConsegnaContenutiApplicativi(alias),
							confCore.getJmxPdDConfigurazioneSistemaNomeMetodoGetConnettoriPrioritari(alias),
							coda);
				}catch(Exception e){
					ControlStationCore.logError("Errore durante la lettura della configurazione (connettori prioritari) del thread pool della coda '"+coda+"' per la consegna agli applicativi (jmxResourcePdD): "+e.getMessage(),e);
					connettoriPrioritari = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
				}
				
				String applicativiPrioritari = null;
				try{
					applicativiPrioritari = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
							confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConsegnaContenutiApplicativi(alias),
							confCore.getJmxPdDConfigurazioneSistemaNomeMetodoGetApplicativiPrioritari(alias),
							coda);
				}catch(Exception e){
					ControlStationCore.logError("Errore durante la lettura della configurazione (applicativi prioritari) del thread pool della coda '"+coda+"' per la consegna agli applicativi (jmxResourcePdD): "+e.getMessage(),e);
					applicativiPrioritari = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
				}
				
				InformazioniStatoPoolThreads info = new InformazioniStatoPoolThreads(coda, stato, configurazione, connettoriPrioritari);
				info.setApplicativiPrioritari(applicativiPrioritari);
				statoPoolThread.add(info);
			}
		}
		
		String statoTimerGenerazioneStatisticheOrarie = null;
		try{
			statoTimerGenerazioneStatisticheOrarie = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheOrarie(alias));
		}catch(Exception e){
			String tipo ="stato timer 'Statistiche "+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_ORARIE+"'";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			statoTimerGenerazioneStatisticheOrarie = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String statoTimerGenerazioneStatisticheGiornaliere = null;
		try{
			statoTimerGenerazioneStatisticheGiornaliere = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheGiornaliere(alias));
		}catch(Exception e){
			String tipo ="stato timer 'Statistiche "+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_GIORNALIERE+"'";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			statoTimerGenerazioneStatisticheGiornaliere = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String statoTimerGenerazioneStatisticheSettimanali = null;
		try{
			statoTimerGenerazioneStatisticheSettimanali = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheSettimanali(alias));
		}catch(Exception e){
			String tipo ="stato timer 'Statistiche "+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_SETTIMANALI+"'";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			statoTimerGenerazioneStatisticheSettimanali = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String statoTimerGenerazioneStatisticheMensili = null;
		try{
			statoTimerGenerazioneStatisticheMensili = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheMensili(alias));
		}catch(Exception e){
			String tipo ="stato timer 'Statistiche "+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_MENSILI+"'";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			statoTimerGenerazioneStatisticheMensili = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String statoTimerMessaggiEliminati = null;
		try{
			statoTimerMessaggiEliminati = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiEliminati(alias));
		}catch(Exception e){
			String tipo ="stato timer '"+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_ELIMINATI+"'";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			statoTimerMessaggiEliminati = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String statoTimerMessaggiScaduti = null;
		try{
			statoTimerMessaggiScaduti = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiScaduti(alias));
		}catch(Exception e){
			String tipo ="stato timer '"+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_SCADUTI+"'";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			statoTimerMessaggiScaduti = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String statoTimerBuste = null;
		try{
			statoTimerBuste = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreRepositoryBuste(alias));
		}catch(Exception e){
			String tipo ="stato timer '"+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_REPOSITORY_BUSTE+"'";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			statoTimerBuste = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String statoTimerCorrelazioneApplicativa = null;
		try{
			statoTimerCorrelazioneApplicativa = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaCorrelazioneApplicativa(alias));
		}catch(Exception e){
			String tipo ="stato timer '"+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_CORRELAZIONE_APPLICATIVA+"'";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			statoTimerCorrelazioneApplicativa = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String statoTimerMessaggiNonGestiti = null;
		try{
			statoTimerMessaggiNonGestiti = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiNonGestiti(alias));
		}catch(Exception e){
			String tipo ="stato timer '"+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_NON_GESTITI+"'";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			statoTimerMessaggiNonGestiti = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String statoTimerMessaggiAnomali = null;
		try{
			statoTimerMessaggiAnomali = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestorePuliziaMessaggiAnomali(alias));
		}catch(Exception e){
			String tipo ="stato timer '"+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_PULIZIA_MESSAGGI_ANOMALI+"'";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			statoTimerMessaggiAnomali = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String statoTimerMonitoraggioRisorse = null;
		try{
			statoTimerMonitoraggioRisorse = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerMonitoraggioRisorseThread(alias));
		}catch(Exception e){
			String tipo ="stato timer '"+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_MONITORAGGIO_RISORSE_THREAD+"'";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			statoTimerMonitoraggioRisorse = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String statoTimerThreshold = null;
		try{
			statoTimerThreshold = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerThresholdThread(alias));
		}catch(Exception e){
			String tipo ="stato timer '"+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_THRESHOLD_THREAD+"'";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			statoTimerThreshold = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String statoTimerEventi = null;
		try{
			statoTimerEventi = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerEventi(alias));
		}catch(Exception e){
			String tipo ="stato timer '"+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_EVENTI+"'";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			statoTimerEventi = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String statoTimerFileSystemRecovery = null;
		try{
			statoTimerFileSystemRecovery = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerFileSystemRecovery(alias));
		}catch(Exception e){
			String tipo ="stato timer '"+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_FILE_SYSTEM_RECOVERY+"'";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			statoTimerFileSystemRecovery = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String statoTimerBusteOneway = null;
		try{
			statoTimerBusteOneway = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreBusteOnewayNonRiscontrate(alias));
		}catch(Exception e){
			String tipo ="stato timer '"+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_BUSTE_ONEWAY_NON_RISCONTRATE+"'";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			statoTimerBusteOneway = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String statoTimerBusteAsincrone = null;
		try{
			statoTimerBusteAsincrone = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreBusteAsincroneNonRiscontrate(alias));
		}catch(Exception e){
			String tipo ="stato timer '"+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_BUSTE_ASINCRONE_NON_RISCONTRATE+"'";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			statoTimerBusteAsincrone = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		String statoTimerRepositoryStateful = null;
		try{
			statoTimerRepositoryStateful = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerRepositoryStatefulThread(alias));
		}catch(Exception e){
			String tipo ="stato timer '"+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_REPOSITORY_STATEFUL_THREAD+"'";
			ControlStationCore.logError("Errore durante la lettura dello stato (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			statoTimerRepositoryStateful = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}

		String infoInstallazione = null;
		try{
			infoInstallazione = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeRisorsa(alias), 
					confCore.getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniInstallazione(alias));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura delle informazioni sull'installazione (jmxResourcePdD): "+e.getMessage(),e);
			infoInstallazione = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		return infoStatoPorta.formatStatoPorta(versionePdD, versioneBaseDati, confDir, versioneJava, vendorJava, messageFactory,
				statoServizioPD,statoServizioPD_abilitazioni,statoServizioPD_disabilitazioni,
				statoServizioPA,statoServizioPA_abilitazioni,statoServizioPA_disabilitazioni,
				statoServizioIM,
				livelloSeveritaDiagnostici, livelloSeveritaDiagnosticiLog4j,
				"true".equals(log4jDiagnostica), "true".equals(log4jOpenspcoop), "true".equals(log4jIntegrationManager), 
				"true".equals(tracciamento), "true".equals(dumpPD), "true".equals(dumpPA),
				"true".equals(log4j_tracciamento), "true".equals(log4j_dump), 
				fileTraceGovWayState,
				"true".equals(errorSpecificTypeProcessRequest), "true".equals(errorSpecificTypeProcessResponse), "true".equals(errorSpecificTypeInternalError),
				"true".equals(errorStatus), "true".equals(errorStatusInSoapFaultCode),
				"true".equals(errorSpecificDetails), "true".equals(errorInstanceId), "true".equals(errorGenerateHttpErrorCodeInSoap),
				infoDatabase, infoConnessioneAltriDB , 
				infoSSL, infoCryptographyKeyLength, 
				infoCharset, infoInternazionalizzazione, infoTimeZone, 
				infoProprietaJavaNetworking, infoProprietaJavaAltro, infoProprietaSistema,
				infoProtocolli,
				statoConnessioniDB, statoConnessioniAltriDB, statoConnessioniJMS,
				statoTransazioniId, statoTransazioniIdProtocollo,
				statoTimerVerificaConnessioni, statoConnessioniPD, statoConnessioniPA, 
				statoTimerConsegnaAsincrona, statoPoolThread,
				statoTimerGenerazioneStatisticheOrarie, statoTimerGenerazioneStatisticheGiornaliere, statoTimerGenerazioneStatisticheSettimanali, statoTimerGenerazioneStatisticheMensili,
				statoTimerMessaggiEliminati, statoTimerMessaggiScaduti, statoTimerBuste, statoTimerCorrelazioneApplicativa, statoTimerMessaggiNonGestiti, statoTimerMessaggiAnomali,
				statoTimerMonitoraggioRisorse, statoTimerThreshold,
				statoTimerEventi, statoTimerFileSystemRecovery, statoTimerBusteOneway, statoTimerBusteAsincrone, statoTimerRepositoryStateful,
				infoInstallazione,
				cacheArray);
	}
}
