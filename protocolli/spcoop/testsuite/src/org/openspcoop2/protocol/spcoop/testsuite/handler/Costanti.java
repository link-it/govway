/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


package org.openspcoop2.protocol.spcoop.testsuite.handler;

/**
 * Libreria per handler testsuite
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {

	public static final String SOAP_ACTION_TEST_HANDLER_QUOTED = "\"http://www.openspcoop2.org/test-handler\"";
	
	public static final String TEST_CONTEXT_GENERA_ERRORE = "TestContext_errore";
	
	public static final String TEST_CONTEXT = "TestContext";
	
	public static final String TEST_CONTEXT_PREFISSO_ERRORE = "[TESTSUITE-HANDLER-ERROR]";
	
	
	public static final String TEST_CONTEXT_ESITO = "TestContext_esito";
	
	public static final String TEST_CONTEXT_DATA_INIZIO_TEST = "TestContext_data_inizio_test";
	
	public static final String TEST_CONTEXT_RISPOSTA_VUOTA_SA_PD = "TestContext_risposta_vuota_sa_pd";
	public static final String TEST_CONTEXT_RISPOSTA_VUOTA_PD_PA = "TestContext_risposta_vuota_pd_pa";
	public static final String TEST_CONTEXT_RISPOSTA_VUOTA_PA_SA = "TestContext_risposta_vuota_pa_sa";
	public static final String TEST_CONTEXT_RISPOSTA_VUOTA_PA_PD = "TestContext_risposta_vuota_pa_pd";
		
	public static final String TEST_CONTEXT_DELEGATA_REQUEST_RETURN_CODE = "TestContext_delegata_req_returnCode";
	public static final String TEST_CONTEXT_DELEGATA_RESPONSE_RETURN_CODE = "TestContext_delegata_res_returnCode";
	public static final String TEST_CONTEXT_APPLICATIVA_REQUEST_RETURN_CODE = "TestContext_applicativa_req_returnCode";
	public static final String TEST_CONTEXT_APPLICATIVA_RESPONSE_RETURN_CODE = "TestContext_applicativa_res_returnCode";
	
	public static final String TEST_CONTEXT_DELEGATA_LOCATION = "TestContext_delegata_location";
	public static final String TEST_CONTEXT_APPLICATIVA_LOCATION = "TestContext_applicativa_location";
	
	public static final String TEST_CONTEXT_DELEGATA_TIPO_CONNETTORE = "TestContext_delegata_tipoConnettore";
	public static final String TEST_CONTEXT_APPLICATIVA_TIPO_CONNETTORE = "TestContext_applicativa_tipoConnettore";
	
	// egov
	public static final String TEST_CONTEXT_EGOV = "TestContext_egov";
	public static final String TEST_CONTEXT_EGOV_TIPO_MITTENTE = "TestContext_egov_tipoMittente";
	public static final String TEST_CONTEXT_EGOV_MITTENTE = "TestContext_egov_mittente";
	public static final String TEST_CONTEXT_EGOV_TIPO_DESTINATARIO = "TestContext_egov_tipoDestinatario";
	public static final String TEST_CONTEXT_EGOV_DESTINATARIO = "TestContext_egov_destinatario";
	public static final String TEST_CONTEXT_EGOV_ID_RICHIESTA = "TestContext_egov_id_richiesta";
	public static final String TEST_CONTEXT_EGOV_ID_RISPOSTA = "TestContext_egov_id_risposta";
	public static final String TEST_CONTEXT_EGOV_ID_VERIFICA_BASE_MITTENTE_DESTINATARIO = "_VERIFICA_ID_";
	public static final String TEST_CONTEXT_EGOV_ID_VERIFICA_NULL = "_VERIFICA_ID_NULL_";
	public static final String TEST_CONTEXT_EGOV_PROFILO_COLLABORAZIONE = "TestContext_egov_profiloCollaborazione";
	public static final String TEST_CONTEXT_EGOV_COLLABORAZIONE = "TestContext_egov_collaborazione";
	public static final String TEST_CONTEXT_EGOV_COLLABORAZIONE_VERIFICA_NULL = "_COLLABORAZIONE_NULL_";
	public static final String TEST_CONTEXT_EGOV_SCENARIO = "TestContext_egov_scenario";
	public static final String TEST_CONTEXT_EGOV_TIPO_SERVIZIO = "TestContext_egov_tipoServizio";
	public static final String TEST_CONTEXT_EGOV_SERVIZIO = "TestContext_egov_servizio";
	public static final String TEST_CONTEXT_EGOV_AZIONE = "TestContext_egov_azione";
	public static final String TEST_CONTEXT_EGOV_AZIONE_VERIFICA_NULL = "_AZIONE_NULL_";
	
	// integration
	public static final String TEST_CONTEXT_CORRELAZIONE_APPLICATIVA_PD_REQ = "TestContext_correlazioneApplicativa_pd_req";
	public static final String TEST_CONTEXT_CORRELAZIONE_APPLICATIVA_PA_REQ = "TestContext_correlazioneApplicativa_pa_req";
	public static final String TEST_CONTEXT_SERVIZIO_APPLICATIVO_FRUITORE = "TestContext_servizioApplicativoFruitore";
	public static final String TEST_CONTEXT_SERVIZIO_APPLICATIVO_FRUITORE_VALUE_ANONIMO = "_SA_FRUITORE_ANONIMO_";
	public static final String TEST_CONTEXT_SERVIZIO_APPLICATIVO_EROGATORE = "TestContext_servizioApplicativoErogatore";
	public static final String TEST_CONTEXT_STATELESS_PD = "TestContext_stateless_pd";
	public static final String TEST_CONTEXT_STATELESS_PA = "TestContext_stateless_pa";
}
