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
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.id.IDPortaApplicativaByNome;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
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

		// Salvo il vecchio PageData
		// PageData pdold = (PageData) session.getAttribute("PageData");

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
		
		try {
			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);

			String id = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			int idAccordo = Integer.parseInt(id);
			String objToRemove = request.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			String tipoAccordo = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;
			
			// Preparo il menu
			apcHelper.makeMenu();

			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(apcCore);
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apcCore);
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apcCore);
			AccordoServizioParteComune as = apcCore.getAccordoServizio(new Long(idAccordo));
			//String nomeacc = as.getNome();
			int totAz = as.sizeAzioneList();
			String nomeaz = "";
			//boolean callBC = false;

			Vector<IDServizio> idServiziWithAccordo = null;
			try{
				idServiziWithAccordo = apsCore.getIdServiziWithAccordo(idAccordoFactory.getIDAccordoFromAccordo(as),true);
			}catch(DriverRegistroServiziNotFound dNotF){}
			
			String userLogin = ServletUtils.getUserLoginFromSession(session);

			StringBuffer errori = new StringBuffer();
			for (int i = 0; i < idsToRemove.size(); i++) {

				nomeaz = idsToRemove.get(i);

				
				// Controllo che l'azione non sia stata correlata da un'altra azione
				if (apcCore.isAzioneCorrelata(new Long(as.getId()).intValue(), nomeaz, nomeaz)) {
					// non rimuovo in quanto correlata
					ArrayList<String> tmp = new ArrayList<String>();
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
					errori.append("Azione "+nomeaz+" non rimuovibile poiche' perche' correlata ad altre azioni:<BR>" );
					for(int p=0; p<tmp.size();p++){
						errori.append("- "+tmp.get(p)+"<BR>");
					}
						
					continue;
				}
				
				

				// Controllo che l'azione non sia in uso (se esistono servizi, allora poi saranno state create PD o PA)
				if(idServiziWithAccordo!=null && idServiziWithAccordo.size()>0){
				
					if(apcCore.isUnicaAzioneInAccordi(nomeaz)){
						
						// Se esiste solo un'azione con tale identificativo, posso effettuare il controllo che non vi siano PA/PD esistenti.
						if (porteApplicativeCore.existsPortaApplicativaAzione(nomeaz)) {
							Vector<IDPortaApplicativaByNome> idPAs = porteApplicativeCore.getPortaApplicativaAzione(nomeaz);
							errori.append("Azione "+nomeaz+" non rimuovibile poiche' in uso in porte applicative: <BR>");
							for(int j=0;j<idPAs.size();j++){
								errori.append("- "+idPAs.get(j).toString()+"<BR>");
							}
							continue;
						}
						if (porteDelegateCore.existsPortaDelegataAzione(nomeaz)) {
							Vector<IDPortaDelegata> idPDs = porteDelegateCore.getPortaDelegataAzione(nomeaz);
							errori.append("Azione "+nomeaz+" non rimuovibile poiche' in uso in porte delegate: <BR>");
							for(int j=0;j<idPDs.size();j++){
								errori.append("- "+idPDs.get(j).toString()+"<BR>");
							}
							continue;
						}
						
					}else{
						
						// Se esiste piu' di un'azione con tale identificativo, non posso effettuare il controllo che non vi siano PA/PD esistenti,
						// poiche' non saprei se l'azione di una PD/PA si riferisce all'azione in questione.
						// Allora non permetto l'eliminazione poiche' esistono dei servizi che implementano l'accordo
						errori.append("Azione "+nomeaz+" non rimuovibile poiche' l'accordo di servizio parte comune viene implementato dai seguenti servizi: <br>");
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

			// Dovrebbe essere gia invocato dalla performUpdate sopra
//			if (callBC) {
//				// Chiedo la setDati
//				if (ch.smista()) {
//					OperazioneDaSmistare opRegistroServizi = new OperazioneDaSmistare();
//					opRegistroServizi.setOperazione(TipiOperazione.MODIFICA);
//					opRegistroServizi.setIDTable(idAccordo);
//					opRegistroServizi.setSuperuser(userLogin);
//					opRegistroServizi.setOggetto("accordo");
//					opRegistroServizi.addParameter(OperationsParameter.NOME_ACCORDO, nomeacc);
//					BackendConnector bc = new BackendConnector();
//					boolean bcOk = bc.setDati(opRegistroServizi);
//					if (!bcOk) {
//						// java.util.Date now = new java.util.Date();
//						try {
//							con.rollback();
//						} catch (SQLException exrool) {
//						}
//						// Chiudo la connessione al DB
//						dbM.releaseConnection(con);
//						pd.setMessage("Si e' verificato un errore durante la setDati.<BR>Si prega di riprovare pi&ugrave; tardi");
//						session.setAttribute("GeneralData", gd);
//						session.setAttribute("PageData", pd);
//						// Remove the Form Bean - don't need to carry values
//						// forward
//						// con jboss 4.2.1 produce errore:
//						// request.removeAttribute(mapping.getAttribute());
//						return (mapping.findForward("Error"));
//					}
//				}
//			}

			// imposto msg di errore se presente
			if(errori.length()>0)
				pd.setMessage(errori.toString());

			// devo aggiornare la lista dei servizi(serviziCorrelati) che
			// implementano l'accordo a cui e' stata aggiunta l'azione
			// basta fare un update del servizio per attivare le operazioni
			// necessarie all'aggiornamento
			List<AccordoServizioParteSpecifica> listaServizi = apsCore.serviziWithIdAccordoList(idAccordo);
			for (AccordoServizioParteSpecifica servizio : listaServizi) {
				apcCore.performUpdateOperation(userLogin, apcHelper.smista(), servizio);
			}

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
	

			int idLista = Liste.ACCORDI_AZIONI;

			ricerca = apcHelper.checkSearchParameters(idLista, ricerca);
			List<Azione> lista = apcCore.accordiAzioniList(idAccordo, ricerca);
			apcHelper.prepareAccordiAzioniList(as, lista, ricerca, tipoAccordo);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			return ServletUtils.getStrutsForward(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_AZIONI, ForwardParams.DEL());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_AZIONI, ForwardParams.DEL());
		} 
	}
}
