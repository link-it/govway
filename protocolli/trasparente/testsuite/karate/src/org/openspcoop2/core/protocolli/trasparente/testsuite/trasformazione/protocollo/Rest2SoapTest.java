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
package org.openspcoop2.core.protocolli.trasparente.testsuite.trasformazione.protocollo;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
* Rest2SoapTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class Rest2SoapTest extends ConfigLoader {

	
	@Test
	public void ok_unilav() throws Exception {
		HttpResponse response = Utilities._test(TipoServizio.EROGAZIONE,HttpConstants.CONTENT_TYPE_JSON, FileSystemUtilities.readBytesFromFile("/etc/govway/test_trasformazioni/richiesteJson/unilav.json"),
				"TrasformazioneRest2SoapUnilav","apincn/unilav/json-comunicazione-obbligatoria", "Unilav", "ok_unilav.xml", HttpConstants.CONTENT_TYPE_SOAP_1_1, 200,
				null);
		Utilities.verificaJsonOk(Utilities.tipo_ok, Utilities.descr_notifica_ok, response);
	}
	
	@Test
	public void ko_unilav() throws Exception {
		HttpResponse response = Utilities._test(TipoServizio.EROGAZIONE,HttpConstants.CONTENT_TYPE_JSON, FileSystemUtilities.readBytesFromFile("/etc/govway/test_trasformazioni/richiesteJson/unilav.json"),
				"TrasformazioneRest2SoapUnilav","apincn/unilav/json-comunicazione-obbligatoria", "Unilav", "ko_unilav.xml", HttpConstants.CONTENT_TYPE_SOAP_1_1, 200,
				null);
		Utilities.verificaJsonOk(Utilities.tipo_ko, Utilities.descr_notifica_ko, response);
	}
	
	@Test
	public void anomalia_unilav() throws Exception {
		HttpResponse response = Utilities._test(TipoServizio.EROGAZIONE,HttpConstants.CONTENT_TYPE_JSON, FileSystemUtilities.readBytesFromFile("/etc/govway/test_trasformazioni/richiesteJson/unilav.json"),
				"TrasformazioneRest2SoapUnilav","apincn/unilav/json-comunicazione-obbligatoria", "Unilav", "anomalia_unilav.xml", HttpConstants.CONTENT_TYPE_SOAP_1_1, 200,
				null);
		Utilities.verificaJsonOk(Utilities.tipo_ko, Utilities.descr_notifica_ko_json, response);
	}
	
	@Test
	public void fault_server_unilav() throws Exception {
		HttpResponse response = Utilities._test(TipoServizio.EROGAZIONE,HttpConstants.CONTENT_TYPE_JSON, FileSystemUtilities.readBytesFromFile("/etc/govway/test_trasformazioni/richiesteJson/unilav.json"),
				"TrasformazioneRest2SoapUnilav","apincn/unilav/json-comunicazione-obbligatoria", "Unilav", "fault_server.xml", HttpConstants.CONTENT_TYPE_SOAP_1_1, 500,
				"Risposta consegnata al mittente con codice di trasporto: 500");
		Utilities.verificaJsonFault(Utilities.fault_code_server_soap, Utilities.fault_string_server, response);
	}
	
	@Test
	public void fault_client_unilav() throws Exception {
		HttpResponse response = Utilities._test(TipoServizio.EROGAZIONE,HttpConstants.CONTENT_TYPE_JSON, FileSystemUtilities.readBytesFromFile("/etc/govway/test_trasformazioni/richiesteJson/unilav.json"),
				"TrasformazioneRest2SoapUnilav","apincn/unilav/json-comunicazione-obbligatoria", "Unilav", "fault_client.xml", HttpConstants.CONTENT_TYPE_SOAP_1_1, 500,
				"Risposta consegnata al mittente con codice di trasporto: 500");
		Utilities.verificaJsonFault(Utilities.fault_code_client_soap, Utilities.fault_string_client, response);
	}
	
	
	@Test
	public void ok_uniurg() throws Exception {
		HttpResponse response = Utilities._test(TipoServizio.EROGAZIONE,HttpConstants.CONTENT_TYPE_JSON, FileSystemUtilities.readBytesFromFile("/etc/govway/test_trasformazioni/richiesteJson/uniurg.json"),
				"TrasformazioneRest2SoapUniurg","apincn/uniurg/json-comunicazione-obbligatoria", "Uniurg", "ok_uniurg.xml", HttpConstants.CONTENT_TYPE_SOAP_1_1, 200,
				null);
		Utilities.verificaJsonOk(Utilities.tipo_ok, Utilities.descr_notifica_ok, response);
	}
	
	@Test
	public void ok_unisomm() throws Exception {
		HttpResponse response = Utilities._test(TipoServizio.EROGAZIONE,HttpConstants.CONTENT_TYPE_JSON, FileSystemUtilities.readBytesFromFile("/etc/govway/test_trasformazioni/richiesteJson/unisomm.json"),
				"TrasformazioneRest2SoapUnisomm","apincn/unisomm/json-comunicazione-obbligatoria", "Unisomm", "ok_unisomm.xml", HttpConstants.CONTENT_TYPE_SOAP_1_1, 200,
				null);
		Utilities.verificaJsonOk(Utilities.tipo_ok, Utilities.descr_notifica_ok, response);
	}
	
	
	@Test
	public void ok_vardatori() throws Exception {
		HttpResponse response = Utilities._test(TipoServizio.EROGAZIONE,HttpConstants.CONTENT_TYPE_JSON, FileSystemUtilities.readBytesFromFile("/etc/govway/test_trasformazioni/richiesteJson/vardatori.json"),
				"TrasformazioneRest2SoapVardatori","apincn/vardatori/json-comunicazione-obbligatoria", "Vardatori", "ok_vardatori.xml", HttpConstants.CONTENT_TYPE_SOAP_1_1, 200,
				null);
		Utilities.verificaJsonOk(Utilities.tipo_ok, Utilities.descr_notifica_ok, response);
	}
	
}
