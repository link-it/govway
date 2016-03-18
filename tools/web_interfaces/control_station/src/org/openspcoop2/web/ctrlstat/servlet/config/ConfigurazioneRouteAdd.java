/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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


package org.openspcoop2.web.ctrlstat.servlet.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
import org.openspcoop2.core.config.RoutingTableDestinazione;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * routingAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneRouteAdd extends Action {

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

			String nome = request.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME);
			String tipo = request.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO);
			String tiporotta = request.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA);
			if (tiporotta == null) {
				tiporotta = ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_REGISTRO;
			}
			String tiposoggrotta = request.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_SOGGETTO_ROTTA);
			String nomesoggrotta = request.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME_SOGGETTO_ROTTA);
			String registrorotta = request.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRO_ROTTA);

			ConfigurazioneCore confCore = new ConfigurazioneCore();
			SoggettiCore soggettiCore = new SoggettiCore(confCore);

			// Soggetti
			List<String> tipiSoggetti = new ArrayList<String>();
			tipiSoggetti.add("-");
			tipiSoggetti.addAll(soggettiCore.getTipiSoggettiGestiti());
			String[] tipiSoggettiLabel = tipiSoggetti.toArray(new String[1]);
			
			String[] tipiSoggettiLabelPerProtocollo = tipiSoggettiLabel;
			if(tipo!=null && !"".equals(tipo) && !"-".equals(tipo)){
				List<String> tipiSoggettiPerRotta = new ArrayList<String>();
				tipiSoggettiPerRotta.add("-");
				tipiSoggettiPerRotta.addAll(soggettiCore.getTipiSoggettiGestitiProtocollo(soggettiCore.getProtocolloAssociatoTipoSoggetto(tipo)));
				tipiSoggettiLabelPerProtocollo = tipiSoggettiPerRotta.toArray(new String[1]);
			}
			
			// Preparo il menu
			confHelper.makeMenu();

			// Prendo la lista di registri e la metto in un array
			// aggiungendo il campo "all"
			List<AccessoRegistroRegistro> list = confCore.registriList(new Search(true));
			String[] registriList = new String[list.size() + 1];
			String[] registriListLabel = new String[list.size() + 1];
			registriList[0] = "0";
			registriListLabel[0] = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_REGISTRO_ROTTA_ALL;
			int i = 1;
			for (AccessoRegistroRegistro arr : list) {
				// registriList[i] = arr.getId().toString();
				registriList[i] = arr.getNome();
				registriListLabel[i] = arr.getNome();
				i++;
			}

			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (ServletUtils.isEditModeInProgress(request)) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<Parameter>();

				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE, null));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TABELLA_DI_ROUTING, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_ROUTING));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_DESTINAZIONI, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_ROTTE_ROUTING_LIST));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null));

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = confHelper.addValoriRottaToDati(TipoOperazione.ADD,  nome, tipo, tiporotta,
						ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_REGISTRO_ROTTA_ALL, 
						 registriList, registriListLabel, dati, "","",
						 tipiSoggettiLabel,tipiSoggettiLabelPerProtocollo);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_ROTTE_ROUTING, 
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = confHelper.routingListCheckData(TipoOperazione.ADD, registriList);
			if (!isOk) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<Parameter>();

				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE, null));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TABELLA_DI_ROUTING, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_ROUTING));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_DESTINAZIONI, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_ROTTE_ROUTING_LIST));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null));

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati =  confHelper.addValoriRottaToDati(TipoOperazione.ADD, nome, tipo, tiporotta,
						registrorotta,  registriList,
						registriListLabel, dati, "","",
						tipiSoggettiLabel,tipiSoggettiLabelPerProtocollo);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_ROTTE_ROUTING, 
						ForwardParams.ADD());
			}

			// Inserisco il routing nel db
			RoutingTable rt = confCore.getRoutingTable();
			RoutingTableDestinazione rtd = new RoutingTableDestinazione();
			rtd.setNome(nome);
			rtd.setTipo(tipo);
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
			rtd.addRoute(tmpR);
			rt.addDestinazione(rtd);

			confCore.performUpdateOperation(userLogin, confHelper.smista(), rt);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			List<RoutingTableDestinazione> lista = confCore.routingList(ricerca);

			confHelper.prepareRoutingList(ricerca, lista);

			pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TABELLA_ROUTING_MODIFICATA_CON_SUCCESSO);
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_ROTTE_ROUTING,
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_ROTTE_ROUTING, ForwardParams.ADD());
		}
	}


}
