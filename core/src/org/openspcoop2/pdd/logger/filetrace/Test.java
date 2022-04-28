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
import org.openspcoop2.pdd.core.token.InformazioniJWTClientAssertion;
import org.openspcoop2.pdd.core.token.InformazioniNegoziazioneToken;
import org.openspcoop2.pdd.core.token.InformazioniNegoziazioneToken_DatiRichiesta;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.token.SorgenteInformazioniToken;
import org.openspcoop2.pdd.core.token.attribute_authority.InformazioniAttributi;
import org.openspcoop2.pdd.core.token.parser.BasicNegoziazioneTokenParser;
import org.openspcoop2.pdd.core.token.parser.BasicTokenParser;
import org.openspcoop2.pdd.core.token.parser.TipologiaClaims;
import org.openspcoop2.pdd.core.token.parser.TipologiaClaimsNegoziazione;
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
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.resources.MapReader;
import org.openspcoop2.utils.transport.TransportUtils;
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
	
	private static String hdr = "ewogICJhbGciOiAiUlMyNTYiLAogICJ0eXAiOiAiSldUIiwKICAia2lkIjogInByb3ZhIiwKICAieDVjIjogWwogICAgIk1JSS4uLi4uZjZ1UXhjbGhVaDBOaXZqNmhJSitDZDNSMS9GdncxejY5RXllT3ROd3FZU2JzdzJnamlkbzhHUGdEVWtxZFVaSThyYnRjbDIyK2x0S2VXbURhUXZOUUZnTlUreUJObTBBPSIKICBdCn0=";
	private static String payload = "ewogICJpYXQiOiAxNjUwMDMyOTAzLAogICJuYmYiOiAxNjUwMDMyOTAzLAogICJleHAiOiAxNjUwMDMzMDAzLAogICJqdGkiOiAiNGU2MWRjMTQtYmNjOC0xMWVjLTllOTktMDA1MDU2YWUwMzA3IiwKICAiYXVkIjogInRlc3QiLAogICJjbGllbnRfaWQiOiAiY1Rlc3QiLAogICJpc3MiOiAiaXNzVGVzdCIsCiAgInN1YiI6ICJzdWJUZXN0Igp9";
	private static String clientAssertionBase64 = hdr+"."+payload+".PDdXpT5htzB6JI0TdYsfsIBjH8tSV0IkIiKAI0S1IYkqcS6pOs84MsfVk3wnd1_dSiR-2KSpGzZU9s8TuGoXcdR-4oa6EN0RNJJsF8zC1KHVx1IBl4jcZGRY5vAgtKwBC87bPz7EaYXtesS3Go-fl5HTFWvZ4OR3yxvsrCfTy_ehQwVJwJy9yKrIpQFq_dSQr_xQbRBL495D9Fp4p54vNdP3IRtoDq16NUhwkH_dbQJGUJdYZ2M31bBZUvgu9RRZz_ftjI78Swwq5FIwIG7r5trwgmVebZtdLF2Ni5Vc2rL7ZNuBpH7Y_knRgRYbH4HxnMoHOU6nU8yM_ZPZyhHneA";
	
	
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
		
		InformazioniToken informazioniToken = new InformazioniToken(SorgenteInformazioniToken.JWT, new String(Base64Utilities.decode(payload.getBytes())), new BasicTokenParser(TipologiaClaims.INTROSPECTION_RESPONSE_RFC_7662));
		
		InformazioniNegoziazioneToken_DatiRichiesta datiRichiesta = new InformazioniNegoziazioneToken_DatiRichiesta();
		datiRichiesta.setPolicy("TEST");
		datiRichiesta.setTransactionId(transazioneDTO.getIdTransazione());
		datiRichiesta.setClientId("clientIdXX");
		datiRichiesta.setClientToken("clientBEARERTOKEN");
		datiRichiesta.setUsername("USERNAME");
		datiRichiesta.setJwtClientAssertion(new InformazioniJWTClientAssertion(log, clientAssertionBase64, false));
		datiRichiesta.setEndpoint("http://retrieveToken");
		datiRichiesta.setGrantType("rfc7523_x509");
		String accessToken = "AAAAATOKEN";
		String refreshToken = "AAAAAREFESHTOKEN";
		String rawResponseAccessToken = "{\"access_token\":\""+accessToken+"\",\"refresh_token\":\""+refreshToken+"\",\"scope\":\"s1 s2\",\"token_type\":\"JWT\",\"expires_in\":3700,\"refresh_expires_in\":4000}";
		InformazioniNegoziazioneToken informazioniNegoziazioneToken = new InformazioniNegoziazioneToken(datiRichiesta, rawResponseAccessToken, new BasicNegoziazioneTokenParser(TipologiaClaimsNegoziazione.OAUTH2_RFC_6749));
		
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
			richiestaIngresso.setBody(DumpByteArrayOutputStream.newInstance("<prova>TEST RICHIESTA_INGRESSO_DUMP_BINARIO</prova>".getBytes()));
			TransportUtils.addHeader(richiestaIngresso.getHeaders(),"Content-Type", "text/xml; charset=\"UTF8\"; tipo=inRequest");
		}
		TransportUtils.addHeader(richiestaIngresso.getHeaders(),"Content-XXX", "ADEDE");
		TransportUtils.addHeader(richiestaIngresso.getHeaders(),"TipoMessaggio", "RICHIESTA_INGRESSO_DUMP_BINARIO");
		
		Messaggio richiestaUscita = new Messaggio();
		richiestaUscita.setTipoMessaggio(TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO);
		if(requestWithPayload) {
			richiestaUscita.setContentType("text/xml; charset=\"UTF8\"");
			richiestaUscita.setBody(DumpByteArrayOutputStream.newInstance("<prova>TEST RICHIESTA_USCITA_DUMP_BINARIO</prova>".getBytes()));
			TransportUtils.addHeader(richiestaUscita.getHeaders(),"Content-Type", "text/xml; charset=\"UTF8\"; tipo=outRequest");
		}
		TransportUtils.addHeader(richiestaUscita.getHeaders(),"Content-XXX", "ADEDE");
		TransportUtils.addHeader(richiestaUscita.getHeaders(),"TipoMessaggio", "RICHIESTA_USCITA_DUMP_BINARIO");
		
		Messaggio rispostaIngresso = new Messaggio();
		rispostaIngresso.setTipoMessaggio(TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO);
		rispostaIngresso.setContentType("text/xml; charset=\"UTF8\"");
		rispostaIngresso.setBody(DumpByteArrayOutputStream.newInstance("<prova>TEST RISPOSTA_INGRESSO_DUMP_BINARIO</prova>".getBytes()));
		TransportUtils.addHeader(rispostaIngresso.getHeaders(),"Content-Type", "text/xml; charset=\"UTF8\"; tipo=inResponse");
		TransportUtils.addHeader(rispostaIngresso.getHeaders(),"Content-XXX", "ADEDE");
		TransportUtils.addHeader(rispostaIngresso.getHeaders(),"TipoMessaggio", "RISPOSTA_INGRESSO_DUMP_BINARIO");
		
		Messaggio rispostaUscita = new Messaggio();
		rispostaUscita.setTipoMessaggio(TipoMessaggio.RISPOSTA_USCITA_DUMP_BINARIO);
		rispostaUscita.setContentType("text/xml; charset=\"UTF8\"");
		rispostaUscita.setBody(DumpByteArrayOutputStream.newInstance("<prova>TEST RISPOSTA_USCITA_DUMP_BINARIO</prova>".getBytes()));
		TransportUtils.addHeader(rispostaUscita.getHeaders(),"Content-Type", "text/xml; charset=\"UTF8\"; tipo=outResponse");
		TransportUtils.addHeader(rispostaUscita.getHeaders(),"Content-XXX", "ADEDE");
		TransportUtils.addHeader(rispostaUscita.getHeaders(),"TipoMessaggio", "RISPOSTA_USCITA_DUMP_BINARIO");
		
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
		
		String path = "/org/openspcoop2/pdd/logger/filetrace/testFileTrace.properties";
		InputStream is = Test.class.getResourceAsStream(path);
		FileTraceConfig.init(is, path, true);
		FileTraceConfig config = FileTraceConfig.getConfig(new File(path), true); // inizializzato sopra
			
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("a1", "v1");
		List<String> l2 = new ArrayList<String>();
		l2.add("v2a");
		l2.add("v2b");
		attributes.put("a2", l2);
		InformazioniAttributi informazioniAttributi = null;
		if(TipoPdD.APPLICATIVA.equals(tipoPdD)) {
			informazioniAttributi = new InformazioniAttributi();
			informazioniAttributi.setAttributes(attributes);
		}
		else {
			l2.add("v2c");
			InformazioniAttributi informazioniAttributiAA1 = new InformazioniAttributi("AA1");
			informazioniAttributiAA1.setAttributes(attributes);
			InformazioniAttributi informazioniAttributiAA2 = new InformazioniAttributi("AA2");
			informazioniAttributiAA2.setAttributes(attributes);
			informazioniAttributi = new InformazioniAttributi(false, informazioniAttributiAA1,informazioniAttributiAA2);
		}
		
		
		test(tipoPdD, log4j, requestWithPayload,
				log, config, credenzialiMittente, 
				informazioniToken,
				informazioniAttributi,
				informazioniNegoziazioneToken,
				transazioneDTO, 
				tracciaRichiesta, richiestaIngresso, richiestaUscita, rispostaIngresso, rispostaUscita);
		
	}
	
	// *** TOPIC 'request' ***
	private static final String retrievedTokenInfo = "\"AAAAATOKEN\"|\"JWT\"|\"UUIDXX\"|\"rfc7523_x509\"|\""+clientAssertionBase64+"\"|\"rfc7523_x509\"|\"clientIdXX\"|\"clientBEARERTOKEN\"|\"USERNAME\"|\"http://retrieveToken\"";
	private static final String logRequestPA_PUT = "\"in\"|\"rest\"|\"erogazione\"|\"esempioCostanteRichiestaErogazione\"|\"UUIDXX\"|\"XX-deXXXRR-deXXXRest\"|\"p1\"|\"p2\"|\"2020-06-25 13:09:05:825\"|\"+0200\"|\"127.0.0.1\"|\"10.113.13.122\"|\"HTTP/1.1\"|\"PUT\"|\"C=IT, O=Prova\"|\"C=IT, O=Prova\"|\"issuerGoogle\"|\"subjectAD5432h43242\"|\"3456ClientId\"|\"rossi\"|\"info@link.it\"|\"issTest\"|\"1650033003\"|\"https://prova:8443/govway/in/EnteEsempio/AAASOAPS1/v1/a1?dklejde=ded&adds=deded\"|\"text/xml; charset=\\\"UTF8\\\"\"|\"51\"|\"HEADERS\"|\"Content-XXX=ADEDE\"|\"TipoMessaggio=RICHIESTA_INGRESSO_DUMP_BINARIO\"|\"Content-Type=text/xml; charset=\\\"UTF8\\\"; tipo=inRequest\"|\"v1\"|\"v2a,v2b\"|"+retrievedTokenInfo+"";
	private static final String logRequestPA_GET = "\"in\"|\"rest\"|\"erogazione\"|\"esempioCostanteRichiestaErogazione\"|\"UUIDXX\"|\"XX-deXXXRR-deXXXRest\"|\"p1\"|\"p2\"|\"2020-06-25 13:09:05:825\"|\"+0200\"|\"127.0.0.1\"|\"10.113.13.122\"|\"HTTP/1.1\"|\"GET\"|\"C=IT, O=Prova\"|\"C=IT, O=Prova\"|\"issuerGoogle\"|\"subjectAD5432h43242\"|\"3456ClientId\"|\"rossi\"|\"info@link.it\"|\"issTest\"|\"1650033003\"|\"https://prova:8443/govway/in/EnteEsempio/AAASOAPS1/v1/a1?dklejde=ded&adds=deded\"|\"\"|\"0\"|\"HEADERS\"|\"Content-XXX=ADEDE\"|\"TipoMessaggio=RICHIESTA_INGRESSO_DUMP_BINARIO\"|\"v1\"|\"v2a,v2b\"|"+retrievedTokenInfo+"";
	private static final String logRequestPD_PUT = "\"out\"|\"soap\"|\"fruizione\"|\"esempioCostanteRichiestaFruizione\"|\"UUIDXX\"|\"XX-deXXXRR-deXXXRest\"|\"p1\"|\"p2\"|\"2020-06-25 13:09:05:925\"|\"+0200\"|\"127.0.0.1\"|\"10.113.13.122\"|\"HTTP/1.1\"|\"PUT\"|\"C=IT, O=Prova\"|\"C=IT, O=Prova\"|\"issuerGoogle\"|\"subjectAD5432h43242\"|\"3456ClientId\"|\"rossi\"|\"info@link.it\"|\"issTest\"|\"1650033003\"|\"http://127.0.0.1:8080/govwayAPIConfig/api/PetStorec0c4269e2ebe413dadb79071/1?dad=ddede&adadad=dede\"|\"text/xml; charset=\\\"UTF8\\\"\"|\"49\"|\"HEADERS\"|\"Content-XXX=ADEDE\"|\"TipoMessaggio=RICHIESTA_USCITA_DUMP_BINARIO\"|\"Content-Type=text/xml; charset=\\\"UTF8\\\"; tipo=outRequest\"|\"v1\"|\"v2a,v2b,v2c\"|"+retrievedTokenInfo+"";
	private static final String logRequestPD_GET = "\"out\"|\"soap\"|\"fruizione\"|\"esempioCostanteRichiestaFruizione\"|\"UUIDXX\"|\"XX-deXXXRR-deXXXRest\"|\"p1\"|\"p2\"|\"2020-06-25 13:09:05:925\"|\"+0200\"|\"127.0.0.1\"|\"10.113.13.122\"|\"HTTP/1.1\"|\"GET\"|\"C=IT, O=Prova\"|\"C=IT, O=Prova\"|\"issuerGoogle\"|\"subjectAD5432h43242\"|\"3456ClientId\"|\"rossi\"|\"info@link.it\"|\"issTest\"|\"1650033003\"|\"http://127.0.0.1:8080/govwayAPIConfig/api/PetStorec0c4269e2ebe413dadb79071/1?dad=ddede&adadad=dede\"|\"\"|\"0\"|\"HEADERS\"|\"Content-XXX=ADEDE\"|\"TipoMessaggio=RICHIESTA_USCITA_DUMP_BINARIO\"|\"v1\"|\"v2a,v2b,v2c\"|"+retrievedTokenInfo+"";	
	
	// *** TOPIC 'requestBody' ***
	private static final String logRequestBodyPA = "UUIDXX.XX-deXXXRR-deXXXRest.MjAyMC0wNi0yNVQxNTowOTowNS44MjUrMDIwMA==.dGV4dC94bWw7IGNoYXJzZXQ9IlVURjgi.PHByb3ZhPlRFU1QgUklDSElFU1RBX0lOR1JFU1NPX0RVTVBfQklOQVJJTzwvcHJvdmE+";
	private static final String logRequestBodyPD = "UUIDXX.XX-deXXXRR-deXXXRest.MjAyMC0wNi0yNVQxNTowOTowNS45MjUrMDIwMA==.dGV4dC94bWw7IGNoYXJzZXQ9IlVURjgi.PHByb3ZhPlRFU1QgUklDSElFU1RBX1VTQ0lUQV9EVU1QX0JJTkFSSU88L3Byb3ZhPg==";
	
	// *** TOPIC 'response' ***
	private static final String logResponsePA = "\"esempioCostanteRispostaErogazione\"|\"ADEDADEAD.DEADADEADAD.dEADEADADEA\"|\"UUIDXX\"|\"XX-deXXXRR-deXXXRest\"|\"p1\"|\"p2\"|\"2020-06-25 13:09:05:890\"|\"+0200\"|\"127.0.0.1\"|\"10.113.13.122\"|\"HTTP/1.1\"|\"PUT\"|\"https://prova:8443/govway/in/EnteEsempio/AAASOAPS1/v1/a1?dklejde=ded&adds=deded\"|\"204\"|\"65000\"|\"text/xml; charset=\\\"UTF8\\\"\"|\"48\"|\"HEADERS\"|\"Content-XXX=ADEDE\"|\"TipoMessaggio=RISPOSTA_USCITA_DUMP_BINARIO\"|\"Content-Type=text/xml; charset=\\\"UTF8\\\"; tipo=outResponse\"|\"X-GovWay-APP-SERVER=10.114.32.21\"|\"X-GovWay-HOSTNAME-APP-SERVER=prova\"|\"X-GovWay-SERVER-ENCODING=UTF-8\"|\"X-GovWay-APP-SERVER-PORT=8443\"|\"X-GovWay-USER=Andrea\"|\"X-GovWay-COMPLEX=versione_api = 1; api = APIEsempio; operazione = azioneDiProva; erogatore = EnteErogatore; soggetto_fruitore = EnteFruitore; applicativo_fruitore = AppXde23; id_messaggio_richiesta = idMsgReqXXX; id_messaggio_risposta = ; id_collaborazione = ; esito = OK;\"";
	private static final String logResponsePD = "\"esempioCostanteRispostaFruizione\"|\"ADEDADEAD.DEADADEADAD.dEADEADADEA\"|\"UUIDXX\"|\"XX-deXXXRR-deXXXRest\"|\"p1\"|\"p2\"|\"2020-06-25 13:09:07:025\"|\"+0200\"|\"127.0.0.1\"|\"10.113.13.122\"|\"HTTP/1.1\"|\"PUT\"|\"http://127.0.0.1:8080/govwayAPIConfig/api/PetStorec0c4269e2ebe413dadb79071/1?dad=ddede&adadad=dede\"|\"202\"|\"1100000\"|\"text/xml; charset=\\\"UTF8\\\"\"|\"50\"|\"HEADERS\"|\"Content-XXX=ADEDE\"|\"TipoMessaggio=RISPOSTA_INGRESSO_DUMP_BINARIO\"|\"Content-Type=text/xml; charset=\\\"UTF8\\\"; tipo=inResponse\"|\"X-GovWay-APP-SERVER=10.114.32.21\"|\"X-GovWay-HOSTNAME-APP-SERVER=prova\"|\"X-GovWay-SERVER-ENCODING=UTF-8\"|\"X-GovWay-APP-SERVER-PORT=8443\"|\"X-GovWay-USER=Andrea\"|\"X-GovWay-COMPLEX=versione_api = 1; api = APIEsempio; operazione = azioneDiProva; erogatore = EnteErogatore; soggetto_fruitore = EnteFruitore; applicativo_fruitore = AppXde23; id_messaggio_richiesta = idMsgReqXXX; id_messaggio_risposta = ; id_collaborazione = ; esito = OK;\"";
	
	// *** TOPIC 'responseBody' ***
	private static final String logResponseBodyPA = "UUIDXX.XX-deXXXRR-deXXXRest.MjAyMC0wNi0yNVQxNTowOTowNS44OTArMDIwMA==.SFRUUC8xLjEgMjA0IE5vIENvbnRlbnQKQ29udGVudC1YWFg6IEFERURFClRpcG9NZXNzYWdnaW86IFJJU1BPU1RBX1VTQ0lUQV9EVU1QX0JJTkFSSU8KQ29udGVudC1UeXBlOiB0ZXh0L3htbDsgY2hhcnNldD1cIlVURjhcIjsgdGlwbz1vdXRSZXNwb25zZQpYLUdvdldheS1BUFAtU0VSVkVSOiAxMC4xMTQuMzIuMjEKWC1Hb3ZXYXktSE9TVE5BTUUtQVBQLVNFUlZFUjogcHJvdmEKWC1Hb3ZXYXktU0VSVkVSLUVOQ09ESU5HOiBVVEYtOApYLUdvdldheS1BUFAtU0VSVkVSLVBPUlQ6IDg0NDMKWC1Hb3ZXYXktVVNFUjogQW5kcmVhClgtR292V2F5LUNPTVBMRVg6IHZlcnNpb25lX2FwaSA9IDE7IGFwaSA9IEFQSUVzZW1waW87IG9wZXJhemlvbmUgPSBhemlvbmVEaVByb3ZhOyBlcm9nYXRvcmUgPSBFbnRlRXJvZ2F0b3JlOyBzb2dnZXR0b19mcnVpdG9yZSA9IEVudGVGcnVpdG9yZTsgYXBwbGljYXRpdm9fZnJ1aXRvcmUgPSBBcHBYZGUyMzsgaWRfbWVzc2FnZ2lvX3JpY2hpZXN0YSA9IGlkTXNnUmVxWFhYOyBpZF9tZXNzYWdnaW9fcmlzcG9zdGEgPSA7IGlkX2NvbGxhYm9yYXppb25lID0gOyBlc2l0byA9IE9LOw==.PHByb3ZhPlRFU1QgUklTUE9TVEFfVVNDSVRBX0RVTVBfQklOQVJJTzwvcHJvdmE+";
	private static final String logResponseBodyPD = "UUIDXX.XX-deXXXRR-deXXXRest.MjAyMC0wNi0yNVQxNTowOTowNy4wMjUrMDIwMA==.SFRUUC8xLjEgMjAyIEFjY2VwdGVkCkNvbnRlbnQtWFhYOiBBREVERQpUaXBvTWVzc2FnZ2lvOiBSSVNQT1NUQV9JTkdSRVNTT19EVU1QX0JJTkFSSU8KQ29udGVudC1UeXBlOiB0ZXh0L3htbDsgY2hhcnNldD1cIlVURjhcIjsgdGlwbz1pblJlc3BvbnNlClgtR292V2F5LUFQUC1TRVJWRVI6IDEwLjExNC4zMi4yMQpYLUdvdldheS1IT1NUTkFNRS1BUFAtU0VSVkVSOiBwcm92YQpYLUdvdldheS1TRVJWRVItRU5DT0RJTkc6IFVURi04ClgtR292V2F5LUFQUC1TRVJWRVItUE9SVDogODQ0MwpYLUdvdldheS1VU0VSOiBBbmRyZWEKWC1Hb3ZXYXktQ09NUExFWDogdmVyc2lvbmVfYXBpID0gMTsgYXBpID0gQVBJRXNlbXBpbzsgb3BlcmF6aW9uZSA9IGF6aW9uZURpUHJvdmE7IGVyb2dhdG9yZSA9IEVudGVFcm9nYXRvcmU7IHNvZ2dldHRvX2ZydWl0b3JlID0gRW50ZUZydWl0b3JlOyBhcHBsaWNhdGl2b19mcnVpdG9yZSA9IEFwcFhkZTIzOyBpZF9tZXNzYWdnaW9fcmljaGllc3RhID0gaWRNc2dSZXFYWFg7IGlkX21lc3NhZ2dpb19yaXNwb3N0YSA9IDsgaWRfY29sbGFib3JhemlvbmUgPSA7IGVzaXRvID0gT0s7.PHByb3ZhPlRFU1QgUklTUE9TVEFfSU5HUkVTU09fRFVNUF9CSU5BUklPPC9wcm92YT4=";

	
	private static void test(TipoPdD tipoPdD, boolean log4j, boolean requestWithPayload,
			Logger log, FileTraceConfig config, 
			CredenzialiMittente credenzialiMittente, 
			InformazioniToken informazioniToken,
			InformazioniAttributi informazioniAttributi,
			InformazioniNegoziazioneToken informazioniNegoziazioneToken,
			Transazione transazioneDTO, Traccia tracciaRichiesta, 
			Messaggio richiestaIngresso, Messaggio richiestaUscita,
			Messaggio rispostaIngresso, Messaggio rispostaUscita) throws Exception {
		
		boolean onlyLogFileTrace_headers = TipoPdD.APPLICATIVA.equals(tipoPdD) || (TipoPdD.DELEGATA.equals(tipoPdD) && transazioneDTO.getEsito()==16);
		boolean onlyLogFileTrace_body = TipoPdD.APPLICATIVA.equals(tipoPdD) && transazioneDTO.getEsito()!=16;
		
		System.out.println("\n\n ---------------------------- ("+tipoPdD+") (esito:"+transazioneDTO.getEsito()+") (httpMethod:"+transazioneDTO.getTipoRichiesta()+") (onlyLogFileTrace headers:"+onlyLogFileTrace_headers+" body:"+onlyLogFileTrace_body+") -----------------------------");
		
		boolean erogazioni = TipoPdD.APPLICATIVA.equals(tipoPdD);
		Transaction transaction = new Transaction("UUIDXX", "FileTraceTest", false);
		transaction.setCredenzialiMittente(credenzialiMittente);
		transaction.setTracciaRichiesta(tracciaRichiesta);
		transaction.addMessaggio(richiestaIngresso, onlyLogFileTrace_headers, onlyLogFileTrace_body);
		transaction.addMessaggio(richiestaUscita, onlyLogFileTrace_headers, onlyLogFileTrace_body);
		transaction.addMessaggio(rispostaIngresso, onlyLogFileTrace_headers, onlyLogFileTrace_body);
		transaction.addMessaggio(rispostaUscita, onlyLogFileTrace_headers, onlyLogFileTrace_body);
		
		System.out.println("Messaggi presenti prima: "+transaction.sizeMessaggi());
		
		FileTraceManager manager = new FileTraceManager(log, config);
		manager.buildTransazioneInfo(transazioneDTO, transaction,
				informazioniToken,
				informazioniAttributi,
				informazioniNegoziazioneToken,
				null);
		
		boolean requestSent = true;
		if(transazioneDTO.getEsito()!=0) {
			requestSent = false;
		}
		
		PdDContext context = new PdDContext();
		if(requestSent) {
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
			if(!erogazioni && !requestSent) {
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
		
		manager.cleanResourcesForOnlyFileTrace(transaction);
		
		int sizeAfter = transaction.sizeMessaggi();
		System.out.println("Messaggi presenti dopo: "+sizeAfter);
		if(onlyLogFileTrace_headers && onlyLogFileTrace_body) {
			if(sizeAfter!=0) {
				throw new Exception("Attesi 0 messaggi");
			}
		}
		else if(onlyLogFileTrace_headers || onlyLogFileTrace_body) {
			if(sizeAfter!=4) {
				throw new Exception("Attesi 4 messaggi");
			}
			if(onlyLogFileTrace_headers) {
				for (Messaggio msg : transaction.getMessaggi()) {
					if(msg.getHeaders()!=null && !msg.getHeaders().isEmpty()) {
						throw new Exception("Heaeders non attesi ("+msg.getTipoMessaggio()+")");
					}
				}
			}	
			else {
				for (Messaggio msg : transaction.getMessaggi()) {
					if(msg.getBody()!=null) {
						throw new Exception("Body non atteso ("+msg.getTipoMessaggio()+")");
					}
					if(msg.getContentType()!=null) {
						throw new Exception("ContentType non atteso ("+msg.getTipoMessaggio()+")");
					}
				}
			}
		}
		else {
			if(sizeAfter!=4) {
				throw new Exception("Attesi 4 messaggi");
			}
		}
	}
}
