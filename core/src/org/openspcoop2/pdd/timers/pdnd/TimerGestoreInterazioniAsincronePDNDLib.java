/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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

package org.openspcoop2.pdd.timers.pdnd;


import java.sql.Connection;
import java.util.Date;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.pdnd.async.InterazioniAsincronePDNDDriverUtils;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.timers.TimerException;
import org.openspcoop2.pdd.timers.TimerMonitoraggioRisorseThread;
import org.openspcoop2.pdd.timers.TimerState;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;


/**     
 * TimerGestoreInterazioniAsincronePDNDLib
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerGestoreInterazioniAsincronePDNDLib {

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

	private static final String PREFIX_LOG = "["+TimerGestoreInterazioniAsincronePDND.ID_MODULO+"] ";
	
	private Date dataLimite;
	
	/** Costruttore */
	public TimerGestoreInterazioniAsincronePDNDLib(Logger logTimer, MsgDiagnostico msgDiag, Date dataLimite) throws TimerException{
		
		this.op2Properties = OpenSPCoop2Properties.getInstance();
		this.tipoDatabase = this.op2Properties.getDatabaseType();
		if(this.tipoDatabase==null){
			throw new TimerException("Tipo Database non definito");
		}
		
		boolean debug = this.op2Properties.isGestoreInterazioniAsincronePDNDDebug();
		if(debug) {
			this.logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		}
		this.logTimer = logTimer;
		this.msgDiag = msgDiag;
		
		this.dataLimite = dataLimite;
	}
	
	public void check() throws TimerException {
		
		// Controllo che il sistema non sia andando in shutdown
		if(OpenSPCoop2Startup.contextDestroyed){
			this.logTimerError(PREFIX_LOG+"Rilevato sistema in shutdown");
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
			this.logTimerError(PREFIX_LOG+"Risorse di sistema non disponibili: "+TimerMonitoraggioRisorseThread.getRisorsaNonDisponibile().getMessage(),TimerMonitoraggioRisorseThread.getRisorsaNonDisponibile());
			return;
		}
		if( !MsgDiagnostico.gestoreDiagnosticaDisponibile){
			this.logTimerError(PREFIX_LOG+"Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage(),MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
			return;
		}
		
		boolean enabled = TimerState.ENABLED.equals(TimerGestoreInterazioniAsincronePDNDLib.state);
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
			this.logTimerInfo("Eliminazione interazioni asincrone PDND scadute terminata in "+Utilities.convertSystemTimeIntoStringMillisecondi(diff, true));
			
		}
		catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"TimerGestoreInterazioniAsincronePDNDLib");
			this.logTimerError("Riscontrato errore durante l'eliminazione delle interazioni asincrone PDND scadute: "+ e.getMessage(),e);
		}
			
	}

	private void process() {
		
		// Non viene utilizzato un lock: l'eliminazione per data è idempotente e può essere eseguita in contemporanea da più nodi
		
		OpenSPCoopStateful openspcoopstate = new OpenSPCoopStateful();
		try {
			long startGestione = DateManager.getTimeMillis();
			
			emitDiagnosticLog("pulizia.inCorso");
			
			openspcoopstate.initResource(this.op2Properties.getIdentitaPortaDefaultWithoutProtocol(),TimerGestoreInterazioniAsincronePDND.ID_MODULO, null);
			Connection connectionDB = ((StateMessage)openspcoopstate.getStatoRichiesta()).getConnectionDB();
			
			int eliminate = InterazioniAsincronePDNDDriverUtils.deleteScadute(connectionDB, this.tipoDatabase, this.dataLimite);
	
			this.msgDiag.addKeyword(CostantiPdD.KEY_NUMERO_RECORD, eliminate+"");
			
			long endGestione = DateManager.getTimeMillis();
			String tempoImpiegato = Utilities.convertSystemTimeIntoStringMillisecondi((endGestione-startGestione), true);
			this.msgDiag.addKeyword(CostantiPdD.KEY_TEMPO_GESTIONE, tempoImpiegato);
			
			emitDiagnosticLog("pulizia.effettuata");
			
		}catch(Exception e) {
			this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
			emitDiagnosticLog("pulizia.fallita");
			this.logTimerError("Eliminazione interazioni asincrone PDND scadute fallita: "+e.getMessage(),e);
		}finally{
			openspcoopstate.releaseResource();
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
