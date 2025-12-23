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
package org.openspcoop2.pdd.core.token.parser;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JwkReaderWriter;
import org.openspcoop2.pdd.core.token.Costanti;
import org.openspcoop2.pdd.core.token.TokenUtilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.security.JWTParser;

/**
 * BasicDPoPParser
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BasicDPoPParser implements IDPoPParser {

	private TipologiaClaimsDPoP tipologiaClaims;
	private Properties parserConfig;
	private String raw;
	private Map<String,Serializable> header;
	private Map<String,Serializable> payload;

	public BasicDPoPParser(TipologiaClaimsDPoP tipologiaClaims) {
		this(tipologiaClaims, null);
	}

	public BasicDPoPParser(TipologiaClaimsDPoP tipologiaClaims, Properties parserConfig) {
		this.tipologiaClaims = tipologiaClaims;
		this.parserConfig = parserConfig;
	}

	@Override
	public void init(String raw) throws UtilsException {
		this.raw = raw;
		// Parse header e payload con JWTParser
		JWTParser jwtParser = new JWTParser(this.raw);
		
		this.header = convert(jwtParser.getHeaderClaims());
		this.payload = convert(jwtParser.getPayloadClaims());
	}
	private Map<String,Serializable> convert(Map<String,String> m){
		Map<String,Serializable> newM = null;
		if(m!=null) {
			newM = new HashMap<>();
			if(!m.isEmpty()) {
				for (Map.Entry<String,String> entry : m.entrySet()) {
					newM.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return newM;
	}

	@Override
	public String getRaw() throws UtilsException{
		return this.raw;
	}
	
	@Override
	public void checkHttpTransaction(Integer httpResponseCode) throws UtilsException {
		// Per DPoP non serve controllare httpResponseCode come per introspection/userInfo
	}

	@Override
	public boolean isValid() {
		// Verifica base che i claim necessari siano presenti
		try {
			return this.header != null && this.payload != null &&
					this.getType() != null &&
					this.getAlgorithm() != null &&
					this.getJsonWebKeyAsString() != null &&
					this.getHttpMethod() != null &&
					this.getHttpUri() != null &&
					this.getIssuedAt() != null &&
					this.getAccessTokenHash() != null &&
					this.getJWTIdentifier() != null;
		}catch(Exception e) {
			return false;
		}
	}

	// Header claims

	@Override
	public String getType() {
		String tmp = null;
		switch (this.tipologiaClaims) {
		case RFC9449:
			tmp = TokenUtilities.getClaimAsString(this.header, Claims.JSON_WEB_TOKEN_RFC_7515_TYPE);
			break;
		case MAPPING:
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.DPOP_TOKEN_PARSER_TYP);
			tmp = TokenUtilities.getFirstClaimAsString(this.header, claimNames);
			break;
		case CUSTOM:
			// Il plugin custom deve implementare direttamente questo metodo
			break;
		}
		return tmp;
	}

	@Override
	public String getAlgorithm() {
		String tmp = null;
		switch (this.tipologiaClaims) {
		case RFC9449:
			tmp = TokenUtilities.getClaimAsString(this.header, Claims.JSON_WEB_TOKEN_RFC_7515_ALGORITHM);
			break;
		case MAPPING:
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.DPOP_TOKEN_PARSER_ALG);
			tmp = TokenUtilities.getFirstClaimAsString(this.header, claimNames);
			break;
		case CUSTOM:
			break;
		}
		return tmp;
	}

	private JsonWebKey jsonWebKeyObject = null;
	@Override
	public JsonWebKey getJsonWebKey() throws UtilsException {
		
		if(this.jsonWebKeyObject != null) {
			return this.jsonWebKeyObject;
		}
		
		String jwkJson = getJsonWebKeyAsString();
		
		// Parse JWK using Apache CXF
		JwkReaderWriter jwkReader = new JwkReaderWriter();
		this.jsonWebKeyObject = jwkReader.jsonToJwk(jwkJson);
		return this.jsonWebKeyObject;
	}
	private String jsonWebKey = null;
	@Override
	public String getJsonWebKeyAsString() throws UtilsException {
		
		if(this.jsonWebKey != null) {
			return this.jsonWebKey;
		}
		
		// RFC 9449 specifies that jwk is a JSON object in the header
		// JWTParser flattens nested objects (jwk.alg, jwk.n, etc.), so we need to parse the raw JWT header directly

		String jwkClaimName = getJsonWebKeyClaimName();
		if(jwkClaimName==null) {
			return null;
		}

		try {
			// Parse JWT header directly from raw token to preserve nested structure
			if(this.raw==null || this.raw.isEmpty()) {
				return null;
			}

			String[] parts = this.raw.split("\\.");
			if(parts.length < 2) {
				return null;
			}

			// Decode Base64URL header
			byte[] headerBytes = java.util.Base64.getUrlDecoder().decode(parts[0]);
			String headerJson = new String(headerBytes, java.nio.charset.StandardCharsets.UTF_8);

			// Parse header as JSON to extract jwk object
			com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
			@SuppressWarnings("unchecked")
			Map<String, Object> headerMap = mapper.readValue(headerJson, Map.class);

			Object jwkObject = headerMap.get(jwkClaimName);
			if(jwkObject==null) {
				return null;
			}

			// Serialize jwk object back to JSON string
			String jwkJson = null;
			if(jwkObject instanceof Map) {
				jwkJson = mapper.writeValueAsString(jwkObject);
			}
			else if(jwkObject instanceof String) {
				jwkJson = (String) jwkObject;
			}
			else {
				return null;
			}

			if(jwkJson==null || jwkJson.trim().isEmpty()) {
				return null;
			}
			this.jsonWebKey = jwkJson;
			return jwkJson;

		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	private String getJsonWebKeyClaimName() {
		String jwkClaimName = null;
		switch (this.tipologiaClaims) {
		case RFC9449:
			jwkClaimName = Claims.DPOP_RFC9449_JWK;
			break;
		case MAPPING:
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.DPOP_TOKEN_PARSER_JWK);
			if(claimNames!=null && !claimNames.isEmpty()) {
				jwkClaimName = claimNames.get(0); // Use first configured name
			}
			break;
		case CUSTOM:
			return null;
		}
		return jwkClaimName;
	}

	// Payload claims

	@Override
	public String getJWTIdentifier() {
		String tmp = null;
		switch (this.tipologiaClaims) {
		case RFC9449:
			tmp = TokenUtilities.getClaimAsString(this.payload, Claims.JSON_WEB_TOKEN_RFC_7519_JWT_ID);
			break;
		case MAPPING:
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.DPOP_TOKEN_PARSER_JTI);
			tmp = TokenUtilities.getFirstClaimAsString(this.payload, claimNames);
			break;
		case CUSTOM:
			break;
		}
		return tmp;
	}

	@Override
	public String getHttpMethod() {
		String tmp = null;
		switch (this.tipologiaClaims) {
		case RFC9449:
			tmp = TokenUtilities.getClaimAsString(this.payload, Claims.DPOP_RFC9449_HTTP_METHOD);
			break;
		case MAPPING:
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.DPOP_TOKEN_PARSER_HTM);
			tmp = TokenUtilities.getFirstClaimAsString(this.payload, claimNames);
			break;
		case CUSTOM:
			break;
		}
		return tmp;
	}

	@Override
	public String getHttpUri() {
		String tmp = null;
		switch (this.tipologiaClaims) {
		case RFC9449:
			tmp = TokenUtilities.getClaimAsString(this.payload, Claims.DPOP_RFC9449_HTTP_URI);
			break;
		case MAPPING:
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.DPOP_TOKEN_PARSER_HTU);
			tmp = TokenUtilities.getFirstClaimAsString(this.payload, claimNames);
			break;
		case CUSTOM:
			break;
		}
		return tmp;
	}

	@Override
	public Date getIssuedAt() {
		String tmp = null;
		switch (this.tipologiaClaims) {
		case RFC9449:
			tmp = TokenUtilities.getClaimAsString(this.payload, Claims.JSON_WEB_TOKEN_RFC_7519_ISSUED_AT);
			break;
		case MAPPING:
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.DPOP_TOKEN_PARSER_IAT);
			tmp = TokenUtilities.getFirstClaimAsString(this.payload, claimNames);
			break;
		case CUSTOM:
			break;
		}
		return tmp!=null ? TokenUtils.parseTimeInSecond(tmp) : null;
	}

	@Override
	public String getAccessTokenHash() {
		String tmp = null;
		switch (this.tipologiaClaims) {
		case RFC9449:
			tmp = TokenUtilities.getClaimAsString(this.payload, Claims.DPOP_RFC9449_ACCESS_TOKEN_HASH);
			break;
		case MAPPING:
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.DPOP_TOKEN_PARSER_ATH);
			tmp = TokenUtilities.getFirstClaimAsString(this.payload, claimNames);
			break;
		case CUSTOM:
			break;
		}
		return tmp;
	}
}
