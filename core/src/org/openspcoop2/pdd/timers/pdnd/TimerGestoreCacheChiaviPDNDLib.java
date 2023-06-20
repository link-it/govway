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

package org.openspcoop2.pdd.timers.pdnd;


import java.sql.Connection;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.keystore.KeystoreException;
import org.openspcoop2.pdd.core.keystore.RemoteStoreProviderDriverUtils;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.timers.TimerException;
import org.openspcoop2.pdd.timers.TimerMonitoraggioRisorseThread;
import org.openspcoop2.pdd.timers.TimerState;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.security.keystore.cache.RemoteStoreCache;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.certificate.remote.RemoteKeyType;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;


/**     
 * TimerGestoreChiaviPDNDLib
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerGestoreCacheChiaviPDNDLib {

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
	private void logTimerDebug(String msg) {
		if(this.logTimer!=null) {
			this.logTimer.debug(msg);
		}
	}
	
	private RemoteStoreConfig remoteStore;
	private RemoteKeyType remoteKeyType;
	
	
	/** Costruttore */
	public TimerGestoreCacheChiaviPDNDLib(Logger logTimer, MsgDiagnostico msgDiag, RemoteStoreConfig remoteStore, RemoteKeyType remoteKeyType) throws TimerException{
		
		this.op2Properties = OpenSPCoop2Properties.getInstance();
		this.tipoDatabase = this.op2Properties.getDatabaseType();
		if(this.tipoDatabase==null){
			throw new TimerException("Tipo Database non definito");
		}
		
		boolean debug = this.op2Properties.isGestoreChiaviPDNDDebug();
		
		this.logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopGestoreChiaviPDND(debug);
		this.logTimer = logTimer;
		this.msgDiag = msgDiag;
		
		this.remoteStore = remoteStore;
		this.remoteKeyType = remoteKeyType;		
	}
	
	public void check() throws TimerException {
		
		// Controllo che il sistema non sia andando in shutdown
		if(OpenSPCoop2Startup.contextDestroyed){
			this.logTimerError("["+TimerGestoreCacheChiaviPDND.ID_MODULO+"] Rilevato sistema in shutdown");
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
		if( !TimerMonitoraggioRisorseThread.risorseDisponibili){
			this.logTimerError("["+TimerGestoreCacheChiaviPDND.ID_MODULO+"] Risorse di sistema non disponibili: "+TimerMonitoraggioRisorseThread.risorsaNonDisponibile.getMessage(),TimerMonitoraggioRisorseThread.risorsaNonDisponibile);
			return;
		}
		if( !MsgDiagnostico.gestoreDiagnosticaDisponibile){
			this.logTimerError("["+TimerGestoreCacheChiaviPDND.ID_MODULO+"] Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage(),MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
			return;
		}
		
		boolean enabled = TimerState.ENABLED.equals(TimerGestoreCacheChiaviPDNDLib.state);
		if(!enabled) {
			emitDiagnosticLog("disabilitato");
			return;
		}
		
		emitDiagnosticLog("letturaCacheKeys");

		long startControlloTimer = DateManager.getTimeMillis();
			
		Connection conConfigurazione = null;
    	DriverConfigurazioneDB driverConfigurazioneDbGestoreConnection = null;
		try{

			Object oConfig = ConfigurazionePdDReader.getDriverConfigurazionePdD();
			if(oConfig instanceof DriverConfigurazioneDB) {
				driverConfigurazioneDbGestoreConnection = (DriverConfigurazioneDB) oConfig;
				conConfigurazione = driverConfigurazioneDbGestoreConnection.getConnection(TimerGestoreChiaviPDND.ID_MODULO, false);
				if(conConfigurazione == null)
					throw new TimerException("Connessione al database della configurazione non disponibile");	
			}
			else {
				throw new TimerException("Gestore utilizzabile solamente con una configurazione su database");
			}
			
			long idStore = RemoteStoreProviderDriverUtils.getIdRemoteStore(conConfigurazione, this.tipoDatabase, this.remoteStore);
			if(idStore<=0) {
				emitDiagnosticLog("letturaCacheKeys.nonNecessaria");
				return;
			}
			
			process(startControlloTimer, conConfigurazione, idStore);
		}
		catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"TimerGestoreCacheChiaviPDND");
			this.logTimerError("Riscontrato errore durante la gestione della cache delle chavi della PDND: "+ e.getMessage(),e);
		}finally{
			try{
				if(conConfigurazione!=null)
					driverConfigurazioneDbGestoreConnection.releaseConnection(conConfigurazione);
			}catch(Exception eClose){
				// ignore
			}
		}
			
	}
	
	private void process(long startControlloTimer, Connection conConfigurazione, long idStore) throws SecurityException, KeystoreException, CoreException {
		// NOTA: non viene eliminato dalla cache di primo livello "GestoreRichieste" poichè:
		// - per default i remote store non sono salvati nella cache di primo livello, vedi proprietà 'org.openspcoop2.pdd.cache.impl.requestManager.remoteStore.saveInCache' di govway.properties
		// - se vengono salvati, il tempo di vita di questi elementi deve essere più basso rispetto alle normali cache (per default è 30 secondi)
		
		List<String> keys = GestoreKeystoreCache.keysRemoteStore();
		int sizeCacheKeys = keys!=null ? keys.size() : 0;
		int size = 0;
		if(sizeCacheKeys>0) {
			for (String key : keys) {
				
				String keyCachePrefix = RemoteStoreCache.RESTORE_STORE_PREFIX+ RemoteStoreCache.getPrefixKeyCache(this.remoteStore, this.remoteKeyType);
				if(key!=null && key.startsWith(keyCachePrefix) && key.length()>keyCachePrefix.length()) {
					size++;
					String kid = key.substring(keyCachePrefix.length());
					if(!RemoteStoreProviderDriverUtils.existsRemoteStoreKey(conConfigurazione, this.tipoDatabase, idStore, kid, true)) {
						removeFromCache(key, kid);
					}
					
				}
				
			}
		}

		long endGenerazione = DateManager.getTimeMillis();
		String tempoImpiegato = Utilities.convertSystemTimeIntoStringMillisecondi((endGenerazione-startControlloTimer), true);
		this.msgDiag.addKeyword(CostantiPdD.KEY_TEMPO_GESTIONE, tempoImpiegato);
		
		this.msgDiag.addKeyword(CostantiPdD.KEY_NUMERO_EVENTI, size+"");
		
		emitDiagnosticLog("letturaCacheKeys.effettuata");
	}

	private void removeFromCache(String key, String kid) throws SecurityException, CoreException {
		this.logTimerDebug("Elimino chiave '"+key+"' dalla cache");
		
		GestoreKeystoreCache.removeRemoteStore(null, kid, this.remoteKeyType, this.remoteStore);
		
		if(this.op2Properties.isGestoreChiaviPDNDclientInfoEnabled()) {
			GestoreKeystoreCache.removeRemoteStoreClientInfo(null, kid, this.remoteStore);
		}
	}
	
	private void emitDiagnosticLog(String code) {
		this.msgDiag.logPersonalizzato(code);
		String msg = this.msgDiag.getMessaggio_replaceKeywords(code);
		this.logCoreInfo(msg);
		this.logTimerInfo(msg);
	}
}
