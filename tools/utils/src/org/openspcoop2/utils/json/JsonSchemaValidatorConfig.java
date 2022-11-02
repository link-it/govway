/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.utils.json;

import java.util.List;

import com.networknt.schema.SpecVersion;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class JsonSchemaValidatorConfig {

	public enum ADDITIONAL {DEFAULT, IF_NULL_DISABLE, IF_NULL_STRING, FORCE_DISABLE, FORCE_STRING }
	
	public enum POLITICA_INCLUSIONE_TIPI {DEFAULT, ANY, ALL}
	
	private boolean verbose = false;
	private boolean emitLogError = true;
	
	private ADDITIONAL additionalProperties = ADDITIONAL.DEFAULT;

	private POLITICA_INCLUSIONE_TIPI politicaInclusioneTipi = POLITICA_INCLUSIONE_TIPI.DEFAULT;
	
	private List<String> tipi;

	private SpecVersion.VersionFlag jsonSchemaVersion; // se non fornito viene comprenso dallo schema 
	
	public SpecVersion.VersionFlag getJsonSchemaVersion() {
		return this.jsonSchemaVersion;
	}

	public void setJsonSchemaVersion(SpecVersion.VersionFlag jsonSchemaVersion) {
		this.jsonSchemaVersion = jsonSchemaVersion;
	}

	public ADDITIONAL getAdditionalProperties() {
		return this.additionalProperties;
	}

	public void setAdditionalProperties(ADDITIONAL defaultAdditionalProperties) {
		this.additionalProperties = defaultAdditionalProperties;
	}

	public POLITICA_INCLUSIONE_TIPI getPoliticaInclusioneTipi() {
		return this.politicaInclusioneTipi;
	}

	public void setPoliticaInclusioneTipi(POLITICA_INCLUSIONE_TIPI politica) {
		this.politicaInclusioneTipi = politica;
	}

	public List<String> getTipi() {
		return this.tipi;
	}

	public void setTipi(List<String> tipi) {
		this.tipi = tipi;
	}
	
	public boolean isVerbose() {
		return this.verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	public boolean isEmitLogError() {
		return this.emitLogError;
	}

	public void setEmitLogError(boolean emitLogError) {
		this.emitLogError = emitLogError;
	}
}
