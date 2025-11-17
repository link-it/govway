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

package org.openspcoop2.pdd.core.connettori;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.beans.DatiTransazione;
import org.openspcoop2.core.controllo_traffico.beans.RisultatoStatistico;
import org.openspcoop2.core.controllo_traffico.constants.TipoPeriodoStatistico;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.core.Utilities;
import org.openspcoop2.pdd.core.controllo_traffico.policy.GestoreCacheControlloTraffico;
import org.openspcoop2.protocol.engine.builder.Imbustamento;
import org.openspcoop2.protocol.engine.builder.Sbustamento;
import org.openspcoop2.protocol.engine.driver.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.engine.validator.Validatore;
import org.openspcoop2.protocol.engine.validator.ValidazioneSintattica;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.BustaRawContent;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.Riscontro;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.constants.FaseImbustamento;
import org.openspcoop2.protocol.sdk.constants.FaseSbustamento;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.state.StatefulMessage;
import org.openspcoop2.protocol.sdk.validator.IValidatoreErrori;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazione;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazioneErrori;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.rest.problem.JsonSerializer;
import org.openspcoop2.utils.rest.problem.ProblemRFC7807;
import org.openspcoop2.utils.rest.problem.XmlSerializer;
import org.openspcoop2.utils.transport.TransportResponseContext;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpUtilities;


