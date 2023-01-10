/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.converter;

import java.text.MessageFormat;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * FileSizeConverter
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class FileSizeConverter implements Converter {
	
	private final static double KB = 1024;
	private final static double MB = 1048576;
	
	@Override
	public Object getAsObject(FacesContext ctx, UIComponent component, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent component, Object value) {
		
		if(value instanceof byte[]){
			int len = ((byte[]) value).length;
			return convert(len);
		}
		
		
		if(value instanceof Number){
			//il valore e' in byte
			Double len = ((Number) value).doubleValue();
			return convert(len);
		}
		
		if(value instanceof String)
			return value.toString();
		
		return "";
	}

	private String convert(double len){
		MessageFormat mf = new MessageFormat("{0,number,#.##}");
		String res = "";
		long d = Math.round(len/FileSizeConverter.KB); 
		if(d<=1){
			//byte
			Object[] objs = {len};
			res = mf.format(objs);
			res += " B";
		}else if(d>1 && d<1000){
			//kilo byte
			Object[] objs = {len/FileSizeConverter.KB};
			res = mf.format(objs);
			res += " KB";
		}else{
			//mega byte
			Object[] objs = {len/FileSizeConverter.MB};
			res = mf.format(objs);
			res += " MB";
		}
		return res;
	}

}
