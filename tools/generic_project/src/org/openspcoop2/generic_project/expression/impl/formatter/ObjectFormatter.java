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
package org.openspcoop2.generic_project.expression.impl.formatter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.utils.TipiDatabase;

/**
 * ObjectFormatter
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ObjectFormatter implements IObjectFormatter {

	private List<ITypeFormatter<?>> typesFormatter = new ArrayList<ITypeFormatter<?>>();
		
	public ObjectFormatter() throws ExpressionException{
		try{
			// default
			this.typesFormatter.add(new BooleanTypeFormatter());
			this.typesFormatter.add(new CalendarTypeFormatter());
			this.typesFormatter.add(new DateTypeFormatter());
			this.typesFormatter.add(new TimestampTypeFormatter());
			this.typesFormatter.add(new CharacterTypeFormatter());
			this.typesFormatter.add(new StringTypeFormatter());
			this.typesFormatter.add(new ByteTypeFormatter());
			this.typesFormatter.add(new ShortTypeFormatter());
			this.typesFormatter.add(new IntegerTypeFormatter());
			this.typesFormatter.add(new LongTypeFormatter());
			this.typesFormatter.add(new DoubleTypeFormatter());
			this.typesFormatter.add(new FloatTypeFormatter());
			this.typesFormatter.add(new EnumTypeFormatter());
			this.typesFormatter.add(new URITypeFormatter());
			this.typesFormatter.add(new ByteArrayTypeFormatter());
		}catch(Exception e){
			throw new ExpressionException(e);
		}
	}
	public ObjectFormatter(ITypeFormatter<?> ... types){
		for (int i = 0; i < types.length; i++) {
			this.typesFormatter.add(types[i]);
		}
	}
	
	@Override
	public void isSupported(Object o) throws ExpressionException{
		boolean supported = false;
		for (Iterator<ITypeFormatter<?>> iterator = this.typesFormatter.iterator(); iterator.hasNext();) {
			ITypeFormatter<?> type = iterator.next();
			try{
				o.getClass().asSubclass(type.getTypeSupported());
				supported = true;
				break;
			}catch(Exception e){}
		}
		if(!supported){
			throwTypeUnsupported(o);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public String toString(Object o) throws ExpressionException {
		for (Iterator<ITypeFormatter<?>> iterator = this.typesFormatter.iterator(); iterator.hasNext();) {
			@SuppressWarnings("rawtypes")
			ITypeFormatter type = iterator.next();
			boolean isType = false;
			try{
				o.getClass().asSubclass(type.getTypeSupported());
				isType = true;
			}catch(Exception e){}
			if(isType){
				return type.toString(o);
			}
		}
		throwTypeUnsupported(o);
		return null;
	}
	
	@Override
	public String toSQLString(Object o) throws ExpressionException {
		return this.toSQLString(o, TipiDatabase.DEFAULT);
	}
	@Override
	@SuppressWarnings("unchecked")
	public String toSQLString(Object o, TipiDatabase databaseType) throws ExpressionException {
		for (Iterator<ITypeFormatter<?>> iterator = this.typesFormatter.iterator(); iterator.hasNext();) {
			@SuppressWarnings("rawtypes")
			ITypeFormatter type = iterator.next();
			boolean isType = false;
			try{
				o.getClass().asSubclass(type.getTypeSupported());
				isType = true;
			}catch(Exception e){}
			if(isType){
				return type.toSQLString(o, databaseType);
			}
		}
		throwTypeUnsupported(o);
		return null;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Object toObject(String o,Class<?> c) throws ExpressionException {
		for (Iterator<ITypeFormatter<?>> iterator = this.typesFormatter.iterator(); iterator.hasNext();) {
			@SuppressWarnings("rawtypes")
			ITypeFormatter type = iterator.next();
			boolean isType = false;
			try{
				c.asSubclass(type.getTypeSupported());
				isType = true;
			}catch(Exception e){}
			if(isType){
				type.toObject(o,c);
			}
		}
		throwTypeUnsupported(c);
		return null;
	}
	
	private void throwTypeUnsupported(Object o) throws ExpressionException {
		StringBuffer bf = new StringBuffer();
		for (Iterator<ITypeFormatter<?>> iterator = this.typesFormatter.iterator(); iterator.hasNext();) {
			ITypeFormatter<?> type = iterator.next();
			if(bf.length()>0){
				bf.append(" , ");
			}
			bf.append(type.getTypeSupported());
		}
		throw new ExpressionException("Unsupported ["+o.getClass().getName()+"] type. Supported types are: "+bf.toString());
	}
}
