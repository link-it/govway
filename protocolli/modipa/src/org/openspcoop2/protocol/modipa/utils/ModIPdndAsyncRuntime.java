/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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

package org.openspcoop2.protocol.modipa.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.constants.RuoloContesto;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.UrlInvocazioneAPIUtils;
import org.openspcoop2.pdd.core.pdnd.async.FaseInterazioneAsincronaPDND;
import org.openspcoop2.pdd.core.pdnd.async.GestoreInterazioniAsincronePDND;
import org.openspcoop2.pdd.core.pdnd.async.InterazioneAsincronaPDND;
import org.openspcoop2.pdd.core.pdnd.async.InterazioniAsincronePDNDException;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.protocol.engine.SecurityTokenUtilities;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.SecurityToken;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaAccordi;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaServizi;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.MapKey;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.slf4j.Logger;

/**
 * Gestione a runtime degli scambi di dati asincroni PDND (start_interaction, callback_invocation, get_resource, confirmation).
 * 
 * - Fruizione (richiesta): vengono preparate le informazioni utilizzate per negoziare il voucher sulla URL asincrona della policy
 *   (scope, urlCallback, interactionId, entityNumber) e verificato lo stato dell'interazione.
 * - Fruizione (risposta): viene registrato l'avanzamento dell'interazione se l'erogatore ha risposto con successo.
 * - Erogazione (richiesta): vengono verificati il voucher ricevuto e lo stato dell'interazione, registrandone l'avanzamento.
 * 
 * L'interactionId emesso dalla PDND viene utilizzato come identificativo di collaborazione (Conversation-ID).
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIPdndAsyncRuntime {

	public static final String CLAIM_SCOPE = "scope";
	public static final String CLAIM_URL_CALLBACK = "urlCallback";
	public static final String CLAIM_INTERACTION_ID = "interactionId";
	public static final String CLAIM_ENTITY_NUMBER = "entityNumber";
	private static final String CLAIM_CLIENT_ID = "client_id";
	
	// informazioni conservate nel contesto tra la gestione della richiesta e quella della risposta
	private static final MapKey<String> CONTEXT_API_CONFIG = org.openspcoop2.utils.Map.newMapKey("MODIPA_PDND_ASYNC_API_CONFIG");
	private static final MapKey<String> CONTEXT_INTERAZIONE = org.openspcoop2.utils.Map.newMapKey("MODIPA_PDND_ASYNC_INTERAZIONE");
	private static final MapKey<String> CONTEXT_ENTITY_NUMBER = org.openspcoop2.utils.Map.newMapKey("MODIPA_PDND_ASYNC_ENTITY_NUMBER");
	// erogazione: fase e dati da registrare alla risposta con successo dell'applicativo erogatore
	// codici HTTP della risposta che indicano il completamento della fase (configurazione della fruizione/erogazione o default)
	private static final MapKey<String> CONTEXT_HTTP_STATUS_SUCCESSO = org.openspcoop2.utils.Map.newMapKey("MODIPA_PDND_ASYNC_HTTP_STATUS_SUCCESSO");
	private static final MapKey<String> CONTEXT_EROGAZIONE_FASE = org.openspcoop2.utils.Map.newMapKey("MODIPA_PDND_ASYNC_EROGAZIONE_FASE");
	private static final MapKey<String> CONTEXT_EROGAZIONE_SERVIZIO_CALLBACK = org.openspcoop2.utils.Map.newMapKey("MODIPA_PDND_ASYNC_EROGAZIONE_SERVIZIO_CALLBACK");
	
	private static final String PREFIX_INTERACTION = "Asynchronous interaction '";
	private static final String CLAIM_PREFIX = "Claim '";
	private static final String FAILED = "' failed: ";
	
	private final Logger log;
	private final ModIProperties modiProperties;
	private final IProtocolFactory<?> protocolFactory;
	private final IState state;
	private final Context context;
	private final RequestInfo requestInfo;
	private final GestoreInterazioniAsincronePDND gestore;
	
	public ModIPdndAsyncRuntime(Logger log, ModIProperties modiProperties, IProtocolFactory<?> protocolFactory, IState state, Context context, RequestInfo requestInfo) {
		this.log = log;
		this.modiProperties = modiProperties;
		this.protocolFactory = protocolFactory;
		this.state = state;
		this.context = context;
		this.requestInfo = requestInfo;
		String idTransazione = context!=null ? (String) context.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE) : null;
		IDSoggetto idDominio = OpenSPCoop2Properties.getInstance().getIdentitaPortaDefault(protocolFactory.getProtocol(), requestInfo);
		this.gestore = new GestoreInterazioniAsincronePDND(state, idDominio, idTransazione);
	}
	
	
	
	
	/* **** Fruizione: richiesta **** */
	
	public void fruizioneRichiesta(Busta busta, AccordoServizioParteComune aspc, AccordoServizioParteSpecifica asps, String azione,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader) throws ProtocolException {
		
		ModIPdndAsyncApiConfig config = ModIPdndAsyncUtils.readApiConfig(aspc);
		String fase = config.isEnabled() ? ModIPdndAsyncUtils.readFase(aspc, asps.getPortType(), azione) : null;
		if(fase==null) {
			if(config.isEnabled()) {
				checkOperazioneSenzaFase(busta, config, azione);
			}
			return;
		}
		
		IDSoggetto fruitore = new IDSoggetto(busta.getTipoMittente(), busta.getMittente());
		IDServizio idServizio = getIdServizio(busta);
		List<ProtocolProperty> fruizioneProperties = getFruizioneProperties(asps, fruitore);
		
		Map<String, Object> claims = new HashMap<>();
		claims.put(CLAIM_SCOPE, fase);
		boolean noCache = true;
		boolean purposeIdDisabled = false;
		
		// la fase viene tracciata nella transazione anche se la richiesta viene rifiutata dalle verifiche successive (stato dell'interazione, URL di callback)
		this.context.addObject(org.openspcoop2.core.constants.Costanti.PDND_ASYNC_FASE, fase);
		
		if(ModICostanti.MODIPA_PDND_ASYNC_FASE_VALUE_START_INTERACTION.equals(fase)) {
			String urlCallback = resolveUrlCallback(fruizioneProperties, aspc, fruitore, registryReader, configIntegrationReader);
			claims.put(CLAIM_URL_CALLBACK, urlCallback);
			this.context.addObject(org.openspcoop2.core.constants.Costanti.PDND_ASYNC_URL_CALLBACK, urlCallback);
		}
		else if(ModICostanti.MODIPA_PDND_ASYNC_FASE_VALUE_CALLBACK_INVOCATION.equals(fase)) {
			// l'erogatore dell'e-service invoca la callback del fruitore
			InterazioneAsincronaPDND interazione = checkFruizioneCallback(busta, fruizioneProperties, config, registryReader);
			claims.put(CLAIM_INTERACTION_ID, interazione.getInteractionId());
			Integer entityNumber = (Integer) this.context.getObject(CONTEXT_ENTITY_NUMBER);
			claims.put(CLAIM_ENTITY_NUMBER, entityNumber);
			purposeIdDisabled = true; // nella fase callback_invocation non deve essere inviato il purposeId
			busta.setCollaborazione(interazione.getInteractionId());
			if(interazione.getUrlCallback()!=null) {
				this.context.addObject(org.openspcoop2.core.constants.Costanti.PDND_ASYNC_URL_CALLBACK, normalizeUrlCallback(interazione.getUrlCallback(), aspc, azione));
				this.context.addObject(org.openspcoop2.core.constants.Costanti.PDND_ASYNC_URL_CALLBACK_ORIGINAL, interazione.getUrlCallback());
			}
		}
		else {
			// get_resource o confirmation
			InterazioneAsincronaPDND interazione = checkFruizioneEService(busta, fase, fruitore, idServizio);
			claims.put(CLAIM_INTERACTION_ID, interazione.getInteractionId());
			// nella fase get_resource il voucher può essere riutilizzato (es. scaricamento a blocchi): la chiave di cache include l'interactionId
			noCache = !ModICostanti.MODIPA_PDND_ASYNC_FASE_VALUE_GET_RESOURCE.equals(fase);
			purposeIdDisabled = !isPurposeIdAbilitato(fruizioneProperties);
			busta.setCollaborazione(interazione.getInteractionId());
		}
		
		this.context.addObject(org.openspcoop2.pdd.core.token.Costanti.PDND_ASYNC_CONTEXT_FASE, fase);
		this.context.addObject(org.openspcoop2.pdd.core.token.Costanti.PDND_ASYNC_CONTEXT_CLAIMS, claims);
		this.context.addObject(org.openspcoop2.pdd.core.token.Costanti.PDND_ASYNC_CONTEXT_PURPOSE_ID_DISABLED, purposeIdDisabled);
		this.context.addObject(org.openspcoop2.pdd.core.token.Costanti.PDND_ASYNC_CONTEXT_NO_CACHE, noCache);
		this.context.addObject(CONTEXT_API_CONFIG, config);
		this.context.addObject(CONTEXT_HTTP_STATUS_SUCCESSO, readHttpStatusSuccesso(fruizioneProperties));
	}
	
	private InterazioneAsincronaPDND checkFruizioneEService(Busta busta, String fase, IDSoggetto fruitore, IDServizio idServizio) throws ProtocolException {
		String interactionId = readConversationId(busta, fase);
		InterazioneAsincronaPDND interazione = getInterazione(interactionId, TipoPdD.DELEGATA);
		if(interazione==null || !interazione.getFruitore().equals(fruitore) || !isStessoServizio(interazione.getServizio(), idServizio)) {
			throw interazioneNonTrovata(interactionId);
		}
		checkStatoFaseRisorsa(interazione, fase);
		this.context.addObject(CONTEXT_INTERAZIONE, interazione);
		return interazione;
	}
	
	/**
	 * La URL di callback comunicata alla PDND dovrebbe essere la URL dell'API di callback (senza il path della risorsa):
	 * per un'API REST il connettore accoda alla URL il path della risorsa invocata. Se il fruitore ha comunicato una URL che termina
	 * già con il path (statico) della risorsa, il path viene eliminato per evitare che venga ripetuto.
	 * La URL originale resta disponibile con la keyword ${context:pdndAsyncUrlCallbackOriginal}.
	 */
	private String normalizeUrlCallback(String urlCallback, AccordoServizioParteComune aspc, String azione) {
		if(aspc==null || !ServiceBinding.REST.equals(aspc.getServiceBinding()) || azione==null) {
			return urlCallback;
		}
		String path = null;
		for (Resource r : aspc.getResourceList()) {
			if(azione.equals(r.getNome())) {
				path = r.getPath();
				break;
			}
		}
		if(path==null || "".equals(path.trim()) || path.contains("{") || path.contains("*")) {
			return urlCallback;
		}
		String p = path.trim();
		while(p.endsWith("/")) {
			p = p.substring(0, p.length()-1);
		}
		if(!p.startsWith("/")) {
			p = "/"+p;
		}
		if("/".equals(p) || "".equals(p)) {
			return urlCallback;
		}
		String base = urlCallback;
		String query = "";
		int idx = urlCallback.indexOf('?');
		if(idx>0) {
			base = urlCallback.substring(0, idx);
			query = urlCallback.substring(idx);
		}
		while(base.endsWith("/")) {
			base = base.substring(0, base.length()-1);
		}
		if(base.length()>p.length() && base.endsWith(p)) {
			String normalizzata = base.substring(0, base.length()-p.length())+query;
			this.log.debug("URL di callback '{}' normalizzata in '{}': il path della risorsa '{}' viene accodato dal connettore", urlCallback, normalizzata, p);
			return normalizzata;
		}
		return urlCallback;
	}
	
	private InterazioneAsincronaPDND checkFruizioneCallback(Busta busta, List<ProtocolProperty> fruizioneProperties, ModIPdndAsyncApiConfig config,
			IRegistryReader registryReader) throws ProtocolException {
		String fase = ModICostanti.MODIPA_PDND_ASYNC_FASE_VALUE_CALLBACK_INVOCATION;
		String interactionId = readConversationId(busta, fase);
		int entityNumber = readEntityNumber(fruizioneProperties);
		
		// l'interazione è stata registrata dall'erogazione dell'e-service: il fruitore della callback è l'erogatore dell'e-service.
		// Il soggetto erogatore della callback non viene confrontato con il fruitore dell'e-service: la fruizione può essere configurata
		// verso un qualsiasi soggetto (es. un'unica fruizione, con connettore ${context:pdndAsyncUrlCallback}, per tutti i fruitori dell'e-service)
		IDSoggetto erogatoreEService = new IDSoggetto(busta.getTipoMittente(), busta.getMittente());
		InterazioneAsincronaPDND interazione = getInterazione(interactionId, TipoPdD.APPLICATIVA);
		if(interazione==null || 
				!interazione.getServizio().getSoggettoErogatore().equals(erogatoreEService) || 
				!isApiCorrelata(registryReader, interazione.getServizio(), config.getUriApiCorrelata())) {
			throw interazioneNonTrovata(interactionId);
		}
		checkStatoFaseCallback(interazione);
		if(interazione.getLimiteEntita()!=null && entityNumber>interazione.getLimiteEntita().intValue()) {
			throw ModIPdndAsyncException.invalidRequest("The entity number '"+entityNumber+"' exceeds the maximum number of entities per response ("+interazione.getLimiteEntita()+") defined for the asynchronous interaction");
		}
		this.context.addObject(CONTEXT_INTERAZIONE, interazione);
		this.context.addObject(CONTEXT_ENTITY_NUMBER, entityNumber);
		return interazione;
	}
	
	
	
	
	/* **** Fruizione: risposta **** */
	
	public void fruizioneRisposta(Busta bustaRisposta, OpenSPCoop2Message msg) throws ProtocolException {
		
		if(this.context==null || !this.context.containsKey(CONTEXT_API_CONFIG)) {
			return;
		}
		String fase = (String) this.context.getObject(org.openspcoop2.pdd.core.token.Costanti.PDND_ASYNC_CONTEXT_FASE);
		ModIPdndAsyncApiConfig config = (ModIPdndAsyncApiConfig) this.context.getObject(CONTEXT_API_CONFIG);
		InterazioneAsincronaPDND interazione = (InterazioneAsincronaPDND) this.context.getObject(CONTEXT_INTERAZIONE);
		boolean successo = isSuccesso(msg);
		Date now = DateManager.getDate();
		
		try {
			if(ModICostanti.MODIPA_PDND_ASYNC_FASE_VALUE_START_INTERACTION.equals(fase)) {
				registraStartFruizione(bustaRisposta, config, successo, now);
				return;
			}
			if(interazione==null) {
				return;
			}
			bustaRisposta.setCollaborazione(interazione.getInteractionId());
			if(!successo) {
				return;
			}
			if(ModICostanti.MODIPA_PDND_ASYNC_FASE_VALUE_GET_RESOURCE.equals(fase)) {
				this.gestore.registraGetResource(interazione.getId(), now);
			}
			else if(ModICostanti.MODIPA_PDND_ASYNC_FASE_VALUE_CONFIRMATION.equals(fase)) {
				this.gestore.registraConfirmation(interazione.getId(), now);
			}
			else if(ModICostanti.MODIPA_PDND_ASYNC_FASE_VALUE_CALLBACK_INVOCATION.equals(fase)) {
				IDServizio idServizioCallback = getIdServizioRisposta(bustaRisposta);
				Integer entityNumber = (Integer) this.context.getObject(CONTEXT_ENTITY_NUMBER);
				this.gestore.registraCallback(interazione.getId(), idServizioCallback, entityNumber, now, addSeconds(now, interazione.getTempoDisponibilita()));
			}
		}catch(InterazioniAsincronePDNDException e) {
			throw new ProtocolException("Registration of the asynchronous interaction phase '"+fase+FAILED+e.getMessage(), e);
		}
	}
	
	private void registraStartFruizione(Busta bustaRisposta, ModIPdndAsyncApiConfig config, boolean successo, Date now) throws ProtocolException, InterazioniAsincronePDNDException {
		SecurityToken securityToken = SecurityTokenUtilities.readSecurityToken(this.context);
		if(securityToken==null || securityToken.getAccessToken()==null) {
			throw new ProtocolException("Voucher negotiated for the 'start_interaction' phase not available");
		}
		String interactionId = null;
		String purposeId = null;
		String consumerId = null;
		String clientId = null;
		try {
			interactionId = securityToken.getAccessToken().getPayloadClaim(CLAIM_INTERACTION_ID);
			purposeId = securityToken.getAccessToken().getPayloadClaim(org.openspcoop2.pdd.core.token.Costanti.PDND_PURPOSE_ID);
			consumerId = securityToken.getAccessToken().getPayloadClaim(org.openspcoop2.pdd.core.token.Costanti.PDND_CONSUMER_ID);
			clientId = securityToken.getAccessToken().getPayloadClaim(CLAIM_CLIENT_ID);
		}catch(Exception e) {
			throw new ProtocolException("Reading claims of the voucher negotiated for the 'start_interaction' phase failed: "+e.getMessage(), e);
		}
		if(interactionId==null || "".equals(interactionId)) {
			throw new ProtocolException(CLAIM_PREFIX+CLAIM_INTERACTION_ID+"' not found in the voucher negotiated for the 'start_interaction' phase");
		}
		bustaRisposta.setCollaborazione(interactionId);
		if(!successo) {
			return;
		}
		
		InterazioneAsincronaPDND interazione = new InterazioneAsincronaPDND();
		interazione.setInteractionId(interactionId);
		interazione.setRuolo(TipoPdD.DELEGATA);
		interazione.setFase(FaseInterazioneAsincronaPDND.START_INTERACTION);
		// nella busta di risposta il mittente è l'erogatore
		interazione.setFruitore(new IDSoggetto(bustaRisposta.getTipoDestinatario(), bustaRisposta.getDestinatario()));
		interazione.setServizio(getIdServizioRisposta(bustaRisposta));
		interazione.setPurposeId(purposeId);
		interazione.setConsumerId(consumerId);
		interazione.setClientId(clientId);
		interazione.setUrlCallback((String) this.context.getObject(org.openspcoop2.core.constants.Costanti.PDND_ASYNC_URL_CALLBACK));
		setConfig(interazione, config);
		interazione.setIdTransazioneStart((String) this.context.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
		interazione.setDataStart(now);
		interazione.setDataScadenza(addSeconds(now, config.getTempoMaxCallback()));
		interazione.setDataAggiornamento(now);
		this.gestore.insert(interazione);
	}
	
	
	
	
	/* **** Erogazione: richiesta **** */
	
	public void erogazioneRichiesta(Busta busta, OpenSPCoop2Message msg, AccordoServizioParteComune aspc, AccordoServizioParteSpecifica asps,
			IRegistryReader registryReader) throws ProtocolException {
		
		ModIPdndAsyncApiConfig config = ModIPdndAsyncUtils.readApiConfig(aspc);
		String fase = config.isEnabled() ? ModIPdndAsyncUtils.readFase(aspc, asps.getPortType(), busta.getAzione()) : null;
		if(fase==null) {
			return;
		}
		
		// la fase viene tracciata nella transazione anche se la richiesta viene rifiutata dalle verifiche successive (voucher, stato dell'interazione)
		this.context.addObject(org.openspcoop2.core.constants.Costanti.PDND_ASYNC_FASE, fase);
		
		InformazioniToken informazioniToken = null;
		if(this.context.containsKey(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE)) {
			informazioniToken = (InformazioniToken) this.context.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
		}
		if(informazioniToken==null) {
			throw ModIPdndAsyncException.invalidRequest("PDND voucher required for the asynchronous interaction phase '"+fase+"' not present");
		}
		if(informazioniToken.getScopes()==null || !informazioniToken.getScopes().contains(fase)) {
			throw ModIPdndAsyncException.invalidRequest("The PDND voucher does not contain the scope '"+fase+"' expected for the invoked operation");
		}
		String interactionId = getClaim(informazioniToken, CLAIM_INTERACTION_ID);
		if(interactionId==null) {
			throw ModIPdndAsyncException.invalidRequest(claimNonTrovato(CLAIM_INTERACTION_ID));
		}
		
		this.context.addObject(CONTEXT_HTTP_STATUS_SUCCESSO, readHttpStatusSuccesso(asps.getProtocolPropertyList()));
		try {
			if(ModICostanti.MODIPA_PDND_ASYNC_FASE_VALUE_START_INTERACTION.equals(fase)) {
				erogazioneStart(busta, msg, asps, config, informazioniToken, interactionId, registryReader);
			}
			else if(ModICostanti.MODIPA_PDND_ASYNC_FASE_VALUE_CALLBACK_INVOCATION.equals(fase)) {
				erogazioneCallback(busta, msg, config, informazioniToken, interactionId, registryReader);
			}
			else {
				erogazioneRisorsa(busta, fase, informazioniToken, interactionId);
			}
		}catch(InterazioniAsincronePDNDException e) {
			throw ModIPdndAsyncException.internalError("Management of the asynchronous interaction phase '"+fase+FAILED+e.getMessage(), e);
		}
		busta.setCollaborazione(interactionId);
	}
	
	private void erogazioneStart(Busta busta, OpenSPCoop2Message msg, AccordoServizioParteSpecifica asps, ModIPdndAsyncApiConfig config,
			InformazioniToken informazioniToken, String interactionId,
			IRegistryReader registryReader) throws ProtocolException, InterazioniAsincronePDNDException {
		
		Date now = DateManager.getDate();
		String urlCallback = getClaim(informazioniToken, CLAIM_URL_CALLBACK);
		if(urlCallback==null) {
			throw ModIPdndAsyncException.invalidRequest(claimNonTrovato(CLAIM_URL_CALLBACK));
		}
		// il soggetto fruitore può non essere noto (es. consumer PDND non registrato come soggetto): l'interazione è associata al consumerId del voucher
		IDSoggetto fruitore = (busta.getTipoMittente()!=null && busta.getMittente()!=null) ? new IDSoggetto(busta.getTipoMittente(), busta.getMittente()) : null;
		IDServizio idServizio = getIdServizio(busta);
		List<ProtocolProperty> erogazioneProperties = asps.getProtocolPropertyList();
		if(isTrue(erogazioneProperties, ModICostanti.MODIPA_PDND_ASYNC_VERIFICA_URL_CALLBACK)) {
			checkUrlCallback(urlCallback, idServizio, registryReader);
		}
		if(getInterazione(interactionId, TipoPdD.APPLICATIVA)!=null) {
			throw ModIPdndAsyncException.invalidState(PREFIX_INTERACTION+interactionId+"' already started");
		}
		
		InterazioneAsincronaPDND interazione = new InterazioneAsincronaPDND();
		interazione.setInteractionId(interactionId);
		interazione.setRuolo(TipoPdD.APPLICATIVA);
		interazione.setFase(FaseInterazioneAsincronaPDND.START_INTERACTION);
		interazione.setFruitore(fruitore);
		interazione.setServizio(idServizio);
		interazione.setPurposeId(getClaim(informazioniToken, org.openspcoop2.pdd.core.token.Costanti.PDND_PURPOSE_ID));
		interazione.setConsumerId(getClaim(informazioniToken, org.openspcoop2.pdd.core.token.Costanti.PDND_CONSUMER_ID));
		interazione.setClientId(informazioniToken.getClientId());
		interazione.setUrlCallback(urlCallback);
		setConfig(interazione, config);
		interazione.setIdTransazioneStart((String) this.context.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
		interazione.setDataStart(now);
		interazione.setDataScadenza(addSeconds(now, config.getTempoMaxCallback()));
		interazione.setDataAggiornamento(now);
		// l'interazione viene registrata solamente se l'applicativo erogatore risponde con successo (vedi erogazioneRisposta)
		setRegistrazioneErogazione(ModICostanti.MODIPA_PDND_ASYNC_FASE_VALUE_START_INTERACTION, interazione);
		
		this.context.addObject(org.openspcoop2.core.constants.Costanti.PDND_ASYNC_URL_CALLBACK, urlCallback);
		
		// la URL di callback viene comunicata all'applicativo erogatore
		String codifica = getStringValue(erogazioneProperties, ModICostanti.MODIPA_PDND_ASYNC_URL_CALLBACK_HEADER_CODIFICA);
		if(codifica==null || ModICostanti.MODIPA_PDND_ASYNC_VALUE_CODIFICA_DEFAULT.equals(codifica)) {
			codifica = this.modiProperties.getPdndAsyncUrlCallbackHeaderEncoding();
		}
		msg.forceTransportHeader(this.modiProperties.getPdndAsyncUrlCallbackHeaderName(), ModIPdndAsyncUtils.encode(urlCallback, codifica));
	}
	
	private void erogazioneRisorsa(Busta busta, String fase, InformazioniToken informazioniToken, String interactionId) throws ProtocolException, InterazioniAsincronePDNDException {
		InterazioneAsincronaPDND interazione = getInterazione(interactionId, TipoPdD.APPLICATIVA);
		String consumerId = getClaim(informazioniToken, org.openspcoop2.pdd.core.token.Costanti.PDND_CONSUMER_ID);
		boolean consumerDifferente = consumerId!=null && interazione!=null && interazione.getConsumerId()!=null && !consumerId.equals(interazione.getConsumerId());
		if(interazione==null || consumerDifferente || !isStessoServizio(interazione.getServizio(), getIdServizio(busta))) {
			throw interazioneNonTrovata(interactionId);
		}
		checkStatoFaseRisorsa(interazione, fase);
		// get_resource e confirmation vengono registrate solamente se l'applicativo erogatore risponde con successo (vedi erogazioneRisposta):
		// in caso contrario il fruitore può riprovare
		setRegistrazioneErogazione(fase, interazione);
	}
	
	
	
	
	/* **** Erogazione: risposta **** */
	
	/**
	 * Le fasi ricevute dall'erogazione vengono registrate solamente se l'applicativo risponde con successo (2xx):
	 * se l'applicativo non è disponibile o risponde con un errore, l'interazione resta nello stato precedente e la fase può essere ripetuta.
	 * Un errore nella registrazione fa fallire la transazione; una registrazione già effettuata da una richiesta concorrente viene solamente segnalata.
	 */
	public void erogazioneRisposta(OpenSPCoop2Message msg) throws ProtocolException {
		if(this.context==null || !this.context.containsKey(CONTEXT_INTERAZIONE) || !this.context.containsKey(CONTEXT_EROGAZIONE_FASE)) {
			return;
		}
		if(!isSuccesso(msg)) {
			return;
		}
		String fase = (String) this.context.getObject(CONTEXT_EROGAZIONE_FASE);
		InterazioneAsincronaPDND interazione = (InterazioneAsincronaPDND) this.context.getObject(CONTEXT_INTERAZIONE);
		Date now = DateManager.getDate();
		try {
			boolean registrata = true;
			if(ModICostanti.MODIPA_PDND_ASYNC_FASE_VALUE_START_INTERACTION.equals(fase)) {
				this.gestore.insert(interazione);
			}
			else if(ModICostanti.MODIPA_PDND_ASYNC_FASE_VALUE_CALLBACK_INVOCATION.equals(fase)) {
				IDServizio servizioCallback = (IDServizio) this.context.getObject(CONTEXT_EROGAZIONE_SERVIZIO_CALLBACK);
				Integer entityNumber = (Integer) this.context.getObject(CONTEXT_ENTITY_NUMBER);
				registrata = this.gestore.registraCallback(interazione.getId(), servizioCallback, entityNumber, now, addSeconds(now, interazione.getTempoDisponibilita()));
			}
			else if(ModICostanti.MODIPA_PDND_ASYNC_FASE_VALUE_GET_RESOURCE.equals(fase)) {
				registrata = this.gestore.registraGetResource(interazione.getId(), now);
			}
			else if(ModICostanti.MODIPA_PDND_ASYNC_FASE_VALUE_CONFIRMATION.equals(fase)) {
				registrata = this.gestore.registraConfirmation(interazione.getId(), now);
			}
			if(!registrata) {
				this.log.warn("Fase '{}' dell'interazione asincrona '{}' non registrata: stato modificato da una richiesta concorrente", fase, interazione.getInteractionId());
			}
		}catch(Exception e) {
			throw ModIPdndAsyncException.internalError("Registration of the asynchronous interaction phase '"+fase+"' for the interaction '"+interazione.getInteractionId()+FAILED+e.getMessage(), e);
		}
	}
	private void setRegistrazioneErogazione(String fase, InterazioneAsincronaPDND interazione) {
		this.context.addObject(CONTEXT_EROGAZIONE_FASE, fase);
		this.context.addObject(CONTEXT_INTERAZIONE, interazione);
	}
	
	private void erogazioneCallback(Busta busta, OpenSPCoop2Message msg, ModIPdndAsyncApiConfig config, InformazioniToken informazioniToken, String interactionId,
			IRegistryReader registryReader) throws ProtocolException, InterazioniAsincronePDNDException {
		// l'interazione è stata registrata dalla fruizione dell'e-service: l'erogatore della callback è il fruitore dell'e-service
		IDSoggetto fruitoreEService = new IDSoggetto(busta.getTipoDestinatario(), busta.getDestinatario());
		InterazioneAsincronaPDND interazione = getInterazione(interactionId, TipoPdD.DELEGATA);
		if(interazione==null || !interazione.getFruitore().equals(fruitoreEService) ||
				!isApiCorrelata(registryReader, interazione.getServizio(), config.getUriApiCorrelata())) {
			throw interazioneNonTrovata(interactionId);
		}
		checkStatoFaseCallback(interazione);
		Integer entityNumber = null;
		String en = getClaim(informazioniToken, CLAIM_ENTITY_NUMBER);
		if(en!=null) {
			try {
				// il claim può essere serializzato come intero o come numero decimale (es. '4' o '4.0')
				entityNumber = new java.math.BigDecimal(en.trim()).intValueExact();
			}catch(Exception e) {
				// valore non numerico: non viene registrato
				this.log.debug("Claim '{}' con valore non numerico '{}' ignorato", CLAIM_ENTITY_NUMBER, en);
			}
		}
		// la callback viene registrata solamente se l'applicativo (del fruitore) risponde con successo (vedi erogazioneRisposta):
		// in caso contrario l'erogatore può invocarla nuovamente
		setRegistrazioneErogazione(ModICostanti.MODIPA_PDND_ASYNC_FASE_VALUE_CALLBACK_INVOCATION, interazione);
		this.context.addObject(CONTEXT_EROGAZIONE_SERVIZIO_CALLBACK, getIdServizio(busta));
		if(entityNumber!=null) {
			this.context.addObject(CONTEXT_ENTITY_NUMBER, entityNumber);
			// il numero di entità, se presente nel voucher, viene inoltrato all'applicativo che implementa l'API di callback
			msg.forceTransportHeader(this.modiProperties.getPdndAsyncEntityNumberHeaderName(), entityNumber.toString());
		}
		if(interazione.getUrlCallback()!=null) {
			this.context.addObject(org.openspcoop2.core.constants.Costanti.PDND_ASYNC_URL_CALLBACK, interazione.getUrlCallback());
		}
	}
	
	
	
	
	/* **** Controlli sullo stato **** */
	
	private void checkStatoFaseRisorsa(InterazioneAsincronaPDND interazione, String fase) throws ModIPdndAsyncException {
		String id = interazione.getInteractionId();
		if(interazione.isConfermata()) {
			throw ModIPdndAsyncException.invalidState(PREFIX_INTERACTION+id+"' already confirmed: phase '"+fase+"' not allowed");
		}
		if(!interazione.isCallbackRicevuta()) {
			checkScadenza(interazione, "Maximum callback time", interazione.getTempoMaxCallback());
			throw ModIPdndAsyncException.invalidState(PREFIX_INTERACTION+id+"': resource not yet available (callback not received), phase '"+fase+"' not allowed");
		}
		checkScadenza(interazione, "Resource availability time", interazione.getTempoDisponibilita());
		// le fasi devono essere invocate in sequenza: la conferma di ricezione richiede che la risposta sia stata ottenuta almeno una volta
		if(ModICostanti.MODIPA_PDND_ASYNC_FASE_VALUE_CONFIRMATION.equals(fase) && interazione.getNumeroGetResource()<=0) {
			throw ModIPdndAsyncException.invalidState(PREFIX_INTERACTION+id+"': response not yet obtained (get_resource), phase '"+fase+"' not allowed");
		}
	}
	private void checkStatoFaseCallback(InterazioneAsincronaPDND interazione) throws ModIPdndAsyncException {
		String id = interazione.getInteractionId();
		if(interazione.isConfermata()) {
			throw ModIPdndAsyncException.invalidState(PREFIX_INTERACTION+id+"' already confirmed: phase 'callback_invocation' not allowed");
		}
		if(interazione.isCallbackRicevuta()) {
			throw ModIPdndAsyncException.invalidState(PREFIX_INTERACTION+id+"': callback already invoked");
		}
		checkScadenza(interazione, "Maximum callback time", interazione.getTempoMaxCallback());
	}
	private void checkScadenza(InterazioneAsincronaPDND interazione, String descrizioneTempo, Long secondi) throws ModIPdndAsyncException {
		Date scadenza = interazione.getDataScadenza();
		if(scadenza!=null && scadenza.before(DateManager.getDate())) {
			throw ModIPdndAsyncException.expired(descrizioneTempo+(secondi!=null ? " ("+secondi+"s)" : "")+" expired at "+
					DateUtils.getSimpleDateFormatMs().format(scadenza)+" for the asynchronous interaction '"+interazione.getInteractionId()+"'");
		}
	}
	
	
	
	
	/* **** URL di callback **** */
	
	private String resolveUrlCallback(List<ProtocolProperty> fruizioneProperties, AccordoServizioParteComune aspc, IDSoggetto fruitore,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader) throws ProtocolException {
		
		String sorgente = getStringValue(fruizioneProperties, ModICostanti.MODIPA_PDND_ASYNC_URL_CALLBACK_SORGENTE);
		if(ModICostanti.MODIPA_PDND_ASYNC_URL_CALLBACK_SORGENTE_VALUE_CLIENT.equals(sorgente)) {
			String url = readUrlCallbackClient(fruizioneProperties);
			if(url!=null) {
				return url;
			}
		}
		return getUrlErogazioneCallback(aspc, fruitore, registryReader, configIntegrationReader);
	}
	
	private String readUrlCallbackClient(List<ProtocolProperty> fruizioneProperties) throws ProtocolException {
		String modalita = getStringValue(fruizioneProperties, ModICostanti.MODIPA_PDND_ASYNC_URL_CALLBACK_MODALITA);
		String nome = getStringValue(fruizioneProperties, ModICostanti.MODIPA_PDND_ASYNC_URL_CALLBACK_NOME);
		if(nome==null) {
			nome = isModalitaQuery(modalita) ? ModICostanti.MODIPA_PDND_ASYNC_URL_CALLBACK_NOME_QUERY_DEFAULT : ModICostanti.MODIPA_PDND_ASYNC_URL_CALLBACK_NOME_DEFAULT;
		}
		String value = readValoreClient(modalita, nome);
		if(value==null || "".equals(value.trim())) {
			if(isTrue(fruizioneProperties, ModICostanti.MODIPA_PDND_ASYNC_URL_CALLBACK_OBBLIGATORIA)) {
				throw ModIPdndAsyncException.invalidRequest("Callback URL not provided ("+getRiferimentoClient(modalita, nome)+")");
			}
			return null;
		}
		String codifica = getStringValue(fruizioneProperties, ModICostanti.MODIPA_PDND_ASYNC_URL_CALLBACK_CODIFICA);
		try {
			return ModIPdndAsyncUtils.decode(value.trim(), codifica);
		}catch(ProtocolException e) {
			throw ModIPdndAsyncException.invalidRequest("Callback URL provided ("+getRiferimentoClient(modalita, nome)+") is not valid: "+e.getMessage());
		}
	}
	
	private String getUrlErogazioneCallback(AccordoServizioParteComune aspc, IDSoggetto fruitore,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader) throws ProtocolException {
		
		List<IDServizio> erogazioni = new ArrayList<>();
		try {
			for (IDAccordo idApiCallback : findApiCallback(registryReader, IDAccordoFactory.getInstance().getIDAccordoFromAccordo(aspc))) {
				ProtocolFiltroRicercaServizi filtro = new ProtocolFiltroRicercaServizi();
				filtro.setIdAccordoServizioParteComune(idApiCallback);
				filtro.setSoggettoErogatore(fruitore);
				erogazioni.addAll(findIdServizi(registryReader, filtro));
			}
		}catch(Exception e) {
			throw new ProtocolException("Search of the callback API implementation failed: "+e.getMessage(), e);
		}
		if(erogazioni.isEmpty()) {
			throw ModIPdndAsyncException.configurationError("Callback API implementation (erogazione) of the subject '"+fruitore+"' not found: the callback URL cannot be determined");
		}
		if(erogazioni.size()>1) {
			throw ModIPdndAsyncException.configurationError("Multiple callback API implementations (erogazioni) of the subject '"+fruitore+"' found: the callback URL cannot be determined");
		}
		IDServizio idServizioCallback = erogazioni.get(0);
		try {
			String nomePorta = null;
			List<MappingErogazionePortaApplicativa> list = configIntegrationReader.getMappingErogazionePortaApplicativaList(idServizioCallback);
			for (MappingErogazionePortaApplicativa mapping : list) {
				if(mapping.isDefault()) {
					nomePorta = mapping.getIdPortaApplicativa().getNome();
				}
			}
			AccordoServizioParteSpecifica aspsCallback = registryReader.getAccordoServizioParteSpecifica(idServizioCallback);
			AccordoServizioParteComune aspcCallback = registryReader.getAccordoServizioParteComune(IDAccordoFactory.getInstance().getIDAccordoFromUri(aspsCallback.getAccordoServizioParteComune()));
			boolean rest = ServiceBinding.REST.equals(aspcCallback.getServiceBinding());
			return UrlInvocazioneAPIUtils.getUrlInvocazione(this.protocolFactory, this.state, this.requestInfo, 
					rest, aspcCallback, RuoloContesto.PORTA_APPLICATIVA, nomePorta, fruitore);
		}catch(Exception e) {
			throw new ProtocolException("Computation of the callback URL failed: "+e.getMessage(), e);
		}
	}
	
	/**
	 * La URL di callback deve corrispondere al connettore di una fruizione, da parte del soggetto erogatore dell'e-service,
	 * di un'API di callback associata all'API dell'e-service. Il soggetto erogatore della callback non viene considerato:
	 * la fruizione può essere configurata verso un qualsiasi soggetto (es. un'unica fruizione per tutti i fruitori dell'e-service).
	 * La URL di callback deve coincidere con la URL del connettore o estenderla con un ulteriore path (es. risorsa di callback).
	 * Le fruizioni con connettore dinamico non vengono considerate; se esistono solamente fruizioni di questo tipo la verifica non viene effettuata.
	 */
	private void checkUrlCallback(String urlCallback, IDServizio idServizioEService, IRegistryReader registryReader) throws ProtocolException {
		List<String> locations = new ArrayList<>();
		try {
			IDAccordo idApiEService = IDAccordoFactory.getInstance().getIDAccordoFromUri(registryReader.getAccordoServizioParteSpecifica(idServizioEService).getAccordoServizioParteComune());
			for (IDAccordo idApiCallback : findApiCallback(registryReader, idApiEService)) {
				ProtocolFiltroRicercaServizi filtro = new ProtocolFiltroRicercaServizi();
				filtro.setIdAccordoServizioParteComune(idApiCallback);
				for (IDServizio idCallback : findIdServizi(registryReader, filtro)) {
					String l = getLocationFruizione(registryReader.getAccordoServizioParteSpecifica(idCallback), idServizioEService.getSoggettoErogatore());
					if(l!=null) {
						locations.add(l);
					}
				}
			}
		}catch(Exception e) {
			throw new ProtocolException("Callback URL verification failed: "+e.getMessage(), e);
		}
		if(locations.isEmpty()) {
			throw ModIPdndAsyncException.configurationError("Callback URL verification failed: no callback API subscription (fruizione) of the subject '"+idServizioEService.getSoggettoErogatore()+"' found");
		}
		boolean soloDinamiche = true;
		String url = normalize(urlCallback);
		for (String location : locations) {
			if(location.contains("${")) {
				continue;
			}
			soloDinamiche = false;
			String base = normalize(location);
			if(url.equals(base) || url.startsWith(base+"/") || url.startsWith(base+"?")) {
				return;
			}
		}
		if(soloDinamiche) {
			// connettori dinamici (es. ${context:pdndAsyncUrlCallback}): la verifica non è applicabile
			this.log.debug("Verifica della URL di callback non effettuata: connettori delle fruizioni dinamici ({})", locations);
			return;
		}
		throw ModIPdndAsyncException.invalidRequest("Callback URL '"+urlCallback+"' does not match the endpoint of any callback API subscription (fruizione) of the subject '"+idServizioEService.getSoggettoErogatore()+"'");
	}
	private static String normalize(String url) {
		String u = url.trim();
		while(u.endsWith("/")) {
			u = u.substring(0, u.length()-1);
		}
		return u;
	}
	private static String getLocationFruizione(AccordoServizioParteSpecifica asps, IDSoggetto idFruitore) {
		for (Fruitore f : asps.getFruitoreList()) {
			if(idFruitore.getTipo().equals(f.getTipo()) && idFruitore.getNome().equals(f.getNome()) && f.getConnettore()!=null) {
				for (Property p : f.getConnettore().getPropertyList()) {
					if(CostantiConnettori.CONNETTORE_LOCATION.equals(p.getNome())) {
						return p.getValore();
					}
				}
			}
		}
		return null;
	}
	
	
	
	
	/* **** Utilities **** */
	
	/**
	 * Un'operazione non associata ad alcuna fase di un'API configurata per gli scambi asincroni viene gestita come operazione sincrona.
	 * Se però il client indica l'identificativo di un'interazione asincrona esistente, la richiesta viene rifiutata:
	 * il client sta cercando di proseguire l'interazione tramite un'operazione che non ne fa parte (es. fase non ancora associata nell'API).
	 */
	private void checkOperazioneSenzaFase(Busta busta, ModIPdndAsyncApiConfig config, String azione) throws ProtocolException {
		String id = null;
		if(this.context.containsKey(org.openspcoop2.core.constants.Costanti.ID_COLLABORAZIONE_RICHIESTA_INTEGRAZIONE)) {
			id = (String) this.context.getObject(org.openspcoop2.core.constants.Costanti.ID_COLLABORAZIONE_RICHIESTA_INTEGRAZIONE);
		}
		if(id==null || "".equals(id)) {
			id = busta.getCollaborazione();
		}
		if(id==null || "".equals(id)) {
			return;
		}
		// fruizione dell'e-service: interazioni registrate dal fruitore; fruizione dell'API di callback: interazioni registrate dall'erogatore
		TipoPdD ruolo = config.isRuoloCallback() ? TipoPdD.APPLICATIVA : TipoPdD.DELEGATA;
		if(getInterazione(id, ruolo)!=null) {
			throw ModIPdndAsyncException.invalidRequest("The invoked operation '"+azione+"' is not associated with any phase of the asynchronous interaction, "
					+ "but the identifier of the existing asynchronous interaction '"+id+"' has been provided");
		}
	}
	
	private String readConversationId(Busta busta, String fase) throws ModIPdndAsyncException {
		String id = null;
		if(this.context.containsKey(org.openspcoop2.core.constants.Costanti.ID_COLLABORAZIONE_RICHIESTA_INTEGRAZIONE)) {
			id = (String) this.context.getObject(org.openspcoop2.core.constants.Costanti.ID_COLLABORAZIONE_RICHIESTA_INTEGRAZIONE);
		}
		if(id==null || "".equals(id)) {
			id = busta.getCollaborazione();
		}
		if(id==null || "".equals(id)) {
			throw ModIPdndAsyncException.invalidRequest("The asynchronous interaction phase '"+fase+"' requires the interaction identifier (Conversation-ID)");
		}
		return id;
	}
	
	private static boolean isModalitaQuery(String modalita) {
		return ModICostanti.MODIPA_PDND_ASYNC_VALUE_MODALITA_QUERY.equals(modalita);
	}
		private int readEntityNumber(List<ProtocolProperty> fruizioneProperties) throws ModIPdndAsyncException {
		String modalita = getStringValue(fruizioneProperties, ModICostanti.MODIPA_PDND_ASYNC_ENTITY_NUMBER_MODALITA);
		String nome = getStringValue(fruizioneProperties, ModICostanti.MODIPA_PDND_ASYNC_ENTITY_NUMBER_NOME);
		if(nome==null) {
			nome = isModalitaQuery(modalita) ? ModICostanti.MODIPA_PDND_ASYNC_ENTITY_NUMBER_NOME_QUERY_DEFAULT : ModICostanti.MODIPA_PDND_ASYNC_ENTITY_NUMBER_NOME_DEFAULT;
		}
		String value = readValoreClient(modalita, nome);
		if(value==null || "".equals(value.trim())) {
			throw ModIPdndAsyncException.invalidRequest("Entity number not provided ("+getRiferimentoClient(modalita, nome)+")");
		}
		int n;
		try {
			n = Integer.parseInt(value.trim());
		}catch(NumberFormatException e) {
			throw ModIPdndAsyncException.invalidRequest("Entity number '"+value+"' provided ("+getRiferimentoClient(modalita, nome)+") is not valid: value must be an integer");
		}
		if(n<=0) {
			throw ModIPdndAsyncException.invalidRequest("Entity number '"+value+"' provided ("+getRiferimentoClient(modalita, nome)+") is not valid: value must be greater than zero");
		}
		return n;
	}
	
	private String readValoreClient(String modalita, String nome) {
		if(nome==null || this.requestInfo==null || this.requestInfo.getProtocolContext()==null) {
			return null;
		}
		TransportRequestContext trc = this.requestInfo.getProtocolContext();
		if(ModICostanti.MODIPA_PDND_ASYNC_VALUE_MODALITA_QUERY.equals(modalita)) {
			return trc.getParameterFirstValue(nome);
		}
		return trc.getHeaderFirstValue(nome);
	}
	private static String getRiferimentoClient(String modalita, String nome) {
		return getDescrizioneModalita(modalita)+" '"+nome+"'";
	}
	private static ModIPdndAsyncException interazioneNonTrovata(String interactionId) {
		return ModIPdndAsyncException.notFound(PREFIX_INTERACTION+interactionId+"' not found");
	}
	private static String claimNonTrovato(String claim) {
		return CLAIM_PREFIX+claim+"' not found in the PDND voucher";
	}
		private static String getDescrizioneModalita(String modalita) {
		return ModICostanti.MODIPA_PDND_ASYNC_VALUE_MODALITA_QUERY.equals(modalita) ? "query parameter" : "HTTP header";
	}
	
	private boolean isPurposeIdAbilitato(List<ProtocolProperty> fruizioneProperties) {
		String v = getStringValue(fruizioneProperties, ModICostanti.MODIPA_PDND_ASYNC_PURPOSE_ID);
		if(ModICostanti.MODIPA_PDND_ASYNC_VALUE_ABILITATO.equals(v)) {
			return true;
		}
		if(ModICostanti.MODIPA_PDND_ASYNC_VALUE_DISABILITATO.equals(v)) {
			return false;
		}
		return this.modiProperties.isPdndAsyncPurposeIdGetResourceConfirmation();
	}
	
	private InterazioneAsincronaPDND getInterazione(String interactionId, TipoPdD ruolo) throws ProtocolException {
		try {
			return this.gestore.get(interactionId, ruolo);
		}catch(InterazioniAsincronePDNDException e) {
			throw new ProtocolException("Reading of the asynchronous interaction '"+interactionId+FAILED+e.getMessage(), e);
		}
	}
	
	private static List<IDAccordo> findApiCallback(IRegistryReader registryReader, IDAccordo idApiEService) throws ProtocolException {
		try {
			ProtocolFiltroRicercaAccordi filtro = new ProtocolFiltroRicercaAccordi();
			org.openspcoop2.protocol.sdk.properties.ProtocolProperties pp = new org.openspcoop2.protocol.sdk.properties.ProtocolProperties();
			pp.addProperty(ModICostanti.MODIPA_PDND_ASYNC_API_CORRELATA, IDAccordoFactory.getInstance().getUriFromIDAccordo(idApiEService));
			filtro.setProtocolProperties(pp);
			return registryReader.findIdAccordiServizioParteComune(filtro);
		}catch(RegistryNotFound notFound) {
			return new ArrayList<>();
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	private static List<IDServizio> findIdServizi(IRegistryReader registryReader, ProtocolFiltroRicercaServizi filtro) throws ProtocolException {
		try {
			return registryReader.findIdAccordiServizioParteSpecifica(filtro);
		}catch(RegistryNotFound notFound) {
			return new ArrayList<>();
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	private static boolean isApiCorrelata(IRegistryReader registryReader, IDServizio idServizioEService, String uriApiEService) throws ProtocolException {
		if(uriApiEService==null) {
			return false;
		}
		try {
			AccordoServizioParteSpecifica asps = registryReader.getAccordoServizioParteSpecifica(idServizioEService);
			return uriApiEService.equals(asps.getAccordoServizioParteComune());
		}catch(RegistryNotFound notFound) {
			return false;
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	private static boolean isStessoServizio(IDServizio a, IDServizio b) {
		return a!=null && b!=null &&
				a.getSoggettoErogatore()!=null && a.getSoggettoErogatore().equals(b.getSoggettoErogatore()) &&
				a.getTipo()!=null && a.getTipo().equals(b.getTipo()) &&
				a.getNome()!=null && a.getNome().equals(b.getNome()) &&
				a.getVersione()!=null && a.getVersione().equals(b.getVersione());
	}
	
	private static IDServizio getIdServizio(Busta busta) throws ProtocolException {
		try {
			return IDServizioFactory.getInstance().getIDServizioFromValues(busta.getTipoServizio(), busta.getServizio(), 
					busta.getTipoDestinatario(), busta.getDestinatario(), busta.getVersioneServizio());
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	private static IDServizio getIdServizioRisposta(Busta bustaRisposta) throws ProtocolException {
		// nella busta di risposta l'erogatore del servizio è il mittente
		try {
			return IDServizioFactory.getInstance().getIDServizioFromValues(bustaRisposta.getTipoServizio(), bustaRisposta.getServizio(), 
					bustaRisposta.getTipoMittente(), bustaRisposta.getMittente(), bustaRisposta.getVersioneServizio());
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	private static List<ProtocolProperty> getFruizioneProperties(AccordoServizioParteSpecifica asps, IDSoggetto fruitore) {
		for (Fruitore f : asps.getFruitoreList()) {
			if(fruitore.getTipo()!=null && fruitore.getTipo().equals(f.getTipo()) && fruitore.getNome()!=null && fruitore.getNome().equals(f.getNome())) {
				return f.getProtocolPropertyList();
			}
		}
		return new ArrayList<>();
	}
	
	private static void setConfig(InterazioneAsincronaPDND interazione, ModIPdndAsyncApiConfig config) {
		interazione.setTempoMaxCallback(config.getTempoMaxCallback());
		interazione.setTempoDisponibilita(config.getTempoDisponibilita());
		interazione.setConfermaRichiesta(config.isConfermaRicezione());
		interazione.setLimiteEntita(config.getLimiteEntita()!=null ? config.getLimiteEntita().intValue() : null);
	}
	
	private static Date addSeconds(Date d, Long seconds) {
		if(seconds==null || seconds.longValue()<=0) {
			return null;
		}
		return new Date(d.getTime() + seconds.longValue()*1000L);
	}
	
	/**
	 * La fase viene considerata completata se il codice HTTP della risposta è tra quelli configurati nella fruizione/erogazione
	 * o, in assenza di ridefinizione, nella proprietà 'org.openspcoop2.protocol.modipa.pdnd.async.httpStatus.successo' (default 200-299)
	 */
	private boolean isSuccesso(OpenSPCoop2Message msg) {
		try {
			if(msg!=null && msg.getTransportResponseContext()!=null && msg.getTransportResponseContext().getCodiceTrasporto()!=null) {
				int code = Integer.parseInt(msg.getTransportResponseContext().getCodiceTrasporto());
				String codici = (String) this.context.getObject(CONTEXT_HTTP_STATUS_SUCCESSO);
				if(codici==null) {
					codici = this.modiProperties.getPdndAsyncHttpStatusSuccesso();
				}
				return ModIPdndAsyncUtils.isCodiceHttpSuccesso(code, codici);
			}
		}catch(Exception e) {
			this.log.error("Verifica del codice HTTP della risposta fallita: "+e.getMessage(), e);
		}
		return false;
	}
	private String readHttpStatusSuccesso(List<ProtocolProperty> properties) throws ProtocolException {
		String mode = getStringValue(properties, ModICostanti.MODIPA_PDND_ASYNC_HTTP_STATUS_MODE);
		String codici = getStringValue(properties, ModICostanti.MODIPA_PDND_ASYNC_HTTP_STATUS);
		if(ModICostanti.MODIPA_PDND_ASYNC_VALUE_RIDEFINITO.equals(mode) && codici!=null && !"".equals(codici.trim())) {
			return codici.trim();
		}
		return this.modiProperties.getPdndAsyncHttpStatusSuccesso();
	}
	
	private static String getClaim(InformazioniToken informazioniToken, String claim) {
		if(informazioniToken.getClaims()==null) {
			return null;
		}
		Object o = informazioniToken.getClaims().get(claim);
		if(o==null || "".equals(o.toString())) {
			return null;
		}
		return o.toString();
	}
	
	private static String getStringValue(List<ProtocolProperty> list, String name) {
		try {
			return ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(list, name);
		}catch(Exception e) {
			return null;
		}
	}
	private static boolean isTrue(List<ProtocolProperty> list, String name) {
		try {
			BooleanNullable b = ProtocolPropertiesUtils.getOptionalBooleanValuePropertyRegistry(list, name);
			return b!=null && b.getValue()!=null && b.getValue().booleanValue();
		}catch(Exception e) {
			return false;
		}
	}
}
