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
package org.openspcoop2.pdd.core.llm.routing;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.message.llm.CanonicalChatRequest;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.handlers.llm.LLMHandlerConstants;
import org.openspcoop2.pdd.core.handlers.llm.LLMHandlerSupport;
import org.openspcoop2.utils.UtilsRuntimeException;

/**
 * Risolve il connettore effettivo da utilizzare per una richiesta LLM.
 *
 * <p>L'erogazione/fruizione configurata in console punta sempre a un "container"
 * di tipo {@link TipiConnettore#DISABILITATO disabilitato} (popolato dalla console
 * tramite {@code wrapAsLlmContainer}); il container porta nel suo
 * {@code connettoreLlm.providerList} l'elenco dei provider concreti supportati,
 * ognuno con i propri {@code property} (campi tecnici) e {@code binding} (alias
 * di modello esposti al client).</p>
 *
 * <p>Il resolver viene invocato dal driver di configurazione subito dopo aver
 * caricato il bean {@code Connettore} dell'erogazione/fruizione: legge dal
 * {@code PdDContext} il {@code CanonicalChatRequest} popolato dall'inbound
 * handler LLM, individua il provider concreto il cui {@code binding} corrisponde
 * a {@code request.model} e ritorna un bean {@code Connettore} "concreto" che
 * sostituisce il container nel resto del flusso PdD. Tutta l'operazione e'
 * in-memory: i dati dei provider sono gia' nel bean (caricati dal driver via
 * {@code connettori_llm} / {@code connettori_llm_binding}), nessuna query a
 * runtime.</p>
 *
 * <p>Conserva anche nel {@code PdDContext} il nome del binding selezionato e
 * del provider concreto, cosi' che il {@code LLMOutboundRequestHandler} non
 * debba ripetere la lookup quando applica il transformer.</p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public final class LLMConnectorResolver {

	private LLMConnectorResolver() {
		// utility
	}

	/**
	 * Variante config namespace: vedi {@link #resolveForRegistry}.
	 */
	public static org.openspcoop2.core.config.Connettore resolveForConfig(
			org.openspcoop2.core.config.Connettore connettore, PdDContext pddContext) {
		if (!isLlmContainerConfig(connettore) || pddContext == null) {
			// no-op se non e' LLM oppure se siamo in un punto del flusso senza PdDContext
			// (es. lookup diagnostico/preview, ConnettoreStatus, getRegistroForImbustamento)
			return connettore;
		}
		String requestedModel = readRequestedModel(pddContext);
		// Indicizza i provider per binding
		List<org.openspcoop2.core.config.ConnettoreLlmProviderRef> matches = new ArrayList<>();
		for (org.openspcoop2.core.config.ConnettoreLlmProviderRef ref : connettore.getConnettoreLlm().getProviderList()) {
			if (ref.getBindingList() == null) {
				continue;
			}
			for (org.openspcoop2.core.config.ConnettoreLlmBinding b : ref.getBindingList()) {
				if (requestedModel.equals(b.getNome())) {
					matches.add(ref);
					break;
				}
			}
		}
		org.openspcoop2.core.config.ConnettoreLlmProviderRef selected = pickSingleMatchOrThrow(matches, requestedModel, connettore);

		// Costruisco il bean del concreto
		org.openspcoop2.core.config.Connettore concreto = new org.openspcoop2.core.config.Connettore();
		concreto.setNome(selected.getNome());
		concreto.setTipo(selected.getTipo());
		if (selected.getPropertyList() != null) {
			for (org.openspcoop2.core.config.Property p : selected.getPropertyList()) {
				concreto.addProperty(p);
			}
		}

		storeSelectionInContext(pddContext, requestedModel, selected.getPropertyList());
		return concreto;
	}

	/**
	 * Risolve il connettore di un container LLM verso il provider concreto
	 * compatibile con {@code request.model}.
	 *
	 * <p>No-op se il bean passato non e' un container LLM (tipo non-disabilitato
	 * oppure {@code connettoreLlm} vuoto): il connettore originale viene ritornato
	 * invariato per non interferire con i flussi non-LLM.</p>
	 *
	 * <p>Solleva {@link UtilsRuntimeException} se il {@code request.model} richiesto
	 * non corrisponde a nessun binding ("Modello LLM non disponibile") oppure se
	 * piu' provider espongono lo stesso binding (errore di configurazione,
	 * "Modello LLM ambiguo").</p>
	 */
	public static org.openspcoop2.core.registry.Connettore resolveForRegistry(
			org.openspcoop2.core.registry.Connettore connettore, PdDContext pddContext) {
		if (!isLlmContainerRegistry(connettore) || pddContext == null) {
			return connettore;
		}
		String requestedModel = readRequestedModel(pddContext);
		List<org.openspcoop2.core.registry.ConnettoreLlmProviderRef> matches = new ArrayList<>();
		for (org.openspcoop2.core.registry.ConnettoreLlmProviderRef ref : connettore.getConnettoreLlm().getProviderList()) {
			if (ref.getBindingList() == null) {
				continue;
			}
			for (org.openspcoop2.core.registry.ConnettoreLlmBinding b : ref.getBindingList()) {
				if (requestedModel.equals(b.getNome())) {
					matches.add(ref);
					break;
				}
			}
		}
		org.openspcoop2.core.registry.ConnettoreLlmProviderRef selected = pickSingleMatchOrThrowRegistry(matches, requestedModel, connettore);

		org.openspcoop2.core.registry.Connettore concreto = new org.openspcoop2.core.registry.Connettore();
		concreto.setNome(selected.getNome());
		concreto.setTipo(selected.getTipo());
		if (selected.getPropertyList() != null) {
			for (org.openspcoop2.core.registry.Property p : selected.getPropertyList()) {
				concreto.addProperty(p);
			}
		}

		storeSelectionInContextRegistry(pddContext, requestedModel, selected.getPropertyList());
		return concreto;
	}

	private static boolean isLlmContainerConfig(org.openspcoop2.core.config.Connettore c) {
		return c != null
				&& TipiConnettore.DISABILITATO.getNome().equals(c.getTipo())
				&& c.getConnettoreLlm() != null
				&& c.getConnettoreLlm().sizeProviderList() > 0;
	}

	private static boolean isLlmContainerRegistry(org.openspcoop2.core.registry.Connettore c) {
		return c != null
				&& TipiConnettore.DISABILITATO.getNome().equals(c.getTipo())
				&& c.getConnettoreLlm() != null
				&& c.getConnettoreLlm().sizeProviderList() > 0;
	}

	private static String readRequestedModel(PdDContext pddContext) {
		if (pddContext == null) {
			throw new UtilsRuntimeException("PdDContext non disponibile, impossibile risolvere il modello LLM richiesto");
		}
		Object o = pddContext.getObject(LLMHandlerConstants.PDD_CTX_LLM_CANONICAL_REQUEST);
		if (!(o instanceof CanonicalChatRequest)) {
			throw new UtilsRuntimeException("Richiesta non riconosciuta come LLM: CanonicalChatRequest non popolato dal layer inbound");
		}
		String model = ((CanonicalChatRequest) o).getModel();
		if (model == null || model.isEmpty()) {
			throw new UtilsRuntimeException("Richiesta LLM senza 'model' specificato");
		}
		return model;
	}

	private static org.openspcoop2.core.config.ConnettoreLlmProviderRef pickSingleMatchOrThrow(
			List<org.openspcoop2.core.config.ConnettoreLlmProviderRef> matches,
			String requestedModel,
			org.openspcoop2.core.config.Connettore container) {
		if (matches.isEmpty()) {
			throw new UtilsRuntimeException("Modello LLM '" + requestedModel + "' non disponibile per questa API");
		}
		if (matches.size() > 1) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < matches.size(); i++) {
				if (i > 0) sb.append(", ");
				sb.append(matches.get(i).getNome());
			}
			throw new UtilsRuntimeException("Modello LLM '" + requestedModel + "' ambiguo: piu' provider lo espongono ["
					+ sb + "] su API '" + (container != null ? container.getNome() : "?") + "'");
		}
		return matches.get(0);
	}

	private static org.openspcoop2.core.registry.ConnettoreLlmProviderRef pickSingleMatchOrThrowRegistry(
			List<org.openspcoop2.core.registry.ConnettoreLlmProviderRef> matches,
			String requestedModel,
			org.openspcoop2.core.registry.Connettore container) {
		if (matches.isEmpty()) {
			throw new UtilsRuntimeException("Modello LLM '" + requestedModel + "' non disponibile per questa API");
		}
		if (matches.size() > 1) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < matches.size(); i++) {
				if (i > 0) sb.append(", ");
				sb.append(matches.get(i).getNome());
			}
			throw new UtilsRuntimeException("Modello LLM '" + requestedModel + "' ambiguo: piu' provider lo espongono ["
					+ sb + "] su API '" + (container != null ? container.getNome() : "?") + "'");
		}
		return matches.get(0);
	}

	private static void storeSelectionInContext(PdDContext pddContext, String requestedModel,
			List<org.openspcoop2.core.config.Property> propertyList) {
		if (pddContext == null) {
			return;
		}
		LLMHandlerSupport.setLLMProviderBindingName(pddContext, requestedModel);
		String providerName = findPropertyValueConfig(propertyList, org.openspcoop2.core.constants.CostantiConnettori.CONNETTORE_LLM_POLICY);
		if (providerName != null) {
			LLMHandlerSupport.setLLMProviderName(pddContext, providerName);
		}
	}

	private static void storeSelectionInContextRegistry(PdDContext pddContext, String requestedModel,
			List<org.openspcoop2.core.registry.Property> propertyList) {
		if (pddContext == null) {
			return;
		}
		LLMHandlerSupport.setLLMProviderBindingName(pddContext, requestedModel);
		String providerName = findPropertyValueRegistry(propertyList, org.openspcoop2.core.constants.CostantiConnettori.CONNETTORE_LLM_POLICY);
		if (providerName != null) {
			LLMHandlerSupport.setLLMProviderName(pddContext, providerName);
		}
	}

	private static String findPropertyValueConfig(List<org.openspcoop2.core.config.Property> props, String name) {
		if (props == null || name == null) return null;
		for (org.openspcoop2.core.config.Property p : props) {
			if (name.equals(p.getNome())) {
				return p.getValore();
			}
		}
		return null;
	}

	private static String findPropertyValueRegistry(List<org.openspcoop2.core.registry.Property> props, String name) {
		if (props == null || name == null) return null;
		for (org.openspcoop2.core.registry.Property p : props) {
			if (name.equals(p.getNome())) {
				return p.getValore();
			}
		}
		return null;
	}
}
