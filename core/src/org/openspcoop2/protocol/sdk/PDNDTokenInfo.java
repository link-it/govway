/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.sdk;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.slf4j.Logger;

/**     
 * PDNDTokenInfo
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$AuthorizationMessageSecurityToken
 */
public class PDNDTokenInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String,PDNDTokenInfoDetails> pdnd = new HashMap<>();
	
	public Map<String, PDNDTokenInfoDetails> getPdnd() {
		return this.pdnd;
	}
	public void setPdnd(Map<String, PDNDTokenInfoDetails> pdnd) {
		this.pdnd = pdnd;
	}
	
	public PDNDTokenInfoDetails getInfo(String infoId) {
		return this.pdnd!=null ? this.pdnd.get(infoId) : null;
	}
	public void setInfo(String infoId, PDNDTokenInfoDetails info) {
		if(this.pdnd==null) {
			this.pdnd = new HashMap<>();
		}
		this.pdnd.put(infoId, info);
	}
	
	public static final String CLIENT_INFO = "client";
	public PDNDTokenInfoDetails getClient() {
		return getInfo(CLIENT_INFO);
	}
	public Serializable getClient(String claim) {
		PDNDTokenInfoDetails details = getInfo(CLIENT_INFO);
		return details!=null && details.getClaims()!=null ? details.getClaims().get(claim) : null;
	}
	public void setClient(PDNDTokenInfoDetails info) {
		setInfo(CLIENT_INFO, info);
	}
	
	public static final String ORGANIZATION_INFO = "organization";
	public PDNDTokenInfoDetails getOrganization() {
		return getInfo(ORGANIZATION_INFO);
	}
	public Serializable getOrganization(String claim) {
		PDNDTokenInfoDetails details = getInfo(ORGANIZATION_INFO);
		return details!=null && details.getClaims()!=null ? details.getClaims().get(claim) : null;
	}
	public void setOrganization(PDNDTokenInfoDetails info) {
		setInfo(ORGANIZATION_INFO, info);
	}
	
	public static final String TOKEN_INFO_PREFIX_PDND = "pdnd.";
	
	public static boolean existsOrganizationInfoPDNDTokenInfo(Map<String, ?> mapPdnd) {
		return isPDNDTokenInfoMap(mapPdnd, PDNDTokenInfo.ORGANIZATION_INFO);
	}
	public static boolean existsClientInfoPDNDTokenInfo(Map<String, ?> mapPdnd) {
		return isPDNDTokenInfoMap(mapPdnd, PDNDTokenInfo.CLIENT_INFO);
	}
	private static boolean isPDNDTokenInfoMap(Map<String, ?> mapPdnd, String key) {
		Object o = mapPdnd.get(key);
		return o instanceof PDNDTokenInfoDetails;
	}
	
	public String getOrganizationJson() {
		return getOrganizationJsonDetailFromPDNDMapTokenInfo(this.pdnd);
	}
	public static String getOrganizationJsonDetailFromPDNDMapTokenInfo(Map<String, PDNDTokenInfoDetails> mapPdnd) {
		PDNDTokenInfoDetails details = mapPdnd.get(PDNDTokenInfo.ORGANIZATION_INFO);
		return details!=null ? details.getDetails(): null;
	}
	public String getClientJson() {
		return getClientJsonDetailFromPDNDMapTokenInfo(this.pdnd);
	}
	public static String getClientJsonDetailFromPDNDMapTokenInfo(Map<String, PDNDTokenInfoDetails> mapPdnd) {
		PDNDTokenInfoDetails details = mapPdnd.get(PDNDTokenInfo.CLIENT_INFO);
		return details!=null ? details.getDetails(): null;
	}
	
	public static String getOrganizationInfoFromPDNDMap(Map<String, ?> mapPdnd, String name) {
		Object o = mapPdnd.get(PDNDTokenInfo.ORGANIZATION_INFO);
		if(o instanceof Map) {
			Map<?, ?> m = (Map<?, ?>) o;
			return (String) m.get(name);
		}
		return null;
	}
	public static String getClientInfoFromPDNDMap(Map<String, ?> mapPdnd, String name) {
		Object o = mapPdnd.get(PDNDTokenInfo.CLIENT_INFO);
		if(o instanceof Map) {
			Map<?, ?> m = (Map<?, ?>) o;
			return (String) m.get(name);
		}
		return null;
	}
	
	private static String getPatternOrganizationJsonExtract(String path, boolean tokenInfo) {
		String pattern = tokenInfo ? "$.."+TOKEN_INFO_PREFIX_PDND+PDNDTokenInfo.ORGANIZATION_INFO+"." : "$.";
		return pattern + path;
	}
	private static String getPatternClientJsonExtract(String path, boolean tokenInfo) {
		String pattern = tokenInfo ? "$.."+TOKEN_INFO_PREFIX_PDND+PDNDTokenInfo.CLIENT_INFO+"." : "$.";
		return pattern + path;
	}
	private static Logger getLogger() {
		return LoggerWrapperFactory.getLogger(PDNDTokenInfo.class);
	}
	
	
	public String getClientId(Logger log) throws ProtocolException {
		return readClientIdFromPDNDMapTokenInfo(log, this.pdnd);
	}
	public String getClientId() throws ProtocolException {
		return readClientIdFromPDNDMapTokenInfo(getLogger(), this.pdnd);
	}
	public static String readClientIdFromPDNDMapTokenInfo(Logger log, Map<String, PDNDTokenInfoDetails> mapPdnd) throws ProtocolException {
		return readClientId(log, getClientJsonDetailFromPDNDMapTokenInfo(mapPdnd), false);
	}
	public static String readClientIdFromPDNDMap(Map<String, ?> mapPdnd) {
		return getClientInfoFromPDNDMap(mapPdnd, "id");
	}
	public static String readClientIdFromTokenInfo(Logger log, String tokenInfo) throws ProtocolException {
		return readClientId(log, tokenInfo, true);
	}
	private static String readClientId(Logger log, String json, boolean tokenInfo) throws ProtocolException {
		String pattern =  getPatternClientJsonExtract("id", tokenInfo);
		try {
			return JsonPathExpressionEngine.extractAndConvertResultAsString(json, pattern, log);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	public String getClientConsumerId(Logger log) throws ProtocolException {
		return readClientConsumerIdFromPDNDMapTokenInfo(log, this.pdnd);
	}
	public String getClientConsumerId() throws ProtocolException {
		return readClientConsumerIdFromPDNDMapTokenInfo(getLogger(), this.pdnd);
	}
	public static String readClientConsumerIdFromPDNDMapTokenInfo(Logger log, Map<String, PDNDTokenInfoDetails> mapPdnd) throws ProtocolException {
		return readClientConsumerId(log, getClientJsonDetailFromPDNDMapTokenInfo(mapPdnd), false);
	}
	public static String readClientConsumerIdFromPDNDMap(Map<String, ?> mapPdnd) {
		return getClientInfoFromPDNDMap(mapPdnd, "consumerId");
	}
	public static String readClientConsumerIdFromTokenInfo(Logger log, String tokenInfo) throws ProtocolException {
		return readClientConsumerId(log, tokenInfo, true);
	}
	private static String readClientConsumerId(Logger log, String json, boolean tokenInfo) throws ProtocolException {
		String pattern =  getPatternClientJsonExtract("consumerId", tokenInfo);
		try {
			return JsonPathExpressionEngine.extractAndConvertResultAsString(json, pattern, log);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}

	public String getOrganizationId(Logger log) throws ProtocolException {
		return readOrganizationIdFromPDNDMapTokenInfo(log, this.pdnd);
	}
	public String getOrganizationId() throws ProtocolException {
		return readOrganizationIdFromPDNDMapTokenInfo(getLogger(), this.pdnd);
	}
	public static String readOrganizationIdFromPDNDMapTokenInfo(Logger log, Map<String, PDNDTokenInfoDetails> mapPdnd) throws ProtocolException {
		return readOrganizationId(log, getOrganizationJsonDetailFromPDNDMapTokenInfo(mapPdnd), false);
	}
	public static String readOrganizationIdFromPDNDMap(Map<String, ?> mapPdnd) {
		return getOrganizationInfoFromPDNDMap(mapPdnd, "id");
	}
	public static String readOrganizationIdFromTokenInfo(Logger log, String tokenInfo) throws ProtocolException {
		return readOrganizationId(log, tokenInfo, true);
	}
	private static String readOrganizationId(Logger log, String json, boolean tokenInfo) throws ProtocolException {
		String pattern =  getPatternOrganizationJsonExtract("id", tokenInfo);
		try {
			return JsonPathExpressionEngine.extractAndConvertResultAsString(json, pattern, log);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	public String getOrganizationName(Logger log) throws ProtocolException {
		return readOrganizationNameFromPDNDMapTokenInfo(log, this.pdnd);
	}
	public String getOrganizationName() throws ProtocolException {
		return readOrganizationNameFromPDNDMapTokenInfo(getLogger(), this.pdnd);
	}
	public static String readOrganizationNameFromPDNDMapTokenInfo(Logger log, Map<String, PDNDTokenInfoDetails> mapPdnd) throws ProtocolException {
		return readOrganizationName(log, getOrganizationJsonDetailFromPDNDMapTokenInfo(mapPdnd), false);
	}
	public static String readOrganizationNameFromPDNDMap(Map<String, ?> mapPdnd) {
		return getOrganizationInfoFromPDNDMap(mapPdnd, "name");
	}
	public static String readOrganizationNameFromTokenInfo(Logger log, String tokenInfo) throws ProtocolException {
		return readOrganizationName(log, tokenInfo, true);
	}
	private static String readOrganizationName(Logger log, String json, boolean tokenInfo) throws ProtocolException {
		String pattern =  getPatternOrganizationJsonExtract("name", tokenInfo);
		try {
			return JsonPathExpressionEngine.extractAndConvertResultAsString(json, pattern, log);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	public String getOrganizationCategory(Logger log) throws ProtocolException {
		return readOrganizationCategoryFromPDNDMapTokenInfo(log, this.pdnd);
	}
	public String getOrganizationCategory() throws ProtocolException {
		return readOrganizationCategoryFromPDNDMapTokenInfo(getLogger(), this.pdnd);
	}
	public static String readOrganizationCategoryFromPDNDMapTokenInfo(Logger log, Map<String, PDNDTokenInfoDetails> mapPdnd) throws ProtocolException {
		return readOrganizationCategory(log, getOrganizationJsonDetailFromPDNDMapTokenInfo(mapPdnd), false);
	}
	public static String readOrganizationCategoryFromPDNDMap(Map<String, ?> mapPdnd) {
		return getOrganizationInfoFromPDNDMap(mapPdnd, "category");
	}
	public static String readOrganizationCategoryFromTokenInfo(Logger log, String tokenInfo) throws ProtocolException {
		return readOrganizationCategory(log, tokenInfo, true);
	}
	private static String readOrganizationCategory(Logger log, String json, boolean tokenInfo) throws ProtocolException {
		String pattern =  getPatternOrganizationJsonExtract("category", tokenInfo);
		try {
			return JsonPathExpressionEngine.extractAndConvertResultAsString(json, pattern, log);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	public String getOrganizationExternalOrigin(Logger log) throws ProtocolException {
		return readOrganizationExternalOriginFromPDNDMapTokenInfo(log, this.pdnd);
	}
	public String getOrganizationExternalOrigin() throws ProtocolException {
		return readOrganizationExternalOriginFromPDNDMapTokenInfo(getLogger(), this.pdnd);
	}
	public static String readOrganizationExternalOriginFromPDNDMapTokenInfo(Logger log, Map<String, PDNDTokenInfoDetails> mapPdnd) throws ProtocolException {
		return readOrganizationExternalOrigin(log, getOrganizationJsonDetailFromPDNDMapTokenInfo(mapPdnd), false);
	}
	public static String readOrganizationExternalOriginFromPDNDMap(Map<String, ?> mapPdnd) {
		return getOrganizationInfoFromPDNDMap(mapPdnd, "externalId.origin");
	}
	public static String readOrganizationExternalOriginFromTokenInfo(Logger log, String tokenInfo) throws ProtocolException {
		return readOrganizationExternalOrigin(log, tokenInfo, true);
	}
	private static String readOrganizationExternalOrigin(Logger log, String json, boolean tokenInfo) throws ProtocolException {
		String pattern =  getPatternOrganizationJsonExtract(tokenInfo ? "['externalId.origin']" : "externalId.origin", tokenInfo);
		try {
			return JsonPathExpressionEngine.extractAndConvertResultAsString(json, pattern, log);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	public String getOrganizationExternalId(Logger log) throws ProtocolException {
		return readOrganizationExternalIdFromPDNDMapTokenInfo(log, this.pdnd);
	}
	public String getOrganizationExternalId() throws ProtocolException {
		return readOrganizationExternalIdFromPDNDMapTokenInfo(getLogger(), this.pdnd);
	}
	public static String readOrganizationExternalIdFromPDNDMapTokenInfo(Logger log, Map<String, PDNDTokenInfoDetails> mapPdnd) throws ProtocolException {
		return readOrganizationExternalId(log, getOrganizationJsonDetailFromPDNDMapTokenInfo(mapPdnd), false);
	}
	public static String readOrganizationExternalIdFromPDNDMap(Map<String, ?> mapPdnd) {
		return getOrganizationInfoFromPDNDMap(mapPdnd, "externalId.id");
	}
	public static String readOrganizationExternalIdFromTokenInfo(Logger log, String tokenInfo) throws ProtocolException {
		return readOrganizationExternalId(log, tokenInfo, true);
	}
	private static String readOrganizationExternalId(Logger log, String json, boolean tokenInfo) throws ProtocolException {
		String pattern =  getPatternOrganizationJsonExtract(tokenInfo ? "['externalId.id']" : "externalId.id", tokenInfo);
		try {
			return JsonPathExpressionEngine.extractAndConvertResultAsString(json, pattern, log);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
}
