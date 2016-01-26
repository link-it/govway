/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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


package org.openspcoop2.web.ctrlstat.servlet.apc;

/*
import java.sql.Connection;
import java.util.List;
*/

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
/*
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.DBManager;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
*/

/**
 * accordiServizioApplicativoList
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComuneServiziApplicativiList extends Action {
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		/*
		
		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper gh = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = gh.initGeneralData("/pddConsole/accordiServizioApplicativoList.do");

		// Prendo il datasource
		DBManager dbM = DBManager.getInstance();
		Connection con = dbM.getConnection();
		if (con == null) {
			pd.setMessage("Il sistema &egrave; momentaneamente indisponibile.<BR>Si prega di riprovare pi&ugrave; tardi");
			session.setAttribute("GeneralData", gd);
			session.setAttribute("PageData", pd);
			// Remove the Form Bean - don't need to carry values forward
			// con jboss 4.2.1 produce errore:
			// request.removeAttribute(mapping.getAttribute());
			return (mapping.findForward("Error"));
		}

		try {
			String myID = request.getParameter("myId");
			if ((myID == null) || myID.equals("")) {
				PageData oldPD = (PageData) session.getAttribute("PageData");
				myID = oldPD.getHidden("myId");
			}
			// String correlato = request.getParameter("correlato");
			// int myIDint = Integer.parseInt(myID);
			String idDelServizioString = request.getParameter("idDelServizio");
			String idSoggettoFruitoreDelServizioString = request.getParameter("idSoggettoFruitoreDelServizio");
			int idServizio = Integer.parseInt(idDelServizioString);
			int idSoggettoFruitoreDelServizio = Integer.parseInt(idSoggettoFruitoreDelServizioString);

			String tipoSICA = request.getParameter("tipoSICA");
			if("".equals(tipoSICA))
				tipoSICA = null;
			
			// Prendo l'id del soggetto fruitore del servizio e l'id del servizio
			ControlStationCore core = new ControlStationCore();
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(core);
			// int idSoggettoFruitoreDelServizio =
			// core.getServizioFruitoreSoggettoFruitoreID(myIDint);
			// int idServizio = core.getServizioFruitoreServizioID(myIDint);
	
			ConsoleHelper ch = new ConsoleHelper(request, pd, session);
			// String id = request.getParameter("id");
	
			// Preparo il menu
			ch.makeMenu();
	
			// Preparo la lista
			Search ricerca = (Search) session.getAttribute("Ricerca");
			if (ricerca == null) {
				ricerca = new Search();
			}
	
			int idLista = Liste.ACCORDI_SERVIZIO_APPLICATIVO;
	
			ricerca = ch.checkSearchParameters(idLista, ricerca);
	
			List<ServizioApplicativo> lista = saCore.accordiServizioApplicativoList(idSoggettoFruitoreDelServizio, idServizio, ricerca);
	
			ch.prepareAccordiServizioApplicativoList(lista, ricerca,tipoSICA);
	
			// Chiudo la connessione al DB
			dbM.releaseConnection(con);
	
			ServletUtils.setSearchObjectIntoSession(session, ricerca);
			session.setAttribute("GeneralData", gd);
			session.setAttribute("PageData", pd);
	
			// Remove the Form Bean - don't need to carry values forward
			// con jboss 4.2.1 produce errore:
			// request.removeAttribute(mapping.getAttribute());
	
			// Forward control to the specified success URI
			return (mapping.findForward("ListAccordiServizioApplicativoOk"));
		} catch (Exception e) {
			ControlStationCore.logError("SistemaNonDisponibile: "+e.getMessage(), e);
			pd.setMessage("Il sistema &egrave; momentaneamente indisponibile.<BR>Si prega di riprovare pi&ugrave; tardi");
			session.setAttribute("GeneralData", gd);
			session.setAttribute("PageData", pd);
			// Remove the Form Bean - don't need to carry values forward
			// con jboss 4.2.1 produce errore:
			// request.removeAttribute(mapping.getAttribute());
			return (mapping.findForward("Error"));
		} finally {
			dbM.releaseConnection(con);
		}
		
		
		*/
		
		return null;
	}
}
