/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.constants.RuoloTipoMatch;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/***
 * 
 * PorteApplicativeControlloAccessi
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteApplicativeControlloAccessi extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
		
		boolean isPortaDelegata = false;

		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session);
		if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;

		try {
			Boolean contaListe = ServletUtils.getContaListeFromSession(session);
			Boolean confPers = (Boolean) session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_CONFIGURAZIONI_PERSONALIZZATE);

			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			String id = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			
			String autenticazione = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE );
			String autenticazioneOpzionale = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_OPZIONALE );
			String autenticazioneCustom = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM );
			String autorizzazione = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE);
			String autorizzazioneCustom = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM);
			
			String autorizzazioneAutenticati = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE);
			String autorizzazioneRuoli = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI);
			String autorizzazioneRuoliTipologia = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_RUOLO_TIPOLOGIA);
			String ruoloMatch = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_RUOLO_MATCH);
			
			String autorizzazioneContenuti = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI);

			String applicaModificaS = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_APPLICA_MODIFICA);
			boolean applicaModifica = ServletUtils.isCheckBoxEnabled(applicaModificaS);

			String gestioneToken = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN);
			String gestioneTokenPolicy = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_POLICY);
			String gestioneTokenValidazioneInput = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_VALIDAZIONE_INPUT);
			String gestioneTokenIntrospection = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_INTROSPECTION);
			String gestioneTokenUserInfo = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_USERINFO);
			String gestioneTokenTokenForward = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_TOKEN_FORWARD);
			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// Prendo il nome della porta
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteApplicativeCore);
			ConfigurazioneCore confCore = new ConfigurazioneCore(porteApplicativeCore);

			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String idporta = pa.getNome();
			
			List<String> ruoli = new ArrayList<>();
			if(pa!=null && pa.getRuoli()!=null && pa.getRuoli().sizeRuoloList()>0){
				for (int i = 0; i < pa.getRuoli().sizeRuoloList(); i++) {
					ruoli.add(pa.getRuoli().getRuolo(i).getNome());
				}
			}
			
			int numRuoli = 0;
			if(pa.getRuoli()!=null){
				numRuoli = pa.getRuoli().sizeRuoloList();
			}
			
			Search searchForCount = new Search(true,1);
			porteApplicativeCore.porteAppSoggettoList(idInt, searchForCount);
			int sizeSoggettiPA = searchForCount.getNumEntries(Liste.PORTE_APPLICATIVE_SOGGETTO);

			// Prendo nome, tipo e pdd del soggetto
			String tipoSoggettoProprietario = null;
			if(porteApplicativeCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggInt);
				tipoSoggettoProprietario = soggetto.getTipo();
			}
			else{
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(soggInt);
				tipoSoggettoProprietario = soggetto.getTipo();
			}
			
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoSoggettoProprietario);
			boolean isSupportatoAutenticazione = soggettiCore.isSupportatoAutenticazioneSoggetti(protocollo);
			
			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);
			
			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI_CONFIG_DI+
						porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI_CONFIG_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta,  null));
			
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParam);
			
			
			Parameter[] urlParmsAutorizzazioneAutenticati = { 
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,id)	,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS,idAsps) };
			Parameter urlAutorizzazioneAutenticatiParam= new Parameter("", PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_SOGGETTO_LIST, urlParmsAutorizzazioneAutenticati);
			String urlAutorizzazioneAutenticati = urlAutorizzazioneAutenticatiParam.getValue();
			
			

			Parameter[] urlParmsAutorizzazioneRuoli = { 
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,id)	,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS,idAsps) };
			Parameter urlAutorizzazioneRuoliParam = new Parameter("", PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_RUOLI_LIST , urlParmsAutorizzazioneRuoli);
			String urlAutorizzazioneRuoli = urlAutorizzazioneRuoliParam.getValue();
			
			String servletChiamante = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI;
			
			List<GenericProperties> gestorePolicyTokenList = confCore.gestorePolicyTokenList(null, ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_GESTIONE_POLICY_TOKEN, null);
			String [] policyLabels = new String[gestorePolicyTokenList.size() + 1];
			String [] policyValues = new String[gestorePolicyTokenList.size() + 1];
			
			policyLabels[0] = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
			policyValues[0] = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
			
			for (int i = 0; i < gestorePolicyTokenList.size(); i++) {
			GenericProperties genericProperties = gestorePolicyTokenList.get(i);
				policyLabels[(i+1)] = genericProperties.getNome();
				policyValues[(i+1)] = genericProperties.getNome();
			}
			
			if(	porteApplicativeHelper.isEditModeInProgress() && !applicaModifica){

				if (autenticazione == null) {
					autenticazione = pa.getAutenticazione();
					if (autenticazione != null &&
							!TipoAutenticazione.getValues().contains(autenticazione)) {
						autenticazioneCustom = autenticazione;
						autenticazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM;
					}
				}
				if(autenticazioneOpzionale==null){
					autenticazioneOpzionale = "";
					if(pa.getAutenticazioneOpzionale()!=null){
						if (pa.getAutenticazioneOpzionale().equals(StatoFunzionalita.ABILITATO)) {
							autenticazioneOpzionale = Costanti.CHECK_BOX_ENABLED;
						}
					}
				}
				if (autorizzazione == null) {
					if (pa.getAutorizzazione() != null &&
							!TipoAutorizzazione.getAllValues().contains(pa.getAutorizzazione())) {
						autorizzazioneCustom = pa.getAutorizzazione();
						autorizzazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM;
					}
					else{
						autorizzazione = AutorizzazioneUtilities.convertToStato(pa.getAutorizzazione());
						if(TipoAutorizzazione.isAuthenticationRequired(pa.getAutorizzazione()))
							autorizzazioneAutenticati = Costanti.CHECK_BOX_ENABLED;
						if(TipoAutorizzazione.isRolesRequired(pa.getAutorizzazione()))
							autorizzazioneRuoli = Costanti.CHECK_BOX_ENABLED;
						autorizzazioneRuoliTipologia = AutorizzazioneUtilities.convertToRuoloTipologia(pa.getAutorizzazione()).getValue();
					}
				}
				
				if (ruoloMatch == null) {
					if(pa.getRuoli()!=null && pa.getRuoli().getMatch()!=null){
						ruoloMatch = pa.getRuoli().getMatch().getValue();
					}
				}
				
				if(autorizzazioneContenuti==null){
					autorizzazioneContenuti = pa.getAutorizzazioneContenuto();
				}
				
				if(gestioneToken == null) {
					if(pa.getGestioneToken() != null) {
						gestioneTokenPolicy = pa.getGestioneToken().getPolicy();
						if(gestioneTokenPolicy == null) {
							gestioneToken = StatoFunzionalita.DISABILITATO.getValue();
							gestioneTokenPolicy = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
						} else {
							gestioneToken = StatoFunzionalita.ABILITATO.getValue();
						}
						
						StatoFunzionalitaConWarning validazione = pa.getGestioneToken().getValidazione();
						if(validazione == null || !validazione.equals(StatoFunzionalitaConWarning.ABILITATO)) {
							gestioneTokenValidazioneInput = "";
						}else { 
							gestioneTokenValidazioneInput = Costanti.CHECK_BOX_ENABLED;
						}
						
						StatoFunzionalitaConWarning introspection = pa.getGestioneToken().getIntrospection();
						if(introspection == null || !introspection.equals(StatoFunzionalitaConWarning.ABILITATO)) {
							gestioneTokenIntrospection = "";
						}else { 
							gestioneTokenIntrospection = Costanti.CHECK_BOX_ENABLED;
						}
						
						StatoFunzionalitaConWarning userinfo = pa.getGestioneToken().getUserInfo();
						if(userinfo == null || !userinfo.equals(StatoFunzionalitaConWarning.ABILITATO)) {
							gestioneTokenUserInfo = "";
						}else { 
							gestioneTokenUserInfo = Costanti.CHECK_BOX_ENABLED;
						}
						
						StatoFunzionalita tokenForward = pa.getGestioneToken().getForward();
						if(tokenForward == null || !tokenForward.equals(StatoFunzionalita.ABILITATO)) {
							gestioneTokenTokenForward = "";
						}else { 
							gestioneTokenTokenForward = Costanti.CHECK_BOX_ENABLED;
						}
					}
					else {
						gestioneToken = StatoFunzionalita.DISABILITATO.getValue();
						gestioneTokenPolicy = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
						gestioneTokenValidazioneInput = "";
						gestioneTokenIntrospection = "";
						gestioneTokenUserInfo = "";
						gestioneTokenTokenForward = "";
					}
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				porteApplicativeHelper.controlloAccessi(dati);

				porteApplicativeHelper.controlloAccessiAutenticazione(dati, autenticazione, autenticazioneCustom, autenticazioneOpzionale, confPers, isSupportatoAutenticazione);
				
				porteApplicativeHelper.controlloAccessiGestioneToken(dati, TipoOperazione.OTHER, gestioneToken, policyLabels, policyValues, gestioneTokenPolicy, gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward, pa);

				// Tipo operazione = CHANGE per evitare di aggiungere if, questa e' a tutti gli effetti una servlet di CHANGE
				porteApplicativeHelper.controlloAccessiAutorizzazione(dati, TipoOperazione.CHANGE, servletChiamante,pa,
						autenticazione, autorizzazione, autorizzazioneCustom, 
						autorizzazioneAutenticati, urlAutorizzazioneAutenticati, sizeSoggettiPA, null, null,
						autorizzazioneRuoli,  urlAutorizzazioneRuoli, numRuoli, null, 
						autorizzazioneRuoliTipologia, ruoloMatch,
						confPers, isSupportatoAutenticazione, contaListe, false, false);
				
				porteApplicativeHelper.controlloAccessiAutorizzazioneContenuti(dati, autorizzazioneContenuti);
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null,idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.controlloAccessiCheck(TipoOperazione.OTHER, autenticazione, autenticazioneOpzionale, 
					autorizzazione, autorizzazioneAutenticati, autorizzazioneRuoli, 
					autorizzazioneRuoliTipologia, ruoloMatch, 
					isSupportatoAutenticazione, isPortaDelegata, pa, ruoli);
					
			if(isOk) {
				isOk = porteApplicativeHelper.controlloAccessiGestioneTokenCheck(TipoOperazione.OTHER, gestioneToken, gestioneTokenPolicy, 
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward, isPortaDelegata, pa);
			}
			if (!isOk) {
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				porteApplicativeHelper.controlloAccessi(dati);
				
				porteApplicativeHelper.controlloAccessiAutenticazione(dati, autenticazione, autenticazioneCustom, autenticazioneOpzionale, confPers, isSupportatoAutenticazione);
				
				porteApplicativeHelper.controlloAccessiGestioneToken(dati, TipoOperazione.OTHER, gestioneToken, policyLabels, policyValues, gestioneTokenPolicy, gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward, pa);

				// Tipo operazione = CHANGE per evitare di aggiungere if, questa e' a tutti gli effetti una servlet di CHANGE
				porteApplicativeHelper.controlloAccessiAutorizzazione(dati, TipoOperazione.CHANGE, servletChiamante,pa,
						autenticazione, autorizzazione, autorizzazioneCustom, 
						autorizzazioneAutenticati, urlAutorizzazioneAutenticati, sizeSoggettiPA, null, null,
						autorizzazioneRuoli,  urlAutorizzazioneRuoli, numRuoli, null, 
						autorizzazioneRuoliTipologia, ruoloMatch,
						confPers, isSupportatoAutenticazione, contaListe, false, false);
				
				porteApplicativeHelper.controlloAccessiAutorizzazioneContenuti(dati, autorizzazioneContenuti);
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null,idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI,
						ForwardParams.OTHER(""));
			}

			
			if (autenticazione == null || !autenticazione.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM))
				pa.setAutenticazione(autenticazione);
			else
				pa.setAutenticazione(autenticazioneCustom);
			if(autenticazioneOpzionale != null){
				if(ServletUtils.isCheckBoxEnabled(autenticazioneOpzionale))
					pa.setAutenticazioneOpzionale(StatoFunzionalita.ABILITATO);
				else 
					pa.setAutenticazioneOpzionale(StatoFunzionalita.DISABILITATO);
			} else 
				pa.setAutenticazioneOpzionale(null);
			if (autorizzazione == null || !autorizzazione.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM))
				pa.setAutorizzazione(AutorizzazioneUtilities.convertToTipoAutorizzazioneAsString(autorizzazione, 
						ServletUtils.isCheckBoxEnabled(autorizzazioneAutenticati), ServletUtils.isCheckBoxEnabled(autorizzazioneRuoli), 
						RuoloTipologia.toEnumConstant(autorizzazioneRuoliTipologia)));
			else
				pa.setAutorizzazione(autorizzazioneCustom);
			
			if(ruoloMatch!=null && !"".equals(ruoloMatch)){
				RuoloTipoMatch tipoRuoloMatch = RuoloTipoMatch.toEnumConstant(ruoloMatch);
				if(tipoRuoloMatch!=null){
					if(pa.getRuoli()==null){
						pa.setRuoli(new AutorizzazioneRuoli());
					}
					pa.getRuoli().setMatch(tipoRuoloMatch);
				}
			}
			pa.setAutorizzazioneContenuto(autorizzazioneContenuti);
			
			if(pa.getGestioneToken() == null)
				pa.setGestioneToken(new GestioneToken());
			
			if(gestioneToken.equals(StatoFunzionalita.ABILITATO.getValue())) {
				pa.getGestioneToken().setPolicy(gestioneTokenPolicy);
				pa.getGestioneToken().setValidazione(ServletUtils.isCheckBoxEnabled(gestioneTokenValidazioneInput) ? StatoFunzionalitaConWarning.ABILITATO : StatoFunzionalitaConWarning.DISABILITATO);
				pa.getGestioneToken().setIntrospection(ServletUtils.isCheckBoxEnabled(gestioneTokenIntrospection) ? StatoFunzionalitaConWarning.ABILITATO :StatoFunzionalitaConWarning.DISABILITATO);
				pa.getGestioneToken().setUserInfo(ServletUtils.isCheckBoxEnabled(gestioneTokenUserInfo) ? StatoFunzionalitaConWarning.ABILITATO :StatoFunzionalitaConWarning.DISABILITATO);
				pa.getGestioneToken().setForward(ServletUtils.isCheckBoxEnabled(gestioneTokenTokenForward) ? StatoFunzionalita.ABILITATO :StatoFunzionalita.DISABILITATO); 	
			} else {
				pa.getGestioneToken().setPolicy(null);
				pa.getGestioneToken().setValidazione(StatoFunzionalitaConWarning.DISABILITATO);
				pa.getGestioneToken().setIntrospection(StatoFunzionalitaConWarning.DISABILITATO);
				pa.getGestioneToken().setUserInfo(StatoFunzionalitaConWarning.DISABILITATO);
				pa.getGestioneToken().setForward(StatoFunzionalita.DISABILITATO); 
			}
			
			
			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);
			
			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();
			
			pa = porteApplicativeCore.getPortaApplicativa(idInt);
			idporta = pa.getNome();
			
			ruoli = new ArrayList<>();
			if(pa!=null && pa.getRuoli()!=null && pa.getRuoli().sizeRuoloList()>0){
				for (int i = 0; i < pa.getRuoli().sizeRuoloList(); i++) {
					ruoli.add(pa.getRuoli().getRuolo(i).getNome());
				}
			}
			
			numRuoli = 0;
			if(pa.getRuoli()!=null){
				numRuoli = pa.getRuoli().sizeRuoloList();
			}
			
			if (autenticazione == null) {
				autenticazione = pa.getAutenticazione();
				if (autenticazione != null &&
						!TipoAutenticazione.getValues().contains(autenticazione)) {
					autenticazioneCustom = autenticazione;
					autenticazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM;
				}
			}
			if(autenticazioneOpzionale==null){
				autenticazioneOpzionale = "";
				if(pa.getAutenticazioneOpzionale()!=null){
					if (pa.getAutenticazioneOpzionale().equals(StatoFunzionalita.ABILITATO)) {
						autenticazioneOpzionale = Costanti.CHECK_BOX_ENABLED;
					}
				}
			}
			if (autorizzazione == null) {
				if (pa.getAutorizzazione() != null &&
						!TipoAutorizzazione.getAllValues().contains(pa.getAutorizzazione())) {
					autorizzazioneCustom = pa.getAutorizzazione();
					autorizzazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM;
				}
				else{
					autorizzazione = AutorizzazioneUtilities.convertToStato(pa.getAutorizzazione());
					if(TipoAutorizzazione.isAuthenticationRequired(pa.getAutorizzazione()))
						autorizzazioneAutenticati = Costanti.CHECK_BOX_ENABLED;
					if(TipoAutorizzazione.isRolesRequired(pa.getAutorizzazione()))
						autorizzazioneRuoli = Costanti.CHECK_BOX_ENABLED;
					autorizzazioneRuoliTipologia = AutorizzazioneUtilities.convertToRuoloTipologia(pa.getAutorizzazione()).getValue();
				}
			}
			
			if (ruoloMatch == null) {
				if(pa.getRuoli()!=null && pa.getRuoli().getMatch()!=null){
					ruoloMatch = pa.getRuoli().getMatch().getValue();
				}
			}
			
			if(autorizzazioneContenuti==null){
				autorizzazioneContenuti = pa.getAutorizzazioneContenuto();
			}
			
			if(pa.getGestioneToken() != null) {
				gestioneTokenPolicy = pa.getGestioneToken().getPolicy();
				if(gestioneTokenPolicy == null) {
					gestioneToken = StatoFunzionalita.DISABILITATO.getValue();
					gestioneTokenPolicy = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
				} else {
					gestioneToken = StatoFunzionalita.ABILITATO.getValue();
				}
				
				StatoFunzionalitaConWarning validazione = pa.getGestioneToken().getValidazione();
				if(validazione == null || !validazione.equals(StatoFunzionalitaConWarning.ABILITATO)) {
					gestioneTokenValidazioneInput = "";
				}else { 
					gestioneTokenValidazioneInput = Costanti.CHECK_BOX_ENABLED;
				}
				
				StatoFunzionalitaConWarning introspection = pa.getGestioneToken().getIntrospection();
				if(introspection == null || !introspection.equals(StatoFunzionalitaConWarning.ABILITATO)) {
					gestioneTokenIntrospection = "";
				}else { 
					gestioneTokenIntrospection = Costanti.CHECK_BOX_ENABLED;
				}
				
				StatoFunzionalitaConWarning userinfo = pa.getGestioneToken().getUserInfo();
				if(userinfo == null || !userinfo.equals(StatoFunzionalitaConWarning.ABILITATO)) {
					gestioneTokenUserInfo = "";
				}else { 
					gestioneTokenUserInfo = Costanti.CHECK_BOX_ENABLED;
				}
				
				StatoFunzionalita tokenForward = pa.getGestioneToken().getForward();
				if(tokenForward == null || !tokenForward.equals(StatoFunzionalita.ABILITATO)) {
					gestioneTokenTokenForward = "";
				}else { 
					gestioneTokenTokenForward = Costanti.CHECK_BOX_ENABLED;
				}
			}
			else {
				gestioneToken = StatoFunzionalita.DISABILITATO.getValue();
				gestioneTokenPolicy = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
				gestioneTokenValidazioneInput = "";
				gestioneTokenIntrospection = "";
				gestioneTokenUserInfo = "";
				gestioneTokenTokenForward = "";
			}
			
			porteApplicativeHelper.controlloAccessi(dati);
			
			porteApplicativeHelper.controlloAccessiAutenticazione(dati, autenticazione, autenticazioneCustom, autenticazioneOpzionale, confPers, isSupportatoAutenticazione);
			
			porteApplicativeHelper.controlloAccessiGestioneToken(dati, TipoOperazione.OTHER, gestioneToken, policyLabels, policyValues, gestioneTokenPolicy, gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward, pa);

			// Tipo operazione = CHANGE per evitare di aggiungere if, questa e' a tutti gli effetti una servlet di CHANGE
			porteApplicativeHelper.controlloAccessiAutorizzazione(dati, TipoOperazione.CHANGE, servletChiamante,pa,
					autenticazione, autorizzazione, autorizzazioneCustom, 
					autorizzazioneAutenticati, urlAutorizzazioneAutenticati, sizeSoggettiPA, null, null,
					autorizzazioneRuoli,  urlAutorizzazioneRuoli, numRuoli, null, 
					autorizzazioneRuoliTipologia, ruoloMatch,
					confPers, isSupportatoAutenticazione, contaListe, false, false);
			
			porteApplicativeHelper.controlloAccessiAutorizzazioneContenuti(dati, autorizzazioneContenuti);
			
			dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, idAsps, dati);
			
			pd.setDati(dati);
			
			pd.setMessage(CostantiControlStation.LABEL_AGGIORNAMENTO_EFFETTUATO_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, 
					ForwardParams.OTHER(""));
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI , 
					ForwardParams.OTHER(""));
		} 
	}

}
