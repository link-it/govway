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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting;

import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils.PolicyAlias;

/**
* SoapBodies
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SoapBodies {
	
	private final static String minuto(String payload) { 
		return "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\n" +  
			"    <soap:Body>\n" + 
			"        <ns2:Minuto xmlns:ns2=\"http://amministrazioneesempio.it/nomeinterfacciaservizio\">\n" + 
						payload +
			"        </ns2:Minuto>\n" + 
			"    </soap:Body>\n" + 
			"</soap:Envelope>";
	}
		
	private final static String minutoDefault(String payload) { 
		return "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\n" +  
			"    <soap:Body>\n" + 
			"        <ns2:MinutoDefault xmlns:ns2=\"http://amministrazioneesempio.it/nomeinterfacciaservizio\">\n" +
						payload +
			"        </ns2:MinutoDefault>\n" + 
			"    </soap:Body>\n" + 
			"</soap:Envelope>";
	}
	
	
	private final static String giornaliero(String payload) {
		return "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\n" +  
			"    <soap:Body>\n" + 
			"        <ns2:Giornaliero xmlns:ns2=\"http://amministrazioneesempio.it/nomeinterfacciaservizio\">\n" +
						payload +
			"        </ns2:Giornaliero>\n" + 
			"    </soap:Body>\n" + 
			"</soap:Envelope>";
	}
	
	
	private final static String orario(String payload) { 
		return "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\n" +  
			"    <soap:Body>\n" + 
			"        <ns2:Orario xmlns:ns2=\"http://amministrazioneesempio.it/nomeinterfacciaservizio\">\n" +
						payload +
			"        </ns2:Orario>\n" + 
			"    </soap:Body>\n" + 
			"</soap:Envelope>";
	}
	
	private final static String richiesteSimultanee(String payload) {
		return "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\n" +  
			"    <soap:Body>\n" + 
			"        <ns2:RichiesteSimultanee xmlns:ns2=\"http://amministrazioneesempio.it/nomeinterfacciaservizio\">\n" +
						payload +
			"        </ns2:RichiesteSimultanee>\n" + 
			"    </soap:Body>\n" + 
			"</soap:Envelope>";
	}
	
	private final static String noPolicy(String payload) {
			return "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\n" +  
			"    <soap:Body>\n" + 
			"        <ns2:NoPolicy xmlns:ns2=\"http://amministrazioneesempio.it/nomeinterfacciaservizio\">\n" +
						payload +
			"        </ns2:NoPolicy>\n" + 
			"    </soap:Body>\n" + 
			"</soap:Envelope>";
	}
	
	
	public final static String get(PolicyAlias alias) {
		return get(alias, "");
	}
	
	public final static String get(PolicyAlias alias, String payload) {
		if (alias == null)
			return noPolicy(payload);	
		switch(alias) {
		case GIORNALIERO:
			return giornaliero(payload);
		case MINUTO:
			return minuto(payload);
		case MINUTODEFAULT:
			return minutoDefault(payload);
		case ORARIO:
			return orario(payload);
		case RICHIESTE_SIMULTANEE:
			return richiesteSimultanee(payload);
		case NO_POLICY:
			return noPolicy(payload);
		default:
			return null;
		}
	}
}
