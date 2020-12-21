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
package org.openspcoop2.web.monitor.statistiche.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComune;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.statistiche.constants.TipoReport;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica;
import org.openspcoop2.monitor.engine.dynamic.DynamicFactory;
import org.openspcoop2.monitor.engine.dynamic.IDynamicLoader;
import org.openspcoop2.monitor.engine.dynamic.IDynamicValidator;
import org.openspcoop2.monitor.sdk.condition.IFilter;
import org.openspcoop2.monitor.sdk.condition.StatisticsContext;
import org.openspcoop2.monitor.sdk.constants.CRUDType;
import org.openspcoop2.monitor.sdk.constants.SearchType;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.monitor.sdk.exceptions.SearchException;
import org.openspcoop2.monitor.sdk.exceptions.ValidationException;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dynamic.Statistiche;
import org.openspcoop2.web.monitor.core.dynamic.components.BaseComponent;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import org.openspcoop2.web.monitor.statistiche.dao.IStatisticaPersonalizzataService;
import org.openspcoop2.web.monitor.statistiche.mbean.StatsPersonalizzateBean;
import org.slf4j.Logger;

/**
 * StatistichePersonalizzateSearchForm
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class StatistichePersonalizzateSearchForm extends StatsSearchForm
implements StatisticsContext{

	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	private IFilter filtroReport;

	private IStatisticaPersonalizzataService service;

	private Hashtable<String, Statistiche> tabellaStatistichePersonalizzate = new Hashtable<String, Statistiche>();

	private List<Parameter<?>> statisticaSelezionataParameters = new ArrayList<Parameter<?>>();

	private String valoriRisorsa[] = null;

	private StatsPersonalizzateBean mBean = null;

	@Override
	public TipiDatabase getDatabaseType() {
		return _getTipoDatabase(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance());
	}

	public void setService(IStatisticaPersonalizzataService service) {
		this.service = service;
	}

	public void setFiltroReport(IFilter filtroReport) {
		this.filtroReport = filtroReport;
	}

	public IFilter getFiltroReport() {
		return this.filtroReport;
	}

	public StatistichePersonalizzateSearchForm() {
		super.setModalitaTemporale(StatisticType.GIORNALIERA);
		this.setTipoReport(TipoReport.BAR_CHART);
	}

	public void initSearchForm() {
		this.setTipoReport(TipoReport.BAR_CHART);
		this.tabellaStatistichePersonalizzate = new Hashtable<String, Statistiche>();
		this.statisticaSelezionataParameters = new ArrayList<Parameter<?>>();
		this.setStatistichePersonalizzate(null);
		this.setStatisticaSelezionata(null);
		this.setFiltro(null);
	}
	
	@Override
	public void initSearchListener(ActionEvent ae) {
		super.initSearchListener(ae);
		this.setTipoReport(TipoReport.BAR_CHART);
	}

	private Hashtable<String, Statistiche> leggiStatistiche(IDAccordo idAccordo,String nomeServizioKey) {
		try {
			if (this.tabellaStatistichePersonalizzate != null
					&& this.tabellaStatistichePersonalizzate.size() > 0)
				return this.tabellaStatistichePersonalizzate;

			if (this.tabellaStatistichePersonalizzate == null)
				this.tabellaStatistichePersonalizzate = new Hashtable<String, Statistiche>();

			List<ConfigurazioneStatistica> stats = this.service.getStatisticheByValues(idAccordo, nomeServizioKey, this.getNomeAzione());

			Statistiche statistiche = new Statistiche();
			if (stats != null && stats.size() > 0) {
				for (ConfigurazioneStatistica s : stats) {
					statistiche.addStatistica(s);
				}
				this.tabellaStatistichePersonalizzate.put(nomeServizioKey,	statistiche);
			}

		} catch (Exception e) {
			StatistichePersonalizzateSearchForm.log.error(e.getMessage(), e);
		}
		return this.tabellaStatistichePersonalizzate;
	}



	@Override
	public void tipologiaRicercaListener(ActionEvent ae) {

		super.tipologiaRicercaListener(ae);

		this.setStatistichePersonalizzate(null);
		this.setStatistichePersonalizzate(null);
		this.setFiltro(null);
	}

	@Override
	public void servizioSelected(ActionEvent ae) {
		try {
			super.servizioSelected(ae);
			this.setStatisticaSelezionata(null);
			this.setStatistichePersonalizzate(null);
			this.setNomeStatisticaPersonalizzata(null);
			this.statisticaSelezionataParameters = new ArrayList<Parameter<?>>();
			this.setFiltro(null);
			this.tabellaStatistichePersonalizzate = null;

			String nomeServizioKey =  null;
			IDAccordo idAccordo =  null;
			Statistiche r =  null;
			if(this.getNomeServizio() != null){
				IDServizio idServizio = Utility.parseServizioSoggetto(this.getNomeServizio());
				String nomeServizio = idServizio.getNome();
				AccordoServizioParteSpecifica aspsFromValues = this.getAspsFromNomeServizio(idServizio);
				nomeServizioKey = aspsFromValues.getPortType() != null ? aspsFromValues.getPortType() : nomeServizio; 

				idAccordo = getIDAccordoFromAsps(aspsFromValues);

				r = leggiStatistiche(idAccordo,nomeServizioKey).get(nomeServizioKey);

				if (r != null) {
					// setto il contesto per le statistiche
					List<ConfigurazioneStatistica> statistiche = r.getStatistiche();
					for (ConfigurazioneStatistica statistica : statistiche) {
						List<Parameter<?>> params = null;
						try {
							params =this.service.instanceParameters(statistica,this);
						} catch (Exception e) {
							StatistichePersonalizzateSearchForm.log.error(e.getMessage(), e);
						}
						if(params != null && params.size() > 0)
							for (Parameter<?> searchParam : params) {
								((BaseComponent<?>) searchParam).setContext(this);
							}
					}
				}

			}
			this.setStatistichePersonalizzate(r);
		} catch (Exception e) {
			StatistichePersonalizzateSearchForm.log.error(e.getMessage(), e);
		}

	}

	/*
	 * Recupera i parametri per l'attuale statistica selezionata
	 * 
	 * @return
	 */
	public List<Parameter<?>> getStatisticaSelezionataParameters() {
		if (this.statisticaSelezionataParameters != null
				&& this.statisticaSelezionataParameters.size() > 0)
			return this.statisticaSelezionataParameters;

		try {
			if (this.getStatisticaSelezionata() != null) {
				ConfigurazioneStatistica s = this.getStatisticaSelezionata();

				this.statisticaSelezionataParameters = this.service
						.instanceParameters(s,this);
				if (this.statisticaSelezionataParameters != null) {
					for (Parameter<?> searchParam : this.statisticaSelezionataParameters) {
						((BaseComponent<?>) searchParam).setContext(this);
					}
				}
				// aggiorno il riferimento ai parametri nella ricerca
			}

		} catch (Exception e) {
			StatistichePersonalizzateSearchForm.log.error(e.getMessage(), e);
		}
		return this.statisticaSelezionataParameters;
	}

	public boolean isStatisticaSelezionataParametersRequired(){
		if (this.statisticaSelezionataParameters != null
				&& this.statisticaSelezionataParameters.size() > 0){
			try {
				for (Parameter<?> searchParam : this.statisticaSelezionataParameters) {
					if(searchParam.getRendering().isRequired()){
						return true;
					}
				}
			} catch (Exception e) {
				StatistichePersonalizzateSearchForm.log.error(e.getMessage(), e);
			}
		}
		return false;
	}
	
	@Override
	public void azioneSelected(ActionEvent ae) {
		try {
			super.azioneSelected(ae);

			this.setNomeStatisticaPersonalizzata(null);
			this.setStatisticaSelezionata(null);
			this.statisticaSelezionataParameters = new ArrayList<Parameter<?>>();
			this.setStatistichePersonalizzate(null);
			this.setFiltro(null);
			// azzero la tabella delle statistiche perche devo ricaricare le
			// statistiche considerando l'azione
			this.tabellaStatistichePersonalizzate = null;

			String nomeServizioKey =  null;
			IDAccordo idAccordo =  null;
			Statistiche r = null;
			if(this.getNomeServizio() != null){
				IDServizio idServizio = Utility.parseServizioSoggetto(this.getNomeServizio());
				String nomeServizio = idServizio.getNome();
				AccordoServizioParteSpecifica aspsFromValues = this.getAspsFromNomeServizio(idServizio);
				nomeServizioKey = aspsFromValues.getPortType() != null ? aspsFromValues.getPortType() : nomeServizio;
				idAccordo = getIDAccordoFromAsps(aspsFromValues);

				r = leggiStatistiche(idAccordo,nomeServizioKey).get(nomeServizioKey);
				if (r != null) {
					// setto il contesto per le statistiche
					List<ConfigurazioneStatistica> statistiche = r.getStatistiche();
					for (ConfigurazioneStatistica statistica : statistiche) {
						List<Parameter<?>> params = null;
						try {
							params =this.service.instanceParameters(statistica,this);
						} catch (Exception e) {
							StatistichePersonalizzateSearchForm.log.error(e.getMessage(), e);
						}
						if(params != null && params.size() > 0)
							for (Parameter<?> searchParam : params) {
								((BaseComponent<?>) searchParam).setContext(this);
							}
					}

				} 
			}

			this.setStatistichePersonalizzate(r);
		} catch (Exception e) {
			StatistichePersonalizzateSearchForm.log.error(e.getMessage(), e);
		}

	}

	@Override
	public void statisticaSelezionataListener(ActionEvent ae) {
		super.statisticaSelezionataListener(ae);

		this.statisticaSelezionataParameters = new ArrayList<Parameter<?>>();
		// devo resettare anche il filtro
		this.setFiltroReport(null);

		// aggiorno la lista dei valori di ricerca
		this.tipoReportSelected(ae); 
	}

	
	@Override
	protected String eseguiFiltra() {

		try {

			// Tipo di periodo selezionato 'Personalizzato'
			if(this.getPeriodo().equals("Personalizzato")){
				if(this.getDataInizio() == null){
					MessageUtils.addErrorMsg("Selezionare Data Inizio");
					this.cleanSVG();
					return null;
				}

				if(this.getDataFine() == null){
					MessageUtils.addErrorMsg("Selezionare Data Fine");
					this.cleanSVG();
					return null;
				}
			}

			// Servizio E' obbligatorio
			if(StringUtils.isEmpty(this.getNomeServizio()) || this.getNomeServizio().equals("--")){
				MessageUtils.addErrorMsg("Selezionare il Servizio.");
				this.cleanSVG();
				return null;
			}
			
			boolean sezioneDatiMittente = this.validaSezioneDatiMittente();
			
			if(!sezioneDatiMittente)
				return null;
			
			if(this.getStatistichePersonalizzate() == null){
				MessageUtils.addErrorMsg("Non sono presenti statistiche personalizzate.");
				this.cleanSVG();
				return null;
			}
			
			// Statistica E' obbligatoria
			if(this.getStatistichePersonalizzate().getStatistiche() != null && this.getStatistichePersonalizzate().getStatistiche().size() > 0 && this.getStatisticaSelezionata() == null) {
				MessageUtils.addErrorMsg("Selezionare una Statistica.");
				this.cleanSVG();
				return null;
			}
			
			// risorsa della statistica personalizzata
			if(this.getTipoReport().equals(TipoReport.ANDAMENTO_TEMPORALE)) {
				if(this.getValoriRisorsa() == null || this.getValoriRisorsa().length == 0) {
					MessageUtils.addErrorMsg("E' necessario selezionare almeno un Valore Risorsa.");
					this.cleanSVG();
					return null;
				}
			}

			// Validita' della statistica scelta.
			IDynamicValidator bv = DynamicFactory.getInstance().newDynamicValidator(this.getStatisticaSelezionata().getPlugin().getTipoPlugin(),this.getStatisticaSelezionata().getPlugin().getTipo(),
					this.getStatisticaSelezionata().getPlugin().getClassName(),StatistichePersonalizzateSearchForm.log);
			bv.validate(this);
			
			// lo recupero la condizione di ricerca personalizzata e la imposto nel filtro
			org.openspcoop2.monitor.engine.dynamic.IDynamicFilter bf = DynamicFactory.getInstance().newDynamicFilter(this.getStatisticaSelezionata().getPlugin().getTipoPlugin(),this.getStatisticaSelezionata().getPlugin().getTipo(),
					this.getStatisticaSelezionata().getPlugin().getClassName(),StatistichePersonalizzateSearchForm.log);
			IFilter r = bf.createConditionFilter(this);
			if (r != null) {
				this.setFiltroReport(r);
			}

		} catch (ValidationException e) {

			MessageUtils.addErrorMsg(e.getMessage());

			Map<String, String> errors = e.getErrors();
			if (errors != null) {
				Set<String> keys = errors.keySet();

				for (String key : keys) {

					Parameter<?> sp = this.getParameter(key);

					String errorMsg = errors.get(key);

					// recupero il label del parametro
					String label = sp != null ? sp.getRendering().getLabel() : key;

					MessageUtils.addErrorMsg(label + ": " + errorMsg);

				}
			}

			this.cleanSVG();
			return null;
		} catch (SearchException re) {
			String msg = re.getMessage();
			MessageUtils.addErrorMsg(msg);
			this.cleanSVG();
			return null;
		}

		this.dataInizioDellaRicerca = this.getDataInizio();
		this.dataFineDellaRicerca = this.getDataFine();
		this.periodoDellaRicerca = this.getPeriodo();
		
		return this.getAction();
	}

	@Override
	public String getAzione() {

		return this.getNomeAzione();
	}

	@Override
	public EsitoTransazione getEsitoTransazione() {
		if(EsitoUtils.ALL_VALUE != this.getEsitoDettaglio()){
			try{
				return EsitiProperties.getInstance(this.getLogger(),this.getProtocollo()).convertToEsitoTransazione( this.getEsitoDettaglio(), this.getEsitoContesto());
			}catch(Exception e){
				throw new RuntimeException(e.getMessage(),e);
			}
		}
		return null;
	}

	@Override
	public Date getIntervalloInferiore() {

		return this.getDataInizio();
	}

	@Override
	public Date getIntervalloSuperiore() {

		return this.getDataFine();
	}

	@Override
	public Parameter<?> getParameter(String paramID) {
		return this.getParameters().get(paramID);
	}

	@Override
	public Map<String, Parameter<?>> getParameters() {
		Map<String, Parameter<?>> map = new TreeMap<String, Parameter<?>>();

		if(this.getStatisticaSelezionataParameters() != null){
			for (Parameter<?> param : this.getStatisticaSelezionataParameters()) {
				map.put(param.getId(), param);
			}
		}
		return map;
	}

	@Override
	public String getTipoServizio() {
		return this.estraiTipoServizioDalServizio();
	}
	@Override
	public String getServizio() {
		return this.estraiNomeServizioDalServizio();
	}
	
	@Override
	public Integer getVersioneServizio() {
		return this.estraiVersioneServizioDalServizio();
	}

	@Override
	public String getTipoSoggettoDestinatario() {
		return this.getTipoDestinatario();
	}
	@Override
	public String getSoggettoDestinatario() {
		return this.getNomeDestinatario();
	}

	@Override
	public String getTipoSoggettoMittente() {
		return this.getTipoMittente();
	}
	@Override
	public String getSoggettoMittente() {

		return this.getNomeMittente();
	}

	@Override
	public SearchType getTipoRicerca() {
		if(this.getTipologiaRicercaEnum() != null)
			switch (this.getTipologiaRicercaEnum()) {
			case ingresso:
				return SearchType.EROGAZIONE;
			case uscita:
				return SearchType.FRUIZIONE;
			case all:
			default:
				return SearchType.ALL;
			}
		return SearchType.ALL;
	}

	public List<SelectItem> getIntervalliTemporali() {

		List<SelectItem> res = new ArrayList<SelectItem>();

		// se selezionata una statistica allora recupero le modalita dal loader corrispondente alla statistica scelta
		if (this.getStatisticaSelezionata() != null) {

			List<StatisticType> listaStatisticTypes = new ArrayList<StatisticType>();

			try {
				IDynamicLoader bl = DynamicFactory.getInstance().newDynamicLoader(this.getStatisticaSelezionata().getPlugin().getTipoPlugin(),this.getStatisticaSelezionata().getPlugin().getTipo(),
						this.getStatisticaSelezionata().getPlugin().getClassName(),StatistichePersonalizzateSearchForm.log);
				listaStatisticTypes = bl.getEnabledStatisticType(this);
			} catch (SearchException e) {
				log.error("Errore durante la lettura dei tipi di statistica: "+e.getMessage(),e); 
			}

			for (StatisticType statisticType : listaStatisticTypes) {
				switch (statisticType) {
				case GIORNALIERA:
					res.add(new SelectItem("giornaliera", "Giornaliera"));
					break;
				case MENSILE:
					res.add(new SelectItem("mensile", "Mensile"));
					break;
				case ORARIA: 
					res.add(new SelectItem("oraria", "Oraria"));
					break;
				case  SETTIMANALE: 
					res.add(new SelectItem("settimanale", "Settimanale"));
					break;
				}
			}
		} else {
			res.add(new SelectItem("oraria", "Oraria"));
			res.add(new SelectItem("giornaliera", "Giornaliera"));
			res.add(new SelectItem("settimanale", "Settimanale"));
			res.add(new SelectItem("mensile", "Mensile"));
		}
		return res;
	}

	public void validateSelectedPlugin(FacesContext context,
			UIComponent component, Object value) {

		if (value != null) {
			String nomeStatistica = (String) value;
			ConfigurazioneStatistica r = this.getStatistichePersonalizzate()
					.getStatisticaByLabel(nomeStatistica);

			try {
				if (r != null){
					DynamicFactory.getInstance().newDynamicLoader(r.getPlugin().getTipoPlugin(),r.getPlugin().getTipo(),
							r.getPlugin().getClassName(),StatistichePersonalizzateSearchForm.log);
					//Class.forName(r.getClassName());
				}
			//} catch (ClassNotFoundException e) {
			} catch (SearchException e) {
				// throw new ValidatorException(new
				// FacesMessage("Impossibile caricare il plugin. La classe indicata ["+r.getClassName()+"] non esiste."));
				String msg = "Impossibile selezionare la statistica ("
						+ nomeStatistica + "). La classe indicata ["
						+ r.getPlugin().getClassName() + "] non esiste.";
				((UIInput) component).setValid(false);
				context.addMessage(
						component.getClientId(context),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
			}
		}

	}

	/**
	 * controlla se il servizio selezionato ha delle statistiche personalizzate
	 * associate
	 */
	public void hasValideStatistichePersonalizzateByNomeServizio(
			FacesContext context, UIComponent component, Object value) {
		try {
			if(value == null)
				this.setNomeServizio(null);

			if (value != null) {

				String nomeServizio = (String) value;
				this.setNomeServizio(nomeServizio);
				if(this.getNomeServizio() != null){
					IDServizio idServizio = Utility.parseServizioSoggetto(this.getNomeServizio());
					nomeServizio = idServizio.getNome();
					AccordoServizioParteSpecifica aspsFromValues = this.getAspsFromNomeServizio(idServizio);
					String nomeServizioKey = aspsFromValues.getPortType() != null ? aspsFromValues.getPortType() : nomeServizio;

					IDAccordo idAccordo = getIDAccordoFromAsps(aspsFromValues);
					Statistiche s = leggiStatistiche(idAccordo,nomeServizioKey).get(nomeServizioKey);

					// se non ci sono statistiche vuol dire che non va bene
					if (s == null || s.getStatistiche() == null
							|| s.getStatistiche().size() < 1) {
						String msg = "Non sono presenti statistiche personalizzate per il servizio selezionato ["
								+ nomeServizio
								+ "]. Provare a selezionare un'azione.";
						((UIInput) component).setValid(true);
						context.addMessage("errorMessageServizio",
								new FacesMessage(FacesMessage.SEVERITY_WARN, msg,
										null));
					}
				}
			}
		} catch (Exception e) {
			StatistichePersonalizzateSearchForm.log.error(e.getMessage(), e);
		}
	}

	public void hasValideStatistichePersonalizzateByNomeAzione(
			FacesContext context, UIComponent component, Object value) {
		if (value != null && !"--".equals(value)) {
			String nomeAzione = (String) value;
			this.setNomeAzione(nomeAzione);
			//	this.azioneSelected(null);
			String nomeServizioKey = null;
			IDAccordo idAccordo = null;
			Statistiche s = null;
			boolean valid = true;
			try{
				if(this.getNomeServizio() != null){
					IDServizio idServizio = Utility.parseServizioSoggetto(this.getNomeServizio());
					String nomeServizio = idServizio.getNome();
					AccordoServizioParteSpecifica aspsFromValues = this.getAspsFromNomeServizio(idServizio);
					nomeServizioKey = aspsFromValues.getPortType() != null ? aspsFromValues.getPortType() : nomeServizio;
					idAccordo = getIDAccordoFromAsps(aspsFromValues);
					s = leggiStatistiche(idAccordo,nomeServizioKey).get(nomeServizioKey);
					// se non ci sono statistiche vuol dire che non va bene

					if (s == null || s.getStatistiche() == null || s.getStatistiche().size() < 1) {
						valid = false;
					}
					// cerco errorMessageServizio e lo rimuovo
					Iterator<FacesMessage> it = context
							.getMessages("errorMessageServizio");
					while (it.hasNext()) {
						it.next();
						it.remove();
					}
					String msg = "Non sono presenti statistiche personalizzate per l'azione selezionata [" + nomeAzione + "] del servizio [" + this.getNomeServizio() + "].";

					((UIInput) component).setValid(valid);
					if (!valid)
						context.addMessage(
								component.getClientId(context),
								new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
				}
			}catch(Exception e){
				StatistichePersonalizzateSearchForm.log.error(e.getMessage(), e);
			}
		}
	}

	public String [] getValoriRisorsa() {
		return this.valoriRisorsa;
	}

	public void setValoriRisorsa(String valoreRisorsa[]) {
		this.valoriRisorsa = valoreRisorsa;
	}

	@Override
	public void tipoReportSelected(ActionEvent ae) {
		this.valoriRisorsa = null;

		if(this.getTipoReport().equals(TipoReport.ANDAMENTO_TEMPORALE) && this.getStatisticaSelezionata() != null){
			List<String> valoriRisorse = null;
			try {
				valoriRisorse = this.mBean.getValoriRisorseAsString();
			} catch (ServiceException e) {
				log.error(e.getMessage(), e);
			}

			if(valoriRisorse != null && valoriRisorse.size() > 0){
				this.valoriRisorsa = new String[1];
				this.valoriRisorsa[0] = valoriRisorse.get(0);
			}
		}	
	}

	public void validateSelectedValoriRisorsa(FacesContext context,	UIComponent component, Object value) {

		// il valore e' valido per default
		boolean valid = true;
		// validazione solo se sono in modalita' andamento temporale e ho selezionato una ricerca.	
		if(this.getTipoReport().equals(TipoReport.ANDAMENTO_TEMPORALE) && this.getStatisticaSelezionata() != null){
			// imposto il valid == false
			valid = false;

			if (value != null) {
				String valori [] = (String[]) value;
				// almeno un valore selezionato
				if(valori.length > 0)
					valid = true;
			}
		}

		if(!valid){
			String msg = "E' necessario selezionare almeno un Valore Risorsa";
			((UIInput) component).setValid(false);
			context.addMessage(	component.getClientId(context),	new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
		}
	}

	public void setmBean(StatsPersonalizzateBean mBean) {
		this.mBean = mBean;
	}

	public StatsPersonalizzateBean getmBean() {
		return this.mBean;
	}
	
	private IDAccordo getIDAccordoFromAsps(AccordoServizioParteSpecifica aspsFromValues)
			throws DriverRegistroServiziException {
		IdAccordoServizioParteComune idAccordoServizioParteComune = aspsFromValues.getIdAccordoServizioParteComune();
		Integer ver = idAccordoServizioParteComune.getVersione();
		String nomeSoggettoReferente = null;
		String tipoSoggettoReferente = null;

		if(idAccordoServizioParteComune.getIdSoggetto() != null){
			nomeSoggettoReferente=	idAccordoServizioParteComune.getIdSoggetto().getNome();
			tipoSoggettoReferente=	idAccordoServizioParteComune.getIdSoggetto().getTipo();
		}

		String nomeAS = idAccordoServizioParteComune.getNome(); 

		IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromValues(nomeAS, tipoSoggettoReferente, nomeSoggettoReferente, ver);
		return idAccordo;
	}

	@Override
	public Logger getLogger() {
		return LoggerManager.getPddMonitorCoreLogger();
	}

	@Override
	public DAOFactory getDAOFactory() {
		try{
			return DAOFactory.getInstance(LoggerManager.getPddMonitorSqlLogger());
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	@Override
	public CRUDType getTipoOperazione() {
		return CRUDType.SEARCH;
	}
}
