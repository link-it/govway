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



package org.openspcoop2.pdd.timers;

import org.slf4j.Logger;
import org.openspcoop2.pdd.config.ConfigurazioneCoda;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.threads.GestoreCodaRunnable;
import org.openspcoop2.utils.threads.RunnableLogger;

/**
 * Thread per la gestione del Threshold
 * 
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerConsegnaContenutiApplicativiThread extends GestoreCodaRunnable{

    /** Variabile che indica il Nome del modulo dell'architettura di OpenSPCoop rappresentato da questa classe */
    public final static String ID_MODULO = "TimerConsegnaContenutiApplicativi";
	
	
	/** Properties Reader */
	@SuppressWarnings("unused")
	private OpenSPCoop2Properties propertiesReader;
	/** MsgDiagnostico */
	private MsgDiagnostico msgDiag;
	
	/** Logger utilizzato per debug. */
	private Logger log = null;

	/** Indicazione se deve essere effettuato il debug */
	private boolean debug = false;
	/** Numero di messaggi prelevati sulla singola query */
	private int limit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;

	private ConfigurazioneCoda configurazioneCoda;
	public String getQueueConfig() {
		return this.configurazioneCoda.toString(false, ", ");
	}

	private static String getModuleId() throws TimerException {
		
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
		
		return ID_MODULO;
	}
	
	private static MsgDiagnostico msgDiag_staticInstance = null; 
	private static synchronized MsgDiagnostico getMsgDiagnostico() throws TimerException {
		if(msgDiag_staticInstance==null) {
			try {
				msgDiag_staticInstance = MsgDiagnostico.newInstance(TimerConsegnaContenutiApplicativiThread.ID_MODULO);
				msgDiag_staticInstance.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_CONSEGNA_CONTENUTI_APPLICATIVI);
				msgDiag_staticInstance.addKeyword(CostantiPdD.KEY_TIMER, ID_MODULO);
			} catch (Exception e) {
				String msgErrore = "Riscontrato Errore durante l'inizializzazione del MsgDiagnostico";
				throw new TimerException(msgErrore,e);
			}
		}
		return msgDiag_staticInstance;
	}
	
	private static TimerConsegnaContenutiApplicativi newTimerConsegnaContenutiApplicativi(ConfigurazioneCoda configurazioneCoda) throws TimerException {
		return new TimerConsegnaContenutiApplicativi(configurazioneCoda, getMsgDiagnostico(), 
				new RunnableLogger(ID_MODULO, OpenSPCoop2Logger.getLoggerOpenSPCoopConsegnaContenuti(configurazioneCoda.isDebug())), 
				new RunnableLogger(ID_MODULO, OpenSPCoop2Logger.getLoggerOpenSPCoopConsegnaContenutiSql(configurazioneCoda.isDebug())), 
				OpenSPCoop2Properties.getInstance(), 
				ConfigurazionePdDManager.getInstance(),
				RegistroServiziManager.getInstance());
	}
	
	
	/** Costruttore */
	public TimerConsegnaContenutiApplicativiThread(ConfigurazioneCoda configurazioneCoda) throws TimerException, UtilsException{
		
		super(getModuleId(), 
				configurazioneCoda.getPoolSize(),
				configurazioneCoda.getQueueSize(),
				configurazioneCoda.getNextMessages_limit(),
				configurazioneCoda.getNextMessages_intervalloControllo(),
				newTimerConsegnaContenutiApplicativi(configurazioneCoda),
				OpenSPCoop2Logger.getLoggerOpenSPCoopConsegnaContenuti(configurazioneCoda.isDebug()));
			
		this.propertiesReader = OpenSPCoop2Properties.getInstance();
		
		this.configurazioneCoda = configurazioneCoda;
		
		this.debug = configurazioneCoda.isDebug();
		
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopConsegnaContenuti(this.debug);
		
		try {
			this.msgDiag = getMsgDiagnostico();
		} catch (Exception e) {
			String msgErrore = "Riscontrato Errore durante l'inizializzazione del MsgDiagnostico";
			this.log.error(msgErrore,e);
			throw new TimerException(msgErrore,e);
		}

		this.msgDiag.logPersonalizzato("avvioInCorso");
		this.log.info(this.msgDiag.getMessaggio_replaceKeywords("avvioInCorso"));
		
		this.setTimeout(configurazioneCoda.getNextMessages_intervalloControllo());
		String s = "secondi";
		if(this.getTimeout() == 1)
			s = "secondo";
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIMEOUT, this.getTimeout()+" "+s);
		
		this.limit = configurazioneCoda.getNextMessages_limit();
		if(this.limit<=0){
			this.limit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;
		}
		this.msgDiag.addKeyword(CostantiPdD.KEY_LIMIT, this.limit+"");
		
		this.msgDiag.logPersonalizzato("avvioEffettuato");
		this.log.info(this.msgDiag.getMessaggio_replaceKeywords("avvioEffettuato"));
	}
	
}
