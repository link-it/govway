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

package org.openspcoop2.utils.date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.joda.time.DateTime;

/**
 * DateFormatTest
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DateFormatTest {

	public static void main(String[] args) throws ParseException {

		// inizializzo per costi
		@SuppressWarnings("unused")
		Date nowDate = DateManager.getDate();
		@SuppressWarnings("unused")
		ZonedDateTime nowDateTime = ZonedDateTime.now();		
		@SuppressWarnings("unused")
		DateTime jodaDateTime = DateTime.now();
		
		boolean threads = false;
		
		int N = 777777;
		//int N = 20;

		String formato = DateUtils.SIMPLE_DATE_FORMAT_MS;
		test(formato, N, threads);
		
		formato = DateUtils.SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ;
		test(formato, N, threads);
		
//		formato = "yyyy-MM-dd_HH:mm:ss.SSSXXX";
//		test(formato, N, threads);
		
		formato = DateUtils.SIMPLE_DATE_FORMAT_SECOND;
		test(formato, N, threads);
		
		formato = DateUtils.SIMPLE_DATE_FORMAT_SECOND_ISO_8601_TZ;
		test(formato, N, threads);
		
		formato = DateUtils.SIMPLE_DATE_FORMAT_MINUTE;
		test(formato, N, threads);
		
		formato = DateUtils.SIMPLE_DATE_FORMAT_MINUTE_ISO_8601_TZ;
		test(formato, N, threads);
		
		formato = DateUtils.SIMPLE_DATE_FORMAT_HOUR;
		test(formato, N, threads);
		
		formato = DateUtils.SIMPLE_DATE_FORMAT_HOUR_ISO_8601_TZ;
		test(formato, N, threads);
		
		formato = DateUtils.SIMPLE_DATE_FORMAT_DAY;
		test(formato, N, threads);
		
		formato = DateUtils.SIMPLE_DATE_FORMAT_DAY_ISO_8601_TZ;
		test(formato, N, threads);
				
	}
	
	private static void test(String formato, int N, boolean threadsEnabled) throws ParseException {
		
		System.out.println("==============================================");
		System.out.println("Formato: "+formato);
		
		//DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(formato);
		DateTimeFormatter dateTimeFormatter = DateUtils.getDateTimeFormatter(formato);
		//String formatoJoda = formato.endsWith("X")?(formato.substring(0, formato.length()-1)+"Z"):formato;
		//org.joda.time.format.DateTimeFormatter jodaDateTimeFormatter = org.joda.time.format.DateTimeFormat.forPattern(formatoJoda);
		org.joda.time.format.DateTimeFormatter jodaDateTimeFormatter = DateUtils.getJodaDateTimeFormatter(formato);
		
		Date nowDate = DateManager.getDate();
		ZonedDateTime nowDateTime = ZonedDateTime.now();		
		DateTime jodaDateTime = DateTime.now();
		
		String sJava = new SimpleDateFormat (formato).format(nowDate);
		System.out.println("SimpleDateFormat normale  :              "+sJava);
		Date ds = new SimpleDateFormat (formato).parse(sJava);
		System.out.println("SimpleDateFormat reversed :              "+new SimpleDateFormat (formato).format(ds));
		
		String sTime = dateTimeFormatter.format(nowDateTime);
		System.out.println("DateTimeFormatter normale  :             "+sTime);
		if(sTime.contains("_")) {
			ds = DateUtils.convertToDateViaInstant( LocalDateTime.parse(sTime, dateTimeFormatter) );
		}
		else {
			ds = DateUtils.convertToDateViaInstant( LocalDate.parse(sTime, dateTimeFormatter) );
		}
		System.out.println("DateTimeFormatter reversed :             "+dateTimeFormatter.format(DateUtils.convertToZonedDateTimeViaInstant(ds)));
		
		if(sJava.contains("_")) {
			ds = DateUtils.convertToDateViaInstant( LocalDateTime.parse(sJava, dateTimeFormatter) );
		}
		else {
			ds = DateUtils.convertToDateViaInstant( LocalDate.parse(sJava, dateTimeFormatter) );
		}
		System.out.println("DateTimeFormatter reverseJ :             "+dateTimeFormatter.format(DateUtils.convertToZonedDateTimeViaInstant(ds)));
		
		System.out.println("DateTimeFormatter (converted):           "+dateTimeFormatter.format(nowDate.toInstant().atZone(ZoneId.systemDefault())));
		
		String sJoda = jodaDateTime.toString(jodaDateTimeFormatter);
		System.out.println("JodaDateTimeFormatter normale  :         "+sJoda);
		
		ds = DateUtils.convertToDate(jodaDateTimeFormatter.parseDateTime(sJoda));
		//System.out.println("JodaDateTimeFormatter reversed :         "+new org.joda.time.LocalDateTime(ds,DateTimeZone.getDefault()).toString(jodaDateTimeFormatter));
		System.out.println("JodaDateTimeFormatter reversed :         "+DateUtils.convertToJodaDateTime(ds).toString(jodaDateTimeFormatter));
		
		ds = DateUtils.convertToDate(jodaDateTimeFormatter.parseDateTime(sJava));
		//System.out.println("JodaDateTimeFormatter reversed :         "+new org.joda.time.LocalDateTime(ds,DateTimeZone.getDefault()).toString(jodaDateTimeFormatter));
		System.out.println("JodaDateTimeFormatter reverseJ :         "+DateUtils.convertToJodaDateTime(ds).toString(jodaDateTimeFormatter));
		
		ds = DateUtils.convertToDate(jodaDateTimeFormatter.parseDateTime(sTime));
		//System.out.println("JodaDateTimeFormatter reversed :         "+new org.joda.time.LocalDateTime(ds,DateTimeZone.getDefault()).toString(jodaDateTimeFormatter));
		System.out.println("JodaDateTimeFormatter reverseT :         "+DateUtils.convertToJodaDateTime(ds).toString(jodaDateTimeFormatter));
				
		System.out.println("JodaDateTime (converted):                "+new DateTime(nowDate).toString(jodaDateTimeFormatter));
		
		if(sJoda.contains("_")) {
			ds = DateUtils.convertToDateViaInstant( LocalDateTime.parse(sJoda, dateTimeFormatter) );
		}
		else {
			ds = DateUtils.convertToDateViaInstant( LocalDate.parse(sJoda, dateTimeFormatter) );
		}
		System.out.println("DateTimeFormatter reversJO :             "+dateTimeFormatter.format(DateUtils.convertToZonedDateTimeViaInstant(ds)));
		
		
		
		
		
		
		long now = System.currentTimeMillis();
		for (int i = 0; i < N; i++) {
			sJava = new SimpleDateFormat (formato).format(nowDate);
			ds = new SimpleDateFormat (formato).parse(sJava);
		}
		long end = System.currentTimeMillis();
		System.out.println("SimpleDateFormat: "+(end-now));
		
		
		now = System.currentTimeMillis();
		for (int i = 0; i < N; i++) {
			sTime = dateTimeFormatter.format(nowDateTime);
			if(sTime.contains("_")) {
				ds = DateUtils.convertToDateViaInstant( LocalDateTime.parse(sTime, dateTimeFormatter) );
			}
			else {
				ds = DateUtils.convertToDateViaInstant( LocalDate.parse(sTime, dateTimeFormatter) );
			}
		}
		end = System.currentTimeMillis();
		System.out.println("DateTimeFormatter: "+(end-now));
		
		
		now = System.currentTimeMillis();
		for (int i = 0; i < N; i++) {
			sTime = dateTimeFormatter.format(DateUtils.convertToZonedDateTimeViaInstant(nowDate));
			if(sTime.contains("_")) {
				ds = DateUtils.convertToDateViaInstant( LocalDateTime.parse(sTime, dateTimeFormatter) );
			}
			else {
				ds = DateUtils.convertToDateViaInstant( LocalDate.parse(sTime, dateTimeFormatter) );
			}
		}
		end = System.currentTimeMillis();
		System.out.println("DateTimeFormatter (origDate converted): "+(end-now));
		
		
		now = System.currentTimeMillis();
		for (int i = 0; i < N; i++) {
			 sJoda = jodaDateTime.toString(jodaDateTimeFormatter);
			 ds = DateUtils.convertToDate(jodaDateTimeFormatter.parseDateTime(sJoda));
		}
		end = System.currentTimeMillis();
		System.out.println("JODA DateTimeFormatter: "+(end-now));
		
		
		now = System.currentTimeMillis();
		for (int i = 0; i < N; i++) {
			 sJoda = DateUtils.convertToJodaDateTime(nowDate).toString(jodaDateTimeFormatter);
			 ds = DateUtils.convertToDate(jodaDateTimeFormatter.parseDateTime(sJoda));
		}
		end = System.currentTimeMillis();
		System.out.println("JODA DateTimeFormatter (origDate converted): "+(end-now));
		
		
		
//		
//		
//		
//		long now = System.currentTimeMillis();
//		for (int i = 0; i < N; i++) {
//			new SimpleDateFormat (formato).format(nowDate);
//		}
//		long end = System.currentTimeMillis();
//		System.out.println("SimpleDateFormat: "+(end-now));
//		
//		now = System.currentTimeMillis();
//		for (int i = 0; i < N; i++) {
//			dateTimeFormatter.format(nowDateTime);
//		}
//		end = System.currentTimeMillis();
//		System.out.println("DateTimeFormatter: "+(end-now));
//		
//		now = System.currentTimeMillis();
//		for (int i = 0; i < N; i++) {
//			dateTimeFormatter.format(nowDate.toInstant().atZone(ZoneId.systemDefault()));
//		}
//		end = System.currentTimeMillis();
//		System.out.println("DateTimeFormatter (converted): "+(end-now));
//		
//		now = System.currentTimeMillis();
//		for (int i = 0; i < N; i++) {
//			jodaDateTime.toString(jodaDateTimeFormatter);
//		}
//		end = System.currentTimeMillis();
//		System.out.println("JodaDateTimeFormatter: "+(end-now));
//		
//		now = System.currentTimeMillis();
//		for (int i = 0; i < N; i++) {
//			new DateTime(nowDate).toString(jodaDateTimeFormatter);
//		}
//		end = System.currentTimeMillis();
//		System.out.println("JodaDateTimeFormatter (converted): "+(end-now));
//
//		
		if(!threadsEnabled) {
			return;
		}


		int threads = 100;
		ExecutorService threadsPool = Executors.newFixedThreadPool(threads);

		// Avvio threads
		now = System.currentTimeMillis();
		List<TestDate> list = new ArrayList<>();
		for (int i = 0; i < threads; i++) {
			TestDate thread = new TestDate(N, formato, false, false, false);
			threadsPool.execute(thread);
			list.add(thread);
		}
		boolean terminated = false;
		while(terminated == false){
			boolean tmpTerminated = true;
			for (TestDate processorThread : list) {
				if(processorThread.isFinished()==false){
					tmpTerminated = false;
					break;
				}
			}
			if(tmpTerminated==false){
				try {
					Thread.sleep(10);
				}catch(Exception e) {}
			}
			else{
				terminated = true;
			}
		}
		end = System.currentTimeMillis();
		System.out.println("SimpleDateFormat: "+(end-now));



		threadsPool = Executors.newFixedThreadPool(threads);
		
		now = System.currentTimeMillis();
		list = new ArrayList<>();
		for (int i = 0; i < threads; i++) {
			TestDate thread = new TestDate(N, formato, true, false, false);
			threadsPool.execute(thread);
			list.add(thread);
		}
		terminated = false;
		while(terminated == false){
			boolean tmpTerminated = true;
			for (TestDate processorThread : list) {
				if(processorThread.isFinished()==false){
					tmpTerminated = false;
					break;
				}
			}
			if(tmpTerminated==false){
				try {
					Thread.sleep(250);
				}catch(Exception e) {}
			}
			else{
				terminated = true;
			}
		}
		end = System.currentTimeMillis();
		System.out.println("DateTimeFormatter: "+(end-now));
		
		
		
		
		threadsPool = Executors.newFixedThreadPool(threads);
		
		now = System.currentTimeMillis();
		list = new ArrayList<>();
		for (int i = 0; i < threads; i++) {
			TestDate thread = new TestDate(N, formato, true, false, true);
			threadsPool.execute(thread);
			list.add(thread);
		}
		terminated = false;
		while(terminated == false){
			boolean tmpTerminated = true;
			for (TestDate processorThread : list) {
				if(processorThread.isFinished()==false){
					tmpTerminated = false;
					break;
				}
			}
			if(tmpTerminated==false){
				try {
					Thread.sleep(250);
				}catch(Exception e) {}
			}
			else{
				terminated = true;
			}
		}
		end = System.currentTimeMillis();
		System.out.println("DateTimeFormatter (converted): "+(end-now));
		
		
		
		
		threadsPool = Executors.newFixedThreadPool(threads);
		
		now = System.currentTimeMillis();
		list = new ArrayList<>();
		for (int i = 0; i < threads; i++) {
			TestDate thread = new TestDate(N, formato, false, true, false);
			threadsPool.execute(thread);
			list.add(thread);
		}
		terminated = false;
		while(terminated == false){
			boolean tmpTerminated = true;
			for (TestDate processorThread : list) {
				if(processorThread.isFinished()==false){
					tmpTerminated = false;
					break;
				}
			}
			if(tmpTerminated==false){
				try {
					Thread.sleep(250);
				}catch(Exception e) {}
			}
			else{
				terminated = true;
			}
		}
		end = System.currentTimeMillis();
		System.out.println("JodaDateTimeFormatter: "+(end-now));
		
		
		
		
		threadsPool = Executors.newFixedThreadPool(threads);
		
		now = System.currentTimeMillis();
		list = new ArrayList<>();
		for (int i = 0; i < threads; i++) {
			TestDate thread = new TestDate(N, formato, false, true, true);
			threadsPool.execute(thread);
			list.add(thread);
		}
		terminated = false;
		while(terminated == false){
			boolean tmpTerminated = true;
			for (TestDate processorThread : list) {
				if(processorThread.isFinished()==false){
					tmpTerminated = false;
					break;
				}
			}
			if(tmpTerminated==false){
				try {
					Thread.sleep(250);
				}catch(Exception e) {}
			}
			else{
				terminated = true;
			}
		}
		end = System.currentTimeMillis();
		System.out.println("JodaDateTimeFormatter (converted): "+(end-now));
		
		

		
		System.out.println("FINITO");
		return;
	}

}

class TestDate extends Thread{

	private int n;
	private String formato;
	private boolean dateTime;
	private boolean jodaTime;
	private boolean convert;
	private boolean finished = false;

	public boolean isFinished() {
		return this.finished;
	}

	public TestDate(int n, String formato, boolean dateTime, boolean jodaTime, boolean convert) {
		this.n = n;
		this.formato = formato;
		this.dateTime = dateTime;
		this.jodaTime = jodaTime;
		this.convert = convert;
	}

	@Override
	public void run() {

		if(this.dateTime && !this.convert) {
			
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(this.formato);
			for (int i = 0; i < this.n; i++) {
				dateTimeFormatter.format(ZonedDateTime.now());
			}	
		}
		else if(this.dateTime && this.convert) {
			
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(this.formato);
			ZoneId zoneId = ZoneId.systemDefault();
			for (int i = 0; i < this.n; i++) {
				dateTimeFormatter.format(DateManager.getDate().toInstant().atZone(zoneId));
			}
			
		}
		else if(this.jodaTime && !this.convert) {
			
			String formatoJoda = this.formato.endsWith("X")?(this.formato.substring(0, this.formato.length()-1)+"Z"):this.formato;
			org.joda.time.format.DateTimeFormatter jodaDateTimeFormatter = org.joda.time.format.DateTimeFormat.forPattern(formatoJoda);
			for (int i = 0; i < this.n; i++) {
				DateTime.now().toString(jodaDateTimeFormatter);
			}	
		}
		else if(this.jodaTime && this.convert) {
			
			String formatoJoda = this.formato.endsWith("X")?(this.formato.substring(0, this.formato.length()-1)+"Z"):this.formato;
			org.joda.time.format.DateTimeFormatter jodaDateTimeFormatter = org.joda.time.format.DateTimeFormat.forPattern(formatoJoda);
			for (int i = 0; i < this.n; i++) {
				new DateTime(DateManager.getDate()).toString(jodaDateTimeFormatter);
			}
			
		}
		else {
			
			for (int i = 0; i < this.n; i++) {
				new SimpleDateFormat (this.formato).format(DateManager.getDate());
			}	
			
		}

		this.finished = true;
	}

}