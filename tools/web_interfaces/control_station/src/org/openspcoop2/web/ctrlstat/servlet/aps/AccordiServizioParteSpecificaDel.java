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


package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedBean;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.plugins.WrapperExtendedBean;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
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
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(apsCore);
			PddCore pddCore = new PddCore(apsCore);
			
			String tipologia = ServletUtils.getObjectFromSession(session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneFruitori = false;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
					gestioneFruitori = true;
				}
			}
			
			//User utente = ServletUtils.getUserFromSession(session);
			
			/*
			 * Validate the request parameters specified by the user Note: Basic
			 * field validation done in porteDomForm.java Business logic
			 * validation done in porteDomAdd.java
			 */
			String objToRemove = apsHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
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

				List<PortaApplicativa> paGenerateAutomcaticamente = null;
				List<IDPortaApplicativa> idPAGenerateAutomaticamente = null;
				
				List<PortaDelegata> pdGenerateAutomcaticamente = null;
				List<IDPortaDelegata> idPDGenerateAutomaticamente = null;
				IDSoggetto idSoggettoFruitore = null;
				
				String uri = idsToRemove.get(i);
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromUri(uri);

				AccordoServizioParteSpecifica asps = apsCore.getServizio(idServizio);
				
				// Verifico se sono in modalità di interfaccia 'standard' che non si tratti della PortaApplicativa generata automaticamente.
				// In tal caso la posso eliminare.
				if(asps!=null){
					boolean generaPACheckSoggetto = true;
					IDSoggetto idSoggettoEr = new IDSoggetto(asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore());
					Soggetto soggetto = soggettiCore.getSoggettoRegistro(idSoggettoEr );
					if(pddCore.isPddEsterna(soggetto.getPortaDominio())){
						generaPACheckSoggetto = false;
					}	
						
					if(gestioneFruitori) {
						
						// Verifico se esiste il mapping con la fruizione
						idSoggettoFruitore = new IDSoggetto(asps.getFruitore(0).getTipo(),asps.getFruitore(0).getNome());
						idPDGenerateAutomaticamente = porteDelegateCore.getIDPorteDelegateAssociate(idServizio, idSoggettoFruitore);
						if(idPDGenerateAutomaticamente!=null && idPDGenerateAutomaticamente.size()>0){
							for (IDPortaDelegata idPortaDeleggata : idPDGenerateAutomaticamente) {
								if(pdGenerateAutomcaticamente==null) {
									pdGenerateAutomcaticamente=new ArrayList<>();
								}
								pdGenerateAutomcaticamente.add(porteDelegateCore.getPortaDelegata(idPortaDeleggata));
							}
						}
						
					}
					else if(generaPACheckSoggetto){
							
						// Verifico se esiste il mapping con l'erogazione
						idPAGenerateAutomaticamente = porteApplicativeCore.getIDPorteApplicativeAssociate(idServizio);
						if(idPAGenerateAutomaticamente!=null && idPAGenerateAutomaticamente.size()>0){
							for (IDPortaApplicativa idPortaApplicativa : idPAGenerateAutomaticamente) {
								if(paGenerateAutomcaticamente==null) {
									paGenerateAutomcaticamente=new ArrayList<>();
								}
								paGenerateAutomcaticamente.add(porteApplicativeCore.getPortaApplicativa(idPortaApplicativa));
							}
						}
						
					}
					
				}
				
				HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
				
				boolean inUso = false;
				if(!gestioneFruitori) {
					inUso = apsCore.isAccordoServizioParteSpecificaInUso(asps, whereIsInUso, idPAGenerateAutomaticamente);
				}
				
				if (inUso) {// accordo in uso
					isInUso = true;
					msg += DBOggettiInUsoUtils.toString(idServizio, whereIsInUso, true, org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
					msg += org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE;
				} else {// accordo non in uso
					
					List<Object> listaOggettiDaEliminare = new ArrayList<Object>();
					
					if(paGenerateAutomcaticamente!=null && paGenerateAutomcaticamente.size()>0){
						
						for (PortaApplicativa paGenerataAutomcaticamente : paGenerateAutomcaticamente) {
							
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
							
							MappingErogazionePortaApplicativa mappingErogazione = new MappingErogazionePortaApplicativa();
							IDSoggetto soggettoErogatore = new IDSoggetto(paGenerataAutomcaticamente.getTipoSoggettoProprietario(),paGenerataAutomcaticamente.getNomeSoggettoProprietario());
							IDPortaApplicativa idPortaApplicativa = new IDPortaApplicativa();
							idPortaApplicativa.setNome(paGenerataAutomcaticamente.getNome());
							mappingErogazione.setIdPortaApplicativa(idPortaApplicativa);
							IDServizio idServizioPA = IDServizioFactory.getInstance().getIDServizioFromValues(paGenerataAutomcaticamente.getServizio().getTipo(),
									paGenerataAutomcaticamente.getServizio().getNome(), soggettoErogatore, paGenerataAutomcaticamente.getServizio().getVersione());
							mappingErogazione.setIdServizio(idServizioPA);
							if(porteApplicativeCore.existsMappingErogazionePortaApplicativa(mappingErogazione)) {
								listaOggettiDaEliminare.add(mappingErogazione);
							}
							
							listaOggettiDaEliminare.add(paGenerataAutomcaticamente);
							
							for (PortaApplicativaServizioApplicativo paSA : paGenerataAutomcaticamente.getServizioApplicativoList()) {
								if(paSA.getNome().equals(paGenerataAutomcaticamente.getNome())) {
									IDServizioApplicativo idSA = new IDServizioApplicativo();
									idSA.setIdSoggettoProprietario(soggettoErogatore);
									idSA.setNome(paSA.getNome());
									ServizioApplicativo saGeneratoAutomaticamente = saCore.getServizioApplicativo(idSA);
									listaOggettiDaEliminare.add(saGeneratoAutomaticamente);
								}
							}
						}
						
					}
					
					if(pdGenerateAutomcaticamente!=null && pdGenerateAutomcaticamente.size()>0){
						
						for (PortaDelegata pdGenerataAutomcaticamente : pdGenerateAutomcaticamente) {
							
							if(extendedServlet!=null){
								List<IExtendedBean> listExt = null;
								try{
									listExt = extendedServlet.extendedBeanList(TipoOperazione.DEL,apsHelper,apsCore,pdGenerataAutomcaticamente);
								}catch(Exception e){
									ControlStationCore.logError(e.getMessage(), e);
								}
								if(listExt!=null && listExt.size()>0){
									for (IExtendedBean iExtendedBean : listExt) {
										WrapperExtendedBean wrapper = new WrapperExtendedBean();
										wrapper.setExtendedBean(iExtendedBean);
										wrapper.setExtendedServlet(extendedServlet);
										wrapper.setOriginalBean(pdGenerataAutomcaticamente);
										wrapper.setManageOriginalBean(false);		
										listaOggettiDaEliminare.add(wrapper);
									}
								}
							}
							
							MappingFruizionePortaDelegata mappingFruizione = new MappingFruizionePortaDelegata();
							mappingFruizione.setIdFruitore(idSoggettoFruitore);
							mappingFruizione.setIdServizio(idServizio);
							IDPortaDelegata idPortaDelegata = new IDPortaDelegata();
							idPortaDelegata.setNome(pdGenerataAutomcaticamente.getNome());
							mappingFruizione.setIdPortaDelegata(idPortaDelegata);
							if(porteDelegateCore.existsMappingFruizionePortaDelegata(mappingFruizione)) {
								listaOggettiDaEliminare.add(mappingFruizione);
							}
							
							listaOggettiDaEliminare.add(pdGenerataAutomcaticamente);
							
						}
						
					}
					
					listaOggettiDaEliminare.add(asps);
					
					apsCore.performDeleteOperation(superUser, apsHelper.smista(), listaOggettiDaEliminare.toArray());

				}
			}// chiudo for

			// se ci sono messaggio di errore li presento
			if ((msg != null) && !msg.equals("")) {
				pd.setMessage(msg);
			}

			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			PermessiUtente pu = ServletUtils.getUserFromSession(session).getPermessi();

			boolean [] permessi = new boolean[2];
			permessi[0] = pu.isServizi();
			permessi[1] = pu.isAccordiCooperazione();
			List<AccordoServizioParteSpecifica> lista = null;
			if(apsCore.isVisioneOggettiGlobale(superUser)){
				lista = apsCore.soggettiServizioList(null, ricerca,permessi, session);
			}else{
				lista = apsCore.soggettiServizioList(superUser, ricerca, permessi, session);
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
