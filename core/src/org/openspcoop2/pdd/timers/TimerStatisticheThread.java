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

package org.openspcoop2.pdd.timers;


import org.openspcoop2.core.statistiche.constants.TipoIntervalloStatistico;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.threads.BaseThread;
import org.slf4j.Logger;


/**     
 * TimerStatisticheThread
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerStatisticheThread extends BaseThread{

	public static final String ID_MODULO = "TimerStatistiche";
	
	
	/** Tipo di Intervallo Statistico */
	private TipoIntervalloStatistico tipoStatistica;
	
	/** Logger utilizzato per debug. */
	private Logger logTimer = null;
	private MsgDiagnostico msgDiag = null;

	/** OpenSPCoop2Properties */
	private OpenSPCoop2Properties op2Properties = null;

	
	
	
	/** Costruttore */
	public TimerStatisticheThread(long timeout, TipoIntervalloStatistico tipo) throws Exception{
	
		// Aspetto inizializzazione di OpenSPCoop (aspetto mezzo minuto e poi segnalo errore)
		int attesa = 90;
		int secondi = 0;
		while( (OpenSPCoop2Startup.initialize==false) && (secondi<attesa) ){
			Utilities.sleep(1000);
			secondi++;
		}
		if(secondi>= 90){
			throw new TimerException("Riscontrata inizializzazione OpenSPCoop non effettuata");
		}   

		this.logTimer = OpenSPCoop2Logger.getLoggerOpenSPCoopTimers();
		
		try {
			this.msgDiag = MsgDiagnostico.newInstance(ID_MODULO);
			this.msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_STATISTICHE);
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER, "Timer"+tipo.getValue());
		} catch (Exception e) {
			String msgErrore = "Riscontrato Errore durante l'inizializzazione del MsgDiagnostico";
			this.logTimer.error(msgErrore,e);
			throw new TimerException(msgErrore,e);
		}

		this.msgDiag.logPersonalizzato("avvioInCorso");
		this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("avvioInCorso"));
		
		try {
			this.op2Properties = OpenSPCoop2Properties.getInstance();
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"InizializzazioneTimer");
			String msgErrore = "Riscontrato errore durante l'inizializzazione del Reader delle Properties di OpenSPCoop: "+e.getMessage();
			this.logTimer.error(msgErrore,e);
			throw new TimerException(msgErrore,e);
		}

		this.tipoStatistica = tipo;
		
		this.setTimeout((int)timeout);
		String sec = "secondi";
		if(this.getTimeout() == 1)
			sec = "secondo";
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIMEOUT, this.getTimeout()+" "+sec);
		
		this.msgDiag.logPersonalizzato("avvioEffettuato");
		this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("avvioEffettuato"));
	}
	

	@Override
	public void process(){
		
		try{
			// Prendo la gestione
			TimerStatisticheLib timerStatistiche = 
				new TimerStatisticheLib(this.tipoStatistica,this.msgDiag,this.logTimer,this.op2Properties);
			
			timerStatistiche.check();
			
		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e,"TimerStatisticheLib.check()");
			this.logTimer.error("Errore generale: "+e.getMessage(),e);
		}finally{
		}
		
	}
	
}
