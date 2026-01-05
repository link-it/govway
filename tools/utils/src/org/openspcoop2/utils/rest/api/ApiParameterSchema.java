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

package org.openspcoop2.utils.rest.api;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsRuntimeException;

/**
 * ApiParameterSchema
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ApiParameterSchema {

	private static final String SEPARATOR_COMPLEX_TYPE = "|||";
	private static final String SEPARATOR_COMPLEX_TYPE_SPLIT = "\\|\\|\\|";
	
	private static final String PREFIX_FORMATO_NON_VALIDO = "Formato non valido (";
	
	private ApiParameterSchemaComplexType complexType = ApiParameterSchemaComplexType.simple;
	private List<ApiParameterTypeSchema> schemas = new ArrayList<>();
	
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
			fillApiParameterSchemaComplexType(info, schema);
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
	private static void fillApiParameterSchemaComplexType(String info, ApiParameterSchema schema) throws UtilsException {
		String[]tmp = info.split(SEPARATOR_COMPLEX_TYPE_SPLIT);
		if(tmp==null || tmp.length<2) {
			throw new UtilsException(PREFIX_FORMATO_NON_VALIDO+info+")");
		}
		String complexType = tmp[0];
		if(complexType==null) {
			throw new UtilsException(PREFIX_FORMATO_NON_VALIDO+info+"): complex type undefined");
		}
		complexType = complexType.trim();
		try {
			ApiParameterSchemaComplexType t = ApiParameterSchemaComplexType.valueOf(complexType);
			schema.setComplexType(t);
		}catch(Exception t) {
			throw new UtilsException(PREFIX_FORMATO_NON_VALIDO+info+"): complex type '"+complexType+"' invalid: "+t.getMessage());
		}
		for (int i = 1; i < tmp.length; i++) {
			String tmpI= tmp[i];
			if(tmpI==null) {
				throw new UtilsException(PREFIX_FORMATO_NON_VALIDO+info+"): i="+i+" undefined?");
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
			}catch(Exception t) {
				throw new UtilsException(PREFIX_FORMATO_NON_VALIDO+info+"): i="+i+" '"+tmpI+"' invalid: "+t.getMessage());
			}
		}
	}
	public static String toString(ApiParameterSchema schema) {
		return schema.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		if(this.complexType==null || ApiParameterSchemaComplexType.simple.equals(this.complexType)) {
			fillSimpleType(sb);
		}
		else {
			fillComplexType(sb);
		}
		
		return sb.toString();
	}
	private void fillSimpleType(StringBuilder sb) {
		if(this.schemas==null || this.schemas.isEmpty()) {
			throw new UtilsRuntimeException("Schema undefined");
		}
		if(this.schemas.size()!=1) {
			throw new UtilsRuntimeException("Uncorrect Schema (found:"+this.schemas.size()+")");
		}
		ApiParameterTypeSchema ts = this.schemas.get(0);
		if(ts==null) {
			throw new UtilsRuntimeException("Schema is empty?");
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
	private void fillComplexType(StringBuilder sb) {
		sb.append(this.complexType);
		if(this.schemas==null || this.schemas.isEmpty()) {
			throw new UtilsRuntimeException("Schema undefined");
		}
		for (ApiParameterTypeSchema ts : this.schemas) {
			sb.append(SEPARATOR_COMPLEX_TYPE);
			if(ts==null) {
				throw new UtilsRuntimeException("Schema is empty?");
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
}
