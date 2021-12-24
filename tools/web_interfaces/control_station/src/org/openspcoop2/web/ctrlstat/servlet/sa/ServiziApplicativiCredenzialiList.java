/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.sa;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * ServiziApplicativiCredenzialiList
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ServiziApplicativiCredenzialiList extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			ServiziApplicativiHelper saHelper = new ServiziApplicativiHelper(request, pd, session);
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione
			Integer parentSA = ServletUtils.getIntegerAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT, session);
			if(parentSA == null) parentSA = ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE;
			Boolean useIdSogg = parentSA == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO;
			
			parentSA = useIdSogg ? ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO : ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE;
			
			String id = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID);
			int idServizioApplicativo = Integer.parseInt(id);

			// salvo il punto di ingresso
			ServletUtils.setObjectIntoSession(session, parentSA, ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT);

			// Preparo il menu
			saHelper.makeMenu();
			
			ServiziApplicativiCore saCore = new ServiziApplicativiCore();
			
			ServizioApplicativo sa = saCore.getServizioApplicativo(idServizioApplicativo);

			saHelper.prepareServizioApplicativoCredenzialiList(sa, id);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForward(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_CREDENZIALI, ForwardParams.LIST());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_CREDENZIALI, ForwardParams.LIST());
		}
	}
}
