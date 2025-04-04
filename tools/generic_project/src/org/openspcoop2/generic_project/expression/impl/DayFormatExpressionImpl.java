/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.generic_project.expression.impl;

import org.openspcoop2.generic_project.beans.ComplexField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.expression.impl.formatter.IObjectFormatter;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.sql.DayFormatEnum;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectCore;

/**
 * DayFormatExpressionImpl
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DayFormatExpressionImpl extends AbstractCommonFieldValueBaseExpressionImpl {

	protected DayFormatEnum dayFormatEnum;
	
	public DayFormatExpressionImpl(IObjectFormatter objectFormatter,IField field,String value,DayFormatEnum dayFormatEnum){
		super(objectFormatter, field, value);
		this.dayFormatEnum = dayFormatEnum;
	}

	@Override
	public String toString(){
		try {
			StringBuilder bf = new StringBuilder();
			if(isNot()){
				bf.append("( NOT ");
			}
			bf.append("( ");
	
			SQLQueryObjectCore sqlQueryObject = (SQLQueryObjectCore) SQLObjectFactory.createSQLQueryObject(TipiDatabase.POSTGRESQL); // lo uso come default
			
			String prefix = sqlQueryObject.getExtractDayFormatFromTimestampFieldPrefix(this.dayFormatEnum);
			bf.append(prefix);
			
			if(this.field instanceof ComplexField){
				ComplexField cf = (ComplexField) this.field;
				if(cf.getFather()!=null){
					bf.append(cf.getFather().getFieldName());
				}else{
					bf.append(this.field.getClassName());
				}
			}else{
				bf.append(this.field.getClassName());
			}
			bf.append(".");
			bf.append(this.field.getFieldName());
	
			String suffix = sqlQueryObject.getExtractDayFormatFromTimestampFieldSuffix(this.dayFormatEnum);
			bf.append(" ");
			bf.append(suffix);
			
			bf.append(" = '");
			
			bf.append(this.value);
			bf.append("'");
			
			bf.append(" )");
			if(isNot()){
				bf.append(" )");
			}
			return bf.toString();
		}catch(Exception e){
			LoggerWrapperFactory.getLogger(DayFormatExpressionImpl.class).error(e.getMessage(),e);
			return " Errore DateTimePart: "+e.getMessage();
		}
	}
	

}
