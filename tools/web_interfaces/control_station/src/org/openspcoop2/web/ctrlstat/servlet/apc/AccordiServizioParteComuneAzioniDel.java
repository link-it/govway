/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * accordiAzioniDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComuneAzioniDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
		
		try {
			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);
			
			// Preparo il menu
			apcHelper.makeMenu();

			String id = apcHelper.getParametroLong(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			long idAccordoLong = Long.valueOf(id);
			String objToRemove = apcHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			String tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;
			
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(apcCore);
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apcCore);
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apcCore);
			AccordoServizioParteComune as = apcCore.getAccordoServizioFull(idAccordoLong);
			//String nomeacc = as.getNome();
			int totAz = as.sizeAzioneList();
			String nomeaz = "";
			//boolean callBC = false;

			List<IDServizio> idServiziWithAccordo = null;
			try{
				idServiziWithAccordo = apsCore.getIdServiziWithAccordo(idAccordoFactory.getIDAccordoFromAccordo(as),true);
			}catch(DriverRegistroServiziNotFound dNotF){}
			
			String userLogin = ServletUtils.getUserLoginFromSession(session);

			StringBuilder errori = new StringBuilder();
			for (int i = 0; i < idsToRemove.size(); i++) {

				nomeaz = idsToRemove.get(i);

				
				// Controllo che l'azione non sia stata correlata da un'altra azione
				if (apcCore.isAzioneCorrelata(as.getId(), nomeaz, nomeaz)) {
					// non rimuovo in quanto correlata
					ArrayList<String> tmp = new ArrayList<>();
					// cerco le azioni che contengono quella che
					// vorrei eliminare
					// come correlata
					for (int l = 0; l < as.sizeAzioneList(); l++) {
						Azione azc  =as.getAzione(l);
						if (nomeaz.equals(azc.getCorrelata())) {
							// questa operazione ha come correlata
							// quella che voglio cancellare
							tmp.add(azc.getNome());
						}
					}

					// aggiungo alla tabella questa operazione indicando
					// le operazioni che contengono questa come
					// corrrelata
					if(errori.length()>0) {
						errori.append("<BR>");
					}
					errori.append("Azione "+nomeaz+" non rimuovibile poichè perche' correlata ad altre azioni:<BR>" );
					for(int p=0; p<tmp.size();p++){
						errori.append("- "+tmp.get(p)+"<BR>");
					}
						
					continue;
				}
				
				

				// Controllo che l'azione non sia in uso (se esistono servizi, allora poi saranno state create PD o PA)
				if(idServiziWithAccordo!=null && idServiziWithAccordo.size()>0){
				
					// Se esiste un mapping
					List<MappingErogazionePortaApplicativa> lPA = porteApplicativeCore.getMappingConGruppiPerAzione(nomeaz, idServiziWithAccordo);
					if(lPA!=null && !lPA.isEmpty()) {
						if(errori.length()>0) {
							errori.append("<BR>");
						}
						errori.append("Azione "+nomeaz+" non rimuovibile poichè riassegnata in un gruppo dell'erogazione del servizio: <BR>");
						for(int j=0;j<lPA.size();j++){
							errori.append("- "+lPA.get(j).getIdServizio()+" (gruppo: '"+lPA.get(j).getDescrizione()+"')<BR>");
						}
						continue;
					}
					List<MappingFruizionePortaDelegata> lPD = porteDelegateCore.getMappingConGruppiPerAzione(nomeaz, idServiziWithAccordo);
					if(lPD!=null && !lPD.isEmpty()) {
						if(errori.length()>0) {
							errori.append("<BR>");
						}
						errori.append("Azione "+nomeaz+" non rimuovibile poichè riassegnata in un gruppo della fruizione del servizio: <BR>");
						for(int j=0;j<lPD.size();j++){
							errori.append("- "+lPD.get(j).getIdServizio()+" (fruitore: "+lPD.get(j).getIdFruitore()+") (gruppo: '"+lPD.get(j).getDescrizione()+"')<BR>");
						}
						continue;
					}
					
					if(apcCore.isUnicaAzioneInAccordi(nomeaz)){
						
						// Se esiste solo un'azione con tale identificativo, posso effettuare il controllo che non vi siano porteApplicative/porteDelegate esistenti.
						if (porteApplicativeCore.existsPortaApplicativaAzione(nomeaz)) {
							List<IDPortaApplicativa> idPAs = porteApplicativeCore.getPortaApplicativaAzione(nomeaz);
							if(errori.length()>0) {
								errori.append("<BR>");
							}
							errori.append("Azione "+nomeaz+" non rimuovibile poichè in uso in porte applicative: <BR>");
							for(int j=0;j<idPAs.size();j++){
								errori.append("- "+idPAs.get(j).toString()+"<BR>");
							}
							continue;
						}
						if (porteDelegateCore.existsPortaDelegataAzione(nomeaz)) {
							List<IDPortaDelegata> idPDs = porteDelegateCore.getPortaDelegataAzione(nomeaz);
							if(errori.length()>0) {
								errori.append("<BR>");
							}
							errori.append("Azione "+nomeaz+" non rimuovibile poichè in uso in porte delegate: <BR>");
							for(int j=0;j<idPDs.size();j++){
								errori.append("- "+idPDs.get(j).toString()+"<BR>");
							}
							continue;
						}
						
					}else{
						
						// Se esiste piu' di un'azione con tale identificativo, non posso effettuare il controllo che non vi siano porteApplicative/porteDelegate esistenti,
						// poichè non saprei se l'azione di una PD/PA si riferisce all'azione in questione.
						// Allora non permetto l'eliminazione poichè esistono dei servizi che implementano l'accordo
						if(errori.length()>0) {
							errori.append("<BR>");
						}
						errori.append("Azione "+nomeaz+" non rimuovibile poichè la API viene implementata dai seguenti servizi: <br>");
						for(int j=0; j<idServiziWithAccordo.size();j++){
							errori.append("- "+idServiziWithAccordo.get(j).toString()+"<br>");
						}
						continue;
						
					}
					
				}

				
				// effettuo eliminazione
				//callBC = true;

				for (int j = 0; j < as.sizeAzioneList(); j++) {
					Azione az = as.getAzione(i);
					if (nomeaz.equals(az.getNome())) {
						as.removeAzione(j);
						totAz = totAz - 1;
						break;
					}
				}
			}

			// controllo se non sono rimaste azioni
			// setto in accordi il campo utilizzo_senza_azione
			boolean modificaUtilizzoSenzaAzione = false;
			if (totAz == 0)
				modificaUtilizzoSenzaAzione = true;
			if (modificaUtilizzoSenzaAzione)
				as.setUtilizzoSenzaAzione(true);

			// effettuo le operazioni
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);


			// imposto msg di errore se presente
			if(errori.length()>0)
				pd.setMessage(errori.toString());

			// devo aggiornare la lista dei servizi(serviziCorrelati) che
			// implementano l'accordo a cui e' stata aggiunta l'azione
			// basta fare un update del servizio per attivare le operazioni
			// necessarie all'aggiornamento
			List<AccordoServizioParteSpecifica> listaServizi = apsCore.serviziWithIdAccordoList(idAccordoLong);
			for (AccordoServizioParteSpecifica servizio : listaServizi) {
				apcCore.performUpdateOperation(userLogin, apcHelper.smista(), servizio);
			}

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
	

			int idLista = Liste.ACCORDI_AZIONI;

			ricerca = apcHelper.checkSearchParameters(idLista, ricerca);
			List<Azione> lista = apcCore.accordiAzioniList(idAccordoLong, ricerca);
			apcHelper.prepareAccordiAzioniList(as, lista, ricerca, id, tipoAccordo);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			return ServletUtils.getStrutsForward(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_AZIONI, ForwardParams.DEL());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_AZIONI, ForwardParams.DEL());
		} 
	}
}
