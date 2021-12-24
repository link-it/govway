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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativoConnettore;
import org.openspcoop2.core.config.constants.TipoBehaviour;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.ConfigurazioneGestioneConsegnaNotifiche;
import org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaFault;
import org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.TipoGestioneNotificaTrasporto;
import org.openspcoop2.pdd.core.jmx.JMXUtils;
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
 * PorteApplicativeConnettoriMultipliConfigLoadBalance
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteApplicativeConnettoriMultipliConfigProprietaNotifiche extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {


		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

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

			String idConnTab = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_ID_CONN_TAB);
			if(StringUtils.isNotEmpty(idConnTab)) {
				ServletUtils.setObjectIntoSession(session, idConnTab, CostantiControlStation.PARAMETRO_ID_CONN_TAB);
			}

			String nomeSAConnettore = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOME_SA);
			String coda = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODA);
			String priorita = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_PRIORITA);
			String prioritaMax = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_PRIORITA_MAX);
			String cadenzaRispedizione = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CADENZA_RISPEDIZIONE);
			String codiceRisposta2xx = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_2XX); 
			String codiceRisposta2xxValueMin = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_2XX_VALUE_MIN); 
			String codiceRisposta2xxValueMax = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_2XX_VALUE_MAX); 
			String codiceRisposta2xxValue = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_2XX_VALUE);
			String codiceRisposta3xx = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_3XX); 
			String codiceRisposta3xxValueMin = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_3XX_VALUE_MIN); 
			String codiceRisposta3xxValueMax = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_3XX_VALUE_MAX); 
			String codiceRisposta3xxValue = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_3XX_VALUE);
			String codiceRisposta4xx = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_4XX); 
			String codiceRisposta4xxValueMin = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_4XX_VALUE_MIN); 
			String codiceRisposta4xxValueMax = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_4XX_VALUE_MAX); 
			String codiceRisposta4xxValue = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_4XX_VALUE);
			String codiceRisposta5xx = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_5XX); 
			String codiceRisposta5xxValueMin = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_5XX_VALUE_MIN); 
			String codiceRisposta5xxValueMax = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_5XX_VALUE_MAX); 
			String codiceRisposta5xxValue = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_5XX_VALUE);
			String gestioneFault = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_GESTIONE); 
			String faultCode = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODE); 
			String faultActor = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_ACTOR); 
			String faultMessage = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_MESSAGE); 

			boolean accessoDaListaAPS = false;
			String accessoDaAPSParametro = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS);
			if(Costanti.CHECK_BOX_ENABLED_TRUE.equals(accessoDaAPSParametro)) {
				accessoDaListaAPS = true;
			}

			boolean isModalitaCompleta = porteApplicativeHelper.isModalitaCompleta();
			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, session);

			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// Prendo nome, tipo e pdd del soggetto
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteApplicativeCore);

			// Prendo nome della porta applicativa
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String idporta = pa.getNome();
			TipoBehaviour beaBehaviourType = TipoBehaviour.toEnumConstant(pa.getBehaviour().getNome());
			long idAspsLong = Long.parseLong(idAsps);
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idAspsLong);
			AccordoServizioParteComuneSintetico as = null;
			if(porteApplicativeCore.isRegistroServiziLocale()){
				int idAcc = asps.getIdAccordo().intValue();
				as = apcCore.getAccordoServizioSintetico(idAcc);
			}
			else{
				as = apcCore.getAccordoServizioSintetico(IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
			}
			
			ServiceBinding serviceBinding = porteApplicativeCore.toMessageServiceBinding(as.getServiceBinding());

			// valora iniziale della configurazione
			PortaApplicativaServizioApplicativo oldPaSA = null;
			boolean connettoreDefault = false;
			TipoBehaviour behaviourType = null;
			if(pa.getBehaviour()!=null) {
				behaviourType = TipoBehaviour.toEnumConstant(pa.getBehaviour().getNome());
			}
			for (PortaApplicativaServizioApplicativo paSATmp : pa.getServizioApplicativoList()) {
				if(paSATmp.getNome().equals(nomeSAConnettore)) {
					oldPaSA = paSATmp;
					connettoreDefault = porteApplicativeHelper.isConnettoreDefault(oldPaSA);
				}
			}
			boolean consegnaSincrona = connettoreDefault && TipoBehaviour.CONSEGNA_CON_NOTIFICHE.equals(behaviourType);
			
			ConfigurazioneGestioneConsegnaNotifiche oldConfigurazioneGestioneConsegnaNotifiche =
					org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.MultiDeliverUtils.read(oldPaSA, ControlStationCore.getLog());
			
			
			String postBackElementName = porteApplicativeHelper.getPostBackElementName();
			if(postBackElementName != null ){
				if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_2XX)) {
					codiceRisposta2xxValueMin = "";
					codiceRisposta2xxValueMax = "";
					codiceRisposta2xxValue = "";
				}
				
				if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_3XX)) {
					codiceRisposta3xxValueMin = "";
					codiceRisposta3xxValueMax = "";
					codiceRisposta3xxValue = "";
				}
				
				if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_4XX)) {
					codiceRisposta4xxValueMin = "";
					codiceRisposta4xxValueMax = "";
					codiceRisposta4xxValue = "";
				}
				
				if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CODICE_RISPOSTA_HTTP_5XX)) {
					codiceRisposta5xxValueMin = "";
					codiceRisposta5xxValueMax = "";
					codiceRisposta5xxValue = "";
				}
				
				if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_GESTIONE)) {
					faultCode = "";
					faultActor = "";
					faultMessage = "";
				}
			}
			

			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				if(accessoDaListaAPS) {
					if(!isModalitaCompleta) {
						if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
							labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI;
						} else {
							labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_DI+
									porteApplicativeHelper.getLabelIdServizio(asps);
						}
					}
					else {
						labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI;
					}
				}
				else {
					labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_DI,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI,
							pa);
				}
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_DI+idporta;
			}

			if(accessoDaListaAPS) {
				lstParam.remove(lstParam.size()-1);
			}

			List<Parameter> listParametersConfigutazioneConnettoriMultipli = new ArrayList<>();
			Parameter pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta);
			Parameter pNomePorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, pa.getNome());
			Parameter pIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps);
			String idTabP = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			Parameter pIdTab = new Parameter(CostantiControlStation.PARAMETRO_ID_TAB, idTabP != null ? idTabP : "");
			Parameter pAccessoDaAPS = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS, accessoDaAPSParametro != null ? accessoDaAPSParametro : "");
			String connettoreAccessoGruppi = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI);
			String connettoreRegistro = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO);
			Parameter pConnettoreAccessoDaGruppi = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI, connettoreAccessoGruppi);
			Parameter pConnettoreAccesso = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO, connettoreRegistro);
			String connettoreAccessoListaConnettori = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_LISTA_CONNETTORI_MULTIPLI);

			listParametersConfigutazioneConnettoriMultipli.add(pIdSogg);
			listParametersConfigutazioneConnettoriMultipli.add(pIdPorta);
			listParametersConfigutazioneConnettoriMultipli.add(pNomePorta);
			listParametersConfigutazioneConnettoriMultipli.add(pIdAsps);
			listParametersConfigutazioneConnettoriMultipli.add(pIdTab);
			listParametersConfigutazioneConnettoriMultipli.add(pAccessoDaAPS);
			listParametersConfigutazioneConnettoriMultipli.add(pConnettoreAccessoDaGruppi);
			listParametersConfigutazioneConnettoriMultipli.add(pConnettoreAccesso);

			lstParam.add(new Parameter(labelPerPorta,  PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_LIST, listParametersConfigutazioneConnettoriMultipli.toArray(new Parameter[1])));

			String labelDi = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_GESTIONE_NOTIFICHE_DI;
			String label = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_GESTIONE_NOTIFICHE;
			if(consegnaSincrona) {
				labelDi = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_GESTIONE_CONSEGNA_DI;
				label = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_GESTIONE_CONSEGNA;
			}
			
			// Label diversa in base all'operazione
			String labelPagina = porteApplicativeHelper.getLabelNomePortaApplicativaServizioApplicativo(
					labelDi,
					label,
					oldPaSA);

			lstParam.add(new Parameter(labelPagina, null));

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParam);

			// Se nome = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (porteApplicativeHelper.isEditModeInProgress()) {

				if(coda==null) {
					if(oldPaSA!=null && oldPaSA.getDatiConnettore()!=null) {
						coda = oldPaSA.getDatiConnettore().getCoda();
					}
					if(coda==null) {
						coda = CostantiConfigurazione.CODA_DEFAULT;
					}
				}
				if(priorita==null) {
					if(oldPaSA!=null && oldPaSA.getDatiConnettore()!=null) {
						priorita = oldPaSA.getDatiConnettore().getPriorita();
						prioritaMax = oldPaSA.getDatiConnettore().isPrioritaMax() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
					}
					if(priorita==null) {
						priorita = CostantiConfigurazione.PRIORITA_DEFAULT;
					}
				}
				
				if(cadenzaRispedizione == null) {
					if(oldConfigurazioneGestioneConsegnaNotifiche != null) {
						// cadenza rispedizione
						cadenzaRispedizione = oldConfigurazioneGestioneConsegnaNotifiche.getCadenzaRispedizione() != null ? oldConfigurazioneGestioneConsegnaNotifiche.getCadenzaRispedizione()+ "" : "";
						
						TipoGestioneNotificaTrasporto gestioneTrasporto2xxE = oldConfigurazioneGestioneConsegnaNotifiche.getGestioneTrasporto2xx();
						if(gestioneTrasporto2xxE != null) {
							codiceRisposta2xx = gestioneTrasporto2xxE.getValue();
							codiceRisposta2xxValueMin = "";
							codiceRisposta2xxValueMax = "";
							codiceRisposta2xxValue = "";

							switch(gestioneTrasporto2xxE) {
							case CONSEGNA_COMPLETATA:
							case CONSEGNA_FALLITA:
								break;
							case CODICI_CONSEGNA_COMPLETATA:
								StringBuilder sb = new StringBuilder();
								for(Integer x : oldConfigurazioneGestioneConsegnaNotifiche.getGestioneTrasporto2xx_codes()) {
									if(sb.length() > 0) {
										sb.append(",");
									}
									sb.append(x+ "");
								}
								codiceRisposta2xxValue = sb.toString();
								break;
							case INTERVALLO_CONSEGNA_COMPLETATA:
								codiceRisposta2xxValueMin = oldConfigurazioneGestioneConsegnaNotifiche.getGestioneTrasporto2xx_leftInterval() + "";
								codiceRisposta2xxValueMax = oldConfigurazioneGestioneConsegnaNotifiche.getGestioneTrasporto2xx_rightInterval() + "";
								break;
							}
						} else {
							codiceRisposta2xx = TipoGestioneNotificaTrasporto.CONSEGNA_COMPLETATA.getValue();
							codiceRisposta2xxValueMin = "";
							codiceRisposta2xxValueMax = "";
							codiceRisposta2xxValue = "";
						}
						
						TipoGestioneNotificaTrasporto gestioneTrasporto3xxE = oldConfigurazioneGestioneConsegnaNotifiche.getGestioneTrasporto3xx();
						if(gestioneTrasporto3xxE != null) {
							codiceRisposta3xx = gestioneTrasporto3xxE.getValue();
							codiceRisposta3xxValueMin = "";
							codiceRisposta3xxValueMax = "";
							codiceRisposta3xxValue = "";

							switch(gestioneTrasporto3xxE) {
							case CONSEGNA_COMPLETATA:
							case CONSEGNA_FALLITA:
								break;
							case CODICI_CONSEGNA_COMPLETATA:
								StringBuilder sb = new StringBuilder();
								for(Integer x : oldConfigurazioneGestioneConsegnaNotifiche.getGestioneTrasporto3xx_codes()) {
									if(sb.length() > 0) {
										sb.append(",");
									}
									sb.append(x+ "");
								}
								codiceRisposta3xxValue = sb.toString();
								break;
							case INTERVALLO_CONSEGNA_COMPLETATA:
								codiceRisposta3xxValueMin = oldConfigurazioneGestioneConsegnaNotifiche.getGestioneTrasporto3xx_leftInterval() + "";
								codiceRisposta3xxValueMax = oldConfigurazioneGestioneConsegnaNotifiche.getGestioneTrasporto3xx_rightInterval() + "";
								break;
							}
						} else {
							codiceRisposta3xx = TipoGestioneNotificaTrasporto.CONSEGNA_COMPLETATA.getValue();
							codiceRisposta3xxValueMin = "";
							codiceRisposta3xxValueMax = "";
							codiceRisposta3xxValue = "";
						}
						
						TipoGestioneNotificaTrasporto gestioneTrasporto4xxE = oldConfigurazioneGestioneConsegnaNotifiche.getGestioneTrasporto4xx();
						if(gestioneTrasporto4xxE != null) {
							codiceRisposta4xx = gestioneTrasporto4xxE.getValue();
							codiceRisposta4xxValueMin = "";
							codiceRisposta4xxValueMax = "";
							codiceRisposta4xxValue = "";

							switch(gestioneTrasporto4xxE) {
							case CONSEGNA_COMPLETATA:
							case CONSEGNA_FALLITA:
								break;
							case CODICI_CONSEGNA_COMPLETATA:
								StringBuilder sb = new StringBuilder();
								for(Integer x : oldConfigurazioneGestioneConsegnaNotifiche.getGestioneTrasporto4xx_codes()) {
									if(sb.length() > 0) {
										sb.append(",");
									}
									sb.append(x+ "");
								}
								codiceRisposta4xxValue = sb.toString();
								break;
							case INTERVALLO_CONSEGNA_COMPLETATA:
								codiceRisposta4xxValueMin = oldConfigurazioneGestioneConsegnaNotifiche.getGestioneTrasporto4xx_leftInterval() + "";
								codiceRisposta4xxValueMax = oldConfigurazioneGestioneConsegnaNotifiche.getGestioneTrasporto4xx_rightInterval() + "";
								break;
							}
						} else {
							codiceRisposta4xx = TipoGestioneNotificaTrasporto.CONSEGNA_COMPLETATA.getValue();
							codiceRisposta4xxValueMin = "";
							codiceRisposta4xxValueMax = "";
							codiceRisposta4xxValue = "";
						}
						
						TipoGestioneNotificaTrasporto gestioneTrasporto5xxE = oldConfigurazioneGestioneConsegnaNotifiche.getGestioneTrasporto5xx();
						if(gestioneTrasporto5xxE != null) {
							codiceRisposta5xx = gestioneTrasporto5xxE.getValue();
							codiceRisposta5xxValueMin = "";
							codiceRisposta5xxValueMax = "";
							codiceRisposta5xxValue = "";

							switch(gestioneTrasporto5xxE) {
							case CONSEGNA_COMPLETATA:
							case CONSEGNA_FALLITA:
								break;
							case CODICI_CONSEGNA_COMPLETATA:
								StringBuilder sb = new StringBuilder();
								for(Integer x : oldConfigurazioneGestioneConsegnaNotifiche.getGestioneTrasporto5xx_codes()) {
									if(sb.length() > 0) {
										sb.append(",");
									}
									sb.append(x+ "");
								}
								codiceRisposta5xxValue = sb.toString();
								break;
							case INTERVALLO_CONSEGNA_COMPLETATA:
								codiceRisposta5xxValueMin = oldConfigurazioneGestioneConsegnaNotifiche.getGestioneTrasporto5xx_leftInterval() + "";
								codiceRisposta5xxValueMax = oldConfigurazioneGestioneConsegnaNotifiche.getGestioneTrasporto5xx_rightInterval() + "";
								break;
							}
						} else {
							codiceRisposta5xx = TipoGestioneNotificaTrasporto.CONSEGNA_COMPLETATA.getValue();
							codiceRisposta5xxValueMin = "";
							codiceRisposta5xxValueMax = "";
							codiceRisposta5xxValue = "";
						}
						
						if(oldConfigurazioneGestioneConsegnaNotifiche.getFault() != null) {
							gestioneFault = oldConfigurazioneGestioneConsegnaNotifiche.getFault().getValue();
						} else {
							gestioneFault = TipoGestioneNotificaFault.CONSEGNA_COMPLETATA.getValue();
						}
						
						faultCode = oldConfigurazioneGestioneConsegnaNotifiche.getFaultCode() != null ? oldConfigurazioneGestioneConsegnaNotifiche.getFaultCode() : "";
						faultActor = oldConfigurazioneGestioneConsegnaNotifiche.getFaultActor() != null ? oldConfigurazioneGestioneConsegnaNotifiche.getFaultActor() : "";
						faultMessage = oldConfigurazioneGestioneConsegnaNotifiche.getFaultMessage() != null ? oldConfigurazioneGestioneConsegnaNotifiche.getFaultMessage() : "";
						
					} else {
						cadenzaRispedizione = "";
						codiceRisposta2xx = TipoGestioneNotificaTrasporto.CONSEGNA_COMPLETATA.getValue();
						codiceRisposta2xxValueMin = "";
						codiceRisposta2xxValueMax = "";
						codiceRisposta2xxValue = "";
						codiceRisposta3xx = TipoGestioneNotificaTrasporto.CONSEGNA_COMPLETATA.getValue();
						codiceRisposta3xxValueMin = "";
						codiceRisposta3xxValueMax = "";
						codiceRisposta3xxValue = "";
						codiceRisposta4xx = TipoGestioneNotificaTrasporto.CONSEGNA_COMPLETATA.getValue();
						codiceRisposta4xxValueMin = "";
						codiceRisposta4xxValueMax = "";
						codiceRisposta4xxValue = "";
						codiceRisposta5xx = TipoGestioneNotificaTrasporto.CONSEGNA_COMPLETATA.getValue();
						codiceRisposta5xxValueMin = "";
						codiceRisposta5xxValueMax = "";
						codiceRisposta5xxValue = "";
						gestioneFault = TipoGestioneNotificaFault.CONSEGNA_COMPLETATA.getValue();
						faultCode = "";
						faultActor = "";
						faultMessage = "";
					}
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addConnettoriMultipliNotificheToDati(dati, TipoOperazione.OTHER, beaBehaviourType, nomeSAConnettore, serviceBinding, cadenzaRispedizione, 
						codiceRisposta2xx, codiceRisposta2xxValueMin, codiceRisposta2xxValueMax, codiceRisposta2xxValue, 
						codiceRisposta3xx, codiceRisposta3xxValueMin, codiceRisposta3xxValueMax, codiceRisposta3xxValue, 
						codiceRisposta4xx, codiceRisposta4xxValueMin, codiceRisposta4xxValueMax, codiceRisposta4xxValue, 
						codiceRisposta5xx, codiceRisposta5xxValueMin, codiceRisposta5xxValueMax, codiceRisposta5xxValue, 
						gestioneFault, faultCode, faultActor, faultMessage,
						consegnaSincrona,
						coda, priorita, prioritaMax);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, idPorta, idsogg, idPorta,idAsps, dati);

				dati = porteApplicativeHelper.addInformazioniGruppiAsHiddenToDati(TipoOperazione.OTHER, dati, idTabP, null, accessoDaAPSParametro != null ? accessoDaAPSParametro : "", 
						connettoreAccessoGruppi, connettoreRegistro, connettoreAccessoListaConnettori);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_PROPRIETA_NOTIFICHE,
						ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.connettoriMultipliNotificheCheckData(TipoOperazione.OTHER, pa, beaBehaviourType, serviceBinding, nomeSAConnettore, cadenzaRispedizione,
					codiceRisposta2xx, codiceRisposta2xxValueMin, codiceRisposta2xxValueMax, codiceRisposta2xxValue, 
					codiceRisposta3xx, codiceRisposta3xxValueMin, codiceRisposta3xxValueMax, codiceRisposta3xxValue, 
					codiceRisposta4xx, codiceRisposta4xxValueMin, codiceRisposta4xxValueMax, codiceRisposta4xxValue, 
					codiceRisposta5xx, codiceRisposta5xxValueMin, codiceRisposta5xxValueMax, codiceRisposta5xxValue, 
					gestioneFault, faultCode, faultActor, faultMessage);

			if (!isOk) {

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addConnettoriMultipliNotificheToDati(dati, TipoOperazione.OTHER, beaBehaviourType, nomeSAConnettore, serviceBinding, cadenzaRispedizione, 
						codiceRisposta2xx, codiceRisposta2xxValueMin, codiceRisposta2xxValueMax, codiceRisposta2xxValue, 
						codiceRisposta3xx, codiceRisposta3xxValueMin, codiceRisposta3xxValueMax, codiceRisposta3xxValue, 
						codiceRisposta4xx, codiceRisposta4xxValueMin, codiceRisposta4xxValueMax, codiceRisposta4xxValue, 
						codiceRisposta5xx, codiceRisposta5xxValueMin, codiceRisposta5xxValueMax, codiceRisposta5xxValue, 
						gestioneFault, faultCode, faultActor, faultMessage,
						consegnaSincrona,
						coda, priorita, prioritaMax);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, idPorta, idsogg, idPorta, idAsps, dati);

				dati = porteApplicativeHelper.addInformazioniGruppiAsHiddenToDati(TipoOperazione.OTHER, dati, idTabP, null, accessoDaAPSParametro != null ? accessoDaAPSParametro : "", 
						connettoreAccessoGruppi, connettoreRegistro, connettoreAccessoListaConnettori);	

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_PROPRIETA_NOTIFICHE, 
						ForwardParams.OTHER(""));
			}

			// valora iniziale della configurazione
			PortaApplicativaServizioApplicativo paSA = null;
			for (PortaApplicativaServizioApplicativo paSATmp : pa.getServizioApplicativoList()) {
				if(paSATmp.getNome().equals(nomeSAConnettore)) {
					paSA = paSATmp;					
				}
			}
			
			PortaApplicativaServizioApplicativoConnettore datiConnettore = paSA.getDatiConnettore();
			
			boolean oldValuePrioritaMax = false;
			String oldCoda = null;
			
			if(datiConnettore == null) { // succede solo se e' la prima volta che modifico la configurazione di default
				datiConnettore = new PortaApplicativaServizioApplicativoConnettore();
				datiConnettore.setNome(CostantiConfigurazione.NOME_CONNETTORE_DEFAULT);
			}
			else {
				oldValuePrioritaMax = datiConnettore.isPrioritaMax();
				oldCoda = datiConnettore.getCoda();
			}
			
			datiConnettore.setCoda(coda);
			datiConnettore.setPriorita(priorita);
			datiConnettore.setPrioritaMax(ServletUtils.isCheckBoxEnabled(prioritaMax));
			
			paSA.setDatiConnettore(datiConnettore);
			
			ConfigurazioneGestioneConsegnaNotifiche nuovaConfigurazioneGestioneConsegnaNotifiche  = porteApplicativeHelper.getConfigurazioneGestioneConsegnaNotifiche(serviceBinding, cadenzaRispedizione,
					codiceRisposta2xx, codiceRisposta2xxValueMin, codiceRisposta2xxValueMax, codiceRisposta2xxValue,
					codiceRisposta3xx, codiceRisposta3xxValueMin, codiceRisposta3xxValueMax, codiceRisposta3xxValue,
					codiceRisposta4xx, codiceRisposta4xxValueMin, codiceRisposta4xxValueMax, codiceRisposta4xxValue, 
					codiceRisposta5xx, codiceRisposta5xxValueMin, codiceRisposta5xxValueMax, codiceRisposta5xxValue,
					gestioneFault, faultCode, faultActor, faultMessage);
			
			org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.MultiDeliverUtils.save(paSA, nuovaConfigurazioneGestioneConsegnaNotifiche);

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);

			boolean resetCoda = false;
			if(oldValuePrioritaMax != datiConnettore.isPrioritaMax()) {
				resetConnettoriPrioritari(coda, porteApplicativeCore);
				resetCoda = true;
			}
			if(!coda.equals(oldCoda)) {
				resetConnettoriPrioritari(oldCoda, porteApplicativeCore);
				if(!resetCoda) {
					resetConnettoriPrioritari(coda, porteApplicativeCore);
				}
			}
			
			// ricarico la configurazione
			pa = porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));

			paSA = null;
			for (PortaApplicativaServizioApplicativo paSATmp : pa.getServizioApplicativoList()) {
				if(paSATmp.getNome().equals(nomeSAConnettore)) {
					paSA = paSATmp;					
				}
			}
			
			ConfigurazioneGestioneConsegnaNotifiche configurazioneGestioneConsegnaNotifiche =
					org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.MultiDeliverUtils.read(paSA, ControlStationCore.getLog());

			if(configurazioneGestioneConsegnaNotifiche != null) {
				// cadenza rispedizione
				cadenzaRispedizione = configurazioneGestioneConsegnaNotifiche.getCadenzaRispedizione() != null ? configurazioneGestioneConsegnaNotifiche.getCadenzaRispedizione()+ "" : "";
				
				TipoGestioneNotificaTrasporto gestioneTrasporto2xxE = configurazioneGestioneConsegnaNotifiche.getGestioneTrasporto2xx();
				if(gestioneTrasporto2xxE != null) {
					codiceRisposta2xx = gestioneTrasporto2xxE.getValue();
					codiceRisposta2xxValueMin = "";
					codiceRisposta2xxValueMax = "";
					codiceRisposta2xxValue = "";

					switch(gestioneTrasporto2xxE) {
					case CONSEGNA_COMPLETATA:
					case CONSEGNA_FALLITA:
						break;
					case CODICI_CONSEGNA_COMPLETATA:
						StringBuilder sb = new StringBuilder();
						for(Integer x : configurazioneGestioneConsegnaNotifiche.getGestioneTrasporto2xx_codes()) {
							if(sb.length() > 0) {
								sb.append(",");
							}
							sb.append(x+ "");
						}
						codiceRisposta2xxValue = sb.toString();
						break;
					case INTERVALLO_CONSEGNA_COMPLETATA:
						codiceRisposta2xxValueMin = configurazioneGestioneConsegnaNotifiche.getGestioneTrasporto2xx_leftInterval() + "";
						codiceRisposta2xxValueMax = configurazioneGestioneConsegnaNotifiche.getGestioneTrasporto2xx_rightInterval() + "";
						break;
					}
				} else {
					codiceRisposta2xx = TipoGestioneNotificaTrasporto.CONSEGNA_COMPLETATA.getValue();
					codiceRisposta2xxValueMin = "";
					codiceRisposta2xxValueMax = "";
					codiceRisposta2xxValue = "";
				}
				
				TipoGestioneNotificaTrasporto gestioneTrasporto3xxE = configurazioneGestioneConsegnaNotifiche.getGestioneTrasporto3xx();
				if(gestioneTrasporto3xxE != null) {
					codiceRisposta3xx = gestioneTrasporto3xxE.getValue();
					codiceRisposta3xxValueMin = "";
					codiceRisposta3xxValueMax = "";
					codiceRisposta3xxValue = "";

					switch(gestioneTrasporto3xxE) {
					case CONSEGNA_COMPLETATA:
					case CONSEGNA_FALLITA:
						break;
					case CODICI_CONSEGNA_COMPLETATA:
						StringBuilder sb = new StringBuilder();
						for(Integer x : configurazioneGestioneConsegnaNotifiche.getGestioneTrasporto3xx_codes()) {
							if(sb.length() > 0) {
								sb.append(",");
							}
							sb.append(x+ "");
						}
						codiceRisposta3xxValue = sb.toString();
						break;
					case INTERVALLO_CONSEGNA_COMPLETATA:
						codiceRisposta3xxValueMin = configurazioneGestioneConsegnaNotifiche.getGestioneTrasporto3xx_leftInterval() + "";
						codiceRisposta3xxValueMax = configurazioneGestioneConsegnaNotifiche.getGestioneTrasporto3xx_rightInterval() + "";
						break;
					}
				} else {
					codiceRisposta3xx = TipoGestioneNotificaTrasporto.CONSEGNA_COMPLETATA.getValue();
					codiceRisposta3xxValueMin = "";
					codiceRisposta3xxValueMax = "";
					codiceRisposta3xxValue = "";
				}
				
				TipoGestioneNotificaTrasporto gestioneTrasporto4xxE = configurazioneGestioneConsegnaNotifiche.getGestioneTrasporto4xx();
				if(gestioneTrasporto4xxE != null) {
					codiceRisposta4xx = gestioneTrasporto4xxE.getValue();
					codiceRisposta4xxValueMin = "";
					codiceRisposta4xxValueMax = "";
					codiceRisposta4xxValue = "";

					switch(gestioneTrasporto4xxE) {
					case CONSEGNA_COMPLETATA:
					case CONSEGNA_FALLITA:
						break;
					case CODICI_CONSEGNA_COMPLETATA:
						StringBuilder sb = new StringBuilder();
						for(Integer x : configurazioneGestioneConsegnaNotifiche.getGestioneTrasporto4xx_codes()) {
							if(sb.length() > 0) {
								sb.append(",");
							}
							sb.append(x+ "");
						}
						codiceRisposta4xxValue = sb.toString();
						break;
					case INTERVALLO_CONSEGNA_COMPLETATA:
						codiceRisposta4xxValueMin = configurazioneGestioneConsegnaNotifiche.getGestioneTrasporto4xx_leftInterval() + "";
						codiceRisposta4xxValueMax = configurazioneGestioneConsegnaNotifiche.getGestioneTrasporto4xx_rightInterval() + "";
						break;
					}
				} else {
					codiceRisposta4xx = TipoGestioneNotificaTrasporto.CONSEGNA_COMPLETATA.getValue();
					codiceRisposta4xxValueMin = "";
					codiceRisposta4xxValueMax = "";
					codiceRisposta4xxValue = "";
				}
				
				TipoGestioneNotificaTrasporto gestioneTrasporto5xxE = configurazioneGestioneConsegnaNotifiche.getGestioneTrasporto5xx();
				if(gestioneTrasporto5xxE != null) {
					codiceRisposta5xx = gestioneTrasporto5xxE.getValue();
					codiceRisposta5xxValueMin = "";
					codiceRisposta5xxValueMax = "";
					codiceRisposta5xxValue = "";

					switch(gestioneTrasporto5xxE) {
					case CONSEGNA_COMPLETATA:
					case CONSEGNA_FALLITA:
						break;
					case CODICI_CONSEGNA_COMPLETATA:
						StringBuilder sb = new StringBuilder();
						for(Integer x : configurazioneGestioneConsegnaNotifiche.getGestioneTrasporto5xx_codes()) {
							if(sb.length() > 0) {
								sb.append(",");
							}
							sb.append(x+ "");
						}
						codiceRisposta5xxValue = sb.toString();
						break;
					case INTERVALLO_CONSEGNA_COMPLETATA:
						codiceRisposta5xxValueMin = configurazioneGestioneConsegnaNotifiche.getGestioneTrasporto5xx_leftInterval() + "";
						codiceRisposta5xxValueMax = configurazioneGestioneConsegnaNotifiche.getGestioneTrasporto5xx_rightInterval() + "";
						break;
					}
				} else {
					codiceRisposta5xx = TipoGestioneNotificaTrasporto.CONSEGNA_COMPLETATA.getValue();
					codiceRisposta5xxValueMin = "";
					codiceRisposta5xxValueMax = "";
					codiceRisposta5xxValue = "";
				}
				
				if(configurazioneGestioneConsegnaNotifiche.getFault() != null) {
					gestioneFault = configurazioneGestioneConsegnaNotifiche.getFault().getValue();
				} else {
					gestioneFault = TipoGestioneNotificaFault.CONSEGNA_COMPLETATA.getValue();
				}
				
				faultCode = configurazioneGestioneConsegnaNotifiche.getFaultCode() != null ? configurazioneGestioneConsegnaNotifiche.getFaultCode() : "";
				faultActor = configurazioneGestioneConsegnaNotifiche.getFaultActor() != null ? configurazioneGestioneConsegnaNotifiche.getFaultActor() : "";
				faultMessage = configurazioneGestioneConsegnaNotifiche.getFaultMessage() != null ? configurazioneGestioneConsegnaNotifiche.getFaultMessage() : "";
				
			} else {
				cadenzaRispedizione = "";
				codiceRisposta2xx = TipoGestioneNotificaTrasporto.CONSEGNA_COMPLETATA.getValue();
				codiceRisposta2xxValueMin = "";
				codiceRisposta2xxValueMax = "";
				codiceRisposta2xxValue = "";
				codiceRisposta3xx = TipoGestioneNotificaTrasporto.CONSEGNA_COMPLETATA.getValue();
				codiceRisposta3xxValueMin = "";
				codiceRisposta3xxValueMax = "";
				codiceRisposta3xxValue = "";
				codiceRisposta4xx = TipoGestioneNotificaTrasporto.CONSEGNA_COMPLETATA.getValue();
				codiceRisposta4xxValueMin = "";
				codiceRisposta4xxValueMax = "";
				codiceRisposta4xxValue = "";
				codiceRisposta5xx = TipoGestioneNotificaTrasporto.CONSEGNA_COMPLETATA.getValue();
				codiceRisposta5xxValueMin = "";
				codiceRisposta5xxValueMax = "";
				codiceRisposta5xxValue = "";
				gestioneFault = TipoGestioneNotificaFault.CONSEGNA_COMPLETATA.getValue();
				faultCode = "";
				faultActor = "";
				faultMessage = "";
			}

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			dati.addElement(ServletUtils.getDataElementForEditModeFinished());

			dati = porteApplicativeHelper.addConnettoriMultipliNotificheToDati(dati, TipoOperazione.OTHER, beaBehaviourType, nomeSAConnettore, serviceBinding, cadenzaRispedizione, 
					codiceRisposta2xx, codiceRisposta2xxValueMin, codiceRisposta2xxValueMax, codiceRisposta2xxValue, 
					codiceRisposta3xx, codiceRisposta3xxValueMin, codiceRisposta3xxValueMax, codiceRisposta3xxValue, 
					codiceRisposta4xx, codiceRisposta4xxValueMin, codiceRisposta4xxValueMax, codiceRisposta4xxValue, 
					codiceRisposta5xx, codiceRisposta5xxValueMin, codiceRisposta5xxValueMax, codiceRisposta5xxValue, 
					gestioneFault, faultCode, faultActor, faultMessage,
					consegnaSincrona,
					coda, priorita, prioritaMax);

			dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, idPorta, idsogg, idPorta, idAsps, dati);

			dati = porteApplicativeHelper.addInformazioniGruppiAsHiddenToDati(TipoOperazione.OTHER, dati, idTabP, null, accessoDaAPSParametro != null ? accessoDaAPSParametro : "", 
					connettoreAccessoGruppi, connettoreRegistro, connettoreAccessoListaConnettori);	

			pd.setDati(dati);

			pd.setMessage(CostantiControlStation.LABEL_AGGIORNAMENTO_EFFETTUATO_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_PROPRIETA_NOTIFICHE, 
					ForwardParams.OTHER(""));
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_PROPRIETA_NOTIFICHE,
					ForwardParams.OTHER(""));
		}  
	}
	
	private void resetConnettoriPrioritari(String coda, PorteApplicativeCore porteApplicativeCore) {
		List<String> aliasJmx = porteApplicativeCore.getJmxPdD_aliases();
		if(aliasJmx!=null && !aliasJmx.isEmpty()) {
			for (String alias : aliasJmx) {
				String metodo = porteApplicativeCore.getJmxPdD_configurazioneSistema_nomeMetodo_updateConnettoriPrioritari(alias);
				try{
					String stato = porteApplicativeCore.getInvoker().invokeJMXMethod(alias, 
							porteApplicativeCore.getJmxPdD_configurazioneSistema_type(alias),
							porteApplicativeCore.getJmxPdD_configurazioneSistema_nomeRisorsaConsegnaContenutiApplicativi(alias), 
							metodo, 
							coda);
					if(stato==null) {
						throw new ServletException("Aggiornamento fallito");
					}
					if(!JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO.equals(stato)) {
						throw new ServletException(stato);
					}
				}catch(Exception e){
					String msgErrore = "Errore durante l'aggiornamento dei connettori prioritari (coda:"+coda+") via jmx (jmxMethod '"+metodo+"') (node:"+alias+"): "+e.getMessage();
					ControlStationCore.logError(msgErrore, e);
				}
			}
		}
	}
}
