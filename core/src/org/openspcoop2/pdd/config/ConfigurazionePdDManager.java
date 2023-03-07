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

package org.openspcoop2.pdd.config;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Level;
import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.utils.FiltroRicercaAllarmi;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiAutenticazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoDatiGestioneToken;
import org.openspcoop2.core.config.AccessoDatiKeystore;
import org.openspcoop2.core.config.AccessoDatiRichieste;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.Cache;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.ConfigurazioneMultitenant;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.Dump;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.StatoServiziPdd;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.Transazioni;
import org.openspcoop2.core.config.TrasformazioneRegolaRichiesta;
import org.openspcoop2.core.config.TrasformazioneRegolaRisposta;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.RuoloContesto;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.config.driver.FiltroRicercaServiziApplicativi;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.ElencoIdPolicy;
import org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDConnettore;
import org.openspcoop2.core.id.IDGenericProperties;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.id.IdentificativiErogazione;
import org.openspcoop2.core.id.IdentificativiFruizione;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.soap.reader.OpenSPCoop2MessageSoapStreamReader;
import org.openspcoop2.monitor.engine.dynamic.IRegistroPluginsReader;
import org.openspcoop2.monitor.sdk.alarm.AlarmStatus;
import org.openspcoop2.monitor.sdk.alarm.IAlarm;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.pdd.core.controllo_traffico.SoglieDimensioneMessaggi;
import org.openspcoop2.pdd.core.controllo_traffico.policy.config.PolicyConfiguration;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.core.dynamic.ErrorHandler;
import org.openspcoop2.pdd.core.dynamic.InformazioniIntegrazione;
import org.openspcoop2.pdd.core.dynamic.InformazioniIntegrazioneCodifica;
import org.openspcoop2.pdd.core.dynamic.InformazioniIntegrazioneSorgente;
import org.openspcoop2.pdd.core.dynamic.MessageContent;
import org.openspcoop2.pdd.core.dynamic.Template;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.token.PolicyGestioneToken;
import org.openspcoop2.pdd.core.token.PolicyNegoziazioneToken;
import org.openspcoop2.pdd.core.token.attribute_authority.PolicyAttributeAuthority;
import org.openspcoop2.pdd.services.connector.FormUrlEncodedHttpServletRequest;
import org.openspcoop2.protocol.engine.mapping.IdentificazioneDinamicaException;
import org.openspcoop2.protocol.registry.CertificateCheck;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.registry.RegistroServiziReader;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.protocol.sdk.state.URLProtocolContext;
import org.openspcoop2.utils.NameValue;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.openspcoop2.utils.transport.TransportResponseContext;
import org.openspcoop2.utils.transport.http.HttpServletTransportRequestContext;
import org.slf4j.Logger;

