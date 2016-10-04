/*
 * OpenSPCoop - Customizable API Gateway 
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


package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.DBOggettiInUsoUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedBean;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.plugins.WrapperExtendedBean;
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
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;

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

	@SuppressWarnings("unused")
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
			
			//User utente = ServletUtils.getUserFromSession(session);
			
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
			String nomeservizio = "", tiposervizio = "";
			String nomesogg = "", tiposogg = "";
			// int idConnettore = 0;

			IExtendedListServlet extendedServlet = porteApplicativeCore.getExtendedServletPortaApplicativa();

			String msg = "";
			boolean isInUso = false;
			
			for (int i = 0; i < idsToRemove.size(); i++) {

				PortaApplicativa paGenerataAutomcaticamente = null;
				
				String nomeEtipo = idsToRemove.get(i);
				StringTokenizer servTok = new StringTokenizer(nomeEtipo, "/");
				tiposervizio = servTok.nextToken();
				nomeservizio = servTok.nextToken();

				tiposogg = servTok.nextToken();
				nomesogg = servTok.nextToken();

				IDServizio idS = new IDServizio(tiposogg, nomesogg, tiposervizio, nomeservizio);

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

				
				// Verifico se sono in modalità di interfaccia 'standard' che non si tratti della PortaApplicativa generata automaticamente.
				// In tal caso la posso eliminare.
				boolean foundPAGenerataAutomaticamente = false;
				String nomeGenerato = null;
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
							
						nomeGenerato = ss.getTipoSoggettoErogatore()+ss.getNomeSoggettoErogatore()+"/"+
								ss.getTipo()+asps.getPortType();
						// provo a vedere se esiste
						if(porteApplicativeCore.existsPortaApplicativa(nomeGenerato, new IDSoggetto(tiposogg, nomesogg))){
							paGenerataAutomcaticamente = porteApplicativeCore.getPortaApplicativa(nomeGenerato, new IDSoggetto(tiposogg, nomesogg));
							foundPAGenerataAutomaticamente = true;
						}
						
					}
					
				}
				
				HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
				
				if (apsCore.isAccordoServizioParteSpecificaInUso(asps, whereIsInUso, nomeGenerato)) {// accordo in uso
					isInUso = true;
					msg += DBOggettiInUsoUtils.toString(idS, whereIsInUso, true, org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
					msg += org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE;
				} else {// accordo non in uso
					
					List<Object> listaOggettiDaEliminare = new ArrayList<Object>();
					if(paGenerataAutomcaticamente!=null){
						
						if(extendedServlet!=null){
							List<IExtendedBean> listExt = null;
							try{
								listExt = extendedServlet.extendedBeanList(TipoOperazione.DEL,apsHelper,apsCore,paGenerataAutomcaticamente);
							}catch(Exception e){
								ControlStationCore.logError(e.getMessage(), e);
							}
							if(listExt!=null && listExt.size()>0){
								for (IExtendedBean iExtendedBean : listExt) {
									WrapperExtendedBean wrapper = new WrapperExtendedBean();
									wrapper.setExtendedBean(iExtendedBean);
									wrapper.setExtendedServlet(extendedServlet);
									wrapper.setOriginalBean(paGenerataAutomcaticamente);
									wrapper.setManageOriginalBean(false);		
									listaOggettiDaEliminare.add(wrapper);
								}
							}
						}
						
						listaOggettiDaEliminare.add(paGenerataAutomcaticamente);
					}
					listaOggettiDaEliminare.add(asps);
					apsCore.performDeleteOperation(superUser, apsHelper.smista(), listaOggettiDaEliminare.toArray());

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
