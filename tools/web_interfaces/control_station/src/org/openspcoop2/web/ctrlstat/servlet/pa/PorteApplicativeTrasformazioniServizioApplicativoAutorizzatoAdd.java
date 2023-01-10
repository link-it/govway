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


package org.openspcoop2.web.ctrlstat.servlet.pa;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRichiesta;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.driver.db.IDServizioApplicativoDB;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.ApiKeyState;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
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
 * PorteApplicativeServizioApplicativoAutorizzatoAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteApplicativeTrasformazioniServizioApplicativoAutorizzatoAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	
		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte applicative
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session, request);
			if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;
			String idPorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			
			String idSoggettoToAdd = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SOGGETTO);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			
			String idSAToAdd = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO);
			
			String idTrasformazioneS = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE);
			long idTrasformazione = Long.parseLong(idTrasformazioneS);

			String listaTmp = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_APPLICABILITA_LIST);
			boolean fromList = false;
			if(listaTmp != null && !"".equals(listaTmp))
				fromList = true;
			
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteApplicativeCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(porteApplicativeCore);

			boolean escludiSoggettoErogatore = false;

			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			boolean escludiSAServer = saCore.isApplicativiServerEnabled(porteApplicativeHelper);
			
			
			// Prendo nome, tipo e pdd del soggetto
			String protocollo = null;
			if(porteApplicativeCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggInt);
				protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(soggetto.getTipo());
			}
			else{
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(soggInt);
				protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(soggetto.getTipo());
			}
			
			boolean isSupportatoAutenticazioneApplicativiEsterni = saCore.isSupportatoAutenticazioneApplicativiEsterniErogazione(protocollo);

			//decodifica soggetto scelto
			String tipoSoggettoScelto = null;
			String nomeSoggettoScelto = null;
			String nomeSAScelto = null;
			if(idSoggettoToAdd != null) {
				long soggettoToAddInt = Long.parseLong(idSoggettoToAdd);
				
				if(porteApplicativeCore.isRegistroServiziLocale()){
					org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggettoToAddInt);
					tipoSoggettoScelto = soggetto.getTipo();
					nomeSoggettoScelto = soggetto.getNome();
				}
				else{
					org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(soggettoToAddInt);
					tipoSoggettoScelto = soggetto.getTipo();
					nomeSoggettoScelto = soggetto.getNome();
				}
				
				if(idSAToAdd != null) {
					long idSAToAddInt = Long.parseLong(idSAToAdd);
					ServizioApplicativo servizioApplicativo = saCore.getServizioApplicativo(idSAToAddInt);
					nomeSAScelto = servizioApplicativo.getNome();
				}
			}
			
			// Prendo nome della porta applicativa
			PortaApplicativa portaApplicativa = porteApplicativeCore.getPortaApplicativa(idInt);
			String nomePorta = portaApplicativa.getNome();
			CredenzialeTipo tipoAutenticazione = CredenzialeTipo.toEnumConstant(portaApplicativa.getAutenticazione());
			@SuppressWarnings("unused")
			Boolean appId = null;
			if(CredenzialeTipo.APIKEY.equals(tipoAutenticazione)) {
				ApiKeyState apiKeyState =  new ApiKeyState(porteApplicativeCore.getParametroAutenticazione(portaApplicativa.getAutenticazione(), portaApplicativa.getProprietaAutenticazioneList()));
				appId = apiKeyState.appIdSelected;
			}
					
			Trasformazioni trasformazioni = portaApplicativa.getTrasformazioni();
			TrasformazioneRegola regola = null;
			for (int j = 0; j < trasformazioni.sizeRegolaList(); j++) {
				TrasformazioneRegola regolaTmp = trasformazioni.getRegola(j);
				if (regolaTmp.getId().longValue() == idTrasformazione) {
					regola = regolaTmp;
					break;
				}
			}
			if(regola==null) {
				throw new Exception("TrasformazioneRegola con id '"+idTrasformazione+"' non trovata");
			}
			
			String nomeTrasformazione = regola.getNome();
			Parameter pIdTrasformazione = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE, idTrasformazione+"");
			TrasformazioneRegolaApplicabilitaRichiesta applicabilita = regola.getApplicabilita();
			
			List<TrasformazioneRegolaApplicabilitaServizioApplicativo> saList = applicabilita != null ? applicabilita.getServizioApplicativoList() : null;
			int saSize = saList!=null ? saList.size() : 0;

			// Calcolo liste
			PorteApplicativeServizioApplicativoAutorizzatoUtilities utilities = new PorteApplicativeServizioApplicativoAutorizzatoUtilities();
			utilities.buildListTrasformazioni(portaApplicativa, false, protocollo, escludiSoggettoErogatore,
					idSoggettoToAdd,
					porteApplicativeCore, porteApplicativeHelper, escludiSAServer,
					isSupportatoAutenticazioneApplicativiEsterni,
					regola);
			
			String[] soggettiList = utilities.soggettiList;
			String[] soggettiListLabel = utilities.soggettiListLabel;
			idSoggettoToAdd = utilities.idSoggettoToAdd;
			
			Map<String,List<IDServizioApplicativoDB>> listServiziApplicativi = utilities.listServiziApplicativi;
			
		
			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI,
						portaApplicativa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_DI+nomePorta;
			}

			lstParam.add(new Parameter(labelPerPorta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_LIST,
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
					));
			
			if(!fromList) {
				lstParam.add(new Parameter(nomeTrasformazione, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_CHANGE, 
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
						new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps), pIdTrasformazione));
			}
			
			String labelPagLista = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_APPLICATIVI_CONFIG;
			
			List<Parameter> parametersList = new ArrayList<>();
			parametersList.add(new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta));
			parametersList.add(new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));
			parametersList.add(new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));
			parametersList.add(pIdTrasformazione);
			if(fromList) {
				parametersList.add(new Parameter (PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_APPLICABILITA_LIST, listaTmp));
			}
			lstParam.add(new Parameter(labelPagLista,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO_AUTORIZZATO_LIST,
					parametersList));
			lstParam.add(ServletUtils.getParameterAggiungi());
			
			// Se servizioApplicativohid = null, devo visualizzare la pagina per
			// l'inserimento dati
			if (porteApplicativeHelper.isEditModeInProgress()) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteApplicativeHelper.addPorteTrasformazioniServizioApplicativoAutorizzatiToDati(TipoOperazione.ADD, dati, idTrasformazioneS, fromList, soggettiListLabel, soggettiList, idSoggettoToAdd, saSize, 
						listServiziApplicativi, idSAToAdd, true);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta, idAsps,dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO_AUTORIZZATO,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.porteAppTrasformazioniServizioApplicativoAutorizzatiCheckData(TipoOperazione.ADD);
			
			TrasformazioneRegolaApplicabilitaServizioApplicativo sa = new TrasformazioneRegolaApplicabilitaServizioApplicativo();
			sa.setNome(nomeSAScelto);
			sa.setTipoSoggettoProprietario(tipoSoggettoScelto);
			sa.setNomeSoggettoProprietario(nomeSoggettoScelto);
			
			if(isOk) {
				
				List<TrasformazioneRegolaApplicabilitaServizioApplicativo> sacheckList = new ArrayList<>();
				sacheckList.add(sa);
				
				String patternDBCheck = (regola.getApplicabilita() != null && StringUtils.isNotEmpty(regola.getApplicabilita().getPattern())) ? regola.getApplicabilita().getPattern() : null;
				String contentTypeAsString = (regola.getApplicabilita() != null &&regola.getApplicabilita().getContentTypeList() != null) ? StringUtils.join(regola.getApplicabilita().getContentTypeList(), ",") : "";
				String contentTypeDBCheck = StringUtils.isNotEmpty(contentTypeAsString) ? contentTypeAsString : null;
				String azioniAsString = (regola.getApplicabilita() != null && regola.getApplicabilita().getAzioneList() != null) ? StringUtils.join(regola.getApplicabilita().getAzioneList(), ",") : "";
				String azioniDBCheck = StringUtils.isNotEmpty(azioniAsString) ? azioniAsString : null;
				String connettoriAsString = (regola.getApplicabilita() != null && regola.getApplicabilita().getConnettoreList() != null) ? StringUtils.join(regola.getApplicabilita().getConnettoreList(), ",") : "";
				String connettoriDBCheck = StringUtils.isNotEmpty(connettoriAsString) ? connettoriAsString : null;
				TrasformazioneRegola trasformazioneDBCheck_criteri = porteApplicativeCore.getTrasformazione(Long.parseLong(idPorta), azioniDBCheck, patternDBCheck, contentTypeDBCheck, connettoriDBCheck,
						null, sacheckList, false);
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

				dati = porteApplicativeHelper.addPorteTrasformazioniServizioApplicativoAutorizzatiToDati(TipoOperazione.ADD, dati, idTrasformazioneS, fromList, soggettiListLabel, soggettiList, idSoggettoToAdd, saSize, 
						listServiziApplicativi, idSAToAdd, true);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta, idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO_AUTORIZZATO, 
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
			
			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), portaApplicativa);
			
			// ricaricare id trasformazione
			portaApplicativa = porteApplicativeCore.getPortaApplicativa(Long.parseLong(idPorta));
	
			TrasformazioneRegola trasformazioneAggiornata = porteApplicativeCore.getTrasformazione(Long.parseLong(idPorta), regola.getNome());


			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO_AUTORIZZATO;

			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
			
			List<TrasformazioneRegolaApplicabilitaServizioApplicativo> lista = porteApplicativeCore.porteAppTrasformazioniServiziApplicativiAutorizzatiList(Integer.parseInt(idPorta), trasformazioneAggiornata.getId(), ricerca);
			
			porteApplicativeHelper.preparePorteAppTrasformazioniServizioApplicativoAutorizzatoList(portaApplicativa.getNome(), trasformazioneAggiornata.getId(), ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO_AUTORIZZATO, 
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO_AUTORIZZATO,
					ForwardParams.ADD());
		} 
	}
}
