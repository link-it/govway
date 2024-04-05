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

package org.openspcoop2.pdd.timers.proxy;


import java.util.Date;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.jmx.JMXUtils;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.services.connector.Proxy;
import org.openspcoop2.pdd.services.connector.proxy.IProxyOperationService;
import org.openspcoop2.pdd.services.connector.proxy.ProxyOperation;
import org.openspcoop2.pdd.timers.TimerException;
import org.openspcoop2.pdd.timers.TimerMonitoraggioRisorseThread;
import org.openspcoop2.pdd.timers.TimerState;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.slf4j.Logger;


/**     
 * TimerGestoreOperazioniRemoteLib
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerGestoreOperazioniRemoteLib {

	private static TimerState state = TimerState.OFF; // abilitato in OpenSPCoop2Startup al momento dell'avvio
	public static TimerState getState() {
		return state;
	}
	public static void setState(TimerState stateParam) {
		state = stateParam;
	}
	
	private OpenSPCoop2Properties op2Properties;
	private String tipoDatabase = null;
	
	private Logger logCore = null;
	private Logger logTimer = null;
	private MsgDiagnostico msgDiag = null;
	
	private void logCoreInfo(String msg) {
		if(this.logCore!=null) {
			this.logCore.info(msg);
		}
	}
	
	private void logTimerError(String msgErrore, Throwable e) {
		if(this.logTimer!=null) {
			this.logTimer.error(msgErrore,e);
		}
	}
	private void logTimerError(String msgErrore) {
		if(this.logTimer!=null) {
			this.logTimer.error(msgErrore);
		}
	}
	private void logTimerInfo(String msg) {
		if(this.logTimer!=null) {
			this.logTimer.info(msg);
		}
	}

	private Date dataUltimoAggiornamento;
	private Date now;
	
	private int limit;
	
	private IProxyOperationService proxyOperationService;

	private String urlPrefix;
	
	/** Costruttore */
	public TimerGestoreOperazioniRemoteLib(Logger logTimer, MsgDiagnostico msgDiag, 
			IProxyOperationService proxyOperationService,
			Date now, Date dataUltimoAggiornamento, String urlPrefix) throws TimerException{
		
		this.op2Properties = OpenSPCoop2Properties.getInstance();
		this.tipoDatabase = this.op2Properties.getDatabaseType();
		if(this.tipoDatabase==null){
			throw new TimerException("Tipo Database non definito");
		}
		
		boolean debug = this.op2Properties.isProxyReadJMXResourcesAsyncProcessByTimerDebug();
		
		this.logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopGestoreOperazioniRemote(debug);
		this.logTimer = logTimer;
		this.msgDiag = msgDiag;
		
		this.dataUltimoAggiornamento = dataUltimoAggiornamento;
		this.now = now;
		
		this.urlPrefix = urlPrefix;
			
		this.limit = this.op2Properties.getProxyReadJMXResourcesAsyncProcessByTimerLimit();
		
		this.proxyOperationService = proxyOperationService;
		
	}
	
	public void check() throws TimerException {
		
		// Controllo che il sistema non sia andando in shutdown
		if(OpenSPCoop2Startup.contextDestroyed){
			this.logTimerError("["+TimerGestoreOperazioniRemote.ID_MODULO+"] Rilevato sistema in shutdown");
			return;
		}

		// Controllo che l'inizializzazione corretta delle risorse sia effettuata
		if(!OpenSPCoop2Startup.initialize){
			this.msgDiag.logFatalError("inizializzazione di OpenSPCoop non effettuata", "Check Inizializzazione");
			String msgErrore = "Riscontrato errore: inizializzazione del Timer o di OpenSPCoop non effettuata";
			this.logTimerError(msgErrore);
			throw new TimerException(msgErrore);
		}

		// Controllo risorse di sistema disponibili
		if( !TimerMonitoraggioRisorseThread.isRisorseDisponibili()){
			this.logTimerError("["+TimerGestoreOperazioniRemote.ID_MODULO+"] Risorse di sistema non disponibili: "+TimerMonitoraggioRisorseThread.getRisorsaNonDisponibile().getMessage(),TimerMonitoraggioRisorseThread.getRisorsaNonDisponibile());
			return;
		}
		if( !MsgDiagnostico.gestoreDiagnosticaDisponibile){
			this.logTimerError("["+TimerGestoreOperazioniRemote.ID_MODULO+"] Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage(),MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
			return;
		}
		
		boolean enabled = TimerState.ENABLED.equals(TimerGestoreOperazioniRemoteLib.state);
		if(!enabled) {
			emitDiagnosticLog("disabilitato");
			return;
		}
		
		emitDiagnosticLog("letturaOperazioni");

		long startControlloTimer = DateManager.getTimeMillis();
			
		try{

			int offset = 0;
			int op = process(offset);
			while (op>0) {
				offset = offset + op;
				op = process(offset);
			}
			
			// end
			long endControlloTimer = DateManager.getTimeMillis();
			long diff = (endControlloTimer-startControlloTimer);
			this.logTimerInfo("Gestione operazioni remote terminata in "+Utilities.convertSystemTimeIntoStringMillisecondi(diff, true));
			
			
		}
		catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"TimerGestoreOperazioniRemoteLib");
			this.logTimerError("Riscontrato errore durante la gestione delle operazioni remote: "+ e.getMessage(),e);
		}
			
	}

	private int process(int offset) throws CoreException {
		
		this.msgDiag.addKeyword(CostantiPdD.KEY_OFFSET, offset+"");
		this.msgDiag.addKeyword(CostantiPdD.KEY_LIMIT, this.limit+"");
		
		try {
			long startGenerazione = DateManager.getTimeMillis();
			
			emitDiagnosticLog("gestioneOperazioni.inCorso");
			
			List<ProxyOperation> list = this.proxyOperationService.next(this.dataUltimoAggiornamento, this.now, offset, this.limit);
			
			int op = 0;
			if(list==null || list.isEmpty()) {
				op = 0;
			}
			else {
				op = list.size();
			}
			this.msgDiag.addKeyword(CostantiPdD.KEY_NUMERO_OPERAZIONI, op+"");
			emitDiagnosticLog("gestioneOperazioni.analisi");
			
			if(op>0) {
				for (ProxyOperation proxyOperation : list) {
					gestioneOperazione(proxyOperation);
				}
			}
			
			long endGenerazione = DateManager.getTimeMillis();
			String tempoImpiegato = Utilities.convertSystemTimeIntoStringMillisecondi((endGenerazione-startGenerazione), true);
			this.msgDiag.addKeyword(CostantiPdD.KEY_TEMPO_GESTIONE, tempoImpiegato);
			emitDiagnosticLog("gestioneOperazioni.effettuata");
			
			return op;
			
		}catch(Exception e) {
			this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
			emitDiagnosticLog("gestioneOperazioni.fallita");
			return -1;
		}
	}
	
	private void gestioneOperazione(ProxyOperation proxyOperation) {
		
		String dettaglioOperazione = proxyOperation.getDescription()+" (command: "+proxyOperation.getCommand()+")";
		this.msgDiag.addKeyword(CostantiPdD.KEY_DATI_OPERAZIONE, dettaglioOperazione+"");
		
		try {
			String stato = null;
			
			String url = this.urlPrefix + proxyOperation.getCommand();
			
			// Https
			boolean https = this.op2Properties.isProxyReadJMXResourcesHttpsEnabled();
			boolean verificaHostname = false;
			boolean autenticazioneServer = false;
			String autenticazioneServerPath = null;
			String autenticazioneServerType = null;
			String autenticazioneServerPassword = null;
			if(https) {
				verificaHostname = this.op2Properties.isProxyReadJMXResourcesHttpsEnabledVerificaHostName();
				autenticazioneServer = this.op2Properties.isProxyReadJMXResourcesHttpsEnabledAutenticazioneServer();
				if(autenticazioneServer) {
					autenticazioneServerPath = this.op2Properties.getProxyReadJMXResourcesHttpsEnabledAutenticazioneServerTruststorePath();
					autenticazioneServerType = this.op2Properties.getProxyReadJMXResourcesHttpsEnabledAutenticazioneServerTruststoreType();
					autenticazioneServerPassword = this.op2Properties.getProxyReadJMXResourcesHttpsEnabledAutenticazioneServerTruststorePassword();
				}
			}
			
			// Timeout
			int readTimeout = this.op2Properties.getProxyReadJMXResourcesReadTimeout();
			int connectTimeout = this.op2Properties.getProxyReadJMXResourcesConnectionTimeout();
			
			// Vengono utilizzate le credenziali del servizio check che dovranno essere uguali su tutti i nodi
			String usernameCheck = this.op2Properties.getCheckReadJMXResourcesUsername();
			String passwordCheck = this.op2Properties.getCheckReadJMXResourcesPassword();
			
			HttpResponse httpResponse = Proxy.invokeHttp(url, 
					readTimeout, connectTimeout, 
					usernameCheck, passwordCheck,
					https, verificaHostname, autenticazioneServer,
					autenticazioneServerPath, autenticazioneServerType,  autenticazioneServerPassword);
			
			String sResponse = null;
			if(httpResponse.getContent()!=null) {
				sResponse = new String(httpResponse.getContent());
			}
			boolean error = sResponse!=null && sResponse.startsWith(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA);
			
			if(httpResponse.getResultHTTPOperation()==200 && !error) {
				stato = "Success";
			}
			else {
				stato = "Failed";
			}
			stato = stato+(sResponse!=null ? "; "+sResponse : "");
			
			this.msgDiag.addKeyword(CostantiPdD.KEY_DETTAGLI_OPERAZIONE, stato);
			emitDiagnosticLog("gestioneOperazioni.operazione");
			
		}catch(Exception e) {
			this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
			emitDiagnosticLog("gestioneOperazioni.operazione.fallita");
		}
		
	}

	private void emitDiagnosticLog(String code) {
		this.msgDiag.logPersonalizzato(code);
		String msg = this.msgDiag.getMessaggio_replaceKeywords(code);
		emitLog(msg);
	}
	private void emitLog(String msg) {
		this.logCoreInfo(msg);
		this.logTimerInfo(msg);
	}
	
	
}
