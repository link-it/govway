/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.id.IDGenericProperties;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
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
 * ConfigurazionePolicyGestioneTokenDel
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazionePolicyGestioneTokenDel extends Action {

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

			ConfigurazioneCore confCore = new ConfigurazioneCore();

			// Preparo il menu
			confHelper.makeMenu();

			String infoType = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE);
			if(infoType==null) {
				infoType = ServletUtils.getObjectFromSession(request, session, String.class, ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE);
			}
			boolean attributeAuthority = ConfigurazioneCostanti.isConfigurazioneAttributeAuthority(infoType);
			
			String objToRemove =confHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE); 

			// Elimino i filtri dal db
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);

			StringBuilder inUsoMessage = new StringBuilder();
			HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
			boolean normalizeObjectIds = !confHelper.isModalitaCompleta();
			for (int i = 0; i < idsToRemove.size(); i++) {

				long idGenericProperties = Long.parseLong(idsToRemove.get(i));
				GenericProperties policy = confCore.getGenericProperties(idGenericProperties);
				
				IDGenericProperties idGP = new IDGenericProperties();
				idGP.setNome(policy.getNome());
				idGP.setTipologia(policy.getTipologia());

				boolean gpInUso = confCore.isGenericPropertiesInUso(idGP,whereIsInUso,normalizeObjectIds);

				if (gpInUso) {
					inUsoMessage.append(DBOggettiInUsoUtils.toString(idGP, whereIsInUso, true, org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE));
					inUsoMessage.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);

				} else {
					
					if(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_VALIDATION.equals(policy.getTipologia()) && confCore.isPolicyGestioneTokenPDND(policy.getNome())) {
						String object = "Token Policy";
						String intestazione = " non eliminabile perch&egrave; :";
						String separator =  org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE;
						String msg = object+" '"+idGP.getNome()+"'" + intestazione+separator;
						inUsoMessage.append(msg+" definisce la policy di validazione dei token ottenuti dalla PDND");
						inUsoMessage.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
					}
					else {
						confCore.performDeleteOperation(userLogin, confHelper.smista(), policy);
					}
				}

			}// chiudo for

			if (inUsoMessage.length()>0) {
				pd.setMessage(inUsoMessage.toString());
			}
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = attributeAuthority ? Liste.CONFIGURAZIONE_GESTIONE_ATTRIBUTE_AUTHORITY : Liste.CONFIGURAZIONE_GESTIONE_POLICY_TOKEN;
			
			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<String> tipologie = new ArrayList<>();
			if(attributeAuthority) {
				tipologie.add(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_ATTRIBUTE_AUTHORITY);
			}
			else {
				tipologie.add(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_GESTIONE_POLICY_TOKEN);
				tipologie.add(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_RETRIEVE_POLICY_TOKEN);
			}
			
			List<GenericProperties> lista = confCore.gestorePolicyTokenList(idLista, tipologie, ricerca);
			
			confHelper.prepareGestorePolicyTokenList(ricerca, lista, idLista); 

			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN, ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN, ForwardParams.DEL());
		}  
	}
}
