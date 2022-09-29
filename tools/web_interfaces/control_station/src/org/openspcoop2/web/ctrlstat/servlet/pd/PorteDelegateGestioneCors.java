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
package org.openspcoop2.web.ctrlstat.servlet.pd;

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
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.CorsConfigurazioneHeaders;
import org.openspcoop2.core.config.CorsConfigurazioneMethods;
import org.openspcoop2.core.config.CorsConfigurazioneOrigin;
import org.openspcoop2.core.config.PortaDelegata;
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

/**
 * PorteDelegateGestioneCors
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteDelegateGestioneCors extends Action {

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
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session, request);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
			String id = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idSoggFruitore = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			String idFruizione= porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null) 
				idFruizione = "";

			// Preparo il menu
			porteDelegateHelper.makeMenu();
			
			// Prendo il nome della porta
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			ConfigurazioneCore confCore = new ConfigurazioneCore(porteDelegateCore);

			PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			String idporta = portaDelegata.getNome();
			
			boolean showStato = true;
			String statoCorsPorta = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_STATO_PORTA);
			String statoCorsTmp = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_STATO);
			boolean corsStato = ServletUtils.isCheckBoxEnabled(statoCorsTmp); 
			String corsTipoTmp = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_TIPO);
			TipoGestioneCORS corsTipo = corsTipoTmp != null ? TipoGestioneCORS.toEnumConstant(corsTipoTmp) : TipoGestioneCORS.GATEWAY;
			String corsAllAllowOriginsTmp = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_ORIGINS);
			boolean corsAllAllowOrigins = ServletUtils.isCheckBoxEnabled(corsAllAllowOriginsTmp);
			String corsAllAllowHeadersTmp = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_HEADERS);
			boolean corsAllAllowHeaders = ServletUtils.isCheckBoxEnabled(corsAllAllowHeadersTmp);
			String corsAllAllowMethodsTmp = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_METHODS);
			boolean corsAllAllowMethods = ServletUtils.isCheckBoxEnabled(corsAllAllowMethodsTmp);
			String corsAllowHeaders =  porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_HEADERS);
			String corsAllowOrigins =  porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_ORIGINS);
			String corsAllowMethods =  porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_METHODS);
			String corsAllowCredentialTmp = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_CREDENTIALS);
			boolean corsAllowCredential =  ServletUtils.isCheckBoxEnabled(corsAllowCredentialTmp);
			String corsExposeHeaders = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_EXPOSE_HEADERS);
			String corsMaxAgeTmp = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_MAX_AGE);
			boolean corsMaxAge =  ServletUtils.isCheckBoxEnabled(corsMaxAgeTmp);
			String corsMaxAgeSecondsTmp = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_MAX_AGE_SECONDS);
			int corsMaxAgeSeconds = -1;
			if(corsMaxAgeSecondsTmp != null) {
				try {
					corsMaxAgeSeconds = Integer.parseInt(corsMaxAgeSecondsTmp);
				}catch(Exception e) {}
			}

			CorsConfigurazione oldConfigurazione = portaDelegata.getGestioneCors();
			
			boolean initConfigurazione = false;
			String postBackElementName = porteDelegateHelper.getPostBackElementName();
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_STATO_PORTA)){
					initConfigurazione = true;
				}
				
				if(postBackElementName.equalsIgnoreCase(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_STATO)){
					if(!porteDelegateHelper.isCorsAbilitato(oldConfigurazione) && corsStato) {
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
			
			// setto la barra del titolo
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idSoggFruitore, idAsps, idFruizione);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				lstParam.remove(lstParam.size()-1);
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORS_CONFIGURAZIONE_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORS_CONFIGURAZIONE,
						portaDelegata);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORS_CONFIGURAZIONE_CONFIG_DI+idporta;
			}
			
			if(labelPerPorta.contains(CostantiControlStation.LABEL_DEL_GRUPPO)) {
				labelPerPorta = labelPerPorta.substring(0, labelPerPorta.indexOf(CostantiControlStation.LABEL_DEL_GRUPPO));
			}
			
			lstParam.add(new Parameter(labelPerPorta,  null));
			
			// edit in progress
			if (porteDelegateHelper.isEditModeInProgress()) {
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
								if(!corsAllAllowHeaders) {
									if(oldConfigurazione.getAccessControlAllowHeaders() != null) {
										corsAllowHeaders = StringUtils.join(oldConfigurazione.getAccessControlAllowHeaders().getHeaderList(), ",");
									}
								}

								corsAllAllowMethods = false;
								if(oldConfigurazione.getAccessControlAllAllowMethods() != null && oldConfigurazione.getAccessControlAllAllowMethods().equals(StatoFunzionalita.ABILITATO)) {
									corsAllAllowMethods = true;
								}
								if(!corsAllAllowMethods) {
									if(oldConfigurazione.getAccessControlAllowMethods() != null) {
										corsAllowMethods = StringUtils.join(oldConfigurazione.getAccessControlAllowMethods().getMethodList(), ",");
									}
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
									if(!corsAllAllowHeaders) {
										if(oldConfigurazione.getAccessControlAllowHeaders() != null) {
											corsAllowHeaders = StringUtils.join(oldConfigurazione.getAccessControlAllowHeaders().getHeaderList(), ",");
										}
									}

									corsAllAllowMethods = false;
									if(oldConfigurazione.getAccessControlAllAllowMethods() != null && oldConfigurazione.getAccessControlAllAllowMethods().equals(StatoFunzionalita.ABILITATO)) {
										corsAllAllowMethods = true;
									}
									if(!corsAllAllowMethods) {
										if(oldConfigurazione.getAccessControlAllowMethods() != null) {
											corsAllowMethods = StringUtils.join(oldConfigurazione.getAccessControlAllowMethods().getMethodList(), ",");
										}
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
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				porteDelegateHelper.addConfigurazioneCorsPorteToDati(tipoOperazione, dati, showStato, statoCorsPorta,
						corsStato, corsTipo, corsAllAllowOrigins, corsAllAllowHeaders, corsAllAllowMethods, 
						corsAllowHeaders, corsAllowOrigins, corsAllowMethods, corsAllowCredential, corsExposeHeaders, corsMaxAge, corsMaxAgeSeconds);
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_GESTIONE_CORS,	ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.checkDataConfigurazioneCorsPorta(tipoOperazione, showStato, statoCorsPorta);
			if (!isOk) {

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				porteDelegateHelper.addConfigurazioneCorsPorteToDati(tipoOperazione, dati, showStato, statoCorsPorta,
						corsStato, corsTipo, corsAllAllowOrigins, corsAllAllowHeaders, corsAllAllowMethods, 
						corsAllowHeaders, corsAllowOrigins, corsAllowMethods, corsAllowCredential, corsExposeHeaders, corsMaxAge, corsMaxAgeSeconds);
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_GESTIONE_CORS,	ForwardParams.OTHER(""));
			}
			
			CorsConfigurazione newConfigurazione = null;
			if(statoCorsPorta.equals(CostantiControlStation.VALUE_PARAMETRO_CORS_STATO_RIDEFINITO)) {
				// se ho confermato effettuo la modifica altrimenti torno direttamente alla lista
				newConfigurazione = porteDelegateHelper.getGestioneCors(corsStato, corsTipo, corsAllAllowOrigins, corsAllAllowHeaders, corsAllAllowMethods, 
						corsAllowHeaders, corsAllowOrigins, corsAllowMethods, corsAllowCredential, corsExposeHeaders, corsMaxAge, corsMaxAgeSeconds);
			}
			
			portaDelegata.setGestioneCors(newConfigurazione);

			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), portaDelegata);
		
			// Preparo la lista
			pd.setMessage(PorteDelegateCostanti.LABEL_PORTE_DELEGATE_CORS_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);

			portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			idporta = portaDelegata.getNome();

			ServletUtils.setPageDataTitle(pd, lstParam);

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();
			dati.addElement(ServletUtils.getDataElementForEditModeFinished());
			
			CorsConfigurazione configurazioneAggiornata  = portaDelegata.getGestioneCors();
			
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
						if(!corsAllAllowHeaders) {
							if(configurazioneAggiornata.getAccessControlAllowHeaders() != null) {
								corsAllowHeaders = StringUtils.join(configurazioneAggiornata.getAccessControlAllowHeaders().getHeaderList(), ",");
							}
						}

						corsAllAllowMethods = false;
						if(configurazioneAggiornata.getAccessControlAllAllowMethods() != null && configurazioneAggiornata.getAccessControlAllAllowMethods().equals(StatoFunzionalita.ABILITATO)) {
							corsAllAllowMethods = true;
						}
						if(!corsAllAllowMethods) {
							if(configurazioneAggiornata.getAccessControlAllowMethods() != null) {
								corsAllowMethods = StringUtils.join(configurazioneAggiornata.getAccessControlAllowMethods().getMethodList(), ",");
							}
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
			
			porteDelegateHelper.addConfigurazioneCorsPorteToDati(tipoOperazione, dati, showStato, statoCorsPorta,
					corsStato, corsTipo, corsAllAllowOrigins, corsAllAllowHeaders, corsAllAllowMethods, 
					corsAllowHeaders, corsAllowOrigins, corsAllowMethods, corsAllowCredential, corsExposeHeaders, corsMaxAge, corsMaxAgeSeconds);
			
			dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
					idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);

			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_GESTIONE_CORS, ForwardParams.OTHER(""));
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_GESTIONE_CORS, ForwardParams.OTHER(""));
		}
	}

}
