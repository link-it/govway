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

package org.openspcoop2.utils.regexp.test;

import java.util.List;

import org.openspcoop2.utils.regexp.RegExpNotFoundException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;

/**
 * Test
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RegexpTest {

	public static void main(String[] args) throws Exception {
		test();
	}
	
	public static void test() throws Exception {
		
		String atteso = "uuid23";
		
		String url = "/govway/out/ENTE/PubblicoAmministratore/TEST/v1/uuid23/altroContesto";
		
		// obiettivo: estrarre 'uuid23'
		String relativeExpression = "/([^/]+)/altroContesto";
		String absoluteExpression = ".*/([^/]+)/altroContesto";
		
		// obiettivo: estrarre 'uuid23' e uuid27 con stessi pattern
		String atteso2 = "uuid27";
		String urlMultipla = "/govway/out/ENTE/PubblicoAmministratore/TEST/v1/uuid23/altroContesto/altro/uuid27/altroContesto";
		
		try {
			System.out.println("SINGLE [MATCH] RELATIVE: "+RegularExpressionEngine.getStringMatchPattern(url, relativeExpression));
			throw new Exception("Attesa eccezione");
		}catch(RegExpNotFoundException notFound) {
			System.out.println("SINGLE [MATCH] RELATIVE NOT FOUND: "+notFound.getMessage());
		}
		try {
			String s = RegularExpressionEngine.getStringMatchPattern(url, absoluteExpression);
			System.out.println("SINGLE [MATCH] ABSOLUTE: "+s);
			if(!atteso.equals(s)) {
				throw new Exception("Atteso come risultato la stringa '"+s+"'");
			}
		}catch(RegExpNotFoundException notFound) {
			throw new Exception("Non attesa eccezione: "+notFound.getMessage());
		}
		
		try {
			String s = RegularExpressionEngine.getStringFindPattern(url, relativeExpression);
			System.out.println("SINGLE [FIND] RELATIVE: "+s);
			if(!atteso.equals(s)) {
				throw new Exception("Atteso come risultato la stringa '"+s+"'");
			}
		}catch(RegExpNotFoundException notFound) {
			throw new Exception("Non attesa eccezione: "+notFound.getMessage());
		}
		try {
			String s = RegularExpressionEngine.getStringFindPattern(url, absoluteExpression);
			System.out.println("SINGLE [FIND] ABSOLUTE: "+s);
			if(!atteso.equals(s)) {
				throw new Exception("Atteso come risultato la stringa '"+s+"'");
			}
		}catch(RegExpNotFoundException notFound) {
			throw new Exception("Non attesa eccezione: "+notFound.getMessage());
		}
		
		
		try {
			System.out.println("ALL [MATCH] RELATIVE: "+RegularExpressionEngine.getAllStringMatchPattern(url, relativeExpression));
			throw new Exception("Attesa eccezione");
		}catch(RegExpNotFoundException notFound) {
			System.out.println("ALL [MATCH] RELATIVE NOT FOUND: "+notFound.getMessage());
		}
		try {
			List<String> l = RegularExpressionEngine.getAllStringMatchPattern(url, absoluteExpression);
			System.out.println("ALL [MATCH] ABSOLUTE: "+l);
			if(l==null || l.size()!=1 || !("["+atteso+"]").equals(l.toString())) {
				throw new Exception("Attesa lista di 1 elemento '"+atteso+"'");
			}
		}catch(RegExpNotFoundException notFound) {
			throw new Exception("Non attesa eccezione: "+notFound.getMessage());
		}
		
		try {
			List<String> l = RegularExpressionEngine.getAllStringFindPattern(url, relativeExpression);
			System.out.println("ALL [FIND] RELATIVE: "+l);
			if(l==null || l.size()!=1 || !("["+atteso+"]").equals(l.toString())) {
				throw new Exception("Attesa lista di 1 elemento '"+atteso+"'");
			}
		}catch(RegExpNotFoundException notFound) {
			throw new Exception("Non attesa eccezione: "+notFound.getMessage());
		}
		try {
			List<String> l = RegularExpressionEngine.getAllStringFindPattern(url, absoluteExpression);
			System.out.println("ALL [FIND] ABSOLUTE: "+l);
			if(l==null || l.size()!=1 || !("["+atteso+"]").equals(l.toString())) {
				throw new Exception("Attesa lista di 1 elemento '"+atteso+"'");
			}
		}catch(RegExpNotFoundException notFound) {
			throw new Exception("Non attesa eccezione: "+notFound.getMessage());
		}
		
		
		
		
		try {
			System.out.println("ALL [MATCH] RELATIVE 'MULTIPLE RESULTS': "+RegularExpressionEngine.getAllStringMatchPattern(urlMultipla, relativeExpression));
			throw new Exception("Attesa eccezione");
		}catch(RegExpNotFoundException notFound) {
			System.out.println("ALL [MATCH] RELATIVE NOT FOUND 'MULTIPLE RESULTS': "+notFound.getMessage());
		}
		try {
			List<String> l = RegularExpressionEngine.getAllStringMatchPattern(urlMultipla, absoluteExpression);
			System.out.println("ALL [MATCH] ABSOLUTE 'MULTIPLE RESULTS': "+l);
			if(l==null || l.size()!=1 || !("["+atteso2+"]").equals(l.toString())) {
				throw new Exception("Attesa lista di 1 elemento '"+atteso2+"'");
			}
		}catch(RegExpNotFoundException notFound) {
			throw new Exception("Non attesa eccezione 'MULTIPLE RESULTS': "+notFound.getMessage());
		}
		
		try {
			List<String> l = RegularExpressionEngine.getAllStringFindPattern(urlMultipla, relativeExpression);
			System.out.println("ALL [FIND] RELATIVE 'MULTIPLE RESULTS': "+l);
			if(l==null || l.size()!=2 || !("["+atteso+", "+atteso2+"]").equals(l.toString())) {
				throw new Exception("Attesa lista di 2 elementi '"+atteso+", "+atteso2+"'");
			}
		}catch(RegExpNotFoundException notFound) {
			throw new Exception("Non attesa eccezione 'MULTIPLE RESULTS': "+notFound.getMessage());
		}
		try {
			List<String> l = RegularExpressionEngine.getAllStringFindPattern(urlMultipla, absoluteExpression);
			System.out.println("ALL [FIND] ABSOLUTE 'MULTIPLE RESULTS': "+l);
			if(l==null || l.size()!=1 || !("["+atteso2+"]").equals(l.toString())) {
				throw new Exception("Attesa lista di 1 elemento '"+atteso2+"'");
			}
		}catch(RegExpNotFoundException notFound) {
			throw new Exception("Non attesa eccezione 'MULTIPLE RESULTS': "+notFound.getMessage());
		}
		
		
		

		
		try {
			boolean b = RegularExpressionEngine.isMatch(url, relativeExpression);
			System.out.println("BOOLEAN [MATCH] RELATIVE: "+b);
			if(b) {
				throw new Exception("Atteso false");
			}
		}catch(RegExpNotFoundException notFound) {
			throw new Exception("Non attesa eccezione: "+notFound.getMessage());
		}
		try {
			boolean b = RegularExpressionEngine.isMatch(url, absoluteExpression);
			System.out.println("BOOLEAN [MATCH] ABSOLUTE: "+b);
			if(!b) {
				throw new Exception("Atteso true");
			}
		}catch(RegExpNotFoundException notFound) {
			throw new Exception("Non attesa eccezione: "+notFound.getMessage());
		}
		
		try {
			boolean b = RegularExpressionEngine.isFind(url, relativeExpression);
			System.out.println("BOOLEAN [FIND] RELATIVE: "+b);
			if(!b) {
				throw new Exception("Atteso true");
			}
		}catch(RegExpNotFoundException notFound) {
			throw new Exception("Non attesa eccezione: "+notFound.getMessage());
		}
		try {
			boolean b = RegularExpressionEngine.isFind(url, absoluteExpression);
			System.out.println("BOOLEAN [FIND] ABSOLUTE: "+b);
			if(!b) {
				throw new Exception("Atteso true");
			}
		}catch(RegExpNotFoundException notFound) {
			throw new Exception("Non attesa eccezione: "+notFound.getMessage());
		}
		
	}

}
