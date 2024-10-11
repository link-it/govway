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
package org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.applicativi_token;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.soap.SOAPFault;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ProblemUtilities;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.core.token.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWSOptions;
import org.openspcoop2.utils.security.JsonSignature;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;
import org.w3c.dom.Node;

import net.minidev.json.JSONObject;

/**
* Utilities
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class Utilities {


	public static String HEADER_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN = "govway-testsuite-govway-token-application";
	public static String HEADER_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN_TIPO_SOGGETTO = "govway-testsuite-govway-token-sender-type";
	public static String HEADER_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN_SOGGETTO = "govway-testsuite-govway-token-sender";
	
	public static String URL_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN = "govway-testsuite-url-govway_token_application";
	public static String URL_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN_TIPO_SOGGETTO = "govway-testsuite-url-govway_token_sender_type";
	public static String URL_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN_SOGGETTO = "govway-testsuite-url-govway_token_sender";
	
	public static void validateResponseHeaderIntegrazione(HttpResponse resp, IDServizioApplicativo idSA) throws Exception {
		if(resp==null || resp.getHeadersValues()==null || resp.getHeadersValues().isEmpty()) {
			throw new Exception("Header non ritornati");
		}
		
		String v = resp.getHeaderFirstValue(Utilities.HEADER_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN);
		if(v==null || StringUtils.isEmpty(v)) {
			throw new Exception("Header atteso '"+Utilities.HEADER_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN+"' non presente");
		}
		if(!idSA.getNome().equals(v)) {
			throw new Exception("Header atteso '"+Utilities.HEADER_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN+"' possiede un valore '"+v+"' differente da quello atteso '"+idSA.getNome()+"'");
		}

		v = resp.getHeaderFirstValue(Utilities.HEADER_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN_TIPO_SOGGETTO);
		if(v==null || StringUtils.isEmpty(v)) {
			throw new Exception("Header atteso '"+Utilities.HEADER_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN_TIPO_SOGGETTO+"' non presente");
		}
		if(!idSA.getIdSoggettoProprietario().getTipo().equals(v)) {
			throw new Exception("Header atteso '"+Utilities.HEADER_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN_TIPO_SOGGETTO+"' possiede un valore '"+v+"' differente da quello atteso '"+idSA.getIdSoggettoProprietario().getTipo()+"'");
		}
		
		v = resp.getHeaderFirstValue(Utilities.HEADER_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN_SOGGETTO);
		if(v==null || StringUtils.isEmpty(v)) {
			throw new Exception("Header atteso '"+Utilities.HEADER_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN_SOGGETTO+"' non presente");
		}
		if(!idSA.getIdSoggettoProprietario().getNome().equals(v)) {
			throw new Exception("Header atteso '"+Utilities.HEADER_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN_SOGGETTO+"' possiede un valore '"+v+"' differente da quello atteso '"+idSA.getIdSoggettoProprietario().getNome()+"'");
		}
		
		
		v = resp.getHeaderFirstValue(Utilities.URL_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN);
		if(v==null || StringUtils.isEmpty(v)) {
			throw new Exception("Header atteso '"+Utilities.URL_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN+"' non presente");
		}
		if(!idSA.getNome().equals(v)) {
			throw new Exception("Header atteso '"+Utilities.URL_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN+"' possiede un valore '"+v+"' differente da quello atteso '"+idSA.getNome()+"'");
		}

		v = resp.getHeaderFirstValue(Utilities.URL_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN_TIPO_SOGGETTO);
		if(v==null || StringUtils.isEmpty(v)) {
			throw new Exception("Header atteso '"+Utilities.URL_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN_TIPO_SOGGETTO+"' non presente");
		}
		if(!idSA.getIdSoggettoProprietario().getTipo().equals(v)) {
			throw new Exception("Header atteso '"+Utilities.URL_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN_TIPO_SOGGETTO+"' possiede un valore '"+v+"' differente da quello atteso '"+idSA.getIdSoggettoProprietario().getTipo()+"'");
		}
		
		v = resp.getHeaderFirstValue(Utilities.URL_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN_SOGGETTO);
		if(v==null || StringUtils.isEmpty(v)) {
			throw new Exception("Header atteso '"+Utilities.URL_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN_SOGGETTO+"' non presente");
		}
		if(!idSA.getIdSoggettoProprietario().getNome().equals(v)) {
			throw new Exception("Header atteso '"+Utilities.URL_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN_SOGGETTO+"' possiede un valore '"+v+"' differente da quello atteso '"+idSA.getIdSoggettoProprietario().getNome()+"'");
		}
	}
	
	public static String SOAP_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN = "govway-testsuite-soap-govway_token_application";
	public static String SOAP_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN_TIPO_SOGGETTO = "govway-testsuite-soap-govway_token_sender_type";
	public static String SOAP_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN_SOGGETTO = "govway-testsuite-soap-govway_token_sender";
	
	public static void validateResponseHeaderIntegrazioneSoap(HttpResponse resp, IDServizioApplicativo idSA) throws Exception {
		if(resp==null || resp.getHeadersValues()==null || resp.getHeadersValues().isEmpty()) {
			throw new Exception("Header non ritornati");
		}
		
		String v = resp.getHeaderFirstValue(Utilities.SOAP_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN);
		if(v==null || StringUtils.isEmpty(v)) {
			throw new Exception("Header atteso '"+Utilities.SOAP_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN+"' non presente");
		}
		if(!idSA.getNome().equals(v)) {
			throw new Exception("Header atteso '"+Utilities.SOAP_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN+"' possiede un valore '"+v+"' differente da quello atteso '"+idSA.getNome()+"'");
		}

		v = resp.getHeaderFirstValue(Utilities.SOAP_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN_TIPO_SOGGETTO);
		if(v==null || StringUtils.isEmpty(v)) {
			throw new Exception("Header atteso '"+Utilities.SOAP_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN_TIPO_SOGGETTO+"' non presente");
		}
		if(!idSA.getIdSoggettoProprietario().getTipo().equals(v)) {
			throw new Exception("Header atteso '"+Utilities.SOAP_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN_TIPO_SOGGETTO+"' possiede un valore '"+v+"' differente da quello atteso '"+idSA.getIdSoggettoProprietario().getTipo()+"'");
		}
		
		v = resp.getHeaderFirstValue(Utilities.SOAP_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN_SOGGETTO);
		if(v==null || StringUtils.isEmpty(v)) {
			throw new Exception("Header atteso '"+Utilities.SOAP_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN_SOGGETTO+"' non presente");
		}
		if(!idSA.getIdSoggettoProprietario().getNome().equals(v)) {
			throw new Exception("Header atteso '"+Utilities.SOAP_TESTSUITE_INTEGRAZIONE_APPLICATIVO_TOKEN_SOGGETTO+"' possiede un valore '"+v+"' differente da quello atteso '"+idSA.getIdSoggettoProprietario().getNome()+"'");
		}
		
	}
		
	
	
	public static String HEADER_TESTSUITE_APPLICATIVO_TOKEN = "govway-testsuite-applicativotoken";
	
	public static String HEADER_TESTSUITE_APPLICATIVO_TOKEN_CONFIG = "govway-testsuite-applicativotoken-config";
	public static String HEADER_TESTSUITE_APPLICATIVO_TOKEN_CONFIG_VALUE_ATTESO = "VT";
	
	public static String HEADER_TESTSUITE_APPLICATIVO_TOKEN_CONFIG_SOGGETTO = "govway-testsuite-applicativotoken-config-soggetto";
	public static String HEADER_TESTSUITE_APPLICATIVO_TOKEN_CONFIG_SOGGETTO_VALUE_ATTESO = "VSOGGT";
	
	public static void validateResponseHeaderTrasformazioni(HttpResponse resp, boolean expected, String nomeAtteso) throws Exception {
		if(resp==null || resp.getHeadersValues()==null || resp.getHeadersValues().isEmpty()) {
			throw new Exception("Header non ritornati");
		}
		
		String v = resp.getHeaderFirstValue(Utilities.HEADER_TESTSUITE_APPLICATIVO_TOKEN);
		if(expected) {
			if(v==null || StringUtils.isEmpty(v)) {
				throw new Exception("Header atteso '"+Utilities.HEADER_TESTSUITE_APPLICATIVO_TOKEN+"' non presente");
			}
			if(!nomeAtteso.equals(v)) {
				throw new Exception("Header atteso '"+Utilities.HEADER_TESTSUITE_APPLICATIVO_TOKEN+"' possiede un valore '"+v+"' differente da quello atteso '"+nomeAtteso+"'");
			}
		}
		else {
			if(v!=null && !StringUtils.isEmpty(v)) {
				throw new Exception("Header '"+Utilities.HEADER_TESTSUITE_APPLICATIVO_TOKEN+"' non atteso");
			}
		}
		
		v = resp.getHeaderFirstValue(Utilities.HEADER_TESTSUITE_APPLICATIVO_TOKEN_CONFIG);
		if(expected) {
			if(v==null || StringUtils.isEmpty(v)) {
				throw new Exception("Header atteso '"+Utilities.HEADER_TESTSUITE_APPLICATIVO_TOKEN_CONFIG+"' non presente");
			}
			if(!Utilities.HEADER_TESTSUITE_APPLICATIVO_TOKEN_CONFIG_VALUE_ATTESO.equals(v)) {
				throw new Exception("Header atteso '"+Utilities.HEADER_TESTSUITE_APPLICATIVO_TOKEN_CONFIG+"' possiede un valore '"+v+"' differente da quello atteso '"+Utilities.HEADER_TESTSUITE_APPLICATIVO_TOKEN_CONFIG_VALUE_ATTESO+"'");
			}
		}
		else {
			if(v!=null && !StringUtils.isEmpty(v)) {
				throw new Exception("Header '"+Utilities.HEADER_TESTSUITE_APPLICATIVO_TOKEN_CONFIG+"' non atteso");
			}
		}
		
		v = resp.getHeaderFirstValue(Utilities.HEADER_TESTSUITE_APPLICATIVO_TOKEN_CONFIG_SOGGETTO);
		if(expected) {
			if(v==null || StringUtils.isEmpty(v)) {
				throw new Exception("Header atteso '"+Utilities.HEADER_TESTSUITE_APPLICATIVO_TOKEN_CONFIG_SOGGETTO+"' non presente");
			}
			if(!Utilities.HEADER_TESTSUITE_APPLICATIVO_TOKEN_CONFIG_SOGGETTO_VALUE_ATTESO.equals(v)) {
				throw new Exception("Header atteso '"+Utilities.HEADER_TESTSUITE_APPLICATIVO_TOKEN_CONFIG_SOGGETTO+"' possiede un valore '"+v+"' differente da quello atteso '"+Utilities.HEADER_TESTSUITE_APPLICATIVO_TOKEN_CONFIG_SOGGETTO_VALUE_ATTESO+"'");
			}
		}
		else {
			if(v!=null && !StringUtils.isEmpty(v)) {
				throw new Exception("Header '"+Utilities.HEADER_TESTSUITE_APPLICATIVO_TOKEN_CONFIG_SOGGETTO+"' non atteso");
			}
		}
	}
	
	public static String DIAGNOSTICO_NESSUN_APPLICATIVO_IDENTIFICATO = "nessun applicativo identificato";
	
	public static IDSoggetto soggettoInternoTest = new IDSoggetto("gw", "SoggettoInternoTest");
	public static IDSoggetto soggettoInternoTestFruitore = new IDSoggetto("gw", "SoggettoInternoTestFruitore");
	
	
	public static String Ruolo1FonteQualsiasi = "Ruolo1TestAutenticazioneTokenFonteQualsiasi";
	public static String Ruolo2FonteRegistro = "Ruolo2TestAutenticazioneTokenFonteRegistro";
	public static String Ruolo3FonteEsterna_idEsterno = "role3Esterno";
	public static String Ruolo3FonteEsterna_idGovWay = "Ruolo3TestAutenticazioneTokenFonteEsterna";
	public static List<String> listaRuolo3 = new ArrayList<>();
	static {
		listaRuolo3.add(Ruolo3FonteEsterna_idEsterno);
	}
	
	
	public static String clientIdNonRegistrato = "clientIdNonRegistrato";
	
	
	public static IDServizioApplicativo idApplicativoToken1SoggettoEsterno = new IDServizioApplicativo();
	static {
		idApplicativoToken1SoggettoEsterno.setIdSoggettoProprietario(new IDSoggetto("gw", "SoggettoEsternoTest"));
		idApplicativoToken1SoggettoEsterno.setNome("ApplicativoToken1SoggettoEsterno");
	}
	public static String clientIdApplicativoToken1SoggettoEsterno = "clientIdApplicativoToken1SoggettoEsterno";
	
	public static IDServizioApplicativo idApplicativoToken2SoggettoEsterno = new IDServizioApplicativo();
	static {
		idApplicativoToken2SoggettoEsterno.setIdSoggettoProprietario(new IDSoggetto("gw", "SoggettoEsternoTest"));
		idApplicativoToken2SoggettoEsterno.setNome("ApplicativoToken2SoggettoEsterno");
	}
	public static String clientIdApplicativoToken2SoggettoEsterno = "clientIdApplicativoToken2SoggettoEsterno";
	
	public static IDServizioApplicativo idApplicativoToken3SoggettoEsterno = new IDServizioApplicativo();
	static {
		idApplicativoToken3SoggettoEsterno.setIdSoggettoProprietario(new IDSoggetto("gw", "SoggettoEsternoTest"));
		idApplicativoToken3SoggettoEsterno.setNome("ApplicativoToken3SoggettoEsterno");
	}
	public static String clientIdApplicativoToken3SoggettoEsterno = "clientIdApplicativoToken3SoggettoEsterno";
	
	
	public static IDServizioApplicativo idApplicativoToken1SoggettoInternoFruitore = new IDServizioApplicativo();
	static {
		idApplicativoToken1SoggettoInternoFruitore.setIdSoggettoProprietario(new IDSoggetto("gw", "SoggettoInternoTestFruitore"));
		idApplicativoToken1SoggettoInternoFruitore.setNome("ApplicativoToken1SoggettoInternoFruitore");
	}
	public static String clientIdApplicativoToken1SoggettoInternoFruitore = "clientIdApplicativoToken1SoggettoInternoFruitore";
	
	public static IDServizioApplicativo idApplicativoToken2SoggettoInternoFruitore = new IDServizioApplicativo();
	static {
		idApplicativoToken2SoggettoInternoFruitore.setIdSoggettoProprietario(new IDSoggetto("gw", "SoggettoInternoTestFruitore"));
		idApplicativoToken2SoggettoInternoFruitore.setNome("ApplicativoToken2SoggettoInternoFruitore");
	}
	public static String clientIdApplicativoToken2SoggettoInternoFruitore = "clientIdApplicativoToken2SoggettoInternoFruitore";
	
	public static IDServizioApplicativo idApplicativoToken3SoggettoInternoFruitore = new IDServizioApplicativo();
	static {
		idApplicativoToken3SoggettoInternoFruitore.setIdSoggettoProprietario(new IDSoggetto("gw", "SoggettoInternoTestFruitore"));
		idApplicativoToken3SoggettoInternoFruitore.setNome("ApplicativoToken3SoggettoInternoFruitore");
	}
	public static String clientIdApplicativoToken3SoggettoInternoFruitore = "clientIdApplicativoToken3SoggettoInternoFruitore";
	
	
	public static IDServizioApplicativo idApplicativoToken1SoggettoInterno = new IDServizioApplicativo();
	static {
		idApplicativoToken1SoggettoInterno.setIdSoggettoProprietario(new IDSoggetto("gw", "SoggettoInternoTest"));
		idApplicativoToken1SoggettoInterno.setNome("ApplicativoToken1SoggettoInterno");
	}
	public static String clientIdApplicativoToken1SoggettoInterno = "clientIdApplicativoToken1SoggettoInterno";
	
	public static IDServizioApplicativo idApplicativoToken2SoggettoInterno = new IDServizioApplicativo();
	static {
		idApplicativoToken2SoggettoInterno.setIdSoggettoProprietario(new IDSoggetto("gw", "SoggettoInternoTest"));
		idApplicativoToken2SoggettoInterno.setNome("ApplicativoToken2SoggettoInterno");
	}
	public static String clientIdApplicativoToken2SoggettoInterno = "clientIdApplicativoToken2SoggettoInterno";
	
	public static IDServizioApplicativo idApplicativoToken3SoggettoInterno = new IDServizioApplicativo();
	static {
		idApplicativoToken3SoggettoInterno.setIdSoggettoProprietario(new IDSoggetto("gw", "SoggettoInternoTest"));
		idApplicativoToken3SoggettoInterno.setNome("ApplicativoToken3SoggettoInterno");
	}
	public static String clientIdApplicativoToken3SoggettoInterno = "clientIdApplicativoToken3SoggettoInterno";
	
	
	
	public static String basicPasswordApplicativoTrasportoXSoggettoInternoFruitore = "123456";
	
	public static IDServizioApplicativo idApplicativoTrasporto1SoggettoInternoFruitore = new IDServizioApplicativo();
	static {
		idApplicativoTrasporto1SoggettoInternoFruitore.setIdSoggettoProprietario(new IDSoggetto("gw", "SoggettoInternoTestFruitore"));
		idApplicativoTrasporto1SoggettoInternoFruitore.setNome("ApplicativoTrasporto1SoggettoInternoFruitore");
	}
	public static String basicUsernameApplicativoTrasporto1SoggettoInternoFruitore = "basicUsernameApplicativoTrasporto1SoggettoInternoFruitore";
	
	public static IDServizioApplicativo idApplicativoTrasporto2SoggettoInternoFruitore = new IDServizioApplicativo();
	static {
		idApplicativoTrasporto2SoggettoInternoFruitore.setIdSoggettoProprietario(new IDSoggetto("gw", "SoggettoInternoTestFruitore"));
		idApplicativoTrasporto2SoggettoInternoFruitore.setNome("ApplicativoTrasporto2SoggettoInternoFruitore");
	}
	public static String basicUsernameApplicativoTrasporto2SoggettoInternoFruitore = "basicUsernameApplicativoTrasporto2SoggettoInternoFruitore";
	
	public static IDServizioApplicativo idApplicativoTrasporto3SoggettoInternoFruitore = new IDServizioApplicativo();
	static {
		idApplicativoTrasporto3SoggettoInternoFruitore.setIdSoggettoProprietario(new IDSoggetto("gw", "SoggettoInternoTestFruitore"));
		idApplicativoTrasporto3SoggettoInternoFruitore.setNome("ApplicativoTrasporto3SoggettoInternoFruitore");
	}
	public static String basicUsernameApplicativoTrasporto3SoggettoInternoFruitore = "basicUsernameApplicativoTrasporto3SoggettoInternoFruitore";
	
	public static IDServizioApplicativo idApplicativoTrasporto4SoggettoInternoFruitore = new IDServizioApplicativo();
	static {
		idApplicativoTrasporto4SoggettoInternoFruitore.setIdSoggettoProprietario(new IDSoggetto("gw", "SoggettoInternoTestFruitore"));
		idApplicativoTrasporto4SoggettoInternoFruitore.setNome("ApplicativoTrasporto4SoggettoInternoFruitore");
	}
	public static String basicUsernameApplicativoTrasporto4SoggettoInternoFruitore = "basicUsernameApplicativoTrasporto4SoggettoInternoFruitore";
	
	public static String SOLO_INFO_SOGGETTO = "__SOLOSOGGETTO__";
	public static IDServizioApplicativo idSoggettoTrasportoFruitore = new IDServizioApplicativo();
	static {
		idSoggettoTrasportoFruitore.setIdSoggettoProprietario(new IDSoggetto("gw", "SoggettoTrasportoFruitore"));
		idSoggettoTrasportoFruitore.setNome(SOLO_INFO_SOGGETTO);
	}
	public static String basicUsernameSoggettoTrasportoFruitore = "basicUsernameSoggettoTrasportoFruitore";
	
	
	public static String buildJson(String clientIdParam, List<String> roles, boolean utente) throws Exception {
			
		String prefix = "TEST";
				
		String issuer = "testAuthToken";
		String subject = "10623542342342323";
		String jti = "33aa1676-1f9e-34e2-8515-0cfca111a188";
		Date now = DateManager.getDate();
		Date campione = new Date( (now.getTime()/1000)*1000);
		Date iat = new Date(campione.getTime());
		Date nbf = new Date(campione.getTime() - (1000*20));
		Date exp = new Date(campione.getTime() + (1000*60));
		String fullName = "Mario Bianchi Rossi";
		String firstName = "Mario";
		String middleName = "Bianchi";
		String familyName = "Rossi";
		String email = "mariorossi@govway.org";
		String audience = "testAutenticaziontToken.apps";
		String aud = "\""+prefix+"aud\":[\""+audience+"\"]";
		
		String jsonInput = 
				"{ "+aud+",";

		String clientId = "\""+prefix+"client_id\":\""+clientIdParam+"\"";
		jsonInput = jsonInput+
				clientId+" ,";
				
		String sub = "\""+prefix+"sub\":\""+subject+"\"";
		jsonInput = jsonInput+
				sub+" , ";

		String iss ="\""+prefix+"iss\":\""+issuer+"\"";
		jsonInput = jsonInput+
				iss+" , ";

		if(utente) {
			String u = "\""+prefix+"username\":\""+"mariorossi"+"\"";
			String name = "\""+prefix+"name\":\""+fullName+"\"";
			jsonInput = jsonInput+
					u+" ,"+
					name+" , ";
					
			String mail = "\""+prefix+"email\":\""+email+"\"";
			jsonInput = jsonInput+
					mail+" , ";
			
			String gName = "\""+prefix+"given_name\":\""+firstName+"\"";
			jsonInput = jsonInput +
					gName+" , ";
			
			String mName = "\""+prefix+"middle_name\":\""+middleName+"\"";
			jsonInput = jsonInput +
					mName+" , ";
			
			String fName = "\""+prefix+"family_name\":\""+familyName+"\"";
			jsonInput = jsonInput +
					fName+" , ";
		}
		
		String roleValue = "";
		if(roles!=null && !roles.isEmpty()) {
			for (String r : roles) {
				if(roleValue.length()>0) {
					roleValue+=",";
				}
				else {
					roleValue+="[";
				}
				roleValue+="\""+r+"\"";
			}
		}
		if(roleValue.length()>0) {
			String r = "\""+prefix+"role\":"+roleValue+"]";
			jsonInput = jsonInput +
					r+" ,";
		}
		
		String iatJson = "\""+prefix+"iat\":"+(iat.getTime()/1000)+""; 
		jsonInput = jsonInput +
				iatJson + " , ";
		
		String nbfJson = "\""+prefix+"nbf\":"+(nbf.getTime()/1000)+"";
		jsonInput = jsonInput +
				nbfJson+ " , ";
		
		String expJson = "\""+prefix+"exp\":"+(exp.getTime()/1000)+"";
		jsonInput = jsonInput +
				expJson + " ,  ";
		
		String jtiS = "\""+prefix+"jti\":\""+jti+"\"";
		jsonInput = jsonInput +
				" "+jtiS+"}";
		
		//System.out.println("TOKEN ["+jsonInput+"]");
		
		return jsonInput;
	}
	
	public static String buildJWT(String clientIdParam, List<String> roles, boolean utente) throws Exception {
		
		String jsonInput = Utilities.buildJson(clientIdParam, roles, utente); 
		
		//System.out.println("TOKEN ["+jsonInput+"]");
		
		Properties props = new Properties();
		props.put("rs.security.keystore.type","JKS");
		String password = "openspcoop";
		props.put("rs.security.keystore.file", "/etc/govway/keys/erogatore.jks");
		props.put("rs.security.keystore.alias","erogatore");
		props.put("rs.security.keystore.password",password);
		props.put("rs.security.key.password",password);
				
		JWSOptions options = new JWSOptions(JOSESerialization.COMPACT);
			
		props.put("rs.security.signature.algorithm","RS256");
		props.put("rs.security.signature.include.cert","false");
		props.put("rs.security.signature.include.key.id","true");
		props.put("rs.security.signature.include.public.key","false");
		props.put("rs.security.signature.include.cert.sha1","false");
		props.put("rs.security.signature.include.cert.sha256","false");
			
		JsonSignature jsonSignature = new JsonSignature(props, options);
		String token = jsonSignature.sign(jsonInput);
		//System.out.println(token);
			
		return token;		
		
	}
	
	
	
	public static HttpResponse _test(TipoServizio tipoServizio, Logger logCore, String api, String operazione,
			IDServizioApplicativo idApplicativoToken, String clientId, List<String> roles,
			IDSoggetto idSoggettoFruitoreTrasporto, IDServizioApplicativo idApplicativoTrasporto, String authBasic, 
			String msgError) throws Exception {
		
		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+api+"/v1/"+operazione;
		
		boolean rest = api.toLowerCase().contains("rest");
		
		String contentType = null;
		byte[]content = null;
		if(rest) {
			contentType = HttpConstants.CONTENT_TYPE_JSON;
			content = Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
		}
		else {
			contentType = HttpConstants.CONTENT_TYPE_SOAP_1_1;
			content = Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes();
		}
		
		HttpRequest request = new HttpRequest();
		
		boolean utente = false;
		String token = Utilities.buildJWT(clientId, roles, utente);
		
		Map<String, String> queryParameters = new HashMap<>();
		Map<String, String> headers = new HashMap<>();
		if(authBasic!=null) {
			queryParameters.put(Costanti.RFC6750_URI_QUERY_PARAMETER_ACCESS_TOKEN,token);
			request.setUsername(authBasic);
			request.setPassword(basicPasswordApplicativoTrasportoXSoggettoInternoFruitore);
		}
		else {
			headers.put(HttpConstants.AUTHORIZATION,HttpConstants.AUTHORIZATION_PREFIX_BEARER+token);
		}
		if(!rest) {
			headers.put(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION,"test");	
		}
		if(queryParameters!=null && !queryParameters.isEmpty()) {
			for (String key : queryParameters.keySet()) {
				url+= url.contains("?") ? "&" : "?";
				url+=key;
				url+="=";
				url+=queryParameters.get(key);
			}
		}
		if(headers!=null && !headers.isEmpty()) {
			for (String hdrName : headers.keySet()) {
				request.addHeader(hdrName, headers.get(hdrName));
			}
		}
		
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		request.setContent(content);
		request.setUrl(url);
						
		HttpResponse response = null;
		try {
			//System.out.println("INVOKE ["+request.getUrl()+"]");
			response = HttpUtilities.httpInvoke(request);
		}catch(Throwable t) {
			throw t;
		}

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		if((msgError!=null && !msgError.equals(Utilities.DIAGNOSTICO_NESSUN_APPLICATIVO_IDENTIFICATO))) {
			
			int code = -1;
			String error = null;
			String msg = null;
			String soapPrefixError = "Client";
			
			if(msgError.contains("differente dal soggetto proprietario della porta invocata")) {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_AUTENTICAZIONE_TOKEN);
				code = 401;
				error = "AuthenticationFailed";
				msg = "Authentication Failed";
			}
			else if(msgError.contains("non è autorizzato ad invocare il servizio") || msgError.contains("non risulta autorizzato")){
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_AUTORIZZAZIONE);
				code = 403;
				error = "AuthorizationDeny";
				msg = "Authorization deny";
				if(operazione.toLowerCase().contains("xacml")) {
					error = "AuthorizationPolicyDeny";
					msg = "Authorization deny by policy";
				}
			}
			else if(msgError.contains("Role") && msgError.contains("not found in")){
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_AUTORIZZAZIONE);
				code = 403;
				error = "AuthorizationMissingRole";
				msg = "Authorization deny by role";
			}
			else if(msgError.contains("unprocessable dynamic value") || msgError.contains("with unexpected value")){
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_AUTORIZZAZIONE);
				code = 403;
				error = "AuthorizationContentDeny";
				msg = "Unauthorized request content";
			}
			verifyKo(response, error, code, msg, true,
					rest, soapPrefixError,
					logCore);
		
		}
		else {
			verifyOk(response, 200, contentType);
		}
						
		DBVerifier.verify(idTransazione, esitoExpected, msgError,
				clientId, idApplicativoToken, 
				idSoggettoFruitoreTrasporto, idApplicativoTrasporto);
		
		return response;
		
	}
	
	public static void verifyOk(HttpResponse response, int code, String expectedContentType) {
		
		assertEquals(code, response.getResultHTTPOperation());
		assertEquals(expectedContentType, response.getContentType());
		
	}
	
	public static void verifyKo(HttpResponse response, String error, int code, String errorMsg, boolean checkErrorTypeGovWay, 
			boolean rest, String soapPrefixError,
			Logger log) {
		verifyKo(response, error, code, errorMsg, checkErrorTypeGovWay, 
				rest, soapPrefixError,
				error,
				log);
	}
	public static void verifyKo(HttpResponse response, String error, int code, String errorMsg, boolean checkErrorTypeGovWay, 
			boolean rest, String soapPrefixError,
			String errorHTTP,
			Logger log) {
		
		if(rest) {
		
			assertEquals(code, response.getResultHTTPOperation());
			
			if(error!=null) {
				assertEquals(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807, response.getContentType());
				
				try {
					JsonPathExpressionEngine jsonPath = new JsonPathExpressionEngine();
					JSONObject jsonResp = JsonPathExpressionEngine.getJSONObject(new String(response.getContent()));
					
					assertEquals("https://govway.org/handling-errors/"+code+"/"+error+".html", jsonPath.getStringMatchPattern(jsonResp, "$.type").get(0));
					assertEquals(error, jsonPath.getStringMatchPattern(jsonResp, "$.title").get(0));
					assertEquals(code, jsonPath.getNumberMatchPattern(jsonResp, "$.status").get(0));
					assertNotEquals(null, jsonPath.getStringMatchPattern(jsonResp, "$.govway_id").get(0));	
					assertEquals(true, jsonPath.getStringMatchPattern(jsonResp, "$.detail").get(0).equals(errorMsg));
					
					if(checkErrorTypeGovWay) {
						assertEquals(error, response.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
					}
		
					if(code==504) {
						assertNotNull(response.getHeaderFirstValue(HttpConstants.RETRY_AFTER));
					}
					
				} catch (Exception e) {
					throw new UtilsRuntimeException(e.getMessage(),e);
				}
			}
			
		}
		
		else {
			
			assertEquals(500, response.getResultHTTPOperation());
			assertEquals(HttpConstants.CONTENT_TYPE_SOAP_1_1, response.getContentType());
			
			try {
				OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
				OpenSPCoop2MessageParseResult parse = factory.createMessage(MessageType.SOAP_11, MessageRole.NONE, response.getContentType(), response.getContent());
				OpenSPCoop2Message msg = parse.getMessage_throwParseThrowable();
				OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
				
				assertEquals(true ,soapMsg.isFault());
				SOAPFault soapFault = soapMsg.getSOAPBody().getFault();
				assertNotNull(soapFault);
				
				assertNotNull(soapFault.getFaultCodeAsQName());
				String faultCode = soapFault.getFaultCodeAsQName().getLocalPart();
				assertNotNull(faultCode);
				assertEquals(soapPrefixError+"."+error, faultCode);
				
				String faultString = soapFault.getFaultString();
				assertNotNull(faultString);
				assertEquals(errorMsg, faultString);
				
				String faultActor = soapFault.getFaultActor();
				assertNotNull(faultActor);
				assertEquals("http://govway.org/integration", faultActor);
							
				assertEquals(true ,ProblemUtilities.existsProblem(soapFault.getDetail(), log));
				Node problemNode = ProblemUtilities.getProblem(soapFault.getDetail(), log);
				
				ProblemUtilities.verificaProblem(problemNode, 
						code, error, errorMsg, true, 
						log);
				
				assertEquals(errorHTTP, response.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
				
				if(code==504) {
					assertNotNull(response.getHeaderFirstValue(HttpConstants.RETRY_AFTER));
				}

			} catch (Throwable e) {
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			
		}
		
	}
	
}
