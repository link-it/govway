package org.openspcoop2.protocol.trasparente.testsuite.units;

import java.util.ArrayList;
import java.util.List;

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
		return new Object[]{"NULL"};
	}

}
