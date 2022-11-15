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


package org.openspcoop2.web.ctrlstat.servlet.pd;

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
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * PorteDelegateAzioneDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteDelegateAzioneDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session, request);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
			String idPorta = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(idPorta);
			String idsogg = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			String idFruizione = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			String objToRemove = porteDelegateHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);

			// Prendo la porta applicativa
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteDelegateCore);
			AccordiServizioParteSpecificaCore aspsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);
			
			PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			String nomePorta = portaDelegata.getNome();

			Long idAspsLong = Long.parseLong(idAsps);
			AccordoServizioParteSpecifica asps = aspsCore.getAccordoServizioParteSpecifica(idAspsLong);
			AccordoServizioParteComuneSintetico aspc = apcCore.getAccordoServizioSintetico(IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
			ServiceBinding serviceBinding = apcCore.toMessageServiceBinding(aspc.getServiceBinding());
			
			List<String> azioni = new ArrayList<>();
			for (int i = 0; i < idsToRemove.size(); i++) {

				String azione = idsToRemove.get(i);
				azioni.add(azione);
				
			}
			
			StringBuilder inUsoMessage = new StringBuilder();
			
			String userLogin = ServletUtils.getUserLoginFromSession(porteDelegateHelper.getSession());
			
			PorteDelegateUtilities.deletePortaDelegataAzioni(portaDelegata, asps, porteDelegateCore, porteDelegateHelper, inUsoMessage, azioni, userLogin);
			
			// imposto msg di errore se presente
			if (inUsoMessage.length()>0) {
				pd.setMessage(inUsoMessage.toString());
			}
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						porteDelegateHelper.getLabelAzioniDi(serviceBinding),
						porteDelegateHelper.getLabelAzioni(serviceBinding),
						portaDelegata);
			}
			else {
				labelPerPorta = porteDelegateHelper.getLabelAzioniDi(serviceBinding)+nomePorta;
			}
			
			// Preparo il menu
			porteDelegateHelper.makeMenu();
			
			// ricarico la porta dal db
			portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			
			List<String> listaAzioni = portaDelegata.getAzione().getAzioneDelegataList();
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
			int idLista = Liste.PORTE_DELEGATE_AZIONI;
			ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
			
			List<Parameter> listaParametriSessione = new ArrayList<>();
			listaParametriSessione.add(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, idPorta));
			listaParametriSessione.add(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg));
			listaParametriSessione.add(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps));
			listaParametriSessione.add(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione));
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			porteDelegateHelper.preparePorteAzioneList(ricerca,
					listaAzioni, idPorta, parentPD, lstParam, nomePorta, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_AZIONE, 
					listaParametriSessione, labelPerPorta, serviceBinding, aspc);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_AZIONE, 
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_AZIONE,
					ForwardParams.DEL());
		}  
	}
}
