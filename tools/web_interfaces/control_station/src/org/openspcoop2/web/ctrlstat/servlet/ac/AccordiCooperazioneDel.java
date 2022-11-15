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


package org.openspcoop2.web.ctrlstat.servlet.ac;

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
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
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
 * accordiCooperazioneDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiCooperazioneDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);

		IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
		
		try {
			AccordiCooperazioneHelper acHelper = new AccordiCooperazioneHelper(request, pd, session);

			String objToRemove = acHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			AccordiCooperazioneCore acCore = new AccordiCooperazioneCore();

			// Preparo il menu
			acHelper.makeMenu();

			// Elimino gli accordi dal db
			// StringTokenizer objTok = new StringTokenizer(objToRemove, ",");
			// int[] idToRemove = new int[objTok.countTokens()];
			//
			// int k = 0;
			// while (objTok.hasMoreElements()) {
			// idToRemove[k++] = Integer.parseInt(objTok.nextToken());
			// }
			String msg = "";
			boolean isInUso = false;
			
			for (int i = 0; i < idsToRemove.size(); i++) {

				// DataElement de = (DataElement) ((Vector<?>) pdold.getDati()
				// .elementAt(idToRemove[i])).elementAt(0);
				// nome = de.getValue();
				String id = idsToRemove.get(i);
				int idAccordo = Integer.parseInt(id);
				String tipoSICA = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_TIPO_SICA);
				if("".equals(tipoSICA))
					tipoSICA = null;

				HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
				
				AccordoCooperazione ac = acCore.getAccordoCooperazione(idAccordo);
				IDAccordoCooperazione idAccordoCooperazione = idAccordoCooperazioneFactory.getIDAccordoFromAccordo(ac);

				boolean normalizeObjectIds = !acHelper.isModalitaCompleta();
				if (acCore.isAccordoCooperazioneInUso(ac, whereIsInUso, normalizeObjectIds)) {// accordo in uso
					isInUso = true;
					msg += DBOggettiInUsoUtils.toString(idAccordoCooperazione, whereIsInUso, true, org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE, normalizeObjectIds);
					msg += org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE;
				} else {// accordo non in uso
					acCore.performDeleteOperation(userLogin, acHelper.smista(), ac);
				}
				
			}

			if (isInUso) {
				pd.setMessage(msg);
			}
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
			
			List<AccordoCooperazione> lista = null;
			if(acCore.isVisioneOggettiGlobale(userLogin)){
				lista = acCore.accordiCooperazioneList(null, ricerca);
			}else{
				lista = acCore.accordiCooperazioneList(userLogin, ricerca);
			}

			acHelper.prepareAccordiCooperazioneList(lista, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			return ServletUtils.getStrutsForward(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE , ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE, 
					ForwardParams.DEL());
		}  
	}
}
