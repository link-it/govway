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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * AccordiServizioParteSpecificaFruitoriPorteDelegateList
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaFruitoriPorteDelegateList extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);

			String idServizio = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			Long idS = Long.parseLong(idServizio);
			
			String idFruizione = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID);
			Long idFru = Long.parseLong(idFruizione);
			
			String idSoggFruitoreDelServizio = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO);
			@SuppressWarnings("unused")
			Long idSoggFru = Long.parseLong(idSoggFruitoreDelServizio);
			
			// Preparo il menu
			apsHelper.makeMenu();
	
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
	
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
	
			int idLista = Liste.CONFIGURAZIONE_FRUIZIONE;
	
			ricerca = apsHelper.checkSearchParameters(idLista, ricerca);
	
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idS);
			IDServizio idServizioFromAccordo = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
			IDSoggetto idSoggettoFruitore = new IDSoggetto();
			String tipoSoggettoFruitore = null;
			String nomeSoggettoFruitore = null;
			if(apsCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggettoFruitore = soggettiCore.getSoggettoRegistro(Integer.parseInt(idSoggFruitoreDelServizio));
				tipoSoggettoFruitore = soggettoFruitore.getTipo();
				nomeSoggettoFruitore = soggettoFruitore.getNome();
			}else{
				org.openspcoop2.core.config.Soggetto soggettoFruitore = soggettiCore.getSoggetto(Integer.parseInt(idSoggFruitoreDelServizio));
				tipoSoggettoFruitore = soggettoFruitore.getTipo();
				nomeSoggettoFruitore = soggettoFruitore.getNome();
			}
			idSoggettoFruitore.setTipo(tipoSoggettoFruitore);
			idSoggettoFruitore.setNome(nomeSoggettoFruitore);
			 
			
			List<MappingFruizionePortaDelegata> lista = apsCore.serviziFruitoriMappingList(idFru, idSoggettoFruitore , idServizioFromAccordo, ricerca);
	
			apsHelper.serviziFruitoriMappingList(lista, idServizio, idSoggFruitoreDelServizio, idFruizione, ricerca);
	
			ServletUtils.setSearchObjectIntoSession(session, ricerca);
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE,
					ForwardParams.LIST());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE,
					ForwardParams.LIST());
		}  
	}
}
