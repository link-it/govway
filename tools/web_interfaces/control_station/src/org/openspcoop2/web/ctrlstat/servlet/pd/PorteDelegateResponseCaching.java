/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneControl;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneGenerale;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaCacheDigestQueryParameter;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * PorteDelegateResponseCaching
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteDelegateResponseCaching extends Action {

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
			
			String idTab = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!porteDelegateHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}

			// Preparo il menu
			porteDelegateHelper.makeMenu();
			
			// Prendo il nome della porta
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			ConfigurazioneCore confCore = new ConfigurazioneCore(porteDelegateCore);

			PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			String idporta = portaDelegata.getNome();
			
			boolean showStato = true;
			String statoResponseCachingPorta = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_STATO_PORTA);
			String responseCachingEnabledTmp = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_STATO);
			boolean responseCachingEnabled = ServletUtils.isCheckBoxEnabled(responseCachingEnabledTmp);
			
			String responseCachingSecondsTmp = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_TIMEOUT);
			int responseCachingSeconds = 1;
			if(responseCachingSecondsTmp != null) {
				try {
					responseCachingSeconds = Integer.parseInt(responseCachingSecondsTmp);
				}catch(Exception e) {} 
			}
			
			
			String responseCachingMaxResponseSizeTmp = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_MAX_RESPONSE_SIZE);
			boolean responseCachingMaxResponseSize = ServletUtils.isCheckBoxEnabled(responseCachingMaxResponseSizeTmp);
			
			String responseCachingMaxResponseSizeBytesTmp = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_MAX_RESPONSE_SIZE_BYTES);
			long responseCachingMaxResponseSizeBytes = 1;
			if(responseCachingMaxResponseSizeBytesTmp != null) {
				try {
					responseCachingMaxResponseSizeBytes = Integer.parseInt(responseCachingMaxResponseSizeBytesTmp);
				}catch(Exception e) {} 
			}
			String responseCachingDigestUrlInvocazioneTmp = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_URI_INVOCAZIONE);
			boolean responseCachingDigestUrlInvocazione = ServletUtils.isCheckBoxEnabled(responseCachingDigestUrlInvocazioneTmp);
			String responseCachingDigestHeadersTmp = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_HEADERS);
			boolean responseCachingDigestHeaders = ServletUtils.isCheckBoxEnabled(responseCachingDigestHeadersTmp);
			String responseCachingDigestPayloadTmp = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_PAYLOAD);
			boolean responseCachingDigestPayload = ServletUtils.isCheckBoxEnabled(responseCachingDigestPayloadTmp);
			String responseCachingDigestHeadersNomiHeaders = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_HEADERS_NOMI_HEADERS);
			StatoFunzionalitaCacheDigestQueryParameter responseCachingDigestQueryParameter = null;
			String responseCachingDigestQueryParameterTmp = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_QUERY_PARAMETERS);
			if(responseCachingDigestQueryParameterTmp!=null && !"".equals(responseCachingDigestQueryParameterTmp)) {
				responseCachingDigestQueryParameter = StatoFunzionalitaCacheDigestQueryParameter.toEnumConstant(responseCachingDigestQueryParameterTmp, true);
			}
			String responseCachingDigestNomiParametriQuery = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_QUERY_PARAMETERS_NOMI);
			
			
			String responseCachingCacheControlNoCacheTmp = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CACHE_CONTROL_NO_CACHE);
			boolean responseCachingCacheControlNoCache = ServletUtils.isCheckBoxEnabled(responseCachingCacheControlNoCacheTmp);
			String responseCachingCacheControlMaxAgeTmp = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CACHE_CONTROL_MAX_AGE);
			boolean responseCachingCacheControlMaxAge = ServletUtils.isCheckBoxEnabled(responseCachingCacheControlMaxAgeTmp);
			String responseCachingCacheControlNoStoreTmp = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CACHE_CONTROL_NO_STORE);
			boolean responseCachingCacheControlNoStore = ServletUtils.isCheckBoxEnabled(responseCachingCacheControlNoStoreTmp);
			
			String servletResponseCachingConfigurazioneRegolaList = PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_LIST;
			List<Parameter> paramsResponseCachingConfigurazioneRegolaList = new ArrayList<Parameter>();
			paramsResponseCachingConfigurazioneRegolaList.add(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id));
			paramsResponseCachingConfigurazioneRegolaList.add(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idSoggFruitore));
			paramsResponseCachingConfigurazioneRegolaList.add(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps));
			paramsResponseCachingConfigurazioneRegolaList.add(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione));

			ResponseCachingConfigurazione oldConfigurazione = portaDelegata.getResponseCaching();
			boolean visualizzaLinkConfigurazioneRegola = porteDelegateHelper.isResponseCachingAbilitato(oldConfigurazione) && responseCachingEnabled;
			List<ResponseCachingConfigurazioneRegola> listaRegoleCachingConfigurazione = oldConfigurazione != null ?  oldConfigurazione.getRegolaList() : null;
			int numeroResponseCachingConfigurazioneRegola = porteDelegateHelper.numeroRegoleResponseCaching(oldConfigurazione);
			
			boolean initConfigurazione = false;
			String postBackElementName = porteDelegateHelper.getPostBackElementName();
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_STATO_PORTA)){
					initConfigurazione = true;
				}
				
				if(postBackElementName.equalsIgnoreCase(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_STATO)){
					if(!porteDelegateHelper.isResponseCachingAbilitato(oldConfigurazione) && responseCachingEnabled) {
						ResponseCachingConfigurazione configurazioneTmp = new ResponseCachingConfigurazione();
						
						responseCachingSeconds = configurazioneTmp.getCacheTimeoutSeconds() != null ? configurazioneTmp.getCacheTimeoutSeconds().intValue() : 1;
						
						responseCachingMaxResponseSize = false;
						if(configurazioneTmp.getMaxMessageSize() != null) {
							responseCachingMaxResponseSize = true;
							responseCachingMaxResponseSizeBytes = configurazioneTmp.getMaxMessageSize().longValue();
						}
						
						responseCachingDigestUrlInvocazione = true;
						responseCachingDigestQueryParameter = StatoFunzionalitaCacheDigestQueryParameter.ABILITATO;
						responseCachingDigestHeaders = false;
						responseCachingDigestPayload = true;
						configurazioneTmp.setHashGenerator(new ResponseCachingConfigurazioneHashGenerator());
						if(configurazioneTmp.getHashGenerator() != null) {
							
							if(configurazioneTmp.getHashGenerator().getQueryParameters() != null)
								responseCachingDigestQueryParameter = configurazioneTmp.getHashGenerator().getQueryParameters();
							
							if(configurazioneTmp.getHashGenerator().getQueryParameterList() != null)  
								responseCachingDigestNomiParametriQuery = StringUtils.join(configurazioneTmp.getHashGenerator().getQueryParameterList(), ",");
							
							if(configurazioneTmp.getHashGenerator().getHeaders() != null)  
								responseCachingDigestHeaders = configurazioneTmp.getHashGenerator().getHeaders().equals(StatoFunzionalita.ABILITATO);
							
							if(configurazioneTmp.getHashGenerator().getHeaderList() != null)  
								responseCachingDigestHeadersNomiHeaders = StringUtils.join(configurazioneTmp.getHashGenerator().getHeaderList(), ",");
							
							if(configurazioneTmp.getHashGenerator().getRequestUri() != null) 
								responseCachingDigestUrlInvocazione = configurazioneTmp.getHashGenerator().getRequestUri().equals(StatoFunzionalita.ABILITATO);
							
							if(configurazioneTmp.getHashGenerator().getPayload() != null) 
								responseCachingDigestPayload = configurazioneTmp.getHashGenerator().getPayload().equals(StatoFunzionalita.ABILITATO);
						}
						
						responseCachingCacheControlNoCache = true;
						responseCachingCacheControlMaxAge = true;
						responseCachingCacheControlNoStore = true;
						configurazioneTmp.setControl(new ResponseCachingConfigurazioneControl());
						if(configurazioneTmp.getControl() != null) {
							responseCachingCacheControlNoCache = configurazioneTmp.getControl().isNoCache();
							responseCachingCacheControlMaxAge = configurazioneTmp.getControl().isMaxAge();
							responseCachingCacheControlNoStore = configurazioneTmp.getControl().isNoStore();
						}
						
						numeroResponseCachingConfigurazioneRegola= configurazioneTmp.sizeRegolaList();
					}
				}
			}
			
			// setto la barra del titolo
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idSoggFruitore, idAsps, idFruizione);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RESPONSE_CACHING_CONFIGURAZIONE_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RESPONSE_CACHING_CONFIGURAZIONE,
						portaDelegata);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RESPONSE_CACHING_CONFIGURAZIONE_CONFIG_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta,  null));
			
			// edit in progress
			if (porteDelegateHelper.isEditModeInProgress()) {
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				if(statoResponseCachingPorta == null) {
					if(oldConfigurazione == null) {
						statoResponseCachingPorta = CostantiControlStation.VALUE_PARAMETRO_RESPONSE_CACHING_STATO_DEFAULT;  
					} else {
						statoResponseCachingPorta = CostantiControlStation.VALUE_PARAMETRO_RESPONSE_CACHING_STATO_RIDEFINITO;
						responseCachingEnabled = false;
						if(oldConfigurazione != null && oldConfigurazione.getStato().equals(StatoFunzionalita.ABILITATO)) {
							responseCachingEnabled = true;
							
							responseCachingSeconds = oldConfigurazione.getCacheTimeoutSeconds() != null ? oldConfigurazione.getCacheTimeoutSeconds().intValue() : 1;
							
							responseCachingMaxResponseSize = false;
							if(oldConfigurazione.getMaxMessageSize() != null) {
								responseCachingMaxResponseSize = true;
								responseCachingMaxResponseSizeBytes = oldConfigurazione.getMaxMessageSize().longValue();
							}
							
							responseCachingDigestUrlInvocazione = true;
							responseCachingDigestQueryParameter = StatoFunzionalitaCacheDigestQueryParameter.ABILITATO;
							responseCachingDigestHeaders = false;
							responseCachingDigestPayload = true;
							responseCachingDigestHeadersNomiHeaders = "";
							if(oldConfigurazione.getHashGenerator() != null) {
								
								if(oldConfigurazione.getHashGenerator().getQueryParameters() != null)
									responseCachingDigestQueryParameter = oldConfigurazione.getHashGenerator().getQueryParameters();
								
								if(oldConfigurazione.getHashGenerator().getQueryParameterList() != null)  
									responseCachingDigestNomiParametriQuery = StringUtils.join(oldConfigurazione.getHashGenerator().getQueryParameterList(), ",");
								
								if(oldConfigurazione.getHashGenerator().getHeaders() != null)  
									responseCachingDigestHeaders = oldConfigurazione.getHashGenerator().getHeaders().equals(StatoFunzionalita.ABILITATO);
								
								if(oldConfigurazione.getHashGenerator().getHeaderList() != null)  
									responseCachingDigestHeadersNomiHeaders = StringUtils.join(oldConfigurazione.getHashGenerator().getHeaderList(), ",");
								
								if(oldConfigurazione.getHashGenerator().getRequestUri() != null) 
									responseCachingDigestUrlInvocazione = oldConfigurazione.getHashGenerator().getRequestUri().equals(StatoFunzionalita.ABILITATO);
								
								if(oldConfigurazione.getHashGenerator().getPayload() != null) 
									responseCachingDigestPayload = oldConfigurazione.getHashGenerator().getPayload().equals(StatoFunzionalita.ABILITATO);
							}
							
							responseCachingCacheControlNoCache = true;
							responseCachingCacheControlMaxAge = true;
							responseCachingCacheControlNoStore = true;
							if(oldConfigurazione.getControl() != null) {
								responseCachingCacheControlNoCache = oldConfigurazione.getControl().isNoCache();
								responseCachingCacheControlMaxAge = oldConfigurazione.getControl().isMaxAge();
								responseCachingCacheControlNoStore = oldConfigurazione.getControl().isNoStore();
							}
							
							//visualizzaLinkConfigurazioneRegola = true;
							visualizzaLinkConfigurazioneRegola = porteDelegateHelper.isResponseCachingAbilitato(oldConfigurazione) && responseCachingEnabled;
							listaRegoleCachingConfigurazione = oldConfigurazione.getRegolaList();
							numeroResponseCachingConfigurazioneRegola = porteDelegateHelper.numeroRegoleResponseCaching(oldConfigurazione);
						}
					}
				}
				
				if(initConfigurazione) {
					if(statoResponseCachingPorta.equals(CostantiControlStation.VALUE_PARAMETRO_RESPONSE_CACHING_STATO_RIDEFINITO)) {
						Configurazione configurazione = confCore.getConfigurazioneGenerale();
						ResponseCachingConfigurazioneGenerale responseCachingGenerale = configurazione.getResponseCaching();
						responseCachingEnabled = false;
						if(responseCachingGenerale != null) {
							oldConfigurazione = responseCachingGenerale.getConfigurazione();
							if(oldConfigurazione != null && oldConfigurazione.getStato().equals(StatoFunzionalita.ABILITATO)) {
								responseCachingEnabled = true;
								
								responseCachingSeconds = oldConfigurazione.getCacheTimeoutSeconds() != null ? oldConfigurazione.getCacheTimeoutSeconds().intValue() : 1;
								
								responseCachingMaxResponseSize = false;
								if(oldConfigurazione.getMaxMessageSize() != null) {
									responseCachingMaxResponseSize = true;
									responseCachingMaxResponseSizeBytes = oldConfigurazione.getMaxMessageSize().longValue();
								}
								
								responseCachingDigestUrlInvocazione = true;
								responseCachingDigestQueryParameter = StatoFunzionalitaCacheDigestQueryParameter.ABILITATO;
								responseCachingDigestHeaders = false;
								responseCachingDigestPayload = true;
								if(oldConfigurazione.getHashGenerator() != null) {
									
									if(oldConfigurazione.getHashGenerator().getQueryParameters() != null)
										responseCachingDigestQueryParameter = oldConfigurazione.getHashGenerator().getQueryParameters();
									
									if(oldConfigurazione.getHashGenerator().getQueryParameterList() != null)  
										responseCachingDigestNomiParametriQuery = StringUtils.join(oldConfigurazione.getHashGenerator().getQueryParameterList(), ",");
									
									if(oldConfigurazione.getHashGenerator().getHeaders() != null)  
										responseCachingDigestHeaders = oldConfigurazione.getHashGenerator().getHeaders().equals(StatoFunzionalita.ABILITATO);
									
									if(oldConfigurazione.getHashGenerator().getHeaderList() != null)  
										responseCachingDigestHeadersNomiHeaders = StringUtils.join(oldConfigurazione.getHashGenerator().getHeaderList(), ",");
									
									if(oldConfigurazione.getHashGenerator().getRequestUri() != null) 
										responseCachingDigestUrlInvocazione = oldConfigurazione.getHashGenerator().getRequestUri().equals(StatoFunzionalita.ABILITATO);
									
									if(oldConfigurazione.getHashGenerator().getPayload() != null) 
										responseCachingDigestPayload = oldConfigurazione.getHashGenerator().getPayload().equals(StatoFunzionalita.ABILITATO);
								}
								
								responseCachingCacheControlNoCache = true;
								responseCachingCacheControlMaxAge = true;
								responseCachingCacheControlNoStore = true;
								if(oldConfigurazione.getControl() != null) {
									responseCachingCacheControlNoCache = oldConfigurazione.getControl().isNoCache();
									responseCachingCacheControlMaxAge = oldConfigurazione.getControl().isMaxAge();
									responseCachingCacheControlNoStore = oldConfigurazione.getControl().isNoStore();
								}
								
								//numeroResponseCachingConfigurazioneRegola= oldConfigurazione.sizeRegolaList();
							}
							else {
								
								responseCachingEnabled = true;
								
								ResponseCachingConfigurazione configurazioneTmp = new ResponseCachingConfigurazione();
								
								responseCachingSeconds = configurazioneTmp.getCacheTimeoutSeconds() != null ? configurazioneTmp.getCacheTimeoutSeconds().intValue() : 1;
								
								responseCachingMaxResponseSize = false;
								if(configurazioneTmp.getMaxMessageSize() != null) {
									responseCachingMaxResponseSize = true;
									responseCachingMaxResponseSizeBytes = configurazioneTmp.getMaxMessageSize().longValue();
								}
								
								responseCachingDigestUrlInvocazione = true;
								responseCachingDigestQueryParameter = StatoFunzionalitaCacheDigestQueryParameter.ABILITATO;
								responseCachingDigestHeaders = false;
								responseCachingDigestPayload = true;
								configurazioneTmp.setHashGenerator(new ResponseCachingConfigurazioneHashGenerator());
								if(configurazioneTmp.getHashGenerator() != null) {
									
									if(configurazioneTmp.getHashGenerator().getQueryParameters() != null)
										responseCachingDigestQueryParameter = configurazioneTmp.getHashGenerator().getQueryParameters();
									
									if(configurazioneTmp.getHashGenerator().getQueryParameterList() != null)  
										responseCachingDigestNomiParametriQuery = StringUtils.join(configurazioneTmp.getHashGenerator().getQueryParameterList(), ",");
									
									if(configurazioneTmp.getHashGenerator().getHeaders() != null)  
										responseCachingDigestHeaders = configurazioneTmp.getHashGenerator().getHeaders().equals(StatoFunzionalita.ABILITATO);
									
									if(configurazioneTmp.getHashGenerator().getHeaderList() != null)  
										responseCachingDigestHeadersNomiHeaders = StringUtils.join(configurazioneTmp.getHashGenerator().getHeaderList(), ",");
									
									if(configurazioneTmp.getHashGenerator().getRequestUri() != null) 
										responseCachingDigestUrlInvocazione = configurazioneTmp.getHashGenerator().getRequestUri().equals(StatoFunzionalita.ABILITATO);
									
									if(configurazioneTmp.getHashGenerator().getPayload() != null) 
										responseCachingDigestPayload = configurazioneTmp.getHashGenerator().getPayload().equals(StatoFunzionalita.ABILITATO);
								}
								
								responseCachingCacheControlNoCache = true;
								responseCachingCacheControlMaxAge = true;
								responseCachingCacheControlNoStore = true;
								configurazioneTmp.setControl(new ResponseCachingConfigurazioneControl());
								if(configurazioneTmp.getControl() != null) {
									responseCachingCacheControlNoCache = configurazioneTmp.getControl().isNoCache();
									responseCachingCacheControlMaxAge = configurazioneTmp.getControl().isMaxAge();
									responseCachingCacheControlNoStore = configurazioneTmp.getControl().isNoStore();
								}
								
								visualizzaLinkConfigurazioneRegola = false;
								listaRegoleCachingConfigurazione = configurazioneTmp.getRegolaList();
								numeroResponseCachingConfigurazioneRegola = porteDelegateHelper.numeroRegoleResponseCaching(configurazioneTmp);
							}
						}
					}
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				porteDelegateHelper.addConfigurazioneResponseCachingPorteToDati(tipoOperazione, dati, showStato, statoResponseCachingPorta,
						responseCachingEnabled, responseCachingSeconds, responseCachingMaxResponseSize, responseCachingMaxResponseSizeBytes,
						responseCachingDigestUrlInvocazione, responseCachingDigestHeaders, responseCachingDigestPayload, responseCachingDigestHeadersNomiHeaders, responseCachingDigestQueryParameter, responseCachingDigestNomiParametriQuery,
						responseCachingCacheControlNoCache, responseCachingCacheControlMaxAge, responseCachingCacheControlNoStore, visualizzaLinkConfigurazioneRegola,
						servletResponseCachingConfigurazioneRegolaList, paramsResponseCachingConfigurazioneRegolaList, numeroResponseCachingConfigurazioneRegola );
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_RESPONSE_CACHING,	ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.checkDataConfigurazioneResponseCachingPorta(tipoOperazione, showStato, statoResponseCachingPorta);
			if (!isOk) {

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				porteDelegateHelper.addConfigurazioneResponseCachingPorteToDati(tipoOperazione, dati, showStato, statoResponseCachingPorta,
						responseCachingEnabled, responseCachingSeconds, responseCachingMaxResponseSize, responseCachingMaxResponseSizeBytes,
						responseCachingDigestUrlInvocazione, responseCachingDigestHeaders, responseCachingDigestPayload, responseCachingDigestHeadersNomiHeaders, responseCachingDigestQueryParameter, responseCachingDigestNomiParametriQuery,
						responseCachingCacheControlNoCache, responseCachingCacheControlMaxAge, responseCachingCacheControlNoStore, visualizzaLinkConfigurazioneRegola,
						servletResponseCachingConfigurazioneRegolaList, paramsResponseCachingConfigurazioneRegolaList, numeroResponseCachingConfigurazioneRegola );
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_RESPONSE_CACHING,	ForwardParams.OTHER(""));
			}
			
			ResponseCachingConfigurazione newConfigurazione = null;
			if(statoResponseCachingPorta.equals(CostantiControlStation.VALUE_PARAMETRO_RESPONSE_CACHING_STATO_RIDEFINITO)) {
				newConfigurazione = porteDelegateHelper.getResponseCaching(responseCachingEnabled, responseCachingSeconds, responseCachingMaxResponseSize, 
						responseCachingMaxResponseSizeBytes, responseCachingDigestUrlInvocazione, responseCachingDigestHeaders, responseCachingDigestPayload, responseCachingDigestHeadersNomiHeaders, responseCachingDigestQueryParameter, responseCachingDigestNomiParametriQuery,
						responseCachingCacheControlNoCache, responseCachingCacheControlMaxAge, responseCachingCacheControlNoStore,listaRegoleCachingConfigurazione);
			}
			
			portaDelegata.setResponseCaching(newConfigurazione);

			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), portaDelegata);
		
			// Preparo la lista
			pd.setMessage( ServletUtils.getMessageFromResourceBundle( session, "PorteDelegate.PorteDelegateResponseCachingConSuccesso" ) , Costanti.MESSAGE_TYPE_INFO); 	//PorteDelegateCostanti.LABEL_PORTE_DELEGATE_RESPONSE_CACHING_CON_SUCCESSO

			portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			idporta = portaDelegata.getNome();

			ServletUtils.setPageDataTitle(pd, lstParam);

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();
			dati.addElement(ServletUtils.getDataElementForEditModeFinished());
			
			ResponseCachingConfigurazione configurazioneAggiornata  = portaDelegata.getResponseCaching();
			
			if(configurazioneAggiornata == null) {
				statoResponseCachingPorta = CostantiControlStation.VALUE_PARAMETRO_RESPONSE_CACHING_STATO_DEFAULT;
			} else {
				statoResponseCachingPorta = CostantiControlStation.VALUE_PARAMETRO_RESPONSE_CACHING_STATO_RIDEFINITO;
				if(configurazioneAggiornata.getStato() != null && configurazioneAggiornata.getStato().equals(StatoFunzionalita.ABILITATO)) {
					responseCachingEnabled = false;
					if(configurazioneAggiornata != null && configurazioneAggiornata.getStato().equals(StatoFunzionalita.ABILITATO)) {
						responseCachingEnabled = true;
						
						responseCachingSeconds = configurazioneAggiornata.getCacheTimeoutSeconds() != null ? configurazioneAggiornata.getCacheTimeoutSeconds().intValue() : 1;
						
						responseCachingMaxResponseSize = false;
						if(configurazioneAggiornata.getMaxMessageSize() != null) {
							responseCachingMaxResponseSize = true;
							responseCachingMaxResponseSizeBytes = configurazioneAggiornata.getMaxMessageSize().longValue();
						}
						
						responseCachingDigestUrlInvocazione = true;
						responseCachingDigestQueryParameter = StatoFunzionalitaCacheDigestQueryParameter.ABILITATO;
						responseCachingDigestHeaders = false;
						responseCachingDigestPayload = true;
						if(configurazioneAggiornata.getHashGenerator() != null) {
							
							if(configurazioneAggiornata.getHashGenerator().getQueryParameters() != null)
								responseCachingDigestQueryParameter = configurazioneAggiornata.getHashGenerator().getQueryParameters();
							
							if(configurazioneAggiornata.getHashGenerator().getQueryParameterList() != null)  
								responseCachingDigestNomiParametriQuery = StringUtils.join(configurazioneAggiornata.getHashGenerator().getQueryParameterList(), ",");
							
							if(configurazioneAggiornata.getHashGenerator().getHeaders() != null)  
								responseCachingDigestHeaders = configurazioneAggiornata.getHashGenerator().getHeaders().equals(StatoFunzionalita.ABILITATO);
							
							if(configurazioneAggiornata.getHashGenerator().getHeaderList() != null)  
								responseCachingDigestHeadersNomiHeaders = StringUtils.join(configurazioneAggiornata.getHashGenerator().getHeaderList(), ",");
							
							if(configurazioneAggiornata.getHashGenerator().getRequestUri() != null) 
								responseCachingDigestUrlInvocazione = configurazioneAggiornata.getHashGenerator().getRequestUri().equals(StatoFunzionalita.ABILITATO);
							
							if(configurazioneAggiornata.getHashGenerator().getPayload() != null) 
								responseCachingDigestPayload = configurazioneAggiornata.getHashGenerator().getPayload().equals(StatoFunzionalita.ABILITATO);
						}
						
						responseCachingCacheControlNoCache = true;
						responseCachingCacheControlMaxAge = true;
						responseCachingCacheControlNoStore = true;
						if(configurazioneAggiornata.getControl() != null) {
							responseCachingCacheControlNoCache = configurazioneAggiornata.getControl().isNoCache();
							responseCachingCacheControlMaxAge = configurazioneAggiornata.getControl().isMaxAge();
							responseCachingCacheControlNoStore = configurazioneAggiornata.getControl().isNoStore();
						}
						
						numeroResponseCachingConfigurazioneRegola= configurazioneAggiornata.sizeRegolaList();
						visualizzaLinkConfigurazioneRegola = true;
					}
				}
			}
			
			porteDelegateHelper.addConfigurazioneResponseCachingPorteToDati(tipoOperazione, dati, showStato, statoResponseCachingPorta,
					responseCachingEnabled, responseCachingSeconds, responseCachingMaxResponseSize, responseCachingMaxResponseSizeBytes, 
					responseCachingDigestUrlInvocazione, responseCachingDigestHeaders, responseCachingDigestPayload, responseCachingDigestHeadersNomiHeaders, responseCachingDigestQueryParameter, responseCachingDigestNomiParametriQuery,
					responseCachingCacheControlNoCache, responseCachingCacheControlMaxAge, responseCachingCacheControlNoStore, visualizzaLinkConfigurazioneRegola,
					servletResponseCachingConfigurazioneRegolaList, paramsResponseCachingConfigurazioneRegolaList, numeroResponseCachingConfigurazioneRegola );
			
			dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
					idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);

			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_RESPONSE_CACHING, ForwardParams.OTHER(""));
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_RESPONSE_CACHING, ForwardParams.OTHER(""));
		}
	}

}
