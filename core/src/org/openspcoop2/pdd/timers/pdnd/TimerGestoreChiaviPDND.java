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


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.PDNDConfig;
import org.openspcoop2.pdd.config.PDNDConfigUtilities;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.timers.TimerException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.threads.BaseThread;
import org.slf4j.Logger;


/**     
 * TimerGestoreChiaviPDND
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerGestoreChiaviPDND extends BaseThread{

	public static final String ID_MODULO = "TimerGestoreChiaviPDND";
	
	static final String CONNESSIONE_NON_DISPONIBILE = "Connessione al database della configurazione non disponibile";
	
	/** Logger utilizzato per debug. */
	private Logger logTimer = null;
	private void logError(String msgErrore, Exception e) {
		if(this.logTimer!=null) {
			this.logTimer.error(msgErrore,e);
		}
	}
	private MsgDiagnostico msgDiag = null;

	/** OpenSPCoop2Properties */
	@SuppressWarnings("unused")
	private OpenSPCoop2Properties op2Properties = null;
	
	private List<PDNDConfig> remoteStores;
	private Map<String, String> mapUrlCheckEventi;
	
	
	
	/** Costruttore */
	public TimerGestoreChiaviPDND(long timeout, List<PDNDConfig> remoteStores) throws TimerException{
	
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
			this.msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_CHIAVI_PDND);
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER, ID_MODULO);
		} catch (Exception e) {
			String msgErrore = "Riscontrato Errore durante l'inizializzazione del MsgDiagnostico";
			this.logError(msgErrore,e);
			throw new TimerException(msgErrore,e);
		}

		this.msgDiag.logPersonalizzato("avvioInCorso");
		this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("avvioInCorso"));
		
		try {
			this.op2Properties = OpenSPCoop2Properties.getInstance();
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"InizializzazioneTimer");
			String msgErrore = "Riscontrato errore durante l'inizializzazione del Reader delle Properties di OpenSPCoop: "+e.getMessage();
			this.logError(msgErrore,e);
			throw new TimerException(msgErrore,e);
		}
		
		this.remoteStores = remoteStores;
		
		initMapUrlCheckEventi();
		

		this.setTimeout((int)timeout);
		String sec = "secondi";
		if(this.getTimeout() == 1)
			sec = "secondo";
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIMEOUT, this.getTimeout()+" "+sec);
		
		this.msgDiag.logPersonalizzato("avvioEffettuato");
		this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("avvioEffettuato"));
	}
	
	private void initMapUrlCheckEventi() throws TimerException {
		if(this.remoteStores!=null && !this.remoteStores.isEmpty()) {
			for (PDNDConfig pdndConfig : this.remoteStores) {
				String remoteStoreName = pdndConfig.getRemoteStoreConfig().getStoreName();
				try {
					String urlCheckEventi = PDNDConfigUtilities.buildUrlCheckEventi(pdndConfig.getRemoteStoreConfig());
					if(this.mapUrlCheckEventi==null) {
						this.mapUrlCheckEventi = new HashMap<>();
					}
					this.mapUrlCheckEventi.put(remoteStoreName, urlCheckEventi);
				} catch (Exception e) {
					this.msgDiag.logErroreGenerico(e,"InizializzazioneTimer");
					String msgErrore = "Riscontrato errore durante l'inizializzazione della configurazione del timer: "+e.getMessage();
					this.logError(msgErrore,e);
					throw new TimerException(msgErrore,e);
				}
			}
		}
	}

	@Override
	public void process(){
		
		try{
			if(this.remoteStores!=null && !this.remoteStores.isEmpty()) {
				for (PDNDConfig remoteStore : this.remoteStores) {
			
					String remoteStoreName = remoteStore.getRemoteStoreConfig().getStoreName();
					this.msgDiag.addKeyword(CostantiPdD.KEY_REMOTE_STORE, remoteStoreName);
					
					String urlCheckEventi = this.mapUrlCheckEventi.get(remoteStoreName);
					
					process(remoteStoreName, remoteStore, urlCheckEventi);
				}
			}
			
		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e,"TimerGestoreChiaviPDNDLib.check()");
			this.logError("Errore generale: "+e.getMessage(),e);
		}
		
	}
	private void process(String remoteStoreName, PDNDConfig remoteStore, String urlCheckEventi){
		try {
			TimerGestoreChiaviPDNDLib timer = 
					new TimerGestoreChiaviPDNDLib(this.logTimer, this.msgDiag, 
							remoteStore.getRemoteStoreConfig(), urlCheckEventi,
							this.getTimeout());
				
			timer.check();
		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e,"TimerGestoreChiaviPDNDLib.check("+remoteStoreName+")");
			this.logError("Errore generale: "+e.getMessage(),e);
		}
	}
	
}
