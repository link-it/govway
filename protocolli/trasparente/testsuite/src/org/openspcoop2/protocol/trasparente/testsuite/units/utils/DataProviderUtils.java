/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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
		Object[] jsonLst = contentTypeXMLLst();
		Object[] returnCodeLst = returnCodeConCon();
		
		
		return getDataProvider(jsonLst, returnCodeLst);
	}

	/**
	 * contentTypeXML Con Senza
	 */
	public static Object[][] contentTypeXMLConSenza(){
		Object[] jsonLst = contentTypeXMLLst();
		Object[] returnCodeLst = returnCodeConSenza();
		
		
		return getDataProvider(jsonLst, returnCodeLst);
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
	
	private static Object[] returnCodeConCon() {
		return new Object[] {200, 400, 403, 500, 501};
	}
	
	private static Object[] returnCodeConSenza() {
		return new Object[] {202, 400, 403, 500, 501};
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
