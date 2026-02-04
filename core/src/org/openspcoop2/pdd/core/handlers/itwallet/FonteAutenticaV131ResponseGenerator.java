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
package org.openspcoop2.pdd.core.handlers.itwallet;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.message.ForcedResponseMessage;
import org.openspcoop2.message.OpenSPCoop2RestJsonMessage;
import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.OutResponseContext;
import org.openspcoop2.pdd.core.handlers.OutResponseHandler;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.core.transazioni.TransactionNotExistsException;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.utils.MapKey;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * FonteAutenticaV131ResponseGenerator
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FonteAutenticaV131ResponseGenerator implements OutResponseHandler {

	private enum FonteAutenticaError {
		SERVER_ERROR(500),
		INVALID_REQUEST(400), 
		INVALID_DPOP_PROOF(400),
		NOT_FOUND(404),
		TEMPORARY_UNAVAILABLE(503),
		INVALID_TOKEN(401);
		
		private Integer code;
		
		private FonteAutenticaError(Integer code) {
			this.code = code;
		}
	}
	
	private static final String ERROR_INVALID_TOKEN = "Token not valid";
	private static final String ERROR_NOT_FOUND_HASH = "Message hash not found";
	private static final String ERROR_NOT_PRESENT_TOKEN = "Token not present";
	private static final String ERROR_MALFORMED_TOKEN = "Token malformed";
	private static final String ERROR_NOT_FOUND = "Resource not found";
	private static final String ERROR_PROCESSING = "Processing error";
	private static final String ERROR_BAD_REQUEST = "Invalid input";
	private static final String ERROR_NOT_FOUND_AGID_JWT = "AgID-JWT-Signature token not found";
	private static final String ERROR_MALFORMED_AGID_JWT = "AgID-JWT-Signature token malformed";
	
	@Override
	public void invoke(OutResponseContext context) throws HandlerException {
		if(context!=null && context.getMessaggio()!=null 
				&& MessageType.JSON.equals(context.getMessaggio().getMessageType()) 
				&& MessageRole.FAULT.equals(context.getMessaggio().getMessageRole())) {
			
			try {
				parseError(context);
			} catch (MessageException | ProtocolException e) {
				context.getLogCore().error("Conversione non riuscita: "+e.getMessage(),e);
			}
			
		}
	}
	
	private String getTransactionId(OutResponseContext context) throws HandlerException {
		String id = null;
		if(context.getPddContext()!=null){
			PdDContext pddContext = context.getPddContext();
			if (pddContext.getObject(Costanti.ID_TRANSAZIONE)==null)
				throw new HandlerException("Identificativo della transazione assente");
			id = (String) pddContext.getObject(Costanti.ID_TRANSAZIONE);
		}
		
		if (id == null)
			throw new HandlerException("Identificativo della transazione assente");
		
		return id;
	}
	
	private Pair<FonteAutenticaError, String> parseValidazioneRichiesta(String idTransazione) {
		if(isErroreValidazioneRichiestaHashNotFound(idTransazione)) {
			return Pair.of(FonteAutenticaError.INVALID_REQUEST, ERROR_NOT_FOUND_HASH);
		} else {
			return Pair.of(FonteAutenticaError.INVALID_REQUEST, ERROR_BAD_REQUEST);
		}
	}
	
	private Optional<Eccezione> getEccezioneWithCodiceErrore(Transaction transaction) {
		return transaction.getTracciaRisposta().getBusta().getListaEccezioni().stream()
		.filter(e -> e.getCodiceEccezione() != null)
		.findAny();
	}
	
	private Pair<FonteAutenticaError, String> parsePddContext(OutResponseContext context, String idTransazione) throws ProtocolException {
		Transaction transaction = getTransaction(idTransazione);

		if(transaction==null 
				|| transaction.getTracciaRisposta() == null 
				|| transaction.getTracciaRisposta().getBusta() == null
				|| transaction.getTracciaRisposta().getBusta().getListaEccezioni() == null
				|| transaction.getTracciaRisposta().getBusta().getListaEccezioni().isEmpty())
			return null;
		
		Optional<Eccezione> eccezioneOpt = getEccezioneWithCodiceErrore(transaction);
		if (eccezioneOpt.isEmpty())
			return null;
		Eccezione eccezione = eccezioneOpt.get();
			
		if(eccezione.getCodiceEccezione().equals(CodiceErroreCooperazione.SICUREZZA_TOKEN_NON_PRESENTE.getCodice())) {
			String desc = eccezione.getDescrizione(context.getProtocolFactory());
			if(desc!=null && "Header HTTP 'Agid-JWT-Signature' non presente".equals(desc)) {
				return Pair.of(FonteAutenticaError.INVALID_TOKEN, ERROR_NOT_FOUND_AGID_JWT); // casoAgitJWTSignatureNonPresente
			}
		} else if(eccezione.getCodiceEccezione().equals(CodiceErroreCooperazione.SICUREZZA_FIRMA_NON_VALIDA.getCodice())
				|| CodiceErroreCooperazione.isEccezioneServizioApplicativoErogatore(eccezione.getCodiceEccezione()) // caso audience non valido
				|| ( CodiceErroreCooperazione.isEccezioneSicurezza(eccezione.getCodiceEccezione()) 
						&& !CodiceErroreCooperazione.isEccezioneSicurezzaToken(eccezione.getCodiceEccezione()))) {
			String desc = eccezione.getDescrizione(context.getProtocolFactory());
			if(desc!=null && desc.contains("Header 'Authorization'")) {
				return Pair.of(FonteAutenticaError.INVALID_TOKEN, ERROR_INVALID_TOKEN); // casoVoucherPDNDNonValido
			} else {
				return Pair.of(FonteAutenticaError.INVALID_TOKEN, ERROR_MALFORMED_AGID_JWT); // casoAgitJWTSignatureNonValido
			}
		} else if(CodiceErroreCooperazione.isEccezioneSicurezzaToken(eccezione.getCodiceEccezione())){ // caso audience non valido per voucher PDND
			return Pair.of(FonteAutenticaError.INVALID_TOKEN, ERROR_INVALID_TOKEN); // casoVoucherPDNDNonValido
		}
		
		return null;
	}
	
	private Pair<FonteAutenticaError, String> parseCode(OutResponseContext context) {
		if (context.getMessaggio().getTransportResponseContext() != null) {
			int code = NumberUtils.toInt(context.getMessaggio().getTransportResponseContext().getCodiceTrasporto(), 500);
			
			if (code == 404) {
				return Pair.of(FonteAutenticaError.NOT_FOUND, ERROR_NOT_FOUND);
			} else if (code == 401 || code == 403) {
				return Pair.of(FonteAutenticaError.INVALID_TOKEN, ERROR_INVALID_TOKEN);
			} else if (code >= 400 && code < 500) {
				return Pair.of(FonteAutenticaError.INVALID_REQUEST, ERROR_BAD_REQUEST);
			}
		}
		return null;
	}
	
	private void parseError(OutResponseContext context) throws MessageException, HandlerException, ProtocolException {
		Pair<FonteAutenticaError, String> error = Pair.of(FonteAutenticaError.TEMPORARY_UNAVAILABLE, "The API is temporary unavailable");
		String idTransazione = getTransactionId(context);
		
		if(isErroreValidazioneRichiesta(context)) {
			error = parseValidazioneRichiesta(idTransazione);
		}
		else if(isRequestReadTimeout(context) ||
				isContenutiRichiestaNonRiconosciuto(context) ||
				isErroreCorrelazioneApplicativaRisposta(context) ||
				isErroreTrasformazioneRichiesta(context)) {
			error = Pair.of(FonteAutenticaError.INVALID_REQUEST, ERROR_BAD_REQUEST);
		}
		else if(isErroreTokenNonPresente(context)) {
			error = Pair.of(FonteAutenticaError.INVALID_TOKEN, ERROR_NOT_PRESENT_TOKEN);
		}
		else if(isErroreToken(context)) {
			error = Pair.of(FonteAutenticaError.INVALID_TOKEN, ERROR_MALFORMED_TOKEN);
		}
		else if(isOperazioneNonIndividuata(context)) {
			error = Pair.of(FonteAutenticaError.NOT_FOUND, ERROR_NOT_FOUND);
		}
		else if(isConnectorError(context) || 
				isConnectionTimeout(context) || 
				isReadTimeout(context) || isResponseReadTimeout(context) ||
				isContenutiRispostaNonRiconosciuto(context) || isErroreCorrelazioneApplicativaRisposta(context) ||
				isErroreSicurezzaMessaggioRisposta(context) || isErroreAllegatiRisposta(context) ||
				isErroreValidazioneRisposta(context) || isErroreTrasformazioneRisposta(context) ||
				isRispostaDuplicata(context)) {
			error = Pair.of(FonteAutenticaError.SERVER_ERROR, ERROR_PROCESSING);
		} else if(context.getPddContext()!=null){		
			error = Objects.requireNonNullElse(parsePddContext(context, idTransazione), 
					Objects.requireNonNullElse(parseCode(context), error));
		} else {
			error = Objects.requireNonNullElse(parseCode(context), error);
		}
		
		buildError(context, error.getLeft(), error.getRight());
	}
	
	
	private static Transaction getTransaction(String idTransazione) {
		Transaction transaction = null;
		try {
			transaction = TransactionContext.getTransaction(idTransazione);
		}catch(TransactionNotExistsException n) {
			// ignore
		}
		return transaction;
	}
	
	private static boolean isErroreValidazioneRichiesta(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.ERRORE_VALIDAZIONE_RICHIESTA, context); 
	}
	private static boolean isErroreValidazioneRisposta(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.ERRORE_VALIDAZIONE_RISPOSTA, context); 
	}
	
	private static boolean isErroreTokenNonPresente(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.TOKEN_NON_PRESENTE, context); 
	}
	private static boolean isErroreToken(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.ERRORE_TOKEN, context); 
	}
	private static boolean isOperazioneNonIndividuata(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.OPERAZIONE_NON_INDIVIDUATA, context); 
	}
	
	private static boolean isReadTimeout(OutResponseContext context) {
		return isErroreString(org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_NAME_CONTROLLO_TRAFFICO_VIOLAZIONE, 
				org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_VALUE_READ_TIMEOUT, context); 
	}
	private static boolean isRequestReadTimeout(OutResponseContext context) {
		return isErroreString(org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_NAME_CONTROLLO_TRAFFICO_VIOLAZIONE, 
				org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_VALUE_REQUEST_READ_TIMEOUT, context); 
	}
	private static boolean isResponseReadTimeout(OutResponseContext context) {
		return isErroreString(org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_NAME_CONTROLLO_TRAFFICO_VIOLAZIONE, 
				org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_VALUE_RESPONSE_READ_TIMEOUT, context); 
	}
	private static boolean isConnectionTimeout(OutResponseContext context) {
		return isErroreString(org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_NAME_CONTROLLO_TRAFFICO_VIOLAZIONE, 
				org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_VALUE_CONNECTION_TIMEOUT, context); 
	}
	private static boolean isConnectorError(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.ERRORE_UTILIZZO_CONNETTORE, context); 
	}
	
	private static boolean isContenutiRichiestaNonRiconosciuto(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, context); 
	}
	private static boolean isContenutiRispostaNonRiconosciuto(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO, context); 
	}
	
	private static boolean isErroreSicurezzaMessaggioRisposta(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.ERRORE_SICUREZZA_MESSAGGIO_RISPOSTA, context); 
	}
	private static boolean isErroreAllegatiRisposta(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.ERRORE_ALLEGATI_MESSAGGIO_RISPOSTA, context); 
	}
	
	private static boolean isErroreCorrelazioneApplicativaRisposta(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.ERRORE_CORRELAZIONE_APPLICATIVA_RISPOSTA, context); 
	}
	
	private static boolean isErroreTrasformazioneRichiesta(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.ERRORE_TRASFORMAZIONE_RICHIESTA, context); 
	}
	private static boolean isErroreTrasformazioneRisposta(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.ERRORE_TRASFORMAZIONE_RISPOSTA, context); 
	}
	
	private static boolean isRispostaDuplicata(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.RISPOSTA_DUPLICATA, context); 
	}
	
	
	static boolean isErroreValidazioneRichiestaHashNotFound(String idTransazione) {
		Transaction transaction = getTransaction(idTransazione);
		if(transaction!=null && transaction.getMsgDiagnostici()!=null && !transaction.getMsgDiagnostici().isEmpty()) {
			for (MsgDiagnostico msg : transaction.getMsgDiagnostici()) {
				if(isMessaggioErroreValidazioneRichiestaHashNotFound(msg)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private static final String HASH_NOT_FOUND_MATCHING = ".*Parameter 'If-Match' is required.*";
	static boolean isMessaggioErroreValidazioneRichiestaHashNotFound(MsgDiagnostico msg) {
		if(msg.getSeverita()<=LogLevels.SEVERITA_ERROR_INTEGRATION && 
				msg.getMessaggio()!=null) {
			boolean match = false;
			try {
				match = RegularExpressionEngine.isFind(msg.getMessaggio(), HASH_NOT_FOUND_MATCHING);
			}catch(Exception e) {
				// ignore
			}
			if(match) {
				return true;
			}
		}
		return false;
	}
	
	static boolean isErroreBoolean(MapKey<String> key, OutResponseContext context) {
		if(context.getPddContext()!=null && context.getPddContext().containsKey(key)) {
			Object o = context.getPddContext().get(key);
			if(o instanceof String) {
				String s = (String) o;
				return "true".equals(s);
			}
			else if(o instanceof Boolean) {
				return (Boolean) o;
			}
		}
		return false;
	}
	
	static boolean isErroreString(String key, String value, OutResponseContext context) {
		MapKey<String> k = org.openspcoop2.utils.Map.newMapKey(key);
		return isErroreString(k, value, context);
	}
	static boolean isErroreString(MapKey<String> key, String value, OutResponseContext context) {
		if(context.getPddContext()!=null && context.getPddContext().containsKey(key)) {
			Object o = context.getPddContext().get(key);
			if(o instanceof String) {
				String s = (String) o;
				return value.equals(s);
			}
		}
		return false;
	}

	static boolean isRisposta5xxDifferenteJson(OutResponseContext context) {
		if(context!=null && 
				context.getMessaggio()!=null && context.getMessaggio().getTransportResponseContext()!=null &&
						context.getMessaggio().getTransportResponseContext().getCodiceTrasporto()!=null
				) {
			int codiceHttp = -1;
			try {
				codiceHttp = Integer.valueOf(context.getMessaggio().getTransportResponseContext().getCodiceTrasporto());
			}catch(Exception e) {
				context.getLogCore().error("isInternalRisposta5xxDifferenteJson: "+e.getMessage(),e);
				// ignore
			}
			if(codiceHttp>=500 && codiceHttp<=599) {
				return isInternalRisposta5xxDifferenteJson(context);
			}
		}
		return false;
	}
	private static boolean isInternalRisposta5xxDifferenteJson(OutResponseContext context) {
		try {
			String ct = context.getMessaggio().getContentType();
			if(ct==null) {
				return true; // 5xx senza contentType deve essere mappato
			}
			if(context.getMessaggio() instanceof OpenSPCoop2RestMessage<?>) {
				OpenSPCoop2RestMessage<?> rest = context.getMessaggio().castAsRest();
				if(!rest.hasContent()) {
					return true; // 5xx senza contenuto
				}
			}
			String baseCT = null;
			if(ContentTypeUtilities.isMultipartType(ct)) {
				baseCT = ContentTypeUtilities.getInternalMultipartContentType(ct);
				if(baseCT!=null) {
					baseCT = ContentTypeUtilities.readBaseTypeFromContentType(baseCT);
				}
			}
			else {
				baseCT = ContentTypeUtilities.readBaseTypeFromContentType(ct);
			}
			if(!HttpConstants.CONTENT_TYPE_JSON.equalsIgnoreCase(baseCT)) {
				return true;
			}
		}catch(Exception e) {
			context.getLogCore().error("isInternalRisposta5xxDifferenteJson failed: "+e.getMessage(),e);
			// ignore
		}
		return false;
	}
	
	private void buildError(OutResponseContext context, FonteAutenticaError error, String errorDetails) throws MessageException {
		ForcedResponseMessage frs = new ForcedResponseMessage();
		OpenSPCoop2RestJsonMessage json = context.getMessaggio().castAsRestJson();
		frs.setResponseCode(error.code+"");
		
		if (FonteAutenticaError.INVALID_TOKEN == error) {
			String msg = String.format("Bearer error=\"%s\", error_description=\"%s\"",
					error.name().toLowerCase(), 
					errorDetails);
			
			frs.setHeadersValues(Map.of(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE,
					List.of(msg)));
			frs.setContent(new byte[0]);
			frs.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
		} else {
			String msg = String.format("{\"error\": \"%s\", \"error_description\": \"%s\"}",
					error.name().toLowerCase(), 
					errorDetails);
			frs.setContent(msg.getBytes());
			frs.setContentType(HttpConstants.CONTENT_TYPE_JSON);
		}
		
		json.forceResponse(frs);
	}

}
