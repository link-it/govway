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


package org.openspcoop2.web.ctrlstat.servlet.sa;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * servizioApplicativoList
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ServiziApplicativiList extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_USA_ID_SOGGETTO , session);


		try {
			String idsogg = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			long soggLong = -1;
			// Posso arrivare a questa pagina anche dal menu' senza specificare il soggetto
			if(idsogg != null){
				soggLong = Long.parseLong(idsogg);
				useIdSogg = true;
			} else {
				useIdSogg = false;
			}

			// salvo il punto di ingresso
			ServletUtils.setObjectIntoSession(session, useIdSogg, ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_USA_ID_SOGGETTO);

			ServiziApplicativiHelper saHelper = new ServiziApplicativiHelper(request, pd, session);

			// Preparo il menu
			saHelper.makeMenu();

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			

			

			ServiziApplicativiCore saCore = new ServiziApplicativiCore();

			String userLogin = (String) ServletUtils.getUserLoginFromSession(session);

			List<ServizioApplicativo> lista = null;

			if(!useIdSogg){
				int idLista = Liste.SERVIZIO_APPLICATIVO;
				ricerca = saHelper.checkSearchParameters(idLista, ricerca);
				if(saCore.isVisioneOggettiGlobale(userLogin)){
					lista = saCore.soggettiServizioApplicativoList(null, ricerca);
				}else{
					lista = saCore.soggettiServizioApplicativoList(userLogin, ricerca);
				}
			}else {
				int idLista = Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO;
				ricerca = saHelper.checkSearchParameters(idLista, ricerca);
				
				lista = saCore.soggettiServizioApplicativoList(ricerca,soggLong);
			}

			saHelper.prepareServizioApplicativoList(ricerca, lista);

			ServletUtils.setSearchObjectIntoSession(session, ricerca);
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForward(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, ForwardParams.LIST());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, ForwardParams.LIST());
		}
	}
}
