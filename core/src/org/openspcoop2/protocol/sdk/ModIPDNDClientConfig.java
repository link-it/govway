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

import java.util.Map;

import org.slf4j.Logger;

/**     
 * ModIPDNDOrganizationConfig
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class ModIPDNDClientConfig extends AbstractModIPDNDConfig {

	private Map<String,String> idJsonPath;
	private Map<String,String> organizationJsonPath;
	private Map<String,String> nameJsonPath;
	private Map<String,String> descriptionJsonPath;
	
	public ModIPDNDClientConfig(Logger log) {
		super(log);
	}
	public ModIPDNDClientConfig(String details, Logger log) {
		super(details, log);
	}
	
	public ModIPDNDClientConfig cloneNewInstance() {
		
		ModIPDNDClientConfig base = new ModIPDNDClientConfig(this.details, this.log);
		
		base.version1JsonPathMatch = this.version1JsonPathMatch; 
		base.version2JsonPathMatch = this.version2JsonPathMatch;
		
		base.overridePrefixJsonPath = this.overridePrefixJsonPath; 
		base.ovveridePatternAsConstant = this.ovveridePatternAsConstant; 
		
		base.details = this.details; 
		
		base.idJsonPath = newMap(this.idJsonPath);
		base.organizationJsonPath = newMap(this.organizationJsonPath);
		base.nameJsonPath = newMap(this.nameJsonPath);
		base.descriptionJsonPath = newMap(this.descriptionJsonPath);
		
		return base;
	}



	public String getId() {
		return getValueEngine(this.idJsonPath);
	}
	public String getId(int version) {
		return getValueEngine(this.idJsonPath, version);
	}
	public String getOrganization() {
		return getValueEngine(this.organizationJsonPath);
	}
	public String getOrganization(int version) {
		return getValueEngine(this.organizationJsonPath, version);
	}
	public String getName() {
		return getValueEngine(this.nameJsonPath);
	}
	public String getName(int version) {
		return getValueEngine(this.nameJsonPath, version);
	}
	public String getDescription() {
		return getValueEngine(this.descriptionJsonPath);
	}
	public String getDescription(int version) {
		return getValueEngine(this.descriptionJsonPath, version);
	}
	
	
	public String getPatternId() {
		return getPatternEngine(this.idJsonPath);
	}
	public String getPatternId(int version) {
		return getPatternEngine(this.idJsonPath, version);
	}
	public String getPatternOrganization() {
		return getPatternEngine(this.organizationJsonPath);
	}
	public String getPatternOrganization(int version) {
		return getPatternEngine(this.organizationJsonPath, version);
	}
	public String getPatternName() {
		return getPatternEngine(this.nameJsonPath);
	}
	public String getPatternName(int version) {
		return getPatternEngine(this.nameJsonPath, version);
	}
	public String getPatternDescription() {
		return getPatternEngine(this.descriptionJsonPath);
	}
	public String getPatternDescription(int version) {
		return getPatternEngine(this.descriptionJsonPath, version);
	}
	
	
	public Map<String,String> getIdJsonPath() {
		return this.idJsonPath;
	}
	public void setIdJsonPath(Map<String,String> idJsonPath) {
		this.idJsonPath = idJsonPath;
	}
	
	public Map<String,String> getNameJsonPath() {
		return this.nameJsonPath;
	}
	public void setNameJsonPath(Map<String,String> nameJsonPath) {
		this.nameJsonPath = nameJsonPath;
	}

	public Map<String,String> getOrganizationJsonPath() {
		return this.organizationJsonPath;
	}
	public void setOrganizationJsonPath(Map<String,String> organizationJsonPath) {
		this.organizationJsonPath = organizationJsonPath;
	}
	
	public Map<String,String> getDescriptionJsonPath() {
		return this.descriptionJsonPath;
	}
	public void setDescriptionJsonPath(Map<String,String> descriptionJsonPath) {
		this.descriptionJsonPath = descriptionJsonPath;
	}
}
