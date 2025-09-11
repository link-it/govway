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
package org.openspcoop2.protocol.sdk;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.utils.LoggerWrapperFactory;
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
	
	private static Map<?, ?> getMapOrganization(Map<String, ?> mapPdnd) {
		return getMap(mapPdnd, ORGANIZATION_INFO);
	}
	private static Map<?, ?> getMapClient(Map<String, ?> mapPdnd) {
		return getMap(mapPdnd, CLIENT_INFO);
	}
	private static Map<?, ?> getMap(Map<String, ?> mapPdnd, String tipoInfo) {
		if(mapPdnd!=null && !mapPdnd.isEmpty()) {
			Object oTipoInfo = mapPdnd.get(tipoInfo);
			if(oTipoInfo instanceof Map<?, ?>) {
				return (Map<?, ?>) oTipoInfo;
			}
		}
		return new HashMap<>();
	}
	
	private static String getMapOrganizationAsJson(Map<String, ?> mapPdnd) {
		return getMapAsJson(mapPdnd, ORGANIZATION_INFO);
	}
	private static String getMapClientAsJson(Map<String, ?> mapPdnd) {
		return getMapAsJson(mapPdnd, CLIENT_INFO);
	}
	private static String getMapAsJson(Map<String, ?> mapPdnd, String tipoInfo) {
		Map<?, ?> mInternal = getMap(mapPdnd, tipoInfo);
		StringBuilder sb = new StringBuilder();
		if(mInternal!=null && !mInternal.isEmpty()) {
			fillMapAsJson(mInternal, sb);
		}
		return "{" + sb.toString() + "}";
	}
	private static void fillMapAsJson(Map<?, ?> mInternal, StringBuilder sb) {
		for (Map.Entry<?,?> entry : mInternal.entrySet()) {
			Object o = entry.getValue();
			if(o instanceof String) {
				if(sb.length()>0) {
					sb.append(",");	
				}
				sb.append("\"").append(entry.getKey()).append("\": \"").append(o).append("\"");
			}
		}
	}
	private static String getFieldNameFromPattern(String pattern) {
		if(pattern.startsWith("$..")) {
			return pattern.replace("$..", "");
		}
		else if(pattern.startsWith("$.")) {
			return pattern.replace("$.", "");
		}
		return pattern;
	}
	
	private static String getTokenInfoPatternOrganizationJsonPathPrefix() {
		return "$.."+TOKEN_INFO_PREFIX_PDND+PDNDTokenInfo.ORGANIZATION_INFO+".";
	}
	private static String getTokenInfoPatternClientJsonPathPrefix() {
		return "$.."+TOKEN_INFO_PREFIX_PDND+PDNDTokenInfo.CLIENT_INFO+".";
	}

	private static Logger getLogger() {
		return LoggerWrapperFactory.getLogger(PDNDTokenInfo.class);
	}
	
	
	// **** Clients ****
		
	// id
	
	public String getClientId(Logger log) throws ProtocolException {
		return readClientIdFromPDNDMapTokenInfo(log, this.pdnd);
	}
	public String getClientId() throws ProtocolException {
		return readClientIdFromPDNDMapTokenInfo(getLogger(), this.pdnd);
	}
	public static String readClientIdFromPDNDMapTokenInfo(Logger log, Map<String, PDNDTokenInfoDetails> mapPdnd) throws ProtocolException {
		return readClientId(log, getClientJsonDetailFromPDNDMapTokenInfo(mapPdnd), false);
	}
	public static String readClientIdFromTokenInfo(Logger log, String tokenInfo) throws ProtocolException {
		return readClientId(log, tokenInfo, true);
	}
	public static String readClientIdFromJson(Logger log, String json) throws ProtocolException {
		return readClientId(log, json, false);
	}
	private static String readClientId(Logger log, String json, boolean tokenInfo) throws ProtocolException {
		ModIPDNDClientConfig config = ModIPropertiesUtils.getAPIPDNDClientConfig(json, log);
		if(tokenInfo) {
			ModIPDNDClientConfig cloned = config.cloneNewInstance();
			cloned.setOverridePrefixJsonPath(getTokenInfoPatternClientJsonPathPrefix());
			return cloned.getId();
		}
		else {
			return config.getId();
		}
	}
	
	public static String readClientIdFromPDNDMap(Map<String, ?> mapPdnd) {
		try {
			ModIPDNDClientConfig config = ModIPropertiesUtils.getAPIPDNDClientConfig(getMapClientAsJson(mapPdnd));
			return (String) getMapClient(mapPdnd).get(getFieldNameFromPattern(config.getPatternId()));
		}	
		catch(Exception e) {
			// ignore
		}
		return null;
	}
	
	
	// consumerId
	
	public String getClientConsumerId(Logger log) throws ProtocolException {
		return readClientConsumerIdFromPDNDMapTokenInfo(log, this.pdnd);
	}
	public String getClientConsumerId() throws ProtocolException {
		return readClientConsumerIdFromPDNDMapTokenInfo(getLogger(), this.pdnd);
	}
	public static String readClientConsumerIdFromPDNDMapTokenInfo(Logger log, Map<String, PDNDTokenInfoDetails> mapPdnd) throws ProtocolException {
		return readClientConsumerId(log, getClientJsonDetailFromPDNDMapTokenInfo(mapPdnd), false);
	}
	public static String readClientConsumerIdFromTokenInfo(Logger log, String tokenInfo) throws ProtocolException {
		return readClientConsumerId(log, tokenInfo, true);
	}
	public static String readClientConsumerIdFromJson(Logger log, String json) throws ProtocolException {
		return readClientConsumerId(log, json, false);
	}
	private static String readClientConsumerId(Logger log, String json, boolean tokenInfo) throws ProtocolException {
		ModIPDNDClientConfig config = ModIPropertiesUtils.getAPIPDNDClientConfig(json, log);
		if(tokenInfo) {
			ModIPDNDClientConfig cloned = config.cloneNewInstance();
			cloned.setOverridePrefixJsonPath(getTokenInfoPatternClientJsonPathPrefix());
			return cloned.getOrganization();
		}
		else {
			return config.getOrganization();
		}
	}
	
	public static String readClientConsumerIdFromPDNDMap(Map<String, ?> mapPdnd) {
		try {
			ModIPDNDClientConfig config = ModIPropertiesUtils.getAPIPDNDClientConfig(getMapClientAsJson(mapPdnd));
			return (String) getMapClient(mapPdnd).get(getFieldNameFromPattern(config.getPatternOrganization()));
		}	
		catch(Exception e) {
			// ignore
		}
		return null;
	}
	
	
	
	// name
	
	public String getClientName(Logger log) throws ProtocolException {
		return readClientNameFromPDNDMapTokenInfo(log, this.pdnd);
	}
	public String getClientName() throws ProtocolException {
		return readClientNameFromPDNDMapTokenInfo(getLogger(), this.pdnd);
	}
	public static String readClientNameFromPDNDMapTokenInfo(Logger log, Map<String, PDNDTokenInfoDetails> mapPdnd) throws ProtocolException {
		return readClientName(log, getClientJsonDetailFromPDNDMapTokenInfo(mapPdnd), false);
	}
	public static String readClientNameFromTokenInfo(Logger log, String tokenInfo) throws ProtocolException {
		return readClientName(log, tokenInfo, true);
	}
	public static String readClientNameFromJson(Logger log, String json) throws ProtocolException {
		return readClientName(log, json, false);
	}
	private static String readClientName(Logger log, String json, boolean tokenInfo) throws ProtocolException {
		ModIPDNDClientConfig config = ModIPropertiesUtils.getAPIPDNDClientConfig(json, log);
		if(tokenInfo) {
			ModIPDNDClientConfig cloned = config.cloneNewInstance();
			cloned.setOverridePrefixJsonPath(getTokenInfoPatternClientJsonPathPrefix());
			return cloned.getName();
		}
		else {
			return config.getName();
		}
	}
	
	public static String readClientNameFromPDNDMap(Map<String, ?> mapPdnd) {
		try {
			ModIPDNDClientConfig config = ModIPropertiesUtils.getAPIPDNDClientConfig(getMapClientAsJson(mapPdnd));
			return (String) getMapClient(mapPdnd).get(getFieldNameFromPattern(config.getPatternName()));
		}	
		catch(Exception e) {
			// ignore
		}
		return null;
	}
	
	
	
	// description
	
	public String getClientDescription(Logger log) throws ProtocolException {
		return readClientDescriptionFromPDNDMapTokenInfo(log, this.pdnd);
	}
	public String getClientDescription() throws ProtocolException {
		return readClientDescriptionFromPDNDMapTokenInfo(getLogger(), this.pdnd);
	}
	public static String readClientDescriptionFromPDNDMapTokenInfo(Logger log, Map<String, PDNDTokenInfoDetails> mapPdnd) throws ProtocolException {
		return readClientDescription(log, getClientJsonDetailFromPDNDMapTokenInfo(mapPdnd), false);
	}
	public static String readClientDescriptionFromTokenInfo(Logger log, String tokenInfo) throws ProtocolException {
		return readClientDescription(log, tokenInfo, true);
	}
	public static String readClientDescriptionFromJson(Logger log, String json) throws ProtocolException {
		return readClientDescription(log, json, false);
	}
	private static String readClientDescription(Logger log, String json, boolean tokenInfo) throws ProtocolException {
		ModIPDNDClientConfig config = ModIPropertiesUtils.getAPIPDNDClientConfig(json, log);
		if(tokenInfo) {
			ModIPDNDClientConfig cloned = config.cloneNewInstance();
			cloned.setOverridePrefixJsonPath(getTokenInfoPatternClientJsonPathPrefix());
			return cloned.getDescription();
		}
		else {
			return config.getDescription();
		}
	}
	
	public static String readClientDescriptionFromPDNDMap(Map<String, ?> mapPdnd) {
		try {
			ModIPDNDClientConfig config = ModIPropertiesUtils.getAPIPDNDClientConfig(getMapClientAsJson(mapPdnd));
			return (String) getMapClient(mapPdnd).get(getFieldNameFromPattern(config.getPatternDescription()));
		}	
		catch(Exception e) {
			// ignore
		}
		return null;
	}
	
	
	
	
	
	// **** Organization ****
	
	// Id
	
	public String getOrganizationId(Logger log) throws ProtocolException {
		return readOrganizationIdFromPDNDMapTokenInfo(log, this.pdnd);
	}
	public String getOrganizationId() throws ProtocolException {
		return readOrganizationIdFromPDNDMapTokenInfo(getLogger(), this.pdnd);
	}
	public static String readOrganizationIdFromPDNDMapTokenInfo(Logger log, Map<String, PDNDTokenInfoDetails> mapPdnd) throws ProtocolException {
		return readOrganizationId(log, getOrganizationJsonDetailFromPDNDMapTokenInfo(mapPdnd), false);
	}
	public static String readOrganizationIdFromTokenInfo(Logger log, String tokenInfo) throws ProtocolException {
		return readOrganizationId(log, tokenInfo, true);
	}
	public static String readOrganizationIdFromJson(Logger log, String json) throws ProtocolException {
		return readOrganizationId(log, json, false);
	}
	private static String readOrganizationId(Logger log, String json, boolean tokenInfo) throws ProtocolException {
		ModIPDNDOrganizationConfig config = ModIPropertiesUtils.getAPIPDNDOrganizationConfig(json, log);
		if(tokenInfo) {
			ModIPDNDOrganizationConfig cloned = config.cloneNewInstance();
			cloned.setOverridePrefixJsonPath(getTokenInfoPatternOrganizationJsonPathPrefix());
			return cloned.getId();
		}
		else {
			return config.getId();
		}
	}

	public static String readOrganizationIdFromPDNDMap(Map<String, ?> mapPdnd) {
		try {
			ModIPDNDOrganizationConfig config = ModIPropertiesUtils.getAPIPDNDOrganizationConfig(getMapOrganizationAsJson(mapPdnd));
			return (String) getMapOrganization(mapPdnd).get(getFieldNameFromPattern(config.getPatternId()));
		}	
		catch(Exception e) {
			// ignore
		}
		return null;
	}
	
	
	// name
	
	public String getOrganizationName(Logger log) throws ProtocolException {
		return readOrganizationNameFromPDNDMapTokenInfo(log, this.pdnd);
	}
	public String getOrganizationName() throws ProtocolException {
		return readOrganizationNameFromPDNDMapTokenInfo(getLogger(), this.pdnd);
	}
	public static String readOrganizationNameFromPDNDMapTokenInfo(Logger log, Map<String, PDNDTokenInfoDetails> mapPdnd) throws ProtocolException {
		return readOrganizationName(log, getOrganizationJsonDetailFromPDNDMapTokenInfo(mapPdnd), false);
	}
	public static String readOrganizationNameFromTokenInfo(Logger log, String tokenInfo) throws ProtocolException {
		return readOrganizationName(log, tokenInfo, true);
	}
	public static String readOrganizationNameFromJson(Logger log, String json) throws ProtocolException {
		return readOrganizationName(log, json, false);
	}
	private static String readOrganizationName(Logger log, String json, boolean tokenInfo) throws ProtocolException {
		ModIPDNDOrganizationConfig config = ModIPropertiesUtils.getAPIPDNDOrganizationConfig(json, log);
		if(tokenInfo) {
			ModIPDNDOrganizationConfig cloned = config.cloneNewInstance();
			cloned.setOverridePrefixJsonPath(getTokenInfoPatternOrganizationJsonPathPrefix());
			return cloned.getName();
		}
		else {
			return config.getName();
		}
	}
	
	public static String readOrganizationNameFromPDNDMap(Map<String, ?> mapPdnd) {
		try {
			ModIPDNDOrganizationConfig config = ModIPropertiesUtils.getAPIPDNDOrganizationConfig(getMapOrganizationAsJson(mapPdnd));
			return (String) getMapOrganization(mapPdnd).get(getFieldNameFromPattern(config.getPatternName()));
		}	
		catch(Exception e) {
			// ignore
		}
		return null;
	}
	
	
	// category
	
	public String getOrganizationCategory(Logger log) throws ProtocolException {
		return readOrganizationCategoryFromPDNDMapTokenInfo(log, this.pdnd);
	}
	public String getOrganizationCategory() throws ProtocolException {
		return readOrganizationCategoryFromPDNDMapTokenInfo(getLogger(), this.pdnd);
	}
	public static String readOrganizationCategoryFromPDNDMapTokenInfo(Logger log, Map<String, PDNDTokenInfoDetails> mapPdnd) throws ProtocolException {
		return readOrganizationCategory(log, getOrganizationJsonDetailFromPDNDMapTokenInfo(mapPdnd), false);
	}
	public static String readOrganizationCategoryFromTokenInfo(Logger log, String tokenInfo) throws ProtocolException {
		return readOrganizationCategory(log, tokenInfo, true);
	}
	public static String readOrganizationCategoryFromJson(Logger log, String json) throws ProtocolException {
		return readOrganizationCategory(log, json, false);
	}
	private static String readOrganizationCategory(Logger log, String json, boolean tokenInfo) throws ProtocolException {
		ModIPDNDOrganizationConfig config = ModIPropertiesUtils.getAPIPDNDOrganizationConfig(json, log);
		if(tokenInfo) {
			ModIPDNDOrganizationConfig cloned = config.cloneNewInstance();
			cloned.setOverridePrefixJsonPath(getTokenInfoPatternOrganizationJsonPathPrefix());
			return cloned.getCategory();
		}
		else {
			return config.getCategory();
		}
	}
	
	public static String readOrganizationCategoryFromPDNDMap(Map<String, ?> mapPdnd) {
		try {
			ModIPDNDOrganizationConfig config = ModIPropertiesUtils.getAPIPDNDOrganizationConfig(getMapOrganizationAsJson(mapPdnd));
			return (String) getMapOrganization(mapPdnd).get(getFieldNameFromPattern(config.getPatternCategory()));
		}	
		catch(Exception e) {
			// ignore
		}
		return null;
	}
	
	
	// subunit
	
	public String getOrganizationSubUnit(Logger log) throws ProtocolException {
		return readOrganizationSubUnitFromPDNDMapTokenInfo(log, this.pdnd);
	}
	public String getOrganizationSubUnit() throws ProtocolException {
		return readOrganizationSubUnitFromPDNDMapTokenInfo(getLogger(), this.pdnd);
	}
	public static String readOrganizationSubUnitFromPDNDMapTokenInfo(Logger log, Map<String, PDNDTokenInfoDetails> mapPdnd) throws ProtocolException {
		return readOrganizationSubUnit(log, getOrganizationJsonDetailFromPDNDMapTokenInfo(mapPdnd), false);
	}
	public static String readOrganizationSubUnitFromTokenInfo(Logger log, String tokenInfo) throws ProtocolException {
		return readOrganizationSubUnit(log, tokenInfo, true);
	}
	public static String readOrganizationSubUnitFromJson(Logger log, String json) throws ProtocolException {
		return readOrganizationSubUnit(log, json, false);
	}
	private static String readOrganizationSubUnit(Logger log, String json, boolean tokenInfo) throws ProtocolException {
		ModIPDNDOrganizationConfig config = ModIPropertiesUtils.getAPIPDNDOrganizationConfig(json, log);
		if(tokenInfo) {
			ModIPDNDOrganizationConfig cloned = config.cloneNewInstance();
			cloned.setOverridePrefixJsonPath(getTokenInfoPatternOrganizationJsonPathPrefix());
			return cloned.getSubUnit();
		}
		else {
			return config.getSubUnit();
		}
	}
	
	public static String readOrganizationSubUnitFromPDNDMap(Map<String, ?> mapPdnd) {
		try {
			ModIPDNDOrganizationConfig config = ModIPropertiesUtils.getAPIPDNDOrganizationConfig(getMapOrganizationAsJson(mapPdnd));
			return (String) getMapOrganization(mapPdnd).get(getFieldNameFromPattern(config.getPatternSubUnit()));
		}	
		catch(Exception e) {
			// ignore
		}
		return null;
	}
	
	
	
	// external origin
	
	public String getOrganizationExternalOrigin(Logger log) throws ProtocolException {
		return readOrganizationExternalOriginFromPDNDMapTokenInfo(log, this.pdnd);
	}
	public String getOrganizationExternalOrigin() throws ProtocolException {
		return readOrganizationExternalOriginFromPDNDMapTokenInfo(getLogger(), this.pdnd);
	}
	public static String readOrganizationExternalOriginFromPDNDMapTokenInfo(Logger log, Map<String, PDNDTokenInfoDetails> mapPdnd) throws ProtocolException {
		return readOrganizationExternalOrigin(log, getOrganizationJsonDetailFromPDNDMapTokenInfo(mapPdnd), false);
	}
	public static String readOrganizationExternalOriginFromTokenInfo(Logger log, String tokenInfo) throws ProtocolException {
		return readOrganizationExternalOrigin(log, tokenInfo, true);
	}
	public static String readOrganizationExternalOriginFromJson(Logger log, String json) throws ProtocolException {
		return readOrganizationExternalOrigin(log, json, false);
	}
	private static String readOrganizationExternalOrigin(Logger log, String json, boolean tokenInfo) throws ProtocolException {
		ModIPDNDOrganizationConfig config = ModIPropertiesUtils.getAPIPDNDOrganizationConfig(json, log);
		if(tokenInfo) {
			ModIPDNDOrganizationConfig cloned = config.cloneNewInstance();
			cloned.setOverridePrefixJsonPath(getTokenInfoPatternOrganizationJsonPathPrefix());
			cloned.setOvveridePatternAsConstant(true);
			return cloned.getExternalOrigin();
		}
		else {
			return config.getExternalOrigin();
		}
	}
	
	public static String readOrganizationExternalOriginFromPDNDMap(Map<String, ?> mapPdnd) {
		try {
			ModIPDNDOrganizationConfig config = ModIPropertiesUtils.getAPIPDNDOrganizationConfig(getMapOrganizationAsJson(mapPdnd));
			return (String) getMapOrganization(mapPdnd).get(getFieldNameFromPattern(config.getPatternExternalOrigin()));
		}	
		catch(Exception e) {
			// ignore
		}
		return null;
	}
	
	
	// External Id
	
	public String getOrganizationExternalId(Logger log) throws ProtocolException {
		return readOrganizationExternalIdFromPDNDMapTokenInfo(log, this.pdnd);
	}
	public String getOrganizationExternalId() throws ProtocolException {
		return readOrganizationExternalIdFromPDNDMapTokenInfo(getLogger(), this.pdnd);
	}
	public static String readOrganizationExternalIdFromPDNDMapTokenInfo(Logger log, Map<String, PDNDTokenInfoDetails> mapPdnd) throws ProtocolException {
		return readOrganizationExternalId(log, getOrganizationJsonDetailFromPDNDMapTokenInfo(mapPdnd), false);
	}
	public static String readOrganizationExternalIdFromTokenInfo(Logger log, String tokenInfo) throws ProtocolException {
		return readOrganizationExternalId(log, tokenInfo, true);
	}
	public static String readOrganizationExternalIdFromJson(Logger log, String json) throws ProtocolException {
		return readOrganizationExternalId(log, json, false);
	}
	private static String readOrganizationExternalId(Logger log, String json, boolean tokenInfo) throws ProtocolException {
		ModIPDNDOrganizationConfig config = ModIPropertiesUtils.getAPIPDNDOrganizationConfig(json, log);
		if(tokenInfo) {
			ModIPDNDOrganizationConfig cloned = config.cloneNewInstance();
			cloned.setOverridePrefixJsonPath(getTokenInfoPatternOrganizationJsonPathPrefix());
			cloned.setOvveridePatternAsConstant(true);
			return cloned.getExternalId();
		}
		else {
			return config.getExternalId();
		}
	}
	
	public static String readOrganizationExternalIdFromPDNDMap(Map<String, ?> mapPdnd) {
		try {
			ModIPDNDOrganizationConfig config = ModIPropertiesUtils.getAPIPDNDOrganizationConfig(getMapOrganizationAsJson(mapPdnd));
			return (String) getMapOrganization(mapPdnd).get(getFieldNameFromPattern(config.getPatternExternalId()));
		}	
		catch(Exception e) {
			// ignore
		}
		return null;
	}
}
