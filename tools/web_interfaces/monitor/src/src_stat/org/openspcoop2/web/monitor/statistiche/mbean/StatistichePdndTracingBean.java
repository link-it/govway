package org.openspcoop2.web.monitor.statistiche.mbean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.statistiche.constants.PossibiliStatiPdnd;
import org.openspcoop2.core.statistiche.constants.PossibiliStatiRichieste;
import org.openspcoop2.utils.CopyStream;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.web.monitor.core.dao.IService;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.mbean.DynamicPdDBean;
import org.openspcoop2.web.monitor.core.utils.MessageManager;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import org.openspcoop2.web.monitor.statistiche.bean.GruppoRicercaStatistichePdnd;
import org.openspcoop2.web.monitor.statistiche.bean.RicercaStatistichePdnd;
import org.openspcoop2.web.monitor.statistiche.bean.StatistichePdndTracingSearchForm;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiConfigurazioni;
import org.openspcoop2.web.monitor.statistiche.constants.ModalitaRicercaStatistichePdnd;
import org.openspcoop2.web.monitor.statistiche.constants.StatisticheCostanti;
import org.openspcoop2.web.monitor.statistiche.dao.StatistichePdndTracingService;
import org.openspcoop2.web.monitor.statistiche.servlet.StatistichePdndTracingExporter;
import org.slf4j.Logger;

