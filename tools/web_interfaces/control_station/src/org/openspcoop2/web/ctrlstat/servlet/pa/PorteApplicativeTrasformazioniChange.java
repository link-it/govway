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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRichiesta;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaSoggetto;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDBLib;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * PorteApplicativeTrasformazioniChange
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteApplicativeTrasformazioniChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);
		
		String userLogin = ServletUtils.getUserLoginFromSession(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
		
		try {
			
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte applicative
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session, request);
			if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;
			
			String idPorta = porteApplicativeHelper.getParametroLong(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String idsogg = porteApplicativeHelper.getParametroLong(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			String idAsps = porteApplicativeHelper.getParametroLong(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			
			
			String first = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_FIRST);
			String nome = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_NOME);
			String stato = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_STATO);
			String azioniAllTmp = porteApplicativeHelper.getParametroBoolean(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_APPLICABILITA_AZIONI_ALL);
			boolean azioniAll = azioniAllTmp==null || "".equals(azioniAllTmp) || CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI_ALL_VALUE_TRUE.equals(azioniAllTmp);
			String [] azioni = porteApplicativeHelper.getParameterValues(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_APPLICABILITA_AZIONI);
			String pattern = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_APPLICABILITA_PATTERN);
			String contentType = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_APPLICABILITA_CT);
			String [] connettori = porteApplicativeHelper.getParameterValues(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_APPLICABILITA_CONNETTORI);
			
			String id = porteApplicativeHelper.getParametroLong(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE);
			long idTrasformazione = Long.parseLong(id);

			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteApplicativeCore);

			// Prendo nome della porta applicativa
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			if(pa==null) {
				throw new Exception("PortaApplicativa con id '"+idInt+"' non trovata");
			}
			String nomePorta = pa.getNome();
			
			Trasformazioni trasformazioni = pa.getTrasformazioni();
			TrasformazioneRegola oldRegola = null;
			for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
				if(reg.getId().longValue() == idTrasformazione) {
					oldRegola = reg;
					break;
				}
			}
			if(oldRegola==null) {
				throw new Exception("TrasformazioneRegola con id '"+idTrasformazione+"' non trovata");
			}
			
			String nomeTrasformazione = oldRegola.getNome();
			Parameter pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta);
			Parameter pIdSoggetto = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps);
			Parameter pIdTrasformazione = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE, id);
			
			// parametri visualizzazione link
			String servletTrasformazioniRichiesta = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA;
			String servletTrasformazioniRispostaList = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_LIST;
			int numeroTrasformazioniRisposte = oldRegola.sizeRispostaList();
			
			List<Parameter> parametriInvocazioneServletTrasformazioniRichiesta = new ArrayList<>();
			parametriInvocazioneServletTrasformazioniRichiesta.add(pIdPorta);
			parametriInvocazioneServletTrasformazioniRichiesta.add(pIdSoggetto);
			parametriInvocazioneServletTrasformazioniRichiesta.add(pIdAsps);
			parametriInvocazioneServletTrasformazioniRichiesta.add(pIdTrasformazione);
			
			List<Parameter> parametriInvocazioneServletTrasformazioniRisposta = new ArrayList<>();
			parametriInvocazioneServletTrasformazioniRisposta.add(pIdPorta);
			parametriInvocazioneServletTrasformazioniRisposta.add(pIdSoggetto);
			parametriInvocazioneServletTrasformazioniRisposta.add(pIdAsps);
			parametriInvocazioneServletTrasformazioniRisposta.add(pIdTrasformazione);
			
			String servletTrasformazioniAutorizzazioneAutenticati =   PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTO_LIST;
			List<Parameter> parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati = new ArrayList<>();
			parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati.add(pIdPorta);
			parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati.add(pIdSoggetto);
			parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati.add(pIdAsps);
			parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati.add(pIdTrasformazione);
			int numAutenticati = oldRegola.getApplicabilita() != null ? oldRegola.getApplicabilita().sizeSoggettoList() : 0;
			String servletTrasformazioniApplicativiAutenticati = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO_AUTORIZZATO_LIST;
			List<Parameter> parametriInvocazioneServletTrasformazioniApplicativiAutenticati  = new ArrayList<>();
			parametriInvocazioneServletTrasformazioniApplicativiAutenticati.add(pIdPorta);
			parametriInvocazioneServletTrasformazioniApplicativiAutenticati.add(pIdSoggetto);
			parametriInvocazioneServletTrasformazioniApplicativiAutenticati.add(pIdAsps);
			parametriInvocazioneServletTrasformazioniApplicativiAutenticati.add(pIdTrasformazione);
			int numApplicativiAutenticati = oldRegola.getApplicabilita() != null ? oldRegola.getApplicabilita().sizeServizioApplicativoList() : 0;
			
			List<TrasformazioneRegolaApplicabilitaServizioApplicativo> applicabilitaApplicativi = oldRegola.getApplicabilita() != null ? oldRegola.getApplicabilita().getServizioApplicativoList() : null;
			List<TrasformazioneRegolaApplicabilitaSoggetto> applicabilitaSoggetti = oldRegola.getApplicabilita() != null ? oldRegola.getApplicabilita().getSoggettoList() : null;
			
			
			MappingErogazionePortaApplicativa mappingAssociatoPorta = porteApplicativeCore.getMappingErogazionePortaApplicativa(pa);
			
			String[] azioniDisponibiliList = null;
			String[] azioniDisponibiliLabelList = null;
			
			List<String> azioniAssociatePorta = new ArrayList<>();
			if(pa.getAzione() != null && pa.getAzione().getAzioneDelegataList() != null)
				azioniAssociatePorta.addAll(pa.getAzione().getAzioneDelegataList());

			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			AccordoServizioParteComuneSintetico apc = apcCore.getAccordoServizioSintetico(asps.getIdAccordo()); 
			ServiceBinding serviceBinding = apcCore.toMessageServiceBinding(apc.getServiceBinding());
			Map<String,String> azioniAccordo = porteApplicativeCore.getAzioniConLabel(asps, apc, false, true, new ArrayList<>());
			
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
			

			String[] connettoriDisponibiliList = null;
			String[] connettoriDisponibiliLabelList = null;
			if(pa!=null && pa.getBehaviour()!=null && pa.sizeServizioApplicativoList()>0) {
				connettoriDisponibiliList = new String[pa.sizeServizioApplicativoList()];
				connettoriDisponibiliLabelList = new String[pa.sizeServizioApplicativoList()];
				int index = 0;
				for (PortaApplicativaServizioApplicativo pasa : pa.getServizioApplicativoList()) {
					String nomeServizioApplicativoErogatoreConnettoreMultiplo = pasa.getNome();
					String nomeConnettoreMultiplo = pasa.getDatiConnettore()!= null ? pasa.getDatiConnettore().getNome() : null;
					if(nomeConnettoreMultiplo==null) {
						nomeConnettoreMultiplo=CostantiConfigurazione.NOME_CONNETTORE_DEFAULT;
					}
					connettoriDisponibiliList[index] = nomeServizioApplicativoErogatoreConnettoreMultiplo;
					connettoriDisponibiliLabelList[index] = nomeConnettoreMultiplo;
					index++;
				}
			}
			
			
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
					pIdPorta,
					pIdSoggetto,
					pIdAsps
					));
			
			lstParam.add(new Parameter(nomeTrasformazione, null));

			ServletUtils.setPageDataTitle(pd, lstParam);
			
			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (porteApplicativeHelper.isEditModeInProgress()) {
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				// primo accesso
				if(first == null) {
					
					nome = oldRegola.getNome();
					
					stato = DriverConfigurazioneDBLib.getValue(oldRegola.getStato());
					
					TrasformazioneRegolaApplicabilitaRichiesta applicabilita = oldRegola.getApplicabilita();
					if(applicabilita != null) {
						pattern = applicabilita.getPattern();
						
						if(applicabilita.getAzioneList() != null) {
							azioni = applicabilita.getAzioneList() .toArray(new String[applicabilita.sizeAzioneList()]);
						} else {
							azioni = new String[0];
						}
						
						contentType = applicabilita.getContentTypeList() != null ? StringUtils.join(applicabilita.getContentTypeList(), ",") : "";  
						
						if(applicabilita.getConnettoreList() != null && pa!=null && pa.getBehaviour()!=null) {
							connettori = applicabilita.getConnettoreList() .toArray(new String[applicabilita.sizeConnettoreList()]);
						} else {
							connettori = null;
						}
					}	
					
					azioniAll = (azioni==null || azioni.length<=0);
				}

				dati = porteApplicativeHelper.addTrasformazioneToDati(TipoOperazione.CHANGE, dati, pa, id, nome, 
						stato, azioniAll, azioniDisponibiliList, azioniDisponibiliLabelList, azioni, pattern, contentType,
						connettoriDisponibiliList, connettoriDisponibiliLabelList, connettori,
						apc.getServiceBinding(),
						servletTrasformazioniRichiesta, parametriInvocazioneServletTrasformazioniRichiesta, servletTrasformazioniRispostaList, parametriInvocazioneServletTrasformazioniRisposta, numeroTrasformazioniRisposte, false,
						servletTrasformazioniAutorizzazioneAutenticati, parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati , numAutenticati,
						servletTrasformazioniApplicativiAutenticati,  parametriInvocazioneServletTrasformazioniApplicativiAutenticati , numApplicativiAutenticati);
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idPorta, idsogg, idPorta,idAsps,  dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI,	ForwardParams.CHANGE());
			}
			
			// quando un parametro viene inviato come vuoto, sul db viene messo null, gestisco il caso
			String azioniAsString = azioni != null ? StringUtils.join(Arrays.asList(azioni), ",") : "";
			String azioniDBCheck = StringUtils.isNotEmpty(azioniAsString) ? azioniAsString : null;
			String patternDBCheck = StringUtils.isNotEmpty(pattern) ? pattern : null;
			String contentTypeDBCheck = StringUtils.isNotEmpty(contentType) ? contentType : null;
			String connettoriAsString = connettori != null ? StringUtils.join(Arrays.asList(connettori), ",") : "";
			String connettoriDBCheck = StringUtils.isNotEmpty(connettoriAsString) ? connettoriAsString : null;
			TrasformazioneRegola trasformazioneDBCheckCriteri = porteApplicativeCore.getTrasformazione(Long.parseLong(idPorta), azioniDBCheck, patternDBCheck, contentTypeDBCheck, connettoriDBCheck,
					applicabilitaSoggetti, applicabilitaApplicativi, true);
			TrasformazioneRegola trasformazioneDBCheckNome = porteApplicativeCore.getTrasformazione(Long.parseLong(idPorta), nome);
			
			boolean isOk = porteApplicativeHelper.trasformazioniCheckData(TipoOperazione.CHANGE, Long.parseLong(idPorta), nome, trasformazioneDBCheckCriteri, trasformazioneDBCheckNome, oldRegola,
					serviceBinding);
			if (!isOk) {

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteApplicativeHelper.addTrasformazioneToDati(TipoOperazione.CHANGE, dati, pa, id, nome, 
						stato, azioniAll, azioniDisponibiliList, azioniDisponibiliLabelList, azioni, pattern, contentType,
						connettoriDisponibiliList, connettoriDisponibiliLabelList, connettori,
						apc.getServiceBinding(),
						servletTrasformazioniRichiesta, parametriInvocazioneServletTrasformazioniRichiesta, servletTrasformazioniRispostaList, parametriInvocazioneServletTrasformazioniRisposta, numeroTrasformazioniRisposte, false,
						servletTrasformazioniAutorizzazioneAutenticati, parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati , numAutenticati,
						servletTrasformazioniApplicativiAutenticati,  parametriInvocazioneServletTrasformazioniApplicativiAutenticati , numApplicativiAutenticati);
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idPorta, idsogg, idPorta,idAsps,  dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI,	ForwardParams.CHANGE());
			}
			
			// aggiorno la regola
			trasformazioni = pa.getTrasformazioni();
			for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
				if(reg.getId().longValue() == idTrasformazione) {
					
					reg.setNome(nome);
					
					reg.setStato(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(stato));
					
					if(reg.getApplicabilita() == null)
						reg.setApplicabilita(new TrasformazioneRegolaApplicabilitaRichiesta());
					
					reg.getApplicabilita().setPattern(pattern);
					reg.getApplicabilita().getContentTypeList().clear();
					if(contentType != null) {
						reg.getApplicabilita().getContentTypeList().addAll(Arrays.asList(contentType.split(",")));
					}
					
					reg.getApplicabilita().getAzioneList().clear();
					if(azioni != null && azioni.length > 0) {
						for (String azione : azioni) {
							reg.getApplicabilita().addAzione(azione);
						}
					}
					
					reg.getApplicabilita().getConnettoreList().clear();
					if(connettori != null && connettori.length > 0) {
						for (String connettore : connettori) {
							reg.getApplicabilita().addConnettore(connettore);
						}
					}
					
					break;
				}
			}
			
			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);
			
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
			
			int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI; 
			
			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
			
			List<TrasformazioneRegola> lista = porteApplicativeCore.porteAppTrasformazioniList(Long.parseLong(idPorta), ricerca);
			
			porteApplicativeHelper.preparePorteAppTrasformazioniRegolaList(ricerca, lista);
						
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI,	ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI, 
					ForwardParams.CHANGE());
		}
	}
}
