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



package org.openspcoop2.pdd.timers;

import java.util.List;

import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.threshold.IThreshold;
import org.openspcoop2.pdd.core.threshold.ThresholdException;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.threads.BaseThread;
import org.slf4j.Logger;

/**
 * Thread per la gestione del Threshold
 * 
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerThresholdThread extends BaseThread{

	private static TimerState STATE = TimerState.OFF; // abilitato in OpenSPCoop2Startup al momento dell'avvio
	
	public static TimerState getSTATE() {
		return STATE;
	}
	public static void setSTATE(TimerState sTATE) {
		STATE = sTATE;
	}

	/** Indicazione di uno spazio delle risorse di sistema corretto */
	public static boolean freeSpace = true;
	
	 /** Variabile che indica il Nome del modulo dell'architettura di OpenSPCoop rappresentato da questa classe */
    public static final String ID_MODULO = "GestoreThreshold";
	 
    
	/** Properties Reader */
	private OpenSPCoop2Properties propertiesReader;
	/** MessaggioDiagnostico */
	private MsgDiagnostico msgDiag;
	/** Gestore Soglia */
	private IThreshold[] gestore = null;
	/** tipi Gestore Soglia */
	private List<String> tipiThreshold = null;
	/** Logger */
	private Logger log;
	/** LastImage */
	private boolean lastCheck = true;
	
	/** Costruttore */
	public TimerThresholdThread() throws ThresholdException{
		this.propertiesReader = OpenSPCoop2Properties.getInstance();
		
		this.msgDiag = MsgDiagnostico.newInstance(TimerThresholdThread.ID_MODULO);
		this.msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_THRESHOLD);
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_THRESHOLD, TimerThresholdThread.ID_MODULO);
		
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopResources();
		this.tipiThreshold = this.propertiesReader.getRepositoryThresholdTypes();
		this.gestore = new IThreshold[this.tipiThreshold.size()];
		Loader loader = Loader.getInstance();
		for(int i=0;i<this.tipiThreshold.size();i++){
			String tipoClass = ClassNameProperties.getInstance().getThreshold(this.tipiThreshold.get(i));
			if(tipoClass == null){
				throw new ThresholdException("Riscontrato errore durante l'istanziazione del threshold di tipo ["+this.tipiThreshold.get(i)+"]: is null");
			}
			try{
				this.gestore[i] = (IThreshold) loader.newInstance(tipoClass);
				this.gestore[i].toString();
			}catch(Exception e){
				//e.printStackTrace();
				throw new ThresholdException("Riscontrato errore durante l'istanziazione del threshold di tipo ["+this.tipiThreshold.get(i)+"]: "+e.getMessage());
			}
		}
	}
	

	@Override
	public void process(){
		// nop: ho ridefinito il metodo run
	}
	
	@Override
	public void run(){
		
		try {
		
			String sec = "secondi";
			if(this.propertiesReader.getRepositoryThresholdCheckInterval() == 1)
				sec = "secondo";
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIMEOUT, this.propertiesReader.getRepositoryThresholdCheckInterval()+" "+sec);
			this.msgDiag.logPersonalizzato("avvioEffettuato");
			
			while(!this.isStop()){
				
				// Controllo che il sistema non sia andando in shutdown
				if(OpenSPCoop2Startup.contextDestroyed){
					this.log.error("["+TimerThresholdThread.ID_MODULO+"] Rilevato sistema in shutdown");
					return;
				}
				
				// Controllo risorse di sistema disponibili
				if( !TimerMonitoraggioRisorseThread.isRisorseDisponibili()){
					this.log.error("["+TimerThresholdThread.ID_MODULO+"] Risorse di sistema non disponibili: "+TimerMonitoraggioRisorseThread.getRisorsaNonDisponibile().getMessage(),TimerMonitoraggioRisorseThread.getRisorsaNonDisponibile());
					this.sleepForNextCheck((int) this.propertiesReader.getRepositoryThresholdCheckInterval(), 1000);
					continue;
				}
				
				if(TimerState.ENABLED.equals(STATE)) {
				
					boolean checkThreshold = true;
					for(int i=0;i<this.tipiThreshold.size();i++){
						this.log.debug("Verifica '"+this.tipiThreshold.get(i)+"' in corso ...");
						this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_THRESHOLD_TIPO, this.tipiThreshold.get(i));
						this.msgDiag.logPersonalizzato("controlloInCorso");
						try{
							checkThreshold = this.gestore[i].check(this.propertiesReader.getRepositoryThresholdParameters(this.tipiThreshold.get(i)));
						}catch(Exception e){
							this.msgDiag.logErroreGenerico(e,"checkThreshold("+this.tipiThreshold.get(i)+")");
							this.log.debug("Controllo threshould non riuscito (tipo:"+this.tipiThreshold.get(i)+")",e);
							checkThreshold = false;
						}
						this.log.debug("Verifica '"+this.tipiThreshold.get(i)+"' completato con esito "+(checkThreshold?"ok":"ko"));
						if(checkThreshold == false)
							break;
					}
						
					
					// avvisi
					if(checkThreshold==false && this.lastCheck==true){
						this.msgDiag.logPersonalizzato("risorsaNonDisponibile");
					}else if(checkThreshold==true && this.lastCheck==false){
						this.msgDiag.logPersonalizzato("risorsaRitornataDisponibile");
					}
					this.lastCheck = checkThreshold;
					TimerThresholdThread.freeSpace = checkThreshold;
					
				}
				else {
					this.log.info("Timer "+ID_MODULO+" disabilitato");
				}
				
				
				// CheckInterval
				this.sleepForNextCheck((int) this.propertiesReader.getRepositoryThresholdCheckInterval(), 1000);
				
			} 
		}finally {
			this.finished();
		}
	}
	
}
