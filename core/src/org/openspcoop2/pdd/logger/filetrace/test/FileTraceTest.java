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

package org.openspcoop2.pdd.logger.filetrace.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.core.transazioni.constants.TipoAPI;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.core.transazioni.utils.CredenzialiMittente;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeTokenClient;
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
import org.openspcoop2.pdd.logger.filetrace.FileTraceConfig;
import org.openspcoop2.pdd.logger.filetrace.FileTraceManager;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.ChannelSecurityToken;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.PDNDTokenInfo;
import org.openspcoop2.protocol.sdk.PDNDTokenInfoDetails;
import org.openspcoop2.protocol.sdk.RestMessageSecurityToken;
import org.openspcoop2.protocol.sdk.SecurityToken;
import org.openspcoop2.protocol.sdk.dump.Messaggio;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.CompressorType;
import org.openspcoop2.utils.io.CompressorUtilities;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.resources.MapReader;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;

/**     
 * Test
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FileTraceTest {

	private static final String DIR_TMP = "/tmp/logs/";
	
	private static final String ENV_NAME_1 = "FILETRACE-TEST-ENV";
	private static final String ENV_VALUE_1 = "envValue";
	private static final String ENV_NAME_2 = "FILETRACE-TEST-ENV2";
	private static final String ENV_VALUE_2 = "envValue2";
	
	public static void initDir() throws UtilsException, ReflectiveOperationException {
		boolean deleteDir = FileSystemUtilities.deleteDirNotEmpty(DIR_TMP,3);
		if(!deleteDir) {
			throw new UtilsException("Directory ["+DIR_TMP+"] non eliminata");
		}
		System.out.println("Directory inizializzata");
		
		updateEnv(ENV_NAME_1, ENV_VALUE_1);
		updateEnv(ENV_NAME_2, ENV_VALUE_2);
		System.out.println("ENV inizializzata ("+ENV_NAME_1+":"+System.getenv(ENV_NAME_1)+") ("+ENV_NAME_2+":"+System.getenv(ENV_NAME_2)+")");
	}
	@SuppressWarnings({ "unchecked" })
	private static void updateEnv(String name, String val) throws ReflectiveOperationException {
		Map<String, String> env = System.getenv();
		Field field = env.getClass().getDeclaredField("m");
	    field.setAccessible(true);
	    ((Map<String, String>) field.get(env)).put(name, val);
	}
	
	public static void main(String [] args) throws Exception{

		initDir();
		
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
	
	// CertificateTest.class.getResourceAsStream(CertificateTest.PREFIX+"govway_test.pem")
	private static final String PEMCERTIFICATE = "-----BEGIN CERTIFICATE-----\n"+
		"MIIDqzCCApMCBFx+TB4wDQYJKoZIhvcNAQELBQAwgZkxGzAZBgkqhkiG9w0BCQEW\n"+
		"DGluZm9AbGluay5pdDELMAkGA1UEBhMCSVQxDjAMBgNVBAgMBUl0YWx5MRYwFAYD\n"+
		"VQQHDA1nb3Z3YXlfdGVzdF9sMRYwFAYDVQQKDA1nb3Z3YXlfdGVzdF9vMRcwFQYD\n"+
		"VQQLDA5nb3Z3YXlfdGVzdF9vdTEUMBIGA1UEAwwLZ292d2F5X3Rlc3QwHhcNMTkw\n"+
		"MzA1MTAxNDU0WhcNMzkwMjI4MTAxNDU0WjCBmTEbMBkGCSqGSIb3DQEJARYMaW5m\n"+
		"b0BsaW5rLml0MQswCQYDVQQGEwJJVDEOMAwGA1UECAwFSXRhbHkxFjAUBgNVBAcM\n"+
		"DWdvdndheV90ZXN0X2wxFjAUBgNVBAoMDWdvdndheV90ZXN0X28xFzAVBgNVBAsM\n"+
		"DmdvdndheV90ZXN0X291MRQwEgYDVQQDDAtnb3Z3YXlfdGVzdDCCASIwDQYJKoZI\n"+
		"hvcNAQEBBQADggEPADCCAQoCggEBAIYKIiJf5v4On8XusNe0kMmbkMEz4fd0aLjf\n"+
		"H795IT3MF9mLL/3QK1Lie724aRLEEIsxmHsYVxLP6gGf4rTCmC7NA5XjmHWMqPRz\n"+
		"8fNqNraJ6lVRHLDmBi8Rfr9tS/HYlNtTYo4aTwUua0WXo+UuiuC4gqS8e9Ns+liz\n"+
		"yzNHveS9/kZ2sKmUu+qmRGK6mMd0NfMcPGv/wOK6QHHjGH+5+5yGkgjX8MIAufC5\n"+
		"SZmmadOhsgttxk56AJEaybZTbCfXuSSuTtxNo6ldT4gsPdPo2pFIT02ld0wmkxaY\n"+
		"tl6gADMUuhN91/WZKQTzxPVMqekdcpPil9pBwQ6x8eqh4Z2GdvECAwEAATANBgkq\n"+
		"hkiG9w0BAQsFAAOCAQEAfYvf74G75VrQJwjSu3Q59jdwpFcdJlitG9MK1/WUVuVm\n"+
		"YURTCHOUs1XPeKFCffNpEqh3X5JAz6ir2rSCwhKNQ5I73IDvKAE/PE1ojYKqwQr2\n"+
		"QHxfkBlmfslX3XChnejE6yHkjWOYlDDWA01bEtEocCrH89mAxWACVfysCt/qkWrJ\n"+
		"76hXtZ/nLnVIE7v+f9WujRHNUPOJRjMAU7Je0/hSq0o4+7AhQhaAZg9XDDzDdX+5\n"+
		"OuJcjmVhtLqmByTErfO4euoYslflgMSEDbmNoUslZzpZqlyZKe4S8+PuzmHWzs5k\n"+
		"Zq21s4Yl5IIZPl8ZAxThukMw9oX3TCBfdgZ8eQurlw==\n"+
		"-----END CERTIFICATE-----";
	
	private static final String PDND_CLIENT_ID = "12345678-cccc-4a60-aaaa-12345678f8dd";
	private static final String PDND_CLIENT_CONSUMER_ID = "12345678-254d-bbbb-aaaa-82e210e12345";
	private static final String PDND_JSON_CLIENT = "{\"consumerId\":\""+PDND_CLIENT_CONSUMER_ID+"\",\"id\":\""+PDND_CLIENT_ID+"\"}";
			
	private static final String PDND_ORGANIZATION_CATEGORY = "Comuni e loro Consorzi e Associazioni";
	private static final String PDND_ORGANIZATION_NAME = "Comune di Test";
	private static final String PDND_ORGANIZATION_ID = "12345678-254d-bbbb-aaaa-82e210e12345";
	private static final String PDND_ORGANIZATIONEXTERNAL_ID = "c001";
	private static final String PDND_ORGANIZATION_EXTERNAL_ORIGIN = "IPA";
	private static final String PDND_JSON_ORGANIZATION = "{\"category\":\""+PDND_ORGANIZATION_CATEGORY+"\",\"externalId\":{\"id\":\""+PDND_ORGANIZATIONEXTERNAL_ID+"\",\"origin\":\""+PDND_ORGANIZATION_EXTERNAL_ORIGIN+"\"},\"id\":\""+PDND_ORGANIZATION_ID+"\",\"name\":\""+PDND_ORGANIZATION_NAME+"\"}";
	
	private static final String SERVIZIO_APPLICATIVO_FRUITORE = "AppXde23";
	
	private static final String CONTENT_TYPE_TEXT_XML_CHARSET = "text/xml; charset=\"UTF8\"";
	
	private static final String HEADER_CONTENT_XXX = "Content-XXX";
	private static final String HEADER_CONTENT_XXX_VALUE = "ADEDE";
	
	private static final String HEADER_TIPO_MESSAGGIO = "TipoMessaggio";
	
	private static final String MSG_FAILED_PREFIX = "FAILED!! \nAtteso:\n";
	
	public static void test(TipoPdD tipoPdD, boolean log4j, int esito, boolean requestWithPayload) throws Exception{
		
		Logger log = LoggerWrapperFactory.getLogger(FileTraceTest.class);
		ConfigurazionePdD confPdD = new ConfigurazionePdD();
		confPdD.setLoader(new Loader());
		confPdD.setLog(log);
		Map<String, IProtocolFactory<?>> m = new HashMap<>();
		m.put(CostantiLabel.TRASPARENTE_PROTOCOL_NAME, Utilities.newInstance("org.openspcoop2.protocol.trasparente.TrasparenteFactory"));
		MapReader<String, IProtocolFactory<?>> map = new MapReader<>(m, false);
		EsitiProperties.initialize(null, log, new Loader(), map);
		
		String testData = "2020-06-25_15:09:05.825";
		Date dataIngressoRichiesta = DateUtils.getSimpleDateFormatMs().parse(testData);
		Date dataIngressoRichiestaStream = new Date(dataIngressoRichiesta.getTime()+50);
		Date dataUscitaRichiesta = new Date(dataIngressoRichiesta.getTime()+100);
		Date dataUscitaRichiestaStream = new Date(dataIngressoRichiesta.getTime()+200);
		Date dataIngressoRisposta = new Date(dataIngressoRichiesta.getTime()+1200);
		Date dataIngressoRispostaStream = new Date(dataIngressoRichiesta.getTime()+1300);
		Date dataUscitaRispostaStream = new Date(dataIngressoRichiesta.getTime()+1400);
		Date dataUscitaRisposta = new Date(dataIngressoRichiesta.getTime()+1665);
		
		Transazione transazioneDTO = new Transazione();
		transazioneDTO.setProtocollo(CostantiLabel.TRASPARENTE_PROTOCOL_NAME);
		transazioneDTO.setEsito(esito);
		transazioneDTO.setIdTransazione("UUIDXX");
		transazioneDTO.setIdCorrelazioneApplicativa("XX-deXXX");
		transazioneDTO.setIdCorrelazioneApplicativaRisposta("RR-deXXXRest");
		
		transazioneDTO.setDataIngressoRichiesta(dataIngressoRichiesta);
		transazioneDTO.setDataIngressoRichiestaStream(dataIngressoRichiestaStream);
		transazioneDTO.setDataUscitaRichiesta(dataUscitaRichiesta);
		transazioneDTO.setDataUscitaRichiestaStream(dataUscitaRichiestaStream);
		transazioneDTO.setDataIngressoRisposta(dataIngressoRisposta);
		transazioneDTO.setDataIngressoRispostaStream(dataIngressoRispostaStream);
		transazioneDTO.setDataUscitaRispostaStream(dataUscitaRispostaStream);
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
		transazioneDTO.setServizioApplicativoFruitore(SERVIZIO_APPLICATIVO_FRUITORE);
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
		
		SecurityToken securityToken = new SecurityToken();
		Certificate cert = 
				ArchiveLoader.load(ArchiveType.CER, PEMCERTIFICATE.getBytes(), 0, null);
		RestMessageSecurityToken restToken = new RestMessageSecurityToken();
		restToken.setCertificate(cert.getCertificate());
		restToken.setToken(clientAssertionBase64);
		securityToken.setAccessToken(restToken);
		securityToken.setAuthorization(restToken);
		securityToken.setIntegrity(restToken);
		securityToken.setAudit(restToken);
		ChannelSecurityToken channel = new ChannelSecurityToken();
		channel.setCertificate(cert.getCertificate());
		securityToken.setChannel(channel);
		
		transazioneDTO.setTokenInfo("ADEDADEAD.DEADADEADAD.dEADEADADEA");
		
		CredenzialiMittente credenzialiMittente = new CredenzialiMittente();

		// Differenzio per avere un requester differente!!
		boolean registraIDApplicativoToken = true;
		boolean registraUsername = true;
		boolean registraInformazioniPDND = true;
		if(requestWithPayload) {
			// POST
			if(TipoPdD.APPLICATIVA.equals(tipoPdD)) {
				registraIDApplicativoToken = false;
				registraUsername = false;
			}
		}
		else {
			// GET
			if(TipoPdD.APPLICATIVA.equals(tipoPdD)) {
				registraIDApplicativoToken = false;
				registraUsername = false;
				registraInformazioniPDND = false;
			}
			else {
				registraUsername = false;
			}
		}
		
		CredenzialeMittente tokenIssuer = new CredenzialeMittente();
		tokenIssuer.setCredenziale("issuerGoogle");
		credenzialiMittente.setTokenIssuer(tokenIssuer);
		
		CredenzialeMittente tokenSubject = new CredenzialeMittente();
		tokenSubject.setCredenziale("subjectAD5432h43242");
		credenzialiMittente.setTokenSubject(tokenSubject);
		
		CredenzialeMittente tokenClientId = new CredenzialeMittente();
		String clientIdToken = "3456ClientId";
		CredenzialeTokenClient c = null;
		if(registraIDApplicativoToken) {
			IDServizioApplicativo idSAToken = new IDServizioApplicativo();
			idSAToken.setNome("SAToken");
			idSAToken.setIdSoggettoProprietario(new IDSoggetto("gw", "SoggettoProprietarioSAToken"));
			c = new CredenzialeTokenClient(clientIdToken, idSAToken);
		}
		else {
			c = new CredenzialeTokenClient(clientIdToken, null); 
		}		
		tokenClientId.setCredenziale(c.getCredenziale());
		credenzialiMittente.setTokenClientId(tokenClientId);
		
		CredenzialeMittente tokenMail = new CredenzialeMittente();
		tokenMail.setCredenziale("info@link.it");
		credenzialiMittente.setTokenEMail(tokenMail);
		
		if(registraUsername) {
			CredenzialeMittente tokenUsername = new CredenzialeMittente();
			tokenUsername.setCredenziale("rossi");
			credenzialiMittente.setTokenUsername(tokenUsername);
		}
		
		CredenzialeMittente trasporto = new CredenzialeMittente();
		trasporto.setCredenziale("C=IT, O=Prova");
		credenzialiMittente.setTrasporto(trasporto);
		
		if(registraInformazioniPDND) {
			if(!TipoPdD.APPLICATIVA.equals(tipoPdD)) {
				securityToken.setPdnd(new PDNDTokenInfo());
			}
			
			if(TipoPdD.APPLICATIVA.equals(tipoPdD)) {
				CredenzialeMittente pdndClientJson = new CredenzialeMittente();
				pdndClientJson.setCredenziale(PDND_JSON_CLIENT);
				credenzialiMittente.setTokenPdndClientJson(pdndClientJson);
			}
			else {
				PDNDTokenInfoDetails d = new PDNDTokenInfoDetails();
				d.setDetails(PDND_JSON_CLIENT);
				d.setId(PDND_CLIENT_ID);
				securityToken.getPdnd().setClient(d);	
			}
			
			if(TipoPdD.APPLICATIVA.equals(tipoPdD)) {
				CredenzialeMittente pdndOrganizationJson = new CredenzialeMittente();
				pdndOrganizationJson.setCredenziale(PDND_JSON_ORGANIZATION);
				credenzialiMittente.setTokenPdndOrganizationJson(pdndOrganizationJson);
			}
			else {
				PDNDTokenInfoDetails d = new PDNDTokenInfoDetails();
				d.setDetails(PDND_JSON_ORGANIZATION);
				d.setId(PDND_ORGANIZATION_ID);
				securityToken.getPdnd().setOrganization(d);			
			}
			
			if(TipoPdD.APPLICATIVA.equals(tipoPdD)) {
				CredenzialeMittente pdndOrganizationName = new CredenzialeMittente();
				pdndOrganizationName.setCredenziale(FileTraceTest.PDND_ORGANIZATION_NAME);
				credenzialiMittente.setTokenPdndOrganizationName(pdndOrganizationName);
			}
		}
		
		Traccia tracciaRichiesta = new Traccia();
		tracciaRichiesta.setBusta(new Busta(CostantiLabel.TRASPARENTE_PROTOCOL_NAME));
		tracciaRichiesta.getBusta().addProperty("ProprietaTest", "Andrea");
		
		Messaggio richiestaIngresso = new Messaggio();
		richiestaIngresso.setTipoMessaggio(TipoMessaggio.RICHIESTA_INGRESSO_DUMP_BINARIO);
		if(requestWithPayload) {
			richiestaIngresso.setContentType(CONTENT_TYPE_TEXT_XML_CHARSET);
			richiestaIngresso.setBody(DumpByteArrayOutputStream.newInstance("<prova>TEST RICHIESTA_INGRESSO_DUMP_BINARIO</prova>".getBytes()));
			TransportUtils.addHeader(richiestaIngresso.getHeaders(),HttpConstants.CONTENT_TYPE, "text/xml; charset=\"UTF8\"; tipo=inRequest");
		}
		TransportUtils.addHeader(richiestaIngresso.getHeaders(),HEADER_CONTENT_XXX, HEADER_CONTENT_XXX_VALUE);
		TransportUtils.addHeader(richiestaIngresso.getHeaders(),HEADER_TIPO_MESSAGGIO, "RICHIESTA_INGRESSO_DUMP_BINARIO");
		
		Messaggio richiestaUscita = new Messaggio();
		richiestaUscita.setTipoMessaggio(TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO);
		if(requestWithPayload) {
			richiestaUscita.setContentType(CONTENT_TYPE_TEXT_XML_CHARSET);
			richiestaUscita.setBody(DumpByteArrayOutputStream.newInstance("<prova>TEST RICHIESTA_USCITA_DUMP_BINARIO</prova>".getBytes()));
			TransportUtils.addHeader(richiestaUscita.getHeaders(),HttpConstants.CONTENT_TYPE, "text/xml; charset=\"UTF8\"; tipo=outRequest");
		}
		TransportUtils.addHeader(richiestaUscita.getHeaders(),HEADER_CONTENT_XXX, HEADER_CONTENT_XXX_VALUE);
		TransportUtils.addHeader(richiestaUscita.getHeaders(),HEADER_TIPO_MESSAGGIO, "RICHIESTA_USCITA_DUMP_BINARIO");
		
		Messaggio rispostaIngresso = new Messaggio();
		rispostaIngresso.setTipoMessaggio(TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO);
		rispostaIngresso.setContentType(CONTENT_TYPE_TEXT_XML_CHARSET);
		rispostaIngresso.setBody(DumpByteArrayOutputStream.newInstance("<prova>TEST RISPOSTA_INGRESSO_DUMP_BINARIO</prova>".getBytes()));
		TransportUtils.addHeader(rispostaIngresso.getHeaders(),HttpConstants.CONTENT_TYPE, "text/xml; charset=\"UTF8\"; tipo=inResponse");
		TransportUtils.addHeader(rispostaIngresso.getHeaders(),HEADER_CONTENT_XXX, HEADER_CONTENT_XXX_VALUE);
		TransportUtils.addHeader(rispostaIngresso.getHeaders(),HEADER_TIPO_MESSAGGIO, "RISPOSTA_INGRESSO_DUMP_BINARIO");
		
		Messaggio rispostaUscita = new Messaggio();
		rispostaUscita.setTipoMessaggio(TipoMessaggio.RISPOSTA_USCITA_DUMP_BINARIO);
		rispostaUscita.setContentType(CONTENT_TYPE_TEXT_XML_CHARSET);
		rispostaUscita.setBody(DumpByteArrayOutputStream.newInstance("<prova>TEST RISPOSTA_USCITA_DUMP_BINARIO</prova>".getBytes()));
		TransportUtils.addHeader(rispostaUscita.getHeaders(),HttpConstants.CONTENT_TYPE, "text/xml; charset=\"UTF8\"; tipo=outResponse");
		TransportUtils.addHeader(rispostaUscita.getHeaders(),HEADER_CONTENT_XXX, HEADER_CONTENT_XXX_VALUE);
		TransportUtils.addHeader(rispostaUscita.getHeaders(),HEADER_TIPO_MESSAGGIO, "RISPOSTA_USCITA_DUMP_BINARIO");
		
		System.setProperty("javaProperty.1", "p1");
		System.setProperty("javaProperty.2", "p2");
		
/**		Properties pTest = new Properties();
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
//		fTmp.delete();*/
		
		String path = "/org/openspcoop2/pdd/logger/filetrace/test/testFileTrace.properties";
		InputStream is = FileTraceTest.class.getResourceAsStream(path);
		FileTraceConfig.init(is, path, true);
		FileTraceConfig config = FileTraceConfig.getConfig(new File(path), true); // inizializzato sopra
			
		Map<String, Serializable> attributes = new HashMap<>();
		attributes.put("a1", "v1");
		ArrayList<String> l2 = new ArrayList<>();
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
				securityToken,
				transazioneDTO, 
				tracciaRichiesta, richiestaIngresso, richiestaUscita, rispostaIngresso, rispostaUscita);
		
	}
	
	// *** TOPIC 'request' ***
	private static final String RETRIEVED_TOKEN_INFO = "\"AAAAATOKEN\"|\"JWT\"|\"UUIDXX\"|\"rfc7523_x509\"|\""+clientAssertionBase64+"\"|\"rfc7523_x509\"|\"clientIdXX\"|\"clientBEARERTOKEN\"|\"USERNAME\"|\"http://retrieveToken\"";
	private static final String CERT_CLIENT_INFO = "\"CN=govway_test, OU=govway_test_ou, O=govway_test_o, L=govway_test_l, ST=Italy, C=IT, EMAILADDRESS=info@link.it\"|\"govway_test\"|\"govway_test_ou\"|\"CN=govway_test, OU=govway_test_ou, O=govway_test_o, L=govway_test_l, ST=Italy, C=IT, EMAILADDRESS=info@link.it\"";
	private static final String TOKEN_INFO = "\"ewogICJhbGciOiAiUlMyNTYiLAogICJ0eXAiOiAiSldUIiwKICAia2lkIjogInByb3ZhIiwKICAieDVjIjogWwogICAgIk1JSS4uLi4uZjZ1UXhjbGhVaDBOaXZqNmhJSitDZDNSMS9GdncxejY5RXllT3ROd3FZU2JzdzJnamlkbzhHUGdEVWtxZFVaSThyYnRjbDIyK2x0S2VXbURhUXZOUUZnTlUreUJObTBBPSIKICBdCn0=.ewogICJpYXQiOiAxNjUwMDMyOTAzLAogICJuYmYiOiAxNjUwMDMyOTAzLAogICJleHAiOiAxNjUwMDMzMDAzLAogICJqdGkiOiAiNGU2MWRjMTQtYmNjOC0xMWVjLTllOTktMDA1MDU2YWUwMzA3IiwKICAiYXVkIjogInRlc3QiLAogICJjbGllbnRfaWQiOiAiY1Rlc3QiLAogICJpc3MiOiAiaXNzVGVzdCIsCiAgInN1YiI6ICJzdWJUZXN0Igp9.PDdXpT5htzB6JI0TdYsfsIBjH8tSV0IkIiKAI0S1IYkqcS6pOs84MsfVk3wnd1_dSiR-2KSpGzZU9s8TuGoXcdR-4oa6EN0RNJJsF8zC1KHVx1IBl4jcZGRY5vAgtKwBC87bPz7EaYXtesS3Go-fl5HTFWvZ4OR3yxvsrCfTy_ehQwVJwJy9yKrIpQFq_dSQr_xQbRBL495D9Fp4p54vNdP3IRtoDq16NUhwkH_dbQJGUJdYZ2M31bBZUvgu9RRZz_ftjI78Swwq5FIwIG7r5trwgmVebZtdLF2Ni5Vc2rL7ZNuBpH7Y_knRgRYbH4HxnMoHOU6nU8yM_ZPZyhHneA\"|\"ewogICJhbGciOiAiUlMyNTYiLAogICJ0eXAiOiAiSldUIiwKICAia2lkIjogInByb3ZhIiwKICAieDVjIjogWwogICAgIk1JSS4uLi4uZjZ1UXhjbGhVaDBOaXZqNmhJSitDZDNSMS9GdncxejY5RXllT3ROd3FZU2JzdzJnamlkbzhHUGdEVWtxZFVaSThyYnRjbDIyK2x0S2VXbURhUXZOUUZnTlUreUJObTBBPSIKICBdCn0=\"|\"ewogICJhbGciOiAiUlMyNTYiLAogICJ0eXAiOiAiSldUIiwKICAia2lkIjogInByb3ZhIiwKICAieDVjIjogWwogICAgIk1JSS4uLi4uZjZ1UXhjbGhVaDBOaXZqNmhJSitDZDNSMS9GdncxejY5RXllT3ROd3FZU2JzdzJnamlkbzhHUGdEVWtxZFVaSThyYnRjbDIyK2x0S2VXbURhUXZOUUZnTlUreUJObTBBPSIKICBdCn0=\"|\"RS256\"|\"kid=prova,x5c=MII.....f6uQxclhUh0Nivj6hIJ+Cd3R1/Fvw1z69EyeOtNwqYSbsw2gjido8GPgDUkqdUZI8rbtcl22+ltKeWmDaQvNQFgNU+yBNm0A=,typ=JWT,alg=RS256\"|\"ewogICJpYXQiOiAxNjUwMDMyOTAzLAogICJuYmYiOiAxNjUwMDMyOTAzLAogICJleHAiOiAxNjUwMDMzMDAzLAogICJqdGkiOiAiNGU2MWRjMTQtYmNjOC0xMWVjLTllOTktMDA1MDU2YWUwMzA3IiwKICAiYXVkIjogInRlc3QiLAogICJjbGllbnRfaWQiOiAiY1Rlc3QiLAogICJpc3MiOiAiaXNzVGVzdCIsCiAgInN1YiI6ICJzdWJUZXN0Igp9\"|\"ewogICJpYXQiOiAxNjUwMDMyOTAzLAogICJuYmYiOiAxNjUwMDMyOTAzLAogICJleHAiOiAxNjUwMDMzMDAzLAogICJqdGkiOiAiNGU2MWRjMTQtYmNjOC0xMWVjLTllOTktMDA1MDU2YWUwMzA3IiwKICAiYXVkIjogInRlc3QiLAogICJjbGllbnRfaWQiOiAiY1Rlc3QiLAogICJpc3MiOiAiaXNzVGVzdCIsCiAgInN1YiI6ICJzdWJUZXN0Igp9\"|\"test\"|\"aud=test,sub=subTest,nbf=1650032903,iss=issTest,exp=1650033003,iat=1650032903,jti=4e61dc14-bcc8-11ec-9e99-005056ae0307,client_id=cTest\"";
	private static final String TLS_CLIENT_INFO = CERT_CLIENT_INFO;
	private static final String ACCESS_TOKEN_JWT = TOKEN_INFO+"|"+CERT_CLIENT_INFO;
	private static final String MODI_AUTHORIZATION = TOKEN_INFO+"|"+CERT_CLIENT_INFO;
	private static final String MODI_INTEGRITY = TOKEN_INFO+"|"+CERT_CLIENT_INFO;
	private static final String MODI_AUDIT = TOKEN_INFO+"|"+CERT_CLIENT_INFO;
	private static final String PDND_CLIENT = "\""+PDND_JSON_CLIENT.replace("\"", "\\\"")+"\"|\""+PDND_CLIENT_ID+"\"|\""+PDND_CLIENT_CONSUMER_ID+"\"";
	private static final String PDND_CLIENT_EMPTY = "\"\"|\"\"|\"\"";
	private static final String PDND_ORGANIZATION = "\""+PDND_JSON_ORGANIZATION.replace("\"", "\\\"")+"\"|\""+PDND_ORGANIZATION_NAME+"\"|\""+PDND_ORGANIZATION_ID+"\"|\""+PDND_ORGANIZATION_CATEGORY+"\"|\""+PDND_ORGANIZATION_EXTERNAL_ORIGIN+"\"|\""+PDND_ORGANIZATIONEXTERNAL_ID+"\"";
	private static final String PDND_ORGANIZATION_EMPTY = "\"\"|\"\"|\"\"|\"\"|\"\"|\"\"";
		
	private static final String DATE = "\"2020-06-25 13:09:05:825\"|\"2020-06-25 13:09:05:875\"|\"2020-06-25 13:09:05:925\"|\"2020-06-25 13:09:06:025\"|\"2020-06-25 13:09:07:025\"|\"2020-06-25 13:09:07:125\"|\"2020-06-25 13:09:07:225\"|\"2020-06-25 13:09:07:490\"";
	private static final String LOG_REQUEST_PA_PUT = "\"in\"|\"rest\"|\"erogazione\"|\"esempioCostanteRichiestaErogazione\"|\"UUIDXX\"|\"XX-deXXXRR-deXXXRest\"|\"p1\"|\"p2\"|\""+ENV_VALUE_1+"\"|\""+ENV_VALUE_2+"\"|\"2020-06-25 13:09:05:825\"|\"+0200\"|\"127.0.0.1\"|\"10.113.13.122\"|\"10.113.13.122\"|\"HTTP/1.1\"|\"PUT\"|\"C=IT, O=Prova\"|\"C=IT, O=Prova\"|\"issuerGoogle\"|\"subjectAD5432h43242\"|\"3456ClientId\"|\"\"|\"info@link.it\"|\"issTest\"|\"1650033003\"|\"\"|\"\"|\"AppXde23\"|\"EnteFruitore\"|\""+PDND_ORGANIZATION_NAME+"\"|\"https://prova:8443/govway/in/EnteEsempio/AAASOAPS1/v1/a1?dklejde=ded&adds=deded\"|\"text/xml; charset=\\\"UTF8\\\"\"|\"51\"|\"HEADERS\"|\"Content-XXX=ADEDE\"|\"TipoMessaggio=RICHIESTA_INGRESSO_DUMP_BINARIO\"|\"Content-Type=text/xml; charset=\\\"UTF8\\\"; tipo=inRequest\"|\"v1\"|\"v2a,v2b\"|"+RETRIEVED_TOKEN_INFO+"|"+TLS_CLIENT_INFO+"|"+ACCESS_TOKEN_JWT+"|"+MODI_AUTHORIZATION+"|"+MODI_INTEGRITY+"|"+MODI_AUDIT+"|"+PDND_CLIENT+"|"+PDND_ORGANIZATION+"|"+DATE;
	private static final String LOG_REQUEST_PA_GET = "\"in\"|\"rest\"|\"erogazione\"|\"esempioCostanteRichiestaErogazione\"|\"UUIDXX\"|\"XX-deXXXRR-deXXXRest\"|\"p1\"|\"p2\"|\""+ENV_VALUE_1+"\"|\""+ENV_VALUE_2+"\"|\"2020-06-25 13:09:05:825\"|\"+0200\"|\"127.0.0.1\"|\"10.113.13.122\"|\"10.113.13.122\"|\"HTTP/1.1\"|\"GET\"|\"C=IT, O=Prova\"|\"C=IT, O=Prova\"|\"issuerGoogle\"|\"subjectAD5432h43242\"|\"3456ClientId\"|\"\"|\"info@link.it\"|\"issTest\"|\"1650033003\"|\"\"|\"\"|\"AppXde23\"|\"EnteFruitore\"|\""+SERVIZIO_APPLICATIVO_FRUITORE+"\"|\"https://prova:8443/govway/in/EnteEsempio/AAASOAPS1/v1/a1?dklejde=ded&adds=deded\"|\"\"|\"0\"|\"HEADERS\"|\"Content-XXX=ADEDE\"|\"TipoMessaggio=RICHIESTA_INGRESSO_DUMP_BINARIO\"|\"v1\"|\"v2a,v2b\"|"+RETRIEVED_TOKEN_INFO+"|"+TLS_CLIENT_INFO+"|"+ACCESS_TOKEN_JWT+"|"+MODI_AUTHORIZATION+"|"+MODI_INTEGRITY+"|"+MODI_AUDIT+"|"+PDND_CLIENT_EMPTY+"|"+PDND_ORGANIZATION_EMPTY+"|"+DATE;
	private static final String LOG_REQUEST_PD_PUT = "\"out\"|\"soap\"|\"fruizione\"|\"esempioCostanteRichiestaFruizione\"|\"UUIDXX\"|\"XX-deXXXRR-deXXXRest\"|\"p1\"|\"p2\"|\""+ENV_VALUE_1+"\"|\""+ENV_VALUE_2+"\"|\"2020-06-25 13:09:05:925\"|\"+0200\"|\"127.0.0.1\"|\"10.113.13.122\"|\"10.113.13.122\"|\"HTTP/1.1\"|\"PUT\"|\"C=IT, O=Prova\"|\"C=IT, O=Prova\"|\"issuerGoogle\"|\"subjectAD5432h43242\"|\"3456ClientId\"|\"rossi\"|\"info@link.it\"|\"issTest\"|\"1650033003\"|\"SAToken\"|\"SoggettoProprietarioSAToken\"|\"AppXde23\"|\"EnteFruitore\"|\"rossi\"|\"http://127.0.0.1:8080/govwayAPIConfig/api/PetStorec0c4269e2ebe413dadb79071/1?dad=ddede&adadad=dede\"|\"text/xml; charset=\\\"UTF8\\\"\"|\"49\"|\"HEADERS\"|\"Content-XXX=ADEDE\"|\"TipoMessaggio=RICHIESTA_USCITA_DUMP_BINARIO\"|\"Content-Type=text/xml; charset=\\\"UTF8\\\"; tipo=outRequest\"|\"v1\"|\"v2a,v2b,v2c\"|"+RETRIEVED_TOKEN_INFO+"|"+TLS_CLIENT_INFO+"|"+ACCESS_TOKEN_JWT+"|"+MODI_AUTHORIZATION+"|"+MODI_INTEGRITY+"|"+MODI_AUDIT+"|"+PDND_CLIENT+"|"+PDND_ORGANIZATION+"|"+DATE;
	private static final String LOG_REQUEST_PD_GET = "\"out\"|\"soap\"|\"fruizione\"|\"esempioCostanteRichiestaFruizione\"|\"UUIDXX\"|\"XX-deXXXRR-deXXXRest\"|\"p1\"|\"p2\"|\""+ENV_VALUE_1+"\"|\""+ENV_VALUE_2+"\"|\"2020-06-25 13:09:05:925\"|\"+0200\"|\"127.0.0.1\"|\"10.113.13.122\"|\"10.113.13.122\"|\"HTTP/1.1\"|\"GET\"|\"C=IT, O=Prova\"|\"C=IT, O=Prova\"|\"issuerGoogle\"|\"subjectAD5432h43242\"|\"3456ClientId\"|\"\"|\"info@link.it\"|\"issTest\"|\"1650033003\"|\"SAToken\"|\"SoggettoProprietarioSAToken\"|\"AppXde23\"|\"EnteFruitore\"|\"SAToken\"|\"http://127.0.0.1:8080/govwayAPIConfig/api/PetStorec0c4269e2ebe413dadb79071/1?dad=ddede&adadad=dede\"|\"\"|\"0\"|\"HEADERS\"|\"Content-XXX=ADEDE\"|\"TipoMessaggio=RICHIESTA_USCITA_DUMP_BINARIO\"|\"v1\"|\"v2a,v2b,v2c\"|"+RETRIEVED_TOKEN_INFO+"|"+TLS_CLIENT_INFO+"|"+ACCESS_TOKEN_JWT+"|"+MODI_AUTHORIZATION+"|"+MODI_INTEGRITY+"|"+MODI_AUDIT+"|"+PDND_CLIENT+"|"+PDND_ORGANIZATION+"|"+DATE;
	
	// *** TOPIC 'requestBody' ***
	private static final String LOG_REQUEST_BODY_PA = "UUIDXX.XX-deXXXRR-deXXXRest.MjAyMC0wNi0yNVQxNTowOTowNS44MjUrMDIwMA==.dGV4dC94bWw7IGNoYXJzZXQ9IlVURjgi.PHByb3ZhPlRFU1QgUklDSElFU1RBX0lOR1JFU1NPX0RVTVBfQklOQVJJTzwvcHJvdmE+";
	private static final String LOG_REQUEST_BODY_PD = "UUIDXX.XX-deXXXRR-deXXXRest.MjAyMC0wNi0yNVQxNTowOTowNS45MjUrMDIwMA==.dGV4dC94bWw7IGNoYXJzZXQ9IlVURjgi.PHByb3ZhPlRFU1QgUklDSElFU1RBX1VTQ0lUQV9EVU1QX0JJTkFSSU88L3Byb3ZhPg==";
	
	// *** TOPIC 'response' ***
	private static final String LOG_RESPONSE_PA = "\"esempioCostanteRispostaErogazione\"|\"ADEDADEAD.DEADADEADAD.dEADEADADEA\"|\"UUIDXX\"|\"XX-deXXXRR-deXXXRest\"|\"p1\"|\"p2\"|\""+ENV_VALUE_1+"\"|\""+ENV_VALUE_2+"\"|\"2020-06-25 13:09:07:490\"|\"+0200\"|\"127.0.0.1\"|\"10.113.13.122\"|\"10.113.13.122\"|\"HTTP/1.1\"|\"PUT\"|\"https://prova:8443/govway/in/EnteEsempio/AAASOAPS1/v1/a1?dklejde=ded&adds=deded\"|\"204\"|\"1665000\"|\"text/xml; charset=\\\"UTF8\\\"\"|\"48\"|\"HEADERS\"|\"Content-XXX=ADEDE\"|\"TipoMessaggio=RISPOSTA_USCITA_DUMP_BINARIO\"|\"Content-Type=text/xml; charset=\\\"UTF8\\\"; tipo=outResponse\"|\"X-GovWay-APP-SERVER=10.114.32.21\"|\"X-GovWay-HOSTNAME-APP-SERVER=prova\"|\"X-GovWay-SERVER-ENCODING=UTF-8\"|\"X-GovWay-APP-SERVER-PORT=8443\"|\"X-GovWay-USER=Andrea\"|\"X-GovWay-COMPLEX=versione_api = 1; api = APIEsempio; operazione = azioneDiProva; erogatore = EnteErogatore; soggetto_fruitore = EnteFruitore; applicativo_fruitore = AppXde23; id_messaggio_richiesta = idMsgReqXXX; id_messaggio_risposta = ; id_collaborazione = ; esito = OK;\"";
	private static final String LOG_RESPONSE_PD = "\"esempioCostanteRispostaFruizione\"|\"ADEDADEAD.DEADADEADAD.dEADEADADEA\"|\"UUIDXX\"|\"XX-deXXXRR-deXXXRest\"|\"p1\"|\"p2\"|\""+ENV_VALUE_1+"\"|\""+ENV_VALUE_2+"\"|\"2020-06-25 13:09:07:025\"|\"+0200\"|\"127.0.0.1\"|\"10.113.13.122\"|\"10.113.13.122\"|\"HTTP/1.1\"|\"PUT\"|\"http://127.0.0.1:8080/govwayAPIConfig/api/PetStorec0c4269e2ebe413dadb79071/1?dad=ddede&adadad=dede\"|\"202\"|\"1100000\"|\"text/xml; charset=\\\"UTF8\\\"\"|\"50\"|\"HEADERS\"|\"Content-XXX=ADEDE\"|\"TipoMessaggio=RISPOSTA_INGRESSO_DUMP_BINARIO\"|\"Content-Type=text/xml; charset=\\\"UTF8\\\"; tipo=inResponse\"|\"X-GovWay-APP-SERVER=10.114.32.21\"|\"X-GovWay-HOSTNAME-APP-SERVER=prova\"|\"X-GovWay-SERVER-ENCODING=UTF-8\"|\"X-GovWay-APP-SERVER-PORT=8443\"|\"X-GovWay-USER=Andrea\"|\"X-GovWay-COMPLEX=versione_api = 1; api = APIEsempio; operazione = azioneDiProva; erogatore = EnteErogatore; soggetto_fruitore = EnteFruitore; applicativo_fruitore = AppXde23; id_messaggio_richiesta = idMsgReqXXX; id_messaggio_risposta = ; id_collaborazione = ; esito = OK;\"";
	
	// *** TOPIC 'responseBody' ***
	private static final String LOG_RESPONSE_BODY_PA = "UUIDXX.XX-deXXXRR-deXXXRest.MjAyMC0wNi0yNVQxNTowOTowNy40OTArMDIwMA==.SFRUUC8xLjEgMjA0IE5vIENvbnRlbnQKQ29udGVudC1YWFg6IEFERURFClRpcG9NZXNzYWdnaW86IFJJU1BPU1RBX1VTQ0lUQV9EVU1QX0JJTkFSSU8KQ29udGVudC1UeXBlOiB0ZXh0L3htbDsgY2hhcnNldD1cIlVURjhcIjsgdGlwbz1vdXRSZXNwb25zZQpYLUdvdldheS1BUFAtU0VSVkVSOiAxMC4xMTQuMzIuMjEKWC1Hb3ZXYXktSE9TVE5BTUUtQVBQLVNFUlZFUjogcHJvdmEKWC1Hb3ZXYXktU0VSVkVSLUVOQ09ESU5HOiBVVEYtOApYLUdvdldheS1BUFAtU0VSVkVSLVBPUlQ6IDg0NDMKWC1Hb3ZXYXktVVNFUjogQW5kcmVhClgtR292V2F5LUNPTVBMRVg6IHZlcnNpb25lX2FwaSA9IDE7IGFwaSA9IEFQSUVzZW1waW87IG9wZXJhemlvbmUgPSBhemlvbmVEaVByb3ZhOyBlcm9nYXRvcmUgPSBFbnRlRXJvZ2F0b3JlOyBzb2dnZXR0b19mcnVpdG9yZSA9IEVudGVGcnVpdG9yZTsgYXBwbGljYXRpdm9fZnJ1aXRvcmUgPSBBcHBYZGUyMzsgaWRfbWVzc2FnZ2lvX3JpY2hpZXN0YSA9IGlkTXNnUmVxWFhYOyBpZF9tZXNzYWdnaW9fcmlzcG9zdGEgPSA7IGlkX2NvbGxhYm9yYXppb25lID0gOyBlc2l0byA9IE9LOw==.PHByb3ZhPlRFU1QgUklTUE9TVEFfVVNDSVRBX0RVTVBfQklOQVJJTzwvcHJvdmE+";
	private static final String LOG_RESPONSE_BODY_PD = "UUIDXX.XX-deXXXRR-deXXXRest.MjAyMC0wNi0yNVQxNTowOTowNy4wMjUrMDIwMA==.SFRUUC8xLjEgMjAyIEFjY2VwdGVkCkNvbnRlbnQtWFhYOiBBREVERQpUaXBvTWVzc2FnZ2lvOiBSSVNQT1NUQV9JTkdSRVNTT19EVU1QX0JJTkFSSU8KQ29udGVudC1UeXBlOiB0ZXh0L3htbDsgY2hhcnNldD1cIlVURjhcIjsgdGlwbz1pblJlc3BvbnNlClgtR292V2F5LUFQUC1TRVJWRVI6IDEwLjExNC4zMi4yMQpYLUdvdldheS1IT1NUTkFNRS1BUFAtU0VSVkVSOiBwcm92YQpYLUdvdldheS1TRVJWRVItRU5DT0RJTkc6IFVURi04ClgtR292V2F5LUFQUC1TRVJWRVItUE9SVDogODQ0MwpYLUdvdldheS1VU0VSOiBBbmRyZWEKWC1Hb3ZXYXktQ09NUExFWDogdmVyc2lvbmVfYXBpID0gMTsgYXBpID0gQVBJRXNlbXBpbzsgb3BlcmF6aW9uZSA9IGF6aW9uZURpUHJvdmE7IGVyb2dhdG9yZSA9IEVudGVFcm9nYXRvcmU7IHNvZ2dldHRvX2ZydWl0b3JlID0gRW50ZUZydWl0b3JlOyBhcHBsaWNhdGl2b19mcnVpdG9yZSA9IEFwcFhkZTIzOyBpZF9tZXNzYWdnaW9fcmljaGllc3RhID0gaWRNc2dSZXFYWFg7IGlkX21lc3NhZ2dpb19yaXNwb3N0YSA9IDsgaWRfY29sbGFib3JhemlvbmUgPSA7IGVzaXRvID0gT0s7.PHByb3ZhPlRFU1QgUklTUE9TVEFfSU5HUkVTU09fRFVNUF9CSU5BUklPPC9wcm92YT4=";

	
	private static void test(TipoPdD tipoPdD, boolean log4j, boolean requestWithPayload,
			Logger log, FileTraceConfig config, 
			CredenzialiMittente credenzialiMittente, 
			InformazioniToken informazioniToken,
			InformazioniAttributi informazioniAttributi,
			InformazioniNegoziazioneToken informazioniNegoziazioneToken,
			SecurityToken securityToken,
			Transazione transazioneDTO, Traccia tracciaRichiesta, 
			Messaggio richiestaIngresso, Messaggio richiestaUscita,
			Messaggio rispostaIngresso, Messaggio rispostaUscita) throws Exception {
		
		boolean onlyLogFileTraceHeaders = TipoPdD.APPLICATIVA.equals(tipoPdD) || (TipoPdD.DELEGATA.equals(tipoPdD) && transazioneDTO.getEsito()==16);
		boolean onlyLogFileTraceBody = TipoPdD.APPLICATIVA.equals(tipoPdD) && transazioneDTO.getEsito()!=16;
		
		System.out.println("\n\n ---------------------------- ("+tipoPdD+") (esito:"+transazioneDTO.getEsito()+") (httpMethod:"+transazioneDTO.getTipoRichiesta()+") (onlyLogFileTrace headers:"+onlyLogFileTraceHeaders+" body:"+onlyLogFileTraceBody+") -----------------------------");
		
		boolean erogazioni = TipoPdD.APPLICATIVA.equals(tipoPdD);
		Transaction transaction = new Transaction("UUIDXX", "FileTraceTest", false);
		transaction.setCredenzialiMittente(credenzialiMittente);
		transaction.setTracciaRichiesta(tracciaRichiesta);
		transaction.addMessaggio(richiestaIngresso, onlyLogFileTraceHeaders, onlyLogFileTraceBody);
		transaction.addMessaggio(richiestaUscita, onlyLogFileTraceHeaders, onlyLogFileTraceBody);
		transaction.addMessaggio(rispostaIngresso, onlyLogFileTraceHeaders, onlyLogFileTraceBody);
		transaction.addMessaggio(rispostaUscita, onlyLogFileTraceHeaders, onlyLogFileTraceBody);
		
		System.out.println("Messaggi presenti prima: "+transaction.sizeMessaggi());
		
		ConfigurazionePdD confPdD = new ConfigurazionePdD();
		confPdD.setLoader(new Loader());
		confPdD.setLog(log);
		ProtocolFactoryManager.initialize(log, confPdD, CostantiLabel.TRASPARENTE_PROTOCOL_NAME);
		
		FileTraceManager manager = new FileTraceManager(log, config);
		IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getDefaultProtocolFactory();
		manager.buildTransazioneInfo(protocolFactory, transazioneDTO, transaction,
				informazioniToken,
				informazioniAttributi,
				informazioniNegoziazioneToken,
				securityToken,
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
			
			/** OUTPUT PRIMA INVOCAZIONE: 
			 *    [testng] Attuale situazione log
   [testng] 	request:11.96kb
   [testng] 	requestBody:171b
   [testng] 	response:911b
   [testng] 	responseBody:903b
			 * */
			
			System.out.println("Iterazione master ...");
			manager.invoke(tipoPdD, context);	
			System.out.println("Iterazione master ok");
			
			boolean compressExpected = true;
			
			Date now = DateManager.getDate();
			String formatDir = "yyyy-MM";
			String formatFile = "MM-dd-yyyy";
			String dirName = DateUtils.getSimpleDateFormat(formatDir).format(now)+"-"+ENV_VALUE_2;
			String dirFile = DateUtils.getSimpleDateFormat(formatFile).format(now);
			
			System.out.println("Verifica log prodotti ...");
			verificaFile(dirName, dirFile, 
					erogazioni, requestWithPayload, !compressExpected,
					transazioneDTO);
			System.out.println("Verifica log prodotti completata");
			
			int numeroInvocazioni = 2;
			for (int i = 0; i < numeroInvocazioni; i++) {
				System.out.println("Iterazione "+(i+1)+"/"+numeroInvocazioni+" ...");
			manager.invoke(tipoPdD, context);	
				System.out.println("Iterazione "+(i+1)+"/"+numeroInvocazioni+" ok");
			}
			
			System.out.println("Verifica log compressi prodotti ...");
			
			verificaFile(dirName, dirFile,
					erogazioni, requestWithPayload, compressExpected,
					transazioneDTO);
			
			System.out.println("Verifica log compressi prodotti completata");
		}
		else {
		
			Map<String, String> outputMap = new HashMap<>();
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
				throw new UtilsException("Attesi "+risultatiAttesi+" risultati");
			}
			
			if(outputMap.size()>0) {
				List<String> l = new ArrayList<>();
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
								if(!logMsg.equals(LOG_REQUEST_PA_PUT)) {
									throw new UtilsException(MSG_FAILED_PREFIX+LOG_REQUEST_PA_PUT+"\nTrovato:\n"+logMsg);
								}
							}
							else {
								if(!logMsg.equals(LOG_REQUEST_PA_GET)) {
									throw new UtilsException(MSG_FAILED_PREFIX+LOG_REQUEST_PA_GET+"\nTrovato:\n"+logMsg);
								}
							}
						}
						else if("requestBody".equals(topic)) {
							if(!logMsg.equals(LOG_REQUEST_BODY_PA)) {
								throw new UtilsException(MSG_FAILED_PREFIX+LOG_REQUEST_BODY_PA+"\nTrovato:\n"+logMsg);
							}
						}
						else if("response".equals(topic)) {
							String atteso = LOG_RESPONSE_PA;
							if(transazioneDTO.getEsito()!=0) {
								atteso = atteso.replace("esito = OK", "esito = ERRORE_AUTENTICAZIONE");
							}
							if(!requestWithPayload) {
								atteso = atteso.replace("PUT", "GET");
							}
							if(!logMsg.equals(atteso)) {
								throw new UtilsException(MSG_FAILED_PREFIX+atteso+"\nTrovato:\n"+logMsg);
							}
						}
						else if("responseBody".equals(topic)) {
							String atteso = LOG_RESPONSE_BODY_PA;
							if(transazioneDTO.getEsito()!=0) {
								atteso = atteso.replace("9yYXppb25lID0gOyBlc2l0byA9IE9LOw==", "9yYXppb25lID0gOyBlc2l0byA9IEVSUk9SRV9BVVRFTlRJQ0FaSU9ORTs=");
							}
							if(!logMsg.equals(atteso)) {
								throw new UtilsException(MSG_FAILED_PREFIX+atteso+"\nTrovato:\n"+logMsg);
							}
						}
						else {
							throw new UtilsException("FAILED!! topic sconosciuto");
						}
					}
					else {
						if("request".equals(topic)) {
							if(requestWithPayload) {
								if(!logMsg.equals(LOG_REQUEST_PD_PUT)) {
									throw new UtilsException(MSG_FAILED_PREFIX+LOG_REQUEST_PD_PUT+"\nTrovato:\n"+logMsg);
								}
							}
							else {
								if(!logMsg.equals(LOG_REQUEST_PD_GET)) {
									throw new UtilsException(MSG_FAILED_PREFIX+LOG_REQUEST_PD_GET+"\nTrovato:\n"+logMsg);
								}
							}
						}
						else if("requestBody".equals(topic)) {
							if(!logMsg.equals(LOG_REQUEST_BODY_PD)) {
								throw new UtilsException(MSG_FAILED_PREFIX+LOG_REQUEST_BODY_PD+"\nTrovato:\n"+logMsg);
							}
						}
						else if("response".equals(topic)) {
							String atteso = LOG_RESPONSE_PD;
							if(!requestWithPayload) {
								atteso = atteso.replace("PUT", "GET");
							}
							if(!logMsg.equals(atteso)) {
								throw new UtilsException(MSG_FAILED_PREFIX+atteso+"\nTrovato:\n"+logMsg);
							}
						}
						else if("responseBody".equals(topic)) {
							if(!logMsg.equals(LOG_RESPONSE_BODY_PD)) {
								throw new UtilsException(MSG_FAILED_PREFIX+LOG_RESPONSE_BODY_PD+"\nTrovato:\n"+logMsg);
							}
						}
						else {
							throw new UtilsException("FAILED!! topic sconosciuto");
						}
					}
				}
			}
		}
		
		manager.cleanResourcesForOnlyFileTrace(transaction);
		
		int sizeAfter = transaction.sizeMessaggi();
		System.out.println("Messaggi presenti dopo: "+sizeAfter);
		if(onlyLogFileTraceHeaders && onlyLogFileTraceBody) {
			if(sizeAfter!=0) {
				throw new UtilsException("Attesi 0 messaggi");
			}
		}
		else if(onlyLogFileTraceHeaders || onlyLogFileTraceBody) {
			if(sizeAfter!=4) {
				throw new UtilsException("Attesi 4 messaggi");
			}
			if(onlyLogFileTraceHeaders) {
				for (Messaggio msg : transaction.getMessaggi()) {
					if(msg.getHeaders()!=null && !msg.getHeaders().isEmpty()) {
						throw new UtilsException("Heaeders non attesi ("+msg.getTipoMessaggio()+")");
					}
				}
			}	
			else {
				for (Messaggio msg : transaction.getMessaggi()) {
					if(msg.getBody()!=null) {
						throw new UtilsException("Body non atteso ("+msg.getTipoMessaggio()+")");
					}
					if(msg.getContentType()!=null) {
						throw new UtilsException("ContentType non atteso ("+msg.getTipoMessaggio()+")");
					}
				}
			}
		}
		else {
			if(sizeAfter!=4) {
				throw new UtilsException("Attesi 4 messaggi");
			}
		}
	}
	
	private static String getContentFile(String file, boolean compress, boolean expected) throws UtilsException, FileNotFoundException {
		File f = new File(file);
		if(!f.exists()) {
			if(expected) {
				throw new UtilsException("File atteso ["+f.getAbsolutePath()+"] non trovato");
			}
			else {
				return null;
			}
		}
		if(!f.canRead()) {
			throw new UtilsException("File atteso ["+f.getAbsolutePath()+"] non leggibile");
		}
		byte [] c = FileSystemUtilities.readBytesFromFile(f);
		if(compress) {
			byte [] cDecompressed = CompressorUtilities.decompress(c, CompressorType.GZIP);
			return new String(cDecompressed);
		}
		else {
			return new String(c);
		}
	}
	
	private static void verificaFile(String dirName,String dirFile,
			boolean erogazioni, boolean requestWithPayload, boolean compressExpected,
			Transazione transazioneDTO) throws FileNotFoundException, UtilsException {
		
		String fileSuffix = ".log";
		String fileGzSuffix = "-1.log.gz";
		String fileGzSuffix2 = "-2.log.gz";	
		
		String dirCompress = dirName+"/";
		String dateFileCompress = "-"+dirFile;
		
		
		String fileRequest = DIR_TMP+""+"fileTrace-"+ENV_VALUE_1+".request"+""+fileSuffix;
		String request = getContentFile(fileRequest,false, true);
		String sizeRequest = Utilities.convertBytesToFormatString(request.length());
		
		String fileRequestCompress = DIR_TMP+dirCompress+"fileTrace-"+ENV_VALUE_1+".request"+dateFileCompress+fileGzSuffix;
		String requestCompress = getContentFile(fileRequestCompress,true,compressExpected);
		
		
		String fileRequestBody = DIR_TMP+""+"fileTrace-"+ENV_VALUE_1+".requestBody"+""+fileSuffix;
		String requestBody = getContentFile(fileRequestBody,false,true);
		String sizeRequestBody = Utilities.convertBytesToFormatString(requestBody.length());
		
		String fileRequestBodyCompress = DIR_TMP+dirCompress+"fileTrace-"+ENV_VALUE_1+".requestBody"+dateFileCompress+fileGzSuffix;
		String requestBodyCompress = getContentFile(fileRequestBodyCompress,true,compressExpected);
		
		
		String fileResponse = DIR_TMP+""+"fileTrace-"+ENV_VALUE_1+".response"+""+fileSuffix;
		String response = getContentFile(fileResponse,false, true);
		String sizeResponse = Utilities.convertBytesToFormatString(response.length());
		
		String fileResponseCompress = DIR_TMP+dirCompress+"fileTrace-"+ENV_VALUE_1+".response"+dateFileCompress+fileGzSuffix;
		String responseCompress = getContentFile(fileResponseCompress,true,compressExpected);
		
		String fileResponseCompress2 = DIR_TMP+dirCompress+"fileTrace-"+ENV_VALUE_1+".response"+dateFileCompress+fileGzSuffix2;
		String responseCompress2 = getContentFile(fileResponseCompress2,true,
				false); // può avvenire dopo
		
		
		String fileResponseBody = DIR_TMP+""+"fileTrace-"+ENV_VALUE_1+".responseBody"+""+fileSuffix;
		String responseBody = getContentFile(fileResponseBody,false, true);
		String sizeResponseBody = Utilities.convertBytesToFormatString(responseBody.length());
		
		String fileResponseBodyCompress = DIR_TMP+dirCompress+"fileTrace-"+ENV_VALUE_1+".responseBody"+dateFileCompress+fileGzSuffix;
		String responseBodyCompress = getContentFile(fileResponseBodyCompress,true,compressExpected);
		
		
		if(!compressExpected) {
			System.out.println("Attuale situazione log\n\trequest:"+sizeRequest+"\n\trequestBody:"+sizeRequestBody+"\n\tresponse:"+sizeResponse+"\n\tresponseBody:"+sizeResponseBody+"");
		}
		
		String nonTrovatoInFileMessage="\nNon trovato in file:";
		String nonTrovatoInFileCompressMessage=" e nemmeno in file compresso:";
		
		String decompressed = ".decompressed";
		String fileRequestDecompressed = fileRequest + decompressed;
		String fileRequestBodyDecompressed = fileRequestBody + decompressed;
		String fileResponseDecompressed = fileResponse + decompressed;
		String fileResponseDecompressed2 = fileResponse + decompressed + "2";
		String fileResponseBodyDecompressed = fileResponseBody + decompressed;
		if(requestCompress!=null) {
			FileSystemUtilities.writeFile(fileRequestDecompressed, requestCompress.getBytes());
		}
		if(requestBodyCompress!=null) {
			FileSystemUtilities.writeFile(fileRequestBodyDecompressed, requestBodyCompress.getBytes());
		}
		if(responseCompress!=null) {
			FileSystemUtilities.writeFile(fileResponseDecompressed, responseCompress.getBytes());
		}
		if(responseCompress2!=null) {
			FileSystemUtilities.writeFile(fileResponseDecompressed2, responseCompress2.getBytes());
		}
		if(responseBodyCompress!=null) {
			FileSystemUtilities.writeFile(fileResponseBodyDecompressed, responseBodyCompress.getBytes());
		}
			
		
		if(erogazioni) {
			
			// request
			if(requestWithPayload) {
				if(!request.contains(LOG_REQUEST_PA_PUT) && 
						(requestCompress==null || !requestCompress.contains(LOG_REQUEST_PA_PUT))
				) {
					throw new UtilsException(MSG_FAILED_PREFIX+LOG_REQUEST_PA_PUT+nonTrovatoInFileMessage+fileRequest+nonTrovatoInFileCompressMessage+fileRequestDecompressed);
				}
			}
			else {
				if(!request.contains(LOG_REQUEST_PA_GET) && 
						(requestCompress == null || !requestCompress.contains(LOG_REQUEST_PA_GET)
				)) {
					throw new UtilsException(MSG_FAILED_PREFIX+LOG_REQUEST_PA_GET+nonTrovatoInFileMessage+fileRequest+nonTrovatoInFileCompressMessage+fileRequestDecompressed);
				}
			}

			// requestBody
			if(!requestBody.contains(LOG_REQUEST_BODY_PA) && 
					(requestBodyCompress == null || !requestBodyCompress.contains(LOG_REQUEST_BODY_PA)
			)) {
				throw new UtilsException(MSG_FAILED_PREFIX+LOG_REQUEST_BODY_PA+nonTrovatoInFileMessage+fileRequestBody+nonTrovatoInFileCompressMessage+fileRequestBodyDecompressed);
			}
	
			// response
			String atteso = LOG_RESPONSE_PA;
			if(transazioneDTO.getEsito()!=0) {
				atteso = atteso.replace("esito = OK", "esito = ERRORE_AUTENTICAZIONE");
			}
			if(!requestWithPayload) {
				atteso = atteso.replace("PUT", "GET");
			}
			if(!response.contains(atteso) &&
					(responseCompress==null || !responseCompress.contains(atteso)) &&
					(responseCompress2==null || !responseCompress2.contains(atteso))) {
				throw new UtilsException(MSG_FAILED_PREFIX+atteso+nonTrovatoInFileMessage+fileResponse+nonTrovatoInFileCompressMessage+fileResponseDecompressed);
			}
			
			// responseBody
			atteso = LOG_RESPONSE_BODY_PA;
			if(transazioneDTO.getEsito()!=0) {
				atteso = atteso.replace("9yYXppb25lID0gOyBlc2l0byA9IE9LOw==", "9yYXppb25lID0gOyBlc2l0byA9IEVSUk9SRV9BVVRFTlRJQ0FaSU9ORTs=");
			}
			if(!responseBody.contains(atteso) &&
					(responseBodyCompress==null || !responseBodyCompress.contains(atteso))) {
				throw new UtilsException(MSG_FAILED_PREFIX+atteso+nonTrovatoInFileMessage+fileResponseBody+nonTrovatoInFileCompressMessage+fileResponseBodyDecompressed);
			}

		}
		else {
			// request
			if(requestWithPayload) {
				if(!request.contains(LOG_REQUEST_PD_PUT) && 
						(requestCompress==null || !requestCompress.contains(LOG_REQUEST_PD_PUT))
				) {
					throw new UtilsException(MSG_FAILED_PREFIX+LOG_REQUEST_PD_PUT+nonTrovatoInFileMessage+fileRequest+nonTrovatoInFileCompressMessage+fileRequestDecompressed);
				}
			}
			else {
				if(!request.contains(LOG_REQUEST_PD_GET) && 
						(requestCompress==null || !requestCompress.contains(LOG_REQUEST_PD_GET))
				) {
					throw new UtilsException(MSG_FAILED_PREFIX+LOG_REQUEST_PD_GET+nonTrovatoInFileMessage+fileRequest+nonTrovatoInFileCompressMessage+fileRequestDecompressed);
				}
			}
			
			// requestBody
			if(!requestBody.contains(LOG_REQUEST_BODY_PD) && 
					(requestBodyCompress == null || !requestBodyCompress.contains(LOG_REQUEST_BODY_PD)
			)) {
				throw new UtilsException(MSG_FAILED_PREFIX+LOG_REQUEST_BODY_PD+nonTrovatoInFileMessage+fileRequestBody+nonTrovatoInFileCompressMessage+fileRequestBodyDecompressed);
			}
			
			// response
			String atteso = LOG_RESPONSE_PD;
			if(!requestWithPayload) {
				atteso = atteso.replace("PUT", "GET");
			}
			if(!response.contains(atteso) &&
					(responseCompress==null || !responseCompress.contains(atteso)) &&
					(responseCompress2==null || !responseCompress2.contains(atteso))) {
				throw new UtilsException(MSG_FAILED_PREFIX+atteso+nonTrovatoInFileMessage+fileResponse+nonTrovatoInFileCompressMessage+fileResponseDecompressed);
			}
			
			// responseBody
			if(!responseBody.contains(LOG_RESPONSE_BODY_PD) && 
					(responseBodyCompress == null || !responseBodyCompress.contains(LOG_RESPONSE_BODY_PD)
			)) {
				throw new UtilsException(MSG_FAILED_PREFIX+LOG_RESPONSE_BODY_PD+nonTrovatoInFileMessage+fileResponseBody+nonTrovatoInFileCompressMessage+fileResponseBodyDecompressed);
			}

		}
	}
}
