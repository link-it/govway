/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import java.util.List;
import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.CorsConfigurazioneHeaders;
import org.openspcoop2.core.config.CorsConfigurazioneMethods;
import org.openspcoop2.core.config.CorsConfigurazioneOrigin;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoGestioneCORS;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/***
 * 
 * PorteApplicativeGestioneCors
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteApplicativeGestioneCors extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		TipoOperazione tipoOperazione = TipoOperazione.OTHER;

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	

		try {

			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte applicative
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session, request);
			if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;
			
			String id = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";

			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			ConfigurazioneCore confCore = new ConfigurazioneCore(porteApplicativeCore);

			boolean showStato = true;
			String statoCorsPorta = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_STATO_PORTA);
			String statoCorsTmp = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_STATO);
			boolean corsStato = ServletUtils.isCheckBoxEnabled(statoCorsTmp); 
			String corsTipoTmp = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_TIPO);
			TipoGestioneCORS corsTipo = corsTipoTmp != null ? TipoGestioneCORS.toEnumConstant(corsTipoTmp) : TipoGestioneCORS.GATEWAY;
			String corsAllAllowOriginsTmp = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_ORIGINS);
			boolean corsAllAllowOrigins = ServletUtils.isCheckBoxEnabled(corsAllAllowOriginsTmp);
			String corsAllAllowHeadersTmp = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_HEADERS);
			boolean corsAllAllowHeaders = ServletUtils.isCheckBoxEnabled(corsAllAllowHeadersTmp);
			String corsAllAllowMethodsTmp = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_METHODS);
			boolean corsAllAllowMethods = ServletUtils.isCheckBoxEnabled(corsAllAllowMethodsTmp);
			String corsAllowHeaders =  porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_HEADERS);
			String corsAllowOrigins =  porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_ORIGINS);
			String corsAllowMethods =  porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_METHODS);
			String corsAllowCredentialTmp = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_CREDENTIALS);
			boolean corsAllowCredential =  ServletUtils.isCheckBoxEnabled(corsAllowCredentialTmp);
			String corsExposeHeaders = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_EXPOSE_HEADERS);
			String corsMaxAgeTmp = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_MAX_AGE);
			boolean corsMaxAge =  ServletUtils.isCheckBoxEnabled(corsMaxAgeTmp);
			String corsMaxAgeSecondsTmp = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_MAX_AGE_SECONDS);
			int corsMaxAgeSeconds = -1;
			if(corsMaxAgeSecondsTmp != null) {
				try {
					corsMaxAgeSeconds = Integer.parseInt(corsMaxAgeSecondsTmp);
				}catch(Exception e) {
					// ignore
				}
			}

			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String idporta = pa.getNome();

			CorsConfigurazione oldConfigurazione = pa.getGestioneCors();

			boolean initConfigurazione = false;
			String postBackElementName = porteApplicativeHelper.getPostBackElementName();
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_STATO_PORTA)){
					initConfigurazione = true;
				}
				
				if(postBackElementName.equalsIgnoreCase(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_STATO)){
					if(!porteApplicativeHelper.isCorsAbilitato(oldConfigurazione) && corsStato) {
						CorsConfigurazione configurazioneTmp = new CorsConfigurazione();
						
						corsTipo = configurazioneTmp.getTipo();
						if(configurazioneTmp.getTipo() != null && configurazioneTmp.getTipo().equals(TipoGestioneCORS.GATEWAY)) {

							corsAllAllowOrigins = true;
							if(configurazioneTmp.getAccessControlAllAllowOrigins() != null && configurazioneTmp.getAccessControlAllAllowOrigins().equals(StatoFunzionalita.DISABILITATO)) {
								corsAllAllowOrigins = false;

								configurazioneTmp.setAccessControlAllowOrigins(new CorsConfigurazioneOrigin());
								corsAllowOrigins = StringUtils.join(configurazioneTmp.getAccessControlAllowOrigins().getOriginList(), ",");
							}

							corsAllAllowHeaders = false;
							if(configurazioneTmp.getAccessControlAllAllowHeaders() != null && configurazioneTmp.getAccessControlAllAllowHeaders().equals(StatoFunzionalita.ABILITATO)) {
								corsAllAllowHeaders = true;
							}
							if(!corsAllAllowHeaders) {
								configurazioneTmp.setAccessControlAllowHeaders(new CorsConfigurazioneHeaders());
								corsAllowHeaders = StringUtils.join(configurazioneTmp.getAccessControlAllowHeaders().getHeaderList(), ",");
							}

							corsAllAllowMethods = false;
							if(configurazioneTmp.getAccessControlAllAllowMethods() != null && configurazioneTmp.getAccessControlAllAllowMethods().equals(StatoFunzionalita.ABILITATO)) {
								corsAllAllowMethods = true;
							}
							if(!corsAllAllowMethods) {
								configurazioneTmp.setAccessControlAllowMethods(new CorsConfigurazioneMethods());
								corsAllowMethods = StringUtils.join(configurazioneTmp.getAccessControlAllowMethods().getMethodList(), ",");
							}

							configurazioneTmp.setAccessControlExposeHeaders(new CorsConfigurazioneHeaders());
							corsExposeHeaders = StringUtils.join(configurazioneTmp.getAccessControlExposeHeaders().getHeaderList(), ",");

							corsAllowCredential = false;
							if(configurazioneTmp.getAccessControlAllowCredentials() != null && configurazioneTmp.getAccessControlAllowCredentials().equals(StatoFunzionalita.ABILITATO)) {
								corsAllowCredential = true;
							}

							corsMaxAge = false;
							corsMaxAgeSeconds = -1;
							if(configurazioneTmp.getAccessControlMaxAge() != null ) {
								corsMaxAge = true;
								corsMaxAgeSeconds = configurazioneTmp.getAccessControlMaxAge();
							}
						}
					}
				}
			}

			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				lstParam.remove(lstParam.size()-1);
				labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORS_CONFIGURAZIONE_CONFIG_DI, 
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORS_CONFIGURAZIONE,
						pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORS_CONFIGURAZIONE_CONFIG_DI+idporta;
			}

			if(labelPerPorta.contains(CostantiControlStation.LABEL_DEL_GRUPPO)) {
				labelPerPorta = labelPerPorta.substring(0, labelPerPorta.indexOf(CostantiControlStation.LABEL_DEL_GRUPPO));
			}
			
			lstParam.add(new Parameter(labelPerPorta,  null));

			// edit in progress
			if (porteApplicativeHelper.isEditModeInProgress()) {
				ServletUtils.setPageDataTitle(pd, lstParam);

				if(statoCorsPorta == null) {
					if(oldConfigurazione == null) {
						statoCorsPorta = CostantiControlStation.VALUE_PARAMETRO_CORS_STATO_DEFAULT;
					} else {
						statoCorsPorta = CostantiControlStation.VALUE_PARAMETRO_CORS_STATO_RIDEFINITO;
						if(oldConfigurazione.getStato() != null && oldConfigurazione.getStato().equals(StatoFunzionalita.ABILITATO)) {
							corsStato = true;

							corsTipo = oldConfigurazione.getTipo();
							if(oldConfigurazione.getTipo() != null && oldConfigurazione.getTipo().equals(TipoGestioneCORS.GATEWAY)) {

								corsAllAllowOrigins = true;
								if(oldConfigurazione.getAccessControlAllAllowOrigins() != null && oldConfigurazione.getAccessControlAllAllowOrigins().equals(StatoFunzionalita.DISABILITATO)) {
									corsAllAllowOrigins = false;

									if(oldConfigurazione.getAccessControlAllowOrigins() != null) {
										corsAllowOrigins = StringUtils.join(oldConfigurazione.getAccessControlAllowOrigins().getOriginList(), ",");
									}
								}

								corsAllAllowHeaders = false;
								if(oldConfigurazione.getAccessControlAllAllowHeaders() != null && oldConfigurazione.getAccessControlAllAllowHeaders().equals(StatoFunzionalita.ABILITATO)) {
									corsAllAllowHeaders = true;
								}
								if(!corsAllAllowHeaders &&
									oldConfigurazione.getAccessControlAllowHeaders() != null) {
									corsAllowHeaders = StringUtils.join(oldConfigurazione.getAccessControlAllowHeaders().getHeaderList(), ",");
								}

								corsAllAllowMethods = false;
								if(oldConfigurazione.getAccessControlAllAllowMethods() != null && oldConfigurazione.getAccessControlAllAllowMethods().equals(StatoFunzionalita.ABILITATO)) {
									corsAllAllowMethods = true;
								}
								if(!corsAllAllowMethods &&
									oldConfigurazione.getAccessControlAllowMethods() != null) {
									corsAllowMethods = StringUtils.join(oldConfigurazione.getAccessControlAllowMethods().getMethodList(), ",");
								}

								if(oldConfigurazione.getAccessControlExposeHeaders() != null) {
									corsExposeHeaders = StringUtils.join(oldConfigurazione.getAccessControlExposeHeaders().getHeaderList(), ",");
								}

								corsAllowCredential = false;
								if(oldConfigurazione.getAccessControlAllowCredentials() != null && oldConfigurazione.getAccessControlAllowCredentials().equals(StatoFunzionalita.ABILITATO)) {
									corsAllowCredential = true;
								}

								corsMaxAge = false;
								corsMaxAgeSeconds = -1;
								if(oldConfigurazione.getAccessControlMaxAge() != null ) {
									corsMaxAge = true;
									corsMaxAgeSeconds = oldConfigurazione.getAccessControlMaxAge();
								}
							}

						}
					}
				}

				if(initConfigurazione) {
					if(statoCorsPorta.equals(CostantiControlStation.VALUE_PARAMETRO_CORS_STATO_RIDEFINITO)) {
						Configurazione configurazione = confCore.getConfigurazioneGenerale();
						oldConfigurazione = configurazione.getGestioneCors();
						corsStato = false;
						if(oldConfigurazione != null) {
							if(oldConfigurazione.getStato() != null && oldConfigurazione.getStato().equals(StatoFunzionalita.ABILITATO)) {
								corsStato = true;

								corsTipo = oldConfigurazione.getTipo();
								if(oldConfigurazione.getTipo() != null && oldConfigurazione.getTipo().equals(TipoGestioneCORS.GATEWAY)) {

									corsAllAllowOrigins = true;
									if(oldConfigurazione.getAccessControlAllAllowOrigins() != null && oldConfigurazione.getAccessControlAllAllowOrigins().equals(StatoFunzionalita.DISABILITATO)) {
										corsAllAllowOrigins = false;

										if(oldConfigurazione.getAccessControlAllowOrigins() != null) {
											corsAllowOrigins = StringUtils.join(oldConfigurazione.getAccessControlAllowOrigins().getOriginList(), ",");
										}
									}

									corsAllAllowHeaders = false;
									if(oldConfigurazione.getAccessControlAllAllowHeaders() != null && oldConfigurazione.getAccessControlAllAllowHeaders().equals(StatoFunzionalita.ABILITATO)) {
										corsAllAllowHeaders = true;
									}
									if(!corsAllAllowHeaders &&
										oldConfigurazione.getAccessControlAllowHeaders() != null) {
										corsAllowHeaders = StringUtils.join(oldConfigurazione.getAccessControlAllowHeaders().getHeaderList(), ",");
									}

									corsAllAllowMethods = false;
									if(oldConfigurazione.getAccessControlAllAllowMethods() != null && oldConfigurazione.getAccessControlAllAllowMethods().equals(StatoFunzionalita.ABILITATO)) {
										corsAllAllowMethods = true;
									}
									if(!corsAllAllowMethods &&
										oldConfigurazione.getAccessControlAllowMethods() != null) {
										corsAllowMethods = StringUtils.join(oldConfigurazione.getAccessControlAllowMethods().getMethodList(), ",");
									}

									if(oldConfigurazione.getAccessControlExposeHeaders() != null) {
										corsExposeHeaders = StringUtils.join(oldConfigurazione.getAccessControlExposeHeaders().getHeaderList(), ",");
									}

									corsAllowCredential = false;
									if(oldConfigurazione.getAccessControlAllowCredentials() != null && oldConfigurazione.getAccessControlAllowCredentials().equals(StatoFunzionalita.ABILITATO)) {
										corsAllowCredential = true;
									}

									corsMaxAge = false;
									corsMaxAgeSeconds = -1;
									if(oldConfigurazione.getAccessControlMaxAge() != null ) {
										corsMaxAge = true;
										corsMaxAgeSeconds = oldConfigurazione.getAccessControlMaxAge();
									}
								}
							}
							else {
								corsStato = true;
								
								CorsConfigurazione configurazioneTmp = new CorsConfigurazione();
								
								corsTipo = configurazioneTmp.getTipo();
								if(configurazioneTmp.getTipo() != null && configurazioneTmp.getTipo().equals(TipoGestioneCORS.GATEWAY)) {

									corsAllAllowOrigins = true;
									if(configurazioneTmp.getAccessControlAllAllowOrigins() != null && configurazioneTmp.getAccessControlAllAllowOrigins().equals(StatoFunzionalita.DISABILITATO)) {
										corsAllAllowOrigins = false;

										configurazioneTmp.setAccessControlAllowOrigins(new CorsConfigurazioneOrigin());
										corsAllowOrigins = StringUtils.join(configurazioneTmp.getAccessControlAllowOrigins().getOriginList(), ",");
									}

									corsAllAllowHeaders = false;
									if(configurazioneTmp.getAccessControlAllAllowHeaders() != null && configurazioneTmp.getAccessControlAllAllowHeaders().equals(StatoFunzionalita.ABILITATO)) {
										corsAllAllowHeaders = true;
									}
									if(!corsAllAllowHeaders) {
										configurazioneTmp.setAccessControlAllowHeaders(new CorsConfigurazioneHeaders());
										corsAllowHeaders = StringUtils.join(configurazioneTmp.getAccessControlAllowHeaders().getHeaderList(), ",");
									}

									corsAllAllowMethods = false;
									if(configurazioneTmp.getAccessControlAllAllowMethods() != null && configurazioneTmp.getAccessControlAllAllowMethods().equals(StatoFunzionalita.ABILITATO)) {
										corsAllAllowMethods = true;
									}
									if(!corsAllAllowMethods) {
										configurazioneTmp.setAccessControlAllowMethods(new CorsConfigurazioneMethods());
										corsAllowMethods = StringUtils.join(configurazioneTmp.getAccessControlAllowMethods().getMethodList(), ",");
									}

									configurazioneTmp.setAccessControlExposeHeaders(new CorsConfigurazioneHeaders());
									corsExposeHeaders = StringUtils.join(configurazioneTmp.getAccessControlExposeHeaders().getHeaderList(), ",");

									corsAllowCredential = false;
									if(configurazioneTmp.getAccessControlAllowCredentials() != null && configurazioneTmp.getAccessControlAllowCredentials().equals(StatoFunzionalita.ABILITATO)) {
										corsAllowCredential = true;
									}

									corsMaxAge = false;
									corsMaxAgeSeconds = -1;
									if(configurazioneTmp.getAccessControlMaxAge() != null ) {
										corsMaxAge = true;
										corsMaxAgeSeconds = configurazioneTmp.getAccessControlMaxAge();
									}
								}
							}
						}
					}
				}

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				porteApplicativeHelper.addConfigurazioneCorsPorteToDati(tipoOperazione, dati, showStato, statoCorsPorta,
						corsStato, corsTipo, corsAllAllowOrigins, corsAllAllowHeaders, corsAllAllowMethods, corsAllowHeaders, corsAllowOrigins, corsAllowMethods, corsAllowCredential, corsExposeHeaders, corsMaxAge, corsMaxAgeSeconds);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_GESTIONE_CORS,	ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.checkDataConfigurazioneCorsPorta(tipoOperazione, showStato, statoCorsPorta);
			if (!isOk) {

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				porteApplicativeHelper.addConfigurazioneCorsPorteToDati(tipoOperazione, dati, showStato, statoCorsPorta,
						corsStato, corsTipo, corsAllAllowOrigins, corsAllAllowHeaders, corsAllAllowMethods, 
						corsAllowHeaders, corsAllowOrigins, corsAllowMethods, corsAllowCredential, corsExposeHeaders, corsMaxAge, corsMaxAgeSeconds);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_GESTIONE_CORS,	ForwardParams.OTHER(""));
			}

			CorsConfigurazione newConfigurazione = null;
			if(statoCorsPorta.equals(CostantiControlStation.VALUE_PARAMETRO_CORS_STATO_RIDEFINITO)) {
				newConfigurazione = porteApplicativeHelper.getGestioneCors(corsStato, corsTipo, corsAllAllowOrigins, corsAllAllowHeaders, corsAllAllowMethods,
						corsAllowHeaders, corsAllowOrigins, corsAllowMethods, corsAllowCredential, corsExposeHeaders, corsMaxAge, corsMaxAgeSeconds);
			}
			
			pa.setGestioneCors(newConfigurazione);
			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);
			// Preparo la lista
			pd.setMessage(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE_CORS_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);

			pa = porteApplicativeCore.getPortaApplicativa(idInt);
			idporta = pa.getNome();

			ServletUtils.setPageDataTitle(pd, lstParam);

			// preparo i campi
			List<DataElement> dati = new ArrayList<>();
			dati.add(ServletUtils.getDataElementForEditModeFinished());

			// ricarico la configurazione

			CorsConfigurazione configurazioneAggiornata = pa.getGestioneCors();
			
			if(configurazioneAggiornata == null) {
				statoCorsPorta = CostantiControlStation.VALUE_PARAMETRO_CORS_STATO_DEFAULT;
			} else {
				statoCorsPorta = CostantiControlStation.VALUE_PARAMETRO_CORS_STATO_RIDEFINITO;
				if(configurazioneAggiornata.getStato() != null && configurazioneAggiornata.getStato().equals(StatoFunzionalita.ABILITATO)) {
					corsStato = true;

					corsTipo = configurazioneAggiornata.getTipo();
					if(configurazioneAggiornata.getTipo() != null && configurazioneAggiornata.getTipo().equals(TipoGestioneCORS.GATEWAY)) {

						corsAllAllowOrigins = true;
						if(configurazioneAggiornata.getAccessControlAllAllowOrigins() != null && configurazioneAggiornata.getAccessControlAllAllowOrigins().equals(StatoFunzionalita.DISABILITATO)) {
							corsAllAllowOrigins = false;

							if(configurazioneAggiornata.getAccessControlAllowOrigins() != null) {
								corsAllowOrigins = StringUtils.join(configurazioneAggiornata.getAccessControlAllowOrigins().getOriginList(), ",");
							}
						}

						corsAllAllowHeaders = false;
						if(configurazioneAggiornata.getAccessControlAllAllowHeaders() != null && configurazioneAggiornata.getAccessControlAllAllowHeaders().equals(StatoFunzionalita.ABILITATO)) {
							corsAllAllowHeaders = true;
						}
						if(!corsAllAllowHeaders &&
							configurazioneAggiornata.getAccessControlAllowHeaders() != null) {
							corsAllowHeaders = StringUtils.join(configurazioneAggiornata.getAccessControlAllowHeaders().getHeaderList(), ",");
						}

						corsAllAllowMethods = false;
						if(configurazioneAggiornata.getAccessControlAllAllowMethods() != null && configurazioneAggiornata.getAccessControlAllAllowMethods().equals(StatoFunzionalita.ABILITATO)) {
							corsAllAllowMethods = true;
						}
						if(!corsAllAllowMethods &&
							configurazioneAggiornata.getAccessControlAllowMethods() != null) {
							corsAllowMethods = StringUtils.join(configurazioneAggiornata.getAccessControlAllowMethods().getMethodList(), ",");
						}

						if(configurazioneAggiornata.getAccessControlExposeHeaders() != null) {
							corsExposeHeaders = StringUtils.join(configurazioneAggiornata.getAccessControlExposeHeaders().getHeaderList(), ",");
						}

						corsAllowCredential = false;
						if(configurazioneAggiornata.getAccessControlAllowCredentials() != null && configurazioneAggiornata.getAccessControlAllowCredentials().equals(StatoFunzionalita.ABILITATO)) {
							corsAllowCredential = true;
						}

						corsMaxAge = false;
						corsMaxAgeSeconds = -1;
						if(configurazioneAggiornata.getAccessControlMaxAge() != null ) {
							corsMaxAge = true;
							corsMaxAgeSeconds = configurazioneAggiornata.getAccessControlMaxAge();
						}
					}

				}
			}

			porteApplicativeHelper.addConfigurazioneCorsPorteToDati(tipoOperazione, dati, showStato, statoCorsPorta,
					corsStato, corsTipo, corsAllAllowOrigins, corsAllAllowHeaders, corsAllAllowMethods, 
					corsAllowHeaders, corsAllowOrigins, corsAllowMethods, corsAllowCredential, corsExposeHeaders, corsMaxAge, corsMaxAgeSeconds);

			dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, idAsps, dati);

			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_GESTIONE_CORS, ForwardParams.OTHER(""));
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_GESTIONE_CORS, ForwardParams.OTHER(""));
		}
	}

}
