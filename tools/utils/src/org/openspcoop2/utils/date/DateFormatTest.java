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

//		Date nowDateT = DateManager.getDate();
//		System.out.println("AAAAAAAAA2: "+DateUtils.getDateTimeFormatter(DateEngineType.JODA,"yyyyMMdd_HHmmssSSS").format(nowDateT));
////		
//		String test = "20200131_105154584";
//		System.out.println("AAAAAAAAA1: "+DateUtils.getDateTimeFormatter(DateEngineType.JAVA_UTIL,"yyyyMMdd_HHmmssSSS").parse(test));
//		System.out.println("AAAAAAAAA1: "+DateUtils.getDateTimeFormatter(DateEngineType.JODA,"yyyyMMdd_HHmmssSSS").parse(test));
//		System.out.println("AAAAAAAAA2: "+DateUtils.getDefaultDateTimeFormatter("yyyyMMdd_HHmmssSSS").parse(test));
//		

		// inizializzo per costi
		@SuppressWarnings("unused")
		Date nowDate = DateManager.getDate();
		@SuppressWarnings("unused")
		ZonedDateTime nowDateTime = ZonedDateTime.now();		
		@SuppressWarnings("unused")
		DateTime jodaDateTime = DateTime.now();
		
		boolean threads = false;
		
		boolean DATE_TIME = true;
		boolean TIME = true;
		boolean DATE = true;
		
		int N = 777777;
		//int N = 20;

		String formato = null;
		
//		// altri formati
		formato =  "yyyy MM dd HH mm s";
		test(formato, N, DATE_TIME, !DATE, !TIME, threads);
		formato =  "dd-MM-yyyy HH:mm:ss.SSS";
		test(formato, N, DATE_TIME, !DATE, !TIME, threads);
		
		// formati speciali accorpati
		formato =  "yyyyMMddHHmmssSSS";
		test(formato, N, DATE_TIME, !DATE, !TIME, threads);
		formato =  "yyyyMMdd_HHmmssSSS";
		test(formato, N, DATE_TIME, !DATE, !TIME, threads);
		formato =  "yyyyMMddHHmm";
		test(formato, N, DATE_TIME, !DATE, !TIME, threads);

		// formati speciali accorpati (data)
		formato =  "yyyyMMdd";
		test(formato, N, !DATE_TIME, DATE, !TIME, threads);
		
		formato = DateUtils.SIMPLE_DATE_FORMAT_MS;
		test(formato, N, DATE_TIME, !DATE, !TIME, threads);
		
		formato = DateUtils.SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ;
		test(formato, N, DATE_TIME, !DATE, !TIME, threads);
		