/**
 * Classe utilizzata per ottenere lo status di una determinata
 * erogazione/fruizione
 *
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ConnettoreStatus extends ConnettoreBaseWithResponse {
	
	@Override
	public String getProtocollo() {
    	return "";
    }
	
	public static final String LOCATION = "govway://status";
    
	private DumpByteArrayOutputStream requestBout = null;

	public static String getMessage(ConnettoreStatusResponseType statusResponseType, MessageType messageTypeRequest) throws UtilsException {
		
		ProblemRFC7807 problemRFC7807 = new ProblemRFC7807();
		problemRFC7807.setStatus(200);
		problemRFC7807.setDetail("Il servizio funziona correttamente");
		problemRFC7807.setTitle("Success");
		problemRFC7807.setType("https://httpstatuses.com/200");
		
		switch(statusResponseType) {
		case MODI:{
			return getMessageModI(messageTypeRequest, problemRFC7807);
		}
		case VUOTO: return "";
		case XML: {
			XmlSerializer xmlSerializer = new XmlSerializer();	
			return xmlSerializer.toString(problemRFC7807).replace("problem", "status");
		}
		case JSON: {
			JsonSerializer jsonSerializer = new JsonSerializer();
			return jsonSerializer.toString(problemRFC7807);
		}
		case TEXT: return "Il servizio funziona correttamente";
		default: return null;
		}
	}
	private static String getMessageModI(MessageType messageTypeRequest, ProblemRFC7807 problemRFC7807) throws UtilsException {
		switch (messageTypeRequest) {
			case SOAP_11:
			case SOAP_12:
			{
				XmlSerializer xmlSerializer = new XmlSerializer();	
				String p = xmlSerializer.toString(problemRFC7807, true);
				return "<?xml version='1.0' encoding='UTF-8'?>"
					+ "<soapenv:Envelope xmlns:soapenv=\""+(MessageType.SOAP_11.equals(messageTypeRequest) ? Costanti.SOAP_ENVELOPE_NAMESPACE : Costanti.SOAP12_ENVELOPE_NAMESPACE)+"\">"
					+ "<soapenv:Body>"
					+ p
					+ "</soapenv:Body>"
					+ "</soapenv:Envelope>";
			}
			case XML:{
				XmlSerializer xmlSerializer = new XmlSerializer();	
				return xmlSerializer.toString(problemRFC7807);
			}
			case JSON:
			case BINARY:
			case MIME_MULTIPART:{
				// in futuro valutare se usare l'accept
				JsonSerializer jsonSerializer = new JsonSerializer();
				return jsonSerializer.toString(problemRFC7807);
			}
		}
		throw new UtilsException("Message type unknown");
	}
	
	public static String getContentType(ConnettoreStatusResponseType statusResponseType, MessageType messageTypeRequest) throws UtilsException {
		switch(statusResponseType) {
		case MODI: return getContentTypeModI(messageTypeRequest);
		case VUOTO: return null;
		case XML: return HttpConstants.CONTENT_TYPE_XML;
		case JSON: return HttpConstants.CONTENT_TYPE_JSON;
		case TEXT: return HttpConstants.CONTENT_TYPE_PLAIN;
		default: return null;
		}
	}
	private static String getContentTypeModI(MessageType messageTypeRequest) throws UtilsException {
		switch (messageTypeRequest) {
			case SOAP_11:{
				return HttpConstants.CONTENT_TYPE_SOAP_1_1;
			}
			case SOAP_12:
			{
				return HttpConstants.CONTENT_TYPE_SOAP_1_2;
			}
			case XML:{
				return HttpConstants.CONTENT_TYPE_XML;
			}
			case JSON:
			case BINARY:
			case MIME_MULTIPART:{
				return HttpConstants.CONTENT_TYPE_JSON;
			}
		}
		throw new UtilsException("Message type unknown");
	}
	
	private void testConnectivityErogazione(Map<Long, String> connettoriServizio, Map<Long, String> connettoriServizioNomeConnettoreMultiplo) throws DriverRegistroServiziException, DriverConfigurazioneException, DriverConfigurazioneNotFound {
		ConfigurazionePdDManager configurazioneManager = ConfigurazionePdDManager.getInstance();
		IDServizio idServizio = this.getServizio();
		List<MappingErogazionePortaApplicativa> mappings = configurazioneManager.getMappingErogazionePortaApplicativaList(idServizio, this.requestInfo);
		for (MappingErogazionePortaApplicativa mapping : mappings) {
			PortaApplicativa pa = configurazioneManager.getPortaApplicativaSafeMethod(mapping.getIdPortaApplicativa(), this.requestInfo);
			if (pa.equals(this.pa)) 
				continue;
			List<PortaApplicativaServizioApplicativo> serviziApplicativi = pa.getServizioApplicativo();
			
			for (PortaApplicativaServizioApplicativo sa : serviziApplicativi) {						
				IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
				idServizioApplicativo.setNome(sa.getNome());
				idServizioApplicativo.setIdSoggettoProprietario(new IDSoggetto(pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()));
				ServizioApplicativo servizioApplicativo = configurazioneManager.getServizioApplicativo(idServizioApplicativo, this.requestInfo);
				
				testConnectivityErogazione(connettoriServizio, connettoriServizioNomeConnettoreMultiplo, 
						mapping,
						sa, servizioApplicativo);
			}
		}
	}
	private void testConnectivityErogazione(Map<Long, String> connettoriServizio, Map<Long, String> connettoriServizioNomeConnettoreMultiplo, 
			MappingErogazionePortaApplicativa mapping,
			PortaApplicativaServizioApplicativo sa, ServizioApplicativo servizioApplicativo) {		
		if (servizioApplicativo.getInvocazioneServizio() != null && 
				servizioApplicativo.getInvocazioneServizio().getConnettore() != null &&
				servizioApplicativo.getInvocazioneServizio().getConnettore().getId()!=null) {
			testConnectivityErogazione(servizioApplicativo.getInvocazioneServizio().getConnettore(), mapping, connettoriServizio);
			if(sa.getDatiConnettore()!=null && sa.getDatiConnettore().getNome()!=null && StringUtils.isNotEmpty(sa.getDatiConnettore().getNome())) {
				connettoriServizioNomeConnettoreMultiplo.put(servizioApplicativo.getInvocazioneServizio().getConnettore().getId(), sa.getDatiConnettore().getNome());
			}
		}
		if (servizioApplicativo.getRispostaAsincrona() != null && 
				servizioApplicativo.getRispostaAsincrona().getConnettore() != null &&
				servizioApplicativo.getRispostaAsincrona().getConnettore().getId()!=null) {
			testConnectivityErogazione(servizioApplicativo.getRispostaAsincrona().getConnettore(), mapping, connettoriServizio);
			connettoriServizioNomeConnettoreMultiplo.put(servizioApplicativo.getRispostaAsincrona().getConnettore().getId(), "Risposta asincrona");
		}
	}
	private void testConnectivityErogazione(Connettore connettore, MappingErogazionePortaApplicativa mapping, Map<Long, String> connettoriServizio) {
		if (connettore != null && connettore.getId() != null && checkConnectivitySupported(connettore.getTipo())) {
			String labelGruppo = mapping.getDescrizione();
			if(mapping.isDefault() &&
				(labelGruppo==null || StringUtils.isEmpty(labelGruppo))) {
				labelGruppo = org.openspcoop2.core.constants.Costanti.MAPPING_DESCRIZIONE_DEFAULT;
			}
			connettoriServizio.put(connettore.getId(), labelGruppo);
		}
	}
	
	private void testConnectivityFruizione(Map<Long, String> connettoriServizio) throws DriverRegistroServiziException, DriverConfigurazioneException, DriverConfigurazioneNotFound {
		ConfigurazionePdDManager configurazioneManager = ConfigurazionePdDManager.getInstance();
		IDServizio idServizio = this.getServizio();
		IDSoggetto idFruitore = new IDSoggetto(this.pd.getTipoSoggettoProprietario(), this.pd.getNomeSoggettoProprietario());
		
		List<MappingFruizionePortaDelegata> mappings = configurazioneManager.getMappingFruizionePortaDelegataList(idFruitore, idServizio, this.requestInfo);
		for (MappingFruizionePortaDelegata mapping : mappings) {
			PortaDelegata pd = configurazioneManager.getPortaDelegataSafeMethod(mapping.getIdPortaDelegata(), null);
			if (pd.equals(this.pd)) 
				continue;					
			PortaDelegataAzione azioni = pd.getAzione();
			for (String azione : azioni.getAzioneDelegataList()) {
				if (idServizio != null)
					idServizio.setAzione(azione);
				Connettore connettore = configurazioneManager.getForwardRoute(idFruitore, idServizio, false, null);
				testConnectivityFruizione(connettore, mapping, connettoriServizio);
			}
		}
	}
	private void testConnectivityFruizione(Connettore connettore, MappingFruizionePortaDelegata mapping, Map<Long, String> connettoriServizio) {
		if (connettore != null && connettore.getId() != null && checkConnectivitySupported(connettore.getTipo())) {
			String labelGruppo = mapping.getDescrizione();
			if(mapping.isDefault() &&
				(labelGruppo==null || StringUtils.isEmpty(labelGruppo))) {
				labelGruppo = org.openspcoop2.core.constants.Costanti.MAPPING_DESCRIZIONE_DEFAULT;
			}
			connettoriServizio.put(connettore.getId(), labelGruppo);
		}
	}
	
	private boolean checkConnectivitySupported(String tipoConnettore) {
		TipiConnettore tipo = TipiConnettore.toEnumFromName(tipoConnettore);
		return tipo!=null && (TipiConnettore.HTTP.equals(tipo) || TipiConnettore.HTTPS.equals(tipo)); 
	}
	
	private void testConnectivity() throws DriverRegistroServiziException, DriverConfigurazioneException, DriverConfigurazioneNotFound, ConnettoreException {
		Map<Long, String> connettoriServizio = new HashMap<>();
		Map<Long, String> connettoriServizioNomeConnettoreMultiplo = new HashMap<>();
		
		if (this.pd != null)
			this.testConnectivityFruizione(connettoriServizio);
		
		if (this.pa != null)
			this.testConnectivityErogazione(connettoriServizio, connettoriServizioNomeConnettoreMultiplo);
		
		if (this.debug)
			this.logger.debug("Inizio test connettivita sul servizio: " + this.getServizio() + ", id connettori: {" + connettoriServizio.keySet() + "}");
		
		for (Map.Entry<Long, String> entry : connettoriServizio.entrySet()) {
			try {
				ConnettoreCheck.check(entry.getKey(), this.pd != null, null);
			} catch(ConnettoreException e) {
				String nomeConnettore = connettoriServizioNomeConnettoreMultiplo.get(entry.getKey());
				if(nomeConnettore==null || StringUtils.isEmpty(nomeConnettore)) {
					nomeConnettore="";
				}
				else {
					nomeConnettore = " '"+nomeConnettore+"'";
				}
				throw new ConnettoreException("connettività fallita verso il connettore"+nomeConnettore+" del gruppo '" + entry.getValue() + "': " + e.getMessage());
			}
		}
	}
	
	private IDServizio getServizio() throws DriverRegistroServiziException {
		if (this.pd != null) {
			return IDServizioFactory.getInstance().getIDServizioFromValues(
					this.pd.getServizio().getTipo(), 
					this.pd.getServizio().getNome(), 
					this.getSoggettoErogatore(),
					this.pd.getServizio().getVersione());
		}
		
		if (this.pa != null) {
			return IDServizioFactory.getInstance().getIDServizioFromValues(
					this.pa.getServizio().getTipo(), 
					this.pa.getServizio().getNome(), 
					this.getSoggettoErogatore(),
					this.pa.getServizio().getVersione());
		}
		
		return null;
	}
	
	private IDSoggetto getSoggettoFruitore() {
		return this.pd == null ? null : new IDSoggetto(this.pd.getTipoSoggettoProprietario(), this.pd.getNomeSoggettoProprietario());
	}
	
	private IDSoggetto getSoggettoErogatore() {
		if (this.pd != null)
			return new IDSoggetto(this.pd.getSoggettoErogatore().getTipo(), this.pd.getSoggettoErogatore().getNome());
		if (this.pa != null)
			return new IDSoggetto(this.pa.getTipoSoggettoProprietario(), this.pa.getNomeSoggettoProprietario());
		return null;
	}
	
	private List<String> getOperations() {
		List<String> l = null;
		if (this.pd != null)
			return this.pd.getAzione().getAzioneDelegata();
		if (this.pa != null)
			return this.pa.getAzione().getAzioneDelegata();
		return l;
	}
	
	private void testStatistics() throws Exception {
		DatiTransazione datiTransazioni = new DatiTransazione();
		datiTransazioni.setDominio(this.getSoggettoErogatore());
		datiTransazioni.setIdTransazione(this.idTransazione);
		datiTransazioni.setModulo(this.idModulo);
		
		String periodRaw = this.getDynamicProperty(this.tipoConnettore, false, CostantiConnettori.CONNETTORE_STATUS_PERIOD, this.dynamicMap);
		String periodValueRaw = this.getDynamicProperty(this.tipoConnettore, false, CostantiConnettori.CONNETTORE_STATUS_PERIOD_VALUE, this.dynamicMap);
		String lifetimeRaw = this.getDynamicProperty(this.tipoConnettore, false, CostantiConnettori.CONNETTORE_STATUS_STAT_LIFETIME, this.dynamicMap);
		
		if (periodRaw == null)
			throw new ConnettoreException("il tipo del periodo statistico non corretto, valori possibili: " + java.util.Arrays.toString(TipoPeriodoStatistico.toArray()));
		
		TipoPeriodoStatistico period = TipoPeriodoStatistico.valueOf(periodRaw.toUpperCase());
		Integer periodValue = Integer.valueOf(periodValueRaw);
		Integer lifeTime = lifetimeRaw != null ? Integer.valueOf(lifetimeRaw) : Integer.MAX_VALUE;
		
		
		if (periodValue <= 0)
			throw new ConnettoreException("il valore del periodo deve essere un valore > 1");
		
		if (this.debug)
			this.logger.debug("Inizio analisi statistica sulle transazioni del servizio:" + this.getServizio());
		
		Date now = new Date();
		RisultatoStatistico numOk = GestoreCacheControlloTraffico.getInstance().readNumeroEsiti(period, periodValue, now, List.of(0), this.getServizio(), this.getSoggettoFruitore(), this.getOperations(), datiTransazioni, this.state, lifeTime);
		RisultatoStatistico numTot = GestoreCacheControlloTraffico.getInstance().readNumeroEsiti(period, periodValue, now, null, this.getServizio(), this.getSoggettoFruitore(), this.getOperations(), datiTransazioni, this.state, lifeTime);
		if (numOk.getRisultato() == 0 && numTot.getRisultato() > 0)
			throw new ConnettoreException("trovate: " + numTot.getRisultato() + " transazioni con errore, nessuna transazione ok");
	
	}
	
	/* ********  METODI  ******** */

	@Override
	protected boolean initializePreSend(ResponseCachingConfigurazione responseCachingConfig, ConnettoreMsg request) {
		return this.initialize(request, false, responseCachingConfig);
	}
	
	
	@Override
	protected boolean send(ConnettoreMsg request) {

		boolean generaTrasmissione = false;
		if(this.properties.get(CostantiConnettori.CONNETTORE_NULL_ECHO_GENERA_TRASMISSIONE)!=null){
			generaTrasmissione = "true".equalsIgnoreCase(this.properties.get(CostantiConnettori.CONNETTORE_NULL_ECHO_GENERA_TRASMISSIONE).trim());
		}
		
		boolean generaTrasmissioneInvertita = false;
		if(this.properties.get(CostantiConnettori.CONNETTORE_NULL_ECHO_GENERA_TRASMISSIONE_INVERTITA)!=null){
			generaTrasmissioneInvertita = "true".equalsIgnoreCase(this.properties.get(CostantiConnettori.CONNETTORE_NULL_ECHO_GENERA_TRASMISSIONE_INVERTITA).trim());
		}
		
		boolean generaTrasmissioneAndataRitorno = false;
		if(this.properties.get(CostantiConnettori.CONNETTORE_NULL_ECHO_GENERA_TRASMISSIONE_ANDATA_RITORNO)!=null){
			generaTrasmissioneAndataRitorno = "true".equalsIgnoreCase(this.properties.get(CostantiConnettori.CONNETTORE_NULL_ECHO_GENERA_TRASMISSIONE_ANDATA_RITORNO).trim());
		}
		
		ConnettoreStatusResponseType statusResponseType = null;
		boolean isStatisticalAnalysis = false;
		boolean isTestConnectivity = false;
		try {
			String statusResponseTypeRaw = this.getDynamicProperty(this.tipoConnettore, true, CostantiConnettori.CONNETTORE_STATUS_RESPONSE_TYPE, this.dynamicMap);
			String statisticalAnalysisRaw = this.getDynamicProperty(this.tipoConnettore, false, CostantiConnettori.CONNETTORE_STATUS_PERIOD, this.dynamicMap);
			String testConnectivityRaw = this.getDynamicProperty(this.tipoConnettore, false, CostantiConnettori.CONNETTORE_STATUS_TEST_CONNECTIVITY, this.dynamicMap);
		
			isStatisticalAnalysis = (statisticalAnalysisRaw != null);
			isTestConnectivity = (testConnectivityRaw != null && testConnectivityRaw.equals(Boolean.TRUE.toString()));
			statusResponseType = ConnettoreStatusResponseType.fromString(statusResponseTypeRaw);
		} catch(ConnettoreException e) {
			statusResponseType = null;
			this.logger.error("Tipologia risposta del connettore status non impostata");
		}
		
		if (this.debug)
			this.logger.debug("connettoreStatus: [type: " + statusResponseType + ", test connectivity: " + isTestConnectivity + ", statistical analysis: " + isStatisticalAnalysis);
		
		this.codice = 200;
		DBManager dbManager = DBManager.getInstance();
		Resource resource = null;
		
		StatefulMessage state = new StatefulMessage(null, this.logger.getLogger());
		
		ValidazioneSintattica validatoreSintattico = null;
		Validatore validatoreProtocollo = null;
		@SuppressWarnings("unused")
		BustaRawContent<?> headerProtocolloRisposta = null;
		String protocol = null;
		try{
			IProtocolFactory<?> protocolFactory = this.getProtocolFactory();
			IProtocolManager protocolManager = protocolFactory.createProtocolManager();
			protocol = protocolFactory.getProtocol();
			
			// Tipologia di servizio
			MessageType requestMessageType = this.requestMsg.getMessageType();
			OpenSPCoop2SoapMessage soapMessageRequest = null;
			if(this.debug)
				this.logger.debug("Tipologia Servizio: "+this.requestMsg.getServiceBinding());
			if(this.isSoap){
				soapMessageRequest = this.requestMsg.castAsSoap();
			}
			
			
			// Collezione header di trasporto per dump
			Map<String, List<String>> propertiesTrasportoDebug = null;
			if(this.isDumpBinarioRichiesta()) {
				propertiesTrasportoDebug = new HashMap<>();
			}
			
			
			// Impostazione Content-Type
			String contentTypeRichiesta = null;
			if(this.debug)
				this.logger.debug("Impostazione content type...");
			if(this.isSoap){
				if(this.sbustamentoSoap && soapMessageRequest.countAttachments()>0 && TunnelSoapUtils.isTunnelOpenSPCoopSoap(soapMessageRequest)){
					contentTypeRichiesta = TunnelSoapUtils.getContentTypeTunnelOpenSPCoopSoap(soapMessageRequest.getSOAPBody());
				}else{
					contentTypeRichiesta = this.requestMsg.getContentType();
				}
				if(contentTypeRichiesta==null){
					throw new ConnettoreException("Content-Type del messaggio da spedire non definito");
				}
			}
			else{
				contentTypeRichiesta = this.requestMsg.getContentType();
				// Content-Type non obbligatorio in REST
			}
			if(this.debug)
				this.logger.info("Impostazione Content-Type ["+contentTypeRichiesta+"]",false);
			if(contentTypeRichiesta!=null){
				setRequestHeader(HttpConstants.CONTENT_TYPE, contentTypeRichiesta, this.logger, propertiesTrasportoDebug);
			}
			
			
			
			// Impostazione timeout
			if(this.debug)
				this.logger.debug("Impostazione timeout...");
			int readConnectionTimeout = -1;
			boolean readConnectionTimeoutConfigurazioneGlobale = true;
			if(this.properties.get(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT)!=null){
				try{
					readConnectionTimeout = Integer.parseInt(this.properties.get(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT));
					readConnectionTimeoutConfigurazioneGlobale = this.properties.containsKey(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT_GLOBALE);
				}catch(Exception e){
					this.logger.error("Parametro "+CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT+" errato",e);
				}
			}
			if(readConnectionTimeout==-1){
				readConnectionTimeout = HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;
			}
			if(this.debug)
				this.logger.info("Impostazione read timeout ["+readConnectionTimeout+"]",false);
			
			
			// Impostazione Proprieta del trasporto
			if(this.debug)
				this.logger.debug("Impostazione header di trasporto...");
			this.forwardHttpRequestHeader();
			if(this.propertiesTrasporto != null){
				Iterator<String> keys = this.propertiesTrasporto.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					List<String> values = this.propertiesTrasporto.get(key);
					if(this.debug && values!=null && !values.isEmpty()) {
			        		for (String value : values) {
			        			this.logger.info("Set proprietà trasporto ["+key+"]=["+value+"]",false);
			        		}
			    		}
					setRequestHeader(key, values, this.logger, propertiesTrasportoDebug);
				}
			}
			
			
			
			// Aggiunga del SoapAction Header in caso di richiesta SOAP
			// spostato sotto il forwardHeader per consentire alle trasformazioni di modificarla
			if(this.isSoap && !this.sbustamentoSoap){
				if(this.debug)
					this.logger.debug("Impostazione soap action...");
				boolean existsTransportProperties = false;
				if(TransportUtils.containsKey(this.propertiesTrasporto, Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION)){
					this.soapAction = TransportUtils.getFirstValue(this.propertiesTrasporto, Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION);
					existsTransportProperties = (this.soapAction!=null);
				}
				if(!existsTransportProperties) {
					this.soapAction = soapMessageRequest.getSoapAction();
				}
				if(this.soapAction==null){
					this.soapAction="\"OpenSPCoop\"";
				}
				if(MessageType.SOAP_11.equals(this.requestMsg.getMessageType()) && !existsTransportProperties){
					// NOTA non quotare la soap action, per mantenere la trasparenza della PdD
					setRequestHeader(Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION,this.soapAction, propertiesTrasportoDebug);
				}
				if(this.debug)
					this.logger.info("SOAP Action inviata ["+this.soapAction+"]",false);
			}
			
			
			
			// SIMULAZIONE WRITE_TO
			boolean consumeRequestMessage = true;
			if(this.debug)
				this.logger.debug("Serializzazione (consume-request-message:"+consumeRequestMessage+")...");
			if(this.isDumpBinarioRichiesta()) {
				this.requestBout = new DumpByteArrayOutputStream(this.dumpBinarioSoglia, this.dumpBinarioRepositoryFile, this.idTransazione, 
						"NullEcho-"+TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO.getValue());
				
				this.emitDiagnosticStartDumpBinarioRichiestaUscita();
				
				if(this.isSoap && this.sbustamentoSoap){
					this.logger.debug("Sbustamento...");
					TunnelSoapUtils.sbustamentoMessaggio(soapMessageRequest,this.requestBout);
				}else{
					this.requestMsg.writeTo(this.requestBout, consumeRequestMessage);
				}
				this.requestBout.flush();
				this.requestBout.close();
									
				this.dataRichiestaInoltrata = DateManager.getDate();
				
				this.dumpBinarioRichiestaUscita(this.requestBout, requestMessageType, contentTypeRichiesta, this.location, propertiesTrasportoDebug);
			}
			else {
				this.requestBout = new DumpByteArrayOutputStream(this.dumpBinarioSoglia, this.dumpBinarioRepositoryFile, this.idTransazione, 
						"NullEcho-"+TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO.getValue()); 
				if(this.isSoap && this.sbustamentoSoap){
					this.logger.debug("Sbustamento...");
					TunnelSoapUtils.sbustamentoMessaggio(soapMessageRequest,this.requestBout);
				}else{
					this.requestMsg.writeTo(this.requestBout, consumeRequestMessage);
				}
				this.requestBout.flush();
				this.requestBout.close();
				
				this.dataRichiestaInoltrata = DateManager.getDate();
			}
			
			
			
			
			
			
			/* ------------  PostOutRequestHandler ------------- */
			this.postOutRequest();
			
			
			
			/* ------------  PreInResponseHandler ------------- */
			this.preInResponse();
			
			// Lettura risposta parametri NotifierInputStream per la risposta
			org.openspcoop2.utils.io.notifier.NotifierInputStreamParams notifierInputStreamParams = null;
			if(this.preInResponseContext!=null){
				notifierInputStreamParams = this.preInResponseContext.getNotifierInputStreamParams();
			}
			
			// verifica connettivita
			if (isTestConnectivity) {
				this.testConnectivity();
			}
			
			// analisi statistica
			if (isStatisticalAnalysis) {
				this.testStatistics();
			}
			
			this.isResponse = new ByteArrayInputStream(ConnettoreStatus.getMessage(statusResponseType, this.requestMsg.getMessageType()).getBytes());
			this.tipoRisposta = ConnettoreStatus.getContentType(statusResponseType, this.requestMsg.getMessageType());
			
			this.normalizeInputStreamResponse(readConnectionTimeout, readConnectionTimeoutConfigurazioneGlobale);
			
			this.initCheckContentTypeConfiguration();
			
			this.messageTypeResponse = this.requestMsg.getMessageType();
			
			if(this.isDumpBinarioRisposta()){
				this.dumpResponse(this.propertiesTrasportoRisposta);
			}
			
			if(this.isResponse!=null) {
				this.emitDiagnosticResponseRead(this.isResponse);
			}
			
			OpenSPCoop2MessageFactory messageFactory = Utilities.getOpenspcoop2MessageFactory(this.logger.getLogger(),this.requestMsg, this.requestInfo, MessageRole.RESPONSE);
			OpenSPCoop2MessageParseResult pr;
			
			if (this.isResponse != null) {
				pr = messageFactory.createMessage(this.messageTypeResponse, MessageRole.RESPONSE,
						this.tipoRisposta,
						this.isResponse,notifierInputStreamParams,
						this.openspcoopProperties.getAttachmentsProcessingMode());
			} else {
				TransportResponseContext responseContext = new TransportResponseContext(this.logger.getLogger());
				responseContext.setCodiceTrasporto(this.codice+"");
				responseContext.setContentLength(0);
				
				pr = messageFactory.createMessage(this.messageTypeResponse,responseContext,
						this.isResponse,notifierInputStreamParams,
						this.openspcoopProperties.getAttachmentsProcessingMode());
			}
			if(pr.getParseException()!=null){
				this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, pr.getParseException());
			}
			this.responseMsg = pr.getMessage_throwParseException();
			
			validatoreSintattico = new ValidazioneSintattica(this.getPddContext(),state,this.responseMsg, this.openspcoopProperties.isReadQualifiedAttribute(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD), protocolFactory); 

			if(validatoreSintattico.verifyProtocolPresence(TipoPdD.APPLICATIVA,null,RuoloMessaggio.RISPOSTA) && 
					!CostantiLabel.SDI_PROTOCOL_NAME.equals(protocolFactory.getProtocol())){ // evitare sdi per far funzionare il protocollo sdi con la sonda.
				
				// getBusta
				ProprietaValidazione property = new ProprietaValidazione();
				property.setValidazioneConSchema(false);
				property.setValidazioneProfiloCollaborazione(false);
				property.setValidazioneManifestAttachments(false);
				
				validatoreProtocollo = new Validatore(this.responseMsg,this.getPddContext(),property,null,
						this.openspcoopProperties.isReadQualifiedAttribute(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD), protocolFactory);
				
				if(!validatoreProtocollo.validazioneSintattica()){
					throw new ConnettoreException("Busta non presente: "+validatoreProtocollo.getErrore().getDescrizione(protocolFactory));
				}
				
				// Leggo busta di richiesta
				Busta busta = validatoreProtocollo.getBusta();
				
				// informazione spcoopErrore
				IValidatoreErrori validatoreErrori = protocolFactory.createValidatoreErrori(state);
				ProprietaValidazioneErrori pValidazioneErrori = new ProprietaValidazioneErrori();
				pValidazioneErrori.setIgnoraEccezioniNonGravi(protocolManager.isIgnoraEccezioniNonGravi());
				boolean isBustaSPCoopErrore = validatoreErrori.isBustaErrore(busta,this.responseMsg,pValidazioneErrori);
				
				// Gestisco il manifest se il messaggio lo possiede
				boolean gestioneManifest = false;
				// La gestione manifest non la devo fare a questo livello.
				// Se mi arriva un messaggio senza manifest, vuol dire che era disabilitata e quindi anche nella risposta non ci deve essere
				// In egual misura se arriva un messaggio con manifest, significa che ci deve essere anche nella risposta
				// Poiche' la risposta e' esattamente uguale (nel body e negli allegati) alla richiesta, 
				// venendo costruita dai bytes della richiesta 
				
				// rimozione vecchia busta
				Sbustamento sbustatore = new Sbustamento(protocolFactory,state);
				ProtocolMessage protocolMessage = sbustatore.sbustamento(this.responseMsg,this.getPddContext(),
						busta,RuoloMessaggio.RICHIESTA, gestioneManifest, 
						this.openspcoopProperties.getProprietaManifestAttachments(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD),
						FaseSbustamento.PRE_CONSEGNA_RICHIESTA,this.requestInfo);
				if(protocolMessage!=null) {
					headerProtocolloRisposta = protocolMessage.getBustaRawContent();
					this.responseMsg = protocolMessage.getMessage(); // updated
				}
				
				// Creo busta di risposta solo se la busta di richiesta non conteneva una busta Errore
				if(!isBustaSPCoopErrore){
			
					TipoOraRegistrazione tipoOraRegistrazione = this.openspcoopProperties.getTipoTempoBusta(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
					Busta bustaRisposta = busta.invertiBusta(tipoOraRegistrazione,protocolFactory.createTraduttore().toString(tipoOraRegistrazione));
				
					bustaRisposta.setProfiloDiCollaborazione(busta.getProfiloDiCollaborazione());
					bustaRisposta.setServizio(busta.getServizio());
					bustaRisposta.setVersioneServizio(busta.getVersioneServizio());
					bustaRisposta.setTipoServizio(busta.getTipoServizio());
					bustaRisposta.setAzione(busta.getAzione());
					
					bustaRisposta.setInoltro(busta.getInoltro(),busta.getInoltroValue());
					bustaRisposta.setConfermaRicezione(busta.isConfermaRicezione());
					
					if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(busta.getProfiloDiCollaborazione()) &&
							busta.isConfermaRicezione() &&
							this.openspcoopProperties.isGestioneRiscontri(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD)){
						// Attendono riscontro
						Riscontro r = new Riscontro();
						r.setID(busta.getID());
						r.setOraRegistrazione(DateManager.getDate());
						r.setTipoOraRegistrazione(TipoOraRegistrazione.SINCRONIZZATO);
						bustaRisposta.addRiscontro(r);
						
					} else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(busta.getProfiloDiCollaborazione()) &&
							busta.getRiferimentoMessaggio()==null){
						// devo generare ricevuta
						bustaRisposta.setTipoServizioCorrelato("SPC");
						bustaRisposta.setServizioCorrelato(busta.getServizio()+"Correlato");
					} else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(busta.getProfiloDiCollaborazione()) &&
							busta.getRiferimentoMessaggio()==null){
					
						// salvo messaggio sul database asincrono/repositoryBuste
					
						// get database
						try{
							resource = dbManager.getResource(this.openspcoopProperties.getIdentitaPortaDefault(protocolFactory.getProtocol(), this.requestInfo),"ConnettoreStatus",busta.getID());
						}catch(Exception e){
							throw new ConnettoreException("Risorsa non ottenibile",e);
						}
						if(resource==null)
							throw new ConnettoreException("Risorsa is null");
						if(resource.getResource() == null)
							throw new ConnettoreException("Connessione is null");
						Connection connectionDB = (Connection) resource.getResource();
						
						state.setConnectionDB(connectionDB);
						
						// repository
						RepositoryBuste repositoryBuste = new RepositoryBuste(state, true,protocolFactory);
						repositoryBuste.registraBustaIntoInBox(busta, new ArrayList<>() ,
								OpenSPCoop2Properties.getInstance().getRepositoryIntervalloScadenzaMessaggi());
						Integrazione infoIntegrazione = new Integrazione();
						infoIntegrazione.setIdModuloInAttesa(null);
						repositoryBuste.aggiornaInfoIntegrazioneIntoInBox(busta.getID(),infoIntegrazione);
					
						// asincrono
						ProfiloDiCollaborazione profiloCollaborazione = new ProfiloDiCollaborazione(state,protocolFactory);
						profiloCollaborazione.asincronoSimmetrico_registraRichiestaRicevuta(busta.getID(),busta.getCollaborazione(),
								busta.getTipoServizioCorrelato(),busta.getServizioCorrelato(),busta.getVersioneServizioCorrelato(),true,
								this.openspcoopProperties.getRepositoryIntervalloScadenzaMessaggi());
					
						// commit
						try{
							connectionDB.setAutoCommit(false);
							state.executePreparedStatement(); 
				
							connectionDB.commit();
							connectionDB.setAutoCommit(true);
						}catch (Exception e) {	
							this.logger.error("Riscontrato errore durante la gestione transazione del DB per la richiesta: "+e.getMessage());
							// Rollback quanto effettuato (se l'errore e' avvenuto sul commit, o prima nell'execute delle PreparedStatement)
							try{
								connectionDB.rollback();
							}catch(Exception er){
								// ignore
							}
							state.closePreparedStatement(); // Chiude le PreparedStatement aperte(e non eseguite) per il save del Msg
						}finally {
							// Ripristino connessione
							try{
								connectionDB.setAutoCommit(true);
							}catch(Exception er){
								// ignore
							}
						}
					
					}
				
					// Riferimento Messaggio
					bustaRisposta.setRiferimentoMessaggio(busta.getID());
					
					// Identificativo Messaggio
					String dominio = null;
					if(request.getConnectorProperties()!=null)
						dominio = request.getConnectorProperties().get("identificativo-porta");
					if(dominio==null){
						dominio = protocolFactory.createTraduttore().getIdentificativoPortaDefault(new IDSoggetto(busta.getTipoDestinatario(), busta.getDestinatario()));
					}
					String idBustaRisposta = null;
					Imbustamento imbustatore = new Imbustamento(this.logger.getLogger(), protocolFactory, state);
					try{
						idBustaRisposta = 
							imbustatore.buildID(new IDSoggetto(busta.getTipoDestinatario(), busta.getDestinatario(), dominio), 
									null, 
									this.openspcoopProperties.getGestioneSerializableDBAttesaAttiva(),
									this.openspcoopProperties.getGestioneSerializableDBCheckInterval(),
									RuoloMessaggio.RISPOSTA);
					}catch(Exception e){
						// rilancio
						throw new ConnettoreException(e);
					}
					bustaRisposta.setID(idBustaRisposta);
					
					if(generaTrasmissioneAndataRitorno){
						Trasmissione t = new Trasmissione();
						t.setTipoOrigine(busta.getTipoMittente());
						t.setOrigine(busta.getMittente());
						t.setIdentificativoPortaOrigine(busta.getIdentificativoPortaMittente());
						t.setIndirizzoOrigine(busta.getIndirizzoMittente());
						
						t.setTipoDestinazione(busta.getTipoDestinatario());
						t.setDestinazione(busta.getDestinatario());
						t.setIdentificativoPortaDestinazione(busta.getIdentificativoPortaDestinatario());
						t.setIndirizzoDestinazione(busta.getIndirizzoDestinatario());
						
						t.setOraRegistrazione(busta.getOraRegistrazione());
						t.setTempo(busta.getTipoOraRegistrazione(), busta.getTipoOraRegistrazioneValue());
						bustaRisposta.addTrasmissione(t);
					}
					if(generaTrasmissione || generaTrasmissioneInvertita || generaTrasmissioneAndataRitorno){
						Trasmissione t = new Trasmissione();
						if(generaTrasmissione || generaTrasmissioneAndataRitorno){
							t.setTipoOrigine(bustaRisposta.getTipoMittente());
							t.setOrigine(bustaRisposta.getMittente());
							t.setIdentificativoPortaOrigine(bustaRisposta.getIdentificativoPortaMittente());
							t.setIndirizzoOrigine(bustaRisposta.getIndirizzoMittente());
							
							t.setTipoDestinazione(bustaRisposta.getTipoDestinatario());
							t.setDestinazione(bustaRisposta.getDestinatario());
							t.setIdentificativoPortaDestinazione(bustaRisposta.getIdentificativoPortaDestinatario());
							t.setIndirizzoDestinazione(bustaRisposta.getIndirizzoDestinatario());
						}
						if(generaTrasmissioneInvertita){
							// invertita
							t.setTipoOrigine(bustaRisposta.getTipoDestinatario());
							t.setOrigine(bustaRisposta.getDestinatario());
							t.setIdentificativoPortaOrigine(bustaRisposta.getIdentificativoPortaDestinatario());
							t.setIndirizzoOrigine(bustaRisposta.getIndirizzoDestinatario());
							
							t.setTipoDestinazione(bustaRisposta.getTipoMittente());
							t.setDestinazione(bustaRisposta.getMittente());
							t.setIdentificativoPortaDestinazione(bustaRisposta.getIdentificativoPortaMittente());
							t.setIndirizzoDestinazione(bustaRisposta.getIndirizzoMittente());
						}
						t.setOraRegistrazione(bustaRisposta.getOraRegistrazione());
						t.setTempo(bustaRisposta.getTipoOraRegistrazione(), bustaRisposta.getTipoOraRegistrazioneValue());
						bustaRisposta.addTrasmissione(t);
					}
								
					// imbustamento nuova busta
					Integrazione integrazione = new Integrazione();
					integrazione.setStateless(true);
					
					ProtocolMessage protocolMessageRisposta = imbustatore.imbustamentoRisposta(this.responseMsg,this.getPddContext(),
							bustaRisposta,busta,integrazione,gestioneManifest,false,
							this.openspcoopProperties.getProprietaManifestAttachments(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD),
							FaseImbustamento.PRIMA_SICUREZZA_MESSAGGIO);
					if(protocolMessageRisposta!=null && !protocolMessageRisposta.isPhaseUnsupported()) {
						this.responseMsg = protocolMessageRisposta.getMessage(); // updated
					}
					
					protocolMessageRisposta = imbustatore.imbustamentoRisposta(this.responseMsg,this.getPddContext(),
							bustaRisposta,busta,integrazione,gestioneManifest,false,
							this.openspcoopProperties.getProprietaManifestAttachments(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD),
							FaseImbustamento.DOPO_SICUREZZA_MESSAGGIO);
					if(protocolMessageRisposta!=null && !protocolMessageRisposta.isPhaseUnsupported()) {
						this.responseMsg = protocolMessageRisposta.getMessage(); // updated
					}

				}
				else{
					// rimuovo il FAULT.
					if(this.responseMsg instanceof OpenSPCoop2SoapMessage){
						OpenSPCoop2SoapMessage soapMessage = this.responseMsg.castAsSoap();
						if(soapMessage.hasSOAPFault()){
							soapMessage.getSOAPBody().removeChild(soapMessage.getSOAPBody().getFault());
						}
					}
				}
			}
			
			// content length
			if(this.responseMsg!=null){
				this.contentLength = this.responseMsg.getIncomingMessageContentLength();
			}
			
		}catch(Exception e){
			this.eccezioneProcessamento = e;
			String msgErrore = this.readExceptionMessageFromException(e);
			if(this.generateErrorWithConnectorPrefix) {
				this.errore = "rilevata anomalia; "+msgErrore;
			}
			else {
				this.errore = msgErrore;
			}
			this.logger.error("Rilevata anomalia: "+msgErrore,e);
			return false;
		}finally{
					
			// *** GB ***
			if(validatoreSintattico!=null){
				validatoreSintattico.setHeaderSOAP(null);
			}
			validatoreSintattico = null;
			if(validatoreProtocollo!=null){
				if(validatoreProtocollo.getValidatoreSintattico()!=null){
					validatoreProtocollo.getValidatoreSintattico().setHeaderSOAP(null);
				}
				validatoreProtocollo.setValidatoreSintattico(null);
			}
			validatoreProtocollo = null;
			headerProtocolloRisposta = null;
			// *** GB ***
			
			// release database
			dbManager.releaseResource(this.openspcoopProperties.getIdentitaPortaDefault(protocol, this.requestInfo),
					"ConnettoreStatus", resource);
		}
		
		return true;
	}
    
    private void setRequestHeader(String key, String value, ConnettoreLogger logger, Map<String, List<String>> propertiesTrasportoDebug) throws Exception {
    	List<String> list = new ArrayList<>(); 
    	list.add(value);
    	this.setRequestHeader(key, list, logger, propertiesTrasportoDebug);
    }
    
    private void setRequestHeader(String key, List<String> values, ConnettoreLogger logger, Map<String, List<String>> propertiesTrasportoDebug) throws Exception {
    	
    	if(this.debug && values!=null && !values.isEmpty()) {
        	for (String value : values) {
        		logger.info("Set proprietà trasporto ["+key+"]=["+value+"]",false);		
        	}
    	}
    	setRequestHeader(key, values, propertiesTrasportoDebug);
    	
    }
    @Override
	protected void setRequestHeader(String key,List<String> values) throws ConnettoreException {
    	this.propertiesTrasportoRisposta.put(key, values);
    }
	
    /**
     * Ritorna l'informazione su dove il connettore sta spedendo il messaggio
     * 
     * @return location di inoltro del messaggio
     */
    @Override
	public String getLocation() throws ConnettoreException {
    	return LOCATION;
    }

    @Override
	public void disconnect() throws ConnettoreException {
    	try {
			if(this.requestBout!=null) {
				this.requestBout.clearResources();
			}
		}catch(Exception e) {
			this.logger.error("Release resources failed: "+e.getMessage(), e);
		}
	}
}





