/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.TrasformazioneRegolaParametro;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.config.constants.TrasformazioneIdentificazioneRisorsaFallita;
import org.openspcoop2.core.config.constants.TrasformazioneRegolaParametroTipoAzione;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * PorteApplicativeTrasformazioniRichiestaUrlParameterChange
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteApplicativeTrasformazioniRichiestaUrlParameterChange extends Action {

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
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			
			String idTrasformazioneS = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE);
			long idTrasformazione = Long.parseLong(idTrasformazioneS);
			
			String idTrasformazioneRichiestaUrlParameterS = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE_RICHIESTA_PARAMETRO);
			long idTrasformazioneRichiestaUrlParameter = Long.parseLong(idTrasformazioneRichiestaUrlParameterS);
			
			String tipo = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_TIPO);
			String nome = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_NOME);
			String valore = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_VALORE);
			String identificazione = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_IDENTIFICAZIONE);

			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteApplicativeCore);
			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// Prendo nome della porta applicativa
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String nomePorta = pa.getNome();
			
			Trasformazioni trasformazioni = pa.getTrasformazioni();
			TrasformazioneRegola oldRegola = null;
			TrasformazioneRegolaParametro oldParametro = null;
			for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
				if(reg.getId().longValue() == idTrasformazione) {
					oldRegola = reg;
					break;
				}
			}

			for (int j = 0; j < oldRegola.getRichiesta().sizeParametroUrlList(); j++) {
				TrasformazioneRegolaParametro parametro = oldRegola.getRichiesta().getParametroUrl(j);
				if (parametro.getId().longValue() == idTrasformazioneRichiestaUrlParameter) {
					oldParametro = parametro;
					break;
				}
			}
			
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			AccordoServizioParteComuneSintetico apc = apcCore.getAccordoServizioSintetico(asps.getIdAccordo()); 
			
			String nomeTrasformazione = oldRegola.getNome();
			String nomeParametro = oldParametro.getNome();
			Parameter pIdTrasformazione = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE, idTrasformazione+"");
			
			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI,
						pa);
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
			
			lstParam.add(new Parameter(nomeTrasformazione, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_CHANGE, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps), pIdTrasformazione));
			
			List<Parameter> parametriInvocazioneServletTrasformazioniRichiesta = new ArrayList<Parameter>();
			parametriInvocazioneServletTrasformazioniRichiesta.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta));
			parametriInvocazioneServletTrasformazioniRichiesta.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));
			parametriInvocazioneServletTrasformazioniRichiesta.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));
			parametriInvocazioneServletTrasformazioniRichiesta.add(pIdTrasformazione);
			
			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA,parametriInvocazioneServletTrasformazioniRichiesta));
						
			List<Parameter> parametriInvocazioneServletTrasformazioniRichiestaUrlParameters = new ArrayList<Parameter>();
			parametriInvocazioneServletTrasformazioniRichiestaUrlParameters.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta));
			parametriInvocazioneServletTrasformazioniRichiestaUrlParameters.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));
			parametriInvocazioneServletTrasformazioniRichiestaUrlParameters.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));
			parametriInvocazioneServletTrasformazioniRichiestaUrlParameters.add(pIdTrasformazione);
			
			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_PARAMETRI,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_LIST,parametriInvocazioneServletTrasformazioniRichiestaUrlParameters ));
			
			lstParam.add(new Parameter(nomeParametro, null));

			ServletUtils.setPageDataTitle(pd, lstParam);
			
			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (porteApplicativeHelper.isEditModeInProgress()) {
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				// primo accesso
				if(nome == null) {
					nome = oldParametro.getNome();
					tipo = oldParametro.getConversioneTipo().getValue();
					valore = oldParametro.getValore();
					identificazione = oldParametro.getIdentificazioneFallita()!=null ? oldParametro.getIdentificazioneFallita().getValue() : CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA;
				}

				dati = porteApplicativeHelper.addTrasformazioneRichiestaUrlParameterToDati(TipoOperazione.CHANGE, dati, idTrasformazioneS, idTrasformazioneRichiestaUrlParameterS, nome, tipo, valore, identificazione, apc.getServiceBinding());
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idPorta, idsogg, idPorta,idAsps,  dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_PARAMETRO,	ForwardParams.CHANGE());
			}
			
			boolean isOk = porteApplicativeHelper.trasformazioniRichiestaUrlParameterCheckData(TipoOperazione.CHANGE);
			if (isOk && (oldParametro!=null && !oldParametro.getNome().equals(nome))) {
				boolean giaRegistrato = porteApplicativeCore.existsTrasformazioneRichiestaUrlParameter(Long.parseLong(idPorta), idTrasformazione, nome, tipo, CostantiControlStation.VALUE_TRASFORMAZIONI_CHECK_UNIQUE_NOME_TIPO_HEADER_URL);

				if (giaRegistrato) {
					pd.setMessage( ServletUtils.getMessageFromResourceBundle( session, "ControlStation.TrasformazioniCheckUniqueNomeTipoUrl" ) );	//CostantiControlStation.MESSAGGIO_TRASFORMAZIONI_CHECK_UNIQUE_NOME_TIPO_URL
					isOk = false;
				}
			}
			if (!isOk) {

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteApplicativeHelper.addTrasformazioneRichiestaUrlParameterToDati(TipoOperazione.CHANGE, dati, idTrasformazioneS, idTrasformazioneRichiestaUrlParameterS, nome, tipo, valore, identificazione, apc.getServiceBinding());
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idPorta, idsogg, idPorta,idAsps,  dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_PARAMETRO,	ForwardParams.CHANGE());
			}
			
			// aggiorno la regola
			TrasformazioneRegola regola = null;
			for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
				if(reg.getId().longValue() == idTrasformazione) {
					regola = reg;
					break;
				}
			}
			
			TrasformazioneRegolaParametro parametroDaAggiornare = null;
			for (int j = 0; j < regola.getRichiesta().sizeParametroUrlList(); j++) {
				TrasformazioneRegolaParametro parametro = regola.getRichiesta().getParametroUrl(j);
				if (parametro.getId().longValue() == idTrasformazioneRichiestaUrlParameter) {
					parametroDaAggiornare = parametro;
					break;
				}
			}
			
			parametroDaAggiornare.setNome(nome);
			parametroDaAggiornare.setValore(valore);
			parametroDaAggiornare.setConversioneTipo(TrasformazioneRegolaParametroTipoAzione.toEnumConstant(tipo));
			if(!TrasformazioneRegolaParametroTipoAzione.DELETE.equals(parametroDaAggiornare.getConversioneTipo())) {
				parametroDaAggiornare.setIdentificazioneFallita(TrasformazioneIdentificazioneRisorsaFallita.toEnumConstant(identificazione));
			}
			else {
				parametroDaAggiornare.setIdentificazioneFallita(null);
			}
			
			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);
			
			// ricaricare id trasformazione
			pa = porteApplicativeCore.getPortaApplicativa(Long.parseLong(idPorta));

			TrasformazioneRegola trasformazioneAggiornata = porteApplicativeCore.getTrasformazione(pa.getId(), regola.getNome());
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_PARAMETRI; 
			
			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
			
			List<TrasformazioneRegolaParametro> lista = porteApplicativeCore.porteAppTrasformazioniRichiestaUrlParameterList(Long.parseLong(idPorta), trasformazioneAggiornata.getId(), ricerca);
			
			porteApplicativeHelper.preparePorteAppTrasformazioniRichiestaUrlParameterList(nomePorta, trasformazioneAggiornata.getId(), ricerca, lista); 
						
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_PARAMETRO, ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_PARAMETRO, 
					ForwardParams.CHANGE());
		}
	}
}
