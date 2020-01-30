/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.engine.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.openspcoop2.monitor.engine.exceptions.EngineException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;

/**
 * ContentFormatter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContentFormatter {

	private static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	
	public static void isSupported(Object o) throws EngineException{
		if(
			! (
				(o instanceof String) || 
				(o instanceof Integer) ||
				(o instanceof Long) ||
				(o instanceof Double) ||
				(o instanceof Float) ||
				(o instanceof Boolean) ||
				(o instanceof Date) ||
				(o instanceof Calendar) ||
				(o instanceof Timestamp)
			)
		){
			throw new EngineException("Tipo ["+o.getClass().getName()+"] non supportato. I tipi supportati sono: "+
					String.class.getName()+","+
					Integer.class.getName()+","+
					Long.class.getName()+","+
					Double.class.getName()+","+
					Float.class.getName()+","+
					Boolean.class.getName()+","+
					Date.class.getName()+","+
					Calendar.class.getName()+","+
					Timestamp.class.getName()+"");
		}	
	}
	public static void isSupported(Object ... collections) throws EngineException{
		for (int i = 0; i < collections.length; i++) {
			ContentFormatter.isSupported(collections[i]);
		}		
	}
	public static void isSupported(Collection<?> collections) throws EngineException{
		for (Iterator<?> iterator = collections.iterator(); iterator.hasNext();) {
			Object object = iterator.next();
			ContentFormatter.isSupported(object);
		}		
	}
	
	public static Collection<String> toString(Object [] o) throws EngineException{
		List<String> l = new ArrayList<String>();
		for (int i = 0; i < o.length; i++) {
			l.add(toString(o[i]));
		}
		return l;
	}
	public static String toString(Object o) throws EngineException{
		isSupported(o);
		if(o.getClass().getName().equals(String.class.getName())){
			return (String)o;
		}
		else if(o.getClass().getName().equals(int.class.getName()) ||
				o.getClass().getName().equals(Integer.class.getName())){
			return toString((Integer)o);
		}
		else if(o.getClass().getName().equals(long.class.getName()) ||
				o.getClass().getName().equals(Long.class.getName())){
			return toString((Long)o);
		}
		else if(o.getClass().getName().equals(double.class.getName()) ||
				o.getClass().getName().equals(Double.class.getName())){
			return toString((Double)o);
		}
		else if(o.getClass().getName().equals(float.class.getName()) ||
				o.getClass().getName().equals(Float.class.getName())){
			return toString((Float)o);
		}
		else if(o.getClass().getName().equals(boolean.class.getName()) ||
				o.getClass().getName().equals(Boolean.class.getName())){
			return toString((Boolean)o);
		}
		else if(o.getClass().getName().equals(Date.class.getName())){
			return toString((Date)o);
		}
		else if(o.getClass().getName().equals(Calendar.class.getName())){
			return toString((Calendar)o);
		}
		else if(o.getClass().getName().equals(Timestamp.class.getName())){
			return toString((Timestamp)o);
		}
		throw new EngineException("Tipo ["+o.getClass().getName()+"] non supportato");
	}
	
	public static String toString(int intValue){
		return intValue+"";
	}
	public static String toString(Integer intValue){
		return ContentFormatter.toString(intValue.intValue());
	}
	
	
	public static String toString(long longValue){
		return longValue+"";
	}
	public static String toString(Long longValue){
		return ContentFormatter.toString(longValue.longValue());
	}
	
	
	public static String toString(double doubleValue){
		return doubleValue+"";
	}
	public static String toString(Double doubleValue){
		return ContentFormatter.toString(doubleValue.doubleValue());
	}
	
	
	public static String toString(float floatValue){
		return floatValue+"";
	}
	public static String toString(Float floatValue){
		return ContentFormatter.toString(floatValue.floatValue());
	}
	
	
	public static String toString(boolean b){
		if(b)
			return "t";
		else
			return "f";
	}
	public static String toString(Boolean b){
		return ContentFormatter.toString(b.booleanValue());
	}
	

	public static String toString(Date date){
		SimpleDateFormat dateformat = DateUtils.getDefaultDateTimeFormatter(DATE_FORMAT);
		return dateformat.format(date);
	}
	
	public static String toString(Calendar calendar){
		SimpleDateFormat dateformat = DateUtils.getDefaultDateTimeFormatter(DATE_FORMAT);
		return dateformat.format(calendar.getTime());
	}
	
	public static String toString(Timestamp t){
		SimpleDateFormat dateformat = DateUtils.getDefaultDateTimeFormatter(DATE_FORMAT);
		return dateformat.format(t);
	}
	
	public static Integer toInteger(String v) throws EngineException{
		try{
			Integer i = null;
			if(v!=null){
				i = Integer.parseInt(v);
			}
			return i;
		}catch(Exception e){
			throw new EngineException("Converting error: "+ e.getMessage(),e);
		}
	}
	public static int toPrimitiveInt(String v) throws EngineException{
		try{
			int i = 0;
			if(v!=null){
				i = Integer.parseInt(v);
			}
			return i;
		}catch(Exception e){
			throw new EngineException("Converting error: "+ e.getMessage(),e);
		}
	}
	public static Long toLong(String v) throws EngineException{
		try{
			Long l = null;
			if(v!=null){
				l = Long.parseLong(v);
			}
			return l;
		}catch(Exception e){
			throw new EngineException("Converting error: "+ e.getMessage(),e);
		}
	}
	public static long toPrimitiveLong(String v) throws EngineException{
		try{
			long l = 0;
			if(v!=null){
				l = Long.parseLong(v);
			}
			return l;
		}catch(Exception e){
			throw new EngineException("Converting error: "+ e.getMessage(),e);
		}
	}
	public static Double toDouble(String v) throws EngineException{
		try{
			Double d = null;
			if(v!=null){
				d = Double.parseDouble(v);
			}
			return d;
		}catch(Exception e){
			throw new EngineException("Converting error: "+ e.getMessage(),e);
		}
	}
	public static double toPrimitiveDouble(String v) throws EngineException{
		try{
			double d = 0;
			if(v!=null){
				d = Double.parseDouble(v);
			}
			return d;
		}catch(Exception e){
			throw new EngineException("Converting error: "+ e.getMessage(),e);
		}
	}
	public static Float toFloat(String v) throws EngineException{
		try{
			Float f = null;
			if(v!=null){
				f = Float.parseFloat(v);
			}
			return f;
		}catch(Exception e){
			throw new EngineException("Converting error: "+ e.getMessage(),e);
		}
	}
	public static float toPrimitiveFloat(String v) throws EngineException{
		try{
			float f = 0;
			if(v!=null){
				f = Float.parseFloat(v);
			}
			return f;
		}catch(Exception e){
			throw new EngineException("Converting error: "+ e.getMessage(),e);
		}
	}
	public static Boolean toBoolean(String v) throws EngineException{
		try{
			Boolean b = null;
			if(v!=null){
				b = Boolean.parseBoolean(v);
			}
			return b;
		}catch(Exception e){
			throw new EngineException("Converting error: "+ e.getMessage(),e);
		}
	}
	public static boolean toPrimitiveBoolean(String v) throws EngineException{
		try{
			boolean b = false;
			if(v!=null){
				b = Boolean.parseBoolean(v);
			}
			return b;
		}catch(Exception e){
			throw new EngineException("Converting error: "+ e.getMessage(),e);
		}
	}
	public static Date toDate(String v) throws EngineException{
		try{
			Date d = null;
			if(v!=null){
				SimpleDateFormat dateformat = DateUtils.getDefaultDateTimeFormatter(DATE_FORMAT);
				d = dateformat.parse(v);
			}
			return d;
		}catch(Exception e){
			throw new EngineException("Converting error: "+ e.getMessage(),e);
		}
	}
	public static Calendar toCalendar(String v) throws EngineException{
		try{
			Calendar c = null;
			if(v!=null){
				c = DateManager.getCalendar();
				c.setTime(toDate(v));
			}
			return c;
		}catch(Exception e){
			throw new EngineException("Converting error: "+ e.getMessage(),e);
		}
	}
	public static Timestamp toTimestamp(String v) throws EngineException{
		try{
			Timestamp t = null;
			if(v!=null){
				t = new Timestamp(toDate(v).getTime());
			}
			return t;
		}catch(Exception e){
			throw new EngineException("Converting error: "+ e.getMessage(),e);
		}
	}
		
}
