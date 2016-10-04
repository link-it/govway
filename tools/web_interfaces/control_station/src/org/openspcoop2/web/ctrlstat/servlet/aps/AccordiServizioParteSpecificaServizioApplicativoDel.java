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


package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.dao.PoliticheSicurezza;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * serviziServizioApplicativoDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaServizioApplicativoDel extends Action {

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

		try {
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			String idDelServizio = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			String idFruitoreDelServizio = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO);

			PageData oldPD = ServletUtils.getPageDataFromSession(session);
			pd.setHidden(oldPD.getHidden());// metto i valori nel pd nuovo

			String idSoggettoErogatoreDelServizio = oldPD.getHidden(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);
			int idSoggErogatoreServizioInt = Integer.parseInt(idSoggettoErogatoreDelServizio);

			int idDelServizioInt = Integer.parseInt(idDelServizio);
			int idFruitoreDelServizioInt = Integer.parseInt(idFruitoreDelServizio);
			// int idServizioApplicativo = 0;

			String nomeSoggFruitoreServ = "";
			String tipoSoggFruitoreServ = "";
			String nomeServizio = "";
			String tipoServizio = "";
			// String nomeSoggErogatoreServ = "";
			// String tipoSoggErogatoreServ = "";
			String nomeSoggettoErogatore = "";
			String tipoSoggettoErogatore = "";
			// String pdd = "";

			String objToRemove = request.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
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

			// prendo nome e tipo del soggetto fruitore del servizio
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(apsCore);
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apsCore);
			
			Soggetto soggetto = soggettiCore.getSoggetto(idFruitoreDelServizioInt);
			// org.openspcoop2.core.registry.Soggetto soggettoReg = core
			// .getSoggettoRegistro(idFruitoreDelServizioInt);
			nomeSoggFruitoreServ = soggetto.getNome();
			tipoSoggFruitoreServ = soggetto.getTipo();
			// pdd = soggettoReg.getServer();

			// prendo nome e tipo del servizio
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idDelServizioInt);
			Servizio servizio = asps.getServizio();
			nomeServizio = servizio.getNome();
			tipoServizio = servizio.getTipo();

			org.openspcoop2.core.registry.Soggetto soggEr = soggettiCore.getSoggettoRegistro(idSoggErogatoreServizioInt);
			nomeSoggettoErogatore = soggEr.getNome();
			tipoSoggettoErogatore = soggEr.getTipo();

			// String tmpTitle = tipoServizio + "/" + nomeServizio
			// + " erogato da " + tipoSoggettoErogatore + "/"
			// + nomeSoggettoErogatore;

			for (int i = 0; i < idsToRemove.size(); i++) {

				// DataElement de = (DataElement) ((Vector<?>) pdold.getDati()
				// .elementAt(idToRemove[i])).elementAt(0);
				// servizioApplicativo = de.getValue();
				servizioApplicativo = idsToRemove.get(i);

				if (!servizioApplicativo.equals("--") || servizioApplicativo.equals("")) {

					// recupero l'id del servizioApplicativo
					int idSA = 0;
					for (int j = 0; j < soggetto.sizeServizioApplicativoList(); j++) {
						ServizioApplicativo tmpSA = soggetto.getServizioApplicativo(j);
						String nome = tmpSA.getNome();
						if (nome.equals(servizioApplicativo))
							idSA = tmpSA.getId().intValue();
					}

					PoliticheSicurezza ps = apsCore.getPoliticheSicurezza(idFruitoreDelServizioInt, idDelServizioInt, idSA);

					// Elimino la politica di sicurezza
					apsCore.deletePoliticheSicurezza(ps);

					// elimino l'associazione nella
					// portadom_porte_delegate_servizioApplicativo
					// mi servono il nome del ServizioApplicativo
					// e l'id della porta delegata (che recupero tramite
					// id_fruitore tipo e nome Erogatore, tipo e nome
					// Servizio)
					// la variabile idErogatore in questo caso contiene l'id
					// del soggetto Fruitore nel caso delle portadelegata
					String idporta = tipoSoggFruitoreServ + nomeSoggFruitoreServ + "/" + tipoSoggettoErogatore + nomeSoggettoErogatore + "/" + tipoServizio + nomeServizio;
					IDSoggetto ids = new IDSoggetto(tipoSoggFruitoreServ, nomeSoggFruitoreServ);
					IDPortaDelegata idpd = new IDPortaDelegata();
					idpd.setSoggettoFruitore(ids);
					idpd.setLocationPD(idporta);
					if (porteDelegateCore.existsPortaDelegata(idpd)) {
						PortaDelegata pde = porteDelegateCore.getPortaDelegata(idpd);
						for (int j = 0; j < pde.sizeServizioApplicativoList(); j++) {
							ServizioApplicativo sa = pde.getServizioApplicativo(j);
							if (servizioApplicativo.equals(sa.getNome())) {
								pde.removeServizioApplicativo(j);
								break;
							}
						}

						String superUser  = ServletUtils.getUserLoginFromSession(session);
						apsCore.performUpdateOperation(superUser, apsHelper.smista(), pde);
					}
				}

			}

			// Preparo il menu
			apsHelper.makeMenu();

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.SERVIZI_SERVIZIO_APPLICATIVO;

			ricerca = apsHelper.checkSearchParameters(idLista, ricerca);

			List<ServizioApplicativo> lista = saCore.serviziServizioApplicativoList(Integer.parseInt(idFruitoreDelServizio), Integer.parseInt(idDelServizio), ricerca);

			apsHelper.prepareServiziServizioApplicativoList(lista, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_SERVIZI_APPLICATIVI, 
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_SERVIZI_APPLICATIVI,
					ForwardParams.DEL());
		}  
	}
}
