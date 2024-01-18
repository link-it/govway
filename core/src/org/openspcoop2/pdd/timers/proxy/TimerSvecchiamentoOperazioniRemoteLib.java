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

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.services.connector.proxy.IProxyOperationService;
import org.openspcoop2.pdd.timers.TimerException;
import org.openspcoop2.pdd.timers.TimerMonitoraggioRisorseThread;
import org.openspcoop2.pdd.timers.TimerState;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;


/**     
 * TimerSvecchiamentoOperazioniRemoteLib
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerSvecchiamentoOperazioniRemoteLib {

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

	private Date oldest;
	
	private IProxyOperationService proxyOperationService;
	
	/** Costruttore */
	public TimerSvecchiamentoOperazioniRemoteLib(Logger logTimer, MsgDiagnostico msgDiag, 
			IProxyOperationService proxyOperationService,
			Date oldest) throws TimerException{
		
		this.op2Properties = OpenSPCoop2Properties.getInstance();
		this.tipoDatabase = this.op2Properties.getDatabaseType();
		if(this.tipoDatabase==null){
			throw new TimerException("Tipo Database non definito");
		}
		
		boolean debug = this.op2Properties.isProxyReadJMXResourcesAsyncProcessByTimerDebug();
		
		this.logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopGestoreOperazioniRemote(debug);
		this.logTimer = logTimer;
		this.msgDiag = msgDiag;
		
		this.oldest = oldest;
		
		this.proxyOperationService = proxyOperationService;
		
	}
	
	public void check() throws TimerException {
		
		// Controllo che il sistema non sia andando in shutdown
		if(OpenSPCoop2Startup.contextDestroyed){
			this.logTimerError("["+TimerSvecchiamentoOperazioniRemote.ID_MODULO+"] Rilevato sistema in shutdown");
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
			this.logTimerError("["+TimerSvecchiamentoOperazioniRemote.ID_MODULO+"] Risorse di sistema non disponibili: "+TimerMonitoraggioRisorseThread.getRisorsaNonDisponibile().getMessage(),TimerMonitoraggioRisorseThread.getRisorsaNonDisponibile());
			return;
		}
		if( !MsgDiagnostico.gestoreDiagnosticaDisponibile){
			this.logTimerError("["+TimerSvecchiamentoOperazioniRemote.ID_MODULO+"] Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage(),MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
			return;
		}
		
		boolean enabled = TimerState.ENABLED.equals(TimerSvecchiamentoOperazioniRemoteLib.state);
		if(!enabled) {
			emitDiagnosticLog("disabilitato");
			return;
		}
		
		long startControlloTimer = DateManager.getTimeMillis();
			
		try{

			process();
			
			// end
			long endControlloTimer = DateManager.getTimeMillis();
			long diff = (endControlloTimer-startControlloTimer);
			this.logTimerInfo("Svecchiamento operazioni remote terminata in "+Utilities.convertSystemTimeIntoStringMillisecondi(diff, true));
			
			
		}
		catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"TimerSvecchiamentoOperazioniRemoteLib");
			this.logTimerError("Riscontrato errore durante lo svecchiamento delle operazioni remote: "+ e.getMessage(),e);
		}
			
	}

	private void process() throws CoreException {
		
		try {
			long startGenerazione = DateManager.getTimeMillis();
			
			emitDiagnosticLog("svecchiamento.inCorso");
			
			int eliminate = this.proxyOperationService.clear(this.oldest);
	
			this.msgDiag.addKeyword(CostantiPdD.KEY_NUMERO_OPERAZIONI, eliminate+"");
			
			long endGenerazione = DateManager.getTimeMillis();
			String tempoImpiegato = Utilities.convertSystemTimeIntoStringMillisecondi((endGenerazione-startGenerazione), true);
			this.msgDiag.addKeyword(CostantiPdD.KEY_TEMPO_GESTIONE, tempoImpiegato);
			
			emitDiagnosticLog("svecchiamento.effettuata");
			
		}catch(Exception e) {
			this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
			emitDiagnosticLog("svecchiamento.fallita");
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
