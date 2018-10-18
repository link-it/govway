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
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.id.IDPortType;
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
 * accordiPorttypeDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComunePortTypesDel extends Action {

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
			String objToRemove = apcHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			String tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;

			// Preparo il menu
			apcHelper.makeMenu();

			// Elimino i port-types dell'accordo dal db
			// StringTokenizer objTok = new StringTokenizer(objToRemove, ",");
			// int[] idToRemove = new int[objTok.countTokens()];
			//
			// int k = 0;
			// while (objTok.hasMoreElements()) {
			// idToRemove[k++] = Integer.parseInt(objTok.nextToken());
			// }
			ArrayList<String> ptsToRemove = Utilities.parseIdsToRemove(objToRemove);
			AccordoServizioParteComune as = apcCore.getAccordoServizio(Long.valueOf(idInt));
			IDPortType idPT = new IDPortType();
			idPT.setIdAccordo(idAccordoFactory.getIDAccordoFromAccordo(as));
			
			String nomept = "";
			boolean modificaAS_effettuata = false;
			StringBuffer errori = new StringBuffer();
			for (int i = 0; i < ptsToRemove.size(); i++) {

				// DataElement de = (DataElement) ((Vector<?>) pdold.getDati()
				// .elementAt(idToRemove[i])).elementAt(0);
				// nomept = de.getValue();
				nomept = ptsToRemove.get(i);

				idPT.setNome(nomept);
				List<IDServizio> idServizi = null;
				try{
					idServizi = apsCore.getIdServiziWithPortType(idPT);
				}catch(DriverRegistroServiziNotFound dNotF){}
				
				if(idServizi==null || idServizi.size()<=0){
				
					// Check che il port type non sia correlato da altri porttype.
					Vector<String> tmp = new Vector<String>();
					for (int j = 0; j < as.sizePortTypeList(); j++) {
						PortType pt = as.getPortType(j);
						if(pt.getNome().equals(nomept)==false){
							for (int check = 0; check < pt.sizeAzioneList(); check++) {
								Operation opCheck = pt.getAzione(check);
								if(opCheck.getCorrelataServizio()!=null && nomept.equals(opCheck.getCorrelataServizio())){
									tmp.add("azione "+opCheck.getNome()+" del servizio "+pt.getNome());
								}
							}
						}
					}
					if(tmp.size()>0){
						if(errori.length()>0) {
							errori.append("<BR>");
						}
						errori.append("Servizio ["+nomept+"] non rimosso poichè correlato da azioni di altri servizi della API: <br>");
						for(int j=0; j<tmp.size();j++){
							errori.append("- "+tmp.get(j).toString()+"<br>");
						}
						continue;
					}
					
					// Effettuo eliminazione
					for (int j = 0; j < as.sizePortTypeList(); j++) {
						PortType pt = as.getPortType(j);
						if (nomept.equals(pt.getNome())) {
							modificaAS_effettuata = true;
							as.removePortType(j);
							break;
						}
					}
					
				}else{
					
					// Se esiste un mapping segnalo l'errore specifico
					List<MappingErogazionePortaApplicativa> lPA = porteApplicativeCore.getMapping(idServizi, true, false);
					if(lPA!=null && !lPA.isEmpty()) {
						if(errori.length()>0) {
							errori.append("<BR>");
						}
						errori.append("Servizio "+nomept+" non rimuovibile poichè implementato nell'erogazione del servizio: <BR>");
						for(int j=0;j<lPA.size();j++){
							errori.append("- "+lPA.get(j).getIdServizio()+"<BR>");
						}
						continue;
					}
					List<MappingFruizionePortaDelegata> lPD = porteDelegateCore.getMapping(idServizi, true, false);
					if(lPD!=null && !lPD.isEmpty()) {
						if(errori.length()>0) {
							errori.append("<BR>");
						}
						errori.append("Servizio "+nomept+" non rimuovibile poichè implementato nella fruizione del servizio: <BR>");
						for(int j=0;j<lPD.size();j++){
							errori.append("- "+lPD.get(j).getIdServizio()+" (fruitore: "+lPD.get(j).getIdFruitore()+")<BR>");
						}
						continue;
					}
					
					// Altrimenti segnalo l'errore più generico sull'accordo
					if(errori.length()>0) {
						errori.append("<BR>");
					}
					errori.append("Servizio ["+nomept+"] non rimosso poichè viene implementato dai seguenti servizi: <br>");
					for(int j=0; j<idServizi.size();j++){
						errori.append("- "+idServizi.get(j).toString()+"<br>");
					}
					//continue;
					
				}
			}
			
			// imposto msg di errore se presente
			if(errori.length()>0)
				pd.setMessage(errori.toString());

			// effettuo le operazioni
			if(modificaAS_effettuata)
				apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);

			// Verifico stato
			apcHelper.setMessageWarningStatoConsistenzaAccordo(false, as);
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			List<PortType> lista = apcCore.accordiPorttypeList(idInt, ricerca);

			// Devo rileggere l'accordo dal db, perche' altrimenti
			// manca l'id dei port-type
			as = apcCore.getAccordoServizio(Long.valueOf(idInt));

			apcHelper.prepareAccordiPorttypeList(id,as, lista, ricerca, tipoAccordo);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			return ServletUtils.getStrutsForward(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPES, ForwardParams.DEL());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPES, ForwardParams.DEL());
		} 
	}
}
