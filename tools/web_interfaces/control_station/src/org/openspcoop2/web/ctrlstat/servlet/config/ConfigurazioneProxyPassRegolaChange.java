/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
import org.openspcoop2.web.ctrlstat.core.Search;
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
 * ConfigurazioneProxyPassRegolaChange
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneProxyPassRegolaChange extends Action {

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
				multiTenant = statoMultitenant.equals(StatoFunzionalita.ABILITATO);
			}
			
			long idRegola = Long.parseLong(idRegolaS);
			ConfigurazioneUrlInvocazioneRegola oldRegola = null;
			if(configurazioneGenerale.getUrlInvocazione() != null) {
				for (ConfigurazioneUrlInvocazioneRegola reg : configurazioneGenerale.getUrlInvocazione().getRegolaList()) {
					if(reg.getId().longValue() == idRegola) {
						oldRegola = reg;
						break;
					}
				}
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
			lstParam.add(new Parameter(oldRegola.getNome(), null));
			ServletUtils.setPageDataTitle(pd, lstParam);

			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (confHelper.isEditModeInProgress()) {
				
				if(nome == null) {
					nome = oldRegola.getNome();
					descrizione = oldRegola.getDescrizione();
					stato = oldRegola.getStato().toString();
					regExpr = oldRegola.isRegexpr();
					regolaText = oldRegola.getRegola();
					contestoEsterno = oldRegola.getContestoEsterno();
					baseUrl = oldRegola.getBaseUrl();
					protocollo = oldRegola.getProtocollo();
					if(protocollo == null)
						protocollo = "";
					if(StringUtils.isNotEmpty(protocollo)) {
						soggetti = soggettiCore.getIdSoggettiOperativi(protocollo);
					}
					if(oldRegola.getSoggetto() == null)
						soggetto = "";
					else 
						soggetto = new IDSoggetto(oldRegola.getSoggetto().getTipo(),oldRegola.getSoggetto().getNome()).toString();
					
					if(oldRegola.getRuolo() != null) {
						switch (oldRegola.getRuolo()) {
						case PORTA_APPLICATIVA:
							ruolo = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROXY_PASS_REGOLA_RUOLO_EROGAZIONE;
							break;
						case PORTA_DELEGATA:
							ruolo = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROXY_PASS_REGOLA_RUOLO_FRUIZIONE;
							break;
						}
					} else 
						ruolo = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROXY_PASS_REGOLA_RUOLO_QUALSIASI;
					
					if(oldRegola.getServiceBinding() != null) {
						switch (oldRegola.getServiceBinding()) {
						case REST:
							serviceBinding = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_REST; 
							break;
						case SOAP:
							serviceBinding = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_SOAP; 
							break;
						}
					} else 
						serviceBinding = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_QUALSIASI; 
				}
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = confHelper.addProxyPassConfigurazioneRegola(TipoOperazione.CHANGE, dati, idRegolaS, nome, descrizione, stato, regExpr, regolaText, contestoEsterno, baseUrl, protocollo, protocolli, soggetto, soggetti, ruolo, serviceBinding, multiTenant);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PROXY_PASS_REGOLA, 
						ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = confHelper.proxyPassConfigurazioneRegolaCheckData(TipoOperazione.CHANGE, oldRegola.getNome());
			if (!isOk) {

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = confHelper.addProxyPassConfigurazioneRegola(TipoOperazione.CHANGE, dati, idRegolaS, nome, descrizione, stato, regExpr, regolaText, contestoEsterno, baseUrl, protocollo, protocolli, soggetto, soggetti, ruolo, serviceBinding, multiTenant);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PROXY_PASS_REGOLA, 
						ForwardParams.CHANGE());
			}

			// rileggo la configurazione
			configurazioneGenerale = confCore.getConfigurazioneGenerale();
			
			if(configurazioneGenerale.getUrlInvocazione() == null)
				configurazioneGenerale.setUrlInvocazione(new ConfigurazioneUrlInvocazione());
			
			// salvataggio regola
			for (ConfigurazioneUrlInvocazioneRegola regola : configurazioneGenerale.getUrlInvocazione().getRegolaList()) {
				if(regola.getId().longValue() == idRegola) {
					regola.setNome(nome);
					if(descrizione!=null && !"".equals(descrizione)) {
						regola.setDescrizione(descrizione);
					}
					else {
						regola.setDescrizione(null);
					}
					if(baseUrl!=null && !"".equals(baseUrl)) {
						regola.setBaseUrl(baseUrl);
					}
					else {
						regola.setBaseUrl(null);
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
					else
						regola.setProtocollo(null);
					if(StringUtils.isNotEmpty(soggetto)) {
						IDSoggetto selezionatoToID = confCore.convertSoggettoSelezionatoToID(soggetto);
						IdSoggetto idSoggetto = new IdSoggetto();
						idSoggetto.setTipo(selezionatoToID.getTipo());
						idSoggetto.setNome(selezionatoToID.getNome());

						// set valori
						regola.setSoggetto(idSoggetto);
					}
					else {
						regola.setSoggetto(null);
					}
					if(StringUtils.isNotEmpty(ruolo)) {
						if(ruolo.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROXY_PASS_REGOLA_RUOLO_EROGAZIONE))
							regola.setRuolo(RuoloContesto.PORTA_APPLICATIVA);
						else if(ruolo.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROXY_PASS_REGOLA_RUOLO_FRUIZIONE))
							regola.setRuolo(RuoloContesto.PORTA_DELEGATA);
					}
					else {
						regola.setRuolo(null);
					}
					if(StringUtils.isNotEmpty(serviceBinding)) {
						if(serviceBinding.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_SOAP))
							regola.setServiceBinding(ServiceBinding.SOAP);
						else if(serviceBinding.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_REST))
							regola.setServiceBinding(ServiceBinding.REST);
						
					}
					else {
						regola.setServiceBinding(null);
					}
					if(stato.equals(StatoFunzionalita.ABILITATO.getValue()))
						regola.setStato(StatoFunzionalita.ABILITATO);
					else 
						regola.setStato(StatoFunzionalita.DISABILITATO);
					break;
				}
			}
			
			confCore.performUpdateOperation(userLogin, confHelper.smista(), configurazioneGenerale);
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.CONFIGURAZIONE_PROXY_PASS_REGOLA;

			ricerca = confHelper.checkSearchParameters(idLista, ricerca);
			
			List<ConfigurazioneUrlInvocazioneRegola> lista = confCore.proxyPassConfigurazioneRegolaList(ricerca); 
			
			confHelper.prepareProxyPassConfigurazioneRegolaList(ricerca, lista);

			pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA_SISTEMA_MODIFICATA_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PROXY_PASS_REGOLA,
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PROXY_PASS_REGOLA, ForwardParams.CHANGE());
		}
	}


}
