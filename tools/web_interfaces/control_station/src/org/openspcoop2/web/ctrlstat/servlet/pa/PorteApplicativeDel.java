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


package org.openspcoop2.web.ctrlstat.servlet.pa;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.monitor.engine.alarm.wrapper.ConfigurazioneAllarmeBean;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedBean;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.plugins.WrapperExtendedBean;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * porteAppDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteApplicativeDel extends Action {

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

		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session);
		if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;
		Boolean useIdSogg = parentPA == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_SOGGETTO;

		try {
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			int soggInt = -1 ;
			if(useIdSogg){
				String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
				soggInt = Integer.parseInt(idsogg);
			}
			String objToRemove = porteApplicativeHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);

			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			ConfigurazioneCore confCore = new ConfigurazioneCore(porteApplicativeCore);
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			// Elimino le porte applicative dal db
			// StringTokenizer objTok = new StringTokenizer(objToRemove, ",");
			// int[] idToRemove = new int[objTok.countTokens()];
			//
			// int k = 0;
			// while (objTok.hasMoreElements()) {
			// idToRemove[k++] = Integer.parseInt(objTok.nextToken());
			// }

			String userLogin = ServletUtils.getUserLoginFromSession(session);
			
			IExtendedListServlet extendedServlet = porteApplicativeCore.getExtendedServletPortaApplicativa();
			List<Object> listPerformOperations = new ArrayList<Object>();
			
			for (int i = 0; i < idsToRemove.size(); i++) {

				// DataElement de = (DataElement) ((Vector<?>) pdold.getDati()
				// .elementAt(idToRemove[i])).elementAt(0);
				// int idporta = Integer.parseInt(de.getValue());

				// Elimino la porta applicativa
				PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(Long.parseLong(idsToRemove.get(i)));
			
				if(extendedServlet!=null){
					List<IExtendedBean> listExt = null;
					try{
						listExt = extendedServlet.extendedBeanList(TipoOperazione.DEL,porteApplicativeHelper,porteApplicativeCore,pa);
					}catch(Exception e){
						ControlStationCore.logError(e.getMessage(), e);
					}
					if(listExt!=null && listExt.size()>0){
						for (IExtendedBean iExtendedBean : listExt) {
							WrapperExtendedBean wrapper = new WrapperExtendedBean();
							wrapper.setExtendedBean(iExtendedBean);
							wrapper.setExtendedServlet(extendedServlet);
							wrapper.setOriginalBean(pa);
							wrapper.setManageOriginalBean(false);		
							listPerformOperations.add(wrapper);
						}
					}
				}
				
				// cancellazione del mapping
				MappingErogazionePortaApplicativa mappingErogazione = new MappingErogazionePortaApplicativa();
				IDSoggetto soggettoErogatore = new IDSoggetto(pa.getTipoSoggettoProprietario(),pa.getNomeSoggettoProprietario());
				IDPortaApplicativa idPortaApplicativa = new IDPortaApplicativa();
				idPortaApplicativa.setNome(pa.getNome());
				mappingErogazione.setIdPortaApplicativa(idPortaApplicativa);
				mappingErogazione.setIdServizio(IDServizioFactory.getInstance().getIDServizioFromValues(pa.getServizio().getTipo(), pa.getServizio().getNome(), 
						soggettoErogatore, pa.getServizio().getVersione()));
				if(porteApplicativeCore.existsMappingErogazionePortaApplicativa(mappingErogazione)) {
					listPerformOperations.add(mappingErogazione);
				}
				
				// cancello per policy associate alla porta se esistono
				List<AttivazionePolicy> listAttivazione = confCore.attivazionePolicyList(new Search(true), RuoloPolicy.APPLICATIVA, pa.getNome());
				if(listAttivazione!=null && !listAttivazione.isEmpty()) {
					listPerformOperations.addAll(listAttivazione);
				}
				
				if(confCore.isConfigurazioneAllarmiEnabled()) {
					// cancello allarmi associati alla porta se esistono
					List<ConfigurazioneAllarmeBean> listAllarmi = confCore.allarmiList(new Search(true), RuoloPorta.APPLICATIVA, pa.getNome());
					if(listAllarmi!=null && !listAllarmi.isEmpty()) {
						listPerformOperations.addAll(listAllarmi);
					}
				}
				
				// cancellazione della porta
				listPerformOperations.add(pa);
			}
			
			porteApplicativeCore.performDeleteOperation(userLogin, porteApplicativeHelper.smista(), listPerformOperations.toArray(new Object[1]));

			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			List<PortaApplicativa> lista = null; 
			int idLista = -1;
			if(useIdSogg){
				idLista = Liste.PORTE_APPLICATIVE_BY_SOGGETTO;
				ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
				lista = porteApplicativeCore.porteAppList(soggInt, ricerca);
			}else{ 
				idLista = Liste.PORTE_APPLICATIVE;
				ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
				lista = porteApplicativeCore.porteAppList(null, ricerca);
			}

			porteApplicativeHelper.preparePorteAppList(ricerca, lista, idLista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE, 
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE,
					ForwardParams.DEL());
		}  
	}
}
