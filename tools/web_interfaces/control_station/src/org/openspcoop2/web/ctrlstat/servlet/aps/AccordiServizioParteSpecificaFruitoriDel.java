/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedBean;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.plugins.WrapperExtendedBean;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * serviziFruitoriDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaFruitoriDel extends Action {

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
			String id = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			int idServizio = Integer.parseInt(id);
			String objToRemove = apsHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);

			// prendo l'id del soggetto erogatore lo propago
			// lo metto nel pd come campo hidden
			// PageData oldPD = (PageData) session.getAttribute("PageData");
			pd.setHidden(pdold.getHidden());

			// Preparo il menu
			apsHelper.makeMenu();

			// Elimino i fruitori del servizio dal db
			// StringTokenizer objTok = new StringTokenizer(objToRemove, ",");
			// int[] idToRemove = new int[objTok.countTokens()];
			//
			// int k = 0;
			// while (objTok.hasMoreElements()) {
			// idToRemove[k++] = Integer.parseInt(objTok.nextToken());
			// }
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			String fruitore = "";
			Fruitore fru = null;

			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apsCore);
			ConfigurazioneCore confCore = new ConfigurazioneCore(apsCore);
			
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idServizio);
			String nomeservizio = asps.getNome();
			String tiposervizio = asps.getTipo();
			Integer versioneservzio = asps.getVersione();
			// Prendo i dati del soggetto erogatore del servizio
			String mynomeprov = asps.getNomeSoggettoErogatore();
			String mytipoprov = asps.getTipoSoggettoErogatore();

			String superUser =  ServletUtils.getUserLoginFromSession(session);

			IExtendedListServlet extendedServlet = porteDelegateCore.getExtendedServletPortaDelegata();
			
			for (int i = 0; i < idsToRemove.size(); i++) {

				// DataElement de = (DataElement) ((Vector<?>) pdold.getDati()
				// .elementAt(idToRemove[i])).elementAt(0);
				fruitore = idsToRemove.get(i);// de.getValue();

				int idFru = Integer.parseInt(fruitore);

				for (int j = 0; j < asps.sizeFruitoreList(); j++) {
					fru = asps.getFruitore(j);
					if (fru.getId().intValue() == idFru) {
						asps.removeFruitore(j);
						break;
					}
				}

				// cancello la porta delegata associata al fruitore
				// del servizio, se esiste
//				String nomePD = fru.getTipo() + fru.getNome() + "/" + mytipoprov + mynomeprov + "/" + tiposervizio + nomeservizio;
//				IDPortaDelegata myidpd = new IDPortaDelegata();
//				IDSoggetto ids = new IDSoggetto(fru.getTipo(), fru.getNome());
//				myidpd.setSoggettoFruitore(ids);
//				myidpd.setLocationPD(nomePD);
//				if (porteDelegateCore.existsPortaDelegata(myidpd)) {
				IDServizio idServizioObject = IDServizioFactory.getInstance().getIDServizioFromValues(tiposervizio, nomeservizio, mytipoprov, mynomeprov, versioneservzio);
				IDSoggetto idSoggettoFruitore = new IDSoggetto(fru.getTipo(), fru.getNome());
				List<IDPortaDelegata> myidpds = porteDelegateCore.getIDPorteDelegateAssociate(idServizioObject, idSoggettoFruitore);
				
				if(myidpds!=null && myidpds.size()>0){
					
					List<Object> listaOggettiDaEliminare = new ArrayList<Object>();
					
					for (IDPortaDelegata myidpd : myidpds) {
						
						PortaDelegata mypd = porteDelegateCore.getPortaDelegata(myidpd);
												
						if(extendedServlet!=null){
							List<IExtendedBean> listExt = null;
							try{
								listExt = extendedServlet.extendedBeanList(TipoOperazione.DEL,apsHelper,apsCore,mypd);
							}catch(Exception e){
								ControlStationCore.logError(e.getMessage(), e);
							}
							if(listExt!=null && listExt.size()>0){
								for (IExtendedBean iExtendedBean : listExt) {
									WrapperExtendedBean wrapper = new WrapperExtendedBean();
									wrapper.setExtendedBean(iExtendedBean);
									wrapper.setExtendedServlet(extendedServlet);
									wrapper.setOriginalBean(mypd);
									wrapper.setManageOriginalBean(false);		
									listaOggettiDaEliminare.add(wrapper);
								}
							}
						}
						
						//cancello il mapping
						MappingFruizionePortaDelegata mappingFruizione = new MappingFruizionePortaDelegata();
						mappingFruizione.setIdFruitore(idSoggettoFruitore);
						mappingFruizione.setIdServizio(idServizioObject);
						mappingFruizione.setIdPortaDelegata(myidpd);
						if(porteDelegateCore.existsMappingFruizionePortaDelegata(mappingFruizione)) {
							listaOggettiDaEliminare.add(mappingFruizione);
						}
						
						// cancello per policy associate alla porta se esistono
						List<AttivazionePolicy> listAttivazione = confCore.attivazionePolicyList(new Search(true), RuoloPolicy.DELEGATA, mypd.getNome());
						if(listAttivazione!=null && !listAttivazione.isEmpty()) {
							listaOggettiDaEliminare.addAll(listAttivazione);
						}
						
						// cancello la porta
						listaOggettiDaEliminare.add(mypd);
						
					}
					
					apsCore.performDeleteOperation(superUser, apsHelper.smista(), listaOggettiDaEliminare.toArray(new Object[1]) );
					
				}
			}

			apsCore.performUpdateOperation(superUser, apsHelper.smista(), asps);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.SERVIZI_FRUITORI;

			ricerca = apsHelper.checkSearchParameters(idLista, ricerca);

			List<Fruitore> lista = apsCore.serviziFruitoriList(idServizio, ricerca);

			apsHelper.prepareServiziFruitoriList(lista, id, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI, 
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,
					ForwardParams.DEL());
		} 
	}
}
