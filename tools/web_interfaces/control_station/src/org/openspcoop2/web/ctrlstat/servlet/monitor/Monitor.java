/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


package org.openspcoop2.web.ctrlstat.servlet.monitor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.monitor.Busta;
import org.openspcoop2.pdd.monitor.BustaServizio;
import org.openspcoop2.pdd.monitor.BustaSoggetto;
import org.openspcoop2.pdd.monitor.Dettaglio;
import org.openspcoop2.pdd.monitor.Messaggio;
import org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna;
import org.openspcoop2.pdd.monitor.StatoPdd;
import org.openspcoop2.pdd.monitor.constants.StatoMessaggio;
import org.openspcoop2.pdd.monitor.driver.DriverMonitoraggio;
import org.openspcoop2.pdd.monitor.driver.FilterSearch;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.web.ctrlstat.config.ConsoleProperties;
import org.openspcoop2.web.ctrlstat.config.DatasourceProperties;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ControlStationLogger;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddTipologia;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.audit.appender.AuditDisabilitatoException;
import org.openspcoop2.web.lib.audit.appender.IDOperazione;
import org.openspcoop2.web.lib.audit.costanti.TipoOperazione;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.GeneralLink;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * 
 * Servlet per la gestione del monitoraggio applicativo
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class Monitor extends Action {

	// Driver Monitoraggio
	private static Boolean initialized = null;
	protected static MonitorCore monitorCore = null;
	protected static PddCore pddCore = null;
	protected static Boolean singlePdD = false;
	protected static ConsoleProperties consoleProperties = null;
	
	/** Locale */
	protected static DriverMonitoraggio driverMonitoraggioLocale = null;

	
	/**
	 * Inizializza il driver di monitoraggio.
	 * 
	 * @throws Exception
	 */
	private static synchronized void initMonitoraggio() throws Exception {

		if (Monitor.initialized == null) {

			// core
			monitorCore = new MonitorCore();
			pddCore = new PddCore(monitorCore);
			
			if(monitorCore.isSinglePdD()){
			
				// Inizializzazione locale
				
				String jndiName = "";
				Properties jndiProp = null;
				String tipoDatabase = null;
				DatasourceProperties datasourceProperties = null;
				try {
					datasourceProperties = DatasourceProperties.getInstance();
	
					jndiName = datasourceProperties.getSinglePdD_MonitorDataSource();
					jndiProp = datasourceProperties.getSinglePdD_MonitorDataSourceContext();
					tipoDatabase = datasourceProperties.getSinglePdD_MonitorTipoDatabase();
	
				} catch (java.lang.Exception e) {
					ControlStationLogger.getPddConsoleCoreLogger().error("[pddConsole] Lettura proprieta' non riuscita : " + e.getMessage());
					throw new Exception("[pddConsole] Lettura proprieta' non riuscita : " + e.getMessage());
				} 
	
				try {
					Monitor.driverMonitoraggioLocale = new DriverMonitoraggio(jndiName, tipoDatabase, jndiProp);
				} catch (java.lang.Exception e) {
					ControlStationLogger.getPddConsoleCoreLogger().error("[pddConsole] Inizializzazione DriverMonitoraggio non riuscita : " + e.getMessage());
					throw new Exception("[pddConsole] Inizializzazione DriverMonitoraggio non riuscita : " + e.getMessage());
				}
				
			}

			singlePdD = monitorCore.isSinglePdD();
			consoleProperties = ConsoleProperties.getInstance();
			
			// inizializzato
			initialized = true;
		}

	}

	
	
	
	
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Salvo il vecchio PageData
		PageData pdold = ServletUtils.getPageDataFromSession(session);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	

		// Inizializzo driver
		if (Monitor.initialized == null) {
			Monitor.initMonitoraggio();
		}

		MonitorHelper monitorHelper = null;
		try {

			MonitorCore monitorCore = new MonitorCore();
			
			monitorHelper = new MonitorHelper(request, pd, session);

			ArrayList<String> errors = new ArrayList<String>();

			String metodo = request.getParameter(MonitorCostanti.PARAMETRO_MONITOR_METHOD);
			String ns = request.getParameter(MonitorCostanti.PARAMETRO_MONITOR_NEW_SEARCH);
			String[] tipoProfcoll = MonitorCostanti.DEFAULT_VALUES_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE;
			String actionConfirm = request.getParameter(MonitorCostanti.PARAMETRO_MONITOR_ACTION_CONFIRM);

			if (ServletUtils.isEditModeInProgress(request) && (metodo == null || !metodo.equals(MonitorCostanti.DEFAULT_VALUE_FORM_BEAN_METHOD_DETAILS))&&
					actionConfirm == null && (ns == null || !ns.equals(MonitorCostanti.DEFAULT_VALUE_FALSE) )) {
				// prima volta che accedo quindi show form

				// recupero eventuali parametri nella request
				MonitorFormBean mb = this.getBeanForm(errors, request);

				this.showForm(session, monitorHelper, pd, tipoProfcoll, MonitorMethods.getMethodsNames(), "", "", mb, monitorCore);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						MonitorCostanti.OBJECT_NAME_MONITOR, 
						MonitorCostanti.TIPO_OPERAZIONE_MONITOR
						);
			}

			String pageSize = request.getParameter(MonitorCostanti.PARAMETRO_MONITOR_PAGE_SIZE);
			// se pageSize settato allora vuol dire che sono in una richiesta di
			// nextPage
			// altrimenti prima richiesta

			
			// se actionConfirm settato, significa che ho chiesto e
			// confermato l'eliminazione delle richieste pendenti

			// recupero i dati inseriti dall'utente nel form
			MonitorFormBean formBean = null;
			if (((pageSize == null) || pageSize.equals("")) &&
					actionConfirm == null) {
				formBean = this.getBeanForm(errors, request);
			} else {
				// prendo il form dalla sessione xe' salvato precedentemente
				formBean =  (MonitorFormBean) session.getAttribute(MonitorCostanti.SESSION_ATTRIBUTE_FORM_BEAN);
				errors.add("Dati form non validi. I dati non sono presenti in sessione.");
			}

			// se form non valido rimando all'inserimento dati
			if (formBean == null) {
				// eventuali messaggi di errore nel pd
				String errMsg = "";
				Iterator<String> it = errors.listIterator();
				while (it.hasNext()) {
					String err = it.next();
					errMsg += err + "<br>";
				}
				pd.setMessage(errMsg);

				//				return mapping.findForward("MonitorForm");

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						MonitorCostanti.OBJECT_NAME_MONITOR, 
						MonitorCostanti.TIPO_OPERAZIONE_MONITOR);
			}

			// controllo se visualizzazione dettaglio messaggio
			if (formBean.getMethod().equals(MonitorCostanti.DEFAULT_VALUE_FORM_BEAN_METHOD_DETAILS)) {

				MonitorFormBean oldFormBeanRicerca = (MonitorFormBean) session.getAttribute(MonitorCostanti.SESSION_ATTRIBUTE_FORM_BEAN);

				String idMessaggio = formBean.getIdMessaggio();
				FilterSearch filter = new FilterSearch();
				filter.setIdMessaggio(idMessaggio);
				filter.setBusta(new Busta());
				filter.setTipo(formBean.getTipo());

				ControlStationCore core = new ControlStationCore();
				SoggettiCore soggettiCore = new SoggettiCore(core);

				List<BustaSoggetto> filtroSoggetti = null;
				if(core.isVisioneOggettiGlobale(userLogin)==false){
					List<IDSoggetto> filtroIds = soggettiCore.getSoggettiWithSuperuser(userLogin);
					filtroSoggetti = new ArrayList<BustaSoggetto>(); 
					for (IDSoggetto idSoggetto : filtroIds) {
						BustaSoggetto sog = new BustaSoggetto();
						sog.setTipo(idSoggetto.getTipo());
						sog.setNome(idSoggetto.getNome());
						filtroSoggetti.add(sog);
					}
				}
				filter.setSoggettoList(filtroSoggetti);

				// prima richiesta con protocollo
				long countMessaggi = MonitorUtilities.countListaRichiestePendenti(filter,formBean.getPdd());
				List<Messaggio> listaMessaggi = MonitorUtilities.getListaRichiestePendenti(filter,formBean.getPdd());
				if (countMessaggi > 0) {
					Messaggio messaggio = listaMessaggi.get(0);
					// visualizzo dettagli messaggio
					this.showDettagliMessaggio(pd, monitorHelper, messaggio, oldFormBeanRicerca);

					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeFinished(mapping, MonitorCostanti.OBJECT_NAME_MONITOR, 
							MonitorCostanti.TIPO_OPERAZIONE_MONITOR_DETTAGLI);

					//					return mapping.findForward("DettagliOk");
				} else {
					// seconda richiesta senza protocollo (NOTA: se l'utente ha visibilita locale, questi msg non li vedra' mai)
					// Solo l'amministratore con visibilita' globale li puo' vedere.
					filter.setBusta(null);
					countMessaggi = MonitorUtilities.countListaRichiestePendenti(filter,formBean.getPdd());
					listaMessaggi = MonitorUtilities.getListaRichiestePendenti(filter,formBean.getPdd());
					if (countMessaggi > 0) {
						Messaggio messaggio = listaMessaggi.get(0);
						// visualizzo dettagli messaggio
						this.showDettagliMessaggio(pd, monitorHelper, messaggio, oldFormBeanRicerca);

						//						return mapping.findForward("DettagliOk");

						ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

						return ServletUtils.getStrutsForwardEditModeFinished(mapping, MonitorCostanti.OBJECT_NAME_MONITOR, 
								MonitorCostanti.TIPO_OPERAZIONE_MONITOR_DETTAGLI);
					} else {
						// non ho trovato messaggi
						pd.setMessage("Il messaggio [" + formBean.getIdMessaggio() + "] non e' piu presente. Impossibile visualizzare il dettaglio.");
						// rinvio alla visulizzazione del form in quanto il
						// messaggio non e' piu presente
						this.showForm(session, monitorHelper, pd, tipoProfcoll, MonitorMethods.getMethodsNames(), "", "", formBean, monitorCore);

						ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

						return ServletUtils.getStrutsForwardEditModeInProgress(mapping, MonitorCostanti.OBJECT_NAME_MONITOR, 
								MonitorCostanti.TIPO_OPERAZIONE_MONITOR_DETTAGLI);
						//						return mapping.findForward("MonitorForm");
					}
				}
			}

			// controllo se cancellazione messaggio
			String objToRemove = request.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			//String action = request.getParameter(MonitorCostanti.PARAMETRO_MONITOR_ACTION);
			if (objToRemove != null) {
//				if ((action != null) && action.equals(MonitorCostanti.DEFAULT_VALUE_PARAMETRO_MONITOR_ACTION_DELETE)) {
				FilterSearch filter = new FilterSearch();

				ControlStationCore core = new ControlStationCore();
				SoggettiCore soggettiCore = new SoggettiCore(core);
				List<BustaSoggetto> filtroSoggetti = null;
				if(core.isVisioneOggettiGlobale(userLogin)==false){
					List<IDSoggetto> filtroIds = soggettiCore.getSoggettiWithSuperuser(userLogin);
					filtroSoggetti = new ArrayList<BustaSoggetto>(); 
					for (IDSoggetto idSoggetto : filtroIds) {
						BustaSoggetto sog = new BustaSoggetto();
						sog.setTipo(idSoggetto.getTipo());
						sog.setNome(idSoggetto.getNome());
						filtroSoggetti.add(sog);
					}
				}
				filter.setSoggettoList(filtroSoggetti);

			

				// Elimino i soggetti dal db
				StringTokenizer objTok = new StringTokenizer(objToRemove, ",");
				int[] idToRemove = new int[objTok.countTokens()];

				int k = 0;
				while (objTok.hasMoreElements()) {
					idToRemove[k++] = Integer.parseInt(objTok.nextToken());
				}
				@SuppressWarnings("unused")
				String msgKO = "Impossibile cancellare Messaggio: <br>";
				@SuppressWarnings("unused")
				String msgOK = "Cancellato Messaggio : <br>";
				for (int i = 0; i < idToRemove.length; i++) {

					DataElement de = (DataElement) ((Vector<?>) pdold.getDati().elementAt(idToRemove[i])).elementAt(0);
					String idMessaggio = de.getValue();
					filter.setIdMessaggio(idMessaggio);
					// imposto tipo messaggio (inbox/outbox)
					// il tipo e' indicato nella colonna hidden 'tipo', la
					// colonna e' la 5a :
					de = (DataElement) ((Vector<?>) pdold.getDati().elementAt(idToRemove[i])).elementAt(4);
					String tipo = de.getValue();
					filter.setTipo(tipo);

					IDOperazione [] idOperazione = null;
					boolean auditDisabiltato = false;
					try{
						idOperazione = core.performAuditRequest(new TipoOperazione []{TipoOperazione.DEL}, userLogin, new Object[] {filter});
					}catch(AuditDisabilitatoException disabilitato){
						auditDisabiltato = true;
					}
					try{
						long n = MonitorUtilities.deleteRichiestePendenti(filter,formBean.getPdd());
						if (n > 0) {
							msgOK += " -" + idMessaggio + "<br>";
						} else {
							msgKO += " - " + idMessaggio + "<br>";
						}
						if(!auditDisabiltato){
							core.performAuditComplete(idOperazione, new TipoOperazione []{TipoOperazione.DEL}, userLogin, new Object[] {filter});
						}
					}catch(Exception e){
						if(!auditDisabiltato){
							core.performAuditError(idOperazione, e.getMessage(), new TipoOperazione []{TipoOperazione.DEL}, userLogin, new Object[] {filter});
						}
						msgKO += " - " + idMessaggio + "<br>";
						continue;
					}

				}

			}

			// richiedo l'azione al ws
			FilterSearch filter = new FilterSearch();

			BustaSoggetto mittente = formBean.getMittente();
			BustaSoggetto destinatario = formBean.getDestinatario();
			BustaServizio servizio = formBean.getServizio();
			String azione = formBean.getAzione();
			String profilo = formBean.getProfiloCollaborazione();
			boolean riscontro = formBean.isRiscontro();

			Busta busta = null;
			if ((mittente != null) || (destinatario != null) || (servizio != null) || (azione != null) || (profilo != null) || riscontro) {
				busta = new Busta();
				busta.setMittente(mittente);
				busta.setDestinatario(destinatario);
				busta.setAzione(azione);
				busta.setProfiloCollaborazione(profilo);
				busta.setServizio(servizio);
				busta.setAttesaRiscontro(riscontro);
				filter.setBusta(busta);
			}

			// setto gli altri filtri
			filter.setMessagePattern(formBean.getMessagePattern());
			filter.setIdMessaggio(formBean.getIdMessaggio());
			if(formBean.getSoglia()>0)
				filter.setSoglia(formBean.getSoglia());
			if(formBean.getStato()!=null)
				filter.setStato(org.openspcoop2.pdd.monitor.constants.StatoMessaggio.toEnumConstant(formBean.getStato()));
			filter.setCorrelazioneApplicativa(formBean.getCorrelazioneApplicativa());


			ControlStationCore core = new ControlStationCore();
			SoggettiCore soggettiCore = new SoggettiCore(core);
			List<BustaSoggetto> filtroSoggetti = null;
			if(core.isVisioneOggettiGlobale(userLogin)==false){
				List<IDSoggetto> filtroIds = soggettiCore.getSoggettiWithSuperuser(userLogin);
				filtroSoggetti = new ArrayList<BustaSoggetto>(); 
				for (IDSoggetto idSoggetto : filtroIds) {
					BustaSoggetto sog = new BustaSoggetto();
					sog.setTipo(idSoggetto.getTipo());
					sog.setNome(idSoggetto.getNome());
					filtroSoggetti.add(sog);
				}
			}
			filter.setSoggettoList(filtroSoggetti);

			// mostro risultati azione in base al tipo di metodo
			if (formBean.getMethod().equals(MonitorMethods.STATO_RICHIESTE.getNome())) {

				StatoPdd statoPdD = MonitorUtilities.getStatoRichiestePendenti(filter,formBean.getPdd());
				this.showStatoPdD(pd, monitorHelper,  statoPdD, MonitorMethods.STATO_RICHIESTE.getNome(), formBean);

				//				return mapping.findForward("StatoNalOk");


				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeFinished(mapping, MonitorCostanti.OBJECT_NAME_MONITOR, 
						MonitorCostanti.TIPO_OPERAZIONE_MONITOR_STATO_PDD);

			} else if (formBean.getMethod().equals(MonitorMethods.LISTA_RICHIESTE_PENDENTI.getNome())) {

				// Criteri di visualizzazione/ricerca
				Search ricerca = new Search();

				String newSearch = request.getParameter(MonitorCostanti.PARAMETRO_MONITOR_NEW_SEARCH);
				if ((newSearch != null) && newSearch.equals(MonitorCostanti.DEFAULT_VALUE_FALSE)) {
					FilterSearch oldfilter = (FilterSearch) session.getAttribute(MonitorCostanti.SESSION_ATTRIBUTE_FILTER_SEARCH);
					if (oldfilter != null) {
						filter = oldfilter;
					}

					ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
				}

				int idLista = Liste.MONITOR_MSG;
				ricerca = monitorHelper.checkSearchParameters(idLista, ricerca);
				ServletUtils.addListElementIntoSession(session, MonitorCostanti.OBJECT_NAME_MONITOR);
				// per i criteri di
				// ricerca dalla jsp
				filter.setLimit(ricerca.getPageSize(idLista));
				filter.setOffset(ricerca.getIndexIniziale(idLista));

				long countMessaggi = MonitorUtilities.countListaRichiestePendenti(filter,formBean.getPdd());
				List<Messaggio> listaMessaggi = MonitorUtilities.getListaRichiestePendenti(filter,formBean.getPdd());

				this.showMessaggi(pd, monitorHelper, countMessaggi, listaMessaggi, MonitorMethods.LISTA_RICHIESTE_PENDENTI.getNome(), ricerca, filter, formBean);

				// imposto i parametri per l'eventuale richiesta di nextPage
				Parameter pMethod = new Parameter(MonitorCostanti.PARAMETRO_MONITOR_METHOD, MonitorMethods.LISTA_RICHIESTE_PENDENTI.getNome());
				Parameter pNewSearch = new Parameter(MonitorCostanti.PARAMETRO_MONITOR_NEW_SEARCH, MonitorCostanti.DEFAULT_VALUE_FALSE);
				Parameter pPdd = new Parameter(MonitorCostanti.PARAMETRO_MONITOR_PDD, formBean.getPdd());


				// salvo l'oggetto ricerca nella sessione
				ServletUtils.setSearchObjectIntoSession(session, ricerca);
				// conservo il form bean nella session per eventuali richieste
				// successive (nextPage)
				session.setAttribute(MonitorCostanti.SESSION_ATTRIBUTE_FORM_BEAN, formBean);
				session.setAttribute(MonitorCostanti.SESSION_ATTRIBUTE_FILTER_SEARCH, filter);

				if(singlePdD){
					request.setAttribute(Costanti.REQUEST_ATTIBUTE_PARAMS, ServletUtils.getParametersAsString(false, pMethod, pNewSearch));
				}
				else{
					request.setAttribute(Costanti.REQUEST_ATTIBUTE_PARAMS, ServletUtils.getParametersAsString(false, pMethod, pNewSearch, pPdd));
				}


				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeFinished(mapping, MonitorCostanti.OBJECT_NAME_MONITOR, 
						MonitorCostanti.TIPO_OPERAZIONE_MONITOR);

				//				return mapping.findForward("MessaggiOk");

			} else if (formBean.getMethod().equals(MonitorMethods.ELIMINAZIONE_RICHIESTE_PENDENTI.getNome())) {

				if (actionConfirm == null) {
					monitorHelper.makeMenu();

					// setto la barra del titolo
					List<Parameter> lstParam = new ArrayList<Parameter>();

					lstParam.add(new Parameter(MonitorCostanti.LABEL_MONITOR, null));

					ServletUtils.setPageDataTitle(pd, lstParam);

					String msg = "";
					if (filter != null) {
						if (filter.getCorrelazioneApplicativa() != null && !"".equals(filter.getCorrelazioneApplicativa()))
							msg += "<br>ID Applicativo: "+filter.getCorrelazioneApplicativa();
						if (filter.getMessagePattern() != null && !"".equals(filter.getMessagePattern()))
							msg += "<br>Contenuto Messaggio: "+filter.getMessagePattern();
						if (filter.getSoglia() > 0)
							msg += "<br>Messaggi piu' vecchi di (minuti): "+filter.getSoglia();
						if (filter.getStato() != null && !"".equals(filter.getStato()))
							msg += "<br>Stato: "+filter.getStato();
						if (filter.getIdMessaggio() != null && !"".equals(filter.getIdMessaggio()))
							msg += "<br>ID: "+filter.getIdMessaggio();
						if (filter.getBusta() != null) {
							if (filter.getBusta().getProfiloCollaborazione() != null && !"".equals(filter.getBusta().getProfiloCollaborazione()))
								msg += "<br>Profilo di collaborazione: "+filter.getBusta().getProfiloCollaborazione();
							if (filter.getBusta().isAttesaRiscontro())
								msg += "<br>In Attesa di Riscontro: "+filter.getBusta().isAttesaRiscontro();
							if (filter.getBusta().getMittente() != null) {
								if (filter.getBusta().getMittente().getTipo() != null && !"".equals(filter.getBusta().getMittente().getTipo()))
									msg += "<br>Tipo Mittente: "+filter.getBusta().getMittente().getTipo();
								if (filter.getBusta().getMittente().getNome() != null && !"".equals(filter.getBusta().getMittente().getNome()))
									msg += "<br>Nome Mittente: "+filter.getBusta().getMittente().getNome();
							}
							if (filter.getBusta().getDestinatario() != null) {
								if (filter.getBusta().getDestinatario().getTipo() != null && !"".equals(filter.getBusta().getDestinatario().getTipo()))
									msg += "<br>Tipo Destinatario: "+filter.getBusta().getDestinatario().getTipo();
								if (filter.getBusta().getDestinatario().getNome() != null && !"".equals(filter.getBusta().getDestinatario().getNome()))
									msg += "<br>Nome Destinatario: "+filter.getBusta().getDestinatario().getNome();
							}
							if (filter.getBusta().getServizio() != null) {
								if (filter.getBusta().getServizio().getTipo() != null && !"".equals(filter.getBusta().getServizio().getTipo()))
									msg += "<br>Tipo Servizio: "+filter.getBusta().getServizio().getTipo();
								if (filter.getBusta().getServizio().getNome() != null && !"".equals(filter.getBusta().getServizio().getNome()))
									msg += "<br>Nome Servizio: "+filter.getBusta().getServizio().getNome();
							}
							if (filter.getBusta().getAzione() != null && !"".equals(filter.getBusta().getAzione()))
								msg += "<br>Azione: "+filter.getBusta().getAzione();
						}
					}
					StatoPdd stato =  MonitorUtilities.getStatoRichiestePendenti(filter,formBean.getPdd());
					if(stato.getTotMessaggi()>0){
						if (msg.equals(""))
							msg = "Eliminare i "+stato.getTotMessaggi()+" messaggi?";
						else
							msg = "Eliminare i "+stato.getTotMessaggi()+" messaggi corrispondenti ai filtri selezionati?<br>"+msg;
					}else{
						if (msg.equals(""))
							msg = "Non esistono messaggi";
						else
							msg = "Non esistono messaggi corrispondenti ai filtri selezionati:<br>"+msg;
						pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
					}
					pd.setMessage(msg);

					// imposto i parametri per l'eventuale richiesta di nextPage
					//					String params = "method=" + MonitorMethods.ELIMINAZIONE_RICHIESTE_PENDENTI.getNome();
					//					params += "&monhid=yes";
					//					params += "&actionConfirm=yes";

					Parameter pMethod = new Parameter(MonitorCostanti.PARAMETRO_MONITOR_METHOD, MonitorMethods.ELIMINAZIONE_RICHIESTE_PENDENTI.getNome());
					Parameter pActionconfirm = new Parameter(MonitorCostanti.PARAMETRO_MONITOR_ACTION_CONFIRM, Costanti.CHECK_BOX_ENABLED);

					session.setAttribute(MonitorCostanti.SESSION_ATTRIBUTE_FORM_BEAN, formBean);
					session.setAttribute(MonitorCostanti.SESSION_ATTRIBUTE_FILTER_SEARCH, filter);

					

					request.setAttribute(Costanti.REQUEST_ATTIBUTE_PARAMS, ServletUtils.getParametersAsString(false, pMethod, pActionconfirm));

					ServletUtils.addListElementIntoSession(session, MonitorCostanti.OBJECT_NAME_MONITOR);
					
					if(stato.getTotMessaggi()>0){

						String[][] bottoni = { { MonitorCostanti.LABEL_MONITOR_BUTTON_OK, MonitorCostanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_1 },
								{ MonitorCostanti.LABEL_MONITOR_BUTTON_ANNULLA, MonitorCostanti.LABEL_MONITOR_BUTTON_ANNULLA_1 } };
						pd.setBottoni(bottoni);
						
						Vector<DataElement>dati = new Vector<DataElement>();
						dati.add(ServletUtils.getDataElementForEditModeFinished());
						pd.setDati(dati);
						
						ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

						return ServletUtils.getStrutsForwardEditModeConfirm(mapping, MonitorCostanti.OBJECT_NAME_MONITOR, 
								MonitorCostanti.TIPO_OPERAZIONE_MONITOR);

						//						return mapping.findForward("MonitorConfirmForm");

					}else{
						
						ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
						return ServletUtils.getStrutsForwardEditModeInProgress(mapping, MonitorCostanti.OBJECT_NAME_MONITOR, 
								MonitorCostanti.TIPO_OPERAZIONE_MONITOR);
						//						return mapping.findForward("MonitorForm");
					}

				} else {
					IDOperazione [] idOperazione = null;
					boolean auditDisabiltato = false;
					try{
						idOperazione = core.performAuditRequest(new TipoOperazione []{TipoOperazione.DEL}, userLogin, new Object[] {filter});
					}catch(AuditDisabilitatoException disabilitato){
						auditDisabiltato = true;
					}
					try{
						long n = MonitorUtilities.deleteRichiestePendenti(filter,formBean.getPdd());
						pd.setMessage("Eliminate " + n + " Richieste Pendenti.");
						if(!auditDisabiltato){
							core.performAuditComplete(idOperazione, new TipoOperazione []{TipoOperazione.DEL}, userLogin, new Object[] {filter});
						}
					}catch(Exception e){
						if(!auditDisabiltato){
							core.performAuditError(idOperazione, e.getMessage(), new TipoOperazione []{TipoOperazione.DEL}, userLogin, new Object[] {filter});
						}
						throw e;
					}	

					this.showForm(  session, monitorHelper, pd, tipoProfcoll, MonitorMethods.getMethodsNames(), "", "", formBean, monitorCore);

					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeInProgress(mapping, MonitorCostanti.OBJECT_NAME_MONITOR, 
							MonitorCostanti.TIPO_OPERAZIONE_MONITOR);

					//					return mapping.findForward("MonitorForm");

					// return mapping.findForward("DeleteOk");
				}
			} else {
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						MonitorCostanti.OBJECT_NAME_MONITOR, 
						MonitorCostanti.TIPO_OPERAZIONE_MONITOR);
			}

		}
		catch(javax.xml.ws.WebServiceException exception){
			
			try{
			
				if(monitorHelper!=null)
					monitorHelper.makeMenu();
				
				ControlStationCore.getLog().error(exception.getMessage(), exception);
				
				Throwable e = exception;
				if(Utilities.existsInnerException(e,  org.apache.cxf.transport.http.HTTPException.class)){
					e = Utilities.getInnerException(e, org.apache.cxf.transport.http.HTTPException.class);
				}
				pd.setMessage(e.getMessage());
				
				pd.disableEditMode();
				
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						MonitorCostanti.OBJECT_NAME_MONITOR, 
						MonitorCostanti.TIPO_OPERAZIONE_MONITOR);
			}
			catch (Throwable e) {
				return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
						MonitorCostanti.OBJECT_NAME_MONITOR, 
						MonitorCostanti.TIPO_OPERAZIONE_MONITOR);
			}  
			
		}
		catch (Throwable e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					MonitorCostanti.OBJECT_NAME_MONITOR, 
					MonitorCostanti.TIPO_OPERAZIONE_MONITOR);
		}  

	}

	/**
	 * Recupera i dati dalla request e riempe il form
	 * 
	 * @param request
	 * @return MonitorFormBean
	 * @throws Exception
	 */
	private MonitorFormBean getBeanForm(ArrayList<String> errors, HttpServletRequest request) throws Exception {
		try {
			MonitorFormBean form = null;

			String method = null;
			// controllo se richiesta corretta
			boolean trovato = false;
			method = request.getParameter(MonitorCostanti.PARAMETRO_MONITOR_METHOD);
			if ((method == null) || method.equals("")) {
				return null;
			}

			MonitorMethods[] methods = MonitorMethods.values();

			for (int i = 0; (i < methods.length) && (trovato == false); i++) {
				if (method.equals(methods[i].getNome()) || method.equals(MonitorCostanti.DEFAULT_VALUE_FORM_BEAN_METHOD_DETAILS)) {
					trovato = true;
					continue;
				}
			}

			if (trovato == false) {
				errors.add("Metodo selezionato sconosciuto. Selezionare un metodo tra:" + MonitorMethods.getMethodNames().toString());
				throw new Exception("Metodo selezionato SCONOSCIUTO.");
			}

			form = new MonitorFormBean();

			form.setMethod(method);

			String idMessaggio = request.getParameter(MonitorCostanti.PARAMETRO_MONITOR_ID_MESSAGGIO);
			if ((idMessaggio != null) && !idMessaggio.equals("")) {
				form.setIdMessaggio(idMessaggio);
			}

			// tipo messaggio outbox/inbox
			String tipo = request.getParameter(MonitorCostanti.PARAMETRO_MONITOR_TIPO);
			if ((tipo != null) && !tipo.equals("")) {
				form.setTipo(tipo);
			}

			// Profilo Collaborazione
			String profcoll = request.getParameter(MonitorCostanti.PARAMETRO_MONITOR_TIPO_PROFILO_COLLABORAZIONE);

			if ((profcoll == null) || profcoll.equals("") || profcoll.equals(MonitorCostanti.DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ANY)) {
				form.setProfiloCollaborazione(null);
			} else if (profcoll.equals(MonitorCostanti.DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO)) {
				form.setProfiloCollaborazione(MonitorCostanti.LABEL_MONITOR_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO);
			} else if (profcoll.equals(MonitorCostanti.DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO)) {
				form.setProfiloCollaborazione(MonitorCostanti.LABEL_MONITOR_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO);
			} else if (profcoll.equals(MonitorCostanti.DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_SINCRONO)) {
				form.setProfiloCollaborazione(MonitorCostanti.LABEL_MONITOR_PROFILO_COLLABORAZIONE_SINCRONO);
			} else if (profcoll.equals(MonitorCostanti.DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ONEWAY)) {
				form.setProfiloCollaborazione(MonitorCostanti.LABEL_MONITOR_PROFILO_COLLABORAZIONE_SINGOLO_ONEWAY);
			}

			// Mittente
			String tipoSogg = request.getParameter(MonitorCostanti.PARAMETRO_MONITOR_TIPO_MITTENTE);
			String nomeSogg = request.getParameter(MonitorCostanti.PARAMETRO_MONITOR_NOME_MITTENTE);
			BustaSoggetto mittente = null;
			if (((tipoSogg != null) && !tipoSogg.equals("")) || ((nomeSogg != null) && !nomeSogg.equals(""))) {
				mittente = new BustaSoggetto();
				mittente.setNome(nomeSogg);
				mittente.setTipo(tipoSogg);
			}
			form.setMittente(mittente);

			// Destinatario
			BustaSoggetto destinatario = null;
			tipoSogg = request.getParameter(MonitorCostanti.PARAMETRO_MONITOR_TIPO_DESTINATARIO);
			nomeSogg = request.getParameter(MonitorCostanti.PARAMETRO_MONITOR_NOME_DESTINATARIO);
			if (((tipoSogg != null) && !tipoSogg.equals("")) || ((nomeSogg != null) && !nomeSogg.equals(""))) {
				destinatario = new BustaSoggetto();
				destinatario.setNome(nomeSogg);
				destinatario.setTipo(tipoSogg);
			}
			form.setDestinatario(destinatario);

			// Servizio
			BustaServizio servizio = null;
			String tipoServizio = request.getParameter(MonitorCostanti.PARAMETRO_MONITOR_TIPO_SERVIZIO);
			String nomeServizio = request.getParameter(MonitorCostanti.PARAMETRO_MONITOR_NOME_SERVIZIO);
			if (((tipoServizio != null) && !tipoServizio.equals("")) || ((nomeServizio != null) && !nomeServizio.equals(""))) {
				servizio = new BustaServizio();
				servizio.setNome(nomeServizio);
				servizio.setTipo(tipoServizio);
			}
			form.setServizio(servizio);

			// Azione
			String azione = request.getParameter(MonitorCostanti.PARAMETRO_MONITOR_AZIONE);
			if ((azione != null) && !azione.equals("")) {
				form.setAzione(azione);
			}

			// Soglia
			String soglia = request.getParameter(MonitorCostanti.PARAMETRO_MONITOR_SOGLIA);
			long sogliaLong = 0;
			try {
				sogliaLong = Long.parseLong(soglia);
			} catch (Exception e) {
				// ignore
			}
			form.setSoglia(sogliaLong);

			// Riscontro
			String riscontro = request.getParameter(MonitorCostanti.PARAMETRO_MONITOR_RISCONTRO);
			if ((riscontro != null) && !riscontro.equals("")) {
				form.setRiscontro(true);
			}

			// pdd
			if(singlePdD==false){
				String pdd = request.getParameter(MonitorCostanti.PARAMETRO_MONITOR_PDD);
				if ((pdd != null) && !pdd.equals("")) {
					form.setPdd(pdd);
				}
			}

			// stato
			String stato = request.getParameter(MonitorCostanti.PARAMETRO_MONITOR_STATO);
			if ((stato != null) && (stato.equals(MonitorCostanti.DEFAULT_VALUE_PARAMETRO_STATO_NONE) || stato.equals(""))) {
				stato = null;
			}
			form.setStato(stato);

			// messagePattern
//			String mp = request.getParameter(MonitorCostanti.PARAMETRO_MONITOR_PATTERN);
//			if ((mp != null) && !mp.equals("")) {
//				form.setMessagePattern(mp);
//			}

			// CorrelazioneApplicativa
			String correlazioneApplicativa = request.getParameter(MonitorCostanti.PARAMETRO_MONITOR_CORRELAZIONE_APPLICATIVA);
			if ((correlazioneApplicativa != null) && !correlazioneApplicativa.equals("")) {
				form.setCorrelazioneApplicativa(correlazioneApplicativa);
			}
			
			return form;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	private List<Parameter> createSearchString(MonitorFormBean monitorFormBean )
			throws Exception {
		try {
			List<Parameter> lstParam = new ArrayList<Parameter>();

			// metodo
			if (monitorFormBean.getMethod() != null) {
				lstParam.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_METHOD, monitorFormBean.getMethod()));
			}
			// pdd
			if (monitorFormBean.getPdd() != null) {
				lstParam.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_PDD,  monitorFormBean.getPdd()));
			}

			// destinatario
			if (monitorFormBean.getDestinatario() != null) {
				BustaSoggetto destinatario = monitorFormBean.getDestinatario();
				lstParam.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_TIPO_DESTINATARIO, destinatario.getTipo()));
				lstParam.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_NOME_DESTINATARIO,  destinatario.getNome()));
			}
			// mittente
			if (monitorFormBean.getMittente() != null) {
				BustaSoggetto mitt = monitorFormBean.getMittente();
				lstParam.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_TIPO_MITTENTE, mitt.getTipo()));
				lstParam.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_NOME_MITTENTE, mitt.getNome()));
			}
			// servizio
			if (monitorFormBean.getServizio() != null) {
				BustaServizio serv = monitorFormBean.getServizio();
				lstParam.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_TIPO_SERVIZIO, serv.getTipo()));
				lstParam.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_NOME_SERVIZIO, serv.getNome()));
			}

			// idmessaggio
			if (monitorFormBean.getIdMessaggio() != null) {
				lstParam.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_ID_MESSAGGIO, monitorFormBean.getIdMessaggio()));
			}
			// pattern
