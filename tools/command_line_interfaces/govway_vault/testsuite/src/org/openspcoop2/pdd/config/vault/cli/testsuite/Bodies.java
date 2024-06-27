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

package org.openspcoop2.pdd.config.vault.cli.testsuite;

/**
* Bodies
*
* @author Andrea Poli (andrea.poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class Bodies {
	
	private Bodies() {}
	
	public static final int SIZE_1K = 1024; // 1024 byte=1K
	
	private static final String PREFIX = "           ";
	
	@SuppressWarnings("unused")
	private static final String getXmlPayload(int sizePayload,String prefix) { 
		return getXmlPayload(sizePayload, prefix, null);
	}
	private static final String getXmlPayload(int sizePayload,String prefix, String applicativeId) { 
		StringBuilder sb = new StringBuilder();
		int index = 1;
		while(sb.length()<sizePayload) {
			sb.append(prefix).append("<xmlFragment"+index+">TEST ESEMPIO ").append(index).append("</xmlFragment"+(index++)+">\n");
		}
		if(applicativeId!=null) {
			sb.append(prefix).append(applicativeId).append("\n");
		}
		return sb.toString();
	}
	
	public static final String getXML(int sizePayload) { 
		return getXML(sizePayload, null);
	}
	public static final String getXML(int sizePayload, String applicativeId) { 
		return "<ns2:Test xmlns:ns2=\"http://govway.org/example\">\n" + 
				getXmlPayload(sizePayload,"",applicativeId) +
				"\n</ns2:Test>";
	}
	
	public static final String getSOAPEnvelope11(int sizePayload) { 
		return getSOAPEnvelope11(0, sizePayload, null);
	}
	public static final String getSOAPEnvelope11(int sizeHeader, int sizePayload) { 
		return getSOAPEnvelope11(sizeHeader, sizePayload, null);
	}
	public static final String getSOAPEnvelope11(int sizePayload, String applicativeId) { 
		return getSOAPEnvelope11(0, sizePayload, applicativeId);
	}
	public static final String getSOAPEnvelope11(int sizeHeader, int sizePayload, String applicativeId) { 
		String env = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n";
		if(sizeHeader>0) {
			env = env+
			"    <soap:Header>\n" + 
			"        <ns2:TestHdr xmlns:ns2=\"http://govway.org/example/header\" soap:actor=\"http://govway.org/example/header\">\n" + 
			getXmlPayload(sizeHeader,PREFIX,applicativeId) +
			"        </ns2:TestHdr>\n" +
			"    </soap:Header>\n";
		}
		env = env+
			"    <soap:Body>\n" + 
			"        <ns2:Test xmlns:ns2=\"http://govway.org/example\">\n" + 
			getXmlPayload(sizePayload,PREFIX,applicativeId) +
			"        </ns2:Test>\n" + 
			"    </soap:Body>\n" + 
			"</soap:Envelope>";
		return env;
	}
	
	public static final String getSOAPEnvelope12(int sizePayload) { 
		return getSOAPEnvelope12(0, sizePayload, null);
	}
	public static final String getSOAPEnvelope12(int sizeHeader, int sizePayload) { 
		return getSOAPEnvelope12(sizeHeader, sizePayload, null);
	}
	public static final String getSOAPEnvelope12(int sizePayload, String applicativeId) { 
		return getSOAPEnvelope12(0, sizePayload, applicativeId);
	}
	public static final String getSOAPEnvelope12(int sizeHeader, int sizePayload, String applicativeId) { 
		String env = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\n"; 
		if(sizeHeader>0) {
			env = env+
			"    <soap:Header>\n" + 
			"        <ns2:TestHdr xmlns:ns2=\"http://govway.org/example/header\" soap:actor=\"http://govway.org/example/header\">\n" + 
			getXmlPayload(sizeHeader,PREFIX,applicativeId) +
			"        </ns2:TestHdr>\n" +
			"    </soap:Header>\n";
		}
		env = env+
			"    <soap:Body>\n" + 
			"        <ns2:Test xmlns:ns2=\"http://govway.org/example\">\n" + 
			getXmlPayload(sizePayload,PREFIX,applicativeId) +
			"        </ns2:Test>\n" + 
			"    </soap:Body>\n" + 
			"</soap:Envelope>";
		return env;
	}
	
	
	private static final String getJsonPayload(int sizePayload) { 
		StringBuilder sb = new StringBuilder();
		int index = 1;
		while(sb.length()<sizePayload) {
			if(index>1) {
				sb.append(",");
			}
			sb.append("\n  \"claim-"+index+"\": \"TEST ESEMPIO ").append(index++).append("\"");
		}
		return sb.toString();
	}
	
	public static final String getJson(int sizePayload) { 
		return "{\n" + 
				getJsonPayload(sizePayload) +
			"\n}";
	}
	public static final String getJson(int sizePayload, String applicativeIdClaim) { 
		return "{\n" + 
				getJsonPayload(sizePayload) +
				","+
				"\n"+applicativeIdClaim+
			"\n}";
	}
		
}
