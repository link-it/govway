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
package org.openspcoop2.web.ctrlstat.servlet.about;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

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
			List<Parameter> lstParam = new ArrayList<>();
			lstParam.add(new Parameter(AboutCostanti.LABEL_ABOUT, null));

			ServletUtils.setPageDataTitle(pd, lstParam);
			
			// preparo i campi
			List<DataElement> dati = new ArrayList<>();

			BinaryParameter infoP = aHelper.getBinaryParameter(AboutCostanti.PARAMETRO_ABOUT_INFO);
			
			String infoDone = aHelper.getParameter(AboutCostanti.PARAMETRO_ABOUT_INFO_FINISH);
			boolean doUpdate = ServletUtils.isCheckBoxEnabled(infoDone);
			
			String aggiornamentoNonRiuscito = null;
			String aggiornamentoEffettuato = null;
			if(doUpdate) {
				if(infoP.getValue()!=null) {
					try {
						aHelper.getCore().updateInfoVersion(request, session, new String(infoP.getValue()));
						aggiornamentoEffettuato = "Aggiornamento completato con successo";
						gd.setTitle(StringEscapeUtils.escapeHtml(aHelper.getCore().getConsoleNomeEsteso(request, session)));
					}catch(Exception e) {
						aggiornamentoNonRiuscito = "Aggiornamento fallito: "+e.getMessage();
					}
				}
				else {
					aggiornamentoNonRiuscito = "Licenza non fornita";
				}
			}
			
			dati = aHelper.addAboutToDati(dati,TipoOperazione.OTHER,userLogin,infoP);
			
			if(!StringUtils.isEmpty(aggiornamentoNonRiuscito)) {
				pd.setMessage(aggiornamentoNonRiuscito, MessageType.ERROR);
			}
			else if(!StringUtils.isEmpty(aggiornamentoEffettuato)) {
				pd.setMessage(aggiornamentoEffettuato, MessageType.INFO);
			}
			
			pd.setDati(dati);
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AboutCostanti.OBJECT_NAME_ABOUT, 
					AboutCostanti.TIPO_OPERAZIONE_ABOUT);
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AboutCostanti.OBJECT_NAME_ABOUT, AboutCostanti.TIPO_OPERAZIONE_ABOUT);
		}  
	}
}
