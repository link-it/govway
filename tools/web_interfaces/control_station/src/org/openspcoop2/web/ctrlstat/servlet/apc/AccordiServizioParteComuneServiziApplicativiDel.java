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


package org.openspcoop2.web.ctrlstat.servlet.apc;
/*
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.DBManager;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.dao.PoliticheSicurezza;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
*/

/**
 * accordiServizioApplicativoDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComuneServiziApplicativiDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		// SERVE??
		
		/*
		
		HttpSession session = request.getSession(true);

		// Salvo il vecchio PageData
		// PageData pdold = (PageData) session.getAttribute("PageData");

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper gh = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = gh.initGeneralData("/pddConsole/accordiServizioApplicativoDel.do");

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
			ConsoleHelper ch = new ConsoleHelper(request, pd, session);
			ControlStationCore core = new ControlStationCore();
			SoggettiCore soggettiCore = new SoggettiCore(core);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(core);
			// String idAccordo = request.getParameter("id");
			String idSoggettoErogatoreDelServizio = request.getParameter("idsogg");

			// PageData oldPD = (PageData) session.getAttribute("PageData");

			// String myId = oldPD.getHidden("myId");

			// rimetto i valori nel pd
			// pd.setHidden(oldPD.getHidden());

			// tramite myId accedo a regserv_servizio_fruitori
			// e recuper le informazioni sul soggetto fruitore e sul servizio
			int idDelServizio = Integer.parseInt(request.getParameter("idDelServizio"));
			int idServizioApplicativo = 0;
			Long idSoggettoFruitoreDelServizio = new Long(request.getParameter("idSoggettoFruitoreDelServizio"));
			String nomeSoggFruitoreServ = "";
			String tipoSoggFruitoreServ = "";
			String nomeServizio = "";
			String tipoServizio = "";
			// String pdd = "";
			// int idFruitore = Integer.parseInt(idAccordo);
			int idErogatore = Integer.parseInt(idSoggettoErogatoreDelServizio);

			String objToRemove = request.getParameter("obj");
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			// Elimino il servizioApplicativo della porta delegata dal db
			// StringTokenizer objTok = new StringTokenizer(objToRemove, ",");
			// int[] idToRemove = new int[objTok.countTokens()];
			//
			// int k = 0;
			// while (objTok.hasMoreElements()) {
			// idToRemove[k++] = Integer.parseInt(objTok.nextToken());
			// }

			String servizioApplicativo = "";

			String tipoSICA = request.getParameter("tipoSICA");
			if("".equals(tipoSICA))
				tipoSICA = null;
			
			try {
				con.setAutoCommit(false);

				// int myIDint = Integer.parseInt(myId);

				// Fruitore myFru = core.getServizioFruitore(myIDint);
				// IDSoggetto ids = new IDSoggetto(myFru.getTipo(),
				// myFru.getNome());
				// Soggetto soggFru = core.getSoggettoRegistro(ids);
				// idSoggettoFruitoreDelServizio = soggFru.getId();
				// idDelServizio = myFru.getIdServizio().intValue();

				// prendo nome e tipo del soggetto fruitore del servizio
				Soggetto mySogg = soggettiCore.getSoggettoRegistro(idSoggettoFruitoreDelServizio);
				nomeSoggFruitoreServ = mySogg.getNome();
				tipoSoggFruitoreServ = mySogg.getTipo();

				// prendo nome e tipo del servizio
				AccordoServizioParteSpecifica asps = core.getAccordoServizioParteSpecifica(idDelServizio);
				Servizio myServ = asps.getServizio();
				nomeServizio = myServ.getNome();
				tipoServizio = myServ.getTipo();

				// recupero nome e tipo soggetto erogatore
				Soggetto mySogg2 = soggettiCore.getSoggettoRegistro(idErogatore);
				String nomeSoggettoErogatore = mySogg2.getNome();
				String tipoSoggettoErogatore = mySogg2.getTipo();
				for (int i = 0; i < idsToRemove.size(); i++) {

					// DataElement de = (DataElement) ((Vector<?>)
					// pdold.getDati()
					// .elementAt(idToRemove[i])).elementAt(0);
					// servizioApplicativo = de.getValue();
					servizioApplicativo = idsToRemove.get(i);

					if (!servizioApplicativo.equals("--") && !servizioApplicativo.equals("")) {

						// recuper l'id del servizioApplicativo

						ServizioApplicativo sa = saCore.getServizioApplicativo(servizioApplicativo, nomeSoggFruitoreServ, tipoSoggFruitoreServ);
						idServizioApplicativo = sa.getId().intValue();
						PoliticheSicurezza ps = core.getPoliticheSicurezza(idSoggettoFruitoreDelServizio, idDelServizio, idServizioApplicativo);
						// int idPS = ps.getId().intValue();

						core.deletePoliticheSicurezza(ps);

						// elimino l'associazione nella
						// portadom_porte_delegate_servizioApplicativo
						// mi servono il nome del ServizioApplicativo
						// e l'id della porta delegata (che recupero tramite
						// id_fruitore tipo e nome Erogatore, tipo e nome
						// Servizio)
						String idporta = tipoSoggFruitoreServ + nomeSoggFruitoreServ + "/" + tipoSoggettoErogatore + nomeSoggettoErogatore + "/" + tipoServizio + nomeServizio;
						IDSoggetto idsf = new IDSoggetto(tipoSoggFruitoreServ, nomeSoggFruitoreServ);
						IDPortaDelegata idpd = new IDPortaDelegata();
						idpd.setSoggettoFruitore(idsf);
						idpd.setLocationPD(idporta);
						if (core.existsPortaDelegata(idpd)) {
							PortaDelegata pde = core.getPortaDelegata(idpd);
							for (int j = 0; j < pde.sizeServizioApplicativoList(); j++) {
								ServizioApplicativo singleSA = pde.getServizioApplicativo(j);
								if (servizioApplicativo.equals(singleSA.getNome())) {
									pde.removeServizioApplicativo(j);
									break;
								}
							}

							String superUser = (String) session.getAttribute("Login");
							core.performUpdateOperation(superUser, ch.smista(), pde);
						}
					}

				}
			} catch (SQLException ex) {
				// java.util.Date now = new java.util.Date();
				try {
					con.rollback();
				} catch (SQLException exrool) {
				}
				// Chiudo la connessione al DB
				dbM.releaseConnection(con);
				pd.setMessage("Il sistema &egrave; momentaneamente indisponibile.<BR>Si prega di riprovare pi&ugrave; tardi");
				session.setAttribute("GeneralData", gd);
				session.setAttribute("PageData", pd);
				// Remove the Form Bean - don't need to carry values forward
				// con jboss 4.2.1 produce errore:
				// request.removeAttribute(mapping.getAttribute());
				return (mapping.findForward("Error"));
			}

			// Preparo il menu
			ch.makeMenu();

			// Preparo la lista
			List<ServizioApplicativo> lista = saCore.accordiServizioApplicativoList(idSoggettoFruitoreDelServizio.intValue(), idDelServizio, new Search());

			ch.prepareAccordiServizioApplicativoList(lista, new Search(),tipoSICA);

			// Chiudo la connessione al DB
			dbM.releaseConnection(con);

			session.setAttribute("GeneralData", gd);
			session.setAttribute("PageData", pd);

			// Remove the Form Bean - don't need to carry values forward
			// con jboss 4.2.1 produce errore:
			// request.removeAttribute(mapping.getAttribute());

			// Forward control to the specified success URI
			return (mapping.findForward("DelAccordiServizioApplicativo"));
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
