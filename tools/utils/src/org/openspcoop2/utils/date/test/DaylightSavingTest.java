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
package org.openspcoop2.utils.date.test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DaylightSavingUtils;
import org.openspcoop2.utils.date.TimeTransitionType;
import org.openspcoop2.utils.date.TimeType;

/**
 * DaylightSavingTest
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DaylightSavingTest {

	public static void main(String[] args) throws UtilsException {
		
		zoneIdTest();
				
		timeChangePendingTodayTest();
		
		oraLegaleToOraSolareTest();
		
		oraSolareToOraLegaleTest();
		
		debug("\n\n** TESTSUITE COMPLETATA **");
	}

	public static void zoneIdTest() throws UtilsException {
		
		debug("\n\n** ZoneId **");
		ZoneId zoneId = DaylightSavingUtils.getZoneIdDefault();
		if(!ZoneId.of("Europe/Rome").equals(zoneId)) {
			throw new UtilsException("Expected zone id default 'Europe/Rome'; found: "+zoneId);
		}
		
	}
	
	public static void timeChangePendingTodayTest() throws UtilsException {
		
		ZoneId zoneId = DaylightSavingUtils.getZoneIdDefault();
		
		for (int i = 0; i < 3; i++) {
			
			String format = null;
			String formatOffset = null;
			DateTimeFormatter formatter = null;
			DateTimeFormatter formatterOffset = null;
			if(i==1) {
				format = "yyyyMMddHHmmss";
				formatOffset = format + "XXX";
			}
			else if(i==2){
				String intFormat = "yyyy-MM-dd HHmmss";
				formatter = DateTimeFormatter.ofPattern(intFormat);
				String intFormatOffset = intFormat + "XXX";
				formatterOffset = DateTimeFormatter.ofPattern(intFormatOffset);
			}
			
			String data = null;
			if(i==0) {
				data = "2024-10-27 02:56:00";	
			}
			else if(i==1) {
				data = "20241027025600";	
			}
			else {
				data = "2024-10-27 025600";
			}
			debug("\n\n** timeChangePendingToday "+data+"  format["+format+"] formatter["+(formatter!=null)+"] **");
			timeChangePendingToday(data, null, null, TimeTransitionType.FROM_DAYLIGHT_SAVING_TO_STANDARD_TIME, format, formatter);
			debug("\n\n** timeChangePendingToday "+data+"+02  format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **");
			timeChangePendingToday(data, "+02", null, TimeTransitionType.FROM_DAYLIGHT_SAVING_TO_STANDARD_TIME, formatOffset, formatterOffset);
			debug("\n\n** timeChangePendingToday "+data+"+02:00  format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **");
			timeChangePendingToday(data, "+02:00", null, TimeTransitionType.FROM_DAYLIGHT_SAVING_TO_STANDARD_TIME, formatOffset, formatterOffset);
			debug("\n\n** timeChangePendingToday "+data+" with zone id  format["+format+"] formatter["+(formatter!=null)+"] **");
			timeChangePendingToday(data, null, zoneId, TimeTransitionType.FROM_DAYLIGHT_SAVING_TO_STANDARD_TIME, format, formatter);
			debug("\n\n** timeChangePendingToday "+data+"+02 with zone id  format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **");
			timeChangePendingToday(data, "+02", zoneId, TimeTransitionType.FROM_DAYLIGHT_SAVING_TO_STANDARD_TIME, formatOffset, formatterOffset);
			debug("\n\n** timeChangePendingToday "+data+"+02:00 with zone id  format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **");
			timeChangePendingToday(data, "+02:00", zoneId, TimeTransitionType.FROM_DAYLIGHT_SAVING_TO_STANDARD_TIME, formatOffset, formatterOffset);
			// con lo +01 l'ora è già scattata e sono nelle 02:56 dell'ora solare
			debug("\n\n** timeChangePendingToday "+data+"+01  format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **"); 
			timeChangePendingToday(data, "+01", null, null, formatOffset, formatterOffset);
			debug("\n\n** timeChangePendingToday "+data+"+01:00  format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **");
			timeChangePendingToday(data, "+01:00", null, null, formatOffset, formatterOffset);
			debug("\n\n** timeChangePendingToday "+data+"+01 with zone id  format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **");
			timeChangePendingToday(data, "+01", zoneId, null, formatOffset, formatterOffset);
			debug("\n\n** timeChangePendingToday "+data+"+01:00 with zone id  format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **");
			timeChangePendingToday(data, "+01:00", zoneId, null, formatOffset, formatterOffset);
			
			if(i==0) {
				data = "2025-03-30 01:56:00";
			}
			else if(i==1) {
				data = "20250330015600";
			}
			else {
				data = "2025-03-30 015600";
			}
			debug("\n\n** timeChangePendingToday "+data+"  format["+format+"] formatter["+(formatter!=null)+"] **");
			timeChangePendingToday(data, null, null, TimeTransitionType.FROM_STANDARD_TO_DAYLIGHT_SAVING_TIME, format, formatter);
			debug("\n\n** timeChangePendingToday "+data+"+01  format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **");
			timeChangePendingToday(data, "+01", null, TimeTransitionType.FROM_STANDARD_TO_DAYLIGHT_SAVING_TIME, formatOffset, formatterOffset);
			debug("\n\n** timeChangePendingToday "+data+"+01:00  format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **");
			timeChangePendingToday(data, "+01:00", null, TimeTransitionType.FROM_STANDARD_TO_DAYLIGHT_SAVING_TIME, formatOffset, formatterOffset);
			debug("\n\n** timeChangePendingToday "+data+" with zone id  format["+format+"] formatter["+(formatter!=null)+"] **");
			timeChangePendingToday(data, null, zoneId, TimeTransitionType.FROM_STANDARD_TO_DAYLIGHT_SAVING_TIME, format, formatter);
			debug("\n\n** timeChangePendingToday "+data+"+01 with zone id  format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **");
			timeChangePendingToday(data, "+01", zoneId, TimeTransitionType.FROM_STANDARD_TO_DAYLIGHT_SAVING_TIME, formatOffset, formatterOffset);
			debug("\n\n** timeChangePendingToday "+data+"+01:00 with zone id  format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **");
			timeChangePendingToday(data, "+01:00", zoneId, TimeTransitionType.FROM_STANDARD_TO_DAYLIGHT_SAVING_TIME, formatOffset, formatterOffset);
			// con lo +02 l'ora è già scattata e si avanza di un'ora
			if(i==0) {
				data = "2025-03-30 03:00:01";
			}
			else if(i==1) {
				data = "20250330030001";
			}
			else {
				data = "2025-03-30 030001";
			}
			debug("\n\n** timeChangePendingToday "+data+"+02  format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **"); 
			timeChangePendingToday(data, "+02", null, null, formatOffset, formatterOffset);
			debug("\n\n** timeChangePendingToday "+data+"+02:00  format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **");
			timeChangePendingToday(data, "+02:00", null, null, formatOffset, formatterOffset);
			debug("\n\n** timeChangePendingToday "+data+"+02 with zone id  format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **");
			timeChangePendingToday(data, "+02", zoneId, null, formatOffset, formatterOffset);
			debug("\n\n** timeChangePendingToday "+data+"+02:00 with zone id  format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **");
			timeChangePendingToday(data, "+02:00", zoneId, null, formatOffset, formatterOffset);
			
			if(i==0) {
				data = "2024-11-20 02:56:00";
			}
			else if(i==1) {
				data = "20241120025600";
			}
			else {
				data = "2024-11-20 025600";
			}
			debug("\n\n** timeChangePendingToday "+data+"  format["+format+"] formatter["+(formatter!=null)+"] **");
			timeChangePendingToday(data, null, null, null, format, formatter);
			debug("\n\n** timeChangePendingToday "+data+"+01  format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **"); 
			timeChangePendingToday(data, "+01", null, null, formatOffset, formatterOffset);
			debug("\n\n** timeChangePendingToday "+data+"+01:00  format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **");
			timeChangePendingToday(data, "+01:00", null, null, formatOffset, formatterOffset);
			debug("\n\n** timeChangePendingToday "+data+"+02  format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **"); 
			timeChangePendingToday(data, "+02", null, null, formatOffset, formatterOffset);
			debug("\n\n** timeChangePendingToday "+data+"+02:00  format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **");
			timeChangePendingToday(data, "+02:00", null, null, formatOffset, formatterOffset);
			
			
		}
		
		
	}
	
	public static void oraLegaleToOraSolareTest() throws UtilsException {
		
		ZoneId zoneId = DaylightSavingUtils.getZoneIdDefault();
		
		for (int i = 0; i < 3; i++) {
			
			String format = null;
			String formatOffset = null;
			DateTimeFormatter formatter = null;
			DateTimeFormatter formatterOffset = null;
			if(i==1) {
				format = "yyyyMMddHHmmss";
				formatOffset = format + "XXX";
			}
			else if(i==2){
				String intFormat = "yyyy-MM-dd HHmmss";
				formatter = DateTimeFormatter.ofPattern(intFormat);
				String intFormatOffset = intFormat + "XXX";
				formatterOffset = DateTimeFormatter.ofPattern(intFormatOffset);
			}

			debug("\n\n** oraLegaleToOraSolare (+02) format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **");
			oraLegaleToOraSolare("+02", null, formatOffset, formatterOffset);
			debug("\n\n** oraLegaleToOraSolare (+02) with zone id format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **");
			oraLegaleToOraSolare("+02", zoneId, formatOffset, formatterOffset);
			
			debug("\n\n** oraLegaleToOraSolare (+02:00) format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **");
			oraLegaleToOraSolare("+02:00", null, formatOffset, formatterOffset);
			debug("\n\n** oraLegaleToOraSolare (+02:00) with zone id format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **");
			oraLegaleToOraSolare("+02:00", zoneId, formatOffset, formatterOffset);
			
			
			
			debug("\n\n** oraLegaleToOraSolare format["+format+"] formatter["+(formatter!=null)+"] **");
			oraLegaleToOraSolareWithoutOffset(null, format, formatter);
			debug("\n\n** oraLegaleToOraSolare with zone id format["+format+"] formatter["+(formatter!=null)+"] **");
			oraLegaleToOraSolareWithoutOffset(zoneId, format, formatter);
		}
		
	}
	
	public static void oraSolareToOraLegaleTest() throws UtilsException {
		
		ZoneId zoneId = DaylightSavingUtils.getZoneIdDefault();
		
		for (int i = 0; i < 3; i++) {
			
			String format = null;
			String formatOffset = null;
			DateTimeFormatter formatter = null;
			DateTimeFormatter formatterOffset = null;
			if(i==1) {
				format = "yyyyMMddHHmmss";
				formatOffset = format + "XXX";
			}
			else if(i==2){
				String intFormat = "yyyy-MM-dd HHmmss";
				formatter = DateTimeFormatter.ofPattern(intFormat);
				String intFormatOffset = intFormat + "XXX";
				formatterOffset = DateTimeFormatter.ofPattern(intFormatOffset);
			}
		
			
			debug("\n\n** oraSolareToOraLegale (+01) format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **");
			oraSolareToOraLegale("+01", null, formatOffset, formatterOffset);
			debug("\n\n** oraSolareToOraLegale (+01) with zone id format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **");
			oraSolareToOraLegale("+01", zoneId, formatOffset, formatterOffset);
			
			debug("\n\n** oraSolareToOraLegale (+01:00) format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **");
			oraSolareToOraLegale("+01:00", null, formatOffset, formatterOffset);
			debug("\n\n** oraSolareToOraLegale (+01:00) with zone id format["+formatOffset+"] formatter["+(formatterOffset!=null)+"] **");
			oraSolareToOraLegale("+01:00", zoneId, formatOffset, formatterOffset);
		
			
			
			debug("\n\n** oraSolareToOraLegale format["+format+"] formatter["+(formatter!=null)+"] **");
			oraSolareToOraLegaleWithoutOffset(null, format, formatter);
			debug("\n\n** oraSolareToOraLegale with zone id format["+format+"] formatter["+(formatter!=null)+"] **");
			oraSolareToOraLegaleWithoutOffset(zoneId, format, formatter);
		}
		

	}
	
	private static void timeChangePendingToday(String dataParam, String offset, ZoneId zoneId, TimeTransitionType expected, String format, DateTimeFormatter formatter) throws UtilsException {
		String data = offset!=null ? dataParam+offset : dataParam;
		debug("\tdate: "+data);
		boolean withoutOffset = offset==null;
		
		TimeTransitionType t = null;
		if(zoneId!=null) {
			if(formatter!=null) {
				t = withoutOffset ? DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data, zoneId, formatter) : DaylightSavingUtils.getTimeChangePendingToday(data, zoneId, formatter);
			}
			else if(format!=null) {
				t = withoutOffset ? DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data, zoneId, format) : DaylightSavingUtils.getTimeChangePendingToday(data, zoneId, format);
			}
			else {
				t = withoutOffset ? DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data, zoneId) : DaylightSavingUtils.getTimeChangePendingToday(data, zoneId);
			}
		}
		else {
			if(formatter!=null) {
				t = withoutOffset ? DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data, formatter) : DaylightSavingUtils.getTimeChangePendingToday(data, formatter);
			}
			else if(format!=null) {
				t = withoutOffset ? DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data, format) : DaylightSavingUtils.getTimeChangePendingToday(data, format);
			}
			else {
				t = withoutOffset ? DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data) : DaylightSavingUtils.getTimeChangePendingToday(data);
			}
		}
		debug("\tTipo: "+t);
		if(t!=null) {
			debug("\tDescrizione: "+t.getItaDescription());
		}
		if(expected==null) {
			if(t!=null) {
				throw new UtilsException("Expected null; found "+t);
			}
		}
		else if(!expected.equals(t)) {
			throw new UtilsException("Expected "+expected+"; found "+t);
		}
	}
	
	private static void oraLegaleToOraSolare(String offset, ZoneId zoneId, String format, DateTimeFormatter formatter) throws UtilsException {
		// Ora Legale che tra 4 minuti torna all'ora solare portando indietro di un'ora
		String data = null;
		TimeType type = null;
		if(formatter!=null) {
			data = "2024-10-27 025600"+offset;
			type = zoneId!=null ? DaylightSavingUtils.getTimeType(data,zoneId,formatter) : DaylightSavingUtils.getTimeType(data,formatter) ;
		}
		else if(format!=null) {
			data = "20241027025600"+offset;
			type = zoneId!=null ? DaylightSavingUtils.getTimeType(data,zoneId,format) : DaylightSavingUtils.getTimeType(data,format) ;
		}
		else {
			data = "2024-10-27 02:56:00"+offset;
			type = zoneId!=null ? DaylightSavingUtils.getTimeType(data,zoneId) : DaylightSavingUtils.getTimeType(data) ;
		}
		debug("\tdate: "+data);
		debug("\tTipo: "+type);
		debug("\tDescrizione: "+type.getItaDescription());
		if(!TimeType.DAYLIGHT_SAVING_TIME.equals(type)) {
			throw new UtilsException("Expected '"+TimeType.DAYLIGHT_SAVING_TIME+"'");
		}
		long m = -1;
		if(formatter!=null) {
			m = zoneId!=null ? DaylightSavingUtils.minutesUntilNextTransition(data, zoneId, formatter) : DaylightSavingUtils.minutesUntilNextTransition(data, formatter);
		}
		else if(format!=null) {
			m = zoneId!=null ? DaylightSavingUtils.minutesUntilNextTransition(data, zoneId, format) : DaylightSavingUtils.minutesUntilNextTransition(data, format);
		}
		else {
			m = zoneId!=null ? DaylightSavingUtils.minutesUntilNextTransition(data, zoneId) : DaylightSavingUtils.minutesUntilNextTransition(data);
		}
		long atteso = 4l;
		debug("\tMinutes nextTransition: "+m);
		if(m!=atteso) {
			throw new UtilsException("Expected "+atteso+" minutes; found "+m);
		}
		
        debug("\tAdd 4 minutes");
		TemporalAccessor d = null;
		if(formatter!=null) {
			d = DaylightSavingUtils.parseOffsetDateTime(data, formatter);
		}
		else if(format!=null) {
			d = DaylightSavingUtils.parseOffsetDateTime(data, format);
		}
		else {
			d = DaylightSavingUtils.parseOffsetDateTime(data);
		}
		OffsetDateTime dateTime = OffsetDateTime.from(d);
        OffsetDateTime updatedDateTime = dateTime.plusMinutes(4);
        if(formatter!=null) {
        	data = formatter.format(updatedDateTime);
        }
        else if(format!=null) {
        	data = DateTimeFormatter.ofPattern(format).format(updatedDateTime);
        }
        else {
        	data = DaylightSavingUtils.getOffsetFormatter().format(updatedDateTime);
        }
        debug("\tNew date: "+data);
		        
        if(formatter!=null) {
			type = zoneId!=null ? DaylightSavingUtils.getTimeType(data,zoneId,formatter) : DaylightSavingUtils.getTimeType(data,formatter) ;
		}
		else if(format!=null) {
			type = zoneId!=null ? DaylightSavingUtils.getTimeType(data,zoneId,format) : DaylightSavingUtils.getTimeType(data,format) ;
		}
		else {
			type = zoneId!=null ? DaylightSavingUtils.getTimeType(data,zoneId) : DaylightSavingUtils.getTimeType(data) ;
		}
		debug("\tTipo: "+type);
		debug("\tDescrizione: "+type.getItaDescription());
		if(!TimeType.STANDARD_TIME.equals(type)) {
			throw new UtilsException("Expected '"+TimeType.STANDARD_TIME+"'");
		}
		if(formatter!=null) {
			m = zoneId!=null ? DaylightSavingUtils.minutesUntilNextTransition(data, zoneId, formatter) : DaylightSavingUtils.minutesUntilNextTransition(data, formatter);
		}
		else if(format!=null) {
			m = zoneId!=null ? DaylightSavingUtils.minutesUntilNextTransition(data, zoneId, format) : DaylightSavingUtils.minutesUntilNextTransition(data, format);
		}
		else {
			m = zoneId!=null ? DaylightSavingUtils.minutesUntilNextTransition(data, zoneId) : DaylightSavingUtils.minutesUntilNextTransition(data);
		}
		atteso = 221760;
		debug("\tMinutes nextTransition: "+m);
		if(m!=atteso) {
			throw new UtilsException("Expected "+atteso+" minutes; found "+m);
		}
	}
	
	private static void oraSolareToOraLegale(String offset, ZoneId zoneId, String format, DateTimeFormatter formatter) throws UtilsException {
		// Ora Legale che tra 4 minuti torna all'ora solare portando indietro di un'ora
		String data = null;
		TimeType type = null;
		if(formatter!=null) {
			data = "2025-03-30 015600"+offset;
			type = zoneId!=null ? DaylightSavingUtils.getTimeType(data,zoneId,formatter) : DaylightSavingUtils.getTimeType(data,formatter) ;
		}
		else if(format!=null) {
			data = "20250330015600"+offset;
			type = zoneId!=null ? DaylightSavingUtils.getTimeType(data,zoneId,format) : DaylightSavingUtils.getTimeType(data,format) ;
		}
		else {
			data = "2025-03-30 01:56:00"+offset;
			type = zoneId!=null ? DaylightSavingUtils.getTimeType(data,zoneId) : DaylightSavingUtils.getTimeType(data) ;
		}
		debug("\tdate: "+data);
		debug("\tTipo: "+type);
		debug("\tDescrizione: "+type.getItaDescription());
		if(!TimeType.STANDARD_TIME.equals(type)) {
			throw new UtilsException("Expected '"+TimeType.STANDARD_TIME+"'");
		}
		long m = -1;
		if(formatter!=null) {
			m = zoneId!=null ? DaylightSavingUtils.minutesUntilNextTransition(data, zoneId, formatter) : DaylightSavingUtils.minutesUntilNextTransition(data, formatter);
		}
		else if(format!=null) {
			m = zoneId!=null ? DaylightSavingUtils.minutesUntilNextTransition(data, zoneId, format) : DaylightSavingUtils.minutesUntilNextTransition(data, format);
		}
		else {
			m = zoneId!=null ? DaylightSavingUtils.minutesUntilNextTransition(data, zoneId) : DaylightSavingUtils.minutesUntilNextTransition(data);
		}
		long atteso = 4l;
		debug("\tMinutes nextTransition: "+m);
		if(m!=atteso) {
			throw new UtilsException("Expected "+atteso+" minutes; found "+m);
		}
		
        debug("\tAdd 4 minutes");
        TemporalAccessor d = null;
		if(formatter!=null) {
			d = DaylightSavingUtils.parseOffsetDateTime(data, formatter);
		}
		else if(format!=null) {
			d = DaylightSavingUtils.parseOffsetDateTime(data, format);
		}
		else {
			d = DaylightSavingUtils.parseOffsetDateTime(data);
		}
		OffsetDateTime dateTime = OffsetDateTime.from(d);
        OffsetDateTime updatedDateTime = dateTime.plusMinutes(4);
        if(formatter!=null) {
        	data = formatter.format(updatedDateTime);
        }
        else if(format!=null) {
        	data = DateTimeFormatter.ofPattern(format).format(updatedDateTime);
        }
        else {
        	data = DaylightSavingUtils.getOffsetFormatter().format(updatedDateTime);
        }
        debug("\tNew date: "+data);
		        
        if(formatter!=null) {
			type = zoneId!=null ? DaylightSavingUtils.getTimeType(data,zoneId,formatter) : DaylightSavingUtils.getTimeType(data,formatter) ;
		}
		else if(format!=null) {
			type = zoneId!=null ? DaylightSavingUtils.getTimeType(data,zoneId,format) : DaylightSavingUtils.getTimeType(data,format) ;
		}
		else {
			type = zoneId!=null ? DaylightSavingUtils.getTimeType(data,zoneId) : DaylightSavingUtils.getTimeType(data) ;
		}
		debug("\tTipo: "+type);
		debug("\tDescrizione: "+type.getItaDescription());
		if(!TimeType.DAYLIGHT_SAVING_TIME.equals(type)) {
			throw new UtilsException("Expected '"+TimeType.DAYLIGHT_SAVING_TIME+"'");
		}
		if(formatter!=null) {
			m = zoneId!=null ? DaylightSavingUtils.minutesUntilNextTransition(data, zoneId, formatter) : DaylightSavingUtils.minutesUntilNextTransition(data, formatter);
		}
		else if(format!=null) {
			m = zoneId!=null ? DaylightSavingUtils.minutesUntilNextTransition(data, zoneId, format) : DaylightSavingUtils.minutesUntilNextTransition(data, format);
		}
		else {
			m = zoneId!=null ? DaylightSavingUtils.minutesUntilNextTransition(data, zoneId) : DaylightSavingUtils.minutesUntilNextTransition(data);
		}
		atteso = 302400;
		debug("\tMinutes nextTransition: "+m);
		if(m!=atteso) {
			throw new UtilsException("Expected "+atteso+" minutes; found "+m);
		}
	}
	
	
	private static void oraLegaleToOraSolareWithoutOffset(ZoneId zoneId, String format, DateTimeFormatter formatter) throws UtilsException {
		// Ora Legale che tra 4 minuti torna all'ora solare portando indietro di un'ora
		String data = null;
		TimeTransitionType type = null;
		if(formatter!=null) {
			data = "2024-10-27 025600";
			type = zoneId!=null ? DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data,zoneId,formatter) : DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data,formatter) ;
		}
		else if(format!=null) {
			data = "20241027025600";
			type = zoneId!=null ? DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data,zoneId,format) : DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data,format) ;
		}
		else {
			data = "2024-10-27 02:56:00";
			type = zoneId!=null ? DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data,zoneId) : DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data) ;
		}
		debug("\tdate: "+data);
		debug("\tTipo: "+type);
		debug("\tDescrizione: "+type.getItaDescription());
		if(!TimeTransitionType.FROM_DAYLIGHT_SAVING_TO_STANDARD_TIME.equals(type)) {
			throw new UtilsException("Expected '"+TimeTransitionType.FROM_DAYLIGHT_SAVING_TO_STANDARD_TIME+"' found: "+type);
		}
		long m = -1;
		if(formatter!=null) {
			m = zoneId!=null ? DaylightSavingUtils.minutesUntilNextTransitionWithoutOffset(data, zoneId, formatter) : DaylightSavingUtils.minutesUntilNextTransitionWithoutOffset(data, formatter);
		}
		else if(format!=null) {
			m = zoneId!=null ? DaylightSavingUtils.minutesUntilNextTransitionWithoutOffset(data, zoneId, format) : DaylightSavingUtils.minutesUntilNextTransitionWithoutOffset(data, format);
		}
		else {
			m = zoneId!=null ? DaylightSavingUtils.minutesUntilNextTransitionWithoutOffset(data, zoneId) : DaylightSavingUtils.minutesUntilNextTransitionWithoutOffset(data);
		}
		long atteso = 4l;
		debug("\tMinutes nextTransition: "+m);
		if(m!=atteso) {
			throw new UtilsException("Expected "+atteso+" minutes; found "+m);
		}
		
        debug("\tAdd 4 minutes");
		TemporalAccessor d = null;
		if(formatter!=null) {
			d = DaylightSavingUtils.parseLocalDateTime(data, formatter);
		}
		else if(format!=null) {
			d = DaylightSavingUtils.parseLocalDateTime(data, format);
		}
		else {
			d = DaylightSavingUtils.parseLocalDateTime(data);
		}
		LocalDateTime dateTime = LocalDateTime.from(d);
		LocalDateTime updatedDateTime = dateTime.plusMinutes(4);
		if(formatter!=null) {
        	data = formatter.format(updatedDateTime);
        }
        else if(format!=null) {
        	data = DateTimeFormatter.ofPattern(format).format(updatedDateTime);
        }
        else {
        	data = DaylightSavingUtils.getBasicFormatter().format(updatedDateTime);
        }
        debug("\tNew date: "+data);
		        
        if(formatter!=null) {
			type = zoneId!=null ? DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data,zoneId,formatter) : DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data,formatter) ;
		}
		else if(format!=null) {
			type = zoneId!=null ? DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data,zoneId,format) : DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data,format) ;
		}
		else {
			type = zoneId!=null ? DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data,zoneId) : DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data) ;
		}
		debug("\tTipo: "+type);
		if(type!=null) {
			debug("\tDescrizione: "+type.getItaDescription());
			throw new UtilsException("Expected null found: '"+type+"'");
		}
		if(formatter!=null) {
			m = zoneId!=null ? DaylightSavingUtils.minutesUntilNextTransitionWithoutOffset(data, zoneId, formatter) : DaylightSavingUtils.minutesUntilNextTransitionWithoutOffset(data, formatter);
		}
		else if(format!=null) {
			m = zoneId!=null ? DaylightSavingUtils.minutesUntilNextTransitionWithoutOffset(data, zoneId, format) : DaylightSavingUtils.minutesUntilNextTransitionWithoutOffset(data, format);
		}
		else {
			m = zoneId!=null ? DaylightSavingUtils.minutesUntilNextTransitionWithoutOffset(data, zoneId) : DaylightSavingUtils.minutesUntilNextTransitionWithoutOffset(data);
		}
		atteso = 221700;
		debug("\tMinutes nextTransition: "+m);
		if(m!=atteso) {
			throw new UtilsException("Expected "+atteso+" minutes; found "+m);
		}
	}
	
	private static void oraSolareToOraLegaleWithoutOffset(ZoneId zoneId, String format, DateTimeFormatter formatter) throws UtilsException {
		// Ora Legale che tra 4 minuti torna all'ora solare portando indietro di un'ora
		String data = null;
		TimeTransitionType type = null;
		if(formatter!=null) {
			data = "2025-03-30 015600";
			type = zoneId!=null ? DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data,zoneId,formatter) : DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data,formatter) ;
		}
		else if(format!=null) {
			data = "20250330015600";
			type = zoneId!=null ? DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data,zoneId,format) : DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data,format) ;
		}
		else {
			data = "2025-03-30 01:56:00";
			type = zoneId!=null ? DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data,zoneId) : DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data) ;
		}
		debug("\tdate: "+data);
		debug("\tTipo: "+type);
		debug("\tDescrizione: "+type.getItaDescription());
		if(!TimeTransitionType.FROM_STANDARD_TO_DAYLIGHT_SAVING_TIME.equals(type)) {
			throw new UtilsException("Expected '"+TimeTransitionType.FROM_STANDARD_TO_DAYLIGHT_SAVING_TIME+"'");
		}
		long m = -1;
		if(formatter!=null) {
			m = zoneId!=null ? DaylightSavingUtils.minutesUntilNextTransitionWithoutOffset(data, zoneId, formatter) : DaylightSavingUtils.minutesUntilNextTransitionWithoutOffset(data, formatter);
		}
		else if(format!=null) {
			m = zoneId!=null ? DaylightSavingUtils.minutesUntilNextTransitionWithoutOffset(data, zoneId, format) : DaylightSavingUtils.minutesUntilNextTransitionWithoutOffset(data, format);
		}
		else {
			m = zoneId!=null ? DaylightSavingUtils.minutesUntilNextTransitionWithoutOffset(data, zoneId) : DaylightSavingUtils.minutesUntilNextTransitionWithoutOffset(data);
		}
		long atteso = 4l;
		debug("\tMinutes nextTransition: "+m);
		if(m!=atteso) {
			throw new UtilsException("Expected "+atteso+" minutes; found "+m);
		}
		
        debug("\tAdd 4 minutes");
        TemporalAccessor d = null;
		if(formatter!=null) {
			d = DaylightSavingUtils.parseLocalDateTime(data, formatter);
		}
		else if(format!=null) {
			d = DaylightSavingUtils.parseLocalDateTime(data, format);
		}
		else {
			d = DaylightSavingUtils.parseLocalDateTime(data);
		}
		LocalDateTime dateTime = LocalDateTime.from(d);
		LocalDateTime updatedDateTime = dateTime.plusMinutes(4);
		if(formatter!=null) {
        	data = formatter.format(updatedDateTime);
        }
        else if(format!=null) {
        	data = DateTimeFormatter.ofPattern(format).format(updatedDateTime);
        }
        else {
        	data = DaylightSavingUtils.getBasicFormatter().format(updatedDateTime);
        }
        debug("\tNew date: "+data);
		        
        if(formatter!=null) {
			type = zoneId!=null ? DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data,zoneId,formatter) : DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data,formatter) ;
		}
		else if(format!=null) {
			type = zoneId!=null ? DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data,zoneId,format) : DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data,format) ;
		}
		else {
			type = zoneId!=null ? DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data,zoneId) : DaylightSavingUtils.getTimeChangePendingTodayWithoutOffset(data) ;
		}
		debug("\tTipo: "+type);
		if(type!=null) {
			debug("\tDescrizione: "+type.getItaDescription());
			throw new UtilsException("Expected null found: '"+type+"'");
		}
		if(formatter!=null) {
			m = zoneId!=null ? DaylightSavingUtils.minutesUntilNextTransitionWithoutOffset(data, zoneId, formatter) : DaylightSavingUtils.minutesUntilNextTransitionWithoutOffset(data, formatter);
		}
		else if(format!=null) {
			m = zoneId!=null ? DaylightSavingUtils.minutesUntilNextTransitionWithoutOffset(data, zoneId, format) : DaylightSavingUtils.minutesUntilNextTransitionWithoutOffset(data, format);
		}
		else {
			m = zoneId!=null ? DaylightSavingUtils.minutesUntilNextTransitionWithoutOffset(data, zoneId) : DaylightSavingUtils.minutesUntilNextTransitionWithoutOffset(data);
		}
		atteso = 302400;
		debug("\tMinutes nextTransition: "+m);
		if(m!=atteso) {
			throw new UtilsException("Expected "+atteso+" minutes; found "+m);
		}
	}
	
	
	private static void debug(String msg) {
		System.out.println(msg);
	}
}
