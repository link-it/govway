/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.pd;

import java.util.List;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAutorizzazioneServiziApplicativi;
import org.openspcoop2.core.config.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.driver.db.IDServizioApplicativoDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.ApiKeyState;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
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
			String id = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String servizioApplicativo = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO_APPLICATIVO);

			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idFruizione = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";
			
			String tokenList = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TOKEN_AUTHORIZATION);
			boolean isToken = tokenList!=null && !"".equals(tokenList) && Boolean.valueOf(tokenList);
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session, request);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
			
			// Preparo il menu
			porteDelegateHelper.makeMenu();

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			
			// Prendo nome, tipo e pdd del soggetto
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteDelegateCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(porteDelegateCore);
			
			boolean escludiSAServer = saCore.isApplicativiServerEnabled(porteDelegateHelper);
			String filtroTipoSA = escludiSAServer ? ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_CLIENT : null;
			
			IDSoggetto idSoggetto = null;
			if(porteDelegateCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggInt);
				idSoggetto = new IDSoggetto(soggetto.getTipo(),soggetto.getNome());
			}
			else{
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(soggInt);
				idSoggetto = new IDSoggetto(soggetto.getTipo(),soggetto.getNome());
			}

			// Prendo nome della porta delegata
			PortaDelegata pde = porteDelegateCore.getPortaDelegata(idInt);
			CredenzialeTipo tipoAutenticazione = null;
			Boolean appId = null;
			String tokenPolicy = null;
			if(isToken) {
				tipoAutenticazione = CredenzialeTipo.TOKEN;
				if(pde.getGestioneToken()!=null && pde.getGestioneToken().getPolicy()!=null) {
					tokenPolicy = pde.getGestioneToken().getPolicy();
				}
			}
			else {
				tipoAutenticazione = CredenzialeTipo.toEnumConstant(pde.getAutenticazione());
				if(CredenzialeTipo.APIKEY.equals(tipoAutenticazione)) {
					ApiKeyState apiKeyState =  new ApiKeyState(porteDelegateCore.getParametroAutenticazione(pde.getAutenticazione(), pde.getProprietaAutenticazioneList()));
					appId = apiKeyState.appIdSelected;
				}
			}
			String idporta = pde.getNome();

			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFrizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);
			Parameter[] urlParms = { pId,pIdSoggetto,pIdAsps,pIdFrizione,
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TOKEN_AUTHORIZATION, isToken+"") };
			
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI,
						pde);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI_CONFIG_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONTROLLO_ACCESSI, 
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, "" + pde.getId()),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA, pde.getNome()),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, pde.getIdSoggetto() + ""),
					pIdAsps, new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione+ "")));
			
			String labelApp = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZIO_APPLICATIVO_CONFIG;
			if(!porteDelegateHelper.isModalitaCompleta() || isToken) {
				labelApp = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_APPLICATIVO_CONFIG;
			}
			String labelPagLista = 
					(
							isToken ? 
							CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN 
							:
							CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_TRASPORTO
					)
					+ " - " +
					labelApp;
			
			lstParam.add(new Parameter(labelPagLista, 
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_SERVIZIO_APPLICATIVO_LIST,urlParms));
			lstParam.add(ServletUtils.getParameterAggiungi());
			
			// Se servizioApplicativohid = null, devo visualizzare la pagina per
			// l'inserimento dati
			if(	porteDelegateHelper.isEditModeInProgress()){
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				
				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				

				// Prendo la lista di servizioApplicativo associati al soggetto
				// e la metto in un array
				List<String> silV = new ArrayList<>();
				boolean bothSslAndToken = false;
				List<IDServizioApplicativoDB> oldSilList = saCore.soggettiServizioApplicativoList(idSoggetto,userLogin,tipoAutenticazione, appId, filtroTipoSA, 
						bothSslAndToken, tokenPolicy);
				for (int i = 0; i < oldSilList.size(); i++) {
					IDServizioApplicativoDB singleSA = oldSilList.get(i);
					String tmpNome = singleSA.getNome();
					boolean trovatoSA = false;
					if(isToken) {
						if(pde.getAutorizzazioneToken()!=null && pde.getAutorizzazioneToken().getServiziApplicativi()!=null) {
							for (int j = 0; j < pde.getAutorizzazioneToken().getServiziApplicativi().sizeServizioApplicativoList(); j++) {
								PortaDelegataServizioApplicativo tmpSA = pde.getAutorizzazioneToken().getServiziApplicativi().getServizioApplicativo(j);
								if (tmpNome.equals(tmpSA.getNome())) {
									trovatoSA = true;
									break;
								}
							}
						}
					}
					else {
						for (int j = 0; j < pde.sizeServizioApplicativoList(); j++) {
							PortaDelegataServizioApplicativo tmpSA = pde.getServizioApplicativo(j);
							if (tmpNome.equals(tmpSA.getNome())) {
								trovatoSA = true;
								break;
							}
						}
					}
					if (!trovatoSA)
						silV.add(tmpNome);
				}
				String[] servizioApplicativoList = null;
				if (!silV.isEmpty()) {
					servizioApplicativoList = new String[silV.size()];
					for (int j = 0; j < silV.size(); j++)
						servizioApplicativoList[j] = silV.get(j);
				}

				dati = porteDelegateHelper.addPorteServizioApplicativoToDati(TipoOperazione.ADD,dati, "", servizioApplicativoList, oldSilList.size(),true, true, isToken);

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, id, idsogg, null, idAsps, 
						idFruizione, pde.getTipoSoggettoProprietario(), pde.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_SERVIZIO_APPLICATIVO, ForwardParams.ADD());

			}

			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.porteDelegateServizioApplicativoCheckData(TipoOperazione.ADD);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				// Prendo la lista di servizioApplicativo (tranne quelli già
				// usati) e la metto in un array
				List<String> silV = new ArrayList<>();
				boolean bothSslAndToken = false;
				List<IDServizioApplicativoDB> oldSilList = saCore.soggettiServizioApplicativoList(idSoggetto,userLogin,tipoAutenticazione, appId, filtroTipoSA, 
						bothSslAndToken, tokenPolicy);
				for (int i = 0; i < oldSilList.size(); i++) {
					IDServizioApplicativoDB singleSA = oldSilList.get(i);
					String tmpNome = singleSA.getNome();
					boolean trovatoSA = false;
					for (int j = 0; j < pde.sizeServizioApplicativoList(); j++) {
						PortaDelegataServizioApplicativo tmpSA = pde.getServizioApplicativo(j);
						if (tmpNome.equals(tmpSA.getNome())) {
							trovatoSA = true;
							break;
						}
					}
					if (!trovatoSA)
						silV.add(tmpNome);
				}
				String[] servizioApplicativoList = null;
				if (!silV.isEmpty()) {
					servizioApplicativoList = new String[silV.size()];
					for (int j = 0; j < silV.size(); j++)
						servizioApplicativoList[j] = silV.get(j);
				}

				dati = porteDelegateHelper.addPorteServizioApplicativoToDati(TipoOperazione.ADD, dati, servizioApplicativo, servizioApplicativoList, oldSilList.size(),true, true, isToken);

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, id, idsogg, null, idAsps, 
						idFruizione, pde.getTipoSoggettoProprietario(), pde.getNomeSoggettoProprietario(), dati);
 
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_SERVIZIO_APPLICATIVO,
						ForwardParams.ADD());
			}

			// Inserisco il servizioApplicativo nel db
			PortaDelegataServizioApplicativo sa = new PortaDelegataServizioApplicativo();
			sa.setNome(servizioApplicativo);
			if(isToken) {
				if(pde.getAutorizzazioneToken().getServiziApplicativi()==null) {
					pde.getAutorizzazioneToken().setServiziApplicativi(new PortaDelegataAutorizzazioneServiziApplicativi());
				}
				pde.getAutorizzazioneToken().getServiziApplicativi().addServizioApplicativo(sa);
			}
			else {
				pde.addServizioApplicativo(sa);
			}

			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), pde);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
			
			int idLista = Liste.PORTE_DELEGATE_SERVIZIO_APPLICATIVO;
			if(isToken) {
				idLista = Liste.PORTE_DELEGATE_TOKEN_SERVIZIO_APPLICATIVO;
			}

			ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);

			List<ServizioApplicativo> lista = isToken ?
					porteDelegateCore.porteDelegateServizioApplicativoTokenList(Integer.parseInt(id), ricerca) 
					:
					porteDelegateCore.porteDelegateServizioApplicativoList(Integer.parseInt(id), ricerca) ;

			porteDelegateHelper.preparePorteDelegateServizioApplicativoList(idporta, ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			// Forward control to the specified success URI
		 	return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_SERVIZIO_APPLICATIVO,
		 			ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_SERVIZIO_APPLICATIVO, 
					ForwardParams.ADD());
		}  
	}


}
