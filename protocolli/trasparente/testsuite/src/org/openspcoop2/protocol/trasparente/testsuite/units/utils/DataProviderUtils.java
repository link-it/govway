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

package org.openspcoop2.protocol.trasparente.testsuite.units.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * DataProviderUtils
 *
 * @author Bussu Giovanni (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DataProviderUtils {

	
	private static class Pair {
		String left;
		Integer right;
	}
	
	public static Object[][] contentTypeJSONConCon(){
		Object[] jsonLst = contentTypeJSONLst();
		Object[] returnCodeLst = returnCodeConCon();
		
		
		return getDataProvider(jsonLst, returnCodeLst);
	}


	/**
	 * contentTypeJSON Con Senza
	 */
	public static Object[][] contentTypeJSONConSenza(){
		Object[] jsonLst = contentTypeJSONLst();
		Object[] returnCodeLst = returnCodeConSenza();
		
		
		return getDataProvider(jsonLst, returnCodeLst);
	}

	/**
	 * contentTypeXML Con Con
	 */
	public static Object[][] contentTypeBinaryConCon(){
		Object[] jsonLst = contentTypeBinaryLst();
		Object[] returnCodeLst = returnCodeConCon();
		
		
		return getDataProvider(jsonLst, returnCodeLst);
	}

	/**
	 * contentTypeXML Con Senza
	 */
	public static Object[][] contentTypeBinaryConSenza(){
		Object[] jsonLst = contentTypeBinaryLst();
		Object[] returnCodeLst = returnCodeConSenza();
		
		
		return getDataProvider(jsonLst, returnCodeLst);
	}
	
	/**
	 * contentTypeXML Con Con
	 */
	public static Object[][] contentTypeXMLConCon(){
		return contentTypeXMLConCon(false);
	}
	public static Object[][] contentTypeXMLConCon(boolean redirect){
		Object[] xmlLst = contentTypeXMLLst();
		Object[] returnCodeLst = null;
		if(redirect) {
			returnCodeLst = returnCodeRedirect();
		}
		else {
			returnCodeLst = returnCodeConCon();
		}
		
		return getDataProvider(xmlLst, returnCodeLst);
	}

	/**
	 * contentTypeXML Con Senza
	 */
	public static Object[][] contentTypeXMLConSenza(){
		return contentTypeXMLConSenza(false);
	}
	public static Object[][] contentTypeXMLConSenza(boolean redirect){
		Object[] xmlLst = contentTypeXMLLst();
		Object[] returnCodeLst = null;
		if(redirect) {
			returnCodeLst = returnCodeRedirect();
		}
		else {
			returnCodeLst = returnCodeConSenza();
		}
				
		return getDataProvider(xmlLst, returnCodeLst);
	}

	private static Object[][] getDataProvider(Object[] jsonLst, Object[] returnCodeLst) {
		List<Pair> lst = new ArrayList<Pair>();
		for(int i = 0; i < jsonLst.length; i++) {
			for(int j = 0; j < returnCodeLst.length; j++) {
				Pair pair = new Pair();
				pair.left = jsonLst[i] + "";
				pair.right = Integer.parseInt(returnCodeLst[j] + "");
				lst.add(pair);
			}
		}
		
		Object[][] returned = new Object[lst.size()][2];
		
		for(int i = 0; i < lst.size(); i++) {
			Pair pair = lst.get(i);
			returned[i][0] = pair.left;
			returned[i][1] = pair.right;
		}
		 
		return returned;
	}
	
	public static Object[][] responseCodeConCon() {
		Object[] returnCodeLst = returnCodeConCon();
		Object[][] returned = new Object[returnCodeLst.length][1];
		
		int i = 0;
		for(Object rc: returnCodeLst) {
			returned[i++][0] = rc;
		}
		return returned;

	}
	
	public static Object[][] responseCodeConSenza() {
		Object[] returnCodeLst = returnCodeConSenza();
		Object[][] returned = new Object[returnCodeLst.length][1];
		
		int i = 0;
		for(Object rc: returnCodeLst) {
			returned[i++][0] = rc;
		}
		return returned;

	}
	
	public static Object[][] responseCodeRedirect() {
		Object[] returnCodeLst = returnCodeRedirect();
		Object[][] returned = new Object[returnCodeLst.length][1];
		
		int i = 0;
		for(Object rc: returnCodeLst) {
			returned[i++][0] = rc;
		}
		return returned;

	}
	
	private static Object[] returnCodeConCon() {
		return new Object[] {200, 400, 403, 500, 501};
	}
		
	private static Object[] returnCodeConSenza() {
		return new Object[] {202, 400, 403, 500, 501};
	}
	
	private static Object[] returnCodeRedirect() {
		return new Object[] {301, 302, 303, 304, 307,
							201, // aggiunto per verificare che anche in questo caso sia corretto header Location (proxy pass revers)
							204  // aggiunto per verificare che anche in caso sia corretto header Content-Location (proxy pass revers)
		};
	}
	
	private static Object[] contentTypeJSONLst(){
		return new Object[]
				{"text/json",
				"text/x-json",
				"application/json",
				"application/x-json",
				"text/doc+json"};
	}
	
	private static Object[] contentTypeXMLLst(){
		return new Object[]{"text/xml",
				"application/xml",
				"text/doc+xml"};
	}
	
	private static Object[] contentTypeBinaryLst(){
		return new Object[]{"pdf", "doc", "zip", "png"};
	}

}
