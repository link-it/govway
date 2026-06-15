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
package org.openspcoop2.pdd.core.handlers.llm;

import java.util.List;

import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.llm.CanonicalChatRequest;
import org.openspcoop2.message.llm.transform.LLMDialect;
import org.openspcoop2.message.llm.transform.LLMOutboundProviderRequestTransformer;
import org.openspcoop2.message.llm.transform.LLMProviderRequest;
import org.openspcoop2.message.llm.transform.LLMTransformerRegistry;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreUscita;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.OutRequestContext;
import org.openspcoop2.pdd.core.handlers.OutRequestHandler;
import org.openspcoop2.utils.transport.TransportRequestContext;

/**
 * Secondo handler della pipeline LLM (catena OutRequest, in testa, prima del connettore).
 * <ul>
 *   <li>risolve il providerId back-end leggendo la LLM Provider Policy associata al
 *       connettore (property {@code llmPolicy} → lookup in {@code generic_properties} →
 *       valore della property {@code llmProvider.type}) e lo salva nel PdDContext per
 *       i successivi handler della pipeline</li>
 *   <li>recupera il messaggio canonical dal context</li>
 *   <li>applica l'outbound provider transformer (canonical → JSON nativo del provider + header)</li>
 *   <li>aggiorna in-place il body del messaggio in modo che il connettore HC5 standard lo
 *       invii senza conoscere il dominio LLM</li>
 *   <li>aggiunge gli header HTTP richiesti dal provider (es. {@code anthropic-version})
 *       via {@code forceTransportHeader}</li>
 *   <li>imposta {@code functionParameters} con il resource path nativo del provider
 *       (es. {@code /messages} o {@code /chat/completions})</li>
 * </ul>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class LLMOutboundRequestHandler implements OutRequestHandler {

	@Override
	public void invoke(OutRequestContext context) throws HandlerException {
		org.slf4j.Logger log = context.getLogCore();
		LLMDialect dialect = LLMHandlerSupport.getLLMFormato(context);
		if (dialect == null) {
			if (log != null && log.isDebugEnabled()) {
				log.debug("LLMOutboundRequestHandler: PdDContext non marcato LLM, no-op");
			}
			return;
		}
		if (log != null && log.isDebugEnabled()) {
			log.debug("LLMOutboundRequestHandler: invocato con dialetto={}", dialect.getValue());
		}
		String providerId = resolveAndStoreProviderId(context, log);
		try {
			CanonicalChatRequest canonical = extractCanonical(context);
			// Sostituiamo il binding name (esposto dall'API gateway) con il vendor model id richiesto dal
			// backend. Il binding name resta visibile sul PdDContext per la transazione/audit, ma il body
			// inviato al provider deve trasportare l'identificativo nativo (es. 'claude-haiku-4-5' per
			// Anthropic). Se il binding non valorizza vendor.model.id lasciamo il valore canonical
			// originale (back-compat con configurazioni dove il binding name coincide con l'id provider).
			String vendorModelId = LLMHandlerSupport.getLLMVendorModelId(context);
			if (vendorModelId != null && !vendorModelId.isEmpty()) {
				canonical.setModel(vendorModelId);
			}
			LLMOutboundProviderRequestTransformer transformer = LLMTransformerRegistry.getOutboundProviderRequestTransformer(providerId);
			LLMProviderRequest providerRequest = transformer.transform(canonical);
			// Aggiorniamo il contenuto del messaggio esistente in-place: il ConnettoreMsg ha
			// già il riferimento all'oggetto (settato prima dell'invocazione degli handler in
			// ConsegnaContenutiApplicativi:2101), quindi sostituirlo con context.setMessaggio()
			// non avrebbe effetto sul body veicolato al backend.
			LLMHandlerSupport.applyJsonBody(context.getMessaggio(), providerRequest.getBody(), providerRequest.getHeaders());
			// Re-routing del path verso l'endpoint nativo del provider: il connettore tiene
			// solo la base URL (es. https://api.anthropic.com/v1) e il transformer outbound
			// decide il path-risorsa (es. /messages per chat). La URL finale viene poi
			// costruita dal connettore via RestUtilities.buildUrl come base + functionParameters.
			applyProviderResourcePath(context.getMessaggio(), transformer, canonical, log);
		} catch (HandlerException e) {
			throw e;
		} catch (Exception e) {
			throw new HandlerException("LLMOutboundRequestHandler: errore nella trasformazione canonical → " + providerId + ": " + e.getMessage(), e);
		}
	}

	private String resolveAndStoreProviderId(OutRequestContext context, org.slf4j.Logger log) throws HandlerException {
		// I nomi del binding selezionato (= request.model) e del provider concreto sono gia'
		// stati popolati nel PdDContext dal LLMConnectorResolver, eseguito al momento del load
		// del connettore di uscita (vedi ConfigurazionePdDReader.getInvocazioneServizio/
		// getForwardRoute). Qui ci limitiamo a leggerli e ad arricchire il PdDContext con i
		// metadati (vendorModelId, pricing, dialect) richiesti dai transformer e dal salvataggio
		// transazione, sfruttando la cache request-scoped di ConfigurazionePdDManager.
		org.openspcoop2.pdd.core.PdDContext pdd = context.getPddContext();
		String bindingName = LLMHandlerSupport.getLLMProviderBindingName(pdd);
		if (bindingName == null || bindingName.isEmpty()) {
			throw new HandlerException("LLMOutboundRequestHandler: nome del LLM Provider Binding non risolto dal LLMConnectorResolver (PdDContext senso del modello)");
		}
		String providerName = LLMHandlerSupport.getLLMProviderName(pdd);
		if (providerName == null || providerName.isEmpty()) {
			throw new HandlerException("LLMOutboundRequestHandler: nome del LLM Provider non risolto dal LLMConnectorResolver (PdDContext senso del provider)");
		}

		ConfigurazionePdDManager mgr = ConfigurazionePdDManager.getInstance(context.getStato());
		org.openspcoop2.protocol.sdk.state.RequestInfo requestInfo = extractRequestInfo(context);

		// 1) lookup del binding -> model name + vendor model id (+ pricing)
		GenericProperties binding = lookupBinding(mgr, requestInfo, bindingName);
		String modelName    = findPropertyValue(binding.getPropertyList(), org.openspcoop2.pdd.core.llm.provider.Costanti.LLM_PROVIDER_BINDING_MODEL);
		String vendorModelId= findPropertyValue(binding.getPropertyList(), org.openspcoop2.pdd.core.llm.provider.Costanti.LLM_PROVIDER_BINDING_VENDOR_MODEL_ID);
		String priceInput   = findPropertyValue(binding.getPropertyList(), org.openspcoop2.pdd.core.llm.provider.Costanti.LLM_PROVIDER_BINDING_PRICE_INPUT);
		String priceOutput  = findPropertyValue(binding.getPropertyList(), org.openspcoop2.pdd.core.llm.provider.Costanti.LLM_PROVIDER_BINDING_PRICE_OUTPUT);
		String priceInputDivisor  = findPropertyValue(binding.getPropertyList(), org.openspcoop2.pdd.core.llm.provider.Costanti.LLM_PROVIDER_BINDING_PRICE_INPUT_DIVISOR);
		String priceOutputDivisor = findPropertyValue(binding.getPropertyList(), org.openspcoop2.pdd.core.llm.provider.Costanti.LLM_PROVIDER_BINDING_PRICE_OUTPUT_DIVISOR);

		// 2) lookup del provider -> tipo (dialect: anthropic/openai/awsBedrock)
		GenericProperties provider = lookupProvider(mgr, requestInfo, providerName);
		String providerType = findPropertyValue(provider.getPropertyList(), org.openspcoop2.pdd.core.llm.provider.Costanti.LLM_PROVIDER_TYPE);
		if (providerType == null || providerType.isEmpty()) {
			throw new HandlerException("LLMOutboundRequestHandler: LLM Provider '" + providerName
					+ "' non valorizza la property "
					+ org.openspcoop2.pdd.core.llm.provider.Costanti.LLM_PROVIDER_TYPE);
		}

		// 3) propaga nel PdDContext i metadati per transformer e finalizzazione transazione
		LLMHandlerSupport.setLLMProvider(pdd, providerType);
		LLMHandlerSupport.setLLMModelName(pdd, modelName);
		LLMHandlerSupport.setLLMVendorModelId(pdd, vendorModelId);
		LLMHandlerSupport.setLLMPriceInput(pdd, priceInput);
		LLMHandlerSupport.setLLMPriceOutput(pdd, priceOutput);
		LLMHandlerSupport.setLLMPriceInputDivisor(pdd, priceInputDivisor);
		LLMHandlerSupport.setLLMPriceOutputDivisor(pdd, priceOutputDivisor);

		if (log != null && log.isDebugEnabled()) {
			log.debug("LLMOutboundRequestHandler: binding '{}' -> provider '{}' ({}), model '{}', vendorModelId '{}'",
					bindingName, providerName, providerType, modelName, vendorModelId);
		}
		return providerType;
	}

	private org.openspcoop2.protocol.sdk.state.RequestInfo extractRequestInfo(OutRequestContext context) {
		if (context.getPddContext() == null) return null;
		Object o = context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		return o instanceof org.openspcoop2.protocol.sdk.state.RequestInfo ? (org.openspcoop2.protocol.sdk.state.RequestInfo) o : null;
	}

	private GenericProperties lookupBinding(ConfigurazionePdDManager mgr, org.openspcoop2.protocol.sdk.state.RequestInfo requestInfo, String bindingName) throws HandlerException {
		try {
			GenericProperties gp = mgr.getPolicyLLMProviderBinding(false, bindingName, requestInfo);
			if (gp == null) {
				throw new HandlerException("LLMOutboundRequestHandler: LLM Provider Binding '" + bindingName + "' non trovato");
			}
			return gp;
		} catch (HandlerException e) {
			throw e;
		} catch (Exception e) {
			throw new HandlerException("LLMOutboundRequestHandler: errore nella lookup del LLM Provider Binding '" + bindingName + "': " + e.getMessage(), e);
		}
	}

	private GenericProperties lookupProvider(ConfigurazionePdDManager mgr, org.openspcoop2.protocol.sdk.state.RequestInfo requestInfo, String providerName) throws HandlerException {
		try {
			GenericProperties gp = mgr.getPolicyLLMProvider(false, providerName, requestInfo);
			if (gp == null) {
				throw new HandlerException("LLMOutboundRequestHandler: LLM Provider '" + providerName + "' non trovato");
			}
			return gp;
		} catch (HandlerException e) {
			throw e;
		} catch (Exception e) {
			throw new HandlerException("LLMOutboundRequestHandler: errore nella lookup del LLM Provider '" + providerName + "': " + e.getMessage(), e);
		}
	}

	private String findPropertyValue(List<Property> props, String name) {
		if (props == null || name == null) {
			return null;
		}
		for (Property p : props) {
			if (name.equals(p.getNome())) {
				return p.getValore();
			}
		}
		return null;
	}

	private void applyProviderResourcePath(OpenSPCoop2Message msg, LLMOutboundProviderRequestTransformer transformer, CanonicalChatRequest canonical, org.slf4j.Logger log) {
		String resourcePath = transformer.getProviderResourcePath(canonical);
		if (resourcePath == null || msg == null) {
			return;
		}
		TransportRequestContext ctx = msg.getTransportRequestContext();
		if (ctx == null) {
			if (log != null) {
				log.warn("LLMOutboundRequestHandler: TransportRequestContext null, impossibile applicare resourcePath={}", resourcePath);
			}
			return;
		}
		ctx.setFunctionParameters(resourcePath);
		if (log != null && log.isDebugEnabled()) {
			log.debug("LLMOutboundRequestHandler: functionParameters sovrascritto -> {}", resourcePath);
		}
	}

	private CanonicalChatRequest extractCanonical(OutRequestContext context) throws HandlerException {
		Object o = context.getPddContext() != null
				? context.getPddContext().getObject(LLMHandlerConstants.PDD_CTX_LLM_CANONICAL_REQUEST)
				: null;
		if (o instanceof CanonicalChatRequest) {
			return (CanonicalChatRequest) o;
		}
		throw new HandlerException("LLMOutboundRequestHandler: CanonicalChatRequest assente nel PdDContext (l'InRequestProtocolHandler LLM non ha popolato il context?)");
	}
}