/**
 * ConfigurazionePdDManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazionePdDManager {

	private static ConfigurazionePdDManager staticInstanceWithoutState = null;
	private static synchronized void initStaticInstanceWithoutState(){
		if(staticInstanceWithoutState == null) {
			staticInstanceWithoutState = new ConfigurazionePdDManager();
			staticInstanceWithoutState.singleInstance = true;
		}
	}

	public static ConfigurazionePdDManager getInstance(){
		//return new ConfigurazionePdDManager();
		if(staticInstanceWithoutState == null) {
			if(ConfigurazionePdDReader.getInstance()==null || RegistroServiziReader.getInstance()==null) {
				return new ConfigurazionePdDManager(); // succede all'avvio
			}
			initStaticInstanceWithoutState();
		}
		return staticInstanceWithoutState;
	}
	public static ConfigurazionePdDManager getInstance(IState state){
		if(state!=null && state instanceof StateMessage) {
			return getInstance((StateMessage)state);
		}
		return getInstance();
	}
	public static ConfigurazionePdDManager getInstance(StateMessage state){
		if(state!=null) {
			return new ConfigurazionePdDManager(state);
		}
		return getInstance();
	}
	public static ConfigurazionePdDManager getInstance(IState requestStateParam, IState responseStateParam){
		StateMessage requestState = null;
		StateMessage responseState = null;
		if(requestStateParam!=null && requestStateParam instanceof StateMessage) {
			requestState = (StateMessage) requestStateParam;
		}
		if(responseStateParam!=null && responseStateParam instanceof StateMessage) {
			responseState = (StateMessage) responseStateParam;
		}
		if(requestState!=null || responseState!=null) {
			return new ConfigurazionePdDManager(requestState,responseState);
		}
		return getInstance();
	}
	public static ConfigurazionePdDManager getInstance(StateMessage requestState, StateMessage responseState){
		if(requestState!=null || responseState!=null) {
			return new ConfigurazionePdDManager(requestState,responseState);
		}
		return getInstance();
	}


	private boolean singleInstance = false;
	private OpenSPCoop2Properties op2Properties = null;
	private ConfigurazionePdDReader configurazionePdDReader = null;
	private RegistroServiziManager registroServiziManager = null;
	private StateMessage state = null;
	private StateMessage responseState = null;

	public RegistroServiziManager getRegistroServiziManager() {
		return this.registroServiziManager;
	}
	public StateMessage getState() {
		return this.state;
	}
	public StateMessage getResponseState() {
		return this.responseState;
	}
	
	public boolean isInitializedConfigurazionePdDReader(){
		return this.configurazionePdDReader!=null;
	}

	public ConfigurazionePdDManager(){
		this.configurazionePdDReader = ConfigurazionePdDReader.getInstance();
		this.registroServiziManager = RegistroServiziManager.getInstance();
		this.op2Properties = OpenSPCoop2Properties.getInstance();
	}
	public ConfigurazionePdDManager(StateMessage state){
		this.configurazionePdDReader = ConfigurazionePdDReader.getInstance();
		this.state = state;
		this.registroServiziManager = RegistroServiziManager.getInstance(this.state);
		this.op2Properties = OpenSPCoop2Properties.getInstance();
	}
	public ConfigurazionePdDManager(StateMessage requestState, StateMessage responseState){
		this.configurazionePdDReader = ConfigurazionePdDReader.getInstance();
		this.state = requestState;
		this.responseState = responseState;
		this.registroServiziManager = RegistroServiziManager.getInstance(this.state, this.responseState);
		this.op2Properties = OpenSPCoop2Properties.getInstance();
	}
	
	public ConfigurazionePdDManager refreshState(IState requestStateParam, IState responseStateParam) {
		StateMessage requestState = null;
		StateMessage responseState = null;
		if(requestStateParam!=null && requestStateParam instanceof StateMessage) {
			requestState = (StateMessage) requestStateParam;
		}
		if(responseStateParam!=null && responseStateParam instanceof StateMessage) {
			responseState = (StateMessage) responseStateParam;
		}
		return refreshState(requestState, responseState);
	}
	public ConfigurazionePdDManager refreshState(StateMessage requestState, StateMessage responseState) {
		return _refreshState(requestState, responseState, null);
	}
	private ConfigurazionePdDManager _refreshState(StateMessage requestState, StateMessage responseState, RegistroServiziManager registroServiziManagerParam) {
		if(requestState==null && responseState==null) {
			return getInstance(); // senza stato
		}
		if(this.singleInstance) {
			return ConfigurazionePdDManager.getInstance(requestState, responseState); // inizialmente era senza stato, ora serve
		}
		this.state = requestState;
		this.responseState = responseState;
		if(registroServiziManagerParam!=null) {
			this.registroServiziManager = registroServiziManagerParam;
		}
		else {
			this.registroServiziManager = this.registroServiziManager.refreshState(this.state, this.responseState);
		}
		return this;
	}
	public ConfigurazionePdDManager refreshState(RegistroServiziManager registroServiziManager) {
		return this._refreshState(registroServiziManager.getState(), registroServiziManager.getResponseState(), registroServiziManager);
	}

	private Connection getConnection() {
		if(this.state!=null) {
			Connection c = StateMessage.getConnection(this.state);
			if(c!=null) {
				return c;
			}
		}
		if(this.responseState!=null) {
			Connection c = StateMessage.getConnection(this.responseState);
			if(c!=null) {
				return c;
			}
		}
		return null;
	}



	/* ********  U T I L S  ******** */ 

	public void isAlive() throws CoreException{
		this.configurazionePdDReader.isAlive();
	}

	public void validazioneSemantica(String[] tipiConnettori,String[] tipiMsgDiagnosticoAppender,String[] tipiTracciamentoAppender,String[] tipiDumpAppender,
			String[]tipoAutenticazionePortaDelegata,String[]tipoAutenticazionePortaApplicativa,
			String[]tipoAutorizzazionePortaDelegata,String[]tipoAutorizzazionePortaApplicativa,
			String[]tipoAutorizzazioneContenutoPortaDelegata,String[]tipoAutorizzazioneContenutoPortaApplicativa,
			String [] tipiIntegrazionePD, String [] tipiIntegrazionePA,
			boolean validazioneSemanticaAbilitataXML,boolean validazioneSemanticaAbilitataAltreConfigurazioni,boolean validaConfigurazione,
			Logger logConsole) throws CoreException{
		this.configurazionePdDReader.validazioneSemantica(tipiConnettori, tipiMsgDiagnosticoAppender, tipiTracciamentoAppender, tipiDumpAppender,
				tipoAutenticazionePortaDelegata, tipoAutenticazionePortaApplicativa,
				tipoAutorizzazionePortaDelegata, tipoAutorizzazionePortaApplicativa,
				tipoAutorizzazioneContenutoPortaDelegata, tipoAutorizzazioneContenutoPortaApplicativa, 
				tipiIntegrazionePD, tipiIntegrazionePA, validazioneSemanticaAbilitataXML, 
				validazioneSemanticaAbilitataAltreConfigurazioni, validaConfigurazione, logConsole);
	}

	public void setValidazioneSemanticaModificaConfigurazionePdDXML(String[] tipiConnettori,
			String[]tipoMsgDiagnosticiAppender,String[]tipoTracciamentoAppender,String[] tipiDumpAppender,
			String[]tipoAutenticazionePortaDelegata,String[]tipoAutenticazionePortaApplicativa,
			String[]tipoAutorizzazionePortaDelegata,String[]tipoAutorizzazionePortaApplicativa,
			String[]tipoAutorizzazioneContenutoPortaDelegata,String[]tipoAutorizzazioneContenutoPortaApplicativa,
			String[]tipoIntegrazionePD,String[]tipoIntegrazionePA) throws CoreException{
		this.configurazionePdDReader.setValidazioneSemanticaModificaConfigurazionePdDXML(tipiConnettori, tipoMsgDiagnosticiAppender, tipoTracciamentoAppender, tipiDumpAppender,
				tipoAutenticazionePortaDelegata, tipoAutenticazionePortaApplicativa,
				tipoAutorizzazionePortaDelegata, tipoAutorizzazionePortaApplicativa,
				tipoAutorizzazioneContenutoPortaDelegata, tipoAutorizzazioneContenutoPortaApplicativa, 
				tipoIntegrazionePD, tipoIntegrazionePA);
	}

	public void verificaConsistenzaConfigurazione() throws DriverConfigurazioneException {
		this.configurazionePdDReader.verificaConsistenzaConfigurazione();
	}

	private void resolveDynamicValue(String oggetto, MessageSecurityConfig config, Logger log, OpenSPCoop2Message message, Busta busta, 
			RequestInfo requestInfo, PdDContext pddContext) {
		if (config != null && config.getFlowParameters() != null && !config.getFlowParameters().isEmpty()) {
			ArrayList<String> valuesForReplace = new ArrayList<>();
			for (String key : config.getFlowParameters().keySet()) {
				Object oValue = config.getFlowParameters().get(key);
				if (oValue != null && oValue instanceof String) {
					String value = (String)oValue;
					if (value.contains("$")) {
						valuesForReplace.add(key);
					}
				}
			}

			if (!valuesForReplace.isEmpty()) {
				try {
					Map<String, List<String>> pTrasporto = null;
					String urlInvocazione = null;
					Map<String, List<String>> pQuery = null;
					Map<String, List<String>> pForm = null;
					if (requestInfo != null && requestInfo.getProtocolContext() != null) {
						pTrasporto = requestInfo.getProtocolContext().getHeaders();
						urlInvocazione = requestInfo.getProtocolContext().getUrlInvocazione_formBased();
						pQuery = requestInfo.getProtocolContext().getParameters();
						if(requestInfo.getProtocolContext() instanceof HttpServletTransportRequestContext) {
							HttpServletTransportRequestContext httpServletContext = (HttpServletTransportRequestContext) requestInfo.getProtocolContext();
							HttpServletRequest httpServletRequest = httpServletContext.getHttpServletRequest();
							if(httpServletRequest!=null && httpServletRequest instanceof FormUrlEncodedHttpServletRequest) {
								FormUrlEncodedHttpServletRequest formServlet = (FormUrlEncodedHttpServletRequest) httpServletRequest;
								if(formServlet.getFormUrlEncodedParametersValues()!=null &&
										!formServlet.getFormUrlEncodedParametersValues().isEmpty()) {
									pForm = formServlet.getFormUrlEncodedParametersValues();
								}
							}
						}
					}

					MessageContent messageContent = null;
					boolean bufferMessage_readOnly =  OpenSPCoop2Properties.getInstance().isReadByPathBufferEnabled();
	    			if (ServiceBinding.SOAP.equals(message.getServiceBinding())) {
						messageContent = new MessageContent(message.castAsSoap(), bufferMessage_readOnly, pddContext);
					} else if (MessageType.XML.equals(message.getMessageType())) {
						messageContent = new MessageContent(message.castAsRestXml(), bufferMessage_readOnly, pddContext);
					} else if (MessageType.JSON.equals(message.getMessageType())) {
						messageContent = new MessageContent(message.castAsRestJson(), bufferMessage_readOnly, pddContext);
					}

					Map<String, Object> dynamicMap = new HashMap<String, Object>();
					ErrorHandler errorHandler = new ErrorHandler();
					DynamicUtils.fillDynamicMapRequest(log, dynamicMap, pddContext, urlInvocazione, message, messageContent, busta, 
							pTrasporto, 
							pQuery, 
							pForm,
							errorHandler);

					Iterator<String> it = valuesForReplace.iterator();
					while(it.hasNext()) {
						String keyForReplace = (String)it.next();
						String value = null;

						try {
							value = (String)config.getFlowParameters().get(keyForReplace);
							String newValue = DynamicUtils.convertDynamicPropertyValue("ConditionalMessageSecurity", value, dynamicMap, pddContext);
							if (newValue != null && !"".contentEquals(newValue)) {
								config.getFlowParameters().put(keyForReplace, newValue);
							}
						} catch (Exception e) {
							log.error(oggetto + " errore durante la risoluzione della proprietà '" + keyForReplace + "' [" + value + "]: " + e.getMessage(), e);
						}
					}
				} catch (Exception e) {
					log.error(oggetto + " errore durante l'analisi delle proprietà dinamiche: " + e.getMessage(), e);
				}
			}
		}

	}



	/* ********  SOGGETTI (Interfaccia)  ******** */

	public String getIdentificativoPorta(IDSoggetto idSoggetto,IProtocolFactory<?> protocolFactory, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && idSoggetto!=null;
		if(useRequestInfo) {
			if( requestInfo.getRequestConfig().getSoggettoErogatoreConfig()!=null && requestInfo.getRequestConfig().getSoggettoErogatoreIdentificativoPorta()!=null &&
					idSoggetto!=null && idSoggetto.getTipo()!=null && idSoggetto.getNome()!=null &&
					idSoggetto.getTipo().equals(requestInfo.getRequestConfig().getSoggettoErogatoreConfig().getTipo()) &&
					idSoggetto.getNome().equals(requestInfo.getRequestConfig().getSoggettoErogatoreConfig().getNome())	) {
				return requestInfo.getRequestConfig().getSoggettoErogatoreIdentificativoPorta();
			}
			else if( requestInfo.getRequestConfig().getSoggettoFruitoreConfig()!=null && requestInfo.getRequestConfig().getSoggettoFruitoreIdentificativoPorta()!=null &&
					idSoggetto!=null && idSoggetto.getTipo()!=null && idSoggetto.getNome()!=null &&
					idSoggetto.getTipo().equals(requestInfo.getRequestConfig().getSoggettoFruitoreConfig().getTipo()) &&
					idSoggetto.getNome().equals(requestInfo.getRequestConfig().getSoggettoFruitoreConfig().getNome())	) {
				return requestInfo.getRequestConfig().getSoggettoFruitoreIdentificativoPorta();
			}
		}
		if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo()!=null) {
			if(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig()!=null && 
					requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreIdentificativoPorta()!=null &&
					idSoggetto!=null && idSoggetto.getTipo()!=null && idSoggetto.getNome()!=null &&
					idSoggetto.getTipo().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig().getTipo()) && 
					idSoggetto.getNome().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig().getNome())) {
				return requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreIdentificativoPorta();
			}
		}
		if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo()!=null) {
			if(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig()!=null && 
					requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreIdentificativoPorta()!=null &&
					idSoggetto!=null && idSoggetto.getTipo()!=null && idSoggetto.getNome()!=null &&
					idSoggetto.getTipo().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig().getTipo()) && 
					idSoggetto.getNome().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig().getNome())) {
				return requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreIdentificativoPorta();
			}
		}
		return this.configurazionePdDReader.getIdentificativoPorta(this.getConnection(), idSoggetto, protocolFactory, requestInfo);
		// il set viene effettuato nei service utils per comprendere se si tratta di fruitore o erogatore
	}

	public boolean isSoggettoVirtuale(IDSoggetto idSoggetto, RequestInfo requestInfo) throws DriverConfigurazioneException { 
		if(!this.op2Properties.isSoggettiVirtualiEnabled()) {
			return false;
		}
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && idSoggetto!=null;
		if(useRequestInfo) {
			if( requestInfo.getRequestConfig().getSoggettoErogatoreConfig()!=null && requestInfo.getRequestConfig().getSoggettoErogatoreSoggettoVirtuale()!=null &&
				idSoggetto.getTipo().equals(requestInfo.getRequestConfig().getSoggettoErogatoreConfig().getTipo()) &&
					idSoggetto.getNome().equals(requestInfo.getRequestConfig().getSoggettoErogatoreConfig().getNome())	) {
				return requestInfo.getRequestConfig().getSoggettoErogatoreSoggettoVirtuale();
			}
			else if( requestInfo.getRequestConfig().getSoggettoFruitoreConfig()!=null && requestInfo.getRequestConfig().getSoggettoFruitoreSoggettoVirtuale()!=null &&
				idSoggetto.getTipo().equals(requestInfo.getRequestConfig().getSoggettoFruitoreConfig().getTipo()) &&
					idSoggetto.getNome().equals(requestInfo.getRequestConfig().getSoggettoFruitoreConfig().getNome())	) {
				return requestInfo.getRequestConfig().getSoggettoFruitoreSoggettoVirtuale();
			}
		}
		if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo()!=null) {
			if(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig()!=null && 
					requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreSoggettoVirtuale()!=null &&
					idSoggetto.getTipo().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig().getTipo()) && 
					idSoggetto.getNome().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig().getNome())) {
				return requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreSoggettoVirtuale();
			}
		}
		if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo()!=null) {
			if(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig()!=null && 
					requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreSoggettoVirtuale()!=null &&
					idSoggetto.getTipo().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig().getTipo()) && 
					idSoggetto.getNome().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig().getNome())) {
				return requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreSoggettoVirtuale();
			}
		}
		return this.configurazionePdDReader.isSoggettoVirtuale(this.getConnection(), idSoggetto);
		// il set viene effettuato nei service utils per comprendere se si tratta di fruitore o erogatore
	}

	public org.openspcoop2.core.config.Soggetto getSoggetto(IDSoggetto idSoggetto, RequestInfo requestInfo)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{  
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && idSoggetto!=null;
		if(useRequestInfo) {
			if( requestInfo.getRequestConfig().getSoggettoErogatoreConfig()!=null &&
				idSoggetto.getTipo().equals(requestInfo.getRequestConfig().getSoggettoErogatoreConfig().getTipo()) &&
					idSoggetto.getNome().equals(requestInfo.getRequestConfig().getSoggettoErogatoreConfig().getNome())	) {
				return requestInfo.getRequestConfig().getSoggettoErogatoreConfig();
			}
			else if( requestInfo.getRequestConfig().getSoggettoFruitoreConfig()!=null && 
				idSoggetto.getTipo().equals(requestInfo.getRequestConfig().getSoggettoFruitoreConfig().getTipo()) &&
					idSoggetto.getNome().equals(requestInfo.getRequestConfig().getSoggettoFruitoreConfig().getNome())	) {
				return requestInfo.getRequestConfig().getSoggettoFruitoreConfig();
			}
		}
		if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo()!=null) {
			if(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig()!=null && idSoggetto!=null && 
					idSoggetto.getTipo().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig().getTipo()) && 
					idSoggetto.getNome().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig().getNome())) {
				return requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig();
			}
		}
		if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo()!=null) {
			if(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig()!=null && idSoggetto!=null && 
					idSoggetto.getTipo().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig().getTipo()) && 
					idSoggetto.getNome().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig().getNome())) {
				return requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig();
			}
		}
		return this.configurazionePdDReader.getSoggetto(this.getConnection(), idSoggetto);
		// il set viene effettuato nei service utils per comprendere se si tratta di fruitore o erogatore
	}
	
	public boolean existsSoggetto(IDSoggetto idSoggetto, RequestInfo requestInfo)throws DriverConfigurazioneException{  
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && idSoggetto!=null;
		if(useRequestInfo) {
			if( requestInfo.getRequestConfig().getSoggettoErogatoreConfig()!=null &&
				idSoggetto.getTipo().equals(requestInfo.getRequestConfig().getSoggettoErogatoreConfig().getTipo()) &&
					idSoggetto.getNome().equals(requestInfo.getRequestConfig().getSoggettoErogatoreConfig().getNome())	) {
				return true;
			}
			else if( requestInfo.getRequestConfig().getSoggettoFruitoreConfig()!=null && 
				idSoggetto.getTipo().equals(requestInfo.getRequestConfig().getSoggettoFruitoreConfig().getTipo()) &&
					idSoggetto.getNome().equals(requestInfo.getRequestConfig().getSoggettoFruitoreConfig().getNome())	) {
				return true;
			}
		}
		if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo()!=null) {
			if(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig()!=null && idSoggetto!=null && 
					idSoggetto.getTipo().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig().getTipo()) && 
					idSoggetto.getNome().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig().getNome())) {
				return true;
			}
		}
		if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo()!=null) {
			if(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig()!=null && idSoggetto!=null && 
					idSoggetto.getTipo().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig().getTipo()) && 
					idSoggetto.getNome().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig().getNome())) {
				return true;
			}
		}
		return this.configurazionePdDReader.existsSoggetto(this.getConnection(), idSoggetto);
		// il set viene effettuato nei service utils per comprendere se si tratta di fruitore o erogatore
	}

	public  List<IDServizio> getServizi_SoggettiVirtuali() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(!this.op2Properties.isSoggettiVirtualiEnabled()) {
			return null;
		}
		return this.configurazionePdDReader.getServizi_SoggettiVirtuali(this.getConnection());
	}


	/* ************* ROUTING **************** */

	public Connettore getForwardRoute(IDSoggetto idSoggettoDestinatario,boolean functionAsRouter, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getForwardRoute(this.getConnection(), this.registroServiziManager, idSoggettoDestinatario, functionAsRouter, requestInfo);
	}

	public Connettore getForwardRoute(IDSoggetto idSoggettoMittente, IDServizio idServizio,boolean functionAsRouter, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getForwardRoute(this.getConnection(), this.registroServiziManager, idSoggettoMittente, idServizio, functionAsRouter, requestInfo);
	}

	public String getRegistroForImbustamento(IDSoggetto idSoggettoMittente, IDServizio idServizio,boolean functionAsRouter, RequestInfo requestInfo)throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getRegistroForImbustamento(this.getConnection(), this.registroServiziManager, idSoggettoMittente, idServizio, functionAsRouter, requestInfo);
	}

	public boolean routerFunctionActive() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.routerFunctionActive(this.getConnection());
	}

	public IDSoggetto getRouterIdentity(IProtocolFactory<?> protocolFactory, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getRouterIdentity(this.getConnection(),protocolFactory, requestInfo);
	}


	/* ********  URLPrefixRewriter  ******** */

	public void setPDUrlPrefixRewriter(org.openspcoop2.core.config.Connettore connettore, IDSoggetto idSoggettoFruitore, RequestInfo requestInfo) throws DriverConfigurazioneNotFound,DriverConfigurazioneException{
		this.configurazionePdDReader.setPDUrlPrefixRewriter(this.getConnection(), connettore, idSoggettoFruitore, requestInfo);
	}

	public void setPAUrlPrefixRewriter(org.openspcoop2.core.config.Connettore connettore, IDSoggetto idSoggettoErogatore, RequestInfo requestInfo) throws DriverConfigurazioneNotFound,DriverConfigurazioneException{
		this.configurazionePdDReader.setPAUrlPrefixRewriter(this.getConnection(), connettore, idSoggettoErogatore, requestInfo);
	}


	/* ********  PORTE DELEGATE (Interfaccia)  ******** */

	public IDPortaDelegata convertToIDPortaDelegata(PortaDelegata pd) throws DriverRegistroServiziException{

		IDPortaDelegata idPD = new IDPortaDelegata();
		idPD.setNome(pd.getNome());

		IdentificativiFruizione idFruizione = new IdentificativiFruizione();

		IDSoggetto soggettoFruitore = new IDSoggetto(pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario());
		idFruizione.setSoggettoFruitore(soggettoFruitore);

		IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(pd.getServizio().getTipo(),pd.getServizio().getNome(), 
				new IDSoggetto(pd.getSoggettoErogatore().getTipo(),pd.getSoggettoErogatore().getNome()), 
				pd.getServizio().getVersione()); 
		if(pd.getAzione()!=null && pd.getAzione().getNome()!=null && !"".equals(pd.getAzione().getNome())){
			idServizio.setAzione(pd.getAzione().getNome());	
		}
		idFruizione.setIdServizio(idServizio);

		idPD.setIdentificativiFruizione(idFruizione);

		return idPD;
	}

	public IDPortaDelegata getIDPortaDelegata(String nome, RequestInfo requestInfo, IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
	
		IDPortaDelegata idPortaDelegata = null;
		if(requestInfo!=null && requestInfo.getRequestConfig()!=null && nome!=null) {
			if( requestInfo.getRequestConfig().getIdPortaDelegata()!=null && nome.equals(requestInfo.getRequestConfig().getIdPortaDelegata().getNome())) {
				idPortaDelegata = requestInfo.getRequestConfig().getIdPortaDelegata();
			}
			else if( requestInfo.getRequestConfig().getIdPortaDelegataDefault()!=null && nome.equals(requestInfo.getRequestConfig().getIdPortaDelegataDefault().getNome())) {
				idPortaDelegata = requestInfo.getRequestConfig().getIdPortaDelegataDefault();
			}
		}
		if(idPortaDelegata==null) {
			idPortaDelegata = this.configurazionePdDReader.getIDPortaDelegata(this.getConnection(), nome);
		}
		
		try{
			if(idPortaDelegata!=null && idPortaDelegata.getIdentificativiFruizione()!=null){
				if(idPortaDelegata.getIdentificativiFruizione().getSoggettoFruitore()!=null){
					IDSoggetto soggetto = idPortaDelegata.getIdentificativiFruizione().getSoggettoFruitore();
					if(soggetto.getCodicePorta()==null){
						soggetto.setCodicePorta(this.registroServiziManager.getDominio(soggetto, null, protocolFactory, requestInfo));
					}
				}
				if(idPortaDelegata.getIdentificativiFruizione().getIdServizio()!=null &&
						idPortaDelegata.getIdentificativiFruizione().getIdServizio().getSoggettoErogatore()!=null){
					IDSoggetto soggetto = idPortaDelegata.getIdentificativiFruizione().getIdServizio().getSoggettoErogatore();
					if(soggetto.getCodicePorta()==null){
						soggetto.setCodicePorta(this.registroServiziManager.getDominio(soggetto, null, protocolFactory, requestInfo));
					}
				}
			}
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

		return idPortaDelegata;
	}

	public PortaDelegata getPortaDelegata(IDPortaDelegata idPD, RequestInfo requestInfo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		if(requestInfo!=null && requestInfo.getRequestConfig()!=null && idPD!=null && idPD.getNome()!=null) {
			if( requestInfo.getRequestConfig().getPortaDelegata()!=null && idPD.getNome().equals(requestInfo.getRequestConfig().getPortaDelegata().getNome())) {
				return requestInfo.getRequestConfig().getPortaDelegata();
			}
			else if( requestInfo.getRequestConfig().getPortaDelegataDefault()!=null && idPD.getNome().equals(requestInfo.getRequestConfig().getPortaDelegataDefault().getNome())) {
				return requestInfo.getRequestConfig().getPortaDelegataDefault();
			}
		}
		return this.configurazionePdDReader.getPortaDelegata(this.getConnection(), idPD);
	}

	public PortaDelegata getPortaDelegata_SafeMethod(IDPortaDelegata idPD, RequestInfo requestInfo)throws DriverConfigurazioneException{
		if(requestInfo!=null && requestInfo.getRequestConfig()!=null && idPD!=null && idPD.getNome()!=null) {
			if( requestInfo.getRequestConfig().getPortaDelegata()!=null && idPD.getNome().equals(requestInfo.getRequestConfig().getPortaDelegata().getNome())) {
				return requestInfo.getRequestConfig().getPortaDelegata();
			}
			else if( requestInfo.getRequestConfig().getPortaDelegataDefault()!=null && idPD.getNome().equals(requestInfo.getRequestConfig().getPortaDelegataDefault().getNome())) {
				return requestInfo.getRequestConfig().getPortaDelegataDefault();
			}
		}
		return this.configurazionePdDReader.getPortaDelegata_SafeMethod(this.getConnection(), idPD);		
	}
	
	public void updateStatoPortaDelegata(IDPortaDelegata idPD, StatoFunzionalita stato) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		this.configurazionePdDReader.updateStatoPortaDelegata(this.getConnection(), idPD, stato);
	}
	
	public Map<String, String> getProprietaConfigurazione(PortaDelegata pd) throws DriverConfigurazioneException {
		return this.configurazionePdDReader.getProprietaConfigurazione(pd);
	}

	public boolean identificazioneContentBased(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.identificazioneContentBased(pd);
	}

	public boolean identificazioneInputBased(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.identificazioneInputBased(pd);
	}

	public String getAzione(PortaDelegata pd,URLProtocolContext urlProtocolContext,RequestInfo requestInfo,
			OpenSPCoop2Message message, OpenSPCoop2MessageSoapStreamReader soapStreamReader, HeaderIntegrazione headerIntegrazione,boolean readFirstHeaderIntegrazione,
			IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound, IdentificazioneDinamicaException { 
		return this.configurazionePdDReader.getAzione(this.registroServiziManager, pd, urlProtocolContext, requestInfo,
				message, soapStreamReader, headerIntegrazione, readFirstHeaderIntegrazione, protocolFactory);
	}

	public MTOMProcessorConfig getPD_MTOMProcessorForSender(PortaDelegata pd) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getPD_MTOMProcessorForSender(pd);
	}

	public MTOMProcessorConfig getPD_MTOMProcessorForReceiver(PortaDelegata pd) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getPD_MTOMProcessorForReceiver(pd);
	}

	public MessageSecurityConfig getPD_MessageSecurityForSender(PortaDelegata pd) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getPD_MessageSecurityForSender(pd);
	}

	public MessageSecurityConfig getPD_MessageSecurityForSender(PortaDelegata pd, Logger log, OpenSPCoop2Message message, Busta busta, RequestInfo requestInfo, PdDContext pddContext) throws DriverConfigurazioneException {
		MessageSecurityConfig config = this.configurazionePdDReader.getPD_MessageSecurityForSender(pd);
		this.resolveDynamicValue("getPD_MessageSecurityForSender[" + pd.getNome() + "]", config, log, message, busta, requestInfo, pddContext);
		return config;
	}

	public MessageSecurityConfig getPD_MessageSecurityForReceiver(PortaDelegata pd) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getPD_MessageSecurityForReceiver(pd);
	}

	public String getAutenticazione(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getAutenticazione(pd);
	}

	public boolean isAutenticazioneOpzionale(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.isAutenticazioneOpzionale(pd);
	}

	public String getGestioneToken(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getGestioneToken(pd);
	}

	public PolicyGestioneToken getPolicyGestioneToken(PortaDelegata pd, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		String nome = null;
		if(pd!=null && pd.getGestioneToken()!=null && pd.getGestioneToken().getPolicy()!=null) {
			nome = pd.getGestioneToken().getPolicy();
		}
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && nome!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getPolicyValidazioneToken(nome);
			if(o!=null && o instanceof PolicyGestioneToken) {
				return (PolicyGestioneToken) o;
			}
		}
		PolicyGestioneToken policy = this.configurazionePdDReader.getPolicyGestioneToken(this.getConnection(), pd);
		if(useRequestInfo && requestInfo!=null) {
			requestInfo.getRequestConfig().addPolicyValidazioneToken(nome, policy, 
					requestInfo.getIdTransazione());
		}
		return policy;
	}

	public String getAutorizzazione(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getAutorizzazione(pd);
	}

	public String getAutorizzazioneContenuto(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getAutorizzazioneContenuto(pd);
	}

	public CorsConfigurazione getConfigurazioneCORS(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getConfigurazioneCORS(this.getConnection(), pd);
	}

	public ResponseCachingConfigurazione getConfigurazioneResponseCaching(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getConfigurazioneResponseCaching(this.getConnection(), pd);
	}

	public boolean ricevutaAsincronaSimmetricaAbilitata(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.ricevutaAsincronaSimmetricaAbilitata(pd);
	}

	public boolean ricevutaAsincronaAsimmetricaAbilitata(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.ricevutaAsincronaAsimmetricaAbilitata(pd);
	}

	public ValidazioneContenutiApplicativi getTipoValidazioneContenutoApplicativo(PortaDelegata pd,String implementazionePdDSoggetto, boolean request) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getTipoValidazioneContenutoApplicativo(this.getConnection(), pd, implementazionePdDSoggetto, request);
	}

	public CorrelazioneApplicativa getCorrelazioneApplicativa(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getCorrelazioneApplicativa(pd);
	}

	public CorrelazioneApplicativaRisposta getCorrelazioneApplicativaRisposta(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getCorrelazioneApplicativaRisposta(pd);
	}

	public String[] getTipiIntegrazione(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getTipiIntegrazione(pd);
	}

	public boolean isGestioneManifestAttachments(PortaDelegata pd, IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.isGestioneManifestAttachments(this.getConnection(), pd, protocolFactory);
	}

	public boolean isAllegaBody(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.isAllegaBody(pd);
	}

	public boolean isScartaBody(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.isScartaBody(pd);
	}

	public boolean isModalitaStateless(PortaDelegata pd, ProfiloDiCollaborazione profiloCollaborazione) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.isModalitaStateless(pd, profiloCollaborazione);
	}

	public boolean isLocalForwardMode(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.isLocalForwardMode(pd);
	}

	public String getLocalForward_NomePortaApplicativa(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getLocalForward_NomePortaApplicativa(pd);
	}

	public boolean isPortaAbilitata(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.isPortaAbilitata(pd);
	}

	public DumpConfigurazione getDumpConfigurazione(PortaDelegata pd) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getDumpConfigurazione(this.getConnection(), pd);
	}
	
	public boolean isTransazioniFileTraceEnabled(PortaDelegata pd) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.isTransazioniFileTraceEnabled(this.getConnection(), pd);
	}
	public boolean isTransazioniFileTraceDumpBinarioHeadersEnabled(PortaDelegata pd) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.isTransazioniFileTraceDumpBinarioHeadersEnabled(this.getConnection(), pd);
	}
	public boolean isTransazioniFileTraceDumpBinarioPayloadEnabled(PortaDelegata pd) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.isTransazioniFileTraceDumpBinarioPayloadEnabled(this.getConnection(), pd);
	}
	public boolean isTransazioniFileTraceDumpBinarioConnettoreHeadersEnabled(PortaDelegata pd) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.isTransazioniFileTraceDumpBinarioConnettoreHeadersEnabled(this.getConnection(), pd);
	}
	public boolean isTransazioniFileTraceDumpBinarioConnettorePayloadEnabled(PortaDelegata pd) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.isTransazioniFileTraceDumpBinarioConnettorePayloadEnabled(this.getConnection(), pd);
	}
	public File getFileTraceConfig(PortaDelegata pd) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getFileTraceConfig(this.getConnection(), pd);
	}

	public SoglieDimensioneMessaggi getSoglieLimitedInputStream(PortaDelegata pd, String azione, String idModulo,
			PdDContext pddContext, RequestInfo requestInfo, 
			IProtocolFactory<?> protocolFactory, Logger log) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getSoglieLimitedInputStream(this.getConnection(), pd, azione, idModulo,
				pddContext, requestInfo, 
				protocolFactory, log);
	}
	
	public boolean isConnettoriUseTimeoutInputStream(PortaDelegata pd) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.isConnettoriUseTimeoutInputStream(this.getConnection(), pd);
	}
	public int getRequestReadTimeout(PortaDelegata pd) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getRequestReadTimeout(this.getConnection(), pd);
	}
	
	public Trasformazioni getTrasformazioni(PortaDelegata pd) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getTrasformazioni(pd);
	}

	public List<String> getPreInRequestHandlers(PortaDelegata pd) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getPreInRequestHandlers(pd);
	}
	public List<String> getInRequestHandlers(PortaDelegata pd) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getInRequestHandlers(pd);
	}
	public List<String> getInRequestProtocolHandlers(PortaDelegata pd) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getInRequestProtocolHandlers(pd);
	}
	public List<String> getOutRequestHandlers(PortaDelegata pd) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getOutRequestHandlers(pd);
	}
	public List<String> getPostOutRequestHandlers(PortaDelegata pd) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getPostOutRequestHandlers(pd);
	}
	public List<String> getPreInResponseHandlers(PortaDelegata pd) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getPreInResponseHandlers(pd);
	}
	public List<String> getInResponseHandlers(PortaDelegata pd) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getInResponseHandlers(pd);
	}
	public List<String> getOutResponseHandlers(PortaDelegata pd) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getOutResponseHandlers(pd);
	}
	public List<String> getPostOutResponseHandlers(PortaDelegata pd) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getPostOutResponseHandlers(pd);
	}

	public List<Object> getExtendedInfo(PortaDelegata pd)throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getExtendedInfo(pd);
	}
	
	public Template getTemplateTrasformazioneRichiesta(IDPortaDelegata idPD, String nomeTrasformazione, TrasformazioneRegolaRichiesta richiesta, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getTemplateTrasformazioneRichiesta(this.getConnection(), idPD, nomeTrasformazione, richiesta, requestInfo);
	}
	public Template getTemplateTrasformazioneSoapRichiesta(IDPortaDelegata idPD, String nomeTrasformazione, TrasformazioneRegolaRichiesta richiesta, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getTemplateTrasformazioneSoapRichiesta(this.getConnection(), idPD, nomeTrasformazione, richiesta, requestInfo);
	}
	public Template getTemplateTrasformazioneRisposta(IDPortaDelegata idPD, String nomeTrasformazione, TrasformazioneRegolaRisposta risposta, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getTemplateTrasformazioneRisposta(this.getConnection(), idPD, nomeTrasformazione, risposta, requestInfo);
	}
	public Template getTemplateTrasformazioneSoapRisposta(IDPortaDelegata idPD, String nomeTrasformazione, TrasformazioneRegolaRisposta risposta, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getTemplateTrasformazioneSoapRisposta(this.getConnection(), idPD, nomeTrasformazione, risposta, requestInfo);
	}
	
	public Template getTemplateCorrelazioneApplicativaRichiesta(IDPortaDelegata idPD, String nomeRegola, byte[] template, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getTemplateCorrelazioneApplicativaRichiesta(this.getConnection(), idPD, nomeRegola, template, requestInfo);
	}
	public Template getTemplateCorrelazioneApplicativaRisposta(IDPortaDelegata idPD, String nomeRegola, byte[] template, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getTemplateCorrelazioneApplicativaRisposta(this.getConnection(), idPD, nomeRegola, template, requestInfo);
	}
	
	public Template getTemplateIntegrazione(IDPortaDelegata idPD, File file, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getTemplateIntegrazione(this.getConnection(), idPD, file, requestInfo);
	}

	public void setInformazioniIntegrazioneDinamiche(Logger log, HttpServletTransportRequestContext transportRequestContext, Context context, PortaDelegata pd) throws DriverConfigurazioneException {
		_setInformazioniIntegrazioneDinamiche(log, transportRequestContext, context, pd.getProprietaList());
	}
	private void _setInformazioniIntegrazioneDinamiche(Logger log, HttpServletTransportRequestContext transportRequestContext, Context context, 
			List<Proprieta> proprieta) throws DriverConfigurazioneException {
		
		try {
			OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
			boolean isEnabled = CostantiProprieta.isInformazioniIntegrazioneEnabled(proprieta, op2Properties.isIntegrazioneDynamicInfoEnabled());
			if(isEnabled) {
				InformazioniIntegrazioneSorgente sourceType = CostantiProprieta.getTipoInformazioniIntegrazione(proprieta, op2Properties.getIntegrazioneDynamicInfoType());
				String sourceName = CostantiProprieta.getNomeSorgenteInformazioniIntegrazione(proprieta, op2Properties.getIntegrazioneDynamicInfoName());
				InformazioniIntegrazioneCodifica sourceEncodeType = CostantiProprieta.getTipoCodificaInformazioniIntegrazione(proprieta, op2Properties.getIntegrazioneDynamicInfoEncodeType());
				boolean required = CostantiProprieta.isInformazioniIntegrazioneRequired(proprieta, op2Properties.isIntegrazioneDynamicInfoRequired());
				InformazioniIntegrazione infoIntegrazione = new InformazioniIntegrazione(sourceType, sourceName, sourceEncodeType, required, log, transportRequestContext);
				context.addObject(Costanti.INFORMAZIONI_INTEGRAZIONE, infoIntegrazione);
			}			
		}catch(Throwable t) {
			throw new DriverConfigurazioneException(t.getMessage(),t);
		}
		
	}
	
	public void setInformazioniIntegrazioneDinamiche(Logger log, TransportResponseContext transportResponseContext, Context context, PortaDelegata pd) throws DriverConfigurazioneException {
		_setInformazioniIntegrazioneDinamiche(log, transportResponseContext, context, pd.getProprietaList());
	}
	private void _setInformazioniIntegrazioneDinamiche(Logger log, TransportResponseContext transportResponseContext, Context context, 
			List<Proprieta> proprieta) throws DriverConfigurazioneException {
		
		try {
			OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
			boolean isEnabled = CostantiProprieta.isInformazioniIntegrazioneRispostaEnabled(proprieta, op2Properties.isIntegrazioneResponseDynamicInfoEnabled());
			if(isEnabled) {
				InformazioniIntegrazioneSorgente sourceType = InformazioniIntegrazioneSorgente.http_header;
				String sourceName = CostantiProprieta.getNomeSorgenteInformazioniIntegrazioneRisposta(proprieta, op2Properties.getIntegrazioneResponseDynamicInfoName());
				InformazioniIntegrazioneCodifica sourceEncodeType = CostantiProprieta.getTipoCodificaInformazioniIntegrazioneRisposta(proprieta, op2Properties.getIntegrazioneResponseDynamicInfoEncodeType());
				boolean required = CostantiProprieta.isInformazioniIntegrazioneRispostaRequired(proprieta, op2Properties.isIntegrazioneResponseDynamicInfoRequired());
				InformazioniIntegrazione infoIntegrazione = new InformazioniIntegrazione(sourceType, sourceName, sourceEncodeType, required, log, transportResponseContext);
				context.addObject(Costanti.INFORMAZIONI_INTEGRAZIONE_RISPOSTA, infoIntegrazione);
			}			
		}catch(Throwable t) {
			throw new DriverConfigurazioneException(t.getMessage(),t);
		}
		
	}
	


	/* ********  PORTE APPLICATIVE  (Interfaccia) ******** */

	public IDPortaApplicativa convertToIDPortaApplicativa(PortaApplicativa pa) throws DriverRegistroServiziException{

		IDPortaApplicativa idPA = new IDPortaApplicativa();
		idPA.setNome(pa.getNome());

		IdentificativiErogazione idErogazione = new IdentificativiErogazione();

		if(pa.getSoggettoVirtuale()!=null){
			IDSoggetto soggettoVirtuale = new IDSoggetto(pa.getSoggettoVirtuale().getTipo(),pa.getSoggettoVirtuale().getNome());
			idErogazione.setSoggettoVirtuale(soggettoVirtuale);
		}

		IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(pa.getServizio().getTipo(),pa.getServizio().getNome(), 
				new IDSoggetto(pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()), 
				pa.getServizio().getVersione()); 
		if(pa.getAzione()!=null && pa.getAzione().getNome()!=null && !"".equals(pa.getAzione().getNome())){
			idServizio.setAzione(pa.getAzione().getNome());	
		}
		idErogazione.setIdServizio(idServizio);

		idPA.setIdentificativiErogazione(idErogazione);

		return idPA;
	}

	public IDPortaApplicativa getIDPortaApplicativa(String nome, RequestInfo requestInfo, IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		IDPortaApplicativa idPortaApplicativa = null;
		if(requestInfo!=null && requestInfo.getRequestConfig()!=null && nome!=null) {
			if( requestInfo.getRequestConfig().getIdPortaApplicativa()!=null && nome.equals(requestInfo.getRequestConfig().getIdPortaApplicativa().getNome())) {
				idPortaApplicativa = requestInfo.getRequestConfig().getIdPortaApplicativa();
			}
			else if( requestInfo.getRequestConfig().getIdPortaApplicativaDefault()!=null && nome.equals(requestInfo.getRequestConfig().getIdPortaApplicativaDefault().getNome())) {
				idPortaApplicativa = requestInfo.getRequestConfig().getIdPortaApplicativaDefault();
			}
		}
		if(idPortaApplicativa==null) {
			idPortaApplicativa = this.configurazionePdDReader.getIDPortaApplicativa(this.getConnection(), nome);
		}
		try{
			if(idPortaApplicativa!=null && idPortaApplicativa.getIdentificativiErogazione()!=null){
				if(idPortaApplicativa.getIdentificativiErogazione().getSoggettoVirtuale()!=null){
					IDSoggetto soggetto = idPortaApplicativa.getIdentificativiErogazione().getSoggettoVirtuale();
					if(soggetto.getCodicePorta()==null){
						soggetto.setCodicePorta(this.registroServiziManager.getDominio(soggetto, null, protocolFactory, requestInfo));
					}
				}
				if(idPortaApplicativa.getIdentificativiErogazione().getIdServizio()!=null &&
						idPortaApplicativa.getIdentificativiErogazione().getIdServizio().getSoggettoErogatore()!=null){
					IDSoggetto soggetto = idPortaApplicativa.getIdentificativiErogazione().getIdServizio().getSoggettoErogatore();
					if(soggetto.getCodicePorta()==null){
						soggetto.setCodicePorta(this.registroServiziManager.getDominio(soggetto, null, protocolFactory, requestInfo));
					}
				}
			}
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		return idPortaApplicativa;
	}

	public Map<IDSoggetto,PortaApplicativa> getPorteApplicative_SoggettiVirtuali(IDServizio idServizio)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getPorteApplicative_SoggettiVirtuali(this.getConnection(), idServizio);
	}

	public boolean existsPA(RichiestaApplicativa richiestaApplicativa, RequestInfo requestInfo) throws DriverConfigurazioneException{
		IDPortaApplicativa idPA = null;
		if(richiestaApplicativa!=null) {
			idPA = richiestaApplicativa.getIdPortaApplicativa();
		}
		if(requestInfo!=null && requestInfo.getRequestConfig()!=null && idPA!=null && idPA.getNome()!=null) {
			if( requestInfo.getRequestConfig().getPortaApplicativa()!=null && idPA.getNome().equals(requestInfo.getRequestConfig().getPortaApplicativa().getNome())) {
				return true;
			}
			else if( requestInfo.getRequestConfig().getPortaApplicativaDefault()!=null && idPA.getNome().equals(requestInfo.getRequestConfig().getPortaApplicativaDefault().getNome())) {
				return true;
			}
		}
		return this.configurazionePdDReader.existsPA(this.getConnection(), richiestaApplicativa);
	}

	public PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPA, RequestInfo requestInfo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		if(requestInfo!=null && requestInfo.getRequestConfig()!=null && idPA!=null && idPA.getNome()!=null) {
			if( requestInfo.getRequestConfig().getPortaApplicativa()!=null && idPA.getNome().equals(requestInfo.getRequestConfig().getPortaApplicativa().getNome())) {
				return requestInfo.getRequestConfig().getPortaApplicativa();
			}
			else if( requestInfo.getRequestConfig().getPortaApplicativaDefault()!=null && idPA.getNome().equals(requestInfo.getRequestConfig().getPortaApplicativaDefault().getNome())) {
				return requestInfo.getRequestConfig().getPortaApplicativaDefault();
			}
		}
		return this.configurazionePdDReader.getPortaApplicativa(this.getConnection(), idPA);
	}

	public PortaApplicativa getPortaApplicativa_SafeMethod(IDPortaApplicativa idPA, RequestInfo requestInfo)throws DriverConfigurazioneException{
		if(requestInfo!=null && requestInfo.getRequestConfig()!=null && idPA!=null && idPA.getNome()!=null) {
			if( requestInfo.getRequestConfig().getPortaApplicativa()!=null && idPA.getNome().equals(requestInfo.getRequestConfig().getPortaApplicativa().getNome())) {
				return requestInfo.getRequestConfig().getPortaApplicativa();
			}
			else if( requestInfo.getRequestConfig().getPortaApplicativaDefault()!=null && idPA.getNome().equals(requestInfo.getRequestConfig().getPortaApplicativaDefault().getNome())) {
				return requestInfo.getRequestConfig().getPortaApplicativaDefault();
			}
		}
		return this.configurazionePdDReader.getPortaApplicativa_SafeMethod(this.getConnection(), idPA);
	}
	
	public void updateStatoPortaApplicativa(IDPortaApplicativa idPA, StatoFunzionalita stato) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		this.configurazionePdDReader.updateStatoPortaApplicativa(this.getConnection(), idPA, stato);
	}
	
	public String updateStatoConnettoreMultiplo(IDPortaApplicativa idPA, String nomeConnettore, StatoFunzionalita stato) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.updateStatoConnettoreMultiplo(this.getConnection(), idPA, nomeConnettore, stato);
	}
	
	public String updateSchedulingConnettoreMultiplo(IDPortaApplicativa idPA, String nomeConnettore, StatoFunzionalita stato) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.updateSchedulingConnettoreMultiplo(this.getConnection(), idPA, nomeConnettore, stato);
	}
	
	public Map<String, String> getProprietaConfigurazione(PortaApplicativa pa) throws DriverConfigurazioneException {
		return this.configurazionePdDReader.getProprietaConfigurazione(pa);
	}

	public boolean identificazioneContentBased(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.identificazioneContentBased(pa);
	}

	public boolean identificazioneInputBased(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.identificazioneInputBased(pa);
	}

	public String getAzione(PortaApplicativa pa,URLProtocolContext urlProtocolContext,RequestInfo requestInfo,
			OpenSPCoop2Message message, OpenSPCoop2MessageSoapStreamReader soapStreamReader, HeaderIntegrazione headerIntegrazione,boolean readFirstHeaderIntegrazione,
			IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException, IdentificazioneDinamicaException { 
		return this.configurazionePdDReader.getAzione(this.registroServiziManager, pa, urlProtocolContext, requestInfo,
				message, soapStreamReader, headerIntegrazione, readFirstHeaderIntegrazione, protocolFactory);
	}

	public String[] getServiziApplicativi(PortaApplicativa pa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getServiziApplicativi(pa);
	}

	public SoggettoVirtuale getServiziApplicativi_SoggettiVirtuali(RichiestaApplicativa idPA)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getServiziApplicativi_SoggettiVirtuali(this.getConnection(), idPA);
	}

	public List<PortaApplicativa> getPorteApplicative(IDServizio idServizio, boolean ricercaPuntuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getPorteApplicative(this.getConnection(), idServizio, ricercaPuntuale);
	}

	public List<PortaApplicativa> getPorteApplicativeVirtuali(IDSoggetto idSoggettoVirtuale, IDServizio idServizio, boolean ricercaPuntuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getPorteApplicativeVirtuali(this.getConnection(), idSoggettoVirtuale, idServizio, ricercaPuntuale);
	}

	public MTOMProcessorConfig getPA_MTOMProcessorForSender(PortaApplicativa pa)throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getPA_MTOMProcessorForSender(pa);
	}

	public MTOMProcessorConfig getPA_MTOMProcessorForReceiver(PortaApplicativa pa)throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getPA_MTOMProcessorForReceiver(pa);
	}

	public MessageSecurityConfig getPA_MessageSecurityForSender(PortaApplicativa pa)throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getPA_MessageSecurityForSender(pa);
	}

	public MessageSecurityConfig getPA_MessageSecurityForReceiver(PortaApplicativa pa, Logger log, OpenSPCoop2Message message, Busta busta, RequestInfo requestInfo, PdDContext pddContext) throws DriverConfigurazioneException {
		MessageSecurityConfig config = this.configurazionePdDReader.getPA_MessageSecurityForReceiver(pa);
		this.resolveDynamicValue("getPA_MessageSecurityForReceiver[" + pa.getNome() + "]", config, log, message, busta, requestInfo, pddContext);
		return config;
	}

	public String getAutenticazione(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getAutenticazione(pa);
	}

	public boolean isAutenticazioneOpzionale(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.isAutenticazioneOpzionale(pa);
	}

	public String getGestioneToken(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getGestioneToken(pa);
	}

	public PolicyGestioneToken getPolicyGestioneToken(PortaApplicativa pa, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		String nome = null;
		if(pa!=null && pa.getGestioneToken()!=null && pa.getGestioneToken().getPolicy()!=null) {
			nome = pa.getGestioneToken().getPolicy();
		}
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && nome!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getPolicyValidazioneToken(nome);
			if(o!=null && o instanceof PolicyGestioneToken) {
				return (PolicyGestioneToken) o;
			}
		}
		PolicyGestioneToken policy = this.configurazionePdDReader.getPolicyGestioneToken(this.getConnection(), pa);
		if(useRequestInfo && requestInfo!=null) {
			requestInfo.getRequestConfig().addPolicyValidazioneToken(nome, policy, 
					requestInfo.getIdTransazione());
		}
		return policy;
	}

	public String getAutorizzazione(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getAutorizzazione(pa);
	}

	public String getAutorizzazioneContenuto(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getAutorizzazioneContenuto(pa);
	}

	public CorsConfigurazione getConfigurazioneCORS(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getConfigurazioneCORS(this.getConnection(), pa);
	}

	public ResponseCachingConfigurazione getConfigurazioneResponseCaching(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getConfigurazioneResponseCaching(this.getConnection(), pa);
	}

	public boolean ricevutaAsincronaSimmetricaAbilitata(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.ricevutaAsincronaSimmetricaAbilitata(pa);
	}

	public boolean ricevutaAsincronaAsimmetricaAbilitata(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.ricevutaAsincronaAsimmetricaAbilitata(pa);
	}

	public ValidazioneContenutiApplicativi getTipoValidazioneContenutoApplicativo(PortaApplicativa pa,String implementazionePdDSoggetto, boolean request) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getTipoValidazioneContenutoApplicativo(this.getConnection(), pa, implementazionePdDSoggetto, request);
	}

	public CorrelazioneApplicativa getCorrelazioneApplicativa(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getCorrelazioneApplicativa(pa);
	}

	public CorrelazioneApplicativaRisposta getCorrelazioneApplicativaRisposta(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getCorrelazioneApplicativaRisposta(pa);
	}

	public String[] getTipiIntegrazione(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound { 
		return this.configurazionePdDReader.getTipiIntegrazione(pa);
	}

	public boolean isGestioneManifestAttachments(PortaApplicativa pa, IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.isGestioneManifestAttachments(this.getConnection(), pa, protocolFactory);
	}

	public boolean isAllegaBody(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.isAllegaBody(pa);
	}

	public boolean isScartaBody(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.isScartaBody(pa);
	}

	public boolean isModalitaStateless(PortaApplicativa pa, ProfiloDiCollaborazione profiloCollaborazione) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.isModalitaStateless(pa, profiloCollaborazione);
	}

	public boolean autorizzazione(PortaApplicativa pa, IDSoggetto soggetto) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.autorizzazione(pa, soggetto);
	}

	public boolean autorizzazione(PortaApplicativa pa, IDServizioApplicativo servizioApplicativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.autorizzazione(pa, servizioApplicativo);
	}

	public boolean autorizzazioneTrasportoRoles(PortaApplicativa pa, Soggetto soggetto, ServizioApplicativo sa, InfoConnettoreIngresso infoConnettoreIngresso,
			PdDContext pddContext, RequestInfo requestInfo,
			boolean checkRuoloRegistro, boolean checkRuoloEsterno,
			StringBuilder details) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.autorizzazioneTrasportoRoles(this.registroServiziManager, pa, soggetto, sa, infoConnettoreIngresso, 
				pddContext, requestInfo,
				checkRuoloRegistro, checkRuoloEsterno, details);
	}
	
	public boolean autorizzazioneTokenRoles(PortaApplicativa pa, ServizioApplicativo sa, InfoConnettoreIngresso infoConnettoreIngresso,
			PdDContext pddContext, RequestInfo requestInfo,
			boolean checkRuoloRegistro, boolean checkRuoloEsterno,
			StringBuilder details) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.autorizzazioneTokenRoles(this.registroServiziManager, pa, sa, infoConnettoreIngresso, 
				pddContext, requestInfo,
				checkRuoloRegistro, checkRuoloEsterno, details);
	}

	public boolean isPortaAbilitata(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.isPortaAbilitata(pa);
	}

	public DumpConfigurazione getDumpConfigurazione(PortaApplicativa pa) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getDumpConfigurazione(this.getConnection(), pa);
	}
	
	public boolean isTransazioniFileTraceEnabled(PortaApplicativa pa) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.isTransazioniFileTraceEnabled(this.getConnection(), pa);
	}
	public boolean isTransazioniFileTraceDumpBinarioHeadersEnabled(PortaApplicativa pa) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.isTransazioniFileTraceDumpBinarioHeadersEnabled(this.getConnection(), pa);
	}
	public boolean isTransazioniFileTraceDumpBinarioPayloadEnabled(PortaApplicativa pa) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.isTransazioniFileTraceDumpBinarioPayloadEnabled(this.getConnection(), pa);
	}
	public boolean isTransazioniFileTraceDumpBinarioConnettoreHeadersEnabled(PortaApplicativa pa) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.isTransazioniFileTraceDumpBinarioConnettoreHeadersEnabled(this.getConnection(), pa);
	}
	public boolean isTransazioniFileTraceDumpBinarioConnettorePayloadEnabled(PortaApplicativa pa) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.isTransazioniFileTraceDumpBinarioConnettorePayloadEnabled(this.getConnection(), pa);
	}
	public File getFileTraceConfig(PortaApplicativa pa) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getFileTraceConfig(this.getConnection(), pa);
	}

	public SoglieDimensioneMessaggi getSoglieLimitedInputStream(PortaApplicativa pa, String azione, String idModulo,
			PdDContext pddContext, RequestInfo requestInfo, 
			IProtocolFactory<?> protocolFactory, Logger log) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getSoglieLimitedInputStream(this.getConnection(), pa, azione, idModulo,
				pddContext, requestInfo, 
				protocolFactory, log);
	}
	
	public boolean isConnettoriUseTimeoutInputStream(PortaApplicativa pa) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.isConnettoriUseTimeoutInputStream(this.getConnection(), pa);
	}
	public int getRequestReadTimeout(PortaApplicativa pa) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getRequestReadTimeout(this.getConnection(), pa);
	}
	
	public Trasformazioni getTrasformazioni(PortaApplicativa pa) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getTrasformazioni(pa);
	}

	public List<String> getPreInRequestHandlers(PortaApplicativa pa) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getPreInRequestHandlers(pa);
	}
	public List<String> getInRequestHandlers(PortaApplicativa pa) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getInRequestHandlers(pa);
	}
	public List<String> getInRequestProtocolHandlers(PortaApplicativa pa) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getInRequestProtocolHandlers(pa);
	}
	public List<String> getOutRequestHandlers(PortaApplicativa pa) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getOutRequestHandlers(pa);
	}
	public List<String> getPostOutRequestHandlers(PortaApplicativa pa) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getPostOutRequestHandlers(pa);
	}
	public List<String> getPreInResponseHandlers(PortaApplicativa pa) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getPreInResponseHandlers(pa);
	}
	public List<String> getInResponseHandlers(PortaApplicativa pa) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getInResponseHandlers(pa);
	}
	public List<String> getOutResponseHandlers(PortaApplicativa pa) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getOutResponseHandlers(pa);
	}
	public List<String> getPostOutResponseHandlers(PortaApplicativa pa) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getPostOutResponseHandlers(pa);
	}
	
	public List<Object> getExtendedInfo(PortaApplicativa pa)throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getExtendedInfo(pa);
	}
	
	public Template getTemplateTrasformazioneRichiesta(IDPortaApplicativa idPA, String nomeTrasformazione, TrasformazioneRegolaRichiesta richiesta, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getTemplateTrasformazioneRichiesta(this.getConnection(), idPA, nomeTrasformazione, richiesta, requestInfo);
	}
	public Template getTemplateTrasformazioneSoapRichiesta(IDPortaApplicativa idPA, String nomeTrasformazione, TrasformazioneRegolaRichiesta richiesta, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getTemplateTrasformazioneSoapRichiesta(this.getConnection(), idPA, nomeTrasformazione, richiesta, requestInfo);
	}
	public Template getTemplateTrasformazioneRisposta(IDPortaApplicativa idPA, String nomeTrasformazione, TrasformazioneRegolaRisposta risposta, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getTemplateTrasformazioneRisposta(this.getConnection(), idPA, nomeTrasformazione, risposta, requestInfo);
	}
	public Template getTemplateTrasformazioneSoapRisposta(IDPortaApplicativa idPA, String nomeTrasformazione, TrasformazioneRegolaRisposta risposta, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getTemplateTrasformazioneSoapRisposta(this.getConnection(), idPA, nomeTrasformazione, risposta, requestInfo);
	}
	
	public Template getTemplateConnettoreMultiploSticky(IDPortaApplicativa idPA, byte[] template, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getTemplateConnettoreMultiploSticky(this.getConnection(), idPA, template, requestInfo);
	}
	public Template getTemplateConnettoreMultiploCondizionale(IDPortaApplicativa idPA, String nomeRegola, byte[] template, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getTemplateConnettoreMultiploCondizionale(this.getConnection(), idPA, nomeRegola, template, requestInfo);
	}
	
	public Template getTemplateCorrelazioneApplicativaRichiesta(IDPortaApplicativa idPA, String nomeRegola, byte[] template, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getTemplateCorrelazioneApplicativaRichiesta(this.getConnection(), idPA, nomeRegola, template, requestInfo);
	}
	public Template getTemplateCorrelazioneApplicativaRisposta(IDPortaApplicativa idPA, String nomeRegola, byte[] template, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getTemplateCorrelazioneApplicativaRisposta(this.getConnection(), idPA, nomeRegola, template, requestInfo);
	}
	
	public Template getTemplateIntegrazione(IDPortaApplicativa idPA, File file, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getTemplateIntegrazione(this.getConnection(), idPA, file, requestInfo);
	}
		
	public void setInformazioniIntegrazioneDinamiche(Logger log, HttpServletTransportRequestContext transportRequestContext, Context context, PortaApplicativa pa) throws DriverConfigurazioneException {
		_setInformazioniIntegrazioneDinamiche(log, transportRequestContext, context, pa.getProprietaList());
	}
	
	public void setInformazioniIntegrazioneDinamiche(Logger log, TransportResponseContext transportResponseContext, Context context, PortaApplicativa pa) throws DriverConfigurazioneException {
		_setInformazioniIntegrazioneDinamiche(log, transportResponseContext, context, pa.getProprietaList());
	}




	/* ********  Servizi Applicativi (Interfaccia)  ******** */

	public boolean existsServizioApplicativo(IDServizioApplicativo idSA, RequestInfo requestInfo) throws DriverConfigurazioneException{ 
		if(requestInfo!=null && requestInfo.getRequestConfig()!=null && 
				idSA!=null && idSA.getIdSoggettoProprietario()!=null &&
				requestInfo.getRequestConfig().getIdServizio()!=null && requestInfo.getRequestConfig().getIdServizio().getSoggettoErogatore()!=null &&
				idSA.getIdSoggettoProprietario().equals(requestInfo.getRequestConfig().getIdServizio().getSoggettoErogatore())) {
			ServizioApplicativo sa = requestInfo.getRequestConfig().getServizioApplicativoErogatore(idSA.getNome());
			if(sa!=null) {
				return true;
			}
		}
		
		if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo()!=null) {
			if(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getIdServizioApplicativoFruitore()!=null && 
					requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getServizioApplicativoFruitore()!=null &&
					idSA!=null && idSA.equals(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getIdServizioApplicativoFruitore())) {
				return true;
			}
		}
		if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo()!=null) {
			if(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getIdServizioApplicativoFruitore()!=null && 
					requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getServizioApplicativoFruitore()!=null &&
					idSA!=null && idSA.equals(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getIdServizioApplicativoFruitore())) {
				return true;
			}
		}
		
		if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo()!=null) {
			if(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getServizioApplicativoFruitoreAnonimo()!=null &&
					idSA!=null && idSA.getNome()!=null && idSA.getNome().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getServizioApplicativoFruitoreAnonimo())) {
				//throw new DriverConfigurazioneNotFound("Servizio applicativo anonimo");
				return false;
			}
			if(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getIdServizioApplicativoFruitore()!=null && 
					requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getServizioApplicativoFruitore()!=null &&
					idSA!=null && idSA.equals(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getIdServizioApplicativoFruitore())) {
				return true;
			}
		}
		
		return this.configurazionePdDReader.existsServizioApplicativo(this.getConnection(), idSA);
	}

	public ServizioApplicativo getServizioApplicativo(IDServizioApplicativo idSA, RequestInfo requestInfo) throws DriverConfigurazioneNotFound,DriverConfigurazioneException{
		if(requestInfo!=null && requestInfo.getRequestConfig()!=null && 
				idSA!=null && idSA.getIdSoggettoProprietario()!=null &&
				requestInfo.getRequestConfig().getIdServizio()!=null && requestInfo.getRequestConfig().getIdServizio().getSoggettoErogatore()!=null &&
				idSA.getIdSoggettoProprietario().equals(requestInfo.getRequestConfig().getIdServizio().getSoggettoErogatore())) {
			ServizioApplicativo sa = requestInfo.getRequestConfig().getServizioApplicativoErogatore(idSA.getNome());
			if(sa!=null) {
				return sa;
			}
		}
		
		if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo()!=null) {
			if(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getIdServizioApplicativoFruitore()!=null && 
					requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getServizioApplicativoFruitore()!=null &&
					idSA!=null && idSA.equals(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getIdServizioApplicativoFruitore())) {
				return requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getServizioApplicativoFruitore();
			}
		}
		if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo()!=null) {
			if(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getIdServizioApplicativoFruitore()!=null && 
					requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getServizioApplicativoFruitore()!=null &&
					idSA!=null && idSA.equals(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getIdServizioApplicativoFruitore())) {
				return requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getServizioApplicativoFruitore();
			}
		}
		
		if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo()!=null) {
			if(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getServizioApplicativoFruitoreAnonimo()!=null &&
					idSA!=null && idSA.getNome()!=null && idSA.getNome().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getServizioApplicativoFruitoreAnonimo())) {
				throw new DriverConfigurazioneNotFound("Servizio applicativo anonimo");
			}
		}
		
		return this.configurazionePdDReader.getServizioApplicativo(this.getConnection(), idSA);
	}

	public IDServizioApplicativo getIdServizioApplicativoByCredenzialiBasic(String aUser,String aPassword, CryptConfig config) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getIdServizioApplicativoByCredenzialiBasic(this.getConnection(), aUser, aPassword, config);
	}
	
	public IDServizioApplicativo getIdServizioApplicativoByCredenzialiApiKey(String aUser,String aPassword, boolean appId, CryptConfig config) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getIdServizioApplicativoByCredenzialiApiKey(this.getConnection(), aUser, aPassword, appId, config);
	}

	public IDServizioApplicativo getIdServizioApplicativoByCredenzialiSsl(String aSubject, String aIssuer) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getIdServizioApplicativoByCredenzialiSsl(this.getConnection(), aSubject, aIssuer);
	}

	public IDServizioApplicativo getIdServizioApplicativoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getIdServizioApplicativoByCredenzialiSsl(this.getConnection(), certificate, strictVerifier);
	}

	public IDServizioApplicativo getIdServizioApplicativoByCredenzialiPrincipal(String principal) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getIdServizioApplicativoByCredenzialiPrincipal(this.getConnection(), principal);
	}
	
	public IDServizioApplicativo getIdServizioApplicativoByCredenzialiToken(String tokenPolicy, String tokenClientId) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getIdServizioApplicativoByCredenzialiToken(this.getConnection(), tokenPolicy, tokenClientId);
	}

	public boolean autorizzazione(PortaDelegata pd, String servizio) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.autorizzazione(pd, servizio);
	}

	public boolean autorizzazioneTrasportoRoles(PortaDelegata pd, ServizioApplicativo sa, InfoConnettoreIngresso infoConnettoreIngresso,
			PdDContext pddContext, RequestInfo requestInfo,
			boolean checkRuoloRegistro, boolean checkRuoloEsterno,
			StringBuilder details) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.autorizzazioneTrasportoRoles(this.registroServiziManager, pd, sa, infoConnettoreIngresso, 
				pddContext, requestInfo,
				checkRuoloRegistro, checkRuoloEsterno, details);
	}
	
	public boolean autorizzazioneTokenRoles(PortaDelegata pd, ServizioApplicativo sa, InfoConnettoreIngresso infoConnettoreIngresso,
			PdDContext pddContext, RequestInfo requestInfo,
			boolean checkRuoloRegistro, boolean checkRuoloEsterno,
			StringBuilder details) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.autorizzazioneTokenRoles(this.registroServiziManager, pd, sa, infoConnettoreIngresso, 
				pddContext, requestInfo,
				checkRuoloRegistro, checkRuoloEsterno, details);
	}

	public void aggiornaProprietaGestioneErrorePD(ProprietaErroreApplicativo gestioneErrore, ServizioApplicativo sa) throws DriverConfigurazioneException {
		this.configurazionePdDReader.aggiornaProprietaGestioneErrorePD(gestioneErrore, sa);
	}

	public boolean invocazionePortaDelegataPerRiferimento(ServizioApplicativo sa) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.invocazionePortaDelegataPerRiferimento(sa);
	}

	public boolean invocazionePortaDelegataSbustamentoInformazioniProtocollo(ServizioApplicativo sa) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.invocazionePortaDelegataSbustamentoInformazioniProtocollo(sa);
	}

	public List<String> getServiziApplicativiConsegnaNotifichePrioritarie(String queue) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getServiziApplicativiConsegnaNotifichePrioritarie(this.getConnection(),queue);
	}
	
	public List<IDConnettore> getConnettoriConsegnaNotifichePrioritarie(String queue) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getConnettoriConsegnaNotifichePrioritarie(this.getConnection(),queue);
	}
	
	public void resetConnettoriConsegnaNotifichePrioritarie(String queue) throws DriverConfigurazioneException{
		this.configurazionePdDReader.resetConnettoriConsegnaNotifichePrioritarie(this.getConnection(),queue);
	}
	
	public Map<String, String> getProprietaConfigurazione(ServizioApplicativo sa) throws DriverConfigurazioneException {
		return this.configurazionePdDReader.getProprietaConfigurazione(sa);
	}
	

	/* ********  Servizi Applicativi (InvocazioneServizio)  ******** */

	public boolean invocazioneServizioConGetMessage(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.invocazioneServizioConGetMessage(sa);
	}

	public boolean invocazioneServizioConSbustamento(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.invocazioneServizioConSbustamento(sa);
	}

	public boolean invocazioneServizioConSbustamentoInformazioniProtocollo(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.invocazioneServizioConSbustamentoInformazioniProtocollo(sa);
	}

	public boolean invocazioneServizioConConnettore(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.invocazioneServizioConConnettore(sa);
	}

	public ConnettoreMsg getInvocazioneServizio(ServizioApplicativo sa,RichiestaApplicativa idPA, RequestInfo requestInfo)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getInvocazioneServizio(this.getConnection(), sa, idPA, requestInfo);
	}

	public GestioneErrore getGestioneErroreConnettore_InvocazioneServizio(IProtocolFactory<?> protocolFactory, ServiceBinding serviceBinding, ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getGestioneErroreConnettore_InvocazioneServizio(protocolFactory, serviceBinding, this.getConnection(), sa);
	}

	public boolean invocazioneServizioPerRiferimento(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.invocazioneServizioPerRiferimento(sa);
	}

	public boolean invocazioneServizioRispostaPerRiferimento(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.invocazioneServizioRispostaPerRiferimento(sa);
	}


	/* ********  Servizi Applicativi (RispostAsincrona)  ******** */

	public boolean existsConsegnaRispostaAsincrona(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound { 
		return this.configurazionePdDReader.existsConsegnaRispostaAsincrona(sa);
	}

	public boolean consegnaRispostaAsincronaConGetMessage(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.consegnaRispostaAsincronaConGetMessage(sa);
	}

	public boolean consegnaRispostaAsincronaConSbustamento(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.consegnaRispostaAsincronaConSbustamento(sa);
	}

	public boolean consegnaRispostaAsincronaConSbustamentoInformazioniProtocollo(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.consegnaRispostaAsincronaConSbustamentoInformazioniProtocollo(sa);
	}

	public boolean consegnaRispostaAsincronaConConnettore(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.consegnaRispostaAsincronaConConnettore(sa);
	}

	public ConnettoreMsg getConsegnaRispostaAsincrona(ServizioApplicativo sa,RichiestaDelegata idPD, RequestInfo requestInfo)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getConsegnaRispostaAsincrona(this.getConnection(), sa, idPD, requestInfo);
	}

	public ConnettoreMsg getConsegnaRispostaAsincrona(ServizioApplicativo sa,RichiestaApplicativa idPA, RequestInfo requestInfo)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getConsegnaRispostaAsincrona(this.getConnection(), sa, idPA, requestInfo);
	}

	public GestioneErrore getGestioneErroreConnettore_RispostaAsincrona(IProtocolFactory<?> protocolFactory, ServiceBinding serviceBinding, ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getGestioneErroreConnettore_RispostaAsincrona(protocolFactory, serviceBinding, this.getConnection(), sa);
	}

	public boolean consegnaRispostaAsincronaPerRiferimento(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.consegnaRispostaAsincronaPerRiferimento(sa);
	}

	public boolean consegnaRispostaAsincronaRispostaPerRiferimento(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.consegnaRispostaAsincronaRispostaPerRiferimento(sa);
	}

	public CertificateCheck checkCertificatoApplicativoWithoutCache(long idSA, int sogliaWarningGiorni,  
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.configurazionePdDReader.checkCertificatoApplicativo(null, false,
				idSA, sogliaWarningGiorni,  
				addCertificateDetails, separator, newLine);
	}

	public CertificateCheck checkCertificatoApplicativoWithoutCache(IDServizioApplicativo idSA, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.configurazionePdDReader.checkCertificatoApplicativo(null, false,
				idSA, sogliaWarningGiorni,  
				addCertificateDetails, separator, newLine);
	}
	
	public CertificateCheck checkCertificatoModiApplicativoWithoutCache(long idSA, int sogliaWarningGiorni,  
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.configurazionePdDReader.checkCertificatoModiApplicativo(null, false,
				idSA, sogliaWarningGiorni,  
				addCertificateDetails, separator, newLine);
	}

	public CertificateCheck checkCertificatoModiApplicativoWithoutCache(IDServizioApplicativo idSA, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.configurazionePdDReader.checkCertificatoModiApplicativo(null, false,
				idSA, sogliaWarningGiorni,  
				addCertificateDetails, separator, newLine);
	}
	
	public CertificateCheck checkCertificatiConnettoreHttpsByIdWithoutCache(long idConnettore, int sogliaWarningGiorni,  
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.configurazionePdDReader.checkCertificatiConnettoreHttpsById(null, false,
				idConnettore, sogliaWarningGiorni,  
				addCertificateDetails, separator, newLine);
	}
	
	public CertificateCheck checkCertificatiJvm(int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException {
		return this.configurazionePdDReader.checkCertificatiJvm(sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine);
	}

	public CertificateCheck checkCertificatiConnettoreHttpsTokenPolicyValidazione(String nomePolicy, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.configurazionePdDReader.checkCertificatiConnettoreHttpsTokenPolicyValidazione(null, false,
				nomePolicy, null, sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine);
	}
	public CertificateCheck checkCertificatiConnettoreHttpsTokenPolicyValidazione(String nomePolicy, String tipo, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.configurazionePdDReader.checkCertificatiConnettoreHttpsTokenPolicyValidazione(null, false,
				nomePolicy, tipo, sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine);
	}
	
	public CertificateCheck checkCertificatiValidazioneJwtTokenPolicyValidazione(String nomePolicy, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.configurazionePdDReader.checkCertificatiValidazioneJwtTokenPolicyValidazione(null, false,
				nomePolicy, sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine);
	}
	
	public CertificateCheck checkCertificatiForwardToJwtTokenPolicyValidazione(String nomePolicy, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.configurazionePdDReader.checkCertificatiForwardToJwtTokenPolicyValidazione(null, false,
				nomePolicy, sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine);
	}
	
	public CertificateCheck checkCertificatiConnettoreHttpsTokenPolicyNegoziazione(String nomePolicy, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.configurazionePdDReader.checkCertificatiConnettoreHttpsTokenPolicyNegoziazione(null, false,
				nomePolicy, sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine);
	}
	
	public CertificateCheck checkCertificatiSignedJwtTokenPolicyNegoziazione(String nomePolicy, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.configurazionePdDReader.checkCertificatiSignedJwtTokenPolicyNegoziazione(null, false,
				nomePolicy, sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine);
	}
	
	public CertificateCheck checkCertificatiConnettoreHttpsAttributeAuthority(String nomePolicy, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.configurazionePdDReader.checkCertificatiConnettoreHttpsAttributeAuthority(null, false,
				nomePolicy, sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine);
	}
	
	public CertificateCheck checkCertificatiAttributeAuthorityJwtRichiesta(String nomePolicy, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.configurazionePdDReader.checkCertificatiAttributeAuthorityJwtRichiesta(null, false,
				nomePolicy, sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine);
	}
	
	public CertificateCheck checkCertificatiAttributeAuthorityJwtRisposta(String nomePolicy, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.configurazionePdDReader.checkCertificatiAttributeAuthorityJwtRisposta(null, false,
				nomePolicy, sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine);
	}
	
	

	

	/* ********  CONFIGURAZIONE  ******** */

	public AccessoRegistro getAccessoRegistroServizi(){
		return this.configurazionePdDReader.getAccessoRegistroServizi(this.getConnection());
	}

	public AccessoConfigurazione getAccessoConfigurazione(){
		return this.configurazionePdDReader.getAccessoConfigurazione(this.getConnection());
	}

	public AccessoDatiAutorizzazione getAccessoDatiAutorizzazione(){
		return this.configurazionePdDReader.getAccessoDatiAutorizzazione(this.getConnection());
	}

	public AccessoDatiAutenticazione getAccessoDatiAutenticazione(){
		return this.configurazionePdDReader.getAccessoDatiAutenticazione(this.getConnection());
	}

	public AccessoDatiGestioneToken getAccessoDatiGestioneToken(){
		return this.configurazionePdDReader.getAccessoDatiGestioneToken(this.getConnection());
	}

	public AccessoDatiKeystore getAccessoDatiKeystore(){
		return this.configurazionePdDReader.getAccessoDatiKeystore(this.getConnection());
	}
	
	public AccessoDatiRichieste getAccessoDatiRichieste(){
		return this.configurazionePdDReader.getAccessoDatiRichieste(this.getConnection());
	}

	public StatoFunzionalitaConWarning getTipoValidazione(String implementazionePdDSoggetto){
		return this.configurazionePdDReader.getTipoValidazione(this.getConnection(), implementazionePdDSoggetto);
	}

	public boolean isLivelloValidazioneNormale(String implementazionePdDSoggetto){
		return this.configurazionePdDReader.isLivelloValidazioneNormale(this.getConnection(), implementazionePdDSoggetto);
	}

	public boolean isLivelloValidazioneRigido(String implementazionePdDSoggetto){
		return this.configurazionePdDReader.isLivelloValidazioneRigido(this.getConnection(), implementazionePdDSoggetto);
	}

	public boolean isValidazioneProfiloCollaborazione(String implementazionePdDSoggetto){
		return this.configurazionePdDReader.isValidazioneProfiloCollaborazione(this.getConnection(), implementazionePdDSoggetto);
	}

	public boolean isValidazioneManifestAttachments(String implementazionePdDSoggetto){
		return this.configurazionePdDReader.isValidazioneManifestAttachments(this.getConnection(), implementazionePdDSoggetto);
	}

	public boolean newConnectionForResponse(){
		return this.configurazionePdDReader.newConnectionForResponse(this.getConnection());
	}

	public boolean isUtilizzoIndirizzoTelematico(){
		return this.configurazionePdDReader.isUtilizzoIndirizzoTelematico(this.getConnection());
	}

	public boolean isGestioneManifestAttachments(){
		return this.configurazionePdDReader.isGestioneManifestAttachments(this.getConnection());
	}

	public long getTimeoutRiscontro(){
		return this.configurazionePdDReader.getTimeoutRiscontro(this.getConnection());
	}

	public Level getLivello_msgDiagnostici(){
		return this.configurazionePdDReader.getLivello_msgDiagnostici(this.getConnection());
	}

	public Level getLivelloLog4J_msgDiagnostici(){
		return this.configurazionePdDReader.getLivelloLog4J_msgDiagnostici(this.getConnection());
	}

	public int getSeverita_msgDiagnostici(){
		return this.configurazionePdDReader.getSeverita_msgDiagnostici(this.getConnection());
	}

	public int getSeveritaLog4J_msgDiagnostici(){
		return this.configurazionePdDReader.getSeveritaLog4J_msgDiagnostici(this.getConnection());
	}

	public MessaggiDiagnostici getOpenSPCoopAppender_MsgDiagnostici(){
		return this.configurazionePdDReader.getOpenSPCoopAppender_MsgDiagnostici(this.getConnection());
	}

	public boolean tracciamentoBuste(){
		return this.configurazionePdDReader.tracciamentoBuste(this.getConnection());
	}

	public Tracciamento getOpenSPCoopAppender_Tracciamento(){
		return this.configurazionePdDReader.getOpenSPCoopAppender_Tracciamento(this.getConnection());
	}

	public Transazioni getTransazioniConfigurazione() {
		return this.configurazionePdDReader.getTransazioniConfigurazione(this.getConnection());
	}

	public DumpConfigurazione getDumpConfigurazionePortaApplicativa() {
		return this.configurazionePdDReader.getDumpConfigurazionePortaApplicativa(this.getConnection());
	}
	
	public DumpConfigurazione getDumpConfigurazionePortaDelegata() {
		return this.configurazionePdDReader.getDumpConfigurazionePortaDelegata(this.getConnection());
	}

	public boolean dumpBinarioPD(){
		return this.configurazionePdDReader.dumpBinarioPD(this.getConnection());
	}

	public boolean dumpBinarioPA(){
		return this.configurazionePdDReader.dumpBinarioPA(this.getConnection());
	}

	public Dump getOpenSPCoopAppender_Dump(){
		return this.configurazionePdDReader.getOpenSPCoopAppender_Dump(this.getConnection());
	}

	public GestioneErrore getGestioneErroreConnettoreComponenteCooperazione(IProtocolFactory<?> protocolFactory, ServiceBinding serviceBinding){
		return this.configurazionePdDReader.getGestioneErroreConnettoreComponenteCooperazione(protocolFactory, serviceBinding, this.getConnection());
	}

	public GestioneErrore getGestioneErroreConnettoreComponenteIntegrazione(IProtocolFactory<?> protocolFactory, ServiceBinding serviceBinding){
		return this.configurazionePdDReader.getGestioneErroreConnettoreComponenteIntegrazione(protocolFactory, serviceBinding, this.getConnection());
	}

	public String[] getIntegrationManagerAuthentication(){
		return this.configurazionePdDReader.getIntegrationManagerAuthentication(this.getConnection());
	}

	public ValidazioneContenutiApplicativi getTipoValidazioneContenutoApplicativo(String implementazionePdDSoggetto) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getTipoValidazioneContenutoApplicativo(this.getConnection(), implementazionePdDSoggetto);
	}

	public Boolean isPDServiceActive(){
		return this.configurazionePdDReader.isPDServiceActive();
	}

	public List<TipoFiltroAbilitazioneServizi> getFiltriAbilitazionePDService(){
		return this.configurazionePdDReader.getFiltriAbilitazionePDService();
	}

	public List<TipoFiltroAbilitazioneServizi> getFiltriDisabilitazionePDService(){
		return this.configurazionePdDReader.getFiltriDisabilitazionePDService();
	}

	public Boolean isPAServiceActive(){
		return this.configurazionePdDReader.isPAServiceActive();
	}

	public List<TipoFiltroAbilitazioneServizi> getFiltriAbilitazionePAService(){
		return this.configurazionePdDReader.getFiltriAbilitazionePAService();
	}

	public List<TipoFiltroAbilitazioneServizi> getFiltriDisabilitazionePAService(){
		return this.configurazionePdDReader.getFiltriDisabilitazionePAService();
	}

	public Boolean isIMServiceActive(){
		return this.configurazionePdDReader.isIMServiceActive();
	}

	public StatoServiziPdd getStatoServiziPdD() throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getStatoServiziPdD();
	}

	public void updateStatoServiziPdD(StatoServiziPdd servizi) throws DriverConfigurazioneException{
		this.configurazionePdDReader.updateStatoServiziPdD(servizi);
	}

	public PolicyNegoziazioneToken getPolicyNegoziazioneToken(boolean forceNoCache, String policyName, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		boolean useRequestInfo = !forceNoCache && requestInfo!=null && requestInfo.getRequestConfig()!=null && policyName!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getPolicyNegoziazioneToken(policyName);
			if(o!=null && o instanceof PolicyNegoziazioneToken) {
				return (PolicyNegoziazioneToken) o;
			}
		}
		PolicyNegoziazioneToken policy = this.configurazionePdDReader.getPolicyNegoziazioneToken(this.getConnection(), forceNoCache, policyName);
		if(useRequestInfo && requestInfo!=null) {
			requestInfo.getRequestConfig().addPolicyNegoziazioneToken(policyName, policy, 
					requestInfo.getIdTransazione());
		}
		return policy;
	}
	
	public PolicyAttributeAuthority getPolicyAttributeAuthority(boolean forceNoCache, String policyName, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		boolean useRequestInfo = !forceNoCache && requestInfo!=null && requestInfo.getRequestConfig()!=null && policyName!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getAttributeAuthority(policyName);
			if(o!=null && o instanceof PolicyAttributeAuthority) {
				return (PolicyAttributeAuthority) o;
			}
		}
		PolicyAttributeAuthority policy = this.configurazionePdDReader.getPolicyAttributeAuthority(this.getConnection(), forceNoCache, policyName);
		if(useRequestInfo && requestInfo!=null) {
			requestInfo.getRequestConfig().addAttributeAuthority(policyName, policy, 
					requestInfo.getIdTransazione());
		}
		return policy;
	}

	public GenericProperties getGenericProperties(String tipologia, String nome) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getGenericProperties(this.getConnection(), tipologia, nome);
	}

	public List<GenericProperties> getGenericProperties(String tipologia) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getGenericProperties(this.getConnection(), tipologia);
	}

	public SystemProperties getSystemPropertiesPdDCached(RequestInfo requestInfo) throws DriverConfigurazioneException{
		if(requestInfo!=null && requestInfo.getRequestConfig()!=null && requestInfo.getRequestConfig().getSystemProperties()!=null) {
			return requestInfo.getRequestConfig().getSystemProperties();
		}
		SystemProperties sp = this.configurazionePdDReader.getSystemPropertiesPdDCached(this.getConnection());
		if(requestInfo!=null && requestInfo.getRequestConfig()!=null && requestInfo.getRequestConfig().getSystemProperties()==null) {
			requestInfo.getRequestConfig().setSystemProperties(sp);
		}
		return sp;
	}
	
	public SystemProperties getSystemPropertiesPdD() throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getSystemPropertiesPdD();
	}

	public void updateSystemPropertiesPdD(SystemProperties systemProperties) throws DriverConfigurazioneException{
		this.configurazionePdDReader.updateSystemPropertiesPdD(systemProperties);
	}

	public CorsConfigurazione getConfigurazioneCORS() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getConfigurazioneCORS(this.getConnection());
	}

	public ConfigurazioneMultitenant getConfigurazioneMultitenant() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getConfigurazioneMultitenant(this.getConnection());
	}

	public ResponseCachingConfigurazione getConfigurazioneResponseCaching() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getConfigurazioneResponseCaching(this.getConnection());
	}

	public Cache getConfigurazioneResponseCachingCache() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getConfigurazioneResponseCachingCache(this.getConnection());
	}

	public Cache getConfigurazioneConsegnaApplicativiCache() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getConfigurazioneConsegnaApplicativiCache(this.getConnection());
	}

	public CanaliConfigurazione getCanaliConfigurazione() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getCanaliConfigurazione(this.getConnection());
	}
	
	public ConfigurazioneCanaliNodo getConfigurazioneCanaliNodo() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getConfigurazioneCanaliNodo(this.getConnection());
	}
	
	public UrlInvocazioneAPI getConfigurazioneUrlInvocazione(IProtocolFactory<?> protocolFactory, RuoloContesto ruolo, ServiceBinding serviceBinding, 
			String interfaceName, IDSoggetto soggettoOperativo,
			IDAccordo idAccordo, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		AccordoServizioParteComune aspc = null;
		try {
			aspc = this.registroServiziManager.getAccordoServizioParteComune(idAccordo, null, false, false, requestInfo);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		return this.configurazionePdDReader.getConfigurazioneUrlInvocazione(this.getConnection(),
				protocolFactory, ruolo, serviceBinding, interfaceName, soggettoOperativo, 
				aspc, 
				requestInfo);
	}
	public UrlInvocazioneAPI getConfigurazioneUrlInvocazione(IProtocolFactory<?> protocolFactory, RuoloContesto ruolo, ServiceBinding serviceBinding, 
			String interfaceName, IDSoggetto soggettoOperativo,
			AccordoServizioParteComune aspc, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getConfigurazioneUrlInvocazione(this.getConnection(),
				protocolFactory, ruolo, serviceBinding, interfaceName, soggettoOperativo, 
				aspc, 
				requestInfo);
	}
	public UrlInvocazioneAPI getConfigurazioneUrlInvocazione(IProtocolFactory<?> protocolFactory, RuoloContesto ruolo, ServiceBinding serviceBinding, 
			String interfaceName, IDSoggetto soggettoOperativo,
			List<String> tags, 
			String canaleApi, 
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getConfigurazioneUrlInvocazione(this.getConnection(),
				protocolFactory, ruolo, serviceBinding, interfaceName, soggettoOperativo, 
				tags, 
				canaleApi, 
				requestInfo);
	}
	
	public List<PolicyGroupByActiveThreadsType> getTipiGestoreRateLimiting() throws DriverConfigurazioneException {
		return this.configurazionePdDReader.getTipiGestoreRateLimiting(this.getConnection());
	}
	
	public List<String> getInitHandlers() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getInitHandlers(this.getConnection());
	}
	public List<String> getExitHandlers() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getExitHandlers(this.getConnection());
	}
	public List<String> getIntegrationManagerRequestHandlers() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getIntegrationManagerRequestHandlers(this.getConnection());
	}
	public List<String> getIntegrationManagerResponseHandlers() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getIntegrationManagerResponseHandlers(this.getConnection());
	}
	
	public List<String> getPreInRequestHandlers() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getPreInRequestHandlers(this.getConnection());
	}
	public List<String> getInRequestHandlers() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getInRequestHandlers(this.getConnection());
	}
	public List<String> getInRequestProtocolHandlers() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getInRequestProtocolHandlers(this.getConnection());
	}
	public List<String> getOutRequestHandlers() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getOutRequestHandlers(this.getConnection());
	}
	public List<String> getPostOutRequestHandlers() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getPostOutRequestHandlers(this.getConnection());
	}
	public List<String> getPreInResponseHandlers() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getPreInResponseHandlers(this.getConnection());
	}
	public List<String> getInResponseHandlers() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getInResponseHandlers(this.getConnection());
	}
	public List<String> getOutResponseHandlers() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getOutResponseHandlers(this.getConnection());
	}
	public List<String> getPostOutResponseHandlers() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getPostOutResponseHandlers(this.getConnection());
	}

	public List<Object> getExtendedInfoConfigurazione() throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getExtendedInfoConfigurazione(this.getConnection());
	}

	public Object getSingleExtendedInfoConfigurazione(String id) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getSingleExtendedInfoConfigurazione(id, this.getConnection());
	}

	public List<Object> getExtendedInfoConfigurazioneFromCache() throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getExtendedInfoConfigurazioneFromCache(this.getConnection());
	}

	public Object getSingleExtendedInfoConfigurazioneFromCache(String id) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getSingleExtendedInfoConfigurazioneFromCache(id, this.getConnection());
	}
	
	public Template getTemplatePolicyNegoziazioneRequest(String policyName, byte[] template, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getTemplatePolicyNegoziazioneRequest(this.getConnection(), policyName, template, requestInfo);
	}
	
	public Template getTemplateAttributeAuthorityRequest(String attributeAuthorityName, byte[] template, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getTemplateAttributeAuthorityRequest(this.getConnection(), attributeAuthorityName, template, requestInfo);
	}
	
	public Template getTemplateIntegrazione(File file, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getTemplateIntegrazione(this.getConnection(), file, requestInfo);
	}


	/* ********  R I C E R C A  I D   E L E M E N T I   P R I M I T I V I  ******** */

	public List<IDPortaDelegata> getAllIdPorteDelegate(FiltroRicercaPorteDelegate filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getAllIdPorteDelegate(filtroRicerca, this.getConnection());
	}

	public List<IDPortaApplicativa> getAllIdPorteApplicative(FiltroRicercaPorteApplicative filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getAllIdPorteApplicative(filtroRicerca, this.getConnection());
	}

	public List<IDServizioApplicativo> getAllIdServiziApplicativi(FiltroRicercaServiziApplicativi filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getAllIdServiziApplicativi(filtroRicerca, this.getConnection());
	}

	/* ******** CONTROLLO TRAFFICO ******** */

	public ConfigurazioneGenerale getConfigurazioneControlloTraffico(RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestRateLimitingConfig()!=null;
		if(useRequestInfo) {
			if(requestInfo.getRequestRateLimitingConfig().getConfigurazioneGenerale()!=null) {
				return requestInfo.getRequestRateLimitingConfig().getConfigurazioneGenerale();
			}
		}
		ConfigurazioneGenerale config = this.configurazionePdDReader.getConfigurazioneControlloTraffico(this.getConnection());
		if(useRequestInfo && requestInfo.getRequestRateLimitingConfig().getConfigurazioneGenerale()==null) {
			requestInfo.getRequestRateLimitingConfig().setConfigurazioneGenerale(config);
		}
		return config;
	}

	public PolicyConfiguration getConfigurazionePolicyRateLimitingGlobali(RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestRateLimitingConfig()!=null;
		if(useRequestInfo) {
			if(requestInfo.getRequestRateLimitingConfig().getConfigurazionePolicyRateLimitingGlobali()!=null) {
				Object o = requestInfo.getRequestRateLimitingConfig().getConfigurazionePolicyRateLimitingGlobali();
				if(o instanceof PolicyConfiguration) {
					return (PolicyConfiguration) o;
				}
			}
		}
		PolicyConfiguration config = this.configurazionePdDReader.getConfigurazionePolicyRateLimitingGlobali(this.getConnection());
		if(useRequestInfo && requestInfo.getRequestRateLimitingConfig().getConfigurazionePolicyRateLimitingGlobali()==null) {
			requestInfo.getRequestRateLimitingConfig().setConfigurazionePolicyRateLimitingGlobali(config);
		}
		return config;
	}
	
	public Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> getElencoIdPolicyAttiveAPI(boolean useCache, TipoPdD tipoPdD, String nomePorta) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getElencoIdPolicyAttiveAPI(this.getConnection(), useCache, tipoPdD, nomePorta);
	}

	public Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> getElencoIdPolicyAttiveGlobali(boolean useCache) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getElencoIdPolicyAttiveGlobali(this.getConnection(), useCache);
	}
	
	public Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> getElencoIdPolicyAttiveAPI_dimensioneMessaggio(boolean useCache, TipoPdD tipoPdD, String nomePorta) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getElencoIdPolicyAttiveAPI_dimensioneMessaggio(this.getConnection(), useCache, tipoPdD, nomePorta);
	}

	public Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> getElencoIdPolicyAttiveGlobali_dimensioneMessaggio(boolean useCache) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getElencoIdPolicyAttiveGlobali_dimensioneMessaggio(this.getConnection(), useCache);
	}

	public AttivazionePolicy getAttivazionePolicy(boolean useCache, String id) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getAttivazionePolicy(this.getConnection(), useCache, id);
	}

	public ElencoIdPolicy getElencoIdPolicy(boolean useCache) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getElencoIdPolicy(this.getConnection(), useCache);
	}

	public ConfigurazionePolicy getConfigurazionePolicy(boolean useCache, String id) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getConfigurazionePolicy(this.getConnection(), useCache, id);
	}
	
	
	/* ******** PLUGINS ******** */
	
	public IRegistroPluginsReader getRegistroPluginsReader() {
		return this.configurazionePdDReader.getRegistroPluginsReader();
	}
	
	public String getPluginClassName(String tipoPlugin, String tipo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getPluginClassName(this.getConnection(), tipoPlugin, tipo);
	}
	public String getPluginClassNameByFilter(String tipoPlugin, String tipo, NameValue ... filtri) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getPluginClassNameByFilter(this.getConnection(), tipoPlugin, tipo, filtri);
	}
	
	public String getPluginTipo(String tipoPlugin, String className) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getPluginTipo(this.getConnection(), tipoPlugin, className);
	}
	public String getPluginTipoByFilter(String tipoPlugin, String className, NameValue ... filtri) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getPluginTipoByFilter(this.getConnection(), tipoPlugin, className, filtri);
	}
	
	
	/* ******** ALLARMI ******** */
	
	public Allarme getAllarme(String nomeAllarme, boolean searchInCache) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.configurazionePdDReader.getAllarme(this.getConnection(), nomeAllarme, searchInCache);
	}
	public List<Allarme> searchAllarmi(FiltroRicercaAllarmi filtroRicerca, boolean searchInCache) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.configurazionePdDReader.searchAllarmi(this.getConnection(), filtroRicerca, searchInCache);
	}
	public List<IAlarm> instanceAllarmi(List<Allarme> listAllarmi) throws DriverConfigurazioneException {
		return this.configurazionePdDReader.instanceAllarmi(this.getConnection(), listAllarmi);
	}
	public void changeStatus(IAlarm alarm, AlarmStatus nuovoStatoAllarme) throws DriverConfigurazioneException {
		this.configurazionePdDReader.changeStatus(this.getConnection(), alarm, nuovoStatoAllarme);
	}

	
	/* ******** MAPPING ******** */

	public List<MappingErogazionePortaApplicativa> getMappingErogazionePortaApplicativaList(IDServizio idServizio, RequestInfo requestInfo) throws DriverConfigurazioneException{
		if(requestInfo!=null && requestInfo.getRequestConfig()!=null && 
				idServizio!=null && requestInfo.getRequestConfig().getIdServizio()!=null ) {
			if( requestInfo.getRequestConfig().getListMappingErogazionePortaApplicativa()!=null && 
					idServizio.equals(requestInfo.getRequestConfig().getIdServizio(),false)) {
				return requestInfo.getRequestConfig().getListMappingErogazionePortaApplicativa();
			}
		}
		List<MappingErogazionePortaApplicativa> list = this.configurazionePdDReader.getMappingErogazionePortaApplicativaList(idServizio, this.getConnection());
		if(requestInfo!=null && requestInfo.getRequestConfig()!=null && requestInfo.getRequestConfig().getListMappingErogazionePortaApplicativa()==null) {
			requestInfo.getRequestConfig().setListMappingErogazionePortaApplicativa(list);
		}
		return list;
	} 
	public List<MappingFruizionePortaDelegata> getMappingFruizionePortaDelegataList(IDSoggetto idFruitore, IDServizio idServizio, RequestInfo requestInfo) throws DriverConfigurazioneException{
		if(requestInfo!=null && requestInfo.getRequestConfig()!=null && 
				idServizio!=null && requestInfo.getRequestConfig().getIdServizio()!=null && 
				idFruitore!=null && requestInfo.getRequestConfig().getIdFruitore()!=null) {
			if( requestInfo.getRequestConfig().getListMappingFruizionePortaDelegata()!=null && 
					idServizio.equals(requestInfo.getRequestConfig().getIdServizio(),false) && 
					idFruitore.equals(requestInfo.getRequestConfig().getIdFruitore())) {
				return requestInfo.getRequestConfig().getListMappingFruizionePortaDelegata();
			}
		}
		List<MappingFruizionePortaDelegata> list = this.configurazionePdDReader.getMappingFruizionePortaDelegataList(idFruitore, idServizio, this.getConnection());
		if(requestInfo!=null && requestInfo.getRequestConfig()!=null && requestInfo.getRequestConfig().getListMappingFruizionePortaDelegata()==null) {
			requestInfo.getRequestConfig().setListMappingFruizionePortaDelegata(list);
		}
		return list;
	} 
	
	/* ******** FORWARD PROXY ******** */
	
	public boolean isForwardProxyEnabled(RequestInfo requestInfo) {
		if(requestInfo!=null && requestInfo.getRequestConfig()!=null && requestInfo.getRequestConfig().getForwardProxyEnabled()!=null) {
			return requestInfo.getRequestConfig().getForwardProxyEnabled();
		}
		boolean b = this.configurazionePdDReader.isForwardProxyEnabled();
		if(requestInfo!=null && requestInfo.getRequestConfig()!=null && requestInfo.getRequestConfig().getForwardProxyEnabled()==null) {
			requestInfo.getRequestConfig().setForwardProxyEnabled(b);
		}
		return b;
	}
	public ForwardProxy getForwardProxyConfigFruizione(IDSoggetto dominio, IDServizio idServizio, IDGenericProperties policy, RequestInfo requestInfo) throws DriverConfigurazioneException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && requestInfo.getRequestConfig().getForwardProxyEnabled()!=null;
		String key = null;
		if(useRequestInfo) {
			key = ConfigurazionePdD._getKey_ForwardProxyConfig(true, dominio, idServizio, policy);
			Object o = requestInfo.getRequestConfig().getForwardProxy(key);
			if(o!=null && o instanceof ForwardProxy) {
				return (ForwardProxy) o;
			}
		}
		ForwardProxy fp = this.configurazionePdDReader.getForwardProxyConfigFruizione(dominio, idServizio, policy, requestInfo);
		if(useRequestInfo && requestInfo!=null) {
			requestInfo.getRequestConfig().addForwardProxy(key, fp, 
					requestInfo.getIdTransazione());
		}
		return fp;
	}
	public ForwardProxy getForwardProxyConfigErogazione(IDSoggetto dominio, IDServizio idServizio, IDGenericProperties policy, RequestInfo requestInfo) throws DriverConfigurazioneException{
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null && requestInfo.getRequestConfig().getForwardProxyEnabled()!=null;
		String key = null;
		if(useRequestInfo) {
			key = ConfigurazionePdD._getKey_ForwardProxyConfig(false, dominio, idServizio, policy);
			Object o = requestInfo.getRequestConfig().getForwardProxy(key);
			if(o!=null && o instanceof ForwardProxy) {
				return (ForwardProxy) o;
			}
		}
		ForwardProxy fp = this.configurazionePdDReader.getForwardProxyConfigErogazione(dominio, idServizio, policy, requestInfo);
		if(useRequestInfo && requestInfo!=null) {
			requestInfo.getRequestConfig().addForwardProxy(key, fp, 
					requestInfo.getIdTransazione());
		}
		return fp;
	}
	
	/* ********  GENERIC FILE  ******** */

	public ContentFile getContentFile(File file)throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getContentFile(file);
	}
}
