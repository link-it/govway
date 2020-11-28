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

package org.openspcoop2.pdd.logger.filetrace;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.core.transazioni.constants.TipoAPI;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.core.transazioni.utils.CredenzialiMittente;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.dump.Messaggio;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.resources.MapReader;
import org.slf4j.Logger;

/**     
 * Test
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Test {

	public static void main(String [] args) throws Exception{
		
		boolean log4j = true;
		boolean requestWithPayload = true;
		
		test(TipoPdD.APPLICATIVA, !log4j, 0, requestWithPayload);
		test(TipoPdD.APPLICATIVA, !log4j, 16, requestWithPayload); // errore autenticazione
		
		test(TipoPdD.DELEGATA, !log4j, 0, requestWithPayload);
		test(TipoPdD.DELEGATA, !log4j, 16, requestWithPayload); // errore autenticazione
		
		test(TipoPdD.APPLICATIVA, log4j, 0, requestWithPayload);
		test(TipoPdD.APPLICATIVA, log4j, 16, requestWithPayload); // errore autenticazione
		
		test(TipoPdD.DELEGATA, log4j, 0, requestWithPayload);
		test(TipoPdD.DELEGATA, log4j, 16, requestWithPayload); // errore autenticazione
		
		test(TipoPdD.APPLICATIVA, !log4j, 0, !requestWithPayload);
		test(TipoPdD.APPLICATIVA, !log4j, 16, !requestWithPayload); // errore autenticazione
		
		test(TipoPdD.DELEGATA, !log4j, 0, !requestWithPayload);
		test(TipoPdD.DELEGATA, !log4j, 16, !requestWithPayload); // errore autenticazione
		
		test(TipoPdD.APPLICATIVA, log4j, 0, !requestWithPayload);
		test(TipoPdD.APPLICATIVA, log4j, 16, !requestWithPayload); // errore autenticazione
		
		test(TipoPdD.DELEGATA, log4j, 0, !requestWithPayload);
		test(TipoPdD.DELEGATA, log4j, 16, !requestWithPayload); // errore autenticazione
		
	}
	
	public static void test(TipoPdD tipoPdD, boolean log4j, int esito, boolean requestWithPayload) throws Exception{
		
		Logger log = LoggerWrapperFactory.getLogger(Test.class);
		ConfigurazionePdD confPdD = new ConfigurazionePdD();
		confPdD.setLoader(new Loader());
		confPdD.setLog(log);
		Map<String, IProtocolFactory<?>> m = new HashMap<String, IProtocolFactory<?>>();
		m.put("trasparente", Utilities.newInstance("org.openspcoop2.protocol.trasparente.TrasparenteFactory"));
		MapReader<String, IProtocolFactory<?>> map = new MapReader<String, IProtocolFactory<?>>(m, false);
		EsitiProperties.initialize(null, log, new Loader(), map);
		
		String testData = "2020-06-25_15:09:05.825";
		Date dataIngressoRichiesta = DateUtils.getSimpleDateFormatMs().parse(testData);
		Date dataUscitaRichiesta = new Date(dataIngressoRichiesta.getTime()+100);
		Date dataIngressoRisposta = new Date(dataIngressoRichiesta.getTime()+1200);
		Date dataUscitaRisposta = new Date(dataIngressoRichiesta.getTime()+65);
		
		Transazione transazioneDTO = new Transazione();
		transazioneDTO.setProtocollo("trasparente");
		transazioneDTO.setEsito(esito);
		transazioneDTO.setIdTransazione("UUIDXX");
		transazioneDTO.setIdCorrelazioneApplicativa("XX-deXXX");
		transazioneDTO.setIdCorrelazioneApplicativaRisposta("RR-deXXXRest");
		
		transazioneDTO.setDataIngressoRichiesta(dataIngressoRichiesta);
		transazioneDTO.setDataUscitaRichiesta(dataUscitaRichiesta);
		transazioneDTO.setDataIngressoRisposta(dataIngressoRisposta);
		transazioneDTO.setDataUscitaRisposta(dataUscitaRisposta);
		
		transazioneDTO.setSocketClientAddress("127.0.0.1");
		transazioneDTO.setTransportClientAddress("10.113.13.122");
		if(requestWithPayload) {
			transazioneDTO.setTipoRichiesta("PUT");
		}
		else {
			transazioneDTO.setTipoRichiesta("GET");
		}
		String function = null;
		if(TipoPdD.APPLICATIVA.equals(tipoPdD)) {
			transazioneDTO.setPddRuolo(PddRuolo.APPLICATIVA);
			transazioneDTO.setTipoApi(TipoAPI.REST.getValoreAsInt());
			function = "in";
		}
		else {
			transazioneDTO.setPddRuolo(PddRuolo.DELEGATA);
			transazioneDTO.setTipoApi(TipoAPI.SOAP.getValoreAsInt());
			function = "out";
		}
		transazioneDTO.setUrlInvocazione("["+function+"] /govway/in/EnteEsempio/AAASOAPS1/v1/a1?dklejde=ded&adds=deded");
		transazioneDTO.setLocationConnettore("[DELETE] http://127.0.0.1:8080/govwayAPIConfig/api/PetStorec0c4269e2ebe413dadb79071/1?dad=ddede&adadad=dede");
		transazioneDTO.setClusterId("hostC1");
		transazioneDTO.setVersioneServizio(1);
		transazioneDTO.setNomeServizio("APIEsempio");
		transazioneDTO.setAzione("azioneDiProva");
		transazioneDTO.setNomeSoggettoErogatore("EnteErogatore");
		transazioneDTO.setNomeSoggettoFruitore("EnteFruitore");
		transazioneDTO.setIdMessaggioRichiesta("idMsgReqXXX");
		transazioneDTO.setServizioApplicativoFruitore("AppXde23");
		transazioneDTO.setCodiceRispostaIngresso("202");
		transazioneDTO.setCodiceRispostaUscita("204");
		transazioneDTO.setCredenziali("C=IT, O=Prova");
			
		transazioneDTO.setTokenInfo("ADEDADEAD.DEADADEADAD.dEADEADADEA");
		
		CredenzialiMittente credenzialiMittente = new CredenzialiMittente();

		CredenzialeMittente token_issuer = new CredenzialeMittente();
		token_issuer.setCredenziale("issuerGoogle");
		credenzialiMittente.setToken_issuer(token_issuer);
		
		CredenzialeMittente token_subject = new CredenzialeMittente();
		token_subject.setCredenziale("subjectAD5432h43242");
		credenzialiMittente.setToken_subject(token_subject);
		
		CredenzialeMittente token_clientId = new CredenzialeMittente();
		token_clientId.setCredenziale("3456ClientId");
		credenzialiMittente.setToken_clientId(token_clientId);
		
		CredenzialeMittente token_mail = new CredenzialeMittente();
		token_mail.setCredenziale("info@link.it");
		credenzialiMittente.setToken_eMail(token_mail);
		
		CredenzialeMittente token_username = new CredenzialeMittente();
		token_username.setCredenziale("rossi");
		credenzialiMittente.setToken_username(token_username);
		
		CredenzialeMittente trasporto = new CredenzialeMittente();
		trasporto.setCredenziale("C=IT, O=Prova");
		credenzialiMittente.setTrasporto(trasporto);
		
		Traccia tracciaRichiesta = new Traccia();
		tracciaRichiesta.setBusta(new Busta("trasparente"));
		tracciaRichiesta.getBusta().addProperty("ProprietaTest", "Andrea");
		
		Messaggio richiestaIngresso = new Messaggio();
		richiestaIngresso.setTipoMessaggio(TipoMessaggio.RICHIESTA_INGRESSO_DUMP_BINARIO);
		if(requestWithPayload) {
			richiestaIngresso.setContentType("text/xml; charset=\"UTF8\"");
			richiestaIngresso.setBody("<prova>TEST RICHIESTA_INGRESSO_DUMP_BINARIO</prova>".getBytes());
			richiestaIngresso.getHeaders().put("Content-Type", "text/xml; charset=\"UTF8\"; tipo=inRequest");
		}
		richiestaIngresso.getHeaders().put("Content-XXX", "ADEDE");
		richiestaIngresso.getHeaders().put("TipoMessaggio", "RICHIESTA_INGRESSO_DUMP_BINARIO");
		
		Messaggio richiestaUscita = new Messaggio();
		richiestaUscita.setTipoMessaggio(TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO);
		if(requestWithPayload) {
			richiestaUscita.setContentType("text/xml; charset=\"UTF8\"");
			richiestaUscita.setBody("<prova>TEST RICHIESTA_USCITA_DUMP_BINARIO</prova>".getBytes());
			richiestaUscita.getHeaders().put("Content-Type", "text/xml; charset=\"UTF8\"; tipo=outRequest");
		}
		richiestaUscita.getHeaders().put("Content-XXX", "ADEDE");
		richiestaUscita.getHeaders().put("TipoMessaggio", "RICHIESTA_USCITA_DUMP_BINARIO");
		
		Messaggio rispostaIngresso = new Messaggio();
		rispostaIngresso.setTipoMessaggio(TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO);
		rispostaIngresso.setContentType("text/xml; charset=\"UTF8\"");
		rispostaIngresso.setBody("<prova>TEST RISPOSTA_INGRESSO_DUMP_BINARIO</prova>".getBytes());
		rispostaIngresso.getHeaders().put("Content-Type", "text/xml; charset=\"UTF8\"; tipo=inResponse");
		rispostaIngresso.getHeaders().put("Content-XXX", "ADEDE");
		rispostaIngresso.getHeaders().put("TipoMessaggio", "RISPOSTA_INGRESSO_DUMP_BINARIO");
		
		Messaggio rispostaUscita = new Messaggio();
		rispostaUscita.setTipoMessaggio(TipoMessaggio.RISPOSTA_USCITA_DUMP_BINARIO);
		rispostaUscita.setContentType("text/xml; charset=\"UTF8\"");
		rispostaUscita.setBody("<prova>TEST RISPOSTA_USCITA_DUMP_BINARIO</prova>".getBytes());
		rispostaUscita.getHeaders().put("Content-Type", "text/xml; charset=\"UTF8\"; tipo=outResponse");
		rispostaUscita.getHeaders().put("Content-XXX", "ADEDE");
		rispostaUscita.getHeaders().put("TipoMessaggio", "RISPOSTA_USCITA_DUMP_BINARIO");
		
		System.setProperty("javaProperty.1", "p1");
		System.setProperty("javaProperty.2", "p2");
		
//		Properties pTest = new Properties();
//		pTest.put("format.escape.\"", "\\\"");
//		pTest.put("format.escape.=", "\\=");
//		pTest.put("format.headers.separator","|");
//		pTest.put("format.headers.header.prefix","\"");
//		pTest.put("format.headers.header.suffix","\"");
//		pTest.put("format.headers.header.separator","=");
//		
//		pTest.put("format.property.1.campiLiberi","versione_api = ${log:versioneServizio}; api = ${log:nomeServizio}; operazione = ${log:idOperazione}; erogatore = ${log:nomeSoggettoErogatore}; soggetto_fruitore = ${log:nomeSoggettoFruitore}; applicativo_fruitore = ${log:applicativoFruitore}; id_messaggio = ${log:idMessaggioRichiesta}; id_collaborazione = ${log:idCollaborazione}; esito = ${log:esito};");
//		pTest.put("format.property.2.campiCustom","\"X-WT-IP-APP-SERVER=${log:javaProperty(jboss.server.ip)}\"|\"X-WT-HOSTNAME-APP-SERVER=${log:javaProperty(jboss.server.name)}\"|\"X-WT-SERVER-ENCODING=${log:javaProperty(file.encoding)}\"|\"X-WT-APP-SERVER-PORT=8445\"|\"X-WT-CAMPI-LIBERI=${log:property(campiLiberi)}\"");
//		pTest.put("format.property.3.campiCustomBody","X-WT-IP-APP-SERVER: ${log:javaProperty(jboss.server.ip)}\nX-WT-HOSTNAME-APP-SERVER: ${log:javaProperty(jboss.server.name)}\nX-WT-SERVER-ENCODING: ${log:javaProperty(file.encoding)}\nX-WT-APP-SERVER-PORT: 8445\nX-WT-CAMPI-LIBERI: ${log:property(campiLiberi)}");
//		pTest.put("format.property.4.httpStatusErogazioni","HTTP/1.1 ${log:httpStatusRispostaUscita} ${log:httpReasonRispostaUscita}");
//		pTest.put("format.property.5.httpStatusFruizioni","HTTP/1.1 ${log:httpStatusRispostaIngresso} ${log:httpReasonRispostaIngresso}");
//		pTest.put("format.property.6.headersRispostaErogazioni","${log:property(httpStatusErogazioni)}\n${log:rispostaUscitaHeaders(\n,: ,,)}\n${log:property(campiCustomBody)}");
//		pTest.put("format.property.7.headersRispostaFruizioni","${log:property(httpStatusFruizioni)}\n${log:rispostaIngressoHeaders(\n,: ,,)}\n${log:property(campiCustomBody)}");
//		
//		File fTmp = File.createTempFile("test", ".properties");
//		try(FileOutputStream fout = new FileOutputStream(fTmp)){
//			pTest.store(fout, "test");
//		}
//		System.out.println("\n\n\nFile prodotto:\n"+FileSystemUtilities.readFile(fTmp)+"\n\n\n");
//		//LogTraceConfig config = LogTraceConfig.getConfig(fTmp);
//		fTmp.delete();
		
		InputStream is = Test.class.getResourceAsStream("/org/openspcoop2/pdd/logger/filetrace/testFileTrace.properties");
		FileTraceConfig.init(is);
		FileTraceConfig config = FileTraceConfig.getConfig(new File("TEST")); // inizializzato sopra
			
		
		test(tipoPdD, log4j, requestWithPayload,
				log, config, credenzialiMittente, transazioneDTO, 
				tracciaRichiesta, richiestaIngresso, richiestaUscita, rispostaIngresso, rispostaUscita);
		
	}
	
	// *** TOPIC 'request' ***
	private static final String logRequestPA_PUT = "\"in\"|\"rest\"|\"erogazione\"|\"esempioCostanteRichiestaErogazione\"|\"UUIDXX\"|\"XX-deXXXRR-deXXXRest\"|\"p1\"|\"p2\"|\"2020-06-25 13:09:05:825\"|\"+0200\"|\"127.0.0.1\"|\"10.113.13.122\"|\"HTTP/1.1\"|\"PUT\"|\"C=IT, O=Prova\"|\"C=IT, O=Prova\"|\"issuerGoogle\"|\"subjectAD5432h43242\"|\"3456ClientId\"|\"rossi\"|\"info@link.it\"|\"https://prova:8443/govway/in/EnteEsempio/AAASOAPS1/v1/a1?dklejde=ded&adds=deded\"|\"text/xml; charset=\\\"UTF8\\\"\"|\"51\"|\"HEADERS\"|\"TipoMessaggio=RICHIESTA_INGRESSO_DUMP_BINARIO\"|\"Content-XXX=ADEDE\"|\"Content-Type=text/xml; charset=\\\"UTF8\\\"; tipo=inRequest\"";
	private static final String logRequestPA_GET = "\"in\"|\"rest\"|\"erogazione\"|\"esempioCostanteRichiestaErogazione\"|\"UUIDXX\"|\"XX-deXXXRR-deXXXRest\"|\"p1\"|\"p2\"|\"2020-06-25 13:09:05:825\"|\"+0200\"|\"127.0.0.1\"|\"10.113.13.122\"|\"HTTP/1.1\"|\"GET\"|\"C=IT, O=Prova\"|\"C=IT, O=Prova\"|\"issuerGoogle\"|\"subjectAD5432h43242\"|\"3456ClientId\"|\"rossi\"|\"info@link.it\"|\"https://prova:8443/govway/in/EnteEsempio/AAASOAPS1/v1/a1?dklejde=ded&adds=deded\"|\"\"|\"0\"|\"HEADERS\"|\"TipoMessaggio=RICHIESTA_INGRESSO_DUMP_BINARIO\"|\"Content-XXX=ADEDE\"";
	private static final String logRequestPD_PUT = "\"out\"|\"soap\"|\"fruizione\"|\"esempioCostanteRichiestaFruizione\"|\"UUIDXX\"|\"XX-deXXXRR-deXXXRest\"|\"p1\"|\"p2\"|\"2020-06-25 13:09:05:925\"|\"+0200\"|\"127.0.0.1\"|\"10.113.13.122\"|\"HTTP/1.1\"|\"PUT\"|\"C=IT, O=Prova\"|\"C=IT, O=Prova\"|\"issuerGoogle\"|\"subjectAD5432h43242\"|\"3456ClientId\"|\"rossi\"|\"info@link.it\"|\"http://127.0.0.1:8080/govwayAPIConfig/api/PetStorec0c4269e2ebe413dadb79071/1?dad=ddede&adadad=dede\"|\"text/xml; charset=\\\"UTF8\\\"\"|\"49\"|\"HEADERS\"|\"TipoMessaggio=RICHIESTA_USCITA_DUMP_BINARIO\"|\"Content-XXX=ADEDE\"|\"Content-Type=text/xml; charset=\\\"UTF8\\\"; tipo=outRequest\"";
	private static final String logRequestPD_GET = "\"out\"|\"soap\"|\"fruizione\"|\"esempioCostanteRichiestaFruizione\"|\"UUIDXX\"|\"XX-deXXXRR-deXXXRest\"|\"p1\"|\"p2\"|\"2020-06-25 13:09:05:925\"|\"+0200\"|\"127.0.0.1\"|\"10.113.13.122\"|\"HTTP/1.1\"|\"GET\"|\"C=IT, O=Prova\"|\"C=IT, O=Prova\"|\"issuerGoogle\"|\"subjectAD5432h43242\"|\"3456ClientId\"|\"rossi\"|\"info@link.it\"|\"http://127.0.0.1:8080/govwayAPIConfig/api/PetStorec0c4269e2ebe413dadb79071/1?dad=ddede&adadad=dede\"|\"\"|\"0\"|\"HEADERS\"|\"TipoMessaggio=RICHIESTA_USCITA_DUMP_BINARIO\"|\"Content-XXX=ADEDE\"";	
	
	// *** TOPIC 'requestBody' ***
	private static final String logRequestBodyPA = "UUIDXX.XX-deXXXRR-deXXXRest.MjAyMC0wNi0yNVQxNTowOTowNS44MjUrMDIwMA==.dGV4dC94bWw7IGNoYXJzZXQ9IlVURjgi.PHByb3ZhPlRFU1QgUklDSElFU1RBX0lOR1JFU1NPX0RVTVBfQklOQVJJTzwvcHJvdmE+";
	private static final String logRequestBodyPD = "UUIDXX.XX-deXXXRR-deXXXRest.MjAyMC0wNi0yNVQxNTowOTowNS45MjUrMDIwMA==.dGV4dC94bWw7IGNoYXJzZXQ9IlVURjgi.PHByb3ZhPlRFU1QgUklDSElFU1RBX1VTQ0lUQV9EVU1QX0JJTkFSSU88L3Byb3ZhPg==";
	
	// *** TOPIC 'response' ***
	private static final String logResponsePA = "\"esempioCostanteRispostaErogazione\"|\"ADEDADEAD.DEADADEADAD.dEADEADADEA\"|\"UUIDXX\"|\"XX-deXXXRR-deXXXRest\"|\"p1\"|\"p2\"|\"2020-06-25 13:09:05:890\"|\"+0200\"|\"127.0.0.1\"|\"10.113.13.122\"|\"HTTP/1.1\"|\"PUT\"|\"https://prova:8443/govway/in/EnteEsempio/AAASOAPS1/v1/a1?dklejde=ded&adds=deded\"|\"204\"|\"65000\"|\"text/xml; charset=\\\"UTF8\\\"\"|\"48\"|\"HEADERS\"|\"TipoMessaggio=RISPOSTA_USCITA_DUMP_BINARIO\"|\"Content-XXX=ADEDE\"|\"Content-Type=text/xml; charset=\\\"UTF8\\\"; tipo=outResponse\"|\"X-GovWay-APP-SERVER=10.114.32.21\"|\"X-GovWay-HOSTNAME-APP-SERVER=prova\"|\"X-GovWay-SERVER-ENCODING=UTF-8\"|\"X-GovWay-APP-SERVER-PORT=8443\"|\"X-GovWay-USER=Andrea\"|\"X-GovWay-COMPLEX=versione_api = 1; api = APIEsempio; operazione = azioneDiProva; erogatore = EnteErogatore; soggetto_fruitore = EnteFruitore; applicativo_fruitore = AppXde23; id_messaggio_richiesta = idMsgReqXXX; id_messaggio_risposta = ; id_collaborazione = ; esito = OK;\"";
	private static final String logResponsePD = "\"esempioCostanteRispostaFruizione\"|\"ADEDADEAD.DEADADEADAD.dEADEADADEA\"|\"UUIDXX\"|\"XX-deXXXRR-deXXXRest\"|\"p1\"|\"p2\"|\"2020-06-25 13:09:07:025\"|\"+0200\"|\"127.0.0.1\"|\"10.113.13.122\"|\"HTTP/1.1\"|\"PUT\"|\"http://127.0.0.1:8080/govwayAPIConfig/api/PetStorec0c4269e2ebe413dadb79071/1?dad=ddede&adadad=dede\"|\"202\"|\"1100000\"|\"text/xml; charset=\\\"UTF8\\\"\"|\"50\"|\"HEADERS\"|\"TipoMessaggio=RISPOSTA_INGRESSO_DUMP_BINARIO\"|\"Content-XXX=ADEDE\"|\"Content-Type=text/xml; charset=\\\"UTF8\\\"; tipo=inResponse\"|\"X-GovWay-APP-SERVER=10.114.32.21\"|\"X-GovWay-HOSTNAME-APP-SERVER=prova\"|\"X-GovWay-SERVER-ENCODING=UTF-8\"|\"X-GovWay-APP-SERVER-PORT=8443\"|\"X-GovWay-USER=Andrea\"|\"X-GovWay-COMPLEX=versione_api = 1; api = APIEsempio; operazione = azioneDiProva; erogatore = EnteErogatore; soggetto_fruitore = EnteFruitore; applicativo_fruitore = AppXde23; id_messaggio_richiesta = idMsgReqXXX; id_messaggio_risposta = ; id_collaborazione = ; esito = OK;\"";
	
	// *** TOPIC 'responseBody' ***
	private static final String logResponseBodyPA = "UUIDXX.XX-deXXXRR-deXXXRest.MjAyMC0wNi0yNVQxNTowOTowNS44OTArMDIwMA==.SFRUUC8xLjEgMjA0IE5vIENvbnRlbnQKVGlwb01lc3NhZ2dpbzogUklTUE9TVEFfVVNDSVRBX0RVTVBfQklOQVJJTwpDb250ZW50LVhYWDogQURFREUKQ29udGVudC1UeXBlOiB0ZXh0L3htbDsgY2hhcnNldD1cIlVURjhcIjsgdGlwbz1vdXRSZXNwb25zZQpYLUdvdldheS1BUFAtU0VSVkVSOiAxMC4xMTQuMzIuMjEKWC1Hb3ZXYXktSE9TVE5BTUUtQVBQLVNFUlZFUjogcHJvdmEKWC1Hb3ZXYXktU0VSVkVSLUVOQ09ESU5HOiBVVEYtOApYLUdvdldheS1BUFAtU0VSVkVSLVBPUlQ6IDg0NDMKWC1Hb3ZXYXktVVNFUjogQW5kcmVhClgtR292V2F5LUNPTVBMRVg6IHZlcnNpb25lX2FwaSA9IDE7IGFwaSA9IEFQSUVzZW1waW87IG9wZXJhemlvbmUgPSBhemlvbmVEaVByb3ZhOyBlcm9nYXRvcmUgPSBFbnRlRXJvZ2F0b3JlOyBzb2dnZXR0b19mcnVpdG9yZSA9IEVudGVGcnVpdG9yZTsgYXBwbGljYXRpdm9fZnJ1aXRvcmUgPSBBcHBYZGUyMzsgaWRfbWVzc2FnZ2lvX3JpY2hpZXN0YSA9IGlkTXNnUmVxWFhYOyBpZF9tZXNzYWdnaW9fcmlzcG9zdGEgPSA7IGlkX2NvbGxhYm9yYXppb25lID0gOyBlc2l0byA9IE9LOw==.PHByb3ZhPlRFU1QgUklTUE9TVEFfVVNDSVRBX0RVTVBfQklOQVJJTzwvcHJvdmE+";
	private static final String logResponseBodyPD = "UUIDXX.XX-deXXXRR-deXXXRest.MjAyMC0wNi0yNVQxNTowOTowNy4wMjUrMDIwMA==.SFRUUC8xLjEgMjAyIEFjY2VwdGVkClRpcG9NZXNzYWdnaW86IFJJU1BPU1RBX0lOR1JFU1NPX0RVTVBfQklOQVJJTwpDb250ZW50LVhYWDogQURFREUKQ29udGVudC1UeXBlOiB0ZXh0L3htbDsgY2hhcnNldD1cIlVURjhcIjsgdGlwbz1pblJlc3BvbnNlClgtR292V2F5LUFQUC1TRVJWRVI6IDEwLjExNC4zMi4yMQpYLUdvdldheS1IT1NUTkFNRS1BUFAtU0VSVkVSOiBwcm92YQpYLUdvdldheS1TRVJWRVItRU5DT0RJTkc6IFVURi04ClgtR292V2F5LUFQUC1TRVJWRVItUE9SVDogODQ0MwpYLUdvdldheS1VU0VSOiBBbmRyZWEKWC1Hb3ZXYXktQ09NUExFWDogdmVyc2lvbmVfYXBpID0gMTsgYXBpID0gQVBJRXNlbXBpbzsgb3BlcmF6aW9uZSA9IGF6aW9uZURpUHJvdmE7IGVyb2dhdG9yZSA9IEVudGVFcm9nYXRvcmU7IHNvZ2dldHRvX2ZydWl0b3JlID0gRW50ZUZydWl0b3JlOyBhcHBsaWNhdGl2b19mcnVpdG9yZSA9IEFwcFhkZTIzOyBpZF9tZXNzYWdnaW9fcmljaGllc3RhID0gaWRNc2dSZXFYWFg7IGlkX21lc3NhZ2dpb19yaXNwb3N0YSA9IDsgaWRfY29sbGFib3JhemlvbmUgPSA7IGVzaXRvID0gT0s7.PHByb3ZhPlRFU1QgUklTUE9TVEFfSU5HUkVTU09fRFVNUF9CSU5BUklPPC9wcm92YT4=";

	
	private static void test(TipoPdD tipoPdD, boolean log4j, boolean requestWithPayload,
			Logger log, FileTraceConfig config, 
			CredenzialiMittente credenzialiMittente,
			Transazione transazioneDTO, Traccia tracciaRichiesta, 
			Messaggio richiestaIngresso, Messaggio richiestaUscita,
			Messaggio rispostaIngresso, Messaggio rispostaUscita) throws Exception {
		
		System.out.println("\n\n ---------------------------- ("+tipoPdD+") (esito:"+transazioneDTO.getEsito()+") (httpMethod:"+transazioneDTO.getTipoRichiesta()+") -----------------------------");
		
		boolean erogazioni = TipoPdD.APPLICATIVA.equals(tipoPdD);
		boolean onlyLogFileTrace = TipoPdD.APPLICATIVA.equals(tipoPdD);
		Transaction transaction = new Transaction("UUIDXX", "FileTraceTest", false);
		transaction.setCredenzialiMittente(credenzialiMittente);
		transaction.setTracciaRichiesta(tracciaRichiesta);
		transaction.addMessaggio(richiestaIngresso, onlyLogFileTrace);
		transaction.addMessaggio(richiestaUscita, onlyLogFileTrace);
		transaction.addMessaggio(rispostaIngresso, onlyLogFileTrace);
		transaction.addMessaggio(rispostaUscita, onlyLogFileTrace);
		
		System.out.println("Messaggi presenti prima: "+transaction.sizeMessaggi());
		
		FileTraceManager manager = new FileTraceManager(log, config);
		manager.buildTransazioneInfo(transazioneDTO, transaction);
		int sizeAfter = transaction.sizeMessaggi();
		System.out.println("Messaggi presenti dopo: "+sizeAfter);
		if(onlyLogFileTrace) {
			if(sizeAfter!=0) {
				throw new Exception("Attesi 0 messaggi");
			}
		}
		else {
			if(sizeAfter!=4) {
				throw new Exception("Attesi 4 messaggi");
			}
		}
		
		boolean requestSended = true;
		if(transazioneDTO.getEsito()!=0) {
			requestSended = false;
		}
		
		PdDContext context = new PdDContext();
		if(requestSended) {
			context.addObject(Costanti.RICHIESTA_INOLTRATA_BACKEND, Costanti.RICHIESTA_INOLTRATA_BACKEND_VALORE);
		}
		
		if(log4j) {
			
			manager.invoke(tipoPdD, context);
		}
		else {
		
			Map<String, String> outputMap = new HashMap<String, String>();
			manager.invoke(tipoPdD, context, outputMap);
			
			int res = outputMap.size();
			System.out.println("Terminato con "+res+" risultati");
			int risultatiAttesi = 4;
			if(!erogazioni && !requestSended) {
				risultatiAttesi = 0;
			}
			else if(!requestWithPayload) {
				risultatiAttesi = 3;
			}
			if(res!=risultatiAttesi) {
				throw new Exception("Attesi "+risultatiAttesi+" risultati");
			}
			
			if(outputMap.size()>0) {
				List<String> l = new ArrayList<String>();
				for (String topic : outputMap.keySet()) {
					l.add(topic);
				}
				Collections.sort(l);
				for (String topic : l) {
					System.out.println("\n\n *** TOPIC '"+topic+"' ***");
					String logMsg = outputMap.get(topic);
					System.out.println(logMsg);
					
					if(erogazioni) {
						if("request".equals(topic)) {
							if(requestWithPayload) {
								if(!logMsg.equals(logRequestPA_PUT)) {
									throw new Exception("FAILED!! \nAtteso:\n"+logRequestPA_PUT+"\nTrovato:\n"+logMsg);
								}
							}
							else {
								if(!logMsg.equals(logRequestPA_GET)) {
									throw new Exception("FAILED!! \nAtteso:\n"+logRequestPA_GET+"\nTrovato:\n"+logMsg);
								}
							}
						}
						else if("requestBody".equals(topic)) {
							if(!logMsg.equals(logRequestBodyPA)) {
								throw new Exception("FAILED!! \nAtteso:\n"+logRequestBodyPA+"\nTrovato:\n"+logMsg);
							}
						}
						else if("response".equals(topic)) {
							String atteso = logResponsePA;
							if(transazioneDTO.getEsito()!=0) {
								atteso = atteso.replace("esito = OK", "esito = ERRORE_AUTENTICAZIONE");
							}
							if(!requestWithPayload) {
								atteso = atteso.replace("PUT", "GET");
							}
							if(!logMsg.equals(atteso)) {
								throw new Exception("FAILED!! \nAtteso:\n"+atteso+"\nTrovato:\n"+logMsg);
							}
						}
						else if("responseBody".equals(topic)) {
							String atteso = logResponseBodyPA;
							if(transazioneDTO.getEsito()!=0) {
								atteso = atteso.replace("9yYXppb25lID0gOyBlc2l0byA9IE9LOw==", "9yYXppb25lID0gOyBlc2l0byA9IEVSUk9SRV9BVVRFTlRJQ0FaSU9ORTs=");
							}
							if(!logMsg.equals(atteso)) {
								throw new Exception("FAILED!! \nAtteso:\n"+atteso+"\nTrovato:\n"+logMsg);
							}
						}
						else {
							throw new Exception("FAILED!! topic sconosciuto");
						}
					}
					else {
						if("request".equals(topic)) {
							if(requestWithPayload) {
								if(!logMsg.equals(logRequestPD_PUT)) {
									throw new Exception("FAILED!! \nAtteso:\n"+logRequestPD_PUT+"\nTrovato:\n"+logMsg);
								}
							}
							else {
								if(!logMsg.equals(logRequestPD_GET)) {
									throw new Exception("FAILED!! \nAtteso:\n"+logRequestPD_GET+"\nTrovato:\n"+logMsg);
								}
							}
						}
						else if("requestBody".equals(topic)) {
							if(!logMsg.equals(logRequestBodyPD)) {
								throw new Exception("FAILED!! \nAtteso:\n"+logRequestBodyPD+"\nTrovato:\n"+logMsg);
							}
						}
						else if("response".equals(topic)) {
							String atteso = logResponsePD;
							if(!requestWithPayload) {
								atteso = atteso.replace("PUT", "GET");
							}
							if(!logMsg.equals(atteso)) {
								throw new Exception("FAILED!! \nAtteso:\n"+atteso+"\nTrovato:\n"+logMsg);
							}
						}
						else if("responseBody".equals(topic)) {
							if(!logMsg.equals(logResponseBodyPD)) {
								throw new Exception("FAILED!! \nAtteso:\n"+logResponseBodyPD+"\nTrovato:\n"+logMsg);
							}
						}
						else {
							throw new Exception("FAILED!! topic sconosciuto");
						}
					}
				}
			}
		}
	}
}
