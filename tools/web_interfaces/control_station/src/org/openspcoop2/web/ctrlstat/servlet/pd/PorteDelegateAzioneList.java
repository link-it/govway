/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaHelper;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * PorteDelegateAzioneList
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteDelegateAzioneList extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
 
		Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session);
		if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;

		try {
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			String idPorta = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(idPorta);
			String idsogg = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			String idFruizione = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			// Preparo il menu
			porteDelegateHelper.makeMenu();
			
			// Prendo la porta applicativa
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteDelegateCore);
			AccordiServizioParteSpecificaCore aspsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);
			
			PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			String nomePorta = portaDelegata.getNome();
			
			Long idAspsLong = Long.parseLong(idAsps);
			AccordoServizioParteSpecifica asps = aspsCore.getAccordoServizioParteSpecifica(idAspsLong);
			AccordoServizioParteComune aspc = apcCore.getAccordoServizio(IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
			ServiceBinding serviceBinding = apcCore.toMessageServiceBinding(aspc.getServiceBinding());
	
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
			
			MappingFruizionePortaDelegata mappingFruizione = porteDelegateCore.getMappingFruizionePortaDelegata(portaDelegata);
			List<String> listaAzioni = null;
			if(mappingFruizione!=null && mappingFruizione.isDefault()) {
				// calcolo azioni rimaste
				List<IDServizio> listId = new ArrayList<>();
				listId.add(IDServizioFactory.getInstance().getIDServizioFromAccordo(asps));
				List<MappingFruizionePortaDelegata> lista = porteDelegateCore.getMapping(listId, false, true);
				if(lista!=null && !lista.isEmpty()) {
					AccordiServizioParteSpecificaHelper aspsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
					Map<String,String> azioni = porteDelegateCore.getAzioniConLabel(asps, aspc, false, true, new ArrayList<String>());
					List<String> azioniL = new ArrayList<>();
					if(azioni != null && azioni.size() > 0)
						azioniL.addAll(azioni.keySet());
					listaAzioni = aspsHelper.getAllActionsNotRedefinedMappingFruizione(azioniL, lista);
				}
			}
			else {
				listaAzioni = portaDelegata.getAzione().getAzioneDelegataList(); 
			}
			
			List<Parameter> listaParametriSessione = new ArrayList<>();
			listaParametriSessione.add(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, idPorta));
			listaParametriSessione.add(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg));
			listaParametriSessione.add(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps));
			listaParametriSessione.add(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione));
			
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			porteDelegateHelper.preparePorteAzioneList(listaAzioni, idPorta, parentPD, lstParam, nomePorta, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_AZIONE, 
					listaParametriSessione, labelPerPorta, serviceBinding, aspc);
	
			if(mappingFruizione!=null && mappingFruizione.isDefault()) {
				pd.setAddButton(false);
				pd.setRemoveButton(false);
				pd.setSelect(false);
			}
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_AZIONE, 
					ForwardParams.LIST());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_AZIONE,
					ForwardParams.LIST());
		}  
	}
}
