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


package org.openspcoop2.web.ctrlstat.servlet.pd;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRichiesta;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.driver.db.IDServizioApplicativoDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.ApiKeyState;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
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
public final class PorteDelegateTrasformazioniServizioApplicativoAdd extends Action {

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
			String idPorta = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(idPorta);
			String idsogg = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String servizioApplicativo = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO);

			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idFruizione = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";
			
			String idTrasformazioneS = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_TRASFORMAZIONE);
			long idTrasformazione = Long.parseLong(idTrasformazioneS);
			
			String listaTmp = porteDelegateHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_APPLICABILITA_LIST);
			boolean fromList = false;
			if(listaTmp != null && !"".equals(listaTmp))
				fromList = true;
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
			
			// Preparo il menu
			porteDelegateHelper.makeMenu();

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			
			// Prendo nome, tipo e pdd del soggetto
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteDelegateCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(porteDelegateCore);
			
			IDSoggetto idSoggetto = null;
			if(porteDelegateCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggInt);
				idSoggetto = new IDSoggetto(soggetto.getTipo(),soggetto.getNome());
			}
			else{
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(soggInt);
				idSoggetto = new IDSoggetto(soggetto.getTipo(),soggetto.getNome());
			}
			// String pdd = soggetto.getServer();

			// Prendo nome della porta delegata
			PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			CredenzialeTipo tipoAutenticazione = CredenzialeTipo.toEnumConstant(portaDelegata.getAutenticazione());
			Boolean appId = null;
			if(CredenzialeTipo.APIKEY.equals(tipoAutenticazione)) {
				ApiKeyState apiKeyState =  new ApiKeyState(porteDelegateCore.getParametroAutenticazione(portaDelegata.getAutenticazione(), portaDelegata.getProprietaAutenticazioneList()));
				appId = apiKeyState.appIdSelected;
			}
			String nomePorta = portaDelegata.getNome();
			
			Trasformazioni trasformazioni = portaDelegata.getTrasformazioni();
			TrasformazioneRegola regola = null;
			for (int j = 0; j < trasformazioni.sizeRegolaList(); j++) {
				TrasformazioneRegola regolaTmp = trasformazioni.getRegola(j);
				if (regolaTmp.getId().longValue() == idTrasformazione) {
					regola = regolaTmp;
					break;
				}
			}
			
			String nomeTrasformazione = regola.getNome();
			Parameter pIdTrasformazione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_TRASFORMAZIONE, idTrasformazione+"");

			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, idPorta);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFruizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);
						
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI,
						portaDelegata);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_DI+nomePorta;
			}

			lstParam.add(new Parameter(labelPerPorta, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_TRASFORMAZIONI_LIST, pId, pIdSoggetto, pIdAsps, pIdFruizione));
			
			if(!fromList) {
				lstParam.add(new Parameter(nomeTrasformazione, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_TRASFORMAZIONI_CHANGE, 
						pId, pIdSoggetto, pIdAsps, pIdFruizione, pIdTrasformazione));
			}
			
			String labelPagLista = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO_CONFIG;
			if(!porteDelegateHelper.isModalitaCompleta()) {
				labelPagLista = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_APPLICATIVO_CONFIG;
			}
			
			List<Parameter> parametersList = new ArrayList<>();
			parametersList.add(pId);
			parametersList.add(pIdSoggetto);
			parametersList.add(pIdAsps);
			parametersList.add(pIdFruizione);
			parametersList.add(pIdTrasformazione);
			if(fromList) {
				parametersList.add(new Parameter (PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_APPLICABILITA_LIST, listaTmp));
			}
			lstParam.add(new Parameter(labelPagLista, 
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO_LIST,parametersList));
			lstParam.add(ServletUtils.getParameterAggiungi());
			
			// Se servizioApplicativohid = null, devo visualizzare la pagina per
			// l'inserimento dati
			if(	porteDelegateHelper.isEditModeInProgress()){
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				TrasformazioneRegolaApplicabilitaRichiesta applicabilita = regola.getApplicabilita();
				
				// Prendo la lista di servizioApplicativo associati al soggetto
				// e la metto in un array
				Vector<String> silV = new Vector<String>();
				List<IDServizioApplicativoDB> oldSilList = saCore.soggettiServizioApplicativoList(idSoggetto,userLogin,tipoAutenticazione,appId);
				for (int i = 0; i < oldSilList.size(); i++) {
					IDServizioApplicativoDB singleSA = oldSilList.get(i);
					String tmpNome = singleSA.getNome();
					boolean trovatoSA = false;
					if(applicabilita != null) {
						for (int j = 0; j < applicabilita.sizeServizioApplicativoList(); j++) {
							TrasformazioneRegolaApplicabilitaServizioApplicativo tmpSA = applicabilita.getServizioApplicativo(j);
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
				if (silV.size() > 0) {
					servizioApplicativoList = new String[silV.size()];
					for (int j = 0; j < silV.size(); j++)
						servizioApplicativoList[j] = silV.get(j);
				}

				dati = porteDelegateHelper.addPorteTrasformazioniServizioApplicativoToDati(TipoOperazione.ADD,dati, idTrasformazioneS, fromList, "", servizioApplicativoList, oldSilList.size(),true, true);

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, null, idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO, ForwardParams.ADD());

			}

			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.porteDelegateTrasformazioniServizioApplicativoCheckData(TipoOperazione.ADD);

			TrasformazioneRegolaApplicabilitaServizioApplicativo sa = new TrasformazioneRegolaApplicabilitaServizioApplicativo();
			sa.setNome(servizioApplicativo);
			sa.setTipoSoggettoProprietario(portaDelegata.getTipoSoggettoProprietario());
			sa.setNomeSoggettoProprietario(portaDelegata.getNomeSoggettoProprietario());
			
			if(isOk) {
				
				List<TrasformazioneRegolaApplicabilitaServizioApplicativo> sacheckList = new ArrayList<>();
				sacheckList.add(sa);
				
				String patternDBCheck = (regola.getApplicabilita() != null && StringUtils.isNotEmpty(regola.getApplicabilita().getPattern())) ? regola.getApplicabilita().getPattern() : null;
				String contentTypeAsString = (regola.getApplicabilita() != null &&regola.getApplicabilita().getContentTypeList() != null) ? StringUtils.join(regola.getApplicabilita().getContentTypeList(), ",") : "";
				String contentTypeDBCheck = StringUtils.isNotEmpty(contentTypeAsString) ? contentTypeAsString : null;
				String azioniAsString = (regola.getApplicabilita() != null && regola.getApplicabilita().getAzioneList() != null) ? StringUtils.join(regola.getApplicabilita().getAzioneList(), ",") : "";
				String azioniDBCheck = StringUtils.isNotEmpty(azioniAsString) ? azioniAsString : null;
				TrasformazioneRegola trasformazioneDBCheck_criteri = porteDelegateCore.getTrasformazione(Long.parseLong(idPorta), azioniDBCheck, patternDBCheck, contentTypeDBCheck, sacheckList);
				if(trasformazioneDBCheck_criteri!=null) {
					isOk = false;
					pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_REGOLA_TRASFORMAZIONE_APPLICABILITA_DUPLICATA_APPLICATIVO);
				}
				
			}
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				TrasformazioneRegolaApplicabilitaRichiesta applicabilita = regola.getApplicabilita();
				// Prendo la lista di servizioApplicativo (tranne quelli già
				// usati) e la metto in un array
				Vector<String> silV = new Vector<String>();
				List<IDServizioApplicativoDB> oldSilList = saCore.soggettiServizioApplicativoList(idSoggetto,userLogin,tipoAutenticazione,appId);
				for (int i = 0; i < oldSilList.size(); i++) {
					IDServizioApplicativoDB singleSA = oldSilList.get(i);
					String tmpNome = singleSA.getNome();
					boolean trovatoSA = false;
					if(applicabilita != null) {
						for (int j = 0; j < applicabilita.sizeServizioApplicativoList(); j++) {
							TrasformazioneRegolaApplicabilitaServizioApplicativo tmpSA = applicabilita.getServizioApplicativo(j);
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
				if (silV.size() > 0) {
					servizioApplicativoList = new String[silV.size()];
					for (int j = 0; j < silV.size(); j++)
						servizioApplicativoList[j] = silV.get(j);
				}

				dati = porteDelegateHelper.addPorteTrasformazioniServizioApplicativoToDati(TipoOperazione.ADD, dati, idTrasformazioneS, fromList, servizioApplicativo, servizioApplicativoList, oldSilList.size(),true, true);

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, null, idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);
 
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO,
						ForwardParams.ADD());
			}

			// salvataggio nuova regola
			for (int j = 0; j < trasformazioni.sizeRegolaList(); j++) {
				TrasformazioneRegola regolaTmp = trasformazioni.getRegola(j);
				if (regolaTmp.getId().longValue() == idTrasformazione) {
					regola = regolaTmp;
					break;
				}
			}
						
			// Inserisco il servizioApplicativo nel db			
			if(regola.getApplicabilita() == null)
				regola.setApplicabilita(new TrasformazioneRegolaApplicabilitaRichiesta());
			
			regola.getApplicabilita().addServizioApplicativo(sa);

			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), portaDelegata);
			
			// ricaricare id trasformazione
			portaDelegata = porteDelegateCore.getPortaDelegata(Long.parseLong(idPorta));

			TrasformazioneRegola trasformazioneAggiornata = porteDelegateCore.getTrasformazione(Long.parseLong(idPorta), regola.getNome());

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			int idLista = Liste.PORTE_DELEGATE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO;

			ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
			
			List<TrasformazioneRegolaApplicabilitaServizioApplicativo> lista = porteDelegateCore.porteDelegateTrasformazioniServiziApplicativiAutorizzatiList(Long.parseLong(idPorta), trasformazioneAggiornata.getId(), ricerca);

			porteDelegateHelper.preparePorteDelegateTrasformazioniServizioApplicativoList(portaDelegata.getNome(), trasformazioneAggiornata.getId(), ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			// Forward control to the specified success URI
		 	return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO,
		 			ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO, 
					ForwardParams.ADD());
		}  
	}


}
