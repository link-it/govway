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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.Route;
import org.openspcoop2.core.config.RouteGateway;
import org.openspcoop2.core.config.RouteRegistro;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.RoutingTableDefault;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * routing
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneRouting extends Action {

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

			String tiporotta = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA);
			if (tiporotta == null) {
				tiporotta = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_GATEWAY;
			}
			String tiposoggrotta = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_SOGGETTO_ROTTA);
			String nomesoggrotta = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME_SOGGETTO_ROTTA);
			String registrorotta = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRO_ROTTA);
			String rottaenabled = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ROTTA_ENABLED);
			String applicaModificaS = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_APPLICA_MODIFICA);
			boolean applicaModifica = ServletUtils.isCheckBoxEnabled(applicaModificaS);

			ConfigurazioneCore confCore = new ConfigurazioneCore();
			SoggettiCore soggettiCore = new SoggettiCore(confCore);

			// Preparo il menu
			confHelper.makeMenu();

			// Prendo la lista di registri e la metto in un array
			// aggiungendo il campo "all"
			List<AccessoRegistroRegistro> list = confCore.registriList(new ConsoleSearch(true));
			int totReg = list.size();
			totReg++;
			String[] registriList = new String[totReg];
			String[] registriListLabel = new String[totReg];
			registriList[0] = "0";
			registriListLabel[0] = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_REGISTRO_ROTTA_ALL;
			int i = 1;
			for (AccessoRegistroRegistro arr : list) {
				registriList[i] = arr.getNome();
				registriListLabel[i] = arr.getNome();
				i++;
			}

			// Soggetti
			List<String> tipiSoggetti = new ArrayList<>();
			tipiSoggetti.add("-");
			tipiSoggetti.addAll(soggettiCore.getTipiSoggettiGestiti());
			String[] tipiSoggettiLabel = tipiSoggetti.toArray(new String[1]);

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TABELLA_DI_ROUTING, null));

			ServletUtils.setPageDataTitle(pd, lstParam);

			String postBackElementName = confHelper.getPostBackElementName();

			// Controllo se ho modificato il protocollo, resetto il referente
			if(postBackElementName != null &&
				postBackElementName.equalsIgnoreCase(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ROTTA_ENABLED)){
				applicaModifica = false;
			}


			// Se tiporottahid != null, modifico i dati della porta di dominio
			// nel
			// db
			if (!(confHelper.isEditModeInProgress() && !applicaModifica)) {
				// Controlli sui campi immessi
				boolean isOk = confHelper.routingCheckData(registriList);
				if (!isOk) {
					// preparo i campi
					List<DataElement> dati = new ArrayList<>();

					dati.add(ServletUtils.getDataElementForEditModeFinished());

					dati = confHelper.addRoutingToDati(TipoOperazione.OTHER, tiporotta, tiposoggrotta,
							nomesoggrotta, registrorotta, rottaenabled,
							registriList, registriListLabel, tipiSoggettiLabel, dati);


					pd.setDati(dati);

					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
							ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_ROUTING,
							ConfigurazioneCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE_ROUTING);
				}

				// Modifico i dati della porta di dominio nel db
				RoutingTable rt = confCore.getRoutingTable();
				if ((rottaenabled == null) || !rottaenabled.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO)) {
					rt.setAbilitata(false);
				} else {
					rt.setAbilitata(true);

					Route defaultRoute = null;
					if ( rt.getDefault()!=null && rt.getDefault().sizeRouteList() > 0) {
						defaultRoute = rt.getDefault().removeRoute(0);
					} else
						defaultRoute = new Route();

					if (tiporotta.equals(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_GATEWAY)) {
						RouteGateway rg = new RouteGateway();
						rg.setTipo(tiposoggrotta);
						rg.setNome(nomesoggrotta);
						defaultRoute.setGateway(rg);
						defaultRoute.setRegistro(null);
					} else {

						RouteRegistro rr = new RouteRegistro();
						rr.setNome(registrorotta);

						defaultRoute.setRegistro(rr);
						defaultRoute.setGateway(null);
					}

					rt.setDefault(new RoutingTableDefault());
					rt.getDefault().addRoute(defaultRoute);

				}

				confCore.performUpdateOperation(userLogin, confHelper.smista(), rt);

				List<DataElement> dati = new ArrayList<>();

				dati = confHelper.addRoutingToDati(TipoOperazione.OTHER, tiporotta, tiposoggrotta, nomesoggrotta, 
						registrorotta, rottaenabled,  registriList, registriListLabel, tipiSoggettiLabel, dati);


				pd.setDati(dati);

				pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TABELLA_ROUTING_MODIFICATA_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeFinished(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_ROUTING,
						ConfigurazioneCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE_ROUTING
						);

			}

			if (rottaenabled == null) {
				RoutingTable rt = confCore.getRoutingTable();

				if ((rt == null) || (rt.getAbilitata()==null || !rt.getAbilitata())) {
					rottaenabled = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
					tiporotta = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_GATEWAY;
					tiposoggrotta = "";
					nomesoggrotta = "";
					registrorotta = "0";
				} else {
					rottaenabled = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
					Route r = null;
					if(rt.getDefault()!=null && rt.getDefault().sizeRouteList()>0){
						r = rt.getDefault().getRoute(0);
					}
					RouteGateway rg = r != null ? r.getGateway() : null;
					if (rg != null) {
						tiporotta = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_GATEWAY;
						tiposoggrotta = rg.getTipo();
						nomesoggrotta = rg.getNome();
						registrorotta = "";
					}
					RouteRegistro rr = r != null ? r.getRegistro() : null;
					if (rr != null) {
						tiporotta = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_REGISTRO;
						tiposoggrotta = "";
						nomesoggrotta = "";
						registrorotta = rr.getNome();
					}
				}
			}

			// preparo i campi
			List<DataElement> dati = new ArrayList<>();

			dati.add(ServletUtils.getDataElementForEditModeFinished());

			dati = confHelper.addRoutingToDati(TipoOperazione.OTHER, tiporotta, tiposoggrotta, nomesoggrotta, 
					registrorotta, rottaenabled,  registriList, registriListLabel, tipiSoggettiLabel, dati);


			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_ROUTING,
					ConfigurazioneCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE_ROUTING
					);
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_ROUTING, 
					ConfigurazioneCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE_ROUTING);
		}
	}


}
