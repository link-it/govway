package org.openspcoop2.web.monitor.core.converter;

import java.text.MessageFormat;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

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
