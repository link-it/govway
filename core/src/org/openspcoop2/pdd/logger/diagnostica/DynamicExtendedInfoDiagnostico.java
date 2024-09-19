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

package org.openspcoop2.pdd.logger.diagnostica;

import org.openspcoop2.core.commons.CoreException;

/**     
 * DynamicExtendedInfoDiagnostico
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DynamicExtendedInfoDiagnostico {

	private DynamicExtendedInfoDiagnosticoType type;
	private int diagnosticPosition;
	private String value;
	
	public DynamicExtendedInfoDiagnostico(DynamicExtendedInfoDiagnosticoType type,int diagnosticPosition,String value){
		this.type = type;
		this.diagnosticPosition = diagnosticPosition;
		this.value = value;
	}
	
	public DynamicExtendedInfoDiagnosticoType getType() {
		return this.type;
	}

	public void setType(DynamicExtendedInfoDiagnosticoType type) {
		this.type = type;
	}

	public int getDiagnosticPosition() {
		return this.diagnosticPosition;
	}

	public void setDiagnosticPosition(int diagnosticPosition) {
		this.diagnosticPosition = diagnosticPosition;
	}
	
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String convertToDBValue(){
		StringBuilder bf = new StringBuilder();
		bf.append(this.type.getValue());
		bf.append(CostantiMappingDiagnostici.DIAGNOSTIC_WITH_DYNAMIC_INFO_TYPE_SEPARATOR);
		bf.append(this.diagnosticPosition);
		bf.append(CostantiMappingDiagnostici.DIAGNOSTIC_WITH_DYNAMIC_INFO_TYPE_SEPARATOR);
		bf.append(this.value);
		return bf.toString();
	}
	
	public static DynamicExtendedInfoDiagnostico convertoFromDBColumnValue(String dbValue) throws CoreException{
		
		if(dbValue==null){
			throw new CoreException("Parameter dbValue not defined");
		}
		if(!dbValue.contains(CostantiMappingDiagnostici.DIAGNOSTIC_WITH_DYNAMIC_INFO_TYPE_SEPARATOR)){
			throw new CoreException("Parameter dbValue with wrong format ("+CostantiMappingDiagnostici.DIAGNOSTIC_WITH_DYNAMIC_INFO_TYPE_SEPARATOR+")");
		}
		
		int indexType = dbValue.indexOf(CostantiMappingDiagnostici.DIAGNOSTIC_WITH_DYNAMIC_INFO_TYPE_SEPARATOR);
		if(indexType<=0){
			throw new CoreException("Parameter dbValue with wrong format (indexType:"+indexType+")");
		}
		if(indexType==dbValue.length()){
			throw new CoreException("Parameter dbValue with wrong format (indexType:"+indexType+" equals length)");
		}
		String tipo = dbValue.substring(0, indexType);
		DynamicExtendedInfoDiagnosticoType type = null;
		try{
			type = DynamicExtendedInfoDiagnosticoType.getEnum(tipo);
		}catch(Exception e){
			throw new CoreException("Parameter dbValue with wrong format (type unknwon '"+tipo+"')");
		}
		
		int indexPosition = dbValue.indexOf(CostantiMappingDiagnostici.DIAGNOSTIC_WITH_DYNAMIC_INFO_TYPE_SEPARATOR,(tipo.length()+1));
		String prefixIndexPosition = "Parameter dbValue with wrong format (indexPosition:"+indexPosition;
		if(indexPosition<=0){
			throw new CoreException(prefixIndexPosition+")");
		}
		if(indexPosition==dbValue.length()){
			throw new CoreException(prefixIndexPosition+" equals length)");
		}
		String tmpPosition = dbValue.substring((tipo.length()+1), indexPosition);
		int position = -1;
		try{
			position = Integer.parseInt(tmpPosition);
		}catch(Exception e){
			throw new CoreException(prefixIndexPosition+" format to int ["+tmpPosition+"] error) "+e.getMessage(),e);
		}
		
		String value = dbValue.substring((indexPosition+1), dbValue.length());
		if(value==null || "".equals(value)){
			throw new CoreException("Parameter dbValue with wrong format (value undefined)");
		}
		
		return new DynamicExtendedInfoDiagnostico(type, position, value);
		
	}
}
