/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.loader.servlet.about;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.loader.core.LoaderCore;
import org.openspcoop2.web.loader.servlet.GeneralHelper;

/**
 * About
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class About extends Action{


	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);

		try {

			AboutHelper aHelper = new AboutHelper(request, pd, session);

			// Preparo il menu
			aHelper.makeMenu();

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();
			lstParam.add(new Parameter(AboutCostanti.LABEL_ABOUT, null));

			ServletUtils.setPageDataTitle(pd, lstParam);
			
			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			dati = aHelper.addAboutToDati(dati,TipoOperazione.OTHER,userLogin);
			
			pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AboutCostanti.OBJECT_NAME_ABOUT, 
					AboutCostanti.TIPO_OPERAZIONE_ABOUT);
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(LoaderCore.log, e, pd, session, gd, mapping, 
					AboutCostanti.OBJECT_NAME_ABOUT, AboutCostanti.TIPO_OPERAZIONE_ABOUT);
		}  
	}
}
