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

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.services.connector.proxy.IProxyOperationService;
import org.openspcoop2.pdd.services.connector.proxy.ProxyOperationServiceFactory;
import org.openspcoop2.pdd.timers.TimerException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.threads.BaseThread;
import org.slf4j.Logger;


/**     
 * TimerGestoreOperazioniRemote
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerGestoreOperazioniRemote extends BaseThread{

	public static final String ID_MODULO = "TimerGestoreOperazioniRemote";
	
	
	/** Logger utilizzato per debug. */
	private Logger logTimer = null;
	private void logError(String msgErrore, Exception e) {
		if(this.logTimer!=null) {
			this.logTimer.error(msgErrore,e);
		}
	}
	private MsgDiagnostico msgDiag = null;

	/** OpenSPCoop2Properties */
	private OpenSPCoop2Properties op2Properties = null;
	
	private Date dataUltimoAggiornamento = null;
	
	private String urlPrefix;

	private IProxyOperationService proxyOperationService = null;
	
	
	/** Costruttore */
	public TimerGestoreOperazioniRemote(long timeout) throws TimerException{
	
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
			this.msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_OPERAZIONI_ASINCRONE);
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
		
		String className = this.op2Properties.getProxyReadJMXResourcesAsyncProcessByTimerServiceImplClass();
		try {
			this.proxyOperationService = ProxyOperationServiceFactory.newInstance(className, this.logTimer);
		}catch(Exception e) {
			this.msgDiag.logErroreGenerico(e,"InizializzazioneTimer-ProxyOperationService");
			String msgErrore = "Riscontrato errore durante l'inizializzazione del ProxyOperationService: "+e.getMessage();
			this.logError(msgErrore,e);
			throw new TimerException(msgErrore,e);
		}
		
		this.urlPrefix = buildUlrPrefix(this.op2Properties);
		
		this.dataUltimoAggiornamento = DateManager.getDate();
		
		this.setTimeout((int)timeout);
		String sec = "secondi";
		if(this.getTimeout() == 1)
			sec = "secondo";
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIMEOUT, this.getTimeout()+" "+sec);
		
		this.msgDiag.logPersonalizzato("avvioEffettuato");
		this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("avvioEffettuato"));
		
	}
	
	private static String buildUlrPrefix(OpenSPCoop2Properties op2Properties) {
		// schema 
		String protocolSchema = op2Properties.getProxyReadJMXResourcesSchema();
		String tmp = op2Properties.getProxyReadJMXResourcesAsyncProcessByTimerSchema();
		if(tmp!=null && StringUtils.isNotEmpty(tmp)) {
			protocolSchema = tmp;
		}
		String protocol = (protocolSchema!=null && protocolSchema.trim().toLowerCase().startsWith("https")) ? "https://" : "http://";
		
		// hostname (solo in caso async-mode)
		String hostnameAsync = op2Properties.getProxyReadJMXResourcesAsyncProcessByTimerHostname();
		if(hostnameAsync==null || StringUtils.isEmpty(hostnameAsync)) {
			hostnameAsync = "localhost";
		}
		
		// port
		int port = 8080;
		if(op2Properties.getProxyReadJMXResourcesAsyncProcessByTimerPort()!=null && op2Properties.getProxyReadJMXResourcesAsyncProcessByTimerPort().intValue()>0) {
			port = op2Properties.getProxyReadJMXResourcesAsyncProcessByTimerPort().intValue();
		}
		else if(op2Properties.getProxyReadJMXResourcesPort()!=null && op2Properties.getProxyReadJMXResourcesPort().intValue()>0) {
			port = op2Properties.getProxyReadJMXResourcesPort().intValue();
		}
		
		String hostnameLocale = op2Properties.getClusterHostname();
		if(hostnameLocale!=null) {
			Integer portHostname = op2Properties.getProxyReadJMXResourcesPort(hostnameLocale);
			if(portHostname!=null && portHostname.intValue()>0) {
				port = portHostname.intValue();
			}
		}
		
		return protocol+hostnameAsync+":"+port;
	}

	@Override
	public void process(){
		
		try{
			
			this.msgDiag.addKeyword(CostantiPdD.DATA_AGGIORNAMENTO, DateUtils.getSimpleDateFormatMs().format(this.dataUltimoAggiornamento));
			
			Date now = DateManager.getDate();
			
			// Prendo la gestione
			TimerGestoreOperazioniRemoteLib timer = new TimerGestoreOperazioniRemoteLib(this.logTimer, this.msgDiag, 
					this.proxyOperationService,
					now, this.dataUltimoAggiornamento, this.urlPrefix);
			
			timer.check();
			
			this.dataUltimoAggiornamento = now;
			
		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e,"TimerGestoreOperazioniRemote.check()");
			this.logError("Errore generale: "+e.getMessage(),e);
		}
		
	}
	
}
