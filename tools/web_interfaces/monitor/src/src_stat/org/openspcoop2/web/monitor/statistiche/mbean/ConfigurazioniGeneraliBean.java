/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.mbean.DynamicPdDBean;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioneGenerale;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioneGeneralePK;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioniGeneraliSearchForm;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiConfigurazioni;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiExporter;
import org.openspcoop2.web.monitor.statistiche.dao.IConfigurazioniGeneraliService;
import org.slf4j.Logger;

/**
 * ConfigurazioniGeneraliBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
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
	private String exportCsvErrorMessage = null;
	private List<SelectItem> exportDisponibili = null;
	private String tipoExport = CostantiExporter.TIPO_FORMATO_CONFIGURAZIONE_CSV;

	public ConfigurazioniGeneraliBean(){
		super();
	}

	public String submit() {
		return "configurazioniGenerali";
	}

	public String filtra() {
		return this.search.filtra();
	}

	public ConfigurazioneGeneralePK getSelectedId() {
		return this.selectedId;
	}

	public void setSelectedId(ConfigurazioneGeneralePK selectedId) {
		this.selectedId = selectedId;

		if(this.selectedId != null) {
			log.debug("Lettura del dettaglio per l'elemento: [{}]", this.selectedId);
			ConfigurazioneGenerale findById = this.service.findById(this.selectedId);
			this.setSelectedElement(findById); 			
		}
	}

	public boolean isAbilitaGestioneGruppiInConfigurazione() {
		return true;
	}
	
	@Override
	public void setSelectedElement(ConfigurazioneGenerale selectedElement) {
		super.setSelectedElement(selectedElement);
	}

	@Override
	public List<SelectItem> getSoggetti()  throws Exception{
		if(this.search==null){
			return new ArrayList<>();
		}
		
		// bug fix: devo usare sempre i soggetti operativi
		return _getSoggetti(true,false,null);
	}
	
	public List<org.openspcoop2.web.monitor.core.bean.SelectItem> soggettiErogatoreAutoComplete(Object val) throws Exception{
		List<org.openspcoop2.web.monitor.core.bean.SelectItem> listaSoggetti = new ArrayList<>();
		List<SelectItem> listaSoggettiTmp = new ArrayList<>();
		if(val==null || StringUtils.isEmpty((String)val)) {
			//donothing
		}else{
			if(this.search!=null){
				// bug fix: devo usare sempre i soggetti operativi
				listaSoggettiTmp = _getSoggetti(true,false,(String)val);
			}
		}
		
		listaSoggettiTmp.add(0, new SelectItem(CostantiConfigurazioni.NON_SELEZIONATO, CostantiConfigurazioni.NON_SELEZIONATO));
		
		for (SelectItem selectItem : listaSoggettiTmp) {
			String label = selectItem.getLabel();
			String value = (String) selectItem.getValue();
			
			org.openspcoop2.web.monitor.core.bean.SelectItem newItem = new org.openspcoop2.web.monitor.core.bean.SelectItem(value, label);
			listaSoggetti.add(newItem);
		}

		return listaSoggetti;
	}
	
	@Override
	public List<SelectItem> getTipiNomiSoggettiAssociati() throws Exception {
		return _getTipiNomiSoggettiAssociati(true);
	}

	@Override
	protected List<SelectItem> _getServizi(String input) throws Exception {
		if(this.search==null){
			return new ArrayList<>();
		}
		if(!this.serviziSelectItemsWidthCheck){
			this.servizi = new ArrayList<SelectItem>();
			
			String tipoSoggetto = this.search.getTipoSoggettoLocale();
			String nomeSoggetto = this.search.getSoggettoLocale();
			
			String tipoProtocollo = this.search.getProtocollo();

			PddRuolo ruoloReport = ((ConfigurazioniGeneraliSearchForm)this.search).getTipologiaTransazioni();

			String gruppo = this.search.getGruppo();
			
			IDAccordo idAccordo = null;
			String api = this.search.getApi();
			if((api!=null && !"".equals(api)) ) {
				idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(api);
			}
			
			boolean distinct = true;
			if(ruoloReport == null || ruoloReport.equals(PddRuolo.DELEGATA)) {
				this.servizi = this.dynamicUtils.getListaSelectItemsElencoConfigurazioneServiziFruizione(tipoProtocollo, gruppo, idAccordo, tipoSoggetto, nomeSoggetto,null,null,input, false, this.search.getPermessiUtenteOperatore(), distinct);
			}else {
				// bisogna filtrare per soggetti operativi
				this.servizi = this.dynamicUtils.getListaSelectItemsElencoConfigurazioneServiziErogazione(tipoProtocollo, gruppo, idAccordo, tipoSoggetto, nomeSoggetto,input, true, this.search.getPermessiUtenteOperatore(), distinct);
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

		return new ArrayList<>();
	}

	public List<ConfigurazioneGenerale> getListaConfigurazioniServizi(){
		try{
			return this.service.findAllInformazioniServizi();
		}catch(Exception e){
			log.error("Errore durante la lettura delle configurazioni servizi: "+e.getMessage(), e);
		}

		return new ArrayList<>();
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
				if(nomeSoggetto!=null && !Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(nomeSoggetto)) {
					IDSoggetto idSoggetto = new IDSoggetto(tipoSoggetto, nomeSoggetto); 
					String label = tipoProtocollo != null ? NamingUtils.getLabelSoggetto(tipoProtocollo,idSoggetto ) : NamingUtils.getLabelSoggetto(idSoggetto);
					this.labelInformazioniServizi = MessageFormat.format(CostantiConfigurazioni.LABEL_INFORMAZIONI_SOGGETTO, label);
				}
				else {
					this.labelInformazioniServizi = CostantiConfigurazioni.LABEL_SERVIZI;
				}
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
		this.tipoExport = CostantiExporter.TIPO_FORMATO_CONFIGURAZIONE_CSV;
	}

	public String exportSelected() {
		try {
			String formatoExport = this.getTipoExport();
			
			// recupero lista diagnostici
			List<String> idReport = new ArrayList<>();

			// se nn sono in select all allore prendo solo quelle selezionate
/**			if (!this.isSelectedAll()) {
//
//				// NOTA: Al massimo sono selezionate 25 report
//				// NOTA2: le configurazioni sono ordinate per nome
//				List<String> orderFix = new ArrayList<>();
//				Map<String, String> mapIds = new HashMap<>();
//
//				Iterator<ConfigurazioneGenerale> it = this.selectedIds.keySet().iterator();
//				while (it.hasNext()) {
//					ConfigurazioneGenerale bean = it.next();
//					if (this.selectedIds.get(bean).booleanValue()) {
//						orderFix.add(bean.getLabel());
//						mapIds.put(bean.getLabel(), bean.getId().toString());
//						it.remove();
//					}
//				}
//
//				Collections.sort(orderFix, new IgnoreCaseComp()); // per adeguarsi ai db
//				for (String nomeConfigurazione : orderFix) {
//					idReport.add(mapIds.get(nomeConfigurazione));	
//				}
//				
//			}*/
			
			// se nn sono in select all allore prendo solo quelle selezionate
			if (this.elencoID != null && this.elencoID.length() > 0) {
				String [] split = this.elencoID.split(",");
				
				// NOTA: Al massimo sono selezionate 25 report
				// NOTA2: i report esportate sono sempre ordinate per data
				List<String> orderFix = new ArrayList<>();
				
				// j_id170:tableReport_tbl:19:tableReport_column_ckb
				for (String idString : split) {
					String tmpId = idString.substring(0, idString.lastIndexOf(":"));
					tmpId = tmpId.substring(tmpId.lastIndexOf(":")+1);
					orderFix.add(tmpId); 
				}
				
				idReport.addAll(orderFix);
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
			sessione.setAttribute(CostantiExporter.PARAMETER_FORMATO_EXPORT_ORIGINALE, formatoExport);
			
			response.sendRedirect(context.getExternalContext()
					.getRequestContextPath()
					+ "/" + CostantiConfigurazioni.CONFIGURAZIONI_EXPORTER_SERVLET_NAME + "?" + CostantiConfigurazioni.PARAMETER_IS_ALL + "="
					+ this.isSelectedAll()
					+ "&" + CostantiConfigurazioni.PARAMETER_IDS + "="
					+ StringUtils.join(idReport, ",")
					+ "&" + CostantiConfigurazioni.PARAMETER_RUOLO + "="
					+ ((ConfigurazioniGeneraliSearchForm)this.search).get_value_tipologiaTransazioni()
					+ "&" + CostantiExporter.TIPO_FORMATO_CONFIGURAZIONE + "="
					+ formatoExport
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

	public boolean isShowFiltroSoggetti() {
		return Utility.isMultitenantAbilitato();
	}
	
	public void esportazioneSelectListener(ActionEvent ae){
		// donothing
	}
	
	public String visualizzaExportCsv(){
		return null;
	}

	public String getExportCsvErrorMessage() {
		return this.exportCsvErrorMessage;
	}

	public void setExportCsvErrorMessage(String exportCsvErrorMessage) {
		this.exportCsvErrorMessage = exportCsvErrorMessage;
	}

	public List<SelectItem> getExportDisponibili() {
		if(this.exportDisponibili == null) {
			this.exportDisponibili = new ArrayList<>();
			this.exportDisponibili.add(new SelectItem(CostantiExporter.TIPO_FORMATO_CONFIGURAZIONE_CSV, CostantiExporter.TIPO_FORMATO_CONFIGURAZIONE_CSV.toUpperCase()));
			this.exportDisponibili.add(new SelectItem(CostantiExporter.TIPO_FORMATO_CONFIGURAZIONE_XLS, CostantiExporter.TIPO_FORMATO_CONFIGURAZIONE_XLS.toUpperCase()));
		}

		return this.exportDisponibili;
	}

	public void setExportDisponibili(List<SelectItem> exportDisponibili) {
		this.exportDisponibili = exportDisponibili;
	}
	
	public String getTipoExport() {
		return this.tipoExport;
	}

	public void setTipoExport(String tipoExport) {
		this.tipoExport = tipoExport;
	}
}
