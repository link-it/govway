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


package org.openspcoop2.web.ctrlstat.servlet.pd;

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
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * porteDelegateServizioApplicativoAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteDelegateServizioApplicativoAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			String id = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String servizioApplicativo = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO_APPLICATIVO);

			// Preparo il menu
			porteDelegateHelper.makeMenu();

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			
			// Prendo nome, tipo e pdd del soggetto
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteDelegateCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(porteDelegateCore);
			
			String tmpTitle = null;
			IDSoggetto idSoggetto = null;
			if(porteDelegateCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggInt);
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
				idSoggetto = new IDSoggetto(soggetto.getTipo(),soggetto.getNome());
			}
			else{
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(soggInt);
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
				idSoggetto = new IDSoggetto(soggetto.getTipo(),soggetto.getNome());
			}
			// String pdd = soggetto.getServer();

			// Prendo nome della porta delegata
			PortaDelegata pde = porteDelegateCore.getPortaDelegata(idInt);
			CredenzialeTipo tipoAutenticazione = CredenzialeTipo.toEnumConstant(pde.getAutenticazione());
			String idporta = pde.getNome();

			// Se servizioApplicativohid = null, devo visualizzare la pagina per
			// l'inserimento dati
			if(	ServletUtils.isEditModeInProgress(request)){
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTI, null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
						new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + tmpTitle, 
								PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST,
								new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
								),
						new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZIO_APPLICATIVO_DI + idporta, 
										PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_SERVIZIO_APPLICATIVO_LIST,
										new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id),
										new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)  
										),
								new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null)
						);
 

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				// Prendo la lista di servizioApplicativo associati al soggetto
				// e la metto in un array
				Vector<String> silV = new Vector<String>();
				List<ServizioApplicativo> oldSilList = saCore.soggettiServizioApplicativoList(idSoggetto,userLogin,tipoAutenticazione);
				for (int i = 0; i < oldSilList.size(); i++) {
					ServizioApplicativo singleSA = oldSilList.get(i);
					String tmpNome = singleSA.getNome();
					boolean trovatoSA = false;
					for (int j = 0; j < pde.sizeServizioApplicativoList(); j++) {
						ServizioApplicativo tmpSA = pde.getServizioApplicativo(j);
						if (tmpNome.equals(tmpSA.getNome())) {
							trovatoSA = true;
							break;
						}
					}
					if (!trovatoSA)
						silV.add(tmpNome);
				}
				String[] servizioApplicativoList = null;
				if (silV.size() > 0) {
					servizioApplicativoList = new String[silV.size()];
					for (int j = 0; j < silV.size(); j++)
						servizioApplicativoList[j] = silV.get(j);
				}

				dati = porteDelegateHelper.addPorteServizioApplicativoToDati(TipoOperazione.ADD,dati, "", servizioApplicativoList);

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD,id, idsogg, null, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_SERVIZIO_APPLICATIVO, ForwardParams.ADD());

			}

			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.porteDelegateServizioApplicativoCheckData(TipoOperazione.ADD);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTI, null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
						new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + tmpTitle, 
								PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST,
								new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
								),
						new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZIO_APPLICATIVO_DI + idporta, 
										PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_SERVIZIO_APPLICATIVO_LIST,
										new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id),
										new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
										),
								new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null)
						);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				// Prendo la lista di servizioApplicativo (tranne quelli già
				// usati) e la metto in un array
				Vector<String> silV = new Vector<String>();
				List<ServizioApplicativo> oldSilList = saCore.soggettiServizioApplicativoList(idSoggetto,userLogin,tipoAutenticazione);
				for (int i = 0; i < oldSilList.size(); i++) {
					ServizioApplicativo singleSA = oldSilList.get(i);
					String tmpNome = singleSA.getNome();
					boolean trovatoSA = false;
					for (int j = 0; j < pde.sizeServizioApplicativoList(); j++) {
						ServizioApplicativo tmpSA = pde.getServizioApplicativo(j);
						if (tmpNome.equals(tmpSA.getNome())) {
							trovatoSA = true;
							break;
						}
					}
					if (!trovatoSA)
						silV.add(tmpNome);
				}
				String[] servizioApplicativoList = null;
				if (silV.size() > 0) {
					servizioApplicativoList = new String[silV.size()];
					for (int j = 0; j < silV.size(); j++)
						servizioApplicativoList[j] = silV.get(j);
				}

				dati = porteDelegateHelper.addPorteServizioApplicativoToDati(TipoOperazione.ADD, dati, servizioApplicativo, servizioApplicativoList);

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD,id, idsogg, null, dati);
 
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_SERVIZIO_APPLICATIVO,
						ForwardParams.ADD());
			}

			// Inserisco il servizioApplicativo nel db
			ServizioApplicativo sa = new ServizioApplicativo();
			sa.setNome(servizioApplicativo);
			pde.addServizioApplicativo(sa);

			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), pde);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			int idLista = Liste.PORTE_DELEGATE_SERVIZIO_APPLICATIVO;

			ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);

			List<ServizioApplicativo> lista = porteDelegateCore.porteDelegateServizioApplicativoList(Integer.parseInt(id), ricerca);

			porteDelegateHelper.preparePorteDelegateServizioApplicativoList(idporta, ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			// Forward control to the specified success URI
		 	return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_SERVIZIO_APPLICATIVO,
		 			ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_SERVIZIO_APPLICATIVO, 
					ForwardParams.ADD());
		}  
	}


}
