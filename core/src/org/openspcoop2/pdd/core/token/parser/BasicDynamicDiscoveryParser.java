/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.pdd.core.token.Costanti;
import org.openspcoop2.pdd.core.token.TokenUtilities;
import org.openspcoop2.utils.UtilsException;

/**     
 * BasicDynamicDiscoveryParser
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BasicDynamicDiscoveryParser implements IDynamicDiscoveryParser {

	protected Integer httpResponseCode;
	protected String raw;
	protected Map<String, Serializable> claims;
	protected TipologiaClaims parser;
	protected Properties parserConfig;
	
	public BasicDynamicDiscoveryParser(TipologiaClaims parser) {
		this(parser, null);
	}
	public BasicDynamicDiscoveryParser(TipologiaClaims parser, Properties parserConfig) {
		this.parser = parser;
		this.parserConfig = parserConfig;
	}
	
	@Override
	public void init(String raw, Map<String, Serializable> claims) {
		this.raw = raw;
		this.claims = claims;
	}
	
	private void throwUnsupportedParser() throws UtilsException {
		String msgError = "Tipo di parser '"+this.parser+"' non supportato";
		throw new UtilsException(msgError);
	}
	
	@Override
	public void checkHttpTransaction(Integer httpResponseCode) throws UtilsException{
		this.httpResponseCode = httpResponseCode;
		switch (this.parser) {
		case OIDC_ID_TOKEN:
		case MAPPING:
		case CUSTOM:
			if(this.httpResponseCode!=null && 
				(this.httpResponseCode.intValue() < 200 || this.httpResponseCode.intValue()>299)) {
				String msgError = "Connessione terminata con errore (codice trasporto: "+this.httpResponseCode.intValue()+")";
				throw new UtilsException(msgError+": "+this.raw);
			}
			break;
		default:
			throwUnsupportedParser();
		}
	}
	
	@Override
	public String getJwksUri() {
		switch (this.parser) {
		case OIDC_ID_TOKEN:
			return TokenUtilities.getClaimAsString(this.claims,Claims.OIDC_ID_DISCOVERY_DYNAMIC_JWK_URI);
		case MAPPING:
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.POLICY_DISCOVERY_JWK_CUSTOM);
			return TokenUtilities.getFirstClaimAsString(this.claims, claimNames);
		case CUSTOM:
			return null;
		default:
			break;
		}
		return null;
	}
		
	@Override
	public String getIntrospectionEndpoint() {
		switch (this.parser) {
		case OIDC_ID_TOKEN:
			return TokenUtilities.getClaimAsString(this.claims,Claims.OIDC_ID_DISCOVERY_DYNAMIC_INTROSPECTION_ENDPOINT);
		case MAPPING:
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.POLICY_DISCOVERY_INTROSPECTION_CUSTOM);
			return TokenUtilities.getFirstClaimAsString(this.claims, claimNames);
		case CUSTOM:
			return null;
		default:
			break;
		}
		return null;
	}

	@Override
	public String getUserInfoEndpoint() {
		switch (this.parser) {
		case OIDC_ID_TOKEN:
			return TokenUtilities.getClaimAsString(this.claims,Claims.OIDC_ID_DISCOVERY_DYNAMIC_USERINFO_ENDPOINT);
		case MAPPING:
			List<String> claimNames = TokenUtilities.getClaims(this.parserConfig, Costanti.POLICY_DISCOVERY_USERINFO_CUSTOM);
			return TokenUtilities.getFirstClaimAsString(this.claims, claimNames);
		case CUSTOM:
			return null;
		default:
			break;
		}
		return null;
	}
	
}
