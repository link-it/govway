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

import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.RoutingTableDestinazione;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * routingDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneRouteDel extends Action {

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

		try {
			ConfigurazioneHelper confHelper = new ConfigurazioneHelper(request, pd, session);

			String objToRemove =request.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE); 

			ConfigurazioneCore confCore = new ConfigurazioneCore();

			// Elimino i routing dal db
			StringTokenizer objTok = new StringTokenizer(objToRemove, ",");
			int[] idToRemove = new int[objTok.countTokens()];

			int k = 0;
			while (objTok.hasMoreElements()) {
				idToRemove[k++] = Integer.parseInt(objTok.nextToken());
			}

			String tipo = "", nome = "", tipoNome;

			// Prendo la routing table
			RoutingTable rt = confCore.getRoutingTable();

			for (int i = 0; i < idToRemove.length; i++) {

				DataElement de = (DataElement) ((Vector<?>) pdold.getDati().elementAt(idToRemove[i])).elementAt(0);
				tipoNome = de.getValue();
				//de = (DataElement) ((Vector<?>) pdold.getDati().elementAt(idToRemove[i])).elementAt(1);
				//tipologia = de.getValue();

				if(tipoNome.contains("/")){
					tipo = tipoNome.split("/")[0];
					nome = tipoNome.split("/")[1];
				}

				for (int j = 0; j < rt.sizeDestinazioneList(); j++) {
					RoutingTableDestinazione rtd = rt.getDestinazione(j);
					if (nome.equals(rtd.getNome()) && tipo.equals(rtd.getTipo())) {
						rt.removeDestinazione(j);
						break;
					}
				}
			}

			confCore.performUpdateOperation(userLogin, confHelper.smista(), rt);

			// Preparo il menu
			confHelper.makeMenu();

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			List<RoutingTableDestinazione> lista = confCore.routingList(ricerca);

			confHelper.prepareRoutingList(ricerca, lista);

			pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TABELLA_ROUTING_MODIFICATA_CON_SUCCESSO);
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_ROTTE_ROUTING,
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_ROTTE_ROUTING, ForwardParams.DEL());
		}
	}
}
