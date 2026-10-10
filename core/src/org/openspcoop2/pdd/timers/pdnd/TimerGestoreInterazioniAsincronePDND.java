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


import java.util.Date;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.timers.TimerException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.threads.BaseThread;
import org.slf4j.Logger;


/**     
 * Timer che elimina le interazioni relative agli scambi di dati asincroni PDND scadute da oltre il periodo di conservazione
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerGestoreInterazioniAsincronePDND extends BaseThread{

	public static final String ID_MODULO = "TimerGestoreInterazioniAsincronePDND";
	
	/** Logger utilizzato per debug. */
	private Logger logTimer = null;
	private void logError(String msgErrore, Exception e) {
		if(this.logTimer!=null) {
			this.logTimer.error(msgErrore,e);
		}
	}
	private MsgDiagnostico msgDiag = null;
	private void logInfo(String keyMsgDiagnostico) {
		if(this.logTimer!=null && this.logTimer.isInfoEnabled()) {
			this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords(keyMsgDiagnostico));
		}
	}

	/** OpenSPCoop2Properties */
	private OpenSPCoop2Properties op2Properties = null;
	
	private long conservazioneSecondi;
	
	
	/** Costruttore */
	public TimerGestoreInterazioniAsincronePDND(long timeout) throws TimerException{
	
		// Aspetto inizializzazione di OpenSPCoop (aspetto mezzo minuto e poi segnalo errore)
		int attesa = 90;
		int secondi = 0;
		while( (!OpenSPCoop2Startup.initialize) && (secondi<attesa) ){
			Utilities.sleep(1000);
			secondi++;
		}
		if(secondi>= 90){
			throw new TimerException("Riscontrata inizializzazione OpenSPCoop non effettuata");
		}   

		this.logTimer = OpenSPCoop2Logger.getLoggerOpenSPCoopTimers();
		
		try {
			this.msgDiag = MsgDiagnostico.newInstance(ID_MODULO);
			this.msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_INTERAZIONI_ASINCRONE_PDND);
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER, ID_MODULO);
		} catch (Exception e) {
			String msgErrore = "Riscontrato Errore durante l'inizializzazione del MsgDiagnostico";
			this.logError(msgErrore,e);
			throw new TimerException(msgErrore,e);
		}

		this.msgDiag.logPersonalizzato("avvioInCorso");
		logInfo("avvioInCorso");
		
		try {
			this.op2Properties = OpenSPCoop2Properties.getInstance();
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"InizializzazioneTimer");
			String msgErrore = "Riscontrato errore durante l'inizializzazione del Reader delle Properties di OpenSPCoop: "+e.getMessage();
			this.logError(msgErrore,e);
			throw new TimerException(msgErrore,e);
		}
		
		this.conservazioneSecondi = this.op2Properties.getGestoreInterazioniAsincronePDNDConservazioneSecondi();
		
		this.setTimeout((int)timeout);
		String sec = "secondi";
		if(this.getTimeout() == 1)
			sec = "secondo";
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIMEOUT, this.getTimeout()+" "+sec);
		
		this.msgDiag.logPersonalizzato("avvioEffettuato");
		logInfo("avvioEffettuato");
		
	}
	

	@Override
	public void process(){
		
		try{
			
			// vengono eliminate le interazioni la cui scadenza è precedente alla data attuale meno il periodo di conservazione
			Date dataLimite = new Date(DateManager.getTimeMillis() - (this.conservazioneSecondi*1000L));
			
			this.msgDiag.addKeyword(CostantiPdD.DATA_AGGIORNAMENTO, DateUtils.getSimpleDateFormatMs().format(dataLimite));
			
			TimerGestoreInterazioniAsincronePDNDLib timer = new TimerGestoreInterazioniAsincronePDNDLib(this.logTimer, this.msgDiag, dataLimite);
			timer.check();
			
		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e,"TimerGestoreInterazioniAsincronePDND.check()");
			this.logError("Errore generale: "+e.getMessage(),e);
		}
		
	}
	
}