//			if (monitorFormBean.getMessagePattern() != null) {
//				lstParam.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_PATTERN, monitorFormBean.getMessagePattern()));
//			}
			
			// azione
			if (monitorFormBean.getAzione() != null) {
				lstParam.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_AZIONE, monitorFormBean.getAzione()));
			}
			// stato
			if (monitorFormBean.getStato() != null) {
				lstParam.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_STATO, monitorFormBean.getStato()));
			}
			// soglia
			if (monitorFormBean.getSoglia() > 0) {
				lstParam.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_SOGLIA,""+ monitorFormBean.getSoglia()));
			}
			// riscontro
			if (monitorFormBean.isRiscontro()) {
				lstParam.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_RISCONTRO, ""+monitorFormBean.isRiscontro()));
			}

			// profilo collaborazione
			String profcoll = monitorFormBean.getProfiloCollaborazione();
			if (profcoll == null) {
				lstParam.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_TIPO_PROFILO_COLLABORAZIONE, 
						MonitorCostanti.DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ANY));
			} else if (profcoll.equals(MonitorCostanti.LABEL_MONITOR_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO)) {
				lstParam.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_TIPO_PROFILO_COLLABORAZIONE, 
						MonitorCostanti.DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO));
			} else if (profcoll.equals(MonitorCostanti.LABEL_MONITOR_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO)) {
				lstParam.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_TIPO_PROFILO_COLLABORAZIONE,
						MonitorCostanti.DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO));
			} else if (profcoll.equals(MonitorCostanti.LABEL_MONITOR_PROFILO_COLLABORAZIONE_SINCRONO)) {
				lstParam.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_TIPO_PROFILO_COLLABORAZIONE, 
						MonitorCostanti.DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_SINCRONO));
			} else if (profcoll.equals(MonitorCostanti.LABEL_MONITOR_PROFILO_COLLABORAZIONE_SINGOLO_ONEWAY)) {
				lstParam.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_TIPO_PROFILO_COLLABORAZIONE,
						MonitorCostanti.DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ONEWAY));
			} else {
				lstParam.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_TIPO_PROFILO_COLLABORAZIONE, 
						MonitorCostanti.DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ANY ));
			}

			// correlazioneApplicative
			if (monitorFormBean.getCorrelazioneApplicativa()!=null) {
				lstParam.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_CORRELAZIONE_APPLICATIVA, monitorFormBean.getCorrelazioneApplicativa()));
			}

			return lstParam;

			//			return ServletUtils.getParametersAsString(inserisciPrimoParametro, lstParam.toArray(new Parameter[lstParam.size()])).replaceAll(" ", "%20");
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	private void showForm( HttpSession session, MonitorHelper monitorHelper, PageData pd, String[] profiliCollaborazione, 
			String[] metodiMonitor, String azione, String soglia, MonitorFormBean mb, MonitorCore monitorCore) throws Exception {
		try {
			monitorHelper.makeMenu();

			//User user = ServletUtils.getUserFromSession(session);

			// setto la barra del titolo
			Vector<GeneralLink> titlelist = new Vector<GeneralLink>();
			GeneralLink tl1 = new GeneralLink();
			tl1.setLabel(MonitorCostanti.LABEL_MONITOR);
			titlelist.addElement(tl1);
			pd.setTitleList(titlelist);

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();
			dati.add(ServletUtils.getDataElementForEditModeFinished());

			// select method
			DataElement de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_METHOD);
			de.setType(DataElementType.SELECT);
			de.setName(MonitorCostanti.PARAMETRO_MONITOR_METHOD);
			de.setSelected(mb != null ? mb.getMethod() : "");
			de.setValues(metodiMonitor);
			dati.addElement(de);
			
			if(singlePdD==false){
                String user = ServletUtils.getUserLoginFromSession(session);
                List<PdDControlStation> pdds = pddCore.pddList(user, new Search(true));
                // String[] nomiPdD = new String[];
                Vector<String> nomiPdD = new Vector<String>();
                for (PdDControlStation pdd : pdds) {
                        // visualizzo tutte le pdd che non sono esterne
                        if (!pdd.getTipo().equals(PddTipologia.ESTERNO.toString())) {
                                nomiPdD.add(pdd.getNome());
                        }
                }
                de = new DataElement();
                de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_PORTA_DOMINIO);
                de.setType(DataElementType.SELECT);
                de.setName(MonitorCostanti.PARAMETRO_MONITOR_PDD);
                de.setValues(nomiPdD.toArray(new String[nomiPdD.size()]));
                de.setSelected(mb != null ? mb.getPdd() : consoleProperties.getGestioneCentralizzata_WSMonitor_pddDefault());
                dati.addElement(de);
			}
			

			// Titolo Filter
			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_MONITOR_FILTRO_RICERCA);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);

			// CorrelazioneApplicativa
			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_CORRELAZIONE_APPLICATIVA);
			de.setName(MonitorCostanti.PARAMETRO_MONITOR_CORRELAZIONE_APPLICATIVA);
			de.setType(DataElementType.TEXT_EDIT);
			de.setValue(mb != null ? mb.getCorrelazioneApplicativa() : "");
			de.setSize(monitorHelper.getSize());
			dati.addElement(de);

			// Message Pattern
