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

package org.openspcoop2.utils.regexp;

/**
 * Test
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Test {

	public static void main(String[] args) throws Exception {
		test();
	}
	
	public static void test() throws Exception {
		
		String url = "/govway/out/ENTE/PubblicoAmministratore/TEST/v1/uuid23/altroContesto";
		
		// obiettivo: estrarre 'uuid23'
		String relativeExpression = "/([^/]+)/altroContesto";
		String absoluteExpression = ".*/([^/]+)/altroContesto";
		
		try {
			System.out.println("SINGLE [MATCH] RELATIVE: "+RegularExpressionEngine.getStringMatchPattern(url, relativeExpression));
			throw new Exception("Attesa eccezione");
		}catch(RegExpNotFoundException notFound) {
			System.out.println("SINGLE [MATCH] RELATIVE NOT FOUND: "+notFound.getMessage());
		}
		try {
			System.out.println("SINGLE [MATCH] ABSOLUTE: "+RegularExpressionEngine.getStringMatchPattern(url, absoluteExpression));
		}catch(RegExpNotFoundException notFound) {
			throw new Exception("Non attesa eccezione: "+notFound.getMessage());
		}
		
		try {
			System.out.println("SINGLE [FIND] RELATIVE: "+RegularExpressionEngine.getStringFindPattern(url, relativeExpression));
		}catch(RegExpNotFoundException notFound) {
			throw new Exception("Non attesa eccezione: "+notFound.getMessage());
		}
		try {
			System.out.println("SINGLE [FIND] ABSOLUTE: "+RegularExpressionEngine.getStringFindPattern(url, absoluteExpression));
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
			System.out.println("ALL [MATCH] ABSOLUTE: "+RegularExpressionEngine.getAllStringMatchPattern(url, absoluteExpression));
		}catch(RegExpNotFoundException notFound) {
			throw new Exception("Non attesa eccezione: "+notFound.getMessage());
		}
		
		try {
			System.out.println("ALL [FIND] RELATIVE: "+RegularExpressionEngine.getAllStringFindPattern(url, relativeExpression));
		}catch(RegExpNotFoundException notFound) {
			throw new Exception("Non attesa eccezione: "+notFound.getMessage());
		}
		try {
			System.out.println("ALL [FIND] ABSOLUTE: "+RegularExpressionEngine.getAllStringFindPattern(url, absoluteExpression));
		}catch(RegExpNotFoundException notFound) {
			throw new Exception("Non attesa eccezione: "+notFound.getMessage());
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
