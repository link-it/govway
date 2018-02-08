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
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.ResourceResponse;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * AccordiServizioParteComuneResourcesRisposteDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComuneResourcesRisposteDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Salvo il vecchio PageData
		// PageData pdold = (PageData) session.getAttribute("PageData");

		// Inizializzo PageData
		PageData pd = new PageData();

		// Inizializzo GeneralData
		GeneralHelper generalHelper = new GeneralHelper(session);

		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = (String) ServletUtils.getUserLoginFromSession(session);

//		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
		
		try {
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
//			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(apcCore);
//			PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apcCore);
//			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apcCore);
			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);

			String id = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			String nomeRisorsa = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_NOME);
			int idInt = Integer.parseInt(id);
			String objToRemove = apcHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			String tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;

			// Preparo il menu
			apcHelper.makeMenu();

			ArrayList<String> resourcesToRemove = Utilities.parseIdsToRemove(objToRemove);
			AccordoServizioParteComune as = apcCore.getAccordoServizio(new Long(idInt));
			Resource risorsa =  null;
			Long idRisorsa = null;
			for (int j = 0; j < as.sizeResourceList(); j++) {
				risorsa = as.getResource(j);
				if (nomeRisorsa.equals(risorsa.getNome())) {
					idRisorsa = risorsa.getId();
					break;
				}
			}
			
//			List<IDServizio> idServiziWithAccordo = null;
//			try{
//				idServiziWithAccordo = apsCore.getIdServiziWithAccordo(idAccordoFactory.getIDAccordoFromAccordo(as),true);
//			}catch(DriverRegistroServiziNotFound dNotF){}
//			
			String responseStatus = "";
			boolean modificaAS_effettuata = false;
//			boolean checkPorte = true;
			StringBuffer errori = new StringBuffer();
			for (int i = 0; i < resourcesToRemove.size(); i++) {
				responseStatus = resourcesToRemove.get(i);
				
//				// Controllo che l'azione non sia in uso (se esistono servizi, allora poi saranno state create PD o PA)
//				if(idServiziWithAccordo!=null && idServiziWithAccordo.size()>0){
//				
//					if(checkPorte){
//						
//						// Se esiste solo un'azione con tale identificativo, posso effettuare il controllo che non vi siano PA/PD esistenti.
//						if (porteApplicativeCore.existsPortaApplicativaAzione(responseStatus)) {
//							List<IDPortaApplicativa> idPAs = porteApplicativeCore.getPortaApplicativaAzione(responseStatus);
//							errori.append("Risorsa "+responseStatus+" non rimuovibile poiche' in uso in porte applicative: <BR>");
//							for(int j=0;j<idPAs.size();j++){
//								errori.append("- "+idPAs.get(j).toString()+"<BR>");
//							}
//							continue;
//						}
//						if (porteDelegateCore.existsPortaDelegataAzione(responseStatus)) {
//							List<IDPortaDelegata> idPDs = porteDelegateCore.getPortaDelegataAzione(responseStatus);
//							errori.append("Risorsa "+responseStatus+" non rimuovibile poiche' in uso in porte delegate: <BR>");
//							for(int j=0;j<idPDs.size();j++){
//								errori.append("- "+idPDs.get(j).toString()+"<BR>");
//							}
//							continue;
//						}
//						
//					}else{
//						
//						// Se esiste piu' di un'azione con tale identificativo, non posso effettuare il controllo che non vi siano PA/PD esistenti,
//						// poiche' non saprei se l'azione di una PD/PA si riferisce all'azione in questione.
//						// Allora non permetto l'eliminazione poiche' esistono dei servizi che implementano l'accordo
//						errori.append("Risorsa "+responseStatus+" non rimuovibile poiche' l'accordo di servizio parte comune viene implementato dai seguenti servizi: <br>");
//						for(int j=0; j<idServiziWithAccordo.size();j++){
//							errori.append("- "+idServiziWithAccordo.get(j).toString()+"<br>");
//						}
//						continue;
//						
//					}
//					
//				}
//				
				// Effettuo eliminazione
				for (int j = 0; j < risorsa.sizeResponseList(); j++) {
					ResourceResponse resp = risorsa.getResponse(j);
					if(Integer.parseInt(responseStatus) == resp.getStatus()){
						modificaAS_effettuata = true;
						risorsa.removeResponse(j);
						break;
					}
				}
			}
			
			// imposto msg di errore se presente
			if(errori.length()>0)
				pd.setMessage(errori.toString());

			// effettuo le operazioni
			if(modificaAS_effettuata)
				apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			List<ResourceResponse> lista = apcCore.accordiResourceResponseList(idRisorsa.intValue(), ricerca);

			// Devo rileggere l'accordo dal db, perche' altrimenti
			// manca l'id dei port-type
			as = apcCore.getAccordoServizio(new Long(idInt));

			apcHelper.prepareAccordiResourcesResponseList(ricerca, lista, id, as, tipoAccordo, nomeRisorsa);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			return ServletUtils.getStrutsForward(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_RESOURCES_RISPOSTE, ForwardParams.DEL());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_RESOURCES_RISPOSTE, ForwardParams.DEL());
		} 
	}
}
