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
package org.openspcoop2.web.monitor.statistiche.mbean;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.mbean.DynamicPdDBean;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioneGenerale;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioneGeneralePK;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioniGeneraliSearchForm;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiConfigurazioni;
import org.openspcoop2.web.monitor.statistiche.dao.IConfigurazioniGeneraliService;

public class ConfigurazioniGeneraliBean extends DynamicPdDBean<ConfigurazioneGenerale, ConfigurazioneGeneralePK,IConfigurazioniGeneraliService> { 


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 

	private static Logger log = LoggerManager.getPddMonitorCoreLogger();

	private String labelInformazioniGenerali = CostantiConfigurazioni.LABEL_REGISTRO;
	private String labelInformazioniServizi = CostantiConfigurazioni.LABEL_SERVIZI;
	private boolean includiInformazioniDettaglio = false;
	private ConfigurazioneGeneralePK selectedId = null;

	public ConfigurazioniGeneraliBean(){
		super();
		try {
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public String submit() {
		return "configurazioniGenerali";
	}

	public String filtra() {
		return this.search.filtra();
		
//		return "configurazioniGenerali";
	}

	public ConfigurazioneGeneralePK getSelectedId() {
		return this.selectedId;
	}

	public void setSelectedId(ConfigurazioneGeneralePK selectedId) {
		this.selectedId = selectedId;

		if(this.selectedId != null) {
			log.debug("Lettura del dettaglio per l'elemento: [" + this.selectedId + "]");
			ConfigurazioneGenerale findById = this.service.findById(this.selectedId);
			this.setSelectedElement(findById); 			
		}
	}

	@Override
	public void setSelectedElement(ConfigurazioneGenerale selectedElement) {
		super.setSelectedElement(selectedElement);
	}

	public List<Soggetto> soggettiErogatoreAutoComplete(Object val){
		List<Soggetto> list = null;
		Soggetto s = new Soggetto();
		s.setNomeSoggetto(CostantiConfigurazioni.NON_SELEZIONATO);

		if(val==null || StringUtils.isEmpty((String)val))
			list = new ArrayList<Soggetto>();
		else{
			String tipoProtocollo = this.search.getProtocollo(); 
			list = this.dynamicUtils.getSoggettiErogatoreAutoComplete(tipoProtocollo, null, (String) val, true);
		}

		list.add(0,s);
		return list;

	}

	@Override
	public List<SelectItem> getTipiNomiSoggettiAssociati() throws Exception {
		return _getTipiNomiSoggettiAssociati(true);
	}

	@Override
	protected List<SelectItem> _getServizi( boolean showAccordo,boolean showTipoServizio) {
		if(this.search==null){
			return new ArrayList<SelectItem>();
		}
		if(!this.serviziSelectItemsWidthCheck){
			this.servizi = new ArrayList<SelectItem>();
			Soggetto erogatore = _getSoggettoErogatore( );

			String tipoSoggetto =  null;
			String nomeSoggetto = null;

			if(erogatore != null){
				tipoSoggetto = erogatore.getTipoSoggetto();
				nomeSoggetto = erogatore.getNomeSoggetto();
			}

			String uriAccordo =null;
			String tipoProtocollo = this.search.getProtocollo();

			PddRuolo ruoloReport = ((ConfigurazioniGeneraliSearchForm)this.search).getTipologiaTransazioni();

			if(ruoloReport == null || ruoloReport.equals(PddRuolo.DELEGATA)) {
				this.servizi = this.dynamicUtils.getListaSelectItemsElencoServiziFromAccordoAndSoggettoErogatore(tipoProtocollo,uriAccordo, null, null,this.showErogatoreOnServizioLabel,false);
			}else {
				// bisogna filtrare per soggetti operativi
				this.servizi = this.dynamicUtils.getListaSelectItemsElencoServiziFromAccordoAndSoggettoErogatore(tipoProtocollo,uriAccordo, tipoSoggetto, nomeSoggetto,this.showErogatoreOnServizioLabel,true);
			}
			Integer lunghezzaSelectList = this.dynamicUtils.getLunghezzaSelectList(this.servizi);
			this.serviziSelectItemsWidth = Math.max(this.serviziSelectItemsWidth,  lunghezzaSelectList);
		}
		return this.servizi;
	}

	public List<ConfigurazioneGenerale> getListaConfigurazioniGenerali(){
		try{
			return this.service.findAllInformazioniGenerali();
		}catch(Exception e){
			log.error("Errore durante la lettura delle configurazioni generali: "+e.getMessage(), e);
		}

		return new ArrayList<ConfigurazioneGenerale>();
	}

	public List<ConfigurazioneGenerale> getListaConfigurazioniServizi(){
		try{
			return this.service.findAllInformazioniServizi();
		}catch(Exception e){
			log.error("Errore durante la lettura delle configurazioni servizi: "+e.getMessage(), e);
		}

		return new ArrayList<ConfigurazioneGenerale>();
	}

	public String getLabelInformazioniGenerali() {
		return this.labelInformazioniGenerali;
	}

	public void setLabelInformazioniGenerali(String labelInformazioniGenerali) {
		this.labelInformazioniGenerali = labelInformazioniGenerali;
	}

	public String getLabelInformazioniServizi() throws Exception {
		if(this.getSearch() != null){
			String tipoProtocollo = this.getSearch().getProtocollo();
			if (StringUtils.isNotBlank(this.getSearch().getNomeServizio())){
				IDServizio idServizio = Utility.parseServizioSoggetto(this.getSearch().getNomeServizio());
				String label = tipoProtocollo != null ? NamingUtils.getLabelAccordoServizioParteSpecifica(tipoProtocollo,idServizio) : NamingUtils.getLabelAccordoServizioParteSpecifica(idServizio);
				this.labelInformazioniServizi = MessageFormat.format(CostantiConfigurazioni.LABEL_INFORMAZIONI_SERVIZIO,label);
			}else if(this.getSearch().getTipoNomeSoggettoLocale()!=null && !StringUtils.isEmpty(this.getSearch().getTipoNomeSoggettoLocale())	&& !"--".equals(this.getSearch().getTipoNomeSoggettoLocale())){
				String tipoSoggetto = this.getSearch().getTipoSoggettoLocale();
				String nomeSoggetto = this.getSearch().getSoggettoLocale();
				IDSoggetto idSoggetto = new IDSoggetto(tipoSoggetto, nomeSoggetto); 
				String label = tipoProtocollo != null ? NamingUtils.getLabelSoggetto(tipoProtocollo,idSoggetto ) : NamingUtils.getLabelSoggetto(idSoggetto);
				this.labelInformazioniServizi = MessageFormat.format(CostantiConfigurazioni.LABEL_INFORMAZIONI_SOGGETTO, label);
			}else {
				// non ho selezionato ne servizio ne soggetto
				this.labelInformazioniServizi = CostantiConfigurazioni.LABEL_SERVIZI;
			}
		}

		return this.labelInformazioniServizi;
	}

	public void setLabelInformazioniServizi(String labelInformazioniServizi) {
		this.labelInformazioniServizi = labelInformazioniServizi;
	}

	public boolean isIncludiInformazioniDettaglio() {
		return this.includiInformazioniDettaglio;
	}

	public void setIncludiInformazioniDettaglio(boolean includiInformazioniDettaglio) {
		this.includiInformazioniDettaglio = includiInformazioniDettaglio;
	}

	@Override
	public void initExportListener(ActionEvent ae) {
		super.initExportListener(ae);
		this.includiInformazioniDettaglio = false;
	}

	public String exportSelected() {
		try {
			// recupero lista diagnostici
			List<String> idReport = new ArrayList<String>();

			// se nn sono in select all allore prendo solo quelle selezionate
			if (!this.isSelectedAll()) {

				// NOTA: Al massimo sono selezionate 25 report
				// NOTA2: le configurazioni sono ordinate per nome
				List<String> orderFix = new ArrayList<String>();
				Map<String, String> mapIds = new HashMap<String, String>();

				Iterator<ConfigurazioneGenerale> it = this.selectedIds.keySet().iterator();
				while (it.hasNext()) {
					ConfigurazioneGenerale bean = it.next();
					if (this.selectedIds.get(bean).booleanValue()) {
						orderFix.add(bean.getLabel());
						mapIds.put(bean.getLabel(), bean.getId().toString());
						it.remove();
					}
				}

				Collections.sort(orderFix);
				for (String nomeConfigurazione : orderFix) {
					idReport.add(mapIds.get(nomeConfigurazione));	
				}
				
			}

			// We must get first our context
			FacesContext context = FacesContext.getCurrentInstance();

			// Then we have to get the Response where to write our file
			HttpServletResponse response = (HttpServletResponse) context
					.getExternalContext().getResponse();
			
			// Salvo i parametri di export in sessione
			HttpSession sessione = (HttpSession) context.getExternalContext().getSession(false);

			sessione.setAttribute(CostantiConfigurazioni.PARAMETER_IDS_ORIGINALI, StringUtils.join(idReport, ","));
			sessione.setAttribute(CostantiConfigurazioni.PARAMETER_IS_ALL_ORIGINALE, this.isSelectedAll());
			sessione.setAttribute(CostantiConfigurazioni.PARAMETER_RUOLO_ORIGINALE, ((ConfigurazioniGeneraliSearchForm)this.search).get_value_tipologiaTransazioni());

			response.sendRedirect(context.getExternalContext()
					.getRequestContextPath()
					+ "/" + CostantiConfigurazioni.CONFIGURAZIONI_EXPORTER_SERVLET_NAME + "?" + CostantiConfigurazioni.PARAMETER_IS_ALL + "="
					+ this.isSelectedAll()
					+ "&" + CostantiConfigurazioni.PARAMETER_IDS + "="
					+ StringUtils.join(idReport, ",")
					+ "&" + CostantiConfigurazioni.PARAMETER_RUOLO + "="
					+ ((ConfigurazioniGeneraliSearchForm)this.search).get_value_tipologiaTransazioni()
					);

			context.responseComplete();

			// End of the method
		} catch (Exception e) {
			FacesContext.getCurrentInstance().responseComplete();
			ConfigurazioniGeneraliBean.log.error(e.getMessage(), e);
			MessageUtils
			.addErrorMsg("Si e' verificato un errore durante l'esportazione delle configurazioni selezionate.");
		}
		return null;
	}
}
