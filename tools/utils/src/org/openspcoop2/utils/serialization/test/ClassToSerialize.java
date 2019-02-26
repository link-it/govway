/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.utils.serialization.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

import org.bouncycastle.util.Arrays;


/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ClassToSerialize implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String str;
	public String getStr() {
		return this.str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public int getIn() {
		return this.in;
	}

	public void setIn(int in) {
		this.in = in;
	}

	public char getChr() {
		return this.chr;
	}

	public void setChr(char chr) {
		this.chr = chr;
	}

	public boolean isBool() {
		return this.bool;
	}

	public void setBool(boolean bool) {
		this.bool = bool;
	}

	public byte getBte() {
		return this.bte;
	}

	public void setBte(byte bte) {
		this.bte = bte;
	}

	public long getLng() {
		return this.lng;
	}

	public void setLng(long lng) {
		this.lng = lng;
	}

	public float getFlt() {
		return this.flt;
	}

	public void setFlt(float flt) {
		this.flt = flt;
	}

	public double getDbl() {
		return this.dbl;
	}

	public void setDbl(double dbl) {
		this.dbl = dbl;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Calendar getCalendar() {
		return this.calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public List<Integer> getSimpleList() {
		return this.simpleList;
	}

	public void setSimpleList(List<Integer> simpleList) {
		this.simpleList = simpleList;
	}

	public List<InnerClass> getComplexList() {
		return this.complexList;
	}

	public void setComplexList(List<InnerClass> complexList) {
		this.complexList = complexList;
	}

	private int in;
	private char chr;
	private boolean bool;
	private byte bte;
	private byte[] bytea;
	private long lng;
	private float flt;
	private double dbl;
	private Date date;
	private Calendar calendar;
	private MyEnum myEnum;
	private List<Integer> simpleList;
	private List<InnerClass> complexList;
	
	public enum MyEnum {




		ENUM_1("enum_1"),


		ENUM_2("enum_2");




		private String value;

		MyEnum(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return String.valueOf(this.value);
		}

		public static MyEnum fromValue(String text) {
			for (MyEnum b : MyEnum.values()) {
				if (String.valueOf(b.value).equals(text)) {
					return b;
				}
			}
			return null;
		}
	}

	public void init() {
		this.str = "String";
		this.in = 1;
		this.chr = 'c';
		this.bool = true;
		this.bte = 12;
		int NUM = 3;
		this.lng = 2l;
		this.flt = 1.2f;
		this.dbl = 2.4;
		this.myEnum = MyEnum.ENUM_1;
		this.date = new Date();
		this.calendar = new GregorianCalendar();
		this.simpleList = new ArrayList<Integer>();
		this.bytea = new byte[NUM];
		this.complexList = new ArrayList<InnerClass>();

		for(int i=0 ; i < NUM; i++) {
			this.bytea[i] = this.bte;
			this.simpleList.add(i);
			InnerClass e = new InnerClass();
			e.init();
			this.complexList.add(e);
		}
		
	}

	public byte[] getBytea() {
		return this.bytea;
	}

	public void setBytea(byte[] bytea) {
		this.bytea = bytea;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if(!(obj instanceof ClassToSerialize))
			return false;
		
		ClassToSerialize classObj = (ClassToSerialize) obj;
		
		if(!Objects.equals(this.str,classObj.getStr()))
			return false;
		if(this.bool != classObj.isBool())
			return false;
		if(this.chr != classObj.getChr())
			return false;
		if(this.bte != classObj.getBte())
			return false;
		if(!Arrays.areEqual(this.bytea, classObj.getBytea()))
			return false;
		if(this.lng != classObj.getLng())
			return false;
		if(this.flt != classObj.getFlt())
			return false;
		if(this.dbl != classObj.getDbl())
			return false;
		if(this.dbl != classObj.getDbl())
			return false;
		if(!Objects.equals(this.myEnum,classObj.getMyEnum()))
			return false;
		if(!Objects.equals(this.date,classObj.getDate()))
			return false;
		if(this.calendar == null && classObj.getCalendar()!=null || this.calendar != null && classObj.getCalendar()==null)
			return false;
		if(!Objects.equals(this.calendar.getTime(),classObj.getCalendar().getTime()))
			return false;
		if(!Objects.equals(this.simpleList,classObj.getSimpleList()))
			return false;
		if(!Objects.equals(this.complexList,classObj.getComplexList()))
			return false;
		
		return true;
		
	}

	public MyEnum getMyEnum() {
		return this.myEnum;
	}

	public void setMyEnum(MyEnum myEnum) {
		this.myEnum = myEnum;
	}
	
//	private static boolean equalsDate(Date a, Date b) {
//		if(a == null)
//			return b == null;
//		if(b == null)
//			return false;
//		boolean c = a.compareTo(b) == 0;
//		if(!c) {
//			System.out.println("A: " + a.getTime());
//			System.out.println("B: " + b.getTime());
//		}
//			
//		return c;
//	}
}
