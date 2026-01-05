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
public class ModIPDNDOrganizationConfig extends AbstractModIPDNDConfig {

	private Map<String,String> idJsonPath;
	private Map<String,String> nameJsonPath;
	private Map<String,String> externalOriginJsonPath;
	private Map<String,String> externalIdJsonPath;
	private Map<String,String> categoryJsonPath;
	private Map<String,String> subUnitJsonPath;
	
	
	
	public ModIPDNDOrganizationConfig(Logger log) {
		super(log);
	}
	public ModIPDNDOrganizationConfig(String details, Logger log) {
		super(details, log);
	}
	public ModIPDNDOrganizationConfig cloneNewInstance() {
		
		ModIPDNDOrganizationConfig base = new ModIPDNDOrganizationConfig(this.details, this.log);
		
		base.version1JsonPathMatch = this.version1JsonPathMatch; 
		base.version2JsonPathMatch = this.version2JsonPathMatch;
		
		base.overridePrefixJsonPath = this.overridePrefixJsonPath; 
		base.ovveridePatternAsConstant = this.ovveridePatternAsConstant; 
		
		base.details = this.details; 
		
		base.idJsonPath = newMap(this.idJsonPath);
		base.nameJsonPath = newMap(this.nameJsonPath);
		base.externalOriginJsonPath = newMap(this.externalOriginJsonPath);
		base.externalIdJsonPath = newMap(this.externalIdJsonPath);
		base.categoryJsonPath = newMap(this.categoryJsonPath);
		base.subUnitJsonPath = newMap(this.subUnitJsonPath);
		
		return base;
	}


	public String getId() {
		return getValueEngine(this.idJsonPath);
	}
	public String getId(int version) {
		return getValueEngine(this.idJsonPath, version);
	}
	public String getName() {
		return getValueEngine(this.nameJsonPath);
	}
	public String getName(int version) {
		return getValueEngine(this.nameJsonPath, version);
	}
	public String getExternalOrigin() {
		return getValueEngine(this.externalOriginJsonPath);
	}
	public String getExternalOrigin(int version) {
		return getValueEngine(this.externalOriginJsonPath, version);
	}
	public String getExternalId() {
		return getValueEngine(this.externalIdJsonPath);
	}
	public String getExternalId(int version) {
		return getValueEngine(this.externalIdJsonPath, version);
	}
	public String getCategory() {
		return getValueEngine(this.categoryJsonPath);
	}
	public String getCategory(int version) {
		return getValueEngine(this.categoryJsonPath, version);
	}
	public String getSubUnit() {
		return getValueEngine(this.subUnitJsonPath);
	}
	public String getSubUnit(int version) {
		return getValueEngine(this.subUnitJsonPath, version);
	}
	
	
	
	
	public String getPatternId() {
		return getPatternEngine(this.idJsonPath);
	}
	public String getPatternId(int version) {
		return getPatternEngine(this.idJsonPath, version);
	}
	public String getPatternName() {
		return getPatternEngine(this.nameJsonPath);
	}
	public String getPatternName(int version) {
		return getPatternEngine(this.nameJsonPath, version);
	}
	public String getPatternExternalOrigin() {
		return getPatternEngine(this.externalOriginJsonPath);
	}
	public String getPatternExternalOrigin(int version) {
		return getPatternEngine(this.externalOriginJsonPath, version);
	}
	public String getPatternExternalId() {
		return getPatternEngine(this.externalIdJsonPath);
	}
	public String getPatternExternalId(int version) {
		return getPatternEngine(this.externalIdJsonPath, version);
	}
	public String getPatternCategory() {
		return getPatternEngine(this.categoryJsonPath);
	}
	public String getPatternCategory(int version) {
		return getPatternEngine(this.categoryJsonPath, version);
	}
	public String getPatternSubUnit() {
		return getPatternEngine(this.subUnitJsonPath);
	}
	public String getPatternSubUnit(int version) {
		return getPatternEngine(this.subUnitJsonPath, version);
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

	public Map<String,String> getExternalOriginJsonPath() {
		return this.externalOriginJsonPath;
	}

	public void setExternalOriginJsonPath(Map<String,String> externalIdOriginJsonPath) {
		this.externalOriginJsonPath = externalIdOriginJsonPath;
	}

	public Map<String,String> getExternalIdJsonPath() {
		return this.externalIdJsonPath;
	}

	public void setExternalIdJsonPath(Map<String,String> externalIdJsonPath) {
		this.externalIdJsonPath = externalIdJsonPath;
	}

	public Map<String,String> getCategoryJsonPath() {
		return this.categoryJsonPath;
	}

	public void setCategoryJsonPath(Map<String,String> categoryJsonPath) {
		this.categoryJsonPath = categoryJsonPath;
	}

	public Map<String,String> getSubUnitJsonPath() {
		return this.subUnitJsonPath;
	}

	public void setSubUnitJsonPath(Map<String,String> subUnitJsonPath) {
		this.subUnitJsonPath = subUnitJsonPath;
	}


}
