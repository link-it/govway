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
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
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
 * accordiPorttypeOperationsDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComunePortTypeOperationsDel extends Action {

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

		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
		
		try {
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(apcCore);
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apcCore);
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apcCore);
			
			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);

			String id = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			int idInt = Integer.parseInt(id);
			String nomept = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME);
			String objToRemove = apcHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			String tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;

			// Preparo il menu
			apcHelper.makeMenu();

			// Elimino le azioni del port-type dal db
			// StringTokenizer objTok = new StringTokenizer(objToRemove, ",");
			// int[] idToRemove = new int[objTok.countTokens()];
			//
			// int k = 0;
			// while (objTok.hasMoreElements()) {
			// idToRemove[k++] = Integer.parseInt(objTok.nextToken());
			// }
			ArrayList<String> optsToRemove = Utilities.parseIdsToRemove(objToRemove);
			AccordoServizioParteComune as = apcCore.getAccordoServizio(Long.valueOf(idInt));

			// Prendo il port-type
			PortType pt = null;
			for (int i = 0; i < as.sizePortTypeList(); i++) {
				pt = as.getPortType(i);
				if (nomept.equals(pt.getNome()))
					break;
			}

			String nomeop = "";

			List<IDServizio> idServiziWithPortType = null;
			try{
				IDPortType idPT = new IDPortType();
				idPT.setNome(pt.getNome());
				idPT.setIdAccordo(idAccordoFactory.getIDAccordoFromAccordo(as));
				idServiziWithPortType = apsCore.getIdServiziWithPortType(idPT);
			}catch(DriverRegistroServiziNotFound dNotF){}
			
			StringBuffer errori = new StringBuffer();
			for (int i = 0; i < optsToRemove.size(); i++) {

				// DataElement de = (DataElement) ((Vector<?>) pdold.getDati()
				// .elementAt(idToRemove[i])).elementAt(0);
				// nomeop = de.getValue();
				nomeop = optsToRemove.get(i);
				
				
				// Controllo che l'azione non sia stata correlata da un'altra azione
				ArrayList<String> tmp = new ArrayList<String>();
				for (int b = 0; b < as.sizePortTypeList(); b++) {
					PortType ptCheck = as.getPortType(b);
					for (int check = 0; check < ptCheck.sizeAzioneList(); check++) {
						Operation opCheck = ptCheck.getAzione(check);
						if(!ptCheck.getNome().equals(nomept) || !opCheck.getNome().equals(nomeop)){
							// controllo le altre azioni che non siano correlate a quella da eliminare
							if(opCheck.getCorrelata()!=null && opCheck.getCorrelata().equals(nomeop)){
								if(opCheck.getCorrelataServizio()==null && ptCheck.getNome().equals(nomept)){
									tmp.add(opCheck.getNome()+" del servizio "+ptCheck.getNome());
								}
								if(opCheck.getCorrelataServizio()!=null && opCheck.getCorrelataServizio().equals(nomept)){
									tmp.add(opCheck.getNome()+" del servizio "+ptCheck.getNome());
								}
							}
						}
					}
				}
				if(tmp.size()>0){
					if(errori.length()>0) {
						errori.append("<BR>");
					}
					errori.append("Azione "+nomeop+" non rimuovibile poichè perche' correlata ad altre azioni:<BR>" );
					for(int p=0; p<tmp.size();p++){
						errori.append("- "+tmp.get(p)+"<BR>");
					}
					continue;
				}
				
				
				// Controllo che l'azione non sia in uso (se esistono servizi, allora poi saranno state create PD o PA)
				if(idServiziWithPortType!=null && idServiziWithPortType.size()>0){
				
					// Se esiste un mapping
					List<MappingErogazionePortaApplicativa> lPA = porteApplicativeCore.getMappingConGruppiPerAzione(nomeop, idServiziWithPortType);
					if(lPA!=null && !lPA.isEmpty()) {
						if(errori.length()>0) {
							errori.append("<BR>");
						}
						errori.append("Azione "+nomeop+" non rimuovibile poichè riassegnata in un gruppo dell'erogazione del servizio: <BR>");
						for(int j=0;j<lPA.size();j++){
							errori.append("- "+lPA.get(j).getIdServizio()+" (gruppo: '"+lPA.get(j).getDescrizione()+"')<BR>");
						}
						continue;
					}
					List<MappingFruizionePortaDelegata> lPD = porteDelegateCore.getMappingConGruppiPerAzione(nomeop, idServiziWithPortType);
					if(lPD!=null && !lPD.isEmpty()) {
						if(errori.length()>0) {
							errori.append("<BR>");
						}
						errori.append("Azione "+nomeop+" non rimuovibile poichè riassegnata in un gruppo della fruizione del servizio: <BR>");
						for(int j=0;j<lPD.size();j++){
							errori.append("- "+lPD.get(j).getIdServizio()+" (fruitore: "+lPD.get(j).getIdFruitore()+") (gruppo: '"+lPD.get(j).getDescrizione()+"')<BR>");
						}
						continue;
					}
					
					if(apcCore.isUnicaAzioneInAccordi(nomeop)){
						
						// Se esiste solo un'azione con tale identificativo, posso effettuare il controllo che non vi siano porteApplicative/porteDelegate esistenti.
						if (porteApplicativeCore.existsPortaApplicativaAzione(nomeop)) {
							List<IDPortaApplicativa> idPAs = porteApplicativeCore.getPortaApplicativaAzione(nomeop);
							if(errori.length()>0) {
								errori.append("<BR>");
							}
							errori.append("Azione "+nomeop+" non rimuovibile poichè in uso in porte applicative: <BR>");
							for(int j=0;j<idPAs.size();j++){
								errori.append("- "+idPAs.get(j).toString()+"<BR>");
							}
							continue;
						}
						if (porteDelegateCore.existsPortaDelegataAzione(nomeop)) {
							List<IDPortaDelegata> idPDs = porteDelegateCore.getPortaDelegataAzione(nomeop);
							if(errori.length()>0) {
								errori.append("<BR>");
							}
							errori.append("Azione "+nomeop+" non rimuovibile poichè in uso in porte delegate: <BR>");
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
						errori.append("Azione "+nomeop+" non rimuovibile poichè il servizio "+nomept+" della API viene implementato dai seguenti servizi: <br>");
						for(int j=0; j<idServiziWithPortType.size();j++){
							errori.append("- "+idServiziWithPortType.get(j).toString()+"<br>");
						}
						continue;
						
					}
					
				}
				
				
				// effettuo eliminazione
				for (int j = 0; j < pt.sizeAzioneList(); j++) {
					Operation op = pt.getAzione(j);
					if (nomeop.equals(op.getNome())) {
						pt.removeAzione(j);
						break;
					}
				}
			}

			// imposto msg di errore se presente
			if(errori.length()>0)
				pd.setMessage(errori.toString());

			// effettuo le operazioni
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), pt);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
					
			List<Operation> lista = apcCore.accordiPorttypeOperationList(pt.getId().intValue(), ricerca);

			apcHelper.prepareAccordiPorttypeOperationsList(ricerca, lista, id, as, tipoAccordo, pt.getNome());

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			return ServletUtils.getStrutsForward(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPE_OPERATIONS, ForwardParams.DEL());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPE_OPERATIONS, ForwardParams.DEL());
		} 
	}
}