//			de = new DataElement();
//			de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_PATTERN);
//			de.setName(MonitorCostanti.PARAMETRO_MONITOR_PATTERN);
//			de.setType(DataElementType.TEXT_EDIT);
//			de.setValue(mb != null ? mb.getMessagePattern() : "");
//			de.setSize(monitorHelper.getSize());
//			dati.addElement(de);

			// Soglia
			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_SOGLIA);
			de.setType(DataElementType.TEXT_EDIT);
			de.setSize(monitorHelper.getSize());
			de.setName(MonitorCostanti.PARAMETRO_MONITOR_SOGLIA);
			de.setValue(mb != null ? "" + mb.getSoglia() : soglia);
			dati.addElement(de);

			// Stato
			String stati[] = MonitorCostanti.DEFAULT_VALUES_PARAMETRO_STATO;
			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_STATO);
			de.setType(DataElementType.SELECT);
			de.setValues(stati);
			de.setSelected(mb != null ? mb.getStato() : MonitorCostanti.DEFAULT_VALUE_PARAMETRO_STATO_NONE);
			de.setName(MonitorCostanti.PARAMETRO_MONITOR_STATO);
			dati.add(de);

			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_MONITOR_INFORMAZIONI_PROTOCOLLO);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);

			// ID Messaggio
			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_ID_MESSAGGIO);
			de.setName(MonitorCostanti.PARAMETRO_MONITOR_ID_MESSAGGIO);
			de.setType(DataElementType.TEXT_EDIT);
			de.setValue(mb != null ? mb.getIdMessaggio() : "");
			de.setSize(monitorHelper.getSize());
			dati.addElement(de);

			// Profilo Collaborazione
			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_TIPO_PROFILO_COLLABORAZIONE);
			de.setType(DataElementType.SELECT);
			de.setName(MonitorCostanti.PARAMETRO_MONITOR_TIPO_PROFILO_COLLABORAZIONE);
			de.setValues(profiliCollaborazione);
			String profcoll = mb != null ? mb.getProfiloCollaborazione() : "";
			if ((profcoll == null) || profcoll.equals("") || profcoll.equals(MonitorCostanti.DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ANY)) {
				de.setSelected(MonitorCostanti.LABEL_MONITOR_PROFILO_COLLABORAZIONE_NONE);
			} else if (profcoll.equals(MonitorCostanti.LABEL_MONITOR_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO)) {
				de.setSelected(MonitorCostanti.DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO);
			} else if (profcoll.equals(MonitorCostanti.LABEL_MONITOR_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO)) {
				de.setSelected(MonitorCostanti.DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO);
			} else if (profcoll.equals(MonitorCostanti.LABEL_MONITOR_PROFILO_COLLABORAZIONE_SINCRONO)) {
				de.setSelected(MonitorCostanti.DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_SINCRONO);
			} else if (profcoll.equals(MonitorCostanti.LABEL_MONITOR_PROFILO_COLLABORAZIONE_SINGOLO_ONEWAY)) {
				de.setSelected(MonitorCostanti.DEFAULT_VALUE_PARAMETRO_TIPO_PROFILO_COLLABORAZIONE_ONEWAY);
			}

			dati.addElement(de);

			// riscontro
			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_RISCONTRO);
			de.setType(DataElementType.CHECKBOX);
			de.setValue(mb != null ? "" + mb.isRiscontro() : "");
			de.setName(MonitorCostanti.PARAMETRO_MONITOR_RISCONTRO);
			dati.add(de);

			// Mittente
			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_MONITOR_SOGGETTO_MITTENTE);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);

			// Tipo Mittente
