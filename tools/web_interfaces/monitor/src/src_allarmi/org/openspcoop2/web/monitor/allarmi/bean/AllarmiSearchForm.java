/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.web.monitor.core.bean.BaseSearchForm;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.slf4j.Logger;

/**
 * AllarmiSearchForm 
 *
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllarmiSearchForm extends BaseSearchForm implements Cloneable {
	
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	private String nomeAllarme;
	private String statoSelezionato;
	
	private static final String statoDefaultPaginaStatoAllarmi = "Non Disabilitato";
	private static final String statoDefaultPaginaConfAllarmi = "All";
	
	public static final String TIPOLOGIA_CONFIGURAZIONE = "configurazione";
	public static final String TIPOLOGIA_DELEGATA = PddRuolo.DELEGATA.toString(); 
	public static final String TIPOLOGIA_APPLICATIVA = PddRuolo.APPLICATIVA.toString(); 
	
	private String statoDefault; 
	
	private String tipologiaAllarme;
	
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
		
		this.statoDefault = statoDefaultPaginaStatoAllarmi;
		if(id!= null && id.equals("confAllarmiLnk"))
			this.statoDefault = statoDefaultPaginaConfAllarmi;
		
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
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	@Override
	protected String ripulisciValori(){
		this.initSearchListener(null);
		return null;
	}
	
	public void nomeAllarmeSelected(ActionEvent ae) {
		
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
	public List<SelectItem> getTipologieRicerca() throws Exception {
		List<SelectItem> listaTipologie = new ArrayList<SelectItem>();
		
		if(Utility.isAmministratore())
			listaTipologie.add(new SelectItem(TIPOLOGIA_CONFIGURAZIONE,"Configurazione"));
		listaTipologie.add(new SelectItem(TIPOLOGIA_APPLICATIVA,"Erogazione"));
		listaTipologie.add(new SelectItem(TIPOLOGIA_DELEGATA,"Fruizione"));
		
		return listaTipologie;
	}
	
	public void tipologiaAllarmeListener(ActionEvent ae){}
}
