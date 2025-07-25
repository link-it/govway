/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.pd;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.monitor.engine.alarm.wrapper.ConfigurazioneAllarmeBean;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedBean;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.plugins.WrapperExtendedBean;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * porteDelegateDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteDelegateDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			
			// Preparo il menu
			porteDelegateHelper.makeMenu();
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session, request);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
			boolean useIdSogg = parentPD!=null && parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_SOGGETTO;
			int soggInt = -1;
			if(useIdSogg){
				String idsogg = porteDelegateHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
				soggInt = Integer.parseInt(idsogg);
			}
			String objToRemove = porteDelegateHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);

			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			ConfigurazioneCore confCore = new ConfigurazioneCore(porteDelegateCore);
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			IExtendedListServlet extendedServlet = porteDelegateCore.getExtendedServletPortaDelegata();
			List<Object> listPerformOperations = new ArrayList<>();
			
			for (int i = 0; i < idsToRemove.size(); i++) {

				// Elimino la porta delegata
				PortaDelegata pde = porteDelegateCore.getPortaDelegata(Long.parseLong(idsToRemove.get(i)));
				
				if(extendedServlet!=null){
					List<IExtendedBean> listExt = null;
					try{
						listExt = extendedServlet.extendedBeanList(TipoOperazione.DEL,porteDelegateHelper,porteDelegateCore,pde);
					}catch(Exception e){
						ControlStationCore.logError(e.getMessage(), e);
					}
					if(listExt!=null && !listExt.isEmpty()){
						for (IExtendedBean iExtendedBean : listExt) {
							WrapperExtendedBean wrapper = new WrapperExtendedBean();
							wrapper.setExtendedBean(iExtendedBean);
							wrapper.setExtendedServlet(extendedServlet);
							wrapper.setOriginalBean(pde);
							wrapper.setManageOriginalBean(false);		
							listPerformOperations.add(wrapper);
						}
					}
				}
				
				// cancellazione del mapping
				if(pde.getSoggettoErogatore()!=null && pde.getSoggettoErogatore().getNome()!=null && !"".equals(pde.getSoggettoErogatore().getNome()) &&
						pde.getServizio()!=null && pde.getServizio().getNome()!=null && !"".equals(pde.getServizio().getNome())) {
					MappingFruizionePortaDelegata mappingFruizione = new MappingFruizionePortaDelegata();
					mappingFruizione.setIdFruitore(new IDSoggetto(pde.getTipoSoggettoProprietario(), pde.getNomeSoggettoProprietario()));
					mappingFruizione.setIdServizio(IDServizioFactory.getInstance().getIDServizioFromValues(pde.getServizio().getTipo(), pde.getServizio().getNome(), 
							new IDSoggetto(pde.getSoggettoErogatore().getTipo(), pde.getSoggettoErogatore().getNome()), 
							pde.getServizio().getVersione()));
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setNome(pde.getNome());
					mappingFruizione.setIdPortaDelegata(idPD);
					if(porteDelegateCore.existsMappingFruizionePortaDelegata(mappingFruizione)) {
						listPerformOperations.add(mappingFruizione);	
					}					
					
				}
				
				// cancello per policy associate alla porta se esistono
				List<AttivazionePolicy> listAttivazione = confCore.attivazionePolicyList(new ConsoleSearch(true), RuoloPolicy.DELEGATA, pde.getNome());
				if(listAttivazione!=null && !listAttivazione.isEmpty()) {
					listPerformOperations.addAll(listAttivazione);
				}
				
				if(confCore.isConfigurazioneAllarmiEnabled()) {
					// cancello allarmi associati alla porta se esistono
					List<ConfigurazioneAllarmeBean> listAllarmi = confCore.allarmiList(new ConsoleSearch(true), RuoloPorta.DELEGATA, pde.getNome());
					if(listAllarmi!=null && !listAllarmi.isEmpty()) {
						listPerformOperations.addAll(listAllarmi);
					}
				}
				
				// cancellazione della porta
				listPerformOperations.add(pde);
			}
			
			porteDelegateCore.performDeleteOperation(userLogin, porteDelegateHelper.smista(), listPerformOperations.toArray(new Object[1]));

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			List<PortaDelegata> lista = null;
			int idLista = -1;
			if(useIdSogg){
				idLista = Liste.PORTE_DELEGATE_BY_SOGGETTO;
				ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
				lista = porteDelegateCore.porteDelegateList(soggInt, ricerca);
			}else{ 
				idLista = Liste.PORTE_DELEGATE;
				ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
				lista = porteDelegateCore.porteDelegateList(null, ricerca);
			}

			porteDelegateHelper.preparePorteDelegateList(ricerca, lista,idLista);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE ,
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE, 
					ForwardParams.DEL());
		} 
	}
}