//			if (InterfaceType.STANDARD.equals(user.getInterfaceType())) {
//				de = new DataElement();
//				de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_TIPO_MITTENTE);
//				de.setType(DataElementType.HIDDEN);
//				de.setSize(monitorHelper.getSize());
//				de.setName(MonitorCostanti.PARAMETRO_MONITOR_TIPO_MITTENTE);
//				de.setValue("");
//				dati.addElement(de);
//			} else {
			
			SoggettiCore soggettiCore = new SoggettiCore(monitorCore);
			List<String> tipiSoggetti = new ArrayList<String>();
			List<String> tipiSoggetti_label = new ArrayList<String>();
			tipiSoggetti.add("");
			tipiSoggetti_label.add("-");
			tipiSoggetti.addAll(soggettiCore.getTipiSoggettiGestiti());
			tipiSoggetti_label.addAll(soggettiCore.getTipiSoggettiGestiti());
			
			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_TIPO_MITTENTE);
			de.setType(DataElementType.SELECT);
			de.setSize(monitorHelper.getSize());
			de.setName(MonitorCostanti.PARAMETRO_MONITOR_TIPO_MITTENTE);
			de.setSelected(mb != null ? (mb.getMittente() != null ? mb.getMittente().getTipo() : "") : "");
			de.setValues(tipiSoggetti);
			de.setLabels(tipiSoggetti_label);
			dati.addElement(de);
			
