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

package org.openspcoop2.pdd.config;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiAutenticazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoDatiGestioneToken;
import org.openspcoop2.core.config.AccessoDatiKeystore;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.Cache;
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
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.StatoServiziPdd;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.Transazioni;
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
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.ElencoIdPolicy;
import org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.id.IdentificativiErogazione;
import org.openspcoop2.core.id.IdentificativiFruizione;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.core.dynamic.ErrorHandler;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.token.PolicyGestioneToken;
import org.openspcoop2.pdd.core.token.PolicyNegoziazioneToken;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.mapping.IdentificazioneDinamicaException;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.slf4j.Logger;
import org.w3c.dom.Element;

/**
 * ConfigurazionePdDManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazionePdDManager {


	public static ConfigurazionePdDManager getInstance(){
		return new ConfigurazionePdDManager();
	}
	public static ConfigurazionePdDManager getInstance(IState ... state){
		return new ConfigurazionePdDManager(state);
	}


	private ConfigurazionePdDReader configurazionePdDReader = null;
	private RegistroServiziManager registroServiziManager = null;
	private List<StateMessage> stati = new ArrayList<StateMessage>();

	public boolean isInitializedConfigurazionePdDReader(){
		return this.configurazionePdDReader!=null;
	}

	public ConfigurazionePdDManager(IState ... state){
		this.configurazionePdDReader = ConfigurazionePdDReader.getInstance();
		if(state!=null){
			for (int i = 0; i < state.length; i++) {
				if(state[i] instanceof StateMessage){
					this.stati.add((StateMessage)state[i]);
				}
			}
		}
		this.registroServiziManager = RegistroServiziManager.getInstance(state);
	}

	public void updateState(IState ... state){
		this.stati.clear();
		if(state!=null){
			for (int i = 0; i < state.length; i++) {
				if(state[i] instanceof StateMessage){
					this.stati.add((StateMessage)state[i]);
				}
			}
		}
		this.registroServiziManager.updateState(state);
	}

	private Connection getConnection(){

		if(this.stati.size()>0){
			for (StateMessage state : this.stati) {
				if(state!=null && state.getConnectionDB()!=null){
					boolean validConnection = false;
					try{
						validConnection = !state.getConnectionDB().isClosed();
					}catch(Exception e){}
					if(validConnection)
						return state.getConnectionDB();
				}
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
			Enumeration<String> keys = config.getFlowParameters().keys();
			ArrayList<String> valuesForReplace = new ArrayList<>();

			while(keys.hasMoreElements()) {
				String key = (String)keys.nextElement();
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
					Map<String, String> pTrasporto = null;
					String urlInvocazione = null;
					Map<String, String> pForm = null;
					if (requestInfo != null && requestInfo.getProtocolContext() != null) {
						pTrasporto = requestInfo.getProtocolContext().getParametersTrasporto();
						urlInvocazione = requestInfo.getProtocolContext().getUrlInvocazione_formBased();
						pForm = requestInfo.getProtocolContext().getParametersFormBased();
					}

					Element element = null;
					String elementJson = null;
					if (ServiceBinding.SOAP.equals(message.getServiceBinding())) {
						element = message.castAsSoap().getSOAPPart().getEnvelope();
					} else if (MessageType.XML.equals(message.getMessageType())) {
						element = (Element)message.castAsRestXml().getContent();
					} else if (MessageType.JSON.equals(message.getMessageType())) {
						elementJson = (String)message.castAsRestJson().getContent();
					}

					Map<String, Object> dynamicMap = new HashMap<String, Object>();
					ErrorHandler errorHandler = new ErrorHandler();
					DynamicUtils.fillDynamicMapRequest(log, dynamicMap, pddContext, urlInvocazione, message, (Element)element, elementJson, busta, pTrasporto, pForm, errorHandler);

					Iterator<String> it = valuesForReplace.iterator();
					while(it.hasNext()) {
						String keyForReplace = (String)it.next();
						String value = null;

						try {
							value = (String)config.getFlowParameters().get(keyForReplace);
							String newValue = DynamicUtils.convertDynamicPropertyValue("ConditionalMessageSecurity", value, dynamicMap, pddContext, true);
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

	public String getIdentificativoPorta(IDSoggetto idSoggetto,IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getIdentificativoPorta(this.getConnection(), idSoggetto, protocolFactory);
	}

	public boolean isSoggettoVirtuale(IDSoggetto idSoggetto) throws DriverConfigurazioneException { 
		return this.configurazionePdDReader.isSoggettoVirtuale(this.getConnection(), idSoggetto);
	}

	public boolean existsSoggetto(IDSoggetto idSoggetto)throws DriverConfigurazioneException{  
		return this.configurazionePdDReader.existsSoggetto(this.getConnection(), idSoggetto);
	}

	public  List<IDServizio> getServizi_SoggettiVirtuali() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getServizi_SoggettiVirtuali(this.getConnection());
	}


	/* ************* ROUTING **************** */

	public Connettore getForwardRoute(IDSoggetto idSoggettoDestinatario,boolean functionAsRouter) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getForwardRoute(this.getConnection(), this.registroServiziManager, idSoggettoDestinatario, functionAsRouter);
	}

	public Connettore getForwardRoute(IDSoggetto idSoggettoMittente, IDServizio idServizio,boolean functionAsRouter) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getForwardRoute(this.getConnection(), this.registroServiziManager, idSoggettoMittente, idServizio, functionAsRouter);
	}

	public String getRegistroForImbustamento(IDSoggetto idSoggettoMittente, IDServizio idServizio,boolean functionAsRouter)throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getRegistroForImbustamento(this.getConnection(), this.registroServiziManager, idSoggettoMittente, idServizio, functionAsRouter);
	}

	public boolean routerFunctionActive() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.routerFunctionActive(this.getConnection());
	}

	public IDSoggetto getRouterIdentity(IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getRouterIdentity(this.getConnection(),protocolFactory);
	}


	/* ********  URLPrefixRewriter  ******** */

	public void setPDUrlPrefixRewriter(org.openspcoop2.core.config.Connettore connettore, IDSoggetto idSoggettoFruitore) throws DriverConfigurazioneNotFound,DriverConfigurazioneException{
		this.configurazionePdDReader.setPDUrlPrefixRewriter(this.getConnection(), connettore, idSoggettoFruitore);
	}

	public void setPAUrlPrefixRewriter(org.openspcoop2.core.config.Connettore connettore, IDSoggetto idSoggettoErogatore) throws DriverConfigurazioneNotFound,DriverConfigurazioneException{
		this.configurazionePdDReader.setPAUrlPrefixRewriter(this.getConnection(), connettore, idSoggettoErogatore);
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

	public IDPortaDelegata getIDPortaDelegata(String nome, IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		IDPortaDelegata idPortaDelegata = this.configurazionePdDReader.getIDPortaDelegata(this.getConnection(), nome);

		try{
			if(idPortaDelegata!=null && idPortaDelegata.getIdentificativiFruizione()!=null){
				if(idPortaDelegata.getIdentificativiFruizione().getSoggettoFruitore()!=null){
					IDSoggetto soggetto = idPortaDelegata.getIdentificativiFruizione().getSoggettoFruitore();
					if(soggetto.getCodicePorta()==null){
						soggetto.setCodicePorta(this.registroServiziManager.getDominio(soggetto, null, protocolFactory));
					}
				}
				if(idPortaDelegata.getIdentificativiFruizione().getIdServizio()!=null &&
						idPortaDelegata.getIdentificativiFruizione().getIdServizio().getSoggettoErogatore()!=null){
					IDSoggetto soggetto = idPortaDelegata.getIdentificativiFruizione().getIdServizio().getSoggettoErogatore();
					if(soggetto.getCodicePorta()==null){
						soggetto.setCodicePorta(this.registroServiziManager.getDominio(soggetto, null, protocolFactory));
					}
				}
			}
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

		return idPortaDelegata;
	}

	public PortaDelegata getPortaDelegata(IDPortaDelegata idPD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getPortaDelegata(this.getConnection(), idPD);
	}

	public PortaDelegata getPortaDelegata_SafeMethod(IDPortaDelegata idPD)throws DriverConfigurazioneException{
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

	public String getAzione(PortaDelegata pd,URLProtocolContext urlProtocolContext,
			OpenSPCoop2Message message, HeaderIntegrazione headerIntegrazione,boolean readFirstHeaderIntegrazione,
			IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound, IdentificazioneDinamicaException { 
		return this.configurazionePdDReader.getAzione(this.registroServiziManager, pd, urlProtocolContext, 
				message, headerIntegrazione, readFirstHeaderIntegrazione, protocolFactory);
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

	public PolicyGestioneToken getPolicyGestioneToken(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getPolicyGestioneToken(this.getConnection(), pd);
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

	public Trasformazioni getTrasformazioni(PortaDelegata pd) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getTrasformazioni(this.getConnection(), pd);
	}

	public List<Object> getExtendedInfo(PortaDelegata pd)throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getExtendedInfo(pd);
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

	public IDPortaApplicativa getIDPortaApplicativa(String nome, IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		IDPortaApplicativa idPortaApplicativa = this.configurazionePdDReader.getIDPortaApplicativa(this.getConnection(), nome);
		try{
			if(idPortaApplicativa!=null && idPortaApplicativa.getIdentificativiErogazione()!=null){
				if(idPortaApplicativa.getIdentificativiErogazione().getSoggettoVirtuale()!=null){
					IDSoggetto soggetto = idPortaApplicativa.getIdentificativiErogazione().getSoggettoVirtuale();
					if(soggetto.getCodicePorta()==null){
						soggetto.setCodicePorta(this.registroServiziManager.getDominio(soggetto, null, protocolFactory));
					}
				}
				if(idPortaApplicativa.getIdentificativiErogazione().getIdServizio()!=null &&
						idPortaApplicativa.getIdentificativiErogazione().getIdServizio().getSoggettoErogatore()!=null){
					IDSoggetto soggetto = idPortaApplicativa.getIdentificativiErogazione().getIdServizio().getSoggettoErogatore();
					if(soggetto.getCodicePorta()==null){
						soggetto.setCodicePorta(this.registroServiziManager.getDominio(soggetto, null, protocolFactory));
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

	public boolean existsPA(RichiestaApplicativa idPA) throws DriverConfigurazioneException{	
		return this.configurazionePdDReader.existsPA(this.getConnection(), idPA);
	}

	public PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPA) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getPortaApplicativa(this.getConnection(), idPA);
	}

	public PortaApplicativa getPortaApplicativa_SafeMethod(IDPortaApplicativa idPA)throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getPortaApplicativa_SafeMethod(this.getConnection(), idPA);
	}
	
	public void updateStatoPortaApplicativa(IDPortaApplicativa idPA, StatoFunzionalita stato) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		this.configurazionePdDReader.updateStatoPortaApplicativa(this.getConnection(), idPA, stato);
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

	public String getAzione(PortaApplicativa pa,URLProtocolContext urlProtocolContext,
			OpenSPCoop2Message message, HeaderIntegrazione headerIntegrazione,boolean readFirstHeaderIntegrazione,
			IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException, IdentificazioneDinamicaException { 
		return this.configurazionePdDReader.getAzione(this.registroServiziManager, pa, urlProtocolContext, 
				message, headerIntegrazione, readFirstHeaderIntegrazione, protocolFactory);
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

	public PolicyGestioneToken getPolicyGestioneToken(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getPolicyGestioneToken(this.getConnection(), pa);
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

	public boolean autorizzazioneRoles(PortaApplicativa pa, Soggetto soggetto, ServizioApplicativo sa, InfoConnettoreIngresso infoConnettoreIngresso,
			PdDContext pddContext,
			boolean checkRuoloRegistro, boolean checkRuoloEsterno,
			StringBuilder details) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.autorizzazioneRoles(this.registroServiziManager, pa, soggetto, sa, infoConnettoreIngresso, 
				pddContext, checkRuoloRegistro, checkRuoloEsterno, details);
	}

	public boolean isPortaAbilitata(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.isPortaAbilitata(pa);
	}

	public DumpConfigurazione getDumpConfigurazione(PortaApplicativa pa) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getDumpConfigurazione(this.getConnection(), pa);
	}

	public Trasformazioni getTrasformazioni(PortaApplicativa pa) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getTrasformazioni(this.getConnection(), pa);
	}

	public List<Object> getExtendedInfo(PortaApplicativa pa)throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getExtendedInfo(pa);
	}




	/* ********  Servizi Applicativi (Interfaccia)  ******** */

	public boolean existsServizioApplicativo(IDServizioApplicativo idSA) throws DriverConfigurazioneException{ 
		return this.configurazionePdDReader.existsServizioApplicativo(this.getConnection(), idSA);
	}

	public ServizioApplicativo getServizioApplicativo(IDServizioApplicativo idSA) throws DriverConfigurazioneNotFound,DriverConfigurazioneException{
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

	public boolean autorizzazione(PortaDelegata pd, String servizio) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.autorizzazione(pd, servizio);
	}

	public boolean autorizzazioneRoles(PortaDelegata pd, ServizioApplicativo sa, InfoConnettoreIngresso infoConnettoreIngresso,
			PdDContext pddContext,
			boolean checkRuoloRegistro, boolean checkRuoloEsterno,
			StringBuilder details) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.autorizzazioneRoles(this.registroServiziManager, pd, sa, infoConnettoreIngresso, 
				pddContext, checkRuoloRegistro, checkRuoloEsterno, details);
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

	public ConnettoreMsg getInvocazioneServizio(ServizioApplicativo sa,RichiestaApplicativa idPA)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getInvocazioneServizio(this.getConnection(), sa, idPA);
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

	public ConnettoreMsg getConsegnaRispostaAsincrona(ServizioApplicativo sa,RichiestaDelegata idPD)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getConsegnaRispostaAsincrona(this.getConnection(), sa, idPD);
	}

	public ConnettoreMsg getConsegnaRispostaAsincrona(ServizioApplicativo sa,RichiestaApplicativa idPA)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getConsegnaRispostaAsincrona(this.getConnection(), sa, idPA);
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

	public DumpConfigurazione getDumpConfigurazione() {
		return this.configurazionePdDReader.getDumpConfigurazione(this.getConnection());
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

	public PolicyNegoziazioneToken getPolicyNegoziazioneToken(boolean forceNoCache, String policyName) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		return this.configurazionePdDReader.getPolicyNegoziazioneToken(this.getConnection(), forceNoCache, policyName);
	}

	public GenericProperties getGenericProperties(String tipologia, String nome) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getGenericProperties(this.getConnection(), tipologia, nome);
	}

	public List<GenericProperties> getGenericProperties(String tipologia) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getGenericProperties(this.getConnection(), tipologia);
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

	public UrlInvocazioneAPI getConfigurazioneUrlInvocazione(IProtocolFactory<?> protocolFactory, RuoloContesto ruolo, ServiceBinding serviceBinding, 
			String interfaceName, IDSoggetto soggettoOperativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getConfigurazioneUrlInvocazione(this.getConnection(),
				protocolFactory, ruolo, serviceBinding, interfaceName, soggettoOperativo);
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

	public ConfigurazioneGenerale getConfigurazioneControlloTraffico() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getConfigurazioneControlloTraffico(this.getConnection());
	}

	public Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> getElencoIdPolicyAttiveAPI(boolean useCache, TipoPdD tipoPdD, String nomePorta) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getElencoIdPolicyAttiveAPI(this.getConnection(), useCache, tipoPdD, nomePorta);
	}

	public Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> getElencoIdPolicyAttiveGlobali(boolean useCache) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdDReader.getElencoIdPolicyAttiveGlobali(this.getConnection(), useCache);
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

	/* ******** MAPPING ******** */

	public List<MappingErogazionePortaApplicativa> getMappingErogazionePortaApplicativaList(IDServizio idServizio) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getMappingErogazionePortaApplicativaList(idServizio, this.getConnection());
	} 
	public List<MappingFruizionePortaDelegata> getMappingFruizionePortaDelegataList(IDSoggetto idFruitore, IDServizio idServizio) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getMappingFruizionePortaDelegataList(idFruitore, idServizio, this.getConnection());
	} 
	
	/* ******** FORWARD PROXY ******** */
	
	public boolean isForwardProxyEnabled() {
		return this.configurazionePdDReader.isForwardProxyEnabled();
	}
	public ForwardProxy getForwardProxyConfigFruizione(IDSoggetto dominio, IDServizio idServizio) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getForwardProxyConfigFruizione(dominio, idServizio);
	}
	public ForwardProxy getForwardProxyConfigErogazione(IDSoggetto dominio, IDServizio idServizio) throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getForwardProxyConfigErogazione(dominio, idServizio);
	}
	
	/* ********  GENERIC FILE  ******** */

	public ContentFile getContentFile(File file)throws DriverConfigurazioneException{
		return this.configurazionePdDReader.getContentFile(file);
	}
}
