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


package org.openspcoop2.web.ctrlstat.servlet.config;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.Route;
import org.openspcoop2.core.config.RouteGateway;
import org.openspcoop2.core.config.RouteRegistro;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.RoutingTableDestinazione;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * routingChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneRouteChange extends Action {

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
			
			// Preparo il menu
			confHelper.makeMenu();

			String id = confHelper.getParametroInteger(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID);
			int idInt = Integer.parseInt(id);
			String nome = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME);
			String tipo = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO);
			String tiporotta = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA);
			String tiposoggrotta = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_SOGGETTO_ROTTA);
			String nomesoggrotta = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME_SOGGETTO_ROTTA);
			String registrorotta = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRO_ROTTA);

			ConfigurazioneCore confCore = new ConfigurazioneCore();
			SoggettiCore soggettiCore = new SoggettiCore(confCore);
			
			// Soggetti
			List<String> tipiSoggetti = new ArrayList<>();
			tipiSoggetti.addAll(soggettiCore.getTipiSoggettiGestiti());
			String[] tipiSoggettiLabel = tipiSoggetti.toArray(new String[1]);
			
			String[] tipiSoggettiLabelPerProtocollo = tipiSoggettiLabel;
			if(tipo!=null && !"".equals(tipo) && !"-".equals(tipo)){
				List<String> tipiSoggettiPerRotta = new ArrayList<>();
				tipiSoggettiPerRotta.add("-");
				tipiSoggettiPerRotta.addAll(soggettiCore.getTipiSoggettiGestitiProtocollo(soggettiCore.getProtocolloAssociatoTipoSoggetto(tipo)));
				tipiSoggettiLabelPerProtocollo = tipiSoggettiPerRotta.toArray(new String[1]);
			}

			// Prendo nome e tipo del routing
			RoutingTable rt = confCore.getRoutingTable();
			RoutingTableDestinazione rtd = null;
			for (int i = 0; i < rt.sizeDestinazioneList(); i++) {
				rtd = rt.getDestinazione(i);
				if (idInt == rtd.getId().intValue()) {
					break;
				}
			}
			// Quando arrivo qui, in rtd ho il mio routing
			String oldNome = rtd.getNome();
			String oldTipo = rtd.getTipo();
			String titolo = oldTipo + "/" + oldNome;
			
			if(nome==null || "".equals(nome)){
				nome=rtd.getNome();
			}
			if(tipo==null || "".equals(tipo)){
				tipo=rtd.getTipo();
			}

			// Prendo la lista di registri e la metto in un array
			// aggiungendo il campo "all"
			List<AccessoRegistroRegistro> list = confCore.registriList(new ConsoleSearch(true));
			String[] registriList = new String[list.size() + 1];
			String[] registriListLabel = new String[list.size() + 1];
			registriList[0] = "0";
			registriListLabel[0] = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_REGISTRO_ROTTA_ALL;
			int i = 1;
			for (AccessoRegistroRegistro arr : list) {
				registriList[i] = arr.getNome();
				registriListLabel[i] = arr.getNome();
				i++;
			}

			// Se nomehid = null, devo visualizzare la pagina per la
			// modifica dati
			if (confHelper.isEditModeInProgress()) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<>();

				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TABELLA_DI_ROUTING, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_ROUTING));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_DESTINAZIONI, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_ROTTE_ROUTING_LIST));
				lstParam.add(new Parameter(titolo, null));

				ServletUtils.setPageDataTitle(pd, lstParam);

				Route r = rtd.getRoute(0);
				RouteGateway rg = r.getGateway();
				RouteRegistro rr = r.getRegistro();
				if (tiporotta == null) {
					if (rg != null) {
						tiporotta = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_GATEWAY;
					} else {
						tiporotta = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_REGISTRO;
					}
				}
				if (tiposoggrotta == null) {
					if (rg != null) {
						tiposoggrotta = rg.getTipo();
						nomesoggrotta = rg.getNome();
					} else {
						tiposoggrotta = "";
						nomesoggrotta = "";
					}
				}
				if (registrorotta == null) {
					if (rr != null) {
						registrorotta = rr.getNome();
					} else {
						registrorotta = "0";
					}
				}

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = confHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, id, null, null, dati);

				dati = confHelper.addValoriRottaToDati(TipoOperazione.CHANGE, nome, tipo, tiporotta, registrorotta, 
						registriList, registriListLabel, dati, tiposoggrotta, nomesoggrotta,
						tipiSoggettiLabel,tipiSoggettiLabelPerProtocollo);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_ROTTE_ROUTING, 
						ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = confHelper.routingListCheckData(TipoOperazione.CHANGE, registriList);
			if (!isOk) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<>();

				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TABELLA_DI_ROUTING, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_ROUTING));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_DESTINAZIONI, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_ROTTE_ROUTING_LIST));
				lstParam.add(new Parameter(titolo, null));

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = confHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, id, null, null, dati);

				dati = confHelper.addValoriRottaToDati(TipoOperazione.CHANGE, nome, tipo, tiporotta, registrorotta, 
						registriList, registriListLabel, dati, tiposoggrotta, nomesoggrotta,
						tipiSoggettiLabel,tipiSoggettiLabelPerProtocollo);


				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_ROTTE_ROUTING, 
						ForwardParams.CHANGE());
			}

			// Modifico i dati del routing nel db
			RoutingTableDestinazione rtdToUpdate = new RoutingTableDestinazione();
			for (i = 0; i < rt.sizeDestinazioneList(); i++) {
				RoutingTableDestinazione tmpRtd = rt.getDestinazione(i);
				if (idInt == tmpRtd.getRoute(0).getId().intValue()) {
					rt.removeDestinazione(i);
					break;
				}
			}

			rtdToUpdate.setNome(nome);
			rtdToUpdate.setTipo(tipo);

			Route tmpR = new Route();
			if (tiporotta.equals(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_GATEWAY)) {
				RouteGateway rg = new RouteGateway();
				rg.setTipo(tiposoggrotta);
				rg.setNome(nomesoggrotta);
				tmpR.setGateway(rg);
			} else {
				RouteRegistro rr = new RouteRegistro();
				rr.setNome(registrorotta);
				tmpR.setRegistro(rr);
			}
			rtdToUpdate.addRoute(tmpR);
			rt.addDestinazione(rtdToUpdate);

			confCore.performUpdateOperation(userLogin, confHelper.smista(), rt);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			List<RoutingTableDestinazione> lista = confCore.routingList(ricerca);

			confHelper.prepareRoutingList(ricerca, lista);

			pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TABELLA_ROUTING_MODIFICATA_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_ROTTE_ROUTING,
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_ROTTE_ROUTING, ForwardParams.CHANGE());
		}
	}
}
