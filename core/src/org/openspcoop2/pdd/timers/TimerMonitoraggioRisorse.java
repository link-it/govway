/*
 * OpenSPCoop - Customizable API Gateway 
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

import java.util.Vector;

import org.slf4j.Logger;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.QueueManager;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.diagnostica.IMsgDiagnosticoOpenSPCoopAppender;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciamentoOpenSPCoopAppender;

/**
 * Thread per la gestione del monitoraggio delle risorse
 * 
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerMonitoraggioRisorse extends Thread{

	/** Indicazione di risorse correttamente disponibili */
	public static boolean risorseDisponibili = true;
	
	/** Motivo dell'errore */
	public static Exception risorsaNonDisponibile = null;
	
	 /** Variabile che indica il Nome del modulo dell'architettura di OpenSPCoop rappresentato da questa classe */
    public final static String ID_MODULO = "MonitoraggioRisorse";
	 
    
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
	
	/** LastImage */
	private boolean lastCheck = true;
	
	/** Logger per debug */
	private Logger logger = null;
	
	/** Risorsa: DB di OpenSPCoop */
	private DBManager dbManager=null;
	/** Risorsa: JMS di OpenSPCoop */
	private QueueManager queueManager=null;
	/** Risorsa: Configurazione di OpenSPCoop */
	private ConfigurazionePdDManager configPdDReader=null;
	/** Risorsa: Registri dei Servizi di OpenSPCoop */
	private RegistroServiziManager registriPdDReader=null;
	/** Risorsa: Tracciamenti Personalizzati */
	private Vector<ITracciamentoOpenSPCoopAppender> tracciamentiPersonalizzati=null;
	private Vector<String> tipiTracciamentiPersonalizzati=null;
	/** Risorsa: MsgDiagnostici Personalizzati */
	private Vector<IMsgDiagnosticoOpenSPCoopAppender> msgDiagnosticiPersonalizzati=null;
	private Vector<String> tipiMsgDiagnosticiPersonalizzati=null;
	
	/** Costruttore */
	public TimerMonitoraggioRisorse() {
		this.propertiesReader = OpenSPCoop2Properties.getInstance();
		
		this.msgDiag = new MsgDiagnostico(this.propertiesReader.getIdentitaPortaDefault(null),TimerMonitoraggioRisorse.ID_MODULO);
		this.msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_MONITORAGGIO_RISORSE);
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_MONITORAGGIO_RISORSE, TimerMonitoraggioRisorse.ID_MODULO);
		
		this.logger = OpenSPCoop2Logger.getLoggerOpenSPCoopResources();
		if(this.propertiesReader.isAbilitatoControlloRisorseDB()){
			this.dbManager = DBManager.getInstance();
		}
		if(this.propertiesReader.isAbilitatoControlloRisorseJMS()){
			this.queueManager = QueueManager.getInstance();
		}
		if(this.propertiesReader.isAbilitatoControlloRisorseConfigurazione() || this.propertiesReader.isAbilitatoControlloValidazioneSemanticaConfigurazione()){
			this.configPdDReader = ConfigurazionePdDManager.getInstance();
		}
		if(this.propertiesReader.isAbilitatoControlloRisorseRegistriServizi() || this.propertiesReader.isAbilitatoControlloValidazioneSemanticaRegistriServizi()){
			this.registriPdDReader = RegistroServiziManager.getInstance();
		}
		if(this.propertiesReader.isAbilitatoControlloRisorseTracciamentiPersonalizzati()){
			this.tracciamentiPersonalizzati = OpenSPCoop2Logger.getLoggerTracciamentoOpenSPCoopAppender();
			this.tipiTracciamentiPersonalizzati = OpenSPCoop2Logger.getTipoTracciamentoOpenSPCoopAppender();
		}
		if(this.propertiesReader.isAbilitatoControlloRisorseMsgDiagnosticiPersonalizzati()){
			this.msgDiagnosticiPersonalizzati = OpenSPCoop2Logger.getLoggerMsgDiagnosticoOpenSPCoopAppender();
			this.tipiMsgDiagnosticiPersonalizzati = OpenSPCoop2Logger.getTipoMsgDiagnosticoOpenSPCoopAppender();
		}
	}
	
	/**
	 * Metodo che fa partire il Thread. 
	 *
	 */
	@Override
	public void run(){
		
		String sec = "secondi";
		if(this.propertiesReader.getControlloRisorseCheckInterval() == 1)
			sec = "secondo";
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIMEOUT, this.propertiesReader.getControlloRisorseCheckInterval()+" "+sec);
		this.msgDiag.logPersonalizzato("avvioEffettuato");
		
		String risorsaNonDisponibile = null;
		while(this.stop == false){
			boolean checkRisorseDisponibili = true;
				
			// Controllo che il sistema non sia andando in shutdown
			if(OpenSPCoop2Startup.contextDestroyed){
				this.logger.error("["+TimerMonitoraggioRisorse.ID_MODULO+"] Rilevato sistema in shutdown");
				return;
			}
			
			// Messaggi diagnostici personalizzati
			// Controllo per prima per sapere se posso usare poi i msg diagnostici per segnalare problemi
			if(checkRisorseDisponibili && this.propertiesReader.isAbilitatoControlloRisorseMsgDiagnosticiPersonalizzati()){
				String tipoMsgDiagnostico = null;
				try{
					for(int i=0; i< this.tipiMsgDiagnosticiPersonalizzati.size(); i++){
						tipoMsgDiagnostico = this.tipiMsgDiagnosticiPersonalizzati.get(i);
						this.logger.debug("Controllo MsgDiagnosticoPersonalizzato ["+tipoMsgDiagnostico+"] di OpenSPCoop");
						this.msgDiagnosticiPersonalizzati.get(i).isAlive();
					}
				}catch(Exception e){
					risorsaNonDisponibile = "[MessaggioDiagnosticoAppender "+tipoMsgDiagnostico+"]";
					TimerMonitoraggioRisorse.risorsaNonDisponibile = new CoreException(risorsaNonDisponibile+" "+e.getMessage(),e);
					this.logger.error(risorsaNonDisponibile+" "+e.getMessage(),e);
					checkRisorseDisponibili = false;
				}
			}
			// Database
			if(checkRisorseDisponibili && this.propertiesReader.isAbilitatoControlloRisorseDB()){
				try{
					this.logger.debug("Controllo Database di OpenSPCoop");
					this.dbManager.isAlive();
				}catch(Exception e){
					risorsaNonDisponibile = "[Database della Porta di Dominio]";
					TimerMonitoraggioRisorse.risorsaNonDisponibile = new CoreException(risorsaNonDisponibile+" "+e.getMessage(),e);
					this.logger.error(risorsaNonDisponibile+" "+e.getMessage(),e);
					checkRisorseDisponibili = false;
				}
			}
			// JMS
			if(this.propertiesReader.isServerJ2EE()){
				if(checkRisorseDisponibili && this.propertiesReader.isAbilitatoControlloRisorseJMS()){
					try{
						this.logger.debug("Controllo BrokerJMS di OpenSPCoop");
						this.queueManager.isAlive();
					}catch(Exception e){
						risorsaNonDisponibile = "[Broker JMS della Porta di Dominio]";
						TimerMonitoraggioRisorse.risorsaNonDisponibile = new CoreException(risorsaNonDisponibile+" "+e.getMessage(),e);
						this.logger.error(risorsaNonDisponibile+" "+e.getMessage(),e);
						checkRisorseDisponibili = false;
					}
				}
			}
			// Configurazione
			if(checkRisorseDisponibili && this.propertiesReader.isAbilitatoControlloRisorseConfigurazione()){
				try{
					this.logger.debug("Controllo Configurazione di OpenSPCoop");
					this.configPdDReader.isAlive();
				}catch(Exception e){
					risorsaNonDisponibile = "[Configurazione della Porta di Dominio]";
					TimerMonitoraggioRisorse.risorsaNonDisponibile = new CoreException(risorsaNonDisponibile+" "+e.getMessage(),e);
					this.logger.error(risorsaNonDisponibile+" "+e.getMessage(),e);
					checkRisorseDisponibili = false;
				}
			}
			// Configurazione, validazione semantica
			if(checkRisorseDisponibili && this.propertiesReader.isAbilitatoControlloValidazioneSemanticaConfigurazione()){
				try{
					this.logger.debug("Controllo Validazione semantica della Configurazione di OpenSPCoop");
					ClassNameProperties classNameReader = ClassNameProperties.getInstance();
					this.configPdDReader.validazioneSemantica(classNameReader.getConnettore(), 
							classNameReader.getMsgDiagnosticoOpenSPCoopAppender(),
							classNameReader.getTracciamentoOpenSPCoopAppender(),
							classNameReader.getAutenticazione(), classNameReader.getAutorizzazione(),
							classNameReader.getAutorizzazioneContenuto(),classNameReader.getAutorizzazioneContenutoBuste(),
							classNameReader.getIntegrazionePortaDelegata(),
							classNameReader.getIntegrazionePortaApplicativa(),
							true,
							true,
							true, null);
				}catch(Exception e){
					risorsaNonDisponibile = "[Validazione semantica della Configurazione della Porta di Dominio]";
					TimerMonitoraggioRisorse.risorsaNonDisponibile = new CoreException(risorsaNonDisponibile+" "+e.getMessage(),e);
					this.logger.error(risorsaNonDisponibile+" "+e.getMessage(),e);
					checkRisorseDisponibili = false;
				}
			}
			// Registro dei Servizi
			if(checkRisorseDisponibili && this.propertiesReader.isAbilitatoControlloRisorseRegistriServizi()){
				try{
					this.logger.debug("Controllo Registri dei Servizi");
					this.registriPdDReader.isAlive(this.propertiesReader.isControlloRisorseRegistriRaggiungibilitaTotale());
				}catch(Exception e){
					risorsaNonDisponibile = "[Registri dei Servizi]";
					TimerMonitoraggioRisorse.risorsaNonDisponibile = new CoreException(risorsaNonDisponibile+" "+e.getMessage(),e);
					this.logger.error(risorsaNonDisponibile+" "+e.getMessage(),e);
					checkRisorseDisponibili = false;
				}
			}
			// Registro dei Servizi, validazione semantica
			if(checkRisorseDisponibili && this.propertiesReader.isAbilitatoControlloValidazioneSemanticaRegistriServizi()){
				try{
					ProtocolFactoryManager pFactoryManager = ProtocolFactoryManager.getInstance();
					this.logger.debug("Controllo Validazione semantica del Registri dei Servizi");
					this.registriPdDReader.validazioneSemantica(this.propertiesReader.isControlloRisorseRegistriRaggiungibilitaTotale(), 
							this.propertiesReader.isValidazioneSemanticaRegistroServiziCheckURI(), 
							pFactoryManager.getSubjectTypesAsArray(),
							pFactoryManager.getServiceTypesAsArray(),
							ClassNameProperties.getInstance().getConnettore(),
							true,
							true,
							null);
				}catch(Exception e){
					risorsaNonDisponibile = "[Validazione semantica del Registri dei Servizi]";
					TimerMonitoraggioRisorse.risorsaNonDisponibile = new CoreException(risorsaNonDisponibile+" "+e.getMessage(),e);
					this.logger.error(risorsaNonDisponibile+" "+e.getMessage(),e);
					checkRisorseDisponibili = false;
				}
			}
			// Tracciamenti personalizzati
			if(checkRisorseDisponibili && this.propertiesReader.isAbilitatoControlloRisorseTracciamentiPersonalizzati()){
				String tipoTracciamento = null;
				try{
					for(int i=0; i< this.tipiTracciamentiPersonalizzati.size(); i++){
						tipoTracciamento = this.tipiTracciamentiPersonalizzati.get(i);
						this.logger.debug("Controllo TracciamentoPersonalizzato ["+tipoTracciamento+"] di OpenSPCoop");
						this.tracciamentiPersonalizzati.get(i).isAlive();
					}
				}catch(Exception e){
					risorsaNonDisponibile = "[TracciamentoAppender "+tipoTracciamento+"]";
					TimerMonitoraggioRisorse.risorsaNonDisponibile = new CoreException(risorsaNonDisponibile+" "+e.getMessage(),e);
					this.logger.error(risorsaNonDisponibile+" "+e.getMessage(),e);
					checkRisorseDisponibili = false;
				}
			}
			
				
			
			// avvisi
			if(risorsaNonDisponibile!=null)
				this.msgDiag.addKeyword(CostantiPdD.KEY_RISORSA_NON_DISPONIBILE, risorsaNonDisponibile);
			if(checkRisorseDisponibili==false && this.lastCheck==true){
				if(risorsaNonDisponibile!=null && (risorsaNonDisponibile.startsWith("[MessaggioDiagnosticoAppender")==false) ){
					if(risorsaNonDisponibile.startsWith("[Validazione semantica")){
						this.msgDiag.logPersonalizzato("validazioneSemanticaFallita");
					}else{
						this.msgDiag.logPersonalizzato("risorsaNonDisponibile");
					}
				}
				else
					this.logger.warn("Il Monitoraggio delle risorse ha rilevato che la risorsa "+risorsaNonDisponibile+" non e' raggiungibile, tutti i servizi/moduli della porta di dominio sono momentanemanete sospesi.");
			}else if(checkRisorseDisponibili==true && this.lastCheck==false){
				this.msgDiag.logPersonalizzato("risorsaRitornataDisponibile");
				risorsaNonDisponibile = null;
			}
			this.lastCheck = checkRisorseDisponibili;
			TimerMonitoraggioRisorse.risorseDisponibili = checkRisorseDisponibili;
						
			// CheckInterval
			if(this.stop==false){
				int i=0;
				while(i<this.propertiesReader.getControlloRisorseCheckInterval()){
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
}
