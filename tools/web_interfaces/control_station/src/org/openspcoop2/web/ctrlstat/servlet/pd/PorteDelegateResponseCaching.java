/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneGenerale;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
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

			ResponseCachingConfigurazione oldConfigurazione = portaDelegata.getResponseCaching();
			
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
						
						responseCachingDigestUrlInvocazione = false;
						responseCachingDigestHeaders = false;
						responseCachingDigestPayload = false;
						configurazioneTmp.setHashGenerator(new ResponseCachingConfigurazioneHashGenerator());
						if(configurazioneTmp.getHashGenerator() != null) {
							if(configurazioneTmp.getHashGenerator().getHeaders() != null && configurazioneTmp.getHashGenerator().getHeaders().equals(StatoFunzionalita.ABILITATO)) 
								responseCachingDigestHeaders = true;
							
							if(configurazioneTmp.getHashGenerator().getRequestUri() != null && configurazioneTmp.getHashGenerator().getRequestUri().equals(StatoFunzionalita.ABILITATO)) 
								responseCachingDigestUrlInvocazione = true;
							
							if(configurazioneTmp.getHashGenerator().getPayload() != null && configurazioneTmp.getHashGenerator().getPayload().equals(StatoFunzionalita.ABILITATO)) 
								responseCachingDigestPayload = true;
						}
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
							
							responseCachingDigestUrlInvocazione = false;
							responseCachingDigestHeaders = false;
							responseCachingDigestPayload = false;
							if(oldConfigurazione.getHashGenerator() != null) {
								if(oldConfigurazione.getHashGenerator().getHeaders() != null && oldConfigurazione.getHashGenerator().getHeaders().equals(StatoFunzionalita.ABILITATO)) 
									responseCachingDigestHeaders = true;
								
								if(oldConfigurazione.getHashGenerator().getRequestUri() != null && oldConfigurazione.getHashGenerator().getRequestUri().equals(StatoFunzionalita.ABILITATO)) 
									responseCachingDigestUrlInvocazione = true;
								
								if(oldConfigurazione.getHashGenerator().getPayload() != null && oldConfigurazione.getHashGenerator().getPayload().equals(StatoFunzionalita.ABILITATO)) 
									responseCachingDigestPayload = true;
							}
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
								
								responseCachingDigestUrlInvocazione = false;
								responseCachingDigestHeaders = false;
								responseCachingDigestPayload = false;
								if(oldConfigurazione.getHashGenerator() != null) {
									if(oldConfigurazione.getHashGenerator().getHeaders() != null && oldConfigurazione.getHashGenerator().getHeaders().equals(StatoFunzionalita.ABILITATO)) 
										responseCachingDigestHeaders = true;
									
									if(oldConfigurazione.getHashGenerator().getRequestUri() != null && oldConfigurazione.getHashGenerator().getRequestUri().equals(StatoFunzionalita.ABILITATO)) 
										responseCachingDigestUrlInvocazione = true;
									
									if(oldConfigurazione.getHashGenerator().getPayload() != null && oldConfigurazione.getHashGenerator().getPayload().equals(StatoFunzionalita.ABILITATO)) 
										responseCachingDigestPayload = true;
								}
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
								
								responseCachingDigestUrlInvocazione = false;
								responseCachingDigestHeaders = false;
								responseCachingDigestPayload = false;
								configurazioneTmp.setHashGenerator(new ResponseCachingConfigurazioneHashGenerator());
								if(configurazioneTmp.getHashGenerator() != null) {
									if(configurazioneTmp.getHashGenerator().getHeaders() != null && configurazioneTmp.getHashGenerator().getHeaders().equals(StatoFunzionalita.ABILITATO)) 
										responseCachingDigestHeaders = true;
									
									if(configurazioneTmp.getHashGenerator().getRequestUri() != null && configurazioneTmp.getHashGenerator().getRequestUri().equals(StatoFunzionalita.ABILITATO)) 
										responseCachingDigestUrlInvocazione = true;
									
									if(configurazioneTmp.getHashGenerator().getPayload() != null && configurazioneTmp.getHashGenerator().getPayload().equals(StatoFunzionalita.ABILITATO)) 
										responseCachingDigestPayload = true;
								}
							}
						}
					}
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				porteDelegateHelper.addConfigurazioneResponseCachingPorteToDati(tipoOperazione, dati, showStato, statoResponseCachingPorta,
						responseCachingEnabled, responseCachingSeconds, responseCachingMaxResponseSize, responseCachingMaxResponseSizeBytes, responseCachingDigestUrlInvocazione, responseCachingDigestHeaders, responseCachingDigestPayload);
				
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
						responseCachingEnabled, responseCachingSeconds, responseCachingMaxResponseSize, responseCachingMaxResponseSizeBytes, responseCachingDigestUrlInvocazione, responseCachingDigestHeaders, responseCachingDigestPayload);
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_RESPONSE_CACHING,	ForwardParams.OTHER(""));
			}
			
			ResponseCachingConfigurazione newConfigurazione = null;
			if(statoResponseCachingPorta.equals(CostantiControlStation.VALUE_PARAMETRO_RESPONSE_CACHING_STATO_RIDEFINITO)) {
				newConfigurazione = porteDelegateHelper.getResponseCaching(responseCachingEnabled, responseCachingSeconds, responseCachingMaxResponseSize, responseCachingMaxResponseSizeBytes, responseCachingDigestUrlInvocazione, responseCachingDigestHeaders, responseCachingDigestPayload);
			}
			
			portaDelegata.setResponseCaching(newConfigurazione);

			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), portaDelegata);
		
			// Preparo la lista
			pd.setMessage(PorteDelegateCostanti.LABEL_PORTE_DELEGATE_RESPONSE_CACHING_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO); 

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
						
						responseCachingDigestUrlInvocazione = false;
						responseCachingDigestHeaders = false;
						responseCachingDigestPayload = false;
						if(configurazioneAggiornata.getHashGenerator() != null) {
							if(configurazioneAggiornata.getHashGenerator().getHeaders() != null && configurazioneAggiornata.getHashGenerator().getHeaders().equals(StatoFunzionalita.ABILITATO)) 
								responseCachingDigestHeaders = true;
							
							if(configurazioneAggiornata.getHashGenerator().getRequestUri() != null && configurazioneAggiornata.getHashGenerator().getRequestUri().equals(StatoFunzionalita.ABILITATO)) 
								responseCachingDigestUrlInvocazione = true;
							
							if(configurazioneAggiornata.getHashGenerator().getPayload() != null && configurazioneAggiornata.getHashGenerator().getPayload().equals(StatoFunzionalita.ABILITATO)) 
								responseCachingDigestPayload = true;
						}
					}
				}
			}
			
			porteDelegateHelper.addConfigurazioneResponseCachingPorteToDati(tipoOperazione, dati, showStato, statoResponseCachingPorta,
					responseCachingEnabled, responseCachingSeconds, responseCachingMaxResponseSize, responseCachingMaxResponseSizeBytes, responseCachingDigestUrlInvocazione, responseCachingDigestHeaders, responseCachingDigestPayload);
			
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
