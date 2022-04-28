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


package org.openspcoop2.web.ctrlstat.servlet.soggetti;

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
import org.openspcoop2.core.registry.Proprieta;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * SoggettiProprietaDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class SoggettiProprietaDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

 

		try {
			SoggettiHelper soggettiHelper = new SoggettiHelper(request, pd, session);
			soggettiHelper.makeMenu();
			
			String id = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID);
			int idSogg = Integer.parseInt(id);
			String nomeprov = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME);
			String tipoprov = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO);
			String objToRemove = soggettiHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			
			SoggettiCore soggettiCore = new SoggettiCore();
			
			Soggetto soggettoRegistry = null;
			org.openspcoop2.core.config.Soggetto soggettoConfig = null;
			
			if(soggettiCore.isRegistroServiziLocale()){
				soggettoRegistry = soggettiCore.getSoggettoRegistro(idSogg);// core.getSoggettoRegistro(new
				// IDSoggetto(tipoprov,nomeprov));
			}

			soggettoConfig = soggettiCore.getSoggetto(idSogg);// core.getSoggetto(new
			
			String nome = "";

			for (int i = 0; i < idsToRemove.size(); i++) {

				// DataElement de = (DataElement) ((Vector<?>) pdold.getDati()
				// .elementAt(idToRemove[i])).elementAt(0);
				// nome = de.getValue();
				nome = idsToRemove.get(i);

				for (int j = 0; j < soggettoRegistry.sizeProprietaList(); j++) {
					Proprieta ssp = soggettoRegistry.getProprieta(j);
					if (nome.equals(ssp.getNome())) {
						soggettoRegistry.removeProprieta(j);
						break;
					}
				}
			}
			
			SoggettoCtrlStat sog = new SoggettoCtrlStat(soggettoRegistry, soggettoConfig);
			sog.setOldNomeForUpdate(nomeprov);
			sog.setOldTipoForUpdate(tipoprov);

			String userLogin = ServletUtils.getUserLoginFromSession(session);
			
			// update
			List<Object> listOggettiDaAggiornare = SoggettiUtilities.getOggettiDaAggiornare(soggettiCore, nomeprov, nomeprov, tipoprov, tipoprov, sog);
			soggettiCore.performUpdateOperation(userLogin, soggettiHelper.smista(), listOggettiDaAggiornare.toArray());
			
			soggettoRegistry = soggettiCore.getSoggettoRegistro(idSogg);
			

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(request, session, Search.class);

			int idLista = Liste.SOGGETTI_PROP;

			ricerca = soggettiHelper.checkSearchParameters(idLista, ricerca);

			List<Proprieta> lista = soggettiCore.soggettiProprietaList(Integer.parseInt(id), ricerca);

			soggettiHelper.prepareSoggettiProprietaList(soggettoRegistry, id, ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI_PROPRIETA, 
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					SoggettiCostanti.OBJECT_NAME_SOGGETTI_PROPRIETA,
					ForwardParams.DEL());
		}  
	}
}
