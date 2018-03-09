/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
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

		// Salvo il vecchio PageData
		// PageData pdold = (PageData) session.getAttribute("PageData");

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
			String objToRemove = porteDelegateHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			// Elimino il servizioApplicativo della porta applicativa dal db
			// StringTokenizer objTok = new StringTokenizer(objToRemove, ",");
			// int[] idToRemove = new int[objTok.countTokens()];
			//
			// int k = 0;
			// while (objTok.hasMoreElements()) {
			// idToRemove[k++] = Integer.parseInt(objTok.nextToken());
			// }

			String azione = "";

			// Prendo la porta applicativa
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			AccordiServizioParteSpecificaCore aspsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);
			PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			String nomePorta = portaDelegata.getNome();

			ConfigurazioneServizioAzione configAzioni = null; 
			boolean updateASPS = false;
			Long idAspsLong = Long.parseLong(idAsps);
			AccordoServizioParteSpecifica asps = aspsCore.getAccordoServizioParteSpecifica(idAspsLong);
			
			String azioneGiaEsistente = portaDelegata.getAzione().getAzioneDelegata(0); // prendo la prima
			
			Fruitore fruitore = null;
			for (Fruitore fruitoreCheck : asps.getFruitoreList()) {
				if(fruitoreCheck.getTipo().equals(portaDelegata.getTipoSoggettoProprietario()) && fruitoreCheck.getNome().equals(portaDelegata.getNomeSoggettoProprietario())) {
					fruitore = fruitoreCheck;
					break;
				}
			}
			for (int j = 0; j < fruitore.sizeConfigurazioneAzioneList(); j++) {
				ConfigurazioneServizioAzione config = fruitore.getConfigurazioneAzione(j);
				if(config!=null) {
					if(config.getAzioneList().contains(azioneGiaEsistente)) {
						configAzioni = config;
						break;
					}
				}
			}
			
			for (int i = 0; i < idsToRemove.size(); i++) {

				// DataElement de = (DataElement) ((Vector<?>) pdold.getDati()
				// .elementAt(idToRemove[i])).elementAt(0);
				// servizioApplicativo = de.getValue();
				azione = idsToRemove.get(i);
				
				for (int j = 0; j < portaDelegata.getAzione().sizeAzioneDelegataList(); j++) {
					String azioneDelegata = portaDelegata.getAzione().getAzioneDelegata(j);
					if (azione.equals(azioneDelegata)) {
						portaDelegata.getAzione().removeAzioneDelegata(j);
						break;
					}
				}
				
				if(configAzioni!=null) {
					for (int j = 0; j < configAzioni.sizeAzioneList(); j++) {
						if(configAzioni.getAzione(j).equals(azione)) {
							configAzioni.removeAzione(j);
							updateASPS = true;
							break;
						}
					}
				}

			}
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_AZIONI_CONFIG_DI+
						porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(portaDelegata);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_AZIONI_CONFIG_DI+nomePorta;
			}
			
			// non posso eliminare tutte le azioni
			if(portaDelegata.getAzione().sizeAzioneDelegataList() == 0) {
				pd.setMessage(PorteDelegateCostanti.MESSAGGIO_ERRORE_NON_E_POSSIBILE_ELIMINARE_TUTTE_LE_AZIONI_ASSOCIATE_ALLA_CONFIGURAZIONE); 
			} else {
				
				List<Object> listaOggettiDaModificare = new ArrayList<Object>();
				
				listaOggettiDaModificare.add(portaDelegata);
				
				if(updateASPS) {
					listaOggettiDaModificare.add(asps);
				}
				
				String userLogin = ServletUtils.getUserLoginFromSession(session);
				porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), listaOggettiDaModificare.toArray());
				
			}
			// Preparo il menu
			porteDelegateHelper.makeMenu();
			
			// ricarico la porta dal db
			portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			
			List<String> listaAzioni = portaDelegata.getAzione().getAzioneDelegataList();
			List<Parameter> listaParametriSessione = new ArrayList<>();
			listaParametriSessione.add(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, idPorta));
			listaParametriSessione.add(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg));
			listaParametriSessione.add(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps));
			listaParametriSessione.add(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione));
			
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			porteDelegateHelper.preparePorteAzioneList(listaAzioni, idPorta, parentPD, lstParam, nomePorta, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_AZIONE, 
					listaParametriSessione, labelPerPorta);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_AZIONE, 
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_AZIONE,
					ForwardParams.DEL());
		}  
	}
}
