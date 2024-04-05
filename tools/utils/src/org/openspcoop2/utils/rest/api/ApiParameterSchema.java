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

package org.openspcoop2.utils.rest.api;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.UtilsException;

/**
 * ApiParameterSchema
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ApiParameterSchema {

	private static String SEPARATOR_COMPLEX_TYPE = "|||";
	private static String SEPARATOR_COMPLEX_TYPE_SPLIT = "\\|\\|\\|";
	
	private ApiParameterSchemaComplexType complexType = ApiParameterSchemaComplexType.simple;
	private List<ApiParameterTypeSchema> schemas = new ArrayList<ApiParameterTypeSchema>();
	
	public boolean isDefined() {
		return this.schemas!=null && !this.schemas.isEmpty();
	}
	
	public ApiParameterSchemaComplexType getComplexType() {
		return this.complexType;
	}
	public void setComplexType(ApiParameterSchemaComplexType complexType) {
		this.complexType = complexType;
	}
	public List<ApiParameterTypeSchema> getSchemas() {
		return this.schemas;
	}
	public void setSchemas(List<ApiParameterTypeSchema> schemas) {
		this.schemas = schemas;
	}
	public void addType(String type, ApiSchemaTypeRestriction restriction) {
		ApiParameterTypeSchema apts = new ApiParameterTypeSchema();
		apts.setType(type);
		apts.setSchema(restriction);
		this.schemas.add(apts);
	}
	
	public String getType() {
		if(this.schemas!=null && !this.schemas.isEmpty() &&
				this.schemas.get(0)!=null) {
			return this.schemas.get(0).getType();
		}
		return null;
	}
	
	public static ApiParameterSchema toApiParameterSchema(String info) throws UtilsException {
		if(info!=null) {
			info = info.trim();	
		}
		if(info==null || "".equals(info)) {
			throw new UtilsException("Formato non valido");
		}
		
		ApiParameterSchema schema = new ApiParameterSchema();
		schema.setComplexType(ApiParameterSchemaComplexType.simple);
		if(info.contains(SEPARATOR_COMPLEX_TYPE)) {
			String[]tmp = info.split(SEPARATOR_COMPLEX_TYPE_SPLIT);
			if(tmp==null || tmp.length<2) {
				throw new UtilsException("Formato non valido ("+info+")");
			}
			String complexType = tmp[0];
			if(complexType==null) {
				throw new UtilsException("Formato non valido ("+info+"): complex type undefined");
			}
			complexType = complexType.trim();
			try {
				ApiParameterSchemaComplexType t = ApiParameterSchemaComplexType.valueOf(complexType);
				schema.setComplexType(t);
			}catch(Throwable t) {
				throw new UtilsException("Formato non valido ("+info+"): complex type '"+complexType+"' invalid: "+t.getMessage());
			}
			for (int i = 1; i < tmp.length; i++) {
				String tmpI= tmp[i];
				if(tmpI==null) {
					throw new UtilsException("Formato non valido ("+info+"): i="+i+" undefined?");
				}
				tmpI = tmpI.trim();
				try {
					ApiSchemaTypeRestriction apiStr = ApiSchemaTypeRestriction.toApiSchemaTypeRestriction(tmpI);
					ApiParameterTypeSchema apiTs = new ApiParameterTypeSchema();
					apiTs.setSchema(apiStr);
					if(apiStr.getFormat()!=null) {
						apiTs.setType(apiStr.getFormat());
					}
					else {
						apiTs.setType(apiStr.getType());
					}
					schema.getSchemas().add(apiTs);
				}catch(Throwable t) {
					throw new UtilsException("Formato non valido ("+info+"): i="+i+" '"+tmpI+"' invalid: "+t.getMessage());
				}
			}
		}
		else {
			ApiSchemaTypeRestriction apiStr = ApiSchemaTypeRestriction.toApiSchemaTypeRestriction(info);
			ApiParameterTypeSchema apiTs = new ApiParameterTypeSchema();
			apiTs.setSchema(apiStr);
			if(apiStr.getFormat()!=null) {
				apiTs.setType(apiStr.getFormat());
			}
			else {
				apiTs.setType(apiStr.getType());
			}
			schema.getSchemas().add(apiTs);
		}
		
		return schema;
	}
	public static String toString(ApiParameterSchema schema) throws UtilsException {
		return schema.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		if(this.complexType==null || ApiParameterSchemaComplexType.simple.equals(this.complexType)) {
			if(this.schemas==null || this.schemas.isEmpty()) {
				throw new RuntimeException("Schema undefined");
			}
			if(this.schemas.size()!=1) {
				throw new RuntimeException("Uncorrect Schema (found:"+this.schemas.size()+")");
			}
			ApiParameterTypeSchema ts = this.schemas.get(0);
			if(ts==null) {
				throw new RuntimeException("Schema is empty?");
			}
			if(ts.getSchema()!=null) {
				sb.append(ts.getSchema().toString());
			}
			else {
				ApiSchemaTypeRestriction serialization = new ApiSchemaTypeRestriction();
				serialization.setType(ts.getType());
				sb.append(serialization.toString());
			}
		}
		else {
			sb.append(this.complexType);
			if(this.schemas==null || this.schemas.isEmpty()) {
				throw new RuntimeException("Schema undefined");
			}
			for (ApiParameterTypeSchema ts : this.schemas) {
				sb.append(SEPARATOR_COMPLEX_TYPE);
				if(ts==null) {
					throw new RuntimeException("Schema is empty?");
				}
				if(ts.getSchema()!=null) {
					sb.append(ts.getSchema().toString());
				}
				else {
					ApiSchemaTypeRestriction serialization = new ApiSchemaTypeRestriction();
					serialization.setType(ts.getType());
					sb.append(serialization.toString());
				}
			}
		}
		
		return sb.toString();
	}
}
