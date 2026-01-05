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
package org.openspcoop2.protocol.sdk;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.slf4j.Logger;

/**     
 * AbstractModIPDNDConfig
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/
public abstract class AbstractModIPDNDConfig {

	protected Logger log;
	
	protected String version1JsonPathMatch;
	protected String version2JsonPathMatch;
	
	protected String overridePrefixJsonPath = ""; // da usare se il details è incluso in altri oggetti json
	protected boolean ovveridePatternAsConstant = false; // per cercare in TokenInfo dove il path es. externalId.origin è proprio il nome dell'elemento json
	
	protected String details;

	public void clone(AbstractModIPDNDConfig base) {
		base.version1JsonPathMatch = this.version1JsonPathMatch; 
		base.version2JsonPathMatch = this.version2JsonPathMatch;
		
		base.overridePrefixJsonPath = this.overridePrefixJsonPath; 
		base.ovveridePatternAsConstant = this.ovveridePatternAsConstant; 
		
		base.details = this.details; 
	}
	
	protected AbstractModIPDNDConfig(Logger log) {
		this.log = log;
	}
	protected AbstractModIPDNDConfig(String details, Logger log) {
		this(log);
		this.details = details;
	}
	
	private static final int DEFAULT_VERSION = 1;
	public int getVersion(String json) {
		if(match(normalizePattern(this.version2JsonPathMatch), json)) {
			return 2;
		}
		else if(match(normalizePattern(this.version1JsonPathMatch), json)) {
			return 1;
		}
		return DEFAULT_VERSION; // default
	}
	
	private static final String NOT_PREFIX = "NOT ";
	private boolean match(String pattern, String json) {
		if(pattern==null || StringUtils.isEmpty(pattern)) {
			return false;
		}
		boolean not = false;
		String searchPattern = pattern;
		if(pattern.toLowerCase().startsWith(NOT_PREFIX.toLowerCase())) {
			not = true;
			searchPattern = pattern.substring(NOT_PREFIX.length()); 
		}
		String v = null;
		try {
			v = JsonPathExpressionEngine.extractAndConvertResultAsString(json, searchPattern, this.log);
		}catch(Exception e) {
			// ignore
		}
		return not ? (v==null || StringUtils.isEmpty(v)) : (v!=null && StringUtils.isNotEmpty(v)); 
	}

	public String getVersion1JsonPathMatch() {
		return this.version1JsonPathMatch;
	}
	public void setVersion1JsonPathMatch(String version1JsonPathMatch) {
		this.version1JsonPathMatch = version1JsonPathMatch;
	}
	public String getVersion2JsonPathMatch() {
		return this.version2JsonPathMatch;
	}
	public void setVersion2JsonPathMatch(String version2JsonPathMatch) {
		this.version2JsonPathMatch = version2JsonPathMatch;
	}
	
	
	protected void checkDetails() throws ProtocolException {
		if(this.details==null) {
			throw new ProtocolException("Details undefined");
		}
	}
	
	public String getDetails() {
		return this.details;
	}

	public void setDetails(String details) {
		this.details = details;
	}
	
	protected String getValueEngine(Map<String,String> jsonPath) {
		int version = getVersion(this.details);
		return getValueEngine(jsonPath, version) ;
	}
	protected String getValueEngine(Map<String,String> jsonPath, int version) {
		String pattern = jsonPath.get(version+"");
		if(pattern==null || StringUtils.isEmpty(pattern)) {
			return null;
		}
		try {
			return JsonPathExpressionEngine.extractAndConvertResultAsString(this.details, normalizePattern(pattern), this.log);
		}catch(Exception e) {
			// ignore
		}
		return null;
	}
	
	protected String getPatternEngine(Map<String,String> jsonPath) {
		int version = getVersion(this.details);
		return getPatternEngine(jsonPath, version) ;
	}
	protected String getPatternEngine(Map<String,String> jsonPath, int version) {
		String pattern = jsonPath.get(version+"");
		if(pattern==null || StringUtils.isEmpty(pattern)) {
			return null;
		}
		return pattern;
	}
	
	private String normalizePattern(String pattern) {
		
		if(pattern==null) {
			return pattern;
		}
		
		String prefix = null;
		if(pattern.startsWith("$..")) {
			prefix = "$..";
		}else if(pattern.startsWith("$.")) {
			prefix = "$.";
		}
		
		if(this.ovveridePatternAsConstant) {
			String base = null;
			if(prefix!=null) {
				base = pattern.replace(prefix, "");
			}
			pattern = prefix + "['"+base+"']";
		}
		
		if(this.overridePrefixJsonPath==null || StringUtils.isEmpty(this.overridePrefixJsonPath)) {
			return pattern;
		}
		if(pattern.startsWith(this.overridePrefixJsonPath)) {
			return pattern; // già applicato
		}
		if(prefix!=null) {
			return pattern.replace(prefix, this.overridePrefixJsonPath);
		}
		return pattern;
	}
	
	public String getOverridePrefixJsonPath() {
		return this.overridePrefixJsonPath;
	}
	public void setOverridePrefixJsonPath(String prefixJsonPath) {
		this.overridePrefixJsonPath = prefixJsonPath;
	}
	public boolean isOvveridePatternAsConstant() {
		return this.ovveridePatternAsConstant;
	}
	public void setOvveridePatternAsConstant(boolean ovveridePatternAsConstant) {
		this.ovveridePatternAsConstant = ovveridePatternAsConstant;
	}
	
	protected Map<String,String> newMap(Map<String,String> src) {
		Map<String,String> dest = null;
		if(src!=null && !src.isEmpty()) {
			dest = new HashMap<>();
			dest.putAll(src);
		}
		return dest;
	}
}
