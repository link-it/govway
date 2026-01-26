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


package org.openspcoop2.security.message.engine;


import java.time.Instant;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.pdd.core.dynamic.DynamicException;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.IMessageSecuritySender;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.MessageSecurityUtilities;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;

import net.minidev.json.JSONObject;

/**
 * Classe per la gestione della Sicurezza (role:Sender)
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author Tommaso Burlon (tommaso.burlom@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class MessageSecuritySender_impl extends MessageSecuritySender {

	protected MessageSecuritySender_impl(MessageSecurityContext messageSecurityContext) {
	    super(messageSecurityContext);
    }

	private boolean isIntegerInRanges(Integer value, String rawRanges) {
		String[] ranges = rawRanges.replace(" ", "").split(",");
		for (String range : ranges) {
			int minRange = 0;
			int maxRange = 0;
			if (range.contains("-")) {
				String[] limits = range.split("-");
				minRange = limits[0].isBlank() ? 0 : Integer.parseInt(limits[0]);
				maxRange = limits.length == 1 ? Integer.MAX_VALUE : Integer.parseInt(limits[1]);
			} else {
				minRange = Integer.parseInt(range);
				maxRange = minRange;
			}

			if (value >= minRange && value <= maxRange)
				return true;
		}
		return false;
	}

	private String resolveDynamicValue(String name, String value, Map<String, Object> dynamicMap, Context context) throws SecurityException {
		if (value == null || dynamicMap == null) {
			return value;
		}
		try {
			return DynamicUtils.convertDynamicPropertyValue(name, value, dynamicMap, context);
		} catch (DynamicException e) {
			throw new SecurityException(e.getMessage(), e);
		}
	}

	private String getOutgoingProperty(String key) {
		return (String) this.messageSecurityContext.getOutgoingProperties().get(key);
	}

	/**
	 * Risolve il valore del claim in base alla policy configurata per i claims esistenti.
	 *
	 * @param existingJson JSON esistente parsato dal messaggio (può essere null)
	 * @param claimName nome del claim da verificare
	 * @param newValue valore da inserire
	 * @param policy policy da applicare (preserve, override, error)
	 * @return il valore da usare (nuovo valore o null se il claim esiste e policy=preserve)
	 * @throws SecurityException se policy=error e il claim esiste già
	 */
	private Object resolveClaimValue(JSONObject existingJson, String claimName,
			Object newValue, String policy) throws SecurityException {

		if (existingJson == null) {
			return newValue;
		}

		boolean claimExists = existingJson.containsKey(claimName);

		if (!claimExists) {
			return newValue;
		}

		// Claim esiste, applica policy
		if (SecurityConstants.JWT_CLAIMS_EXISTING_POLICY_PRESERVE.equals(policy)) {
			// Mantieni originale, non aggiungere nulla
			return null;
		} else if (SecurityConstants.JWT_CLAIMS_EXISTING_POLICY_OVERRIDE.equals(policy)) {
			// Sovrascrivi con il nuovo valore
			return newValue;
		} else if (SecurityConstants.JWT_CLAIMS_EXISTING_POLICY_ERROR.equals(policy)) {
			throw new SecurityException("Il claim '" + claimName + "' è già presente nella risposta");
		}

		// Default: preserve
		return null;
	}

	private JSONObject getJsonMessage(OpenSPCoop2Message message) {
		JSONObject existingJson = null;
		try {
			if (message.castAsRestJson().hasContent()) {
				String jsonContent = message.castAsRestJson().getContent();
				if (jsonContent != null && !jsonContent.trim().isEmpty()) {
					existingJson = JsonPathExpressionEngine.getJSONObject(jsonContent);
				}
			}
		} catch (Exception e) {
			this.messageSecurityContext.getLog().debug("Parsing JSON esistente fallito: {}", e.getMessage());
		}
		
		return existingJson;
	}
	
	private JSONObject addIssClaim(Context context, OpenSPCoop2Message message, JSONObject existingJson, String policy) throws SecurityException, MessageException, MessageNotSupportedException {
		Map<String, Object> dynamicMap = Costanti.readDynamicMap(context);
		
		String iss = getOutgoingProperty(SecurityConstants.JWT_CLAIMS_ISSUER);
		if (iss != null) {
			if (existingJson == null)
				existingJson = getJsonMessage(message);
			
			iss = resolveDynamicValue("iss", iss, dynamicMap, context);
			Object resolvedIss = resolveClaimValue(existingJson, "iss", iss, policy);
			if (resolvedIss != null) {
				message.castAsRestJson().addSimpleElement("iss", resolvedIss);
			}
		}
		
		return existingJson;
	}
	
	private JSONObject addAudClaim(Context context, OpenSPCoop2Message message, JSONObject existingJson, String policy) throws SecurityException, MessageException, MessageNotSupportedException {
		Map<String, Object> dynamicMap = Costanti.readDynamicMap(context);
		if (existingJson == null)
			existingJson = getJsonMessage(message);
		
		String audMode = getOutgoingProperty(SecurityConstants.JWT_CLAIMS_AUDIENCE_MODE);
		String aud = getOutgoingProperty(SecurityConstants.JWT_CLAIMS_AUDIENCE_VALUE);
		if (SecurityConstants.JWT_CLAIMS_AUDIENCE_MODE_VOUCHER.equals(audMode)) {
			aud = "${tokenInfo:sub}";
		}
		if (aud != null) {
			if (existingJson == null)
				existingJson = getJsonMessage(message);
			
			aud = resolveDynamicValue("aud", aud, dynamicMap, context);
			Object resolvedAud = resolveClaimValue(existingJson, "aud", aud, policy);
			if (resolvedAud != null) {
				message.castAsRestJson().addSimpleElement("aud", resolvedAud);
			}
		}
		
		return existingJson;
	}
	
	private JSONObject addExpClaim(Context context, OpenSPCoop2Message message, JSONObject existingJson, String policy) throws SecurityException, MessageException, MessageNotSupportedException {
		Map<String, Object> dynamicMap = Costanti.readDynamicMap(context);
		
		String ttl = getOutgoingProperty(SecurityConstants.JWT_CLAIMS_EXPIRED_TTL);
		if (ttl != null) {
			if (existingJson == null)
				existingJson = getJsonMessage(message);
			
			ttl = resolveDynamicValue("ttl", ttl, dynamicMap, context);
			long instant = Instant.now().getEpochSecond();

			Object resolvedIat = resolveClaimValue(existingJson, "iat", instant, policy);
			if (resolvedIat != null) {
				message.castAsRestJson().addSimpleElement("iat", resolvedIat);
			}

			Object resolvedNbf = resolveClaimValue(existingJson, "nbf", instant, policy);
			if (resolvedNbf != null) {
				message.castAsRestJson().addSimpleElement("nbf", resolvedNbf);
			}

			long expValue = instant + Integer.parseInt(ttl);
			Object resolvedExp = resolveClaimValue(existingJson, "exp", expValue, policy);
			if (resolvedExp != null) {
				message.castAsRestJson().addSimpleElement("exp", resolvedExp);
			}
		}
		
		return existingJson;
	}
	
	private JSONObject addJtiClaim(Context context, OpenSPCoop2Message message, JSONObject existingJson, String policy) throws SecurityException, MessageException, MessageNotSupportedException {
		Map<String, Object> dynamicMap = Costanti.readDynamicMap(context);
		
		String jtiMode = getOutgoingProperty(SecurityConstants.JWT_CLAIMS_JTI_MODE);
		String jti = getOutgoingProperty(SecurityConstants.JWT_CLAIMS_JTI_VALUE);
		if (SecurityConstants.JWT_CLAIMS_JTI_MODE_TRANSACTION_ID.equals(jtiMode)) {
			jti = message.getTransactionId();
		} else if (jti != null) {
			jti = resolveDynamicValue("jti", jti, dynamicMap, context);
		}
		if (jti != null) {
			if (existingJson == null)
				existingJson = getJsonMessage(message);
			
			Object resolvedJti = resolveClaimValue(existingJson, "jti", jti, policy);
			if (resolvedJti != null) {
				message.castAsRestJson().addSimpleElement("jti", resolvedJti);
			}
		}
		
		return existingJson;
	}
	
	private void addJSONClaims(OpenSPCoop2Message message, org.openspcoop2.utils.Map<Object> ctx) throws MessageException, MessageNotSupportedException, SecurityException {
		Context context = (ctx instanceof Context) ? (Context) ctx : null;

		// Leggi la policy per i claims esistenti (default: preserve)
		String policy = getOutgoingProperty(SecurityConstants.JWT_CLAIMS_EXISTING_POLICY);
		if (policy == null || policy.isEmpty()) {
			policy = SecurityConstants.JWT_CLAIMS_EXISTING_POLICY_PRESERVE;
		}		

		String contentType = getOutgoingProperty(SecurityConstants.MESSAGE_SECURITY_CONTENT_TYPE);
		if (contentType != null) {
			message.setContentType(contentType);
		}

		JSONObject json = null; // la struttura json viene valorizzata solo se necessario
		
		json = addIssClaim(context, message, json, policy);

		json = addAudClaim(context, message, json, policy);

		json = addExpClaim(context, message, json, policy);

		addJtiClaim(context, message, json, policy);
	
	}

    @Override
    protected boolean process(OpenSPCoop2Message message, org.openspcoop2.utils.Map<Object> ctx) {
		try{

			IMessageSecuritySender senderInterface = this.messageSecurityContext.getMessageSecuritySender();


			String codeRanges = getOutgoingProperty(SecurityConstants.MESSAGE_SECURITY_APPLY_ON_BACKEND_RETURN_CODE);
			if (codeRanges != null) {
				String responseCode = message.getTransportResponseContext() != null ? message.getTransportResponseContext().getCodiceTrasporto() : message.getForcedResponseCode();
				Integer code = NumberUtils.toInt(responseCode, -1);
				if (code >= 0 && !isIntegerInRanges(code, codeRanges))
					return true;
			}

			// Fix per SOAPFault (quando ci sono le encryptionParts o le signatureParts, la Security fallisce se c'e' un SOAPFault)
			if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
				if(message.isFault() || message.castAsSoap().getSOAPBody().hasFault()){

					if(MessageSecurityUtilities.processSOAPFault(this.messageSecurityContext.getOutgoingProperties()) == false){
						return true; // non devo applicare la sicurezza.
					}

				}
			}
			else if(ServiceBinding.REST.equals(message.getServiceBinding())){
				if(message.isFault() || message.castAsRest().isProblemDetailsForHttpApis_RFC7807()){

					if(MessageSecurityUtilities.processProblemDetails(this.messageSecurityContext.getOutgoingProperties()) == false){
						return true; // non devo applicare la sicurezza.
					}

				}
			}

			String action = getOutgoingProperty(SecurityConstants.ACTION);
			if(action==null || "".equals(action.trim())){
				return true; // nessuna action: non devo applicare la sicurezza.
			}

			if (Arrays.stream(action.split(" ")).anyMatch(a -> a.trim().contains(SecurityConstants.SIGNATURE_ACTION))
					&& MessageType.JSON.equals(message.getMessageType())) {
				addJSONClaims(message, ctx);
			}

			// Utilizzo l'engine di sicurezza
			senderInterface.process(this.messageSecurityContext, message, ctx);

		}
		catch(Exception e){

			String prefix = "Generatosi errore durante il processamento Message-Security(Sender): ";

			this.messageSecurityContext.getLog().error(prefix+e.getMessage(),e);

			this.msgErrore =  prefix+e.getMessage();
    	    this.codiceErrore = CodiceErroreCooperazione.SICUREZZA;

			if(e instanceof SecurityException){
				SecurityException securityException = (SecurityException) e;
				if(securityException.getMsgErrore()!=null){
					this.msgErrore = prefix+securityException.getMsgErrore();
				}
				if(securityException.getCodiceErrore()!=null){
					this.codiceErrore = securityException.getCodiceErrore();
				}
			}

    	    return false;
		}
		return true;
    }

}