/**
 * StatistichePdndTracingBean
 * 
 * @author Pintori Giuliano (giuliano.pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class StatistichePdndTracingBean extends
DynamicPdDBean<org.openspcoop2.web.monitor.statistiche.bean.StatistichePdndTracingBean, Long, IService<org.openspcoop2.web.monitor.statistiche.bean.StatistichePdndTracingBean, Long>>{

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerManager.getPddMonitorCoreLogger();

	private List<SelectItem> stati = null;
	private List<SelectItem> statiPdnd = null;
	private Integer minTentativiPubblicazione = 0;
	private org.openspcoop2.web.monitor.statistiche.bean.StatistichePdndTracingBean statisticaPdndTracing;

	private String selectedTab = null;
	
	private List<GruppoRicercaStatistichePdnd> tipiRicerca;
	private String tipoRicerca;
	private boolean updateTipoRicerca = false;

	public boolean isShowFiltroSoggetti() {
		return this.search.isShowFiltroSoggettoLocale();
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

	private List<String> getIdSelected() {
		List<String> idReport = new ArrayList<>();

		// se nn sono in select all allore prendo solo quelle selezionate
		if (!this.isSelectedAll()) {

			Iterator<org.openspcoop2.web.monitor.statistiche.bean.StatistichePdndTracingBean> it = this.selectedIds.keySet().iterator();
			while (it.hasNext()) {
				org.openspcoop2.web.monitor.statistiche.bean.StatistichePdndTracingBean bean = it.next();
				if (this.selectedIds.get(bean).booleanValue()) {
					idReport.add(bean.getId().toString());
					it.remove();
				}
			}
		}
		return idReport;
	}
	
	public String exportSelected() {
		try {
			// recupero lista diagnostici
			List<String> idReport = getIdSelected();

			// We must get first our context
			FacesContext context = FacesContext.getCurrentInstance();

			// Then we have to get the Response where to write our file
			HttpServletResponse response = (HttpServletResponse) context
					.getExternalContext().getResponse();

			// Salvo i parametri di export in sessione
			HttpSession sessione = (HttpSession) context.getExternalContext().getSession(false);

			sessione.setAttribute(StatisticheCostanti.PARAMETER_IDS_ORIGINALI, StringUtils.join(idReport, ","));
			sessione.setAttribute(StatisticheCostanti.PARAMETER_IS_ALL_ORIGINALE, this.isSelectedAll());

			response.sendRedirect(context.getExternalContext()
					.getRequestContextPath()
					+ "/" + StatisticheCostanti.STATISTICHE_PDND_EXPORTER_SERVLET_NAME + "?" + StatisticheCostanti.PARAMETER_IS_ALL + "="
					+ this.isSelectedAll()
					+ "&" + CostantiConfigurazioni.PARAMETER_IDS + "="
					+ StringUtils.join(idReport, ",")
					);

			context.responseComplete();

			// End of the method
		} catch (Exception e) {
			FacesContext.getCurrentInstance().responseComplete();
			StatistichePdndTracingBean.log.error(e.getMessage(), e);
			MessageUtils.addErrorMsg("Si e' verificato un errore durante l'esportazione dei Tracing PDND selezionati.");
		}
		return null;
	}

	public String downloadCsv(){
		StatistichePdndTracingBean.log.debug("downloading csv: {}", this.statisticaPdndTracing.getId());
		//recupero informazioni sul file


		// We must get first our context
		FacesContext context = FacesContext.getCurrentInstance();

		// Then we have to get the Response where to write our file
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

		String contentType = "text/csv";
		// Now set the content type for our response, be sure to use the best
		// suitable content type depending on your file
		// the content type presented here is ok for, lets say, text files and
		// others (like CSVs, PDFs)
		response.setContentType(contentType);

		String fileName = StatistichePdndTracingExporter.getCsvFileName(this.statisticaPdndTracing);

		try (ByteArrayInputStream bais = new ByteArrayInputStream(this.statisticaPdndTracing.getCsv())){
			// Setto Proprietà Export File
			HttpUtilities.setOutputFile(response, true, fileName, contentType);

			// Streams we will use to read, write the file bytes to our response
			OutputStream os = null;

			os = response.getOutputStream();

			CopyStream.copy(bais, os);

			// Clean resources
			os.flush();
			os.close();

			FacesContext.getCurrentInstance().responseComplete();

			// End of the method
		}catch (IOException | UtilsException e) {
			StatistichePdndTracingBean.log.error(e.getMessage(), e);
			MessageUtils.addErrorMsg("Si e' verificato un errore durante il download del csv.");
		}
		return null;
	}

	public List<SelectItem> getStati(){
		if(this.stati!= null)
			return this.stati;

		this.stati = new ArrayList<>();

		this.stati.add(new SelectItem(StatisticheCostanti.NON_SELEZIONATO));
		this.stati.add(new SelectItem(PossibiliStatiRichieste.PUBLISHED.getValue(), MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_STATO_PUBLISHED_LABEL_KEY)));
		this.stati.add(new SelectItem(PossibiliStatiRichieste.FAILED.getValue(), MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_STATO_FAILED_LABEL_KEY)));
		this.stati.add(new SelectItem(StatisticheCostanti.STATS_PDND_TRACING_STATO_IN_ATTESA_VALUE, MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_STATO_IN_ATTESA_LABEL_KEY)));

		return this.stati;
	}

	public List<SelectItem> getStatiPdnd() {
		if(this.statiPdnd!= null)
			return this.statiPdnd;

		this.statiPdnd = new ArrayList<>();

		this.statiPdnd.add(new SelectItem(StatisticheCostanti.NON_SELEZIONATO));
		this.statiPdnd.add(new SelectItem(PossibiliStatiPdnd.OK.getValue(), MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_STATO_PDND_OK_LABEL_KEY)));
		this.statiPdnd.add(new SelectItem(PossibiliStatiPdnd.PENDING.getValue(), MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_STATO_PDND_PENDING_LABEL_KEY)));
		//this.statiPdnd.add(new SelectItem(PossibiliStatiPdnd.WAITING.getValue(), MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_STATO_PDND_WAITING_LABEL_KEY)))
		this.statiPdnd.add(new SelectItem(PossibiliStatiPdnd.ERROR.getValue(), MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_STATO_PDND_ERROR_LABEL_KEY)));

		return this.statiPdnd;
	}

	public Integer getMinTentativiPubblicazione() {
		return this.minTentativiPubblicazione;
	}

	public void setMinTentativiPubblicazione(Integer minTentativiPubblicazione) {
		this.minTentativiPubblicazione = minTentativiPubblicazione;
	}

	public org.openspcoop2.web.monitor.statistiche.bean.StatistichePdndTracingBean getStatisticaPdndTracing() {
		return this.statisticaPdndTracing;
	}

	public void setStatisticaPdndTracing(org.openspcoop2.web.monitor.statistiche.bean.StatistichePdndTracingBean statisticaPdndTracing) {
		this.statisticaPdndTracing = statisticaPdndTracing;
	}

	public String getSelectedTab() {
		return this.selectedTab;
	}

	public void setSelectedTab(String selectedTab){
		this.selectedTab = selectedTab;
	}
	
	public List<GruppoRicercaStatistichePdnd> getTipiRicerca() {
		if (this.tipiRicerca != null) {
			return this.tipiRicerca;
		}
		
		this.tipiRicerca = new ArrayList<>();
		
		GruppoRicercaStatistichePdnd gruppoRicerca = new GruppoRicercaStatistichePdnd();
		gruppoRicerca.setLabel(MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_SEARCH_TIPO_RICERCA_TEMPORALE_LABEL_KEY));
		
		List<RicercaStatistichePdnd> listaGruppoTemporale = new ArrayList<>();
		listaGruppoTemporale.add(new RicercaStatistichePdnd(ModalitaRicercaStatistichePdnd.ANDAMENTO_TEMPORALE.getValue(), 
				MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_BASE_LABEL_KEY), 
				ModalitaRicercaStatistichePdnd.ANDAMENTO_TEMPORALE,
				MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_BASE_ICON_KEY)));
				
		listaGruppoTemporale.add(new RicercaStatistichePdnd(ModalitaRicercaStatistichePdnd.TRACING_ID.getValue(), 
				MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_SEARCH_TIPO_RICERCA_ID_RICERCA_TRACING_ID_LABEL_KEY), 
				ModalitaRicercaStatistichePdnd.TRACING_ID,
				MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_SEARCH_TIPO_RICERCA_ID_RICERCA_TRACING_ID_ICON_KEY)));
		
		gruppoRicerca.setListaRicerche(listaGruppoTemporale);
		this.tipiRicerca.add(gruppoRicerca);
		
		return this.tipiRicerca;
	}
	
	public String getTipoRicerca() {
		return this.tipoRicerca;
	}

	public void setTipoRicerca(String tipoRicerca) {
		this.tipoRicerca = tipoRicerca;
		
		if(this.updateTipoRicerca) {
			this.search.initSearchListener(null);
			((StatistichePdndTracingSearchForm)this.search).setModalitaRicerca(this.tipoRicerca);
			this.updateTipoRicerca = false;
		}
	}

	public boolean isUpdateTipoRicerca() {
		return this.updateTipoRicerca;
	}

	public void setUpdateTipoRicerca(boolean updateTipoRicerca) {
		this.updateTipoRicerca = updateTipoRicerca;
	}
	
	public boolean isMaxAttemptReached() {
		return false;
	}
	
	public String forcePublishSelected() {
		List<Long> idReport = getIdSelected().stream().map(Long::valueOf).collect(Collectors.toList());
		StatistichePdndTracingService pdndService = (StatistichePdndTracingService) this.service;

		try {
			if (!this.isSelectedAll())
				pdndService.forcePublish(idReport);
			else
				pdndService.forcePublish();
		} catch (Exception e) {
			StatistichePdndTracingBean.log.error("errore nell'azzeramento dei tentativi di pubblicazione, ids: {}", idReport, e);
		}
		
		return null;
	}
	
	public boolean getForcePublishSelectedEnabled() {
		return ((StatistichePdndTracingSearchForm)this.search).getStato().equals(PossibiliStatiRichieste.FAILED.toString());
	}
	
	public String resetAttempts() {
		
		String soggettoReadable = this.statisticaPdndTracing.getSoggettoReadable();
		
		StatistichePdndTracingService pdndService = (StatistichePdndTracingService) this.service;
		try {
			pdndService.forcePublish(this.getStatisticaPdndTracing());
		
			this.statisticaPdndTracing = new org.openspcoop2.web.monitor.statistiche.bean.StatistichePdndTracingBean(pdndService.findById(this.statisticaPdndTracing.getId()));
		} catch (Exception e) {
			StatistichePdndTracingBean.log.error("errore nell'azzeramento dei tentativi di pubblicazione, id: {}", this.statisticaPdndTracing.getId(), e);
		}
		
		this.statisticaPdndTracing.setSoggettoReadable(soggettoReadable);
		
		return null;
	}
	
	public boolean isForcePublishEnabled() {
		StatistichePdndTracingService pdndService = (StatistichePdndTracingService) this.service;
		return pdndService.isForcePublishEnabled(this.statisticaPdndTracing);
	}
	
	
	public StatistichePdndTracingBean(){
		super();
	}
	
	public StatistichePdndTracingBean(org.openspcoop2.core.commons.search.dao.IServiceManager serviceManager, org.openspcoop2.core.plugins.dao.IServiceManager pluginsServiceManager,
			DriverRegistroServiziDB driverRegistroServiziDB, DriverConfigurazioneDB driverConfigurazioneDB){
		super(serviceManager, pluginsServiceManager, driverRegistroServiziDB, driverConfigurazioneDB);
	}
}