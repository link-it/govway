/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneMultitenant;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazione;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola;
import org.openspcoop2.core.config.IdSoggetto;
import org.openspcoop2.core.config.constants.RuoloContesto;
import org.openspcoop2.core.config.constants.ServiceBinding;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ConfigurazioneProxyPassRegolaAdd
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneProxyPassRegolaAdd extends Action {

	

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	

		try {
			ConfigurazioneHelper confHelper = new ConfigurazioneHelper(request, pd, session);
			
			String idRegolaS = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_ID_REGOLA);
			String nome = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_NOME);
			String descrizione = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_DESCRIZIONE);
			String stato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_STATO);
			String regExprS = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_REG_EXPR);
			boolean regExpr = regExprS != null ? ServletUtils.isCheckBoxEnabled(regExprS) : false;
			String regolaText = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_REGOLA_TEXT);
			String contestoEsterno = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_CONTESTO_ESTERNO);
			String baseUrl = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_BASE_URL);
			String protocollo = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_PROFILO);
			String soggetto = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_SOGGETTO);
			String ruolo = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_RUOLO);
			String serviceBinding = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_SERVICE_BINDING);
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();
			SoggettiCore soggettiCore = new SoggettiCore(confCore);
			Configurazione configurazioneGenerale = confCore.getConfigurazioneGenerale();
			ConfigurazioneMultitenant configurazioneMultitenant = configurazioneGenerale.getMultitenant();
			boolean multiTenant = false;
			if(configurazioneMultitenant != null) {
				StatoFunzionalita statoMultitenant = configurazioneMultitenant.getStato();
				multiTenant = StatoFunzionalita.ABILITATO.equals(statoMultitenant);
			}

			// Preparo il menu
			confHelper.makeMenu();
			List<Parameter> lstParam = new ArrayList<Parameter>();
			
			String postBackElementName = confHelper.getPostBackElementName();
			
			List<String> protocolli = confCore.getProtocolli();
			List<IDSoggetto> soggetti = new ArrayList<>();
			// soggetti per profilo
			if(StringUtils.isNotEmpty(protocollo)) {
				  soggetti = soggettiCore.getIdSoggettiOperativi(protocollo);
				} 
			
			// se ho modificato il soggetto ricalcolo il servizio e il service binding
			if (postBackElementName != null) {
				if(postBackElementName.equals(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_PROFILO)) {
				}
			}

			// setto la barra del titolo
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROXY_PASS_REGOLE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_PROXY_PASS_REGOLA_LIST));
			lstParam.add(ServletUtils.getParameterAggiungi());
			ServletUtils.setPageDataTitle(pd, lstParam);

			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (confHelper.isEditModeInProgress()) {
				
				if(nome == null) {
					nome = "";
					descrizione = "";
					stato = StatoFunzionalita.ABILITATO.toString();
					regExpr = false;
					regolaText = "";
					contestoEsterno = "";
					baseUrl = "";
					protocollo = "";
					soggetto = "";
					ruolo = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROXY_PASS_REGOLA_RUOLO_QUALSIASI;
					serviceBinding = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_QUALSIASI; 
				}
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = confHelper.addProxyPassConfigurazioneRegola(TipoOperazione.ADD, dati, idRegolaS, nome, descrizione, stato, regExpr, regolaText, contestoEsterno, baseUrl, protocollo, protocolli, soggetto, soggetti, ruolo, serviceBinding, multiTenant);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PROXY_PASS_REGOLA, 
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = confHelper.proxyPassConfigurazioneRegolaCheckData(TipoOperazione.ADD, null);
			if (!isOk) {

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = confHelper.addProxyPassConfigurazioneRegola(TipoOperazione.ADD, dati, idRegolaS, nome, descrizione, stato, regExpr, regolaText, contestoEsterno, baseUrl, protocollo, protocolli, soggetto, soggetti, ruolo, serviceBinding, multiTenant);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PROXY_PASS_REGOLA, 
						ForwardParams.ADD());
			}

			// rileggo la configurazione
			configurazioneGenerale = confCore.getConfigurazioneGenerale();
			
			if(configurazioneGenerale.getUrlInvocazione() == null)
				configurazioneGenerale.setUrlInvocazione(new ConfigurazioneUrlInvocazione());
			
			// calcolo prossima posizione
			int posizione = ConfigurazioneUtilities.getProssimaPosizioneUrlInvocazioneRegola(configurazioneGenerale);
			
			// salvataggio regola
			ConfigurazioneUrlInvocazioneRegola regola = new ConfigurazioneUrlInvocazioneRegola();
			
			regola.setNome(nome);
			if(descrizione!=null && !"".equals(descrizione)) {
				regola.setDescrizione(descrizione);
			}
			if(baseUrl!=null && !"".equals(baseUrl)) {
				regola.setBaseUrl(baseUrl);
			}
			if(contestoEsterno!=null && !"".equals(contestoEsterno)) {
				regola.setContestoEsterno(contestoEsterno);
			}
			else {
				regola.setContestoEsterno("");
			}
			regola.setRegexpr(regExpr);
			regola.setRegola(regolaText);
			if(StringUtils.isNotEmpty(protocollo))
				regola.setProtocollo(protocollo);
			if(StringUtils.isNotEmpty(soggetto)) {
				IDSoggetto selezionatoToID = confCore.convertSoggettoSelezionatoToID(soggetto);
				IdSoggetto idSoggetto = new IdSoggetto();
				idSoggetto.setTipo(selezionatoToID.getTipo());
				idSoggetto.setNome(selezionatoToID.getNome());

				// set valori
				regola.setSoggetto(idSoggetto);
			}
			if(StringUtils.isNotEmpty(ruolo)) {
				if(ruolo.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROXY_PASS_REGOLA_RUOLO_EROGAZIONE))
					regola.setRuolo(RuoloContesto.PORTA_APPLICATIVA);
				else if(ruolo.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROXY_PASS_REGOLA_RUOLO_FRUIZIONE))
					regola.setRuolo(RuoloContesto.PORTA_DELEGATA);
			}
			if(StringUtils.isNotEmpty(serviceBinding)) {
				if(serviceBinding.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_SOAP))
					regola.setServiceBinding(ServiceBinding.SOAP);
				else if(serviceBinding.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_REST))
					regola.setServiceBinding(ServiceBinding.REST);
				
			}
			if(stato.equals(StatoFunzionalita.ABILITATO.getValue()))
				regola.setStato(StatoFunzionalita.ABILITATO);
			else 
				regola.setStato(StatoFunzionalita.DISABILITATO);
			
			regola.setPosizione(posizione);
			
			
//			configurazioneGenerale.getUrlInvocazione().addRegola(regola);
//			
//			confCore.performUpdateOperation(userLogin, confHelper.smista(), configurazioneGenerale);
			
			confCore.performCreateOperation(userLogin, confHelper.smista(), regola);
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.CONFIGURAZIONE_PROXY_PASS_REGOLA;

			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<ConfigurazioneUrlInvocazioneRegola> lista = confCore.proxyPassConfigurazioneRegolaList(ricerca); 
			
			confHelper.prepareProxyPassConfigurazioneRegolaList(ricerca, lista);
						
			pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA_SISTEMA_MODIFICATA_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PROXY_PASS_REGOLA,
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PROXY_PASS_REGOLA, ForwardParams.ADD());
		}
	}


}
