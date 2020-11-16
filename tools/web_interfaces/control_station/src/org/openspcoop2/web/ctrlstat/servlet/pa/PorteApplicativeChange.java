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
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
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
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaBehaviour;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.transazioni.utils.PropertiesSerializator;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.autorizzazione.CostantiAutorizzazione;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniHelper;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;

/**
 * porteAppChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteApplicativeChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session);
		if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;

		try {
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			String nomePorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA);
			String idPorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String descr = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_DESCRIZIONE);
			String statoPorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_STATO_PORTA);
			String soggvirt = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SOGGETTO_VIRTUALE);
			IDSoggetto idSoggettoVirtuale = null;
			if ((soggvirt != null) && !soggvirt.equals("") && !soggvirt.equals("-")) {
				idSoggettoVirtuale = new IDSoggetto(soggvirt.split("/")[0],soggvirt.split("/")[1]);
			}
			String servizio = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SERVIZIO);
			String azione = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONE);
			String stateless = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_STATELESS);
			String behaviour = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_BEHAVIOUR);
			String gestBody = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_GESTIONE_BODY);
			String gestManifest = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_GESTIONE_MANIFEST);
			String ricsim = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_RICEVUTA_ASINCRONA_SIMMETRICA);
			String ricasim = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_RICEVUTA_ASINCRONA_ASIMMETRICA);
			String scadcorr = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SCADENZA_CORRELAZIONE_APPLICATIVA);
			String integrazione = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_INTEGRAZIONE);
			String servizioApplicativo = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_SERVIZIO_APPLICATIVO);

			String azid = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONE_ID);
			String modeaz = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_AZIONE);
			String forceWsdlBased = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_FORCE_INTERFACE_BASED);
			
			String messageEngine = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_GESTIONE_MESSAGE_ENGINE);
			
			BinaryParameter allegatoXacmlPolicy = porteApplicativeHelper.getBinaryParameter(CostantiControlStation.PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY);
			
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idTab = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!porteApplicativeHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}
			
			String serviceBindingS = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SERVICE_BINDING);
			ServiceBinding serviceBinding = null;
			if(StringUtils.isNotEmpty(serviceBindingS))
				serviceBinding = ServiceBinding.valueOf(serviceBindingS);
			
			// check su oldNomePD
			PageData pdOld =  ServletUtils.getPageDataFromSession(session);
			String oldNomePA = pdOld.getHidden(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_OLD_NOME_PA);
			oldNomePA = (((oldNomePA != null) && !oldNomePA.equals("")) ? oldNomePA : nomePorta);
			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			String postBackElementName = porteApplicativeHelper.getPostBackElementName();
			if(postBackElementName!=null && PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_AZIONE.equals(postBackElementName)) {
				// ho cambiato modalita', elimino il valore
				azione = null;
			}
			
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteApplicativeCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteApplicativeCore);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(porteApplicativeCore);
			
			boolean usataInConfigurazioni = false;
			boolean usataInConfigurazioneDefault = false;
			boolean addTrattinoSelezioneNonEffettuata = false;

			// Prendo la porta applicativa
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));

			// Prendo nome, tipo e pdd del soggetto
			String tipoNomeSoggettoProprietario = null;
			String tipoSoggettoProprietario = null;
			String nomeSoggettoProprietario = null;
			if(porteApplicativeCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggInt);
				tipoNomeSoggettoProprietario = soggetto.getTipo() + "/" + soggetto.getNome();
				tipoSoggettoProprietario = soggetto.getTipo();
				nomeSoggettoProprietario = soggetto.getNome();
			}else{
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(soggInt);
				tipoNomeSoggettoProprietario = soggetto.getTipo() + "/" + soggetto.getNome();
				tipoSoggettoProprietario = soggetto.getTipo();
				nomeSoggettoProprietario = soggetto.getNome();
			}

			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoSoggettoProprietario);
			//			List<String> tipiServizioCompatibiliAccordo = apsCore.getTipiServiziGestitiProtocollo(protocollo);
			// String pdd = ss.getServer();

			boolean isSupportatoAutenticazioneSoggetti = soggettiCore.isSupportatoAutenticazioneSoggetti(protocollo);
			
			Long idAspsLong = -1L;
			if(idAsps.equals("")) {
				PortaApplicativaServizio servizio2 = pa.getServizio();
				idAspsLong = servizio2.getId();
			} else {
				idAspsLong = Long.parseLong(idAsps);
			}
			
			// controllo se la porta e' usata in qualche configurazione e se e' usata nella configurazione di default
			MappingErogazionePortaApplicativa mappingErogazione = new MappingErogazionePortaApplicativa();
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idAspsLong);
			IDServizio idServizioCheck = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
			mappingErogazione.setIdServizio(idServizioCheck);
			IDPortaApplicativa idPa = new IDPortaApplicativa();
			idPa.setNome(oldNomePA);
			mappingErogazione.setIdPortaApplicativa(idPa);
			usataInConfigurazioni = porteApplicativeCore.existsMappingErogazionePortaApplicativa(mappingErogazione);
			IDPortaApplicativa idPortaApplicativaAssociataDefault = porteApplicativeCore.getIDPortaApplicativaAssociataDefault(idServizioCheck);
			usataInConfigurazioneDefault = (idPortaApplicativaAssociataDefault != null && idPortaApplicativaAssociataDefault.getNome().equals(idPa.getNome()));
			
			// Informazioni sul numero di ServiziApplicativi, Correlazione Applicativa e stato Message-Security
			int numSA =pa.sizeServizioApplicativoList();
			int numRuoli = 0;
			if(pa.getRuoli()!=null){
				numRuoli = pa.getRuoli().sizeRuoloList();
			}
			int numScope = 0;
			if(pa.getScope()!=null){
				numScope = pa.getScope().sizeScopeList();
			}
			
			String[] servizioApplicativoList = null;
			Long idSa = null;
			if(numSA<=1){
				if(servizioApplicativo==null || "".equals(servizioApplicativo)){
					if(numSA==1){
						servizioApplicativo = pa.getServizioApplicativo(0).getNome();
						idSa = pa.getServizioApplicativo(0).getIdServizioApplicativo();
					}
					else{
						servizioApplicativo = "-";
					}
				}
				String[]  servizioApplicativoList_tmp = PorteApplicativeServizioApplicativoAdd.loadSAErogatori(pa, saCore, soggInt, false);
				servizioApplicativoList = new String[servizioApplicativoList_tmp.length+1];
				servizioApplicativoList[0] = "-";
				for (int i = 0; i < servizioApplicativoList_tmp.length; i++) {
					servizioApplicativoList[(i+1)] = servizioApplicativoList_tmp[i];
				}
			}
			else{
				servizioApplicativo = null;
			}
			
			String statoMessageSecurity  = pa.getStatoMessageSecurity() ;
			int numCorrelazioneReq =0; 
			if(pa.getCorrelazioneApplicativa() != null)
				numCorrelazioneReq  = pa.getCorrelazioneApplicativa().sizeElementoList();

			int numCorrelazioneRes =0;
			if(pa.getCorrelazioneApplicativaRisposta() != null)
				numCorrelazioneRes = pa.getCorrelazioneApplicativaRisposta().sizeElementoList();

			int numProprProt = pa.sizeProprietaList();

			// Stato MTOM
			boolean isMTOMAbilitatoReq = false;
			boolean isMTOMAbilitatoRes= false;
			if(pa.getMtomProcessor()!= null){
				if(pa.getMtomProcessor().getRequestFlow() != null){
					if(pa.getMtomProcessor().getRequestFlow().getMode() != null){
						MTOMProcessorType mode = pa.getMtomProcessor().getRequestFlow().getMode();
						if(!mode.equals(MTOMProcessorType.DISABLE))
							isMTOMAbilitatoReq = true;
					}
				}

				if(pa.getMtomProcessor().getResponseFlow() != null){
					if(pa.getMtomProcessor().getResponseFlow().getMode() != null){
						MTOMProcessorType mode = pa.getMtomProcessor().getResponseFlow().getMode();
						if(!mode.equals(MTOMProcessorType.DISABLE))
							isMTOMAbilitatoRes = true;
					}
				}
			}

			String statoMTOM  = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_DISABILITATO;

			if(isMTOMAbilitatoReq || isMTOMAbilitatoRes)
				statoMTOM  = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_ABILITATO;


			// Prendo il numero di correlazioni applicative
			CorrelazioneApplicativa ca = pa.getCorrelazioneApplicativa();
			int numCorrApp = 0;
			if (ca != null) {
				numCorrApp = ca.sizeElementoList();
			}
			
			String statoValidazione = null;
			String tipoValidazione = null;
			String applicaMTOM = "";
			ValidazioneContenutiApplicativi vx = pa.getValidazioneContenutiApplicativi();
			if (vx == null) {
				statoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_DISABILITATO;
				tipoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE_INTERFACE;
			} else {
				if(vx.getStato()!=null)
					statoValidazione = vx.getStato().toString();
				if ((statoValidazione == null) || "".equals(statoValidazione)) {
					statoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_DISABILITATO;
				}
				
				if(vx.getTipo()!=null)
					tipoValidazione = vx.getTipo().toString();
				if (tipoValidazione == null || "".equals(tipoValidazione)) {
					tipoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE_INTERFACE ;
				}
				
				if(vx.getAcceptMtomMessage()!=null)
					if (vx.getAcceptMtomMessage().equals(StatoFunzionalita.ABILITATO)) 
						applicaMTOM = Costanti.CHECK_BOX_ENABLED;
			}

			String autenticazione = pa.getAutenticazione();
			String autenticazioneCustom = null;
			if (autenticazione != null &&
					!TipoAutenticazione.getValues().contains(autenticazione)) {
				autenticazioneCustom = autenticazione;
				autenticazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM;
			}

			String autenticazioneOpzionale = "";
			if(pa.getAutenticazioneOpzionale()!=null){
				if (pa.getAutenticazioneOpzionale().equals(StatoFunzionalita.ABILITATO)) {
					autenticazioneOpzionale = Costanti.CHECK_BOX_ENABLED;
				}
			}
			
			TipoAutenticazionePrincipal autenticazionePrincipal = porteApplicativeCore.getTipoAutenticazionePrincipal(pa.getProprietaAutenticazioneList());
			List<String> autenticazioneParametroList = porteApplicativeCore.getParametroAutenticazione(autenticazione, pa.getProprietaAutenticazioneList());

			String autorizzazione = null;
			String autorizzazioneCustom = null;
			String autorizzazioneAutenticati = null;
			String autorizzazioneRuoli = null;
			String autorizzazioneRuoliTipologia = null;
			String ruoloMatch = null;
			String autorizzazioneContenuti = pa.getAutorizzazioneContenuto();
			String autorizzazioneContenutiProperties = null;
			String autorizzazioneContenutiStato = null;
			
			if(autorizzazioneContenuti == null) {
				autorizzazioneContenutiStato = StatoFunzionalita.DISABILITATO.getValue();
			} else if(autorizzazioneContenuti.equals(CostantiAutorizzazione.AUTORIZZAZIONE_CONTENUTO_BUILT_IN)) {
				autorizzazioneContenutiStato = StatoFunzionalita.ABILITATO.getValue();
				List<Proprieta> proprietaAutorizzazioneContenutoList = pa.getProprietaAutorizzazioneContenutoList();
				StringBuilder sb = new StringBuilder();
				for (Proprieta proprieta : proprietaAutorizzazioneContenutoList) {
					if(sb.length() >0)
						sb.append("\n");
					
					sb.append(proprieta.getNome()).append("=").append(proprieta.getValore()); 
				}						
				
				autorizzazioneContenutiProperties = sb.toString();
			} else { // custom
				autorizzazioneContenutiStato = CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_CUSTOM;
			}
			
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
			
			
			if (ruoloMatch == null) {
				if(pa.getRuoli()!=null && pa.getRuoli().getMatch()!=null){
					ruoloMatch = pa.getRuoli().getMatch().getValue();
				}
			}
			
			String gestioneToken = null; 
			String gestioneTokenPolicy = null;
			String gestioneTokenOpzionale = null;
			String gestioneTokenValidazioneInput = null;
			String gestioneTokenIntrospection = null;
			String gestioneTokenUserInfo = null;
			String gestioneTokenTokenForward = null;
			
			String autenticazioneTokenIssuer = null;
			String autenticazioneTokenClientId = null;
			String autenticazioneTokenSubject = null;
			String autenticazioneTokenUsername = null;
			String autenticazioneTokenEMail = null;
			
			String autorizzazione_tokenOptions = null;
			String autorizzazione_token = null;
			
			if(pa.getGestioneToken() != null) {
				gestioneTokenPolicy = pa.getGestioneToken().getPolicy();
				if(gestioneTokenPolicy == null) {
					gestioneToken = StatoFunzionalita.DISABILITATO.getValue();
					gestioneTokenPolicy = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
				} else {
					gestioneToken = StatoFunzionalita.ABILITATO.getValue();
				}
				
				StatoFunzionalita tokenOpzionale = pa.getGestioneToken().getTokenOpzionale();
				if(tokenOpzionale == null) {
					gestioneTokenOpzionale = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_OPZIONALE;
				}else { 
					gestioneTokenOpzionale = tokenOpzionale.getValue();
				}
				
				StatoFunzionalitaConWarning validazione = pa.getGestioneToken().getValidazione();
				if(validazione == null) {
					gestioneTokenValidazioneInput = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_VALIDAZIONE_INPUT;
				}else { 
					gestioneTokenValidazioneInput = validazione.getValue();
				}
				
				StatoFunzionalitaConWarning introspection = pa.getGestioneToken().getIntrospection();
				if(introspection == null) {
					gestioneTokenIntrospection = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_INTROSPECTION;
				}else { 
					gestioneTokenIntrospection = introspection.getValue();
				}
				
				StatoFunzionalitaConWarning userinfo = pa.getGestioneToken().getUserInfo();
				if(userinfo == null) {
					gestioneTokenUserInfo = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_USER_INFO;
				}else { 
					gestioneTokenUserInfo = userinfo.getValue();
				}
				
				StatoFunzionalita tokenForward = pa.getGestioneToken().getForward();
				if(tokenForward == null) {
					gestioneTokenTokenForward = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TOKEN_FORWARD;
				}else { 
					gestioneTokenTokenForward = tokenForward.getValue();
				}
				
				autorizzazione_tokenOptions = pa.getGestioneToken().getOptions();
				if((autorizzazione_tokenOptions!=null && !"".equals(autorizzazione_tokenOptions))) {
					autorizzazione_token = Costanti.CHECK_BOX_ENABLED;
				}
				else {
					autorizzazione_token = Costanti.CHECK_BOX_DISABLED;
				}
				
				if(pa.getGestioneToken().getAutenticazione() != null) {
					
					StatoFunzionalita issuer = pa.getGestioneToken().getAutenticazione().getIssuer();
					if(issuer == null) {
						autenticazioneTokenIssuer = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_ISSUER;
					}else { 
						autenticazioneTokenIssuer = issuer.getValue();
					}
					
					StatoFunzionalita clientId = pa.getGestioneToken().getAutenticazione().getClientId();
					if(clientId == null) {
						autenticazioneTokenClientId = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_CLIENT_ID;
					}else { 
						autenticazioneTokenClientId = clientId.getValue();
					}
					
					StatoFunzionalita subject = pa.getGestioneToken().getAutenticazione().getSubject();
					if(subject == null) {
						autenticazioneTokenSubject = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_SUBJECT;
					}else { 
						autenticazioneTokenSubject = subject.getValue();
					}
					
					StatoFunzionalita username = pa.getGestioneToken().getAutenticazione().getUsername();
					if(username == null) {
						autenticazioneTokenUsername = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_USERNAME;
					}else { 
						autenticazioneTokenUsername = username.getValue();
					}
					
					StatoFunzionalita mailTmp = pa.getGestioneToken().getAutenticazione().getEmail();
					if(mailTmp == null) {
						autenticazioneTokenEMail = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_EMAIL;
					}else { 
						autenticazioneTokenEMail = mailTmp.getValue();
					}
					
				}
				else {
					autenticazioneTokenIssuer = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_ISSUER;
					autenticazioneTokenClientId = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_CLIENT_ID;
					autenticazioneTokenSubject = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_SUBJECT;
					autenticazioneTokenUsername = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_USERNAME;
					autenticazioneTokenEMail = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_EMAIL;
				}
			}
			else {
				gestioneToken = StatoFunzionalita.DISABILITATO.getValue();
				gestioneTokenPolicy = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
				gestioneTokenOpzionale = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_OPZIONALE;
				
				gestioneTokenValidazioneInput = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_VALIDAZIONE_INPUT;
				gestioneTokenIntrospection = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_INTROSPECTION;
				gestioneTokenUserInfo = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_USER_INFO;
				gestioneTokenTokenForward = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TOKEN_FORWARD;
				
				autenticazioneTokenIssuer = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_ISSUER;
				autenticazioneTokenClientId = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_CLIENT_ID;
				autenticazioneTokenSubject = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_SUBJECT;
				autenticazioneTokenUsername = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_USERNAME;
				autenticazioneTokenEMail = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_EMAIL;
			}
			
			String autorizzazioneScope = null;
			String autorizzazioneScopeMatch = null;
			
			if(pa.getScope() != null) {
				autorizzazioneScope =  pa.getScope().getStato().equals(StatoFunzionalita.ABILITATO) ? Costanti.CHECK_BOX_ENABLED : ""; 
								
				if(pa.getScope()!=null && pa.getScope().getMatch()!=null){
					autorizzazioneScopeMatch = pa.getScope().getMatch().getValue();
				}
			} else {
				autorizzazioneScope = "";
			}
			
			// se ho modificato il soggetto ricalcolo il servizio e il service binding
			if (postBackElementName != null) {
				if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SOGGETTO_VIRTUALE)) {
					servizio = null;
					serviceBinding = null;
				} else if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SERVIZIO)) {
					serviceBinding = null;
				}  
			}
			
			List<String> tipiServizioCompatibiliAccordo = new ArrayList<String>();
			if(serviceBinding == null) {
				List<ServiceBinding> serviceBindingListProtocollo = apsCore.getServiceBindingListProtocollo(protocollo);
				
				if(serviceBindingListProtocollo != null && serviceBindingListProtocollo.size() > 0) {
					for (ServiceBinding serviceBinding2 : serviceBindingListProtocollo) {
						List<String> tipiServizioCompatibiliAccordoTmp = apsCore.getTipiServiziGestitiProtocollo(protocollo,serviceBinding2);
						
						for (String tipoTmp : tipiServizioCompatibiliAccordoTmp) {
							if(!tipiServizioCompatibiliAccordo.contains(tipoTmp))
								tipiServizioCompatibiliAccordo.add(tipoTmp);
						}
					}
				}
			} else {
				tipiServizioCompatibiliAccordo = apsCore.getTipiServiziGestitiProtocollo(protocollo,serviceBinding);
			}
			
			List<Parameter> lstParm = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);
			
			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, session);
			boolean datiAltroPorta = ServletUtils.isCheckBoxEnabled(porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_ALTRO_PORTA));
			boolean datiAltroApi = ServletUtils.isCheckBoxEnabled(porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_ALTRO_API));
			boolean datiInvocazione = ServletUtils.isCheckBoxEnabled(porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_DATI_INVOCAZIONE));
						
			String nomeBreadCrumb = oldNomePA;
			if(parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE) {
//				List<MappingErogazionePortaApplicativa> mappingServiziPorteAppList = apsCore.mappingServiziPorteAppList(idServizioCheck, idAspsLong, null);
//				MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa = null;
//				for (MappingErogazionePortaApplicativa mappingErogazionePortaApplicativaTmp : mappingServiziPorteAppList) {
//					if(mappingErogazionePortaApplicativaTmp.getIdPortaApplicativa().getNome().equals(oldNomePA)) {
//						mappingErogazionePortaApplicativa = mappingErogazionePortaApplicativaTmp;
//						break;
//					}
//				}
//				
//				if(mappingErogazionePortaApplicativa.isDefault()) {
//					nomeBreadCrumb = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MAPPING_EROGAZIONE_PA_NOME_DEFAULT;
//				} else {
//					nomeBreadCrumb = mappingErogazionePortaApplicativa.getNome(); 
//				}
				
				if(datiInvocazione) {
					lstParm.remove(lstParm.size()-1);
					if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
						lstParm.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_PORTE_APPLICATIVE_MODIFICA_DATI_INVOCAZIONE,null));
					} else {
						lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_DATI_INVOCAZIONE_DI + porteApplicativeHelper.getLabelIdServizio(asps),null));
					}
					nomeBreadCrumb=null;
				}
				else if(datiAltroPorta) {
					String labelPerPorta = null;
					if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
						labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
								PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_OPZIONI_AVANZATE_DI,
								PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_OPZIONI_AVANZATE,
								pa);
					}
					else {
						lstParm.remove(lstParm.size()-1);
						labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_OPZIONI_AVANZATE_DI+pa.getNome();
					}
					
					lstParm.add(new Parameter(labelPerPorta,  null));
					//lstParm.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_OPZIONI_AVANZATE_DI + porteApplicativeHelper.getLabelIdServizio(asps),null));
					nomeBreadCrumb=null;
				}
				else if(datiAltroApi) {
					lstParm.remove(lstParm.size()-1);
					if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
						lstParm.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_OPZIONI_AVANZATE,null));
					} else {
						lstParm.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_OPZIONI_AVANZATE_DI + porteApplicativeHelper.getLabelIdServizio(asps),null));
					}
					nomeBreadCrumb=null;
				}
				else {
					nomeBreadCrumb = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(null,null,pa);
				}
			}
			
			if(nomeBreadCrumb!=null) {
				lstParm.add(new Parameter(nomeBreadCrumb , null));
			}

			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if (porteApplicativeHelper.isEditModeInProgress()) {

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm);
				
				String patternAzione = azione;

				if (descr == null) {
					descr = pa.getDescrizione();
				}
				if (statoPorta == null) {
					statoPorta = pa.getStato()!=null ? pa.getStato().getValue() : CostantiConfigurazione.ABILITATO.getValue();
				}
				if (stateless == null) {
					if(pa.getStateless()!=null)
						stateless = pa.getStateless().toString();
				}
				if (behaviour == null) {
					behaviour = (pa.getBehaviour()!=null ? pa.getBehaviour().getNome() : null);
				}
				if (gestBody == null) {
					String allegaBody = null;
					if(pa.getAllegaBody()!=null)
						allegaBody = pa.getAllegaBody().toString();
					String scartaBody = null;
					if(pa.getScartaBody()!=null)
						scartaBody = pa.getScartaBody().toString();
					if (PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_ABILITATO.equals(allegaBody) &&
							PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO.equals(scartaBody))
						gestBody = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_BODY_ALLEGA;
					else if (PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO.equals(allegaBody) && 
							PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_ABILITATO.equals(scartaBody))
						gestBody = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_BODY_SCARTA;
					else if (PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO.equals(allegaBody) && 
							PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO.equals(scartaBody))
						gestBody = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_BODY_NONE;
				}
				if (gestManifest == null) {
					if(pa.getGestioneManifest()!=null)
						gestManifest = pa.getGestioneManifest().toString();
				}
				if (ricsim == null) {
					if(pa.getRicevutaAsincronaSimmetrica()!=null)
						ricsim = pa.getRicevutaAsincronaSimmetrica().toString();
					if ((ricsim == null) || "".equals(ricsim)) {
						ricsim = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_ABILITATO;
					}
				}
				if (ricasim == null) {
					if(pa.getRicevutaAsincronaAsimmetrica()!=null)
						ricasim = pa.getRicevutaAsincronaAsimmetrica().toString();
					if ((ricasim == null) || "".equals(ricasim)) {
						ricasim = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_ABILITATO;
					}
				}
				if ((scadcorr == null) && (ca != null)) {
					scadcorr = ca.getScadenza();
				}

				if (integrazione == null) {
					integrazione = pa.getIntegrazione();
				}
				
				if (messageEngine == null) {
					if(pa.getOptions()!=null) {
						Hashtable<String, String> props = PropertiesSerializator.convertoFromDBColumnValue(pa.getOptions());
						if(props!=null && props.size()>0) {
							String msgFactory = props.get(CostantiPdD.OPTIONS_MESSAGE_FACTORY);
							if(msgFactory!=null) {
								messageEngine = msgFactory;
							}
						}
					}
				}
				
				if (soggvirt == null) {
					// soggvirt = risultato.getString("soggvirt");
					PortaApplicativaSoggettoVirtuale pasv = pa.getSoggettoVirtuale();
					if (pasv == null) {
						soggvirt="-";					
					} else {
						soggvirt = pasv.getTipo()+"/"+pasv.getNome();
						servizio = soggvirt + " " +pa.getServizio().getTipo()+"/"+pa.getServizio().getNome();
					}
				}

				// recupero informazioni su servizio se non specificato l'id
				// del servizio
				if ( servizio==null || "".equals(servizio)) {
					if( !(soggvirt!=null && idSoggettoVirtuale!=null) ){
						servizio = tipoSoggettoProprietario+"/"+nomeSoggettoProprietario+" "+pa.getServizio().getTipo()+"/"+pa.getServizio().getNome() + "/" + pa.getServizio().getVersione().intValue();
					}
				}
				
				if (modeaz == null) {
					PortaApplicativaAzione paa = pa.getAzione();
					if (paa == null) {
						modeaz = "";
						azione = "";
						patternAzione = "";
					} else {
						if(paa.getIdentificazione()!=null)
							modeaz = paa.getIdentificazione().toString();
						azione = paa.getNome();
						patternAzione = paa.getPattern();
						azid = "" + paa.getId();
					}
					if ((modeaz == null) || "".equals(modeaz)) {
						modeaz = PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT;
					}
					boolean useForceWSDLBased = true;
					// assegno costante
					if (modeaz != null) {
						switch (PortaApplicativaAzioneIdentificazione.toEnumConstant(modeaz)) {
						case STATIC:
							useForceWSDLBased = false;
							int azidInt = 0;
							if (azid != null)
								azidInt = Integer.parseInt(azid);
							if ( (azidInt == -2) || (azidInt>0) ) {
//								modeaz = IdentificazioneView.REGISTER_INPUT.toString();
								azid = azione;
							} else {
//								modeaz = IdentificazioneView.USER_INPUT.toString();
							}
							break;
						case INTERFACE_BASED:
							useForceWSDLBased = false;
							break;
						case CONTENT_BASED:
						case INPUT_BASED:
						case HEADER_BASED:
						case URL_BASED:
						case SOAP_ACTION_BASED:
						default:
							break;
						}

						if(useForceWSDLBased){
							StatoFunzionalita forceWsdlBased2 = paa.getForceInterfaceBased();

							if(forceWsdlBased2 != null && forceWsdlBased2.equals(StatoFunzionalita.ABILITATO)){
								forceWsdlBased = "yes";
							} else {
								forceWsdlBased = "";
							}
						}
					}
				}

				if ((modeaz != null) && !modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && 
						(azione == null)) {
					azione = "";
				}

				if(!usataInConfigurazioneDefault) {
					PortaApplicativaAzione paa = pa.getAzione();
					if(paa != null) {
						List<String> azioneDelegataList = paa.getAzioneDelegataList();
						StringBuilder sb = new StringBuilder();
						
						for (String aD : azioneDelegataList) {
							if(sb.length() > 0) sb.append(", ");
							sb.append(aD);
						}
						azione = sb.toString();
					}
				}

				// Prendo la lista di soggetti (tranne il mio) e li metto in un
				// array per la funzione SoggettoVirtuale
				String[] soggettiList = null;
				String[] soggettiListLabel = null;
				Boolean soggVirt = (Boolean) session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_SOGGETTI_VIRTUALI);
				if (soggVirt) {
					List<IDServizio> list = null;
					List<IDSoggetto> listSoggetti = new ArrayList<IDSoggetto>();
					Vector<String> identitaSoggetti = new Vector<String>();
					try{
						list = apsCore.getAllIdServizi(new FiltroRicercaServizi());
					}catch(DriverRegistroServiziNotFound dNotFound){}
					if(list!=null){
						for (int i = 0; i < list.size(); i++) {
							String idSoggetto = list.get(i).getSoggettoErogatore().toString();
							if (!idSoggetto.equals(tipoNomeSoggettoProprietario)){ // non aggiungo il soggetto proprietario della porta applicativa
								if(identitaSoggetti.contains(idSoggetto)==false){
									identitaSoggetti.add(idSoggetto);
									IDSoggetto soggettoErogatore = list.get(i).getSoggettoErogatore();
									String protocolloSoggettoErogatore = soggettiCore.getProtocolloAssociatoTipoSoggetto(soggettoErogatore.getTipo());
									if(protocolloSoggettoErogatore.equals(protocollo)){
										listSoggetti.add(soggettoErogatore);
									}
								}
							}
						}
					}
					int totEl = listSoggetti.size() + 1;
					soggettiList = new String[totEl];
					soggettiListLabel = new String[totEl];
					soggettiList[0] = "-";
					soggettiListLabel[0] = "-";
					Map<String, String> soggettiMapTmp = new HashMap<String,String>();
					List<String> listSoggettiOrdered = new ArrayList<String>();
					for (IDSoggetto idSoggetto : listSoggetti) {
						listSoggettiOrdered.add(idSoggetto.getTipo() + "/" + idSoggetto.getNome());
						soggettiMapTmp.put(idSoggetto.getTipo() + "/" + idSoggetto.getNome(), porteApplicativeHelper.getLabelNomeSoggetto(protocollo,
								idSoggetto.getTipo(), idSoggetto.getNome()));
					}
					Collections.sort(listSoggettiOrdered);
					int i = 1;
					for (String idSOrdered : listSoggettiOrdered) {
						soggettiList[i] = idSOrdered;
						soggettiListLabel[i] = soggettiMapTmp.get(idSOrdered); 
						i++;
					}
					
				}

				// Prendo la lista di servizi e li metto in un array
				String[] serviziList = null;
				String[] serviziListLabel = null;
				FiltroRicercaServizi filtroServizi = new FiltroRicercaServizi();
				if ( (!soggvirt.equals("")) && (!soggvirt.equals("-")) ){
					filtroServizi.setTipoSoggettoErogatore(soggvirt.split("/")[0]);
					filtroServizi.setNomeSoggettoErogatore(soggvirt.split("/")[1]);
				}
				else{
					filtroServizi.setTipoSoggettoErogatore(tipoSoggettoProprietario);
					filtroServizi.setNomeSoggettoErogatore(nomeSoggettoProprietario);
				}
				List<IDServizio> listIdServ = null;
				try{
					listIdServ = apsCore.getAllIdServizi(filtroServizi);
				}catch(DriverRegistroServiziNotFound dNotFound){}
				
				if(listIdServ!=null && listIdServ.size()>0){
					List<String> serviziListTmp = new ArrayList<String>();

					Map<String, IDServizio> serviziMapTmp = new HashMap<String,IDServizio>();
					for (IDServizio idServizio : listIdServ) {
						if(tipiServizioCompatibiliAccordo.contains(idServizio.getTipo())){
							String keyServizio = idServizio.getSoggettoErogatore().getTipo() + "/" + idServizio.getSoggettoErogatore().getNome() + " " + idServizio.getTipo() + "/" + idServizio.getNome() + "/" + idServizio.getVersione().intValue();
							serviziListTmp.add(keyServizio);
							serviziMapTmp.put(keyServizio, idServizio);
						}
					}

					Collections.sort(serviziListTmp);
					serviziList = serviziListTmp.toArray(new String[1]);
					serviziListLabel = new String[serviziList.length];
					for (int i = 0; i < serviziList.length; i++) {
						String idServTmp = serviziList[i];
						serviziListLabel[i] = porteApplicativeHelper.getLabelIdServizio(protocollo, serviziMapTmp.get(idServTmp));
					}
				}
				
				IDServizio idServizio = null;
				AccordoServizioParteSpecifica servS = null;
				if (servizio != null) {
					boolean servizioPresenteInLista  = false;
					if(serviziList!=null && serviziList.length>0){
						for (int i = 0; i < serviziList.length; i++) {
							if(serviziList[i].equals(servizio)){
								servizioPresenteInLista = true;
								break;
							}
						}
					}
					if(servizioPresenteInLista){
						String [] tmp = servizio.split(" ");
						idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tmp[1].split("/")[0],tmp[1].split("/")[1], 
								tmp[0].split("/")[0],tmp[0].split("/")[1], 
								Integer.parseInt(tmp[1].split("/")[2])); 
						try{
							servS = apsCore.getServizio(idServizio);
						}catch(DriverRegistroServiziNotFound dNotFound){
						}
					}
					if(servS==null){
						
						// è cambiato il soggetto erogatore. non è più valido il servizio
						servizio = null;
						idServizio = null;
						if(serviziList!=null && serviziList.length>0){
							servizio = serviziList[0];
							String [] tmp = servizio.split(" ");
							idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tmp[1].split("/")[0],tmp[1].split("/")[1], 
									tmp[0].split("/")[0],tmp[0].split("/")[1], 
									Integer.parseInt(tmp[1].split("/")[2])); 
							try{
								servS = apsCore.getServizio(idServizio);
							}catch(DriverRegistroServiziNotFound dNotFound){
							}
							if(servS==null){
								servizio = null;
								idServizio = null;
							}
						}
					}
				}
				
				AccordoServizioParteComuneSintetico as = null;
				if (servS != null) {
					IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(servS.getAccordoServizioParteComune());
					as = apcCore.getAccordoServizioSintetico(idAccordo);
					if(serviceBinding == null) {
						serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());
					}
				}
				
				
				String[] azioniList = null;
				String[] azioniListLabel = null;
				List<String> filtraAzioniUtilizzate = new ArrayList<String>(); // TODO controllare
				if ((modeaz != null) && modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT)) {
				
					Map<String,String> azioni = apcCore.getAzioniConLabel(servS, as, addTrattinoSelezioneNonEffettuata , true, filtraAzioniUtilizzate);
					if(azioni != null && azioni.size() > 0) {
						azioniList = new String[azioni.size()];
						azioniListLabel = new String[azioni.size()];
						int i = 0;
						for (String string : azioni.keySet()) {
							azioniList[i] = string;
							azioniListLabel[i] = azioni.get(string);
							i++;
						}
					}
				}

				// setto oldNomePD
				pd.addHidden(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_OLD_NOME_PA, oldNomePA);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idPorta, idsogg, idPorta, idAsps, dati);

				dati = porteApplicativeHelper.addPorteAppToDati(TipoOperazione.CHANGE,dati, 
						nomePorta, descr, soggvirt, soggettiList, soggettiListLabel, servizio,
						serviziList, serviziListLabel, azione, azioniList, azioniListLabel, stateless, ricsim, ricasim, idsogg, 
						idPorta, statoValidazione, tipoValidazione, gestBody, gestManifest,integrazione,
						numCorrApp,scadcorr,autorizzazioneContenutiStato, autorizzazioneContenuti, autorizzazioneContenutiProperties, protocollo,
						numSA,numRuoli,ruoloMatch,
						statoMessageSecurity,statoMTOM,numCorrelazioneReq,numCorrelazioneRes,numProprProt,applicaMTOM,
						behaviour,servizioApplicativoList,servizioApplicativo,idSa,
						autenticazione, autorizzazione,
						autenticazioneOpzionale, autenticazionePrincipal, autenticazioneParametroList, autenticazioneCustom, autorizzazioneCustom,
						isSupportatoAutenticazioneSoggetti,autorizzazioneAutenticati,autorizzazioneRuoli,autorizzazioneRuoliTipologia,
						servS,as,serviceBinding,
						statoPorta,modeaz,  azid, patternAzione, forceWsdlBased, usataInConfigurazioni,usataInConfigurazioneDefault,
						StatoFunzionalita.ABILITATO.equals(pa.getRicercaPortaAzioneDelegata()), 
						(pa.getAzione()!=null ? pa.getAzione().getNomePortaDelegante() : null), gestioneToken,null,null,
						gestioneTokenPolicy,gestioneTokenOpzionale,
						gestioneTokenValidazioneInput,gestioneTokenIntrospection,gestioneTokenUserInfo,gestioneTokenTokenForward,
						autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						autorizzazione_token,autorizzazione_tokenOptions,
						autorizzazioneScope,numScope, autorizzazioneScopeMatch,allegatoXacmlPolicy,
						messageEngine, pa.getCanale());

				pd.setDati(dati);



				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE,
						ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.porteAppCheckData(TipoOperazione.CHANGE, oldNomePA, isSupportatoAutenticazioneSoggetti);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd,lstParm);

				// Prendo la lista di soggetti (tranne il mio) e li metto in un
				// array per la funzione SoggettoVirtuale
				String[] soggettiList = null;
				String[] soggettiListLabel = null;
				Boolean soggVirt = (Boolean) session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_SOGGETTI_VIRTUALI);
				if (soggVirt) {
					List<IDServizio> list = null;
					List<IDSoggetto> listSoggetti = new ArrayList<IDSoggetto>();
					Vector<String> identitaSoggetti = new Vector<String>();
					try{
						list = apsCore.getAllIdServizi(new FiltroRicercaServizi());
					}catch(DriverRegistroServiziNotFound dNotFound){}
					if(list!=null){
						for (int i = 0; i < list.size(); i++) {
							String idSoggetto = list.get(i).getSoggettoErogatore().toString();
							if (!idSoggetto.equals(tipoNomeSoggettoProprietario)){ // non aggiungo il soggetto proprietario della porta applicativa
								if(identitaSoggetti.contains(idSoggetto)==false){
									identitaSoggetti.add(idSoggetto);
									listSoggetti.add(list.get(i).getSoggettoErogatore());
								}
							}
						}
					}
					int totEl = listSoggetti.size() + 1;
					soggettiList = new String[totEl];
					soggettiListLabel = new String[totEl];
					soggettiList[0] = "-";
					soggettiListLabel[0] = "-";
					Map<String, String> soggettiMapTmp = new HashMap<String,String>();
					int i = 1;
					List<String> listSoggettiOrdered = new ArrayList<String>();
					for (IDSoggetto idSoggetto : listSoggetti) {
						listSoggettiOrdered.add(idSoggetto.getTipo() + "/" + idSoggetto.getNome());
						soggettiMapTmp.put(idSoggetto.getTipo() + "/" + idSoggetto.getNome(), porteApplicativeHelper.getLabelNomeSoggetto(protocollo,
								idSoggetto.getTipo(), idSoggetto.getNome()));
					}
					Collections.sort(listSoggettiOrdered);
					for (String idSOrdered : listSoggettiOrdered) {
						soggettiList[i] = idSOrdered;
						soggettiListLabel[i] = soggettiMapTmp.get(idSOrdered); 
						i++;
					}
				}

				// Prendo la lista di servizi e li metto in un array
				String[] serviziList = null;
				String[] serviziListLabel = null;
				FiltroRicercaServizi filtroServizi = new FiltroRicercaServizi();
				if ( (!soggvirt.equals("")) && (!soggvirt.equals("-")) ){
					filtroServizi.setTipoSoggettoErogatore(soggvirt.split("/")[0]);
					filtroServizi.setNomeSoggettoErogatore(soggvirt.split("/")[1]);
				}
				else{
					filtroServizi.setTipoSoggettoErogatore(tipoSoggettoProprietario);
					filtroServizi.setNomeSoggettoErogatore(nomeSoggettoProprietario);
				}
				List<IDServizio> listIdServ = null;
				try{
					listIdServ = apsCore.getAllIdServizi(filtroServizi);
				}catch(DriverRegistroServiziNotFound dNotFound){}
				
				if(listIdServ!=null && listIdServ.size()>0){
					List<String> serviziListTmp = new ArrayList<String>();
					Map<String, IDServizio> serviziMapTmp = new HashMap<String,IDServizio>();
					for (IDServizio idServizio : listIdServ) {
						if(tipiServizioCompatibiliAccordo.contains(idServizio.getTipo())){
							String keyServizio = idServizio.getSoggettoErogatore().getTipo() + "/" + idServizio.getSoggettoErogatore().getNome() + " " + idServizio.getTipo() + "/" + idServizio.getNome() + "/" + idServizio.getVersione().intValue();
							serviziListTmp.add(keyServizio);
							serviziMapTmp.put(keyServizio, idServizio);
						}
					}

					Collections.sort(serviziListTmp);
					serviziList = serviziListTmp.toArray(new String[1]);
					serviziListLabel = new String[serviziList.length];
					for (int i = 0; i < serviziList.length; i++) {
						String idServTmp = serviziList[i];
						serviziListLabel[i] = porteApplicativeHelper.getLabelIdServizio(protocollo, serviziMapTmp.get(idServTmp));
					}
				}

				AccordoServizioParteSpecifica servS = null;
				IDServizio idServizio = null;
				try{
					if (servizio != null) {
						String [] tmp = servizio.split(" ");
						idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tmp[1].split("/")[0],tmp[1].split("/")[1], 
								tmp[0].split("/")[0],tmp[0].split("/")[1], 
								Integer.parseInt(tmp[1].split("/")[2])); 
						try{
							servS = apsCore.getServizio(idServizio);
						}catch(DriverRegistroServiziNotFound dNotFound){
						}
					}
				} catch (Exception e) {
					// Il refresh, in seguito al cambio della validazione puo'
					// avvenire anche se il servizio non e' selezionato
				}
				
				AccordoServizioParteComuneSintetico as = null;
				if(servS!=null){
					IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(servS.getAccordoServizioParteComune());
					as = apcCore.getAccordoServizioSintetico(idAccordo);
				}
				
				// Prendo le azioni associate al servizio
				String[] azioniList = null;
				String[] azioniListLabel = null;
				List<String> filtraAzioniUtilizzate = new ArrayList<String>(); // TODO controllare
				if ((modeaz != null) && modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT)) {
				
					Map<String,String> azioni = apcCore.getAzioniConLabel(servS, as, addTrattinoSelezioneNonEffettuata , true, filtraAzioniUtilizzate);
					if(azioni != null && azioni.size() > 0) {
						azioniList = new String[azioni.size()];
						azioniListLabel = new String[azioni.size()];
						int i = 0;
						for (String string : azioni.keySet()) {
							azioniList[i] = string;
							azioniListLabel[i] = azioni.get(string);
							i++;
						}
					}
				}

				// setto oldNomePD
				pd.addHidden(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_OLD_NOME_PA, oldNomePA);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idPorta, idsogg, idPorta, idAsps, dati);

				dati = porteApplicativeHelper.addPorteAppToDati(TipoOperazione.CHANGE,dati,
						nomePorta, descr, soggvirt, soggettiList, soggettiListLabel, servizio, 
						serviziList, serviziListLabel, azione, azioniList, azioniListLabel,  stateless, ricsim,
						ricasim, idsogg, idPorta, statoValidazione, tipoValidazione, gestBody, gestManifest,integrazione,
						numCorrApp,scadcorr,autorizzazioneContenutiStato, autorizzazioneContenuti, autorizzazioneContenutiProperties,protocollo,
						numSA,numRuoli,ruoloMatch,
						statoMessageSecurity,statoMTOM,numCorrelazioneReq,numCorrelazioneRes,numProprProt,applicaMTOM,
						behaviour,servizioApplicativoList,servizioApplicativo,idSa,
						autenticazione, autorizzazione,
						autenticazioneOpzionale, autenticazionePrincipal, autenticazioneParametroList, autenticazioneCustom, autorizzazioneCustom,
						isSupportatoAutenticazioneSoggetti,autorizzazioneAutenticati,autorizzazioneRuoli,autorizzazioneRuoliTipologia,
						servS,as,serviceBinding,
						statoPorta,modeaz,  azid, azione, forceWsdlBased, usataInConfigurazioni,usataInConfigurazioneDefault,
						StatoFunzionalita.ABILITATO.equals(pa.getRicercaPortaAzioneDelegata()), 
						(pa.getAzione()!=null ? pa.getAzione().getNomePortaDelegante() : null), gestioneToken,null,null,
						gestioneTokenPolicy,gestioneTokenOpzionale,
						gestioneTokenValidazioneInput,gestioneTokenIntrospection,gestioneTokenUserInfo,gestioneTokenTokenForward,
						autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						autorizzazione_token,autorizzazione_tokenOptions,
						autorizzazioneScope,numScope, autorizzazioneScopeMatch,allegatoXacmlPolicy,
						messageEngine, pa.getCanale());

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE, 
						ForwardParams.CHANGE());
			}

			// Modifico i dati della porta applicativa nel db
			pa.setNome(nomePorta);
			IDPortaApplicativa oldIDPortaApplicativaForUpdate = new IDPortaApplicativa();
			oldIDPortaApplicativaForUpdate.setNome(oldNomePA);
			pa.setOldIDPortaApplicativaForUpdate(oldIDPortaApplicativaForUpdate);
			pa.setDescrizione(descr);
			if(statoPorta==null || "".equals(statoPorta) || CostantiConfigurazione.ABILITATO.toString().equals(statoPorta)){
				pa.setStato(StatoFunzionalita.ABILITATO);
			}
			else{
				pa.setStato(StatoFunzionalita.DISABILITATO);
			}
		
			if (stateless!=null && stateless.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_STATELESS_DEFAULT))
				pa.setStateless(null);
			else
				pa.setStateless(StatoFunzionalita.toEnumConstant(stateless));
			if (PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_BODY_ALLEGA.equals(gestBody))
				pa.setAllegaBody(StatoFunzionalita.toEnumConstant(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_ABILITATO));
			else
				pa.setAllegaBody(StatoFunzionalita.toEnumConstant(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO));
			if (PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_BODY_SCARTA.equals(gestBody))
				pa.setScartaBody(StatoFunzionalita.toEnumConstant(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_ABILITATO));
			else
				pa.setScartaBody(StatoFunzionalita.toEnumConstant(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO));
			if (gestManifest!=null && gestManifest.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_MANIFEST_DEFAULT))
				pa.setGestioneManifest(null);
			else
				pa.setGestioneManifest(StatoFunzionalita.toEnumConstant(gestManifest));
			pa.setRicevutaAsincronaSimmetrica(StatoFunzionalita.toEnumConstant(ricsim));
			pa.setRicevutaAsincronaAsimmetrica(StatoFunzionalita.toEnumConstant(ricasim));
			
			Hashtable<String, String> props = PropertiesSerializator.convertoFromDBColumnValue(pa.getOptions());
			props.remove(CostantiPdD.OPTIONS_MESSAGE_FACTORY);
			if(messageEngine!=null && !"".equals(messageEngine) && !CostantiControlStation.GESTIONE_MESSAGE_ENGINE_DEFAULT.equals(messageEngine)) {
				props.put(CostantiPdD.OPTIONS_MESSAGE_FACTORY, messageEngine);
			}
			if(props.size()>0) {
				PropertiesSerializator ps = new PropertiesSerializator(props);
				pa.setOptions(ps.convertToDBColumnValue());
			}
			else {
				pa.setOptions(null);
			}
			
			if (idSoggettoVirtuale!=null) {
				String tipoSoggVirt = idSoggettoVirtuale.getTipo();
				String nomeSoggVirt = idSoggettoVirtuale.getNome();
				PortaApplicativaSoggettoVirtuale pasv = new PortaApplicativaSoggettoVirtuale();
				pasv.setTipo(tipoSoggVirt);
				pasv.setNome(nomeSoggVirt);
				pa.setSoggettoVirtuale(pasv);
			} else
				pa.setSoggettoVirtuale(null);
			if (servizio!=null) {
				String [] tmp = servizio.split(" ");
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tmp[1].split("/")[0],tmp[1].split("/")[1], 
						tmp[0].split("/")[0],tmp[0].split("/")[1], 
						Integer.parseInt(tmp[1].split("/")[2])); 
				PortaApplicativaServizio pas = new PortaApplicativaServizio();
				pas.setTipo(idServizio.getTipo());
				pas.setNome(idServizio.getNome());
				pas.setVersione(idServizio.getVersione());
				pa.setServizio(pas);
			} else
				pa.setServizio(null);
			
			// se l azione e' settata allora creo il bean
			if(modeaz!=null && modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_DELEGATED_BY)) {
				// non modifico paAzione
			}
			else if ((( (azione!=null && !azione.equals("")) || 
							modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_INPUT_BASED) ||
							modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_SOAP_ACTION_BASED) ||
							modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_PROTOCOL_BASED) ||
							modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_HEADER_BASED) ||
							modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_INTERFACE_BASED))) ||
							(azid!=null && !azid.equals("")) ) {
				PortaApplicativaAzione paa = new PortaApplicativaAzione();

				if (modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT)) {
					azione = azid;
					if(paa.getId()<=0){
						paa.setId(-2l);
					}
				}
				
				paa.setIdentificazione(PortaApplicativaAzioneIdentificazione.toEnumConstant(modeaz));
				
				if (modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_HEADER_BASED) ||
						modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_URL_BASED) ||
						modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_CONTENT_BASED)
						) {
					paa.setNome(null);
					paa.setPattern(azione);
				}
				else if (modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT)
						) {
					paa.setNome(azione);
					paa.setPattern(null);
				}
				else {
					paa.setNome(null);
					paa.setPattern(null);
				}
				
				//FORCE WSDL BASED
				if(!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT) && 
						!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_PROTOCOL_BASED) &&
						!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_INTERFACE_BASED)){

					if(forceWsdlBased != null && (ServletUtils.isCheckBoxEnabled(forceWsdlBased))){
						paa.setForceInterfaceBased(StatoFunzionalita.ABILITATO);
					}else {
						paa.setForceInterfaceBased(StatoFunzionalita.DISABILITATO);
					}
				}else {
					paa.setForceInterfaceBased(null);
				}

				pa.setAzione(paa);
			}  else {
				pa.setAzione(null);
			}

			// Cambio i dati della vecchia CorrelazioneApplicativa
			// Non ne creo una nuova, altrimenti mi perdo le vecchie entry
			if (ca != null)
				ca.setScadenza(scadcorr);
			pa.setCorrelazioneApplicativa(ca);
			pa.setIntegrazione(integrazione);
			
			if(!porteApplicativeCore.isConnettoriMultipliEnabled()) {
				if(behaviour!=null && !"".equals(behaviour)){
					pa.setBehaviour(new PortaApplicativaBehaviour());
					pa.getBehaviour().setNome(behaviour);
				}else 
					pa.setBehaviour(null);
			}

			if(!datiInvocazione && !datiAltroApi && !datiAltroPorta) {
				if(servizioApplicativo!=null && !"".equals(servizioApplicativo)){
					// Se il servizioApplicativo e' valorizzato deve esistere un solo SA nella porta applicativa
					if(pa.sizeServizioApplicativoList()>0)
						pa.removeServizioApplicativo(0);
					if(!"-".equals(servizioApplicativo)){
						PortaApplicativaServizioApplicativo sa = new PortaApplicativaServizioApplicativo();
						sa.setNome(servizioApplicativo);
						pa.addServizioApplicativo(sa);
					}
				}
			}
			
			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);


			List<PortaApplicativa> lista = null;
			int idLista = -1;
			
		
			switch (parentPA) {
			case PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE:
				
				boolean datiInvocazioneCheck = ServletUtils.isCheckBoxEnabled(porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_DATI_INVOCAZIONE));
				boolean datiAltroApiCheck = ServletUtils.isCheckBoxEnabled(porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_ALTRO_API));
				if(datiInvocazioneCheck || datiAltroApiCheck) {
					if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
						ErogazioniHelper erogazioniHelper = new ErogazioniHelper(request, pd, session);
						erogazioniHelper.prepareErogazioneChange(TipoOperazione.CHANGE, asps, null);
						ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
						return ServletUtils.getStrutsForwardEditModeFinished(mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI, ForwardParams.CHANGE());
					}
					
					idLista = Liste.SERVIZI;
					ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
					
					String tipologia = ServletUtils.getObjectFromSession(session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
					if(tipologia!=null) {
						if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
							ricerca.addFilter(idLista, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE);
						}
					}
					
					boolean [] permessi = new boolean[2];
					PermessiUtente pu = ServletUtils.getUserFromSession(session).getPermessi();
					permessi[0] = pu.isServizi();
					permessi[1] = pu.isAccordiCooperazione();
					List<AccordoServizioParteSpecifica> listaS = null;
					String superUser   = ServletUtils.getUserLoginFromSession(session);
					if(apsCore.isVisioneOggettiGlobale(superUser)){
						listaS = apsCore.soggettiServizioList(null, ricerca,permessi,session);
					}else{
						listaS = apsCore.soggettiServizioList(superUser, ricerca,permessi,session);
					}
					AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
					apsHelper.prepareServiziList(ricerca, listaS);
				}
				else {			
					idLista = Liste.CONFIGURAZIONE_EROGAZIONE;
					ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
					int idServizio = Integer.parseInt(idAsps);
					asps = apsCore.getAccordoServizioParteSpecifica(idServizio);
					IDServizio idServizio2 = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps); 
					Long idSoggetto = asps.getIdSoggetto() != null ? asps.getIdSoggetto() : -1L;
					List<MappingErogazionePortaApplicativa> lista2 = apsCore.mappingServiziPorteAppList(idServizio2,asps.getId(),ricerca);
					AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
					apsHelper.prepareServiziConfigurazioneList(lista2, idAsps, idSoggetto+"", ricerca);
				}
				break;
			case PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_SOGGETTO:
				idLista = Liste.PORTE_APPLICATIVE_BY_SOGGETTO;
				ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
				lista = porteApplicativeCore.porteAppList(soggInt, ricerca);
				porteApplicativeHelper.preparePorteAppList(ricerca, lista,idLista);
				break;
			case PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE:
			default:
				idLista = Liste.PORTE_APPLICATIVE;
				ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
				lista = porteApplicativeCore.porteAppList(null, ricerca);
				porteApplicativeHelper.preparePorteAppList(ricerca, lista,idLista);
				break;
			}

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			ForwardParams fwP = ForwardParams.CHANGE();
			
			if(datiAltroPorta && !porteApplicativeHelper.isModalitaCompleta()) {
				fwP = PorteApplicativeCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE;
			}
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE, 
					fwP);
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE,
					ForwardParams.CHANGE());

		} 
	}
}