//		formato = "yyyy-MM-dd_HH:mm:ss.SSSXXX";
//		test(formato, N, threads);
		
		formato = DateUtils.SIMPLE_DATE_FORMAT_SECOND;
		test(formato, N, DATE_TIME, !DATE, !TIME, threads);
		
		formato = DateUtils.SIMPLE_DATE_FORMAT_SECOND_ISO_8601_TZ;
		test(formato, N, DATE_TIME, !DATE, !TIME, threads);
		
		formato = DateUtils.SIMPLE_DATE_FORMAT_MINUTE;
		test(formato, N, DATE_TIME, !DATE, !TIME, threads);
		
		formato = DateUtils.SIMPLE_DATE_FORMAT_MINUTE_ISO_8601_TZ;
		test(formato, N, DATE_TIME, !DATE, !TIME, threads);
		
		formato = DateUtils.SIMPLE_DATE_FORMAT_HOUR;
		test(formato, N, DATE_TIME, !DATE, !TIME, threads);
		
		formato = DateUtils.SIMPLE_DATE_FORMAT_HOUR_ISO_8601_TZ;
		test(formato, N, DATE_TIME, !DATE, !TIME, threads);
		
		formato = DateUtils.SIMPLE_DATE_FORMAT_DAY;
		test(formato, N, !DATE_TIME, DATE, !TIME, threads);
		
		formato = DateUtils.SIMPLE_DATE_FORMAT_DAY_ISO_8601_TZ;
		test(formato, N, !DATE_TIME, DATE, !TIME, threads);
				
		formato =  "HH:mm:ss.SSS";
		test(formato, N, !DATE_TIME, !DATE, TIME, threads);
	}
	
	private static void test(String formato, int N, boolean dateTime, boolean date, boolean time, boolean threadsEnabled) throws ParseException {
		
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
		if(dateTime) {
			ds = DateUtils.convertToDateViaInstant( DateUtils.parseToLocalDateTime(formato, sTime) );
		}
		else if(time) {
			ds = DateUtils.convertToDateViaInstant( DateUtils.parseToLocalTime(formato, sTime) );
		}
		else {
			ds = DateUtils.convertToDateViaInstant( DateUtils.parseToLocalDate(formato, sTime) );
		}
		System.out.println("DateTimeFormatter reversed :             "+dateTimeFormatter.format(DateUtils.convertToZonedDateTimeViaInstant(ds)));
		
		if(dateTime) {
			ds = DateUtils.convertToDateViaInstant( DateUtils.parseToLocalDateTime(formato, sJava) );
		}
		else if(time) {
			ds = DateUtils.convertToDateViaInstant( DateUtils.parseToLocalTime(formato, sJava) );
		}
		else {
			ds = DateUtils.convertToDateViaInstant( DateUtils.parseToLocalDate(formato, sJava) );
		}
		System.out.println("DateTimeFormatter reverseJ :             "+dateTimeFormatter.format(DateUtils.convertToZonedDateTimeViaInstant(ds)));
		
		System.out.println("DateTimeFormatter (converted):           "+dateTimeFormatter.format(nowDate.toInstant().atZone(ZoneId.systemDefault())));
		
		String sJoda = jodaDateTime.toString(jodaDateTimeFormatter);
		System.out.println("JodaDateTimeFormatter normale  :         "+sJoda);
		
		ds = DateUtils.convertToDate(DateUtils.parseToJodaDateTime(formato, sJoda));
		//System.out.println("JodaDateTimeFormatter reversed :         "+new org.joda.time.LocalDateTime(ds,DateTimeZone.getDefault()).toString(jodaDateTimeFormatter));
		System.out.println("JodaDateTimeFormatter reversed :         "+DateUtils.convertToJodaDateTime(ds).toString(jodaDateTimeFormatter));
		
		ds = DateUtils.convertToDate(DateUtils.parseToJodaDateTime(formato, sJava));
		//System.out.println("JodaDateTimeFormatter reversed :         "+new org.joda.time.LocalDateTime(ds,DateTimeZone.getDefault()).toString(jodaDateTimeFormatter));
		System.out.println("JodaDateTimeFormatter reverseJ :         "+DateUtils.convertToJodaDateTime(ds).toString(jodaDateTimeFormatter));
		
		ds = DateUtils.convertToDate(DateUtils.parseToJodaDateTime(formato, sTime));
		//System.out.println("JodaDateTimeFormatter reversed :         "+new org.joda.time.LocalDateTime(ds,DateTimeZone.getDefault()).toString(jodaDateTimeFormatter));
		System.out.println("JodaDateTimeFormatter reverseT :         "+DateUtils.convertToJodaDateTime(ds).toString(jodaDateTimeFormatter));
				
		System.out.println("JodaDateTime (converted):                "+new DateTime(nowDate).toString(jodaDateTimeFormatter));
		
		if(dateTime) {
			ds = DateUtils.convertToDateViaInstant( DateUtils.parseToLocalDateTime(formato, sJoda) );
		}
		else if(time) {
			ds = DateUtils.convertToDateViaInstant( DateUtils.parseToLocalTime(formato, sJoda) );
		}
		else {
			ds = DateUtils.convertToDateViaInstant( DateUtils.parseToLocalDate(formato, sJoda) );
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
			if(dateTime) {
				ds = DateUtils.convertToDateViaInstant( DateUtils.parseToLocalDateTime(formato, sTime) );
			}
			else if(time) {
				ds = DateUtils.convertToDateViaInstant( DateUtils.parseToLocalTime(formato, sTime) );
			}
			else {
				ds = DateUtils.convertToDateViaInstant( DateUtils.parseToLocalDate(formato, sTime) );
			}
		}
		end = System.currentTimeMillis();
		System.out.println("DateTimeFormatter: "+(end-now));
		
		
		now = System.currentTimeMillis();
		for (int i = 0; i < N; i++) {
			sTime = dateTimeFormatter.format(DateUtils.convertToZonedDateTimeViaInstant(nowDate));
			if(dateTime) {
				ds = DateUtils.convertToDateViaInstant( DateUtils.parseToLocalDateTime(formato, sTime) );
			}
			else if(time) {
				ds = DateUtils.convertToDateViaInstant( DateUtils.parseToLocalTime(formato, sTime) );
			}
			else {
				ds = DateUtils.convertToDateViaInstant( DateUtils.parseToLocalDate(formato, sTime) );
			}
		}
		end = System.currentTimeMillis();
		System.out.println("DateTimeFormatter (origDate converted): "+(end-now));
		
		
		now = System.currentTimeMillis();
		for (int i = 0; i < N; i++) {
			 sJoda = jodaDateTime.toString(jodaDateTimeFormatter);
			 ds = DateUtils.convertToDate(DateUtils.parseToJodaDateTime(formato, sJoda));
		}
		end = System.currentTimeMillis();
		System.out.println("JODA DateTimeFormatter: "+(end-now));
		
		
		now = System.currentTimeMillis();
		for (int i = 0; i < N; i++) {
			 sJoda = DateUtils.convertToJodaDateTime(nowDate).toString(jodaDateTimeFormatter);
			 ds = DateUtils.convertToDate(DateUtils.parseToJodaDateTime(formato, sJoda));
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