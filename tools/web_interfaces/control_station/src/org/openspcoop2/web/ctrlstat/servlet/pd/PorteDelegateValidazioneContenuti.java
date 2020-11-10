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
import java.util.List;
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
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativiPattern;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativiStato;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**     
 * PorteDelegateValidazioneContenuti
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteDelegateValidazioneContenuti extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		boolean isPortaDelegata = true;

		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session);
		if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;

		try {
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			String id = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idSoggFruitore = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			String idFruizione= porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null) 
				idFruizione = "";

			String statoValidazione = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_XSD);
			String tipoValidazione = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE);
			String applicaMTOM = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_APPLICA_MTOM);

			String applicaModificaS = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_APPLICA_MODIFICA);
			boolean applicaModifica = ServletUtils.isCheckBoxEnabled(applicaModificaS);

			String idTab = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!porteDelegateHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}
			
			String soapAction = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_SOAP_ACTION);
			String jsonSchema = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_JSON_SCHEMA);
			
			String patternAndS = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_PATTERN_AND);
			String patternNotS = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_PATTERN_NOT); 

			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// Prendo il nome della porta
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			AccordiServizioParteSpecificaCore aspsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);
			AccordiServizioParteComuneCore aspcCore = new AccordiServizioParteComuneCore(porteDelegateCore);

			PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			String idporta = portaDelegata.getNome();
			ValidazioneContenutiApplicativi validazioneContenutiApplicativi = portaDelegata.getValidazioneContenutiApplicativi();
			ValidazioneContenutiApplicativiStato validazioneContenutiApplicativiStato = validazioneContenutiApplicativi != null ? validazioneContenutiApplicativi.getConfigurazione() : null; 

			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(portaDelegata.getServizio().getTipo(), portaDelegata.getServizio().getNome(), 
					portaDelegata.getSoggettoErogatore().getTipo(), portaDelegata.getSoggettoErogatore().getNome(), portaDelegata.getServizio().getVersione());
			long idServizioLong = aspsCore.getIdAccordoServizioParteSpecifica(idServizio);
			AccordoServizioParteSpecifica asps = aspsCore.getAccordoServizioParteSpecifica(idServizioLong, false);
			AccordoServizioParteComuneSintetico aspc = aspcCore.getAccordoServizioSintetico(IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
			ServiceBinding serviceBinding = ServiceBinding.valueOf(aspc.getServiceBinding().name());
			
			boolean tipoValidazioneJsonEnabled = false;
			boolean visualizzaLinkPattern = validazioneContenutiApplicativiStato != null ? validazioneContenutiApplicativiStato.getTipo().equals(ValidazioneContenutiApplicativiTipo.PATTERN) : false;
			
			boolean visualizzaLinkRichiesta = validazioneContenutiApplicativiStato != null ? !validazioneContenutiApplicativiStato.getStato().equals(StatoFunzionalitaConWarning.DISABILITATO) : false;
			String servletPatternList = PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_PATTERN_LIST;
			List<Parameter> paramsPatternList = new ArrayList<Parameter>();
			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idSoggFruitore);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFruizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);
			paramsPatternList.add(pId);
			paramsPatternList.add(pIdSoggetto);
			paramsPatternList.add(pIdAsps);
			paramsPatternList.add(pIdFruizione);
			
			int numeroRichieste = porteDelegateCore.numeroRichiesteValidazioneContenuti(portaDelegata);
			String servletRichiesteList = PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_RICHIESTA_LIST;
			List<Parameter> paramsRichiesteList = new ArrayList<Parameter>();
			paramsRichiesteList.addAll(paramsPatternList);
			int numeroRisposte = porteDelegateCore.numeroRisposteValidazioneContenuti(portaDelegata);
			String servletRisposteList = PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_RISPOSTA_LIST;
			List<Parameter> paramsRisposteList = new ArrayList<Parameter>();
			paramsRisposteList.addAll(paramsPatternList);
			
			List<String> listaJsonSchema = new ArrayList<String>();
			int numeroPattern = porteDelegateCore.numeroPatternValidazioneContenuti(portaDelegata);
			
			String postBackElementName = porteDelegateHelper.getPostBackElementName();
			if(postBackElementName!=null) {
				if(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE.equals(postBackElementName)) {
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
				List<Documento> accordiAllegatiList = aspcCore.accordiAllegatiList(aspc.getId(), ricerca); 
				if(accordiAllegatiList != null && accordiAllegatiList.size() > 0) {
					tipoValidazioneJsonEnabled = true;
					listaJsonSchema = accordiAllegatiList.stream().map(doc -> doc.getFile()).collect(Collectors.toList());
				}
			}
			
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idSoggFruitore, idAsps, idFruizione);

			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI,
						portaDelegata);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_CONFIG_DI+idporta;
			}
			lstParam.add(new Parameter(labelPerPorta,  null));

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParam);

			
			if(	porteDelegateHelper.isEditModeInProgress() && !applicaModifica){

				if (statoValidazione == null) {
					if (validazioneContenutiApplicativiStato == null) {
						statoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
					} else {
						if(validazioneContenutiApplicativiStato.getStato()!=null)
							statoValidazione = validazioneContenutiApplicativiStato.getStato().toString();
						if ((statoValidazione == null) || "".equals(statoValidazione)) {
							statoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
						}
					}
				}
				if (tipoValidazione == null) {
					if (validazioneContenutiApplicativiStato == null) {
						tipoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_INTERFACE;
					} else {
						if(validazioneContenutiApplicativiStato.getTipo()!=null && !StatoFunzionalitaConWarning.DISABILITATO.equals(validazioneContenutiApplicativiStato.getStato()))
							tipoValidazione = validazioneContenutiApplicativiStato.getTipo().toString();
						if (tipoValidazione == null || "".equals(tipoValidazione)) {
							tipoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_INTERFACE ;
						}
					}
				}
				if (applicaMTOM == null) {
					applicaMTOM = "";
					if (validazioneContenutiApplicativiStato != null) {
						if(validazioneContenutiApplicativiStato.getAcceptMtomMessage()!=null)
							if (validazioneContenutiApplicativiStato.getAcceptMtomMessage().equals(StatoFunzionalita.ABILITATO)) 
								applicaMTOM = Costanti.CHECK_BOX_ENABLED;
					}
				}
				
				if(jsonSchema == null) {
					jsonSchema = "";
					if (validazioneContenutiApplicativiStato != null) {
						jsonSchema = validazioneContenutiApplicativiStato.getJsonSchema();
					}
				}
				
				if(soapAction == null) {
					if (validazioneContenutiApplicativiStato == null) {
						soapAction = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_ABILITATO;
					} else {
						if(validazioneContenutiApplicativiStato.getSoapAction()!=null)
							soapAction = validazioneContenutiApplicativiStato.getSoapAction().toString();
						if ((soapAction == null) || "".equals(soapAction)) {
							soapAction = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_ABILITATO;
						}
					}
				}
				
				if(patternAndS == null) {
					if (validazioneContenutiApplicativiStato == null) {
						patternAndS = Costanti.CHECK_BOX_ENABLED;
					} else {
						ValidazioneContenutiApplicativiPattern configurazionePattern = validazioneContenutiApplicativiStato.getConfigurazionePattern();
						if(configurazionePattern != null) {
							patternAndS = ServletUtils.boolToCheckBoxStatus(configurazionePattern.getAnd());
						} else {
							patternAndS = Costanti.CHECK_BOX_ENABLED;
						}
					}
				}
				
				if(patternNotS == null) {
					if (validazioneContenutiApplicativiStato == null) {
						patternNotS = Costanti.CHECK_BOX_DISABLED;
					} else {
						ValidazioneContenutiApplicativiPattern configurazionePattern = validazioneContenutiApplicativiStato.getConfigurazionePattern();
						if(configurazionePattern != null) {
							patternNotS = ServletUtils.boolToCheckBoxStatus(configurazionePattern.getNot());
						} else {
							patternNotS = Costanti.CHECK_BOX_DISABLED;
						}
					}
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				porteDelegateHelper.validazioneContenuti(TipoOperazione.OTHER,dati, isPortaDelegata, statoValidazione, tipoValidazione, applicaMTOM,
						serviceBinding, aspc.getFormatoSpecifica(),tipoValidazioneJsonEnabled,	soapAction, jsonSchema, listaJsonSchema,
						patternAndS, patternNotS, numeroPattern, servletPatternList, paramsPatternList, visualizzaLinkPattern, visualizzaLinkRichiesta,
						numeroRichieste, servletRichiesteList, paramsRichiesteList, numeroRisposte, servletRisposteList, paramsRisposteList);

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI, ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.validazioneContenutiCheck(TipoOperazione.OTHER, isPortaDelegata);

			if (!isOk) {
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				porteDelegateHelper.validazioneContenuti(TipoOperazione.OTHER,dati, isPortaDelegata, statoValidazione, tipoValidazione, applicaMTOM,
						serviceBinding, aspc.getFormatoSpecifica(),tipoValidazioneJsonEnabled,	soapAction, jsonSchema, listaJsonSchema,
						patternAndS, patternNotS, numeroPattern, servletPatternList, paramsPatternList, visualizzaLinkPattern, visualizzaLinkRichiesta,
						numeroRichieste, servletRichiesteList, paramsRichiesteList, numeroRisposte, servletRisposteList, paramsRisposteList);

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI,
						ForwardParams.OTHER(""));
			}

			ValidazioneContenutiApplicativi vx = new ValidazioneContenutiApplicativi();
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
			
			switch (validazioneContenutiApplicativiTipo) {
			case INTERFACE:
			case OPENSPCOOP:
				if(serviceBinding.equals(ServiceBinding.SOAP)) {
					vxStato.setSoapAction(StatoFunzionalita.toEnumConstant(soapAction));
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

			vx.setConfigurazione(vxStato);
			portaDelegata.setValidazioneContenutiApplicativi(vx);

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), portaDelegata);

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			validazioneContenutiApplicativi = portaDelegata.getValidazioneContenutiApplicativi();
			validazioneContenutiApplicativiStato = validazioneContenutiApplicativi != null ? validazioneContenutiApplicativi.getConfigurazione() : null; 

			if (statoValidazione == null) {
				if (validazioneContenutiApplicativiStato == null) {
					statoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
				} else {
					if(validazioneContenutiApplicativiStato.getStato()!=null)
						statoValidazione = validazioneContenutiApplicativiStato.getStato().toString();
					if ((statoValidazione == null) || "".equals(statoValidazione)) {
						statoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
					}
				}
			}
			if (tipoValidazione == null) {
				if (validazioneContenutiApplicativiStato == null) {
					tipoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_INTERFACE;
				} else {
					if(validazioneContenutiApplicativiStato.getTipo()!=null)
						tipoValidazione = validazioneContenutiApplicativiStato.getTipo().toString();
					if (tipoValidazione == null || "".equals(tipoValidazione)) {
						tipoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_INTERFACE ;
					}
				}
			}
			if (applicaMTOM == null) {
				applicaMTOM = "";
				if (validazioneContenutiApplicativiStato != null) {
					if(validazioneContenutiApplicativiStato.getAcceptMtomMessage()!=null)
						if (validazioneContenutiApplicativiStato.getAcceptMtomMessage().equals(StatoFunzionalita.ABILITATO)) 
							applicaMTOM = Costanti.CHECK_BOX_ENABLED;
				}
			}
			
			if(jsonSchema == null) {
				jsonSchema = "";
				if (validazioneContenutiApplicativiStato != null) {
					jsonSchema = validazioneContenutiApplicativiStato.getJsonSchema();
				}
			}
			
			if(soapAction == null) {
				if (validazioneContenutiApplicativiStato == null) {
					soapAction = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_ABILITATO;
				} else {
					if(validazioneContenutiApplicativiStato.getSoapAction()!=null)
						soapAction = validazioneContenutiApplicativiStato.getSoapAction().toString();
					if ((soapAction == null) || "".equals(soapAction)) {
						soapAction = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_ABILITATO;
					}
				}
			}
			
			if(patternAndS == null) {
				if (validazioneContenutiApplicativiStato == null) {
					patternAndS = Costanti.CHECK_BOX_ENABLED;
				} else {
					ValidazioneContenutiApplicativiPattern configurazionePattern = validazioneContenutiApplicativiStato.getConfigurazionePattern();
					if(configurazionePattern != null) {
						patternAndS = ServletUtils.boolToCheckBoxStatus(configurazionePattern.getAnd());
					} else {
						patternAndS = Costanti.CHECK_BOX_ENABLED;
					}
				}
			}
			
			if(patternNotS == null) {
				if (validazioneContenutiApplicativiStato == null) {
					patternNotS = Costanti.CHECK_BOX_DISABLED;
				} else {
					ValidazioneContenutiApplicativiPattern configurazionePattern = validazioneContenutiApplicativiStato.getConfigurazionePattern();
					if(configurazionePattern != null) {
						patternNotS = ServletUtils.boolToCheckBoxStatus(configurazionePattern.getNot());
					} else {
						patternNotS = Costanti.CHECK_BOX_DISABLED;
					}
				}
			}
			
			visualizzaLinkRichiesta = validazioneContenutiApplicativiStato != null ? !validazioneContenutiApplicativiStato.getStato().equals(StatoFunzionalitaConWarning.DISABILITATO) : false;
			visualizzaLinkPattern = validazioneContenutiApplicativiStato != null ? validazioneContenutiApplicativiStato.getTipo().equals(ValidazioneContenutiApplicativiTipo.PATTERN) : false;

			porteDelegateHelper.validazioneContenuti(TipoOperazione.OTHER,dati, isPortaDelegata, statoValidazione, tipoValidazione, applicaMTOM,
					serviceBinding, aspc.getFormatoSpecifica(),tipoValidazioneJsonEnabled,	soapAction, jsonSchema, listaJsonSchema,
					patternAndS, patternNotS, numeroPattern, servletPatternList, paramsPatternList, visualizzaLinkPattern, visualizzaLinkRichiesta,
					numeroRichieste, servletRichiesteList, paramsRichiesteList, numeroRisposte, servletRisposteList, paramsRisposteList);

			dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
					idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);

			pd.setDati(dati);

			pd.setMessage(CostantiControlStation.LABEL_AGGIORNAMENTO_EFFETTUATO_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			//pd.disableEditMode();
			dati.addElement(ServletUtils.getDataElementForEditModeFinished());
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI, 
					ForwardParams.OTHER(""));

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI, 
					ForwardParams.OTHER(""));
		} 
	}
}