//			de = new DataElement();
//			de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_TIPO_MITTENTE);
//			de.setType(DataElementType.TEXT_EDIT);
//			de.setSize(monitorHelper.getSize());
//			de.setName(MonitorCostanti.PARAMETRO_MONITOR_TIPO_MITTENTE);
//			de.setValue(mb != null ? (mb.getMittente() != null ? mb.getMittente().getTipoSoggetto() : "") : "");
//			dati.addElement(de);
			//}

			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_NOME_MITTENTE );
			de.setType(DataElementType.TEXT_EDIT);
			de.setSize(monitorHelper.getSize());
			de.setName(MonitorCostanti.PARAMETRO_MONITOR_NOME_MITTENTE);
			de.setValue(mb != null ? (mb.getMittente() != null ? mb.getMittente().getNome() : "") : "");
			dati.addElement(de);

			// Destinatario
			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_MONITOR_SOGGETTO_DESTINATARIO);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
			// Destinatario
//			if (InterfaceType.STANDARD.equals(user.getInterfaceType())) {
//				de = new DataElement();
//				de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_TIPO_DESTINATARIO );
//				de.setType(DataElementType.HIDDEN);
//				de.setSize(monitorHelper.getSize());
//				de.setName(MonitorCostanti.PARAMETRO_MONITOR_TIPO_DESTINATARIO);
//				de.setValue("");
//				dati.addElement(de);
//			} else {
//			de = new DataElement();
//			de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_TIPO_DESTINATARIO);
//			de.setType(DataElementType.TEXT_EDIT);
//			de.setSize(monitorHelper.getSize());
//			de.setName(MonitorCostanti.PARAMETRO_MONITOR_TIPO_DESTINATARIO);
//			de.setValue(mb != null ? (mb.getDestinatario() != null ? mb.getDestinatario().getTipoSoggetto() : "") : "");
//			dati.addElement(de);
			//}

			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_TIPO_DESTINATARIO);
			de.setType(DataElementType.SELECT);
			de.setSize(monitorHelper.getSize());
			de.setName(MonitorCostanti.PARAMETRO_MONITOR_TIPO_DESTINATARIO);
			de.setSelected(mb != null ? (mb.getDestinatario() != null ? mb.getDestinatario().getTipo() : "") : "");
			de.setValues(tipiSoggetti);
			de.setLabels(tipiSoggetti_label);
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_NOME_DESTINATARIO );
			de.setType(DataElementType.TEXT_EDIT);
			de.setSize(monitorHelper.getSize());
			de.setName(MonitorCostanti.PARAMETRO_MONITOR_NOME_DESTINATARIO);
			de.setValue(mb != null ? (mb.getDestinatario() != null ? mb.getDestinatario().getNome() : "") : "");
			dati.addElement(de);

			// Servizio
			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_MONITOR_SERVIZIO);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
			// Servizio
