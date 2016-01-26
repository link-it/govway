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


package org.openspcoop2.web.ctrlstat.servlet.apc;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
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
 * accordiAllegatiDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComuneAllegatiDel extends Action {

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

		String userLogin = (String) ServletUtils.getUserLoginFromSession(session);


		try {
			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);

			String objToRemove = request.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			
			String tipoAccordo = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;
			
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
			ArchiviCore archiviCore = new ArchiviCore(apcCore);

			// Preparo il menu
			apcHelper.makeMenu();

			AccordoServizioParteComune as = null;
			
			for (int i = 0; i < idsToRemove.size(); i++) {
				long idAllegato = Long.parseLong(idsToRemove.get(i));
				
				Documento doc = archiviCore.getDocumento(idAllegato, false);
				
				if(as==null)
					as = apcCore.getAccordoServizio(doc.getIdProprietarioDocumento());
				
				switch (RuoliDocumento.valueOf(doc.getRuolo())) {
					case allegato:
						//rimuovo il vecchio doc dalla lista
						for (int j = 0; j < as.sizeAllegatoList(); j++) {
							Documento documento = as.getAllegato(j);						
							if(documento.getFile().equals(doc.getFile()))
								as.removeAllegato(j);
						}
						
						break;

					case specificaSemiformale:
						
						for (int j = 0; j < as.sizeSpecificaSemiformaleList(); j++) {
							Documento documento = as.getSpecificaSemiformale(j);						
							if(documento.getFile().equals(doc.getFile()))
								as.removeSpecificaSemiformale(j);
						}
						break;
					
					case specificaCoordinamento:
						if(as.getServizioComposto()!=null){
							for (int j = 0; j < as.getServizioComposto().sizeSpecificaCoordinamentoList(); j++) {
								Documento documento = as.getServizioComposto().getSpecificaCoordinamento(j);						
								if(documento.getFile().equals(doc.getFile()))
									as.getServizioComposto().removeSpecificaCoordinamento(j);
							}
						}
						break;
					case specificaLivelloServizio:
						break;
					case specificaSicurezza:
						break;
				}
				
			}

			// effettuo le operazioni
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			List<Documento> lista = apcCore.accordiAllegatiList(as.getId().intValue(), ricerca);

			apcHelper.prepareAccordiAllegatiList(as, ricerca, lista,tipoAccordo);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_ALLEGATI, ForwardParams.DEL());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_ALLEGATI, ForwardParams.DEL());
		}
	}
}
