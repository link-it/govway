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
import org.openspcoop2.core.config.PortaApplicativa;
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
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**     
 * PorteApplicativeValidazioneContenuti
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteApplicativeValidazioneContenuti extends Action {

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
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			String id = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";

			String applicaModificaS = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_APPLICA_MODIFICA);
			boolean applicaModifica = ServletUtils.isCheckBoxEnabled(applicaModificaS);

			String statoValidazione = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_XSD);
			String tipoValidazione = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE);
			String applicaMTOM = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_APPLICA_MTOM);

			String idTab = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!porteApplicativeHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}
			
			String soapAction = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_SOAP_ACTION);
			String jsonSchema = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_JSON_SCHEMA);
			
			String patternAndS = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_PATTERN_AND);
			String patternNotS = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_PATTERN_NOT); 

			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// Prendo il nome della porta
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			AccordiServizioParteSpecificaCore aspsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
			AccordiServizioParteComuneCore aspcCore = new AccordiServizioParteComuneCore(porteApplicativeCore);

			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String idporta = pa.getNome();
			ValidazioneContenutiApplicativi validazioneContenutiApplicativi = pa.getValidazioneContenutiApplicativi();
			ValidazioneContenutiApplicativiStato validazioneContenutiApplicativiStato = validazioneContenutiApplicativi != null ? validazioneContenutiApplicativi.getConfigurazione() : null; 

			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(pa.getServizio().getTipo(), pa.getServizio().getNome(), 
					pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario(), pa.getServizio().getVersione());
			long idServizioLong = aspsCore.getIdAccordoServizioParteSpecifica(idServizio);
			AccordoServizioParteSpecifica asps = aspsCore.getAccordoServizioParteSpecifica(idServizioLong, false);
			AccordoServizioParteComuneSintetico aspc = aspcCore.getAccordoServizioSintetico(IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
			ServiceBinding serviceBinding = ServiceBinding.valueOf(aspc.getServiceBinding().name()); 
			
			boolean tipoValidazioneJsonEnabled = false;
			boolean visualizzaLinkPattern = validazioneContenutiApplicativiStato != null ? validazioneContenutiApplicativiStato.getTipo().equals(ValidazioneContenutiApplicativiTipo.PATTERN) : false;
			List<String> listaJsonSchema = new ArrayList<String>();
			int numeroPattern = porteApplicativeCore.numeroPatternValidazioneContenuti(pa);
			String servletPatternList = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_PATTERN_LIST;
			List<Parameter> paramsPatternList = new ArrayList<Parameter>();
			paramsPatternList.add(new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, id));
			paramsPatternList.add(new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));
			paramsPatternList.add(new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));
			
			boolean visualizzaLinkRichiesta = validazioneContenutiApplicativiStato != null ? !validazioneContenutiApplicativiStato.getStato().equals(StatoFunzionalitaConWarning.DISABILITATO) : false;
			int numeroRichieste = porteApplicativeCore.numeroRichiesteValidazioneContenuti(pa);
			String servletRichiesteList = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_RICHIESTA_LIST;
			List<Parameter> paramsRichiesteList = new ArrayList<Parameter>();
			paramsRichiesteList.addAll(paramsPatternList);
			int numeroRisposte = porteApplicativeCore.numeroRisposteValidazioneContenuti(pa);
			String servletRisposteList = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_RISPOSTA_LIST;
			List<Parameter> paramsRisposteList = new ArrayList<Parameter>();
			paramsRisposteList.addAll(paramsPatternList);
			
			String postBackElementName = porteApplicativeHelper.getPostBackElementName();
			if(postBackElementName!=null) {
				if(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE.equals(postBackElementName)) {
					if (tipoValidazione.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE_JSON)) {
						jsonSchema = null;
					}
					
					if (tipoValidazione.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE_PATTERN)) {
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
			

			
			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI,
						pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_CONFIG_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta,  null));

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParam);
			
			
			if(	porteApplicativeHelper.isEditModeInProgress() && !applicaModifica){

				if (statoValidazione == null) {
					if (validazioneContenutiApplicativiStato == null) {
						statoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_DISABILITATO;
					} else {
						if(validazioneContenutiApplicativiStato.getStato()!=null)
							statoValidazione = validazioneContenutiApplicativiStato.getStato().toString();
						if ((statoValidazione == null) || "".equals(statoValidazione)) {
							statoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_DISABILITATO;
						}
					}
				}
				if (tipoValidazione == null) {
					if (validazioneContenutiApplicativiStato == null) {
						tipoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE_INTERFACE;
					} else {
						if(validazioneContenutiApplicativiStato.getTipo()!=null && !StatoFunzionalitaConWarning.DISABILITATO.equals(validazioneContenutiApplicativiStato.getStato()))
							tipoValidazione = validazioneContenutiApplicativiStato.getTipo().toString();
						if (tipoValidazione == null || "".equals(tipoValidazione)) {
							tipoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE_INTERFACE ;
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

				porteApplicativeHelper.validazioneContenuti(TipoOperazione.OTHER,dati, isPortaDelegata, statoValidazione, tipoValidazione, applicaMTOM,
						serviceBinding, aspc.getFormatoSpecifica(),tipoValidazioneJsonEnabled,	soapAction, jsonSchema, listaJsonSchema,
						patternAndS, patternNotS, numeroPattern, servletPatternList, paramsPatternList, visualizzaLinkPattern, visualizzaLinkRichiesta,
						numeroRichieste, servletRichiesteList, paramsRichiesteList, numeroRisposte, servletRisposteList, paramsRisposteList);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null,idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI, ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.validazioneContenutiCheck(TipoOperazione.OTHER, isPortaDelegata);

			if (!isOk) {
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				porteApplicativeHelper.validazioneContenuti(TipoOperazione.OTHER,dati, isPortaDelegata, statoValidazione, tipoValidazione, applicaMTOM,
						serviceBinding, aspc.getFormatoSpecifica(), tipoValidazioneJsonEnabled,	soapAction, jsonSchema, listaJsonSchema,
						patternAndS, patternNotS, numeroPattern, servletPatternList, paramsPatternList, visualizzaLinkPattern, visualizzaLinkRichiesta,
						numeroRichieste, servletRichiesteList, paramsRichiesteList, numeroRisposte, servletRisposteList, paramsRisposteList);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null,idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI,
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
			pa.setValidazioneContenutiApplicativi(vx);

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			pa = porteApplicativeCore.getPortaApplicativa(idInt);
			validazioneContenutiApplicativi = pa.getValidazioneContenutiApplicativi();
			validazioneContenutiApplicativiStato = validazioneContenutiApplicativi != null ? validazioneContenutiApplicativi.getConfigurazione() : null; 

			if (statoValidazione == null) {
				if (validazioneContenutiApplicativiStato == null) {
					statoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_DISABILITATO;
				} else {
					if(validazioneContenutiApplicativiStato.getStato()!=null)
						statoValidazione = validazioneContenutiApplicativiStato.getStato().toString();
					if ((statoValidazione == null) || "".equals(statoValidazione)) {
						statoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_DISABILITATO;
					}
				}
			}
			if (tipoValidazione == null) {
				if (validazioneContenutiApplicativiStato == null) {
					tipoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE_INTERFACE;
				} else {
					if(validazioneContenutiApplicativiStato.getTipo()!=null)
						tipoValidazione = validazioneContenutiApplicativiStato.getTipo().toString();
					if (tipoValidazione == null || "".equals(tipoValidazione)) {
						tipoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE_INTERFACE ;
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

			porteApplicativeHelper.validazioneContenuti(TipoOperazione.OTHER,dati, isPortaDelegata, statoValidazione, tipoValidazione, applicaMTOM,
					serviceBinding, aspc.getFormatoSpecifica(), tipoValidazioneJsonEnabled,	soapAction, jsonSchema, listaJsonSchema,
					patternAndS, patternNotS, numeroPattern, servletPatternList, paramsPatternList, visualizzaLinkPattern, visualizzaLinkRichiesta,
					numeroRichieste, servletRichiesteList, paramsRichiesteList, numeroRisposte, servletRisposteList, paramsRisposteList);

			dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null,idAsps, dati);

			pd.setDati(dati);

			pd.setMessage(CostantiControlStation.LABEL_AGGIORNAMENTO_EFFETTUATO_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			//pd.disableEditMode();
			dati.addElement(ServletUtils.getDataElementForEditModeFinished());
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI, 
					ForwardParams.OTHER(""));

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI , 
					ForwardParams.OTHER(""));
		} 
	}			
}
