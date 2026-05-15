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

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.llm.CanonicalChatRequest;
import org.openspcoop2.message.llm.transform.LLMDialect;
import org.openspcoop2.message.llm.transform.LLMInboundRequestTransformer;
import org.openspcoop2.message.llm.transform.LLMTransformerRegistry;
import org.openspcoop2.pdd.core.ProtocolContext;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.InRequestProtocolContext;
import org.openspcoop2.pdd.core.handlers.InRequestProtocolHandler;
import org.openspcoop2.protocol.registry.RegistroServiziManager;

/**
 * Primo handler della pipeline LLM. Implementato come {@link InRequestProtocolHandler}
 * (non InRequestHandler) perché necessita che la risoluzione protocollare sia
 * già avvenuta — in particolare deve conoscere l'{@link IDAccordo} per leggere
 * {@code FormatoSpecifica} dell'API.
 * <p>
 * Catena di esecuzione (per ogni transazione):
 * </p>
 * <ol>
 *   <li>recupera {@code IDAccordo} dal {@link ProtocolContext}</li>
 *   <li>risolve {@link AccordoServizioParteComune} dal registro</li>
 *   <li>controlla {@code FormatoSpecifica.isLLM()}: se no, no-op</li>
 *   <li>mappa {@code FormatoSpecifica} → {@link LLMDialect} (chiave interna al modulo message)</li>
 *   <li>popola il PdDContext con {@code LLM_FORMATO} e {@code LLM_PROVIDER}
 *       (gli altri 3 handler LLM si attivano leggendo queste chiavi)</li>
 *   <li>legge il body bytes della richiesta</li>
 *   <li>applica l'inbound transformer del dialetto front-door</li>
 *   <li>sostituisce il messaggio con un {@link OpenSPCoop2LLMCanonicalMessage}
 *       che ospita un {@link CanonicalChatRequest}</li>
 * </ol>
 * <p>
 * Vivere come {@code InRequestProtocolHandler} permette di usare lo stesso handler
 * per fruizioni (TipoPdD.DELEGATA, gestite da RicezioneContenutiApplicativi) e
 * erogazioni (TipoPdD.APPLICATIVA, gestite da RicezioneBuste) senza modifiche
 * a quei due punti d'ingresso.
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class LLMInboundRequestHandler implements InRequestProtocolHandler {

	@Override
	public void invoke(InRequestProtocolContext context) throws HandlerException {
		org.slf4j.Logger log = context.getLogCore();
		AccordoServizioParteComune apc = resolveAccordo(context);
		if (apc == null) {
			if (log != null && log.isDebugEnabled()) {
				log.debug("LLMInboundRequestHandler: apc null, no-op (idAccordo non risolto dal ProtocolContext)");
			}
			return;
		}
		FormatoSpecifica fs = apc.getFormatoSpecifica();
		if (log != null && log.isDebugEnabled()) {
			log.debug("LLMInboundRequestHandler: apc risolto, formatoSpecifica=" + (fs != null ? fs.getValue() : "null") + ", isLLM=" + (fs != null && fs.isLLM()));
		}
		if (fs == null || !fs.isLLM()) {
			return;
		}
		LLMDialect dialect = LLMDialect.fromValue(fs.getValue());
		if (dialect == null) {
			if (log != null) {
				log.warn("LLMInboundRequestHandler: dialect non mappato per formatoSpecifica={}", fs.getValue());
			}
			return;
		}
		try {
			LLMHandlerSupport.populateLLMContext(context.getPddContext(), apc);
			transformRequest(context, dialect);
			if (log != null && log.isDebugEnabled()) {
				log.debug("LLMInboundRequestHandler: trasformazione completata (dialetto={})", dialect.getValue());
			}
		} catch (HandlerException e) {
			throw e;
		} catch (Exception e) {
			throw new HandlerException("LLMInboundRequestHandler: errore nella trasformazione client → canonical (dialetto=" + dialect.getValue() + "): " + e.getMessage(), e);
		}
	}

	/**
	 * Risolve l'AccordoServizioParteComune dal context. Ritorna null se l'API in
	 * lavorazione non è risolvibile (es. errore protocollo, richiesta non instradata):
	 * in tal caso l'handler resta no-op per non interferire con il flusso normale.
	 */
	private AccordoServizioParteComune resolveAccordo(InRequestProtocolContext context) {
		ProtocolContext pc = context != null ? context.getProtocollo() : null;
		if (pc == null) {
			return null;
		}
		try {
			RegistroServiziManager mgr = RegistroServiziManager.getInstance();
			IDAccordo idAccordo = pc.getIdAccordo();
			if (idAccordo == null) {
				idAccordo = resolveIdAccordoFromServizio(pc, mgr);
			}
			if (idAccordo == null) {
				return null;
			}
			return mgr.getAccordoServizioParteComune(idAccordo, null, false, false, null);
		} catch (Exception e) {
			// API non risolvibile sul registro: la pipeline LLM non si attiva,
			// gli altri error path di GovWay gestiranno il caso.
			return null;
		}
	}

	private IDAccordo resolveIdAccordoFromServizio(ProtocolContext pc, RegistroServiziManager mgr) throws Exception {
		IDSoggetto erogatore = pc.getErogatore();
		if (erogatore == null || pc.getTipoServizio() == null || pc.getServizio() == null || pc.getVersioneServizio() == null) {
			return null;
		}
		IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(
				pc.getTipoServizio(), pc.getServizio(), erogatore, pc.getVersioneServizio());
		AccordoServizioParteSpecifica asps = mgr.getAccordoServizioParteSpecifica(idServizio, null, false, null);
		if (asps == null || asps.getAccordoServizioParteComune() == null) {
			return null;
		}
		return IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
	}

	private void transformRequest(InRequestProtocolContext context, LLMDialect dialect) throws Exception {
		byte[] body = LLMHandlerSupport.readBody(context.getMessaggio(), null, LLMHandlerSupport.getIdTransazione(context));
		LLMInboundRequestTransformer transformer = LLMTransformerRegistry.getInboundRequestTransformer(dialect);
		CanonicalChatRequest canonical = transformer.transform(body);
		// Lo stream flag del canonical lo abbiamo già: i transformer inbound front-door
		// (OpenAI + Anthropic) leggono body.stream e lo settano sul canonical. Lo propaghiamo
		// nel PdDContext per gli handler downstream — il canonical poi finirà serializzato
		// nella request provider con lo stesso valore. Lo step di gestione SSE end-to-end
		// (chunk transformer + hook NIO) è ancora da implementare: per ora se stream:true
		// la response provider sarà SSE e ci aspettiamo che l'InResponseHandler gestisca
		// (o segnali) il caso.
		boolean stream = Boolean.TRUE.equals(canonical.getStream());
		context.getPddContext().addObject(LLMHandlerConstants.PDD_CTX_LLM_STREAM, Boolean.valueOf(stream));
		// Il canonical viaggia nel PdDContext: non sostituiamo il messaggio nel context
		// (altrimenti step downstream come la validazione contenuti lo rimpiazzerebbero
		// con un OpenSPCoop2Message_json_impl, perdendo il canonical).
		context.getPddContext().addObject(LLMHandlerConstants.PDD_CTX_LLM_CANONICAL_REQUEST, canonical);
	}
}
