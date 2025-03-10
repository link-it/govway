/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * ConfigurazioneCanaliDel
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneCanaliDel extends Action {

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

			String objToRemove =confHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE); 
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			ConfigurazioneCore confCore = new ConfigurazioneCore();

			Long id = null;

			// Prendo l'accesso registro
			Configurazione configurazioneGenerale = confCore.getConfigurazioneGenerale();
			CanaliConfigurazione gestioneCanali = configurazioneGenerale.getGestioneCanali();
			
			StringBuilder inUsoMessage = new StringBuilder();
			
			for (int i = 0; i < idsToRemove.size(); i++) {

				id = Long.parseLong(idsToRemove.get(i));

				for (int j = 0; j < gestioneCanali.sizeCanaleList(); j++) {
					CanaleConfigurazione canale = gestioneCanali.getCanale(j); 
					if (canale.getId().longValue() == id.longValue()) {
						if(!canale.isCanaleDefault()) { // canale di default non si puo' eliminare
							// calcolo utilizzo canale 
							//Non sarà possibil eliminare un canale se utilizzato:
							//	- in un nodo (vedi lista successiva)
							//	- assegnato ad una API, ad una erogazione o ad una fruizione
							
							boolean deleteCanale = ConfigurazioneCanaliUtilities.deleteCanale(canale, userLogin, confCore, confHelper, inUsoMessage, 
									org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
							if(deleteCanale) {
								gestioneCanali.removeCanale(j);
							}
						}
						else {
							inUsoMessage.append("Canale '"+canale.getNome()+"' non eliminabile essendo quello di default");
							inUsoMessage.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
							inUsoMessage.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
						}
					}
				}
			}
			
			if (inUsoMessage.length()>0) {
				pd.setMessage(inUsoMessage.toString());
			}

			confCore.performUpdateOperation(userLogin, confHelper.smista(), configurazioneGenerale);

			// Preparo il menu
			confHelper.makeMenu();

			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.CONFIGURAZIONE_CANALI;

			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<CanaleConfigurazione> lista = confCore.canaleConfigurazioneList(ricerca); 
			
			confHelper.prepareCanaleConfigurazioneList(ricerca, lista);
				
			if (inUsoMessage.length() == 0)  {
				pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA_SISTEMA_MODIFICATA_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			}
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CANALI,
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CANALI, ForwardParams.DEL());
		}
	}
}
