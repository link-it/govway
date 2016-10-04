/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.generic_project.beans;

import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.formatter.BooleanTypeFormatter;
import org.openspcoop2.generic_project.expression.impl.formatter.ByteTypeFormatter;
import org.openspcoop2.generic_project.expression.impl.formatter.CalendarTypeFormatter;
import org.openspcoop2.generic_project.expression.impl.formatter.CharacterTypeFormatter;
import org.openspcoop2.generic_project.expression.impl.formatter.DateTypeFormatter;
import org.openspcoop2.generic_project.expression.impl.formatter.DoubleTypeFormatter;
import org.openspcoop2.generic_project.expression.impl.formatter.FloatTypeFormatter;
import org.openspcoop2.generic_project.expression.impl.formatter.IntegerTypeFormatter;
import org.openspcoop2.generic_project.expression.impl.formatter.LongTypeFormatter;
import org.openspcoop2.generic_project.expression.impl.formatter.NullTypeFormatter;
import org.openspcoop2.generic_project.expression.impl.formatter.ObjectFormatter;
import org.openspcoop2.generic_project.expression.impl.formatter.ShortTypeFormatter;
import org.openspcoop2.generic_project.expression.impl.formatter.StringTypeFormatter;
import org.openspcoop2.generic_project.expression.impl.formatter.TimestampTypeFormatter;
import org.openspcoop2.generic_project.expression.impl.formatter.URITypeFormatter;
import org.openspcoop2.utils.TipiDatabase;

/**
 * ConstantField
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConstantField extends Field {

	private Object fieldValue;
	private ObjectFormatter objectFormatter= null;
	
	public ConstantField(String fieldName, Object fieldValue, Class<?> fieldType) throws ExpressionException{
		super(fieldName, fieldType, ConstantField.class.getSimpleName(), ConstantField.class);
		
		this.fieldValue = fieldValue;
		
		// Creo un formatte con alcuni tipi tra quelli supportati complessivamente
		this.objectFormatter = new ObjectFormatter(new BooleanTypeFormatter(),
				new CalendarTypeFormatter(), new DateTypeFormatter(), new TimestampTypeFormatter(),
				new CharacterTypeFormatter(), new StringTypeFormatter(),
				new ByteTypeFormatter(), new ShortTypeFormatter(),
				new IntegerTypeFormatter(), new LongTypeFormatter(),
				new DoubleTypeFormatter(), new FloatTypeFormatter(),
				new URITypeFormatter(), new NullTypeFormatter());
		
		this.objectFormatter.isSupported(this.fieldValue);
			
	}

	public String getConstantValue(TipiDatabase databaseType) throws ExpressionException {
		
		return this.objectFormatter.toSQLString(this.fieldValue, databaseType);
		
	}
	public String getAlias() {
		return this.getFieldName();
	}
	

	@Override
	public String toString(int indent){
		
		StringBuffer indentBuffer = new StringBuffer();
		for (int i = 0; i < indent; i++) {
			indentBuffer.append("	");
		}
		
		StringBuffer bf = new StringBuffer(super.toString(indent));
		
		bf.append(indentBuffer.toString());
		bf.append("- fieldValue: "+this.fieldValue);
		bf.append("\n");
				
		return bf.toString();
	}

}
