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
package org.openspcoop2.web.ctrlstat.servlet.pd;

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
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativiPattern;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativiRichiesta;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativiRichiestaApplicabilita;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativiStato;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
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
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * PorteDelegateValidazioneContenutiRichiestaAdd
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteDelegateValidazioneContenutiRichiestaAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	
		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session);
		if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
		
		try {
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			String id = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			
			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idFruizione = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";
			Long idFru = Long.parseLong(idFruizione);
			
			String idRichiestaS = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RICHIESTA_ID_RICHIESTA);
			String nome = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RICHIESTA_NOME);
			// configurazione stato (ereditata da validazionecontenuti)
			String statoValidazione = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RICHIESTA_STATO);
			String tipoValidazione = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RICHIESTA_TIPO);
			String applicaMTOM = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RICHIESTA_MTOM);

			String soapAction = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_SOAP_ACTION);
			String jsonSchema = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RICHIESTA_JSON_SCHEMA);
			
			String patternAndS = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RICHIESTA_AND);
			String patternNotS = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RICHIESTA_NOT); 
			// configurazione applicabilita (ereditata dalle trasformazioni)
			String azioniAllTmp = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RICHIESTA_APPLICABILITA_AZIONI_ALL);
			boolean azioniAll = azioniAllTmp==null || "".equals(azioniAllTmp) || CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI_ALL_VALUE_TRUE.equals(azioniAllTmp);
			String [] azioni = porteDelegateHelper.getParameterValues(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RICHIESTA_APPLICABILITA_AZIONI);
			String pattern = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RICHIESTA_APPLICABILITA_PATTERN);
			String contentType = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RICHIESTA_APPLICABILITA_CT);
			
			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFruizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);

			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteDelegateCore);
			SoggettiCore soggettiCore = new SoggettiCore(porteDelegateCore);
			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// Prendo nome della porta applicativa
			PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			String nomePorta = portaDelegata.getNome();
			
			MappingFruizionePortaDelegata mappingAssociatoPorta = porteDelegateCore.getMappingFruizionePortaDelegata(portaDelegata);
			
			String[] azioniDisponibiliList = null;
			String[] azioniDisponibiliLabelList = null;
			
			
			List<String> azioniAssociatePorta = new ArrayList<>();
			if(portaDelegata.getAzione() != null && portaDelegata.getAzione().getAzioneDelegataList() != null)
				azioniAssociatePorta.addAll(portaDelegata.getAzione().getAzioneDelegataList());

			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			AccordoServizioParteComuneSintetico apc = apcCore.getAccordoServizioSintetico(asps.getIdAccordo()); 
			ServiceBinding serviceBinding = ServiceBinding.valueOf(apc.getServiceBinding().name());
			Map<String,String> azioniAccordo = porteDelegateCore.getAzioniConLabel(asps, apc, false, true, new ArrayList<String>());
			
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
					IDServizio idServizioFromAccordo = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
					IDSoggetto idSoggettoFruitore = new IDSoggetto();
					String tipoSoggettoFruitore = null;
					String nomeSoggettoFruitore = null;
					if(apsCore.isRegistroServiziLocale()){
						org.openspcoop2.core.registry.Soggetto soggettoFruitore = soggettiCore.getSoggettoRegistro(Integer.parseInt(idsogg));
						tipoSoggettoFruitore = soggettoFruitore.getTipo();
						nomeSoggettoFruitore = soggettoFruitore.getNome();
					}else{
						org.openspcoop2.core.config.Soggetto soggettoFruitore = soggettiCore.getSoggetto(Integer.parseInt(idsogg));
						tipoSoggettoFruitore = soggettoFruitore.getTipo();
						nomeSoggettoFruitore = soggettoFruitore.getNome();
					}
					idSoggettoFruitore.setTipo(tipoSoggettoFruitore);
					idSoggettoFruitore.setNome(nomeSoggettoFruitore);
					
					List<MappingFruizionePortaDelegata> listaMappingFruizione = apsCore.serviziFruitoriMappingList(idFru, idSoggettoFruitore , idServizioFromAccordo, null);
					List<String> azioniOccupate = new ArrayList<>();
					int listaMappingErogazioneSize = listaMappingFruizione != null ? listaMappingFruizione.size() : 0;
					if(listaMappingErogazioneSize > 0) {
						for (int i = 0; i < listaMappingFruizione.size(); i++) {
							MappingFruizionePortaDelegata mappingErogazionePortaDelegata = listaMappingFruizione.get(i);
							// colleziono le azioni gia' configurate
							PortaDelegata portaDelegataTmp = porteDelegateCore.getPortaDelegata(mappingErogazionePortaDelegata.getIdPortaDelegata());
							if(portaDelegataTmp.getAzione() != null && portaDelegataTmp.getAzione().getAzioneDelegataList() != null)
								azioniOccupate.addAll(portaDelegataTmp.getAzione().getAzioneDelegataList());
						}
					}
					Map<String,String> azioniAccordoDisponibili = porteDelegateCore.getAzioniConLabel(asps, apc, false, true, azioniOccupate);
					
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
			String servletPatternList = PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_PATTERN_LIST;
			List<Parameter> paramsPatternList = new ArrayList<Parameter>();
			paramsPatternList.add(pId);
			paramsPatternList.add(pIdSoggetto);
			paramsPatternList.add(pIdAsps);
			paramsPatternList.add(pIdFruizione);
			List<String> listaJsonSchema = new ArrayList<String>();
			int numeroPattern = 0; // porteDelegateCore.numeroPatternValidazioneContenuti(portaDelegata);
			
			String postBackElementName = porteDelegateHelper.getPostBackElementName();
			
			// se ho modificato il soggetto ricalcolo il servizio e il service binding
			if (postBackElementName != null) {
				if(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RICHIESTA_TIPO.equals(postBackElementName)) {
					if (tipoValidazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_JSON)) { 
						jsonSchema = null;
					}
					
					if (tipoValidazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_PATTERN)) {
						patternAndS = null;
						patternNotS = null;
					}
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

			
			// setto il titolo
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI,
						portaDelegata);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_CONFIG_DI+nomePorta;
			}

			lstParam.add(new Parameter(labelPerPorta, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI,  pId, pIdSoggetto, pIdAsps, pIdFruizione));
			lstParam.add(new Parameter(CostantiControlStation.LABEL_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RICHIESTE,  PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_RICHIESTA_LIST,
					pId, pIdSoggetto, pIdAsps, pIdFruizione));
			lstParam.add(ServletUtils.getParameterAggiungi());

			ServletUtils.setPageDataTitle(pd, lstParam);

			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (porteDelegateHelper.isEditModeInProgress()) {
				
				if(nome == null) {
					nome = "";
					statoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
					tipoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_INTERFACE;
					applicaMTOM = "";
					jsonSchema = "";
					soapAction = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_ABILITATO; 
					patternAndS = Costanti.CHECK_BOX_ENABLED;
					patternNotS = Costanti.CHECK_BOX_DISABLED;
				}
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteDelegateHelper.addValidazioneContenutiRichiestaToDati(dati, TipoOperazione.ADD, null, 
						idRichiestaS, nome, true, statoValidazione, tipoValidazione, applicaMTOM, 
						serviceBinding, apc.getFormatoSpecifica(), tipoValidazioneJsonEnabled, soapAction, jsonSchema, listaJsonSchema, 
						patternAndS, patternNotS, numeroPattern, servletPatternList, paramsPatternList, visualizzaLinkPattern, 
						azioniAll, azioniDisponibiliList, azioniDisponibiliLabelList, azioniDisponibiliLabelList, pattern, contentType);
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, id, idsogg, null, idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_RICHIESTA, ForwardParams.ADD());
			}
			
			boolean isOk = porteDelegateHelper.validazioneContenutiRichiestaCheckData(TipoOperazione.ADD, null, true, portaDelegata.getId());
			if (!isOk) {

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteDelegateHelper.addValidazioneContenutiRichiestaToDati(dati, TipoOperazione.ADD, null, 
						idRichiestaS, nome, true, statoValidazione, tipoValidazione, applicaMTOM, 
						serviceBinding, apc.getFormatoSpecifica(), tipoValidazioneJsonEnabled, soapAction, jsonSchema, listaJsonSchema, 
						patternAndS, patternNotS, numeroPattern, servletPatternList, paramsPatternList, visualizzaLinkPattern, 
						azioniAll, azioniDisponibiliList, azioniDisponibiliLabelList, azioniDisponibiliLabelList, pattern, contentType);
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, id, idsogg, null, idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_RICHIESTA,	ForwardParams.ADD());
			}
			
			// calcolo prossima posizione
			int posizione = 1;
			for (ValidazioneContenutiApplicativiRichiesta check : portaDelegata.getValidazioneContenutiApplicativi().getRichiestaList()) {
				if(check.getPosizione()>=posizione) {
					posizione = check.getPosizione()+1;
				}
			}
			
			// salvataggio nuova regola
			ValidazioneContenutiApplicativiRichiesta richiesta = new ValidazioneContenutiApplicativiRichiesta();
			richiesta.setNome(nome);
			
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
			
			richiesta.setConfigurazione(vxStato);
			
			// applicabilita
			ValidazioneContenutiApplicativiRichiestaApplicabilita applicabilita = new ValidazioneContenutiApplicativiRichiestaApplicabilita();
			
			applicabilita.setMatch(pattern);
			if(contentType != null) {
				applicabilita.getContentTypeList().addAll(Arrays.asList(contentType.split(",")));
			}
			
			if(azioni != null && azioni.length > 0) {
				for (String azione : azioni) {
					applicabilita.addAzione(azione);
				}
			}
			
			richiesta.setApplicabilita(applicabilita);
			
			portaDelegata.getValidazioneContenutiApplicativi().addRichiesta(richiesta);
			
			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), portaDelegata);
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			int idLista = Liste.PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_RICHIESTA; 
			
			ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
			
			List<ValidazioneContenutiApplicativiRichiesta> lista = porteDelegateCore.listaRichiesteValidazioneContenuti(Long.parseLong(id), ricerca);
			
			porteDelegateHelper.preparePorteDelegateValidazioneContenutiRichiestaList(ricerca, lista);  
						
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_RICHIESTA, ForwardParams.ADD());


		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_RICHIESTA, ForwardParams.ADD());
		} 
	}
}
