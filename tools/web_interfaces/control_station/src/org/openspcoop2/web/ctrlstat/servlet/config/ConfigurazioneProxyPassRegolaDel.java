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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazione;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * ConfigurazioneProxyPassRegolaDel
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneProxyPassRegolaDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Salvo il vecchio PageData
		// PageData pdold = (PageData) session.getAttribute("PageData");

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	

		try {
			ConfigurazioneHelper confHelper = new ConfigurazioneHelper(request, pd, session);

			String objToRemove =confHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE); 
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			ConfigurazioneCore confCore = new ConfigurazioneCore();

			Long id = null;

			// Prendo l'accesso registro
			Configurazione configurazioneGenerale = confCore.getConfigurazioneGenerale();
			ConfigurazioneUrlInvocazione urlInvocazione = configurazioneGenerale.getUrlInvocazione();
			List<ConfigurazioneUrlInvocazioneRegola> regoleDaEliminare = new ArrayList<ConfigurazioneUrlInvocazioneRegola>();
			
			for (int i = 0; i < idsToRemove.size(); i++) {

				id = Long.parseLong(idsToRemove.get(i));

				for (int j = 0; j < urlInvocazione.sizeRegolaList(); j++) {
					ConfigurazioneUrlInvocazioneRegola regola = urlInvocazione.getRegola(j); 
					if (regola.getId().longValue() == id.longValue()) {
						regoleDaEliminare.add(urlInvocazione.removeRegola(j));
					}
				}
			}

			//confCore.performUpdateOperation(userLogin, confHelper.smista(), configurazioneGenerale);
			if(!regoleDaEliminare.isEmpty()) {
				confCore.performDeleteOperation(userLogin, confHelper.smista(), regoleDaEliminare.toArray(new Object[1]) );
			}

			// Preparo il menu
			confHelper.makeMenu();

			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(request, session, Search.class);

			int idLista = Liste.CONFIGURAZIONE_PROXY_PASS_REGOLA;

			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<ConfigurazioneUrlInvocazioneRegola> lista = confCore.proxyPassConfigurazioneRegolaList(ricerca); 
			
			confHelper.prepareProxyPassConfigurazioneRegolaList(ricerca, lista);
						
			pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA_SISTEMA_MODIFICATA_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PROXY_PASS_REGOLA,
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PROXY_PASS_REGOLA, ForwardParams.DEL());
		}
	}
}
