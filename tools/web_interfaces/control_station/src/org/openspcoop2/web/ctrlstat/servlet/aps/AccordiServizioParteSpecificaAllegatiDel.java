/*
 * OpenSPCoop - Customizable API Gateway 
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


package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * serviziAllegatiDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaAllegatiDel extends Action {

	@SuppressWarnings("incomplete-switch")
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
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);

			String objToRemove = request.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			ArchiviCore archiviCore = new ArchiviCore(apsCore);

			// Preparo il menu
			apsHelper.makeMenu();

			AccordoServizioParteSpecifica asps = null;

			for (int i = 0; i < idsToRemove.size(); i++) {
				long idAllegato = Long.parseLong(idsToRemove.get(i));

				Documento doc = archiviCore.getDocumento(idAllegato, false);

				if(asps==null)
					asps = apsCore.getAccordoServizioParteSpecifica(doc.getIdProprietarioDocumento());

				switch (RuoliDocumento.valueOf(doc.getRuolo())) {
				case allegato:
					//rimuovo il vecchio doc dalla lista
					for (int j = 0; j < asps.sizeAllegatoList(); j++) {
						Documento documento = asps.getAllegato(j);						
						if(documento.getFile().equals(doc.getFile())){
							asps.removeAllegato(j);
							break;
						}
					}

					break;

				case specificaSemiformale:

					for (int j = 0; j < asps.sizeSpecificaSemiformaleList(); j++) {
						Documento documento = asps.getSpecificaSemiformale(j);						
						if(documento.getFile().equals(doc.getFile())){
							asps.removeSpecificaSemiformale(j);
							break;
						}
					}
					break;
				case specificaSicurezza:
					for (int j = 0; j < asps.sizeSpecificaSicurezzaList(); j++) {
						Documento documento = asps.getSpecificaSicurezza(j);						
						if(documento.getFile().equals(doc.getFile())){
							asps.removeSpecificaSicurezza(j);
							break;
						}
					}
					break;

				case specificaLivelloServizio:
					for (int j = 0; j < asps.sizeSpecificaLivelloServizioList(); j++) {
						Documento documento = asps.getSpecificaLivelloServizio(j);						
						if(documento.getFile().equals(doc.getFile())){
							asps.removeSpecificaLivelloServizio(j);
							break;
						}
					}
					break;
				}

			}

			// effettuo le operazioni
			apsCore.performUpdateOperation(userLogin, apsHelper.smista(), asps);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			List<Documento> lista = apsCore.serviziAllegatiList(asps.getId().intValue(), ricerca);

			apsHelper.prepareServiziAllegatiList(asps, ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_ALLEGATI, 
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_ALLEGATI,
					ForwardParams.DEL());
		}  
	}
}
