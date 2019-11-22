/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaBehaviour;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.behaviour.built_in.BehaviourType;
import org.openspcoop2.pdd.core.behaviour.built_in.load_balance.ConfigurazioneLoadBalancer;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;


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

		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session);
		if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;

		try {
			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, session);

			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			boolean isModalitaCompleta = porteApplicativeHelper.isModalitaCompleta();
			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			String id = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";

			// Prendo il nome della porta
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteApplicativeCore);

			PortaApplicativa portaApplicativa = porteApplicativeCore.getPortaApplicativa(idInt);
			String idporta = portaApplicativa.getNome();

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

			String consegnaCondizionaleS = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONSEGNA_CONDIZIONALE);
			boolean consegnaCondizionale = ServletUtils.isCheckBoxEnabled(consegnaCondizionaleS);

			String connettoreImplementaAPI = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONNETTORE_IMPLEMENTA_API);
			String notificheCondizionaliEsitoS = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CONDIZIONALI_ESITO);
			boolean notificheCondizionaliEsito = ServletUtils.isCheckBoxEnabled(notificheCondizionaliEsitoS);
			String [] esitiTransazione = porteApplicativeHelper.getParameterValues(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_ESITI_TRANSAZIONE);

			boolean accessoDaListaAPS = false;
			String accessoDaAPSParametro = null;
			// nell'erogazione vale sempre
			accessoDaAPSParametro = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS);
			if(Costanti.CHECK_BOX_ENABLED_TRUE.equals(accessoDaAPSParametro)) {
				accessoDaListaAPS = true;
			}

			String idTab = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!porteApplicativeHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
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
			String connettoreAccessoGruppi = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI);
			String connettoreRegistro = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO);
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

			boolean visualizzaLinkProprietaCustom = false;
			boolean modificaStatoAbilitata = true;
			if(portaApplicativa.getBehaviour() != null) {
				BehaviourType behaviourType = BehaviourType.toEnumConstant(portaApplicativa.getBehaviour().getNome());
				visualizzaLinkProprietaCustom = behaviourType.equals(BehaviourType.CUSTOM);

				if(portaApplicativa.sizeServizioApplicativoList() > 1)
					modificaStatoAbilitata = false;
			}

			String postBackElementName = porteApplicativeHelper.getPostBackElementName();
			if(postBackElementName != null ){

			}

			boolean visualizzaGestioneNotifiche = false;

			if(stato!= null && stato.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_ABILITATO)) {
				if(BehaviourType.CONSEGNA_MULTIPLA.getValue().equals(modalitaConsegna)) {
					visualizzaGestioneNotifiche = porteApplicativeHelper.visualizzaSezioneGestioneNotifiche(portaApplicativa, mappingErogazionePortaApplicativa, asps, as, serviceBinding);
				}
			}

			// select list della sezione notifiche
			List<String> connettoriImplementaAPIValues= null;
			List<String> connettoriImplementaAPILabels= null;
			if(visualizzaGestioneNotifiche) {
				connettoriImplementaAPIValues = new ArrayList<>();
				connettoriImplementaAPILabels = new ArrayList<>();
				List<PortaApplicativaServizioApplicativo> servizioApplicativoList = portaApplicativa.getServizioApplicativoList();
				if(servizioApplicativoList != null) {
					for (PortaApplicativaServizioApplicativo paSA : servizioApplicativoList) {
						String nomeConnettorePaSA = porteApplicativeHelper.getLabelNomePortaApplicativaServizioApplicativo(paSA);

						connettoriImplementaAPIValues.add(nomeConnettorePaSA);
						connettoriImplementaAPILabels.add(nomeConnettorePaSA);
					}
				}
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

			if(	porteApplicativeHelper.isEditModeInProgress()){

				if(stato == null) {
					if(portaApplicativa.getBehaviour() == null) {
						stato = PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_DISABILITATO;
						modalitaConsegna = BehaviourType.CONSEGNA_MULTIPLA.getValue();
						consegnaCondizionale = false;
						connettoreImplementaAPI = "";
						notificheCondizionaliEsito = false;
						esitiTransazione = null;
					} else {
						stato = PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_ABILITATO;

						BehaviourType behaviourType = BehaviourType.toEnumConstant(portaApplicativa.getBehaviour().getNome());

						modalitaConsegna = behaviourType.getValue();
						if(behaviourType.equals(BehaviourType.CONSEGNA_LOAD_BALANCE)) {
							loadBalanceStrategia = ConfigurazioneLoadBalancer.readLoadBalancerType(portaApplicativa.getBehaviour());
							consegnaCondizionale = org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.isConfigurazioneCondizionale(portaApplicativa, ControlStationCore.getLog());
						} else if(behaviourType.equals(BehaviourType.CONSEGNA_MULTIPLA)) {
							consegnaCondizionale = org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.isConfigurazioneCondizionale(portaApplicativa, ControlStationCore.getLog());

							if(visualizzaGestioneNotifiche) {
								org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.ConfigurazioneMultiDeliver configurazioneMultiDeliver = org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.MultiDeliverUtils.read(portaApplicativa, ControlStationCore.getLog());
								connettoreImplementaAPI = configurazioneMultiDeliver.getTransazioneSincrona_nomeConnettore() != null ? configurazioneMultiDeliver.getTransazioneSincrona_nomeConnettore() : "";
								notificheCondizionaliEsito = configurazioneMultiDeliver.isNotificheByEsito();
								esitiTransazione = porteApplicativeHelper.getEsitiTransazione(configurazioneMultiDeliver);
							} else {
								connettoreImplementaAPI = "";
								notificheCondizionaliEsito = false;
								esitiTransazione = null;
							}
						}
						else if(behaviourType.equals(BehaviourType.CUSTOM)) {
							visualizzaLinkProprietaCustom = true;
							tipoCustom = portaApplicativa.getBehaviour().getNome();
						}
					}
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null,idAsps, dati);

				dati = porteApplicativeHelper.addConnettoriMultipliConfigurazioneToDati(dati, TipoOperazione.OTHER, accessoDaAPSParametro, stato, modalitaConsegna, tipoCustom, 
						numeroProprietaCustom, servletProprietaCustom, listaParametriServletProprietaCustom, visualizzaLinkProprietaCustom, loadBalanceStrategia, modificaStatoAbilitata, 
						consegnaCondizionale, visualizzaGestioneNotifiche, connettoreImplementaAPI, connettoriImplementaAPIValues, connettoriImplementaAPILabels, notificheCondizionaliEsito, esitiTransazione

						);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				// Forward control to the specified success URI
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONFIGURAZIONE_CONNETTORI_MULTIPLI, 
						ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.connettoriMultipliConfigurazioneCheckData(TipoOperazione.OTHER, stato, modalitaConsegna, tipoCustom, loadBalanceStrategia, visualizzaGestioneNotifiche);

			if(!isOk) {
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addConnettoriMultipliConfigurazioneToDati(dati, TipoOperazione.OTHER, accessoDaAPSParametro, stato, modalitaConsegna, tipoCustom, 
						numeroProprietaCustom, servletProprietaCustom, listaParametriServletProprietaCustom, visualizzaLinkProprietaCustom, loadBalanceStrategia, modificaStatoAbilitata,
						consegnaCondizionale, visualizzaGestioneNotifiche, connettoreImplementaAPI, connettoriImplementaAPIValues, connettoriImplementaAPILabels, notificheCondizionaliEsito, esitiTransazione

						);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null,idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONFIGURAZIONE_CONNETTORI_MULTIPLI,
						ForwardParams.OTHER(""));
			}

			if(stato.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_ABILITATO)) {
				PortaApplicativaBehaviour behaviour = new PortaApplicativaBehaviour();

				BehaviourType behaviourType = BehaviourType.toEnumConstant(modalitaConsegna);

				switch (behaviourType) {
				case CONSEGNA_LOAD_BALANCE:	{
					behaviour.setNome(modalitaConsegna);
					ConfigurazioneLoadBalancer.addLoadBalancerType(behaviour, loadBalanceStrategia);
					
					// configurazione multideliver
					org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.ConfigurazioneMultiDeliver configurazioneMultiDeliver = porteApplicativeHelper.toConfigurazioneMultiDeliver(connettoreImplementaAPI, notificheCondizionaliEsito, esitiTransazione);
					org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.MultiDeliverUtils.save(portaApplicativa, configurazioneMultiDeliver);
					
					// configurazione condizionale
				} 
				break;
				case CONSEGNA_MULTIPLA: {
					behaviour.setNome(modalitaConsegna);
					
					// configurazione multideliver
					org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.ConfigurazioneMultiDeliver configurazioneMultiDeliver = porteApplicativeHelper.toConfigurazioneMultiDeliver(connettoreImplementaAPI, notificheCondizionaliEsito, esitiTransazione);
					org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.MultiDeliverUtils.save(portaApplicativa, configurazioneMultiDeliver);
					
					// configurazione condizionale
				}
				break;
				case CUSTOM:
					behaviour.setNome(tipoCustom);
					break;
				}

				portaApplicativa.setBehaviour(behaviour);
			} else {
				portaApplicativa.setBehaviour(null);
			}

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), portaApplicativa);

			// rileggo la configurazione
			portaApplicativa = porteApplicativeCore.getPortaApplicativa(idInt);

			visualizzaLinkProprietaCustom = false;

			if(portaApplicativa.getBehaviour() == null) {
				stato = PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_DISABILITATO;
				modalitaConsegna = BehaviourType.CONSEGNA_MULTIPLA.getValue();
				consegnaCondizionale = false;
				connettoreImplementaAPI = "";
				notificheCondizionaliEsito = false;
				esitiTransazione = null;
			} else {
				stato = PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_ABILITATO;

				BehaviourType behaviourType = BehaviourType.toEnumConstant(portaApplicativa.getBehaviour().getNome());

				modalitaConsegna = behaviourType.getValue();
				if(behaviourType.equals(BehaviourType.CONSEGNA_LOAD_BALANCE)) {
					loadBalanceStrategia = ConfigurazioneLoadBalancer.readLoadBalancerType(portaApplicativa.getBehaviour());
					consegnaCondizionale = org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.isConfigurazioneCondizionale(portaApplicativa, ControlStationCore.getLog());
				} else if(behaviourType.equals(BehaviourType.CONSEGNA_MULTIPLA)) {
					consegnaCondizionale = org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.isConfigurazioneCondizionale(portaApplicativa, ControlStationCore.getLog());

					if(visualizzaGestioneNotifiche) {
						org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.ConfigurazioneMultiDeliver configurazioneMultiDeliver = org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.MultiDeliverUtils.read(portaApplicativa, ControlStationCore.getLog());
						connettoreImplementaAPI = configurazioneMultiDeliver.getTransazioneSincrona_nomeConnettore() != null ? configurazioneMultiDeliver.getTransazioneSincrona_nomeConnettore() : "";
						notificheCondizionaliEsito = configurazioneMultiDeliver.isNotificheByEsito();
						esitiTransazione = porteApplicativeHelper.getEsitiTransazione(configurazioneMultiDeliver);
					} else {
						connettoreImplementaAPI = "";
						notificheCondizionaliEsito = false;
						esitiTransazione = null;
					}
				}
				else if(behaviourType.equals(BehaviourType.CUSTOM)) {
					visualizzaLinkProprietaCustom = true;
					tipoCustom = portaApplicativa.getBehaviour().getNome();
				}
			}

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			dati = porteApplicativeHelper.addConnettoriMultipliConfigurazioneToDati(dati, TipoOperazione.OTHER, accessoDaAPSParametro, stato, modalitaConsegna, tipoCustom, 
					numeroProprietaCustom, servletProprietaCustom, listaParametriServletProprietaCustom, visualizzaLinkProprietaCustom,loadBalanceStrategia, modificaStatoAbilitata,
					consegnaCondizionale, visualizzaGestioneNotifiche, connettoreImplementaAPI, connettoriImplementaAPIValues, connettoriImplementaAPILabels, notificheCondizionaliEsito, esitiTransazione

					);

			dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, idAsps, dati);

			pd.setDati(dati);

			pd.setMessage(CostantiControlStation.LABEL_AGGIORNAMENTO_EFFETTUATO_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			//pd.disableEditMode();
			dati.addElement(ServletUtils.getDataElementForEditModeFinished());

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);


			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONFIGURAZIONE_CONNETTORI_MULTIPLI, ForwardParams.OTHER(""));

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONFIGURAZIONE_CONNETTORI_MULTIPLI , 
					ForwardParams.OTHER(""));
		} 
	}

}

