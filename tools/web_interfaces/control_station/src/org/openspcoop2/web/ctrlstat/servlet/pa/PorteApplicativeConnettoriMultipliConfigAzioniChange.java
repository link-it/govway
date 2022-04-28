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
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.behaviour.conditional.ConfigurazioneSelettoreCondizioneRegola;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
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
 * PorteApplicativeConnettoriMultipliConfigAzioniChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteApplicativeConnettoriMultipliConfigAzioniChange extends Action {

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
			String oldNome = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_OLD_NOME);
			String nome = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_NOME);
			String patternOperazione = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_PATTERN_OPERAZIONE);
			String identificazioneCondizionale = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_IDENTIFICAZIONE_CONDIZIONALE);
			String identificazioneCondizionalePattern = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_IDENTIFICAZIONE_CONDIZIONALE_PATTERN);
			String identificazioneCondizionalePrefisso = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_IDENTIFICAZIONE_CONDIZIONALE_PREFISSO);
			String identificazioneCondizionaleSuffisso = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_IDENTIFICAZIONE_CONDIZIONALE_SUFFISSO);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteApplicativeCore);
			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// Prendo nome della porta applicativa
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String nomePorta = pa.getNome();
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
			
			String idTabP = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			Parameter pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta);
			Parameter pNomePorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, nomePorta);
			Parameter pIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps);
			String accessoDaAPSParametro = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS);
			Parameter pAccessoDaAPS = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS, accessoDaAPSParametro != null ? accessoDaAPSParametro : "");
			String connettoreAccessoGruppi = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI);
			Parameter pConnettoreAccessoDaGruppi = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI, connettoreAccessoGruppi);
			String connettoreRegistro = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO);
			Parameter pConnettoreRegistro = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO, connettoreRegistro);
			String connettoreAccessoCM = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_LISTA_CONNETTORI_MULTIPLI);
			Parameter pConnettoreAccessoCM = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_LISTA_CONNETTORI_MULTIPLI, connettoreAccessoCM);
			
			boolean accessoDaListaAPS = false;
			if(Costanti.CHECK_BOX_ENABLED_TRUE.equals(accessoDaAPSParametro)) {
				accessoDaListaAPS = true;
			}
			
			org.openspcoop2.pdd.core.behaviour.conditional.ConfigurazioneCondizionale configurazioneCondizionale = org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.read(pa, ControlStationCore.getLog());
			
			Set<String> regoleEsistenti = configurazioneCondizionale.getRegoleOrdinate();
			ConfigurazioneSelettoreCondizioneRegola oldRegola = configurazioneCondizionale.getRegola(oldNome);
			
			boolean selezioneConnettoreByFiltro = configurazioneCondizionale.isByFilter();
			// select list connettori in caso di selezione by nome
			List<String> connettoriValues = new ArrayList<>();
			List<String> connettoriLabels = new ArrayList<>();
			if(!selezioneConnettoreByFiltro) {
				List<PortaApplicativaServizioApplicativo> servizioApplicativoList = pa.getServizioApplicativoList();
				if(servizioApplicativoList != null) {
					for (PortaApplicativaServizioApplicativo paSA : servizioApplicativoList) {
						String nomeConnettorePaSA = porteApplicativeHelper.getLabelNomePortaApplicativaServizioApplicativo(paSA);
	
						connettoriValues.add(nomeConnettorePaSA);
						connettoriLabels.add(nomeConnettorePaSA);
					}
				}
			}
			
			String postBackElementName = porteApplicativeHelper.getPostBackElementName();
			if(postBackElementName != null ){
				
				if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_IDENTIFICAZIONE_CONDIZIONALE)) {
					identificazioneCondizionalePattern = "";
					identificazioneCondizionalePrefisso = "";
					identificazioneCondizionaleSuffisso = ""; 
				}
			}
			
			boolean isModalitaCompleta = porteApplicativeHelper.isModalitaCompleta();
			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, session, request);
			// setto la barra del titolo
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
							pa);
				}
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_DI+nomePorta;
			}
			
			if(accessoDaListaAPS) {
				lstParam.remove(lstParam.size()-1);
			}
			
			lstParam.add(new Parameter(labelPerPorta,PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONFIGURAZIONE_CONNETTORI_MULTIPLI, pIdSogg, pIdPorta, pIdAsps,
					pAccessoDaAPS, pConnettoreAccessoDaGruppi,	pConnettoreRegistro, pConnettoreAccessoCM));
			
			String labelPagLista = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_AZIONI_REGOLE;
			
			List<Parameter> listaParametriServletProprietaCustom = new ArrayList<>();
			listaParametriServletProprietaCustom.add(pIdSogg);
			listaParametriServletProprietaCustom.add(pIdPorta);
			listaParametriServletProprietaCustom.add(pNomePorta);
			listaParametriServletProprietaCustom.add(pIdAsps);
			listaParametriServletProprietaCustom.add(pAccessoDaAPS);
			listaParametriServletProprietaCustom.add(pConnettoreAccessoDaGruppi);
			listaParametriServletProprietaCustom.add(pConnettoreRegistro);
			listaParametriServletProprietaCustom.add(pConnettoreAccessoCM);
			lstParam.add(new Parameter(labelPagLista, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_AZIONI_LIST,	listaParametriServletProprietaCustom.toArray(new Parameter[listaParametriServletProprietaCustom.size()])));
			
			lstParam.add(new Parameter(oldRegola.getRegola(), null));

			// Se servizioApplicativohid = null, devo visualizzare la pagina per
			// l'inserimento dati
			if (porteApplicativeHelper.isEditModeInProgress()) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				if(nome == null) {
					nome = oldRegola.getRegola();
					patternOperazione = oldRegola.getPatternOperazione();
					
					if(oldRegola.getStaticInfo() == null) {
						identificazioneCondizionale = oldRegola.getTipoSelettore().getValue();
						identificazioneCondizionalePattern = oldRegola.getPattern();
					} else {
						identificazioneCondizionale = PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_IDENTIFICAZIONE_CONDIZIONALE_STATIC_INFO;
						identificazioneCondizionalePattern = oldRegola.getStaticInfo();
					}
					identificazioneCondizionalePrefisso = oldRegola.getPrefix();
					identificazioneCondizionaleSuffisso = oldRegola.getSuffix(); 
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addAzioneConnettoriMultipliConfigToDati(dati, TipoOperazione.CHANGE, serviceBinding, oldNome, nome,
						patternOperazione,  selezioneConnettoreByFiltro, identificazioneCondizionale, identificazioneCondizionalePattern, 
						identificazioneCondizionalePrefisso, identificazioneCondizionaleSuffisso,
						connettoriValues, connettoriLabels
						);
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idPorta, idsogg, idPorta,idAsps,  dati);
				
				dati = porteApplicativeHelper.addInformazioniGruppiAsHiddenToDati(TipoOperazione.OTHER, dati, idTabP, null, accessoDaAPSParametro != null ? accessoDaAPSParametro : "", 
						connettoreAccessoGruppi, connettoreRegistro, null);	

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_AZIONI,
						ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.azioneConnettoriMultipliConfigCheckData(TipoOperazione.CHANGE, serviceBinding, idPorta, oldNome, nome,
					patternOperazione,  selezioneConnettoreByFiltro, identificazioneCondizionale, identificazioneCondizionalePattern,
					identificazioneCondizionalePrefisso, identificazioneCondizionaleSuffisso, regoleEsistenti);
			if (!isOk) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteApplicativeHelper.addAzioneConnettoriMultipliConfigToDati(dati, TipoOperazione.CHANGE, serviceBinding, oldNome, nome,
						patternOperazione,  selezioneConnettoreByFiltro, identificazioneCondizionale, identificazioneCondizionalePattern, 
						identificazioneCondizionalePrefisso, identificazioneCondizionaleSuffisso,
						connettoriValues, connettoriLabels
						);
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idPorta, idsogg, idPorta, idAsps, dati);
				
				dati = porteApplicativeHelper.addInformazioniGruppiAsHiddenToDati(TipoOperazione.OTHER, dati, idTabP, null, accessoDaAPSParametro != null ? accessoDaAPSParametro : "", 
						connettoreAccessoGruppi, connettoreRegistro, null);	

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_AZIONI, 
						ForwardParams.CHANGE());
			}

			// Inserisco il ruolo nel db
			configurazioneCondizionale = org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.read(pa, ControlStationCore.getLog());
			
			ConfigurazioneSelettoreCondizioneRegola regola = new ConfigurazioneSelettoreCondizioneRegola();
			
			regola.setRegola(nome);
			regola.setPatternOperazione(patternOperazione);
			
			if(!identificazioneCondizionale.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_AZIONI_IDENTIFICAZIONE_CONDIZIONALE_STATIC_INFO)) {
				regola.setTipoSelettore(org.openspcoop2.pdd.core.behaviour.conditional.TipoSelettore.toEnumConstant(identificazioneCondizionale));
				regola.setPattern(identificazioneCondizionalePattern);
			} else {
				regola.setStaticInfo(identificazioneCondizionalePattern);
			}
			regola.setPrefix(identificazioneCondizionalePrefisso);
			regola.setSuffix(identificazioneCondizionaleSuffisso);
			
			configurazioneCondizionale.removeRegola(oldNome);
			configurazioneCondizionale.addRegola(regola);
			
			org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.save(pa, configurazioneCondizionale);

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);

			// ricarico la configurazione
			pa = porteApplicativeCore.getPortaApplicativa(idInt); 

			configurazioneCondizionale = org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.read(pa, ControlStationCore.getLog());
			
			Set<String> lista = null;
			if(configurazioneCondizionale != null) {
				lista = configurazioneCondizionale.getRegoleOrdinate();
			}

			// Preparo la lista
			Search ricerca = null;
						
			porteApplicativeHelper.preparePorteApplicativeConnettoriMultipliConfigAzioniList(pa, configurazioneCondizionale, ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_AZIONI, 
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_AZIONI,
					ForwardParams.CHANGE());
		} 
	}
	
}
