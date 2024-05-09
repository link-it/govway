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
package org.openspcoop2.utils.certificate.byok;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.DynamicStringReplace;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.digest.DigestEncoding;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.HexBinaryUtilities;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;

/**
 * BYOKInstance
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BYOKInstance {

	private BYOKConfig config;
	
	private HttpRequest httpRequest;
	
	private byte [] localKey;
	private BYOKLocalConfig localConfigResolved;
	
	private String keyCache;
	
	public BYOKInstance(BYOKConfig config, HttpRequest httpRequest, String keyCache) {
		this.config = config;
		this.httpRequest = httpRequest;
		this.keyCache = keyCache;
	}
	public BYOKInstance(BYOKConfig config, BYOKLocalConfig localConfigResolved, byte[] key, String keyCache) {
		this.config = config;
		this.localConfigResolved = localConfigResolved;
		this.localKey = key;
		this.keyCache = keyCache;
	}
	
	public BYOKConfig getConfig() {
		return this.config;
	}
	
	public BYOKLocalConfig getLocalConfigResolved() {
		return this.localConfigResolved;
	}
	public byte[] getLocalKey() {
		return this.localKey;
	}
	
	public HttpRequest getHttpRequest() {
		return this.httpRequest;
	}
	
	public String getKeyCache() {
		return this.keyCache;
	}
	
	private static final String BYOK_REQUEST_PARAMS_UNDEFINED = "BYOKRequestParams undefined";
	private static final String BYOK_REQUEST_PARAMS_CONFIG_UNDEFINED = "BYOKRequestParams config undefined";

	public static BYOKInstance newInstance(Logger log, BYOKRequestParams requestParams, byte[] key) throws UtilsException {
		if(requestParams==null) {
			throw new UtilsException(BYOK_REQUEST_PARAMS_UNDEFINED);
		}
		if(requestParams.getConfig()==null) {
			throw new UtilsException(BYOK_REQUEST_PARAMS_CONFIG_UNDEFINED);
		}
		if(BYOKEncryptionMode.LOCAL.equals(requestParams.getConfig().getEncryptionMode())) {
			return BYOKInstance.newLocalInstance(log, requestParams, key);
		}
		else {
			return BYOKInstance.newRemoteInstance(log, requestParams, key);
		}
	}
	public static BYOKInstance newInstance(Logger log, Map<String,Object> dynamicMap, BYOKConfig config, Map<String,String> inputMap, String keyCache, byte[] key) throws UtilsException {
		if(config==null) {
			throw new UtilsException(BYOK_REQUEST_PARAMS_CONFIG_UNDEFINED);
		}
		if(BYOKEncryptionMode.LOCAL.equals(config.getEncryptionMode())) {
			return BYOKInstance.newLocalInstance(log, dynamicMap, config, inputMap, keyCache, key);
		}
		else {
			return BYOKInstance.newRemoteInstance(log, dynamicMap, config, inputMap, key);
		}
	}
	
	public static BYOKInstance newRemoteInstance(Logger log, BYOKRequestParams requestParams, byte[] key) throws UtilsException {
		if(requestParams==null) {
			throw new UtilsException(BYOK_REQUEST_PARAMS_UNDEFINED);
		}
		return newRemoteInstance(log, requestParams.getDynamicMap(), requestParams.getConfig(), requestParams.getInputMap(), key);
	}
	public static BYOKInstance newRemoteInstance(Logger log, Map<String,Object> dynamicMap, BYOKConfig config, Map<String,String> inputMap, byte[] key) throws UtilsException {
		HttpRequest httpRequest = buildHttpRequest(log, config, dynamicMap, inputMap, key);
		String keyCache = buildKeyCache(httpRequest);
		return new BYOKInstance(config, httpRequest, keyCache);
	}
	
	public static BYOKInstance newLocalInstance(Logger log, BYOKRequestParams requestParams, byte[] key) throws UtilsException {
		if(requestParams==null) {
			throw new UtilsException(BYOK_REQUEST_PARAMS_UNDEFINED);
		}
		if(requestParams.getKeyIdentity()==null) {
			throw new UtilsException("BYOKRequestParams key identity undefined");
		}
		return newLocalInstance(log, requestParams.getDynamicMap(), requestParams.getConfig(), requestParams.getInputMap(), requestParams.getKeyIdentity(), key);
	}
	public static BYOKInstance newLocalInstance(Logger log, Map<String,Object> dynamicMap, BYOKConfig config, Map<String,String> inputMap, String keyCache, byte[] key) throws UtilsException {
		if(log!=null) {
			 // nop
		}
		BYOKLocalConfig localConfig = buildBYOKLocalConfig(config, dynamicMap, inputMap, null);
		return new BYOKInstance(config, localConfig, key, keyCache);
	}
	
	private static BYOKLocalConfig buildBYOKLocalConfig(BYOKConfig config, Map<String,Object> dynamicMap, Map<String,String> inputMap, byte[] key) throws UtilsException {
		
		BYOKLocalConfig localConfig = new BYOKLocalConfig();
		
		List<BYOKConfigParameter> inputParameters = config.getInputParameters();
		
		localConfig.encryptionEngine = resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
				BYOKCostanti.PROPERTY_SUFFIX_LOCAL_IMPL, config.getLocalConfig().encryptionEngine);
		
		localConfig.keystoreType = config.getLocalConfig().keystoreType;
		localConfig.keystoreHsmType = config.getLocalConfig().keystoreHsmType;
		localConfig.keystorePath = resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
				BYOKCostanti.PROPERTY_SUFFIX_LOCAL_KEYSTORE_PATH, config.getLocalConfig().keystorePath);
		localConfig.keystorePassword = resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
				BYOKCostanti.PROPERTY_SUFFIX_LOCAL_KEYSTORE_PASSWORD, config.getLocalConfig().keystorePassword);
		
		localConfig.keyPath = resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
				BYOKCostanti.PROPERTY_SUFFIX_LOCAL_KEY_PATH, config.getLocalConfig().keyPath);
		localConfig.keyInline = resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
				BYOKCostanti.PROPERTY_SUFFIX_LOCAL_KEY_INLINE, config.getLocalConfig().keyInline);
		localConfig.keyEncoding = resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
				BYOKCostanti.PROPERTY_SUFFIX_LOCAL_KEY_ENCODING, config.getLocalConfig().keyEncoding);
		localConfig.keyAlgorithm = resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
				BYOKCostanti.PROPERTY_SUFFIX_LOCAL_KEY_ALGORITHM, config.getLocalConfig().keyAlgorithm);
		localConfig.keyAlias = resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
				BYOKCostanti.PROPERTY_SUFFIX_LOCAL_KEY_ALIAS, config.getLocalConfig().keyAlias);
		localConfig.keyPassword = resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
				BYOKCostanti.PROPERTY_SUFFIX_LOCAL_KEY_PASSWORD, config.getLocalConfig().keyPassword);
		localConfig.keyId = resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
				BYOKCostanti.PROPERTY_SUFFIX_LOCAL_KEY_ID, config.getLocalConfig().keyId);
		localConfig.keyWrap = config.getLocalConfig().keyWrap;
		
		localConfig.publicKeyPath = resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
				BYOKCostanti.PROPERTY_SUFFIX_LOCAL_PUBLIC_KEY_PATH, config.getLocalConfig().publicKeyPath);
		localConfig.publicKeyInline = resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
				BYOKCostanti.PROPERTY_SUFFIX_LOCAL_PUBLIC_KEY_INLINE, config.getLocalConfig().publicKeyInline);
		localConfig.publicKeyEncoding = resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
				BYOKCostanti.PROPERTY_SUFFIX_LOCAL_PUBLIC_KEY_ENCODING, config.getLocalConfig().publicKeyEncoding);
		
		localConfig.contentAlgorithm = resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
				BYOKCostanti.PROPERTY_SUFFIX_LOCAL_CONTENT_ALGORITHM, config.getLocalConfig().contentAlgorithm);
		
		localConfig.javaEncoding = resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
				BYOKCostanti.PROPERTY_SUFFIX_LOCAL_JAVA_ENCODING, config.getLocalConfig().javaEncoding);

		localConfig.joseIncludeCert = config.getLocalConfig().joseIncludeCert;
		localConfig.joseIncludePublicKey = config.getLocalConfig().joseIncludePublicKey;
		localConfig.joseIncludeKeyId = config.getLocalConfig().joseIncludeKeyId;
		localConfig.joseIncludeCertSha1 = config.getLocalConfig().joseIncludeCertSha1;
		localConfig.joseIncludeCertSha256 = config.getLocalConfig().joseIncludeCertSha256;
		
		return localConfig;
	}
	
	private static HttpRequest buildHttpRequest(Logger log, BYOKConfig configParam, Map<String,Object> dynamicMap, Map<String,String> inputMap, byte[] key) throws UtilsException {
		
		HttpRequest http = new HttpRequest();
		
		BYOKRemoteConfig config = configParam.getRemoteConfig();
		List<BYOKConfigParameter> inputParameters = configParam.getInputParameters();
		
		http.setUrl(resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
			BYOKCostanti.PROPERTY_SUFFIX_HTTP_ENDPOINT, config.getHttpEndpoint()));
		String m = null;
		try {
			m = resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
					BYOKCostanti.PROPERTY_SUFFIX_HTTP_METHOD, config.getHttpMethod());
			if(m==null) {
				throw new UtilsException("Undefined");
			}
			http.setMethod(HttpRequestMethod.valueOf(m.toUpperCase()));
		}catch(Exception e) {
			throw new UtilsException("Invalid request http method ("+config.getHttpEndpoint()+"; resolved:"+m+"): "+e.getMessage(),e);
		}
		
		if(config.getHttpConnectionTimeout()!=null) {
			http.setConnectTimeout(config.getHttpConnectionTimeout());
		}
		if(config.getHttpReadTimeout()!=null) {
			http.setConnectTimeout(config.getHttpReadTimeout());
		}
		
		setHttpHeader(config, dynamicMap, 
				inputParameters, inputMap, key, http);
		
		setPayload(config, dynamicMap, 
				inputParameters, inputMap, key, http);
		
		setHttps(log, config, dynamicMap,
				inputParameters, inputMap, key, http);
		
		return http;
	}
	private static void setHttpHeader(BYOKRemoteConfig config, Map<String,Object> dynamicMap, 
			List<BYOKConfigParameter> inputParameters, Map<String,String> inputMap, byte[] key,
			HttpRequest http) throws UtilsException {
		if(config.getHttpHeaders()!=null && !config.getHttpHeaders().isEmpty()) {
			for(Map.Entry<String,String> entry : config.getHttpHeaders().entrySet()) {
				String nome = resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
						BYOKCostanti.PROPERTY_SUFFIX_HTTP_HEADER+"<name>", entry.getKey());
				String valore = resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
						BYOKCostanti.PROPERTY_SUFFIX_HTTP_HEADER+entry.getKey(), entry.getValue());
				if(nome!=null && valore!=null) {
					http.addHeader(nome, valore);
				}
			}
		}
		if(config.getHttpUsername()!=null) {
			http.setUsername(resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
						BYOKCostanti.PROPERTY_SUFFIX_HTTP_USERNAME, config.getHttpUsername()));
		}
		if(config.getHttpPassword()!=null) {
			http.setPassword(resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
						BYOKCostanti.PROPERTY_SUFFIX_HTTP_PASSWORD, config.getHttpPassword()));
		}
	}
	private static void setPayload(BYOKRemoteConfig config, Map<String,Object> dynamicMap, 
			List<BYOKConfigParameter> inputParameters, Map<String,String> inputMap, byte[] key,
			HttpRequest http) throws UtilsException {
		if(config.getHttpPayloadInLine()!=null) {
			String content = resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
					BYOKCostanti.PROPERTY_SUFFIX_HTTP_PAYLOAD_INLINE, config.getHttpPayloadInLine());
			if(content!=null) {
				http.setContent(content.getBytes());
			}
		}
		else if(config.getHttpPayloadPath()!=null) {
			byte[]fileContent = null;
			try {
				fileContent = FileSystemUtilities.readBytesFromFile(config.getHttpPayloadPath());
			}catch(Exception e) {
				throw new UtilsException("Invalid request payload file ("+config.getHttpPayloadPath()+"): "+e.getMessage(),e);
			}
			http.setContent(resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
					BYOKCostanti.PROPERTY_SUFFIX_HTTP_PAYLOAD_PATH, fileContent));
		}
		
		if(http.getContent()!=null) {
			String ct = http.getHeaderFirstValue(HttpConstants.CONTENT_TYPE);
			if(ct==null || StringUtils.isEmpty(ct)) {
				http.setContentType(HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM);
			}
		}
	}
	
	private static void setHttps(Logger log, BYOKRemoteConfig config, Map<String,Object> dynamicMap, 
			List<BYOKConfigParameter> inputParameters, Map<String,String> inputMap, byte[] key,
			HttpRequest http) throws UtilsException {
		
		if(log!=null) {
			// nop
		}
		
		if(config.isHttps()) {
			
			http.setHostnameVerifier(config.isHttpsHostnameVerifier());
			
			setHttpsServer(config, dynamicMap, inputParameters, inputMap, key, http);
			
			setHttpsClient(config, dynamicMap, inputParameters, inputMap, key, http);
			
		}
		
	}
	private static void setHttpsServer(BYOKRemoteConfig config, Map<String,Object> dynamicMap, 
			List<BYOKConfigParameter> inputParameters, Map<String,String> inputMap, byte[] key,
			HttpRequest http) throws UtilsException {
		if(config.isHttpsServerAuth()) {
			
			if(config.getHttpsServerAuthTrustStorePath()!=null) {
				http.setTrustStorePath(resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
						BYOKCostanti.PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_SERVER_TRUSTSTORE_PATH, config.getHttpsServerAuthTrustStorePath()));
			}
			if(config.getHttpsServerAuthTrustStoreType()!=null) {
				http.setTrustStoreType(resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
						BYOKCostanti.PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_SERVER_TRUSTSTORE_TYPE, config.getHttpsServerAuthTrustStoreType()));
			}
			if(config.getHttpsServerAuthTrustStorePassword()!=null) {
				http.setTrustStorePassword(resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
						BYOKCostanti.PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_SERVER_TRUSTSTORE_PASSWORD, config.getHttpsServerAuthTrustStorePassword()));
			}
			if(config.getHttpsServerAuthTrustStoreCrls()!=null) {
				http.setCrlPath(resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
						BYOKCostanti.PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_SERVER_CRLS, config.getHttpsServerAuthTrustStoreCrls()));
			}
			if(config.getHttpsServerAuthTrustStoreOcspPolicy()!=null) {
				http.setOcspPolicy(resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
						BYOKCostanti.PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_SERVER_OCSP_POLICY, config.getHttpsServerAuthTrustStoreOcspPolicy()));
			}
			
		}
		else {
			http.setTrustAllCerts(true);
		}
	}
	private static void setHttpsClient(BYOKRemoteConfig config, Map<String,Object> dynamicMap, 
			List<BYOKConfigParameter> inputParameters, Map<String,String> inputMap, byte[] key,
			HttpRequest http) throws UtilsException {
		if(config.isHttpsClientAuth()) {
			
			if(config.getHttpsClientAuthKeyStorePath()!=null) {
				http.setKeyStorePath(resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
						BYOKCostanti.PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_CLIENT_KEYSTORE_PATH, config.getHttpsClientAuthKeyStorePath()));
			}
			if(config.getHttpsClientAuthKeyStoreType()!=null) {
				http.setKeyStoreType(resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
						BYOKCostanti.PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_CLIENT_KEYSTORE_TYPE, config.getHttpsClientAuthKeyStoreType()));
			}
			if(config.getHttpsClientAuthKeyStorePassword()!=null) {
				http.setKeyStorePassword(resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
						BYOKCostanti.PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_CLIENT_KEYSTORE_PASSWORD, config.getHttpsClientAuthKeyStorePassword()));
			}
			
			
			if(config.getHttpsClientAuthKeyAlias()!=null) {
				http.setKeyAlias(resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
						BYOKCostanti.PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_CLIENT_KEY_ALIAS, config.getHttpsClientAuthKeyAlias()));
			}
			if(config.getHttpsClientAuthKeyPassword()!=null) {
				http.setKeyPassword(resolveKsmConstants(dynamicMap, inputParameters, inputMap, key,
						BYOKCostanti.PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_CLIENT_KEY_PASSWORD, config.getHttpsClientAuthKeyPassword()));
			}
		}
	}
	
	private static String buildKeyCache(HttpRequest httpRequest) throws UtilsException {
		StringBuilder sb = new StringBuilder();
		if(httpRequest!=null) {
			
			sb.append(httpRequest.getMethod()).append("_").append(httpRequest.getUrl());
		
			addKeyCacheHttpHeader(httpRequest, sb);
			
			if(httpRequest.getContent()!=null && httpRequest.getContent().length>0) {
				String digestAlgorithm = "SHA256";
				String digestAuditValue = org.openspcoop2.utils.digest.DigestUtils.getDigestValue(httpRequest.getContent(), digestAlgorithm, DigestEncoding.HEX, 
						true); // se rfc3230 true aggiunge prefisso algoritmo=
				sb.append("_");
				sb.append(digestAuditValue);
			}
		}
		return sb.toString();
	}
	private static void addKeyCacheHttpHeader(HttpRequest httpRequest, StringBuilder sb) {
		if(httpRequest.getHeadersValues()!=null && !httpRequest.getHeadersValues().isEmpty()) {
			for(String name : httpRequest.getHeadersValues().keySet()) {
				List<String> values = httpRequest.getHeadersValues().get(name);
				if(values!=null && !values.isEmpty()) {
					for (String v : values) {
						sb.append("_");
						sb.append(name).append(":").append(v);
					}
				}
			}
		}
	}
	
	private static byte[] resolveKsmConstants(Map<String,Object> dynamicMap, List<BYOKConfigParameter> inputParameters,
			Map<String,String> inputMap, byte[] key,
			String name, byte[] value) throws UtilsException {
		byte[] returnArray = null;
		if(value!=null) {
			String v = new String(value);
			if(BYOKCostanti.VARIABILE_KSM_KEY.equals(v)) {
				returnArray = key;
			}
			else if(BYOKCostanti.VARIABILE_KSM_KEY_URL_ENCODED.equals(v)) {
				returnArray = TransportUtils.urlEncodeParam(v, Charset.UTF_8.getValue()).getBytes();
			}
			else if(BYOKCostanti.VARIABILE_KSM_KEY_BASE64.equals(v)) {
				returnArray = Base64Utilities.encode(key);
			}
			else if(BYOKCostanti.VARIABILE_KSM_KEY_BASE64_URL_ENCODED.equals(v)) {
				String base64 = Base64Utilities.encodeAsString(key);
				returnArray = TransportUtils.urlEncodeParam(base64, Charset.UTF_8.getValue()).getBytes();
			}
			else if(BYOKCostanti.VARIABILE_KSM_KEY_HEX.equals(v)) {
				returnArray = HexBinaryUtilities.encodeAsString(key).getBytes();
			}
			else if(BYOKCostanti.VARIABILE_KSM_KEY_HEX_URL_ENCODED.equals(v)) {
				String hex =  HexBinaryUtilities.encodeAsString(key);
				returnArray = TransportUtils.urlEncodeParam(hex, Charset.UTF_8.getValue()).getBytes();
			}
			else {
				returnArray = resolveKsmConstants(dynamicMap, inputParameters, 
						inputMap, key,
						name, v).getBytes();
			}
		}
		return returnArray;
	}
	private static String resolveKsmConstants(Map<String,Object> dynamicMap, List<BYOKConfigParameter> inputParameters,
			Map<String,String> inputMap, byte[] key,
			String name, String value) throws UtilsException {
		
		if(value==null) {
			return value;
		}
		
		String newValue = resolveKsmConstant(value, BYOKCostanti.VARIABILE_KSM_KEY, key);
		newValue = resolveKsmConstant(newValue, BYOKCostanti.VARIABILE_KSM_KEY_URL_ENCODED, key);
		
		newValue = resolveKsmConstant(newValue, BYOKCostanti.VARIABILE_KSM_KEY_BASE64, key);
		newValue = resolveKsmConstant(newValue, BYOKCostanti.VARIABILE_KSM_KEY_BASE64_URL_ENCODED, key);
		
		newValue = resolveKsmConstant(newValue, BYOKCostanti.VARIABILE_KSM_KEY_HEX, key);
		newValue = resolveKsmConstant(newValue, BYOKCostanti.VARIABILE_KSM_KEY_HEX_URL_ENCODED, key);
		
		if(newValue.contains(BYOKCostanti.VARIABILE_KSM_KEY_PREFIX) && !dynamicMap.containsKey(BYOKCostanti.VARIABILE_KSM)) {
			Map<String, String> k = new HashMap<>();
			for (BYOKConfigParameter parameter: inputParameters) {
				if(inputMap!=null && inputMap.containsKey(parameter.getName())) {
					String paramValue = inputMap.get(parameter.getName());
					String valueResolved = resolve(parameter.getName(), paramValue, dynamicMap); // potrebbe essere a sua volta dinamico
					k.put(parameter.getName(), valueResolved);
				}
			} 
			dynamicMap.put(BYOKCostanti.VARIABILE_KSM, k);
		}
		
		return resolve(name, newValue, dynamicMap);
	}
	private static String resolveKsmConstant(String value, String constant, byte[] key) throws UtilsException {
		if(value!=null && value.contains(constant)){
			
			String replaceValue = null;
			if(BYOKCostanti.VARIABILE_KSM_KEY.equals(constant) || BYOKCostanti.VARIABILE_KSM_KEY_URL_ENCODED.equals(constant)) {
				replaceValue = new String(key);
			}
			else if(BYOKCostanti.VARIABILE_KSM_KEY_BASE64.equals(constant) || BYOKCostanti.VARIABILE_KSM_KEY_BASE64_URL_ENCODED.equals(constant)) {
				replaceValue = Base64Utilities.encodeAsString(key);
			}
			else if(BYOKCostanti.VARIABILE_KSM_KEY_HEX.equals(constant) || BYOKCostanti.VARIABILE_KSM_KEY_HEX_URL_ENCODED.equals(constant)) {
				replaceValue = HexBinaryUtilities.encodeAsString(key);
			}
			
			if(BYOKCostanti.VARIABILE_KSM_KEY_URL_ENCODED.equals(constant) || 
					BYOKCostanti.VARIABILE_KSM_KEY_BASE64_URL_ENCODED.equals(constant) || 
					BYOKCostanti.VARIABILE_KSM_KEY_HEX_URL_ENCODED.equals(constant)) {
				replaceValue = TransportUtils.urlEncodeParam(replaceValue,Charset.UTF_8.getValue());
			}
			
			while(value.contains(constant)){
				value = value.replace(constant, replaceValue);
			}
		}
		return value;
	}
	
	private static String resolve(String name, String value, Map<String,Object> dynamicMap) throws UtilsException {
		try{
			return DynamicStringReplace.replace(value, dynamicMap, true);
		}catch(Exception e){
			String prefix = "["+name+"] contiene un valore non corretto: ";
			throw new UtilsException(prefix+e.getMessage(),e);
		}
	}
}
