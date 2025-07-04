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
package org.openspcoop2.pdd.core.handlers.suap;

import java.util.HashMap;
import java.util.List;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.message.ForcedResponseMessage;
import org.openspcoop2.message.OpenSPCoop2Message;
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
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.utils.Map;
import org.openspcoop2.utils.MapKey;
import org.openspcoop2.utils.transport.TransportResponseContext;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * SUAPResponseGenerator
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SUAPResponseGenerator implements OutResponseHandler {

	private static final String ERROR_400_001_CODE = "ERROR_400_001";
	private static final String ERROR_400_001_MESSAGE = "incorrect request input";
	
	private static final String ERROR_401_001_CODE = "ERROR_401_001";
	private static final String ERROR_401_001_MESSAGE = "PDND token not found";
	
	private static final String ERROR_401_002_CODE = "ERROR_401_002";
	private static final String ERROR_401_002_MESSAGE = "Invalid PDND token";
	
	private static final String ERROR_401_003 = "ERROR_401_003";
	private static final String ERROR_401_003_MESSAGE = "AgID-JWT-Signature token not found";
	
	private static final String ERROR_401_004_CODE = "ERROR_401_004";
	private static final String ERROR_401_004_MESSAGE = "invalid AgID-JWT-Signature token";
	
	private static final String ERROR_404_001_CODE = "ERROR_404_001";
	private static final String ERROR_404_001_MESSAGE = "resource not found";
	
	private static final String ERROR_500_007 = "ERROR_500_007";
	private static final String ERROR_500_007_MESSAGE = "response processing error";
	
	@Override
	public void invoke(OutResponseContext context) throws HandlerException {
		if(context!=null && context.getMessaggio()!=null 
				&& MessageType.JSON.equals(context.getMessaggio().getMessageType()) 
				&& MessageRole.FAULT.equals(context.getMessaggio().getMessageRole())) {
			try {
				
				if(isErroreValidazioneRichiesta(context) || 
						isRequestReadTimeout(context) ||
						isContenutiRichiestaNonRiconosciuto(context) ||
						isErroreCorrelazioneApplicativaRisposta(context) ||
						isErroreTrasformazioneRichiesta(context)) {
					modifyErrorMessage(400, ERROR_400_001_CODE, ERROR_400_001_MESSAGE, context.getMessaggio());
				}
				else if(isErroreTokenNonPresente(context)) {
					modifyErrorMessage(401, ERROR_401_001_CODE, ERROR_401_001_MESSAGE, context.getMessaggio());
				}
				else if(isErroreToken(context)) {
					modifyErrorMessage(401, ERROR_401_002_CODE, ERROR_401_002_MESSAGE, context.getMessaggio());
				}
				else if(isOperazioneNonIndividuata(context)) {
					modifyErrorMessage(404, ERROR_404_001_CODE, ERROR_404_001_MESSAGE, context.getMessaggio());
				}
				else if(isConnectorError(context) || 
						isConnectionTimeout(context) || 
						isReadTimeout(context) || isResponseReadTimeout(context) ||
						isContenutiRispostaNonRiconosciuto(context) || isErroreCorrelazioneApplicativaRisposta(context) ||
						isErroreSicurezzaMessaggioRisposta(context) || isErroreAllegatiRisposta(context) ||
						isErroreValidazioneRisposta(context) || isErroreTrasformazioneRisposta(context) ||
						isRispostaDuplicata(context)) {
					modifyErrorMessage(500, ERROR_500_007, ERROR_500_007_MESSAGE, context.getMessaggio());
				}
				else if(context.getPddContext()!=null){
					PdDContext pddContext = context.getPddContext();
					if (pddContext.getObject(Costanti.ID_TRANSAZIONE)==null)
						throw new HandlerException("Identificativo della transazione assente");
					String idTransazione = (String) pddContext.getObject(Costanti.ID_TRANSAZIONE);
					if (idTransazione==null)
						throw new HandlerException("Identificativo della transazione assente");
					
					Transaction transaction = getTransaction(idTransazione);
					boolean casoAgitJWTSignatureNonPresente = false;
					boolean casoAgitJWTSignatureNonValido = false;
					boolean casoVoucherPDNDNonValido = false;
					if(transaction!=null && transaction.getTracciaRisposta()!=null && transaction.getTracciaRisposta().getBusta()!=null &&
							transaction.getTracciaRisposta().getBusta().getListaEccezioni()!=null &&
							!transaction.getTracciaRisposta().getBusta().getListaEccezioni().isEmpty()) {
						for (Eccezione eccezione : transaction.getTracciaRisposta().getBusta().getListaEccezioni()) {
							if(eccezione.getCodiceEccezione()!=null) {
								if(eccezione.getCodiceEccezione().equals(CodiceErroreCooperazione.SICUREZZA_TOKEN_NON_PRESENTE.getCodice())) {
									String desc = eccezione.getDescrizione(context.getProtocolFactory());
									if(desc!=null && "Header HTTP 'Agid-JWT-Signature' non presente".equals(desc)) {
										casoAgitJWTSignatureNonPresente = true;
									}
								}
								else if(eccezione.getCodiceEccezione().equals(CodiceErroreCooperazione.SICUREZZA_FIRMA_NON_VALIDA.getCodice()) ||
										( CodiceErroreCooperazione.isEccezioneSicurezza(eccezione.getCodiceEccezione()) && !CodiceErroreCooperazione.isEccezioneSicurezzaToken(eccezione.getCodiceEccezione()) ) ||
										CodiceErroreCooperazione.isEccezioneServizioApplicativoErogatore(eccezione.getCodiceEccezione()) // caso audience non valido
										) {
									String desc = eccezione.getDescrizione(context.getProtocolFactory());
									if(desc!=null && desc.contains("Header 'Authorization'")) {
										casoVoucherPDNDNonValido = true;
									}
									else {
										casoAgitJWTSignatureNonValido = true;
									}
								}
								else if(CodiceErroreCooperazione.isEccezioneSicurezzaToken(eccezione.getCodiceEccezione())){ // caso audience non valido per voucher PDND
									casoVoucherPDNDNonValido = true;
								}
							}
						}
					}
					if(casoAgitJWTSignatureNonPresente) {
						modifyErrorMessage(401, ERROR_401_003, ERROR_401_003_MESSAGE, context.getMessaggio());
					}
					else if(casoAgitJWTSignatureNonValido) {
						modifyErrorMessage(401, ERROR_401_004_CODE, ERROR_401_004_MESSAGE, context.getMessaggio());
					}
					else if(casoVoucherPDNDNonValido) {
						modifyErrorMessage(401, ERROR_401_002_CODE, ERROR_401_002_MESSAGE, context.getMessaggio());
					}
				}
				
			}catch(Exception e) {
				context.getLogCore().error("Conversione non riuscita: "+e.getMessage(),e);
			}
		}
		else if(context!=null && context.getMessaggio()!=null) {
			try {					
				if(isRisposta5xxDifferenteJson(context)) {
					if(MessageType.JSON.equals(context.getMessaggio().getMessageType())) { 
						modifyErrorMessage(500, ERROR_500_007, ERROR_500_007_MESSAGE, context.getMessaggio());
					}
					else {
						replaceErrorMessage(500, ERROR_500_007, ERROR_500_007_MESSAGE, context);
					}
				}
			}catch(Exception e) {
				context.getLogCore().error("Conversione (risposta backend) non riuscita: "+e.getMessage(),e);
			}
		}
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
	
	static boolean isErroreValidazioneRichiesta(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.ERRORE_VALIDAZIONE_RICHIESTA, context); 
	}
	static boolean isErroreValidazioneRisposta(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.ERRORE_VALIDAZIONE_RISPOSTA, context); 
	}
	
	static boolean isErroreTokenNonPresente(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.TOKEN_NON_PRESENTE, context); 
	}
	static boolean isErroreToken(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.ERRORE_TOKEN, context); 
	}
	static boolean isOperazioneNonIndividuata(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.OPERAZIONE_NON_INDIVIDUATA, context); 
	}
	
	static boolean isReadTimeout(OutResponseContext context) {
		return isErroreString(org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_NAME_CONTROLLO_TRAFFICO_VIOLAZIONE, 
				org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_VALUE_READ_TIMEOUT, context); 
	}
	static boolean isRequestReadTimeout(OutResponseContext context) {
		return isErroreString(org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_NAME_CONTROLLO_TRAFFICO_VIOLAZIONE, 
				org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_VALUE_REQUEST_READ_TIMEOUT, context); 
	}
	static boolean isResponseReadTimeout(OutResponseContext context) {
		return isErroreString(org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_NAME_CONTROLLO_TRAFFICO_VIOLAZIONE, 
				org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_VALUE_RESPONSE_READ_TIMEOUT, context); 
	}
	static boolean isConnectionTimeout(OutResponseContext context) {
		return isErroreString(org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_NAME_CONTROLLO_TRAFFICO_VIOLAZIONE, 
				org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_VALUE_CONNECTION_TIMEOUT, context); 
	}
	static boolean isConnectorError(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.ERRORE_UTILIZZO_CONNETTORE, context); 
	}
	
	static boolean isContenutiRichiestaNonRiconosciuto(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, context); 
	}
	static boolean isContenutiRispostaNonRiconosciuto(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO, context); 
	}
	
	static boolean isErroreSicurezzaMessaggioRisposta(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.ERRORE_SICUREZZA_MESSAGGIO_RISPOSTA, context); 
	}
	static boolean isErroreAllegatiRisposta(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.ERRORE_ALLEGATI_MESSAGGIO_RISPOSTA, context); 
	}
	
	static boolean isErroreCorrelazioneApplicativaRichiesta(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.ERRORE_CORRELAZIONE_APPLICATIVA_RICHIESTA, context); 
	}
	static boolean isErroreCorrelazioneApplicativaRisposta(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.ERRORE_CORRELAZIONE_APPLICATIVA_RISPOSTA, context); 
	}
	
	static boolean isErroreTrasformazioneRichiesta(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.ERRORE_TRASFORMAZIONE_RICHIESTA, context); 
	}
	static boolean isErroreTrasformazioneRisposta(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.ERRORE_TRASFORMAZIONE_RISPOSTA, context); 
	}
	
	static boolean isRispostaDuplicata(OutResponseContext context) {
		return isErroreBoolean(org.openspcoop2.core.constants.Costanti.RISPOSTA_DUPLICATA, context); 
	}
	
	static boolean isErroreBoolean(String key, OutResponseContext context) {
		MapKey<String> k = Map.newMapKey(key);
		return isErroreBoolean(k, context);
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
		MapKey<String> k = Map.newMapKey(key);
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
	
	static void modifyErrorMessage(int responseCode, String code, String message, OpenSPCoop2Message msg) throws MessageException {
		String error = "{\"code\": \""+code+"\", \"message\": \""+message+"\"}";
		OpenSPCoop2RestJsonMessage json = msg.castAsRestJson();
		ForcedResponseMessage frs = new ForcedResponseMessage();
		frs.setContent(error.getBytes());
		frs.setContentType(HttpConstants.CONTENT_TYPE_JSON);
		frs.setResponseCode(responseCode+"");
		json.forceResponse(frs);
	}
	static void replaceErrorMessage(int responseCode, String code, String message, OutResponseContext context) throws MessageException {
		try {
			String error = "{\"code\": \""+code+"\", \"message\": \""+message+"\"}";
			TransportResponseContext responseContext = new TransportResponseContext();
			responseContext.setCodiceTrasporto(responseCode+"");
			java.util.Map<String, List<String>> headers = new HashMap<>();
			TransportUtils.addHeader(headers, HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			responseContext.setHeaders(headers);
			OpenSPCoop2Message errorMessage = context.getMessaggio().getFactory().createMessage(MessageType.JSON, responseContext, error.getBytes()).getMessage_throwParseException();
			context.setMessaggio(errorMessage);
		}catch(Exception e) {
			throw new MessageException(e.getMessage(),e);
		}
	}
}

