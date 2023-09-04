/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import org.apache.commons.lang.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
			
			// Preparo il menu
			apsHelper.makeMenu();

			String idServizio = apsHelper.getParametroLong(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			Long idS = Long.parseLong(idServizio);
			
			String idFruizione = apsHelper.getParametroLong(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID);
			Long idFru = Long.parseLong(idFruizione);
			
			String idSoggFruitoreDelServizio = apsHelper.getParametroLong(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO);
			@SuppressWarnings("unused")
			Long idSoggFru = Long.parseLong(idSoggFruitoreDelServizio);
			
			String paramGestioneGruppi = apsHelper.getParametroBoolean(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_GRUPPI);
			if(paramGestioneGruppi!=null && !"".equals(paramGestioneGruppi)) {
				boolean gestioneGruppi = Boolean.valueOf(paramGestioneGruppi);
				ServletUtils.setObjectIntoSession(request, session, gestioneGruppi+"", AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_GRUPPI);
			}
			
			String paramGestioneConfigurazioni = apsHelper.getParametroBoolean(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_CONFIGURAZIONI);
			if(paramGestioneConfigurazioni!=null && !"".equals(paramGestioneConfigurazioni)) {
				boolean gestioneConfigurazioni = Boolean.valueOf(paramGestioneConfigurazioni);
				ServletUtils.setObjectIntoSession(request, session, gestioneConfigurazioni+"", AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_CONFIGURAZIONI);
			}
			
			String idTab = apsHelper.getParametroInteger(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!apsHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(request, session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}
			
			// validazione parametri tipoSoggFru e nomeSoggFru
			
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
	
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
	
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
	
			apsHelper.serviziFruitoriMappingList(lista, idServizio, idSoggFruitoreDelServizio, idSoggettoFruitore, idFruizione, ricerca);
	
			ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			// Forward control to the specified success URI
			ForwardParams fwP = apsHelper.isModalitaCompleta() ? ForwardParams.LIST() : AccordiServizioParteSpecificaCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE;
			
			return ServletUtils.getStrutsForward (mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE, fwP);
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE,
					ForwardParams.LIST());
		}  
	}
}
