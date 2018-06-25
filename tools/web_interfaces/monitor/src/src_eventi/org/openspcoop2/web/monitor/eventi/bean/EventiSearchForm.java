/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.web.monitor.eventi.bean;


import javax.faces.event.ActionEvent;

import org.slf4j.Logger;

import org.openspcoop2.web.monitor.core.bean.AbstractDateSearchForm;
import org.openspcoop2.web.monitor.core.constants.CaseSensitiveMatch;
import org.openspcoop2.web.monitor.core.constants.TipoMatch;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;


/****
 * 
 * Classe che raccoglie i parametri di ricerca per gli eventi
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class EventiSearchForm extends AbstractDateSearchForm {
	
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	public static final String NON_SELEZIONATO = "--"; 
	
	private String severita;
	private String tipo;
	private String codice;
	
	private TipoMatch matchingType = TipoMatch.EQUALS;
	private CaseSensitiveMatch caseSensitiveType = CaseSensitiveMatch.SENSITIVE;
	
	private String idCluster;
	private String idConfigurazione;

	private String severitaDefault = EventiSearchForm.NON_SELEZIONATO;

	@Override
	protected String ripulisciValori(){
		this.initSearchListener(null);
		return null;
	}
	
	public EventiSearchForm(){
		super();
		try {
			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(log);
			this.setUseCount(govwayMonitorProperties.isAttivoUtilizzaCountListaEventi()); 
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		ripulisciValori();
	}


//	public void setImpostazioniSeverita(String defaultValue){
//		this.severitaDefault = defaultValue;
//	}

	@Override
	public void initSearchListener(ActionEvent ae) {
		this.setPeriodo(this.periodoDefault != null ? this.periodoDefault
				: "Ultimo mese");
		_setPeriodo();
		
		this.aggiornaNuovaDataRicerca();
		
		this.severita= this.severitaDefault;
		this.tipo = null;
		this.codice = null;
		
		this.matchingType = TipoMatch.EQUALS;
		this.caseSensitiveType = CaseSensitiveMatch.SENSITIVE;
		
		this.idCluster = null;
		this.idConfigurazione = null;
		
		this.severitaDefault = EventiSearchForm.NON_SELEZIONATO;
		this.executeQuery = false;
	}

	@Override
	protected String eseguiAggiorna() {
		return this._filtra(true);
	}
	
	@Override
	protected String eseguiFiltra() {
		this.setAggiornamentoDatiAbilitato(true); // abilito aggiornamento
		return this._filtra(false);
	}
	
	private String _filtra(boolean mantieniDataRicerca) {
	
		// Tipo di periodo selezionato 'Personalizzato'
		if(this.getPeriodo().equals("Personalizzato")){
			if(this.getDataInizio() == null){
				MessageUtils.addErrorMsg("Selezionare Data Inizio");
				return null;
			}

			if(this.getDataFine() == null){
				MessageUtils.addErrorMsg("Selezionare Data Fine");
				return null;
			}
		}
		
		// Date
		if(mantieniDataRicerca == false){
			//System.out.println("Reset dataRicerca");
			this.aggiornaNuovaDataRicerca();
			this._setPeriodo();
		}
		else{
			// La funzionalità di non vedere nuovo eventi e' garantita dalla data ricerca, nel caso si utilizzi il pulsante 'aggiorna'.
			// In questo caso sicuramente non si vedranno 'nuovi eventi'.
			// In più il calcolo dell'intervallo delle due date data inizio e fine viene ricalcolato solamente se viene cambiato l'intervallo
			// dove in tal caso viene scaturito il cambio di periodo tramite l'invocazione _setPeriodo
			this.congelaDataRicerca();
		}
		
		// gia' eseguito nella super del filtro
//		this.executeQuery = true;
//		this._setPeriodo();
		return null;
	}
	
	public String getSeverita() {
		return this.severita;
	}
	public void setSeverita(String severita) {
		this.severita = severita;
	}

	public String getTipo() {
		return this.tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getCodice() {
		return this.codice;
	}
	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getSeveritaDefault() {
		return this.severitaDefault;
	}
	public void setSeveritaDefault(String severitaDefault) {
		this.severitaDefault = severitaDefault;
	}
	
	public String getIdCluster() {
		if("--".equals(this.idCluster)){
			return null;
		}
		return this.idCluster;
	}
	public void setIdCluster(String idCluster) {
		if("--".equals(idCluster)){
			this.idCluster = null;	
		}
		else{
			this.idCluster = idCluster;		
		}
	}

	public String getIdConfigurazione() {
		return this.idConfigurazione;
	}
	public void setIdConfigurazione(String idConfigurazione) {
		this.idConfigurazione = idConfigurazione;
	}
	
	public String getMatchingType() {
		if(this.matchingType!=null)
			return this.matchingType.name();
		return null;
	}

	public void setMatchingType(
			String MatchingType) {
		if(MatchingType!=null){
			this.matchingType = TipoMatch.valueOf(MatchingType);
		}
	}
	
	public String getCaseSensitiveType() {
		if(this.caseSensitiveType!=null)
			return this.caseSensitiveType.name();
		return null;
	}

	public void setCaseSensitiveType(
			String caseSensitiveType) {
		if(caseSensitiveType!=null){
			this.caseSensitiveType = CaseSensitiveMatch.valueOf(caseSensitiveType);
		}
	}
	
	@Override
	public String getPrintPeriodo(){
		return super.getDefaultPrintPeriodoBehaviour();
	}

}
