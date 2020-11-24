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
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;

/**
 * serviziDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaDel extends Action {

	@SuppressWarnings("unused")
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
			ErogazioniHelper apsHelper = new ErogazioniHelper(request, pd, session);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apsCore);
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apsCore);
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(apsCore);
			PddCore pddCore = new PddCore(apsCore);
			ConfigurazioneCore confCore = new ConfigurazioneCore(apsCore);
			
			String tipologia = ServletUtils.getObjectFromSession(session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneFruitori = false;
			boolean gestioneErogatori = false;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
					gestioneFruitori = true;
				}
				else if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
					gestioneErogatori = true;
				}
			}
						
			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, session);
			
			//User utente = ServletUtils.getUserFromSession(session);
			
			/*
			 * Validate the request parameters specified by the user Note: Basic
			 * field validation done in porteDomForm.java Business logic
			 * validation done in porteDomAdd.java
			 */
			String objToRemove = apsHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			// Preparo il menu
			apsHelper.makeMenu();

			String superUser =   ServletUtils.getUserLoginFromSession(session); 

			// Elimino i servizi dal db
			// StringTokenizer objTok = new StringTokenizer(objToRemove, ",");
			// int[] idToRemove = new int[objTok.countTokens()];
			//
			// int k = 0;
			// while (objTok.hasMoreElements()) {
			// idToRemove[k++] = Integer.parseInt(objTok.nextToken());
			// }
			//
			// String nomeservizio = "", tiposervizio = "";
			String nomeservizio = "", tiposervizio = "";
			String nomesogg = "", tiposogg = "";
			// int idConnettore = 0;

			IExtendedListServlet extendedServlet = porteApplicativeCore.getExtendedServletPortaApplicativa();

			StringBuilder inUsoMessage = new StringBuilder();
			
			boolean deleteAlmostOneApi = false;
			
			for (int i = 0; i < idsToRemove.size(); i++) {

				
				IDSoggetto idSoggettoFruitore = null;
				
				String uri = idsToRemove.get(i);
				if(gestioneFruitori && uri.contains("@")) {
					String tipoNomeFruitore = uri.split("@")[1];
					uri = uri.split("@")[0];
					idSoggettoFruitore = new IDSoggetto(tipoNomeFruitore.split("/")[0], tipoNomeFruitore.split("/")[1]);
				}
				
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromUri(uri);
				AccordoServizioParteSpecifica asps = apsCore.getServizio(idServizio);
				
				boolean deleted = AccordiServizioParteSpecificaUtilities.deleteAccordoServizioParteSpecifica(asps, gestioneFruitori, gestioneErogatori,
						idSoggettoFruitore, idServizio, extendedServlet, superUser, apsCore, apsHelper, inUsoMessage, org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
				if(deleted) {
					deleteAlmostOneApi = true;
				}
				
			}// chiudo for

			if(deleteAlmostOneApi) {
				ServletUtils.removeRisultatiRicercaFromSession(session, Liste.SERVIZI);
			}
			
			// se ci sono messaggio di errore li presento
			if (inUsoMessage.length()>0) {
				pd.setMessage(inUsoMessage.toString());
			}

			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			PermessiUtente pu = ServletUtils.getUserFromSession(session).getPermessi();

			boolean [] permessi = new boolean[2];
			permessi[0] = pu.isServizi();
			permessi[1] = pu.isAccordiCooperazione();
			List<AccordoServizioParteSpecifica> lista = null;
			if(apsCore.isVisioneOggettiGlobale(superUser)){
				lista = apsCore.soggettiServizioList(null, ricerca,permessi, session);
			}else{
				lista = apsCore.soggettiServizioList(superUser, ricerca, permessi, session);
			}

			if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
				apsHelper.prepareErogazioniList(ricerca, lista);
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				return ServletUtils.getStrutsForward(mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI, ForwardParams.DEL());
			}
			
			apsHelper.prepareServiziList(ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
					ForwardParams.DEL());
		}  
	}
}
