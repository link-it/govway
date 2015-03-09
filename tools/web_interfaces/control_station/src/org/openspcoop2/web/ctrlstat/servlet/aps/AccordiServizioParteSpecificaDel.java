/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ErrorsHandler;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddTipologia;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.users.dao.InterfaceType;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;
import org.openspcoop2.web.lib.users.dao.User;

/**
 * serviziDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaDel extends Action {

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

		try {
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apsCore);
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apsCore);
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
			PddCore pddCore = new PddCore(apsCore);
			
			User utente = ServletUtils.getUserFromSession(session);
			
			/*
			 * Validate the request parameters specified by the user Note: Basic
			 * field validation done in porteDomForm.java Business logic
			 * validation done in porteDomAdd.java
			 */
			String objToRemove = request.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			// Preparo il menu
			apsHelper.makeMenu();

			String superUser =   ServletUtils.getUserLoginFromSession(session); 

			// Elimino i servizi dal db
			// StringTokenizer objTok = new StringTokenizer(objToRemove, ",");
			// int[] idToRemove = new int[objTok.countTokens()];
			//
			// int k = 0;
			// while (objTok.hasMoreElements()) {
			// idToRemove[k++] = Integer.parseInt(objTok.nextToken());
			// }
			//
			// String nomeservizio = "", tiposervizio = "";
			ErrorsHandler errors;
			String nomeservizio = "", tiposervizio = "";
			String nomesogg = "", tiposogg = "";
			String msg = "";
			// int idConnettore = 0;


			for (int i = 0; i < idsToRemove.size(); i++) {

				PortaApplicativa paGenerataAutomcaticamente = null;
				
				String nomeEtipo = idsToRemove.get(i);
				StringTokenizer servTok = new StringTokenizer(nomeEtipo, "/");
				tiposervizio = servTok.nextToken();
				nomeservizio = servTok.nextToken();

				tiposogg = servTok.nextToken();
				nomesogg = servTok.nextToken();

				IDServizio idS = new IDServizio(tiposogg, nomesogg, tiposervizio, nomeservizio);

				boolean isInUso = false;
				// DataElement de = (DataElement) ((Vector<?>)
				// pdold.getDati()
				// .elementAt(idToRemove[i])).elementAt(0);
				// String nomeEtipo = de.getValue();
				// StringTokenizer servTok = new StringTokenizer(nomeEtipo,
				// "/");
				// tiposervizio = servTok.nextToken();
				// nomeservizio = servTok.nextToken();
				// de = (DataElement) ((Vector<?>)
				// pdold.getDati().elementAt(
				// idToRemove[i])).elementAt(1);
				// String tmpprov = de.getValue();
				// StringTokenizer provTok = new StringTokenizer(tmpprov,
				// "/");
				// String tipoprov = provTok.nextToken();
				// String nomeprov = provTok.nextToken();

				errors = new ErrorsHandler();

				// IDSoggetto idS = new IDSoggetto(tipoprov, nomeprov);
				// Soggetto soggetto = core.getSoggettoRegistro(idS);
				// int idProv = soggetto.getId().intValue();

				// Prendo l'id del servizio e del connettore associato al
				// servizio
				// IDServizio idServ = new IDServizio(tipoprov, nomeprov,
				// tiposervizio, nomeservizio);
				AccordoServizioParteSpecifica asps = apsCore.getServizio(idS);
				Servizio ss = asps.getServizio();
				// int idInt = ss.getId().intValue();
				// Connettore connServ = ss.getConnettore();
				// idConnettore = connServ.getId().intValue();

				// Controllo che il servizio non sia in uso in porte
				// applicative (servizio e' identificato anche
				// dall'erogatore del servizio)
				List<PortaApplicativa> paList = porteApplicativeCore.getPorteApplicativeWithServizio(asps.getId(), ss.getTipo(), ss.getNome(), asps.getIdSoggetto(), ss.getTipoSoggettoErogatore(), ss.getNomeSoggettoErogatore());
				for (int j = 0; j < paList.size(); j++) {
					PortaApplicativa myPA = paList.get(j);
					String nome_porta = myPA.getNome();

					// Verifico se sono in modalità di interfaccia 'standard' che non si tratti della PortaApplicativa generata automaticamente.
					// In tal caso la posso eliminare.
					boolean foundPAGenerataAutomaticamente = false;
										
					//if(InterfaceType.STANDARD.equals(utente.getInterfaceType()) && paList.size()==1){
					if(paList.size()==1){ // anche l'eliminazione della PD associata alla fruizione viene effettuata anche in modalità avanzata
						
						if(apsCore.isGenerazioneAutomaticaPorteApplicative() && asps!=null && asps.getPortType()!=null && !"".equals(asps.getPortType())){
							boolean generaPACheckSoggetto = true;
							IDSoggetto idSoggettoEr = new IDSoggetto(ss.getTipoSoggettoErogatore(), ss.getNomeSoggettoErogatore());
							Soggetto soggetto = soggettiCore.getSoggettoRegistro(idSoggettoEr );
							
							if (soggetto.getPortaDominio() != null) {
								String nomePdd = soggetto.getPortaDominio();
								
								PdDControlStation portaDominio = pddCore.getPdDControlStation(nomePdd);
								
								if(portaDominio.getTipo().equals(PddTipologia.ESTERNO.toString()))
									generaPACheckSoggetto = false;
								
							} else {
								// se non ho una porta di domini non devo generare la porta applicativa
								generaPACheckSoggetto  =false;
							}
							
							if(generaPACheckSoggetto){
								
								String nomeGenerato = ss.getTipoSoggettoErogatore()+ss.getNomeSoggettoErogatore()+"/"+
										ss.getTipo()+asps.getPortType();
								if(nomeGenerato.equals(nome_porta)){
									paGenerataAutomcaticamente = porteApplicativeCore.getPortaApplicativa(myPA.getNome(), new IDSoggetto(tiposogg, nomesogg));
									foundPAGenerataAutomaticamente = true;
								}
								
							}
						}
						
					}
					
					if(!foundPAGenerataAutomaticamente){
						isInUso = true;
						errors.addError(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE, nome_porta);
					}

				}

				// Controllo che il servizio non sia in uso in porte
				// delegate (servizio e' identificato anche dall'erogatore
				// del servizio)
				List<PortaDelegata> pdeList = porteDelegateCore.getPorteDelegateWithServizio(asps.getId(), ss.getTipo(), ss.getNome(), asps.getIdSoggetto(), ss.getTipoSoggettoErogatore(), ss.getNomeSoggettoErogatore());
				for (int j = 0; j < pdeList.size(); j++) {
					PortaDelegata myPD = pdeList.get(j);
					isInUso = true;
					String nome_porta = myPD.getNome();
					errors.addError(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE, nome_porta);
				}

				// controllo se ci sono fruitori
				List<Fruitore> sfList = apsCore.getServiziFruitoriWithServizio(asps.getId().intValue());
				if (sfList != null) {
					for (int j = 0; j < sfList.size(); j++) {
						Fruitore myFru = sfList.get(j);
						isInUso = true;
						String nomeFruitore = myFru.getTipo() + "/" + myFru.getNome();
						errors.addError(ErrorsHandlerCostant.POSSIEDE_FRUITORI, nomeFruitore);
					}
				}

				// controllo se in uso, altrimenti posso eliminare il
				// servizio
				if (isInUso) {
					errors.setCustomMessage("Servizio [" + ss.getTipo() + "/" + ss.getNome() + "] di [" + ss.getTipoSoggettoErogatore() + "/" + ss.getNomeSoggettoErogatore() + "] non rimosso perche':");
					// formatto i messaggi di errore da visualizzare
					// all'utente
					msg += errors.formatErrorMessage();
				} else {
					ErrorsHandler errorsh = new ErrorsHandler();
					HashMap<ErrorsHandlerCostant, String> whereIsInUso = new HashMap<ErrorsHandlerCostant, String>();
					if (apsCore.isServizioInUso(asps, whereIsInUso)) {

						Iterator<ErrorsHandlerCostant> it = whereIsInUso.keySet().iterator();
						while (it.hasNext()) {
							ErrorsHandlerCostant key = it.next();
							errorsh.addError(key, whereIsInUso.get(key));
						}

						errorsh.setCustomMessage("Servizio [" + ss.getTipo()+"/"+ss.getNome() + "] erogato dal soggetto ["+ss.getTipoSoggettoErogatore()+"/"+ss.getNomeSoggettoErogatore()+"] non rimosso perche' :");
						msg += errorsh.formatErrorMessage();
					} else {
						List<Object> listaOggettiDaEliminare = new ArrayList<Object>();
						if(paGenerataAutomcaticamente!=null){
							listaOggettiDaEliminare.add(paGenerataAutomcaticamente);
						}
						listaOggettiDaEliminare.add(asps);
						apsCore.performDeleteOperation(superUser, apsHelper.smista(), listaOggettiDaEliminare.toArray());
					}
				}
			}// chiudo for

			// se ci sono messaggio di errore li presento
			if ((msg != null) && !msg.equals("")) {
				pd.setMessage(msg);
			}

			//				con.commit();
			//				con.setAutoCommit(true);
			//			} catch (SQLException ex) {
			//				// java.util.Date now = new java.util.Date();
			//				try {
			//					con.rollback();
			//				} catch (SQLException exrool) {
			//				}
			//				// Chiudo la connessione al DB
			//				dbM.releaseConnection(con);
			//				pd.setMessage("Il sistema &egrave; momentaneamente indisponibile.<BR>Si prega di riprovare pi&ugrave; tardi");
			//				session.setAttribute("GeneralData", gd);
			//				session.setAttribute("PageData", pd);
			//				// Remove the Form Bean - don't need to carry values forward
			//				// con jboss 4.2.1 produce errore:
			//				// request.removeAttribute(mapping.getAttribute());
			//				return (mapping.findForward("Error"));
			//			}

			// Preparo la lista
			// boolean ersql = ch.serviziList();
			// if (ersql) {
			// //Chiudo la connessione al DB
			// dbM.releaseConnection(con);
			// pd.setMessage("Il sistema &egrave; momentaneamente
			// indisponibile.<BR>Si prega di riprovare pi&ugrave; tardi");
			// session.setAttribute("GeneralData", gd);
			// session.setAttribute("PageData", pd);
			// // Remove the Form Bean - don't need to carry values forward
			// //con jboss 4.2.1 produce errore:
			// request.removeAttribute(mapping.getAttribute());
			// return (mapping.findForward("Error"));
			// }

			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			PermessiUtente pu = ServletUtils.getUserFromSession(session).getPermessi();

			boolean [] permessi = new boolean[2];
			permessi[0] = pu.isServizi();
			permessi[1] = pu.isAccordiCooperazione();
			List<AccordoServizioParteSpecifica> lista = null;
			if(apsCore.isVisioneOggettiGlobale(superUser)){
				lista = apsCore.soggettiServizioList(null, ricerca,permessi);
			}else{
				lista = apsCore.soggettiServizioList(superUser, ricerca, permessi);
			}

			apsHelper.prepareServiziList(ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
					ForwardParams.DEL());
		}  
	}
}
