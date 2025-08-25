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
package org.openspcoop2.web.monitor.statistiche.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.web.monitor.core.bean.BaseSearchForm;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.ricerche.ModuloRicerca;
import org.slf4j.Logger;

/**
 * ConfigurazioniGeneraliSearchForm
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ConfigurazioniGeneraliSearchForm extends BaseSearchForm implements Cloneable{

	private static Logger log = LoggerManager.getPddMonitorCoreLogger();
	private PddRuolo tipologiaTransazioni = null;
	
	private static List<String> elencoFieldsRicercaDaIgnorare = new ArrayList<>();	
	
	static {
		// aggiungo tutti i fields delle super classi
		elencoFieldsRicercaDaIgnorare.addAll(Arrays.asList(Costanti.SEARCH_FORM_FIELDS_DA_NON_SALVARE));
	}
	
	public ConfigurazioniGeneraliSearchForm(){
		super();
		try {
			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(log);
			this.setUseCount(govwayMonitorProperties.isAttivoUtilizzaCountStatisticheListaConfigurazioni()); 
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public void initSearchListener(ActionEvent ae) {
		super.initSearchListener(ae);
		this.setTipologiaRicerca("ingresso"); 
		this.tipologiaTransazioni = PddRuolo.APPLICATIVA;
		this.tipologiaTransazioniListener(ae);
		this.executeQuery = false;
	}

	@Override
	protected String ripulisciValori(){
		this.initSearchListener(null);
		return null;
	}

	public void tipologiaTransazioniListener(ActionEvent ae){}
	
	public PddRuolo getTipologiaTransazioni() {
		return this.tipologiaTransazioni;
	}
	public void setTipologiaTransazioni(PddRuolo tipologiaTransazioni) {
		this.tipologiaTransazioni = tipologiaTransazioni;
	}

	public void set_value_tipologiaTransazioni(String value) {
		if (StringUtils.isEmpty(value) || "*".equals(value))
			this.tipologiaTransazioni = null;
		else 
			this.tipologiaTransazioni = (PddRuolo) PddRuolo.toEnumConstantFromString(value);
	}

	public String get_value_tipologiaTransazioni() {
		if(this.tipologiaTransazioni == null){
			return "*";
		}else{
			return this.tipologiaTransazioni.toString();
		}
	}
	
	@Override
	protected String eseguiAggiorna() {
		return null;
	}
	
	
	@Override
	public List<SelectItem> getTipologieRicerca() {
		List<SelectItem> listaTipologie = new ArrayList<>();
		
		listaTipologie.add(new SelectItem(PddRuolo.APPLICATIVA.toString(),"Erogazione"));
		listaTipologie.add(new SelectItem(PddRuolo.DELEGATA.toString(),"Fruizione"));
		
		return listaTipologie;
	}
	
	@Override
	public ModuloRicerca getModulo() {
		return ModuloRicerca.CONFIGURAZIONI;
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
}
