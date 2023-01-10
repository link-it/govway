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

package org.openspcoop2.protocol.trasparente.testsuite.units.soap.integrazione;

/**
 * IntegrazioneUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrazioneUtils {


	public static Object[][] getContentTypesOk(){
		return new Object[][]{
			{"text/xml"},
			{"text/xml;"},
			{"text/xml ;"},
			{"application/soap+xml; charset=UTF-8"},
			{"application/soap+xml;charset=UTF-8"},
			{"text/xml; altro=AAA; charset=UTF-8"},
			{"text/xml;charset=\"UTF-8\""},
		};	
	}
	
	public static Object[][] getContentTypesKo(){
		return new Object[][]{
			{"application","In Content-Type string <application>, expected '/', got null"},
			{"/","In Content-Type string </>, expected MIME type, got /"},
			{"application/","In Content-Type string <application/>, expected MIME subtype, got null"},
			{"/xml","In Content-Type string </xml>, expected MIME type, got /"},
			{"text/xml, charset=UTF-8","In parameter list <, charset=UTF-8>, expected ';', got \",\""},
			{"text/xml;charsetUTF-8","In parameter list <;charsetUTF-8>, expected '=', got \"null\""},
			// non e' ammissibile usare "" per contornare il tipe/subtype
			{"\"text/xml\"","In Content-Type string <\"text/xml\">, expected MIME type, got text/xml"},
			{"\"text/xml\";charset=\"UTF-8\"","In Content-Type string <\"text/xml\";charset=\"UTF-8\">, expected MIME type, got text/xml"}
		};	
	}
	
	public static Object[][] getAlternativeSoap12ContentTypesSupported(){
		return new Object[][]{
			{"application/soap"},
			{"application/soap; charset=UTF-8"},
			{"application/soap;charset=UTF-8"},
			{"text/soap; altro=AAA; charset=UTF-8"},
			{"altro/test;charset=\"UTF-8\""},
			{"altro/test"},
		};	
	}
	public static Object[][] getAlternativeSoap12ContentTypesUnsupported(){
		return new Object[][]{
			{"application/soap2; charset=UTF-8"},
			{"application/soap2;charset=UTF-8"},
			{"text/soap2; altro=AAA; charset=UTF-8"},
			{"altro/test2;charset=\"UTF-8\""},
		};	
	}
	

}
