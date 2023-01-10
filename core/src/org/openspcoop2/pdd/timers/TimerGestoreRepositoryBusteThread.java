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



package org.openspcoop2.pdd.timers;

import org.slf4j.Logger;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.threads.BaseThread;

/**
 * Thread per la gestione del Threshold
 * 
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerGestoreRepositoryBusteThread extends BaseThread{

	/**
	 * Timeout che definisce la scadenza di un messaggio
	 */
	private long scadenzaMessaggio = 60 * 24 * 5; // cablato a 5 giorni (60m * 24h * 5giorni).
	/** Properties Reader */
	private OpenSPCoop2Properties propertiesReader;
	/** MsgDiagnostico */
	private MsgDiagnostico msgDiag;
	/** Logger */
	private Logger logTimer;

	/** Indicazione se deve essere effettuato il log delle query */
	private boolean logQuery = false;
	/** Numero di messaggi prelevati sulla singola query */
	private int limit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;
	/** Indicazione se deve essere effettuato l'order by nelle query */
	private boolean orderByQuery = false;
	
	
	
	/** Costruttore */
	public TimerGestoreRepositoryBusteThread() throws TimerException{
		
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
			this.msgDiag = MsgDiagnostico.newInstance(TimerGestoreRepositoryBuste.ID_MODULO);
			this.msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_REPOSITORY_BUSTE);
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_REPOSITORY_BUSTE, TimerGestoreRepositoryBuste.ID_MODULO);
		} catch (Exception e) {
			String msgErrore = "Riscontrato Errore durante l'inizializzazione del MsgDiagnostico";
			this.logTimer.error(msgErrore,e);
			throw new TimerException(msgErrore,e);
		}

		this.msgDiag.logPersonalizzato("avvioInCorso");
		this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("avvioInCorso"));
		
		try {
			this.propertiesReader = OpenSPCoop2Properties.getInstance();
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"InizializzazioneTimer");
			String msgErrore = "Riscontrato errore durante l'inizializzazione del Reader delle Properties di OpenSPCoop: "+e.getMessage();
			this.logTimer.error(msgErrore,e);
			throw new TimerException(msgErrore,e);
		}

		this.setTimeout((int)this.propertiesReader.getRepositoryIntervalloEliminazioneMessaggi());
		String sec = "secondi";
		if(this.getTimeout() == 1)
			sec = "secondo";
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIMEOUT, this.getTimeout()+" "+sec);
		
		this.scadenzaMessaggio = this.propertiesReader.getRepositoryIntervalloScadenzaMessaggi();
		String s = "minuti";
		if(this.scadenzaMessaggio == 1)
			s = "minuto";
		this.msgDiag.addKeyword(CostantiPdD.KEY_SCADENZA_MESSAGGIO, this.scadenzaMessaggio+" "+s);
		
		this.logQuery = this.propertiesReader.isTimerGestoreRepositoryBusteAbilitatoLog();
		this.orderByQuery = this.propertiesReader.isTimerGestoreRepositoryBusteAbilitatoOrderBy();
		
		this.limit = this.propertiesReader.getTimerGestoreRepositoryBusteLimit();
		if(this.limit<=0){
			this.limit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;
		}
		this.msgDiag.addKeyword(CostantiPdD.KEY_LIMIT, this.limit+"");
		
		this.msgDiag.logPersonalizzato("avvioEffettuato");
		this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("avvioEffettuato"));
	}
	
	@Override
	public void process(){
		try{
			// Prendo la gestione
			TimerGestoreRepositoryBusteLib gestoreMessaggiLib = 
				new TimerGestoreRepositoryBusteLib(this.msgDiag,this.logTimer,this.propertiesReader,this.logQuery,this.limit,this.orderByQuery);
			
			gestoreMessaggiLib.check();
			
		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e,"TimerGestoreRepositoryBusteLib.check()");
			this.logTimer.error("Errore generale: "+e.getMessage(),e);
		}finally{
		}
	}
}
