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

import java.util.List;

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
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazione;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * ConfigurazioneProxyPassRegolaList
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneProxyPassRegolaList extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);
		String userLogin = ServletUtils.getUserLoginFromSession(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			ConfigurazioneHelper confHelper = new ConfigurazioneHelper(request, pd, session);

			String cambiaPosizione = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_POSIZIONE);
			String idRegolaS = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_ID_REGOLA);
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();

			// Preparo il menu
			confHelper.makeMenu();
			
			if(StringUtils.isNotEmpty(cambiaPosizione)) {
				
				Configurazione configurazioneGenerale = confCore.getConfigurazioneGenerale();
				ConfigurazioneUrlInvocazione urlInvocazione = configurazioneGenerale.getUrlInvocazione();
				
				if(urlInvocazione != null) {
					long idRegola = Long.parseLong(idRegolaS);
					ConfigurazioneUrlInvocazioneRegola regolaToMove = null;
					for (ConfigurazioneUrlInvocazioneRegola reg : urlInvocazione.getRegolaList()) {
						if(reg.getId().longValue() == idRegola) {
							regolaToMove = reg;
							break;
						}
					}
					
					int posizioneAttuale = regolaToMove.getPosizione();
					int posizioneNuova = cambiaPosizione.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_POSIZIONE_SU) ? (posizioneAttuale - 1) : (posizioneAttuale + 1);
					
					ConfigurazioneUrlInvocazioneRegola regolaToSwitch = null;
					for (ConfigurazioneUrlInvocazioneRegola reg : urlInvocazione.getRegolaList()) {
						if(reg.getPosizione() == posizioneNuova) {
							regolaToSwitch = reg;
							break;
						}
					}
					
					regolaToMove.setPosizione(posizioneNuova);
					regolaToSwitch.setPosizione(posizioneAttuale);
					
					confCore.performUpdateOperation(userLogin, confHelper.smista(), configurazioneGenerale);
					if(CostantiControlStation.VISUALIZZA_MESSAGGIO_CONFERMA_SPOSTAMENTO_REGOLA_PROXY_PASS)
						pd.setMessage(CostantiControlStation.MESSAGGIO_CONFERMA_REGOLA_PROXY_PASS_SPOSTATA_CORRETTAMENTE, MessageType.INFO);
				}
			}

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(request, session, Search.class);

			int idLista = Liste.CONFIGURAZIONE_PROXY_PASS_REGOLA;

			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<ConfigurazioneUrlInvocazioneRegola> lista = confCore.proxyPassConfigurazioneRegolaList(ricerca); 
			
			confHelper.prepareProxyPassConfigurazioneRegolaList(ricerca, lista);
			
			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(request, session, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PROXY_PASS_REGOLA,
					ForwardParams.LIST());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PROXY_PASS_REGOLA, ForwardParams.LIST());
		}
	}
}
