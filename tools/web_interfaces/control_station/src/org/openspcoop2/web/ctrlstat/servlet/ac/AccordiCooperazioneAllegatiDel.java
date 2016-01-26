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


package org.openspcoop2.web.ctrlstat.servlet.ac;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * accordiCoopAllegatiDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiCooperazioneAllegatiDel extends Action {

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
			AccordiCooperazioneHelper acHelper = new AccordiCooperazioneHelper(request, pd, session);

			String objToRemove = request.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			String tipoSICA = request.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_TIPO_SICA);
			if("".equals(tipoSICA))
				tipoSICA = null;
			
			AccordiCooperazioneCore acCore = new AccordiCooperazioneCore();
			ArchiviCore archiviCore =  new ArchiviCore(acCore);

			// Preparo il menu
			acHelper.makeMenu();

			AccordoCooperazione ac = null;
			
			for (int i = 0; i < idsToRemove.size(); i++) {
				long idAllegato = Long.parseLong(idsToRemove.get(i));
				
				Documento doc = archiviCore.getDocumento(idAllegato, false);
				
				if(ac==null)
					ac = acCore.getAccordoCooperazione(doc.getIdProprietarioDocumento());
				
				switch (RuoliDocumento.valueOf(doc.getRuolo())) {
					case allegato:
						//rimuovo il vecchio doc dalla lista
						for (int j = 0; j < ac.sizeAllegatoList(); j++) {
							Documento documento = ac.getAllegato(j);						
							if(documento.getFile().equals(doc.getFile()))
								ac.removeAllegato(j);
						}
						
						break;

					case specificaSemiformale:
						
						for (int j = 0; j < ac.sizeSpecificaSemiformaleList(); j++) {
							Documento documento = ac.getSpecificaSemiformale(j);						
							if(documento.getFile().equals(doc.getFile()))
								ac.removeSpecificaSemiformale(j);
						}
						break;
					
					case specificaCoordinamento:
						break;
					case specificaLivelloServizio:
						break;
					case specificaSicurezza:
						break;
				}
				
			}

			// effettuo le operazioni
			acCore.performUpdateOperation(userLogin, acHelper.smista(), ac);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			List<Documento> lista = acCore.accordiCoopAllegatiList(ac.getId().intValue(), ricerca);

			acHelper.prepareAccordiCoopAllegatiList(ac, ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			return ServletUtils.getStrutsForward(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_AC_ALLEGATI , ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiCooperazioneCostanti.OBJECT_NAME_AC_ALLEGATI, 
					ForwardParams.DEL());
		}  
	}
}
