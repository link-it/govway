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


package org.openspcoop2.web.ctrlstat.servlet.pa;

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
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRichiesta;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaSoggetto;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.driver.db.IDSoggettoDB;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.ApiKeyState;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * porteAppServizioApplicativoAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteApplicativeTrasformazioniSoggettoAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	
		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session);
		if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;

		try {
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			String idPorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			
			String idSoggettoToAdd = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SOGGETTO);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			
			String idTrasformazioneS = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE);
			long idTrasformazione = Long.parseLong(idTrasformazioneS);

			String listaTmp = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_APPLICABILITA_LIST);
			boolean fromList = false;
			if(listaTmp != null && !"".equals(listaTmp))
				fromList = true;
			
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteApplicativeCore);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);

			String tipologia = ServletUtils.getObjectFromSession(session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneErogatori = false;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
					gestioneErogatori = true;
				}
			}
			
			PddTipologia pddTipologiaSoggettoAutenticati = null;
			boolean gestioneErogatori_soggettiAutenticati_escludiSoggettoErogatore = false;
			if(gestioneErogatori) {
				if(apsCore.isMultitenant() && apsCore.getMultitenantSoggettiErogazioni()!=null) {
					switch (apsCore.getMultitenantSoggettiErogazioni()) {
					case SOLO_SOGGETTI_ESTERNI:
						pddTipologiaSoggettoAutenticati = PddTipologia.ESTERNO;
						break;
					case ESCLUDI_SOGGETTO_EROGATORE:
						gestioneErogatori_soggettiAutenticati_escludiSoggettoErogatore = true;
						break;
					case TUTTI:
						break;
					}
				}
			}
			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();

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

			//decodifica soggetto scelto
			String tipoSoggettoScelto = null;
			String nomeSoggettoScelto = null;
			if(idSoggettoToAdd != null) {
				int soggettoToAddInt = Integer.parseInt(idSoggettoToAdd);
				
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
			}
			
			// Prendo nome della porta applicativa
			PortaApplicativa portaApplicativa = porteApplicativeCore.getPortaApplicativa(idInt);
			String nomePorta = portaApplicativa.getNome();
			CredenzialeTipo tipoAutenticazione = CredenzialeTipo.toEnumConstant(portaApplicativa.getAutenticazione());
			Boolean appId = null;
			if(CredenzialeTipo.APIKEY.equals(tipoAutenticazione)) {
				ApiKeyState apiKeyState =  new ApiKeyState(porteApplicativeCore.getParametroAutenticazione(portaApplicativa.getAutenticazione(), portaApplicativa.getProprietaAutenticazioneList()));
				appId = apiKeyState.appIdSelected;
			}
			
			List<String> tipiSoggettiGestitiProtocollo = soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);
			
			Trasformazioni trasformazioni = portaApplicativa.getTrasformazioni();
			TrasformazioneRegola regola = null;
			for (int j = 0; j < trasformazioni.sizeRegolaList(); j++) {
				TrasformazioneRegola regolaTmp = trasformazioni.getRegola(j);
				if (regolaTmp.getId().longValue() == idTrasformazione) {
					regola = regolaTmp;
					break;
				}
			}
			
			String nomeTrasformazione = regola.getNome();
			Parameter pIdTrasformazione = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE, idTrasformazione+"");
			TrasformazioneRegolaApplicabilitaRichiesta applicabilita = regola.getApplicabilita();
			
			// lista soggetti disponibili
			String[] soggettiList = null;
			String[] soggettiListLabel = null;
			
			// calcolo soggetti compatibili con tipi protocollo supportati dalla pa e credenziali indicate
			List<IDSoggettoDB> list = null;
			if(apsCore.isVisioneOggettiGlobale(userLogin)){
				list = soggettiCore.getSoggettiFromTipoAutenticazione(tipiSoggettiGestitiProtocollo, null, tipoAutenticazione, appId, pddTipologiaSoggettoAutenticati);
			}else{
				list = soggettiCore.getSoggettiFromTipoAutenticazione(tipiSoggettiGestitiProtocollo, userLogin, tipoAutenticazione, appId, pddTipologiaSoggettoAutenticati);
			}
			if(list!=null && !list.isEmpty() && gestioneErogatori_soggettiAutenticati_escludiSoggettoErogatore) {
				for (int i = 0; i < list.size(); i++) {
					IDSoggettoDB soggettoCheck = list.get(i);
					if(soggettoCheck.getTipo().equals(portaApplicativa.getTipoSoggettoProprietario()) && soggettoCheck.getNome().equals(portaApplicativa.getNomeSoggettoProprietario())) {
						list.remove(i);
						break;
					}
				}
			}

			boolean multiTenant = porteApplicativeCore.isMultitenant();
			
			List<TrasformazioneRegolaApplicabilitaSoggetto> soggettoList  = applicabilita != null ? applicabilita.getSoggettoList() : null; 
			int sizeSoggettoList = soggettoList != null ? soggettoList.size() : 0;
			if (list.size() > 0) {
				List<String> soggettiListTmp = new ArrayList<String>();
				List<String> soggettiListLabelTmp = new ArrayList<String>();
				for (IDSoggettoDB soggetto : list) {
					// scartare i soggetti gia associati
					boolean found = false;
					
					if(soggettoList != null && sizeSoggettoList > 0) {
						for (TrasformazioneRegolaApplicabilitaSoggetto soggettoAssociatoPa : soggettoList) { 
							if(soggettoAssociatoPa.getTipo().equals(soggetto.getTipo()) && soggettoAssociatoPa.getNome().equals(soggetto.getNome())) {
								found = true;
								break;
							}
						}
					}
					
					boolean soggettoErogatoreServizio = false;
					if(portaApplicativa.getTipoSoggettoProprietario().equals(soggetto.getTipo()) &&
							portaApplicativa.getNomeSoggettoProprietario().equals(soggetto.getNome())) {
						soggettoErogatoreServizio = true;
					}
										
					if(!found && (!soggettoErogatoreServizio || multiTenant)){
						soggettiListTmp.add(soggetto.getId().toString());
						soggettiListLabelTmp.add(porteApplicativeHelper.getLabelNomeSoggetto(protocollo, soggetto.getTipo() , soggetto.getNome()));
					}
				}
				if(soggettiListTmp!=null && soggettiListTmp.size()>0) {
					soggettiList = soggettiListTmp.toArray(new String[1]);
					soggettiListLabel = soggettiListLabelTmp.toArray(new String[1]);
				}
			}
			
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
			
			String labelPagLista = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTO_CONFIG;
			
			List<Parameter> parametersList = new ArrayList<>();
			parametersList.add(new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta));
			parametersList.add(new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));
			parametersList.add(new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));
			parametersList.add(pIdTrasformazione);
			if(fromList) {
				parametersList.add(new Parameter (PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_APPLICABILITA_LIST, listaTmp));
			}
			lstParam.add(new Parameter(labelPagLista,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTO_LIST,
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
				
				dati = porteApplicativeHelper.addPorteTrasformazioniSoggettoToDati(TipoOperazione.ADD, dati, idTrasformazioneS, fromList, soggettiListLabel, soggettiList, idSoggettoToAdd, sizeSoggettoList, true, true);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta, idAsps,dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTO,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.porteAppTrasformazioniSoggettoCheckData(TipoOperazione.ADD);
			
			TrasformazioneRegolaApplicabilitaSoggetto paSoggetto = new TrasformazioneRegolaApplicabilitaSoggetto();
			paSoggetto.setTipo(tipoSoggettoScelto);
			paSoggetto.setNome(nomeSoggettoScelto);
			
			if(isOk) {
				
				List<TrasformazioneRegolaApplicabilitaSoggetto> applicabilitaCheckSoggetti = new ArrayList<>();
								
				applicabilitaCheckSoggetti.add(paSoggetto);
				
				String patternDBCheck = (regola.getApplicabilita() != null && StringUtils.isNotEmpty(regola.getApplicabilita().getPattern())) ? regola.getApplicabilita().getPattern() : null;
				String contentTypeAsString = (regola.getApplicabilita() != null &&regola.getApplicabilita().getContentTypeList() != null) ? StringUtils.join(regola.getApplicabilita().getContentTypeList(), ",") : "";
				String contentTypeDBCheck = StringUtils.isNotEmpty(contentTypeAsString) ? contentTypeAsString : null;
				String azioniAsString = (regola.getApplicabilita() != null && regola.getApplicabilita().getAzioneList() != null) ? StringUtils.join(regola.getApplicabilita().getAzioneList(), ",") : "";
				String azioniDBCheck = StringUtils.isNotEmpty(azioniAsString) ? azioniAsString : null;
				TrasformazioneRegola trasformazioneDBCheck_criteri = porteApplicativeCore.getTrasformazione(Long.parseLong(idPorta), azioniDBCheck, patternDBCheck, contentTypeDBCheck, 
						applicabilitaCheckSoggetti, null, false);
				if(trasformazioneDBCheck_criteri!=null) {
					isOk = false;
					pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_REGOLA_TRASFORMAZIONE_APPLICABILITA_DUPLICATA_SOGGETTO);
				}
				
			}		
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addPorteTrasformazioniSoggettoToDati(TipoOperazione.ADD, dati, idTrasformazioneS, fromList, soggettiListLabel, soggettiList, idSoggettoToAdd, sizeSoggettoList, true, true);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta, idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTO, 
						ForwardParams.ADD());
			}

			// Inserisco il servizioApplicativo nel db
				
			if(regola.getApplicabilita() == null)
				regola.setApplicabilita(new TrasformazioneRegolaApplicabilitaRichiesta());
			
			regola.getApplicabilita().addSoggetto(paSoggetto);

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), portaApplicativa);
			
			// ricaricare id trasformazione
			portaApplicativa = porteApplicativeCore.getPortaApplicativa(Long.parseLong(idPorta));
	
			TrasformazioneRegola trasformazioneAggiornata = porteApplicativeCore.getTrasformazione(Long.parseLong(idPorta), regola.getNome());


			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTO;

			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
			
			List<TrasformazioneRegolaApplicabilitaSoggetto> lista = porteApplicativeCore.porteAppTrasformazioniSoggettoList(Integer.parseInt(idPorta), trasformazioneAggiornata.getId(), ricerca);
			
			porteApplicativeHelper.preparePorteAppTrasformazioniSoggettoList(portaApplicativa.getNome(), trasformazioneAggiornata.getId(), ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTO, 
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTO,
					ForwardParams.ADD());
		} 
	}
}
