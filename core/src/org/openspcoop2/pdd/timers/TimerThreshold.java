/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

/**
 * Thread per la gestione del Threshold
 * 
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerThreshold extends Thread{

	/** Indicazione di uno spazio delle risorse di sistema corretto */
	public static boolean freeSpace = true;
	
	 /** Variabile che indica il Nome del modulo dell'architettura di OpenSPCoop rappresentato da questa classe */
    public final static String ID_MODULO = "GestoreThreshold";
	 
    
    // VARIABILE PER STOP
	private boolean stop = false;
	
	public boolean isStop() {
		return this.stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}
	
	/** Properties Reader */
	private OpenSPCoop2Properties propertiesReader;
	/** MessaggioDiagnostico */
	private MsgDiagnostico msgDiag;
	/** Gestore Soglia */
	private IThreshold[] gestore = null;
	/** tipi Gestore Soglia */
	private String[] tipiThreshold = null;
	/** Logger */
	private Logger log;
	/** LastImage */
	private boolean lastCheck = true;
	
	/** Costruttore */
	public TimerThreshold() throws ThresholdException{
		this.propertiesReader = OpenSPCoop2Properties.getInstance();
		
		this.msgDiag = new MsgDiagnostico(this.propertiesReader.getIdentitaPortaDefault(null),TimerThreshold.ID_MODULO);
		this.msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_THRESHOLD);
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_THRESHOLD, TimerThreshold.ID_MODULO);
		
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopResources();
		this.tipiThreshold = this.propertiesReader.getRepositoryThresholdTypes();
		this.gestore = new IThreshold[this.tipiThreshold.length];
		Loader loader = Loader.getInstance();
		for(int i=0;i<this.tipiThreshold.length;i++){
			String tipoClass = ClassNameProperties.getInstance().getThreshold(this.tipiThreshold[i]);
			if(tipoClass == null){
				throw new ThresholdException("Riscontrato errore durante l'istanziazione del threshold di tipo ["+this.tipiThreshold[i]+"]: is null");
			}
			try{
				this.gestore[i] = (IThreshold) loader.newInstance(tipoClass);
				this.gestore[i].toString();
			}catch(Exception e){
				//e.printStackTrace();
				throw new ThresholdException("Riscontrato errore durante l'istanziazione del threshold di tipo ["+this.tipiThreshold[i]+"]: "+e.getMessage());
			}
		}
	}
	
	/**
	 * Metodo che fa partire il Thread. 
	 *
	 */
	@Override
	public void run(){
		
		String sec = "secondi";
		if(this.propertiesReader.getRepositoryThresholdCheckInterval() == 1)
			sec = "secondo";
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIMEOUT, this.propertiesReader.getRepositoryThresholdCheckInterval()+" "+sec);
		this.msgDiag.logPersonalizzato("avvioEffettuato");
		
		while(this.stop == false){
			
			// Controllo che il sistema non sia andando in shutdown
			if(OpenSPCoop2Startup.contextDestroyed){
				this.log.error("["+TimerThreshold.ID_MODULO+"] Rilevato sistema in shutdown");
				return;
			}
			
			// Controllo risorse di sistema disponibili
			if( TimerMonitoraggioRisorse.risorseDisponibili == false){
				this.log.error("["+TimerThreshold.ID_MODULO+"] Risorse di sistema non disponibili: "+TimerMonitoraggioRisorse.risorsaNonDisponibile.getMessage(),TimerMonitoraggioRisorse.risorsaNonDisponibile);
				this.sleep();
				continue;
			}
			
			boolean checkThreshold = true;
			for(int i=0;i<this.tipiThreshold.length;i++){
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_THRESHOLD_TIPO, this.tipiThreshold[i]);
				this.msgDiag.logPersonalizzato("controlloInCorso");
				try{
					checkThreshold = this.gestore[i].check(this.propertiesReader.getRepositoryThresholdParameters(this.tipiThreshold[i]));
				}catch(Exception e){
					this.msgDiag.logErroreGenerico(e,"checkThreshold("+this.tipiThreshold[i]+")");
					this.log.debug("Controllo threshould non riuscito (tipo:"+this.tipiThreshold[i]+")",e);
					checkThreshold = false;
				}
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
			TimerThreshold.freeSpace = checkThreshold;
			
			
			// CheckInterval
			this.sleep();
		} 
	}
	
	private void sleep(){
		if(this.stop==false){
			int i=0;
			while(i<this.propertiesReader.getRepositoryThresholdCheckInterval()){
				try{
					Thread.sleep(1000);		
				}catch(Exception e){}
				if(this.stop){
					break; // thread terminato, non lo devo far piu' dormire
				}
				i++;
			}
		}
	}
}
