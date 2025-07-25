/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.web.monitor.allarmi.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.web.monitor.allarmi.constants.AllarmiCostanti;
import org.openspcoop2.web.monitor.core.bean.BaseSearchForm;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.ricerche.ModuloRicerca;
import org.openspcoop2.web.monitor.core.utils.MessageManager;
import org.slf4j.Logger;

/**
 * AllarmiSearchForm 
 *
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllarmiSearchForm extends BaseSearchForm 
	//implements Cloneable 
	{
	
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	private String nomeAllarme;
	private String statoSelezionato;
	
	private static final String STATO_DEFAULT_PAGINA_STATO_ALLARMI = "Non Disabilitato";
	private static final String STATO_DEFAULT_PAGINA_CONF_ALLARMI = "All";
	
	public static final String TIPOLOGIA_CONFIGURAZIONE = CostantiConfigurazione.ALLARMI_TIPOLOGIA_CONFIGURAZIONE;
	public static final String TIPOLOGIA_DELEGATA = CostantiConfigurazione.ALLARMI_TIPOLOGIA_DELEGATA;
	public static final String TIPOLOGIA_APPLICATIVA = CostantiConfigurazione.ALLARMI_TIPOLOGIA_APPLICATIVA;
	
	private String statoDefault; 
	
	private String tipologiaAllarme;
	
	private static List<String> elencoFieldsRicercaDaIgnorare = new ArrayList<>();
	
	static {
		// aggiungo tutti i fields delle super classi
		elencoFieldsRicercaDaIgnorare.addAll(Arrays.asList(Costanti.SEARCH_FORM_FIELDS_DA_NON_SALVARE));
		// aggiungo i field di questa classe
		elencoFieldsRicercaDaIgnorare.addAll(Arrays.asList(AllarmiCostanti.SEARCH_FORM_FIELDS_DA_NON_SALVARE));
	}
	
	public AllarmiSearchForm() {
		super();
		try {
			PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(log);
			this.setUseCount(pddMonitorProperties.isAttivoUtilizzaCountListaAllarmi()); 
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	@Override
	protected String eseguiFiltra() {
		this.setAggiornamentoDatiAbilitato(true); // abilito aggiornamento
		return super.eseguiFiltra();
	}
	
	@Override
	public void initSearchListener(ActionEvent ae) {
		super.initSearchListener(ae);
		
		this.nomeAllarme = null;
		this.statoSelezionato = this.statoDefault;
		this.tipologiaAllarme = Utility.isAmministratore() ? TIPOLOGIA_CONFIGURAZIONE : TIPOLOGIA_APPLICATIVA;
		this.executeQuery = false;
	}
	
	public void menuClickSearchListener(ActionEvent ae) {
		UIComponent component = ae.getComponent();
		String id = component.getId();
		
		this.statoDefault = STATO_DEFAULT_PAGINA_STATO_ALLARMI;
		if(id!= null && id.equals("confAllarmiLnk"))
			this.statoDefault = STATO_DEFAULT_PAGINA_CONF_ALLARMI;
		
		this.initSearchListener(ae);
	}
	
	public String getNomeAllarme() {
		return this.nomeAllarme;
	}

	public String getStatoSelezionato() {
		return this.statoSelezionato;
	}

	public void setNomeAllarme(String nomeAllarme) {
		this.nomeAllarme = nomeAllarme;
		
		if(StringUtils.isEmpty(nomeAllarme) || "--".equals(nomeAllarme))
			this.nomeAllarme = null;
	}

	public void setStatoSelezionato(String statoSelezionato) {
		this.statoSelezionato = statoSelezionato;
		if(StringUtils.isEmpty(statoSelezionato) || "--".equals(statoSelezionato))
			this.statoSelezionato = null;
	}
		
	@Override
	protected String ripulisciValori(){
		this.initSearchListener(null);
		return null;
	}
	
	public void nomeAllarmeSelected(ActionEvent ae) {
		// lasciato per retrocompatibilita'
	}
	
	@Override
	protected String eseguiAggiorna() {
		return null;
	}

	public String getTipologiaAllarme() {
		return this.tipologiaAllarme;
	}

	public void setTipologiaAllarme(String tipologiaAllarme) {
		this.tipologiaAllarme = tipologiaAllarme;
	}
	
	@Override
	public List<SelectItem> getTipologieRicerca() {
		List<SelectItem> listaTipologie = new ArrayList<>();
		
		if(Utility.isAmministratore())
			listaTipologie.add(new SelectItem(TIPOLOGIA_CONFIGURAZIONE, MessageManager.getInstance().getMessage(AllarmiCostanti.SEARCH_TIPOLOGIA_CONFIGURAZIONE_LABEL_KEY)));
		listaTipologie.add(new SelectItem(TIPOLOGIA_APPLICATIVA, MessageManager.getInstance().getMessage(AllarmiCostanti.SEARCH_TIPOLOGIA_EROGAZIONE_LABEL_KEY)));
		listaTipologie.add(new SelectItem(TIPOLOGIA_DELEGATA, MessageManager.getInstance().getMessage(AllarmiCostanti.SEARCH_TIPOLOGIA_FRUIZIONE_LABEL_KEY)));
		
		return listaTipologie;
	}
	
	public void tipologiaAllarmeListener(ActionEvent ae){
		// lasciato per retrocompatibilita'
	}
	
	@Override
	public ModuloRicerca getModulo() {
		return ModuloRicerca.ALLARMI;
	}
	
	@Override
	public String getModalitaRicerca() {
		return Costanti.NON_SELEZIONATO;
	}
	
	@Override
	public boolean isVisualizzaComandoSalvaRicerca() {
		return true;
	}
	
	@Override
	public List<String> getElencoFieldRicercaDaIgnorare() {
		return elencoFieldsRicercaDaIgnorare;
	}
	
	@Override
	public String getProtocolloRicerca() {
		String tipologiaAllarme2 = this.getTipologiaAllarme();
		
		// allarme non globale 
		if(tipologiaAllarme2 != null && !tipologiaAllarme2.equals(TIPOLOGIA_CONFIGURAZIONE)) {
			return Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(this.getProtocollo()) ? null : this.getProtocollo();	
		}
		
		return TIPOLOGIA_CONFIGURAZIONE;
	}
	
	@Override
	public String getSoggettoRicerca() {
		String tipologiaAllarme2 = this.getTipologiaAllarme();
		
		// allarme non globale 
		if(tipologiaAllarme2 != null && !tipologiaAllarme2.equals(TIPOLOGIA_CONFIGURAZIONE)) {
			return Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(this.getTipoNomeSoggettoLocale()) ? null : this.getTipoNomeSoggettoLocale();	
		}
		
		return null;
	}
	
	@Override
	public void ripulisciRicercaUtente() {
		String currentTipologiaAllarme = this.getTipologiaAllarme();
		
		// allarme non globale 
		if(currentTipologiaAllarme != null && !currentTipologiaAllarme.equals(TIPOLOGIA_CONFIGURAZIONE)) {
			String currentProtocollo = this.getProtocollo();
			String currentTipoNomeSoggettoLocale = this.getTipoNomeSoggettoLocale();
			
			this.ripulisci();
			
			this.setProtocollo(currentProtocollo);
			this.setTipoNomeSoggettoLocale(currentTipoNomeSoggettoLocale);
		} else {
			this.ripulisci();
		}
	}
}
