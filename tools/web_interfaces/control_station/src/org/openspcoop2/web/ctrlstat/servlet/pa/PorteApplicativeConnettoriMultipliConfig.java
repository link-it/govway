/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import org.apache.commons.lang.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaBehaviour;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoBehaviour;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.behaviour.built_in.load_balance.ConfigurazioneLoadBalancer;
import org.openspcoop2.pdd.core.behaviour.built_in.load_balance.health_check.HealthCheckConfigurazione;
import org.openspcoop2.pdd.core.behaviour.built_in.load_balance.health_check.HealthCheckUtils;
import org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyConfigurazione;
import org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyUtils;
import org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.ConfigurazioneMultiDeliver;
import org.openspcoop2.pdd.core.behaviour.conditional.ConfigurazioneCondizionale;
import org.openspcoop2.pdd.core.behaviour.conditional.ConfigurazioneSelettoreCondizione;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ControlStationLogger;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


/**     
 * PorteApplicativeConnettoriMultipliConfig
 *
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteApplicativeConnettoriMultipliConfig extends Action {

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
			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();
			
			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, session, request).getValue();
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte applicative
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session, request);
			if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;
			
			boolean isModalitaCompleta = porteApplicativeHelper.isModalitaCompleta();

			String id = porteApplicativeHelper.getParametroLong(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = porteApplicativeHelper.getParametroLong(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			String idAsps = porteApplicativeHelper.getParametroLong(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";

			// Prendo il nome della porta
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteApplicativeCore);
			SoggettiCore soggettiCore = new SoggettiCore(porteApplicativeCore);
			
			PortaApplicativa portaApplicativa = porteApplicativeCore.getPortaApplicativa(idInt);
			String idporta = portaApplicativa.getNome();
			
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(portaApplicativa.getTipoSoggettoProprietario());

			MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa = porteApplicativeCore.getMappingErogazionePortaApplicativa(portaApplicativa);

			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Long.parseLong(idAsps));
			AccordoServizioParteComuneSintetico as = null;
			if(porteApplicativeCore.isRegistroServiziLocale()){
				int idAcc = asps.getIdAccordo().intValue();
				as = apcCore.getAccordoServizioSintetico(idAcc);
			}
			else{
				as = apcCore.getAccordoServizioSintetico(IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
			}

			ServiceBinding serviceBinding = porteApplicativeCore.toMessageServiceBinding(as.getServiceBinding());


			String stato = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO);
			String modalitaConsegna = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA);
			String tipoCustom = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_CUSTOM_TIPO);
			String loadBalanceStrategia = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_STRATEGIA);

			String consegnaCondizionaleS = porteApplicativeHelper.getParametroBoolean(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONSEGNA_CONDIZIONALE);
			boolean consegnaCondizionale = ServletUtils.isCheckBoxEnabled(consegnaCondizionaleS);

			String connettoreImplementaAPI = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_IMPLEMENTA_API);
			String notificheCondizionaliEsitoS = porteApplicativeHelper.getParametroBoolean(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CONDIZIONALI_ESITO);
			boolean notificheCondizionaliEsito = ServletUtils.isCheckBoxEnabled(notificheCondizionaliEsitoS);
			String [] esitiTransazione = porteApplicativeHelper.getParameterValues(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_ESITI_TRANSAZIONE);
			
			String selezioneConnettoreBy = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SELEZIONE_CONNETTORE_BY);
			String identificazioneCondizionale = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_IDENTIFICAZIONE_CONDIZIONALE);
			String identificazioneCondizionalePattern = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_IDENTIFICAZIONE_CONDIZIONALE_PATTERN);
			String identificazioneCondizionalePrefisso = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_IDENTIFICAZIONE_CONDIZIONALE_PREFISSO);
			String identificazioneCondizionaleSuffisso = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_IDENTIFICAZIONE_CONDIZIONALE_SUFFISSO);
			
			String servletRegolePerAzioni = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_AZIONI_LIST;
			List<Parameter> listaParametriServletRegolePerAzioni = new ArrayList<>();
			int numeroRegolePerAzioni = 0;
			
			String condizioneNonIdentificataAbortTransactionS = porteApplicativeHelper.getParametroBoolean(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_ABORT_TRANSACTION);
			boolean condizioneNonIdentificataAbortTransaction = ServletUtils.isCheckBoxEnabled(condizioneNonIdentificataAbortTransactionS);
			String condizioneNonIdentificataDiagnostico = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_DIAGNOSTICO);
			String condizioneNonIdentificataConnettore = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_CONNETTORE);
			
			String connettoreNonTrovatoAbortTransactionS = porteApplicativeHelper.getParametroBoolean(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_ABORT_TRANSACTION);
			boolean connettoreNonTrovatoAbortTransaction = ServletUtils.isCheckBoxEnabled(connettoreNonTrovatoAbortTransactionS);
			String connettoreNonTrovatoDiagnostico = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_DIAGNOSTICO);
			String connettoreNonTrovatoConnettore = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_NON_TROVATO_CONNETTORE);
			
			String stickyS = porteApplicativeHelper.getParametroBoolean(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_STICKY);
			boolean sticky = ServletUtils.isCheckBoxEnabled(stickyS);
			String stickyTipoSelettore = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_STICKY_TIPO_SELETTORE);
			String stickyTipoSelettorePattern = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_STICKY_PATTERN);
			String stickyMaxAge = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_STICKY_MAX_AGE);
			
			String passiveHealthCheckS = porteApplicativeHelper.getParametroBoolean(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_PASSIVE_HEALTH_CHECK);
			boolean passiveHealthCheck = ServletUtils.isCheckBoxEnabled(passiveHealthCheckS);
			String passiveHealthCheckExcludeForSeconds = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA_LOAD_BALANCE_PASSIVE_HEALTH_CHECK_EXCLUDE_FOR_SECONDS);
			
			boolean accessoDaListaAPS = false;
			String accessoDaAPSParametro = null;
			// nell'erogazione vale sempre
			accessoDaAPSParametro = porteApplicativeHelper.getParametroBoolean(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS);
			if(Costanti.CHECK_BOX_ENABLED_TRUE.equals(accessoDaAPSParametro)) {
				accessoDaListaAPS = true;
			}

			String idTab = porteApplicativeHelper.getParametroInteger(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!porteApplicativeHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(request, session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}

			int numeroProprietaCustom = portaApplicativa.getBehaviour() != null ? portaApplicativa.getBehaviour().sizeProprietaList() : 0;
			String servletProprietaCustom = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_PROPERTIES_LIST;
			List<Parameter> listaParametriServletProprietaCustom = new ArrayList<>();
			Parameter pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, id);
			Parameter pNomePorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, idporta);
			Parameter pIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps);
			String idTabP = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			Parameter pIdTab = new Parameter(CostantiControlStation.PARAMETRO_ID_TAB, idTabP != null ? idTabP : "");
			Parameter pAccessoDaAPS = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS, accessoDaAPSParametro != null ? accessoDaAPSParametro : "");
			String connettoreAccessoGruppi = porteApplicativeHelper.getParametroBoolean(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI);
			String connettoreRegistro = porteApplicativeHelper.getParametroBoolean(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO);
			Parameter pConnettoreAccessoDaGruppi = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI, connettoreAccessoGruppi);
			Parameter pConnettoreAccesso = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO, connettoreRegistro);

			listaParametriServletProprietaCustom.add(pIdSogg);
			listaParametriServletProprietaCustom.add(pIdPorta);
			listaParametriServletProprietaCustom.add(pNomePorta);
			listaParametriServletProprietaCustom.add(pIdAsps);
			listaParametriServletProprietaCustom.add(pIdTab);
			listaParametriServletProprietaCustom.add(pAccessoDaAPS);
			listaParametriServletProprietaCustom.add(pConnettoreAccessoDaGruppi);
			listaParametriServletProprietaCustom.add(pConnettoreAccesso);
			
			listaParametriServletRegolePerAzioni.addAll(listaParametriServletProprietaCustom);

			boolean visualizzaLinkProprietaCustom = false;
			boolean modificaStatoAbilitata = true;
			boolean visualizzaLinkRegolePerAzioni = false;
			org.openspcoop2.pdd.core.behaviour.conditional.ConfigurazioneCondizionale configurazioneCondizionaleOld = null;
			if(portaApplicativa.getBehaviour() != null) {
				TipoBehaviour behaviourType = TipoBehaviour.toEnumConstant(portaApplicativa.getBehaviour().getNome());
				visualizzaLinkProprietaCustom = behaviourType.equals(TipoBehaviour.CUSTOM);

				if(portaApplicativa.sizeServizioApplicativoList() > 1)
					modificaStatoAbilitata = false;
				
				if(org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.isConfigurazioneCondizionale(portaApplicativa, ControlStationCore.getLog())){
					configurazioneCondizionaleOld = org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.read(portaApplicativa, ControlStationCore.getLog());
					numeroRegolePerAzioni = configurazioneCondizionaleOld.getRegoleOrdinate().size();
					visualizzaLinkRegolePerAzioni = true;
				}
			}

			String postBackElementName = porteApplicativeHelper.getPostBackElementName();
			if(postBackElementName != null ){
				
				boolean reinitParametriCondizionali = false;
				
				if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONSEGNA_CONDIZIONALE)) {
					reinitParametriCondizionali = consegnaCondizionale;
				}
				else if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_MODALITA_CONSEGNA)) {
					reinitParametriCondizionali = TipoBehaviour.CONSEGNA_CONDIZIONALE.getValue().equals(modalitaConsegna);
					consegnaCondizionale = false;
					
					if(TipoBehaviour.CONSEGNA_CON_NOTIFICHE.getValue().equals(modalitaConsegna)) {
						// uso il default
						ConfigurazioneMultiDeliver config = new ConfigurazioneMultiDeliver();
						notificheCondizionaliEsito = config.isNotificheByEsito();
						esitiTransazione = porteApplicativeHelper.getEsitiTransazione(config);
					}
				}else if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_IDENTIFICAZIONE_CONDIZIONALE)) {
					identificazioneCondizionalePattern = "";
					identificazioneCondizionalePrefisso = "";
					identificazioneCondizionaleSuffisso = ""; 
				}
				
				if(reinitParametriCondizionali) {
					selezioneConnettoreBy = PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SELEZIONE_CONNETTORE_BY_FILTRO;
					identificazioneCondizionale = org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.HEADER_BASED.getValue();
					identificazioneCondizionalePattern = "";
					identificazioneCondizionalePrefisso = "";
					identificazioneCondizionaleSuffisso = ""; 
					condizioneNonIdentificataAbortTransaction = true;
					condizioneNonIdentificataDiagnostico = StatoFunzionalita.DISABILITATO.getValue();
					condizioneNonIdentificataConnettore = "";
					connettoreNonTrovatoAbortTransaction = true;
					connettoreNonTrovatoDiagnostico = StatoFunzionalita.DISABILITATO.getValue();
					connettoreNonTrovatoConnettore = "";
				}
			}

			boolean isSoapOneWay = false;

			if(stato!= null && stato.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_ABILITATO)) {
				isSoapOneWay = porteApplicativeHelper.isSoapOneWay(portaApplicativa, mappingErogazionePortaApplicativa, asps, as, serviceBinding);
			}

			// select list della sezione notifiche
			List<String> connettoriImplementaAPIValues = new ArrayList<>();
			List<String> connettoriImplementaAPILabels = new ArrayList<>();
			String connettorePrincipale = null;
			List<PortaApplicativaServizioApplicativo> servizioApplicativoList = portaApplicativa.getServizioApplicativoList();
			if(servizioApplicativoList != null) {
				TipoBehaviour behaviourType = null;
				if(portaApplicativa.getBehaviour()!=null) {
					behaviourType = TipoBehaviour.toEnumConstant(portaApplicativa.getBehaviour().getNome());
				}
				for (PortaApplicativaServizioApplicativo paSA : servizioApplicativoList) {
					String nomeConnettorePaSA = porteApplicativeHelper.getLabelNomePortaApplicativaServizioApplicativo(paSA);

					connettoriImplementaAPIValues.add(nomeConnettorePaSA);
					connettoriImplementaAPILabels.add(nomeConnettorePaSA);
					
					if(TipoBehaviour.CONSEGNA_CON_NOTIFICHE.equals(behaviourType) && porteApplicativeHelper.isConnettoreDefault(paSA)) {
						connettorePrincipale=nomeConnettorePaSA;
					}
				}
			}
			if(connettoreImplementaAPI!=null && !"".equals(connettoreImplementaAPI)) {
				connettorePrincipale=connettoreImplementaAPI; // posso averlo cambiato
			}

			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				if(accessoDaListaAPS) {
					if(!isModalitaCompleta) {
						if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
							labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG;
						} else {
							labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_DI+
									porteApplicativeHelper.getLabelIdServizio(asps);
						}
					}
					else {
						labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG;
					}
				}
				else {
					labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_DI,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG,
							portaApplicativa);
				}
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_DI+idporta;
			}

			if(accessoDaListaAPS) {
				lstParam.remove(lstParam.size()-1);
			}

			lstParam.add(new Parameter(labelPerPorta,  null));

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParam);
			
			// imposta menu' contestuale
			porteApplicativeHelper.impostaComandiMenuContestualePA(asps, protocollo);

			if(	porteApplicativeHelper.isEditModeInProgress()){

				if(stato == null) {
					if(portaApplicativa.getBehaviour() == null) {
						stato = PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_DISABILITATO;
						modalitaConsegna = TipoBehaviour.CONSEGNA_LOAD_BALANCE.getValue();
						consegnaCondizionale = false;
						connettoreImplementaAPI = "";
						notificheCondizionaliEsito = false;
						esitiTransazione = null;
						
						selezioneConnettoreBy = PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SELEZIONE_CONNETTORE_BY_FILTRO;
						identificazioneCondizionale = org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.HEADER_BASED.getValue();
						identificazioneCondizionalePattern = "";
						identificazioneCondizionalePrefisso = "";
						identificazioneCondizionaleSuffisso = ""; 
						condizioneNonIdentificataAbortTransaction = true;
						condizioneNonIdentificataDiagnostico = StatoFunzionalita.DISABILITATO.getValue();
						condizioneNonIdentificataConnettore = "";
						connettoreNonTrovatoAbortTransaction = true;
						connettoreNonTrovatoDiagnostico = StatoFunzionalita.DISABILITATO.getValue();
						connettoreNonTrovatoConnettore = "";
						
					} else {
						stato = PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_ABILITATO;

						TipoBehaviour behaviourType = TipoBehaviour.toEnumConstant(portaApplicativa.getBehaviour().getNome());

						modalitaConsegna = behaviourType.getValue();
						
						isSoapOneWay = porteApplicativeHelper.isSoapOneWay(portaApplicativa, mappingErogazionePortaApplicativa, asps, as, serviceBinding);
																		
						if(behaviourType.equals(TipoBehaviour.CONSEGNA_LOAD_BALANCE)) {
							loadBalanceStrategia = ConfigurazioneLoadBalancer.readLoadBalancerType(portaApplicativa.getBehaviour());
							
							if(StickyUtils.isConfigurazioneSticky(portaApplicativa, ControlStationLogger.getPddConsoleCoreLogger())) {
								sticky = true;
								StickyConfigurazione stickyConfig = StickyUtils.read(portaApplicativa, ControlStationLogger.getPddConsoleCoreLogger());
								stickyTipoSelettore = stickyConfig.getTipoSelettore().getValue();
								stickyTipoSelettorePattern = stickyConfig.getPattern();
								if(stickyConfig.getMaxAgeSeconds()!=null && stickyConfig.getMaxAgeSeconds()>0) {
									stickyMaxAge = stickyConfig.getMaxAgeSeconds().intValue()+"";
								}
								else {
									stickyMaxAge = null;
								}
							}
							else {
								sticky = false;
							}
							
							HealthCheckConfigurazione config = HealthCheckUtils.read(portaApplicativa, ControlStationLogger.getPddConsoleCoreLogger());
							passiveHealthCheck = config.isPassiveCheckEnabled();
							if(passiveHealthCheck) {
								passiveHealthCheckExcludeForSeconds = config.getPassiveHealthCheck_excludeForSeconds().intValue()+"";
							}
							
							consegnaCondizionale = org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.isConfigurazioneCondizionale(portaApplicativa, ControlStationCore.getLog());
														
						} else if(behaviourType.equals(TipoBehaviour.CONSEGNA_MULTIPLA) ||
								behaviourType.equals(TipoBehaviour.CONSEGNA_CONDIZIONALE) ||
								behaviourType.equals(TipoBehaviour.CONSEGNA_CON_NOTIFICHE)) {
							consegnaCondizionale = org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.isConfigurazioneCondizionale(portaApplicativa, ControlStationCore.getLog());

							if(!behaviourType.equals(TipoBehaviour.CONSEGNA_MULTIPLA)) {
								org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.ConfigurazioneMultiDeliver configurazioneMultiDeliver = org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.MultiDeliverUtils.read(portaApplicativa);
								connettoreImplementaAPI = configurazioneMultiDeliver.getTransazioneSincrona_nomeConnettore() != null ? configurazioneMultiDeliver.getTransazioneSincrona_nomeConnettore() : "";
								notificheCondizionaliEsito = configurazioneMultiDeliver.isNotificheByEsito();
								esitiTransazione = porteApplicativeHelper.getEsitiTransazione(configurazioneMultiDeliver);
							} else {
								connettoreImplementaAPI = "";
								notificheCondizionaliEsito = false;
								esitiTransazione = null;
							}
						}
						else if(behaviourType.equals(TipoBehaviour.CUSTOM)) {
							visualizzaLinkProprietaCustom = true;
							tipoCustom = portaApplicativa.getBehaviour().getNome();
						}
						
						if(consegnaCondizionale) {
							org.openspcoop2.pdd.core.behaviour.conditional.ConfigurazioneCondizionale configurazioneCondizionale = org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.read(portaApplicativa, ControlStationCore.getLog());
							
							ConfigurazioneSelettoreCondizione configurazioneSelettoreCondizione = configurazioneCondizionale.getDefaultConfig();
							
							selezioneConnettoreBy = configurazioneCondizionale.isByFilter() ? PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SELEZIONE_CONNETTORE_BY_FILTRO:
								PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SELEZIONE_CONNETTORE_BY_NOME;
							identificazioneCondizionale = configurazioneSelettoreCondizione.getTipoSelettore().getValue();
							identificazioneCondizionalePattern = configurazioneSelettoreCondizione.getPattern();
							identificazioneCondizionalePrefisso = configurazioneSelettoreCondizione.getPrefix();
							identificazioneCondizionaleSuffisso = configurazioneSelettoreCondizione.getSuffix();
							
							org.openspcoop2.pdd.core.behaviour.conditional.IdentificazioneFallitaConfigurazione condizioneNonIdentificata = configurazioneCondizionale.getCondizioneNonIdentificata();
							
							condizioneNonIdentificataAbortTransaction = condizioneNonIdentificata.isAbortTransaction();
							
							if(!condizioneNonIdentificata.isEmitDiagnosticError() && !condizioneNonIdentificata.isEmitDiagnosticInfo())
								condizioneNonIdentificataDiagnostico = StatoFunzionalita.DISABILITATO.getValue();
							else if(condizioneNonIdentificata.isEmitDiagnosticError())
								condizioneNonIdentificataDiagnostico = PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_DIAGNOSTICO_ERROR;
							else if(condizioneNonIdentificata.isEmitDiagnosticInfo())
								condizioneNonIdentificataDiagnostico = PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_DIAGNOSTICO_INFO;
								
							condizioneNonIdentificataConnettore = condizioneNonIdentificata.getNomeConnettore();
							
							org.openspcoop2.pdd.core.behaviour.conditional.IdentificazioneFallitaConfigurazione connettoreNonTrovato = configurazioneCondizionale.getNessunConnettoreTrovato();
							
							connettoreNonTrovatoAbortTransaction = connettoreNonTrovato.isAbortTransaction();
							
							if(!connettoreNonTrovato.isEmitDiagnosticError() && !connettoreNonTrovato.isEmitDiagnosticInfo())
								connettoreNonTrovatoDiagnostico = StatoFunzionalita.DISABILITATO.getValue();
							else if(connettoreNonTrovato.isEmitDiagnosticError())
								connettoreNonTrovatoDiagnostico = PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_DIAGNOSTICO_ERROR;
							else if(connettoreNonTrovato.isEmitDiagnosticInfo())
								connettoreNonTrovatoDiagnostico = PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_DIAGNOSTICO_INFO;
								
							connettoreNonTrovatoConnettore = connettoreNonTrovato.getNomeConnettore();
						} else {
							selezioneConnettoreBy = PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SELEZIONE_CONNETTORE_BY_FILTRO;
							identificazioneCondizionale = org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.HEADER_BASED.getValue();
							identificazioneCondizionalePattern = "";
							identificazioneCondizionalePrefisso = "";
							identificazioneCondizionaleSuffisso = ""; 
							condizioneNonIdentificataAbortTransaction = true;
							condizioneNonIdentificataDiagnostico = StatoFunzionalita.DISABILITATO.getValue();
							condizioneNonIdentificataConnettore = "";
							connettoreNonTrovatoAbortTransaction = true;
							connettoreNonTrovatoDiagnostico = StatoFunzionalita.DISABILITATO.getValue();
							connettoreNonTrovatoConnettore = "";
						}
					}
				}

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null,idAsps, dati);

				dati = porteApplicativeHelper.addConnettoriMultipliConfigurazioneToDati(dati, TipoOperazione.OTHER, protocollo, accessoDaAPSParametro, stato, modalitaConsegna, tipoCustom, 
						numeroProprietaCustom, servletProprietaCustom, listaParametriServletProprietaCustom, visualizzaLinkProprietaCustom, loadBalanceStrategia, modificaStatoAbilitata, 
						consegnaCondizionale, isSoapOneWay, connettoreImplementaAPI, connettoriImplementaAPIValues, connettoriImplementaAPILabels, connettorePrincipale,
						notificheCondizionaliEsito, esitiTransazione,
						serviceBinding, selezioneConnettoreBy, identificazioneCondizionale, identificazioneCondizionalePattern,
						identificazioneCondizionalePrefisso, identificazioneCondizionaleSuffisso, visualizzaLinkRegolePerAzioni, servletRegolePerAzioni, listaParametriServletRegolePerAzioni,
						numeroRegolePerAzioni,  condizioneNonIdentificataAbortTransaction,  condizioneNonIdentificataDiagnostico, condizioneNonIdentificataConnettore,
						connettoreNonTrovatoAbortTransaction, connettoreNonTrovatoDiagnostico, connettoreNonTrovatoConnettore,
						sticky, stickyTipoSelettore, stickyTipoSelettorePattern, stickyMaxAge,
						passiveHealthCheck, passiveHealthCheckExcludeForSeconds
						);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				// Forward control to the specified success URI
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONFIGURAZIONE_CONNETTORI_MULTIPLI, 
						ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.connettoriMultipliConfigurazioneCheckData(TipoOperazione.OTHER, stato, modalitaConsegna, tipoCustom, loadBalanceStrategia, isSoapOneWay,
					sticky, stickyTipoSelettore, stickyTipoSelettorePattern, stickyMaxAge,
					passiveHealthCheck, passiveHealthCheckExcludeForSeconds,
					serviceBinding);

			if(!isOk) {
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addConnettoriMultipliConfigurazioneToDati(dati, TipoOperazione.OTHER, protocollo, accessoDaAPSParametro, stato, modalitaConsegna, tipoCustom, 
						numeroProprietaCustom, servletProprietaCustom, listaParametriServletProprietaCustom, visualizzaLinkProprietaCustom, loadBalanceStrategia, modificaStatoAbilitata,
						consegnaCondizionale, isSoapOneWay, connettoreImplementaAPI, connettoriImplementaAPIValues, connettoriImplementaAPILabels, connettorePrincipale,
						notificheCondizionaliEsito, esitiTransazione,
						serviceBinding, selezioneConnettoreBy, identificazioneCondizionale, identificazioneCondizionalePattern,
						identificazioneCondizionalePrefisso, identificazioneCondizionaleSuffisso, visualizzaLinkRegolePerAzioni, servletRegolePerAzioni, listaParametriServletRegolePerAzioni,
						numeroRegolePerAzioni,  condizioneNonIdentificataAbortTransaction,  condizioneNonIdentificataDiagnostico,  condizioneNonIdentificataConnettore,
						connettoreNonTrovatoAbortTransaction, connettoreNonTrovatoDiagnostico, connettoreNonTrovatoConnettore,
						sticky, stickyTipoSelettore, stickyTipoSelettorePattern, stickyMaxAge,
						passiveHealthCheck, passiveHealthCheckExcludeForSeconds
						);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null,idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONFIGURAZIONE_CONNETTORI_MULTIPLI,
						ForwardParams.OTHER(""));
			}

			if(stato.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_ABILITATO)) {
				PortaApplicativaBehaviour behaviour = new PortaApplicativaBehaviour();
				portaApplicativa.setBehaviour(behaviour);
				
				TipoBehaviour behaviourType = TipoBehaviour.toEnumConstant(modalitaConsegna);

				switch (behaviourType) {
				case CONSEGNA_LOAD_BALANCE:	{
					behaviour.setNome(modalitaConsegna);
					ConfigurazioneLoadBalancer.addLoadBalancerType(behaviour, loadBalanceStrategia);
					
					// configurazione sticky
					org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyConfigurazione stickyConfig = 
							porteApplicativeHelper.toConfigurazioneSticky(sticky, stickyTipoSelettore, stickyTipoSelettorePattern, stickyMaxAge);
					StickyUtils.save(portaApplicativa, stickyConfig);
					
					// configurazione healthCheck
					org.openspcoop2.pdd.core.behaviour.built_in.load_balance.health_check.HealthCheckConfigurazione healthCheckConfig = 
							porteApplicativeHelper.toConfigurazioneHealthCheck(passiveHealthCheck, passiveHealthCheckExcludeForSeconds);
					HealthCheckUtils.save(portaApplicativa, healthCheckConfig);
					
					// configurazione multideliver
					org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.ConfigurazioneMultiDeliver configurazioneMultiDeliver = porteApplicativeHelper.toConfigurazioneMultiDeliver(connettoreImplementaAPI, notificheCondizionaliEsito, esitiTransazione);
					boolean differenziazioneConsegnaDaNotifiche = false;
					org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.MultiDeliverUtils.save(portaApplicativa, configurazioneMultiDeliver, differenziazioneConsegnaDaNotifiche);
					
					// configurazione condizionale
					if(consegnaCondizionale) {
						ConfigurazioneCondizionale configurazioneCondizionale = porteApplicativeHelper.toConfigurazioneCondizionale(consegnaCondizionale, selezioneConnettoreBy, identificazioneCondizionale, identificazioneCondizionalePattern, identificazioneCondizionalePrefisso, identificazioneCondizionaleSuffisso, condizioneNonIdentificataAbortTransaction, condizioneNonIdentificataDiagnostico, condizioneNonIdentificataConnettore, connettoreNonTrovatoAbortTransaction, connettoreNonTrovatoDiagnostico, connettoreNonTrovatoConnettore);
						org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.save(portaApplicativa, configurazioneCondizionale);
					}
					
				} 
				break;
				case CONSEGNA_MULTIPLA:
				case CONSEGNA_CONDIZIONALE:
				case CONSEGNA_CON_NOTIFICHE:
				{
					behaviour.setNome(modalitaConsegna);
					
					// configurazione multideliver
					org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.ConfigurazioneMultiDeliver configurazioneMultiDeliver = porteApplicativeHelper.toConfigurazioneMultiDeliver(connettoreImplementaAPI, notificheCondizionaliEsito, esitiTransazione);
					boolean differenziazioneConsegnaDaNotifiche = TipoBehaviour.CONSEGNA_CON_NOTIFICHE.equals(behaviourType);
					org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.MultiDeliverUtils.save(portaApplicativa, configurazioneMultiDeliver, differenziazioneConsegnaDaNotifiche);
					
					// configurazione condizionale
					if(consegnaCondizionale) {
						ConfigurazioneCondizionale configurazioneCondizionale = porteApplicativeHelper.toConfigurazioneCondizionale(consegnaCondizionale, selezioneConnettoreBy, identificazioneCondizionale, identificazioneCondizionalePattern, identificazioneCondizionalePrefisso, identificazioneCondizionaleSuffisso, condizioneNonIdentificataAbortTransaction, condizioneNonIdentificataDiagnostico, condizioneNonIdentificataConnettore, connettoreNonTrovatoAbortTransaction, connettoreNonTrovatoDiagnostico, connettoreNonTrovatoConnettore);
						if(configurazioneCondizionaleOld!=null && configurazioneCondizionaleOld.sizeRegole()>0) {
							configurazioneCondizionale.setRegolaList(configurazioneCondizionaleOld.getRegolaList());
						}
						org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.save(portaApplicativa, configurazioneCondizionale);
					}
				}
				break;
				case CUSTOM:
					behaviour.setNome(tipoCustom);
					break;
				}
			} else {
				portaApplicativa.setBehaviour(null);
			}

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), portaApplicativa);

			// rileggo la configurazione
			portaApplicativa = porteApplicativeCore.getPortaApplicativa(idInt);

			visualizzaLinkProprietaCustom = false;
			numeroProprietaCustom = 0;
			visualizzaLinkRegolePerAzioni = false;
			numeroRegolePerAzioni = 0;

			if(portaApplicativa.getBehaviour() == null) {
				stato = PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_DISABILITATO;
				modalitaConsegna = TipoBehaviour.CONSEGNA_LOAD_BALANCE.getValue();
				consegnaCondizionale = false;
				connettoreImplementaAPI = "";
				notificheCondizionaliEsito = false;
				esitiTransazione = null;
				
				selezioneConnettoreBy = PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SELEZIONE_CONNETTORE_BY_FILTRO;
				identificazioneCondizionale = org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.HEADER_BASED.getValue();
				identificazioneCondizionalePattern = "";
				identificazioneCondizionalePrefisso = "";
				identificazioneCondizionaleSuffisso = ""; 
				condizioneNonIdentificataAbortTransaction = true;
				condizioneNonIdentificataDiagnostico = StatoFunzionalita.DISABILITATO.getValue();
				condizioneNonIdentificataConnettore = "";
				connettoreNonTrovatoAbortTransaction = true;
				connettoreNonTrovatoDiagnostico = StatoFunzionalita.DISABILITATO.getValue();
				connettoreNonTrovatoConnettore = "";
			} else {
				stato = PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_ABILITATO;

				TipoBehaviour behaviourType = TipoBehaviour.toEnumConstant(portaApplicativa.getBehaviour().getNome());

				modalitaConsegna = behaviourType.getValue();
				if(behaviourType.equals(TipoBehaviour.CONSEGNA_LOAD_BALANCE)) {
					loadBalanceStrategia = ConfigurazioneLoadBalancer.readLoadBalancerType(portaApplicativa.getBehaviour());
					if(StickyUtils.isConfigurazioneSticky(portaApplicativa, ControlStationLogger.getPddConsoleCoreLogger())) {
						sticky = true;
						StickyConfigurazione stickyConfig = StickyUtils.read(portaApplicativa, ControlStationLogger.getPddConsoleCoreLogger());
						stickyTipoSelettore = stickyConfig.getTipoSelettore().getValue();
						stickyTipoSelettorePattern = stickyConfig.getPattern();
						if(stickyConfig.getMaxAgeSeconds()!=null && stickyConfig.getMaxAgeSeconds()>0) {
							stickyMaxAge = stickyConfig.getMaxAgeSeconds().intValue()+"";
						}
						else {
							stickyMaxAge = null;
						}
					}
					else {
						sticky = false;
					}
					
					HealthCheckConfigurazione config = HealthCheckUtils.read(portaApplicativa, ControlStationLogger.getPddConsoleCoreLogger());
					passiveHealthCheck = config.isPassiveCheckEnabled();
					if(passiveHealthCheck) {
						passiveHealthCheckExcludeForSeconds = config.getPassiveHealthCheck_excludeForSeconds().intValue()+"";
					}
					
					consegnaCondizionale = org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.isConfigurazioneCondizionale(portaApplicativa, ControlStationCore.getLog());
				} else if(behaviourType.equals(TipoBehaviour.CONSEGNA_MULTIPLA)) {
					consegnaCondizionale = org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.isConfigurazioneCondizionale(portaApplicativa, ControlStationCore.getLog());

					if(!behaviourType.equals(TipoBehaviour.CONSEGNA_MULTIPLA)) {
						org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.ConfigurazioneMultiDeliver configurazioneMultiDeliver = org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.MultiDeliverUtils.read(portaApplicativa);
						connettoreImplementaAPI = configurazioneMultiDeliver.getTransazioneSincrona_nomeConnettore() != null ? configurazioneMultiDeliver.getTransazioneSincrona_nomeConnettore() : "";
						notificheCondizionaliEsito = configurazioneMultiDeliver.isNotificheByEsito();
						esitiTransazione = porteApplicativeHelper.getEsitiTransazione(configurazioneMultiDeliver);
					} else {
						connettoreImplementaAPI = "";
						notificheCondizionaliEsito = false;
						esitiTransazione = null;
					}
				}
				else if(behaviourType.equals(TipoBehaviour.CUSTOM)) {
					visualizzaLinkProprietaCustom = true;
					tipoCustom = portaApplicativa.getBehaviour().getNome();
					numeroProprietaCustom = portaApplicativa.getBehaviour().sizeProprietaList();
				}
				
				if(consegnaCondizionale) {
					org.openspcoop2.pdd.core.behaviour.conditional.ConfigurazioneCondizionale configurazioneCondizionale = org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.read(portaApplicativa, ControlStationCore.getLog());
					
					ConfigurazioneSelettoreCondizione configurazioneSelettoreCondizione = configurazioneCondizionale.getDefaultConfig();
					
					selezioneConnettoreBy = configurazioneCondizionale.isByFilter() ? PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SELEZIONE_CONNETTORE_BY_FILTRO:
						PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SELEZIONE_CONNETTORE_BY_NOME;
					identificazioneCondizionale = configurazioneSelettoreCondizione.getTipoSelettore().getValue();
					identificazioneCondizionalePattern = configurazioneSelettoreCondizione.getPattern();
					identificazioneCondizionalePrefisso = configurazioneSelettoreCondizione.getPrefix();
					identificazioneCondizionaleSuffisso = configurazioneSelettoreCondizione.getSuffix();
					
					org.openspcoop2.pdd.core.behaviour.conditional.IdentificazioneFallitaConfigurazione condizioneNonIdentificata = configurazioneCondizionale.getCondizioneNonIdentificata();
					
					condizioneNonIdentificataAbortTransaction = condizioneNonIdentificata.isAbortTransaction();
					
					if(!condizioneNonIdentificata.isEmitDiagnosticError() && !condizioneNonIdentificata.isEmitDiagnosticInfo())
						condizioneNonIdentificataDiagnostico = StatoFunzionalita.DISABILITATO.getValue();
					else if(condizioneNonIdentificata.isEmitDiagnosticError())
						condizioneNonIdentificataDiagnostico = PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_DIAGNOSTICO_ERROR;
					else if(condizioneNonIdentificata.isEmitDiagnosticInfo())
						condizioneNonIdentificataDiagnostico = PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_DIAGNOSTICO_INFO;
						
					condizioneNonIdentificataConnettore = condizioneNonIdentificata.getNomeConnettore();
					
					org.openspcoop2.pdd.core.behaviour.conditional.IdentificazioneFallitaConfigurazione connettoreNonTrovato = configurazioneCondizionale.getNessunConnettoreTrovato();
					
					connettoreNonTrovatoAbortTransaction = connettoreNonTrovato.isAbortTransaction();
					
					if(!connettoreNonTrovato.isEmitDiagnosticError() && !connettoreNonTrovato.isEmitDiagnosticInfo())
						connettoreNonTrovatoDiagnostico = StatoFunzionalita.DISABILITATO.getValue();
					else if(connettoreNonTrovato.isEmitDiagnosticError())
						connettoreNonTrovatoDiagnostico = PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_DIAGNOSTICO_ERROR;
					else if(connettoreNonTrovato.isEmitDiagnosticInfo())
						connettoreNonTrovatoDiagnostico = PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONDIZIONE_NON_IDENTIFICATA_DIAGNOSTICO_INFO;
						
					connettoreNonTrovatoConnettore = connettoreNonTrovato.getNomeConnettore();
					numeroRegolePerAzioni = configurazioneCondizionale.getRegoleOrdinate().size();
					visualizzaLinkRegolePerAzioni = true;
				} else {
					selezioneConnettoreBy = PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SELEZIONE_CONNETTORE_BY_FILTRO;
					identificazioneCondizionale = org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.HEADER_BASED.getValue();
					identificazioneCondizionalePattern = "";
					identificazioneCondizionalePrefisso = "";
					identificazioneCondizionaleSuffisso = ""; 
					condizioneNonIdentificataAbortTransaction = true;
					condizioneNonIdentificataDiagnostico = StatoFunzionalita.DISABILITATO.getValue();
					condizioneNonIdentificataConnettore = "";
					connettoreNonTrovatoAbortTransaction = true;
					connettoreNonTrovatoDiagnostico = StatoFunzionalita.DISABILITATO.getValue();
					connettoreNonTrovatoConnettore = "";
				}
			}

			// select list della sezione notifiche
			connettoriImplementaAPIValues = new ArrayList<>();
			connettoriImplementaAPILabels = new ArrayList<>();
			connettorePrincipale = null;
			servizioApplicativoList = portaApplicativa.getServizioApplicativoList();
			if(servizioApplicativoList != null) {
				TipoBehaviour behaviourType = null;
				if(portaApplicativa.getBehaviour()!=null) {
					behaviourType = TipoBehaviour.toEnumConstant(portaApplicativa.getBehaviour().getNome());
				}
				for (PortaApplicativaServizioApplicativo paSA : servizioApplicativoList) {
					String nomeConnettorePaSA = porteApplicativeHelper.getLabelNomePortaApplicativaServizioApplicativo(paSA);

					connettoriImplementaAPIValues.add(nomeConnettorePaSA);
					connettoriImplementaAPILabels.add(nomeConnettorePaSA);
					
					if(TipoBehaviour.CONSEGNA_CON_NOTIFICHE.equals(behaviourType) && porteApplicativeHelper.isConnettoreDefault(paSA)) {
						connettorePrincipale=nomeConnettorePaSA;
					}
				}
			}
			
			// preparo i campi
			List<DataElement> dati = new ArrayList<>();

			dati = porteApplicativeHelper.addConnettoriMultipliConfigurazioneToDati(dati, TipoOperazione.OTHER, protocollo, accessoDaAPSParametro, stato, modalitaConsegna, tipoCustom, 
					numeroProprietaCustom, servletProprietaCustom, listaParametriServletProprietaCustom, visualizzaLinkProprietaCustom,loadBalanceStrategia, modificaStatoAbilitata,
					consegnaCondizionale, isSoapOneWay, connettoreImplementaAPI, connettoriImplementaAPIValues, connettoriImplementaAPILabels, connettorePrincipale,
					notificheCondizionaliEsito, esitiTransazione,
					serviceBinding, selezioneConnettoreBy, identificazioneCondizionale, identificazioneCondizionalePattern,
					identificazioneCondizionalePrefisso, identificazioneCondizionaleSuffisso, visualizzaLinkRegolePerAzioni, servletRegolePerAzioni, listaParametriServletRegolePerAzioni,
					numeroRegolePerAzioni,  condizioneNonIdentificataAbortTransaction,  condizioneNonIdentificataDiagnostico,  condizioneNonIdentificataConnettore,
					connettoreNonTrovatoAbortTransaction, connettoreNonTrovatoDiagnostico, connettoreNonTrovatoConnettore,
					sticky, stickyTipoSelettore, stickyTipoSelettorePattern, stickyMaxAge,
					passiveHealthCheck, passiveHealthCheckExcludeForSeconds
					);

			dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, idAsps, dati);

			pd.setDati(dati);

			pd.setMessage(CostantiControlStation.LABEL_AGGIORNAMENTO_EFFETTUATO_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);

			dati.add(ServletUtils.getDataElementForEditModeFinished());

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);


			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONFIGURAZIONE_CONNETTORI_MULTIPLI, ForwardParams.OTHER(""));

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONFIGURAZIONE_CONNETTORI_MULTIPLI , 
					ForwardParams.OTHER(""));
		} 
	}

}