//			if (InterfaceType.STANDARD.equals(user.getInterfaceType())) {
//				de = new DataElement();
//				de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_TIPO_SERVIZIO );
//				de.setType(DataElementType.HIDDEN);
//				de.setSize(monitorHelper.getSize());
//				de.setName(MonitorCostanti.PARAMETRO_MONITOR_TIPO_SERVIZIO);
//				de.setValue("");
//				dati.addElement(de);
//			} else {
//			de = new DataElement();
//			de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_TIPO_SERVIZIO);
//			de.setType(DataElementType.TEXT_EDIT);
//			de.setSize(monitorHelper.getSize());
//			de.setName(MonitorCostanti.PARAMETRO_MONITOR_TIPO_SERVIZIO);
//			de.setValue(mb != null ? (mb.getServizio() != null ? mb.getServizio().getTipo() : "") : "");
//			dati.addElement(de);
			
			AccordiServizioParteSpecificaCore aspsCore = new AccordiServizioParteSpecificaCore(monitorCore);
			List<String> tipiServizi = new ArrayList<String>();
			List<String> tipiServizi_label = new ArrayList<String>();
			tipiServizi.add("");
			tipiServizi_label.add("-");
			tipiServizi.addAll(aspsCore.getTipiServiziGestiti());
			tipiServizi_label.addAll(aspsCore.getTipiServiziGestiti());
			
			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_TIPO_SERVIZIO);
			de.setType(DataElementType.SELECT);
			de.setSize(monitorHelper.getSize());
			de.setName(MonitorCostanti.PARAMETRO_MONITOR_TIPO_SERVIZIO);
			de.setSelected(mb != null ? (mb.getServizio() != null ? mb.getServizio().getTipo() : "") : "");
			de.setValues(tipiServizi);
			de.setLabels(tipiServizi_label);
			dati.addElement(de);
			//}

			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_NOME_SERVIZIO);
			de.setType(DataElementType.TEXT_EDIT);
			de.setSize(monitorHelper.getSize());
			de.setName(MonitorCostanti.PARAMETRO_MONITOR_NOME_SERVIZIO);
			de.setValue(mb != null ? (mb.getServizio() != null ? mb.getServizio().getNome() : "") : "");
			dati.addElement(de);

			// Azione
			
			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_MONITOR_AZIONE);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_AZIONE);
			de.setType(DataElementType.TEXT_EDIT);
			de.setSize(monitorHelper.getSize());
			de.setName(MonitorCostanti.PARAMETRO_MONITOR_AZIONE);
			de.setValue(mb != null ? mb.getAzione() : azione);
			dati.addElement(de);

			pd.setDati(dati);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	private void showStatoPdD(PageData pd, MonitorHelper monitorHelper,   StatoPdd statoPdD, String methodName, MonitorFormBean monitorFormBean)
			throws Exception{
		try {
			// index e pagesize non sono utilizzate
			// ma bisogna settarla comunque
			pd.setIndex(0);
			pd.setPageSize(20);

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(MonitorCostanti.LABEL_MONITOR, MonitorCostanti.SERVLET_NAME_MONITOR));
			lstParam.add(new Parameter(MonitorCostanti.LABEL_MONITOR_RICERCA, MonitorCostanti.SERVLET_NAME_MONITOR,
					this.createSearchString(monitorFormBean).toArray(new Parameter[lstParam.size()])));
			lstParam.add(new Parameter(methodName, null));

			ServletUtils.setPageDataTitle(pd, lstParam);

			monitorHelper.makeMenu();

			// totale
			DataElement de = new DataElement();
			Vector<DataElement> dati = new Vector<DataElement>();

			// Se statoNal e' null allora nessuna informazione presente
			if (statoPdD == null) {
				de = new DataElement();
				de.setLabel(MonitorCostanti.LABEL_MONITOR_NESSUNA_INFORMAZIONE_PRESENTE);
				de.setType(DataElementType.TEXT);
				de.setSize(monitorHelper.getSize());
				dati.addElement(de);
			} else {

				// Stampo informazioni relative al numero di messaggi

				de = new DataElement();
				de.setLabel(MonitorCostanti.LABEL_MONITOR_STATO_PORTA_DOMINIO);
				de.setType(DataElementType.TITLE);
				de.setSize(monitorHelper.getSize());
				dati.addElement(de);

				/* Totali */
				de = new DataElement();
				de.setLabel(MonitorCostanti.LABEL_MONITOR_TOTALE_MESSAGGI);
				de.setType(DataElementType.TEXT);
				long totMsg = statoPdD.getTotMessaggi();
				de.setValue(totMsg > 0 ? "" + totMsg : MonitorCostanti.LABEL_MONITOR_NESSUN_MESSAGGIO);
				de.setSize(monitorHelper.getSize());
				dati.addElement(de);

				if (totMsg > 0) {
					// Max
					de = new DataElement();
					de.setLabel(MonitorCostanti.LABEL_MONITOR_TEMPO_MASSIMO_ATTESA);
					de.setType(DataElementType.TEXT);
					de.setValue(Utilities.convertSystemTimeIntoString_millisecondi(statoPdD.getTempoMaxAttesa() * 1000, false));
					de.setSize(monitorHelper.getSize());
					dati.addElement(de);
					// Medio
					de = new DataElement();
					de.setLabel(MonitorCostanti.LABEL_MONITOR_TEMPO_MEDIO_ATTESA);
					de.setType(DataElementType.TEXT);
					de.setValue(Utilities.convertSystemTimeIntoString_millisecondi(statoPdD.getTempoMedioAttesa() * 1000, false));
					de.setSize(monitorHelper.getSize());
					dati.addElement(de);

				}

				/* Consegna */
				de = new DataElement();
				de.setLabel(MonitorCostanti.LABEL_MONITOR_MESSAGGI_CONSEGNA);
				de.setType(DataElementType.TITLE);
				de.setSize(monitorHelper.getSize());
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(MonitorCostanti.LABEL_MONITOR_TOTALE_MESSAGGI);
				de.setType(DataElementType.TEXT);
				long inConsegna = statoPdD.getNumMsgInConsegna();
				de.setValue(inConsegna > 0 ? "" + inConsegna : MonitorCostanti.LABEL_MONITOR_NESSUN_MESSAGGIO_CONSEGNA);
				de.setSize(monitorHelper.getSize());
				dati.addElement(de);
				if (inConsegna > 0) {
					// Max
					de = new DataElement();
					de.setLabel(MonitorCostanti.LABEL_MONITOR_TEMPO_MASSIMO_ATTESA);
					de.setType(DataElementType.TEXT);
					de.setValue(Utilities.convertSystemTimeIntoString_millisecondi(statoPdD.getTempoMaxAttesaInConsegna() * 1000, false));
					de.setSize(monitorHelper.getSize());
					dati.addElement(de);
					// Medio
					de = new DataElement();
					de.setLabel(MonitorCostanti.LABEL_MONITOR_TEMPO_MEDIO_ATTESA);
					de.setType(DataElementType.TEXT);
					de.setValue(Utilities.convertSystemTimeIntoString_millisecondi(statoPdD.getTempoMedioAttesaInConsegna() * 1000, false));
					de.setSize(monitorHelper.getSize());
					dati.addElement(de);
				}

				/* Spedizione */
				de = new DataElement();
				de.setLabel(MonitorCostanti.LABEL_MONITOR_MESSAGGI_SPEDIZIONE);
				de.setType(DataElementType.TITLE);
				de.setSize(monitorHelper.getSize());
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(MonitorCostanti.LABEL_MONITOR_TOTALE_MESSAGGI);
				de.setType(DataElementType.TEXT);
				long inSpedizione = statoPdD.getNumMsgInSpedizione();
				de.setValue(inSpedizione > 0 ? "" + inSpedizione : MonitorCostanti.LABEL_MONITOR_NESSUN_MESSAGGIO_SPEDIZIONE_);
				de.setSize(monitorHelper.getSize());
				dati.addElement(de);
				if (inSpedizione > 0) {
					// Max
					de = new DataElement();
					de.setLabel(MonitorCostanti.LABEL_MONITOR_TEMPO_MASSIMO_ATTESA);
					de.setType(DataElementType.TEXT);
					de.setValue(Utilities.convertSystemTimeIntoString_millisecondi(statoPdD.getTempoMaxAttesaInSpedizione() * 1000, false));
					de.setSize(monitorHelper.getSize());
					dati.addElement(de);
					// Medio
					de = new DataElement();
					de.setLabel(MonitorCostanti.LABEL_MONITOR_TEMPO_MEDIO_ATTESA);
					de.setType(DataElementType.TEXT);
					de.setValue(Utilities.convertSystemTimeIntoString_millisecondi(statoPdD.getTempoMedioAttesaInSpedizione() * 1000, false));
					de.setSize(monitorHelper.getSize());
					dati.addElement(de);
				}

				/* Processamento */
				de = new DataElement();
				de.setLabel(MonitorCostanti.LABEL_MONITOR_MESSAGGI_PROCESSAMENTO);
				de.setType(DataElementType.TITLE);
				de.setSize(monitorHelper.getSize());
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(MonitorCostanti.LABEL_MONITOR_TOTALE_MESSAGGI_PROCESSAMENTO);
				de.setType(DataElementType.TEXT);
				long inProcess = statoPdD.getNumMsgInProcessamento();
				de.setValue(inProcess > 0 ? "" + inProcess : MonitorCostanti.LABEL_MONITOR_NESSUN_MESSAGGIO_PROCESSAMENTO);
				de.setSize(monitorHelper.getSize());
				dati.addElement(de);
				if (inProcess > 0) {
					// Max
					de = new DataElement();
					de.setLabel(MonitorCostanti.LABEL_MONITOR_TEMPO_MASSIMO_ATTESA);
					de.setType(DataElementType.TEXT);
					de.setValue(Utilities.convertSystemTimeIntoString_millisecondi(statoPdD.getTempoMaxAttesaInProcessamento() * 1000, false));
					de.setSize(monitorHelper.getSize());
					dati.addElement(de);
					// Medio
					de = new DataElement();
					de.setLabel(MonitorCostanti.LABEL_MONITOR_TEMPO_MEDIO_ATTESA);
					de.setType(DataElementType.TEXT);
					de.setValue(Utilities.convertSystemTimeIntoString_millisecondi(statoPdD.getTempoMedioAttesaInProcessamento() * 1000, false));
					de.setSize(monitorHelper.getSize());
					dati.addElement(de);
				}

				/* Messaggi Duplicati */
				de = new DataElement();
				de.setLabel(MonitorCostanti.LABEL_MONITOR_MESSAGGI_DUPLICATI);
				de.setType(DataElementType.TITLE);
				de.setSize(monitorHelper.getSize());
				dati.addElement(de);
				de = new DataElement();
				de.setLabel(MonitorCostanti.LABEL_MONITOR_TOTALE_MESSAGGI);
				de.setType(DataElementType.TEXT);
				long duplicati = statoPdD.getTotMessaggiDuplicati();
				de.setValue(duplicati > 0 ? "" + duplicati : MonitorCostanti.LABEL_MONITOR_NESSUN_MESSAGGIO_DUPLICATO);
				de.setSize(monitorHelper.getSize());
				dati.addElement(de);

			}
			pd.setInserisciBottoni(false);
			pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
			pd.setDati(dati);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	private void showDettagliMessaggio(PageData pd, MonitorHelper monitorHelper, Messaggio messaggio, MonitorFormBean monitorFormBean)
			throws Exception {
		try {

			List<Parameter> lstUrlParam = this.createSearchString(monitorFormBean);

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstUrlParam.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_NEW_SEARCH, MonitorCostanti.DEFAULT_VALUE_FALSE));

			lstParam.add(new Parameter(MonitorCostanti.LABEL_MONITOR, MonitorCostanti.SERVLET_NAME_MONITOR));
			lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, MonitorCostanti.SERVLET_NAME_MONITOR,
					lstUrlParam.toArray(new Parameter[lstUrlParam.size()])));
			lstParam.add(new Parameter(MonitorCostanti.LABEL_MONITOR_DETTAGLIO_MESSAGGIO, null));

			ServletUtils.setPageDataTitle(pd, lstParam);

			monitorHelper.makeMenu();
			Vector<DataElement> dati = new Vector<DataElement>();
			DataElement de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_ID_MESSAGGIO);
			de.setValue(messaggio.getIdMessaggio());
			de.setName(MonitorCostanti.PARAMETRO_MONITOR_ID_MESSAGGIO);
			dati.add(de);

			/* BUSTA */
			Busta busta = messaggio.getBustaInfo();
			BustaSoggetto mittente = (busta != null ? busta.getMittente() : null);
			BustaSoggetto destinatario = (busta != null ? busta.getDestinatario() : null);
			BustaServizio servizio = (busta != null ? busta.getServizio() : null);
			String azione = (busta != null ? busta.getAzione() : null);
			String profilo = (busta != null ? busta.getProfiloCollaborazione() : null);
			String riferimento = (busta != null ? busta.getRiferimentoMessaggio() : null);
			boolean riscontro = (busta != null ? busta.isAttesaRiscontro() : false);

			if (mittente != null) {
				/* Mittente */
				de = new DataElement();
				de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_MITTENTE);
				de.setType(DataElementType.TEXT);
				de.setValue(mittente.getTipo() + "/" + mittente.getNome());
				de.setName(MonitorCostanti.PARAMETRO_MONITOR_MITTENTE);
				dati.add(de);
			}

			if (destinatario != null) {
				/* Destinatario */
				de = new DataElement();
				de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_DESTINATARIO);
				de.setType(DataElementType.TEXT);
				de.setValue(destinatario.getTipo() + "/" + destinatario.getNome());
				de.setName(MonitorCostanti.PARAMETRO_MONITOR_DESTINATARIO);
				dati.add(de);
			}

			if (servizio != null) {
				/* Servizio */
				de = new DataElement();
				de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_SERVIZIO);
				de.setType(DataElementType.TEXT);
				de.setValue(servizio.getTipo() + "/" + servizio.getNome());
				de.setName(MonitorCostanti.PARAMETRO_MONITOR_SERVIZIO);
				dati.add(de);
			}
			if (azione != null) {
				/* Azione */
				de = new DataElement();
				de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_AZIONE);
				de.setType(DataElementType.TEXT);
				de.setValue(busta.getAzione());
				de.setName(MonitorCostanti.PARAMETRO_MONITOR_AZIONE);
				dati.add(de);
			}
			if (riferimento != null) {
				/* Riferimento */
				de = new DataElement();
				de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_RIFERIMENTO);
				de.setType(DataElementType.TEXT);
				de.setValue(riferimento);
				de.setName(MonitorCostanti.PARAMETRO_MONITOR_RIFERIMENTO);
				dati.add(de);
			}
			if (profilo != null) {
				/* Profilo Collaborazione */
				de = new DataElement();
				de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_PROFILO);
				de.setType(DataElementType.TEXT);
				de.setValue(profilo);
				de.setName(MonitorCostanti.PARAMETRO_MONITOR_PROFILO);
				dati.add(de);
			}

			/* Riscontro */
			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_RISCONTRO);
			de.setType(DataElementType.TEXT);
			de.setValue("" + riscontro);
			de.setName(MonitorCostanti.PARAMETRO_MONITOR_RISCONTRO);
			dati.add(de);

			/* Dettaglio */
			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_MONITOR_DETTAGLIO);
			de.setType(DataElementType.TITLE);
			dati.add(de);

			Dettaglio dettaglio = messaggio.getDettaglio();
			// tipo
			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_TIPO);
			de.setType(DataElementType.TEXT);
			de.setValue(dettaglio.getTipo());
			de.setName(MonitorCostanti.PARAMETRO_MONITOR_TIPO);
			dati.add(de);

			// idmodulo
			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_MODULO );
			de.setType(DataElementType.TEXT);
			de.setValue(dettaglio.getIdModulo());
			de.setName(MonitorCostanti.PARAMETRO_MONITOR_MODULO);
			dati.add(de);
			// stato
			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_STATO);
			de.setType(DataElementType.TEXT);
			if(messaggio.getStato()!=null)
				de.setValue(messaggio.getStato().getValue());	
			de.setName(MonitorCostanti.PARAMETRO_MONITOR_STATO);
			dati.add(de);
			if(dettaglio.getProprietaList()!=null && dettaglio.getProprietaList().size()>0){
				for (int i = 0; i < dettaglio.getProprietaList().size(); i++) {
					de = new DataElement();
					de.setLabel(dettaglio.getProprietaList().get(i).getNome());
					de.setType(DataElementType.TEXT);
					de.setValue(dettaglio.getProprietaList().get(i).getValore());
					de.setName(dettaglio.getProprietaList().get(i).getNome());
					dati.add(de);
				}
			}
			if(dettaglio.getIdCorrelazioneApplicativa()!=null){
				// CorrelazioneApplicativa
				de = new DataElement();
				de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_CORRELAZIONE_APPLICATIVA);
				de.setType(DataElementType.TEXT);
				de.setValue(dettaglio.getIdCorrelazioneApplicativa());
				de.setName(MonitorCostanti.PARAMETRO_MONITOR_CORRELAZIONE_APPLICATIVA);
				dati.add(de);
			}

			SimpleDateFormat formatter = new SimpleDateFormat("yy.MM.dd hh:mm aaa"); // SimpleDateFormat non e' thread-safe
			// ora registrazione
			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_ORA_REGISTRAZIONE);
			de.setType(DataElementType.TEXT);
			de.setValue(formatter.format(messaggio.getOraRegistrazione().getTime()));
			de.setName(MonitorCostanti.PARAMETRO_MONITOR_ORA_REGISTRAZIONE);
			dati.add(de);

			// ora attuale
			de = new DataElement();
			de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_ORA_ATTUALE);
			de.setType(DataElementType.TEXT);
			de.setValue(formatter.format(messaggio.getOraAttuale().getTime()));
			de.setName(MonitorCostanti.PARAMETRO_MONITOR_ORA_ATTUALE);
			dati.add(de);

			String erroreProcessamento = dettaglio.getErroreProcessamento();
			// servizi applicativi consegna
			if (messaggio.getDettaglio().getServizioApplicativoConsegnaList().size()>0) {
				// visualizzo i dettagli consegna
				for (int i = 0; i < messaggio.getDettaglio().getServizioApplicativoConsegnaList().size(); i++) {
					ServizioApplicativoConsegna sac1 = messaggio.getDettaglio().getServizioApplicativoConsegnaList().get(i);
					erroreProcessamento = sac1.getErroreProcessamento();

					de = new DataElement();
					de.setLabel(MonitorCostanti.LABEL_MONITOR_DETTAGLI_CONSEGNA+" [" + sac1.getNome() + "]");
					de.setType(DataElementType.TITLE);
					dati.add(de);

					// nome
					de = new DataElement();
					de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_NOME_CONSEGNA);
					de.setType(DataElementType.TEXT);
					de.setValue(sac1.getNome());
					de.setName(MonitorCostanti.PARAMETRO_MONITOR_NOME_CONSEGNA);
					dati.add(de);
					// tipo consegna
					de = new DataElement();
					de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_TIPO_CONSEGNA);
					de.setType(DataElementType.TEXT);
					de.setValue(sac1.getTipoConsegna());
					de.setName(MonitorCostanti.PARAMETRO_MONITOR_TIPO_CONSEGNA);
					dati.add(de);
					// errore processamento
					if (erroreProcessamento != null) {
						de = new DataElement();
						de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_ERRORE);
						de.setType(DataElementType.TEXT);
						de.setValue(erroreProcessamento);
						de.setName(MonitorCostanti.PARAMETRO_MONITOR_ERRORE);
						dati.add(de);
					}

					// integration manager
					de = new DataElement();
					de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_AUTORIZZAZIONE);
					de.setType(DataElementType.TEXT);
					de.setValue("" + sac1.isAutorizzazioneIntegrationManager());
					de.setName(MonitorCostanti.PARAMETRO_MONITOR_AUTORIZZAZIONE);
					dati.add(de);
					// sbustamento soap
					de = new DataElement();
					de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_SBUSTAMENTO);
					de.setType(DataElementType.TEXT);
					de.setValue("" + sac1.isSbustamentoSoap());
					de.setName(MonitorCostanti.PARAMETRO_MONITOR_SBUSTAMENTO);
					dati.add(de);
				}

			} else {
				// Errore processamento
				de = new DataElement();
				de.setLabel(MonitorCostanti.LABEL_PARAMETRO_MONITOR_ERRORE );
				de.setType(DataElementType.TEXT);
				de.setValue(erroreProcessamento);
				de.setName(MonitorCostanti.PARAMETRO_MONITOR_ERRORE);
				dati.add(de);
			}

			pd.setDati(dati);

			pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	private void showMessaggi(PageData pd, MonitorHelper monitorHelper, long countMessaggi,  List<Messaggio> listaMessaggi, 
			String methodName, Search ricerca, FilterSearch filterSearch, MonitorFormBean monitorFormBean)
			throws Exception {
		try {
			// index e pagesize non sono utilizzate
			// ma bisogna settarla comunque
			int idLista = Liste.MONITOR_MSG;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			// String search =
			// (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" :
			// ricerca.getSearchString(idLista));

			pd.setIndex(offset);
			pd.setPageSize(limit);
			pd.setSearch("off");

			int totMsg = new Long(countMessaggi).intValue();

			pd.setNumEntries(totMsg);

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(MonitorCostanti.LABEL_MONITOR, MonitorCostanti.SERVLET_NAME_MONITOR));
			lstParam.add(new Parameter(MonitorCostanti.LABEL_MONITOR_RICERCA, MonitorCostanti.SERVLET_NAME_MONITOR,
					this.createSearchString(monitorFormBean).toArray(new Parameter[lstParam.size()])));
			lstParam.add(new Parameter(methodName, null));

			ServletUtils.setPageDataTitle(pd, lstParam);

			monitorHelper.makeMenu();

			// preparo string filtro ricerca
			Busta busta = filterSearch.getBusta();
			BustaServizio servizio = (busta != null ? busta.getServizio() : null);
			BustaSoggetto mittente = (busta != null ? busta.getMittente() : null);
			BustaSoggetto destinatario = (busta != null ? busta.getDestinatario() : null);
			String profilo = (busta != null ? busta.getProfiloCollaborazione() : null);
			String azione = (busta != null ? busta.getAzione() : null);
			String idMessaggio = filterSearch.getIdMessaggio();
			//String msgPattern = filterSearch.getMessagePattern();
			String correlazioneApplicativa = filterSearch.getCorrelazioneApplicativa();
			long soglia = filterSearch.getSoglia();
			boolean riscontro = (busta != null ? busta.isAttesaRiscontro() : false);

			String _mittente = (mittente != null ? mittente.getTipo() + "/" + mittente.getNome() : null);
			String _destinatario = (destinatario != null ? destinatario.getTipo() + "/" + destinatario.getNome() : null);
			String _servizio = (servizio != null ? servizio.getTipo() + "/" + servizio.getNome() : null);

			List<Parameter> lstParamMsg = new ArrayList<Parameter>();

			if (correlazioneApplicativa != null) {
				lstParamMsg.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_CORRELAZIONE_APPLICATIVA, correlazioneApplicativa ));
			}
