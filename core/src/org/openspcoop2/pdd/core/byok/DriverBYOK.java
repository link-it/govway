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

package org.openspcoop2.pdd.core.byok;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.byok.BYOKUtilities;
import org.openspcoop2.core.byok.BYOKWrappedValue;
import org.openspcoop2.core.byok.IDriverBYOK;
import org.openspcoop2.pdd.core.dynamic.DynamicInfo;
import org.openspcoop2.pdd.core.dynamic.DynamicMapBuilderUtils;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.core.jmx.JMXUtils;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.BYOKLocalEncrypt;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Semaphore;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.byok.BYOKCostanti;
import org.openspcoop2.utils.certificate.byok.BYOKInstance;
import org.openspcoop2.utils.certificate.byok.BYOKManager;
import org.openspcoop2.utils.certificate.byok.BYOKMode;
import org.openspcoop2.utils.certificate.byok.BYOKProvider;
import org.openspcoop2.utils.certificate.byok.BYOKRemoteUtils;
import org.openspcoop2.utils.certificate.byok.BYOKRequestParams;
import org.openspcoop2.utils.certificate.byok.BYOKSecurityConfig;
import org.openspcoop2.utils.certificate.byok.BYOKSecurityConfigParameter;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**
 * DriverBYOK
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverBYOK implements IDriverBYOK {

	private Logger log;
	private String securityPolicy;
	private String securityRemotePolicy;
	private Map<String, Map<String, Object>> dynamicMapForSecurityPolicy; // per supportare l'unwrap di secrets codificati con security policy differenti da quelle di default
	private boolean checkJmxPrefixOperazioneNonRiuscita;
	
	public DriverBYOK(Logger log, String securityPolicy, String securityRemotePolicy) {
		this(log, securityPolicy, securityRemotePolicy, buildDynamicMap(log), false);
	}
	public static Map<String, Object> buildDynamicMap(Logger log){
		Map<String, Object> dynamicMap = new HashMap<>();
		DynamicInfo dynamicInfo = new  DynamicInfo();
		DynamicUtils.fillDynamicMap(log, dynamicMap, dynamicInfo);
		return dynamicMap;
	}
	DriverBYOK(Logger log, String securityPolicy, String securityRemotePolicy, Map<String, Object> dynamicMapParam, boolean checkJmxPrefixOperazioneNonRiuscita) {
		this.log = log;
		if(securityPolicy!=null && StringUtils.isNotEmpty(securityPolicy)) {
			this.securityPolicy = securityPolicy;
		}
		if(securityRemotePolicy!=null && StringUtils.isNotEmpty(securityRemotePolicy)) {
			this.securityRemotePolicy = securityRemotePolicy;
		}
		
		this.dynamicMapForSecurityPolicy = new HashMap<>();
		Map<String, Object> defaultPolicy = dynamicMapParam==null ? new HashMap<>() : dynamicMapParam;
		this.dynamicMapForSecurityPolicy.put(this.securityRemotePolicy!=null ? this.securityRemotePolicy : this.securityPolicy, defaultPolicy);
		this.checkJmxPrefixOperazioneNonRiuscita = checkJmxPrefixOperazioneNonRiuscita;
	}
	
	private Semaphore semaphoreDynamicMap = new Semaphore("dynamicMap");
	private Map<String, Object> getDynamicMap(String securityPolicy) throws UtilsException{
		if(!this.dynamicMapForSecurityPolicy.containsKey(securityPolicy)) {
			this.initDynamicMap(securityPolicy);
		}
		return this.dynamicMapForSecurityPolicy.get(securityPolicy);
	}
	private void initDynamicMap(String securityPolicy) throws UtilsException {
		this.semaphoreDynamicMap.acquire("initDynamicMap");
		try {
			if(!this.dynamicMapForSecurityPolicy.containsKey(securityPolicy)) {
				Map<String, Object> mDefault = this.dynamicMapForSecurityPolicy.get(this.securityRemotePolicy!=null ? this.securityRemotePolicy : this.securityPolicy);
				Map<String, Object> mNew = new HashMap<>();
				for (Map.Entry<String,Object> entry : mDefault.entrySet()) {
					if(!BYOKCostanti.VARIABILE_KSM.equals(entry.getKey())){ // devo escludere ksm object contenente le variabili di un'altra security policy
						mNew.put(entry.getKey(), entry.getValue());
					}
				}
				this.dynamicMapForSecurityPolicy.put(securityPolicy, mNew);
			}
		}finally {
			this.semaphoreDynamicMap.release("initDynamicMap");
		}
	}
	
	@Override
	public BYOKWrappedValue wrap(String value) throws UtilsException {
		if(value==null) {
			throw new UtilsException("Value undefined");
		}
		if(BYOKUtilities.isWrappedValue(value)) {
			return new BYOKWrappedValue(value, BYOKUtilities.extractPrefixWrappedValue(value));
		}
		
		if(this.securityPolicy==null || StringUtils.isEmpty(value)) {
			return null;
		}
		
		BYOKRequestParams p = getBYOKRequestParams(true, this.securityPolicy);
		String prefix = BYOKUtilities.newPrefixWrappedValue((this.securityRemotePolicy!=null ? this.securityRemotePolicy : this.securityPolicy));
		byte[]wrapped = process(getBYOKInstance(this.log,value.getBytes(),p));
		
		String wrappedValue = new String(wrapped);
		if(!wrappedValue.startsWith(prefix)){
			wrappedValue = prefix + wrappedValue;
		}
		
		return new BYOKWrappedValue(wrappedValue, 
				prefix.substring(0, prefix.length()-1) // elimino il punto finale
				);
	}
	@Override
	public BYOKWrappedValue wrap(byte[] value) throws UtilsException {
		if(value==null) {
			throw new UtilsException("Value undefined");
		}
		if(BYOKUtilities.isWrappedValue(value)) {
			return new BYOKWrappedValue(value, BYOKUtilities.extractPrefixWrappedValue(value));
		}
		
		if(this.securityPolicy==null) {
			return null;
		}
		
		BYOKRequestParams p = getBYOKRequestParams(true, this.securityPolicy);
		String prefix = BYOKUtilities.newPrefixWrappedValue((this.securityRemotePolicy!=null ? this.securityRemotePolicy : this.securityPolicy));
		byte[]wrapped = process(getBYOKInstance(this.log,value,p));
		
		String wrappedValue = new String(wrapped);
		if(!wrappedValue.startsWith(prefix)){
			wrappedValue = prefix + wrappedValue;
		}
		
		return new BYOKWrappedValue(wrappedValue, 
				prefix.substring(0, prefix.length()-1) // elimino il punto finale
				);
	}

	public boolean isAlreadyWrappedBySecPolicy(String check) throws UtilsException {
		// Serve per evitare di effettuare un ulteriore livello di wrap ad una informazione già cifrata
		// nei metodi wrap non viene usato poichè viene già verificato più in generale con 'isWrappedValue'
		// si lascia come utility generica il metodo
		try {
			return getSecPolicyIdForUnwrap(check)!=null;
		}catch(Exception e) {
			return false;
		}
	}
	private String getSecPolicyIdForUnwrap(String check) throws UtilsException {
		// Serve per decodificare valori cifrati con security policy differente da quella impostata per la gestione della cifratura
		// Il comportamento serve a supportare eventuali valori cifrati dopo un cambio della policy senza un aggiornamento delle informazioni sensibili
		String secPolicy = this.securityPolicy;
		String wrapPolicy = BYOKUtilities.getPolicy(check);
		if(!secPolicy.equals(wrapPolicy)) {
			// dato cifrato con altra policy
			// verifico che comunque esista
			if(BYOKManager.getInstance().existsSecurityEngineByType(wrapPolicy)){
				secPolicy = wrapPolicy;
			}
			else {
				throw new UtilsException("Security policy '"+wrapPolicy+"' unknown");
			}
		}
		return secPolicy;
	}
	
	@Override
	public byte[] unwrap(byte[] value) throws UtilsException {
		if(value==null || value.length<=0) {
			return value;
		}
		String check = new String(value);
		if(BYOKUtilities.isWrappedValue(check)) {
			if(this.securityPolicy==null) {
				return value;
			}
			
			String rawWrappedValue =  BYOKUtilities.getRawWrappedValue(check);
			
			BYOKRequestParams p = getBYOKRequestParams(false, getSecPolicyIdForUnwrap(check));
			return process(getBYOKInstance(this.log,rawWrappedValue.getBytes(),p));
		}
		return value;
	}
	@Override
	public byte[] unwrap(String value) throws UtilsException {
		return unwrap(value, null, false);
	}
	public byte[] unwrap(String value, boolean checkAppendPrefix) throws UtilsException{
		return unwrap(value, 
				this.securityRemotePolicy!=null ? this.securityRemotePolicy : this.securityPolicy, 
				checkAppendPrefix);
	}
	public byte[] unwrap(String value, String securityPolicy, boolean checkAppendPrefix) throws UtilsException {
		if(BYOKUtilities.isWrappedValue(value)) {
			if(this.securityPolicy==null) {
				return value.getBytes();
			}
			
			String rawWrappedValue =  BYOKUtilities.getRawWrappedValue(value);
			BYOKRequestParams p = getBYOKRequestParams(false, getSecPolicyIdForUnwrap(value));
			return process(getBYOKInstance(this.log,rawWrappedValue.getBytes(),p));
		}
		else if(checkAppendPrefix){
			String newWrappedValue = BYOKUtilities.newPrefixWrappedValue(securityPolicy)+value;
			return this.unwrap(newWrappedValue);
		}
		else {
			return value.getBytes();
		}
	}
	
	public String unwrapAsString(String value, boolean checkAppendPrefix) throws UtilsException{
		return unwrapAsString(value, 
				this.securityRemotePolicy!=null ? this.securityRemotePolicy : this.securityPolicy, 
				checkAppendPrefix);
	}
	public String unwrapAsString(String value, String securityPolicy, boolean checkAppendPrefix) throws UtilsException{
		if(BYOKUtilities.isWrappedValue(value)) {
			return this.unwrapAsString(value);
		}
		else if(checkAppendPrefix){
			String newWrappedValue = BYOKUtilities.newPrefixWrappedValue(securityPolicy)+value;
			String unwrappedValue = this.unwrapAsString(newWrappedValue);
			if(newWrappedValue.equals(unwrappedValue)) {
				// decodifica non riuscita
				return value;
			}
			else {
				return unwrappedValue;
			}
		}
		else {
			return value;
		}
	}
	
	private BYOKInstance getBYOKInstance(Logger log,byte[] key, BYOKRequestParams p) throws UtilsException {
		return BYOKInstance.newInstance(log, p, key);
	}
	private BYOKRequestParams getBYOKRequestParams(boolean wrap, String securityPolicy) throws UtilsException {
		return getBYOKRequestParamsBySecurityPolicy(wrap, securityPolicy, this. getDynamicMap(securityPolicy));
	}
	public static BYOKRequestParams getBYOKRequestParamsBySecurityPolicy(boolean wrap, String securityPolicy, Map<String, Object> dynamicMap) throws UtilsException {
		
		if(securityPolicy==null) {
			return null;
		}
		
		BYOKManager manager = BYOKManager.getInstance();
		if(manager==null) {
			throw new UtilsException("BYOKManager not initialized");
		}
		
		BYOKSecurityConfig secConfig = manager.getKSMSecurityConfig(securityPolicy); 
		
		String ksmId = wrap ? secConfig.getWrapId() : secConfig.getUnwrapId();
		if(ksmId==null) {
			throw new UtilsException("BYOK security configuration '"+securityPolicy+"' without "+(wrap ?  "wrap" : "unwrap")+" ksm id");
		}
				
		Map<String, String> inputMap = new HashMap<>();
		if(secConfig.getInputParameters()!=null && !secConfig.getInputParameters().isEmpty()) {
			for (BYOKSecurityConfigParameter sec : secConfig.getInputParameters()) {
				inputMap.put(sec.getName(), sec.getValue());
			}
		}
		
		return getBYOKRequestParamsByKsmId(ksmId, manager, 
				inputMap, dynamicMap);
	}
	
	public static BYOKRequestParams getBYOKRequestParamsByUnwrapBYOKPolicy(String ksmId,
			Busta busta, RequestInfo requestInfo, Context context, Logger log) throws UtilsException {
		if(BYOKProvider.isPolicyDefined(ksmId)){
			Map<String,Object> dynamicMap = DynamicMapBuilderUtils.buildDynamicMap(busta, requestInfo, context, log);
			return BYOKProvider.getBYOKRequestParamsByUnwrapBYOKPolicy(ksmId, dynamicMap);
		}
		return null;
	}
	
	public static BYOKRequestParams getBYOKRequestParamsByKsmId(String ksmId, 
			Map<String, String> inputMap, Map<String, Object> dynamicMap) throws UtilsException {
		return BYOKRequestParams.getBYOKRequestParamsByKsmId(ksmId, 
				inputMap,dynamicMap);
	}
	public static BYOKRequestParams getBYOKRequestParamsByKsmId(String ksmId, BYOKManager manager, 
			Map<String, String> inputMap, Map<String, Object> dynamicMap) throws UtilsException {
		return BYOKRequestParams.getBYOKRequestParamsByKsmId(ksmId, manager, 
				inputMap, dynamicMap);
	}
	
	private byte[] process(BYOKInstance instance) throws UtilsException{
		return processInstance(instance, this.checkJmxPrefixOperazioneNonRiuscita);
	}
	public static byte[] processInstance(BYOKInstance instance, boolean checkJmxPrefixOperazioneNonRiuscita) throws UtilsException{
		
		try{
			if(instance==null){
				throw new SecurityException("Instance non fornita");
			}
			
			if(instance.getHttpRequest()!=null) {
				return remoteProcess(instance, checkJmxPrefixOperazioneNonRiuscita);
			}
			else {
				BYOKLocalEncrypt localEncrypt = new BYOKLocalEncrypt();
				if(BYOKMode.WRAP.equals(instance.getConfig().getMode())) {
					return localEncrypt.wrap(instance.getLocalConfigResolved(), instance.getLocalKey()).getBytes();
				}
				else {
					return localEncrypt.unwrap(instance.getLocalConfigResolved(), instance.getLocalKey());
				}
			}

		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
		
	}
	private static byte[] remoteProcess(BYOKInstance instance, boolean checkJmxPrefixOperazioneNonRiuscita) throws UtilsException{
		String debugUrl = "'"+instance.getConfig().getLabel()+"' (endpoint:"+instance.getHttpRequest().getUrl()+")";
		
		HttpResponse httpResponse = HttpUtilities.httpInvoke(instance.getHttpRequest());
		if(httpResponse==null || httpResponse.getContent()==null) {
			throw new UtilsException("Store "+debugUrl+" unavailable");
		}
		if(httpResponse.getResultHTTPOperation()!=200) {
			throw new UtilsException("Retrieve store "+debugUrl+" failed (returnCode:"+httpResponse.getResultHTTPOperation()+")");
		}
		byte [] content = null;
		if(checkJmxPrefixOperazioneNonRiuscita) {
			byte[] b = httpResponse.getContent();
			if(b==null || b.length<=0) {
				throw new UtilsException("Store "+debugUrl+" empty response");
			}
			String check = new String(b);
			if(check.startsWith(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA)) {
				throw new UtilsException("Retrieve store "+debugUrl+" failed (returnCode:"+httpResponse.getResultHTTPOperation()+"): "+check);
			}
			content = b;
		}
		else {
			content = httpResponse.getContent();
		}
		if(content!=null && content.length>0) {
			content = BYOKRemoteUtils.normalizeResponse(instance, content, LoggerWrapperFactory.getLogger(DriverBYOK.class));
		}
		return content;
	}
	
	public boolean isWrappedWithInternalPolicy(byte[] value) {
		if(value==null || value.length<=0) {
			return false;
		}
		return isWrappedWithInternalPolicy(new String(value));
	}
	public boolean isWrappedWithInternalPolicy(String value) {
		String policy = this.securityRemotePolicy!=null ? this.securityRemotePolicy : this.securityPolicy;
		return DriverBYOKUtilities.isWrappedWithPolicy(this.log, value, policy);
	}
	
}
