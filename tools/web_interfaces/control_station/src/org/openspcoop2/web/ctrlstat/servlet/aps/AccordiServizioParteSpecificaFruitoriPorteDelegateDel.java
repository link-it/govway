/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * AccordiServizioParteSpecificaFruitoriPorteDelegateDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaFruitoriPorteDelegateDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Salvo il vecchio PageData
		PageData pdold = ServletUtils.getPageDataFromSession(session);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			/*
			 * Validate the request parameters specified by the user Note: Basic
			 * field validation done in porteDomForm.java Business logic
			 * validation done in porteDomAdd.java
			 */
			String idServizio = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			Long idS = Long.parseLong(idServizio);
			
			String idFruizione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID);
			Long idFru = Long.parseLong(idFruizione);
			
			String idSoggFruitoreDelServizio = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO);
			@SuppressWarnings("unused")
			Long idSoggFru = Long.parseLong(idSoggFruitoreDelServizio);
			
			String objToRemove = apsHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			// gli id contenuti nell'array sono i nomi delle porte applicative
			// da rimuovere
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			// prendo l'id del soggetto erogatore lo propago
			// lo metto nel pd come campo hidden
			// PageData oldPD = (PageData) session.getAttribute("PageData");
			pd.setHidden(pdold.getHidden());

			// Preparo il menu
			apsHelper.makeMenu();

			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apsCore);
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
			
			// Elimino le porte applicative del servizio dal db
			// StringTokenizer objTok = new StringTokenizer(objToRemove, ",");
			// int[] idToRemove = new int[objTok.countTokens()];
			//
			// int k = 0;
			// while (objTok.hasMoreElements()) {
			// idToRemove[k++] = Integer.parseInt(objTok.nextToken());
			// }

			// Prendo l'id del soggetto erogatore del servizio
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idS);
			IDServizio idServizioFromAccordo = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
			IDSoggetto idSoggettoFruitore = new IDSoggetto();
			String tipoSoggettoFruitore = null;
			String nomeSoggettoFruitore = null;
			if(apsCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggettoFruitore = soggettiCore.getSoggettoRegistro(Integer.parseInt(idSoggFruitoreDelServizio));
				tipoSoggettoFruitore = soggettoFruitore.getTipo();
				nomeSoggettoFruitore = soggettoFruitore.getNome();
			}else{
				org.openspcoop2.core.config.Soggetto soggettoFruitore = soggettiCore.getSoggetto(Integer.parseInt(idSoggFruitoreDelServizio));
				tipoSoggettoFruitore = soggettoFruitore.getTipo();
				nomeSoggettoFruitore = soggettoFruitore.getNome();
			}
			idSoggettoFruitore.setTipo(tipoSoggettoFruitore);
			idSoggettoFruitore.setNome(nomeSoggettoFruitore);
			
			List<Object> listaOggettiDaModificare = new ArrayList<Object>();
			Fruitore fruitore = null;
			for (Fruitore fruitoreCheck : asps.getFruitoreList()) {
				if(fruitoreCheck.getTipo().equals(tipoSoggettoFruitore) && fruitoreCheck.getNome().equals(nomeSoggettoFruitore)) {
					fruitore = fruitoreCheck;
					break;
				}
			}
			
			boolean updateASPS = false;
			
			String superUser   = ServletUtils.getUserLoginFromSession(session);
			String errMsg = null;
			for (int i = 0; i < idsToRemove.size(); i++) {
				
				List<Object> listaOggettiDaEliminare = new ArrayList<Object>();
				
				// ricevo come parametro l'id della pd associata al mapping da cancellare
				IDPortaDelegata idPortaDelegata = new IDPortaDelegata();
				idPortaDelegata.setNome(idsToRemove.get(i));
				// Prendo la porta delegata
				PortaDelegata tmpPD = porteDelegateCore.getPortaDelegata(idPortaDelegata);
				
				// controllo se il mapping e' di default, se lo e' salto questo elemento
				boolean isDefault = apsCore.isDefaultMappingFruizione(idServizioFromAccordo, idSoggettoFruitore, idPortaDelegata );
				
				if(!isDefault) {
					//cancello il mapping
					MappingFruizionePortaDelegata mappingFruizione = new MappingFruizionePortaDelegata();
					mappingFruizione.setIdFruitore(idSoggettoFruitore);
					mappingFruizione.setIdPortaDelegata(idPortaDelegata);
					mappingFruizione.setIdServizio(idServizioFromAccordo);
					listaOggettiDaEliminare.add(mappingFruizione);
				
					// cancello la porta associata
					listaOggettiDaEliminare.add(tmpPD);
					
					// Elimino entrambi gli oggetti
					apsCore.performDeleteOperation(superUser, apsHelper.smista(), listaOggettiDaEliminare.toArray(new Object[1]));
					
					// Connettore della fruizione
					int index = -1;
					for (int j = 0; j < fruitore.sizeConfigurazioneAzioneList(); j++) {
						ConfigurazioneServizioAzione config = fruitore.getConfigurazioneAzione(j);
						if(config!=null) {
							String azione = tmpPD.getAzione().getAzioneDelegata(0); // prendo la prima
							if(config.getAzioneList().contains(azione)) {
								index = j;
								break;
							}
						}
					}
					if(index>=0) {
						updateASPS = true;
						fruitore.removeConfigurazioneAzione(index);
					}
					
				}else {
					errMsg = AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_IMPOSSIBILE_ELIMINARE_LA_CONFIGURAZIONE_DI_DEFAULT;
				}
				
			}// for
			
			
			if(updateASPS) {
				
				listaOggettiDaModificare.add(asps);
				
			}
			
			if(listaOggettiDaModificare.size()>0) {
				porteDelegateCore.performUpdateOperation(superUser, apsHelper.smista(), listaOggettiDaModificare.toArray());
			}
			
			if(errMsg != null)
				pd.setMessage(errMsg);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.CONFIGURAZIONE_FRUIZIONE;

			ricerca = apsHelper.checkSearchParameters(idLista, ricerca);

			List<MappingFruizionePortaDelegata> lista =
					apsCore.serviziFruitoriMappingList(idFru, idSoggettoFruitore, idServizioFromAccordo, ricerca);
	
			apsHelper.serviziFruitoriMappingList(lista, idServizio, idSoggFruitoreDelServizio, idFruizione, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE, 
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE,
					ForwardParams.DEL());
		}  
	}
}
