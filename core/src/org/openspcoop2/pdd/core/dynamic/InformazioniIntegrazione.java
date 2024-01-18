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


package org.openspcoop2.pdd.core.dynamic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.HexBinaryUtilities;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.TransportResponseContext;
import org.openspcoop2.utils.xml2json.JsonXmlPathExpressionEngine;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

/**     
 * InformazioniIntegrazioneCustom
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InformazioniIntegrazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InformazioniIntegrazione() {} // per serializzatore
	public InformazioniIntegrazione(InformazioniIntegrazioneSorgente sourceType,  String sourceName, InformazioniIntegrazioneCodifica sourceEncodeType, boolean sourceRequired,
			Logger log, TransportRequestContext transportRequestContext) throws Exception {
		init(sourceType, sourceName, sourceEncodeType, sourceRequired, 
				log, transportRequestContext, null);
	}
	public InformazioniIntegrazione(InformazioniIntegrazioneSorgente sourceType,  String sourceName, InformazioniIntegrazioneCodifica sourceEncodeType, boolean sourceRequired,
			Logger log, TransportResponseContext transportResponseContext) throws Exception {
		init(sourceType, sourceName, sourceEncodeType, sourceRequired, 
				log, null, transportResponseContext);
	}
	
	private static boolean logError = true;
	public static boolean isLogError() {
		return logError;
	}
	public static void setLogError(boolean logError) {
		InformazioniIntegrazione.logError = logError;
	}
	private void init(InformazioniIntegrazioneSorgente sourceType,  String sourceName, InformazioniIntegrazioneCodifica sourceEncodeType, boolean sourceRequired,
			Logger log, TransportRequestContext transportRequestContext, TransportResponseContext transportResponseContext) throws Exception {
		
		this.log = log;
		
		this.sourceType = sourceType;
		this.sourceName = sourceName;
		this.sourceEncodeType = sourceEncodeType;
		this.sourceRequired = sourceRequired;
		
		if(this.sourceType==null) {
			throw new Exception("Source type undefined");
		}
		if(this.sourceName==null) {
			throw new Exception("Source name undefined");
		}
		if(this.sourceEncodeType==null) {
			throw new Exception("Source encode type undefined");
		}
		if(transportRequestContext==null && transportResponseContext==null) {
			throw new Exception("TransportContext undefined");
		}
		
		List<String> values = null;
		String debugName = null;
		switch (this.sourceType) {
		case http_header:
			if(transportRequestContext!=null) {
				values = transportRequestContext.getHeaderValues(this.sourceName);
			}
			else if(transportResponseContext!=null) {
				values = transportResponseContext.getHeaderValues(this.sourceName);
			}
			debugName = "http header "+this.sourceName;
			break;
		case query_parameter:
			if(transportRequestContext!=null) {
				values = transportRequestContext.getParameterValues(this.sourceName);
			}
			else {
				throw new Exception("QueryParameter unsupported for transport response context");
			}
			debugName = "query parameter "+this.sourceName;
			break;
		}
		
		if(values==null || values.isEmpty()) {
			if(this.sourceRequired) {
				throw new Exception("Required " +debugName+" not found");
			}
			else {
				this.claims = new HashMap<>();
				this.integrationInfo = new HashMap<>();
				return;
			}
		}
		
		if(values.size()>1) {
			throw new Exception("Found more than one "+debugName+" ("+values.size()+")");
		}
		
		this.raw = values.get(0);
		if(this.raw==null || StringUtils.isEmpty(this.raw)) {
			throw new Exception("Found empty " +debugName);
		}
		
		switch (this.sourceEncodeType) {
		case plain:
			this.rawDecoded = this.raw.getBytes();
			break;
		case base64:
			try {
				this.rawDecoded = Base64Utilities.decode(this.raw);
			}catch(Throwable t) {
				throw new Exception("Base64 decode " +debugName+" failed: "+t.getMessage(),t);
			}
			break;
		case hex:
			try {
				this.rawDecoded = HexBinaryUtilities.decode(this.raw);
			}catch(Throwable t) {
				throw new Exception("HEX decode " +debugName+" failed: "+t.getMessage(),t);
			}
			break;
		case jwt:
			try {
				String [] split = this.raw.split("\\.");
				if(split==null || split.length<2) {
					throw new Exception("uncorrect jwt format");
				}
				String payload = split[1];
				if(payload==null || StringUtils.isEmpty(payload)) {
					throw new Exception("Found empty jwt payload in " +debugName);
				}
				this.rawDecoded = Base64Utilities.decode(payload);
			}catch(Throwable t) {
				throw new Exception("JWT decode " +debugName+" failed: "+t.getMessage(),t);
			}
			break;
		}

		//System.out.println("JSON ["+new String(this.rawDecoded)+"]");
				
		JSONUtils jsonUtils = JSONUtils.getInstance();
		
		JsonNode root = null;
		if(logError) {
			try {
				root = jsonUtils.getAsNode(this.rawDecoded);
			}catch(Throwable t) {
				throw new Exception("Content in " +debugName+" isn't a json: "+t.getMessage(),t);
			}
		}
		else {
			if(jsonUtils.isJson(this.rawDecoded)) {
				root = jsonUtils.getAsNode(this.rawDecoded);
			}
		}
		
		if(root!=null) {
			Map<String, Object> readClaims = jsonUtils.convertToSimpleMap(root);
			if(readClaims!=null && readClaims.size()>0) {
				this.claims.putAll(readClaims);
			}
		}
		
		this.integrationInfo = jsonUtils.convertToMap(log, debugName, this.rawDecoded);
		
	}

		
	// NOTA: l'ordine stabilisce come viene serializzato nell'oggetto json
	
	private transient Logger log;
	
	// Informazioni
	private Map<String, Object> integrationInfo;
	
	// Claims
	private Map<String,Object> claims = new HashMap<>();
	
	// NOTA: l'ordine stabilisce come viene serializzato nell'oggetto json
		
	// Raw
	private String raw;
	private byte[] rawDecoded;
	
	// Nome dell'header HTTP dove sono stati reperite le informazioni
	private InformazioniIntegrazioneSorgente sourceType;
	private String sourceName;
	private InformazioniIntegrazioneCodifica sourceEncodeType;
	private boolean sourceRequired;
	
	
	public Map<String, Object> getIntegrationInfo() {
		return this.integrationInfo;
	}
	public Map<String, Object> getInfo() {
		return this.integrationInfo;
	}
	public void setIntegrationInfo(Map<String, Object> integrationInfo) {
		this.integrationInfo = integrationInfo;
	}
	public List<String> getInfoNames(){
		return getIntegrationInfoNames();
	}
	public List<String> getIntegrationInfoNames(){
		if(this.integrationInfo!=null && !this.integrationInfo.isEmpty()) {
			List<String> attributesNames = new ArrayList<>();
			for (String attrName : this.integrationInfo.keySet()) {
				attributesNames.add(attrName);
			}
			Collections.sort(attributesNames);
			return attributesNames;
		}
		return null;
	}
	
	public String getClaim(String name) throws Exception {
		if(name==null) {
			throw new Exception("Claim name is null");
		}
		String pattern ="$.."+name;
		return JsonXmlPathExpressionEngine.extractAndConvertResultAsString(getRawDecodedAsString(), pattern, this.log);
	}
	
	public Map<String, Object> getClaims() {
		return this.claims;
	}
	public void setClaims(Map<String, Object> claims) {
		this.claims = claims;
	}
			
	public String getRaw() {
		return this.raw;
	}
	public void setRaw(String raw) {
		this.raw = raw;
	}
	public void setRawDecoded(byte[] rawDecoded) {
		this.rawDecoded = rawDecoded;
	}
	public byte[] getRawDecoded() {
		return this.rawDecoded;
	}
	public String getRawDecodedAsString() {
		return new String(this.rawDecoded);
	}
	
	public InformazioniIntegrazioneSorgente getSourceType() {
		return this.sourceType;
	}
	public void setSourceType(InformazioniIntegrazioneSorgente sourceType) {
		this.sourceType = sourceType;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	public void setSourceEncodeType(InformazioniIntegrazioneCodifica sourceEncodeType) {
		this.sourceEncodeType = sourceEncodeType;
	}
	public void setSourceRequired(boolean sourceRequired) {
		this.sourceRequired = sourceRequired;
	}
	public String getSourceName() {
		return this.sourceName;
	}
	public InformazioniIntegrazioneCodifica getSourceEncodeType() {
		return this.sourceEncodeType;
	}
	public boolean isSourceRequired() {
		return this.sourceRequired;
	}
	
}