//			if ((msgPattern != null) && !msgPattern.equals("")) {
//				lstParamMsg.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_PATTERN, msgPattern ));
//			}
			if (soglia > 0) {
				lstParamMsg.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_SOGLIA, soglia + ""));
			}
			if (filterSearch.getStato()!=null && !"".equals(filterSearch.getStato()) && !MonitorCostanti.DEFAULT_VALUE_PARAMETRO_STATO_NONE.equals(filterSearch.getStato())) {
				lstParamMsg.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_STATO, filterSearch.getStato() + ""));
			}
			if (idMessaggio!=null) {
				lstParamMsg.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_ID_MESSAGGIO, idMessaggio));
			}
			if(busta!=null){
				if (profilo != null) {
					lstParamMsg.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_PROFILO, profilo));
				}
				if (riscontro) {
					lstParamMsg.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_RISCONTRO, riscontro + ""));
				}
				if (_mittente != null) {
					lstParamMsg.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_MITTENTE, _mittente));
				}
				if (_destinatario != null) {
					lstParamMsg.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_DESTINATARIO, _destinatario));
				}
				if (_servizio != null) {
					lstParamMsg.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_SERVIZIO, _servizio));
				}
				if ((azione != null) && !azione.equals("")) {
					lstParamMsg.add(new Parameter(MonitorCostanti.PARAMETRO_MONITOR_AZIONE,azione ));
				}
			}
			
			// se ci sono opzioni di filtraggio impostate allora visualizzo il
			// filtro
			if (lstParamMsg.size() > 0) {
				StringBuilder sb = new StringBuilder("Filtro: ");
				sb.append(ServletUtils.getParametersAsString(false, lstParamMsg.toArray(new Parameter[lstParamMsg.size()])).substring(1));
				pd.setMessage(sb.toString());
			}

			String[] labels = MonitorCostanti.LABEL_LISTA_MESSAGGI;

			pd.setLabels(labels);

			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();
			

			if (listaMessaggi != null) {
				// aggiungo i messaggi
				DataElement de = null;
				// Dichiaro il formatter per la data
				SimpleDateFormat formatter = new SimpleDateFormat("yy.MM.dd hh:mm aaa"); // SimpleDateFormat non e' thread-safe

				for (int i = 0; i < listaMessaggi.size(); i++) {

					Vector<DataElement> e = new Vector<DataElement>();
					
					Messaggio messaggio = listaMessaggi.get(i);

					String tipo = messaggio.getDettaglio() != null ? messaggio.getDettaglio().getTipo() : "";

					de = new DataElement();
					de.setLabel(MonitorCostanti.LABEL_MONITOR_IDMESSAGGIO);
					de.setName(MonitorCostanti.PARAMETRO_MONITOR_ID_MESSAGGIO);
					String pdd = "";
					if(monitorFormBean.getPdd()!=null && !"".equals(monitorFormBean.getPdd())){
						pdd = monitorFormBean.getPdd();
					}
					de.setUrl(
							MonitorCostanti.SERVLET_NAME_MONITOR,
							new Parameter(MonitorCostanti.PARAMETRO_MONITOR_ID_MESSAGGIO, messaggio.getIdMessaggio()),
							new Parameter(MonitorCostanti.PARAMETRO_MONITOR_METHOD, MonitorCostanti.DEFAULT_VALUE_FORM_BEAN_METHOD_DETAILS),
							new Parameter(MonitorCostanti.PARAMETRO_MONITOR_TIPO, tipo),
							new Parameter(MonitorCostanti.PARAMETRO_MONITOR_PDD, pdd)
							);
					de.setValue(messaggio.getIdMessaggio());
					e.addElement(de);

					// Ora Registrazione
					Date oraRegistrazione = messaggio.getOraRegistrazione();
					de = new DataElement();
					de.setValue(formatter.format(oraRegistrazione));
					e.addElement(de);

					// Stato
					de = new DataElement();
					// recupero informazioni per visualizzare IntegrationManager
					StatoMessaggio stato = messaggio.getStato();
					String statoS = "";
					if(stato!=null)
						statoS = stato.getValue();
					Dettaglio dettaglio = messaggio.getDettaglio();
					if (dettaglio != null) {
						if (dettaglio.getServizioApplicativoConsegnaList().size()>0) {
							ServizioApplicativoConsegna sac1 = dettaglio.getServizioApplicativoConsegnaList().get(0);
							if (sac1 != null && MonitorCostanti.LABEL_MONITOR_INTEGRATION_MANAGER.equals(sac1.getTipoConsegna())) {
								statoS += "[IM]";
							}
						}

					}
					de.setValue(statoS);
					e.addElement(de);

					// Dettaglio troncato
					de = new DataElement();
					String erroreProcessamento = "";
					if (dettaglio.getServizioApplicativoConsegnaList().size()>0) {
						ServizioApplicativoConsegna sac1 = dettaglio.getServizioApplicativoConsegnaList().get(0);
						erroreProcessamento = sac1.getErroreProcessamento();
					} else {
						erroreProcessamento = messaggio.getDettaglio().getErroreProcessamento();
					}
					String dettagli = messaggio.getDettaglio().getIdModulo();
					if ((erroreProcessamento != null) && !erroreProcessamento.equals("")) {
						dettagli += "::" + erroreProcessamento;
					}
					if (dettagli.length() > 80) {
						char[] arr = dettagli.toCharArray();
						dettagli = "";
						for (int ci = 0; ci < 80; ci++) {
							dettagli += arr[ci];
						}
						dettagli += "...";
					}
					de.setValue(dettagli);
					de.setSize(80);
					e.addElement(de);

					de = new DataElement();
					de.setType(DataElementType.HIDDEN);
					de.setName(MonitorCostanti.PARAMETRO_MONITOR_TIPO);
					de.setValue(tipo);
					e.addElement(de);

					dati.addElement(e);
				}
			}

			// aggiungo la matrice delle informazioni
			// pd.setSelect(false);
			// pd.setRemoveButton(true);

			pd.setAddButton(false);

			pd.setDati(dati);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
}
