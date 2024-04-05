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


package org.openspcoop2.utils.openapi;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

import org.openspcoop2.utils.json.IJsonSchemaValidator;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * SwaggerApi
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class OpenapiApiValidatorStructure implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String, byte[]> schemiValidatorePrincipale = null;
	private Map<String, JsonNode> nodeValidatorePrincipale = null;
	
	private Map<String, File> fileSchema;
	
	private Map<String, IJsonSchemaValidator> validatorMap;

	
	public Map<String, byte[]> getSchemiValidatorePrincipale() {
		return this.schemiValidatorePrincipale;
	}
	public void setSchemiValidatorePrincipale(Map<String, byte[]> schemiValidatorePrincipale) {
		this.schemiValidatorePrincipale = schemiValidatorePrincipale;
	}
	public Map<String, JsonNode> getNodeValidatorePrincipale() {
		return this.nodeValidatorePrincipale;
	}
	public void setNodeValidatorePrincipale(Map<String, JsonNode> nodeValidatorePrincipale) {
		this.nodeValidatorePrincipale = nodeValidatorePrincipale;
	}
	
	public Map<String, File> getFileSchema() {
		return this.fileSchema;
	}
	public void setFileSchema(Map<String, File> fileSchema) {
		this.fileSchema = fileSchema;
	}
	
	public Map<String, IJsonSchemaValidator> getValidatorMap() {
		return this.validatorMap;
	}
	public void setValidatorMap(Map<String, IJsonSchemaValidator> validatorMap) {
		this.validatorMap = validatorMap;
	}
}
