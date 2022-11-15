/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.AutorizzazioneRuoli;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * PorteApplicativeRuoliAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteApplicativeRuoliAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

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
			String nome = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_RUOLO);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			
			String autorizzazioneModi = request.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_MODIPA);
			boolean isAutorizzazioneModi = false;
			if(autorizzazioneModi!=null && "true".equalsIgnoreCase(autorizzazioneModi)) {
				isAutorizzazioneModi = true;
			}
			
			String tokenList = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TOKEN_AUTHORIZATION);
			boolean isToken = tokenList!=null && !"".equals(tokenList) && Boolean.valueOf(tokenList);
			
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteApplicativeCore);

			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// Prendo nome della porta applicativa
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(pa.getTipoSoggettoProprietario());
			boolean modi = porteApplicativeCore.isProfiloModIPA(protocollo);
			String nomePorta = pa.getNome();
			
			// Cerco Ruoli
			FiltroRicercaRuoli filtroRuoli = new FiltroRicercaRuoli();
			filtroRuoli.setContesto(RuoloContesto.PORTA_APPLICATIVA);
			filtroRuoli.setTipologia(RuoloTipologia.QUALSIASI);
			if(isToken) {
				if(pa.getAutorizzazioneToken()!=null && pa.getAutorizzazioneToken().getTipologiaRuoli()!=null) {
					RuoloTipologia r = RuoloTipologia.toEnumConstant(pa.getAutorizzazioneToken().getTipologiaRuoli().getValue());
					filtroRuoli.setTipologia(r);
				}
			}
			else {
				if(TipoAutorizzazione.isInternalRolesRequired(pa.getAutorizzazione()) ){
					filtroRuoli.setTipologia(RuoloTipologia.INTERNO);
				}
				else if(TipoAutorizzazione.isExternalRolesRequired(pa.getAutorizzazione()) ){
					filtroRuoli.setTipologia(RuoloTipologia.ESTERNO);
				}
			}
						
			List<String> ruoli = new ArrayList<>();
			if(isToken) {
				if(pa.getAutorizzazioneToken()!=null && pa.getAutorizzazioneToken().getRuoli()!=null && pa.getAutorizzazioneToken().getRuoli().getRuoloList()!=null && pa.getAutorizzazioneToken().getRuoli().getRuoloList().size()>0){
					for (Ruolo ruolo : pa.getAutorizzazioneToken().getRuoli().getRuoloList()) {
						ruoli.add(ruolo.getNome());	
					}
				}
			}
			else {
				if(pa.getRuoli()!=null && pa.getRuoli().getRuoloList()!=null && pa.getRuoli().getRuoloList().size()>0){
					for (Ruolo ruolo : pa.getRuoli().getRuoloList()) {
						ruoli.add(ruolo.getNome());	
					}
				}
			}
			
			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);
			
			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI,
						pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI_CONFIG_DI+nomePorta;
			}
			
			lstParam.add(new Parameter(labelPerPorta, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)));
			
			String labelPagLista = 
					isToken ? 
							((isAutorizzazioneModi || modi)? CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_MESSAGGIO : CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN)
							:
							(modi? CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CANALE : CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_TRASPORTO);
			labelPagLista = labelPagLista
							+ " - " +
					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RUOLI_CONFIG;
			
			lstParam.add(new Parameter(labelPagLista,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_RUOLI_LIST,
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TOKEN_AUTHORIZATION, isToken+""),
					new Parameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_MODIPA, isAutorizzazioneModi+"")
					));
			lstParam.add(ServletUtils.getParameterAggiungi());

			// Se servizioApplicativohid = null, devo visualizzare la pagina per
			// l'inserimento dati
			if (porteApplicativeHelper.isEditModeInProgress()) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addRuoliToDati(TipoOperazione.ADD, dati, false, filtroRuoli, nome, ruoli, false, true, true, null, isToken);
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta,idAsps,  dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_RUOLI,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.ruoloCheckData(TipoOperazione.ADD, nome, ruoli);
			if (!isOk) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteApplicativeHelper.addRuoliToDati(TipoOperazione.ADD, dati, false, filtroRuoli, nome, ruoli, false, true, true, null, isToken);
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta, idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_RUOLI, 
						ForwardParams.ADD());
			}

			// Inserisco il ruolo nel db
			Ruolo ruolo = new Ruolo();
			ruolo.setNome(nome);
			if(isToken) {
				if(pa.getAutorizzazioneToken().getRuoli()==null) {
					pa.getAutorizzazioneToken().setRuoli(new AutorizzazioneRuoli());
				}
				pa.getAutorizzazioneToken().getRuoli().addRuolo(ruolo);
			}
			else {
				if(pa.getRuoli()==null){
					pa.setRuoli(new AutorizzazioneRuoli());
				}
				pa.getRuoli().addRuolo(ruolo);
			}

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.PORTE_APPLICATIVE_RUOLI;
			if(isToken) {
				idLista = Liste.PORTE_APPLICATIVE_TOKEN_RUOLI;
			}

			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);

			List<String> lista = isToken ?
					porteApplicativeCore.portaApplicativaRuoliTokenList(idInt, ricerca) 
					:
					porteApplicativeCore.portaApplicativaRuoliList(idInt, ricerca);	

			porteApplicativeHelper.preparePorteApplicativeRuoliList(nomePorta, ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_RUOLI, 
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_RUOLI,
					ForwardParams.ADD());
		} 
	}
	
}
