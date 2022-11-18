/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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


package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;

/**
 * RequestBuilder
 * 
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RequestBuilder {

	public static HttpRequest buildRequest_HeaderHttp(String connettore, String erogazione) {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-header-http"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		request.addHeader(Common.HEADER_ID_CONDIZIONE, connettore);
		
		return request;
	}
	

	public static HttpRequest buildRequest_Statica(String erogazione) {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-statica"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		
		return request;
	}
	
	
	public static HttpRequest buildRequest_ClientIp(String erogazione) {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-client-ip"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		
		return request;
	}
	
	
	public static HttpRequest buildRestRequest(String erogazione) {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		
		return request;
	}
	
	public static HttpRequest buildRestRequestProblem(String erogazione) {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-header-http"
				+ "?&returnCode=500&replyQueryParameter=id_connettore&problem=true&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		
		return request;
	}

	public static HttpRequest buildRequest_UrlInvocazione(String connettore, String erogazione) {
		// L'espressione regolare sull'erogazione matcha il parametro query govway-testsuite-id_connettore_request.		
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-url-invocazione"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&govway-testsuite-id_connettore_request="+connettore); 		 
	
		return request;
	}

	public static HttpRequest buildRequest_ParametroUrl(String connettore, String erogazione) {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-parametro-url"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&govway-testsuite-id_connettore_request="+connettore); 		 
	
		return request;
	}

	public static HttpRequest buildRequest_Contenuto(String connettore, String erogazione) {
		final String content = "{ \"id_connettore_request\": \""+connettore+"\" }";
		
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-contenuto"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		request.setContentType("application/json");
		request.setContent(content.getBytes());
	
		return request;
	}

	public static HttpRequest buildRequest_Template(String connettore, String erogazione) {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-template"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&govway-testsuite-id_connettore_request="+connettore); 		 
	
		return request;
	}

	public static HttpRequest buildRequest_FreemarkerTemplate(String connettore, String erogazione) {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-freemarker-template"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&govway-testsuite-id_connettore_request="+connettore); 		 
	
		return request;
	}

	public static HttpRequest buildRequest_VelocityTemplate(String connettore, String erogazione) {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-velocity-template"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&govway-testsuite-id_connettore_request="+connettore); 		 
	
		return request;
	}

	public static List<HttpRequest> buildRequests_ForwardedFor(List<String> filtri, List<String> forwardingHeaders, String erogazione) {
		// Costruisco un'array di richieste, ciascuna richiesta è costruita scegliendo un filtro e il forwardingHeader
		// su cui inviarlo
		
		List<HttpRequest> ret = new ArrayList<>();
		
		// Faccio viaggiare ogni filtro su tutti gli headers
		for(String filtro : filtri) {
			for(String header : forwardingHeaders) {
				HttpRequest request = new HttpRequest();
				request.setMethod(HttpRequestMethod.GET);
				request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-xforwarded-for"
						+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
				request.addHeader(header, filtro);
				ret.add(request);
			}
		}
				
		return ret;
	}

	public static HttpRequest buildRequest_ForwardedFor(String connettore, String erogazione) throws UtilsException {
		return buildRequest_ForwardedFor(connettore, "X-Forwarded-For",  erogazione);
	}
	
	
	public static HttpRequest buildRequest_ForwardedFor(String connettore, String header,  String erogazione) throws UtilsException {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-xforwarded-for"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		request.addHeader(header, connettore);
		
		return request;
	}
	

	public static HttpRequest buildSoapRequest_Semplice(String erogazione, String contentTypeSoap) {
		String operazione = "operazioneSemplice";
		return RequestBuilder.buildSoapRequest(erogazione, operazione, operazione,  contentTypeSoap);
	}
	
	public static HttpRequest buildSoapRequestFault(String erogazione, String azione, String soapAction, String soapContentType) {
		String faultSoapVersion = soapContentType.equals(HttpConstants.CONTENT_TYPE_SOAP_1_1)  ?  "11" : "12";
		var requestSoapFault  = buildSoapRequest(erogazione, azione, soapAction, soapContentType);
		requestSoapFault.setUrl(requestSoapFault.getUrl() +"&returnCode=500&fault=true&faultSoapVersion="+faultSoapVersion);
		return requestSoapFault;
	}

	
	public static HttpRequest buildSoapRequest(String erogazione, String azione, String soapAction, String contentTypeSoap) {
		return buildSoapRequest(erogazione, azione, soapAction, contentTypeSoap, "");
	}

	public static HttpRequest buildSoapRequest(String erogazione, String azione, String soapAction, String contentTypeSoap, String body) {
		// nel soap 1.1 l'azione è specifica nello header SOAPAction
		// versioneSoap =[ HttpConstants.CONTENT_TYPE_SOAP_1_1; o  HttpConstants.CONTENT_TYPE_SOAP_1_2 ]
				
		if (contentTypeSoap.equals(HttpConstants.CONTENT_TYPE_SOAP_1_1)) {	
			String content = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +  
					"    <soap:Body>\n" + 
					"        <ns2:BodySoap1_1 xmlns:ns2=\"http://amministrazioneesempio.it/nomeinterfacciaservizio\">\n" +
					body +
					"        </ns2:BodySoap1_1>\n" + 
					"    </soap:Body>\n" + 
					"</soap:Envelope>";
			
			HttpRequest request = new HttpRequest();
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\""+soapAction+"\"");
			
			request.setMethod(HttpRequestMethod.POST);
			request.setContent(content.getBytes());
			request.setContentType(contentTypeSoap);
			request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/"+azione
					+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
			
			return request;
			
		} else if (contentTypeSoap.equals(HttpConstants.CONTENT_TYPE_SOAP_1_2)) {
			String content = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\n" +  
					"    <soap:Body>\n" + 
					"        <ns2:BodySoap1_2 xmlns:ns2=\"http://amministrazioneesempio.it/nomeinterfacciaservizio\">\n" +
					body +
					"        </ns2:BodySoap1_2>\n" + 
					"    </soap:Body>\n" + 
					"</soap:Envelope>";
			
			HttpRequest request = new HttpRequest();
			request.setMethod(HttpRequestMethod.POST);
			request.setContentType(contentTypeSoap + ";" + HttpConstants.SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION+"=\""+soapAction+"\"");
			request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/"+azione
					+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
			request.setContent(content.getBytes());
			
			return request;
			
		} else throw new IllegalArgumentException("Content Type Soap non riconosciuto: " + contentTypeSoap);
		
	}

	public static List<HttpRequest> buildSoapRequests(List<String> filtri, String erogazione, String contentTypeSoap) {
		return filtri.stream()
				.map( filtro -> buildSoapRequest(erogazione, filtro, filtro,  contentTypeSoap))
				.collect(Collectors.toList());
	}

	public static List<HttpRequest> buildRequests_Contenuto(List<String> filtri, String erogazione) {
		return filtri.stream()
				.map( filtro ->
					buildRequest_Contenuto(filtro, erogazione))
				.collect(Collectors.toList());
	}

	public static List<HttpRequest> buildRequests_Template(List<String> filtri, String erogazione) {
		return filtri.stream()
				.map( filtro ->
					buildRequest_Template(filtro, erogazione))
				.collect(Collectors.toList());				
	}

	public static List<HttpRequest> buildRequests_FreemarkerTemplate(List<String> filtri, String erogazione) {
		return filtri.stream()
				.map( filtro ->
					buildRequest_FreemarkerTemplate(filtro, erogazione))
				.collect(Collectors.toList());				
	}

	public static List<HttpRequest> buildRequests_VelocityTemplate(List<String> filtri, String erogazione) {
		return filtri.stream()
				.map( filtro ->
					buildRequest_VelocityTemplate(filtro, erogazione))
				.collect(Collectors.toList());
	}

	public static List<HttpRequest> buildRequests_ParametroUrl(List<String> filtri, String erogazione) {
		return filtri.stream()
				.map( filtro ->
					buildRequest_ParametroUrl(filtro, erogazione))
				.collect(Collectors.toList());
	}

	public static List<HttpRequest> buildRequests_UrlInvocazione(List<String> filtri, String erogazione) {
		return filtri.stream()
				.map( filtro ->
					buildRequest_UrlInvocazione(filtro, erogazione))
				.collect(Collectors.toList());
	}

	public static List<HttpRequest> buildRequests_HeaderHttp(List<String> filtri, String erogazione) {
		return filtri.stream()
				.map( filtro ->
					buildRequest_HeaderHttp(filtro, erogazione))
				.collect(Collectors.toList());
	}

}
