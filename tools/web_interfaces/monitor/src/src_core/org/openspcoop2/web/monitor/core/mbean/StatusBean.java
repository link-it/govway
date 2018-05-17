package org.openspcoop2.web.monitor.core.mbean;

import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.status.ISondaPdd;
import org.openspcoop2.web.monitor.core.status.SondaPddManager;
import org.openspcoop2.web.monitor.core.status.SondaStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

/***
 * 
 * Definisce un Bean per il controllo dello stato della PdD
 * 
 * @author pintori
 *
 */
public class StatusBean implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String intervalloRefresh;

	private static Logger log = LoggerManager.getPddMonitorCoreLogger();

	private String tooltipIconaStato = "";

	private String iconaStato = "";

	private boolean aggiornamentoAutomatico = false;

	private boolean enable ;

	private static final String ICONA_STATO_OK= "/images/tema_link/status_green.png";
	private static final String ICONA_STATO_WARN= "/images/tema_link/status_yellow.png";
	private static final String ICONA_STATO_ERR= "/images/tema_link/status_red.png";

	private List<ISondaPdd> sondePdd = null;

	private transient boolean initialized = false; // bug fix per tomcat dove non viene re-effettuato il restore dei bean (non avviene il login essendo la sessione già attiva)
	private synchronized void initialize(){
		if(this.initialized==false){
			
			try {

				PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(StatusBean.log);

				this.intervalloRefresh = pddMonitorProperties.getIntervalloRefreshStatusPdD();

				this.enable = pddMonitorProperties.isStatusPdDEnabled();

				this.sondePdd = SondaPddManager.getInstance(log).getSondePdd();

				log.debug("Verranno monitorate ["+this.sondePdd.size()+"] Sonde.");
				//
				//			List<String> nomiPdD = pddMonitorProperties.getListaPdDMonitorate_StatusPdD();
				//			for (String nome : nomiPdD) {
				//				StatusBean.PddBean pddBean = new PddBean();
				//				pddBean.setNome(nome);
				//				pddBean.setUrl(pddMonitorProperties.getUrlCheckStatusPdD(nome));
				//				this.listaPdd.add(pddBean);
				//			}

				//			log.debug("Verranno monitorate ["+this.listaPdd.size()+"] PdD.");

				log.debug("Creazione Status Bean Completata.");

				this.initialized = true;
				
				// calcolo lo stato iniziale
				aggiornaStato();

			} catch (Exception e) {
				StatusBean.log.error("si e' verificato un errore durante la init: " + e.getMessage(), e);  
			}
			
		}
	}

	public StatusBean(){
		
		this.initialize();

	}

	public boolean isEnable() {
		return this.enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getIntervalloRefresh() {
		return this.intervalloRefresh;
	}


	/***
	 * 
	 * Resistuisce il valore della label dello stato
	 * 
	 * @return tooltip stato
	 */
	public String getTooltipIconaStato() {
		return this.tooltipIconaStato;

	}

	/***
	 * 
	 * Restituisce il nome della classe CSS da applicare alla label dello stato.
	 * 
	 * @return icona stato
	 */
	public String getIconaStato(){
		return this.iconaStato;
	}


	/**
	 * 
	 * Aggiorna lo stato manualmente
	 * 
	 * @return outcome navigazione verso la pagina dello stato
	 */
	public String aggiorna(){

		aggiornaStato();

		return null;//"statusPdD";
	}

	/**
	 * 
	 * @return outcome navigazione verso la pagina dello stato
	 */
	public String mostraStato(){
		return "statusPdD";
	}


	private void aggiornaStato(){
		
		// verifica che le sonde siano inizializzate
		// bug fix per tomcat dove non viene re-effettuato il restore dei bean (non avviene il login essendo la sessione già attiva)
		this.initialize();
		
		log.debug("Aggiornamento stato in corso...");
		try{
			// Reset Lista stati
			for (ISondaPdd sondaPdd: this.sondePdd) {
				sondaPdd.reset();
			}

			// Aggiorna la lista delle pdd
			updateStatoPdd();

			int tot_ok = 0;
			int tot_err = 0;
			int tot_warn = 0;

			// Check stato per aggiornare l'icona in cima alla pagina
			for (ISondaPdd pdd : this.sondePdd) {
				SondaStatus statoSonda = pdd.getStatoSondaPdd();
				log.debug("SondaPdd: ["+pdd.getName()+"], Stato: ["+statoSonda.toString()+"]"); 

				if(statoSonda.equals(SondaStatus.OK))
					tot_ok ++;
				if(statoSonda.equals(SondaStatus.ERROR))
					tot_err ++;
				if(statoSonda.equals(SondaStatus.WARNING))
					tot_warn ++;
			}

			// tutto ok
			if(tot_ok == this.sondePdd.size()){
				this.iconaStato = ICONA_STATO_OK;
				this.tooltipIconaStato = "Non risultano anomalie";
				
			} else {
				// almeno una in error
				if(tot_err > 0){
					this.iconaStato = ICONA_STATO_ERR;
					this.tooltipIconaStato = "Il sistema ha rilevato condizioni di errore";
				}else {
					// almeno una in warning
					if(tot_warn > 0){
						this.iconaStato = ICONA_STATO_WARN;
						this.tooltipIconaStato =  "Il sistema ha rilevato condizioni di warning";
					}				
				}
			}
			log.debug("Aggiornamento stato completato");
		}catch(Exception e){
			log.error("Si e' verificato un errore durante l'update dello stato delle SondePdD: "+e.getMessage(),e);
			this.iconaStato = ICONA_STATO_ERR;
			this.tooltipIconaStato = "Stato della PdD non disponibile.";
		}
	}


	public boolean isAggiornamentoAutomatico() {
		return this.aggiornamentoAutomatico;
	}

	/**
	 * 	indica che l'aggiornamento e' stato richiesto dalla chiamata ajax
	 * @param aggiornamentoAutomatico
	 */
	public void setAggiornamentoAutomatico(boolean aggiornamentoAutomatico) {
		this.aggiornamentoAutomatico = aggiornamentoAutomatico;
	}

	/***
	 * Metodo getter "finto" che serve alla chiamata ajax per scatenare l'aggiornamento dello stato.
	 * 
	 * @return label stato
	 */
	public String getLabelStato(){
		if(this.aggiornamentoAutomatico)
			aggiornaStato();

		this.aggiornamentoAutomatico = false;

		return "";
	}

	public List<ISondaPdd> getListaSondePdd() {
		if(this.sondePdd == null)
			this.sondePdd = new ArrayList<ISondaPdd>();
		
		return this.sondePdd;
	}
	public void setListaSondePdd(List<ISondaPdd> sondePdd) {
		this.sondePdd = sondePdd;
	}

	private void updateStatoPdd(){
		for (ISondaPdd sondaPdd : this.sondePdd) {
			try{ 
				sondaPdd.updateStato();
			}catch(Exception e){
				log.error("Si e' verificato un errore durante l'update dello stato della sonda ["+sondaPdd.getName()+"]: " + e.getMessage(),e);
			}
		}
	}
}
