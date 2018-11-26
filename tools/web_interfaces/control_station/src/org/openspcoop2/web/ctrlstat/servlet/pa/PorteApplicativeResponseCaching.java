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
package org.openspcoop2.web.ctrlstat.servlet.pa;

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
import org.openspcoop2.core.config.PortaApplicativa;
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

/***
 * 
 * PorteApplicativeResponseCaching
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteApplicativeResponseCaching extends Action {

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

			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			ConfigurazioneCore confCore = new ConfigurazioneCore(porteApplicativeCore);

			boolean showStato = true;
			String statoResponseCachingPorta = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_STATO_PORTA);
			String responseCachingEnabledTmp = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_STATO);
			boolean responseCachingEnabled = ServletUtils.isCheckBoxEnabled(responseCachingEnabledTmp);
			
			String responseCachingSecondsTmp = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_TIMEOUT);
			int responseCachingSeconds = 1;
			if(responseCachingSecondsTmp != null) {
				try {
					responseCachingSeconds = Integer.parseInt(responseCachingSecondsTmp);
				}catch(Exception e) {} 
			}
			
			
			String responseCachingMaxResponseSizeTmp = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_MAX_RESPONSE_SIZE);
			boolean responseCachingMaxResponseSize = ServletUtils.isCheckBoxEnabled(responseCachingMaxResponseSizeTmp);
			
			String responseCachingMaxResponseSizeBytesTmp = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_MAX_RESPONSE_SIZE_BYTES);
			long responseCachingMaxResponseSizeBytes = 1;
			if(responseCachingMaxResponseSizeBytesTmp != null) {
				try {
					responseCachingMaxResponseSizeBytes = Integer.parseInt(responseCachingMaxResponseSizeBytesTmp);
				}catch(Exception e) {} 
			}
			String responseCachingDigestUrlInvocazioneTmp = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_URI_INVOCAZIONE);
			boolean responseCachingDigestUrlInvocazione = ServletUtils.isCheckBoxEnabled(responseCachingDigestUrlInvocazioneTmp);
			String responseCachingDigestHeadersTmp = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_HEADERS);
			boolean responseCachingDigestHeaders = ServletUtils.isCheckBoxEnabled(responseCachingDigestHeadersTmp);
			String responseCachingDigestPayloadTmp = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_PAYLOAD);
			boolean responseCachingDigestPayload = ServletUtils.isCheckBoxEnabled(responseCachingDigestPayloadTmp);


			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String idporta = pa.getNome();

			ResponseCachingConfigurazione oldConfigurazione = pa.getResponseCaching();

			boolean initConfigurazione = false;
			String postBackElementName = porteApplicativeHelper.getPostBackElementName();
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_STATO_PORTA)){
					initConfigurazione = true;
				}
				
				if(postBackElementName.equalsIgnoreCase(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_STATO)){
					if(!porteApplicativeHelper.isResponseCachingAbilitato(oldConfigurazione) && responseCachingEnabled) {
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

			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RESPONSE_CACHING_CONFIGURAZIONE_CONFIG_DI, 
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RESPONSE_CACHING_CONFIGURAZIONE,
						pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RESPONSE_CACHING_CONFIGURAZIONE_CONFIG_DI+idporta;
			}

			lstParam.add(new Parameter(labelPerPorta,  null));

			// edit in progress
			if (porteApplicativeHelper.isEditModeInProgress()) {
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

				porteApplicativeHelper.addConfigurazioneResponseCachingPorteToDati(tipoOperazione, dati, showStato, statoResponseCachingPorta,
						responseCachingEnabled, responseCachingSeconds, responseCachingMaxResponseSize, responseCachingMaxResponseSizeBytes, responseCachingDigestUrlInvocazione, responseCachingDigestHeaders, responseCachingDigestPayload);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_RESPONSE_CACHING,	ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.checkDataConfigurazioneResponseCachingPorta(tipoOperazione, showStato, statoResponseCachingPorta);
			if (!isOk) {

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				porteApplicativeHelper.addConfigurazioneResponseCachingPorteToDati(tipoOperazione, dati, showStato, statoResponseCachingPorta,
						responseCachingEnabled, responseCachingSeconds, responseCachingMaxResponseSize, responseCachingMaxResponseSizeBytes, responseCachingDigestUrlInvocazione, responseCachingDigestHeaders, responseCachingDigestPayload);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_RESPONSE_CACHING,	ForwardParams.OTHER(""));
			}

			ResponseCachingConfigurazione newConfigurazione = null;
			if(statoResponseCachingPorta.equals(CostantiControlStation.VALUE_PARAMETRO_RESPONSE_CACHING_STATO_RIDEFINITO)) {
				newConfigurazione = porteApplicativeHelper.getResponseCaching(responseCachingEnabled, responseCachingSeconds, responseCachingMaxResponseSize, responseCachingMaxResponseSizeBytes, responseCachingDigestUrlInvocazione, responseCachingDigestHeaders, responseCachingDigestPayload);
			}
			
			pa.setResponseCaching(newConfigurazione);
			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);
			// Preparo la lista
			pd.setMessage(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE_RESPONSE_CACHING_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);

			pa = porteApplicativeCore.getPortaApplicativa(idInt);
			idporta = pa.getNome();

			ServletUtils.setPageDataTitle(pd, lstParam);

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();
			dati.addElement(ServletUtils.getDataElementForEditModeFinished());

			// ricarico la configurazione

			ResponseCachingConfigurazione configurazioneAggiornata = pa.getResponseCaching();
			
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

			porteApplicativeHelper.addConfigurazioneResponseCachingPorteToDati(tipoOperazione, dati, showStato, statoResponseCachingPorta,
					responseCachingEnabled, responseCachingSeconds, responseCachingMaxResponseSize, responseCachingMaxResponseSizeBytes, responseCachingDigestUrlInvocazione, responseCachingDigestHeaders, responseCachingDigestPayload);

			dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, idAsps, dati);

			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_RESPONSE_CACHING, ForwardParams.OTHER(""));
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_RESPONSE_CACHING, ForwardParams.OTHER(""));
		}
	}

}
