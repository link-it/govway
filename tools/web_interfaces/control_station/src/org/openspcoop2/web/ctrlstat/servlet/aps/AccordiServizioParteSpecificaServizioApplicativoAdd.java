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


package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

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
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.dao.PoliticheSicurezza;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * serviziServizioApplicativoAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaServizioApplicativoAdd extends Action {


	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
		
		try {
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			String idServizio = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			int idServizioInt = Integer.parseInt(idServizio);
			String idSoggFruitoreDelServizio = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO);
			int idSoggFruitoreDelServizioInt = Integer.parseInt(idSoggFruitoreDelServizio);
			
//			session.setAttribute("elemento", idServizio);
//			session.setAttribute("elemento1", idSoggFruitoreDelServizio);
			
			String servizioApplicativo = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_SERVIZIO_APPLICATIVO);

			PageData oldPD = ServletUtils.getPageDataFromSession(session);

			String myID = oldPD.getHidden(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID);
			String idSoggettoErogatore = oldPD.getHidden(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);

			ArrayList<?> serviziAggiunti = (ArrayList<?>) session.getAttribute(AccordiServizioParteSpecificaCostanti.SESSION_ATTRIBUTE_APS_SERVIZI_AGGIUNTI);

			// int idServizio = 0;

			String nomeSoggFruitoreServ = "";
			String tipoSoggFruitoreServ = "";
			String nomeServizio = "";
			String tipoServizio = "";
			// String pdd = "";

			// Preparo il menu
			apsHelper.makeMenu();

			// Prendo nome, tipo e pdd del soggetto
			// Prendo nome della porta delegata
			String tmpTitle = "";
			// String pdd = "";
			// String idporta = "";
			ArrayList<String> serviziApplicativiList = new ArrayList<String>();
			serviziApplicativiList.add("--"); // elemento nullo di default

			// prendo nome e tipo del soggetto fruitore del servizio
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(apsCore);
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apsCore);
			
			Soggetto soggetto = soggettiCore.getSoggetto(idSoggFruitoreDelServizioInt);
			// org.openspcoop2.core.registry.Soggetto soggettoReg = core
			// .getSoggettoRegistro(idSoggFruitoreDelServizioInt);
			nomeSoggFruitoreServ = soggetto.getNome();
			tipoSoggFruitoreServ = soggetto.getTipo();
			// pdd = soggettoReg.getServer();

			// prendo nome e tipo del servizio
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idServizioInt);
			Servizio servizio = asps.getServizio();
			nomeServizio = servizio.getNome();
			tipoServizio = servizio.getTipo();

			// Prendo il nome e il tipo del soggetto erogatore del servizio
			org.openspcoop2.core.registry.Soggetto soggEr = soggettiCore.getSoggettoRegistro(Integer.parseInt(idSoggettoErogatore));
			String tipoSoggettoErogatore = soggEr.getTipo();
			String nomeSoggettoErogatore = soggEr.getNome();

			tmpTitle = tipoServizio + "/" + nomeServizio + " erogato da " + tipoSoggettoErogatore + "/" + nomeSoggettoErogatore;
			// aggiorno tmpTitle
			String tmpVersione = asps.getVersione();
			if(apsCore.isShowVersioneAccordoServizioParteSpecifica()==false){
				tmpVersione = null;
			}
			tmpTitle = idAccordoFactory.getUriFromValues(asps.getNome(), 
					servizio.getTipoSoggettoErogatore(), servizio.getNomeSoggettoErogatore(), 
					tmpVersione);
			
			// recupero lista servizi applicativi
			for (int i = 0; i < soggetto.sizeServizioApplicativoList(); i++) {
				ServizioApplicativo tmpSA = soggetto.getServizioApplicativo(i);
				String nome = tmpSA.getNome();
				if (!serviziAggiunti.contains(nome))
					serviziApplicativiList.add(nome);
			}

			// cmq setto i dati hidden che mi servono sempre
			Hashtable<String, String> hidden = new Hashtable<String, String>();
			hidden.put(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, myID);
			hidden.put(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, idSoggettoErogatore);
			pd.setHidden(hidden);

			if (ServletUtils.isEditModeInProgress(request)) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST),
						new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI + tmpTitle, 
								AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST ,
								new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+idServizio),
								new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO, ""+idSoggFruitoreDelServizioInt),
								new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+idSoggettoErogatore),
								new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, nomeServizio),
								new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, tipoServizio),
								new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, myID)
								),
								new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_SERVIZI_APPLICATIVI_AUTORIZZATI_DI + tipoSoggFruitoreServ + "/" + nomeSoggFruitoreServ , null)
						);
				
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = apsHelper.addPorteServizioApplicativoToDati(TipoOperazione.ADD, dati, "", serviziApplicativiList.toArray(new String[serviziApplicativiList.size()]));

				dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idServizio, idSoggFruitoreDelServizio, null, dati);
				
				dati = apsHelper.addTipoNomeServizioToDati(TipoOperazione.ADD, myID, tipoServizio, nomeServizio, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_SERVIZI_APPLICATIVI,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			// nn c'e' nessun controllo da fare
			// boolean isOk = ch.accordiServizioApplicativoCheckData("add");
			// if (!isOk) {
			// TODO implementare
			// operazione da eseguire nel caso il check dati ritornasse false
			// }

			// Inserisco il servizioApplicativo nel db

			// se il servizio applicativo e' diverso da quello di default
			// allora lo inserisco nel db
			if (!servizioApplicativo.equals("--")) {
				PoliticheSicurezza polSic = new PoliticheSicurezza();
				polSic.setNomeServizioApplicativo(servizioApplicativo);
				polSic.setIdServizio(idServizioInt);
				polSic.setIdFruitore(idSoggFruitoreDelServizioInt);
				// Long idPS =
				//apsCore.createPoliticheSicurezza(polSic);
				String superUser =   ServletUtils.getUserLoginFromSession(session);
				apsCore.performCreateOperation(superUser, apsHelper.smista(), polSic);

				String idporta = tipoSoggFruitoreServ + nomeSoggFruitoreServ + "/" + tipoSoggettoErogatore + nomeSoggettoErogatore + "/" + tipoServizio + nomeServizio;
				IDSoggetto ids = new IDSoggetto(tipoSoggFruitoreServ, nomeSoggFruitoreServ);
				IDPortaDelegata idpd = new IDPortaDelegata();
				idpd.setSoggettoFruitore(ids);
				idpd.setLocationPD(idporta);
				if (porteDelegateCore.existsPortaDelegata(idpd)) {
					PortaDelegata pde = porteDelegateCore.getPortaDelegata(idpd);
					ServizioApplicativo sa = saCore.getServizioApplicativo(servizioApplicativo, nomeSoggFruitoreServ, tipoSoggFruitoreServ);
					pde.addServizioApplicativo(sa);

					apsCore.performUpdateOperation(superUser, apsHelper.smista(), pde);
				}
			}

			// preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.SERVIZI_SERVIZIO_APPLICATIVO;

			ricerca = apsHelper.checkSearchParameters(idLista, ricerca);

			List<ServizioApplicativo> lista = saCore.serviziServizioApplicativoList(Integer.parseInt(idSoggFruitoreDelServizio), Integer.parseInt(idServizio), ricerca);

			apsHelper.prepareServiziServizioApplicativoList(lista, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_SERVIZI_APPLICATIVI,
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_SERVIZI_APPLICATIVI,
					ForwardParams.ADD());
		}  
	}
}
