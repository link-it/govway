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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativiPattern;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativiRisposta;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativiRispostaApplicabilita;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativiStato;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * PorteApplicativeValidazioneContenutiRispostaAdd
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteApplicativeValidazioneContenutiRispostaAdd extends Action {

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
			
			String idRispostaS = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_ID_RISPOSTA);
			String nome = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_NOME);
			// configurazione stato (ereditata da validazionecontenuti)
			String statoValidazione = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_STATO);
			String tipoValidazione = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_TIPO);
			String applicaMTOM = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_MTOM);

			String jsonSchema = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_JSON_SCHEMA);
			
			String patternAndS = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_AND);
			String patternNotS = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_NOT); 
			// configurazione applicabilita (ereditata dalle trasformazioni)
			String azioniAllTmp = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RICHIESTA_APPLICABILITA_AZIONI_ALL);
			boolean azioniAll = azioniAllTmp==null || "".equals(azioniAllTmp) || CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI_ALL_VALUE_TRUE.equals(azioniAllTmp);
			String [] azioni = porteApplicativeHelper.getParameterValues(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RICHIESTA_APPLICABILITA_AZIONI);
			String pattern = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RICHIESTA_APPLICABILITA_PATTERN);
			String contentType = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RICHIESTA_APPLICABILITA_CT);
			
			String returnCode = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_APPLICABILITA_STATUS);
			if(returnCode == null)
				returnCode = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_QUALSIASI;
			
			String statusMin = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_APPLICABILITA_STATUS_MIN);
			if(statusMin == null)
				statusMin = "";
			String statusMax = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_APPLICABILITA_STATUS_MAX);
			if(statusMax == null)
				statusMax = "";
			String emptyResponse = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_APPLICABILITA_EMPTY_RESPONSE);
			String problemDetail = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_APPLICABILITA_PROBLEM_DETAIL);

			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteApplicativeCore);
			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// Prendo nome della porta applicativa
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String nomePorta = pa.getNome();
			
			MappingErogazionePortaApplicativa mappingAssociatoPorta = porteApplicativeCore.getMappingErogazionePortaApplicativa(pa); 
			
			String[] azioniDisponibiliList = null;
			String[] azioniDisponibiliLabelList = null;
			
			
			List<String> azioniAssociatePorta = new ArrayList<>();
			if(pa.getAzione() != null && pa.getAzione().getAzioneDelegataList() != null)
				azioniAssociatePorta.addAll(pa.getAzione().getAzioneDelegataList());

			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			AccordoServizioParteComuneSintetico apc = apcCore.getAccordoServizioSintetico(asps.getIdAccordo()); 
			ServiceBinding serviceBinding = ServiceBinding.valueOf(apc.getServiceBinding().name());
			Map<String,String> azioniAccordo = porteApplicativeCore.getAzioniConLabel(asps, apc, false, true, new ArrayList<String>());
			
			if(azioniAccordo!=null && azioniAccordo.size()>0) {
				// porte ridefinite
				if(!mappingAssociatoPorta.isDefault()) {
					
					azioniDisponibiliList = new String[azioniAssociatePorta.size()];
					azioniDisponibiliLabelList = new String[azioniAssociatePorta.size()];
					int i = 0;
					for (String string : azioniAccordo.keySet()) {
						if(azioniAssociatePorta.contains(string)) {
							azioniDisponibiliList[i] = string;
							azioniDisponibiliLabelList[i] = azioniAccordo.get(string);
							i++;
						}
					}
				} else { // elenco azioni non associate a nessun mapping
					IDServizio idServizio2 = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps); 
					List<MappingErogazionePortaApplicativa> listaMappingErogazione = apsCore.mappingServiziPorteAppList(idServizio2,asps.getId(), null);
					List<String> azioniOccupate = new ArrayList<>();
					int listaMappingErogazioneSize = listaMappingErogazione != null ? listaMappingErogazione.size() : 0;
					if(listaMappingErogazioneSize > 0) {
						for (int i = 0; i < listaMappingErogazione.size(); i++) {
							MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa = listaMappingErogazione.get(i);
							// colleziono le azioni gia' configurate
							PortaApplicativa portaApplicativa = porteApplicativeCore.getPortaApplicativa(mappingErogazionePortaApplicativa.getIdPortaApplicativa());
							if(portaApplicativa.getAzione() != null && portaApplicativa.getAzione().getAzioneDelegataList() != null)
								azioniOccupate.addAll(portaApplicativa.getAzione().getAzioneDelegataList());
						}
					}
					Map<String,String> azioniAccordoDisponibili = porteApplicativeCore.getAzioniConLabel(asps, apc, false, true, azioniOccupate);
					
					azioniDisponibiliList = new String[azioniAccordoDisponibili.size()];
					azioniDisponibiliLabelList = new String[azioniAccordoDisponibili.size()];
					int i = 0;
					for (String string : azioniAccordoDisponibili.keySet()) {
						azioniDisponibiliList[i] = string;
						azioniDisponibiliLabelList[i] = azioniAccordoDisponibili.get(string);
						i++;
					}
				}
			}
			
			boolean tipoValidazioneJsonEnabled = false;
			boolean visualizzaLinkPattern = false;
			String servletPatternList = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_PATTERN_LIST;
			List<Parameter> paramsPatternList = new ArrayList<Parameter>();
			Parameter pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta);
			Parameter pIdSoggetto = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps);
			
			paramsPatternList.add(pIdPorta);
			paramsPatternList.add(pIdSoggetto);
			paramsPatternList.add(pIdAsps);
			List<String> listaJsonSchema = new ArrayList<String>();
			int numeroPattern = 0; // porteDelegateCore.numeroPatternValidazioneContenuti(portaDelegata);
			
			String postBackElementName = porteApplicativeHelper.getPostBackElementName();
			
			// se ho modificato il soggetto ricalcolo il servizio e il service binding
			if (postBackElementName != null) {
				if(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_TIPO.equals(postBackElementName)) {
					if (tipoValidazione.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE_JSON)) { 
						jsonSchema = null;
					}
					
					if (tipoValidazione.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE_PATTERN)) {
						patternAndS = null;
						patternNotS = null;
					}
				}
				
				if(postBackElementName.equals(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_APPLICABILITA_STATUS)) {
					statusMin = "";
					statusMax = "";
				}
			}
			
			if(serviceBinding.equals(ServiceBinding.REST)) {
				ISearch ricerca = new Search();
				ricerca.addFilter(Liste.ACCORDI_ALLEGATI, Filtri.FILTRO_RUOLO_DOCUMENTO, RuoliDocumento.specificaSemiformale.toString());
				// ricaricare la lista dei json schema
				List<Documento> accordiAllegatiList = apcCore.accordiAllegatiList(apc.getId(), ricerca); 
				if(accordiAllegatiList != null && accordiAllegatiList.size() > 0) {
					tipoValidazioneJsonEnabled = true;
					listaJsonSchema = accordiAllegatiList.stream().map(doc -> doc.getFile()).collect(Collectors.toList());
				}
			}
			
			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI,
						pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_CONFIG_DI+nomePorta;
			}
			
			
			
			lstParam.add(new Parameter(labelPerPorta,  PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI, pIdSoggetto, pIdPorta, pIdAsps));
			lstParam.add(new Parameter(CostantiControlStation.LABEL_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTE, 
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_RISPOSTA_LIST, pIdSoggetto, pIdPorta, pIdAsps));
			lstParam.add(ServletUtils.getParameterAggiungi());

			ServletUtils.setPageDataTitle(pd, lstParam);

			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (porteApplicativeHelper.isEditModeInProgress()) {
				
				if(nome == null) {
					nome = "";
					statoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_DISABILITATO;
					tipoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE_INTERFACE;
					applicaMTOM = "";
					jsonSchema = "";
					patternAndS = Costanti.CHECK_BOX_ENABLED;
					patternNotS = Costanti.CHECK_BOX_DISABLED;
				}
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteApplicativeHelper.addValidazioneContenutiRispostaToDati(dati, TipoOperazione.ADD, null, 
						idRispostaS, nome, false, statoValidazione, tipoValidazione, applicaMTOM, 
						serviceBinding, apc.getFormatoSpecifica(), tipoValidazioneJsonEnabled, jsonSchema, listaJsonSchema, 
						patternAndS, patternNotS, numeroPattern, servletPatternList, paramsPatternList, visualizzaLinkPattern, 
						azioniAll, azioniDisponibiliList, azioniDisponibiliLabelList, azioniDisponibiliLabelList, pattern, contentType, 
						returnCode, statusMin, statusMax, problemDetail, emptyResponse);
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta,idAsps,  dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_RISPOSTA,	ForwardParams.ADD());
			}
			
			
			boolean isOk = porteApplicativeHelper.validazioneContenutiRispostaCheckData(TipoOperazione.ADD, null, false, pa.getId());
			
			if (!isOk) {

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteApplicativeHelper.addValidazioneContenutiRispostaToDati(dati, TipoOperazione.ADD, null, 
						idRispostaS, nome, true, statoValidazione, tipoValidazione, applicaMTOM, 
						serviceBinding, apc.getFormatoSpecifica(), tipoValidazioneJsonEnabled, jsonSchema, listaJsonSchema, 
						patternAndS, patternNotS, numeroPattern, servletPatternList, paramsPatternList, visualizzaLinkPattern, 
						azioniAll, azioniDisponibiliList, azioniDisponibiliLabelList, azioniDisponibiliLabelList, pattern, contentType, 
						returnCode, statusMin, statusMax, problemDetail, emptyResponse);
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta,idAsps,  dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_RISPOSTA,	ForwardParams.ADD());
			}
			
			// salvataggio nuova regola
			ValidazioneContenutiApplicativiRisposta risposta = new ValidazioneContenutiApplicativiRisposta();
			risposta.setNome(nome);
			
			// stato
			ValidazioneContenutiApplicativiStato vxStato = new ValidazioneContenutiApplicativiStato();
			
			vxStato.setStato(StatoFunzionalitaConWarning.toEnumConstant(statoValidazione));
			ValidazioneContenutiApplicativiTipo validazioneContenutiApplicativiTipo = ValidazioneContenutiApplicativiTipo.toEnumConstant(tipoValidazione);
			vxStato.setTipo(validazioneContenutiApplicativiTipo);
			if(applicaMTOM != null){
				if(ServletUtils.isCheckBoxEnabled(applicaMTOM))
					vxStato.setAcceptMtomMessage(StatoFunzionalita.ABILITATO);
				else 
					vxStato.setAcceptMtomMessage(StatoFunzionalita.DISABILITATO);
			} else 
				vxStato.setAcceptMtomMessage(null);
			if(validazioneContenutiApplicativiTipo != null) {
				switch (validazioneContenutiApplicativiTipo) {
				case INTERFACE:
				case OPENSPCOOP:
					if(serviceBinding.equals(ServiceBinding.SOAP)) {
					//	vxStato.setSoapAction(StatoFunzionalita.toEnumConstant(soapAction));
					}
					break;
				case JSON:
					if(serviceBinding.equals(ServiceBinding.REST)) {
						vxStato.setJsonSchema(jsonSchema);
					}
					break;
				case PATTERN:
					if(vxStato.getConfigurazionePattern() == null)
						vxStato.setConfigurazionePattern(new ValidazioneContenutiApplicativiPattern());
					
					vxStato.getConfigurazionePattern().setAnd(ServletUtils.isCheckBoxEnabled(patternAndS));
					vxStato.getConfigurazionePattern().setNot(ServletUtils.isCheckBoxEnabled(patternNotS));
					break;
				case XSD:
				default:
					break;
				}
			}
			risposta.setConfigurazione(vxStato);
			
			// applicabilita
			ValidazioneContenutiApplicativiRispostaApplicabilita applicabilita = new ValidazioneContenutiApplicativiRispostaApplicabilita();
			
			applicabilita.setMatch(pattern);
			if(contentType != null) {
				applicabilita.getContentTypeList().addAll(Arrays.asList(contentType.split(",")));
			}
			
			if(azioni != null && azioni.length > 0) {
				for (String azione : azioni) {
					applicabilita.addAzione(azione);
				}
			}
			
			if(returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_QUALSIASI)) {
				applicabilita.setReturnCodeMin(null);
				applicabilita.setReturnCodeMax(null);
			} else if(returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_ESATTO)) {
				applicabilita.setReturnCodeMin(StringUtils.isNotEmpty(statusMin) ? Integer.parseInt(statusMin) : null);
				applicabilita.setReturnCodeMax(StringUtils.isNotEmpty(statusMin) ? Integer.parseInt(statusMin) : null);
			} else { // intervallo
				applicabilita.setReturnCodeMin(StringUtils.isNotEmpty(statusMin) ? Integer.parseInt(statusMin) : null);
				applicabilita.setReturnCodeMax(StringUtils.isNotEmpty(statusMax) ? Integer.parseInt(statusMax) : null);
			}
			
			if(problemDetail != null) {
				applicabilita.setRestProblemDetail(StatoFunzionalita.toEnumConstant(problemDetail));
			}
			
			if(emptyResponse != null) {
				applicabilita.setRestEmptyResponse(StatoFunzionalita.toEnumConstant(emptyResponse));
			}
			
			risposta.setApplicabilita(applicabilita);
			
			pa.getValidazioneContenutiApplicativi().addRisposta(risposta);
			
			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			int idLista = Liste.PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_RISPOSTA; 
			
			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
			
			List<ValidazioneContenutiApplicativiRisposta> lista = porteApplicativeCore.listaRisposteValidazioneContenuti(Long.parseLong(idPorta), ricerca);
			 
			porteApplicativeHelper.preparePorteAppValidazioneContenutiRispostaList(ricerca, lista); 
						
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_RISPOSTA, ForwardParams.ADD());


		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_RISPOSTA, ForwardParams.ADD());
		} 
	}
}
