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

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.eventi.Evento;
import org.openspcoop2.core.eventi.constants.CodiceEventoStatoGateway;
import org.openspcoop2.core.eventi.constants.TipoEvento;
import org.openspcoop2.core.eventi.constants.TipoSeverita;
import org.openspcoop2.core.eventi.utils.SeveritaConverter;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.DBTransazioniManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.QueueManager;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.FileSystemSerializer;
import org.openspcoop2.pdd.core.eventi.GestoreEventi;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.manifest.constants.ServiceBinding;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticProducer;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaProducer;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.threads.BaseThread;
import org.slf4j.Logger;

/**
 * Thread per la gestione del monitoraggio delle risorse
 * 
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerMonitoraggioRisorseThread extends BaseThread{

	public static TimerState STATE = TimerState.OFF; // abilitato in OpenSPCoop2Startup al momento dell'avvio
	
	/** Indicazione di risorse correttamente disponibili */
	public static boolean risorseDisponibili = true;
	
	/** Motivo dell'errore */
	public static Exception risorsaNonDisponibile = null;
	
	 /** Variabile che indica il Nome del modulo dell'architettura di OpenSPCoop rappresentato da questa classe */
    public final static String ID_MODULO = "MonitoraggioRisorse";
	 
    
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
	private List<ITracciaProducer> tracciamentiPersonalizzati=null;
	private List<String> tipiTracciamentiPersonalizzati=null;
	/** Risorsa: Transazioni di OpenSPCoop */
	private DBTransazioniManager dbTransazioniManager=null;
	/** Risorsa: MsgDiagnostici Personalizzati */
	private List<IDiagnosticProducer> msgDiagnosticiPersonalizzati=null;
	private List<String> tipiMsgDiagnosticiPersonalizzati=null;
	
	private GestoreEventi gestoreEventi;
	
	/** Costruttore 
	 * @throws Exception */
	public TimerMonitoraggioRisorseThread() throws Exception {
		this.propertiesReader = OpenSPCoop2Properties.getInstance();
		
		this.msgDiag = MsgDiagnostico.newInstance(TimerMonitoraggioRisorseThread.ID_MODULO);
		this.msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_MONITORAGGIO_RISORSE);
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_MONITORAGGIO_RISORSE, TimerMonitoraggioRisorseThread.ID_MODULO);
		
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
			if(this.propertiesReader.isTransazioniUsePddRuntimeDatasource()==false) {
				this.dbTransazioniManager = DBTransazioniManager.getInstance();
			}
		}
		if(this.propertiesReader.isAbilitatoControlloRisorseMsgDiagnosticiPersonalizzati()){
			this.msgDiagnosticiPersonalizzati = OpenSPCoop2Logger.getLoggerMsgDiagnosticoOpenSPCoopAppender();
			this.tipiMsgDiagnosticiPersonalizzati = OpenSPCoop2Logger.getTipoMsgDiagnosticoOpenSPCoopAppender();
		}
		if(this.propertiesReader.isControlloRisorseRegistrazioneEvento()) {
			this.gestoreEventi = GestoreEventi.getInstance();
		}		
	}
	
	/**
	 * Metodo che fa partire il Thread. 
	 *
	 */
	@Override
	public void run(){
		
		try {
		
			String sec = "secondi";
			if(this.propertiesReader.getControlloRisorseCheckInterval() == 1)
				sec = "secondo";
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIMEOUT, this.propertiesReader.getControlloRisorseCheckInterval()+" "+sec);
			this.msgDiag.logPersonalizzato("avvioEffettuato");
			
			String risorsaNonDisponibile = null;
			while(this.isStop() == false){
				boolean checkRisorseDisponibili = true;
				
				// Controllo che il sistema non sia andando in shutdown
				if(OpenSPCoop2Startup.contextDestroyed){
					this.logger.error("["+TimerMonitoraggioRisorseThread.ID_MODULO+"] Rilevato sistema in shutdown");
					return;
				}
				
				if(TimerState.ENABLED.equals(STATE)) {
				
					String descrizione = null;
					
					// Messaggi diagnostici personalizzati
					// Controllo per prima per sapere se posso usare poi i msg diagnostici per segnalare problemi
					if(checkRisorseDisponibili && this.propertiesReader.isAbilitatoControlloRisorseMsgDiagnosticiPersonalizzati()){
						String tipoMsgDiagnostico = null;
						try{
							for(int i=0; i< this.tipiMsgDiagnosticiPersonalizzati.size(); i++){
								tipoMsgDiagnostico = this.tipiMsgDiagnosticiPersonalizzati.get(i);
								this.logger.debug("Controllo MsgDiagnosticoPersonalizzato ["+tipoMsgDiagnostico+"]");
								this.msgDiagnosticiPersonalizzati.get(i).isAlive();
							}
						}catch(Exception e){
							risorsaNonDisponibile = "[MessaggioDiagnosticoAppender "+tipoMsgDiagnostico+"]";
							TimerMonitoraggioRisorseThread.risorsaNonDisponibile = new CoreException(risorsaNonDisponibile+" "+e.getMessage(),e);
							this.logger.error(risorsaNonDisponibile+" "+e.getMessage(),e);
							descrizione = risorsaNonDisponibile+" "+e.getMessage();
							checkRisorseDisponibili = false;
						}
					}
					// Database
					if(checkRisorseDisponibili && this.propertiesReader.isAbilitatoControlloRisorseDB()){
						try{
							this.logger.debug("Controllo Database");
							this.dbManager.isAlive();
						}catch(Exception e){
							risorsaNonDisponibile = "[Database]";
							TimerMonitoraggioRisorseThread.risorsaNonDisponibile = new CoreException(risorsaNonDisponibile+" "+e.getMessage(),e);
							this.logger.error(risorsaNonDisponibile+" "+e.getMessage(),e);
							descrizione = risorsaNonDisponibile+" "+e.getMessage();
							checkRisorseDisponibili = false;
						}
					}
					// JMS
					if(this.propertiesReader.isServerJ2EE()){
						if(checkRisorseDisponibili && this.propertiesReader.isAbilitatoControlloRisorseJMS()){
							try{
								this.logger.debug("Controllo BrokerJMS");
								this.queueManager.isAlive();
							}catch(Exception e){
								risorsaNonDisponibile = "[Broker JMS]";
								TimerMonitoraggioRisorseThread.risorsaNonDisponibile = new CoreException(risorsaNonDisponibile+" "+e.getMessage(),e);
								this.logger.error(risorsaNonDisponibile+" "+e.getMessage(),e);
								descrizione = risorsaNonDisponibile+" "+e.getMessage();
								checkRisorseDisponibili = false;
							}
						}
					}
					// Configurazione
					if(checkRisorseDisponibili && this.propertiesReader.isAbilitatoControlloRisorseConfigurazione()){
						try{
							this.logger.debug("Controllo Configurazione");
							this.configPdDReader.isAlive();
						}catch(Exception e){
							risorsaNonDisponibile = "[Configurazione]";
							TimerMonitoraggioRisorseThread.risorsaNonDisponibile = new CoreException(risorsaNonDisponibile+" "+e.getMessage(),e);
							this.logger.error(risorsaNonDisponibile+" "+e.getMessage(),e);
							descrizione = risorsaNonDisponibile+" "+e.getMessage();
							checkRisorseDisponibili = false;
						}
					}
					// Configurazione, validazione semantica
					if(checkRisorseDisponibili && this.propertiesReader.isAbilitatoControlloValidazioneSemanticaConfigurazione()){
						try{
							this.logger.debug("Controllo Validazione semantica della Configurazione");
							ClassNameProperties classNameReader = ClassNameProperties.getInstance();
							this.configPdDReader.validazioneSemantica(classNameReader.getConnettore(), 
									classNameReader.getMsgDiagnosticoOpenSPCoopAppender(),
									classNameReader.getTracciamentoOpenSPCoopAppender(),
									classNameReader.getDumpOpenSPCoopAppender(),
									classNameReader.getAutenticazionePortaDelegata(), classNameReader.getAutenticazionePortaApplicativa(), 
									classNameReader.getAutorizzazionePortaDelegata(),classNameReader.getAutorizzazionePortaApplicativa(),
									classNameReader.getAutorizzazioneContenutoPortaDelegata(),classNameReader.getAutorizzazioneContenutoPortaApplicativa(),
									classNameReader.getIntegrazionePortaDelegata(),
									classNameReader.getIntegrazionePortaApplicativa(),
									true,
									true,
									true, null);
						}catch(Exception e){
							risorsaNonDisponibile = "[Validazione semantica della Configurazione]";
							TimerMonitoraggioRisorseThread.risorsaNonDisponibile = new CoreException(risorsaNonDisponibile+" "+e.getMessage(),e);
							this.logger.error(risorsaNonDisponibile+" "+e.getMessage(),e);
							descrizione = risorsaNonDisponibile+" "+e.getMessage();
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
							TimerMonitoraggioRisorseThread.risorsaNonDisponibile = new CoreException(risorsaNonDisponibile+" "+e.getMessage(),e);
							this.logger.error(risorsaNonDisponibile+" "+e.getMessage(),e);
							descrizione = risorsaNonDisponibile+" "+e.getMessage();
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
									pFactoryManager.getOrganizationTypesAsArray(),
									pFactoryManager.getServiceTypesAsArray(ServiceBinding.SOAP),
									pFactoryManager.getServiceTypesAsArray(ServiceBinding.REST),
									ClassNameProperties.getInstance().getConnettore(),
									true,
									true,
									null);
						}catch(Exception e){
							risorsaNonDisponibile = "[Validazione semantica del Registri dei Servizi]";
							TimerMonitoraggioRisorseThread.risorsaNonDisponibile = new CoreException(risorsaNonDisponibile+" "+e.getMessage(),e);
							this.logger.error(risorsaNonDisponibile+" "+e.getMessage(),e);
							descrizione = risorsaNonDisponibile+" "+e.getMessage();
							checkRisorseDisponibili = false;
						}
					}
					// Tracciamenti personalizzati
					if(checkRisorseDisponibili && this.propertiesReader.isAbilitatoControlloRisorseTracciamentiPersonalizzati()){
						String tipoTracciamento = null;
						try{
							for(int i=0; i< this.tipiTracciamentiPersonalizzati.size(); i++){
								tipoTracciamento = this.tipiTracciamentiPersonalizzati.get(i);
								this.logger.debug("Controllo TracciamentoPersonalizzato ["+tipoTracciamento+"]");
								this.tracciamentiPersonalizzati.get(i).isAlive();
							}
						}catch(Exception e){
							risorsaNonDisponibile = "[TracciamentoAppender "+tipoTracciamento+"]";
							TimerMonitoraggioRisorseThread.risorsaNonDisponibile = new CoreException(risorsaNonDisponibile+" "+e.getMessage(),e);
							this.logger.error(risorsaNonDisponibile+" "+e.getMessage(),e);
							descrizione = risorsaNonDisponibile+" "+e.getMessage();
							checkRisorseDisponibili = false;
						}
						
						// nel tracciamento considero anche le transazioni
						if(checkRisorseDisponibili && this.propertiesReader.isTransazioniUsePddRuntimeDatasource()==false) {
							try{
								this.logger.debug("Controllo Database Transazioni");
								this.dbTransazioniManager.isAlive();
							}catch(Exception e){
								risorsaNonDisponibile = "[DatabaseTransazioni]";
								TimerMonitoraggioRisorseThread.risorsaNonDisponibile = new CoreException(risorsaNonDisponibile+" "+e.getMessage(),e);
								this.logger.error(risorsaNonDisponibile+" "+e.getMessage(),e);
								descrizione = risorsaNonDisponibile+" "+e.getMessage();
								checkRisorseDisponibili = false;
							}
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
						
						registraEvento(false, descrizione);
						
					}else if(checkRisorseDisponibili==true && this.lastCheck==false){
						this.msgDiag.logPersonalizzato("risorsaRitornataDisponibile");
						risorsaNonDisponibile = null;
						
						registraEvento(true, this.msgDiag.getMessaggio_replaceKeywords("risorsaRitornataDisponibile"));
					}
					this.lastCheck = checkRisorseDisponibili;
					TimerMonitoraggioRisorseThread.risorseDisponibili = checkRisorseDisponibili;
					
				}
				else {
					this.logger.info("Timer "+ID_MODULO+" disabilitato");
				}
				
							
				// CheckInterval
				this.sleepForNextCheck((int) this.propertiesReader.getControlloRisorseCheckInterval(), 1000);
				
			} 
		}finally {
			this.finished();
		}
	}
	
	private void registraEvento(boolean risorseDisponibili, String descrizione) {
		String clusterID = this.propertiesReader.getClusterId(false);
		Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopResources();
		Evento evento = null;
		try{
			if(this.gestoreEventi!=null){
				if(this.propertiesReader.isControlloRisorseRegistrazioneEvento()){
					evento = new Evento();
					evento.setTipo(TipoEvento.STATO_GATEWAY.getValue());
					evento.setCodice(risorseDisponibili ? CodiceEventoStatoGateway.RISORSE_SISTEMA_DISPONIBILI.getValue() : CodiceEventoStatoGateway.RISORSE_SISTEMA_NON_DISPONIBILI.getValue());
					evento.setSeverita(risorseDisponibili ? SeveritaConverter.toIntValue(TipoSeverita.INFO) : SeveritaConverter.toIntValue(TipoSeverita.ERROR));
					evento.setClusterId(clusterID);
					evento.setDescrizione(descrizione);
					this.gestoreEventi.log(evento,true);
				}
			}
		}catch(Exception e){
			// L'errore puo' avvenire poiche' lo shutdown puo' anche disattivare il datasource
			log.debug("Errore durante la registrazione dell'evento (risorse disponibili:"+descrizione+" '"+descrizione+"'): "+e.getMessage(),e);
			if(evento!=null){
				try{
			    	if(evento.getOraRegistrazione()==null){
			    		evento.setOraRegistrazione(DateManager.getDate());
			    	}
			    	ByteArrayOutputStream bout = new ByteArrayOutputStream();
			    	evento.writeTo(bout, WriteToSerializerType.XML_JAXB);
			    	bout.flush();
			    	bout.close();
					FileSystemSerializer.getInstance().registraEvento(bout.toByteArray(), evento.getOraRegistrazione());
				}catch(Exception eSerializer){
					log.error("Errore durante la registrazione su file system dell'evento: "+eSerializer.getMessage(),eSerializer);
				}
			}
		}
	}
}
